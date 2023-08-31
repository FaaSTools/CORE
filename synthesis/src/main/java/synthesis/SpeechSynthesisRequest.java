package synthesis;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpeechSynthesisRequest {
    private String inputFile; // local filesystem, AWS S3, Google Cloud Storage
    private String language; // for example en-US or de-DE
    private TextType textType; // PLAIN_TEXT | SSML
    private Gender gender; // MALE | FEMALE
}
