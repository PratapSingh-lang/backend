package in.co.bel.ims.initial.service.util;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.FlatColorBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;
import in.co.bel.ims.initial.security.service.ImsTextProducer;

public class ImsCipherUtil {

	public static String generateHash(String dataToHash, String salt) {

		String generatedHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt.getBytes());
			byte[] bytes = md.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedHash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedHash;
	}
	
	public static String generateHash(String dataToHash) {

		String generatedHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedHash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedHash;
	}

	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}

	public static String generateRandomPassword() {
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String ints = "0123456789";
		String special = "!@$*#";
		SecureRandom secureRandom = new SecureRandom();
		String generatedString = "";
		int defaultPassLength = 0;
		while (defaultPassLength <= secureRandom.nextInt(5)) {
			generatedString += alpha.charAt(secureRandom.nextInt(25));
			generatedString += alpha.toLowerCase().charAt(secureRandom.nextInt(25));
			defaultPassLength++;
		}
		generatedString += special.charAt(secureRandom.nextInt(5));
		while (defaultPassLength <= 9) {
			generatedString += ints.charAt(secureRandom.nextInt(10));
			defaultPassLength++;
		}
		return generatedString;
	}

	public static boolean validate(String enteredData, String salt, String actualHash) {
		String enteredHash = generateHash(enteredData, salt);
		return enteredHash.equals(actualHash);
	}

	public static String generateOTP() {
		String ints = "0123456789";
		SecureRandom secureRandom = new SecureRandom();
		String generatedString = "";
		int defaultPassLength = 0;

		while (defaultPassLength < 6) {
			generatedString += ints.charAt(secureRandom.nextInt(10));
			defaultPassLength++;
		}
		return generatedString;
	}

	// Creating Captcha Object
	public static Captcha createCaptcha(Integer width, Integer height) {
		Color backgroundColor = Color.WHITE;
		List<Color> textColors = Arrays.asList(Color.BLACK);
		List<Font> textFonts = Arrays.asList(new Font("Arial", Font.BOLD, 40), new Font("Courier", Font.BOLD, 40));
		char[] codeNumber = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
				'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
				'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
		FlatColorBackgroundProducer bgProducer = new FlatColorBackgroundProducer(backgroundColor);
		DefaultWordRenderer wordRenderer = new DefaultWordRenderer(textColors, textFonts);
		return new Captcha.Builder(width, height).addText(new ImsTextProducer(6, codeNumber), wordRenderer)
				.addBackground(bgProducer).addBorder().gimp(new ImsGimpyRenderer()).addNoise(new CurvedLineNoiseProducer(Color.RED, 2f)).build();
	}
	// Converting to binary String
	public static String encodeCaptcha(Captcha captcha) {
		String image = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(captcha.getImage(), "jpg", bos);
			byte[] byteArray = Base64.getEncoder().encode(bos.toByteArray());
			image = new String(byteArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static String generateTxnId() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}
	
	
	
	/* --------------------------- Start of Data Encryption ------------------------------------------ */
	
	private final static String privateKey = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAwWAxD6z0gkxrJFymolXPvisSV8Nih08RBtOfuHk3PvQN2MVGMcc2+ESPCMDOK+pfReDM6whRJjTCakym/I6TNQIDAQABAkAlKHf8+GHamNccu138wysCcpHZge6wq/mMW4VtRAOg6ZyH/IJrZ/Qd8srzgDL0xEWr74pacxrF0ECFO+QZ6MMpAiEA0bNjC+mD7xZgwRYyn27NRr8lhf5eROySdHRZtfiird0CIQDsEhddhzzPki8p6XxkHIuoapKNsVeGfyqnvb4uvcgBOQIhAKEBCFUHD4MsPVFCx89ddUKSVC8S5DIrKS88ffyXT+AJAiEAoaE1XDUG3ruhDRour8CXokTteJQOcCjLRLP17bzEnokCIQCKFkn+3056pHf29xPq5FVIafE8jkR35rYqkX4ZhicttA==";
	private final static String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMFgMQ+s9IJMayRcpqJVz74rElfDYodPEQbTn7h5Nz70DdjFRjHHNvhEjwjAzivqX0XgzOsIUSY0wmpMpvyOkzUCAwEAAQ==";
	
	public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
	
	public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
	
	public static String encrypt(String input) {
		Cipher cipher;
		byte[] cipherText = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
			cipherText = cipher.doFinal(input.getBytes());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText){
		Cipher cipher;
		byte[] plainText = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
			plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(plainText);
	}

	public static void generateKey() {
		KeyPairGenerator keyGenerator;
		KeyPair keyPair = null;
		try {
			keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(512);
			keyPair = keyGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("---------------------- Start of  Private Key ----------------------");
		System.out.println(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
		System.out.println("---------------------- End of  Private Key ----------------------");

		System.out.println("---------------------- Start of  Public Key ----------------------");
		System.out.println(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
		System.out.println("---------------------- End of  Public Key ----------------------");

}
	
	/* --------------------------- End of Data Encryption ------------------------------------------ */
	
//	public static void main(String[] args) {
//		String dataToHash = "e9w6xm|UtlAx0O79baxb4|10.00|IMS|Nam|mail@gmail.com|||||||||||cOB1zjDFDERvBXYIyaTZuBXOnWT8l1AE";
//		String hashedData = generateHash(dataToHash);
//		System.out.println(hashedData);
//	}
	
	
//	public static void main(String[] args){
//		String myName = "I am Madhavi";
//		System.out.println("Encrypt ---- "+encrypt(myName));
//		System.out.println(" Decrypt ----- "+decrypt(encrypt(myName)));
//	}
	
}
