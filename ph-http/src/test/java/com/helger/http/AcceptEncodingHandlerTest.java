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
package com.helger.http;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;

public final class AcceptEncodingHandlerTest
{
  @Test
  public void testReadFromSpecs ()
  {
    assertNotNull (AcceptEncodingHandler.getAcceptEncodings ("compress, gzip"));
    assertNotNull (AcceptEncodingHandler.getAcceptEncodings (""));
    assertNotNull (AcceptEncodingHandler.getAcceptEncodings ("*"));
    assertNotNull (AcceptEncodingHandler.getAcceptEncodings ("compress;q=0.5, gzip;q=1.0"));
    assertNotNull (AcceptEncodingHandler.getAcceptEncodings ("gzip;q=1.0, identity; q=0.5, *;q=0"));
  }

  @Test
  public void testReadFromSpecs2 ()
  {
    AcceptEncodingList aAE = AcceptEncodingHandler.getAcceptEncodings ("compress, gzip");
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("identity"));
    aAE = AcceptEncodingHandler.getAcceptEncodings ("");
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("identity"));
    aAE = AcceptEncodingHandler.getAcceptEncodings ("*");
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("identity"));
    aAE = AcceptEncodingHandler.getAcceptEncodings ("compress;q=0.5, gzip;q=1.0");
    CommonsAssert.assertEquals (0.5, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("identity"));
    aAE = AcceptEncodingHandler.getAcceptEncodings ("gzip;q=1.0, identity; q=0.5, *;q=0");
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (0, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (0.5, aAE.getQualityOfEncoding ("identity"));
    aAE = AcceptEncodingHandler.getAcceptEncodings ("gzip;q=1.0, identity; q=0.5, *;q=0.7");
    CommonsAssert.assertEquals (0.7, aAE.getQualityOfEncoding ("compress"));
    CommonsAssert.assertEquals (1, aAE.getQualityOfEncoding ("gzip"));
    CommonsAssert.assertEquals (0.7, aAE.getQualityOfEncoding ("other"));
    CommonsAssert.assertEquals (0.5, aAE.getQualityOfEncoding ("identity"));
  }
}
