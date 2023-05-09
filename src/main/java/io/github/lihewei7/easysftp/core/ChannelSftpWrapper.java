package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.Assert;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

public class ChannelSftpWrapper {
    private final ChannelSftp channelSftp;

    public ChannelSftpWrapper(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }

    /**
     * Download files from the sftp server to the specified local directory
     * @see SftpTemplate#download(String, String)
     * @param from Path of the remote file
     * @param to   Path after downloading the file to a local directory
     * @throws SftpException
     */
    public void download(String from, String to) throws SftpException {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SftpException(e.id, "remote file '" + from + "' not exists.");
            }
            throw e;
        }
    }

    /**
     * File upload: Upload local files to sftp
     * @see SftpTemplate#upload(String, String)
     * @param from Local source file path
     * @param to   Remote path after the file is uploaded
     * @throws SftpException
     */
    public void upload(String from, String to) throws SftpException {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        if (!new File(from).exists()) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "local file '" + from + "' not exists.", new FileNotFoundException(from));
        }
        String dir = to.substring(0, to.lastIndexOf(File.separator) + 1);
        if (!"".equals(dir)) {
            cdAndMkdir(dir);
        }
        channelSftp.put(from, to.substring(to.lastIndexOf(File.separator) + 1));
    }

    /**
     * Create and enter the path.
     * @param path sftp Remote path
     * @throws SftpException
     */
    public final void cdAndMkdir(String path) throws SftpException {
        Assert.hasLength(path, "path must not be null");
        try {
            cd(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SftpException(e.id, "failed to change remote directory '" + path + "'." + e.getMessage(), e.getCause());
            }
            if (path.startsWith(File.separator)) {
                cd(File.separator);
            }
            String[] dirs = path.split(File.separator);
            for (String dir : dirs) {
                if ("".equals(dir)) {
                    continue;
                } else if (!isDir(dir)) {
                    mkdir(dir);
                }
                cd(dir);
            }
        }
    }

    public void cd(String path) throws SftpException {
        try {
            channelSftp.cd(path);
        } catch (SftpException e) {
            throw new SftpException(e.id, "failed to change remote directory '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    public boolean isDir(String dir) throws SftpException {
        try {
            return channelSftp.lstat(dir).isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "cannot check status for dir '" + dir + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * Create Routes
     * @param path the remote path.
     * @throws SftpException
     */
    public void mkdir(String path) throws SftpException {
        try {
            this.channelSftp.mkdir(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_FAILURE || !exists(path)) {
                throw new SftpException(e.id, "failed to create remote directory '" + path + "'." + e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * Check whether the path exists
     * @see SftpTemplate#exists(String)
     * @param path Address of the path to be verified
     * @return Destination path existence：true/false
     * @throws SftpException
     */
    public boolean exists(String path) throws SftpException {
        try {
            this.channelSftp.lstat(path);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "cannot check status for path '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * @see SftpTemplate#list(String)
     */
    public ChannelSftp.LsEntry[] list(String path) throws SftpException {
        Assert.hasLength(path, "path must not be null");
        try {
            Vector<?> lsEntries = this.channelSftp.ls(path);
            if (lsEntries == null) {
                return new ChannelSftp.LsEntry[0];
            }
            ChannelSftp.LsEntry[] entries = new ChannelSftp.LsEntry[lsEntries.size()];
            for (int i = 0; i < lsEntries.size(); i++) {
                Object next = lsEntries.get(i);
                Assert.state(next instanceof ChannelSftp.LsEntry, "expected only LsEntry instances from channel.ls()");
                entries[i] = (ChannelSftp.LsEntry) next;
            }
            return entries;
        } catch (SftpException e) {
            throw new SftpException(e.id, "failed to list files." + e.getMessage(), e.getCause());
        }
    }
}
