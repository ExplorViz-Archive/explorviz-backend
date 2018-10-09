package net.explorviz.landscape.repository;

import java.util.LinkedList;
import java.util.Random;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.helper.ModelHelper;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.repository.helper.DummyLandscapeHelper;

/**
 * Creates a dummy landscape for developing or demo purposes.
 */
final class LandscapeDummyCreator {

  private static final int CALLS_GENERATOR_BOUND = 300000;

  // CHECKSTYLE.OFF: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines
  // CHECKSTYLE.OFF: MagicNumberCheck - Much more readable than NOCS in many lines

  private static int applicationId = 0;
  private static Landscape dummyLandscape = null;
  private static int formatFactor = 1024 * 1024 * 1024;

  private LandscapeDummyCreator() {
    // Utility class
  }

  public static Landscape createDummyLandscape() {

    if (dummyLandscape != null) {
      dummyLandscape.getTimestamp().setCalls(new Random().nextInt(CALLS_GENERATOR_BOUND));
      return dummyLandscape;
    }

    final Landscape landscape = new Landscape();
    landscape.initializeId();
    landscape.getTimestamp().setCalls(new Random().nextInt(CALLS_GENERATOR_BOUND));

    final System requestSystem = new System();
    requestSystem.initializeId();
    requestSystem.setName("Requests");
    requestSystem.setParent(landscape);
    landscape.getSystems().add(requestSystem);

    final NodeGroup requestsNodeGroup = createNodeGroup("10.0.99.1", landscape, requestSystem);
    final Node requestsNode = createNode("10.0.99.1", requestsNodeGroup);

    final Application requestsApp = createApplication("Requests", requestsNode);

    requestsNodeGroup.getNodes().add(requestsNode);
    requestSystem.getNodeGroups().add(requestsNodeGroup);

    final System ocnEditor = new System();
    ocnEditor.initializeId();
    ocnEditor.setName("OCN Editor");
    ocnEditor.setParent(landscape);
    landscape.getSystems().add(ocnEditor);

    final NodeGroup ocnEditorNodeGroup = createNodeGroup("10.0.1.1", landscape, ocnEditor);
    final Node ocnEditorNode = createNode("10.0.1.1", ocnEditorNodeGroup);
    final Application ocnEditorApp = createApplication("Frontend", ocnEditorNode);

    final NodeGroup ocnEditorNodeGroup2 = createNodeGroup("10.0.1.2", landscape, ocnEditor);
    final Node ocnEditorNode2 = createNode("10.0.1.2", ocnEditorNodeGroup2);
    final Application ocnEditorApp2 = createApplication("Database", ocnEditorNode2);

    ocnEditorNodeGroup.getNodes().add(ocnEditorNode);
    ocnEditor.getNodeGroups().add(ocnEditorNodeGroup);
    ocnEditorNodeGroup2.getNodes().add(ocnEditorNode2);
    ocnEditor.getNodeGroups().add(ocnEditorNodeGroup2);

    final System ocnDatabase = new System();
    ocnDatabase.initializeId();
    ocnDatabase.setName("OCN Database");
    ocnDatabase.setParent(landscape);
    landscape.getSystems().add(ocnDatabase);

    final NodeGroup ocnDatabaseNodeGroup = createNodeGroup("10.0.2.1", landscape, ocnDatabase);
    final Node ocnDatabaseNode = createNode("10.0.2.1", ocnDatabaseNodeGroup);
    final Application ocnDatabaseApp = createApplication("Interface", ocnDatabaseNode);

    final NodeGroup ocnDatabaseNodeGroup2 = createNodeGroup("10.0.2.2", landscape, ocnDatabase);
    final Node ocnDatabaseNode2 = createNode("10.0.2.2", ocnDatabaseNodeGroup2);
    final Application ocnDatabaseApp2 = createApplication("Database", ocnDatabaseNode2);

    ocnDatabaseNodeGroup.getNodes().add(ocnDatabaseNode);
    ocnDatabase.getNodeGroups().add(ocnDatabaseNodeGroup);
    ocnDatabaseNodeGroup2.getNodes().add(ocnDatabaseNode2);
    ocnDatabase.getNodeGroups().add(ocnDatabaseNodeGroup2);

    final System kielprints = new System();
    kielprints.initializeId();

    kielprints.setName("OceanRep");
    kielprints.setParent(landscape);
    landscape.getSystems().add(kielprints);

    final NodeGroup kielprintsNodeGroup = createNodeGroup("10.0.3.1", landscape, kielprints);
    final Node kielprintsNode = createNode("10.0.3.1", kielprintsNodeGroup);
    final Application kielprintsApp = createApplication("Webinterface", kielprintsNode);
    final Application kielprintsApp2 = createApplication("Eprints", kielprintsNode);

    final NodeGroup kielprintsNodeGroup2 = createNodeGroup("10.0.3.2", landscape, kielprints);
    final Node kielprintsNode2 = createNode("10.0.3.2", kielprintsNodeGroup2);
    final Application kielprintsApp3 = createApplication("Database", kielprintsNode2);

    kielprintsNodeGroup.getNodes().add(kielprintsNode);
    kielprints.getNodeGroups().add(kielprintsNodeGroup);
    kielprintsNodeGroup2.getNodes().add(kielprintsNode2);
    kielprints.getNodeGroups().add(kielprintsNodeGroup2);

    final System portal = new System();
    portal.initializeId();
    portal.setName("OSIS-Kiel");
    portal.setParent(landscape);
    landscape.getSystems().add(portal);

    final NodeGroup portalNodeGroup = createNodeGroup("10.0.4.1", landscape, portal);
    final Node portalNode = createNode("10.0.4.1", portalNodeGroup);
    final Application portalApp = createApplication("Wiki", portalNode);

    final NodeGroup portalNodeGroup2 = createNodeGroup("10.0.4.2", landscape, portal);
    final Node portalNode2 = createNode("10.0.4.2", portalNodeGroup2);
    final Application portalApp2 = createApplication("Artifacts", portalNode2);

    portalNodeGroup.getNodes().add(portalNode);
    portal.getNodeGroups().add(portalNodeGroup);
    portalNodeGroup2.getNodes().add(portalNode2);
    portal.getNodeGroups().add(portalNodeGroup2);

    final System pangea = new System();
    pangea.initializeId();
    pangea.setName("WDC-Mare");
    pangea.setParent(landscape);
    landscape.getSystems().add(pangea);

    final NodeGroup pangeaNodeGroup = createNodeGroup("10.0.5.1", landscape, pangea);
    final Node pangeaNode = createNode("10.0.5.1", pangeaNodeGroup);
    final Application pangeaApp = createApplication("4D", pangeaNode);

    final NodeGroup pangeaNodeGroup2 = createNodeGroup("10.0.5.2", landscape, pangea);
    final Node pangeaNode2 = createNode("10.0.5.2", pangeaNodeGroup2);
    final Application pangeaApp2 = createApplication("Jira", pangeaNode2);
    final Application pangeaApp3 = createApplication("PostgreSQL", pangeaNode2);

    pangeaNodeGroup.getNodes().add(pangeaNode);
    pangea.getNodeGroups().add(pangeaNodeGroup);
    pangeaNodeGroup2.getNodes().add(pangeaNode2);
    pangea.getNodeGroups().add(pangeaNodeGroup2);

    final System pubflow = new System();
    pubflow.initializeId();
    pubflow.setName("PubFlow");
    pubflow.setParent(landscape);
    landscape.getSystems().add(pubflow);

    final NodeGroup jiraNodeGroup = createNodeGroup("10.0.0.1 - 10.0.0.2", landscape, pubflow);

    final Node jira1Node = createNode("10.0.0.1", jiraNodeGroup);
    final Application jira1 = createApplication("Jira", jira1Node);

    final Node jira2Node = createNode("10.0.0.2", jiraNodeGroup);
    final Application jira2 = createApplication("Jira", jira2Node);

    jiraNodeGroup.getNodes().add(jira1Node);
    jiraNodeGroup.getNodes().add(jira2Node);
    pubflow.getNodeGroups().add(jiraNodeGroup);

    final NodeGroup postgreSqlNodeGroup = createNodeGroup("10.0.0.3", landscape, pubflow);
    final Node postgreSqlNode = createNode("10.0.0.3", postgreSqlNodeGroup);

    final Application postgreSql = createApplication("PostgreSQL", postgreSqlNode);

    postgreSqlNodeGroup.getNodes().add(postgreSqlNode);
    pubflow.getNodeGroups().add(postgreSqlNodeGroup);

    final NodeGroup workflowNodeGroup = createNodeGroup("10.0.0.4 - 10.0.0.7", landscape, pubflow);

    final Node workflow1Node = createNode("10.0.0.4", workflowNodeGroup);
    final Application workflow1 = createApplication("Workflow", workflow1Node);
    final Application provenance1 = createApplication("Provenance", workflow1Node);

    final Node workflow2Node = createNode("10.0.0.5", workflowNodeGroup);
    final Application workflow2 = createApplication("Workflow", workflow2Node);
    final Application provenance2 = createApplication("Provenance", workflow2Node);

    final Node workflow3Node = createNode("10.0.0.6", workflowNodeGroup);
    final Application workflow3 = createApplication("Workflow", workflow3Node);
    final Application provenance3 = createApplication("Provenance", workflow3Node);

    final Node workflow4Node = createNode("10.0.0.7", workflowNodeGroup);
    final Application workflow4 = createApplication("Workflow", workflow4Node);
    final Application provenance4 = createApplication("Provenance", workflow4Node);

    workflowNodeGroup.getNodes().add(workflow1Node);
    workflowNodeGroup.getNodes().add(workflow2Node);
    workflowNodeGroup.getNodes().add(workflow3Node);
    workflowNodeGroup.getNodes().add(workflow4Node);

    pubflow.getNodeGroups().add(workflowNodeGroup);

    final NodeGroup webshopNodeGroup = createNodeGroup("10.0.0.9", landscape, pubflow);
    final Node webshopNode = createNode("10.0.0.9", webshopNodeGroup);
    final Application webshop = createApplication("Webshop", webshopNode);
    createWebshopApplication(webshop);

    webshopNodeGroup.getNodes().add(webshopNode);
    pubflow.getNodeGroups().add(webshopNodeGroup);

    final NodeGroup cacheNodeGroup = createNodeGroup("10.0.0.8", landscape, pubflow);
    final Node cacheNode = createNode("10.0.0.8", cacheNodeGroup);

    final Application cache = createApplication("Cache", cacheNode);

    final Application databaseConnector = createApplication("Database Connector", cacheNode);
    createDatabaseConnector(databaseConnector);

    cacheNodeGroup.getNodes().add(cacheNode);
    pubflow.getNodeGroups().add(cacheNodeGroup);

    createApplicationCommunication(requestsApp, ocnEditorApp, landscape, 100);

    createApplicationCommunication(pangeaApp, pangeaApp2, landscape, 100);
    createApplicationCommunication(pangeaApp2, pangeaApp3, landscape, 100);
    createApplicationCommunication(ocnEditorApp, ocnDatabaseApp, landscape, 100);
    createApplicationCommunication(ocnDatabaseApp, ocnDatabaseApp2, landscape, 100);
    createApplicationCommunication(ocnEditorApp, ocnEditorApp2, landscape, 100);
    createApplicationCommunication(ocnDatabaseApp, workflow1, landscape, 100);
    createApplicationCommunication(workflow1, pangeaApp, landscape, 100);

    createApplicationCommunication(workflow1, kielprintsApp, landscape, 100);
    createApplicationCommunication(kielprintsApp, kielprintsApp2, landscape, 100);
    createApplicationCommunication(kielprintsApp2, kielprintsApp3, landscape, 100);

    createApplicationCommunication(workflow1, portalApp, landscape, 100);
    createApplicationCommunication(portalApp, portalApp2, landscape, 100);

    createApplicationCommunication(jira1, postgreSql, landscape, 100);
    createApplicationCommunication(jira2, postgreSql, landscape, 200);

    createApplicationCommunication(jira1, workflow1, landscape, 100);
    createApplicationCommunication(jira1, workflow2, landscape, 500);
    createApplicationCommunication(jira1, workflow3, landscape, 100);

    createApplicationCommunication(jira2, workflow4, landscape, 200);

    createApplicationCommunication(workflow1, provenance1, landscape, 400);
    createApplicationCommunication(workflow2, provenance2, landscape, 300);
    createApplicationCommunication(workflow3, provenance3, landscape, 500);
    createApplicationCommunication(workflow4, provenance4, landscape, 200);

    createApplicationCommunication(workflow1, cache, landscape, 100);
    createApplicationCommunication(workflow2, cache, landscape, 100);
    createApplicationCommunication(workflow3, cache, landscape, 300);
    createApplicationCommunication(workflow4, cache, landscape, 100);

    createApplicationCommunication(cache, databaseConnector, landscape, 300 * 2);

    createApplicationCommunication(provenance1, webshop, landscape, 100);
    createApplicationCommunication(provenance2, webshop, landscape, 200);
    createApplicationCommunication(provenance3, webshop, landscape, 300);
    createApplicationCommunication(provenance4, webshop, landscape, 100);

    final Landscape preparedLandscape = LandscapePreparer.prepareLandscape(landscape);
    dummyLandscape = preparedLandscape;

    return preparedLandscape;

  }

