package com.sftp.file.util;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

public class SecretManagerUtil {

    public static String getSecret(String projectId, String secretId, String versionId) throws Exception {

        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        }
    }

    public static String getSftpUsername(String projectId, String secretId, String versionId) throws Exception {
        return getSecret(projectId, secretId, versionId);
    }


}
