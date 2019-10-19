package api.net;

import com.github.scribejava.core.model.OAuth2AccessToken;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class SessionStorage {
    private OAuth2AccessToken accessToken;
    private File storageFile;
    private final Cipher cipher;
    private static final String key = "51C7A08D-D34D-4C07-BDB3-BF22B564C8D1";

    SessionStorage(String storageFileName) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES/CBC");
        storageFile = new File(storageFileName);
        if (storageFile.exists())
            decipherToken();
    }

    private void cipherToken() {
        //cipher.init(Cipher.ENCRYPT_MODE, key);
        try (FileOutputStream writer = new FileOutputStream(storageFile.getPath())) {
            byte[] cipheredBytes = cipher.doFinal(accessToken.getAccessToken().getBytes(StandardCharsets.UTF_8));
            writer.write(cipheredBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void decipherToken() {
        //cipher.init(Cipher.DECRYPT_MODE, key);
        try (FileInputStream reader = new FileInputStream(storageFile.getPath())) {
            byte[] cipheredBytes = reader.readAllBytes();
            accessToken = new OAuth2AccessToken(new String(cipher.doFinal(cipheredBytes), StandardCharsets.UTF_8));
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    void setAccessToken(OAuth2AccessToken accessTokenF) {
        this.accessToken = accessTokenF;
        cipherToken();
    }

    boolean isInitialised() {
        return storageFile.exists();
    }
}
