package de.digitalcollections.cudami.external.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessingMetrics {

  public enum MetadataOperation {
    GET_FULL_DIGITALOBJECT,
    GET_DIGITALOBJECT,
    GET_ITEM,
    GET_MANIFESTATION,
    GET_WORK
  }

  private final Map<MetadataOperation, Timer> processingDurations;
  private final Counter processedImages;

  public ProcessingMetrics(MeterRegistry meterRegistry) {
    processedImages = Counter.builder("metadataservice").register(meterRegistry);
    processingDurations = new HashMap<>();
    for (MetadataOperation op : MetadataOperation.values()) {
      Timer timer =
          Timer.builder("metadataservice.duration")
              .description("Duration of metadata service retrieval operations")
              .tag("operation", op.name().toLowerCase())
              .register(meterRegistry);
      processingDurations.put(op, timer);
    }
  }

  public Watch startMeasure(MetadataOperation imageOperation) {
    return new Watch(processingDurations.get(imageOperation));
  }

}
