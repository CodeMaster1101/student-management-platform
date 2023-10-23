package com.student.management.platform.global.persistence;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class CustomPersistenceService<T> {

  @PersistenceContext EntityManager em;

  @Transactional
  public void persist(T entity) {
    try (Session session = em.unwrap(Session.class)) {
        session.persist(entity);
    }
  }

  @Transactional
  public void update(T entity) {
    try (Session session = em.unwrap(Session.class)) {
      session.update(entity);
    }
  }
}
