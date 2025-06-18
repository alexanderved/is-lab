package server.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import server.model.Organization;
import server.model.RefreshToken;
import server.model.User;

public final class HibernateConfiguration {
    private final SessionFactory sessionFactory;

    public HibernateConfiguration(String url, String username, String password) {
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.url", url);
        cfg.setProperty("hibernate.connection.username", username);
        cfg.setProperty("hibernate.connection.password", password);
        cfg.setProperty("hibernate.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        cfg.addAnnotatedClass(User.class);
        cfg.addAnnotatedClass(Organization.class);
        cfg.addAnnotatedClass(RefreshToken.class);

        cfg.configure();

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties());

        sessionFactory = cfg.buildSessionFactory(builder.build());
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
