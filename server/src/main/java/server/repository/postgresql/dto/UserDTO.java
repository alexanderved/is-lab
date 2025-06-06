package server.repository.postgresql.dto;

import common.domain.OrganizationType;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import server.service.auth.HashedUser;
import server.service.auth.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserDTO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    @Check(constraints = "LENGTH(name) > 0")
    private String name;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "password_hash", length = 128, nullable = false)
    private String passwordHash;

    @Column(name = "salt", length = 24, nullable = false)
    private String salt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private final List<RefreshTokenDTO> refreshTokens = new ArrayList<>();

    public UserDTO() {

    }

    public UserDTO(HashedUser user) {
        name = user.getUsername();
        role = user.getRole();
        passwordHash = user.getPasswordHash();
        salt = user.getSalt();

        user.getRefreshTokens()
                .stream()
                .map((tokenString) -> new RefreshTokenDTO(tokenString, this))
                .forEach(refreshTokens::add);
    }

    public void update(HashedUser user) {
        name = user.getUsername();
        role = user.getRole();
        passwordHash = user.getPasswordHash();
        salt = user.getSalt();

        List<RefreshTokenDTO> tokens = new ArrayList<>(getRefreshTokens());
        outer: for (String tokenString : user.getRefreshTokens()) {
            var it = tokens.iterator();
            while (it.hasNext()) {
                RefreshTokenDTO dto = it.next();
                if (dto.getToken().equals(tokenString)) {
                    it.remove();
                    continue outer;
                }
            }

            getRefreshTokens().add(new RefreshTokenDTO(tokenString, this));
        }

        List<String> userTokens = new ArrayList<>(user.getRefreshTokens());
        var it = getRefreshTokens().iterator();
        outer: while (it.hasNext()) {
            RefreshTokenDTO dto = it.next();

            var iit = userTokens.iterator();
            while (iit.hasNext()) {
                String tokenString = iit.next();
                if (dto.getToken().equals(tokenString)) {
                    iit.remove();
                    continue outer;
                }
            }

            it.remove();
        }
    }

    public HashedUser toHashedUser() {
        HashedUser user = new HashedUser(name, role, passwordHash, salt);
        refreshTokens.stream()
                .map(RefreshTokenDTO::getToken)
                .forEach(user::addRefreshToken);

        return user;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public List<RefreshTokenDTO> getRefreshTokens() {
        return refreshTokens;
    }
}
