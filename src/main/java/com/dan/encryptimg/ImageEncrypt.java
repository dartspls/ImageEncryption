package com.dan.encryptimg;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;



public class ImageEncrypt {
    private static final String KEY = "770A8A65DA156D24EE2A093277530142"; // From assignment PDF
    private static final String PADDING = "PKCS5Padding";

    private IvParameterSpec getIV() {
        byte[] ivBytes = new byte[16]; // 128 bit IV
        new SecureRandom().nextBytes(ivBytes); // fill byte array with next available random bytes
        return new IvParameterSpec(ivBytes);
    }

    private SecretKeySpec createKey() {
        byte[] keyBytes = Base64.getDecoder().decode(ImageEncrypt.KEY);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    private void Encrypt(File plaintext, String algorithm, String outputFilePrefix, boolean useIV) {
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKey key = createKey();
            if(useIV) {
                cipher.init(Cipher.ENCRYPT_MODE, key, getIV());
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }

            File output = new File(outputFilePrefix + plaintext.getName());

            FileInputStream fis = new FileInputStream(plaintext);
            FileOutputStream fos = new FileOutputStream(output);
            byte[] plaintextData = fis.readAllBytes();
            BmpImage image = new BmpImage(plaintextData); // create BMP object to help with getting image header and body
            byte[] outBuffer;

            outBuffer = cipher.doFinal(image.getImgData()); // encrypt
            fos.write(image.getImgHeader()); // write img header
            fos.write(outBuffer); // write encrypted img data

            fos.flush();
            fos.close();
            fis.close();

        } catch (NoSuchAlgorithmException nsa) {
            System.err.println("No algorithm matching 'AES/ECB'");
        } catch (InvalidKeyException ik) {
            System.err.println("Invalid key provided");
        } catch (NoSuchPaddingException nsp) {
            System.err.println("No padding algorithm matching '" + PADDING + "'");
        }  catch (BadPaddingException | IllegalBlockSizeException bpe) {
            System.err.println("Data not padded correctly");
        } catch (FileNotFoundException fnf) {
            System.err.println("No file found with name '" + plaintext.getName() + "'");
        } catch (IOException ioe) {
            System.err.println("IO Exception: \n" + ioe.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Error using IV with unsupported algorithm.\n" + e.getMessage());
        }
    }

    private void run(String filename) {
        File plaintext = new File(filename);
        Encrypt(plaintext, "AES/ECB/PKCS5Padding", "ecb_encrypted_", false);
        Encrypt(plaintext, "AES/CBC/PKCS5Padding", "cbc_encrypted_", true);
        Encrypt(plaintext, "AES/CFB/PKCS5Padding", "cfb_encrypted_", true);
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: ImageEncrypt <path/filename>");
            return;
        }

        new ImageEncrypt().run(args[0]);
    }
}
