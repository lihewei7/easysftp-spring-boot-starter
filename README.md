# EasySftp

 ![](https://raw.githubusercontent.com/lihewei7/img/master/sftp3.png)

- **Github：[https://github.com/lihewei7/easysftp-spring-boot-starter](https://github.com/lihewei7/easysftp-spring-boot-starter)**


- **Gitee：[https://gitee.com/lihewei7/easysftp-spring-boot-starter](https://gitee.com/lihewei7/easysftp-spring-boot-starter)**

## EasySftp是什么？

EasySftp 是一个 SFTP 的 SpringBoot Starter，提供和 RedisTemplate 一样优雅的 SftpTemplate。主要包含了：文件上传、下载、校验、查看等功能，为用户提供了一种安全的方式来发送和接收文件和文件夹。使用池技术管理SFTP连接，避免频繁创建新连接造成连接耗时问题。

## Maven 依赖

- EasySftp已上传至Maven中央仓库，在工程中导入依赖即可使用
- 依赖 Apache commons-pool2

```xml
<dependency>
    <groupId>io.github.lihewei7</groupId>
    <artifactId>easysftp-spring-boot-starter</artifactId>
    <version>1.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

| 毕业版本   | 描述                                       |
| ------ | ---------------------------------------- |
| v1.0.0 | Version initialization                   |
| v1.0.1 | The encryption algorithm must be added for the new ssh version |
| v1.1.0 | Support multiple SFTP simultaneous connections |
| v1.2.0 | Support Login using a key                |

## 配置

### 单主机配置

- sftp基本配置（密码登录）

```yaml
sftp:
  enabled-log: false
  host: localhost
  port: 22
  username: root
  password: 1234
```
- sftp基本配置（密钥登录）


```yaml
sftp:
  enabled-log: false
  host: 10.1.61.118
  port: 19222
  username: lihw
  check-to-host-key: true
  key-path: /home/lihw/.ssh/id_rsa
  password: 生成密钥时的密码
  connect-timeout: 1500
```

- 连接池配置（可不配置使用默认值）

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

### 多主机配置

在多 Host 使用  SftpTemplate 需要为 Easysftp 指定将要使用的主机，详细操作见下方API。hosts 下可配置多台主机。rd-1为主机名（sftp.hosts 下 map 中的 key 代表 hostName ，可自定义主机名）

- 多 host ，密码登录

```yaml
sftp:
  enabled-log: false
  hosts:
    rd-1:
      host: 127.0.0.1
      port: 22
      username: lihw
      password: 1234
    rd-2:
      host: 127.0.0.2
      port: 22
      username: lihw
      password: 1234
```

-  多 host ，密码 + 密钥登录方式

```yaml
sftp:
  enabled-log: false
  hosts:
    rd-118:
      host: 10.1.61.118
      port: 19222
      username: lihw
      password: 1234
      connect-timeout: 1500
    rd-118:
      host: 10.1.61.119
      port: 19222
      username: lihw
      check-to-host-key: true
      key-path: /home/lihw/.ssh/id_rsa
      password: 生成密钥时设置的密码
      connect-timeout: 1500
```

-  多 Host 连接池配置（可不配置使用默认值）

```yaml
sftp:
  pool:
    min-idle-per-key: 1
    max-idle-per-key: 8
    max-active-per-key: 8
    max-active: 8
    max-wait: -1
    test-on-borrow: true
    test-on-return: false
    test-while-idle: true
    time-between-eviction-runs: 600000
    min-evictable-idle-time-millis: 1800000
```

### 密钥登录注意事项

EasySftp 使用`Jsch`作为 SFTP 的实现，而`Jsch`不支持密钥登录，因此你需要一些小改动：

- 更改`jsch`的实现：Jcraft 自 2018 年推送的 0.1.55 版本后就停止更新了，所以更换为其他人 fork 的 Jsch 库

  ```xml
  <dependency>
  	<groupId>io.github.lihewei7</groupId>
  	<artifactId>easysftp-spring-boot-starter</artifactId>
  	<version>1.2.0</version>
  	<exclusions>
  		<exclusion>
  			<groupId>com.jcraft</groupId>
  			<artifactId>jsch</artifactId>
  		</exclusion>
  	</exclusions>
  </dependency>
  <dependency>
  	<groupId>com.github.mwiede</groupId>
  	<artifactId>jsch</artifactId>
  	<version>0.2.9</version>
  </dependency>
  ```

- 密钥生成规则：[JSch - 配置SFTP服务器SSH密钥登录 - lihewei - 博客园 (cnblogs.com)](https://www.cnblogs.com/lihw/p/17336989.html)

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

​	所有方法都可能抛出 `SftpException`，这通常代表连接出问题了，也可能是你上传或下载的文件不存在。sftp 操作可能会改变工作目录，因此在连接返回给池前，框架会重置工作目录为原始目录。注意这只会重置远端工作路径，不会重置本地工作路径。下面的介绍全部使用 `配置` 章节中的配置进行说明，因此初始工作目录是 `/root`。

### upload

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

### 多Host

- `HostsManage.changeHost(string)` ：通过 hostName 指定下次使用的连接。注意它只能指定下一次的连接！！！

```java
HostsManage.changeHost("rd-1");
// 成功打印 rd-1 对应连接的原始目录
sftpTemplate.execute(ChannelSftp::pwd);
// 第二次执行失败，抛出空指针，需要再次指定对应连接才能继续使用
sftpTemplate.execute(ChannelSftp::pwd);
```

- `HostsManage.changeHost(string, boolean)`：连续使用相同 host 进行操作，避免执行一次 SftpTemplate 就要设置一次 hostName。注意要配合 `HostHolder.clearHost()` 使用！！！

```java
HostsManage.changeHost("rd-1", false);
try {
  sftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
  sftpTemplate.upload("D:\\b.pdf", "easysftp/b.pdf");
  sftpTemplate.upload("D:\\c.doc", "c.doc");
} finally {
  HostsManage.clearHost();
}
```

- `HostsManage.hostNames()` 与 ：获取所有的 host 连接的 name

```java
//有时需要批量执行配置的 n 个 host 连接，此时可以通过该方法获取所有或过滤后的 hostName 集合。
for (String hostName : HostsManage.hostNames()) {
   HostsManage.changeHost(hostName);
   sftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
}
```

- `HostsManage.hostNames(Predicate<String>)`：获取过滤后的 host 连接的 name

```java
// 获取所有以“rd-”开头的 hostName
for (String hostName : HostsManage.hostNames(s -> s.startsWith("rd-"))) {
  HostsManage.changeHost(hostName);
  sftpTemplate.upload("D:\\a.docx", "/home/easysftp/a.docx");
}
```
