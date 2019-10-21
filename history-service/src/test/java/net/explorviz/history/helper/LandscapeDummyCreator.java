package net.explorviz.history.helper;

import java.util.LinkedList;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.model.store.Timestamp;
import net.explorviz.shared.common.idgen.IdGenerator;

/**
 * Creates a dummy landscape for developing or demo purposes.
 */
public final class LandscapeDummyCreator {

  // CHECKSTYLE.OFF: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines
  // CHECKSTYLE.OFF: MagicNumberCheck - Much more readable than NOCS in many lines

  public static int applicationId = 0;
  public static int formatFactor = 1024 * 1024 * 1024;

  private static IdGenerator idGen;


  private LandscapeDummyCreator() {
    // Utility class
  }


  /**
   * Create a dummy landscape for demo and mocking purposes
   *
   * @return a prepared dummy landscape
   */
  public static Landscape createDummyLandscape(final IdGenerator idGen) {

    LandscapeDummyCreator.idGen = idGen;

    final int randomRequestCount = DummyLandscapeHelper.getRandomNum(500, 25000);

    final Landscape landscape = new Landscape(LandscapeDummyCreator.idGen.generateId(),
        new Timestamp(LandscapeDummyCreator.idGen.generateId(),
            java.lang.System.currentTimeMillis(), randomRequestCount));
    landscape.getTimestamp().setTotalRequests(DummyLandscapeHelper.getRandomNum(500, 25000));


    DummyLandscapeHelper.idGen = LandscapeDummyCreator.idGen;

    final System requestSystem = DummyLandscapeHelper.createSystem("Requests", landscape);

    final NodeGroup requestsNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.99.1", requestSystem);
    final Node requestsNode =
        DummyLandscapeHelper.createNode("10.0.99.1", requestsNodeGroup, landscape);

    final Application requestsApp =
        DummyLandscapeHelper.createApplication("Requests", requestsNode, landscape);

    requestsNodeGroup.getNodes().add(requestsNode);
    requestSystem.getNodeGroups().add(requestsNodeGroup);

    final System ocnEditor = DummyLandscapeHelper.createSystem("OCN Editor", landscape);

    final NodeGroup ocnEditorNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.1.1", ocnEditor);
    final Node ocnEditorNode =
        DummyLandscapeHelper.createNode("10.0.1.1", ocnEditorNodeGroup, landscape);
    final Application ocnEditorApp =
        DummyLandscapeHelper.createApplication("Frontend", ocnEditorNode, landscape);

    final NodeGroup ocnEditorNodeGroup2 =
        DummyLandscapeHelper.createNodeGroup("10.0.1.2", ocnEditor);
    final Node ocnEditorNode2 =
        DummyLandscapeHelper.createNode("10.0.1.2", ocnEditorNodeGroup2, landscape);
    final Application ocnEditorApp2 =
        DummyLandscapeHelper.createApplication("Database", ocnEditorNode2, landscape);

    ocnEditorNodeGroup.getNodes().add(ocnEditorNode);
    ocnEditor.getNodeGroups().add(ocnEditorNodeGroup);
    ocnEditorNodeGroup2.getNodes().add(ocnEditorNode2);
    ocnEditor.getNodeGroups().add(ocnEditorNodeGroup2);

    final System ocnDatabase = DummyLandscapeHelper.createSystem("OCN Database", landscape);

    final NodeGroup ocnDatabaseNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.2.1", ocnDatabase);
    final Node ocnDatabaseNode =
        DummyLandscapeHelper.createNode("10.0.2.1", ocnDatabaseNodeGroup, landscape);
    final Application ocnDatabaseApp =
        DummyLandscapeHelper.createApplication("Interface", ocnDatabaseNode, landscape);

    final NodeGroup ocnDatabaseNodeGroup2 =
        DummyLandscapeHelper.createNodeGroup("10.0.2.2", ocnDatabase);
    final Node ocnDatabaseNode2 =
        DummyLandscapeHelper.createNode("10.0.2.2", ocnDatabaseNodeGroup2, landscape);
    final Application ocnDatabaseApp2 =
        DummyLandscapeHelper.createApplication("Database", ocnDatabaseNode2, landscape);

    ocnDatabaseNodeGroup.getNodes().add(ocnDatabaseNode);
    ocnDatabase.getNodeGroups().add(ocnDatabaseNodeGroup);
    ocnDatabaseNodeGroup2.getNodes().add(ocnDatabaseNode2);
    ocnDatabase.getNodeGroups().add(ocnDatabaseNodeGroup2);

    final System kielprints = new System(idGen.generateId());

    kielprints.setName("OceanRep");
    kielprints.setParent(landscape);
    landscape.getSystems().add(kielprints);

    final NodeGroup kielprintsNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.3.1", kielprints);
    final Node kielprintsNode =
        DummyLandscapeHelper.createNode("10.0.3.1", kielprintsNodeGroup, landscape);
    final Application kielprintsApp =
        DummyLandscapeHelper.createApplication("Webinterface", kielprintsNode, landscape);
    final Application kielprintsApp2 =
        DummyLandscapeHelper.createApplication("Eprints", kielprintsNode, landscape);

    final NodeGroup kielprintsNodeGroup2 =
        DummyLandscapeHelper.createNodeGroup("10.0.3.2", kielprints);
    final Node kielprintsNode2 =
        DummyLandscapeHelper.createNode("10.0.3.2", kielprintsNodeGroup2, landscape);
    final Application kielprintsApp3 =
        DummyLandscapeHelper.createApplication("Database", kielprintsNode2, landscape);

    kielprintsNodeGroup.getNodes().add(kielprintsNode);
    kielprints.getNodeGroups().add(kielprintsNodeGroup);
    kielprintsNodeGroup2.getNodes().add(kielprintsNode2);
    kielprints.getNodeGroups().add(kielprintsNodeGroup2);

    final System portal = DummyLandscapeHelper.createSystem("OSIS-Kiel", landscape);

    final NodeGroup portalNodeGroup = DummyLandscapeHelper.createNodeGroup("10.0.4.1", portal);
    final Node portalNode = DummyLandscapeHelper.createNode("10.0.4.1", portalNodeGroup, landscape);
    final Application portalApp =
        DummyLandscapeHelper.createApplication("Wiki", portalNode, landscape);

    final NodeGroup portalNodeGroup2 = DummyLandscapeHelper.createNodeGroup("10.0.4.2", portal);
    final Node portalNode2 =
        DummyLandscapeHelper.createNode("10.0.4.2", portalNodeGroup2, landscape);
    final Application portalApp2 =
        DummyLandscapeHelper.createApplication("Artifacts", portalNode2, landscape);

    portalNodeGroup.getNodes().add(portalNode);
    portal.getNodeGroups().add(portalNodeGroup);
    portalNodeGroup2.getNodes().add(portalNode2);
    portal.getNodeGroups().add(portalNodeGroup2);

    final System pangea = DummyLandscapeHelper.createSystem("WDC-Mare", landscape);

    final NodeGroup pangeaNodeGroup = DummyLandscapeHelper.createNodeGroup("10.0.5.1", pangea);
    final Node pangeaNode = DummyLandscapeHelper.createNode("10.0.5.1", pangeaNodeGroup, landscape);
    final Application pangeaApp =
        DummyLandscapeHelper.createApplication("4D", pangeaNode, landscape);

    final NodeGroup pangeaNodeGroup2 = DummyLandscapeHelper.createNodeGroup("10.0.5.2", pangea);
    final Node pangeaNode2 =
        DummyLandscapeHelper.createNode("10.0.5.2", pangeaNodeGroup2, landscape);
    final Application pangeaApp2 =
        DummyLandscapeHelper.createApplication("Jira", pangeaNode2, landscape);
    final Application pangeaApp3 =
        DummyLandscapeHelper.createApplication("PostgreSQL", pangeaNode2, landscape);

    pangeaNodeGroup.getNodes().add(pangeaNode);
    pangea.getNodeGroups().add(pangeaNodeGroup);
    pangeaNodeGroup2.getNodes().add(pangeaNode2);
    pangea.getNodeGroups().add(pangeaNodeGroup2);

    final System pubflow = new System(idGen.generateId());

    pubflow.setName("PubFlow");
    pubflow.setParent(landscape);
    landscape.getSystems().add(pubflow);

    final NodeGroup jiraNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.0.1 - 10.0.0.2", pubflow);

    final Node jira1Node = DummyLandscapeHelper.createNode("10.0.0.1", jiraNodeGroup, landscape);
    final Application jira1 = DummyLandscapeHelper.createApplication("Jira", jira1Node, landscape);

    final Node jira2Node = DummyLandscapeHelper.createNode("10.0.0.2", jiraNodeGroup, landscape);
    final Application jira2 = DummyLandscapeHelper.createApplication("Jira", jira2Node, landscape);

    jiraNodeGroup.getNodes().add(jira1Node);
    jiraNodeGroup.getNodes().add(jira2Node);
    pubflow.getNodeGroups().add(jiraNodeGroup);

    final NodeGroup postgreSqlNodeGroup = DummyLandscapeHelper.createNodeGroup("10.0.0.3", pubflow);
    final Node postgreSqlNode =
        DummyLandscapeHelper.createNode("10.0.0.3", postgreSqlNodeGroup, landscape);

    final Application postgreSql =
        DummyLandscapeHelper.createApplication("PostgreSQL", postgreSqlNode, landscape);

    postgreSqlNodeGroup.getNodes().add(postgreSqlNode);
    pubflow.getNodeGroups().add(postgreSqlNodeGroup);

    final NodeGroup workflowNodeGroup =
        DummyLandscapeHelper.createNodeGroup("10.0.0.4 - 10.0.0.7", pubflow);

    final Node workflow1Node =
        DummyLandscapeHelper.createNode("10.0.0.4", workflowNodeGroup, landscape);
    final Application workflow1 =
        DummyLandscapeHelper.createApplication("Workflow", workflow1Node, landscape);
    final Application provenance1 =
        DummyLandscapeHelper.createApplication("Provenance", workflow1Node, landscape);

    final Node workflow2Node =
        DummyLandscapeHelper.createNode("10.0.0.5", workflowNodeGroup, landscape);
    final Application workflow2 =
        DummyLandscapeHelper.createApplication("Workflow", workflow2Node, landscape);
    final Application provenance2 =
        DummyLandscapeHelper.createApplication("Provenance", workflow2Node, landscape);

    final Node workflow3Node =
        DummyLandscapeHelper.createNode("10.0.0.6", workflowNodeGroup, landscape);
    final Application workflow3 =
        DummyLandscapeHelper.createApplication("Workflow", workflow3Node, landscape);
    final Application provenance3 =
        DummyLandscapeHelper.createApplication("Provenance", workflow3Node, landscape);

    final Node workflow4Node =
        DummyLandscapeHelper.createNode("10.0.0.7", workflowNodeGroup, landscape);
    final Application workflow4 =
        DummyLandscapeHelper.createApplication("Workflow", workflow4Node, landscape);
    final Application provenance4 =
        DummyLandscapeHelper.createApplication("Provenance", workflow4Node, landscape);

    workflowNodeGroup.getNodes().add(workflow1Node);
    workflowNodeGroup.getNodes().add(workflow2Node);
    workflowNodeGroup.getNodes().add(workflow3Node);
    workflowNodeGroup.getNodes().add(workflow4Node);

    pubflow.getNodeGroups().add(workflowNodeGroup);

    final NodeGroup webshopNodeGroup = DummyLandscapeHelper.createNodeGroup("10.0.0.9", pubflow);
    final Node webshopNode =
        DummyLandscapeHelper.createNode("10.0.0.9", webshopNodeGroup, landscape);
    final Application webshop =
        DummyLandscapeHelper.createApplication("Webshop", webshopNode, landscape);
    createWebshopApplication(webshop);

    webshopNodeGroup.getNodes().add(webshopNode);
    pubflow.getNodeGroups().add(webshopNodeGroup);

    final NodeGroup cacheNodeGroup = DummyLandscapeHelper.createNodeGroup("10.0.0.8", pubflow);
    final Node cacheNode = DummyLandscapeHelper.createNode("10.0.0.8", cacheNodeGroup, landscape);

    final Application cache = DummyLandscapeHelper.createApplication("Cache", cacheNode, landscape);

    final Application databaseConnector =
        DummyLandscapeHelper.createApplication("Database Connector", cacheNode, landscape);
    createDatabaseConnector(databaseConnector);

    cacheNodeGroup.getNodes().add(cacheNode);
    pubflow.getNodeGroups().add(cacheNodeGroup);

    DummyLandscapeHelper.createApplicationCommunication(requestsApp, ocnEditorApp, landscape, 100);

    DummyLandscapeHelper.createApplicationCommunication(pangeaApp, pangeaApp2, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(pangeaApp2, pangeaApp3, landscape, 100);
    DummyLandscapeHelper
        .createApplicationCommunication(ocnEditorApp, ocnDatabaseApp, landscape, 100);
    DummyLandscapeHelper
        .createApplicationCommunication(ocnDatabaseApp, ocnDatabaseApp2, landscape, 100);
    DummyLandscapeHelper
        .createApplicationCommunication(ocnEditorApp, ocnEditorApp2, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(ocnDatabaseApp, workflow1, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(workflow1, pangeaApp, landscape, 100);

    DummyLandscapeHelper.createApplicationCommunication(workflow1, kielprintsApp, landscape, 100);
    DummyLandscapeHelper
        .createApplicationCommunication(kielprintsApp, kielprintsApp2, landscape, 100);
    DummyLandscapeHelper
        .createApplicationCommunication(kielprintsApp2, kielprintsApp3, landscape, 100);

    DummyLandscapeHelper.createApplicationCommunication(workflow1, portalApp, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(portalApp, portalApp2, landscape, 100);

    DummyLandscapeHelper.createApplicationCommunication(jira1, postgreSql, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(jira2, postgreSql, landscape, 200);

    DummyLandscapeHelper.createApplicationCommunication(jira1, workflow1, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(jira1, workflow2, landscape, 500);
    DummyLandscapeHelper.createApplicationCommunication(jira1, workflow3, landscape, 100);

    DummyLandscapeHelper.createApplicationCommunication(jira2, workflow4, landscape, 200);

    DummyLandscapeHelper.createApplicationCommunication(workflow1, provenance1, landscape, 400);
    DummyLandscapeHelper.createApplicationCommunication(workflow2, provenance2, landscape, 300);
    DummyLandscapeHelper.createApplicationCommunication(workflow3, provenance3, landscape, 500);
    DummyLandscapeHelper.createApplicationCommunication(workflow4, provenance4, landscape, 200);

    DummyLandscapeHelper.createApplicationCommunication(workflow1, cache, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(workflow2, cache, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(workflow3, cache, landscape, 300);
    DummyLandscapeHelper.createApplicationCommunication(workflow4, cache, landscape, 100);

    DummyLandscapeHelper
        .createApplicationCommunication(cache, databaseConnector, landscape, 300 * 2);

    DummyLandscapeHelper.createApplicationCommunication(provenance1, webshop, landscape, 100);
    DummyLandscapeHelper.createApplicationCommunication(provenance2, webshop, landscape, 200);
    DummyLandscapeHelper.createApplicationCommunication(provenance3, webshop, landscape, 300);
    DummyLandscapeHelper.createApplicationCommunication(provenance4, webshop, landscape, 100);

    landscape.createOutgoingApplicationCommunication();

    return landscape;
  }

  /**
   * <<<<<<< HEAD Creates a dummy webshop application within the dummy landscape
   *
   * @param application
   * @return webshop application ======= Creating a communication between two clazzes within the
   *         dummy landscape.
   *
   * @param traceId the id of the trace
   * @param requests the request
   * @param sourceClazz the source class
   * @param targetClazz the target class
   * @param application the appliaction >>>>>>> dev-1
   */
  private static Application createWebshopApplication(final Application application) {

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
    final String firstTraceId = "1";

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
    final String secondTraceId = "2";

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

    return application;
  }

  /**
   * Creates a dummy database-query including application within the dummy landscape
   *
   * @param application
   * @return database connector application
   */
  private static Application createDatabaseConnector(final Application application) {
    final Component org = DummyLandscapeHelper.createComponent("org", null, application);
    application.getComponents().add(org);
    final Component mapleLeaf = DummyLandscapeHelper.createComponent("database", org, application);
    final Component database =
        DummyLandscapeHelper.createComponent("connector", mapleLeaf, application);
    DummyLandscapeHelper.createClazz("Connection", database, 80);

    final LinkedList<DatabaseQuery> dbQueryList = new LinkedList<>();

    final int maxIterations = 25;
    for (int i = 0; i < maxIterations; i++) {
      DatabaseQuery dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement(
          "CREATE TABLE IF NOT EXISTS `order` (oid integer PRIMARY KEY, name text NOT NULL, "
              + "email text NOT NULL, odate text NOT NULL, itemid integer NOT NULL);");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
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
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) "
          + "VALUES('" + DummyLandscapeHelper.getNextSequenceId()
          + "', 'Carol K. Durham', 'durham@uni-kiel.de', '2017-10-08', '1');");
      dbQueryTmp.setReturnValue("null");
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Carol K. Durham");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);

      dbQueryTmp = new DatabaseQuery(idGen.generateId());
      dbQueryTmp.setStatementType("Statement");
      dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Tom B. Erichsen");
      dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
      dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
      dbQueryTmp.setTimestamp(java.lang.System.currentTimeMillis());
      dbQueryTmp.setParentApplication(application);
      dbQueryList.add(dbQueryTmp);
    }
    application.setDatabaseQueries(dbQueryList);

    return application;
  }
  // CHECKSTYLE.ON: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines
  // CHECKSTYLE.ON: MultipleStringLiteralsCheck - Much more readable than NOCS in many lines

}
