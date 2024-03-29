package com.assemblyrobot.simulator.system.stages;

import com.assemblyrobot.shared.config.Config;
import com.assemblyrobot.shared.config.model.StationConfig;
import com.assemblyrobot.shared.constants.ErrorType;
import com.assemblyrobot.shared.constants.StageID;
import com.assemblyrobot.simulator.system.components.Material;
import com.assemblyrobot.simulator.system.components.Stage;
import com.assemblyrobot.simulator.system.components.StageController;
import com.assemblyrobot.simulator.system.components.StationQueue;
import com.assemblyrobot.simulator.system.stations.FixStation;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** {@link Stage} implementation that represents the error fixing stage in the simulator. */
public class FixStage extends Stage {

  @Getter private final StageController stageController;
  @Getter private static final HashMap<ErrorType, StationQueue> substations = new HashMap<>();
  private final StationConfig config = Config.getConfig().getStationParams();
  private static final Logger logger = LogManager.getLogger();

  public FixStage(@NonNull StageController stageController) {
    this.stageController = stageController;
    createStations();
  }

  /**
   * Gets the amount of stations to be created from the configuration. Used later in {@link
   * FixStage#createStations()}.
   *
   * @param type Type of error station
   * @return {@link Integer}
   */
  private int getStationAmountForErrorType(@NonNull ErrorType type) {
    return switch (type) {
      case FITTING -> config.getFittingFixStationAmount();
      case BOLTING -> config.getBoltingFixStationAmount();
      case RIVETING -> config.getRivetingFixStationAmount();
      case WELDING -> config.getWeldingFixStationAmount();
      case RETURNING -> config.getReturningFixStationAmount();
    };
  }

  @Override
  protected void createStations() {
    // Create the defined amount of fix stations for each error type
    Arrays.stream(ErrorType.values())
        .forEach(
            errorType -> {
              val stationQueue = new StationQueue();

              for (int i = 0; i < getStationAmountForErrorType(errorType); i++) {
                stationQueue.add(new FixStation(this, errorType));
              }

              substations.put(errorType, stationQueue);
            });
  }

  @Override
  public void addToStationQueue(@NonNull Material material, ErrorType errorType) {
    logger.trace("Material {}: Has error in {}.", material.getId(), errorType);

    val nextFreeStation = substations.get(errorType).peek();
    if (nextFreeStation != null) {
      nextFreeStation.addToStationQueue(material, StageID.FIX);
    }
  }
}
