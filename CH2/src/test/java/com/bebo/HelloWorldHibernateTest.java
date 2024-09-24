package com.bebo;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldHibernateTest {
    /**
     * Configure SessionFactory
     *
     * @apiNote Hibernate's behavior differs from JPA's auto-detection for several reasons:
     * <ul>
     *     <li><b>Explicit Configuration:</b> Hibernate emphasizes explicit com.bebo.configuration,
     *         allowing developers finer control over which classes are registered
     *         and how they are mapped. This is particularly useful in complex applications.</li>
     *     <li><b>Flexibility:</b> By requiring explicit registration, Hibernate enables
     *         selective inclusion or exclusion of classes, simplifying the management
     *         of large applications with many entities.</li>
     *     <li><b>Legacy Support:</b> Created prior to the JPA standardization, Hibernate
     *         maintains its own com.bebo.configuration mechanisms for backward compatibility
     *         and specific features, even as JPA builds on its capabilities.</li>
     * </ul>
     */
    private static SessionFactory createSessionFactory() {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml").addAnnotatedClass(Message.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(cfg.getProperties())
                .build();
        return cfg.buildSessionFactory(serviceRegistry);
    }

    @Test
    public void storeLoadTest() {
        try (SessionFactory sessionFactory = createSessionFactory(); Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Message message = new Message();
            message.setText("Hello World from Hibernate");

            session.persist(message);
            session.getTransaction().commit();

            session.getTransaction().begin();

            CriteriaQuery<Message> query = session.getCriteriaBuilder().createQuery(Message.class);
            query.from(Message.class);
            List<Message> messages = session.createQuery(query).getResultList();

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hello World from Hibernate", messages.get(0).getText())
            );

        }
    }
}
