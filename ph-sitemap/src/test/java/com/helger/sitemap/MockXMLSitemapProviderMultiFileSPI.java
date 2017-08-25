/**
 * Copyright (C) 2016-2017 Philip Helger (www.helger.com)
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
package com.helger.sitemap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.url.SimpleURL;

@IsSPIImplementation
public final class MockXMLSitemapProviderMultiFileSPI implements IXMLSitemapProviderSPI
{
  @Nonnull
  public XMLSitemapURLSet createURLSet ()
  {
    final XMLSitemapURLSet ret = new XMLSitemapURLSet ();
    for (int i = 0; i < XMLSitemapURLSet.MAX_URLS_PER_FILE + 1; ++i)
      ret.addURL (new XMLSitemapURL (new SimpleURL ("http://www.helger.com?xx=" + i)));
    return ret;
  }
}
