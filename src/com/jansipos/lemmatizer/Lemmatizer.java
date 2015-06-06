package com.jansipos.lemmatizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public final class Lemmatizer {

	private static String dirOut = "res/out.txt";

	private static Map<String, Entry> dictionary;
	private static Map<String, Integer> results;

	private static List<String> lines;
	private static StringBuilder sb;
	private static int unknown;

	public static Lemmatizer instance;
	
	private static final String[] LIST_QUE_WORDS = new String[] {"absque", "abusque", "adaeque", "adque", "adusque", "aeque", "alterutraque", "alterutrique", "antique", "apsque", "atque", "circumquaque", "circumundique", "conseque", "coque", "cuiusmodicumque", "cumque", "cuncumque", "denique", "deque", "hocusque", "hucusque", "itaque", "iamiamque", "longinque", "namque", "neque", "peraeque", "plerumque", "quacumque", "quacunque", "qualitercumque", "qualitercunque", "quandiucumque", "quandocumque", "quandocunque", "quandoque", "quantumcumque", "quinque", "quoadusque", "quocumque", "quocunque", "quomodocumque", "quomodocunque", "quoque", "quoquomque", "quotcumque", "quotcunque", "quotienscumque", "quotienscunque", "quotiensquonque", "quotiescumque", "quotiescunque", "quotiesquonque", "quotusquisque", "quousque", "sesque", "simulatque", "susque", "ubicumque", "ubiquaque", "ubique", "ubiquomque", "undecumque", "undique", "usque", "usquequaque", "utcumque", "utcunque", "utique", "utquomque", "utrimque", "utrinque", "utrobique", "utroque", "utrubique"};
	private static final Set<String> SET_QUE_WORDS = new HashSet<String>(Arrays.asList(LIST_QUE_WORDS));

	private Lemmatizer() {
		dictionary = POSLemmaList.getInstance().getDictionary();
		results = new HashMap<String, Integer>();
	}

	public static Lemmatizer getInstance() {
		if (instance == null) {
			instance = new Lemmatizer();
		}
		return instance;
	}

	public void lemmatize(File input) {
		
		results.clear();
		unknown = 0; // resetira broj nepoznatih oblika
		
		dirOut = input.getParent() + "/out/lemmata-" + input.getName();

		try {
			lines = FileUtils.readLines(input);
		} catch (IOException e1) {
			System.out.println("Can't read input file: " + e1.getMessage());
		}

		sb = new StringBuilder();

		for (String line : lines) {
			String[] words = line.split(" ");

			for (String word : words) {

				try {
					if (word.endsWith("que") && !SET_QUE_WORDS.contains(word)){ // ako 'que' nije dio leme
						word = word.substring(0, word.length()-3); // ukloni 'que' na kraju riječi
					}

					Entry entry = dictionary.get(word);
					String lemma = entry.getLemma();
					
					if (results.containsKey(lemma)) { // ako je ta lema već viđena
						results.put(lemma, results.get(lemma) + 1); // dodaj 1 na broj pojavaka
					}
					else {						
						results.put(entry.getLemma(), 1);
					}
//					sb.append(entry.getLemma() + "\n");
				} catch (NullPointerException e) {
					// System.out.println(word);
					unknown++;
				}
			}
		}
		printResults();
		System.out.println("Number of unknown forms: " + unknown);
	}

	private void printResults() {
		
		StringBuilder sb = new StringBuilder();
		
		ValueComparator comparator =  new ValueComparator(results);
		TreeMap<String, Integer> sorted = new TreeMap<String, Integer>(comparator);
		
		sorted.putAll(results);
		
		for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
			sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
		}
		
		try {
			FileUtils.write(new File(dirOut), sb.toString());
		} catch (IOException e) {
			System.out.println("Can't write to output file: " + e.getMessage());
		}
	}
	
	private static class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
	    
		public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        }
	    }
	}

	private static class POSLemmaList {

		private static String dirBaza = "res/POS-baza.txt";

		private HashMap<String, Entry> dictionary;
		private static List<String> forms;
		private static List<String> lines;
		private static String lemma;
		private static String POS;

		private static POSLemmaList instance;

		private POSLemmaList() {
			buildDictionary(new File(dirBaza));
		}

		public static POSLemmaList getInstance() {
			if (instance == null) {
				instance = new POSLemmaList();
			}
			return instance;
		}

		private void buildDictionary(File file) {

			dictionary = new HashMap<String, Entry>();
			lines = new ArrayList<String>();

			try {
				lines = FileUtils.readLines(file);

				for (String line : lines) {
					forms = new LinkedList<String>(Arrays.asList(line.split(" ")));
					POS = forms.remove(0);
					lemma = forms.get(0);

					Entry entry = new Entry(lemma, POS);

					for (String form : forms) {
						dictionary.put(form, entry);
					}
				}
			} catch (IOException e) {
				System.out.println("Can't read dictionary file: " + e.getMessage());
			}
		}

		public HashMap<String, Entry> getDictionary() {
			return dictionary;
		}

	}

	private static class Entry {

		private String lemma;
		private String POS;

		public Entry(String lemma, String POS) {
			this.lemma = lemma;
			this.POS = POS;
		}

		public String getLemma() {
			return lemma;
		}

		public String getPOS() {
			return POS;
		}
	}
}