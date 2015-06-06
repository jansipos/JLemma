package com.jansipos.lemmatizer;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class Main {
	
	static String dir = "res/text/test";
	static String[] extensions = new String[]{"txt"};

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		
		Iterator<File> iterator = FileUtils.iterateFiles(new File(dir), extensions, false);
		Lemmatizer lemmatizer = Lemmatizer.getInstance();
 
		while (iterator.hasNext()) {
			lemmatizer.lemmatize(iterator.next());
		}

		
		// debugging
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		
		FileUtils.cleanDirectory(new File("res/text/test/out"));
	}
}

