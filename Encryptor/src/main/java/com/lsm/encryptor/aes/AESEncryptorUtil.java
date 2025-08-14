package com.lsm.encryptor.aes;

import com.lsm.encryptor.CryptoException;

public enum AESEncryptorUtil{
  aes256;
  AESEncryptor encryptor = new AESEncryptor();	
  
  public void rebuild(String salt) throws CryptoException {
	 encryptor= new AESEncryptor(salt); 
  }
  
  public String encrypt(String password) throws CryptoException {
	  return encryptor.encrypt(password);
  }
  
  public String decrypt(String password) throws CryptoException {
	  return encryptor.decrypt(password);
  }
}
