package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TranslateInput {
  private String inputFile;
  private String outputBucket;
  private String language;
  private String provider;
  private String region;
}
