package synthesis;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.Storage;
import storage.StorageImpl;

public class SpeechSynthesizer {

  private Configuration configuration;
  private Credentials credentials;

  public SpeechSynthesizer(Configuration configuration, Credentials credentials) {
    this.credentials = credentials;
    this.configuration = configuration;
  }

  private SpeechSynthesisResponse synthesizeSpeech(
      SpeechSynthesisRequest speechSynthesisRequest, SpeechSynthesis speechSynthesizer)
      throws Exception {
    // invoke service
    return speechSynthesizer.synthesizeSpeech(
        speechSynthesisRequest.getInputFile(),
        speechSynthesisRequest.getLanguage(),
        speechSynthesisRequest.getTextType(),
        speechSynthesisRequest.getGender(),
        AudioFormat.PCM);
  }

  /** Provider is explicitly selected. */
  public SpeechSynthesisResponse synthesizeSpeech(
      SpeechSynthesisRequest speechSynthesisRequest, Provider provider) throws Exception {
    // initialize provider
    Storage storage = new StorageImpl(credentials);
    Runtime runtime = new Runtime();
    SpeechSynthesisFactoryImpl factory =
        new SpeechSynthesisFactoryImpl(configuration, credentials, runtime, storage);
    SpeechSynthesis speechSynthesizer = factory.getT2SProvider(provider);
    // invoke service
    return this.synthesizeSpeech(speechSynthesisRequest, speechSynthesizer);
  }

  public SpeechSynthesisResponse synthesizeSpeech(
      SpeechSynthesisRequest speechSynthesisRequest, Provider provider, String region)
      throws Exception {
    // initialize provider
    Storage storage = new StorageImpl(credentials);
    Runtime runtime = new Runtime();
    SpeechSynthesisFactoryImpl factory =
        new SpeechSynthesisFactoryImpl(configuration, credentials, runtime, storage);
    SpeechSynthesis speechSynthesizer = factory.getT2SProvider(provider, region);
    // invoke service
    return this.synthesizeSpeech(speechSynthesisRequest, speechSynthesizer);
  }

  /**
   * Provider is selected based on the audio format. If the audio format is supported by both
   * providers, the default provider is used.
   */
  public SpeechSynthesisResponse synthesizeSpeech(
      SpeechSynthesisRequest speechSynthesisRequest, AudioFormat audioFormat) throws Exception {
    // initialize provider
    Storage storage = new StorageImpl(credentials);
    Runtime runtime = new Runtime();
    SpeechSynthesisFactoryImpl factory =
        new SpeechSynthesisFactoryImpl(configuration, credentials, runtime, storage);
    SpeechSynthesis provider = factory.getT2SProvider(audioFormat);
    // invoke service
    return provider.synthesizeSpeech(
        speechSynthesisRequest.getInputFile(),
        speechSynthesisRequest.getLanguage(),
        speechSynthesisRequest.getTextType(),
        speechSynthesisRequest.getGender(),
        audioFormat);
  }

  /** Provider is selected based on the location of the output. */
  public SpeechSynthesisResponse synthesizeSpeech(SpeechSynthesisRequest speechSynthesisRequest)
      throws Exception {
    // initialize provider
    Storage storage = new StorageImpl(credentials);
    Runtime runtime = new Runtime();
    SpeechSynthesisFactoryImpl factory =
        new SpeechSynthesisFactoryImpl(configuration, credentials, runtime, storage);
    SpeechSynthesis provider = factory.getT2SProvider();
    // invoke service
    return this.synthesizeSpeech(speechSynthesisRequest, provider);
  }
}
