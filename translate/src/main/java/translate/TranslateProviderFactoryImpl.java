package translate;

import java.io.IOException;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.Storage;
import storage.StorageImpl;

public class TranslateProviderFactoryImpl implements TranslateProviderFactory {

  private Configuration configuration;
  private Credentials credentials;
  private Runtime runtime;

  public TranslateProviderFactoryImpl(
          Configuration configuration, Credentials credentials, Runtime runtime) {
    this.configuration = configuration;
    this.credentials = credentials;
    this.runtime = runtime;
  }

  @Override
  public TranslateProvider getProvider(Provider provider) throws IOException {
    Storage storage = new StorageImpl(credentials);
    if (provider.equals(Provider.AWS)){
      return new TranslateProviderAmazon(credentials, runtime, storage, configuration);
    }
    if (provider.equals(Provider.GCP)){
      return new TranslateProviderGoogle(credentials, runtime, storage, configuration);
    }
    throw new RuntimeException("Failed to initialize translate provider.");
  }

  @Override
  public TranslateProvider getProvider(Provider provider, String region) throws IOException {
    Storage storage = new StorageImpl(credentials);
    if (provider.equals(Provider.AWS)) {
      return new TranslateProviderAmazon(credentials, runtime, storage, configuration, region);
    }
    if (provider.equals(Provider.GCP)) {
      return new TranslateProviderGoogle(credentials, runtime, storage, configuration, region);
    }
    throw new RuntimeException("Failed to initialize translate provider.");
  }
}
