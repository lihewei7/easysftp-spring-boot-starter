package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.Assert;

/**
 * @author: lihewei
 */
public class SftpTemplate {
    private final SftpPool sftpPool;

    public SftpTemplate(SftpPool sftpPool) {
        this.sftpPool = sftpPool;
    }

    /**
     * sftp template methods with return values include basic SFTP operations,
     * such as obtaining and returning connections.
     */
    public <T> T execute(SftpCallback<T> action) throws SftpException {
        Assert.notNull(action, "Callback object must not be null");
        String hostName = sftpPool.isUniqueHost() ? null : HostsManage.getHostName();
        SftpClient sftpClient = null;
        try {
            sftpClient = sftpPool.borrowObject(hostName);
            return action.doInSftp(sftpClient.getChannelSftp());
        } finally {
            HostsManage.clear();
            if (sftpClient != null) {
                if (sftpClient.reset()) {
                    sftpPool.returnObject(hostName, sftpClient);
                } else {
                    sftpPool.invalidateObject(hostName, sftpClient);
                }
            }
        }
    }

    /**
     * The sftp template method with no return value includes basic SFTP operations,
     * such as obtaining and returning connections.
     */
    public void executeWithoutResult(SftpCallbackWithoutResult action) throws SftpException {
        Assert.notNull(action, "Callback object must not be null");
        this.execute(ChannelSftp -> {
            action.doInSftp(ChannelSftp);
            return null;
        });
    }

    public void download(String from, String to) throws SftpException {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
    }

    public void upload(String from, String to) throws SftpException {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
    }

    public boolean exists(String path) throws SftpException {
        return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).exists(path));
    }

    public ChannelSftp.LsEntry[] list(String path) throws SftpException {
        return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).list(path));
    }
}
