import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
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

    private SecretKeySpec createKey(String keyText) {
        byte[] keyBytes = Base64.getDecoder().decode(keyText);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    private void ECB(File plaintext) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey key = createKey(KEY);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            File output = new File("encrypted_" + plaintext.getName());

            FileInputStream fis = new FileInputStream(plaintext);
            FileOutputStream fos = new FileOutputStream(output);
            byte[] inBuffer = new byte[4096]; // byte buffer for input
            byte[] outBuffer;
            int rc;

            while ((rc = fis.read(inBuffer)) != -1) {
                outBuffer = cipher.update(inBuffer, 0, rc);
                if(outBuffer != null) {
                    fos.write(outBuffer);
                }
            }
            outBuffer = cipher.doFinal();
            if(outBuffer != null) {
                fos.write(outBuffer);
            }

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
        }
    }

    private void run(String filename, String algoMode) {
        File plaintext = new File(filename);

        int x = 5;
    }
}
