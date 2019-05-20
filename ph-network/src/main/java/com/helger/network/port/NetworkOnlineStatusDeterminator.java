/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.network.port;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.ExecutorServiceHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.datetime.PDTFactory;

/**
 * Global class to determine if the client is offline or not. The state is
 * cached for a configurable duration (by default 1 minute) and than
 * re-evaluated.
 *
 * @author Philip Helger
 * @since 9.1.2
 */
@ThreadSafe
public final class NetworkOnlineStatusDeterminator
{
  /** Default cache time is 1 minute */
  public static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes (1);

  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static Duration s_aCacheDuration = DEFAULT_CACHE_DURATION;
  @GuardedBy ("s_aRWLock")
  private static LocalDateTime s_aLastCheckDT = null;
  @GuardedBy ("s_aRWLock")
  private static ENetworkOnlineStatus s_eStatus = ENetworkOnlineStatus.UNDEFINED;

  @PresentForCodeCoverage
  private static final NetworkOnlineStatusDeterminator s_aInstance = new NetworkOnlineStatusDeterminator ();

  private NetworkOnlineStatusDeterminator ()
  {}

  /**
   * @return The current caching duration. Never <code>null</code>.
   */
  @Nonnull
  public static Duration getCacheDuration ()
  {
    return s_aRWLock.readLocked ( () -> s_aCacheDuration);
  }

  /**
   * Set the caching duration for the offline state.
   *
   * @param aCacheDuration
   *        The duration to use. May not be <code>null</code>.
   */
  public static void setCacheDuration (@Nonnull final Duration aCacheDuration)
  {
    ValueEnforcer.notNull (aCacheDuration, "CacheDuration");
    s_aRWLock.writeLocked ( () -> s_aCacheDuration = aCacheDuration);
  }

  /**
   * @return The last date time when the offline state was checked. May be
   *         <code>null</code> if the check was not performed yet.
   */
  @Nullable
  public static LocalDateTime getLastCheckDT ()
  {
    return s_aRWLock.readLocked ( () -> s_aLastCheckDT);
  }

  /**
   * @return The current offline state from cache only. No update is performed.
   *         Never <code>null</code>.
   */
  @Nonnull
  public static ENetworkOnlineStatus getCachedNetworkStatus ()
  {
    return s_aRWLock.readLocked ( () -> s_eStatus);
  }

  /**
   * Check if the system is offline or not. This method uses the cache.
   *
   * @return The online/offline status. Never <code>null</code>.
   */
  @Nonnull
  public static ENetworkOnlineStatus getNetworkStatus ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      if (s_eStatus.isDefined ())
      {
        // Can we use the cached value?
        final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
        if (s_aLastCheckDT != null && s_aLastCheckDT.plus (s_aCacheDuration).isAfter (aNow))
          return s_eStatus;
      }
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }

    // An update is needed
    return getNetworkStatusNoCache ();
  }

  /**
   * Check if the system is offline or not. This method does NOT use the cache.
   *
   * @return The online/offline status. Never <code>null</code>.
   */
  @Nonnull
  public static ENetworkOnlineStatus getNetworkStatusNoCache ()
  {
    final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
    s_aRWLock.writeLock ().lock ();
    try
    {
      // Check all host names in parallel, if they are reachable
      final ICommonsList <String> aHostNames = new CommonsArrayList <> ("www.google.com",
                                                                        "www.facebook.com",
                                                                        "www.microsoft.com");
      final ExecutorService aES = Executors.newFixedThreadPool (aHostNames.size ());
      final AtomicInteger aReachable = new AtomicInteger (0);
      for (final String sHostName : aHostNames)
        aES.submit ( () -> {
          // Silent mode, timeout 2 seconds
          if (NetworkPortHelper.checkPortOpen (sHostName, 80, 2000, true).isPortOpen ())
            aReachable.incrementAndGet ();
        });
      ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (aES);
      s_eStatus = aReachable.intValue () > 0 ? ENetworkOnlineStatus.ONLINE : ENetworkOnlineStatus.OFFLINE;
      s_aLastCheckDT = aNow;
      return s_eStatus;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }
}
