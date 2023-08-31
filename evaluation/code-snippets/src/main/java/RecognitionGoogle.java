import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.BoolValue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecognitionGoogle {

    public void recognition(String bucket, String key, GoogleCredentials credentials) throws IOException, ExecutionException, InterruptedException {
        String gcsUrl = "gs://" + bucket + "/" + key;
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUrl).build();
        RecognitionConfig config =
                RecognitionConfig.newBuilder()
                        .setSampleRateHertz(16000)
                        .setLanguageCode("de-DE")
                        .setAudioChannelCount(1)
                        .setEnableWordConfidence(true)
                        .setEnableWordTimeOffsets(true)
                        .setEnableAutomaticPunctuation(true)
                        .setProfanityFilter(false)
                        .setEnableSpokenEmojis(BoolValue.of(false))
                        .setEnableSpokenPunctuation(BoolValue.of(false))
                        .build();
        SpeechSettings settings =
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(credentials))
                        .build();
        SpeechClient speechClient = SpeechClient.create(settings);
        OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> future =
                speechClient.longRunningRecognizeAsync(config, audio);
        List<SpeechRecognitionResult> results =
                future.get().getResultsList();
        SpeechRecognitionAlternative result = results.get(0).getAlternatives(0);
    }

}
