package com.dad.common.util;

/**
 * Snowflake ID Generator
 * Distributed unique ID generator based on Twitter's Snowflake algorithm
 */
public class SnowflakeIdGenerator {

    // Start timestamp (2024-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1704067200000L;

    // Number of bits occupied by each part
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // Maximum value of each part
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // Shift bits for each part
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Constructor
     *
     * @param datacenterId Data center ID (0-31)
     * @param workerId     Worker ID (0-31)
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                String.format("Datacenter ID must be between 0 and %d", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * Generate next ID
     *
     * @return Unique ID
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        // Clock callback check
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards. Refusing to generate ID for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        // Same millisecond, sequence auto-increment
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // Sequence overflow, wait for next millisecond
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // Different milliseconds, sequence reset
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Generate ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
            | (datacenterId << DATACENTER_ID_SHIFT)
            | (workerId << WORKER_ID_SHIFT)
            | sequence;
    }

    /**
     * Get current timestamp
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Wait for next millisecond
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    // ==================== Static instance ====================

    private static final SnowflakeIdGenerator DEFAULT_INSTANCE =
        new SnowflakeIdGenerator(1, 1);

    /**
     * Generate ID using default instance
     */
    public static long generateId() {
        return DEFAULT_INSTANCE.nextId();
    }

    /**
     * Generate ID as String
     */
    public static String generateIdStr() {
        return String.valueOf(generateId());
    }
}
