package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import shared.Credentials;
import storage.Storage;
import storage.StorageImpl;

public class DownUpCore implements HttpFunction, RequestHandler<DownUpInput, DownUpOutput> {

  @Override
  public DownUpOutput handleRequest(DownUpInput downUpInput, Context context) {
    try {
      return doWork(downUpInput);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    Gson gson = new Gson();
    JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
    DownUpInput input = gson.fromJson(body.toString(), DownUpInput.class);
    DownUpOutput output = doWork(input);
    response.getWriter().write(gson.toJson(output));
  }

  public DownUpOutput doWork(DownUpInput input) throws Exception {
    long startExecution = System.currentTimeMillis();
    // download
    long startDownload = System.currentTimeMillis();
    Credentials c1 = Credentials.loadDefaultCredentials();
    Storage s1 = new StorageImpl(c1);
    byte[] data = s1.read(input.getInputFileUrl());
    long endDownload = System.currentTimeMillis();
    // upload
    long startUpload = System.currentTimeMillis();
    Credentials c2 = Credentials.loadDefaultCredentials();
    Storage s2 = new StorageImpl(c2);
    s2.write(data, input.getOutputFileUrl());
    long endUpload = System.currentTimeMillis();
    long endExecution = System.currentTimeMillis();
    return DownUpOutput.builder()
        .executionTime(endExecution - startExecution)
        .uploadTime(endUpload - startUpload)
        .downloadTime(endDownload - startDownload)
        .build();
  }
}
