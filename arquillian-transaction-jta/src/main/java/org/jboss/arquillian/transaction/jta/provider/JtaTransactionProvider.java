/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.arquillian.transaction.jta.provider;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.transaction.spi.annotation.TransactionScope;
import org.jboss.arquillian.transaction.spi.provider.TransactionProvider;
import org.jboss.arquillian.transaction.spi.test.TransactionalTest;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * JTA transaction provider.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class JtaTransactionProvider implements TransactionProvider {

    /**
     * Represents the default JNDI name of the JTA transaction.
     */
    private static final String DEFAULT_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";

    /**
     * JNDI context.
     */
    @Inject
    private Instance<Context> contextInstance;

    /**
     * The JTA transaction.
     */
    @Inject
    @TransactionScope
    private InstanceProducer<UserTransaction> userTransactionInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginTransaction(TransactionalTest transactionalTest) {

        try {
            // retrieves the jta transaction
            UserTransaction userTransaction = getUserTransaction(transactionalTest);
            userTransactionInstance.set(userTransaction);

            // begins the transaction
            userTransaction.begin();
        } catch (NotSupportedException e) {

            // TODO introduce new exception type?
            throw new RuntimeException("The transaction could not to be begun.", e);
        } catch (SystemException e) {

            // TODO introduce new exception type?
            throw new RuntimeException("The transaction could not to be begun.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitTransaction(TransactionalTest transactionalTest) {

        try {

            // commits the transaction
            userTransactionInstance.get().commit();
        } catch (Exception e) {

            throw new RuntimeException("Could not commit the transaction.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollbackTransaction(TransactionalTest transactionalTest) {

        try {

            // rollbacks the transaction
            userTransactionInstance.get().commit();
        } catch (Exception e) {

            throw new RuntimeException("Could not rollback the transaction.", e);
        }
    }

    /**
     * Retrieves the {@link UserTransaction} from JNDI context.
     *
     * @param transactionalTest the transaction test
     *
     * @return the {@link UserTransaction}
     */
    private UserTransaction getUserTransaction(TransactionalTest transactionalTest) {

        String jndiName = getJtaTransactionJndiName(transactionalTest);

        try {

            Context context = contextInstance.get();

            if (context == null) {
                throw new RuntimeException("No Naming Context available.");
            }

            return (UserTransaction) context.lookup(jndiName);
        } catch (NamingException e) {

            throw new RuntimeException("Failed obtaining transaction.", e);
        }
    }

    /**
     * Retrieves the JTA transaction JNDI name.
     *
     * @param transactionalTest the transactional test
     *
     * @return the JNDI name
     */
    private String getJtaTransactionJndiName(TransactionalTest transactionalTest) {
        String jndiName = DEFAULT_TRANSACTION_JNDI_NAME;

        if (transactionalTest.getManager() != null) {
            jndiName = transactionalTest.getManager();
        }

        return jndiName;
    }
}
