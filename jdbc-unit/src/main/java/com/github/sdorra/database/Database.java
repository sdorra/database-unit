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

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;

/**
 * The database interface helps to interact with the started in-memory database.
 *
 * @author Sebastian Sdorra 
 */
public interface Database
{

  /**
   * Create a new jdbc connection.
   *
   *
   * @return new connection
   */
  public Connection createConnection();

  /**
   * Loads and executes an sql script from the classpath.
   *
   *
   * @param contextClass context object is used to obtain the class loader
   * @param resource path to the sql script
   * @param encoding encoding of the script
   */
  public void execute(Object contextClass, String resource, String encoding);

  /**
   * Shutdown the in-memory database. This method is automatically invoked, 
   * after the execution of the unit test.
   *
   */
  public void shutdown();

  /**
   * Starts the in-memory database. This method is automatically invoked, 
   * before the execution of the unit test.
   *
   */
  public void start();

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the jdbc driver class for the in-memory database connection.
   *
   *
   * @return jdbc driver class
   */
  public String getDriver();

  /**
   * Returns the jdbc url for the in-memory database connection.
   *
   *
   * @return jdbc ulr
   */
  public String getUrl();

  /**
   * Returns {@code true} if the the database is running.
   *
   *
   * @return {@code true} if the the database is running
   */
  public boolean isRunning();
}
