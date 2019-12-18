<p align="center">
  <img width="60%" src="https://raw.githubusercontent.com/ExplorViz/Docs/master/images/explorviz-logo.png">
</p>

___

[![TravsCI Build Status](https://travis-ci.org/ExplorViz/explorviz-backend.svg?branch=master)](https://travis-ci.org/ExplorViz/explorviz-backend)

## Project Description
ExplorViz uses dynamic analysis techniques to provide live trace visualization of the communication in large software landscape. It targets system and program comprehension in those landscapes while still providing details on the communication within an application. A landscape perspective enriches current system visualizations with additional abstraction levels for efficient comprehension of communication between hundreds of applications which is often encountered in, for instance, Cloud environments. On the application level perspective, ExplorViz utilizes the 3D city metaphor combined with an interactive concept of showing only details that are in focus of the analysis. For best accessibility, ExplorViz is a web-based tool featuring cutting-edge technologies like WebGL and HTML 5.

Given the 3D city metaphor visualization of an application, we investigate new interaction styles and higher immersion for a more effective and efficient program comprehension process. For this purpose, we utilize uprising technologies and evaluate developed approaches in experiments with real test subjects.

The usability and effectiveness of ExplorViz has been investigated in controlled experiments which resulted in increased efficiency and effectiveness over competing approaches.

This project contains the modernized version of [ExplorViz](https://github.com/ExplorViz/Explorviz). 

It substitutes [GWT](http://www.gwtproject.org/) and it's (asynchronous) RPC framework with [Jersey](https://jersey.java.net/) for creating a RESTful API. 
This is only the backend, you will need the [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend) as well.

This gradle project includes or installs every necessary dependency.

## Reference
Citing ExplorViz as a tool:

Florian Fittkau, Alexander Krause, Wilhelm Hasselbring (2017): Software landscape and application visualization for system comprehension with ExplorViz. Information and Software Technology, Volume 87. pp. 259-277. DOI https://doi.org/10.1016/j.infsof.2016.07.004.

[[BibTex]](http://eprints.uni-kiel.de/cgi/export/eprint/33464/BibTeX/cau-eprint-33464.bib) | [[Endnote]](http://eprints.uni-kiel.de/cgi/export/eprint/33464/EndNote/cau-eprint-33464.enw)

Citing ExplorViz' collaborative modularization process:

Zirkelbach, Christian, Krause, Alexander and Hasselbring, Wilhelm (2019): Modularization of Research Software for Collaborative Open Source Development. In Proceedings of the Ninth International Conference on Advanced Collaborative Networks, Systems and Applications (COLLA 2019), June 30 - July 04, 2019, Rome, Italy.

[[BibTex]](http://eprints.uni-kiel.de/cgi/export/eprint/46777/BibTeX/cau-eprint-46777.bib) | [[Endnote]](http://eprints.uni-kiel.de/cgi/export/eprint/46777/EndNote/cau-eprint-46777.enw)

## Documentation
- The API documentation is available [here](https://explorviz.github.io/explorviz-backend/)
* The documentation for endpoints of services (SwaggerUI) is available [here](http://localhost:8001), once all services are started

## Eclipse Setup
1. Download the latest [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/eclipse-packages/) (ExplorViz requires JDK 11 compliance)
2. Follow the [CAU-SE guidelines for quality assurance](https://github.com/cau-se/code-quality-configurations/tree/master/java) (Only necessary if you contribute code)

## Setup Development (Docker and docker-compose are required)
1. Follow the previous [Eclipse Setup](#eclipse-setup)
2. Clone this repository
3. Import project into Eclipse: via `Import -> Gradle -> Existing Gradle project -> path/to/explorviz-backend`
4. Follow the instructions inside of the [docker-compose](docker-compose) folder to start mandatory docker-based services (e.g. MongoDB)
5. Start **explorviz-backend** microservices via run configurations in the [run-configurations](run-configurations) folder: `Right click single config -> Run as -> ExplorViz-X` (where `X` is the name of a microservice)
6. Setup and start [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

### Further Information
* To force the download of newly added dependencies: `Right click on the project explorviz-backend -> Gradle -> Refresh Gradle Project`. You might need to uncomment the respective lines in the build.gradle file.
* Generating API Documentation: Travis CI automatically builds all Javadoc on successful master-branch builds

## Quick start with sample (dummy) monitoring
1. Follow the above [Setup Development](#setup-development) procedure
2. Clone [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication)
3. Start the [TestApplication](https://github.com/czirkelbach/kiekerSampleApplication) as described [here](https://github.com/czirkelbach/kiekerSampleApplication/blob/master/README.md).
4. Open [explorviz-frontend](https://github.com/ExplorViz/explorviz-frontend)

**Important**: Changes that are related to dependency injections require a recompilation of the application. Thus, restart the microservices. Afterwards, start the monitoring again.
