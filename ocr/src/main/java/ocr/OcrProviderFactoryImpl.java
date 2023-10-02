package ocr;

import java.io.IOException;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.Storage;
import storage.StorageImpl;

public class OcrProviderFactoryImpl implements OcrProviderFactory {

    private Configuration configuration;
    private Credentials credentials;
    private Runtime runtime;

    public OcrProviderFactoryImpl(
            Configuration configuration, Credentials credentials, Runtime runtime) {
        this.configuration = configuration;
        this.credentials = credentials;
        this.runtime = runtime;
    }

    @Override
    public OcrProvider getProvider(Provider provider) throws IOException {
        Storage storage = new StorageImpl(credentials);
        if (provider.equals(Provider.AWS)) {
            return new OcrProviderAmazon(credentials, runtime, storage, configuration);
        }
        if (provider.equals(Provider.GCP)) {
            return new OcrProviderGoogle(credentials, runtime, storage, configuration);
        }
        throw new RuntimeException("Failed to initialize translate provider.");
    }

  @Override
  public OcrProvider getProvider(Provider provider, String serviceRegion) throws IOException {
    Storage storage = new StorageImpl(credentials);
    if (provider.equals(Provider.AWS)) {
      return new OcrProviderAmazon(credentials, runtime, storage, configuration, serviceRegion);
    }
    if (provider.equals(Provider.GCP)) {
      return new OcrProviderGoogle(credentials, runtime, storage, configuration, serviceRegion);
    }
    throw new RuntimeException("Failed to initialize translate provider.");
  }
}
