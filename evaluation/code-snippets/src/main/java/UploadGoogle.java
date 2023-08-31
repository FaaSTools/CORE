import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.nio.file.Paths;

public class UploadGoogle {

    public static void upload(
            String projectId, String bucketName, String objectName, String filePath, GoogleCredentials credentials) throws IOException {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build();
        Storage storage = storageOptions.getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.createFrom(blobInfo, Paths.get(filePath));
    }

}
