package translate;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.translate.v3.*;
import java.io.IOException;
import java.util.Optional;
import shared.Configuration;
import shared.Credentials;
import shared.Runtime;
import storage.Storage;

public class TranslateProviderGoogle implements TranslateProvider {

  private static final String ENDPOINT = "translate-%s.googleapis.com:443";
  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;
  private String serviceRegion;

  public TranslateProviderGoogle(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  public TranslateProviderGoogle(
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
    // read the input text
    String text = new String(storage.read(inputFile));
    // translate text
    LocationName parent = LocationName.of(credentials.getGoogleProjectId(), "global");
    TranslateTextRequest.Builder requestBuilder =
        TranslateTextRequest.newBuilder()
            .setParent(parent.toString())
            .setMimeType("text/plain")
            .setTargetLanguageCode(language)
            .addContents(text);
    if (serviceRegion != null && !serviceRegion.isEmpty()) {
      requestBuilder.setParent(
          LocationName.of(credentials.getGoogleProjectId(), serviceRegion).toString());
    }
    TranslationServiceClient translateClient = getTranslateClient();
    TranslateTextResponse response = translateClient.translateText(requestBuilder.build());
    String translatedText = response.getTranslations(0).getTranslatedText();
    // return response
    return TranslateResponse.builder().text(translatedText).build();
  }

  public TranslationServiceClient getTranslateClient() throws IOException {
    TranslationServiceSettings.Builder builder =
        TranslationServiceSettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(
                    Optional.ofNullable(credentials.getGcpClientCredentials())
                        .orElse(credentials.getGcpCredentials())));
    if (serviceRegion != null) {
      if (serviceRegion.startsWith("eu")) {
        builder.setEndpoint(String.format(ENDPOINT, "eu"));
      } else if (serviceRegion.startsWith("us")) {
        builder.setEndpoint(String.format(ENDPOINT, "us"));
      }
    }
    return TranslationServiceClient.create(builder.build());
  }
}
