package com.assemblyrobot.ui.controllers;

import com.assemblyrobot.shared.config.Config;
import com.assemblyrobot.shared.config.model.ApplicationConfig;
import com.assemblyrobot.shared.db.dao.RunDAO;
import com.assemblyrobot.shared.db.model.EngineDTO;
import com.assemblyrobot.shared.db.model.MaterialDTO;
import com.assemblyrobot.shared.db.model.RunDTO;
import com.assemblyrobot.shared.db.model.StageControllerDTO;
import com.assemblyrobot.shared.db.model.StationDTO;
import com.assemblyrobot.simulator.core.metrics.CentralMetricsCollector;
import com.assemblyrobot.simulator.system.SimulatorEngine;
import com.assemblyrobot.simulator.system.components.Material;
import com.assemblyrobot.simulator.system.components.StageController;
import com.assemblyrobot.simulator.system.components.StageController.Metrics;
import com.assemblyrobot.simulator.system.components.Station;
import com.assemblyrobot.simulator.system.metricscollectors.EngineMetricsCollector;
import com.assemblyrobot.simulator.system.metricscollectors.MaterialMetricsCollector;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class OverviewController {

  @Getter private static final SimulatorEngine engine = new SimulatorEngine();
  private final ApplicationConfig config = Config.getConfig();
  private final RunDAO dao = RunDAO.getInstance();
  private final CentralMetricsCollector metricsCollector = CentralMetricsCollector.getInstance();
  private static final Logger logger = LogManager.getLogger();

  public void startEngine() {
    Configurator.setRootLevel(Level.TRACE);
    engine.start();
  }

  public void stopEngine() {
    engine.endRun();
  }

  public void setPause(boolean isPause) {
    engine.setPause(isPause);
  }

  public void setCanProceed(boolean canProceed) {
    engine.setCanProceed(canProceed);
  }

  public void takeStep() {
    engine.setCanProceed(true);
    logger.trace("Taking a step forward.");
  }

  public void setSpeed(double value) {
    engine.setSpeedMultiplier(value);
  }

  public void logRun() {
    val run =
        new RunDTO(
            config.getArrivalIntervalParams(),
            config.getAssemblyTimeParams(),
            config.getErrorCheckTimeParams(),
            config.getErrorOccurrenceParams(),
            config.getErrorFixTimes(),
            config.getStationParams());
    AtomicReference<EngineDTO> engine = new AtomicReference<>();
    AtomicReference<StageControllerDTO> stageController = new AtomicReference<>();
    ArrayList<StationDTO> stationsList = new ArrayList<StationDTO>();
    ArrayList<MaterialDTO> materialsList = new ArrayList<MaterialDTO>();

    metricsCollector
        .getCollectors()
        .values()
        .forEach(
            collector -> {
              switch (collector.getType()) {
                case ENGINE -> engine
                    .set(new EngineDTO(collector.getMetric(EngineMetricsCollector.getTOTAL_SIMULATION_TIME_METRIC_NAME())));

                case STAGE -> {}

                case STAGE_CONTROLLER -> stageController.set(
                    new StageControllerDTO(collector.getMetric(StageController.Metrics.TOTAL_MATERIAL_AMOUNT.getMetricName()),
                        collector.getMetric(StageController.Metrics.TOTAL_ASSEMBLED_AMOUNT.getMetricName())));

                case STATION -> stationsList.add(new StationDTO(collector.getHostName(),
                        collector.getMetric(Station.Metrics.STATION_MATERIAL_AMOUNT.getMetricName()),
                        collector.getMetric(Station.Metrics.STATION_PROCESSED_AMOUNT.getMetricName()),
                        collector.getMetric(Station.Metrics.STATION_BUSY_TIME.getMetricName()),
                        collector.getMetric(Station.Metrics.STATION_TOTAL_PASSTHROUGH_TIME.getMetricName())));

                case MATERIAL -> materialsList.add(new MaterialDTO(collector.getHostName(),
                        collector.getMetric(MaterialMetricsCollector.Metrics.QUEUE_START_TIME.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.QUEUE_END_TIME.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.QUEUE_DURATION.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.PROCESSING_START_TIME.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.PROCESSING_END_TIME.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.PROCESSING_DURATION.getMetricName()),
                        collector.getMetric(MaterialMetricsCollector.Metrics.PASSTHROUGH_TIME.getMetricName())
                    ));
              }
            });

    StationDTO[] stations = (StationDTO[]) stationsList.toArray();
    MaterialDTO[] materials = (MaterialDTO[]) materialsList.toArray();

    dao.logRun(run, engine.get(), stageController.get(), stations, materials);

  }

  public void resetMetricsCollectors(){
    metricsCollector.dump();
  }
}
