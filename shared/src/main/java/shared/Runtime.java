package shared;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Runtime {

  /**
   * Detects the provider where the function is running.
   *
   * @return An enum indicating the function provider. Null, if the code is not running inside a
   *     serverless function.
   */
  public Provider getFunctionProvider() {
    try {
      if (System.getenv("FUNCTION_TARGET") != null) {
        return Provider.GCP;
      } else if (System.getenv("AWS_REGION") != null) {
        return Provider.AWS;
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  /**
   * Detects the region where the function is running.
   *
   * @return For AWS, the single-region-code (e.g. us-east-1). For GCP the multi-region-code (e.g.
   *     eu, us). Null, if the code is not running inside a serverless function.
   */
  public String getFunctionRegion() {
    try {
      Provider functionProvider = getFunctionProvider();
      if (functionProvider.equals(Provider.AWS)) {
        return getRegionAmazon();
      }
      if (functionProvider.equals(Provider.GCP)) {
        return getRegionGoogle();
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  private String getRegionGoogle() throws IOException {
    // query the zone from internal compute engine metadata server
    String zone = null;
    HttpURLConnection conn = null;
    URL url = new URL("http://metadata.google.internal/computeMetadata/v1/instance/zone");
    conn = (HttpURLConnection) (url.openConnection());
    conn.setRequestProperty("Metadata-Flavor", "Google");
    zone = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    conn.disconnect();
    // convert zone code to region code
    String[] parts = zone.split("/");
    String zoneCode = parts[parts.length - 1];
    String multiRegionEndpoint = zoneCode.substring(0, 2);
    if (multiRegionEndpoint.equals("eu") || multiRegionEndpoint.equals("us")) {
      return multiRegionEndpoint;
    }
    return null;
  }

  private String getRegionAmazon() throws IOException {
    return System.getenv().get("AWS_REGION");
  }
}
