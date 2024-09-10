package com.sftp.file.service;

import com.hdfcbank.ef.apiconnect.builder.PostRequest;
import com.hdfcbank.ef.apiconnect.service.APIClient;
import com.sftp.file.model.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.http.HttpHeaders;
import java.time.Duration;

@Slf4j
@Service
public class EmailService {

	@Autowired
	private APIClient apiClient;

	@Value("${mail.restURL}")
	private String emailRestURL;

	@Value("${email.config.success.to}")
	private String to;

	@Value("${email.config.success.from}")
	private String from;

	@Value("${email.config.success.cc}")
	private String cc;

	@Value("${email.config.success.bcc}")
	private String bcc;

	@Value("${email.config.success.status}")
	private String status;

	@Value("${email.config.success.subject}")
	private String subject;


	public void sendEmail(String body,String to,String from, String cc, String bcc, String status, String subject) throws Exception {
        log.error("Email body {}",body);
        try {


			Email mailBody = setEmailData(body, to, from, cc, bcc, status, subject);
		
			log.error("Email body {}1",mailBody);
            var apiRequest = PostRequest.builder().url(emailRestURL).body(mailBody).responseType(String.class).headers(headers -> headers.add("Authorization", "Bearer token"))
				.build();

			Mono<String> resp = ((Mono<String>) apiClient.execute(apiRequest)).retryWhen(Retry.backoff(1, Duration.ofSeconds(5)));
			resp.share().block();
        }
		catch(Exception e) {
			log.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
    }

    private Email setEmailData(String body,String to,String from, String cc, String bcc, String status, String subject) {
    	return Email.builder()
    			.to(to)
    			.from(from)
    			.cc(cc)
    			.bcc(bcc)
    			.status(status)
    			.subject(subject)
    			.mailBody(body)
				.build();
    }

	public void setMailParams(Exchange exchange){
		log.error("in set mail params");
		exchange.getIn().setHeader("to-address",to);
		exchange.getIn().setHeader("from-address",from);
		exchange.getIn().setHeader("cc",cc);
		exchange.getIn().setHeader("bcc",bcc);
		exchange.getIn().setHeader("status",status);
		exchange.getIn().setHeader("subject",subject);
	}

}
