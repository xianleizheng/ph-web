/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.servlet.http;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.http.EHTTPMethod;
import com.helger.http.EHTTPVersion;
import com.helger.servlet.async.AsyncServletRunnerDefault;
import com.helger.servlet.async.ExtAsyncContext2;
import com.helger.servlet.async.IAsyncServletRunner;
import com.helger.servlet.async.ServletAsyncSpec;

/**
 * Abstract handler based HTTP servlet with support for async processing.
 *
 * @author Philip Helger
 * @since 8.7.5
 */
public abstract class AbstractAsyncHttpServlet extends AbstractHttpServlet
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractAsyncHttpServlet.class);

  private static IAsyncServletRunner s_aAsyncServletRunner = new AsyncServletRunnerDefault ();

  private final ServletAsyncSpec m_aAsyncSpec;

  /**
   * Set the async runner to be used.
   *
   * @param aAsyncServletRunner
   *        The runner to be used. May not be <code>null</code>.
   */
  public static void setAsyncServletRunner (@Nonnull final IAsyncServletRunner aAsyncServletRunner)
  {
    ValueEnforcer.notNull (aAsyncServletRunner, "AsyncServletRunner");
    s_aAsyncServletRunner = aAsyncServletRunner;
  }

  /**
   * @return The global async runner. Never <code>null</code>.
   */
  @Nonnull
  public static IAsyncServletRunner getAsyncServletRunner ()
  {
    return s_aAsyncServletRunner;
  }

  /**
   * Default constructor for synchronous servlets.
   */
  protected AbstractAsyncHttpServlet ()
  {
    // By default synchronous
    this (ServletAsyncSpec.createSync ());
  }

  /**
   * Constructor.
   *
   * @param aAsyncSpec
   *        The async/sync spec to be used. May not be <code>null</code>.
   */
  protected AbstractAsyncHttpServlet (@Nonnull final ServletAsyncSpec aAsyncSpec)
  {
    m_aAsyncSpec = ValueEnforcer.notNull (aAsyncSpec, "AsyncSpec");
  }

  /**
   * @return <code>true</code> if this servlet acts synchronously (for certain
   *         HTTP methods), <code>false</code> if it acts asynchronously.
   */
  public final boolean isAsynchronous ()
  {
    return m_aAsyncSpec.isAsynchronous ();
  }

  /**
   * @return The internal async spec. Never <code>null</code>.
   */
  @Nonnull
  protected final ServletAsyncSpec internalGetAsyncSpec ()
  {
    return m_aAsyncSpec;
  }

  @Override
  protected void onServiceRequest (@Nonnull final IHttpServletHandler aOriginalHandler,
                                   @Nonnull final HttpServletRequest aOriginalHttpRequest,
                                   @Nonnull final HttpServletResponse aOriginalHttpResponse,
                                   @Nonnull final EHTTPVersion eOriginalHttpVersion,
                                   @Nonnull final EHTTPMethod eOriginalHttpMethod) throws ServletException, IOException
  {
    if (m_aAsyncSpec.isAsynchronous () && m_aAsyncSpec.isAsyncHTTPMethod (eOriginalHttpMethod))
    {
      // Run asynchronously

      final ExtAsyncContext2 aEAC = ExtAsyncContext2.create (aOriginalHttpRequest,
                                                             aOriginalHttpResponse,
                                                             eOriginalHttpVersion,
                                                             eOriginalHttpMethod,
                                                             m_aAsyncSpec);

      // Put into async processing queue
      s_aAsyncServletRunner.runAsync (aOriginalHttpRequest, aOriginalHttpResponse, aEAC, () -> {
        try
        {
          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug ("ASYNC request processing started: " + aEAC.getRequest ());

          aOriginalHandler.handle (aEAC.getRequest (),
                                   aEAC.getResponse (),
                                   aEAC.getHTTPVersion (),
                                   aEAC.getHTTPMethod ());
        }
        catch (final Throwable t)
        {
          s_aLogger.error ("Error processing async request " + aEAC.getRequest (), t);
          try
          {
            final String sErrorMsg = "Internal error processing your request. Please try again later. Technical details: " +
                                     t.getClass ().getName () +
                                     ":" +
                                     t.getMessage ();
            aEAC.getResponse ().getWriter ().write (sErrorMsg);
          }
          catch (final Throwable t2)
          {
            s_aLogger.error ("Error writing first exception to response", t2);
          }
        }
        finally
        {
          try
          {
            aEAC.complete ();
          }
          catch (final Throwable t)
          {
            s_aLogger.error ("Error completing async context", t);
          }
        }
      });
    }
    else
    {
      // Run synchronously
      aOriginalHandler.handle (aOriginalHttpRequest, aOriginalHttpResponse, eOriginalHttpVersion, eOriginalHttpMethod);
    }
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("AsyncSpec", m_aAsyncSpec).getToString ();
  }
}
