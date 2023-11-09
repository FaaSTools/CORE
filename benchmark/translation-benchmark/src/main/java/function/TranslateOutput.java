package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TranslateOutput {
  private long executionTime;
  private long serviceTime;
}
