package com.aefyr.pseudoapksigner;

import java.io.*;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class Utils {

    public static byte[] getFileHash(File file, String hashingAlgorithm) throws Exception {
        try (var inputStream = new FileInputStream(file)) {
            return getFileHash(inputStream, hashingAlgorithm);
        }
    }

    static byte[] getFileHash(InputStream inputStream, String hashingAlgorithm) throws Exception {
        var messageDigest = MessageDigest.getInstance(hashingAlgorithm);
        var buffer = new byte[1024 * 1024];
        int read;
        while ((read = inputStream.read(buffer)) > 0) {
            messageDigest.update(buffer, 0, read);
        }
        return messageDigest.digest();
    }

    static byte[] hash(byte[] bytes, String hashingAlgorithm) throws Exception {
        var messageDigest = MessageDigest.getInstance(hashingAlgorithm);
        messageDigest.update(bytes);
        return messageDigest.digest();
    }

    static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, 0);
    }

    static void copyStream(InputStream from, OutputStream to) throws IOException {
        var buf = new byte[1024 * 1024];
        int len;
        while ((len = from.read(buf)) > 0) {
            to.write(buf, 0, len);
        }
    }

    static byte[] sign(String hashingAlgorithm, PrivateKey privateKey, byte[] message) throws Exception {
        var sign = Signature.getInstance(hashingAlgorithm + "withRSA");
        sign.initSign(privateKey);
        sign.update(message);
        return sign.sign();
    }

    static RSAPrivateKey readPrivateKey(File file) throws Exception {
        var keySpec = new PKCS8EncodedKeySpec(readFile(file));
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    static byte[] readFile(File file) throws IOException {
        var fileBytes = new byte[(int) file.length()];
        try (var inputStream = new FileInputStream(file)) {
            inputStream.read(fileBytes);
        }
        return fileBytes;
    }
}
