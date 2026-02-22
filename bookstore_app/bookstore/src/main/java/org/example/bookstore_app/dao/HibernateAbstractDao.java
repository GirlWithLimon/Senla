package org.example.bookstore_app.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.List;

public abstract class HibernateAbstractDao<T, PK extends Serializable>
        implements GenericDAO<T, PK> {

    private Class<T> type;

    @Autowired
    private SessionFactory sessionFactory;

    public HibernateAbstractDao(Class<T> type) {
        this.type = type;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public PK save(T entity) {
        return (PK) getCurrentSession().save(entity);
    }

    @Override
    public void update(T entity) {
        getCurrentSession().update(entity);
    }

    @Override
    public void delete(PK id) {
        T entity = getCurrentSession().load(type, id);
        getCurrentSession().delete(entity);
    }

    @Override
    public List<T> findAll() {
        return getCurrentSession()
                .createQuery("FROM " + type.getSimpleName(), type)
                .getResultList();
    }

    @Override
    public T find(PK id) {
        return getCurrentSession().get(type, id);
    }
}