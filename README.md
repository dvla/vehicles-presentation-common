Master [![Build Status](https://travis-ci.org/dvla/vehicles-online.svg?branch=master)](https://travis-ci.org/dvla/vehicles-online)

DVLA Vehicles Online
====================

`vehicles-online` is the Web frontend for disposing vehicles online, where 'disposing' implies changing a vehicle's
ownership through trade.

Architectural overview
----------------------

### Presentation layer

This project encapsulates the presentation layer of the application.

The codebase is predominantly [Scala][scala] and is implemented against [Play][play-framework]: a 'full stack' Web
framework for the JVM.

### Microservices

Most complex business decisions are deferred to a network of [RESTful][rest] microservices. These are maintained through
separate projects:

-   `ordnance-survey`
-   `vehicles-lookup`
-   `vehicles-dispose-fulfil`

These services are mocked for automated testing, but must be running locally for manual testing/development of dependant
components within the presentation layer.

Development environment
-----------------------

1.  Each project must be checked-out to the same directory using the following names:

    -   `vehicles-online`
    -   `vehicles-dispose-fulfil`
    -   `vehicles-lookup`
    -   `<<the appropriate secrets repo>>'
    -   `os-address-lookup`

2.  JDK 1.7 must be installed

3.  Install SASS. The [current documentation][install-sass] suggests:

        sudo gem install sass

4.  Install SBT.  The [current documentation][install-sbt] suggests:

        brew install sbt

5.  Increase 'permanent generation space' requirements for SBT.

    Create the file `~/.sbtconfig` with the following content:

        SBT_OPTS="$SBT_OPTS -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:PermSize=256M -XX:MaxPermSize=2048M"

6.  Decrypt secret keys:

        cd <<the appropriate secrets repo>>
        ./setup XYZ

    *where `XYZ` is an offline secret key obtained through a trusted team member*

Running the application
-----------------------

1.  Run the `vehicles-online` application:

        cd vehicles-online
        sbt run

2.  Open in Web browser:

        http://localhost:9000/

3.  Repeat *step 1* for each required microservice (if any).


### Running with production logging

To emulate production-level logging:

1.  Ensure `syslog` is configured. Details have been provided for [configuring `syslog` on OSX][syslog-osx].

2.  Run the `vehicles-online` application:

        cd vehicles-online
        ./startWithLog.sh
        
3.  Open in Web browser:

        http://localhost:9000/

Running the sandbox
-------------------
Vehicles-online depends on three services in order to successfully complete a disposal. These are:

1. os-address-lookup
1. vehicles-lookup
1. vehicles-dispose-fulfil

Vehicles-lookup and vehicles-dispose-fulfil depend on two legacy stub services.
All these are located in different git repositories and need to be manually cloned built and run if the vehicles-online is going to run normally.
The sandbox provides a way to run all the required microservices along with the vehicles-online itself with a single command.

The sandbox is implemented as a single sbt task so you can run it like every other sbt task. Just do ```sbt sandbox```. This will download the latest versions of the services and will start them. After the sandbox is started the app is running on http://localhost:9000 and the user should be able to go all the way to disposing a vehicle.

If here are any local changes to the vehicles-online code base they would be automatically picked up next time the browser is refreshed.



Session encryption
------------------

Please refer to the [session encryption][session-encryption] document for details on the encryption algorithm used.

[install-sass]: http://sass-lang.com/install "Install SASS"
[install-sbt]: http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html#installing-sbt "Install SBT"
[rest]: https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm "REST"
[play-framework]: http://www.playframework.com/ "Play Framework"
[scala]: http://www.scala-lang.org/ "Scala Language"
[syslog-osx]: syslog-osx.md "Configuring syslog on OSX"
[session-encryption]: encrypted-session-state.md "Session Encryption"