package recognition;

import lombok.*;
import shared.Provider;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpeechRecognitionResponse {
  private Provider provider; // AWS | GCP
  private long recognitionTime; // in ms
  private String fullTranscript; // transcript of the whole audio file
  private List<Word> words; // information about each detected word
  private String srtSubtitles; // subtitles in SRT format
  private String vttSubtitles; // subtitles in VTT format
}
