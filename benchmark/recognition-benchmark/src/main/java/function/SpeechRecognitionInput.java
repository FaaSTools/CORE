package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpeechRecognitionInput {
  private String inputFile;
  private String outputBucket;
  private String provider;
  private String region;
  private String language;
}
