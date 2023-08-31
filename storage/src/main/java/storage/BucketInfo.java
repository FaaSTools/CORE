package storage;

import lombok.*;
import shared.Provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BucketInfo {

  public static final String AWS_BUCKET_REGEX = "(http|https)://(.*).s3.(.*.)?amazonaws.com/?";
  public static final String GCP_BUCKET_REGEX = "(http|https)://storage.cloud.google.com/(.*?)/(.*)";

  private Provider provider; // AWS | GCP
  private String region; // this is null for GCP, as the region is not included in the url
  private String bucketName; //  simply the bucket name
  private String bucketUrl; // includes trailing slash at the end

  public static BucketInfo parse(String bucketUrl) {
    return BucketInfo.builder()
        .provider(getProvider(bucketUrl))
        .bucketName(getBucketName(bucketUrl))
        .region(getBucketRegion(bucketUrl))
        .bucketUrl(bucketUrl)
        .build();
  }

  /** Get provider from bucket URL. */
  private static Provider getProvider(String bucketUrl) {
    if (bucketUrl.matches(AWS_BUCKET_REGEX)) {
      return Provider.AWS;
    } else if (bucketUrl.matches(GCP_BUCKET_REGEX)) {
      return Provider.GCP;
    }
    return null;
  }

  /** Get the location where the storage bucket resides. */
  private static String getBucketRegion(String bucketUrl) {
    if (bucketUrl.matches(AWS_BUCKET_REGEX)) {
      // region is encoded in the storage url
      Pattern p = null;
      p = Pattern.compile(AWS_BUCKET_REGEX);
      Matcher m = p.matcher(bucketUrl);
      if (m.find()) {
        String region = m.group(3);
        if (region == null || region.isEmpty() || region.isBlank()) {
          return null;
        }
        return region.substring(0, region.length() - 1);
      }
    }
    return null;
  }

  /** Get bucket name from bucket URL. */
  private static String getBucketName(String buketUrl) {
    Pattern p = null;
    if (buketUrl.matches(AWS_BUCKET_REGEX)) {
      p = Pattern.compile(AWS_BUCKET_REGEX);
      Matcher m = p.matcher(buketUrl);
      if (m.find()) {
        return m.group(2);
      }
    } else if (buketUrl.matches(GCP_BUCKET_REGEX)) {
      p = Pattern.compile(GCP_BUCKET_REGEX);
      Matcher m = p.matcher(buketUrl);
      if (m.find()) {
        return m.group(2);
      }
    }
    return null;
  }

}
