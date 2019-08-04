package com.open.tool.revolut.repository.impl;

import com.google.inject.Singleton;
import com.open.tool.revolut.exception.BackendException;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.repository.AccountRepository;
import org.hibernate.Hibernate;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


@Singleton
public class AccountRepositoryImpl extends AbstractRepository<Account> implements AccountRepository {

    /**
     * {@inheritDoc}
     */
    public Account createAccount(final Account account) throws BackendException {
        if (account == null) {
            throw new IllegalArgumentException("'Account' must be not null");
        }
        //id of new account
        return doInTransaction(() -> {
            session().save(account);
            return account;
        });

    }


    /**
     * {@inheritDoc}
     */
    public Account findAccountById(Long id) throws BackendException {
        if (id == null) {
            throw new IllegalArgumentException("Account id must be not null");
        }

        // nullable
        return doInTransaction(() -> session().find(Account.class, id));
    }

    /**
     * {@inheritDoc}
     */
    public Account findAccountByIdFull(Long id) throws BackendException {
        if (id == null) {
            throw new IllegalArgumentException("Account id must be not null");
        }

        // nullable
        return doInTransaction(() -> {
            Account account = session().find(Account.class, id);
            Hibernate.initialize(account.getInOperations());
            Hibernate.initialize(account.getOutOperations());
            return account;
        });
    }

    /**
     * {@inheritDoc}
     */
    public void update(Account account) throws BackendException {
        doInTransaction(() -> {
            session().update(account);
            return null;
        });
    }

    @Override
    public Account findAccountByName(String name) throws BackendException {
        return doInTransaction(() -> {
            CriteriaBuilder builder = session().getCriteriaBuilder();
            CriteriaQuery<Account> query = builder.createQuery(Account.class);
            Root<Account> root = query.from(Account.class);
            query.select(root)
                    .where(
                            builder.equal(root.get("name"), name)
                    );
            try {
                return session().createQuery(query).getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        });

    }
}
