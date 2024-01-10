package ocr;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.Optional;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.FileInfo;
import storage.Storage;

public class OcrProviderGoogle implements OcrProvider {

  private static final String ENDPOINT = "%s-vision.googleapis.com:443";

  private Credentials credentials;
  private Storage storage;
  private Runtime runtime;
  private Configuration configuration;
  private String serviceRegion;

  public OcrProviderGoogle(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  public OcrProviderGoogle(
      Credentials credentials,
      Runtime runtime,
      Storage storage,
      Configuration configuration,
      String serviceRegion) {
    this(credentials, runtime, storage, configuration);
    this.serviceRegion = serviceRegion;
  }

  @Override
  public OcrResponse extract(String inputFile) throws Exception {
    FileInfo inputFileInfo = FileInfo.parse(inputFile);
    OcrConfiguration ocrConfiguration = OcrConfiguration.createDefaultFrom(configuration);
    InputConfig inputConfig;
    Optional<Long> downloadTime = Optional.empty();
    if (!inputFileInfo.isLocal()
        && Provider.GCP.equals(inputFileInfo.getBucketInfo().getProvider())
        && ocrConfiguration.isUseCallByReferenceIfPossible()) {
      String gsutilUrl =
          "gs://"
              + inputFileInfo.getBucketInfo().getBucketName()
              + "/"
              + inputFileInfo.getFileName();
      GcsSource gcsSource = GcsSource.newBuilder().setUri(gsutilUrl).build();
      inputConfig =
          InputConfig.newBuilder().setMimeType("application/pdf").setGcsSource(gcsSource).build();
    } else {
      long startDownload = System.currentTimeMillis();
      byte[] data = storage.read(inputFile);
      ByteString content = ByteString.copyFrom(data);
      inputConfig =
          InputConfig.newBuilder().setMimeType("application/pdf").setContent(content).build();
      downloadTime = Optional.of(System.currentTimeMillis() - startDownload);
    }
    long startOcr = System.currentTimeMillis();
    // invoke service
    ImageAnnotatorClient imageAnnotatorClient = getCloudVisionClient();
    Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
    AnnotateFileRequest fileRequest =
        AnnotateFileRequest.newBuilder().setInputConfig(inputConfig).addFeatures(feature).build();
    BatchAnnotateFilesRequest request =
        BatchAnnotateFilesRequest.newBuilder().addRequests(fileRequest).build();
    BatchAnnotateFilesResponse response = imageAnnotatorClient.batchAnnotateFiles(request);
    // parse response
    String fullText = "";
    for (AnnotateFileResponse fileResponse : response.getResponsesList()) {
      for (AnnotateImageResponse imageResponse : fileResponse.getResponsesList()) {
        fullText += "\n" + imageResponse.getFullTextAnnotation().getText();
      }
    }
    long endOcr = System.currentTimeMillis();
    return OcrResponse.builder().ocrTime(endOcr - startOcr).downloadTime(downloadTime).text(fullText).build();
  }

  /** Create google cloud vision client */
  private ImageAnnotatorClient getCloudVisionClient() throws IOException {
    ImageAnnotatorSettings.Builder builder =
        ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(
                    Optional.ofNullable(credentials.getGcpClientCredentials())
                        .orElse(credentials.getGcpCredentials())));

    if (serviceRegion != null && !serviceRegion.isEmpty()) {
      builder.setEndpoint(String.format(ENDPOINT, serviceRegion));
    }
    return ImageAnnotatorClient.create(builder.build());
  }
}
