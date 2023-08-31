package recognition;

import shared.Provider;

import java.io.IOException;

public interface SpeechRecognitionFactory {

  SpeechRecognition getS2TProvider(String inputFile) throws IOException;

  SpeechRecognition getS2TProvider(Provider provider) throws IOException;

  SpeechRecognition getS2TProvider(SpeechRecognitionFeatures speechRecognitionFeatures) throws IOException;
}
