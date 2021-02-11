package com.assemblyrobot.simulator.core.metrics;

import java.util.HashMap;
import lombok.Getter;
import lombok.NonNull;

/**
 * Generic metrics collector. Can be attached to any class to store metrics about various facets of
 * said class.
 */
public class MetricsCollector {
  @Getter private final HashMap<String, Double> metrics = new HashMap<>();
  @Getter @NonNull private final MetricsCollectorType type;

  public MetricsCollector(@NonNull String collectingClassName, @NonNull String typeClassName) {
    this.type = MetricsCollectorType.getByClass(typeClassName);
    CentralMetricsCollector.getInstance().registerMetricsCollector(collectingClassName, this);
  }

  /**
   * Gets a metric by name.
   *
   * @param metricName Metric name to get.
   * @return {@link Double} (Null if no metric by this name exists)
   */
  public double getMetric(@NonNull String metricName) {
    return metrics.get(metricName);
  }

  /**
   * Gets a metric by name, specifying a default value to use when a metric by this name does not
   * exist.
   *
   * @param metricName Metric name to get.
   * @param defaultValue Default value to use in case no metric by this name exists.
   * @return {@link Double}
   */
  public double getMetricWithDefault(@NonNull String metricName, double defaultValue) {
    return metrics.getOrDefault(metricName, defaultValue);
  }

  /**
   * Sets or updates a metric.
   *
   * @param metricName Metric name to set/update.
   * @param measurement Metric value to set/update.
   */
  public void putMetric(@NonNull String metricName, double measurement) {
    metrics.put(metricName, measurement);
  }
}
