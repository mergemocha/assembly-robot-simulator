package com.assemblyrobot.simulator.system.utils;

import com.assemblyrobot.simulator.core.clock.Clock;
import com.assemblyrobot.simulator.core.events.Event;
import com.assemblyrobot.simulator.core.events.EventQueue;
import com.assemblyrobot.simulator.core.events.EventType;
import com.assemblyrobot.simulator.core.generators.ArrivalEventIntervalGenerator;
import com.google.common.collect.Iterables;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Propagates {@link EventType#ARRIVAL} events to the {@link
 * com.assemblyrobot.simulator.core.Engine} event queue.
 */
@RequiredArgsConstructor
public class ArrivalEventPropagator {
  @NonNull private final EventQueue eventQueue;
  private Event nextEvent = new Event(0, EventType.ARRIVAL);
  private static final Logger logger = LogManager.getLogger();

  /** Schedules next {@link Event} for the {@link EventQueue}. */
  public void feedNext() {
    logger.debug("Scheduling next arrival event {}.", nextEvent);
    eventQueue.schedule(nextEvent);

    // Tee up the next arrival event to schedule
    val currentTick = Clock.getInstance().getCurrentTick();
    val fullEventQueue = eventQueue.dump();
    val lastEvent = Iterables.getLast(fullEventQueue);
    val nextArrivalTime =
        currentTick
            + lastEvent.getExecutionTime()
            + ArrivalEventIntervalGenerator.getInstance().nextLong();
    nextEvent = new Event(nextArrivalTime, EventType.ARRIVAL);

    logger.debug("Scheduling complete. Teed next arrival event {}.", nextEvent);
  }
}
