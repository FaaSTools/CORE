package synthesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import shared.*;
import shared.Runtime;
import storage.Storage;

import java.io.IOException;

public class SpeechSynthesisGoogle implements SpeechSynthesis {

  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;

  public SpeechSynthesisGoogle(
      Credentials credentials, Storage storage, Configuration configuration, Runtime runtime) {
    this.credentials = credentials;
    this.storage = storage;
    this.configuration = configuration;
    this.runtime = runtime;
  }

  @Override
  public SpeechSynthesisResponse synthesizeSpeech(
      String inputFile,
      String language,
      TextType textType,
      Gender gender,
      AudioFormat audioFormat)
      throws Exception {
    // read input text
    String text = new String(storage.read(inputFile));
    // get voice for language and gender
    VoiceGoogle voice = getVoice(language, "standard", gender.name().toLowerCase());
    SsmlVoiceGender ssmlVoiceGender =
        gender.equals(Gender.MALE) ? SsmlVoiceGender.MALE : SsmlVoiceGender.FEMALE;
    VoiceSelectionParams voiceSelectionParams =
        VoiceSelectionParams.newBuilder()
            .setName(voice.getName())
            .setLanguageCode(language)
            .setSsmlGender(ssmlVoiceGender)
            .build();
    // create synthesis input
    SynthesisInput synthesisInput;
    if (textType.equals(TextType.PLAIN_TEXT)) {
      synthesisInput = SynthesisInput.newBuilder().setText(text).build();
    } else {
      synthesisInput = SynthesisInput.newBuilder().setSsml(text).build();
    }
    // create audio config
    AudioConfig audioConfig =
        AudioConfig.newBuilder()
            .setAudioEncoding(getAudioEncoding(audioFormat))
            .setSampleRateHertz(16000)
            .build();
    long startSynthesis = System.currentTimeMillis();
    TextToSpeechClient textToSpeechClient = getGoogleT2sClient();
    ByteString byteString =
        textToSpeechClient
            .synthesizeSpeech(synthesisInput, voiceSelectionParams, audioConfig)
            .getAudioContent();
    byte[] audio = byteString.toByteArray();
    long endSynthesis = System.currentTimeMillis();
    return SpeechSynthesisResponse.builder()
            .provider(Provider.GCP)
            .audio(audio)
            .synthesisTime(endSynthesis - startSynthesis)
            .build();
  }

  private VoiceGoogle getVoice(String languageCode, String engine, String gender)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    VoiceGoogle[] voices =
        mapper.readValue(
            this.getClass().getResourceAsStream("gcp_voices.json"), VoiceGoogle[].class);
    for (VoiceGoogle voice : voices) {
      if (voice.getLanguage().equals(languageCode)
          && voice.getSupportedEngine().equalsIgnoreCase(engine)
          && voice.getSsmlVoiceGender().equalsIgnoreCase(gender)) {
        return voice;
      }
    }
    return null;
  }

  private AudioEncoding getAudioEncoding(AudioFormat audioFormat) {
    switch (audioFormat) {
      case MP3:
        return AudioEncoding.MP3;
      case PCM:
        return AudioEncoding.LINEAR16;
      case OGG_OPUS:
        return AudioEncoding.OGG_OPUS;
      case MULAW:
        return AudioEncoding.MULAW;
      case ALAW:
        return AudioEncoding.ALAW;
      default:
        break;
    }
    return null;
  }

  /** Create Google Text2Speech Client */
  private TextToSpeechClient getGoogleT2sClient() throws IOException {
    TextToSpeechSettings settings =
        TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(credentials.getGcpCredentials()))
            .build();
    return TextToSpeechClient.create(settings);
  }
}
