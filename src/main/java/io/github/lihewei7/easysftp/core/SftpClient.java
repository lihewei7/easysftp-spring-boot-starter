package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import io.github.lihewei7.easysftp.config.SftpProperties;

import java.util.Properties;

/**
 * @explain: SFTP Client.
 * @author: lihewei
 */
public class SftpClient {

    private final ChannelSftp channelSftp;
    private final Session session;
    private final String originalDir;

    public ChannelSftp getChannelSftp() {
        return channelSftp;
    }

    public SftpClient(SftpProperties sftpProperties) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpProperties.getUsername(), sftpProperties.getHost(), sftpProperties.getPort());
            session.setPassword(sftpProperties.getPassword());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
            //The encryption algorithm must be added for the new ssh version
            config.put("kex", "diffie-hellman-group1-sha1,"
                    + "diffie-hellman-group-exchange-sha1,"
                    + "diffie-hellman-group-exchange-sha256");
            session.setConfig(config);
            session.connect(sftpProperties.getConnectTimeout());
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            originalDir = channelSftp.pwd();
        } catch (Exception e) {
            disconnect();
            throw new IllegalStateException("failed to create sftp Client", e);
        }
    }

    /**
     * disconnect.
     */
    protected final void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * test connection.
     */
    protected boolean test() {
        try {
            if (channelSftp.isConnected() && originalDir.equals(channelSftp.pwd())) {
                channelSftp.lstat(originalDir);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Reset the connection and restore the initial sftp path.
     */
    protected boolean reset() {
        try {
            channelSftp.cd(originalDir);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }
}
