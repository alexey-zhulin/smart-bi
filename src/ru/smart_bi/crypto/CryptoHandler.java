package ru.smart_bi.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

public class CryptoHandler {
	private static final String ALGORITHM = "AES";
	private static final String CODE_PHRASE = "FyA53zdyBU6eK2UtDbOW3g==";
    private static Cipher ecipher;
    private static Cipher dcipher;
	
	public void Init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		ecipher = Cipher.getInstance(ALGORITHM);
		dcipher = Cipher.getInstance(ALGORITHM);
		SecretKey key = StrToSecretKey(CODE_PHRASE); 
		// Инициализируем объекты шифрования / дешифрования
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
	}
	
	// Функция генерации ключа шифрования
	public SecretKey GenerateKey() throws NoSuchAlgorithmException {
		return KeyGenerator.getInstance(ALGORITHM).generateKey();
	}
	
	// Функция преобразует SecretKey в строку
	public String SecretKeyToStr(SecretKey secretKey) {
		// http://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}
	
	// Функция преобразует строку в SecretKey
	public SecretKey StrToSecretKey(String secretKeyStr) {
		// http://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
		byte[] decodedKey = Base64.getDecoder().decode(CODE_PHRASE);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM); 
	}
	
	public String encrypt(String str)
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] utf8 = str.getBytes("UTF8");
		byte[] enc = ecipher.doFinal(utf8);
		enc = BASE64EncoderStream.encode(enc);
		return new String(enc);
	}
	
	public String decrypt(String str) throws IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		byte[] dec = BASE64DecoderStream.decode(str.getBytes());
		byte[] utf8 = dcipher.doFinal(dec);
		return new String(utf8, "UTF8");
	}
	
}
