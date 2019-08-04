package com.open.tool.revolut.repository.impl;

import com.open.tool.revolut.exception.BackendException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

abstract class AbstractRepository<T> {

    private SessionFactory sf = new Configuration().configure().buildSessionFactory();
    private static Logger log = LoggerFactory.getLogger(AbstractRepository.class);

    Session session() {
        if (sf.isOpen()) {
            return sf.getCurrentSession();
        } else {
            return sf.openSession();
        }
    }

    T doInTransaction(Supplier<T> sup) throws BackendException {
        Transaction t = null;
        T result;
        try {
            t = session().beginTransaction();
            result = sup.get();
        } catch (Exception ex) {
            log.error("Something wrong with transaction", ex);
            if(t != null) {
                t.rollback();
            }
            throw new BackendException("Something wrong with transaction", 400);
        } finally {
            if (t != null && t.isActive()) {
                t.commit();
            }
        }
        return result;
    }

}
