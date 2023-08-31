package storage;

import shared.Provider;

import java.io.IOException;
import java.util.List;

public interface Storage {

  /**
   * Read a file.
   *
   * @param fileUrl The url of the file to read. Can point to the local filesystem, AWS S3, Google
   *     Cloud Storage or Azure Blob Storage.
   * @throws Exception If the file does not exist or if the url can not be parsed.
   * @return The file contents as byte array.
   */
  byte[] read(String fileUrl) throws Exception;

  /**
   * Write data to a file. Creates a new file if it does not exist and overwrites a file if it
   * already exists.
   *
   * @param data The content that should be written to the file as byte array.
   * @param fileUrl The url of the file to read. Can point to the local filesystem, AWS S3, Google
   *     Cloud Storage or Azure Blob Storage.
   * @throws Exception If the url can not be parsed.
   */
  void write(byte[] data, String fileUrl) throws Exception;

  /**
   * Delete a file.
   *
   * @param fileUrl The url of the file to read. Can point to the local filesystem, AWS S3, Google
   *     Cloud Storage or Azure Blob Storage.
   * @throws Exception If the url can not be parsed.
   * @return True if the file was deleted and false if the file did not exist.
   */
  boolean delete(String fileUrl) throws IOException;

  /**
   * Create a new bucket.
   *
   * @param provider The bucket provider.
   * @param bucketName The bucket name.
   * @param region The region code where the bucket is created. For AWS this is the single-region
   *     code. For GCP it could be either single-region or multi-region code.
   * @throws IOException If the bucket already exists.
   * @return The name of the new bucket.
   */
  String createBucket(Provider provider, String bucketName, String region) throws Exception;

  /**
   * Delete an existing storage bucket. If the bucket is not empty, all files are deleted before
   * deleting the actual bucket itself.
   *
   * @param provider The bucket provider.
   * @param bucketName The bucket name.
   * @param region The exact region code where the bucket is located.
   * @throws IOException If the bucket does not exist.
   * @return The name of the deleted bucket.
   */
  String deleteBucket(Provider provider, String bucketName, String region) throws IOException;

  /**
   * Obtain the region code of an existing bucket. For AWS, this will return the single-region code
   * (e.g. us-east-1). For GCP, this will return the multi-region code. The multi-region code for
   * all regions in europe is "eu", while the multi-region code for all regions in the united-states
   * is "us". For all other regions null is returned.
   *
   * @param bucketUrl The url of the bucket with trailing slash at the end.
   * @throws IOException If the bucket does not exist.
   * @return For AWS the single-region code. For GCP either us, eu or null.
   */
  String getRegion(String bucketUrl) throws IOException;

  List<String> listFiles(String bucketUrl) throws IOException;
}
