package recognition;

import java.io.IOException;
import shared.Provider;

public interface SpeechRecognitionFactory {

  SpeechRecognition getS2TProvider(String inputFile) throws IOException;

  SpeechRecognition getS2TProvider(Provider provider) throws IOException;

  SpeechRecognition getS2TProvider(SpeechRecognitionFeatures speechRecognitionFeatures)
      throws IOException;

  SpeechRecognition getS2TProvider(Provider provider, String region);
}
