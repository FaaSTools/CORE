package ocr;

import lombok.*;

import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OcrResponse {
  private String text;
  private long ocrTime;
  private Optional<Long> downloadTime;
}
