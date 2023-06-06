package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.*;
import io.github.lihewei7.easysftp.config.SftpProperties;

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
            if (sftpProperties.isCheckToHostKey()) {
                session.setConfig("PreferredAuthentications", "publickey");
                session.setConfig("userauth.gssapi-with-mic", "no");
                session.setConfig("StrictHostKeyChecking", "ask");
                session.setUserInfo(new SftpAuthKeyUserInfo(sftpProperties.getPassword()));
                jsch.addIdentity(sftpProperties.getKeyPath());
            } else {
                session.setConfig("PreferredAuthentications", "password");
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(sftpProperties.getPassword());
            }
            session.setConfig("UseDNS", "no");
            session.setConfig("kex", "diffie-hellman-group1-sha1,"
                    + "diffie-hellman-group-exchange-sha1,"
                    + "diffie-hellman-group-exchange-sha256");
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

    private static class SftpAuthKeyUserInfo  implements UserInfo {
        /**
         * ssh private key passphrase
         */
        private final String passphrase;

        public SftpAuthKeyUserInfo  (String passphrase) {
            this.passphrase = passphrase;
        }

        @Override
        public String getPassphrase() {
            return passphrase;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return true;
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public void showMessage(String message) {
        }
    }
}
