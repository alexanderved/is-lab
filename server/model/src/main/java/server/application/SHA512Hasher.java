package server.application;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512Hasher extends Hasher {
    private final MessageDigest md;

    public SHA512Hasher() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("SHA-512");
    }

    @Override
    public String hash(String s) {
        byte[] bytes = md.digest(s.getBytes());
        BigInteger num = new BigInteger(1, bytes);
        StringBuilder hashStringBuilder = new StringBuilder(num.toString(16));

        while (hashStringBuilder.length() < 128) {
            hashStringBuilder.insert(0, "0");
        }

        return hashStringBuilder.toString();
    }
}
