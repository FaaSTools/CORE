package recognition;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpeechRecognitionFeatures {
    private boolean srtSubtitles; // generate subtitles in srt format
    private boolean vttSubtitles; // generate subtitles in vtt format
    private boolean profanityFilter; // mask profane words with asterisk
    private boolean spokenPunctuation; // detect spoken punctuation (e.g. question mark)
    private boolean spokenEmoji; // detect spoken emojis (e.g. raised hand)
}
