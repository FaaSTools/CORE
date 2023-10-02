package recognition;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.gson.Gson;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.FileInfo;
import storage.Storage;

@ToString
public class SpeechRecognitionGoogle implements SpeechRecognition {

  private static final String ENDPOINT = "%s-speech.googleapis.com:443";
  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;
  private String serviceRegion;

  public SpeechRecognitionGoogle(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.runtime = runtime;
    this.storage = storage;
    this.configuration = configuration;
  }

  public SpeechRecognitionGoogle(
      Credentials credentials,
      Runtime runtime,
      Storage storage,
      Configuration configuration,
      String serviceRegion) {
    this(credentials, runtime, storage, configuration);
    this.serviceRegion = serviceRegion;
  }

  @Override
  public SpeechRecognitionResponse recognizeSpeech(
      String inputFile,
      int sampleRate,
      String languageCode,
      int channelCount,
      boolean srtSubtitles,
      boolean vttSubtitles,
      boolean profanityFilter,
      boolean spokenEmoji,
      boolean spokenPunctuation)
      throws Exception {
    // parse input file url
    FileInfo inputFileInfo = FileInfo.parse(inputFile);
    // check if call by reference is possible
    boolean callByReference = isCallByReferencePossible(inputFileInfo);
    // init audio config
    RecognitionAudio audio;
    if (callByReference) {
      audio = createGcsRecognitionAudio(inputFileInfo);
    } else {
      byte[] contents = storage.read(inputFile);
      audio = createLocalRecognitionAudio(contents);
    }
    // init recognition config
    RecognitionConfig config =
        createRecognitionConfig(
            sampleRate,
            languageCode,
            channelCount,
            profanityFilter,
            spokenEmoji,
            spokenPunctuation);
    // invoke service
    long start = System.currentTimeMillis();
    SpeechClient speechClient = getSpeechClient();
    OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> future =
        speechClient.longRunningRecognizeAsync(config, audio);
    List<com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult> results =
        future.get().getResultsList();
    SpeechRecognitionAlternative alternative = results.get(0).getAlternatives(0);
    long end = System.currentTimeMillis();
    // parse response
    String jsonString = new Gson().toJson(alternative);
    SpeechRecognitionResponse response = parseResponse(jsonString);
    response.setProvider(Provider.GCP);
    response.setRecognitionTime(end - start);
    speechClient.close();
    return response;
  }

  private RecognitionAudio createGcsRecognitionAudio(FileInfo file) {
    String bucket = file.getBucketInfo().getBucketName();
    String key = file.getFileName();
    String gcsUrl = "gs://" + bucket + "/" + key;
    return RecognitionAudio.newBuilder().setUri(gcsUrl).build();
  }

  private RecognitionAudio createLocalRecognitionAudio(byte[] data) throws IOException {
    ByteString audioBytes = ByteString.copyFrom(data);
    return RecognitionAudio.newBuilder().setContent(audioBytes).build();
  }

  private RecognitionConfig createRecognitionConfig(
      int sampleRate,
      String languageCode,
      int channelCount,
      boolean profanityFilter,
      boolean spokenEmoji,
      boolean spokenPunctuation) {
    return RecognitionConfig.newBuilder()
        // .setEncoding(RecognitionConfig.AudioEncoding.MP3)
        .setSampleRateHertz(sampleRate)
        .setLanguageCode(languageCode) // en-US
        .setAudioChannelCount(channelCount)
        .setEnableWordConfidence(true)
        .setEnableWordTimeOffsets(true)
        .setEnableAutomaticPunctuation(true)
        .setProfanityFilter(profanityFilter)
        .setEnableSpokenEmojis(BoolValue.of(spokenEmoji))
        .setEnableSpokenPunctuation(BoolValue.of(spokenPunctuation))
        .build();
  }

  private SpeechRecognitionResponse parseResponse(String jsonString) throws IOException {
    JSONObject jsonRoot = new JSONObject(jsonString);
    ArrayList<Word> words = new ArrayList<>();
    String fullText = jsonRoot.getString("transcript_");
    JSONArray jsonWords = jsonRoot.getJSONArray("words_");
    for (int i = 0; i < jsonWords.length(); i++) {
      JSONObject jsonWord = jsonWords.getJSONObject(i);
      double startTime =
          jsonWord.getJSONObject("startTime_").getDouble("seconds_")
              + jsonWord.getJSONObject("startTime_").getDouble("nanos_") / 1000000000;
      double endTime =
          jsonWord.getJSONObject("endTime_").getDouble("seconds_")
              + jsonWord.getJSONObject("endTime_").getDouble("nanos_") / 1000000000;
      String content = jsonWord.getString("word_");
      double confidence = jsonWord.getDouble("confidence_");
      Word word =
          Word.builder()
              .startTime(startTime)
              .endTime(endTime)
              .confidence(confidence)
              .content(content)
              .build();
      words.add(word);
    }
    // create response object
    return SpeechRecognitionResponse.builder().fullTranscript(fullText).words(words).build();
  }

  private boolean isCallByReferencePossible(FileInfo inputFileInfo) {
    return !inputFileInfo.isLocal()
        && inputFileInfo.getBucketInfo().getProvider().equals(Provider.GCP);
  }

  private SpeechClient getSpeechClient() throws IOException {
    SpeechSettings.Builder builder =
        SpeechSettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(
                    Optional.ofNullable(credentials.getGcpClientCredentials())
                        .orElse(credentials.getGcpCredentials())));
    if (serviceRegion != null && !serviceRegion.isEmpty()) {
      builder.setEndpoint(String.format(ENDPOINT, serviceRegion));
    }
    return SpeechClient.create(builder.build());
  }
}
