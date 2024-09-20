package com.sftp.file.service;


import com.hdfc.ef.secretManager.model.SecretConfigProps;
import com.hdfc.ef.secretManager.service.SecretProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecretManagerService {

    @Value("${gcp.secret.cacheName}")
    private String cacheName;

    @Autowired
    private SecretProvider secretProvider;

    public String getSecret(String projectId, String secretId, String versionId) throws Exception {

        var secretProp = SecretConfigProps.builder().projectId(projectId).secretId(secretId)
                .secretVersion(versionId).isGCP(Boolean.TRUE).build();
        return secretProvider.getSecret(secretProp, cacheName, secretProp.getSecretId());
    }

    public  String getSftpUsername(String projectId, String secretId, String versionId) throws Exception {
      return this.getSecret(projectId,secretId,versionId);
    }


}
