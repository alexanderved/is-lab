package server.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import server.service.auth.AuthService;
import server.service.auth.Role;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity
public class ServerSecurityConfig {
    private final TokenFilter tokenFilter;
    private final AuthService authService;

    public ServerSecurityConfig(TokenFilter tokenFilter, AuthService authService) {
        this.tokenFilter = tokenFilter;
        this.authService = authService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/admin/**").hasAuthority(
                                    Role.ROLE_ADMIN.toString())
                            .requestMatchers("/api/organizations/**").hasAuthority(
                                    Role.ROLE_USER.toString())
                            .requestMatchers("/api/users/**").hasAuthority(
                                    Role.ROLE_USER.toString())
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
                return authService.findByName(name);
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
