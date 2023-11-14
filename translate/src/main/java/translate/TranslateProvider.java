package translate;

public interface TranslateProvider {

    TranslateResponse translate(String inputFile, String language) throws Exception;

}
