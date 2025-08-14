package com.lsm.encryptor.aes;


import org.junit.jupiter.api.Test;

import com.lsm.encryptor.CryptoException;

public class AESEncryptorTest{

	@Test
	public void encrypt(){
		String test = "LIFE_POLICA:123456789123:123456789";
	
		try{
		//	AESEncryptorUtil.aes256.rebuild("migdal-");
		  String encrypted = AESEncryptorUtil.aes256.encrypt(test);
			System.out.println(" encrypt:"+encrypted);
			System.out.println(" decrypt:"+ AESEncryptorUtil.aes256.decrypt(encrypted));
		  }catch(CryptoException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	}

	@Test
	public void decrypt(){
		String test = "-7122fe4f0977101d7cd86df41ceeb975e146d3c587f7913dc3282798654514b17d00c85851a289e";
	
		try{
			AESEncryptorUtil.aes256.rebuild("migdal");
			String decrypted = AESEncryptorUtil.aes256.decrypt(test);
			System.out.println("encrypt:" +decrypted);
		}catch(CryptoException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
