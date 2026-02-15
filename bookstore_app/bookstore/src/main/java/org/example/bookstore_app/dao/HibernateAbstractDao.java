package org.example.bookstore_app.dao;

import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

public abstract class HibernateAbstractDao <T, PK extends Serializable>
                                    implements GenericDAO<T,PK>{
    private Class <T> type;

    public HibernateAbstractDao(Class<T> type) {
        this.type=type;
    }

    @Override
    public PK save(T entity) {
        Session session = HibernateUtil.getCurrentSession();
        return (PK) session.save(entity);
    }

    @Override
    public void update(T entity) {
        Session session = HibernateUtil.getCurrentSession();
        session.update(entity);
    }

    @Override
    public void delete(PK id) {
        Session session = HibernateUtil.getCurrentSession();
        T entity = session.load(type, id);
        session.delete(entity);
    }

    @Override
    public List<T> findAll() {
        Session session = HibernateUtil.getCurrentSession();
        String hql = "FROM " + type.getSimpleName();
        Query<T> query = session.createQuery(hql, type);
        return query.getResultList();
    }

    @Override
    public T find(PK id) {
        Session session = HibernateUtil.getCurrentSession();
        return session.get(type, id);
    }
}
