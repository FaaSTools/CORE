package storage;

import shared.Provider;

public interface StorageProviderFactory {

  /**
   * Initialize the storage provider based on the location of the file. Return null if the file is
   * not stored in cloud storage.
   */
  StorageProvider getStorageProvider(FileInfo fileInfo);

  /**
   * Initialize the storage provider based on the location of the bucket. Return null if the bucket
   * provider can not be determined.
   */
  StorageProvider getStorageProvider(BucketInfo bucketInfo);

  /** Initialize a specific storage provider. */
  StorageProvider getStorageProvider(Provider provider);
}
