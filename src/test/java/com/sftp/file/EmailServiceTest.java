package com.sftp.file;

import com.hdfc.ef.secretManager.service.SecretProvider;
import com.hdfcbank.ef.apiconnect.builder.PostRequest;
import com.hdfcbank.ef.apiconnect.service.APIClient;
import com.sftp.file.model.Email;
import com.sftp.file.service.EmailService;
import com.sftp.file.service.SecretManagerService;
import io.dapr.client.DaprClient;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class EmailServiceTest {
    @MockBean
    @Qualifier("buildDaprClient")
    DaprClient daprClient;
    @InjectMocks
    private EmailService emailService;
    @MockBean
    private SecretProvider secretProvider;
    @Autowired
    private SecretManagerService secretManagerService;
    @MockBean
    private APIClient apiClient;
    @Mock
    private Exchange exchange;
    @Mock
    private Message message;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testSendEmail() throws Exception {
        Mono<String> mockedResponse = Mono.just("Email sent successfully");
        doReturn(mockedResponse).when(apiClient).execute(any(PostRequest.class));
        emailService.sendEmail("This is a test email body",
                "to@mocking.com",
                "from@mocking.com",
                "cc@mocking.com",
                "bcc@mocking.com",
                "success",
                "Mocked Subject");
        verify(apiClient, times(1)).execute(any(PostRequest.class));
    }


    @Test
    public void testSetEmailData() throws Exception {

        EmailService emailService = new EmailService();
        String body = "This is a test email body.";
        String to = "recipient@mocking.com";
        String from = "sender@mocking.com";
        String cc = "cc@mocking.com";
        String bcc = "bcc@mocking.com";
        String status = "Sent";
        String subject = "Test Email Subject";
        Method setEmailDataMethod = EmailService.class.getDeclaredMethod(
                "setEmailData", String.class, String.class, String.class, String.class, String.class, String.class, String.class
        );
        setEmailDataMethod.setAccessible(true);
        Email email = (Email) setEmailDataMethod.invoke(emailService, body, to, from, cc, bcc, status, subject);
        assertEquals(to, email.getTo(), "Email 'to' address should match");
        assertEquals(from, email.getFrom(), "Email 'from' address should match");
        assertEquals(cc, email.getCc(), "Email 'cc' address should match");
        assertEquals(bcc, email.getBcc(), "Email 'bcc' address should match");
        assertEquals(status, email.getStatus(), "Email status should match");
        assertEquals(subject, email.getSubject(), "Email subject should match");
        assertEquals(body, email.getMailBody(), "Email body should match");

    }


    @Test
    public void testSetMailParams() {

        ReflectionTestUtils.setField(emailService, "to", "to@mocking.com");
        ReflectionTestUtils.setField(emailService, "from", "from@mocking.com");
        ReflectionTestUtils.setField(emailService, "cc", "cc@mocking.com");
        ReflectionTestUtils.setField(emailService, "bcc", "bcc@mocking.com");
        ReflectionTestUtils.setField(emailService, "status", "status");
        ReflectionTestUtils.setField(emailService, "subject", "subject");
        when(exchange.getIn()).thenReturn(message);
        emailService.setMailParams(exchange);
        verify(message).setHeader("to-address", "to@mocking.com");
        verify(message).setHeader("from-address", "from@mocking.com");
        verify(message).setHeader("cc", "cc@mocking.com");
        verify(message).setHeader("bcc", "bcc@mocking.com");
        verify(message).setHeader("status", "status");
        verify(message).setHeader("subject", "subject");
    }
}