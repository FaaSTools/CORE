package synthesis;

import shared.Provider;

import java.io.IOException;

public interface SpeechSynthesisFactory {

  /** Select provider explicitly. */
  SpeechSynthesis getT2SProvider(Provider provider) throws IOException;

  /** Select provider based on the audio format. */
  SpeechSynthesis getT2SProvider(AudioFormat audioFormat) throws Exception;

  /** Select provider based on the function location. */
  SpeechSynthesis getT2SProvider() throws Exception;



}
