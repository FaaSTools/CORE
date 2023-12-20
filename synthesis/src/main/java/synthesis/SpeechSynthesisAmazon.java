package synthesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;
import storage.BucketInfo;
import storage.Storage;

public class SpeechSynthesisAmazon implements SpeechSynthesis {

  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;
  private BucketInfo tmpOutputBucket;
  private String serviceRegion;

  public SpeechSynthesisAmazon(
      Credentials credentials, Storage storage, Configuration configuration, Runtime runtime) {
    this.credentials = credentials;
    this.storage = storage;
    this.configuration = configuration;
    this.runtime = runtime;
  }

  public SpeechSynthesisAmazon(
      Credentials credentials,
      Storage storage,
      Configuration configuration,
      Runtime runtime,
      String serviceRegion) {
    this(credentials, storage, configuration, runtime);
    this.serviceRegion = serviceRegion;
  }

  @Override
  public SpeechSynthesisResponse synthesizeSpeech(
      String inputFile, String language, TextType textType, Gender gender, AudioFormat audioFormat)
      throws Exception {
    try {
      // select region where to run the service
      if (serviceRegion != null && !serviceRegion.isEmpty()) {
        serviceRegion = selectRegionSync();
      }
      long startDownload = System.currentTimeMillis();
      // read the input text
      String text = new String(storage.read(inputFile));
      long downloadTime = System.currentTimeMillis() - startDownload;
      // get voice for language and gender
      VoiceAmazon voice = getVoice(language, Engine.STANDARD, gender.name().toLowerCase());
      // create request
      SynthesizeSpeechRequest synthesizeSpeechRequest =
          SynthesizeSpeechRequest.builder()
              .text(text)
              .voiceId(voice.getId())
              .textType(getTextType(textType))
              .outputFormat(getOutputFormat(audioFormat))
              .engine(Engine.STANDARD)
              .sampleRate(Integer.toString(16000))
              .build();
      // invoke service
      long startSynthesis = System.currentTimeMillis();
      PollyClient pollyClient = getPollyClient(serviceRegion);
      ResponseInputStream<SynthesizeSpeechResponse> in =
          pollyClient.synthesizeSpeech(synthesizeSpeechRequest);
      byte[] audio = in.readAllBytes();
      long endSynthesis = System.currentTimeMillis();
      return SpeechSynthesisResponse.builder()
          .provider(Provider.AWS)
          .audio(audio)
          .synthesisTime(endSynthesis - startSynthesis)
          .downloadTime(downloadTime)
          .build();
    } finally {
      serviceRegion = null;
    }
  }

  private VoiceAmazon getVoice(String languageCode, Engine engine, String gender)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    VoiceAmazon[] voices =
        mapper.readValue(
            this.getClass().getResourceAsStream("aws_voices.json"), VoiceAmazon[].class);
    for (VoiceAmazon voice : voices) {
      if (voice.getLanguageCode().equalsIgnoreCase(languageCode)
          && voice.getSupportedEngines().contains(engine.toString().toLowerCase())
          && voice.getGender().equalsIgnoreCase(gender)) {
        return voice;
      }
    }
    return null;
  }

  private OutputFormat getOutputFormat(AudioFormat audioFormat) {
    switch (audioFormat) {
      case MP3:
        return OutputFormat.MP3;
      case OGG_VORBIS:
        return OutputFormat.OGG_VORBIS;
      case PCM:
        return OutputFormat.PCM;
      default:
        break;
    }
    return null;
  }

  private software.amazon.awssdk.services.polly.model.TextType getTextType(TextType textType) {
    switch (textType) {
      case PLAIN_TEXT:
        return software.amazon.awssdk.services.polly.model.TextType.TEXT;
      case SSML:
        return software.amazon.awssdk.services.polly.model.TextType.SSML;
      default:
        break;
    }
    return null;
  }

  private String selectRegionSync() {
    Provider functionProvider = runtime.getFunctionProvider();
    String functionRegion = runtime.getFunctionRegion();
    if (Provider.AWS.equals(functionProvider) && functionRegion != null) {
      // run in function region
      return functionRegion;
    }
    // run in default region
    return configuration.getDefaultRegionAws();
  }

  /** Create amazon polly client Java SDK V2 */
  private PollyClient getPollyClient(String region) throws IOException {
    return PollyClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create("https://polly." + region + ".amazonaws.com/"))
        .credentialsProvider(credentials.getAwsCredentials())
        .build();
  }
}
