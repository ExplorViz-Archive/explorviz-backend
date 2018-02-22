package net.explorviz.repository;

import java.util.LinkedList;
import java.util.Random;

import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.DatabaseQuery;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;
import net.explorviz.repository.helper.DummyLandscapeHelper;

/**
 * Creates a dummy landscape for developing or demo purposes
 *
 * @author Christian Zirkelbach (czi@informatik.uni-kiel.de)
 *
 */
class LandscapeDummyCreator {
	public static int counter = 1;
	static int applicationId = 0;
	static Landscape dummyLandscape = null;
	public static int formatFactor = (1024 * 1024 * 1024);

	public static Landscape createDummyLandscape() {

		if (dummyLandscape != null) {
			dummyLandscape.setOverallCalls(new Random().nextInt(300000));
			return dummyLandscape;
		}

		final Landscape landscape = new Landscape();
		landscape.initializeID();
		landscape.setOverallCalls(new Random().nextInt(300000));

		final System requestSystem = new System();
		requestSystem.initializeID();
		requestSystem.setName("Requests");
		requestSystem.setParent(landscape);
		landscape.getSystems().add(requestSystem);

		final NodeGroup requestsNodeGroup = createNodeGroup("10.0.99.1", landscape, requestSystem);
		final Node requestsNode = createNode("10.0.99.1", requestsNodeGroup);

		final Application requestsApp = createApplication("Requests", requestsNode);

		requestsNodeGroup.getNodes().add(requestsNode);
		requestSystem.getNodeGroups().add(requestsNodeGroup);

		final System ocnEditor = new System();
		ocnEditor.initializeID();
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
		ocnDatabase.initializeID();
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
		kielprints.initializeID();

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
		portal.initializeID();
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
		pangea.initializeID();
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
		pubflow.initializeID();
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

		final NodeGroup postgreSQLNodeGroup = createNodeGroup("10.0.0.3", landscape, pubflow);
		final Node postgreSQLNode = createNode("10.0.0.3", postgreSQLNodeGroup);

		final Application postgreSQL = createApplication("PostgreSQL", postgreSQLNode);

		postgreSQLNodeGroup.getNodes().add(postgreSQLNode);
		pubflow.getNodeGroups().add(postgreSQLNodeGroup);

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

		final NodeGroup neo4jNodeGroup = createNodeGroup("10.0.0.9", landscape, pubflow);
		final Node neo4jNode = createNode("10.0.0.9", neo4jNodeGroup);
		final Application neo4j = createApplication("Neo4j", neo4jNode);

		createNeo4JDummyApplication(neo4j);

		neo4jNodeGroup.getNodes().add(neo4jNode);
		pubflow.getNodeGroups().add(neo4jNodeGroup);

		final NodeGroup cacheNodeGroup = createNodeGroup("10.0.0.8", landscape, pubflow);
		final Node cacheNode = createNode("10.0.0.8", cacheNodeGroup);

		final Application cache = createApplication("Cache", cacheNode);

		final Application mapleLeafApplication = createApplication("MapleLeaf DB Connector", cacheNode);
		createMapleLeafApplication(mapleLeafApplication);

		cacheNodeGroup.getNodes().add(cacheNode);
		pubflow.getNodeGroups().add(cacheNodeGroup);

		createCommunication(requestsApp, ocnEditorApp, landscape, 100);

		createCommunication(pangeaApp, pangeaApp2, landscape, 100);
		createCommunication(pangeaApp2, pangeaApp3, landscape, 100);
		createCommunication(ocnEditorApp, ocnDatabaseApp, landscape, 100);
		createCommunication(ocnDatabaseApp, ocnDatabaseApp2, landscape, 100);
		createCommunication(ocnEditorApp, ocnEditorApp2, landscape, 100);
		createCommunication(ocnDatabaseApp, workflow1, landscape, 100);
		createCommunication(workflow1, pangeaApp, landscape, 100);

		createCommunication(workflow1, kielprintsApp, landscape, 100);
		createCommunication(kielprintsApp, kielprintsApp2, landscape, 100);
		createCommunication(kielprintsApp2, kielprintsApp3, landscape, 100);

		createCommunication(workflow1, portalApp, landscape, 100);
		createCommunication(portalApp, portalApp2, landscape, 100);

		createCommunication(jira1, postgreSQL, landscape, 100);
		createCommunication(jira2, postgreSQL, landscape, 200);

		createCommunication(jira1, workflow1, landscape, 100);
		createCommunication(jira1, workflow2, landscape, 500);
		createCommunication(jira1, workflow3, landscape, 100);

		createCommunication(jira2, workflow4, landscape, 200);

		createCommunication(workflow1, provenance1, landscape, 400);
		createCommunication(workflow2, provenance2, landscape, 300);
		createCommunication(workflow3, provenance3, landscape, 500);
		createCommunication(workflow4, provenance4, landscape, 200);

		createCommunication(workflow1, cache, landscape, 100);
		createCommunication(workflow2, cache, landscape, 100);
		createCommunication(workflow3, cache, landscape, 300);
		createCommunication(workflow4, cache, landscape, 100);

		createCommunication(cache, mapleLeafApplication, landscape, 300 * 2);

		createCommunication(provenance1, neo4j, landscape, 100);
		createCommunication(provenance2, neo4j, landscape, 200);
		createCommunication(provenance3, neo4j, landscape, 300);
		createCommunication(provenance4, neo4j, landscape, 100);

		final Landscape preparedLandscape = LandscapePreparer.prepareLandscape(landscape);
		counter = 1;
		dummyLandscape = preparedLandscape;

		return preparedLandscape;

	}

