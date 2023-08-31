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

  /** Provider is explicitly selected. */
  public SpeechRecognitionResponse recognizeSpeech(
          SpeechRecognitionRequest speechRecognitionRequest, Provider provider) throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
            new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition speechRecognizer = factory.getS2TProvider(provider);
    // invoke the service
    SpeechRecognitionResponse response =
            speechRecognizer.recognizeSpeech(
                    speechRecognitionRequest.getInputFile(),
                    speechRecognitionRequest.getSampleRate(),
                    speechRecognitionRequest.getLanguageCode(),
                    speechRecognitionRequest.getChannelCount(),
                    false,
                    false,
                    false,
                    false,
                    false);
    return response;
  }

  /**
   * Provider is selected based on the features. If needed, the service is invoked on both
   * providers and the result is merged.
   */
  public SpeechRecognitionResponse recognizeSpeech(
          SpeechRecognitionRequest speechRecognitionRequest, SpeechRecognitionFeatures speechRecognitionFeatures)
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
  public SpeechRecognitionResponse recognizeSpeech(SpeechRecognitionRequest speechRecognitionRequest)
      throws Exception {
    // select provider
    Runtime runtime = new Runtime();
    SpeechRecognitionFactoryImpl factory =
        new SpeechRecognitionFactoryImpl(configuration, credentials, runtime);
    SpeechRecognition provider =
        factory.getS2TProvider(speechRecognitionRequest.getInputFile());
    // invoke the service
    SpeechRecognitionResponse response =
        provider.recognizeSpeech(
            speechRecognitionRequest.getInputFile(),
            speechRecognitionRequest.getSampleRate(),
            speechRecognitionRequest.getLanguageCode(),
            speechRecognitionRequest.getChannelCount(),
            false,
            false,
            false,
            false,
            false);
    return response;
  }

}
