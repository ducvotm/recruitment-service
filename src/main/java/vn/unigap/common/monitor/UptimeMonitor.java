package vn.unigap.common.monitor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UptimeMonitor {
    private final AtomicInteger downtimeCount = new AtomicInteger(0);

    @Scheduled(fixedRate = 60000)  // Run every 1 minute
    public void checkHealth() {
        if (isServiceDown()) {
            downtimeCount.incrementAndGet();
            System.out.println("Service down at: " + LocalDateTime.now());
        }
    }

    private boolean isServiceDown() {
        return false; // Change this logic to check real health
    }

    public double getUptimePercentage(int totalMinutes) {
        return 100 - ((downtimeCount.get() / (double) totalMinutes) * 100);
    }
}
