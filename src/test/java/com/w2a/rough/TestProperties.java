package com.w2a.rough;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestProperties {
	
	
	public static void main(String[] args) throws IOException {
		
		Properties config = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir").concat("/src/test/resources/properties/Config.properties"));
		config.load(fis);
	}

}
:w