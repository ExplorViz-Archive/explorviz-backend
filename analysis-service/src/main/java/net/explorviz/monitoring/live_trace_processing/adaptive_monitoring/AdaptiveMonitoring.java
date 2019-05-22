package net.explorviz.monitoring.live_trace_processing.adaptive_monitoring;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.explorviz.common.live_trace_processing.adaptive_monitoring.AdaptiveMonitoringPatternList;
import net.explorviz.common.live_trace_processing.adaptive_monitoring.IAdaptiveMonitoringPatternListChangeListener;
import net.explorviz.monitoring.live_trace_processing.writer.TCPWriter;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

public class AdaptiveMonitoring implements IAdaptiveMonitoringPatternListChangeListener {
  private static final ConcurrentHashMap<Signature, Boolean> signatureActivationMap =
      new ConcurrentHashMap<>();

  public AdaptiveMonitoring() {
    AdaptiveMonitoringPatternList.registerObserver(this);
  }

  public final boolean isMethodEnabled(final Signature signature) {
    final Boolean isActive = signatureActivationMap.get(signature);
    if (isActive != null) {
      return isActive;
    } else {
      final boolean determinedActive = this.matchSignatureToPatterns(signature);
      signatureActivationMap.put(signature, determinedActive);
      return determinedActive;
    }
  }

  private final boolean matchSignatureToPatterns(final Signature signature) {
    final String applicationName = TCPWriter.getApplicationName();
    if (applicationName == null) {
      return true;
    }

    final Set<String> patterns =
        AdaptiveMonitoringPatternList.getApplicationToPatternMap().get(applicationName); // TODO
                                                                                         // concurrent...

    if (patterns == null || patterns.isEmpty()) {
      return true;
    }

    if (signature instanceof MethodSignature) {
      for (final String pattern : patterns) {
        if (this.matchMethodSignatureToPattern((MethodSignature) signature, pattern)) {
          return true;
        }
      }
      return false;
    } else if (signature instanceof ConstructorSignature) {
      for (final String pattern : patterns) {
        if (this.matchConstructorSignatureToPattern((ConstructorSignature) signature, pattern)) {
          return true;
        }
      }
      return false;
    }

    return true;
  }

  private boolean matchMethodSignatureToPattern(final MethodSignature methodSignature,
      final String pattern) {
    // TODO Auto-generated method stub
    return false;
  }

  private boolean matchConstructorSignatureToPattern(
      final ConstructorSignature constructorSignature, final String pattern) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void patternListChanged() {
    signatureActivationMap.clear();
  }
}
