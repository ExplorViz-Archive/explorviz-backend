package net.explorviz.landscape.model.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.explorviz.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.landscape.model.application.Application;
import net.explorviz.landscape.model.application.ApplicationCommunication;
import net.explorviz.landscape.model.application.Clazz;
import net.explorviz.landscape.model.application.ClazzCommunication;
import net.explorviz.landscape.model.application.Component;
import net.explorviz.landscape.model.application.DatabaseQuery;
import net.explorviz.landscape.model.application.Trace;
import net.explorviz.landscape.model.application.TraceStep;
import net.explorviz.landscape.model.event.Event;
import net.explorviz.landscape.model.landscape.Landscape;
import net.explorviz.landscape.model.landscape.Node;
import net.explorviz.landscape.model.landscape.NodeGroup;
import net.explorviz.landscape.model.landscape.System;
import net.explorviz.landscape.model.store.Timestamp;

public class TypeProvider {

  public static Map<String, Class<?>> getExplorVizCoreTypesAsMap() {

    final Map<String, Class<?>> typeMap = new HashMap<>();

    typeMap.putIfAbsent("Timestamp", Timestamp.class);
    typeMap.putIfAbsent("Event", Event.class);
    // typeMap.putIfAbsent("EEventType", EEventType.class);
    typeMap.putIfAbsent("Landscape", Landscape.class);
    typeMap.putIfAbsent("System", System.class);
    typeMap.putIfAbsent("NodeGroup", NodeGroup.class);
    typeMap.putIfAbsent("Node", Node.class);
    typeMap.putIfAbsent("Application", Application.class);
    typeMap.putIfAbsent("ApplicationCommunication", ApplicationCommunication.class);
    typeMap.putIfAbsent("Component", Component.class);
    typeMap.putIfAbsent("Clazz", Clazz.class);
    typeMap.putIfAbsent("ClazzCommunication", ClazzCommunication.class);
    typeMap.putIfAbsent("Trace", Trace.class);
    typeMap.putIfAbsent("TraceStep", TraceStep.class);
    typeMap.putIfAbsent("AggregatedClazzCommunication", AggregatedClazzCommunication.class);
    typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);

    return typeMap;
  }

  public static List<Class<?>> getExplorVizCoreTypesAsList() {
    return new ArrayList<>(TypeProvider.getExplorVizCoreTypesAsMap().values());
  }

  public static Class<?>[] getExplorVizCoreTypesAsArray() {
    final List<Class<?>> typeList = TypeProvider.getExplorVizCoreTypesAsList();
    return typeList.toArray(new Class<?>[typeList.size()]);
  }

}
