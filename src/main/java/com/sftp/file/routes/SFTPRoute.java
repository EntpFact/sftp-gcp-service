package com.sftp.file.routes;

import com.sftp.file.processor.FileMetaDataProcessor;
import com.sftp.file.service.GCPBucketService;
import com.sftp.file.service.SecretManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ComponentScan(basePackages = { "com.sftp.file" })
public class SFTPRoute extends RouteBuilder {

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private int sftpPort;

    @Value("${sftp.path}")
    private String sftpPath;

    @Value("${sftp.username}")
    private String sftpUsername;

    @Value("${sftp.delay}")
    private long sftpDelay;

    @Value("${camel.component.gcs.bucket-folder}")
    private String gcpTargetPath;

    @Value("${camel.component.gcs.bucket}")
    private String bucketName;

    @Value("${gcp.projectId}")
    private String projectId;

    @Value("${gcp.secret.sftpSecret.secretId}")
    private String secretId;

    @Value("${gcp.secret.sftpSecret.secretVersion}")
    private String secretVersion;

    @Value("${gcp.secret.sftpUsername.secretId}")
    private String secretUserName;

    @Value("${gcp.secret.sftpUsername.secretVersion}")
    private String secretVersionForUserName;

    @Value("${sftp.keyFilePath}")
    private String keyFilePath;

    @Value("${email.config.success.flag}")
    private boolean emailSend;

    @Autowired
    private SecretManagerService service;

    @Override
    public void configure() throws Exception {

        String password= service.getSecret(projectId,secretId,secretVersion);
        String userName=service.getSftpUsername(projectId,secretUserName,secretVersionForUserName);

        String sftpUri = String.format(
                "sftp://%s@%s:%d%s?delay=%d&password=%s&privateKeyFile=%s&strictHostKeyChecking=no&useUserKnownHostsFile=false&passiveMode=false&delete=true",
                userName,
                sftpHost,
                sftpPort,
                sftpPath,
                sftpDelay,
                password,
               keyFilePath);

        onException(Exception.class)
                .log(LoggingLevel.ERROR,"Exception occurred: ${exception.printStackTrace()}")
                .handled(true)
                .to("direct:failure");

                 from(sftpUri)
                 .setHeader("emailFlag",constant(emailSend))
                .routeId("sftp route")
                .log("File detected: ${header.CamelFileName}")
                .process(new FileMetaDataProcessor(bucketName,gcpTargetPath))
                .to("direct:gcpRoute").log("File uploaded to GCS successfully")
                .bean(GCPBucketService.class,"pushFileMetadata")
                .log(LoggingLevel.ERROR,"File and metadata processing completed")
                .end();
    }
}
