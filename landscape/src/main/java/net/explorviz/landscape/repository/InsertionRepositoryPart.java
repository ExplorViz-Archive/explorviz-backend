package net.explorviz.landscape.repository; // NOPMD

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
import explorviz.live_trace_processing.record.misc.SystemMonitoringRecord;
import explorviz.live_trace_processing.record.trace.HostApplicationMetaDataRecord;
import explorviz.live_trace_processing.record.trace.Trace;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.helper.EProgrammingLanguage;
import net.explorviz.landscape.model.helper.ModelHelper;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.repository.helper.Signature;
import net.explorviz.landscape.repository.helper.SignatureParser;

/**
 * InsertionRepositoryPart TODODescr.
 */
public class InsertionRepositoryPart {

  private static final String DEFAULT_COMPONENT_NAME = "(default)";

  private final Map<String, Node> nodeCache = new HashMap<>();
  private final Map<String, Application> applicationCache = new HashMap<>();
  private final Map<Application, Map<String, Clazz>> clazzCache = new HashMap<>();

  /**
   * TODODescr.
   *
   * @param inputIRecord - Record that will be inserted into the passed landscape
   * @param landscape - Target for the insertion of records
   * @param remoteCallRepositoryPart - TODOa
   */
  public void insertIntoModel(final IRecord inputIRecord, final Landscape landscape,
      final RemoteCallRepositoryPart remoteCallRepositoryPart) {

    if (inputIRecord instanceof Trace) {
      final Trace trace = (Trace) inputIRecord;

      final List<HostApplicationMetaDataRecord> hostApplicationMetadataList =
          trace.getTraceEvents().get(0).getHostApplicationMetadataList();

      synchronized (landscape) {
        for (int i = 0; i < hostApplicationMetadataList.size(); i++) {
          final HostApplicationMetaDataRecord hostApplicationRecord =
              hostApplicationMetadataList.get(i);
          final System system =
              this.seekOrCreateSystem(landscape, hostApplicationRecord.getSystemname());

          final boolean isNewNode = this.nodeCache.get(hostApplicationRecord.getHostname() + "_"
              + hostApplicationRecord.getIpaddress()) == null;
          final Node node = this.seekOrCreateNode(hostApplicationRecord, landscape);

          final boolean isNewApplication = this.applicationCache
              .get(node.getName() + "_" + hostApplicationRecord.getApplication()) == null;
          final Application application =
              this.seekOrCreateApplication(node, hostApplicationRecord, landscape);

          if (isNewNode) {
            final NodeGroup nodeGroup = this.seekOrCreateNodeGroup(system, node);
            nodeGroup.getNodes().add(node);
            node.setParent(nodeGroup);

            nodeGroup.updateName();
          } else {
            if (isNewApplication) {
              // if new app, node might be placed in a different
              // nodeGroup

              final NodeGroup oldNodeGroup = node.getParent();
              oldNodeGroup.getNodes().remove(node);

              final NodeGroup nodeGroup = this.seekOrCreateNodeGroup(system, node);

              if (!oldNodeGroup.equals(nodeGroup)) {
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

          this.createCommuInApp(trace, hostApplicationRecord.getHostname(), application, landscape,
              remoteCallRepositoryPart, i);

          // landscape.updateLandscapeAccess(java.lang.System.nanoTime());
        }
      }
    } else if (inputIRecord instanceof SystemMonitoringRecord) {
      final SystemMonitoringRecord systemMonitoringRecord = (SystemMonitoringRecord) inputIRecord;

      for (final Node node : this.nodeCache.values()) {
        if (node.getName()
            .equalsIgnoreCase(systemMonitoringRecord.getHostApplicationMetadata().getHostname())
            && node.getIpAddress().equalsIgnoreCase(
                systemMonitoringRecord.getHostApplicationMetadata().getIpaddress())) {

          node.setCpuUtilization(systemMonitoringRecord.getCpuUtilization());
          node.setFreeRam(
              systemMonitoringRecord.getAbsoluteRAM() - systemMonitoringRecord.getUsedRAM());
          node.setUsedRam(systemMonitoringRecord.getUsedRAM());
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
    system.initializeId();

    system.setName(systemname);
    system.setParent(landscape);
    landscape.getSystems().add(system);
    this.addToEvents(landscape, "New system '" + systemname + "' detected"); // NOCS

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
    while (landscape.getExceptions().containsKey(currentMillis)) {
      currentMillis++;
    }
    landscape.getExceptions().put(currentMillis, cause);
  }

  protected Node seekOrCreateNode(final HostApplicationMetaDataRecord hostApplicationRecord,
      final Landscape landscape) {
    final String nodeName =
        hostApplicationRecord.getHostname() + "_" + hostApplicationRecord.getIpaddress();
    Node node = this.nodeCache.get(nodeName);

    if (node == null) {
      // new node, add to nodeCache for the moment
      // eventual, put in NodeGroup
      node = new Node();
      node.initializeId();

      node.setIpAddress(hostApplicationRecord.getIpaddress());
      node.setName(hostApplicationRecord.getHostname());
      this.nodeCache.put(nodeName, node);

      this.addToEvents(landscape, "New node '" + hostApplicationRecord.getHostname()
          + "' in system '" + hostApplicationRecord.getSystemname() + "' detected");
    }

    return node;
  }

  private NodeGroup seekOrCreateNodeGroup(final System system, final Node node) {
    for (final NodeGroup existingNodeGroup : system.getNodeGroups()) {
      if (!existingNodeGroup.getNodes().isEmpty()
          && this.nodeMatchesNodeType(node, existingNodeGroup.getNodes().get(0))) {
        // familiar NodeGroup
        return existingNodeGroup;
      }
    }

    // new NodeGroup, add to system, therefore, internalLandscape
    final NodeGroup nodeGroup = new NodeGroup();
    nodeGroup.initializeId();

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

  Application seekOrCreateApplication(final Node node,
      final HostApplicationMetaDataRecord hostMetaDataRecord, final Landscape landscape) {
    final String applicationName = hostMetaDataRecord.getApplication();
    Application application = this.applicationCache.get(node.getName() + "_" + applicationName);

    if (application == null) {
      // new application, put in applicationCache for the moment
      // eventually, parent Node must not be in the old NodeGroup
      application = new Application();
      application.initializeId();
      // application.setId((node.getName() + "_" + applicationName).hashCode());
      application.setLastUsage(java.lang.System.currentTimeMillis());
      application.setName(applicationName);

      final String language = hostMetaDataRecord.getProgrammingLanguage();

      if ("JAVA".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.JAVA);
      } else if ("C".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.C);
      } else if ("CPP".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.CPP);
      } else if ("CSHARP".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.CSHARP);
      } else if ("PERL".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.PERL);
      } else if ("JAVASCRIPT".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.JAVASCRIPT);
      } else if ("PYTHON".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.PYTHON);
      } else if ("RUBY".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.RUBY);
      } else if ("PHP".equalsIgnoreCase(language)) {
        application.setProgrammingLanguage(EProgrammingLanguage.PHP);
      } else {
        application.setProgrammingLanguage(EProgrammingLanguage.UNKNOWN);
      }

      application.setParent(node);

      node.getApplications().add(application);
      this.applicationCache.put(node.getName() + "_" + applicationName, application);

      this.addToEvents(landscape,
          "New application '" + applicationName + "' on node '" + node.getName() + "' detected");
    }
    return application;
  }

  /**
   * Communication between clazzes within a single application.
   *
   * @param trace - TODOa
   * @param currentHostname - TODOa
   * @param currentApplication - TODOa
   * @param landscape - TODOa
   * @param remoteCallRepositoryPart - TODOa
   * @param runtimeIndex - TODOa
   */
  private void createCommuInApp(final Trace trace, final String currentHostname,
      final Application currentApplication, final Landscape landscape,
      final RemoteCallRepositoryPart remoteCallRepositoryPart, final int runtimeIndex) {
    Clazz callerClazz = null;
    final Stack<Clazz> callerClazzesHistory = new Stack<>();

    int orderIndex = 1;
    double overallTraceDuration = -1d;

    final int eventsLength = trace.getTraceEvents().size();

    for (int i = 0; i < eventsLength; i++) {
      final AbstractEventRecord event = trace.getTraceEvents().get(i);

      if (event instanceof AbstractBeforeOperationEventRecord) {
        final AbstractBeforeOperationEventRecord abstractBeforeEventRecord =
            (AbstractBeforeOperationEventRecord) event;

        if (overallTraceDuration < 0d) { // NOPMD
          overallTraceDuration = abstractBeforeEventRecord.getRuntimeStatisticInformationList()
              .get(runtimeIndex).getAverage();
        }

        final String clazzName = getClazzName(abstractBeforeEventRecord);

        final Clazz currentClazz =
            this.seekOrCreateClazz(clazzName, currentApplication, abstractBeforeEventRecord
                .getRuntimeStatisticInformationList().get(runtimeIndex).getObjectIds());

        if (callerClazz != null) {
          final boolean isConstructor =
              abstractBeforeEventRecord instanceof BeforeConstructorEventRecord;
          final String methodName =
              getMethodName(abstractBeforeEventRecord.getOperationSignature(), isConstructor);

          boolean isAbstractConstructor = false;

          if (isConstructor) {
            final BeforeConstructorEventRecord constructor =
                (BeforeConstructorEventRecord) abstractBeforeEventRecord;
            final String constructorClass =
                constructor.getClazz().substring(constructor.getClazz().lastIndexOf('.') + 1);
            final String constructorClassFromOperation = methodName.substring(4);

            isAbstractConstructor =
                !constructorClass.equalsIgnoreCase(constructorClassFromOperation);
          }

          if (!isAbstractConstructor) {
            this.createOrUpdateCall(callerClazz, currentClazz, currentApplication,
                abstractBeforeEventRecord.getRuntimeStatisticInformationList().get(runtimeIndex)
                    .getCount(),
                abstractBeforeEventRecord.getRuntimeStatisticInformationList().get(runtimeIndex)
                    .getAverage(),
                overallTraceDuration, abstractBeforeEventRecord.getTraceId(), orderIndex,
                methodName, landscape);
            orderIndex++;
          }

          if (abstractBeforeEventRecord instanceof BeforeJDBCOperationEventRecord) {
            final BeforeJDBCOperationEventRecord jdbcOperationEventRecord =
                (BeforeJDBCOperationEventRecord) abstractBeforeEventRecord;
            final DatabaseQuery databaseQuery = new DatabaseQuery(); // NOPMD
            databaseQuery.initializeId();

            databaseQuery.setSqlStatement(jdbcOperationEventRecord.getSqlStatement());
            currentApplication.getDatabaseQueries().add(databaseQuery);
          }
        }

        callerClazz = currentClazz;
        callerClazzesHistory.push(currentClazz);
      } else if (event instanceof AbstractAfterEventRecord
          || event instanceof AbstractAfterFailedEventRecord) {
        if (event instanceof AbstractAfterFailedEventRecord && callerClazz != null) {
          String cause = ((AbstractAfterFailedEventRecord) event).getCause();
          final String[] splitCause = cause.split("\n");
          if (splitCause.length > 6) { // NOPMD
            cause = splitCause[0] + "\n" + splitCause[1] + "\n" + splitCause[2] + "\n"
                + splitCause[3] + "\n" + splitCause[4] + "\n" + splitCause[5] + "\n" + "\t ...";
          }
          this.addToErrors(landscape,
              "Exception thrown in application '" + currentApplication.getName() + "' by class '"
                  + callerClazz.getFullQualifiedName() + "':\n " + cause);
        }

        final List<DatabaseQuery> databaseQueries = currentApplication.getDatabaseQueries();

        if (event instanceof AfterJDBCOperationEventRecord && !databaseQueries.isEmpty()) {
          final AfterJDBCOperationEventRecord jdbcOperationEventRecord =
              (AfterJDBCOperationEventRecord) event;

          final DatabaseQuery databaseQuery = databaseQueries.get(databaseQueries.size() - 1);
          databaseQuery.setReturnValue(jdbcOperationEventRecord.getFormattedReturnValue());
          databaseQuery.setResponseTime(jdbcOperationEventRecord.getMethodDuration());
        }

        if (!callerClazzesHistory.isEmpty()) {
          callerClazzesHistory.pop();
        }
        if (!callerClazzesHistory.isEmpty()) {
          callerClazz = callerClazzesHistory.peek();
        }
      } else if (event instanceof BeforeSentRemoteCallRecord) {
        final BeforeSentRemoteCallRecord sentRemoteCallRecord = (BeforeSentRemoteCallRecord) event;

        remoteCallRepositoryPart.insertSentRecord(callerClazz, sentRemoteCallRecord, landscape,
            this, runtimeIndex);
      } else if (event instanceof BeforeReceivedRemoteCallRecord) {
        final BeforeReceivedRemoteCallRecord receivedRemoteCallRecord =
            (BeforeReceivedRemoteCallRecord) event;

        Clazz firstReceiverClazz = null;

        if (i + 1 < eventsLength
            && trace.getTraceEvents().get(i + 1) instanceof AbstractBeforeOperationEventRecord) {
          final AbstractBeforeOperationEventRecord abstractBeforeEventRecord =
              (AbstractBeforeOperationEventRecord) trace.getTraceEvents().get(i + 1);

          final String clazzName = getClazzName(abstractBeforeEventRecord);

          firstReceiverClazz =
              this.seekOrCreateClazz(clazzName, currentApplication, abstractBeforeEventRecord
                  .getRuntimeStatisticInformationList().get(runtimeIndex).getObjectIds());
        }

        remoteCallRepositoryPart.insertReceivedRecord(receivedRemoteCallRecord, firstReceiverClazz,
            landscape, this, runtimeIndex);
      }
      // else if (event instanceof BeforeUnknownReceivedRemoteCallRecord) {
      // }
    }

  }

  /**
   * Returns the clazz name for the passend event record.
   *
   * @param abstractBeforeEventRecord - TODOa
   * @return The clazz name for the passend event record
   */
  public static String getClazzName(
      final AbstractBeforeOperationEventRecord abstractBeforeEventRecord) {
    String clazzName = abstractBeforeEventRecord.getClazz();

    if (clazzName.contains("$")) {
      // found an anonymous class
      final String implementedInterface = abstractBeforeEventRecord.getImplementedInterface();

      if (implementedInterface != null && !implementedInterface.isEmpty()) {
        final int lastIndexOfDollar = clazzName.lastIndexOf('$');
        if (lastIndexOfDollar > -1 && lastIndexOfDollar + 1 < clazzName.length()) {
          final char suffixChar = clazzName.charAt(lastIndexOfDollar + 1);
          if ('0' <= suffixChar && suffixChar <= '9') {
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

  private void createOrUpdateCall(final Clazz caller, final Clazz callee,
      final Application application, final int requests, final double average,
      final double overallTraceDuration, final long traceId, final int orderIndex,
      final String operationName, final Landscape landscape) {

    landscape.getTimestamp().setCalls(landscape.getTimestamp().getCalls() + requests);

    // add clazzCommunication to clazz and aggregatedClazzCommunication to
    // application
    ModelHelper.addClazzCommunication(caller, callee, application, requests, average,
        overallTraceDuration, traceId, orderIndex, operationName);
  }

  private Clazz seekOrCreateClazz(final String fullQName, final Application application,
      final TIntHashSet objectIds) {
    final String[] splittedName = fullQName.split("\\.");

    Map<String, Clazz> appCached = this.clazzCache.get(application);
    if (appCached == null) {
      appCached = new HashMap<>();
      this.clazzCache.put(application, appCached);
    }
    Clazz clazz = appCached.get(fullQName);

    if (clazz == null) {
      // new clazz
      clazz = this.seekrOrCreateClazzHelper(fullQName, splittedName, application, null, 0);
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
      final Application application, final Component parent, final int index) {
    final String currentPart = splittedName[index];

    Component potentialParent = parent;

    if (index < splittedName.length - 1) {
      List<Component> list = null;

      if (potentialParent == null) {
        list = application.getComponents();
      } else {
        list = potentialParent.getChildren();
      }

      for (final Component component : list) {
        if (component.getName().equalsIgnoreCase(currentPart)) {
          return this.seekrOrCreateClazzHelper(fullQName, splittedName, application, component,
              index + 1);
        }
      }
      final Component component = new Component();
      component.initializeId();

      String fullQNameComponent = "";
      for (int i = 0; i <= index; i++) {
        fullQNameComponent += splittedName[i] + ".";
      }
      fullQNameComponent = fullQNameComponent.substring(0, fullQNameComponent.length() - 1);
      component.setFullQualifiedName(fullQNameComponent);
      component.setName(currentPart);
      component.setParentComponent(potentialParent);
      component.setBelongingApplication(application);
      list.add(component);
      return this.seekrOrCreateClazzHelper(fullQName, splittedName, application, component,
          index + 1);
    } else {
      if (potentialParent == null) {
        for (final Component component : application.getComponents()) {
          if (component.getFullQualifiedName().equals(DEFAULT_COMPONENT_NAME)) {
            potentialParent = component;
            break;
          }
        }

        if (potentialParent == null) {
          final Component component = new Component();
          component.initializeId();

          component.setFullQualifiedName(DEFAULT_COMPONENT_NAME);
          component.setName(DEFAULT_COMPONENT_NAME);
          component.setParentComponent(null);
          component.setBelongingApplication(application);
          application.getComponents().add(component);
          potentialParent = component;
        }
      }

      for (final Clazz clazz : potentialParent.getClazzes()) {
        if (clazz.getName().equalsIgnoreCase(currentPart)) {
          // familiar clazz
          return clazz;
        }
      }

      // new clazz
      final Clazz clazz = new Clazz();
      clazz.initializeId();

      clazz.setName(currentPart);
      clazz.setFullQualifiedName(fullQName);
      clazz.setParent(potentialParent);
      potentialParent.getClazzes().add(clazz);
      return clazz;
    }
  }

  public static String getMethodName(final String operationSignatureStr,
      final boolean constructor) {
    final Signature signature = SignatureParser.parse(operationSignatureStr, constructor);
    return signature.getOperationName();
  }

}