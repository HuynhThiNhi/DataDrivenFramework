package com.w2a.rough;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Properties;

public class TestProperties{
	
	
	public static void main(String[] args) throws IOException {
		
		Properties config = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/Config.properties"));
		config.load(fis);

		Properties OR = new Properties();
		System.out.println(System.getProperty("user.dir").concat("/src/test/resources/properties/OR.properties"));
		fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/OR.properties"));
		OR.load(fis);
		System.out.println(OR.getProperty("bmlBtn_CSS"));
	}

}