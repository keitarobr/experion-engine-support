package br.ufsc.ppgcc.experion.extractor.algorithm.keygraph;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;

public class Utils {
	public static int intersect(HashMap<String, Document> c1, HashMap<String, Document> c2) {
		int i = 0;
		for (String k1 : c1.keySet())
			if (c2.containsKey(k1))
				i++;
		return i;
	}

	public static void extractKeys() {
	}

	public static HashSet<String> importStopwords() {
		HashSet<String> stopwords = new HashSet<String>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream("stopword.txt"));
			String line = null;
			while ((line = in.readLine()) != null)
				stopwords.add(line.trim().toLowerCase());
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stopwords;
	}
}
