package ocr;

import lombok.*;
import shared.Configuration;
import shared.Provider;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcrConfiguration extends Configuration {

    private boolean useCallByReferenceIfPossible;

    private OcrConfiguration(Provider defaultProvider, String defaultRegionAws, String defaultRegionGcp, boolean useCallByReferenceIfPossible) {
        super(defaultProvider, defaultRegionAws, defaultRegionGcp);
        this.useCallByReferenceIfPossible = useCallByReferenceIfPossible;
    }

    public static OcrConfiguration createDefaultFrom(Configuration configuration) {
        if (configuration instanceof OcrConfiguration conf) {
            return conf;
        }
        return new OcrConfiguration(configuration.getDefaultProvider(),
                configuration.getDefaultRegionAws(),
                configuration.getDefaultRegionGcp(),
                true);
    }

    public static OcrConfiguration createWithParamsFrom(Configuration configuration,
                                                        boolean useCallByReferenceIfPossible) {
        return new OcrConfiguration(configuration.getDefaultProvider(),
                configuration.getDefaultRegionAws(),
                configuration.getDefaultRegionGcp(),
                useCallByReferenceIfPossible);
    }
}
