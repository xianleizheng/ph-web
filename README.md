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
  * ph-httpclient
    * Contains Apache HTTP client related helpers
  * ph-mail
    * Contains javax.mail related helpers
  * ph-smtp
    * Contains helper classes for asynchronous mail (re-)sending
  * ph-web
    * Other high-level abstraction for servlets, file-upload, browser information, sitemap, web scopes etc.
  
# News and noteworthy

  * v8.8.3 - work in progress
    * Updated to javax.mail 1.6.0
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
  <version>8.8.2</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
