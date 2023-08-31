import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URI;

public class DownloadAmazon {

    public void download(String bucket, String key, String region, AwsCredentialsProvider credentials) throws IOException {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create("https://s3." + region + ".amazonaws.com/"))
                .credentialsProvider(credentials)
                .build();
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
        ResponseInputStream<GetObjectResponse> response = s3.getObject(getObjectRequest);
        byte[] data = response.readAllBytes();
        s3.close();
    }

}
