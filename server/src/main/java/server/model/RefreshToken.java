package server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private RefreshToken() {

    }

    public RefreshToken(String token, User user) {
        this.refreshToken = token;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}
