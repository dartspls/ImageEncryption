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

    /***
     * Create a 128 bit Initialisation Vector
     *
     * @return 128 bit initialisation vector
     */
    private IvParameterSpec getIV() {
        byte[] ivBytes = new byte[16]; // 128 bit IV
        new SecureRandom().nextBytes(ivBytes); // fill byte array with next available random bytes
        return new IvParameterSpec(ivBytes);
    }

    /**
     * Create a key from a string set at the top of the file
     *
     * @return key object
     */
    private SecretKeySpec createKey() {
        byte[] keyBytes = Base64.getDecoder().decode(ImageEncrypt.KEY);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    /**
     * Encrypt the content of an image with the given algorithm
     *
     * @param plaintext image file to encrypt
     * @param algorithm algorithm and mode. E.g "AES/CFB/PKCS5Padding"
     * @param outputFilePrefix prefix added to filename for output file
     * @param useIV whether to use an Initialisation Vector or not. Depends on algorithm used.
     */
    private void Encrypt(File plaintext, String algorithm, String outputFilePrefix, boolean useIV) {
        try{
            // set up cipher and key
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKey key = createKey();

            // if an IV is needed, create one and initialise the cipher object.
            // could also do this by parsing the 'algorithm' variable. This potentially makes it easier to add
            // more algorithms in the future though.
            if(useIV) {
                cipher.init(Cipher.ENCRYPT_MODE, key, getIV());
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }

            // set up file IO stuff and BMP image object.
            File output = new File(outputFilePrefix + plaintext.getName());
            FileInputStream fis = new FileInputStream(plaintext);
            FileOutputStream fos = new FileOutputStream(output);
            byte[] plaintextData = fis.readAllBytes();
            BmpImage image = new BmpImage(plaintextData); // create BMP object to help with getting image header and body
            byte[] outBuffer;

            outBuffer = cipher.doFinal(image.getImgData()); // encrypt
            fos.write(image.getImgHeader()); // write img header
            fos.write(outBuffer); // write encrypted img data

            // closing
            fos.flush();
            fos.close();
            fis.close();

        } catch (NoSuchAlgorithmException nsa) {
            System.err.println("No algorithm matching 'AES/ECB'");
        } catch (InvalidKeyException ik) {
            System.err.println("Invalid key provided");
        } catch (NoSuchPaddingException nsp) {
            System.err.println("No padding algorithm matching padding segment in '" + algorithm + "'");
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

    /**
     * Run the encryption program
     *
     * @param filename path/to/file.xxx
     */
    private void run(String filename) {
        File plaintext = new File(filename);
        if(!plaintext.exists()) {
            System.err.println("Error: No file found: " + filename);
            return;
        }
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
