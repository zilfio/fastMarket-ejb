package it.univaq.mwt.fastmarket.common.utility;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 {

	public static String generateMD5(String password) {
		return DigestUtils.md5Hex(password);
		
	}
	
}