  private static NodeGroup createNodeGroup(final String name, final Landscape parent,
      final System system) {
    final NodeGroup nodeGroup = new NodeGroup();
    nodeGroup.initializeId();
    nodeGroup.setName(name);
    nodeGroup.setParent(system);
    return nodeGroup;
  }

  private static Node createNode(final String ipAddress, final NodeGroup parent) {
    final Node node = new Node();
    node.initializeId();
    node.setIpAddress(ipAddress);
    node.setParent(parent);

    // set random usage
    node.setCpuUtilization((double) DummyLandscapeHelper.getRandomNum(10, 100) / 100);
    node.setFreeRam((long) DummyLandscapeHelper.getRandomNum(1, 4) * formatFactor);
    node.setUsedRam((long) DummyLandscapeHelper.getRandomNum(1, 4) * formatFactor);

    return node;
  }

  private static Application createApplication(final String name, final Node parent) {
    final Application application = new Application();
    application.initializeId();

    applicationId = applicationId + 1;
    application.setParent(parent);

    application.setLastUsage(java.lang.System.currentTimeMillis());
    application.setProgrammingLanguage(EProgrammingLanguage.JAVA);

    if (name == "Eprints") {
      application.setProgrammingLanguage(EProgrammingLanguage.PERL);
    }

    application.setName(name);
    parent.getApplications().add(application);

    return application;
  }

