import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class RecognitionAmazon {

    public void recognition(String bucket, String key, String languageCode, int sampleRate, String region, AwsCredentialsProvider credentials) throws IOException {
        var jobName = UUID.randomUUID().toString();
        var s3Uri = "s3://" + bucket + "/" + key;
        var builder =
                StartTranscriptionJobRequest.builder()
                        .transcriptionJobName(jobName)
                        .media(Media.builder().mediaFileUri(s3Uri).build())
                        .languageCode(languageCode) // en-US
                        .mediaSampleRateHertz(sampleRate);
        StartTranscriptionJobRequest request = builder.build();
        var transcribeClient = TranscribeClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create("https://transcribe." + region + ".amazonaws.com/"))
                .credentialsProvider(credentials)
                .build();
        transcribeClient.startTranscriptionJob(request);
        TranscriptionJob transcriptionJob;
        while (true) {
            var getTranscriptionJobRequest =
                    GetTranscriptionJobRequest.builder().transcriptionJobName(jobName).build();
            var transcriptionJobResponse =
                    transcribeClient.getTranscriptionJob(getTranscriptionJobRequest);
            var status =
                    transcriptionJobResponse.transcriptionJob().transcriptionJobStatus();
            if (status.equals(TranscriptionJobStatus.COMPLETED)) {
                transcriptionJob = transcriptionJobResponse.transcriptionJob();
                break;
            } else if (status.equals(TranscriptionJobStatus.FAILED)) {
                throw new RuntimeException("Transcription failed.");
            }
        }
        var transcript = transcriptionJob.transcript();
        var transcriptFileUri = transcript.transcriptFileUri();
        var url = new URL(transcriptFileUri);
        var bis = new BufferedInputStream(url.openStream());
        var transcriptJsonString = new String(bis.readAllBytes());
        bis.close();
    }
}
