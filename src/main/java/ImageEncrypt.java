import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class ImageEncrypt {
    private static final String key = "770A8A65DA156D24EE2A093277530142"; // From assignment PDF

    private IvParameterSpec getIV() {
        byte[] ivBytes = new byte[16]; // 128 bit IV
        new SecureRandom().nextBytes(ivBytes); // fill byte array with next available random bytes
        return new IvParameterSpec(ivBytes);
    }
}
