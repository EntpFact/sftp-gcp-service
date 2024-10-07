package com.sftp.file.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.google.storage.GoogleCloudStorageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileMetaDataProcessor implements Processor {


    private static final Logger log = LoggerFactory.getLogger(FileMetaDataProcessor.class);
    private final String gcpTargetPath;
    private final  String bucketName;

    public FileMetaDataProcessor(String bucketName, String gcpTargetPath) {
        this.gcpTargetPath = gcpTargetPath;
        this.bucketName = bucketName;

    }


    @Override
    public void process(Exchange exchange) throws Exception {

        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);

        Long fileSize = exchange.getIn().getHeader(Exchange.FILE_LENGTH, Long.class);
        exchange.getIn().setHeader(GoogleCloudStorageConstants.CONTENT_LENGTH, fileSize);
        exchange.getIn().setHeader(GoogleCloudStorageConstants.OBJECT_NAME, gcpTargetPath + fileName);
        exchange.getIn().setHeader(GoogleCloudStorageConstants.BUCKET_NAME,bucketName);
        exchange.getIn().setHeader(GoogleCloudStorageConstants.FILE_NAME,fileName);
        long currentTimestamp = System.currentTimeMillis();
        Date date = new Date(currentTimestamp);
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        exchange.getIn().setHeader(GoogleCloudStorageConstants.METADATA_CREATE_TIME,formattedDate);
    }
}
