package translate;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TranslateRequest {
  private String inputFile;
  private String language;
}