  private static ApplicationCommunication createApplicationCommunication(final Application source,
      final Application target, final Landscape landscape, final int requests) {
    final ApplicationCommunication communication = new ApplicationCommunication();
    communication.initializeId();
    communication.setSourceApplication(source);
    communication.setTargetApplication(target);
    communication.setRequests(requests);
    source.getOutgoingApplicationCommunications().add(communication);
    landscape.getOutgoingApplicationCommunications().add(communication);

    return communication;
  }

  private static Component createComponent(final String name, final Component parent,
      final Application app) {
    final Component component = new Component();
    component.initializeId();
    component.setName(name);
    component.setParentComponent(parent);
    component.setBelongingApplication(app);
    if (parent == null) {
      component.setFullQualifiedName(name);
    } else {
      component.setFullQualifiedName(parent.getFullQualifiedName() + "." + name);
      parent.getChildren().add(component);
    }
    return component;
  }

  private static Clazz createClazz(final String name, final Component component,
      final int instanceCount) {
    final Clazz clazz = new Clazz();
    clazz.initializeId();
    clazz.setName(name);
    clazz.setFullQualifiedName(component.getFullQualifiedName() + "." + name);
    clazz.setInstanceCount(instanceCount);
    clazz.setParent(component);
    component.getClazzes().add(clazz);

    return clazz;
  }

