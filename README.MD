# ExplorViz Backend
[![TravsCI Build Status](https://travis-ci.org/ExplorViz/explorviz-backend.svg?branch=master)](https://travis-ci.org/ExplorViz/explorviz-backend)
[![Jenkins Build Status](https://build.se.informatik.uni-kiel.de/jenkins/job/explorviz-backend/badge/icon)](https://build.se.informatik.uni-kiel.de/jenkins/job/ExplorViz-backend/)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=explorviz-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=explorviz-backend)
[![Dockerhub Pulls](https://img.shields.io/docker/pulls/explorviz/explorviz-docker.svg)](https://hub.docker.com/r/explorviz/explorviz-docker)

This project contains the modernized version of [ExplorViz](https://github.com/ExplorViz/Explorviz). 

It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. 
This is only the backend, you will need the [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend) as well.

This gradle project includes or installs every necessary dependency.

## Documentation (Work in progress)
The API documentation is available [here](https://explorviz.github.io/explorviz-backend/).

## Eclipse Setup
1. Download the latest [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/eclipse-packages/) (ExplorViz requires JDK 8 compliance)
2. Follow the [CAU-SE guidelines for quality assurance](https://github.com/cau-se/code-quality-configurations/tree/master/java)

## Setup Development
1. Follow the previous [Eclipse Setup](#eclipse-setup)
2. Clone this repository
3. Import project into Eclipse: via `Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend`
4. Start **explorviz-backend** microservices via run configurations in the [run-configurations](run-configurations) folder: `Right click single config -> Run as -> ExplorViz-X` (where `X` is the name of a microservice)
5. Setup and start [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

### Further Information
* To force the download of newly added dependencies: `Right click on the project explorviz-backend -> Gradle -> Refresh Gradle Project`. You might need to uncomment the respective lines in the build.gradle file.
* Generating API Documentation: Travis CI automatically builds all Javadoc on successful master-branch builds

## Quick start with sample (dummy) monitoring

1. Follow the above [Setup Development](#setup-development) procedure
2. Clone [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication)
3. Start the [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication) as described [here](https://github.com/czirkelbach/kiekerSampleApplication/blob/master/README.md).
4. Open [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the gradle task `explorviz-backend -> gretty -> appRestart` to recompile and start the server. Then, start the monitoring again.
