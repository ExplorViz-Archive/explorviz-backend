package net.explorviz.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import explorviz.live_trace_processing.record.IRecord;
import explorviz.live_trace_processing.record.event.AbstractAfterEventRecord;
import explorviz.live_trace_processing.record.event.AbstractAfterFailedEventRecord;
import explorviz.live_trace_processing.record.event.AbstractBeforeOperationEventRecord;
import explorviz.live_trace_processing.record.event.AbstractEventRecord;
import explorviz.live_trace_processing.record.event.constructor.BeforeConstructorEventRecord;
import explorviz.live_trace_processing.record.event.jdbc.AfterJDBCOperationEventRecord;
import explorviz.live_trace_processing.record.event.jdbc.BeforeJDBCOperationEventRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeReceivedRemoteCallRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeSentRemoteCallRecord;
import explorviz.live_trace_processing.record.event.remote.BeforeUnknownReceivedRemoteCallRecord;
import explorviz.live_trace_processing.record.misc.SystemMonitoringRecord;
import explorviz.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import explorviz.live_trace_processing.record.trace.Trace;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.Component;
import net.explorviz.model.DatabaseQuery;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.System;
import net.explorviz.model.helper.ELanguage;
import net.explorviz.repository.helper.Signature;
import net.explorviz.repository.helper.SignatureParser;
import net.explorviz.server.main.Configuration;

public class InsertionRepositoryPart {

	static final Logger logger = LoggerFactory.getLogger(InsertionRepositoryPart.class.getName());
	private static final String DEFAULT_COMPONENT_NAME = "(default)";

	private final Map<String, Node> nodeCache = new HashMap<String, Node>();
	private final Map<String, Application> applicationCache = new HashMap<String, Application>();
	private final Map<Application, Map<String, Clazz>> clazzCache = new HashMap<Application, Map<String, Clazz>>();

	public void insertIntoModel(final IRecord inputIRecord, final Landscape landscape,
			final RemoteCallRepositoryPart remoteCallRepositoryPart) {

		if (inputIRecord instanceof Trace) {
			final Trace trace = (Trace) inputIRecord;

			// if (Configuration.rsfExportEnabled) {
			// RigiStandardFormatExporter.insertTrace(trace);
			// }

			final List<HostApplicationMetaDataRecord> hostApplicationMetadataList = trace.getTraceEvents().get(0)
					.getHostApplicationMetadataList();

			synchronized (landscape) {
				for (int i = 0; i < hostApplicationMetadataList.size(); i++) {
					final HostApplicationMetaDataRecord hostApplicationRecord = hostApplicationMetadataList.get(i);
					final System system = seekOrCreateSystem(landscape, hostApplicationRecord.getSystemname());

					final boolean isNewNode = nodeCache.get(
							hostApplicationRecord.getHostname() + "_" + hostApplicationRecord.getIpaddress()) == null;
					final Node node = seekOrCreateNode(hostApplicationRecord, landscape);

					final boolean isNewApplication = applicationCache
							.get(node.getName() + "_" + hostApplicationRecord.getApplication()) == null;
					final Application application = seekOrCreateApplication(node, hostApplicationRecord, landscape);

					if (isNewNode) {
						final NodeGroup nodeGroup = seekOrCreateNodeGroup(system, node);
						nodeGroup.getNodes().add(node);
						node.setParent(nodeGroup);

						nodeGroup.updateName();
					} else {
						if (isNewApplication) {
							// if new app, node might be placed in a different
							// nodeGroup

							final NodeGroup oldNodeGroup = node.getParent();
							oldNodeGroup.getNodes().remove(node);

							final NodeGroup nodeGroup = seekOrCreateNodeGroup(system, node);

							if (oldNodeGroup != nodeGroup) {
								if (oldNodeGroup.getNodes().isEmpty()) {
									oldNodeGroup.getParent().getNodeGroups().remove(oldNodeGroup);
								} else {
									oldNodeGroup.updateName();
								}
							}

							nodeGroup.getNodes().add(node);
							node.setParent(nodeGroup);

							nodeGroup.updateName();
						}
					}

					createCommunicationInApplication(trace, hostApplicationRecord.getHostname(), application, landscape,
							remoteCallRepositoryPart, i);

					// landscape.updateLandscapeAccess(java.lang.System.nanoTime());
				}
			}
		} else if (inputIRecord instanceof SystemMonitoringRecord) {
			final SystemMonitoringRecord systemMonitoringRecord = (SystemMonitoringRecord) inputIRecord;

			for (final Node node : nodeCache.values()) {
				if (node.getName().equalsIgnoreCase(systemMonitoringRecord.getHostApplicationMetadata().getHostname())
						&& node.getIpAddress()
								.equalsIgnoreCase(systemMonitoringRecord.getHostApplicationMetadata().getIpaddress())) {

					node.setCpuUtilization(systemMonitoringRecord.getCpuUtilization());
					node.setFreeRAM(systemMonitoringRecord.getAbsoluteRAM() - systemMonitoringRecord.getUsedRAM());
					node.setUsedRAM(systemMonitoringRecord.getUsedRAM());
				}
			}
		}

	}

