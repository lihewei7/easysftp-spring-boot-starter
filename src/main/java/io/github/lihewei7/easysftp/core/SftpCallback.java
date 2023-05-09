package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

@FunctionalInterface
public interface SftpCallback<T> {

    T doInSftp(ChannelSftp channelSftp) throws SftpException;
}
