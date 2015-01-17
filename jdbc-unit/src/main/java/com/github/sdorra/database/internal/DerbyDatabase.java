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



package com.github.sdorra.database.internal;

//~--- non-JDK imports --------------------------------------------------------

import com.github.sdorra.database.Database;
import com.github.sdorra.database.DatabaseException;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sebastian Sdorra 
 */
public class DerbyDatabase implements Database
{

  /** Field description */
  private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

  /** Field description */
  private static final String JDBC_URL = "jdbc:derby:memory:%s";

  /** Field description */
  private static final String JDBC_START_URL = JDBC_URL.concat(";create=true");

  /** Field description */
  private static final String JDBC_SHUTDOWN_URL = JDBC_URL.concat(";drop=true");

  /** Field description */
  private static final int STOP_SUCCESS = 45000;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param databaseName
   */
  public DerbyDatabase(String databaseName)
  {
    this.databaseName = databaseName;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Connection createConnection()
  {
    try
    {
      return DriverManager.getConnection(url(JDBC_URL));
    }
    catch (SQLException ex)
    {
      throw new DatabaseException("could not create jdbc connection", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param contextClass
   * @param resource
   * @param encoding
   */
  @Override
  public void load(Object contextClass, String resource, String encoding)
  {
    InputStream stream = contextClass.getClass().getResourceAsStream(resource);

    if (stream == null)
    {
      throw new DatabaseException(
        "could not find sql script ".concat(resource));
    }

    Reader reader = null;
    Connection connection = null;

    try
    {
      reader = new InputStreamReader(stream, encoding);
      connection = createConnection();
      new SQLScriptRunner(connection, true).runScript(reader);
    }
    catch (IOException ex)
    {
      throw new DatabaseException("could not load sql script", ex);
    }
    finally
    {
      Closeables.close(reader);
      Closeables.close(connection);
    }
  }

  /**
   * Method description
   *
   */
  @Override
  public void shutdown()
  {
    try
    {
      running = false;
      DriverManager.getConnection(url(JDBC_SHUTDOWN_URL)).close();
    }
    catch (SQLException ex)
    {
      if (ex.getErrorCode() != STOP_SUCCESS)
      {
        throw new DatabaseException("database shutdown failed");
      }

      // Shutdown success
    }
  }

  /**
   * Method description
   *
   */
  @Override
  public void start()
  {
    try
    {
      Class.forName(DRIVER);
    }
    catch (ClassNotFoundException ex)
    {
      throw new DatabaseException("could not find jdbc driver", ex);
    }

    try
    {
      DriverManager.getConnection(url(JDBC_START_URL)).close();
      running = true;
    }
    catch (SQLException ex)
    {
      throw new DatabaseException("could not create database", ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String getDriver()
  {
    return DRIVER;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String getUrl()
  {
    return url(JDBC_URL);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public boolean isRunning()
  {
    return running;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param template
   *
   * @return
   */
  private String url(String template)
  {
    return String.format(template, databaseName);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String databaseName;

  /** Field description */
  private boolean running = false;
}
