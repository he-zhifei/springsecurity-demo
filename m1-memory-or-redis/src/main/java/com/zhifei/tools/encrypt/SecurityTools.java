package com.zhifei.tools.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类：Base64编解码、Md5、DES、AES（CBC模式，PV和随机策略硬编码）、RSA
 *
 * Rsa工具类：
 * <p>
 * 1.公钥加密结合私钥解密使用（登录表单的密码）
 * 2.私钥加密结合公钥解密使用（jwt）
 * </p>
 *
 * @author He Zhifei
 * @date 2020/8/25 15:46
 */
public class SecurityTools {

    /**
     * 默认字符集
     */
    private static final String CHARSET = StandardCharsets.UTF_8.name();

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTools.class);

    /////////////////////////////////////////////// Base64 ///////////////////////////////////////////////

    /**
     * Base64编码
     * @param bytes 字节数组
     * @return 编码后的字节数组
     */
    public static byte[] base64Encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    /**
     * Base64解码
     * @param bytes 字节数组
     * @return 解码后的字节数组
     */
    public static byte[] base64Decode(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    /**
     * Base64编码
     * @param str 字符串
     * @return 编码后的字符串
     */
    public static String base64EncodeToStr(String str) {
        String encode = null;
        try {
            encode = new String(base64Encode(str.getBytes(CHARSET)), CHARSET);
        } catch (Exception e) {
            LOGGER.error("SecurityTools.base64EncodeToStr error: ", e);
        }
        return encode;
    }

    /**
     * Base64解码
     * @param str 字符串
     * @return 解码后的字符串
     */
    public static String base64DecodeToStr(String str) {
        String decode = null;
        try {
            decode = new String(base64Decode(str.getBytes(CHARSET)), CHARSET);
        } catch (Exception e) {
            LOGGER.error("SecurityTools.base64DecodeToStr error: ", e);
        }
        return decode;
    }

    /////////////////////////////////////////////// MD5 ///////////////////////////////////////////////

    /**
     * MD5加密字符串
     * @param str 被加密的字符串
     * @return 密文
     */
    public static String md5(String str) {
        return new Md5Tools().generate(str);
    }

    /**
     * MD5加密输入流
     * @param inputStream 被加密的输入流
     * @return 密文
     */
    public static String md5(InputStream inputStream) {
        return new Md5Tools().generate(inputStream);
    }

    /**
     * MD5加密文件
     * @param file 被加密的文件
     * @return 密文
     */
    public static String md5(File file) {
        String encrypt = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            encrypt = new Md5Tools().generate(inputStream);
        } catch (Exception e) {
            LOGGER.error("SecurityTools.md5(File) error: ", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("SecurityTools.md5(File) inputStream.close error: ", e);
            }
        }
        return encrypt;
    }

    /////////////////////////////////////////////// DES ///////////////////////////////////////////////

    /**
     * DES加密
     * @param data 被加密的字节数组
     * @param key 密钥字节数组
     * @return 加密后的字节数组
     */
    public static byte[] desEncrypt(byte[] data, byte[] key) {
        return new DesTools().encrypt(data, key);
    }

    /**
     * DES解密
     * @param data 被解密的字节数组
     * @param key 密钥字节数组
     * @return 解密后的字节数组
     */
    public static byte[] desDecrypt(byte[] data, byte[] key) {
        return new DesTools().decrypt(data, key);
    }

    /**
     * DES加密（常用）
     * @param str 被加密的字符串
     * @param key 密钥
     * @return 加密后再通过Base64编码后的字符串
     */
    public static String desEncryptBase64(String str, String key) {
        return new DesTools().encrypt(str, key);
    }

    /**
     * DES解密（常用）
     * @param str 被解密的字符串
     * @param key 密钥
     * @return 通过Base64解码再解密后的字符串
     */
    public static String desDecryptBase64(String str, String key) {
        return new DesTools().decrypt(str, key);
    }

    /////////////////////////////////////////////// AES ///////////////////////////////////////////////

    /**
     * AES的CBC模式加密
     * @param data 被加密的字节数组
     * @param key 密钥字节数组
     * @return 加密后的字节数组
     */
    public static byte[] aesEncrypt(byte[] data, byte[] key) {
        return new AesTools().encrypt(data, key);
    }

    /**
     * AES的CBC模式解密
     * @param data 被解密的字节数组
     * @param key 密钥字节数组
     * @return 解密后的字节数组
     */
    public static byte[] aesDecrypt(byte[] data, byte[] key) {
        return new AesTools().decrypt(data, key);
    }

    /**
     * AES的CBC模式加密（常用）
     * @param str 被加密的字符串
     * @param key 密钥
     * @return 加密后再通过Base64编码后的字符串
     */
    public static String aesEncryptBase64(String str, String key) {
        return new AesTools().encrypt(str, key);
    }

    /**
     * AES的CBC模式解密（常用）
     * @param str 被解密的字符串
     * @param key 密钥
     * @return 通过Base64解码再解密后的字符串
     */
    public static String aesDecryptBase64(String str, String key) {
        return new AesTools().decrypt(str, key);
    }

    /////////////////////////////////////////////// RSA ///////////////////////////////////////////////

    /**
     * 生成RSA公私钥对
     * @param keySize 常用1024或2048
     * @return 密钥对
     */
    public static KeyPair rsaKeyPair(int keySize) {
        return new RsaTools().genKeyPair(keySize);
    }

    /**
     * 获取密钥对中的公钥字符串
     * @param keyPair 密钥对
     * @return 公钥字符串
     */
    public static String rsaPublicKeyStr(KeyPair keyPair) {
        return new RsaTools().getPublicKey(keyPair);
    }

    /**
     * 获取密钥对中的私钥字符串
     * @param keyPair 密钥对
     * @return 私钥字符串
     */
    public static String rsaPrivateKeyStr(KeyPair keyPair) {
        return new RsaTools().getPrivateKey(keyPair);
    }

    /**
     * 公钥字符串转公钥对象
     * @param publicKey 公钥字符串
     * @return 公钥对象
     */
    public static RSAPublicKey rsaPublicKey(String publicKey) {
        return new RsaTools().fromPublicKeyStr(publicKey);
    }

    /**
     * 私钥字符串转私钥对象
     * @param privateKey 私钥字符串
     * @return 私钥对象
     */
    public static RSAPrivateKey rsaPrivateKey(String privateKey) {
        return new RsaTools().fromPrivateKeyStr(privateKey);
    }

    /**
     * 公钥加密
     * @param str
     * @param publicKey
     * @return
     */
    public static String rsaEncryptByPublicKey(String str, String publicKey) {
        return new RsaTools().publicKeyAction(str, publicKey, true);
    }

    /**
     * 私钥解密
     * @param str
     * @param privateKey
     * @return
     */
    public static String rsaDecryptByPrivateKey(String str, String privateKey) {
        return new RsaTools().privateKeyAction(str, privateKey, false);
    }

    /**
     * 私钥加密
     * @param str
     * @param privateKey
     * @return
     */
    public static String rsaEncryptByPrivateKey(String str, String privateKey) {
        return new RsaTools().privateKeyAction(str, privateKey, true);
    }

    /**
     * 公钥解密
     * @param str
     * @param publicKey
     * @return
     */
    public static String rsaDecryptByPublicKey(String str, String publicKey) {
        return new RsaTools().publicKeyAction(str, publicKey, false);
    }

    /////////////////////////////////////////////// 静态内部类 ///////////////////////////////////////////////

    /**
     * md5加密工具类
     */
    private static class Md5Tools {

        /**
         * 加密算法
         */
        private static final String DIGEST_NAME = "MD5";

        /**
         * md5加密
         * @param str 明文
         * @return
         */
        private String generate(String str) {
            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            String encrypt = null;
            try {
                byte[] bytes = str.getBytes();
                MessageDigest md = MessageDigest.getInstance(DIGEST_NAME);
                md.update(bytes);
                byte[] mdByte = md.digest();
                int len = mdByte.length;
                char[] charArr = new char[len * 2];
                int k = 0;
                for (int i = 0; i < len; i++) {
                    byte b = mdByte[i];
                    charArr[(k++)] = hexDigits[(b >>> 4 & 0xF)];
                    charArr[(k++)] = hexDigits[(b & 0xF)];
                }
                encrypt = new String(charArr);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.Md5Tools.generate(String) error: ", e);
            }
            return encrypt;
        }

        /**
         * md5加密输入流
         * @param inputStream 输入流
         * @return
         */
        private String generate(InputStream inputStream) {
            byte[] buffer = new byte[8192];
            int len = 0;
            String encrypt = null;
            try {
                MessageDigest md = MessageDigest.getInstance(DIGEST_NAME);
                while ((len = inputStream.read(buffer)) != -1) {
                    md.update(buffer, 0, len);
                }
                byte[] mdByte = md.digest();
                BigInteger bi = new BigInteger(1, mdByte);
                encrypt = bi.toString(16);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.Md5Tools.generate(InputStream) error: ", e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("SecurityTools.Md5Tools.generate(InputStream) inputStream close error: ", e);
                }
            }
            return encrypt;
        }
    }

    /**
     * DES加密工具类
     */
    private static class DesTools {

        /**
         * 加密算法
         */
        private static final String DIGEST_NAME = "DES";

        /**
         * DES算法加解密
         *
         * @param data 明文/密文字节数组
         * @param key 密钥字节数组
         * @param cipherMode 可选值Cipher.ENCRYPT_MODE、Cipher.DECRYPT_MODE
         * @return 加密/解密后字节数组
         */
        private byte[] des(byte[] data, byte[] key, int cipherMode) {
            byte[] bytes = null;
            try {
                // 生成一个可信任的随机数源
                SecureRandom secureRandom = new SecureRandom();

                // 从原始密钥数据创建DESKeySpec对象
                DESKeySpec dks = new DESKeySpec(key);

                // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DIGEST_NAME);
                SecretKey secretKey = keyFactory.generateSecret(dks);

                // Cipher对象实际完成加密操作
                Cipher cipher = Cipher.getInstance(DIGEST_NAME);

                // 用密钥初始化Cipher对象
                cipher.init(cipherMode, secretKey, secureRandom);

                bytes = cipher.doFinal(data);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.DesTools.aes(byte[], byte[], int) error: ", e);
            }
            return bytes;
        }

        /**
         * 根据原数据的byte数组和秘钥的byte数组进行加密
         * @param data 原数据的byte数组
         * @param key 秘钥的byte数组
         * @return
         * @throws Exception
         */
        private byte[] encrypt(byte[] data, byte[] key) {
            return des(data, key, Cipher.ENCRYPT_MODE);
        }

        /**
         * 根据加密数据的byte数组和秘钥的byte数组进行解密
         * @param data 加密数据的byte数组
         * @param key 秘钥的byte数组
         * @return
         * @throws Exception
         */
        private byte[] decrypt(byte[] data, byte[] key) {
            return des(data, key, Cipher.DECRYPT_MODE);
        }

        /**
         * 根据原数据和秘钥进行加密
         * @param str 原数据
         * @param key 秘钥
         * @return
         * @throws Exception
         */
        private String encrypt(String str, String key) {
            String encrypt = null;
            try {
                byte[] bytes = encrypt(str.getBytes(CHARSET), key.getBytes(CHARSET));
                encrypt = new String(base64Encode(bytes), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.DesTools.encrypt(String, String) error: ", e);
            }
            return encrypt;
        }

        /**
         * 根据加密数据和秘钥进行解密
         * @param str 加密数据
         * @param key 秘钥
         * @return
         * @throws Exception
         */
        private String decrypt(String str, String key) {
            String decrypt = null;
            try {
                byte[] bytes = base64Decode(str.getBytes(CHARSET));
                decrypt = new String(decrypt(bytes, key.getBytes(CHARSET)), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.DesTools.decrypt(String, String) error: ", e);
            }
            return decrypt;
        }
    }

    /**
     * AES加密工具类
     */
    private static class AesTools {

        /**
         * CBC模式
         */
        private static final String PADDING = "AES/CBC/PKCS5Padding";

        /**
         * 向量
         */
        private static final String IV ="DOSDFW08SL0hJS==";

        /**
         * 加密算法
         */
        private static final String DIGEST_NAME = "AES";

        /**
         * SecureRandom的策略
         */
        private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

        /**
         * AES算法的CBC模式加解密
         *
         * @param data 明文/密文字节数组
         * @param key 密钥字节数组
         * @param cipherMode 可选值Cipher.ENCRYPT_MODE、Cipher.DECRYPT_MODE
         * @return 加密/解密后字节数组
         */
        private byte[] aes(byte[] data, byte[] key, int cipherMode) {
            byte[] bytes = null;
            try {
                //创建AES的Key生产者
                KeyGenerator keyGenerator = KeyGenerator.getInstance(DIGEST_NAME);

                //根据用户密码，生成一个密钥
                SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
                random.setSeed(key);
                keyGenerator.init(128, random);
                SecretKey secretKey = keyGenerator.generateKey();

                //返回基本编码格式的密钥
                byte[] encoded = secretKey.getEncoded();

                //转换为AES专用密钥
                SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, DIGEST_NAME);

                //创建密码器
                Cipher cipher = Cipher.getInstance(PADDING);

                //CBC模式创建向量
                IvParameterSpec ivs = new IvParameterSpec(IV.getBytes());

                //初始化为解密模式的密码器
                cipher.init(cipherMode, secretKeySpec, ivs);

                bytes = cipher.doFinal(data);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.AesTools.aes error: ", e);
            }
            return bytes;
        }

        /**
         * AES算法的CBC模式加密
         *
         * @param data 待加密的字节数组
         * @param key 密钥的字节数组
         * @return 加密后的字节数组
         */
        private byte[] encrypt(byte[] data, byte[] key) {
            return aes(data, key, Cipher.ENCRYPT_MODE);
        }

        /**
         * AES算法的CBC模式解密
         *
         * @param data 密文字节数组
         * @param key 密钥的字节数组
         * @return 解密后字节数组
         */
        private byte[] decrypt(byte[] data, byte[] key) {
            return aes(data, key, Cipher.DECRYPT_MODE);
        }

        /**
         * AES算法的CBC模式加密
         *
         * @param data 明文
         * @param key 密钥
         * @return 密文
         */
        private String encrypt(String data, String key) {
            String encrypt = null;
            try {
                byte[] bytes = encrypt(data.getBytes(CHARSET), key.getBytes(CHARSET));
                encrypt = new String(base64Encode(bytes), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.AesTools.encrypt(String, String) error: ", e);
            }
            return encrypt;
        }

        /**
         * AES算法的CBC模式解密
         *
         * @param data 密文
         * @param key 密钥
         * @return 明文
         */
        private String decrypt(String data, String key) {
            String decrypt = null;
            try {
                byte[] bytes = base64Decode(data.getBytes(CHARSET));
                decrypt = new String(decrypt(bytes, key.getBytes(CHARSET)), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.AesTools.decrypt(String, String) error: ", e);
            }
            return decrypt;
        }

    }

    /**
     * RSA加密工具类
     */
    private static class RsaTools {

        /**
         * 算法名称
         */
        private static final String DIGEST_NAME = "RSA";

        /**
         * 随机生成密钥对
         *
         * @return 密钥对
         */
        private KeyPair genKeyPair(int keySize) {
            KeyPair keyPair = null;
            try {
                // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
                KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(DIGEST_NAME);

                // 初始化密钥对生成器，密钥大小为 96 - 2048 位
                keyPairGen.initialize(keySize, new SecureRandom());

                // 生成一个密钥对，保存在keyPair中
                keyPair = keyPairGen.generateKeyPair();
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.genKeyPair error: ", e);
            }
            return keyPair;
        }

        /**
         * 获取公钥
         *
         * @param keyPair 密钥对
         * @return 公钥字符串
         */
        private String getPublicKey(KeyPair keyPair) {
            String publicKey = null;
            try {
                publicKey = new String(base64Encode(keyPair.getPublic().getEncoded()), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.getPublicKey error: ", e);
            }
            return publicKey;
        }

        /**
         * 获取私钥
         *
         * @param keyPair 密钥对
         * @return 私钥字符串
         */
        private String getPrivateKey(KeyPair keyPair) {
            String privateKey = null;
            try {
                privateKey = new String(base64Encode(keyPair.getPrivate().getEncoded()), CHARSET);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.getPrivateKey error: ", e);
            }
            return privateKey;
        }

        /**
         * 公钥字符串转公钥对象
         * @param publicKey 公钥字符串
         * @return 公钥对象
         */
        private RSAPublicKey fromPublicKeyStr(String publicKey) {
            RSAPublicKey rsaPublicKey = null;
            try {
                byte[] decoded = base64Decode(publicKey.getBytes(CHARSET));
                rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance(DIGEST_NAME).generatePublic(new X509EncodedKeySpec(decoded));
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.fromPublicKeyStr error: ", e);
            }
            return rsaPublicKey;
        }

        /**
         * 私钥字符串转私钥对象
         * @param privateKey 私钥字符串
         * @return 私钥对象
         */
        private RSAPrivateKey fromPrivateKeyStr(String privateKey) {
            RSAPrivateKey rsaPrivateKey = null;
            try {
                byte[] decoded = base64Decode(privateKey.getBytes(CHARSET));
                rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance(DIGEST_NAME).generatePrivate(new PKCS8EncodedKeySpec(decoded));
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.fromPrivateKeyStr error: ", e);
            }
            return rsaPrivateKey;
        }

        /**
         * 公钥加解密
         * @param str 明文/密文
         * @param publicKey 公钥
         * @param isEncrypt 加密还是解密
         * @return 公钥加密或解密后数据
         */
        private String publicKeyAction(String str, String publicKey, boolean isEncrypt) {
            String result = null;
            try {
                Cipher cipher = Cipher.getInstance(DIGEST_NAME);
                cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, fromPublicKeyStr(publicKey));
                result = getStr(str, cipher, isEncrypt);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.publicKeyAction error: ", e);
            }
            return result;
        }

        /**
         * 私钥加解密
         * @param str 明文/密文
         * @param privateKey 密钥
         * @param isEncrypt 加密还是解密
         * @return 私钥加密或解密后数据
         */
        private String privateKeyAction(String str, String privateKey, boolean isEncrypt) {
            String result = null;
            try {
                Cipher cipher = Cipher.getInstance(DIGEST_NAME);
                cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, fromPrivateKeyStr(privateKey));
                result = getStr(str, cipher, isEncrypt);
            } catch (Exception e) {
                LOGGER.error("SecurityTools.RsaTools.privateKeyAction error: ", e);
            }
            return result;
        }

        /**
         * 处理公私钥加解密的结果
         * @param str
         * @param cipher
         * @param isEncrypt
         * @return
         */
        private String getStr(String str, Cipher cipher, boolean isEncrypt) {
            String result = null;
            try {
                result = isEncrypt ? new String(base64Encode(cipher.doFinal(str.getBytes(CHARSET))), CHARSET) :
                        new String(cipher.doFinal(base64Decode(str.getBytes(CHARSET))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
