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

import java.sql.Connection;

/**
 * The JDBCRule starts an in-memory sql database (namely apache derby). The
 * database is started before each test method is executed and the database is
 * shutdown after the method execution.
 *
 * @author Sebastian Sdorra 
 */
public class JDBCRule implements MethodRule
{

  /**
   * {@inheritDoc}
   */
  @Override
  public Statement apply(final Statement base, final FrameworkMethod method,
    final Object target)
  {
    final JDBC jdbc = Annotations.find(method, target, JDBC.class);

    return new Statement()
    {

      @Override
      public void evaluate() throws Throwable
      {
        database.start();

        String sql = getSQLScript(jdbc);

        if (sql != null)
        {
          database.execute(target, sql, jdbc.encoding());
        }

        try
        {
          base.evaluate();
        }
        finally
        {
          database.shutdown();

          if (connection != null)
          {
            connection.close();
          }
        }
      }
    };
  }

  /**
   * Creates a new jdbc connection for the in-memory database. Note this
   * connection is not closed atfer the method execution.
   *
   * @return jdbc connection
   */
  public Connection createConnection()
  {
    return database.createConnection();
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns a connection which is closed after the method finished the
   * execution.
   *
   * @return jdbc connection
   */
  public Connection getConnection()
  {
    if (connection == null)
    {
      connection = createConnection();
    }

    return connection;
  }

  /**
   * Returns the in-memory database.
   *
   *
   * @return in-memory database.
   */
  public Database getDatabase()
  {
    return database;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param jdbc
   *
   * @return
   */
  private String getSQLScript(JDBC jdbc)
  {
    String sql = null;

    if (jdbc != null)
    {
      String script = jdbc.sql();

      if ((script != null) && (script.length() > 0))
      {
        sql = script;
      }
    }

    return sql;
  }

  //~--- fields ---------------------------------------------------------------

  /** database */
  private final Database database = new DerbyDatabase("jdbc-unit");

  /** jdbc database connection */
  private Connection connection;
}
