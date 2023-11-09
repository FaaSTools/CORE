package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DownUpOutput {
  private long executionTime;
  private long downloadTime;
  private long uploadTime;
}
