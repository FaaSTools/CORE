package translate;

import shared.Configuration;
import shared.Credentials;
import shared.Provider;
import shared.Runtime;

public class TranslateService {

    private Configuration configuration;
    private Credentials credentials;

    public TranslateService(Configuration configuration, Credentials credentials){
        this.credentials = credentials;
        this.configuration = configuration;
    }

    public TranslateResponse translate(TranslateRequest translateRequest, Provider provider) throws Exception {
        // select provider
        Runtime runtime = new Runtime();
        TranslateProviderFactoryImpl factory = new TranslateProviderFactoryImpl(configuration, credentials, runtime);
        TranslateProvider translateProvider = factory.getProvider(provider);
        // invoke the service
        TranslateResponse response = translateProvider.translate(
                translateRequest.getInputFile(),
                translateRequest.getLanguage()
        );
        return response;
    }

}
