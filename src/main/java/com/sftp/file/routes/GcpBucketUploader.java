package com.sftp.file.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GcpBucketUploader extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from( "direct:gcpRoute")
                .routeId("direct-gcpRoute")

                .log(LoggingLevel.ERROR,"in the gcp route")
                .doTry()
                .to("google-storage://{{camel.storage.googleStorage}}")
                //.to("google-storage://{{camel.component.gcs.bucket}}?serviceAccountKey=file:/C:/Users/TUSHAR/Downloads/hbl-poc-enterprisefac-pm-prj-0c29c74f5be2.json")
                .setHeader("completionStatus", constant("success"))
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error occurred: ${exception.message}")
                .setHeader("completionStatus", constant("failure"))
                .doFinally()
                .to("direct:email")
                .end();













    }
}
