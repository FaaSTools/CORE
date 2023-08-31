import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.nio.file.Paths;

public class DownloadGoogle {

    public void download(
            String projectId, String bucketName, String objectName, String destFilePath, GoogleCredentials credentials) throws IOException {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build();
        Storage storage = storageOptions.getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);
        blob.downloadTo(Paths.get(destFilePath));
    }

}
