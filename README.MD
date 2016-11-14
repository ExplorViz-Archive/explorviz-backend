# ExplorViz UI Backend

This project is a wip replica of ExplorViz's visualization component. It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. This is only the backend, you will need the (to be developed) frontend as well.

Use the predefined maven configurations in *explorviz-ui-backend/runconfigs* to compile and execute the server. This maven project includes or installs every necessary dependency.

Quick start with dummy monitoring:

1. Eclipse: Download and import the projects [explorviz-ui-backend](https://github.com/ExplorViz/common), [common](https://github.com/ExplorViz/common) and [monitoring](https://github.com/ExplorViz/monitoring).
2. Start **explorviz-ui-backend** via predefined maven config
3. Click *Start Monitoring Test App* in run configurations
4. Navigate to [localhost:8080/currentLandscape/landscape](http://localhost:8080/currentLandscape/landscape)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Use the maven config `explorviz-ui-backend-compile-exec` to recompile and start the server. Then, start the Test app again.

[![Stories in Ready](https://badge.waffle.io/ExplorViz/explorviz-ui-backend.png?label=ready&title=Ready)](http://waffle.io/ExplorViz/explorviz-ui-backend