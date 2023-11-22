package recognition;

import lombok.*;
import shared.Configuration;
import shared.Provider;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwsSpeechRecognitionConfiguration extends Configuration {
    // formula to wait = factor * times_failed + offset
    private int jobFinishedRequestBackoffFactor;
    private int jobFinishedRequestBackoffOffsetMillis;

    private AwsSpeechRecognitionConfiguration(Provider defaultProvider, String defaultRegionAws, String defaultRegionGcp, int jobFinishedRequestBackoffFactor, int jobFinishedRequestBackoffOffsetMillis) {
        super(defaultProvider, defaultRegionAws, defaultRegionGcp);
        this.jobFinishedRequestBackoffFactor = jobFinishedRequestBackoffFactor;
        this.jobFinishedRequestBackoffOffsetMillis = jobFinishedRequestBackoffOffsetMillis;
    }

    public static AwsSpeechRecognitionConfiguration createDefaultFrom(Configuration configuration) {
        if (configuration instanceof AwsSpeechRecognitionConfiguration conf) {
            return conf;
        }
        return new AwsSpeechRecognitionConfiguration(configuration.getDefaultProvider(),
                configuration.getDefaultRegionAws(),
                configuration.getDefaultRegionGcp(),
                1, 500);
    }

    public static AwsSpeechRecognitionConfiguration createWithParamsFrom(Configuration configuration,
                                                                         int jobFinishedRequestBackoffFactor,
                                                                         int jobFinishedRequestBackoffOffsetMillis) {
        return new AwsSpeechRecognitionConfiguration(configuration.getDefaultProvider(),
                configuration.getDefaultRegionAws(),
                configuration.getDefaultRegionGcp(),
                jobFinishedRequestBackoffFactor,
                jobFinishedRequestBackoffOffsetMillis);
    }


}
