package storage;

import shared.Credentials;
import shared.Provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StorageImpl implements Storage {

  private Credentials credentials;

  public StorageImpl(Credentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public byte[] read(String fileUrl) throws Exception {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    if (fileInfo.isLocal()) {
      // file is stored on the local filesystem
      FileInputStream in = new FileInputStream(fileUrl);
      return in.readAllBytes();
    }
    // file is stored in cloud storage
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider provider = factory.getStorageProvider(fileInfo);
    return provider.read(fileUrl);
  }

  @Override
  public void write(byte[] data, String fileUrl) throws Exception {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    if (fileInfo.isLocal()) {
      // file is stored on the local filesystem
      FileOutputStream out = new FileOutputStream(fileUrl);
      out.write(data);
      return;
    }
    // file is stored in cloud storage
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider provider = factory.getStorageProvider(fileInfo);
    provider.write(data, fileUrl);
  }

  @Override
  public boolean delete(String fileUrl) throws IOException {
    FileInfo fileInfo = FileInfo.parse(fileUrl);
    if (fileInfo.isLocal()) {
      // file is stored on the local filesystem
      File file = new File(fileUrl);
      return file.delete();
    }
    // file is stored in cloud storage
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider provider = factory.getStorageProvider(fileInfo);
    return provider.delete(fileUrl);
  }

  @Override
  public String createBucket(Provider provider, String bucketName, String region) throws Exception {
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider storageProvider = factory.getStorageProvider(provider);
    storageProvider.createBucket(bucketName, region);
    return bucketName;
  }

  @Override
  public String deleteBucket(Provider provider, String bucketName, String region)
      throws IOException {
    try {
      StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
      StorageProvider storageProvider = factory.getStorageProvider(provider);
      storageProvider.deleteBucket(bucketName, region);
      return bucketName;
    } catch (NullPointerException e) {
      throw new NullPointerException("Bucket does not exist.");
    }
  }

  @Override
  public String getRegion(String bucketUrl) throws IOException {
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider storageProvider = factory.getStorageProvider(bucketInfo.getProvider());
    String region = storageProvider.getRegion(bucketInfo.getBucketUrl());
    return region;
  }

  @Override
  public List<String> listFiles(String bucketUrl) throws IOException {
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    StorageProviderFactory factory = new StorageProviderFactoryImpl(credentials);
    StorageProvider storageProvider = factory.getStorageProvider(bucketInfo.getProvider());
    return storageProvider.listFiles(bucketUrl);
  }

}
