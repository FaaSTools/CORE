package translate;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.translate.v3.*;
import shared.Configuration;
import shared.Credentials;
import shared.Runtime;
import storage.Storage;

import java.io.IOException;

public class TranslateProviderGoogle implements TranslateProvider {

  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;

  public TranslateProviderGoogle(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  @Override
  public TranslateResponse translate(String inputFile, String language) throws Exception {
    // read the input text
    String text = new String(storage.read(inputFile));
    // translate text
    LocationName parent = LocationName.of(credentials.getGoogleProjectId(), "global");
    TranslateTextRequest request =
        TranslateTextRequest.newBuilder()
            .setParent(parent.toString())
            .setMimeType("text/plain")
            .setTargetLanguageCode(language)
            .addContents(text)
            .build();
    TranslationServiceClient translateClient = getTranslateClient();
    TranslateTextResponse response = translateClient.translateText(request);
    String translatedText = response.getTranslations(0).getTranslatedText();
    // return response
    return TranslateResponse.builder().text(translatedText).build();
  }

  public TranslationServiceClient getTranslateClient() throws IOException {
    TranslationServiceSettings settings =
        TranslationServiceSettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(credentials.getGcpCredentials()))
            .build();
    return TranslationServiceClient.create(settings);
  }
}
