package com.jh.utils.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author tangjianghua
 * @date 2020/2/5
 * @time 10:31
 */
public class RSAUtils {

    static{
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null){
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 生成密钥对
     * @param: null 
     * @return KeyPair
     * @exception 
     * @author: tangjianghua
     * @date 2020/2/5
     */
    public static KeyPair generateKeyPair() {
        try {
            // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            // 初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(1024,new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            //打印公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println(publicKeyString);
            //打印私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            System.out.println(privateKeyString);
            return keyPair;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * * 加密 *
     *
     * @param pk  加密的密钥 *
     * @param data 待加密的明文数据 *
     * @return 加密后的数据 *
     */
    public static byte[] encrypt(PublicKey pk, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();
            // 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
            // 加密块大小为127
            // byte,加密后为128个byte;因此共有2个加密块，第一个127
            // byte第二个为1个byte
            int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize){
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                }
                else{

                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
                }
                // 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到
                // ByteArrayOutputStream中，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了
                // OutputSize所以只好用dofinal方法。

                i++;
            }
            return raw;
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
    }


    /**
     * * 解密 *
     *
     * @param pk 解密的密钥 *
     * @param raw 已经加密的数据 *
     * @return 解密后的明文 *
     * @throws Exception
     */
    public static byte[] decrypt(PrivateKey pk, byte[] raw) {
        try {
            Cipher cipher = Cipher.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, pk);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;

            while (raw.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * * *
     *
     * @param args *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 生成密钥对
        KeyPair keyPair = generateKeyPair();
       // 测试公钥加密，私钥解密
        String test = "hello world";

        byte[] en_test = encrypt(keyPair.getPublic(), test.getBytes());
//        System.out.println("密文(公钥加密)："+ByteArrayUtil.byteArray2HexString(en_test));
        byte[] de_test = decrypt(keyPair.getPrivate(), en_test);
//        System.out.println("明文(私钥解密)："+ByteArrayUtil.byteArray2HexString(de_test));
        System.out.println("明文(私钥解密)："+new String(de_test));
        System.out.println(Integer.toHexString(16));
        System.out.println(Integer.toHexString('g'));
        System.out.println("2011-23-2323".substring(0,10));
    }
}
