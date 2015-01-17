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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.Closeable;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Util class to handle closeable objects.
 *
 * @author Sebastian Sdorra 
 */
public final class Closeables
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(Closeables.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  private Closeables() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Closes the given closeable.
   *
   *
   * @param closeable closeable
   */
  public static void close(Closeable closeable)
  {
    if (closeable != null)
    {
      try
      {
        closeable.close();
      }
      catch (IOException ex)
      {
        logger.trace("could not close closeable", ex);
      }
    }
  }

  /**
   * Closes the given connection.
   *
   *
   * @param connection connection
   */
  public static void close(Connection connection)
  {
    if (connection != null)
    {
      try
      {
        connection.close();
      }
      catch (SQLException ex)
      {
        logger.trace("could not close connection", ex);
      }
    }
  }
}
