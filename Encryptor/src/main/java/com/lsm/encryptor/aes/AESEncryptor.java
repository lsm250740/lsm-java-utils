package com.lsm.encryptor.aes;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.lsm.encryptor.CryptoException;
import com.lsm.encryptor.Encryptor;

/**
 * https://www.baeldung.com/java-aes-encryption-decryption sample with using
 * certificate files https://www.baeldung.com/java-bouncy-castle 256-bit
 * encryption is a data/file encryption technique that uses a 256-bit key to
 * encrypt and de-crypt data or files.
 * 
 * @author olgaso
 *
 */
class  AESEncryptor extends Encryptor{
	private final String key;
	/**
	 * generating a secret key, we can use the KeyGenerator class. Lets define a
	 * method for generating the AES key with the size of n (128, 192, and 256)
	 * bits
	 * */
    private final int sizeOfKey ;
	AESEncryptor() {
		super();
		key = buildKey();
		sizeOfKey = 256;
	}
	/**
	 * 
	 * @param ofKey
	 * @throws CryptoException 
	 */
	AESEncryptor(String ofKey) throws CryptoException {
		super();
		 int pow = highestPowerOf2(ofKey.length());
		 if(pow >32 || pow < 8 ) {
			 throw new CryptoException("Invalid AES key length: "+pow+"  bytes");
		 }
	     int c = ofKey.length()%pow ;
		 if(c>0) {
			ofKey = StringUtils.repeat("*", pow-c) + ofKey; 
		 }
		key = toHex( ofKey.getBytes(StandardCharsets.UTF_8));
		sizeOfKey = 256;
		
	}
	static int highestPowerOf2(int n) 
	{ 
		return (int) Math.pow(2, Math.ceil(Math.log(n)/Math.log(2)));
	} 
	/**
	 * 
	 * 
	 * @param mode
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	protected Cipher generateCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		byte[] arrayOfByte1 = key.getBytes();
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(sizeOfKey);
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(arrayOfByte1, "AES");
		Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		localCipher.init(mode, localSecretKeySpec);
		return localCipher;

	}

	@SuppressWarnings("unused")
	/**
	 * generating the AES key with the size of n (128, 192, and 256) bits:
	 * 
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private SecretKey getKeyFromPassword(String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException{
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), key.getBytes(), 65536, sizeOfKey);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
				.getEncoded(), "AES");
		return secret;
	}

	public String encrypt(String paramString) throws CryptoException{
		try{
			Cipher localCipher = generateCipher(Cipher.ENCRYPT_MODE);

			byte[] arrayOfByte2 = localCipher.doFinal(paramString.getBytes());
			BigInteger localBigInteger = new BigInteger(arrayOfByte2);
			return localBigInteger.toString(16);
		}catch(Exception localException){
			throw new CryptoException(localException);
		}
	}

	public String decrypt(String paramString) throws CryptoException{
		try{
			Cipher localCipher = generateCipher(Cipher.DECRYPT_MODE);
			BigInteger localBigInteger = new BigInteger(paramString, 16);
			byte[] arrayOfByte2 = localBigInteger.toByteArray();
			byte[] arrayOfByte3 = localCipher.doFinal(arrayOfByte2);
			return new String(arrayOfByte3);
		}catch(Exception localException){
			throw new CryptoException(localException);
		}
	}

}
