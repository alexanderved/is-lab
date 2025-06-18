package server.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import server.service.auth.AuthService;
import server.service.user.Role;
import server.service.user.UserService;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity
public class ServerSecurityConfig {
    private final TokenFilter tokenFilter;
    private final UserService userService;

    public ServerSecurityConfig(TokenFilter tokenFilter,
                                UserService userService) {
        this.tokenFilter = tokenFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/admin/**").hasAuthority(
                                    Role.ROLE_ADMIN.toString())
                            .requestMatchers("/api/organizations/**").hasAuthority(
                                    Role.ROLE_USER.toString())
                            .requestMatchers("/api/users/**").permitAll()
                            .requestMatchers("/api/upload", "/api/download/**").permitAll()
                            .anyRequest().authenticated();
                })
                .userDetailsService(userDetailsService)
                .exceptionHandling(e -> {
                    e.accessDeniedHandler((request, response, exception) -> {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                    });
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (name) -> {
            try {
                return userService.findByName(name);
            } catch (Exception e) {
                throw new UsernameNotFoundException(e.getMessage(), e);
            }
        };
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("")
                .role(Role.ROLE_SUPERADMIN.toString()).implies(Role.ROLE_ADMIN.toString())
                .role(Role.ROLE_ADMIN.toString()).implies(Role.ROLE_USER.toString())
                .build();
    }
}
