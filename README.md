# ph-web

Java library with common web stuff. It contains the following sub-projects:
* ph-network
    * Contains general network related stuff like Authenticators, DNS helpers, port and proxy classes
* ph-http
    * Contains special HTTP related helpers 
* ph-useragent
    * Contains User-Agent/Browser related helpers 
* ph-servlet
    * Contains special javax.servlet related helpers 
* ph-mail
    * Contains javax.mail related helpers
* ph-smtp
    * Contains helper classes for asynchronous mail (re-)sending
* ph-httpclient
    * Contains Apache HTTP client related helpers
* ph-web
    * Other high-level abstraction for servlets, file-upload and web scopes
* ph-sitemap (since v9.0.0)
    * Containing Sitemap generation helper
* ph-xservlet (since v9.0.0)
    * Contains an extended Servlet framework (XServlet is just a name I gave it internally and not an official name)  
  
# News and noteworthy

* v9.1.4 - 2019-10-08
    * Updated to Apache httpcore 4.4.12
    * Updated to Apache httpclient 4.5.10
    * Extended `UnifiedRepsonse` and `UnifiedResponseDefaultSettings` API to make HTTP header value unification and quoting customizable.
    * Updated to Jakarta dependencies
    * `AbstractXServlet.destroy` is no longer final
* v9.1.3 - 2019-08-28
    * Updated to Apache httpclient 4.5.9
    * Updated to ph-commons 9.3.6
    * Added new method `IRequestWebScopeWithoutResponse.getURI()`
    * `HttpDebugger` is disabled by default
    * `AcceptMimeTypeHandler.safeParseMimeType` now automatically tries to decode RFC 2616 encoded values
* v9.1.2 - 2019-06-05
    * Updated to Apache httpclient 4.5.8
    * Updated to ph-commons 9.3.3
    * Added new method `LocalDateTime IWebScope.getScopeCreationDateTime ()`
    * Added new class `NetworkOnlineStatusDeterminator` in `ph-network`
* v9.1.1 - 2019-02-17
    * Updated to JSch 0.1.55
    * Updated to Apache httpcore 4.4.11
    * Updated to Apache httpclient 4.5.7
    * `XServletSettings` got a setting to set the `X-Frame-Options` HTTP header with a default value of `SAMEORIGIN`
    * `XServletFilterSecurityHttpReferrerPolicy` adds the header before the request instead of afterwards
    * Removed deprecated methods in classes `RequestField` and `SessionBackedRequestField`
    * `HttpClientFactory` can now handle "non proxy hosts" as well
* v9.1.0 - 2018-11-22
    * Fixed potential NPE in `EmailAttachmentMicroTypeConverter` if the attachment cannot be read
    * By default all request param values are now also Unicode normalized
    * Added possibility to specify a custom "param value cleanser" using `RequestWebScope.setParamValueCleanser`
    * Requires ph-commons 9.2.0
* v9.0.5 - 2018-10-11
    * Changed `IContentTransferEncoding` to deliver a full codec and not just a decoder
    * Extended `NetworkPortHelper` with a remote port status checker
    * Added new package `com.helger.http.tls` with TLS version and configuration enums
* v9.0.4 - 2018-09-17
    * Added forbidden character check in request parameter value handling
* v9.0.3 - 2018-09-14
    * Added new class `AbstractXFilterUnifiedResponse`
    * `IRequestParamMap` was extended to easily retrieve trimmed values as well
    * Updated to javax.mail 1.6.2
    * Extended `CSP2SourceList` to support kyword `'none'`
    * Extended `CSP2Policy` with new default directives
    * Fixed potential stack overflow in RequestParamMap
* v9.0.2 - 2018-07-24
    * Fixed OSGI ServiceProvider configuration
    * Requires ph-commons 9.1.3
    * Fixed `XServlet` status reset in case Servlet init failed
    * Added classes around `RequestFieldDataMultiValue`
    * Updated to Apache HttpCore 4.4.10
    * Reworked the ph-network/proxy handling (in an incompatible way)
    * Added `IRequestParameter.getAsStringTrimmed`
* v9.0.1 - 2018-04-18
    * Updated to javax.mail 1.6.1
    * Removed exclusion of javax.activation
    * `AbstractXServlet` is now derived from `HttpServlet` and no longer from `GenericServlet`
    * Extended `HttpClientFactory` methods slightly
* v9.0.0 - 2017-12-20
    * Updated to javax.mail 1.6.0
    * Updated to ph-commons 9.0.0
    * Added HTTP Referrer-Policy header support
    * Extracted `ph-sitemap` from `ph-web`
    * Added new sub-project `ph-xservlet` to contain an extended Servlet framework
    * `HttpClientFactory` supports customizable retry count 
* v8.8.2 - 2017-07-04
    * Made debug flag in certain `ResponseHandler*` implementations customizable
    * `ServletHelper` caught Exception logging can now be enabled and disabled
    * Custom `HostnameVerifier` in `HttpClientFactory`
    * `HttpDebugger` has an `afterRequest` method now 
* v8.8.1 - 2017-05-29
    * Improved exception handling in HTTP client helper
* v8.8.0 - 2017-05-10
    * New HTTP servlet class hierarchy for better separation of concerns
    * Started new Servlet base infrastructure
    * Added possibility to disable DNS client caching in `HttpClient`
* v8.7.4 - 2017-03-29
    * Improved PAC support to also handle IP6 addresses and `dnsResolveEx`
    * Updated to ph-commons 8.6.3
    * Updated to ph-schedule 3.6.1
* v8.7.3 - 2017-03-02
    * API extensions
    * Added package `com.helger.servlet.logging` based on https://github.com/librucha/servlet-logging-filter
* v8.7.2 - 2017-01-18
    * Request multipart parsing for uploaded files is now limited to MIME type "multipart/form-data"
* v8.7.1 - 2016-12-21
    * API extensions
    * Logging and JavaDoc improvements
* v8.7.0 - 2016-12-12
    * Binds to ph-commons 8.5.6
    * Extracted new subprojects `ph-servlet` and `ph-useragent`
* v8.6.3 - 2016-11-11
    * Binds to ph-commons 8.5.3
    * Work around some Tomcat/Jetty exceptions
* v8.6.2 - 2016-09-25
    * Added possibility to disable URL encoding (putting SESSION ID in URL) with WebSettings
* v8.6.1 - 2016-09-09
    * Binds to ph-commons 8.5.x
* v8.6.0 - 2016-08-21
    * Binds to ph-commons 8.4.x
* v8.5.0 - 2016-07-26
    * Binds to ph-commons 8.3.x

# Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-web</artifactId>
  <version>9.1.4</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
