package recognition;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpeechRecognitionRequest {
    private String inputFile; // local filesystem, AWS S3, Google Cloud Storage
    private int sampleRate; // audio frequency in Hz
    private int channelCount; // 1 for mono and 2 for stereo
    private String languageCode; // W3C language code (e.g. en-US, de-DE, etc)
}
