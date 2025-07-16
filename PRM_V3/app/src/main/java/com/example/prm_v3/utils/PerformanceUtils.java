package com.example.prm_v3.utils;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for comparing performance between direct and legacy API endpoints
 */
public class PerformanceUtils {
    private static final String TAG = "PerformanceUtils";

    // Performance metrics storage
    private static final Map<String, EndpointMetrics> directEndpointMetrics = new HashMap<>();
    private static final Map<String, EndpointMetrics> legacyEndpointMetrics = new HashMap<>();

    public static class EndpointMetrics {
        public AtomicLong totalRequests = new AtomicLong(0);
        public AtomicLong totalResponseTime = new AtomicLong(0);
        public AtomicLong totalRecords = new AtomicLong(0);
        public AtomicLong errorCount = new AtomicLong(0);
        public long lastRequestTime = 0;

        public double getAverageResponseTime() {
            long requests = totalRequests.get();
            return requests > 0 ? (double) totalResponseTime.get() / requests : 0.0;
        }

        public double getAverageRecordsPerRequest() {
            long requests = totalRequests.get();
            return requests > 0 ? (double) totalRecords.get() / requests : 0.0;
        }

        public double getErrorRate() {
            long requests = totalRequests.get();
            return requests > 0 ? (double) errorCount.get() / requests * 100 : 0.0;
        }

        public double getThroughput() {
            double avgResponseTime = getAverageResponseTime();
            return avgResponseTime > 0 ? 1000.0 / avgResponseTime : 0.0; // requests per second
        }
    }

    /**
     * Record performance metrics for direct endpoint
     */
    public static void recordDirectEndpointMetrics(String status, long responseTime, int recordCount, boolean isError) {
        String key = "direct_" + (status != null ? status : "all");
        EndpointMetrics metrics = directEndpointMetrics.computeIfAbsent(key, k -> new EndpointMetrics());

        metrics.totalRequests.incrementAndGet();
        metrics.totalResponseTime.addAndGet(responseTime);
        metrics.totalRecords.addAndGet(recordCount);
        metrics.lastRequestTime = System.currentTimeMillis();

        if (isError) {
            metrics.errorCount.incrementAndGet();
        }

        Log.d(TAG, String.format("Direct %s: %dms, %d records, errors: %d/%d",
                status, responseTime, recordCount,
                metrics.errorCount.get(), metrics.totalRequests.get()));
    }

    /**
     * Record performance metrics for legacy endpoint
     */
    public static void recordLegacyEndpointMetrics(String status, long responseTime, int recordCount, boolean isError) {
        String key = "legacy_" + (status != null ? status : "all");
        EndpointMetrics metrics = legacyEndpointMetrics.computeIfAbsent(key, k -> new EndpointMetrics());

        metrics.totalRequests.incrementAndGet();
        metrics.totalResponseTime.addAndGet(responseTime);
        metrics.totalRecords.addAndGet(recordCount);
        metrics.lastRequestTime = System.currentTimeMillis();

        if (isError) {
            metrics.errorCount.incrementAndGet();
        }

        Log.d(TAG, String.format("Legacy %s: %dms, %d records, errors: %d/%d",
                status, responseTime, recordCount,
                metrics.errorCount.get(), metrics.totalRequests.get()));
    }

    /**
     * Compare performance between direct and legacy endpoints for a specific status
     */
    public static void logPerformanceComparison(String status) {
        String directKey = "direct_" + (status != null ? status : "all");
        String legacyKey = "legacy_" + (status != null ? status : "all");

        EndpointMetrics directMetrics = directEndpointMetrics.get(directKey);
        EndpointMetrics legacyMetrics = legacyEndpointMetrics.get(legacyKey);

        Log.i(TAG, "=== Performance Comparison for " + status + " ===");

        if (directMetrics != null) {
            Log.i(TAG, String.format("DIRECT - Avg Response: %.2fms, Throughput: %.2f req/s, Error Rate: %.2f%%",
                    directMetrics.getAverageResponseTime(),
                    directMetrics.getThroughput(),
                    directMetrics.getErrorRate()));
        }

        if (legacyMetrics != null) {
            Log.i(TAG, String.format("LEGACY - Avg Response: %.2fms, Throughput: %.2f req/s, Error Rate: %.2f%%",
                    legacyMetrics.getAverageResponseTime(),
                    legacyMetrics.getThroughput(),
                    legacyMetrics.getErrorRate()));
        }

        if (directMetrics != null && legacyMetrics != null) {
            double improvement = ((legacyMetrics.getAverageResponseTime() - directMetrics.getAverageResponseTime())
                    / legacyMetrics.getAverageResponseTime()) * 100;
            Log.i(TAG, String.format("IMPROVEMENT: %.2f%% faster response time with direct endpoints", improvement));
        }
    }

