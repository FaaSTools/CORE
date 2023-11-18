package storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import shared.Provider;

class BucketInfoTest {

  @Test
  public void shouldIdentifyBucketInfoFromSimpleAwsUrl() {
    String url = "https://baasless-input-us-e1.s3.amazonaws.com";
    BucketInfo bucketInfo = BucketInfo.parse(url);
    assertEquals("baasless-input-us-e1", bucketInfo.getBucketName());
    assertEquals(Provider.AWS, bucketInfo.getProvider());
    assertEquals(url, bucketInfo.getBucketUrl());
    assertEquals("us-east-1", bucketInfo.getRegion());
  }

  @Test
  public void shouldIdentifyBucketInfoFromSimpleAwsUrlWithRegion() {
    String url = "https://baasless-input-us-e1.s3.us-east-1.amazonaws.com";
    BucketInfo bucketInfo = BucketInfo.parse(url);
    assertEquals("baasless-input-us-e1", bucketInfo.getBucketName());
    assertEquals(Provider.AWS, bucketInfo.getProvider());
    assertEquals(url, bucketInfo.getBucketUrl());
    assertEquals("us-east-1", bucketInfo.getRegion());
  }

  @Test
  public void shouldIdentifyBucketInfoFromSimpleAwsFileUrl() {
    String url = "https://baasless-input-us-e1.s3.amazonaws.com/sample-1.wav";
    BucketInfo bucketInfo = BucketInfo.parse(url);
    assertEquals("baasless-input-us-e1", bucketInfo.getBucketName());
    assertEquals(Provider.AWS, bucketInfo.getProvider());
    assertEquals(url, bucketInfo.getBucketUrl());
    assertEquals("us-east-1", bucketInfo.getRegion());
  }

  @Test
  public void shouldIdentifyBucketNameFromSimpleGcpUrl() {
    String url = "https://storage.cloud.google.com/europe-west1-intents/";
    BucketInfo bucketInfo = BucketInfo.parse(url);
    assertEquals("europe-west1-intents", bucketInfo.getBucketName());
    assertEquals(Provider.GCP, bucketInfo.getProvider());
    assertEquals(url, bucketInfo.getBucketUrl());
    assertNull(bucketInfo.getRegion());
  }

  @Test
  public void shouldIdentifyBucketNameFromSimpleGcpFileUrl() {
    String url = "https://storage.cloud.google.com/europe-west1-intents/sample-1.wav";
    BucketInfo bucketInfo = BucketInfo.parse(url);
    assertEquals("europe-west1-intents", bucketInfo.getBucketName());
    assertEquals(Provider.GCP, bucketInfo.getProvider());
    assertEquals(url, bucketInfo.getBucketUrl());
    assertNull(bucketInfo.getRegion());
  }
}
