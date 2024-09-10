package com.sftp.file.routes;

import com.sftp.file.service.EmailService;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:email")
                .routeId("email-route")
                .log(LoggingLevel.ERROR,"in the email route")
                .bean(EmailService.class,"setMailParams")
                .choice()
                .when(header("completionStatus").isEqualTo("success"))
                .log(LoggingLevel.ERROR,"Sending success email")
                .to("string-template:templates/gcp-fileupload-success-email-template.tm")
                 .otherwise()
                .log(LoggingLevel.ERROR,"Sending failure email")
                .to("string-template:templates/gcp-fileupload-failure-email-template.tm")
                .end()
                .bean(EmailService.class,"sendEmail(${body},${header.to-address},${header.from-address},${header.cc},${header.bcc},${header.status},${header.subject})");

    }
}
