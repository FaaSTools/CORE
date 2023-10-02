package ocr;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;

public class OcrService {

  private Configuration configuration;
  private Credentials credentials;

  public OcrService(Configuration configuration, Credentials credentials) {
    this.credentials = credentials;
    this.configuration = configuration;
  }

  private OcrResponse extract(OcrRequest request, OcrProvider ocrProvider) throws Exception {
    return ocrProvider.extract(request.getInputFile());
  }

  public OcrResponse extract(OcrRequest translateRequest, Provider provider) throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    OcrProviderFactory factory = new OcrProviderFactoryImpl(configuration, credentials, runtime);
    return this.extract(translateRequest, factory.getProvider(provider));
  }

  public OcrResponse extract(OcrRequest translateRequest, Provider provider, String serviceRegion)
      throws Exception {
    OcrProviderFactory factory =
        new OcrProviderFactoryImpl(configuration, credentials, new Runtime());
    return this.extract(translateRequest, factory.getProvider(provider, serviceRegion));
  }
}