	private System seekOrCreateSystem(final Landscape landscape, final String systemname) {
		for (final System system : landscape.getSystems()) {
			if (system.getName().equalsIgnoreCase(systemname)) {
				// familiar system, return old instance
				// of internalLandscape
				return system;
			}
		}

		// New system, add to internalLandscape
		final System system = new System();
		system.initializeID();

		system.setName(systemname);
		system.setParent(landscape);
		landscape.getSystems().add(system);
		addToEvents(landscape, "New system '" + systemname + "' detected");

		return system;
	}

	private void addToEvents(final Landscape landscape, final String event) {
		long currentMillis = java.lang.System.currentTimeMillis();
		while (landscape.getEvents().containsKey(currentMillis)) {
			currentMillis++;
		}
		landscape.getEvents().put(currentMillis, event);
	}

	private void addToErrors(final Landscape landscape, final String cause) {
		long currentMillis = java.lang.System.currentTimeMillis();
		while (landscape.getErrors().containsKey(currentMillis)) {
			currentMillis++;
		}
		landscape.getErrors().put(currentMillis, cause);
	}

	Node seekOrCreateNode(final HostApplicationMetaDataRecord hostApplicationRecord, final Landscape landscape) {
		final String nodeName = hostApplicationRecord.getHostname() + "_" + hostApplicationRecord.getIpaddress();
		Node node = nodeCache.get(nodeName);

		if (node == null) {
			// new node, add to nodeCache for the moment
			// eventual, put in NodeGroup
			node = new Node();
			node.initializeID();

			node.setIpAddress(hostApplicationRecord.getIpaddress());
			node.setName(hostApplicationRecord.getHostname());
			nodeCache.put(nodeName, node);

			addToEvents(landscape, "New node '" + hostApplicationRecord.getHostname() + "' in system '"
					+ hostApplicationRecord.getSystemname() + "' detected");
		}

		return node;
	}

	private NodeGroup seekOrCreateNodeGroup(final System system, final Node node) {
		for (final NodeGroup existingNodeGroup : system.getNodeGroups()) {
			if (!existingNodeGroup.getNodes().isEmpty()) {
				if (nodeMatchesNodeType(node, existingNodeGroup.getNodes().get(0))) {
					// familiar NodeGroup
					return existingNodeGroup;
				}
			}
		}

		// new NodeGroup, add to system, therefore, internalLandscape
		final NodeGroup nodeGroup = new NodeGroup();
		nodeGroup.initializeID();

		nodeGroup.setName(node.getIpAddress());
		system.getNodeGroups().add(nodeGroup);
		nodeGroup.setParent(system);

		return nodeGroup;
	}

