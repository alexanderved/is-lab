package server.repository.postgresql;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import server.repository.postgresql.dto.OrganizationDTO;
import server.repository.postgresql.dto.RefreshTokenDTO;
import server.repository.postgresql.dto.UserDTO;

public final class PostgreSQLContext {
    private final SessionFactory sessionFactory;

    public PostgreSQLContext(String url, String username, String password) {
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.url", url);
        cfg.setProperty("hibernate.connection.username", username);
        cfg.setProperty("hibernate.connection.password", password);
        cfg.setProperty("hibernate.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        cfg.addAnnotatedClass(UserDTO.class);
        cfg.addAnnotatedClass(OrganizationDTO.class);
        cfg.addAnnotatedClass(RefreshTokenDTO.class);

        cfg.configure();

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());

        sessionFactory = cfg.buildSessionFactory(builder.build());
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
