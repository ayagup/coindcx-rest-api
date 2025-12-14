package com.coindcx.springclient.dto;

/**
 * DTO for API call statistics
 */
public class ApiCallStatistics {

    private String serviceName;
    private Long totalCalls;
    private Long successfulCalls;
    private Long failedCalls;
    private Double averageExecutionTimeMs;
    private Long minExecutionTimeMs;
    private Long maxExecutionTimeMs;

    public ApiCallStatistics() {
    }

    public ApiCallStatistics(String serviceName, Long totalCalls, Long successfulCalls, 
                            Long failedCalls, Double averageExecutionTimeMs) {
        this.serviceName = serviceName;
        this.totalCalls = totalCalls;
        this.successfulCalls = successfulCalls;
        this.failedCalls = failedCalls;
        this.averageExecutionTimeMs = averageExecutionTimeMs;
    }

    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(Long totalCalls) {
        this.totalCalls = totalCalls;
    }

    public Long getSuccessfulCalls() {
        return successfulCalls;
    }

    public void setSuccessfulCalls(Long successfulCalls) {
        this.successfulCalls = successfulCalls;
    }

    public Long getFailedCalls() {
        return failedCalls;
    }

    public void setFailedCalls(Long failedCalls) {
        this.failedCalls = failedCalls;
    }

    public Double getAverageExecutionTimeMs() {
        return averageExecutionTimeMs;
    }

    public void setAverageExecutionTimeMs(Double averageExecutionTimeMs) {
        this.averageExecutionTimeMs = averageExecutionTimeMs;
    }

    public Long getMinExecutionTimeMs() {
        return minExecutionTimeMs;
    }

    public void setMinExecutionTimeMs(Long minExecutionTimeMs) {
        this.minExecutionTimeMs = minExecutionTimeMs;
    }

    public Long getMaxExecutionTimeMs() {
        return maxExecutionTimeMs;
    }

    public void setMaxExecutionTimeMs(Long maxExecutionTimeMs) {
        this.maxExecutionTimeMs = maxExecutionTimeMs;
    }

    @Override
    public String toString() {
        return "ApiCallStatistics{" +
                "serviceName='" + serviceName + '\'' +
                ", totalCalls=" + totalCalls +
                ", successfulCalls=" + successfulCalls +
                ", failedCalls=" + failedCalls +
                ", averageExecutionTimeMs=" + averageExecutionTimeMs +
                '}';
    }
}
