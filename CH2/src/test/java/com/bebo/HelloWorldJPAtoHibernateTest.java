package com.bebo;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldJPAtoHibernateTest {
    private static SessionFactory getSessionFactory(EntityManagerFactory emf) {
        return emf.unwrap(SessionFactory.class);
    }

    @Test
    public void storeLoadTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ch02");
        try (SessionFactory sessionFactory = getSessionFactory(emf); Session session = sessionFactory.openSession()) {
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

