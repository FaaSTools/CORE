package ocr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;
import storage.FileInfo;
import storage.Storage;

public class OcrProviderAmazon implements OcrProvider {

  private final Credentials credentials;
  private final Storage storage;
  private final Runtime runtime;
  private final Configuration configuration;
  private String serviceRegion;

  public OcrProviderAmazon(
      Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
    this.credentials = credentials;
    this.storage = storage;
    this.runtime = runtime;
    this.configuration = configuration;
  }

  public OcrProviderAmazon(
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
    boolean callByValue = inputFileInfo.isLocal() || !ocrConfiguration.isUseCallByReferenceIfPossible();

    if (!callByValue && !Provider.AWS.equals(inputFileInfo.getBucketInfo().getProvider())) {
      callByValue = true;
    }

    if (!callByValue) {
      String storageRegion = storage.getRegion(inputFileInfo.getBucketInfo().getBucketUrl());
      if (this.serviceRegion != null
          && !this.serviceRegion.isEmpty()
          && !this.serviceRegion.equals(storageRegion)) {
        callByValue = true;
      }
    }

    String region;
    Document doc;
    long startDownload = System.currentTimeMillis();
    if (!callByValue) {
      region = storage.getRegion(inputFileInfo.getBucketInfo().getBucketUrl());
      S3Object s3Object =
          S3Object.builder()
              .bucket(inputFileInfo.getBucketInfo().getBucketName())
              .name(inputFileInfo.getFileName())
              .build();
      doc = Document.builder().s3Object(s3Object).build();
    } else {
      region = this.serviceRegion != null ? this.serviceRegion : selectRegion();
      byte[] data = storage.read(inputFile);
      ByteArrayInputStream sourceStream = new ByteArrayInputStream(data);
      SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);
      doc = Document.builder().bytes(sourceBytes).build();
    }
    long endDownload = System.currentTimeMillis();

    // invoke service
    long startTime = System.currentTimeMillis();
    TextractClient textractClient = getTextractClient(region);
    DetectDocumentTextRequest detectDocumentTextRequest =
        DetectDocumentTextRequest.builder().document(doc).build();
    DetectDocumentTextResponse response =
        textractClient.detectDocumentText(detectDocumentTextRequest);
    // parse response
    StringBuilder resultBuilder = new StringBuilder();
    for (Block block : response.blocks()) {
      if (block.blockType() == BlockType.LINE) {
        resultBuilder.append(block.text() + "\n");
      }
    }
    long endTime = System.currentTimeMillis();
    String text = resultBuilder.toString();
    return OcrResponse.builder()
        .ocrTime(endTime - startTime)
        .downloadTime(Optional.ofNullable(callByValue ? endDownload - startDownload : null))
        .text(text)
        .build();
  }

  private String selectRegion() {
    Provider functionProvider = runtime.getFunctionProvider();
    String functionRegion = runtime.getFunctionRegion();
    if (Provider.AWS.equals(functionProvider) && functionRegion != null) {
      // run in function region
      return functionRegion;
    }
    // run in default region
    return configuration.getDefaultRegionAws();
  }

  /** Create amazon textract client Java SDK V2 */
  private TextractClient getTextractClient(String region) throws IOException {
    return TextractClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create("https://textract." + region + ".amazonaws.com/"))
        .credentialsProvider(credentials.getAwsCredentials())
        .build();
  }
}
