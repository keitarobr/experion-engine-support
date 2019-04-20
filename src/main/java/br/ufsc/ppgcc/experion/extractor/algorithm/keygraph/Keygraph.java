package br.ufsc.ppgcc.experion.extractor.algorithm.keygraph;

import com.optimaize.langdetect.i18n.LdLocale;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public abstract class Keygraph {

	public Map<LdLocale, List<DocumentCluster>> getGraphs() {
		return graphs;
	}

	public abstract InputStream getConfig();

	private Map<LdLocale, List<DocumentCluster>> graphs = new HashMap<>();

	public void createGraph(Map<LdLocale, Set<String>> wordsSet, Map<LdLocale, List<String>> data) throws RuntimeException {
		graphs.clear();
		InputStream resource = getConfig();
		Constants constants = null;
		try {
			constants = new Constants(new DataInputStream(resource));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		for (LdLocale language : wordsSet.keySet()) {
//			FileOutputStream output = new FileOutputStream(new File("keygraph_" + language.getLanguage() + ".txt"));
//			PrintStream out = new PrintStream(output);
//
			Set<String> words = wordsSet.get(language);

//			ArrayList<String> palavras = new ArrayList<String>(words.size());
			List<String> palavras = data.get(language);

			double toMins = 1000 * 60;
			HashMap<String, Double> DF = new HashMap<String, Double>();
			HashMap<String, Document> docs = new HashMap<String, Document>();

			long time1 = System.currentTimeMillis();
			Porter porter = new Porter();
			System.out.println("Loading Documents...");
			try {
				new DataLoader(constants).loadDocuments(palavras, docs, DF, porter, constants.REMOVE_DUPLICATES);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			long time2 = System.currentTimeMillis();
			System.out.println(docs.size() + " documents are loaded (after filtering)!");
			ArrayList<DocumentCluster> clusters = new DocumentAnalyze(constants).clusterbyKeyGraph(docs, DF);
//			DocumentAnalyze.printTopics(clusters, out);
			graphs.put(language, clusters);
//			ClusterVisualizations.viewGraph(clusters);
//			out.flush();
//			out.close();
		}
	}

}
