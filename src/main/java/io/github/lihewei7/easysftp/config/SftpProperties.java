package io.github.lihewei7.easysftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.LinkedHashMap;

/**
 * @explain: SFTP client configuration information
 * @author: lihewei
*/
@ConfigurationProperties("sftp")
public class SftpProperties {

    private String host = "localhost";
    private int port = 22;
    private String username;
    private String password = "";
    /**
     * Connection timeout.
     */
    private int connectTimeout = 0;
    /**
     * Enable jsch log, Cannot be individually turned on or off for one of multiple hosts.
     */
    private boolean enabledLog = false;

    /**
     * Configuring multiple hosts.
     */
    private LinkedHashMap<String,SftpProperties> hosts;

    public LinkedHashMap<String, SftpProperties> getHosts() {
        return hosts;
    }

    public void setHosts(LinkedHashMap<String, SftpProperties> hosts) {
        this.hosts = hosts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabledLog() {
        return enabledLog;
    }

    public void setEnabledLog(boolean enabledLog) {
        this.enabledLog = enabledLog;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
