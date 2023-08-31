package storage;

import shared.Credentials;
import shared.Provider;

public class StorageProviderFactoryImpl implements StorageProviderFactory {

  private Credentials credentials;

  public StorageProviderFactoryImpl(Credentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public StorageProvider getStorageProvider(FileInfo fileInfo) {
    if (!fileInfo.isLocal()) {
      return getStorageProvider(fileInfo.getBucketInfo());
    }
    return null;
  }

  @Override
  public StorageProvider getStorageProvider(BucketInfo bucketInfo) {
    if (bucketInfo.getProvider() != null) {
      return getStorageProvider(bucketInfo.getProvider());
    }
    return null;
  }

  @Override
  public StorageProvider getStorageProvider(Provider provider) {
    if (provider.equals(Provider.AWS)) {
      return new StorageProviderAmazon(credentials);
    } else if (provider.equals(Provider.GCP)) {
      return new StorageProviderGoogle(credentials);
    }
    return null;
  }
}
