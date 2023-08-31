package synthesis;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoiceGoogle {
    private String name;
    private String language;
    private String ssmlVoiceGender;
    private int naturalSampleRateHertz;
    private String supportedEngine;
}
