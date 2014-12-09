package serverSnarovIA;

import java.security.*;
import java.util.*;

//инскапсулирует пароль и реализует поведение для его задание и проверки
public class Password {

	//константы
	private static final String HASH_ALG = "MD5";		//алгоритм шифрования пароля
	public static final String PWD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";
	//поля
	private byte[] encPassword = null;

	//сеттер пароля (true если пароль установлен успешно)
	public boolean setPassword(String password) {
		return (encPassword = encrypt(password)) != null;
	}

	public boolean comparePass(String password) {
		return Arrays.equals(encPassword, encrypt(password));
	}

	public boolean isSetted() {
		return encPassword != null;
	}

	private byte[] encrypt(String password) {
		byte[] notEncPassword = password.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALG);
			md.reset();
			md.update(notEncPassword);
			return md.digest();
		} catch (NoSuchAlgorithmException exc) {
			System.err.println(exc.getMessage());
			return null;
		} finally {
			Arrays.fill(notEncPassword, (byte) 0);
		}
	}
}
