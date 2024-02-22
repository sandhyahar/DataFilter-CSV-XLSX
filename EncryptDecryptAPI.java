package com.API.Controller;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EncryptDecryptAPI {
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	@PostMapping(value = "/Encrypt")
	public String Encrypt(HttpServletRequest request, String sercretKey, String message) {
		JSONObject res = new JSONObject();
		try {
			res.put("base64EncryptedString", encrypt(message, sercretKey));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res.toString();
	}

	public String encrypt(String message, String sercretKey) {
		String base64EncryptedString = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF_8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);
			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
			byte[] plainTextBytes = message.getBytes(UTF_8);
			byte[] buf = cipher.doFinal(plainTextBytes);
			byte[] base64Bytes = Base64.getEncoder().encode(buf);
			base64EncryptedString = new String(base64Bytes);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return base64EncryptedString;
	}

	@PostMapping(value = "/Decrypt")
	public String Decrypt(HttpServletRequest request, String sercretKey, String message) {
		JSONObject res = new JSONObject();
		try {
			res.put("base64DecryptedString", decrypt(message, sercretKey));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return res.toString();
	}

	public String decrypt(String encryptedText, String sercretKey) {
		String base64DecryptedString = "";
		byte[] plainText = null;
		try {
			byte[] msg = encryptedText.getBytes(UTF_8);
			byte[] message = Base64.getDecoder().decode(msg);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF_8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);
			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			decipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			plainText = decipher.doFinal(message);
			base64DecryptedString = new String(plainText);
		} catch (Exception ex) {
			System.out.println("Exception decrypt " + ex);
		}
		return base64DecryptedString;
	}
}
