import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

public class UploadAmazon {

    public void upload(byte[] data, String bucket, String key, String region, AwsCredentialsProvider credentials) throws IOException {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create("https://s3." + region + ".amazonaws.com/"))
                .credentialsProvider(credentials)
                .build();
        PutObjectRequest objectRequest =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
        s3.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(data)));
        s3.close();
    }
}
