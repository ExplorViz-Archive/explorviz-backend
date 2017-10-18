# ExplorViz UI Backend
<a href="https://travis-ci.org/ExplorViz/explorviz-ui-backend"><img src="https://travis-ci.org/ExplorViz/explorviz-ui-backend.svg?branch=master" alt="Build Status"></a>
[![Stories in Backlog](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=backlog&title=Backlog)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Ready](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=Ready)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Progress](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=in%20progress&title=In%20Progress)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Done](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=done&title=Done)](http://waffle.io/ExplorViz/explorviz-ui-backend)


This project is a wip replica of ExplorViz's visualization component. It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. This is only the backend, you will need the [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend) as well.

Use the predefined maven configurations in *explorviz-ui-backend/runConfigs* to compile and execute the server. This maven project includes or installs every necessary dependency.

Quick start with dummy monitoring:

1. Download the Eclipse IDE for Java and DSL Developers and Java SDK 8
2. Download projects [explorviz-ui-backend](https://github.com/ExplorViz/explorviz-ui-backend) and [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication).
3. $[explorviz-ui-backend-folder]/gradle eclipse (to generate eclipse files)
4. Import project into eclipse: via Import -> Gradle -> Existing Gradle project -> explorviz-ui-backend
5. Eclipse: explorviz-ui-backend-core: Project -> Clean (to generate xtend files (xtend-gen)) 
6. Start **explorviz-ui-backend** via Eclipse Tab: Gradle Tasks -> explorviz-ui-backend -> gretty -> appStart
7. Start the [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication) via .bat or .sh file
8. (Optional) Navigate to e.g. [localhost:8080/landscapes/1](http://localhost:8080/landscapes/1)
9. Start the [explorviz-ui-frontend](https://github.com/ExplorViz/explorviz-ui-frontend).

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the maven config `explorviz-ui-backend-compile-exec` to recompile and start the server. Then, start the Test app again.


