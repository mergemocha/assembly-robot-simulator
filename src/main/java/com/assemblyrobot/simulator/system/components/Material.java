package com.assemblyrobot.simulator.system.components;

import com.assemblyrobot.shared.constants.ErrorType;
import com.assemblyrobot.shared.constants.StageID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Represents a product traveling through the production line in a simulated environment */
@ToString
public class Material implements Comparable<Material> {

  private static int nextFreeId = 1;

  @Getter private final long id;
  @Getter @Setter private long queueStartTime = 0;
  @Getter @Setter private long queueEndTime = 0;
  @Getter @Setter private long processingStartTime = 0;
  @Getter @Setter private long processingEndTime = 0;
  @Getter @Setter private StageID currentStage;
  @Getter @Setter private ErrorType error;

  public Material() {
    id = nextFreeId;
    nextFreeId++;
  }

  public static void resetId() {
    nextFreeId = 1;
  }

  public void reset() {
    queueStartTime = 0;
    queueEndTime = 0;
    processingStartTime = 0;
    processingEndTime = 0;
  }

  public void setNextStage(StageID currentStage) {
    this.currentStage = currentStage;
  }

  /**
   * Calculates the amount of time the {@link Material} spent in the system.
   *
   * @return The difference of {@link Material#processingEndTime} and {@link
   *     Material#processingStartTime}
   */
  public long getTotalPassthroughTime() {
    return processingEndTime - queueStartTime;
  }

  public int compareTo(Material material) {
    return Long.compare(this.getQueueStartTime(), material.getQueueStartTime());
  }
}
