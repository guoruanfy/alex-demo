package com.alex.demo.java.cryptor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class RSASignature {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSASignature.class);
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    public static final String ENCODING = "utf-8";
    public static final String X509 = "X.509";

    /**
     * 获取私钥
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key.getBytes(ENCODING));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 获取公钥
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        return pubKey;
    }

    /**
     * RSA私钥签名
     *
     * @param content 待签名数据
     * @param privateKey 私钥
     * @return 签名值
     */
    public static String signByPrivateKey(String content, String privateKey) {
        try {
            PrivateKey priKey = getPrivateKey(privateKey);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(content.getBytes(ENCODING));
            byte[] signed = signature.sign();
            return new String(UrlBase64.encode(signed), ENCODING);
        } catch (Exception e) {
            LOGGER.warn("sign error, content: {}, priKey: {}", new Object[] {
                    content, privateKey
            });
            LOGGER.error("sign error", e);
        }
        return null;
    }

    /**
     * RSA公钥验签，签名UrlBase64编码
     *
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 公钥, 是用了Base64编码的
     * @return 布尔值
     */
    public static boolean verifySignByPublicKey(String content, String sign, String publicKey) {
        try {
            PublicKey pubKey = getPublicKey(publicKey);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(ENCODING));
            return signature.verify(UrlBase64.decode(sign.getBytes(ENCODING)));
        } catch (Exception e) {
            LOGGER.error("sign error, content: {}, sign:len:{},{}, pubKey: {}",
                    new Object[] {content, sign.length(), sign, publicKey});
            LOGGER.error("sign error", e);
        }
        return false;
    }

    public static String encrypt(String publicKey, String plainText) {
        try {
            return encrypt(getPublicKey(publicKey), plainText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用公钥对明文进行加密，返回BASE64编码的字符串
     *
     * @param publicKey
     * @param plainText
     * @return
     */
    public static String encrypt(PublicKey publicKey, String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes(ENCODING));
            return new String(Base64.encodeBase64(enBytes));
        } catch (Exception e) {
            LOGGER.error("rsa encrypt exception={}", e);
        }
        return null;
    }

    public static String decrypt(String privateKey, String enStr) {
        try {
            return decrypt(getPrivateKey(privateKey), enStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用私钥对明文密文进行解密
     *
     * @param privateKey
     * @param enStr
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String enStr) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64.decodeBase64(enStr));
            return new String(deBytes, ENCODING);
        } catch (Exception e) {
            LOGGER.error("rsa decrypt exception={}", e);
        }
        return null;
    }

    public static String getSignContent(final Map<String, String> params) {
        if (params instanceof TreeMap) {
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (StringUtils.isEmpty(entry.getKey()) || StringUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                if (!isFirst) {
                    builder.append("&");
                }
                builder.append(entry.getKey()).append("=").append(entry.getValue());
                isFirst = false;
            }
            return builder.toString();
        } else {
            Map<String, String> sortedMap = new TreeMap<String, String>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return getSignContent(sortedMap);
        }
    }

    public static String signByPrivateKey(final Map<String, String> params, String privateKey) {
        return signByPrivateKey(getSignContent(params), privateKey);
    }

    public static boolean verifySignByPublicKey(final Map<String, String> params, String sign, String publicKey) {
        return verifySignByPublicKey(getSignContent(params), sign, publicKey);
    }
}
