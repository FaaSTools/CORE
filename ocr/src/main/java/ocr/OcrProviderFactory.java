package ocr;

import shared.Provider;

import java.io.IOException;

public interface OcrProviderFactory {

    OcrProvider getProvider(Provider provider) throws IOException;

}
