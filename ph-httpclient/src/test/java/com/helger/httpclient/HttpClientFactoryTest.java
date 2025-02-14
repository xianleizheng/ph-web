/**
 * Copyright (C) 2016-2019 Philip Helger (www.helger.com)
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
package com.helger.httpclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link HttpClientFactory}.
 *
 * @author Philip Helger
 */
public final class HttpClientFactoryTest
{
  @Test
  public void testAddNonProxyHostsFromPipeString ()
  {
    final HttpClientFactory x = new HttpClientFactory ();
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString (null);
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString ("");
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString ("          ");
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString ("   |    ");
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString ("  |||||   ||  |||| |   |");
    assertEquals (0, x.nonProxyHosts ().size ());
    x.addNonProxyHostsFromPipeString (" 127.0.0.1 | localhost ");
    assertEquals (2, x.nonProxyHosts ().size ());
    assertTrue (x.nonProxyHosts ().contains ("127.0.0.1"));
    assertTrue (x.nonProxyHosts ().contains ("localhost"));
    x.addNonProxyHostsFromPipeString ("127.0.0.1|localhost");
    assertEquals (2, x.nonProxyHosts ().size ());
    assertTrue (x.nonProxyHosts ().contains ("127.0.0.1"));
    assertTrue (x.nonProxyHosts ().contains ("localhost"));
    x.addNonProxyHostsFromPipeString ("127.0.0.1|127.0.0.1|127.0.0.1|127.0.0.1|127.0.0.1|127.0.0.1|127.0.0.1|127.0.0.1|localhost");
    assertEquals (2, x.nonProxyHosts ().size ());
    assertTrue (x.nonProxyHosts ().contains ("127.0.0.1"));
    assertTrue (x.nonProxyHosts ().contains ("localhost"));
  }
}
