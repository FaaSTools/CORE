package ocr;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;
import storage.FileInfo;
import storage.Storage;

import java.io.IOException;

public class OcrProviderGoogle implements OcrProvider {

    private Credentials credentials;
    private Storage storage;
    private Runtime runtime;
    private Configuration configuration;

    public OcrProviderGoogle(
            Credentials credentials, Runtime runtime, Storage storage, Configuration configuration) {
        this.credentials = credentials;
        this.storage = storage;
        this.runtime = runtime;
        this.configuration = configuration;
    }

    @Override
    public OcrResponse extract(String inputFile) throws Exception {
        FileInfo inputFileInfo = FileInfo.parse(inputFile);
        InputConfig inputConfig;
        if (!inputFileInfo.isLocal() && Provider.GCP.equals(inputFileInfo.getBucketInfo().getProvider())) {
            String gsutilUrl = "gs://" + inputFileInfo.getBucketInfo().getBucketName() + "/" + inputFileInfo.getFileName();
            GcsSource gcsSource = GcsSource.newBuilder().setUri(gsutilUrl).build();
            inputConfig = InputConfig.newBuilder().setMimeType("application/pdf").setGcsSource(gcsSource).build();
        } else {
            byte[] data = storage.read(inputFile);
            ByteString content = ByteString.copyFrom(data);
            inputConfig = InputConfig.newBuilder().setMimeType("application/pdf").setContent(content).build();
        }
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
        return OcrResponse.builder().text(fullText).build();
    }

    /**
     * Create google cloud vision client
     */
    private ImageAnnotatorClient getCloudVisionClient() throws IOException {
        ImageAnnotatorSettings settings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(credentials.getGcpCredentials()))
                        .build();
        return ImageAnnotatorClient.create(settings);
    }

}
