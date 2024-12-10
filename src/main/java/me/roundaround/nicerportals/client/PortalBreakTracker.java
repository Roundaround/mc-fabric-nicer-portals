package me.roundaround.nicerportals.client;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PortalBreakTracker {
  private static final PortalBreakTracker INSTANCE = new PortalBreakTracker();

  private final Set<Long> portalBreakTicks = new HashSet<>();

  private PortalBreakTracker() {
  }

  public static PortalBreakTracker getInstance() {
    return INSTANCE;
  }

  public void add(long tick) {
    this.portalBreakTicks.add(tick);
  }

  public void cleanup(long tick) {
    this.portalBreakTicks.stream()
        .filter((portalBreakTick) -> tick >= portalBreakTick + 2)
        .collect(Collectors.toSet())
        .forEach(this.portalBreakTicks::remove);
  }

  public boolean isAlreadyTracked(long tick) {
    return this.portalBreakTicks.contains(tick) || this.portalBreakTicks.contains(tick - 1);
  }
}
