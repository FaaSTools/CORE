package translate;

import java.io.IOException;
import shared.Provider;

public interface TranslateProviderFactory {

    TranslateProvider getProvider(Provider provider) throws IOException;

  TranslateProvider getProvider(Provider provider, String region) throws IOException;
}
