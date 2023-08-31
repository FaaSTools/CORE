package storage;

import java.io.IOException;
import java.util.List;

public interface StorageProvider {

  byte[] read(String fileUrl) throws Exception;

  void write(byte[] data, String fileUrl) throws Exception;

  boolean delete(String fileUrl) throws IOException;

  String createBucket(String bucketName, String region) throws IOException;

  String deleteBucket(String bucketName, String region) throws IOException;

  /**
   * Retrieve the location where the storage bucket resides. First, the bucket url is parsed, if it
   * can not be parsed from the bucket url, the providers API is used to query the bucket location.
   *
   * @param bucketUrl The url of the storage bucket.
   * @return The location of the storage bucket. For AWS, this is the region code. For GCP, this is
   *     the single-region or multi-region code. For Azure, this is the name of the storage account,
   *     which is bound to a specific region.
   */
  String getRegion(String bucketUrl) throws IOException;

  List<String> listFiles(String bucketUrl) throws IOException;

}
