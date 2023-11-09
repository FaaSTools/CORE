package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import storage.Storage;
import storage.StorageImpl;
import translate.TranslateRequest;
import translate.TranslateResponse;
import translate.TranslateService;

public class TranslateCore
    implements HttpFunction, RequestHandler<TranslateInput, TranslateOutput> {

  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    TranslateInput input =
        TranslateInput.builder()
            .inputFile(
                "https://storage.cloud.google.com/tommi-test-bucket/transcribe/recognition-15.txt")
            .outputBucket("https://storage.cloud.google.com/tommi-test-bucket/")
            .language("en-US")
            .provider("GCP")
            .build();
    TranslateCore function = new TranslateCore();
    TranslateOutput output = function.doWork(input);
    System.out.println(output);
  }

  @Override
  public TranslateOutput handleRequest(TranslateInput input, Context context) {
    try {
      return doWork(input);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
    TranslateInput input = gson.fromJson(body.toString(), TranslateInput.class);
    TranslateOutput output = doWork(input);
    response.getWriter().write(gson.toJson(output));
  }

  public TranslateOutput doWork(TranslateInput input) throws Exception {
    long startTime = System.currentTimeMillis();
    // construct output file url
    String baseName = FilenameUtils.getBaseName(input.getInputFile());
    String outputFile = input.getOutputBucket() + "translate/" + baseName + "." + "txt";
    // invoke translation
    Credentials credentials = Credentials.loadDefaultCredentials();
    Configuration configuration = Configuration.builder().build();
    TranslateService translateService = new TranslateService(configuration, credentials);
    TranslateRequest request =
        TranslateRequest.builder()
            .inputFile(input.getInputFile())
            .language(input.getLanguage())
            .build();
    TranslateResponse response =
        translateService.translate(
            request, Provider.valueOf(input.getProvider()), input.getRegion());
    // write result to output bucket
    Storage storage = new StorageImpl(Credentials.loadDefaultCredentials());
    storage.write(response.getText().getBytes(), outputFile);
    // return response
    long endTime = System.currentTimeMillis();
    return TranslateOutput.builder()
        .executionTime(endTime - startTime)
        .serviceTime(response.getTranslateTime())
        .build();
  }
}
