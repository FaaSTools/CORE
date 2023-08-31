package translate;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import storage.Storage;

import java.io.IOException;
import java.net.URI;

public class TranslateProviderAmazon implements TranslateProvider {

  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;

  public TranslateProviderAmazon(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  @Override
  public TranslateResponse translate(String inputFile, String language) throws Exception {
    // select region where to run the service
    String serviceRegion = selectRegion();
    // read the input text
    String text = new String(storage.read(inputFile));
    // translate text
    TranslateClient translateClient = getTranslateClient(serviceRegion);
    TranslateTextRequest textRequest =
        TranslateTextRequest.builder()
            .sourceLanguageCode("auto")
            .targetLanguageCode(language)
            .text(text)
            .build();
    TranslateTextResponse textResponse = translateClient.translateText(textRequest);
    String translatedText = textResponse.translatedText();
    // return response
    return TranslateResponse.builder().text(translatedText).build();
  }

  private String selectRegion() {
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
