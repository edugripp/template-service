package com.example.template.task;

import com.example.template.filter.AggregatedLoggingFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
@ConditionalOnProperty(name = "metrics.aggregated.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class MetricsLoggerTask {

    private final AggregatedLoggingFilter loggingFilter;

    @Scheduled(fixedRateString = "${metrics.log.interval:5000}")
    public void printAggregatedMetrics() {
        ConcurrentMap<String, AggregatedLoggingFilter.EndpointStats> snapshot = loggingFilter.extractAndReset();

        if (snapshot.isEmpty()) {
            return;
        }

        StringBuilder logBuilder = new StringBuilder("\n--- [Aggregated Throughput (Past 5s)] ---\n");
        long totalSuccess = 0;
        long totalErrors = 0;

        for (var entry : snapshot.entrySet()) {
            long success = entry.getValue().success().sum();
            long error = entry.getValue().errors().sum();
            totalSuccess += success;
            totalErrors += error;

            logBuilder.append(
                    String.format("URI: %-25s | Success: %-6d | Errors: %-6d%n", entry.getKey(), success, error));
        }

        logBuilder.append(String.format("------------------------------------------%n"));
        logBuilder.append(String.format("TOTAL                        | Success: %-6d | Errors: %-6d%n", totalSuccess,
                totalErrors));
        logBuilder.append("------------------------------------------");

        if (totalSuccess > 0 || totalErrors > 0) {
            log.info(logBuilder.toString());
        }
    }
}
