package io.github.lihewei7.easysftp.core;

import io.github.lihewei7.easysftp.config.PoolProperties;
import io.github.lihewei7.easysftp.config.SftpProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author: lihewei
 */
public class SftpPool {
    private static final Log _logger = LogFactory.getLog(SftpPool.class);
    public static final String COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL = "Could not get a resource from the pool";
    private GenericObjectPool<SftpClient> sftpPool;

    public SftpPool(SftpProperties sftpProperties, PoolProperties poolProperties) {
        this.sftpPool = new GenericObjectPool<>(new PooledClientFactory(sftpProperties), getPoolConfig(poolProperties));
        _logger.info("Easysftp: Created");
    }

    /**
     * @Description: Obtain an sftp connection from the pool.
     * @author: lihewei
     */
    public SftpClient borrowObject() {
        try {
            return sftpPool.borrowObject();
        } catch (Exception e) {
            throw new PoolException(COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL, e);
        }
    }

    /**
     * @Description: The sftp connection is returned to the pool.
     * @author: lihewei
     */
    public void returnObject(SftpClient sftpClient) {
        try {
            sftpPool.returnObject(sftpClient);
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }

    /**
     * @Description: The sftp connection is destroyed from the pool.
     * @author: lihewei
     */
    public void invalidateObject(SftpClient sftpClient) {
        try {
            sftpPool.invalidateObject(sftpClient);
        } catch (Exception e) {
            throw new PoolException("Could not invalidate the broken resource", e);
        }
    }


    private static class PooledClientFactory extends BasePooledObjectFactory<SftpClient> {

        private final SftpProperties sftpProperties;

        public PooledClientFactory(SftpProperties sftpProperties) {
            this.sftpProperties = sftpProperties;
        }

        @Override
        public SftpClient create() {
            return new SftpClient(sftpProperties);
        }

        @Override
        public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
            return new DefaultPooledObject<>(sftpClient);
        }

        @Override
        public boolean validateObject(PooledObject<SftpClient> p) {
            return p.getObject().test();
        }

        @Override
        public void destroyObject(PooledObject<SftpClient> p) {
            p.getObject().disconnect();
        }

    }

    private GenericObjectPoolConfig<SftpClient> getPoolConfig(PoolProperties poolProperties) {
        GenericObjectPoolConfig<SftpClient> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(poolProperties.getMinIdle());
        config.setMaxIdle(poolProperties.getMaxIdle());
        config.setMaxTotal(poolProperties.getMaxActive());
        config.setMaxWaitMillis(poolProperties.getMaxWait());
        config.setTestOnBorrow(poolProperties.isTestOnBorrow());
        config.setTestOnReturn(poolProperties.isTestOnReturn());
        config.setTestWhileIdle(poolProperties.isTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(poolProperties.getTimeBetweenEvictionRuns());
        config.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
        return config;
    }
}
