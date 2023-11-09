package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.commons.io.FilenameUtils;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import storage.StorageImpl;
import synthesis.*;

public class SpeechSynthesisCore
    implements HttpFunction, RequestHandler<SpeechSynthesisInput, SpeechSynthesisOutput> {

  @Override
  public SpeechSynthesisOutput handleRequest(SpeechSynthesisInput request, Context context) {
    try {
      return doWork(request);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    Gson gson = new Gson();
    JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
    SpeechSynthesisInput input = gson.fromJson(body.toString(), SpeechSynthesisInput.class);
    SpeechSynthesisOutput output = doWork(input);
    response.getWriter().write(gson.toJson(output));
  }

  public SpeechSynthesisOutput doWork(SpeechSynthesisInput input) throws Exception {
    long start = System.currentTimeMillis();
    var credentials = Credentials.loadDefaultCredentials();
    var config = Configuration.builder().build();
    var service = new SpeechSynthesizer(config, credentials);
    String baseName = FilenameUtils.getBaseName(input.getInputFile());
    String outputFile = input.getOutputBucket() + "synthesis/" + baseName + "." + "wav";

    var request =
        SpeechSynthesisRequest.builder()
            .inputFile(input.getInputFile())
            .gender(Gender.MALE)
            .textType(TextType.PLAIN_TEXT)
            .language(input.getLanguage())
            .build();
    SpeechSynthesisResponse response = service.synthesizeSpeech(request, Provider.valueOf(input.getProvider()), input.getRegion());
    byte[] wav = pcmToWav(response.getAudio(), 16000, 16, 1);
    var storage = new StorageImpl(credentials);
    storage.write(wav, outputFile);
    long end = System.currentTimeMillis();
    return SpeechSynthesisOutput.builder()
        .executionTime(end - start)
        .synthesisTime(response.getSynthesisTime())
        .build();
  }

  private byte[] pcmToWav(byte[] data, int sampleRate, int sampleSizeInBits, int channelCount)
      throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    javax.sound.sampled.AudioFormat format =
        new AudioFormat(sampleRate, sampleSizeInBits, channelCount, true, false);
    AudioSystem.write(
        new AudioInputStream(new ByteArrayInputStream(data), format, data.length),
        AudioFileFormat.Type.WAVE,
        out);
    return out.toByteArray();
  }
}
