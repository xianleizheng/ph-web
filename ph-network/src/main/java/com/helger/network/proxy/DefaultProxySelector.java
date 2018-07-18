package com.helger.network.proxy;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.priviledged.IPrivilegedAction;
import com.helger.commons.state.EHandled;
import com.helger.network.proxy.settings.IProxySettings;
import com.helger.network.proxy.settings.ProxySettingsManager;

public class DefaultProxySelector extends ProxySelector
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DefaultProxySelector.class);

  private final ProxySelector m_aDefault;

  public DefaultProxySelector (@Nullable final ProxySelector aDefault)
  {
    m_aDefault = aDefault;
  }

  /**
   * @param aURI
   *        The URI that a connection is required to. Never <code>null</code>.
   * @return May be <code>null</code> in which case no proxy will be used.
   */
  @Nullable
  protected List <Proxy> selectProxies (@Nonnull final URI aURI)
  {
    final String sProtocol = aURI.getScheme ();
    final String sHostName = aURI.getHost ();
    final int nPort = aURI.getPort ();
    final ICommonsList <IProxySettings> aProxySettings = ProxySettingsManager.findAllProxySettings (sProtocol,
                                                                                                    sHostName,
                                                                                                    nPort);

    // None found so far
    if (m_aDefault != null)
      return m_aDefault.select (aURI);

    return null;
  }

  @Override
  @Nonnull
  public List <Proxy> select (@Nonnull final URI aURI)
  {
    // Let's stick to the specs
    ValueEnforcer.notNull (aURI, "URI");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Selecting proxies for '" + aURI + "'");

    List <Proxy> ret = selectProxies (aURI);
    if (ret == null || ret.isEmpty ())
    {
      // Fall back to "no proxy"
      ret = new CommonsArrayList <> (Proxy.NO_PROXY);
    }

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("For '" + aURI + "' the following proxies were selected: " + ret);
    return ret;
  }

  /**
   * @param aURI
   *        The URI that the proxy at sa failed to serve. Never
   *        <code>null</code>.
   * @param aAddr
   *        The socket address of the proxy/SOCKS server. Never
   *        <code>null</code>.
   * @param ex
   *        The I/O exception thrown when the connect failed. Never
   *        <code>null</code>.
   * @return {@link EHandled}
   */
  @Nonnull
  protected EHandled handleConnectFailed (@Nonnull final URI aURI,
                                          @Nonnull final SocketAddress aAddr,
                                          @Nonnull final IOException ex)
  {
    return EHandled.UNHANDLED;
  }

  @Override
  public void connectFailed (@Nonnull final URI aURI, @Nonnull final SocketAddress aAddr, @Nonnull final IOException ex)
  {
    ValueEnforcer.notNull (aURI, "URI");
    ValueEnforcer.notNull (aAddr, "SockerAddr");
    ValueEnforcer.notNull (ex, "Exception");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Connection to '" + aURI + "' using " + aAddr + " failed", ex);

    if (handleConnectFailed (aURI, aAddr, ex).isUnhandled ())
    {
      // Pass to default (if present)
      if (m_aDefault != null)
        m_aDefault.connectFailed (aURI, aAddr, ex);
    }
  }

  public static boolean isInstalled ()
  {
    return IPrivilegedAction.proxySelectorGetDefault ().invokeSafe () instanceof DefaultProxySelector;
  }

  public static void install ()
  {
    final ProxySelector aDefault = IPrivilegedAction.proxySelectorGetDefault ().invokeSafe ();
    if (!(aDefault instanceof DefaultProxySelector))
    {
      IPrivilegedAction.proxySelectorSetDefault (new DefaultProxySelector (aDefault)).invokeSafe ();
    }
  }
}
