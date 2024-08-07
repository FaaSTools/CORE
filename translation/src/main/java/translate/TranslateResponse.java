package translate;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TranslateResponse {
  private String text;
  private long translateTime;
  private long downloadTime;
}
