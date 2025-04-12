package api.net;

import com.github.scribejava.core.model.OAuth2AccessToken;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

class SessionStorage {
    private final File storageFile;
    private final File keystoreFile;
    private final Cipher cipher;
    private final KeyStore keyStore;

    SessionStorage(String storageFileName, String keyStoreFileName)
            throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException,
            IOException, CertificateException {
        storageFile = new File(storageFileName);
        keystoreFile = new File(keyStoreFileName);

        cipher = Cipher.getInstance("AES");
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (keystoreFile.exists()) {
            try (InputStream keyStoreData = new FileInputStream(keystoreFile)) {
                keyStore.load(keyStoreData, "password".toCharArray());
            }
        }
        else {
            initKeyStorage();
        }
    }

    private void cipherToken(OAuth2AccessToken accessToken)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException,
            CertificateException, IOException {
        if (!keystoreFile.exists()) {
            initKeyStorage();
        }
        Key key = keyStore.getKey("key", "password".toCharArray());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        try (FileOutputStream writer = new FileOutputStream(storageFile.getPath())) {
            byte[] cipheredBytes = cipher.doFinal(accessToken.getAccessToken().getBytes(StandardCharsets.UTF_8));
            writer.write(cipheredBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String decipherToken()
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException {
        String token;
        Key key = keyStore.getKey("key", "password".toCharArray());
        cipher.init(Cipher.DECRYPT_MODE, key);
        try (FileInputStream reader = new FileInputStream(storageFile.getPath())) {
            byte[] cipheredBytes = reader.readAllBytes();
            token = new String(cipher.doFinal(cipheredBytes), StandardCharsets.UTF_8);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    private void initKeyStorage() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        keyStore.load(null, "password".toCharArray());
        if (storageFile.exists()) {
            storageFile.delete();
        }
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        SecretKey key = kg.generateKey();
        keyStore.setEntry("key",
                new KeyStore.SecretKeyEntry(key), new KeyStore.PasswordProtection("password".toCharArray()));
        try (FileOutputStream keyStoreOutputStream = new FileOutputStream(keystoreFile)) {
            keyStore.store(keyStoreOutputStream, "password".toCharArray());
        }
    }

    boolean isInitialised() {
        return storageFile.exists();
    }

    OAuth2AccessToken getAccessToken()
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException {
        return new OAuth2AccessToken(decipherToken());
    }

    void setAccessToken(OAuth2AccessToken accessToken)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException,
            CertificateException, IOException {
        cipherToken(accessToken);
    }

    void tearDown() {
        storageFile.delete();
        keystoreFile.delete();
    }
}
