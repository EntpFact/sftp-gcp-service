package com.sftp.file.routes;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ErrorRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("direct:failure")
		.routeId("direct-errorRoute")
		.process(new Processor() {
			public void process(Exchange exchange) throws Exception{
				Exception exception = (Exception)exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
				log.info("Exception : {}",exception.getMessage());
			}
		}).end();
	}

}
