package io.github.hossensyedriadh.inventrackrestfulservice.configuration.cloud;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Log4j
@Component
@PropertySource("classpath:application.properties")
public class OracleCloudObjectStorage {
    @Value("${oracle-cloud.config.file-path}")
    private String configFilePath;

    @Value("${oracle-cloud.namespace}")
    private String namespace;

    @Value("${oracle-cloud.object-storage.bucket-name}")
    private String bucketName;

    @SuppressWarnings("all")
    public String uploadFile(@NonNull String folderName, @NonNull String objectName, @NonNull HashMap<String, String> fileMetadata,
                             @NonNull MultipartFile multipartFile) {
        fileMetadata.put("Upload Time", String.valueOf(LocalDateTime.now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"))));

        try {
            File temp = new File(System.getProperty("java.io.tmpdir") + "/" + objectName);
            multipartFile.transferTo(temp);

            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(this.configFilePath);
            final ConfigFileAuthenticationDetailsProvider authenticationDetailsProvider = new ConfigFileAuthenticationDetailsProvider(configFile);

            try (ObjectStorage storage = new ObjectStorageClient(authenticationDetailsProvider)) {
                storage.setRegion(Region.AP_SINGAPORE_1);

                UploadConfiguration uploadConfiguration = UploadConfiguration.builder()
                        .allowMultipartUploads(true).allowParallelUploads(false).build();

                UploadManager uploadManager = new UploadManager(storage, uploadConfiguration);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucketName(this.bucketName).namespaceName(this.namespace)
                        .objectName(folderName + "/" + objectName).contentType(multipartFile.getContentType())
                        .opcMeta(fileMetadata).build();

                UploadManager.UploadRequest uploadRequest = UploadManager.UploadRequest.builder(temp).allowOverwrite(true)
                        .build(putObjectRequest);
                UploadManager.UploadResponse response = uploadManager.upload(uploadRequest);

                log.info(response.toString());

                temp.delete();

                return storage.getEndpoint().concat("/n/").concat(this.namespace).concat("/b/").concat(this.bucketName)
                        .concat("/o/").concat(folderName).concat("/").concat(objectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to upload object to Oracle Cloud Object Storage", e);
        }

        return null;
    }
}
