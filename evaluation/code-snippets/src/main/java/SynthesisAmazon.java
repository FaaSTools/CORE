import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.IOException;
import java.net.URI;

public class SynthesisAmazon {

    public void synthesis(String text, String region, AwsCredentialsProvider credentials) throws IOException {
        SynthesizeSpeechRequest synthesizeSpeechRequest =
                SynthesizeSpeechRequest.builder()
                        .text(text)
                        .voiceId(VoiceId.MATTHEW)
                        .textType(TextType.TEXT)
                        .outputFormat(OutputFormat.PCM)
                        .engine(Engine.STANDARD)
                        .sampleRate(Integer.toString(16000))
                        .build();
        // invoke service
        PollyClient pollyClient = PollyClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create("https://polly." + region + ".amazonaws.com/"))
                .credentialsProvider(credentials)
                .build();
        ResponseInputStream<SynthesizeSpeechResponse> in =
                pollyClient.synthesizeSpeech(synthesizeSpeechRequest);
        byte[] audio = in.readAllBytes();
    }

}
