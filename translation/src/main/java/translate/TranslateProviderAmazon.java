package translate;

import java.io.IOException;
import java.net.URI;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import storage.Storage;

public class TranslateProviderAmazon implements TranslateProvider {

  private final Credentials credentials;
  private final Storage storage;
  private final Runtime runtime;
  private final Configuration configuration;

  private String serviceRegion;

  public TranslateProviderAmazon(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  public TranslateProviderAmazon(
      Credentials credentials,
      Runtime runtime,
      Storage storage,
      Configuration configuration,
      String serviceRegion) {
    this(credentials, runtime, storage, configuration);
    this.serviceRegion = serviceRegion;
  }

  @Override
  public TranslateResponse translate(String inputFile, String language) throws Exception {
    // select region where to run the service
    String serviceRegion = selectRegion();
    // read the input text
    String text = new String(storage.read(inputFile));
    // translate text
    long startTime = System.currentTimeMillis();
    TranslateClient translateClient = getTranslateClient(serviceRegion);
    TranslateTextRequest textRequest =
        TranslateTextRequest.builder()
            .sourceLanguageCode("auto")
            .targetLanguageCode(language)
            .text(text)
            .build();
    TranslateTextResponse textResponse = translateClient.translateText(textRequest);
    long endTime = System.currentTimeMillis();
    String translatedText = textResponse.translatedText();
    // return response
    return TranslateResponse.builder()
        .translateTime(endTime - startTime)
        .text(translatedText)
        .build();
  }

  private String selectRegion() {
    if (serviceRegion != null && !serviceRegion.isEmpty()) {
      return serviceRegion;
    }
    Provider functionProvider = runtime.getFunctionProvider();
    String functionRegion = runtime.getFunctionRegion();
    if (Provider.AWS.equals(functionProvider) && functionRegion != null) {
      // run in function region
      return functionRegion;
    }
    // run in default region
    return configuration.getDefaultRegionAws();
  }

  /** Create amazon translate client Java SDK V2 */
  public TranslateClient getTranslateClient(String region) throws IOException {
    return TranslateClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create("https://translate." + region + ".amazonaws.com/"))
        .credentialsProvider(credentials.getAwsCredentials())
        .build();
  }
}
