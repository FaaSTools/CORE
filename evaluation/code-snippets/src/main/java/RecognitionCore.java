import recognition.SpeechRecognitionRequest;
import recognition.SpeechRecognizer;
import shared.Configuration;
import shared.Credentials;

public class RecognitionCore {

    public void recognition(String fileUrl, String language, int sampleRate, int channelCount, Credentials credentials) throws Exception {
        var service = new SpeechRecognizer(Configuration.builder().build(), credentials);
        var request = SpeechRecognitionRequest.builder()
                .inputFile(fileUrl)
                .languageCode(language)
                .sampleRate(sampleRate)
                .channelCount(channelCount)
                .build();
        var response = service.recognizeSpeech(request);
    }

}