	private boolean nodeMatchesNodeType(final Node node, final Node node2) {
		if (node.getApplications().size() != node2.getApplications().size()) {
			return false;
		}

		for (final Application app1 : node.getApplications()) {
			boolean found = false;
			for (final Application app2 : node2.getApplications()) {
				if (app1.getName().equalsIgnoreCase(app2.getName())) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}

		return true;
	}

	Application seekOrCreateApplication(final Node node, final HostApplicationMetaDataRecord hostMetaDataRecord,
			final Landscape landscape) {
		final String applicationName = hostMetaDataRecord.getApplication();
		Application application = applicationCache.get(node.getName() + "_" + applicationName);

		if (application == null) {
			// new application, put in applicationCache for the moment
			// eventually, parent Node must not be in the old NodeGroup
			application = new Application();
			application.initializeID();

			application.setDatabase(isApplicationDatabase(applicationName));
			// application.setId((node.getName() + "_" + applicationName).hashCode());
			application.setLastUsage(java.lang.System.currentTimeMillis());
			application.setName(applicationName);

			final String language = hostMetaDataRecord.getProgrammingLanguage();

			if (language.equalsIgnoreCase("JAVA")) {
				application.setProgrammingLanguage(ELanguage.JAVA);
			} else if (language.equalsIgnoreCase("C")) {
				application.setProgrammingLanguage(ELanguage.C);
			} else if (language.equalsIgnoreCase("CPP")) {
				application.setProgrammingLanguage(ELanguage.CPP);
			} else if (language.equalsIgnoreCase("CSHARP")) {
				application.setProgrammingLanguage(ELanguage.CSHARP);
			} else if (language.equalsIgnoreCase("PERL")) {
				application.setProgrammingLanguage(ELanguage.PERL);
			} else if (language.equalsIgnoreCase("JAVASCRIPT")) {
				application.setProgrammingLanguage(ELanguage.JAVASCRIPT);
			} else if (language.equalsIgnoreCase("PYTHON")) {
				application.setProgrammingLanguage(ELanguage.PYTHON);
			} else if (language.equalsIgnoreCase("RUBY")) {
				application.setProgrammingLanguage(ELanguage.RUBY);
			} else if (language.equalsIgnoreCase("PHP")) {
				application.setProgrammingLanguage(ELanguage.PHP);
			} else {
				application.setProgrammingLanguage(ELanguage.UNKNOWN);
			}

			application.setParent(node);

			node.getApplications().add(application);
			applicationCache.put(node.getName() + "_" + applicationName, application);

			addToEvents(landscape,
					"New application '" + applicationName + "' on node '" + node.getName() + "' detected");
		}
		return application;
	}

	private boolean isApplicationDatabase(final String applicationName) {
		boolean isDatabase = false;
		final List<String> databaseNames = Configuration.DATABASE_NAMES;
		for (final String databaseName : databaseNames) {
			if (applicationName.toLowerCase().contains(databaseName)) {
				isDatabase = true;
				break;
			}
		}
		return isDatabase;
	}

	private void createCommunicationInApplication(final Trace trace, final String currentHostname,
			final Application currentApplication, final Landscape landscape,
			final RemoteCallRepositoryPart remoteCallRepositoryPart, final int runtimeIndex) {
		Clazz callerClazz = null;
		final Stack<Clazz> callerClazzesHistory = new Stack<Clazz>();

		int orderIndex = 1;
		double overallTraceDuration = -1d;

		final int eventsLength = trace.getTraceEvents().size();

		for (int i = 0; i < eventsLength; i++) {
			final AbstractEventRecord event = trace.getTraceEvents().get(i);

			if (event instanceof AbstractBeforeOperationEventRecord) {
				final AbstractBeforeOperationEventRecord abstractBeforeEventRecord = (AbstractBeforeOperationEventRecord) event;

				if (overallTraceDuration < 0d) {
					overallTraceDuration = abstractBeforeEventRecord.getRuntimeStatisticInformationList()
							.get(runtimeIndex).getAverage();
				}

				final String clazzName = getClazzName(abstractBeforeEventRecord);

				final Clazz currentClazz = seekOrCreateClazz(clazzName, currentApplication, abstractBeforeEventRecord
						.getRuntimeStatisticInformationList().get(runtimeIndex).getObjectIds());

				if (callerClazz != null) {
					final boolean isConstructor = abstractBeforeEventRecord instanceof BeforeConstructorEventRecord;
					final String methodName = getMethodName(abstractBeforeEventRecord.getOperationSignature(),
							isConstructor);

					boolean isAbstractConstructor = false;

					if (isConstructor) {
						final BeforeConstructorEventRecord constructor = (BeforeConstructorEventRecord) abstractBeforeEventRecord;
						final String constructorClass = constructor.getClazz()
								.substring(constructor.getClazz().lastIndexOf('.') + 1);
						final String constructorClassFromOperation = methodName.substring(4);

						isAbstractConstructor = !constructorClass.equalsIgnoreCase(constructorClassFromOperation);
					}

					if (!isAbstractConstructor) {
						createOrUpdateCall(callerClazz, currentClazz, currentApplication,
								abstractBeforeEventRecord.getRuntimeStatisticInformationList().get(runtimeIndex)
										.getCount(),
								abstractBeforeEventRecord.getRuntimeStatisticInformationList().get(runtimeIndex)
										.getAverage(),
								overallTraceDuration, abstractBeforeEventRecord.getTraceId(), orderIndex, methodName,
								landscape);
						orderIndex++;
					}

					if (abstractBeforeEventRecord instanceof BeforeJDBCOperationEventRecord) {
						final BeforeJDBCOperationEventRecord jdbcOperationEventRecord = (BeforeJDBCOperationEventRecord) abstractBeforeEventRecord;
						final DatabaseQuery databaseQuery = new DatabaseQuery();
						databaseQuery.initializeID();

						databaseQuery.setSqlStatement(jdbcOperationEventRecord.getSqlStatement());
						currentApplication.getDatabaseQueries().add(databaseQuery);
					}
				}

				callerClazz = currentClazz;
				callerClazzesHistory.push(currentClazz);
			} else if ((event instanceof AbstractAfterEventRecord)
					|| (event instanceof AbstractAfterFailedEventRecord)) {
				if ((event instanceof AbstractAfterFailedEventRecord) && (callerClazz != null)) {
					String cause = ((AbstractAfterFailedEventRecord) event).getCause();
					final String[] splitCause = cause.split("\n");
					if (splitCause.length > 6) {
						cause = splitCause[0] + "\n" + splitCause[1] + "\n" + splitCause[2] + "\n" + splitCause[3]
								+ "\n" + splitCause[4] + "\n" + splitCause[5] + "\n" + "\t ...";
					}
					addToErrors(landscape, "Exception thrown in application '" + currentApplication.getName()
							+ "' by class '" + callerClazz.getFullQualifiedName() + "':\n " + cause);
				}

				final List<DatabaseQuery> databaseQueries = currentApplication.getDatabaseQueries();

				if ((event instanceof AfterJDBCOperationEventRecord) && !databaseQueries.isEmpty()) {
					final AfterJDBCOperationEventRecord jdbcOperationEventRecord = (AfterJDBCOperationEventRecord) event;

					final DatabaseQuery databaseQuery = databaseQueries.get(databaseQueries.size() - 1);
					databaseQuery.setReturnValue(jdbcOperationEventRecord.getFormattedReturnValue());
					databaseQuery.setTimeInNanos(jdbcOperationEventRecord.getMethodDuration());
				}

				if (!callerClazzesHistory.isEmpty()) {
					callerClazzesHistory.pop();
				}
				if (!callerClazzesHistory.isEmpty()) {
					callerClazz = callerClazzesHistory.peek();
				}
			} else if (event instanceof BeforeSentRemoteCallRecord) {
				final BeforeSentRemoteCallRecord sentRemoteCallRecord = (BeforeSentRemoteCallRecord) event;

				remoteCallRepositoryPart.insertSentRecord(callerClazz, sentRemoteCallRecord, landscape, this,
						runtimeIndex);
			} else if (event instanceof BeforeReceivedRemoteCallRecord) {
				final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord = (BeforeReceivedRemoteCallRecord) event;

				Clazz firstReceiverClazz = null;

				if (((i + 1) < eventsLength)
						&& (trace.getTraceEvents().get(i + 1) instanceof AbstractBeforeOperationEventRecord)) {
					final AbstractBeforeOperationEventRecord abstractBeforeEventRecord = (AbstractBeforeOperationEventRecord) trace
							.getTraceEvents().get(i + 1);

					final String clazzName = getClazzName(abstractBeforeEventRecord);

					firstReceiverClazz = seekOrCreateClazz(clazzName, currentApplication, abstractBeforeEventRecord
							.getRuntimeStatisticInformationList().get(runtimeIndex).getObjectIds());
				}

				remoteCallRepositoryPart.insertReceivedRecord(receivedRemoteCallRecord, firstReceiverClazz, landscape,
						this, runtimeIndex);
			} else if (event instanceof BeforeUnknownReceivedRemoteCallRecord) {
			}
		}

	}

	public static String getClazzName(final AbstractBeforeOperationEventRecord abstractBeforeEventRecord) {
		String clazzName = abstractBeforeEventRecord.getClazz();

		if (clazzName.contains("$")) {
			// found an anonymous class
			final String implementedInterface = abstractBeforeEventRecord.getImplementedInterface();

			if ((implementedInterface != null) && !implementedInterface.isEmpty()) {
				final int lastIndexOfDollar = clazzName.lastIndexOf('$');
				if ((lastIndexOfDollar > -1) && ((lastIndexOfDollar + 1) < clazzName.length())) {
					final char suffixChar = clazzName.charAt(lastIndexOfDollar + 1);
					if (('0' <= suffixChar) && (suffixChar <= '9')) {
						String interfaceName = implementedInterface;
						final int interfaceNameIndex = interfaceName.lastIndexOf('.');
						if (interfaceNameIndex > -1) {
							interfaceName = interfaceName.substring(interfaceNameIndex + 1);
						}

						clazzName = clazzName.substring(0, lastIndexOfDollar + 1) + "[" + interfaceName + "]"
								+ clazzName.substring(lastIndexOfDollar + 1);
					}
				}
			}
		}
		return clazzName;
	}

	private void createOrUpdateCall(final Clazz caller, final Clazz callee, final Application application,
			final int requests, final double average, final double overallTraceDuration, final long traceId,
			final int orderIndex, final String methodName, final Landscape landscape) {
		landscape.setActivities(landscape.getActivities() + requests);

		for (final CommunicationClazz commu : application.getCommunications()) {
			if (((commu.getSourceClazz() == caller) && (commu.getTargetClazz() == callee)
					&& (commu.getMethodName().equalsIgnoreCase(methodName)))) {

				commu.addRuntimeInformation(traceId, requests, orderIndex, requests, (float) average,
						(float) overallTraceDuration);
				return;
			}
		}

		final CommunicationClazz commu = new CommunicationClazz();
		commu.initializeID();

		// TODO
		commu.setSourceClazz(caller);
		commu.setTargetClazz(callee);
		commu.getSourceClazz().getOutgoingCommunications().add(commu);
		commu.getTargetClazz().getIncomingCommunications().add(commu);
		//

		commu.addRuntimeInformation(traceId, requests, orderIndex, requests, (float) average,
				(float) overallTraceDuration);
		commu.setMethodName(methodName);

		application.getCommunications().add(commu);
	}

	private Clazz seekOrCreateClazz(final String fullQName, final Application application,
			final TIntHashSet objectIds) {
		final String[] splittedName = fullQName.split("\\.");

		Map<String, Clazz> appCached = clazzCache.get(application);
		if (appCached == null) {
			appCached = new HashMap<String, Clazz>();
			clazzCache.put(application, appCached);
		}
		Clazz clazz = appCached.get(fullQName);

		if (clazz == null) {
			// new clazz
			clazz = seekrOrCreateClazzHelper(fullQName, splittedName, application, null, 0);
			appCached.put(fullQName, clazz);
		}

		if (objectIds != null) {
			final TIntIterator iterator = objectIds.iterator();
			while (iterator.hasNext()) {
				clazz.getObjectIds().add(iterator.next());
			}
			clazz.setInstanceCount(clazz.getObjectIds().size());
		}

		return clazz;
	}

	private Clazz seekrOrCreateClazzHelper(final String fullQName, final String[] splittedName,
			final Application application, Component parent, final int index) {
		final String currentPart = splittedName[index];

		if (index < (splittedName.length - 1)) {
			List<Component> list = null;

			if (parent == null) {
				list = application.getComponents();
			} else {
				list = parent.getChildren();
			}

			for (final Component component : list) {
				if (component.getName().equalsIgnoreCase(currentPart)) {
					return seekrOrCreateClazzHelper(fullQName, splittedName, application, component, index + 1);
				}
			}
			final Component component = new Component();
			component.initializeID();

			String fullQNameComponent = "";
			for (int i = 0; i <= index; i++) {
				fullQNameComponent += splittedName[i] + ".";
			}
			fullQNameComponent = fullQNameComponent.substring(0, fullQNameComponent.length() - 1);
			component.setFullQualifiedName(fullQNameComponent);
			component.setName(currentPart);
			component.setParentComponent(parent);
			component.setBelongingApplication(application);
			list.add(component);
			return seekrOrCreateClazzHelper(fullQName, splittedName, application, component, index + 1);
		} else {
			if (parent == null) {
				for (final Component component : application.getComponents()) {
					if (component.getFullQualifiedName().equals(DEFAULT_COMPONENT_NAME)) {
						parent = component;
						break;
					}
				}

				if (parent == null) {
					final Component component = new Component();
					component.initializeID();

					component.setFullQualifiedName(DEFAULT_COMPONENT_NAME);
					component.setName(DEFAULT_COMPONENT_NAME);
					component.setParentComponent(null);
					component.setBelongingApplication(application);
					application.getComponents().add(component);
					parent = component;
				}
			}

			for (final Clazz clazz : parent.getClazzes()) {
				if (clazz.getName().equalsIgnoreCase(currentPart)) {
					// familiar clazz
					return clazz;
				}
			}

			// new clazz
			final Clazz clazz = new Clazz();
			clazz.initializeID();

			clazz.setName(currentPart);
			clazz.setFullQualifiedName(fullQName);
			clazz.setParent(parent);
			parent.getClazzes().add(clazz);
			return clazz;
		}
	}

	public static String getMethodName(final String operationSignatureStr, final boolean constructor) {
		final Signature signature = SignatureParser.parse(operationSignatureStr, constructor);
		return signature.getOperationName();
	}
}
