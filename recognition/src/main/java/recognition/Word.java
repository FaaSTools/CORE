package recognition;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Word {
    private Double startTime;
    private Double endTime;
    private Double confidence;
    private String content;
}
