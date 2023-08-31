package ocr;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;

public class OcrService {

    private Configuration configuration;
    private Credentials credentials;

    public OcrService(Configuration configuration, Credentials credentials) {
        this.credentials = credentials;
        this.configuration = configuration;
    }

    public OcrResponse extract(OcrRequest translateRequest, Provider provider) throws Exception {
        // select provider
        Runtime runtime = new Runtime();
        OcrProviderFactory factory = new OcrProviderFactoryImpl(configuration, credentials, runtime);
        OcrProvider ocrProvider = factory.getProvider(provider);
        // invoke the service
        OcrResponse response = ocrProvider.extract(
                translateRequest.getInputFile()
        );
        return response;
    }
}
