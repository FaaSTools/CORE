package ocr;

import java.io.IOException;
import shared.Provider;

public interface OcrProviderFactory {

  OcrProvider getProvider(Provider provider) throws IOException;

  OcrProvider getProvider(Provider provider, String serviceRegion) throws IOException;
}