	/**
	 * Creates a new {@link Nodegroup}
	 *
	 * @param name
	 * @param parent
	 * @param system
	 * @return
	 */
	private static NodeGroup createNodeGroup(final String name, final Landscape parent, final System system) {
		final NodeGroup nodeGroup = new NodeGroup();
		nodeGroup.initializeID();
		nodeGroup.setName(name);
		nodeGroup.setParent(system);
		return nodeGroup;
	}

	/**
	 * Creates a new {@link Node}
	 *
	 * @param ipAddress
	 * @param parent
	 * @return
	 */
	private static Node createNode(final String ipAddress, final NodeGroup parent) {
		final Node node = new Node();
		node.initializeID();
		node.setIpAddress(ipAddress);
		node.setParent(parent);

		// set random usage
		node.setCpuUtilization(DummyLandscapeHelper.getRandomNum(10, 100));
		node.setFreeRAM(DummyLandscapeHelper.getRandomNum(1, 4) * formatFactor);
		node.setUsedRAM(DummyLandscapeHelper.getRandomNum(1, 4) * formatFactor);

		return node;
	}

	/**
	 * Creates a new {@link Application}
	 *
	 * @param name
	 * @param parent
	 * @return
	 */
	private static Application createApplication(final String name, final Node parent) {
		final Application application = new Application();
		application.initializeID();

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

	/**
	 * Creates a new {@link ApplicationCommunication}
	 *
	 * @param source
	 * @param target
	 * @param landscape
	 * @param requests
	 * @return
	 */
	private static ApplicationCommunication createCommunication(final Application source, final Application target,
			final Landscape landscape, final int requests) {
		final ApplicationCommunication communication = new ApplicationCommunication();
		communication.initializeID();
		communication.setSourceApplication(source);
		communication.setTargetApplication(target);
		communication.setRequests(requests);
		source.getOutgoingApplicationCommunications().add(communication);

		return communication;
	}

	/**
	 * Creates a new {@link Component}
	 *
	 * @param name
	 * @param parent
	 * @param app
	 * @return
	 */
	private static Component createComponent(final String name, final Component parent, final Application app) {
		final Component component = new Component();
		component.initializeID();
		component.setName(name);
		component.setParentComponent(parent);
		component.setBelongingApplication(app);
		if (parent != null) {
			component.setFullQualifiedName(parent.getFullQualifiedName() + "." + name);
			parent.getChildren().add(component);
		} else {
			component.setFullQualifiedName(name);
		}
		return component;
	}

	/**
	 * Creates a new {@link Clazz}
	 *
	 * @param name
	 * @param component
	 * @param instanceCount
	 */
	private static Clazz createClazz(final String name, final Component component, final int instanceCount) {
		final Clazz clazz = new Clazz();
		clazz.initializeID();
		clazz.setName(name);
		clazz.setFullQualifiedName(component.getFullQualifiedName() + "." + name);
		clazz.setInstanceCount(instanceCount);
		clazz.setParent(component);
		component.getClazzes().add(clazz);

		return clazz;
	}

	/**
	 * Creates a new {@link ClazzCommunication}
	 *
	 * @param requests
	 * @param sourceClazz
	 * @param targetClazz
	 * @param application
	 */
	private static ClazzCommunication createCommuClazz(final int requests, final Clazz sourceClazz,
			final Clazz targetClazz, final Application application) {
		final ClazzCommunication communication = new ClazzCommunication();
		communication.initializeID();
		communication.addRuntimeInformation(0L, 1, 1, requests, 10, 10);
		communication.setOperationName("getMethod()");
		communication.setSourceClazz(sourceClazz);
		communication.setTargetClazz(targetClazz);
		communication.getSourceClazz().getOutgoingCommunications().add(communication);

		return communication;
	}

	/**
	 * Creates a dummy application "Neo4J"
	 *
	 * @param application
	 */
	private static Application createNeo4JDummyApplication(final Application application) {
		final Component org = createComponent("org", null, application);
		application.getComponents().add(org);
		final Component neo4j = createComponent("neo4j", org, application);

		final Component graphdb = createComponent("graphdb", neo4j, application);
		final Clazz graphDbClazz = createClazz("Label", graphdb, 20);
		createClazz("Label2", graphdb, 20);
		createClazz("Label3", graphdb, 20);
		createClazz("Label4", graphdb, 20);
		createClazz("Label5", graphdb, 20);

		final Component helpers = createComponent("helpers", neo4j, application);
		final Clazz helpersClazz = createClazz("x", helpers, 30);
		createClazz("x2", helpers, 40);
		createClazz("x3", helpers, 35);
		createClazz("x4", helpers, 35);
		createClazz("x5", helpers, 35);

		final Component tooling = createComponent("tooling", neo4j, application);
		final Clazz toolingClazz = createClazz("AccountSqlMapDao", tooling, 5);
		createClazz("BaseSqlMapDao", tooling, 20);
		createClazz("CategorySqlMapDao", tooling, 30);
		createClazz("ItemSqlMapDao", tooling, 35);
		createClazz("ProductSqlMapDao", tooling, 20);
		createClazz("SequenceSqlMapDao", tooling, 15);

		final Component unsafe = createComponent("unsafe", neo4j, application);
		final Clazz unsafeClazz = createClazz("AbstractBean", unsafe, 20);
		createClazz("CartBean", unsafe, 40);

		final Component kernel = createComponent("kernel", neo4j, application);

		final Component api = createComponent("api", kernel, application);
		final Clazz apiClazz = createClazz("cleanupX", api, 25);
		createClazz("cleanupX", api, 25);
		final Component configuration = createComponent("configuration", kernel, application);
		final Clazz configurationClazz = createClazz("cleanupX", configuration, 35);
		createClazz("cleanupX", configuration, 5);
		final Component myextension = createComponent("extension", kernel, application);
		createClazz("cleanupX", myextension, 25);
		createClazz("cleanupX", myextension, 5);
		final Component guard = createComponent("guard", kernel, application);
		final Clazz guardClazz = createClazz("cleanupX", guard, 35);
		createClazz("cleanupX", guard, 25);

		final Component impl = createComponent("impl", kernel, application);
		final Clazz implClazz = createClazz("cleanupX", impl, 45);
		final Component annotations = createComponent("annotations", impl, application);
		createClazz("cleanupX", annotations, 35);
		final Component apiImpl = createComponent("api", impl, application);
		final Clazz apiImplClazz = createClazz("cleanupX", apiImpl, 25);
		final Component cache = createComponent("cache", impl, application);
		createClazz("cleanupX", cache, 45);
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

		createCommuClazz(40, graphDbClazz, helpersClazz, application);
		createCommuClazz(100, toolingClazz, implClazz, application);
		createCommuClazz(60, implClazz, helpersClazz, application);
		createCommuClazz(60, implClazz, apiImplClazz, application);
		createCommuClazz(1000, implClazz, loggingClazz, application);
		createCommuClazz(100, guardClazz, unsafeClazz, application);
		createCommuClazz(100, apiClazz, configurationClazz, application);
		createCommuClazz(150, lifecycleClazz, loggingClazz, application);
		createCommuClazz(1200, guardClazz, implClazz, application);

		return application;
	}

	/**
	 * A dummy application containing sample database queries based on
	 * https://github.com/czirkelbach/kiekerSampleApplication/
	 *
	 * @param application
	 */
	private static Application createMapleLeafApplication(final Application application) {
		final Component org = createComponent("org", null, application);
		application.getComponents().add(org);
		final Component mapleLeaf = createComponent("mapleleaf", org, application);
		final Component database = createComponent("database", mapleLeaf, application);
		createClazz("Label", database, 20);

		final LinkedList<DatabaseQuery> dbQueryList = new LinkedList<DatabaseQuery>();

		final int maxIterations = 25;
		for (int i = 0; i < maxIterations; i++) {
			DatabaseQuery dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement(
					"CREATE TABLE IF NOT EXISTS `order` (oid integer PRIMARY KEY, name text NOT NULL, email text NOT NULL, odate text NOT NULL, itemid integer NOT NULL);");
			dbQueryTmp.setReturnValue("null");
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);

			dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) " + "VALUES('"
					+ DummyLandscapeHelper.getNextSequenceId()
					+ "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
			dbQueryTmp.setReturnValue("null");
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);

			dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) " + "VALUES('"
					+ DummyLandscapeHelper.getNextSequenceId()
					+ "'Tom B. Erichsen', 'erichsen@uni-kiel.de', '2017-11-16', '1');");
			dbQueryTmp.setReturnValue("null");
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);

			dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement("INSERT INTO `order` (oid, name, email, odate, itemid) " + "VALUES('"
					+ DummyLandscapeHelper.getNextSequenceId()
					+ "', 'Carol K. Durham', 'durham@uni-kiel.de', '2017-10-08', '1');");
			dbQueryTmp.setReturnValue("null");
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);

			dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Carol K. Durham");
			dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);

			dbQueryTmp = new DatabaseQuery();
			dbQueryTmp.initializeID();
			dbQueryTmp.setSqlStatement("SELECT * FROM `order` WHERE name = Tom B. Erichsen");
			dbQueryTmp.setReturnValue(String.valueOf(DummyLandscapeHelper.getRandomNum(5, 100)));
			dbQueryTmp.setResponseTime(DummyLandscapeHelper.getRandomNum(10, 1000));
			dbQueryTmp.setTimestamp(DummyLandscapeHelper.getCurrentTimestamp());
			dbQueryList.add(dbQueryTmp);
		}
		application.setDatabaseQueries(dbQueryList);

		return application;
	}

}
