import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;

public class SynthesisGoogle {

    public void synthesis(String text, String languageCode, GoogleCredentials credentials) throws IOException {
        String voice = "en-US-Standard-A";
        SsmlVoiceGender ssmlVoiceGender = SsmlVoiceGender.MALE;
        VoiceSelectionParams voiceSelectionParams =
                VoiceSelectionParams.newBuilder()
                        .setName(voice)
                        .setLanguageCode(languageCode)
                        .setSsmlGender(ssmlVoiceGender)
                        .build();
        SynthesisInput synthesisInput = SynthesisInput.newBuilder().setText(text).build();
        AudioConfig audioConfig =
                AudioConfig.newBuilder()
                        .setAudioEncoding(AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .build();
        TextToSpeechSettings settings =
                TextToSpeechSettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(credentials))
                        .build();
        TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings);
        ByteString byteString =
                textToSpeechClient
                        .synthesizeSpeech(synthesisInput, voiceSelectionParams, audioConfig)
                        .getAudioContent();
        byte[] audio = byteString.toByteArray();
    }

}
