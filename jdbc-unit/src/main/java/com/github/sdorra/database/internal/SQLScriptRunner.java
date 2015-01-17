/*
 * Modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project.
 */
/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */



package com.github.sdorra.database.internal;

//~--- non-JDK imports --------------------------------------------------------

import com.github.sdorra.database.DatabaseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Execute sql scripts on the given database.
 */
public class SQLScriptRunner
{

  /** Field description */
  private static final String DEFAULT_DELIMITER = ";";

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(SQLScriptRunner.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Default constructor.
   * 
   * @param connection jdbc connection
   * @param autoCommit auto commit
   */
  public SQLScriptRunner(Connection connection, boolean autoCommit)
  {
    this.connection = connection;
    this.autoCommit = autoCommit;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Reads and executes the sql script from the given reader.
   *
   * @param reader reader for the sql script
   */
  public void runScript(Reader reader)
  {
    try
    {
      boolean originalAutoCommit = connection.getAutoCommit();

      try
      {
        if (originalAutoCommit != this.autoCommit)
        {
          connection.setAutoCommit(this.autoCommit);
        }

        runScript(connection, reader);
      }
      finally
      {
        connection.setAutoCommit(originalAutoCommit);
      }
    }
    catch (IOException ex)
    {
      throw new DatabaseException("failed to run sql script", ex);
    }
    catch (SQLException ex)
    {
      throw new DatabaseException("failed to run sql script", ex);
    }
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param delimiter
   * @param fullLineDelimiter
   */
  public void setDelimiter(String delimiter, boolean fullLineDelimiter)
  {
    this.delimiter = delimiter;
    this.fullLineDelimiter = fullLineDelimiter;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param conn
   * @param reader
   *
   * @throws IOException
   * @throws SQLException
   */
  private void runScript(Connection conn, Reader reader)
    throws IOException, SQLException
  {
    StringBuilder buffer = null;

    try
    {
      LineNumberReader lineReader = new LineNumberReader(reader);
      String line;

      while ((line = lineReader.readLine()) != null)
      {
        if (buffer == null)
        {
          buffer = new StringBuilder();
        }

        String trimmedLine = line.trim();

        if (trimmedLine.startsWith("--"))
        {
          logger.trace(trimmedLine);
        }
        else if ((trimmedLine.length() < 1) || trimmedLine.startsWith("//"))
        {

          // do nothing
        }
        else if ((trimmedLine.length() < 1) || trimmedLine.startsWith("--"))
        {

          // do nothing
        }
        else if ((!fullLineDelimiter && trimmedLine.endsWith(getDelimiter()))
          || (fullLineDelimiter && trimmedLine.equals(getDelimiter())))
        {
          buffer.append(line.substring(0, line.lastIndexOf(getDelimiter())));
          buffer.append(" ");

          Statement statement = conn.createStatement();

          String command = buffer.toString();

          logger.trace(command);

          boolean hasResults = statement.execute(command);

          if (autoCommit &&!conn.getAutoCommit())
          {
            conn.commit();
          }

          ResultSet rs = statement.getResultSet();

          if (hasResults && (rs != null))
          {
            StringBuilder secBuffer = new StringBuilder();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            for (int i = 0; i < cols; i++)
            {
              String name = md.getColumnLabel(i);

              secBuffer.append(name).append("\t");
            }

            logger.trace(secBuffer.toString());
            secBuffer = new StringBuilder();

            while (rs.next())
            {
              for (int i = 0; i < cols; i++)
              {
                String value = rs.getString(i);

                secBuffer.append(value).append("\t");
              }

              logger.trace(secBuffer.toString());
            }
          }

          buffer = null;

          try
          {
            statement.close();
          }
          catch (Exception e)
          {

            // Ignore to workaround a bug in Jakarta DBCP
          }
        }
        else
        {
          buffer.append(line);
          buffer.append(" ");
        }
      }

      if (!autoCommit)
      {
        conn.commit();
      }
    }
    catch (SQLException ex)
    {
      throw new DatabaseException("error durring script execution", ex);
    }
    catch (IOException ex)
    {
      throw new DatabaseException("error durring script execution", ex);
    }
    finally
    {
      conn.rollback();
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private String getDelimiter()
  {
    return delimiter;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final boolean autoCommit;

  /** Field description */
  private final Connection connection;

  /** Field description */
  private String delimiter = DEFAULT_DELIMITER;

  /** Field description */
  private boolean fullLineDelimiter = false;
}
