package storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FileInfoTest {

  @Test
  public void shouldIdentifyLocalFileInfoFromPath() {
    String path = "/home/user1/Documents/file1.txt";
    FileInfo fileInfo = FileInfo.parse(path);
    assertEquals("file1.txt", fileInfo.getFileName());
    assertEquals("file1.txt", fileInfo.getName());
    assertNull(fileInfo.getBucketInfo());
    assertEquals(path, fileInfo.getFileUrl());
    assertTrue(fileInfo.isLocal());
    assertEquals("/home/user1/Documents/file1.txt", fileInfo.getPath());
  }

    @Test
    public void shouldIdentifyFileInfoFromAwsBucket() {
        String url = "https://baasless-input-us-e1.s3.amazonaws.com/";
        FileInfo fileInfo = FileInfo.parse(url);
        assertNotNull(fileInfo.getBucketInfo());
        assertEquals("", fileInfo.getFileName());
        assertEquals("", fileInfo.getPath());
        assertEquals("", fileInfo.getName());
        assertEquals(url, fileInfo.getFileUrl());
        assertFalse(fileInfo.isLocal());
    }

  @Test
  public void shouldIdentifyFileInfoFromSimpleAwsFile() {
    String url = "https://baasless-input-us-e1.s3.amazonaws.com/file1.txt";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("file1.txt", fileInfo.getFileName());
    assertEquals("file1.txt", fileInfo.getPath());
    assertEquals("file1.txt", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }

  @Test
  public void shouldIdentifyFileInfoFromComplexAwsFile() {
    String url = "https://baasless-input-us-e1.s3.amazonaws.com/directory1/directory2/file1.txt";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("directory1/directory2/file1.txt", fileInfo.getFileName());
    assertEquals("directory1/directory2/file1.txt", fileInfo.getPath());
    assertEquals("file1.txt", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }

  @Test
  public void shouldIdentifyFileInfoFromAwsDirectory() {
    String url = "https://baasless-input-us-e1.s3.amazonaws.com/directory1/directory2/";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("directory1/directory2/", fileInfo.getFileName());
    assertEquals("directory1/directory2/", fileInfo.getPath());
    assertEquals("", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }

    @Test
    public void shouldIdentifyFileInfoFromGcpBucket() {
        String url = "https://storage.cloud.google.com/europe-west1-intents/";
        FileInfo fileInfo = FileInfo.parse(url);
        assertNotNull(fileInfo.getBucketInfo());
        assertEquals("", fileInfo.getFileName());
        assertEquals("", fileInfo.getPath());
        assertEquals("", fileInfo.getName());
        assertEquals(url, fileInfo.getFileUrl());
        assertFalse(fileInfo.isLocal());
    }

  @Test
  public void shouldIdentifyFileInfoFromSimpleGcpFile() {
    String url =
        "https://storage.cloud.google.com/europe-west1-intents/file1.txt";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("file1.txt", fileInfo.getFileName());
    assertEquals("file1.txt", fileInfo.getPath());
    assertEquals("file1.txt", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }

  @Test
  public void shouldIdentifyFileInfoFromComplexGcpFile() {
    String url = "https://storage.cloud.google.com/europe-west1-intents/directory1/directory2/file1.txt";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("directory1/directory2/file1.txt", fileInfo.getFileName());
    assertEquals("directory1/directory2/file1.txt", fileInfo.getPath());
    assertEquals("file1.txt", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }

  @Test
  public void shouldIdentifyFileInfoFromGcpDirectory() {
    String url = "https://storage.cloud.google.com/europe-west1-intents/directory1/directory2/";
    FileInfo fileInfo = FileInfo.parse(url);
    assertNotNull(fileInfo.getBucketInfo());
    assertEquals("directory1/directory2/", fileInfo.getFileName());
    assertEquals("directory1/directory2/", fileInfo.getPath());
    assertEquals("", fileInfo.getName());
    assertEquals(url, fileInfo.getFileUrl());
    assertFalse(fileInfo.isLocal());
  }
}
