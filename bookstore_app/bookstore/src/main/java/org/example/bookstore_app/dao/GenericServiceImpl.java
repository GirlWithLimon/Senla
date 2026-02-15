package org.example.bookstore_app.dao;

import org.example.bookstore_app.dao.GenericDAO;
import org.example.bookstore_app.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.Serializable;

public abstract class GenericServiceImpl<T, PK extends Serializable, R extends GenericDAO<T, PK>>
        implements GenericDAO<T, PK> {

    protected R defaultRepository;

    public GenericServiceImpl(R repository) {
        this.defaultRepository = repository;
    }

    @Override
    public PK save(T entity) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            PK id = defaultRepository.save(entity);
            transaction.commit();
            return id;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при сохранении", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public void update(T entity) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            defaultRepository.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при обновлении", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public void delete(PK id) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            defaultRepository.delete(id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при удалении", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }

    @Override
    public T find(PK id) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            T entity = defaultRepository.find(id);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка при поиске", e);
        } finally {
            HibernateUtil.closeSession();
        }
    }
}