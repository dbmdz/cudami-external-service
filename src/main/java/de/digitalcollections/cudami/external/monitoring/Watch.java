package de.digitalcollections.cudami.external.monitoring;

import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

public class Watch {

  private final Timer timer;
  private long startNanoSeconds;

  public Watch(Timer timer) {
    this.timer = timer;
    this.startNanoSeconds = System.nanoTime();
  }

  public void stop() {
    timer.record(System.nanoTime() - startNanoSeconds, TimeUnit.NANOSECONDS);
  }

  public void reset() {
    this.startNanoSeconds = System.nanoTime();
  }

}
