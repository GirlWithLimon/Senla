package org.example.bookstore_app.dao;

import org.example.bookstore_app.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends HibernateAbstractDao<User, Integer> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }
}