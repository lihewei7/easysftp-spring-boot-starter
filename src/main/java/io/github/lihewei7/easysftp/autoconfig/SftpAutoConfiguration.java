package io.github.lihewei7.easysftp.autoconfig;

import com.jcraft.jsch.JSch;
import io.github.lihewei7.easysftp.config.PoolProperties;
import io.github.lihewei7.easysftp.config.SftpProperties;
import io.github.lihewei7.easysftp.core.HostsManage;
import io.github.lihewei7.easysftp.core.JschLogger;
import io.github.lihewei7.easysftp.core.SftpPool;
import io.github.lihewei7.easysftp.core.SftpTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author: lihewei
*/
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SftpProperties.class, PoolProperties.class})
public class SftpAutoConfiguration {

    @Bean
    public SftpPool sftpPool(SftpProperties sftpProperties, PoolProperties poolProperties) {
        JSch.setLogger(new JschLogger(sftpProperties.isEnabledLog()));
        return sftpProperties.getHosts() == null ?
                new SftpPool(sftpProperties, poolProperties) :
                new SftpPool(HostsManage.initHostKeys(sftpProperties.getHosts()),poolProperties);
    }

    @Bean
    @DependsOn("sftpPool")
    public SftpTemplate sftpTemplate(SftpPool sftpPool) {
        return new SftpTemplate(sftpPool);
    }
}
