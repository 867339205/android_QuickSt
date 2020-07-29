package com.quickst.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class md5 {
    public static String getMD5(String data){
        String s1 = data;
        for(int i = 0;i < 100;i++){
            s1 = get(s1);
        }
        return s1;
    }
    private static String get(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}
