// HelloWorld.java
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecureObject implements Comparable, Serializable {

    private String username;
    private String password;
    private String encrypted_password;
    // private String encrypted_username;

    private static final String SECRET_KEY = "ThisIsASecretKey";
    private static final String INIT_VECTOR = "RandomInitVector";

    // constructor
    public SecureObject(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LOGIN: " +
                "[ username=" + username + " | " +
                "password=" + password +
                " ]";
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof SecureObject) {
            if (this.username.equals(((SecureObject) obj).getUsername()) && this.password.equals(((SecureObject) obj).getPassword()))
                return 0;
            else return -1;
        } else {
            return -1;
        }
    }



    // encryption function
    // uses the AES algorithm
    public SecureObject encode(){
        try{

            // Create an IvParameterSpec object using the initialization vector (IV).
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));

            // Create a SecretKeySpec object using the secret key and specifying the AES algorithm.
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            // Create a Cipher object instance using the AES algorithm, CBC mode, and PKCS5 padding scheme.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            // Initialize the Cipher object for encryption mode with the specified key and IV.
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            // Perform the encryption operation on the input (password) and obtain the encrypted byte array.
            byte[] encrypted = cipher.doFinal(password.getBytes());

            // Convert the encrypted byte array to a Base64-encoded string.
            encrypted_password = Base64.getEncoder().encodeToString(encrypted);

            password = encrypted_password;
            return this;

        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void decode() {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(password));
            password = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
