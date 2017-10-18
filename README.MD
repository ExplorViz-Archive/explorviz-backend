# ExplorViz UI Backend
<a href="https://travis-ci.org/ExplorViz/explorviz-ui-backend"><img src="https://travis-ci.org/ExplorViz/explorviz-ui-backend.svg?branch=master" alt="Build Status"></a>


This project is a wip replica of ExplorViz's visualization component. It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. This is only the backend, you will need the [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend) as well.

This gradle project includes or installs every necessary dependency.

## Setup Development
1. Download the Eclipse IDE for Java and DSL Developers and Java SDK 8
2. Clone this repository
3. Navigate to the cloned folder and run `gradlew eclipse` (to generate eclipse files)
4. Import project into eclipse: via Import -> Gradle -> Existing Gradle project -> path/to/explorviz-ui-backend
5. In Eclipse: explorviz-ui-backend-core: Project -> Clean (to generate .java files from .xtend files (xtend-gen))
6. Start **explorviz-ui-backend** via Eclipse Tab: Gradle Tasks -> `explorviz-ui-backend -> gretty -> appStart`
7. Setup and start [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend)

## Quick start with dummy monitoring

1. Follow the above [Setup Development](#setup-development) procedure
2. Clone [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication)
3. $[explorviz-ui-backend-folder]/gradlew eclipse (to generate eclipse files)
4. Start the [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication) via .bat or .sh file
5. Open [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the gradle task `explorviz-ui-backend -> gretty -> appRestart` to recompile and start the server. Then, start the Test app again.


