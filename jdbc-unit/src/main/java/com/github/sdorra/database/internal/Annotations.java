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

import org.junit.runners.model.FrameworkMethod;

//~--- JDK imports ------------------------------------------------------------

import java.lang.annotation.Annotation;

/**
 * Util class to handle annotations.
 *
 * @author Sebastian Sdorra 
 */
public final class Annotations
{

  /**
   * Constructs ...
   *
   */
  private Annotations() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Searches a annotation. The method will first look at the method and after 
   * that it will check the class of the target object.
   *
   *
   * @param method method
   * @param target target object
   * @param type annotation
   * @param <T> annotation type
   *
   * @return found annotation or null
   */
  public static <T extends Annotation> T find(FrameworkMethod method,
    Object target, Class<T> type)
  {
    T annotation = method.getAnnotation(type);

    if (annotation == null)
    {
      annotation = target.getClass().getAnnotation(type);
    }

    return annotation;
  }
}