    /**
     * Get detailed performance report
     */
    public static String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== API Performance Report ===\n");

        // Direct endpoints report
        report.append("\nDIRECT ENDPOINTS:\n");
        for (Map.Entry<String, EndpointMetrics> entry : directEndpointMetrics.entrySet()) {
            EndpointMetrics metrics = entry.getValue();
            report.append(String.format("%s: %.2fms avg, %.2f req/s, %.2f%% errors (%d requests)\n",
                    entry.getKey(),
                    metrics.getAverageResponseTime(),
                    metrics.getThroughput(),
                    metrics.getErrorRate(),
                    metrics.totalRequests.get()));
        }

        // Legacy endpoints report
        report.append("\nLEGACY ENDPOINTS:\n");
        for (Map.Entry<String, EndpointMetrics> entry : legacyEndpointMetrics.entrySet()) {
            EndpointMetrics metrics = entry.getValue();
            report.append(String.format("%s: %.2fms avg, %.2f req/s, %.2f%% errors (%d requests)\n",
                    entry.getKey(),
                    metrics.getAverageResponseTime(),
                    metrics.getThroughput(),
                    metrics.getErrorRate(),
                    metrics.totalRequests.get()));
        }

        return report.toString();
    }

    /**
     * Reset all performance metrics
     */
    public static void resetMetrics() {
        directEndpointMetrics.clear();
        legacyEndpointMetrics.clear();
        Log.i(TAG, "Performance metrics reset");
    }

    /**
     * Check if direct endpoint should be preferred based on performance history
     */
    public static boolean shouldUseDirectEndpoint(String status) {
        String directKey = "direct_" + (status != null ? status : "all");
        String legacyKey = "legacy_" + (status != null ? status : "all");

        EndpointMetrics directMetrics = directEndpointMetrics.get(directKey);
        EndpointMetrics legacyMetrics = legacyEndpointMetrics.get(legacyKey);

        // If no data available, prefer direct endpoint by default
        if (directMetrics == null && legacyMetrics == null) {
            return true;
        }

        // If only one type has data, use that
        if (directMetrics == null) return false;
        if (legacyMetrics == null) return true;

        // Compare performance - consider both response time and error rate
        double directScore = calculateEndpointScore(directMetrics);
        double legacyScore = calculateEndpointScore(legacyMetrics);

        return directScore > legacyScore;
    }

    /**
     * Calculate endpoint performance score (higher is better)
     */
    private static double calculateEndpointScore(EndpointMetrics metrics) {
        if (metrics.totalRequests.get() == 0) return 0.0;

        double avgResponseTime = metrics.getAverageResponseTime();
        double errorRate = metrics.getErrorRate();

        // Score = 1000 / avg_response_time * (1 - error_rate/100)
        // Higher score means better performance
        double timeScore = avgResponseTime > 0 ? 1000.0 / avgResponseTime : 0.0;
        double reliabilityMultiplier = 1.0 - (errorRate / 100.0);

        return timeScore * reliabilityMultiplier;
    }

    /**
     * Get recommended endpoint strategy for each status
     */
    public static Map<String, String> getRecommendedStrategy() {
        Map<String, String> recommendations = new HashMap<>();
        String[] statuses = {"all", "pending", "confirmed", "preparing", "delivered", "completed", "cancelled"};

        for (String status : statuses) {
            boolean useDirect = shouldUseDirectEndpoint(status);
            recommendations.put(status, useDirect ? "direct" : "legacy");
        }

        return recommendations;
    }

    /**
     * Performance monitoring constants
     */
    public static class Thresholds {
        public static final long SLOW_RESPONSE_THRESHOLD = 2000; // 2 seconds
        public static final double HIGH_ERROR_RATE_THRESHOLD = 5.0; // 5%
        public static final long MIN_REQUESTS_FOR_COMPARISON = 5;
    }

    /**
     * Check if endpoint performance is concerning
     */
    public static boolean isPerformanceConcerning(String endpointType, String status) {
        String key = endpointType + "_" + (status != null ? status : "all");
        Map<String, EndpointMetrics> metricsMap = endpointType.equals("direct") ?
                directEndpointMetrics : legacyEndpointMetrics;

        EndpointMetrics metrics = metricsMap.get(key);
        if (metrics == null || metrics.totalRequests.get() < Thresholds.MIN_REQUESTS_FOR_COMPARISON) {
            return false;
        }

        return metrics.getAverageResponseTime() > Thresholds.SLOW_RESPONSE_THRESHOLD ||
                metrics.getErrorRate() > Thresholds.HIGH_ERROR_RATE_THRESHOLD;
    }
}