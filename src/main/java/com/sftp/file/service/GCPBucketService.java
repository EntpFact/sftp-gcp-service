package com.sftp.file.service;


import com.hdfcbank.messageconnect.config.PubSubOptions;
import com.hdfcbank.messageconnect.dapr.producer.DaprProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ComponentScan(basePackages = { "com.hdfcbank" })
public class GCPBucketService {

    @Value("${file.upload.topicname}")
    private String topicName;

    @Value("${file.upload.pubsubname}")
    private String pubsubName;

    @Value("${file.uploader.storage.provider}")
    private String storageProviderName;

    @Autowired
    private DaprProducer daprProducer;

    public void pushFileMetadata(Exchange exchange) throws Exception{

        log.error("pushFileMetadata is getting call");

        Map<String,Object> bucketMetaData=new HashMap<>();
        String fileSize = String.valueOf(exchange.getIn().getHeader("CamelGoogleCloudStorageContentLength", Long.class));

        String bucketName= exchange.getIn().getHeader("CamelGoogleCloudStorageBucketName", String.class);

        String filePath= exchange.getIn().getHeader("CamelGoogleCloudStorageObjectName", String.class);
        String objectName = exchange.getIn().getHeader("CamelFileName", String.class);
        bucketMetaData.put("storageProvider",storageProviderName);
        bucketMetaData.put("fileDirectory",filePath);
        bucketMetaData.put("fileName",objectName);
        bucketMetaData.put("bucketName",bucketName);
        bucketMetaData.put("fileSize",fileSize);
        publishDataToKafka(bucketMetaData, this.daprProducer);

    }

    private void publishDataToKafka(Map<String, Object> bucketMetaData, DaprProducer daprProducer) {
        var pubsubOptions= PubSubOptions.builder()
                .pubsubName(pubsubName)
                .requestData(bucketMetaData)
                .metadata(null)
                .topic(topicName)
                .build();

        daprProducer.invokeDaprPublishEvent(pubsubOptions).subscribe((resp) -> {
            log.error("Pubsub Mono Complete");
        });
    }

    public void publishFileMetadataToKafk(Map<String, Object> payload){

        publishDataToKafka(payload, this.daprProducer);
    }
}
