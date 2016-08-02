package com.minsung.Converter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class Converter
{
	public HexEDCoder hex;
	public AESCoder aes;
	public static final double version = 1.0;
	
	public Converter() throws UnsupportedEncodingException
	{
		log("---------------------------------------");
		log("Thanks for using converter. Version: " + version);
		log("This supports to convert AES128 & Hex2Str.");
		log("Developer: Minsung Kim");
		
		log("Converter is initializing..");
		
		hex = new HexEDCoder();
		aes = new AESCoder(128, 10000);
		
		log("Producers intialized.");
		
		log("Starting to test producers..");
		String test = "TESTING1234";
		log("Some text: " + test);
		String hex2 = hex.stringToHex(test);
		log("Hex encrypted: " + hex2);
		log("Hex decrypted: " + hex.hexToString(hex2));
		
		log("Second - AES128 ED Test..");
		String encrypt = aes.encrypt(test);
		log("AES128 Encrypted: " + encrypt);
		log("AES128 Decrypted: " + aes.decrypt(encrypt));
		
		log("All function initialized, tested AES128 & Hex");
	}
	
	public String encrypt(String str)
	{
		return aes.encrypt(str);
	}
	
	public String decrypt(String encrypted)
	{
		return aes.decrypt(encrypted);
	}
	
	public String hex2str(String hex2)
	{
		return hex.hexToString(hex2);
	}
	
	public String str2hex(String input) throws UnsupportedEncodingException
	{
		return hex.stringToHex(input);
	}
	
	public void log(String m)
	{
		System.out.println(m);
	}
	
	public CoderType getType(Coder coder)
	{
		return coder.getType();
	}
	
	public String getCoderType(Coder coder)
	{
		return coder.getCoderType();
	}
}

enum CoderType
{
	default_coder_4A3CD3F3EAF3E4FAEF3E4F5EAF5EF,
	hex,
	aes_encrypting
}

class Coder extends Object
{
	private CoderType coder_type;
	private String coder_name;
	
	public Coder(CoderType type, String name)
	{
		coder_type = type;
		coder_name = name;
	}
	
	public String getName()
	{
		return this.coder_name;
	}
	
	public CoderType getType()
	{
		return this.coder_type;
	}
	
	public String getCoderType()
	{
		return coder_type.name();
	}
}

class AESCoder extends Coder
{
	private static final int KEY_SIZE = 128;
	private static final int ITERATION_COUNT = 10000;
	private static final String IV = "F27D5C9927726BCEFE7510B1BDD3D137";
	private static final String SALT = "3FF2EC019C627B945225DEBAD71A01B6985FE84C95A70EB132882F88C0A59A55";
	private static final String PASSPHRASE = "passPhrase passPhrase aes encoding algorithm";
	private static final String PLAIN_TEXT = "AES ENCODING ALGORITHM PLAIN TEXT";
	
	private final int keySize;
    private final int iterationCount;
    private final Cipher cipher;

    public AESCoder(int keySize, int iterationCount) {
    	super(CoderType.aes_encrypting, "AESCoder");
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw fail((Exception) e);
		}
    }

    public String encrypt(String salt, String iv, String passphrase,
            String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv,
                    plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        } catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    public String decrypt(String salt, String iv, String passphrase,
            String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv,
                    base64(ciphertext));
            return new String(decrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }
    
    public String encrypt(String str)
    {
    	return encrypt(SALT, IV, PASSPHRASE, str);
    }
    
    public String decrypt(String str)
    {
    	return decrypt(SALT, IV, PASSPHRASE, str);
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv,
            byte[] bytes) {
    	try {
			cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
			return cipher.doFinal(bytes);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(),
                    hex(salt), iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), "AES");
            return key;
        } catch(Exception e)
        {
        	throw fail(e);
        }
    }

    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }

    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }

    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }
}

class HexEDCoder extends Coder
{
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
	
	public HexEDCoder()
	{
		super(CoderType.hex, "HexEDCoder");
	}
	
	public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }
 
    public String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
                txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }
    
    private String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
}