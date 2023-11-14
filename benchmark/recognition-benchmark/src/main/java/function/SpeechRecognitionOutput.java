package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpeechRecognitionOutput {
  private long executionTime;
  private long recognitionTime;
}
