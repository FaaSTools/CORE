package ocr;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OcrRequest {
    private String inputFile;
}
