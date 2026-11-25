package server.resource.entities;

import jakarta.persistence.*;
import server.resource.domain.user.Role;
import server.resource.domain.user.User;
import server.resource.domain.user.UserTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(
            name = "username",
            unique = true,
            nullable = false,
            check = @CheckConstraint(constraint = "LENGTH(username) > 0")
    )
    private String username;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "password_hash", length = 128, nullable = false)
    private String passwordHash;

    @Column(name = "salt", length = 24, nullable = false)
    private String salt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "user", orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    public UserEntity() {

    }

    public UserEntity(UserTemplate template) {
        username = Objects.requireNonNull(template.getUsername());
        role = Objects.requireNonNull(template.getRole());
        passwordHash = Objects.requireNonNull(template.getPasswordHash());
        salt = Objects.requireNonNull(template.getSalt());

        template.getRefreshTokens()
                .stream()
                .map((tokenString) -> new RefreshTokenEntity(tokenString, this))
                .forEach(refreshTokens::add);
    }

    public void update(User user) {
        username = Objects.requireNonNull(user.getUsername());
        role = Objects.requireNonNull(user.getRole());
        passwordHash = Objects.requireNonNull(user.getPasswordHash());
        salt = Objects.requireNonNull(user.getSalt());

        List<RefreshTokenEntity> tokens = new ArrayList<>(getRefreshTokens());
        outer: for (String tokenString : user.getRefreshTokens()) {
            var it = tokens.iterator();
            while (it.hasNext()) {
                RefreshTokenEntity dto = it.next();
                if (dto.getToken().equals(tokenString)) {
                    it.remove();
                    continue outer;
                }
            }

            getRefreshTokens().add(new RefreshTokenEntity(tokenString, this));
        }

        List<String> userTokens = new ArrayList<>(user.getRefreshTokens());
        var it = getRefreshTokens().iterator();
        outer: while (it.hasNext()) {
            RefreshTokenEntity dto = it.next();

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

    public User toUser() {
        UserTemplate template = new UserTemplate()
                .setUsername(username)
                .setRole(role)
                .setPasswordHash(passwordHash)
                .setSalt(salt);

        List<String> tokens = refreshTokens.stream()
                .map(RefreshTokenEntity::getToken)
                .toList();
        template.setRefreshTokens(tokens);

        return new User(id, template);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public List<RefreshTokenEntity> getRefreshTokens() {
        return refreshTokens;
    }
}
