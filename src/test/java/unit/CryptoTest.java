package unit;

import iaik.security.provider.IAIK;
import lib.TestDataLib;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import unit.AuthenticationTest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static org.junit.Assert.assertNotNull;

public class CryptoTest {

    private Log mLog = LogFactory.getLog(AuthenticationTest.class);

    private TestDataLib tl = new TestDataLib();

    private String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCrgHC5EO97ttPsD0R/mSSR6h0yNIR+onq9nLjzmFX5vDMNb8fqr+7qrfAXz2VOAmoxROEbk2omxkuDJbzDPTiarieVUm1Xxigzfy7ZQ6ChXUmyTi7Upt919xLGCfWf7NsDEAdtVgTTimPMWue03YxJaWzDMMhAnU84ZVSo3QVR7Ue9W48EFSeBxdY8y9pI/wp3Qfh/JqoUwAwFaWL4rIjSB0oX3muRIgbAscf12k6VICxnVWksNsfCHVcAaqiSV3Wy6d13TY0npWeDgnrQeyr+/ecobIhR0k8+1KUMbHDA+sbOyrXr8DD41ggJGFM5hrZjLsIwq33W99xzOypJrQVJAgMBAAECggEATHZYS3qMe0jZFT79WDJMBq4tVCvsApWoSY9tOlXpxw0sacozhKPpzkG8cLErxmr3M341Ktk/k4gHNLKHhThvWy9YQI/eZirX05XZqk4neKoh6FhJMtAvguWkPh1EoIe2YZgs54dZYYMq2XqSIaZBHJ3fOust7Pj1z/wFkb/w1kRMLHXLk3b700cZGjJN0MJPhXNwhbh1bcd46kxp3ZzEm6e57N4a7uBRJPnxYV7NfOM8Jp9aN97hQH8s9BkL8BSYEWJQ2Hi1AAFPuxwqMikN5v+wZQY/3ptuwd5wx8HpOB2OIznLzXcCNBgBlXpr9OuzdAI/GWeTE2/bMA/PgRXDgQKBgQDYvd4aJAa5c01UnOF2i/5UF9PXB18s1P4868p/IKEdKmowzSW75PhUTcdB2/x9nKQxxEDpywUOFAx7WXydGWT4pDAAjN9nFZIx6nipIIi1HBXd9nD7ys6JNdNLbYrFQ+Gu9FZaXYLS0JT63AaK7NclxBaLiuGjhyO73SNqb8hiWQKBgQDKkNVlBgYB9K/AptnfzKrHJOv4paL/IpLZtlDZwCNNXx5lEHCvxOBs0/uvMHpklfbrQxFn/BYpKm40/6gjGdQcgcBaaE8+ODgz0MeJR7FYVWd8IU3R0p+H7D1i7Gw9oQNYWRw3rRFMAu25cpmdnqJm6S8NtWL9Tgq5Fp01C3r8cQKBgQC6wW9dKum4Zged/sevNBVjNFzOjYWOxcCDkdCcFet9jAyQHFSFQFhRm+szbnBzc8NvNmGQkeS7Kr9Okd70Jut/60mJT9k3o7ii1hw97tCc7aRR64WTED9Cr2RvO+Y077bI37SAjM6rBBs6xNfGYSlJW1rpxjOCinFZ8MQRdFw/kQKBgAy40+4oVrBAwdkDoaNgo0kmlHtlS7tiXH636JQEcTMmyi3mvm0LnFy5Y5E5JjazBHL6H1m8h1fGhm1lsRWjRaMUsxP6gR+TQFRgxGdmzUiS9JDjNpP6+nH68FAKMTIdrqwZzJ/iirePZ4bCH8u6btCvzHi775+hCyNcvDrcobjBAoGBAMzndST3kQLdJLPuzRPcSrw4OBZgfENUXnOIav60kg5G3x6f0USOL4PNLrFWexyo1NZCgT/lKq7VBRMPZFgAjDqu8O668clAoeV6VpWmna3LWkEj6VFDXknbudlpXSxMRKUkJZsJRPH/nc32l5sf/eWG5JlOqtxcki5VYgQ7CiVX";

    private String challenge = "XIP57KnbS+6Cw0Vuw/YKfBgkf8FHH5MLk7bXa46P2yWKlw4pwuEzkWMztj9w3shvpRBixMhDBtkm9Lxenu9Wq/hx7M1aCpImiUa1nskoTfVMnr+BtkkTQmv6V0X5oeuLNK7TKI3UEktOpl6/wPLTT6AMia7sCwzf4va7zjpzQ4Ij3lJdU5Bt/Jb43EsfMI2gRmpLNc9hQd3LfR22ucJPMcyqpG1bsreQ9TvaANG7WFtmYRPbSYKs+kV9SsqJi2TlkpOvGMCSoHtUatp88BU829x0Q8RWPg3xSeTCAP/IoLNDdxalvTx+7H8T/o0HOipjSt+Yn3ufozJ1wpYwDqVk+g==";


    @Test
    public void testIssue21() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        IAIK.addAsProvider();
        PrivateKey pk = tl.string2privatekey(privateKey);
        String data = tl.decode(challenge, pk);
        assertNotNull(data);
        mLog.debug("Challenge: " + data);
    }

}
