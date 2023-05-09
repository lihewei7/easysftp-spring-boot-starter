package io.github.lihewei7.easysftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.StringJoiner;

/**
 * @explain: Connection pool configuration information
 * @author: lihewei
*/
@ConfigurationProperties("sftp.pool")
public class PoolProperties {
    /**
     * Minimum number of free connections in the connection pool The default value is 1.
     */
    private int minIdle = 1;

    /**
     * Minimum number of free connections in the connection pool. The default value is 8.
     */
    private int maxIdle = 8;

    /**
     * Maximum number of free connections in the link pool The default value is 8.
     */
    private int maxActive = 8;

    /**
     * The maximum amount of time the caller blocks when the connection pool resource is exhausted,
     * and the timeout will run an exception. Unit, milliseconds; The default value is -1.
     * It indicates that the system never times out
     */
    private long maxWait = -1;

    /**
     * When the "linked" resource is printed to the caller, it detects whether it is valid,
     * and if it is not, it is removed from the connection pool and attempts to retrieve it.
     * The default value is false. You are advised to keep the default value.
     */
    private boolean testOnBorrow = true;

    /**
     * Whether to check the validity of the link object when returning a link to the connection pool.
     * The default value is false. You are advised to keep the default value.
     */
    private boolean testOnReturn = false;

    /**
     * Whether the idle timeout of the "linked" object is detected when it is printed to the caller;
     * The default value is false. If the link idle times out,
     * it will be removed. You are advised to keep the default value.
     */
    private boolean testWhileIdle = true;

    /**
     * "Free link" detection threads, detection cycles, milliseconds.
     * If the value is negative, Check Thread is not running. The default value is -1.
     */
    private long timeBetweenEvictionRuns = 1000L * 60L * 10L;

    /**
     * The minimum time when the connection is idle,
     * after which the idle connection may be removed.
     * A negative value (-1) indicates that it is not removed.
     */
    private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;

    /**
     * Sets the target for the minimum number of idle objects to maintain in
     * each of the keyed sub-pools. This setting only has an effect if it is
     * positive and timeBetweenEvictionRuns is greater than zero. If this is
     * the case, an attempt is made to ensure that each sub-pool has the required
     * minimum number of instances during idle object eviction runs.
     * If the configured value of minIdlePerKey is greater than the configured
     * value for maxIdlePerKey then the value of maxIdlePerKey will be used
     * instead.
     */
    private int minIdlePerKey = 1;

    /**
     * Sets the cap on the number of "idle" instances per key in the pool.
     * If maxIdlePerKey is set too low on heavily loaded systems it is possible
     * you will see objects being destroyed and almost immediately new objects
     * being created. This is a result of the active threads momentarily
     * returning objects faster than they are requesting them, causing the
     * number of idle objects to rise above maxIdlePerKey. The best value for
     * maxIdlePerKey for heavily loaded system will vary but the default is a
     * good starting point.
     */
    private int maxIdlePerKey = 8;

    /**
     * Sets the limit on the number of object instances allocated by the pool
     * (checked out or idle), per key. When the limit is reached, the sub-pool
     * is said to be exhausted. A negative value indicates no limit.
     */
    private int maxActivePerKey = 8;

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRuns() {
        return timeBetweenEvictionRuns;
    }

    public void setTimeBetweenEvictionRuns(long timeBetweenEvictionRuns) {
        this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getMinIdlePerKey() {
        return minIdlePerKey;
    }

    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    public int getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public int getMaxActivePerKey() {
        return maxActivePerKey;
    }

    public void setMaxActivePerKey(int maxActivePerKey) {
        this.maxActivePerKey = maxActivePerKey;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PoolProperties.class.getSimpleName() + "[", "]")
                .add("minIdle=" + minIdle)
                .add("maxIdle=" + maxIdle)
                .add("maxActive=" + maxActive)
                .add("maxWait=" + maxWait)
                .add("testOnBorrow=" + testOnBorrow)
                .add("testOnReturn=" + testOnReturn)
                .add("testWhileIdle=" + testWhileIdle)
                .add("timeBetweenEvictionRuns=" + timeBetweenEvictionRuns)
                .add("minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis)
                .add("minIdlePerKey=" + minIdlePerKey)
                .add("maxIdlePerKey=" + maxIdlePerKey)
                .add("maxActivePerKey=" + maxActivePerKey)
                .toString();
    }
}
