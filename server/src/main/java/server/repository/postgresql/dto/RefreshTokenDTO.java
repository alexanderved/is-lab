package server.repository.postgresql.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private int id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;

    private RefreshTokenDTO() {

    }

    public RefreshTokenDTO(String token, UserDTO user) {
        this.refreshToken = token;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return refreshToken;
    }

    public UserDTO getUser() {
        return user;
    }
}
