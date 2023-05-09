[toc]

# EasySftp

**Github：[https://github.com/lihewei7/easysftp-spring-boot-starter](https://github.com/lihewei7/easysftp-spring-boot-starter)**

**Gitee：[https://gitee.com/lihewei7/easysftp-spring-boot-starter](https://gitee.com/lihewei7/easysftp-spring-boot-starter)**

## 简介

EasySftp是一个 SFTP 的 SpringBoot Starter，使用池技术管理SFTP连接，避免频繁创建新连接，提供和 RedisTemplate 一样优雅的 SftpTemplate。主要提供文件上传、下载、校验、查看等功能。

## Maven 依赖

依赖 Apache commons-pool2：

```xml
<dependency>
    <groupId>io.github.lihewei7</groupId>
    <artifactId>easysftp-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

## 配置

详细的配置属性说明见参考开发工具的自动提示。

### SFTP基本配置

```yaml
sftp:
  enabled-log: false
  host: localhost
  port: 22
  username: root
  password: 1234
  # 适配新版本ssh需添加对应的加密算法(参考下面配置即可)
  kex: diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256
```
### 连接池（可以不配置）

```yaml
sftp:
  pool:
    min-idle: 1
    max-idle: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

## 使用

EasySftp 提供 SftpTemplate 类，它与 `spring-boot-starter-data-redis`  提供的 RedisTemplate 使用方法相同，任意方式注入即可使用：

```java
@Component
public class XXXService {
  
  @Autowired
  private SftpTemplate sftpTemplate;

  public void downloadFileWork(String from, String to) throws Exception {
    sftpTemplate.download(from, to);
  }
  
  public void uploadFileWork(String from, String to) throws Exception {
    sftpTemplate.upload(from, to);
  }
}
```

## API

- 所有方法都可能抛出 `SftpException`，这通常代表连接出问题了，也可能是你上传或下载的文件不存在。
- sftp 操作可能会改变工作目录，因此在连接返回给池前，框架会重置工作目录为原始目录。注意这只会重置远端工作路径，不会重置本地工作路径。

下面的介绍全部使用 `配置` 章节中的配置进行说明，因此初始工作目录是 `/root`。

### upload

`public void upload(String from, String to) throws SftpException `

上传文件，该方法会递归创建上传的远程文件所在的父目录。

```java
// 上传 D:\\a.docx 到 /home/easysftp/a.docx
sftpTemplate.upload("D:\\a.docx", "/home/easysftp/aptx4869.docx");

// 上传 D:\\a.pdf 到 /root/easysftp/a.pdf
sftpTemplate.upload("D:\\a.pdf", "easysftp/a.pdf");

// 上传 D:\\a.doc 到 /root/a.doc
sftpTemplate.upload("D:\\a.doc", "a.doc");
```

### download

`public void download(String from, String to) throws SftpException`

下载文件，该方法只会创建下载的本地文件，不会创建本地文件的父目录。

```java
// 下载 /home/easysftp/b.docx 到 D:\\b.docx
sftpTemplate.download("/home/easysftp/b.docx", "D:\\b.docx");

// 下载 /root/easysftp/b.pdf 到 D:\\b.pdf
sftpTemplate.download("easysftp/b.pdf", "D:\\b.pdf");

// 下载 /root/b.doc 到 D:\\b.doc
sftpTemplate.download("b.doc", "D:\\b.doc");
```

### exists

`public boolean exists(String path) throws SftpException`

校验文件是否存在，存在返回true，不存在返回false

```java
// 测试 /home/easysftp/c.docx 是否存在
boolean result1 = sftpTemplate.exists("/home/easysftp/c.pdf");
// 测试 /root/easysftp/c.docx 是否存在
boolean result2 = sftpTemplate.exists("easysftp/c.docx");
// 测试 /root/c.docx 是否存在
boolean result3 = sftpTemplate.exists("c.doc");
```

### list

`public ChannelSftp.LsEntry[] list(String path) throws SftpException`

查看文件/目录

```java
// 查看文件 /home/easysftp/d.pdf
LsEntry[] list1 = sftpTemplate.list("/home/easysftp/d.pdf");
// 查看文件 /root/easysftp/d.docx
LsEntry[] list2 = sftpTemplate.list("easysftp/d.docx");
// 查看文件 /root/d.doc
LsEntry[] list3 = sftpTemplate.list("d.doc");

// 查看目录 /home/easysftp
LsEntry[] list4 = sftpTemplate.list("/home/easysftp");
// 查看目录 /root/easysftp
LsEntry[] list5 = sftpTemplate.list("easysftp");
```

### execute

`execute(SftpCallback<T> action)` 用于执行自定义 SFTP 操作，比如查看 SFTP 默认目录（关于 ChannelSftp 的其他用法请参考 jsch 的 API）：[JSch - Java实现的SFTP（文件上传下载） - lihewei - 博客园 (cnblogs.com)](https://www.cnblogs.com/lihw/p/17168705.html)

```java
String dir = sftpTemplate.execute(ChannelSftp::pwd);
//或
String dir2 = sftpTemplate.execute(ChannelSftp -> pwd());
```

### executeWithoutResult

`executeWithoutResult(SftpCallbackWithoutResult action)`用于执行自定义没有返回值的SFTP操作，比如查看默认的SFTP目录（ChannelSftp的其他用途，请参考 jsch 的 API）：[JSch - Java实现的SFTP（文件上传下载） - lihewei - 博客园 (cnblogs.com)](https://www.cnblogs.com/lihw/p/17168705.html)

```java
sftpTemplate.executeWithoutResult(channelSftp -> System.out.println(channelSftp.getHome()));
```



## 未来展望

- 兼容ftp协议
- 实现SFTP监控传输进度
