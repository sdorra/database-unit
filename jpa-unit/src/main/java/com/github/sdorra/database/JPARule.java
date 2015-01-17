/*
 * The MIT License
 *
 * Copyright (c) 2015, Sebastian Sdorra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */



package com.github.sdorra.database;

//~--- non-JDK imports --------------------------------------------------------

import com.github.sdorra.database.internal.Annotations;
import com.github.sdorra.database.internal.DerbyDatabase;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author Sebastian Sdorra 
 */
public class JPARule implements MethodRule
{

  /** Field description */
  private static final String EMPTY = "";

  /** Field description */
  private static final String PROPERTY_CREATE =
    "javax.persistence.schema-generation.database.action";

  /** Field description */
  private static final String PROPERTY_DRIVER = "javax.persistence.jdbc.driver";

  /** Field description */
  private static final String PROPERTY_HIBERNATE_DIALECT = "hibernate.dialect";

  /** Field description */
  private static final String PROPERTY_PASSWORD =
    "javax.persistence.jdbc.password";

  /** Field description */
  private static final String PROPERTY_URL = "javax.persistence.jdbc.url";

  /** Field description */
  private static final String PROPERTY_USER = "javax.persistence.jdbc.user";

  /** Field description */
  private static final String VALUE_CREATE = "create";

  /** Field description */
  private static final String VALUE_HIBERNATE_DIALECT =
    "org.hibernate.dialect.DerbyTenSevenDialect";

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param base
   * @param method
   * @param target
   *
   * @return
   */
  @Override
  public Statement apply(final Statement base, final FrameworkMethod method,
    final Object target)
  {
    final JPA jpa = Annotations.find(method, target, JPA.class);

    return new Statement()
    {

      @Override
      public void evaluate() throws Throwable
      {
        if (jpa != null)
        {
          database.start();

          jpaUnit = jpa.value();

          String sql = jpa.sql();

          if (sql.trim().length() > 0)
          {
            database.load(target, sql, jpa.encoding());
          }

          EntityTransaction transaction = null;

          try
          {
            if (jpa.autoTransaction())
            {
              transaction = getEntityManager().getTransaction();
              transaction.begin();
            }

            base.evaluate();

            if (transaction != null)
            {
              transaction.commit();
            }
          }
          finally
          {
            close(transaction);
          }
        }
        else
        {
          base.evaluate();
        }
      }
    };
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public EntityManager createEntityManager()
  {
    checkIsRunning();

    return getEntityManagerFactory().createEntityManager();
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public Database getDatabase()
  {
    return database;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public EntityManager getEntityManager()
  {
    if (entityManager == null)
    {
      entityManager = createEntityManager();
    }

    return entityManager;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public EntityManagerFactory getEntityManagerFactory()
  {
    if (entityManagerFactory == null)
    {
      checkIsRunning();

      Map<String, String> props = new HashMap<String, String>();

      props.put(PROPERTY_DRIVER, database.getDriver());
      props.put(PROPERTY_URL, database.getUrl());
      props.put(PROPERTY_USER, EMPTY);
      props.put(PROPERTY_PASSWORD, EMPTY);
      props.put(PROPERTY_CREATE, VALUE_CREATE);

      // put hibernate specific properties
      props.put(PROPERTY_HIBERNATE_DIALECT, VALUE_HIBERNATE_DIALECT);

      // create entity manager factory
      entityManagerFactory = Persistence.createEntityManagerFactory(jpaUnit,
        props);
    }

    return entityManagerFactory;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getJpaUnit()
  {
    return jpaUnit;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  private void checkIsRunning()
  {
    if (!database.isRunning())
    {
      String msg =
        "the database is not started. Perhaps you have forgotten to "
        + "add the JPA annotation to the class or method.";

      throw new DatabaseNotStartedException(msg);
    }
  }

  /**
   * Method description
   *
   *
   * @param transaction
   */
  private void close(EntityTransaction transaction)
  {
    if ((transaction != null) && transaction.isActive())
    {
      transaction.rollback();
    }

    if (entityManager != null)
    {
      entityManager.close();
    }

    if (entityManagerFactory != null)
    {
      entityManagerFactory.close();
    }

    database.shutdown();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Database database = new DerbyDatabase("jpa-unit");

  /** Field description */
  private EntityManager entityManager;

  /** Field description */
  private EntityManagerFactory entityManagerFactory;

  /** Field description */
  private String jpaUnit;
}
