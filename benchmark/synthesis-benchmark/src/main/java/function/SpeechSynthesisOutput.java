package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpeechSynthesisOutput {
  private long executionTime;
  private long synthesisTime;
}
