package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ocr.OcrRequest;
import ocr.OcrService;
import org.apache.commons.io.FilenameUtils;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import storage.StorageImpl;

public class ImageRecognitionCore
    implements HttpFunction, RequestHandler<ImageRecognitionInput, ImageRecognitionOutput> {

  @Override
  public ImageRecognitionOutput handleRequest(ImageRecognitionInput s2TInput, Context context) {
    try {
      return doWork(s2TInput);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    Gson gson = new Gson();
    JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
    ImageRecognitionInput input = gson.fromJson(body.toString(), ImageRecognitionInput.class);
    ImageRecognitionOutput output = doWork(input);
    response.getWriter().write(gson.toJson(output));
  }

  public ImageRecognitionOutput doWork(ImageRecognitionInput input) throws Exception {
    long startExecution = System.currentTimeMillis();
    var credentials = Credentials.loadDefaultCredentials();
    var config = Configuration.builder().build();
    var service = new OcrService(config, credentials);
    String baseName = FilenameUtils.getBaseName(input.getInputFile());
    String outputFile = input.getOutputBucket() + "ocr/" + baseName + "." + "txt";
    var request = OcrRequest.builder().inputFile(input.getInputFile()).build();
    var response =
        service.extract(request, Provider.valueOf(input.getProvider()), input.getRegion());
    var storage = new StorageImpl(credentials);
    storage.write(new Gson().toJson(response).getBytes(), outputFile);
    long endExecution = System.currentTimeMillis();
    return ImageRecognitionOutput.builder()
        .executionTime(endExecution - startExecution)
        .ocrTime(response.getOcrTime())
        .build();
  }
}
