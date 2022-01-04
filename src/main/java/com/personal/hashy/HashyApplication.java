package com.personal.hashy;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class HashyApplication {

	public static void main(String[] args) {
		//simplest stuff: get the file and hash file for breaking this down.

		//need 3 args: digest, file-to-digest, expected...
		if (args.length < 3) {
			System.out.println("PLEASE SUPPLY SOME ARGS TO CHECK FILES");
			return;
		}

		//get the message digest for digest string..
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(args[0]);
		} catch (NoSuchAlgorithmException ex) {
			//no digest...
//			System.out.print(ex.getMessage());
			return;
		}

		File file = new File(args[1]);
		File expectedHash = new File(args[2]);
		if (!file.exists() || !expectedHash.exists()) {
			System.out.println("Where the heck are the files?");
			return;
		}

		//hash file with the type, then compare to text of 2nd file
		FileInputStream readFile;
		byte[] fileBytes;

		//read all bytes into mem at once... could split for digest if doesn't work.
		int byteBuffLength = 4096;
		if (file.length() > Runtime.getRuntime().freeMemory()) {
			fileBytes = new byte[byteBuffLength];
		} else {
			fileBytes = new byte[(int) file.length()];
		}

		try {
			//fd is opened on read; gather bytes to digest.
			readFile = new FileInputStream(file);

			for (int i = 0; (readFile.read(fileBytes)) != -1; i++) {
				digest.update(fileBytes);
			}

			byte[] finishedDigest = digest.digest();

			readFile.close();

			final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
			//need to determine how to extract the digest...
			char[] hexDigest = new char[finishedDigest.length * 2];
			for (int j = 0; j < finishedDigest.length; j++) {
				int v = finishedDigest[j] & 0xFF;
				hexDigest[j * 2] = HEX_ARRAY[v >>> 4];
				hexDigest[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
			}
			String digestText = new String(hexDigest).toLowerCase(Locale.ROOT);

			// now need to read the other file to compare text to digest.
			BufferedReader hashReader = new BufferedReader(new FileReader(expectedHash));
			int ch;
			StringBuilder exHashStr = new StringBuilder();
			while ((ch = hashReader.read()) != -1) {
				exHashStr.append(Character.toString(ch));
			}
			hashReader.close();


			System.out.printf("COMPARING\n%s\n%s\n", digestText, exHashStr);
			//compare.
			if (digestText.equals(exHashStr.toString())) {
				System.out.printf("%s digest of %s MATCHES expected hash\n", args[0], args[1]);
			} else {
				System.out.printf("%s digest of %s DOES NOT MATCH expected hash\n", args[0], args[1]);
			}
		} catch (FileNotFoundException ex) {
			//missing file...
			System.out.print(ex.getMessage());
		} catch (Exception ex) {
			System.out.printf("monkaS: %s\n", ex.getMessage());
			ex.printStackTrace();
		}
	}

}
