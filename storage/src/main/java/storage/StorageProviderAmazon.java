package storage;

import shared.Credentials;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public class StorageProviderAmazon implements StorageProvider {

  private Credentials credentials;

  public StorageProviderAmazon(Credentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public byte[] read(String fileUrl) throws Exception {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    String region = getRegion(fileInfo.getBucketInfo().getBucketUrl());
    S3Client s3 = getAmazonS3Client(credentials, region);
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder()
            .bucket(fileInfo.getBucketInfo().getBucketName())
            .key(fileInfo.getFileName())
            .build();
    ResponseInputStream<GetObjectResponse> response = s3.getObject(getObjectRequest);
    byte[] data = response.readAllBytes();
    s3.close();
    return data;
  }

  @Override
  public void write(byte[] data, String fileUrl) throws Exception {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    String region = getRegion(fileInfo.getBucketInfo().getBucketUrl());
    S3Client s3 = getAmazonS3Client(credentials, region);
    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket(fileInfo.getBucketInfo().getBucketName())
            .key(fileInfo.getFileName())
            .build();
    s3.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(data)));
    s3.close();
  }

  @Override
  public boolean delete(String fileUrl) throws IOException {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    String region = getRegion(fileInfo.getBucketInfo().getBucketUrl());
    try (S3Client s3 = getAmazonS3Client(credentials, region)) {
      DeleteObjectRequest deleteObjectRequest =
          DeleteObjectRequest.builder()
              .bucket(fileInfo.getBucketInfo().getBucketName())
              .key(fileInfo.getFileName())
              .build();
      s3.deleteObject(deleteObjectRequest);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public String createBucket(String bucketName, String region) throws IOException {
    S3Client s3 = getAmazonS3Client(credentials, region);
    CreateBucketRequest createBucketRequest =
        CreateBucketRequest.builder().bucket(bucketName).build();
    s3.createBucket(createBucketRequest);
    s3.close();
    return bucketName;
  }

  @Override
  public String deleteBucket(String bucketName, String region) throws IOException {
    // init aws s3 client
    S3Client s3 = getAmazonS3Client(credentials, region);
    // delete all files from the bucket if necessary
    ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();
    ListObjectsV2Iterable list = s3.listObjectsV2Paginator(request);
    for (ListObjectsV2Response response : list) {
      List<S3Object> objects = response.contents();
      if (objects.size() > 0) {
        List<ObjectIdentifier> objectIdentifiers =
            objects.stream()
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .collect(Collectors.toList());
        DeleteObjectsRequest deleteObjectsRequest =
            DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objectIdentifiers).build())
                .build();
        s3.deleteObjects(deleteObjectsRequest);
      }
    }
    // delete the actual bucket itself
    DeleteBucketRequest deleteBucketRequest =
        DeleteBucketRequest.builder().bucket(bucketName).build();
    s3.deleteBucket(deleteBucketRequest);
    s3.close();
    return bucketName;
  }

  @Override
  public String getRegion(String bucketUrl) throws IOException {
    // try to parse the region from the bucket url
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    if (bucketInfo.getRegion() != null) {
      return bucketInfo.getRegion();
    }
    // query the region if it cannot be parsed from the url
    S3Client s3 = getAmazonS3Client(credentials, "us-east-1");
    GetBucketLocationResponse response =
        s3.getBucketLocation(
            GetBucketLocationRequest.builder().bucket(bucketInfo.getBucketName()).build());
    String locationConstraint = response.locationConstraint().toString();
    if (locationConstraint == "null") {
      return "us-east-1";
    }
    return locationConstraint;
  }

  @Override
  public List<String> listFiles(String bucketUrl) throws IOException {
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    String region = getRegion(bucketUrl);
    S3Client s3 = getAmazonS3Client(credentials, region);
    ListObjectsRequest listObjects =
        ListObjectsRequest.builder().bucket(bucketInfo.getBucketName()).build();
    ListObjectsResponse res = s3.listObjects(listObjects);
    List<S3Object> objects = res.contents();
    List<String> fileKeys = objects.stream().map(o -> o.key()).collect(Collectors.toList());
    return fileKeys;
  }

  /** Create amazon S3 client of SDK V2 */
  private static S3Client getAmazonS3Client(Credentials credentials, String region) {
    return S3Client.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create("https://s3." + region + ".amazonaws.com/"))
        .credentialsProvider(credentials.getAwsCredentials())
        .build();
  }
}
