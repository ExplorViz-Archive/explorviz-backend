# ExplorViz Backend
<a href="https://travis-ci.org/ExplorViz/explorviz-backend"><img src="https://travis-ci.org/ExplorViz/explorviz-backend.svg?branch=master" alt="Build Status"></a>
[![](https://img.shields.io/docker/pulls/explorviz/explorviz-docker.svg)](https://hub.docker.com/r/explorviz/explorviz-docker "Click to view the image on Docker Hub")

This project contains the modernized version of [ExplorViz](https://github.com/ExplorViz/Explorviz). 

It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. 
This is only the backend, you will need the [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend) as well.

This gradle project includes or installs every necessary dependency.

### Eclipse Setup
1. Download the latest [**Eclipse IDE for Java Developers**](http://www.eclipse.org/downloads/eclipse-packages/) and [**Oracale Java SDK 8**](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. Install **SpotBugs (formerly known as Findbugs)**
    - *Start Eclipse -> Help -> Eclipse Marketplace* - type in the following and install **SpotBugs Eclipse plugin 3.1.2*
3. Install **PMD** and **Checkstyle** embedded in [qa-eclipse-plugin
](https://github.com/ChristianWulf/qa-eclipse-plugin)
    - In Eclipse: *Help -> Install New Software -> Insert https://build.se.informatik.uni-kiel.de/eus/qa/snapshot/ in "Work with" -> Check "QA tooling" -> Finish, download, and install*
    - Follow the configuration as listed [here](https://github.com/ChristianWulf/qa-eclipse-plugin)
4. Configure the respective tool views (In Eclipse: *Window -> Show View -> Other -> ...)

## Setup Development
1. Follow the [Eclipse Setup](https://github.com/ExplorViz/explorviz-backend#eclipse-setup)
2. Clone this repository
3. Import project into eclipse: via *Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend*
4. Start **explorviz-backend** via Eclipse Tab: *Gradle Tasks -> explorviz-backend -> gretty -> appStart*
5. Setup and start [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

To force the download of newly added dependencies: *Right click explorviz-backend -> Grade -> Refresh Gradle Project*. 

## Quick start with sample (dummy) monitoring

1. Follow the above [Setup Development](#setup-development) procedure
2. Clone [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication)
3. Start the [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication) as described [here](https://github.com/czirkelbach/kiekerSampleApplication/blob/master/README.md).
4. Open [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the gradle task *explorviz-backend -> gretty -> appRestart* to recompile and start the server. Then, start the Test app again.
