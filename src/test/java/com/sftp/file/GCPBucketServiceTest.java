package com.sftp.file;

import com.hdfcbank.messageconnect.dapr.producer.DaprProducer;
import com.sftp.file.routes.SFTPRoute;
import com.sftp.file.service.EmailService;
import com.sftp.file.service.GCPBucketService;
import com.sftp.file.service.SecretManagerService;
import io.dapr.client.DaprClient;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;


@CamelSpringBootTest
@SpringBootTest
@ComponentScan(basePackages = {"com.sftp.file"})
class GCPBucketServiceTest {
    @MockBean
    @Qualifier("buildDaprClient")
    DaprClient daprClient;
    @InjectMocks
    private GCPBucketService gcpBucketService;
    @Mock
    private DaprProducer daprProducer;
    @Mock
    private Exchange exchange;
    @Mock
    private Message message;
    @MockBean
    private SecretManagerService secretManager;
    @MockBean
    private ProducerTemplate template;
    @MockBean
    private EmailService emailService;
    @InjectMocks
    private SFTPRoute sftpRoute;
    private static final String TOPIC_NAME = "mocked-topic";
    private static final String PUBSUB_NAME = "mocked-pubsub";
    private static final String STORAGE_PROVIDER_NAME = "mocked-storage-provider";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPushFileMetadata() throws Exception {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader("CamelGoogleCloudStorageContentLength", Long.class)).thenReturn(1024L);
        when(message.getHeader("CamelGoogleCloudStorageBucketName", String.class)).thenReturn("test-bucket");
        when(message.getHeader("CamelGoogleCloudStorageObjectName", String.class)).thenReturn("test-folder/testfile.txt");
        when(message.getHeader("CamelFileName", String.class)).thenReturn("testfile.txt");
        when(daprProducer.invokeDaprPublishEvent(any())).thenReturn(Mono.empty());
        gcpBucketService.pushFileMetadata(exchange);
        verify(daprProducer, times(1)).invokeDaprPublishEvent(any());
    }
    @Test
    void testPublishFileMetadataToKafka() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("fileName", "testfile.txt");
        metadata.put("bucketName", "test-bucket");
        when(daprProducer.invokeDaprPublishEvent(any())).thenReturn(Mono.empty());
        gcpBucketService.publishFileMetadataToKafk(metadata);
        verify(daprProducer, times(1)).invokeDaprPublishEvent(any());
    }
    @Test
    void testPublishDataToKafka_SubscriptionComplete() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("kashkey", "hashvalue");
        when(daprProducer.invokeDaprPublishEvent(any())).thenReturn(Mono.empty());
        gcpBucketService.publishFileMetadataToKafk(metadata);
        verify(daprProducer, times(1)).invokeDaprPublishEvent(any());
    }


}