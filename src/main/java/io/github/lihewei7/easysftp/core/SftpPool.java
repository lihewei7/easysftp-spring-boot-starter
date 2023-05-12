package io.github.lihewei7.easysftp.core;

import io.github.lihewei7.easysftp.config.PoolProperties;
import io.github.lihewei7.easysftp.config.SftpProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;

import java.util.LinkedHashMap;

/**
 * @author: lihewei
 */
public class SftpPool {
    private static final Log _logger = LogFactory.getLog(SftpPool.class);
    public static final String COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL = "Could not get a resource from the pool";
    private GenericObjectPool<SftpClient> genericSftpPool;
    private GenericKeyedObjectPool<String, SftpClient> genericKeyedSftpPool;

    public SftpPool(SftpProperties sftpProperties, PoolProperties poolProperties) {
        this.genericSftpPool = new GenericObjectPool<>(new PooledClientFactory(sftpProperties), getPoolConfig(poolProperties));
        _logger.info("Easysftp: Created");
    }

    public SftpPool(LinkedHashMap sftpPropertiesMap,PoolProperties poolProperties){
        this.genericKeyedSftpPool = new GenericKeyedObjectPool<>(new keyedPooledClientFactory(sftpPropertiesMap),getKeyedPoolConfig(poolProperties));
        _logger.info("multiple-host Easysftp Successfully created");
    }

    /**
     * Check whether it is a single host.
     */
    public boolean isUniqueHost() {
        return genericSftpPool != null;
    }

    /**
     * @Description: Obtain an sftp connection from the pool.
     * @author: lihewei
     */
    public SftpClient borrowObject(String key) {
        try {
            return key == null ?
                    genericSftpPool.borrowObject() : genericKeyedSftpPool.borrowObject(key);
        } catch (Exception e) {
            throw new PoolException(COULD_NOT_GET_A_RESOURCE_FROM_THE_POOL, e);
        }
    }

    /**
     * @Description: The sftp connection is returned to the pool.
     * @author: lihewei
     */
    public void returnObject(String key,SftpClient sftpClient) {
        try {
            if (key == null){
                genericSftpPool.returnObject(sftpClient);
            }else {
                genericKeyedSftpPool.returnObject(key, sftpClient);
            }
        } catch (Exception e) {
            throw new PoolException("Could not return a resource from the pool", e);
        }
    }

    /**
     * @Description: The sftp connection is destroyed from the pool.
     * @author: lihewei
     */
    public void invalidateObject(String key, SftpClient sftpClient) {
        try {
            if (key == null){
                genericSftpPool.invalidateObject(sftpClient);
            }else {
                genericKeyedSftpPool.invalidateObject(key,sftpClient);
            }
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


    private static class keyedPooledClientFactory extends BaseKeyedPooledObjectFactory<String, SftpClient> {

        private LinkedHashMap<String,SftpProperties> sftpPropertiesMap;

        public keyedPooledClientFactory(LinkedHashMap sftpPropertiesMap){
            this.sftpPropertiesMap = sftpPropertiesMap;
        }

        @Override
        public SftpClient create(String key) {
            return new SftpClient(sftpPropertiesMap.get(key));
        }

        @Override
        public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
            return new DefaultPooledObject<>(sftpClient);
        }

        @Override
        public void destroyObject(String key, PooledObject<SftpClient> p) {
            p.getObject().disconnect();
        }

        @Override
        public boolean validateObject(String key, PooledObject<SftpClient> p) {
            return p.getObject().test();
        }
    }

    private GenericObjectPoolConfig<SftpClient> getPoolConfig(PoolProperties poolProperties) {
        GenericObjectPoolConfig<SftpClient> config = commonPoolConfig(new GenericObjectPoolConfig<>(), poolProperties);
        config.setMinIdle(poolProperties.getMinIdle());
        config.setMaxIdle(poolProperties.getMaxIdle());
        config.setMaxTotal(poolProperties.getMaxActive());
        return config;
    }

    private GenericKeyedObjectPoolConfig<SftpClient> getKeyedPoolConfig(PoolProperties poolProperties) {
        GenericKeyedObjectPoolConfig<SftpClient> config = commonPoolConfig(new GenericKeyedObjectPoolConfig<>(), poolProperties);
        config.setMinIdlePerKey(poolProperties.getMinIdle());
        config.setMaxIdlePerKey(poolProperties.getMaxIdle());
        config.setMaxTotalPerKey(poolProperties.getMaxActivePerKey());
        config.setMaxTotal(poolProperties.getMaxActive());
        return config;
    }

    private <T extends BaseObjectPoolConfig<SftpClient>> T commonPoolConfig(T config, PoolProperties poolProperties) {
        config.setMaxWaitMillis(poolProperties.getMaxWait());
        config.setTestOnBorrow(poolProperties.isTestOnBorrow());
        config.setTestOnReturn(poolProperties.isTestOnReturn());
        config.setTestWhileIdle(poolProperties.isTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(poolProperties.getTimeBetweenEvictionRuns());
        config.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
        return config;
    }
}
