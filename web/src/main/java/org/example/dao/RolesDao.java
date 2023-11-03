package org.example.dao;

import org.example.core.HibernateUtils;
import org.example.models.Role;
import org.example.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

@Component
public class RolesDao {
    private final SessionFactory sessionFactory;

    {
        sessionFactory = HibernateUtils.buildSessionFactory(User.class, Role.class);
    }

    public Role getRole(String role) {
        try (Session session = sessionFactory.openSession()){
            session.getTransaction().begin();


            Query query = session.createQuery("FROM Role WHERE name = :name", Role.class);
            query.setParameter("name", role);

            Role selected = (Role) query.uniqueResult();
            return selected;
        }
    }

    public void update(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.merge(role);
            session.getTransaction().commit();
        }
    }

    public void create(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            Role entity = session.merge(role);
            session.persist(entity);
            session.getTransaction().commit();
        }
    }
}
