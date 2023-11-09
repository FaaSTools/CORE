package function;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ImageRecognitionOutput {
    private long executionTime;
    private long ocrTime;
}
