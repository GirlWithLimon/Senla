package org.example.bookstore_app.service;

import org.example.bookstore_app.dao.GenericDAO;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class GenericServiceImpl<T, PK extends Serializable, R extends GenericDAO<T, PK>>
        implements GenericService<T, PK> {

    protected R defaultRepository;

    public GenericServiceImpl(R repository) {
        this.defaultRepository = repository;
    }

    @Override
    @Transactional
    public PK save(T entity) {
        return defaultRepository.save(entity);
    }

    @Override
    @Transactional
    public void update(T entity) {
        defaultRepository.update(entity);
    }

    @Override
    @Transactional
    public void delete(PK id) {
        defaultRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T find(PK id) {
        return defaultRepository.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return defaultRepository.findAll();
    }
}