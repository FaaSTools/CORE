package synthesis;

import lombok.*;
import shared.Provider;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpeechSynthesisResponse {
  private Provider provider; // AWS | GCP
  private long synthesisTime; // in ms
  private byte[] audio; // binary stream containing the synthesized speech
}
