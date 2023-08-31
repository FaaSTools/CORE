package storage;

import lombok.*;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.FileSystems;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileInfo {

  public static final String AWS_FILE_REGEX = "(http|https)://(.*).s3.(.*?).?amazonaws.com/(.*)";
  public static final String GCP_FILE_REGEX = "(http|https)://storage.cloud.google.com/(.*?)/(.*)";

  private boolean isLocal;
  private String fileName;
  private String fileUrl;
  private BucketInfo bucketInfo;

  public static FileInfo parse(String fileUrl) {
    if (isLocalFile(fileUrl)) {
      return parseLocalFileUrl(fileUrl);
    } else {
      return parseCloudStorageFileUrl(fileUrl);
    }
  }

  private static FileInfo parseLocalFileUrl(String fileUrl) {
    String absolutePath =
        FileSystems.getDefault().getPath(fileUrl).normalize().toAbsolutePath().toString();
    String fileName = FilenameUtils.getName(fileUrl);
    return FileInfo.builder().isLocal(true).fileUrl(absolutePath).fileName(fileName).build();
  }

  private static FileInfo parseCloudStorageFileUrl(String fileUrl) {
    String bucketUrl = getBucketUrl(fileUrl);
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    String fileName = getFileName(fileUrl);
    return FileInfo.builder()
        .isLocal(false)
        .fileName(fileName)
        .fileUrl(fileUrl)
        .bucketInfo(bucketInfo)
        .build();
  }

  /** Returns true if the file is not a cloud storage url. */
  private static boolean isLocalFile(String fileUrl) {
    if (!fileUrl.matches(AWS_FILE_REGEX)
        && !fileUrl.matches(GCP_FILE_REGEX)) {
      return true;
    }
    return false;
  }

  /** Get file key from file URL. */
  private static String getFileName(String fileUrl) {
    if (isLocalFile(fileUrl)) {
      return null;
    }
    if (fileUrl.matches(AWS_FILE_REGEX)) {
      Pattern p = Pattern.compile(AWS_FILE_REGEX);
      Matcher m = p.matcher(fileUrl);
      if (m.find()) {
        return m.group(4);
      }
    } else if (fileUrl.matches(GCP_FILE_REGEX)) {
      Pattern p = Pattern.compile(GCP_FILE_REGEX);
      Matcher m = p.matcher(fileUrl);
      if (m.find()) {
        return m.group(3);
      }
    }
    return null;
  }

  private static String getBucketUrl(String fileUrl) {
    if (isLocalFile(fileUrl)) {
      return null;
    }
    String fileName = getFileName(fileUrl);
    String bucketUrl = fileUrl.substring(0, fileUrl.length() - fileName.length());
    return bucketUrl;
  }

}
