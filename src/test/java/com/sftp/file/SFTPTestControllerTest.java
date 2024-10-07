package com.sftp.file;

import com.sftp.file.controller.SFTPController;
import com.sftp.file.processor.FileMetaDataProcessor;
import com.sftp.file.routes.SFTPRoute;
import com.sftp.file.service.EmailService;
import com.sftp.file.service.GCPBucketService;
import com.sftp.file.service.SecretManagerService;
import io.dapr.client.DaprClient;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class SFTPTestControllerTest {

    @MockBean
    @Qualifier("buildDaprClient")
    DaprClient daprClient;
    @MockBean
    private GCPBucketService gcpBucketService;
    @MockBean
    private FileMetaDataProcessor fileMetaDataProcessor;
    @MockBean
    private EmailService emailService;
    @MockBean
    private SecretManagerService secretManager;
    @MockBean
    private ProducerTemplate template;
    @InjectMocks
    private SFTPRoute sftpRoute;
    private MockMvc mockMvc;
    @InjectMocks
    private SFTPController sftpTestController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sftpTestController).build();
    }
    @Test
    public void testPublishDataToKafka_Success() throws Exception {
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        metadata.put("data", data);
        doNothing().when(gcpBucketService).publishFileMetadataToKafk(data);
        mockMvc.perform(post("/sftp/publishDataToKafka").contentType(MediaType.APPLICATION_JSON).content("{ \"data\": { \"key1\": \"value1\" } }")).andExpect(status().isOk()).andExpect(content().string("Publish Data Successfully to Kafka Topic"));
    }
    @Test
    public void testPublishDataToKafka_InternalServerError() throws Exception {
        Map<String, Object> metadataerror = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        metadataerror.put("data", data);
        doThrow(new RuntimeException("Kafka publishing failed")).when(gcpBucketService).publishFileMetadataToKafk(data);
        mockMvc.perform(post("/sftp/publishDataToKafka").contentType(MediaType.APPLICATION_JSON).content("{ \"data\": { \"key1\": \"value1\" } }")).andExpect(status().isInternalServerError()).andExpect(content().string("Kafka publishing failed"));
    }
}