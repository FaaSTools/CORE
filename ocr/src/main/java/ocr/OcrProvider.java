package ocr;

public interface OcrProvider {

  OcrResponse extract(String inputFile) throws Exception;

}
