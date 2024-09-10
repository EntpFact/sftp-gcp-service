
package com.sftp.file.controller;

import com.sftp.file.service.GCPBucketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sftp")
@Slf4j
public class SFTPTestController {

    @Autowired
    private GCPBucketService gcpBucketService;

    @PostMapping("/publishDataToKafka")
    public ResponseEntity<String> publishDataToKafka(@RequestBody Map<String, Object> payload) throws Exception {
        try {

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            gcpBucketService.publishFileMetadataToKafk(data);
            return ResponseEntity.ok("Publish Data Successfully to Kafka Topic");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}

