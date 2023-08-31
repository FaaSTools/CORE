package synthesis;

import lombok.*;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoiceAmazon {
    private String gender;
    private String id;
    private String languageCode;
    private String languageName;
    private String name;
    private List<String> supportedEngines;
}
