package com.lsm.encryptor.aes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.lsm.encryptor.CryptoException;

public class AESEncryptorTest {

	@Test
	public void testEncryptDecrypt() throws CryptoException {
		String test = "LIFE_POLICA:123456789123:123456789";
		
		String encrypted = AESEncryptorUtil.aes256.encrypt(test);
		assertNotNull(encrypted);
		assertNotEquals(test, encrypted);
		
		String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
		assertEquals(test, decrypted);
	}

	@Test
	public void testDecryptWithRebuild() throws CryptoException {
		String test = "Test message for round trip";;
		final String originalEncrypted = AESEncryptorUtil.aes256.encrypt(test);
		
		AESEncryptorUtil.aes256.rebuild("mynew");
		assertThrows(CryptoException.class, () -> {
			   AESEncryptorUtil.aes256.decrypt(originalEncrypted);
	        });
		
		final String encrypted = AESEncryptorUtil.aes256.encrypt(test);
		String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
		assertNotNull(decrypted);
		assertFalse(decrypted.isEmpty());
	}
	
	@Test
	public void testRoundTripEncryption() throws CryptoException {
		String original = "Test message for round trip";
		
		String encrypted = AESEncryptorUtil.aes256.encrypt(original);
		String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
		
		assertEquals(original, decrypted);
	}
}
