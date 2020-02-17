package net.explorviz.landscape.repository.helper.dummy;

import java.util.LinkedList;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.shared.common.idgen.IdGenerator;

public final class DummyLandscapeCreator {

  private DummyLandscapeCreator() {
    // Utility class
  }

  /**
   * Create a dummy landscape for demo and mocking purposes
   *
   * @return a prepared dummy landscape
   */
  public static Landscape createDummyLandscape(final IdGenerator idGen) {

    DummyLandscapeHelper.idGen = idGen;

    final Landscape landscape = DummyLandscapeHelper.createLandscape();

    /* External Communication Endpoint */

    final System sRequests = DummyLandscapeHelper.createSystem("Requests", landscape);
    final NodeGroup ngRequests = DummyLandscapeHelper.createNodeGroup("10.0.0.1", sRequests);
    final Node nRequests = DummyLandscapeHelper.createNode("10.0.0.1", ngRequests, landscape);
    final Application appRequests = DummyLandscapeHelper
        .createApplication("Requests", nRequests, landscape, EProgrammingLanguage.JAVA);

    /* ExplorViz Backend */

    final System sBackend = DummyLandscapeHelper.createSystem("explorviz-backend", landscape);

    final NodeGroup ngAnalysis = DummyLandscapeHelper.createNodeGroup("10.0.99.1", sBackend);

    final Node nAnalysis1 = DummyLandscapeHelper.createNode("10.0.99.1", ngAnalysis, landscape);
    final Application appAnalysisService1 = DummyLandscapeHelper
        .createApplication("analysis-service", nAnalysis1, landscape, EProgrammingLanguage.JAVA);

    final Node nAnalysis2 = DummyLandscapeHelper.createNode("10.0.99.2", ngAnalysis, landscape);
    final Application appAnalysisService2 = DummyLandscapeHelper
        .createApplication("analysis-service", nAnalysis2, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngLandscape = DummyLandscapeHelper.createNodeGroup("10.0.99.13", sBackend);
    final Node nLandscape = DummyLandscapeHelper.createNode("10.0.99.13", ngLandscape, landscape);
    final Application appLandscapeService = DummyLandscapeHelper
        .createApplication("landscape-service", nLandscape, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngUser = DummyLandscapeHelper.createNodeGroup("10.0.99.14", sBackend);
    final Node nUser = DummyLandscapeHelper.createNode("10.0.99.14", ngUser, landscape);
    final Application appUserService = DummyLandscapeHelper
        .createApplication("user-service", nUser, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngDiscovery = DummyLandscapeHelper.createNodeGroup("10.0.99.15", sBackend);
    final Node nDiscovery = DummyLandscapeHelper.createNode("10.0.99.15", ngDiscovery, landscape);
    final Application appDiscoveryService = DummyLandscapeHelper
        .createApplication("discovery-service", nDiscovery, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngSettings = DummyLandscapeHelper.createNodeGroup("10.0.99.16", sBackend);
    final Node nSettings = DummyLandscapeHelper.createNode("10.0.99.16", ngSettings, landscape);
    final Application appSettingsService = DummyLandscapeHelper
        .createApplication("settings-service", nSettings, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngBroadcast = DummyLandscapeHelper.createNodeGroup("10.0.99.17", sBackend);
    final Node nBroadcast = DummyLandscapeHelper.createNode("10.0.99.17", ngBroadcast, landscape);
    final Application appBroadcastService = DummyLandscapeHelper
        .createApplication("broadcast-service", nBroadcast, landscape, EProgrammingLanguage.JAVA);

    final NodeGroup ngHistory = DummyLandscapeHelper.createNodeGroup("10.0.99.18", sBackend);
    final Node nHistory = DummyLandscapeHelper.createNode("10.0.99.18", ngHistory, landscape);
    final Application appHistoryService = DummyLandscapeHelper
        .createApplication("history-service", nHistory, landscape, EProgrammingLanguage.JAVA);

    /* ExplorViz Frontend */

    final System sFrontend = DummyLandscapeHelper.createSystem("explorviz-frontend", landscape);
    final NodeGroup ngFrontend = DummyLandscapeHelper.createNodeGroup("10.0.99.78", sFrontend);
    final Node nFrontend = DummyLandscapeHelper.createNode("10.0.99.78", ngFrontend, landscape);
    final Application appFrontend = DummyLandscapeHelper.createApplication("explorviz-frontend",
        nFrontend,
        landscape,
        EProgrammingLanguage.JAVASCRIPT);

    /* Sample Application Webshop Backend */

    final System sWebshop = DummyLandscapeHelper.createSystem("Webshop", landscape);
    final NodeGroup ngWebshopBackend = DummyLandscapeHelper.createNodeGroup("10.0.98.23", sWebshop);
    final Node nWebshopBackend =
        DummyLandscapeHelper.createNode("10.0.98.23", ngWebshopBackend, landscape);
    final Application appWebshopBackend = DummyLandscapeHelper
        .createApplication("Backend", nWebshopBackend, landscape, EProgrammingLanguage.JAVA);

    /* Sample Application Webshop Frontend */
    final NodeGroup ngWebshopFrontend =
        DummyLandscapeHelper.createNodeGroup("10.0.98.33", sWebshop);
    final Node nFrontendWebshop =
        DummyLandscapeHelper.createNode("10.0.98.33", ngWebshopFrontend, landscape);
    final Application appWebshopFrontend = DummyLandscapeHelper
        .createApplication("Frontend", nFrontendWebshop, landscape, EProgrammingLanguage.JAVA);
    populateWebshopApplication(idGen, appWebshopFrontend);

    /* Application Communication */

    DummyLandscapeHelper
        .createApplicationCommunication(appRequests, appWebshopBackend, landscape, "HTTP", 25, 750);

    DummyLandscapeHelper.createApplicationCommunication(appWebshopBackend,
        appWebshopFrontend,
        landscape,
        "RMI",
        65,
        1250);

    DummyLandscapeHelper.createApplicationCommunication(appRequests,
        appAnalysisService1,
        landscape,
        "HTTP",
        35,
        400);

    DummyLandscapeHelper.createApplicationCommunication(appRequests,
        appAnalysisService2,
        landscape,
        "HTTP",
        15,
        250);

    DummyLandscapeHelper.createApplicationCommunication(appAnalysisService1,
        appLandscapeService,
        landscape,
        "Kafka",
        50,
        250);

    DummyLandscapeHelper.createApplicationCommunication(appAnalysisService2,
        appLandscapeService,
        landscape,
        "Kafka",
        35,
        350);

    DummyLandscapeHelper.createApplicationCommunication(appLandscapeService,
        appHistoryService,
        landscape,
        "Kafka",
        1,
        1);

    DummyLandscapeHelper.createApplicationCommunication(appLandscapeService,
        appBroadcastService,
        landscape,
        "Kafka",
        1,
        1);

    DummyLandscapeHelper
        .createApplicationCommunication(appSettingsService, appFrontend, landscape, "HTTP", 1, 2);

    DummyLandscapeHelper
        .createApplicationCommunication(appUserService, appFrontend, landscape, "HTTP", 1, 20);

    DummyLandscapeHelper
        .createApplicationCommunication(appBroadcastService, appFrontend, landscape, "HTTP", 1, 5);


    DummyLandscapeHelper
        .createApplicationCommunication(appLandscapeService, appFrontend, landscape, "HTTP", 5, 15);

    // outgoing communication from applications to the landscape (an another application)
    landscape.createOutgoingApplicationCommunication();

    return landscape;
  }

  /*
   * Populates a passed application with a sample webshop content
   *
   * @param application - the application to be populated
   *
   * @return the populated application
   */
  private static Application populateWebshopApplication(final IdGenerator idGen,
      final Application application) {

    final Component org = DummyLandscapeHelper.createComponent("org", null, application);
    application.getComponents().add(org);
    final Component neo4j = DummyLandscapeHelper.createComponent("webshop", org, application);

    final Component graphdb = DummyLandscapeHelper.createComponent("labeling", neo4j, application);
    final Clazz graphDbClazz = DummyLandscapeHelper.createClazz("BaseLabeler", graphdb, 20);
    DummyLandscapeHelper.createClazz("ProcuctLabeler", graphdb, 30);
    DummyLandscapeHelper.createClazz("CategoryLabeler", graphdb, 10);
    DummyLandscapeHelper.createClazz("ItemLabeler", graphdb, 55);
    DummyLandscapeHelper.createClazz("DescriptionLabeler", graphdb, 5);

    final Component helpers = DummyLandscapeHelper.createComponent("helpers", neo4j, application);
    final Clazz helpersClazz = DummyLandscapeHelper.createClazz("BaseHelper", helpers, 30);
    DummyLandscapeHelper.createClazz("ProductHelper", helpers, 40);
    DummyLandscapeHelper.createClazz("CategoryHelper", helpers, 35);
    DummyLandscapeHelper.createClazz("ItemHelper", helpers, 35);
    DummyLandscapeHelper.createClazz("SequenceHelper", helpers, 35);

    final Component tooling = DummyLandscapeHelper.createComponent("tooling", neo4j, application);
    final Clazz toolingClazz = DummyLandscapeHelper.createClazz("AccountSqlMapDao", tooling, 5);
    DummyLandscapeHelper.createClazz("BaseSqlMapDao", tooling, 20);
    DummyLandscapeHelper.createClazz("CategorySqlMapDao", tooling, 30);
    DummyLandscapeHelper.createClazz("ItemSqlMapDao", tooling, 45);
    DummyLandscapeHelper.createClazz("ProductSqlMapDao", tooling, 20);
    DummyLandscapeHelper.createClazz("SequenceSqlMapDao", tooling, 15);

    final Component unsafe = DummyLandscapeHelper.createComponent("unsafe", neo4j, application);
    final Clazz unsafeClazz = DummyLandscapeHelper.createClazz("AbstractBean", unsafe, 20);
    DummyLandscapeHelper.createClazz("CartBean", unsafe, 40);

    final Component kernel = DummyLandscapeHelper.createComponent("kernel", neo4j, application);

    final Component api = DummyLandscapeHelper.createComponent("api", kernel, application);
    final Clazz apiClazz = DummyLandscapeHelper.createClazz("APIHandler", api, 25);
    DummyLandscapeHelper.createClazz("APIHandler", api, 25);
    final Component configuration =
        DummyLandscapeHelper.createComponent("configuration", kernel, application);
    final Clazz configurationClazz =
        DummyLandscapeHelper.createClazz("ConfigurationHandler", configuration, 35);
    DummyLandscapeHelper.createClazz("ConfigurationHandler", configuration, 5);
    final Component myextension =
        DummyLandscapeHelper.createComponent("extension", kernel, application);
    DummyLandscapeHelper.createClazz("SingleExtensionHandler", myextension, 25);
    DummyLandscapeHelper.createClazz("MultipleExtensionHandler", myextension, 5);
    final Component guard = DummyLandscapeHelper.createComponent("guard", kernel, application);
    final Clazz guardClazz = DummyLandscapeHelper.createClazz("GuardHandler", guard, 35);
    DummyLandscapeHelper.createClazz("GuardHandler", guard, 25);

    final Component impl = DummyLandscapeHelper.createComponent("impl", kernel, application);
    final Clazz implClazz = DummyLandscapeHelper.createClazz("ImplementationHandler", impl, 45);
    final Component annotations =
        DummyLandscapeHelper.createComponent("annotations", impl, application);
    DummyLandscapeHelper.createClazz("AnnotationHandler", annotations, 35);
    final Component apiImpl = DummyLandscapeHelper.createComponent("api", impl, application);
    final Clazz apiImplClazz = DummyLandscapeHelper.createClazz("APIImpl", apiImpl, 25);
    final Component cache = DummyLandscapeHelper.createComponent("cache", impl, application);
    DummyLandscapeHelper.createClazz("CacheImpl", cache, 45);
    final Component persistence =
        DummyLandscapeHelper.createComponent("persistence", impl, application);
    DummyLandscapeHelper.createClazz("AccountSqlMapDao", persistence, 45);

    final Component info = DummyLandscapeHelper.createComponent("info", kernel, application);
    DummyLandscapeHelper.createClazz("AccountSqlMapDao", info, 5);
    DummyLandscapeHelper.createClazz("AccountSqlMapDao", info, 25);
    final Component lifecycle =
        DummyLandscapeHelper.createComponent("lifecycle", kernel, application);
    final Clazz lifecycleClazz =
        DummyLandscapeHelper.createClazz("AccountSqlMapDao", lifecycle, 25);
    DummyLandscapeHelper.createClazz("AccountSqlMapDao", lifecycle, 15);

    final Component logging = DummyLandscapeHelper.createComponent("logging", kernel, application);
    final Clazz loggingClazz = DummyLandscapeHelper.createClazz("AccountSqlMapDao", logging, 25);
    DummyLandscapeHelper.createClazz("AccountSqlMapDao2", logging, 5);

    // specify a first trace for the dummy landscape
    final String firstTraceId = idGen.generateId();

    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 1, 40, graphDbClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 2, 800, toolingClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 3, 60, implClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 4, 600, implClazz, apiImplClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 5, 1000, implClazz, loggingClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 6, 100, guardClazz, unsafeClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 7, 1000, apiClazz, configurationClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 8, 150, lifecycleClazz, loggingClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 9, 12000, guardClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 10, 3500, implClazz, loggingClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 11, 500, loggingClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 12, 4200, implClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 13, 4200, helpersClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 14, 2100, implClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(firstTraceId, 15, 2100, helpersClazz, implClazz, application);

    // specify a second trace for the dummy landscape
    final String secondTraceId = idGen.generateId();

    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 1, 2500, implClazz, loggingClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 2, 900, loggingClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 3, 8200, implClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 4, 11200, helpersClazz, implClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 5, 1200, implClazz, helpersClazz, application);
    DummyLandscapeHelper
        .createClazzCommunication(secondTraceId, 6, 390, helpersClazz, implClazz, application);

    // create dummy database communication
    final LinkedList<DatabaseQuery> dbQueryList = new LinkedList<>();

    final int maxIterations = 25;
    for (int i = 0; i < maxIterations; i++) {
      DatabaseQuery dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement(
          "CREATE TABLE IF NOT EXISTS `order` (oid integer PRIMARY KEY, name text NOT NULL, "
              + "email text NOT NULL, odate text NOT NULL, itemid integer NOT NULL);");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setParentApplication(application);
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "', 'Carol K. Durham', 'durham@uni-kiel.de', '2017-10-08', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Carol K. Durham");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Tom B. Erichsen");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(1000, 100000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);
    }
    application.setDatabaseQueries(dbQueryList);

    return application;
  }

}
