# ExplorViz UI Backend
<img src="https://travis-ci.org/ExplorViz/explorviz-ui-backend.svg?branch=master" alt="Build Status">
[![Stories in Backlog](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=Backlog)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Ready](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=Ready)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Progress](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=In%20Progress)](http://waffle.io/ExplorViz/explorviz-ui-backend)
[![Stories in Done](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=Done)](http://waffle.io/ExplorViz/explorviz-ui-backend)


This project is a wip replica of ExplorViz's visualization component. It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. This is only the backend, you will need the (to be developed) frontend as well.

Use the predefined maven configurations in *explorviz-ui-backend/runConfigs* to compile and execute the server. This maven project includes or installs every necessary dependency.

Quick start with dummy monitoring:

1. Download the Eclipse IDE for Java and DSL Developers
2. Download and import the projects [explorviz-ui-backend](https://github.com/ExplorViz/common) and [monitoringTestApplication](https://github.com/ExplorViz/monitoringTestApplication).
3. Start **explorviz-ui-backend** via predefined maven config
4. Start the test application via .bat or .sh file
5. Navigate to [localhost:8080/currentLandscape/landscape](http://localhost:8080/currentLandscape/landscape)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the maven config `explorviz-ui-backend-compile-exec` to recompile and start the server. Then, start the Test app again.


