package org.example.dao;

import org.example.core.HibernateUtils;
import org.example.models.Role;
import org.example.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDao {


    private final SessionFactory sessionFactory;


    {
        sessionFactory = HibernateUtils.buildSessionFactory(User.class, Role.class);
    }


    public User getUser(int id) {
        try (Session session = sessionFactory.openSession()){
            session.getTransaction().begin();

            User selected = session.get(User.class, id);
            return selected;
        }
    }

    @Transactional
    public User getUser(String username) {
        try (Session session = sessionFactory.openSession()){
            session.getTransaction().begin();


            Query query = session.createQuery("FROM User WHERE username = :name", User.class);
            query.setParameter("name", username);

            User selected = (User) query.uniqueResult();
            session.getTransaction().commit();
            return selected;
        }
    }

    @Transactional
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.remove(getUser(id));
            session.getTransaction().commit();
        }
    }

    public void delete(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.remove(getUser(username));
            session.getTransaction().commit();
        }
    }

}
