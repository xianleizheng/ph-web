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
package com.helger.mail.cte;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.codec.IByteArrayCodec;
import com.helger.commons.codec.IDecoder;
import com.helger.commons.id.IHasID;

/**
 * Base interface for a content transfer encoding. See
 * {@link EContentTransferEncoding} for predefined ones.
 *
 * @author Philip Helger
 */
public interface IContentTransferEncoding extends IHasID <String>, Serializable
{
  /**
   * @return A new decoder for this Content Transfer Encoding. May not be
   *         <code>null</code>.
   * @deprecated Use {@link #createCodec()} instead because it can encode and
   *             decode
   */
  @Nonnull
  @Deprecated
  default IDecoder <byte [], byte []> createDecoder ()
  {
    return createCodec ();
  }

  /**
   * @return A new encoder for this Content Transfer Encoding. May not be
   *         <code>null</code>.
   * @since 9.0.5
   */
  @Nonnull
  IByteArrayCodec createCodec ();
}
