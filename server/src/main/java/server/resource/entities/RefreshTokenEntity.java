package server.resource.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private RefreshTokenEntity() {

    }

    public RefreshTokenEntity(String token, UserEntity user) {
        this.refreshToken = token;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return refreshToken;
    }

    public UserEntity getUser() {
        return user;
    }
}
