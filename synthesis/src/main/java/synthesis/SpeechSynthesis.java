package synthesis;

public interface SpeechSynthesis {

  SpeechSynthesisResponse synthesizeSpeech(
      String inputFile,
      String language,
      TextType textType,
      Gender gender,
      AudioFormat audioFormat)
      throws Exception;

}
