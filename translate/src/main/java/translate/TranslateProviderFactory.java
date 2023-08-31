package translate;

import shared.Provider;

import java.io.IOException;

public interface TranslateProviderFactory {

    TranslateProvider getProvider(Provider provider) throws IOException;

}
