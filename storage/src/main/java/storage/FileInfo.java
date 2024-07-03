package storage;

import java.nio.file.FileSystems;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.*;
import org.apache.commons.io.FilenameUtils;

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
  @Deprecated private String fileName;

  private String fileUrl;
  private BucketInfo bucketInfo;
  private String path;
  private String name;

  private static FileInfo parseLocalFileUrl(String fileUrl) {
    String absolutePath =
        FileSystems.getDefault().getPath(fileUrl).normalize().toAbsolutePath().toString();
    return FileInfo.builder()
        .isLocal(true)
        .fileUrl(absolutePath)
        .fileName(FilenameUtils.getName(fileUrl))
        .name(FilenameUtils.getName(fileUrl))
        .path(absolutePath)
        .build();
  }

  public static FileInfo parse(String fileUrl) {
    if (isLocalFile(fileUrl)) {
      return parseLocalFileUrl(fileUrl);
    } else {
      return parseCloudStorageFileUrl(fileUrl);
    }
  }

  private static FileInfo parseCloudStorageFileUrl(String fileUrl) {
    String bucketUrl = getBucketUrl(fileUrl);
    BucketInfo bucketInfo = BucketInfo.parse(bucketUrl);
    String fileName = getFileName(fileUrl);
    return FileInfo.builder()
        .isLocal(false)
        .fileName(fileName)
        .path(fileName)
        .name(FilenameUtils.getName(fileName))
        .fileUrl(fileUrl)
        .bucketInfo(bucketInfo)
        .build();
  }

  /**
   * Filename is confusing and returns:

   * <p>- for local files the filename (e.g. for "/home/user1/file.txt" -> "file1.txt"),
   *
   * <p>- but path for cloud files (e.g. for
   * "https://storage.cloud.google.com/region/folder1/file1.txt" -> "/folder1/file1.txt")
   *
   * <p>Replace with {@link #getName()} and {@link #getPath()} respectively
   *
   * @return as described above
   */
  @Deprecated
  public String getFileName() {
    return fileName;
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
