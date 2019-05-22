package net.explorviz.common.live_trace_processing.adaptive_monitoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AdaptiveMonitoringPatternList {
	private static final ConcurrentHashMap<String, Set<String>> applicationToPatternMap = new ConcurrentHashMap<String, Set<String>>();
	private static final List<IAdaptiveMonitoringPatternListChangeListener> observerList = new ArrayList<IAdaptiveMonitoringPatternListChangeListener>();

	public static ConcurrentHashMap<String, Set<String>> getApplicationToPatternMap() {
		return applicationToPatternMap;
	}

	public static void registerObserver(
			final IAdaptiveMonitoringPatternListChangeListener listener) {
		observerList.add(listener);
	}

	public static final void addPattern(final String application,
			final String pattern) {
		System.out.println("Added pattern " + pattern + " and application "
				+ application);

		Set<String> patterns = applicationToPatternMap.get(application);
		if (patterns == null) {
			patterns = new HashSet<String>();
		}
		patterns.add(pattern);
		applicationToPatternMap.put(application, patterns);

		for (final IAdaptiveMonitoringPatternListChangeListener observer : observerList) {
			observer.patternListChanged();
		}
	}

	public static final void removePattern(final String application,
			final String pattern) {
		Set<String> patterns = applicationToPatternMap.get(application);
		if (patterns == null) {
			patterns = new HashSet<String>();
		}
		patterns.remove(pattern);
		applicationToPatternMap.put(application, patterns);

		for (final IAdaptiveMonitoringPatternListChangeListener observer : observerList) {
			observer.patternListChanged();
		}
	}
}
