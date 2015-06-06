package com.jansipos.lemmatizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public final class Lemmatizer {

	private static String dirOut = "res/out.txt";

	private static Map<String, Entry> dictionary;

	private static List<String> lines;
	private static StringBuilder sb;
	private static int i;

	public static Lemmatizer instance;
	
	private static final String[] LIST_QUE_WORDS = new String[] {"adque", "atque", "cuncumque", "itaque", "namque", "neque", "abusque", "adaeque", "adusque", "aeque", "alterutraque", "alterutrique", "antique", "circumquaque", "circumundique", "conseque", "coque", "cujusmodicumque", "cumque", "denique", "deque", "hocusque", "hucusque", "itaque", "jamjamque", "longinque", "neque", "peraeque", "plerumque", "quacumque", "quacunque", "qualitercumque", "qualitercunque", "quandiucumque", "quandocumque", "quandocunque", "quandoque", "quantumcumque", "quoadusque", "quocumque", "quocunque", "quomodocumque", "quomodocunque", "quoque", "quoquomque", "quotienscumque", "quotienscunque", "quotiensquonque", "quotiescumque", "quotiescunque", "quotiesquonque", "quotusquisque", "quousque", "sesque", "simulatque", "susque", "ubicumque", "ubiquaque", "ubique", "ubiquomque", "undecumque", "undique", "usque", "usquequaque", "utcumque", "utcunque", "utique", "utquomque", "utrimque", "utrinque", "utrobique", "utroque", "neque", "peraeque", "plerumque", "quacumque", "quacunque", "qualitercumque", "qualitercunque", "quandiucumque", "quandocumque", "quandocunque", "quandoque", "quantumcumque", "quoadusque", "quocumque", "quocunque", "quomodocumque", "quomodocunque", "quoque", "quoquomque", "quotienscumque", "quotienscunque", "quotiensquonque", "quotiescumque", "quotiescunque", "quotiesquonque", "quotusquisque", "quousque", "sesque", "simulatque", "susque", "ubicumque", "ubiquaque", "ubique", "ubiquomque", "undecumque", "undique", "usque", "usquequaque", "utcumque", "utcunque", "utique", "utquomque", "utrimque", "utrinque", "utrobique", "utroque", "utrubique", "absque", "abusque", "adusque", "apsque", "usque", "quotcumque", "quotcunque", "quinque"};
	private static final Set<String> SET_QUE_WORDS = new HashSet<String>(Arrays.asList(LIST_QUE_WORDS));

	private Lemmatizer() {
		dictionary = POSLemmaList.getInstance().getDictionary();
	}

	public static Lemmatizer getInstance() {
		if (instance == null) {
			instance = new Lemmatizer();
		}
		return instance;
	}

	public void lemmatize(File input) {

		i = 0; // resetira broj nepoznatih oblika
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
					sb.append(entry.getLemma() + "\n");
				} catch (NullPointerException e) {
					// System.out.println(word);
					i++;
				}
			}
		}

		try {
			FileUtils.write(new File(dirOut), sb.toString());
		} catch (IOException e) {
			System.out.println("Can't write to output file: " + e.getMessage());
		}
		System.out.println("Number of unknown forms: " + i);
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