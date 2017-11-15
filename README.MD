# ExplorViz Backend
<a href="https://travis-ci.org/ExplorViz/explorviz-backend"><img src="https://travis-ci.org/ExplorViz/explorviz-backend.svg?branch=master" alt="Build Status"></a>
[![](https://img.shields.io/docker/pulls/explorviz/explorviz-docker.svg)](https://hub.docker.com/r/explorviz/explorviz-docker "Click to view the image on Docker Hub")

This project is a wip replica of ExplorViz's visualization component. 
It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. 
This is only the backend, you will need the [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend) as well.

This gradle project includes or installs every necessary dependency.

### Eclipse Setup
1. Download the latest **Eclipse IDE for Java and DSL Developers** and Java SDK 8
2. *Start Eclipse -> Help -> Eclipse Marketplace*, insert and download
- *FindBugs Eclipse Plugin 3.0.2*
- *Checkstyle Plug-in 8.0.0*
3. In Eclipse: *Help -> Install New Software -> Insert https://dl.bintray.com/pmd/pmd-eclipse-plugin/updates/ in "Work with" -> Check "PMD for Eclipse 4" -> Finish and download* (**Attention:** Don't use the eclipse-pmd version from the marketplace)

## Setup Development
1. Follow the [Eclipse Setup](https://github.com/ExplorViz/explorviz-backend#eclipse-setup)
2. Clone this repository
3. Import project into eclipse: via *Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend*
4. In Eclipse: explorviz-backend: *Project -> Clean* (to generate .java files from .xtend files (xtend-gen))
5. Start **explorviz-backend** via Eclipse Tab: *Gradle Tasks -> explorviz-backend -> gretty -> appStart*
6. Setup and start [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend)

To force the download of newly added dependencies: *Right click explorviz-backend -> Grade -> Refresh Gradle Project*. 

## Quick start with dummy monitoring

1. Follow the above [Setup Development](#setup-development) procedure
2. Clone [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication)
3. Start the [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication) via included .bat or .sh file
4. Open [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the gradle task *explorviz-backend -> gretty -> appRestart* to recompile and start the server. Then, start the Test app again.
