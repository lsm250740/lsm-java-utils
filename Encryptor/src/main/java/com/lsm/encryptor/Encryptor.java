package com.lsm.encryptor;

import java.math.BigInteger;

public abstract class Encryptor{
	public abstract String encrypt(String paramString) throws CryptoException;
	public abstract String decrypt(String paramString) throws CryptoException;
	
	protected  String toHex(byte[] paramArrayOfByte){
		BigInteger localBigInteger = new BigInteger(1, paramArrayOfByte);
		String str = localBigInteger.toString(16);
		int i = paramArrayOfByte.length * 2 - str.length();
		if(i > 0){
			return String.format(new StringBuilder("%0").append(i).append("d").toString(), new Object[] { Integer.valueOf(0) }) + str;
		}
		return str;
	}

	protected  byte[] fromHex(String paramString){
		byte[] arrayOfByte = new byte[paramString.length() / 2];
		for (int i = 0; i < arrayOfByte.length; i++){
			arrayOfByte[i] = ((byte) Integer.parseInt(paramString.substring(2 * i, 2 * i + 2), 16));
		}
		return arrayOfByte;
	}
	
		
	protected String buildKey() {
	    return toHex((new String(fromHex("2a")) +new String(fromHex("58")) + new String(fromHex("69")) + new String(fromHex("67")) + new String(fromHex("64"))
			+ new String(fromHex("61")) + new String(fromHex("6c")) + new String(fromHex("69")) ).getBytes());
	}
	
}
