package com.bebo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldHibernateToJPATest {

    private static EntityManagerFactory createEntityManagerFactory() {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml").addAnnotatedClass(Message.class);

        Enumeration<?> propertyNames = cfg.getProperties().propertyNames();
        HashMap<String, String> properties = new HashMap<>();

        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            properties.put(propertyName, cfg.getProperty(propertyName));
        }

        return Persistence.createEntityManagerFactory("ch02", properties);
    }

    @Test
    public void storeLoadMessage() {
        // Represents the persistent-unit, and it's thread safe
        try (EntityManagerFactory emf = createEntityManagerFactory();
             // Represents a new session with the database. It's the context for all persistence-operations
             EntityManager em = emf.createEntityManager()) {

            // access Transaction API and begin transaction on this thread
            em.getTransaction().begin();

            Message message = new Message();
            message.setText("Hello World");

            // Make an instance managed and persistent. Hibernate doesn't call the database immediately.
            em.persist(message);

            em.getTransaction().commit();

            em.getTransaction().begin();

            // creates TypedQuery. Query are in JPQL.
            List<Message> messages = em.createQuery("select m from Message m", Message.class)
                    .getResultList();

            messages.get(messages.size() - 1).setText("Hello World from JPA");

            em.getTransaction().commit();

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hello World from JPA", messages.get(0).getText())
            );
        }
    }
}