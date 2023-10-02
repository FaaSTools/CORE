package recognition;

import shared.*;
import shared.Runtime;

public class SpeechRecognizer {

  private Configuration configuration;
  private Credentials credentials;

  public SpeechRecognizer(Configuration configuration, Credentials credentials) {
    this.credentials = credentials;
    this.configuration = configuration;
  }

  private SpeechRecognitionResponse recognizeSpeech(
      SpeechRecognitionRequest speechRecognitionRequest, SpeechRecognition recognizer)
      throws Exception {
    // invoke the service
    return recognizer.recognizeSpeech(
        speechRecognitionRequest.getInputFile(),
        speechRecognitionRequest.getSampleRate(),
        speechRecognitionRequest.getLanguageCode(),
        speechRecognitionRequest.getChannelCount(),
        false,
        false,
        false,
        false,
        false);
  }

  /** Provider is explicitly selected. */
  public SpeechRecognitionResponse recognizeSpeech(
      SpeechRecognitionRequest speechRecognitionRequest, Provider provider) throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition speechRecognizer = factory.getS2TProvider(provider);
    // invoke the service
    return this.recognizeSpeech(speechRecognitionRequest, speechRecognizer);
  }

  /**
   * Same as {@link #recognizeSpeech(SpeechRecognitionRequest, Provider) but allows to specify the
   * region as well}
   */
  public SpeechRecognitionResponse recognizeSpeech(
      SpeechRecognitionRequest speechRecognitionRequest, Provider provider, String region)
      throws Exception {
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition speechRecognizer = factory.getS2TProvider(provider, region);
    // invoke the service
    return this.recognizeSpeech(speechRecognitionRequest, speechRecognizer);
  }

  /**
   * Provider is selected based on the features. If needed, the service is invoked on both providers
   * and the result is merged.
   */
  public SpeechRecognitionResponse recognizeSpeech(
      SpeechRecognitionRequest speechRecognitionRequest,
      SpeechRecognitionFeatures speechRecognitionFeatures)
      throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition speechRecognizer = factory.getS2TProvider(speechRecognitionFeatures);
    // invoke the service
    SpeechRecognitionResponse response =
        speechRecognizer.recognizeSpeech(
            speechRecognitionRequest.getInputFile(),
            speechRecognitionRequest.getSampleRate(),
            speechRecognitionRequest.getLanguageCode(),
            speechRecognitionRequest.getChannelCount(),
            speechRecognitionFeatures.isSrtSubtitles(),
            speechRecognitionFeatures.isVttSubtitles(),
            speechRecognitionFeatures.isProfanityFilter(),
            speechRecognitionFeatures.isSpokenEmoji(),
            speechRecognitionFeatures.isSpokenPunctuation());
    return response;
  }

  /** Provider is selected based on the location of the input. */
  public SpeechRecognitionResponse recognizeSpeech(
      SpeechRecognitionRequest speechRecognitionRequest) throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition provider = factory.getS2TProvider(speechRecognitionRequest.getInputFile());
    // invoke the service
    return this.recognizeSpeech(speechRecognitionRequest, provider);
  }
}
