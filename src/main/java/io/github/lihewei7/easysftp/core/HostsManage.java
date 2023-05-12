package io.github.lihewei7.easysftp.core;

import io.github.lihewei7.easysftp.config.SftpProperties;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * If multiple hosts are used, manage them.
 *
 * @author: lihewei
 */
public class HostsManage {
    private static final ThreadLocal<Hosts> THREADLOCAL = new ThreadLocal<>();
    private static Set<String> hostNames;

    public static LinkedHashMap<String, SftpProperties> initHostKeys(LinkedHashMap<String, SftpProperties> sftpPropertiesMap) {
        if (hostNames != null) {
            throw new UnsupportedOperationException("HostHolder hostNames unsupported modify");
        }
        hostNames = Collections.unmodifiableSet(sftpPropertiesMap.keySet());
        return sftpPropertiesMap;
    }

    /**
     * Return all host names.
     * @return host names.
     * @see SftpProperties#getHosts().key
     */
    public static Set<String> hostNames() {
        Assert.notNull(hostNames, "Not multiple hosts");
        return hostNames;
    }

    /**
     * Return the filtered host name.
     * @param predicate filter condition.
     * @return host names.
     */
    public static Set<String> hostNames(Predicate<String> predicate) {
        Assert.notNull(hostNames, "Not multiple hosts");
        return hostNames.stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * Provides a method for users to set up hosts.
     * @param hostName host key.
     */
    public static void changeHost(String hostName) {
        Assert.notNull(hostName, "hostName must not be null");
        THREADLOCAL.set(new Hosts(hostName,true));
    }

    /**
     * Provides the user with a way to set up the host and stay connected.
     * @param hostName host key.
     * @param autoClose Set true to preserve the connection.
     */
    public static void changeHost(String hostName, boolean autoClose) {
        Assert.notNull(hostName, "hostName must not be null");
        THREADLOCAL.set(new Hosts(hostName, autoClose));
    }

    /**
     * Clear the host bound to the thread
     * Manually disable the keep-connected state.
     */
    public static void clearHost() {
        THREADLOCAL.remove();
    }

    /**
     * Clear the host bound to the thread
     * This method is called every time a template method is used.
     */
    protected static void clear() {
        Hosts hosts;
        if ((hosts = THREADLOCAL.get()) != null && hosts.autoClose) {
            THREADLOCAL.remove();
        }
    }

    /**
     * Gets the host name bound to the current thread.
     * @return
     */
    public static String getHostName() {
        Hosts hosts;
        Assert.notNull(hosts = THREADLOCAL.get() , "Host key not set");
        return hosts.hostName;
    }

    static class Hosts {
        private String hostName;
        private Boolean autoClose;

        public Hosts(String hostName, Boolean autoClose) {
            this.hostName = hostName;
            this.autoClose = autoClose;
        }
    }
}
