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
package com.helger.httpclient.config;

import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.thirdparty.ELicense;
import com.helger.commons.thirdparty.IThirdPartyModule;
import com.helger.commons.thirdparty.IThirdPartyModuleProviderSPI;
import com.helger.commons.thirdparty.ThirdPartyModule;
import com.helger.commons.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party
 * modules.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_ph_httpclient implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule HTTP_CORE = new ThirdPartyModule ("Apache HttpCore",
                                                                          "Apache",
                                                                          ELicense.APACHE2,
                                                                          new Version (4, 4, 12),
                                                                          "http://hc.apache.org/");
  public static final IThirdPartyModule HTTP_COMPONENTS = new ThirdPartyModule ("Apache HttpComponents",
                                                                                "Apache",
                                                                                ELicense.APACHE2,
                                                                                new Version (4, 5, 10),
                                                                                "http://hc.apache.org/");

  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { HTTP_CORE, HTTP_COMPONENTS };
  }
}
