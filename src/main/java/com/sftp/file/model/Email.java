package com.sftp.file.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Email {

	private String to;
	private String from;
	private String cc;
	private String bcc;
	private String status;
	private String subject;
	private String mailBody;
	
}
