package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DownUpInput {
  private String inputFileUrl;
  private String outputFileUrl;
}