  private static void createClazzCommunication(final int requests, final Clazz sourceClazz,
      final Clazz targetClazz, final Application application) {
    ModelHelper.addClazzCommunication(sourceClazz, targetClazz, application, requests,
        0L + DummyLandscapeHelper.getRandomNum(10, 1000),
        0L + DummyLandscapeHelper.getRandomNum(1000, 10000), 0L, 1,
        "getMethod" + DummyLandscapeHelper.getRandomNum(1, 50) + "()");
  }

  private static Application createWebshopApplication(final Application application) {
    final Component org = createComponent("org", null, application);
    application.getComponents().add(org);
    final Component neo4j = createComponent("webshop", org, application);

    final Component graphdb = createComponent("labeling", neo4j, application);
    final Clazz graphDbClazz = createClazz("BaseLabeler", graphdb, 20);
    createClazz("ProcuctLabeler", graphdb, 30);
    createClazz("CategoryLabeler", graphdb, 10);
    createClazz("ItemLabeler", graphdb, 55);
    createClazz("DescriptionLabeler", graphdb, 5);

    final Component helpers = createComponent("helpers", neo4j, application);
    final Clazz helpersClazz = createClazz("BaseHelper", helpers, 30);
    createClazz("ProductHelper", helpers, 40);
    createClazz("CategoryHelper", helpers, 35);
    createClazz("ItemHelper", helpers, 35);
    createClazz("SequenceHelper", helpers, 35);

    final Component tooling = createComponent("tooling", neo4j, application);
    final Clazz toolingClazz = createClazz("AccountSqlMapDao", tooling, 5);
    createClazz("BaseSqlMapDao", tooling, 20);
    createClazz("CategorySqlMapDao", tooling, 30);
    createClazz("ItemSqlMapDao", tooling, 45);
    createClazz("ProductSqlMapDao", tooling, 20);
    createClazz("SequenceSqlMapDao", tooling, 15);

    final Component unsafe = createComponent("unsafe", neo4j, application);
    final Clazz unsafeClazz = createClazz("AbstractBean", unsafe, 20);
    createClazz("CartBean", unsafe, 40);

    final Component kernel = createComponent("kernel", neo4j, application);

    final Component api = createComponent("api", kernel, application);
    final Clazz apiClazz = createClazz("APIHandler", api, 25);
    createClazz("APIHandler", api, 25);
    final Component configuration = createComponent("configuration", kernel, application);
    final Clazz configurationClazz = createClazz("ConfigurationHandler", configuration, 35);
    createClazz("ConfigurationHandler", configuration, 5);
    final Component myextension = createComponent("extension", kernel, application);
    createClazz("SingleExtensionHandler", myextension, 25);
    createClazz("MultipleExtensionHandler", myextension, 5);
    final Component guard = createComponent("guard", kernel, application);
    final Clazz guardClazz = createClazz("GuardHandler", guard, 35);
    createClazz("GuardHandler", guard, 25);

    final Component impl = createComponent("impl", kernel, application);
    final Clazz implClazz = createClazz("ImplementationHandler", impl, 45);
    final Component annotations = createComponent("annotations", impl, application);
    createClazz("AnnotationHandler", annotations, 35);
    final Component apiImpl = createComponent("api", impl, application);
    final Clazz apiImplClazz = createClazz("APIImpl", apiImpl, 25);
    final Component cache = createComponent("cache", impl, application);
    createClazz("CacheImpl", cache, 45);
    final Component persistence = createComponent("persistence", impl, application);
    createClazz("AccountSqlMapDao", persistence, 45);

    final Component info = createComponent("info", kernel, application);
    createClazz("AccountSqlMapDao", info, 5);
    createClazz("AccountSqlMapDao", info, 25);
    final Component lifecycle = createComponent("lifecycle", kernel, application);
    final Clazz lifecycleClazz = createClazz("AccountSqlMapDao", lifecycle, 25);
    createClazz("AccountSqlMapDao", lifecycle, 15);

    final Component logging = createComponent("logging", kernel, application);
    final Clazz loggingClazz = createClazz("AccountSqlMapDao", logging, 25);
    createClazz("AccountSqlMapDao2", logging, 5);

    createClazzCommunication(40, graphDbClazz, helpersClazz, application);
    createClazzCommunication(800, toolingClazz, implClazz, application);
    createClazzCommunication(60, implClazz, helpersClazz, application);
    createClazzCommunication(600, implClazz, apiImplClazz, application);
    createClazzCommunication(1000, implClazz, loggingClazz, application);
    createClazzCommunication(100, guardClazz, unsafeClazz, application);
    createClazzCommunication(1000, apiClazz, configurationClazz, application);
    createClazzCommunication(150, lifecycleClazz, loggingClazz, application);
    createClazzCommunication(12000, guardClazz, implClazz, application);

    createClazzCommunication(3500, implClazz, loggingClazz, application);
    createClazzCommunication(500, loggingClazz, implClazz, application);

    return application;
  }

  private static Application createDatabaseConnector(final Application application) {
    final Component org = createComponent("org", null, application);
    application.getComponents().add(org);
    final Component mapleLeaf = createComponent("database", org, application);
    final Component database = createComponent("connector", mapleLeaf, application);
    createClazz("Connection", database, 80);

    final LinkedList<DatabaseQuery> dbQueryList = new LinkedList<>();

    final int maxIterations = 25;
    for (int i = 0; i < maxIterations; i++) {
      DatabaseQuery dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement(
          "CREATE TABLE IF NOT EXISTS `order` (oid integer PRIMARY KEY, name text NOT NULL, "
              + "email text NOT NULL, odate text NOT NULL, itemid integer NOT NULL);");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "', 'Carol K. Durham', 'durham@uni-kiel.de', '2017-10-08', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Carol K. Durham");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery();
      dbQueryTmp.initializeId();
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Tom B. Erichsen");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
      dbQueryList.add(dbQueryTmp);
    }
    application.setDatabaseQueries(dbQueryList);

    return application;
  }
  // CHECKSTYLE.ON: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines
  // CHECKSTYLE.ON: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines

}
