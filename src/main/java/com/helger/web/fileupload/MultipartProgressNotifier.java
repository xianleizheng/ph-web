package com.helger.web.fileupload;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;

/**
 * Internal class, which is used to invoke the {@link IProgressListener}.
 */
public final class MultipartProgressNotifier
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MultipartProgressNotifier.class);
  /**
   * The listener to invoke.
   */
  private final IProgressListener m_aListener;
  /**
   * Number of expected bytes, if known, or -1.
   */
  private final long m_nContentLength;
  /**
   * Number of bytes, which have been read so far.
   */
  private long m_nBytesRead;
  /**
   * Number of items, which have been read so far.
   */
  private int m_nItems;

  /**
   * Creates a new instance with the given listener and content length.
   *
   * @param aListener
   *        The listener to invoke.
   * @param nContentLength
   *        The expected content length.
   */
  MultipartProgressNotifier (@Nullable final IProgressListener aListener, final long nContentLength)
  {
    if (aListener != null && s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("setting listener " + aListener.getClass ().getName ());
    m_aListener = aListener;
    m_nContentLength = nContentLength;
  }

  /**
   * Called for notifying the listener.
   */
  private void _notifyListener ()
  {
    if (m_aListener != null)
      m_aListener.update (m_nBytesRead, m_nContentLength, m_nItems);
  }

  /**
   * Called to indicate that bytes have been read.
   *
   * @param nBytes
   *        Number of bytes, which have been read.
   */
  void noteBytesRead (@Nonnegative final int nBytes)
  {
    ValueEnforcer.isGE0 (nBytes, "Bytes");
    /*
     * Indicates, that the given number of bytes have been read from the input
     * stream.
     */
    m_nBytesRead += nBytes;
    _notifyListener ();
  }

  /**
   * Called to indicate, that a new file item has been detected.
   */
  void noteItem ()
  {
    ++m_nItems;
    _notifyListener ();
  }
}