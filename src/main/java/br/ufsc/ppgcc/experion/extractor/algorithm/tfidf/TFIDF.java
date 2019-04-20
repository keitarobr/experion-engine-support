package br.ufsc.ppgcc.experion.extractor.algorithm.tfidf;

import br.ufsc.ppgcc.experion.extractor.algorithm.support.DataParser;
import com.optimaize.langdetect.i18n.LdLocale;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TFIDF extends DataParser {

    private Double tfidfFraction = 0.3;
    private Map<LdLocale, Set<String>> languagesWordsWithTopTFIDFWordsOnly;
    private Map<LdLocale, Map<String, Integer>> totalDocsPerLanguageWord;
    private Map<LdLocale, Map<String, Double>> tfidfPerLanguage;
    private Map<LdLocale, List<String>> languagesWithTopTFIDFWordsOnly;

    public TFIDF() throws IOException {
        super();
        languagesWithTopTFIDFWordsOnly = new HashMap<>();
        languagesWordsWithTopTFIDFWordsOnly = new HashMap<>();
        tfidfPerLanguage = new HashMap<>();
        totalDocsPerLanguageWord = new HashMap<>();
    }

    //<editor-fold desc="Getters and setters">
    public Double getTfidfFraction() {
        return tfidfFraction;
    }

    public void setTfidfFraction(Double tfidfFraction) {
        this.tfidfFraction = tfidfFraction;
    }

    public Map<LdLocale, List<String>> getLanguagesWithTopTFIDFWordsOnly() {
        return languagesWithTopTFIDFWordsOnly;
    }

    public Map<LdLocale, Set<String>> getLanguagesWordsWithTopTFIDFWordsOnly() {
        return languagesWordsWithTopTFIDFWordsOnly;
    }

    //</editor-fold>

    //<editor-fold desc="TFIDF calculation">
    public TFIDF calculateTFIDFs() {
        for (LdLocale language : getLinesWithoutStopWords().keySet()) {
            calculateTFIDF(language, getLinesWithoutStopWords().get(language), getLanguagesWordsWithoutStopWords().get(language));
        }
        createTFIDFFilteredLanguages();
        return this;
    }

    public TFIDF calculateTFIDF(LdLocale language, List<String> lines, Set<String> words) {
        Map<Integer, Map<String, Integer>> wordsPerDoc = new HashMap<Integer, Map<String, Integer>>();
        Map<String, Integer> docsPerWord = new HashMap<String, Integer>();
        Map<Integer, Map<String, Double>> tfIDF = new HashMap<Integer, Map<String, Double>>();

        for (String word : words) {
            docsPerWord.put(word, 0);
        }

        int docN = 0;
        for (String linha : lines) {
            Map<String, Integer> docMap = new HashMap<String, Integer>();
            wordsPerDoc.put(docN, docMap);

            for (String word : linha.split(" ")) {
                if (docsPerWord.containsKey(word)) {
                    docsPerWord.replace(word, docsPerWord.get(word) + 1);
                } else {
                    docsPerWord.put(word, 1);
                }
                if (docMap.containsKey(word)) {
                    docMap.replace(word, docMap.get(word) + 1);
                } else {
                    docMap.put(word, 1);
                }
            }
            docN++;
        }

        int totalDocs = lines.size();

        for (Integer doc : wordsPerDoc.keySet()) {
            Map<String, Double> docMap = new HashMap<String, Double>();
            for (String word : wordsPerDoc.get(doc).keySet()) {
                double idf = wordsPerDoc.get(doc).get(word) * Math.log(totalDocs / docsPerWord.get(word));
                docMap.put(word, idf);
            }
            tfIDF.put(doc, docMap);
        }

        Map<String, Double> fullIDF = new HashMap<String, Double>();

        for (String word : words) {
            fullIDF.put(word, 0.0);
        }

        for (Integer doc : tfIDF.keySet()) {
            Map<String, Double> docMap = tfIDF.get(doc);
            for (String word : docMap.keySet()) {
                try {
                    fullIDF.replace(word, fullIDF.get(word) + docMap.get(word));
                } catch (Exception e) {
                    throw new RuntimeException("Word missing from the dictionary: " + word);
                }
            }
        }

        tfidfPerLanguage.put(language, fullIDF);
        totalDocsPerLanguageWord.put(language, docsPerWord);

        return this;

    }

    private TFIDF createTFIDFFilteredLanguages() {
        for (LdLocale language : getLinesWithoutStopWords().keySet()) {
            createFilteredLanguage(language, getLinesWithoutStopWords().get(language));
        }

        return this;
    }

    private void createFilteredLanguage(LdLocale language, List<String> lines) {
        List<String> filteredLines = new LinkedList<>();
        Set<String> words = new HashSet<>();

        Stream<Map.Entry<String, Double>> sorted =
                tfidfPerLanguage.get(language).entrySet().stream()
                        .sorted(Map.Entry.comparingByValue()).limit((int) Math.floor(tfidfPerLanguage.get(language).keySet().size() * (1 - tfidfFraction)));

        sorted.forEachOrdered(new Consumer<Map.Entry<String, Double>>() {
            public void accept(Map.Entry<String, Double> stringDoubleEntry) {
                words.add(stringDoubleEntry.getKey());
            }
        });

        for (String line : lines) {
            List<String> lineWords = Arrays.asList();
            String cleanLine = StringUtils.join(Arrays.stream(line.split(" ")).filter(x -> !words.contains(x)).toArray(), " ");
            if (!StringUtils.isBlank(cleanLine)) {
                filteredLines.add(cleanLine);
            }
        }

        languagesWithTopTFIDFWordsOnly.put(language, filteredLines);
        languagesWordsWithTopTFIDFWordsOnly.put(language, words);
    }
    //</editor-fold>

    public List<String> getTopTFIDFWords(LdLocale language, Integer numWords) {
        final List<String> words = new LinkedList<>();

        final Map<String, Double> sorted = tfidfPerLanguage.get(language).entrySet()
                .stream()
                .sorted((Map.Entry.<String, Double>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        for (String word : sorted.keySet()) {
            if (words.size() < numWords) {
                words.add(word);
            } else {
                break;
            }
        }

        return words;
    }

    //<editor-fold desc="Visualization">
    public String describeTFIDFs() {
        StringBuffer result = new StringBuffer();
        for (LdLocale language : tfidfPerLanguage.keySet()) {
            result.append(String.format("TFIDF => %s\n(\n", language.getLanguage()));
            Stream<Map.Entry<String, Double>> sorted =
                    tfidfPerLanguage.get(language).entrySet().stream()
                            .sorted(Map.Entry.comparingByValue());

            sorted.forEachOrdered(new Consumer<Map.Entry<String, Double>>() {
                public void accept(Map.Entry<String, Double> stringDoubleEntry) {
                    result.append(String.format("\t%s => %s\n", stringDoubleEntry.getKey(), stringDoubleEntry.getValue()));
                }
            });

            result.append(")\n");
        }

        return result.toString();
    }

    public String describeLanguagesWithTopTFIDFWordsOnly() {
        StringBuffer result = new StringBuffer();
        for (LdLocale language : languagesWithTopTFIDFWordsOnly.keySet()) {
            result.append(String.format("Lines in %s => %d \n(\n\t%s\n)\n\n", language.getLanguage(), languagesWithTopTFIDFWordsOnly.get(language).size(),
                    StringUtils.join(languagesWithTopTFIDFWordsOnly.get(language), "\n\t\n")));
        }
        if (languagesWithTopTFIDFWordsOnly.keySet().isEmpty()) {
            result.append("No languages.\n");
        }

        return result.toString();
    }

    public Double getTFIDFForWord(LdLocale language, String word) {
        return this.tfidfPerLanguage.get(language).get(word);
    }

    //</editor-fold>
}
