import shared.Configuration;
import shared.Credentials;
import synthesis.*;

public class SynthesisCore {

    public void synthesis(String fileUrl, String languageCode, Credentials credentials) throws Exception {
        var service = new SpeechSynthesizer(Configuration.builder().build(), credentials);
        var request = SpeechSynthesisRequest.builder()
                .inputFile(fileUrl)
                .gender(Gender.MALE)
                .textType(TextType.PLAIN_TEXT)
                .language(languageCode)
                .build();
        SpeechSynthesisResponse response = service.synthesizeSpeech(request);
    }
}
