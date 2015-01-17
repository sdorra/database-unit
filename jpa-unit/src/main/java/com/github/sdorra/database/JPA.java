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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The jpa annotation can be used to mark tests for which the in-memory database
 * should be started. The annnotation can be used on the class or on the method
 * level, to apply the jpa settings for all tests of a class or only for one
 * specific test.
 *
 * @author Sebastian Sdorra
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface JPA
{

  /**
   * Persistence unit name.
   *
   * @return persistence unit name
   */
  String value();

  /**
   * Path to a sql file in the classpath. This sql file is loaded on database
   * start.
   *
   * @return path to sql file
   */
  String sql() default "";

  /**
   * Encoding of the sql file.
   *
   * @return encoding of sql file
   */
  String encoding() default "UTF-8";

  /**
   * Set to {@code true} to start a transaction before the test begins and
   * commit it after the tests ends.
   *
   * @return {@code true} if auto transactions are enabled
   */
  boolean autoTransaction() default false;
}
