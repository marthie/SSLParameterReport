# SSLReport for servers
based on the code of TestSSLServer by Thomas Pornin (http://www.bolet.org/TestSSLServer/)

## Introduction

SSLReport gets public SSL/TLS information by connecting to a given host. Following data is fetched by SSLReport:

* Protocol versions
* Support for compression
* Cipher Suites
* Certificates

All public SSL/TLS handshake information and the certificate within the host response is displayed on the system console or on a web interface.
The information won't be evaluated by SSLreport but is free for the evaluation by individuals.

## Build SSLReport

SSLReport supports the maven build process. Get the source with git or download it as zip and run within the SSLReport folder:

```
mvn clean package
```

The maven-assembly plugin is used to make a single execution jar and is bounded to the **package** lifecycle of maven.
**During development the JDK 1.8u45 and java compiler 1.7 were used. Other java versions are untested!**

## Run SSLReport

### Get help

Get help on command line arguments by executing the command: `java -jar SSLReport-<Version>.jar --help`

### SSLReport system console

To get a SSL/TLS report output on system console from a given host execute this command:

```
java -jar SSLReport-<Version>.jar [--webName|-wn]=<host> [[-p|--port]=<port>]
```

### SSLReport web interface

Start the embedded Jetty server to get access to the web interface by executing:

```
java -jar SSLReport-<Version>.jar --server
```

With a http browser you can access the SSLReport web interface by the url: `http://localhost:8080/`
