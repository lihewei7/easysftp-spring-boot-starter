package io.github.lihewei7.easysftp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

@FunctionalInterface
public interface SftpCallbackWithoutResult {

  void doInSftp(ChannelSftp channelSftp) throws SftpException;
}
