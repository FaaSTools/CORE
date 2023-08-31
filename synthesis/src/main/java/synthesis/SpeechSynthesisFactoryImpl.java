package synthesis;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.Storage;
import storage.StorageImpl;

import java.util.List;

public class SpeechSynthesisFactoryImpl implements SpeechSynthesisFactory {

  private List<AudioFormat> audioFormatsAmazon =
      List.of(AudioFormat.MP3, AudioFormat.PCM, AudioFormat.OGG_VORBIS);
  private List<AudioFormat> audioFormatsGoogle =
      List.of(
          AudioFormat.MP3,
          AudioFormat.PCM,
          AudioFormat.OGG_OPUS,
          AudioFormat.MULAW,
          AudioFormat.ALAW);

  private Configuration configuration;
  private Credentials credentials;
  private Runtime runtime;
  private Storage storage;

  public SpeechSynthesisFactoryImpl(
      Configuration configuration, Credentials credentials, Runtime runtime, Storage storage) {
    this.configuration = configuration;
    this.credentials = credentials;
    this.runtime = runtime;
    this.storage = storage;
  }

  /** Select provider explicitly. */
  @Override
  public SpeechSynthesis getT2SProvider(Provider provider) {
    Storage storage = new StorageImpl(credentials);
    Runtime runtime = new Runtime();
    if (provider.equals(Provider.AWS)) {
      return new SpeechSynthesisAmazon(credentials, storage, configuration, runtime);
    }
    if (provider.equals(Provider.GCP)) {
      return new SpeechSynthesisGoogle(credentials, storage, configuration, runtime);
    }
    throw new RuntimeException("Provider must not be null!");
  }

  /** Select provider based on the audio format. */
  @Override
  public SpeechSynthesis getT2SProvider(AudioFormat audioFormat) throws Exception {
    // supported by both providers
    if (audioFormatsAmazon.contains(audioFormat) && audioFormatsGoogle.contains(audioFormat)) {
      return getT2SProvider(configuration.getDefaultProvider());
    }
    // only supported by amazon
    if (audioFormatsAmazon.contains(audioFormat)) {
      return getT2SProvider(Provider.AWS);
    }
    // only supported by google
    if (audioFormatsGoogle.contains(audioFormat)) {
      return getT2SProvider(Provider.GCP);
    }
    throw new RuntimeException("Invalid audio format!");
  }

  /** Select provider based on the function location. */
  @Override
  public SpeechSynthesis getT2SProvider() throws Exception {
    // run on function provider
    Provider functionProvider = runtime.getFunctionProvider();
    if (functionProvider != null) {
      // run on function provider
      return getT2SProvider(functionProvider);
    }
    // run on default provider
    return getT2SProvider(configuration.getDefaultProvider());
  }
}
