package com.assemblyrobot.simulator.system.metricscollectors;

import com.assemblyrobot.shared.constants.StageID;
import com.assemblyrobot.simulator.core.metrics.MetricsCollector;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
* Collects Material and Station specific data
*/
public class MaterialMetricsCollector {
  @Getter private final StageID stageId;
  @Getter private final String stationId;
  @Getter @Setter private long queueStartTime;
  @Getter @Setter private long queueEndTime;
  @Getter @Setter private long processingStartTime;
  @Getter @Setter private long processingEndTime;

  private final MetricsCollector metricsCollector;

  @RequiredArgsConstructor
  private enum Metrics {
    QUEUE_START_TIME("material_queue_start_time"),
    QUEUE_END_TIME("material_queue_end_time"),
    QUEUE_DURATION("material_queue_duration"),
    PROCESSING_START_TIME("material_processing_start_time"),
    PROCESSING_END_TIME("material_processing_end_time"),
    PROCESSING_DURATION("material_processing_duration"),
    PASSTHROUGH_TIME("material_passthrough_time");

    @Getter private final String metricName;
  }

  public MaterialMetricsCollector(StageID stageId, String stationId, long materialId) {
    this.stageId = stageId;
    this.stationId = stationId;
    metricsCollector =
        new MetricsCollector(
            "Material-%d [%s]".formatted(materialId, stationId),
            getClass().getName());
  }

  /**
   * Gets the duration of how long a material has queued.
   *
   * @return {@link Long}
   */
  public long getQueueDuration() {
    return queueEndTime - queueStartTime;
  }

  /**
   * Gets the duration of how long it has taken to process a material.
   *
   * @return {@link Long}
   */
  public long getProcessingDuration() {
    return processingEndTime - processingStartTime;
  }

  /**
   * Gets the total duration of how long it took a material to pass through a station (queueing + processing time).
   *
   * @return {@link Long}
   */
  public long getTotalPassthroughTime() {
    return processingEndTime - queueStartTime;
  }

  /**
   * Updates all metrics.
   */
  public void updateMetrics() {
    Arrays.stream(Metrics.values())
        .forEach(
            metric -> {
              switch (metric) {
                case QUEUE_START_TIME -> putMetric(Metrics.QUEUE_START_TIME, queueStartTime);
                case QUEUE_END_TIME -> putMetric(Metrics.QUEUE_END_TIME, queueEndTime);
                case QUEUE_DURATION -> putMetric(Metrics.QUEUE_DURATION, getQueueDuration());
                case PROCESSING_START_TIME -> putMetric(Metrics.PROCESSING_START_TIME, processingStartTime);
                case PROCESSING_END_TIME -> putMetric(Metrics.PROCESSING_END_TIME, processingEndTime);
                case PROCESSING_DURATION -> putMetric(Metrics.PROCESSING_DURATION, getProcessingDuration());
                case PASSTHROUGH_TIME -> putMetric(Metrics.PASSTHROUGH_TIME, getTotalPassthroughTime());
              }
            });
  }

  /**
   * Adds a metric.
   *
   * @param metric Name of the metric.
   * @param measurement Value of the metric.
   */
  private void putMetric(Metrics metric, double measurement) {
    metricsCollector.putMetric(metric.getMetricName(), measurement);
  }
}
