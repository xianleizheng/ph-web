package com.helger.network.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.url.URLHelper;
import com.helger.network.proxy.settings.ProxySettings;
import com.helger.network.proxy.settings.ProxySettingsManager;

/**
 * Test class for class {@link ProxySelectorProxySettingsManager}.
 *
 * @author Philip Helger
 */
public final class ProxySelectorProxySettingsManagerTest
{
  @Before
  public void before ()
  {
    ProxySettingsManager.removeAllProviders ();
    ProxySelectorProxySettingsManager.setAsDefault ();
  }

  @Test
  public void testNoConfiguration ()
  {
    final URI aURI = URLHelper.getAsURI ("http://www.helger.com/blafoo");

    // Nothing configured -> will use direct proxy
    final List <Proxy> aProxies = ProxySelector.getDefault ().select (aURI);
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.DIRECT, aProxies.get (0).type ());
  }

  @Test
  public void testSimpleResolver ()
  {
    ProxySettingsManager.registerProvider ( (sProtocol,
                                             sHostName,
                                             nPort) -> new CommonsArrayList <> (new ProxySettings (Proxy.Type.HTTP,
                                                                                                   "http://proxysrv",
                                                                                                   8080)));

    // Will choose the proxy
    final List <Proxy> aProxies = ProxySelector.getDefault ()
                                               .select (URLHelper.getAsURI ("http://www.helger.com/blafoo"));
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.HTTP, aProxies.get (0).type ());
    assertEquals ("http://proxysrv", ((InetSocketAddress) aProxies.get (0).address ()).getHostString ());
    assertEquals (8080, ((InetSocketAddress) aProxies.get (0).address ()).getPort ());
  }

  @Test
  public void testDifferentProtocol ()
  {
    ProxySettingsManager.registerProvider ( (sProtocol,
                                             sHostName,
                                             nPort) -> new CommonsArrayList <> (new ProxySettings (Proxy.Type.HTTP,
                                                                                                   "http://proxysrv",
                                                                                                   "http".equals (sProtocol) ? 8080
                                                                                                                             : 8443)));

    // Will choose the http port
    List <Proxy> aProxies = ProxySelector.getDefault ().select (URLHelper.getAsURI ("http://www.helger.com/blafoo"));
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.HTTP, aProxies.get (0).type ());
    assertEquals ("http://proxysrv", ((InetSocketAddress) aProxies.get (0).address ()).getHostString ());
    assertEquals (8080, ((InetSocketAddress) aProxies.get (0).address ()).getPort ());

    // Will choose the https port
    aProxies = ProxySelector.getDefault ().select (URLHelper.getAsURI ("https://www.helger.com/blafoo"));
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.HTTP, aProxies.get (0).type ());
    assertEquals ("http://proxysrv", ((InetSocketAddress) aProxies.get (0).address ()).getHostString ());
    assertEquals (8443, ((InetSocketAddress) aProxies.get (0).address ()).getPort ());
  }

  @Test
  public void testDifferentHost ()
  {
    ProxySettingsManager.registerProvider ( (sProtocol,
                                             sHostName,
                                             nPort) -> new CommonsArrayList <> (sHostName.contains (".orf.at") ? ProxySettings.createNoProxySettings ()
                                                                                                               : new ProxySettings (Proxy.Type.HTTP,
                                                                                                                                    "http://proxysrv",
                                                                                                                                    8080)));

    // Will choose the http proxy
    List <Proxy> aProxies = ProxySelector.getDefault ().select (URLHelper.getAsURI ("http://www.helger.com/blafoo"));
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.HTTP, aProxies.get (0).type ());
    assertEquals ("http://proxysrv", ((InetSocketAddress) aProxies.get (0).address ()).getHostString ());
    assertEquals (8080, ((InetSocketAddress) aProxies.get (0).address ()).getPort ());

    // Will choose the no proxy
    aProxies = ProxySelector.getDefault ().select (URLHelper.getAsURI ("http://anyserver.orf.at/blafoo"));
    assertNotNull (aProxies);
    assertEquals (1, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.DIRECT, aProxies.get (0).type ());
  }

  @Test
  public void testMultipleProxies ()
  {
    ProxySettingsManager.registerProvider ( (sProtocol,
                                             sHostName,
                                             nPort) -> new CommonsArrayList <> (new ProxySettings (Proxy.Type.HTTP,
                                                                                                   "http://proxysrv",
                                                                                                   8080),
                                                                                new ProxySettings (Proxy.Type.HTTP,
                                                                                                   "http://proxysrv2",
                                                                                                   8080)));

    // Will choose the http proxy
    final List <Proxy> aProxies = ProxySelector.getDefault ()
                                               .select (URLHelper.getAsURI ("http://www.helger.com/blafoo"));
    assertNotNull (aProxies);
    assertEquals (2, aProxies.size ());
    assertNotNull (aProxies.get (0));
    assertEquals (Proxy.Type.HTTP, aProxies.get (0).type ());
    assertEquals ("http://proxysrv", ((InetSocketAddress) aProxies.get (0).address ()).getHostString ());
    assertEquals (8080, ((InetSocketAddress) aProxies.get (0).address ()).getPort ());

    assertNotNull (aProxies.get (1));
    assertEquals (Proxy.Type.HTTP, aProxies.get (1).type ());
    assertEquals ("http://proxysrv2", ((InetSocketAddress) aProxies.get (1).address ()).getHostString ());
    assertEquals (8080, ((InetSocketAddress) aProxies.get (1).address ()).getPort ());
  }
}
