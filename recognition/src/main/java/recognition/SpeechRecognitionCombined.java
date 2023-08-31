package recognition;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;

public class SpeechRecognitionCombined implements SpeechRecognition {

  private Credentials credentials;
  private Runtime runtime;
  private Configuration configuration;

  public SpeechRecognitionCombined(
      Credentials credentials, Runtime runtime, Configuration configuration) {
    this.credentials = credentials;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  @Override
  public SpeechRecognitionResponse recognizeSpeech(
      String inputFile,
      int sampleRate,
      String languageCode,
      int channelCount,
      boolean srtSubtitles,
      boolean vttSubtitles,
      boolean profanityFilter,
      boolean spokenEmoji,
      boolean spokenPunctuation)
      throws Exception {
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition amazonSpeechRecognition = factory.getS2TProvider(Provider.AWS);
    SpeechRecognition googleSpeechRecognition = factory.getS2TProvider(Provider.GCP);
    SpeechRecognitionResponse amazonResult =
        amazonSpeechRecognition.recognizeSpeech(
            inputFile,
            sampleRate,
            languageCode,
            channelCount,
            srtSubtitles,
            vttSubtitles,
            profanityFilter,
            spokenEmoji,
            spokenPunctuation);
    SpeechRecognitionResponse googleResult =
        googleSpeechRecognition.recognizeSpeech(
            inputFile,
            sampleRate,
            languageCode,
            channelCount,
            srtSubtitles,
            vttSubtitles,
            profanityFilter,
            spokenEmoji,
            spokenPunctuation);
    googleResult.setSrtSubtitles(amazonResult.getSrtSubtitles());
    googleResult.setVttSubtitles(amazonResult.getVttSubtitles());
    return googleResult;
  }
}
