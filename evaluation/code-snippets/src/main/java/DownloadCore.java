import shared.Credentials;
import storage.Storage;
import storage.StorageImpl;

public class DownloadCore {

    public void download(Credentials credentials, String fileUrl) throws Exception {
        Storage storage = new StorageImpl(credentials);
        byte[] data = storage.read(fileUrl);
    }
}
