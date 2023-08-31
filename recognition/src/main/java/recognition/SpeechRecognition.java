package recognition;

public interface SpeechRecognition {

  SpeechRecognitionResponse recognizeSpeech(
      String inputFile,
      int sampleRate,
      String languageCode,
      int channelCount,
      boolean srtSubtitles,
      boolean vttSubtitles,
      boolean profanityFilter,
      boolean spokenEmoji,
      boolean spokenPunctuation)
      throws Exception;
}
