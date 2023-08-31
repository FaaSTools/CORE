import shared.Credentials;
import storage.Storage;
import storage.StorageImpl;

public class UploadCore {

    public void upload(Credentials credentials, byte[] data, String fileUrl) throws Exception {
        Storage storage = new StorageImpl(credentials);
        storage.write(data, fileUrl);
    }
}
