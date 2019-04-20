package br.ufsc.ppgcc.experion.extractor.algorithm.support;

import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.*;

public class DataParser {

    //<editor-fold desc="Static properties">
    public static List<LdLocale> supportedLanguages = Arrays.asList(new LdLocale[] {
            LdLocale.fromString("pt"),
            LdLocale.fromString("es"),
            LdLocale.fromString("ro"),
            LdLocale.fromString("en"),
            LdLocale.fromString("it"),
            LdLocale.fromString("fr"),
            LdLocale.fromString("de")
    });
    //</editor-fold>

    private Map<LdLocale, CharArraySet> stopWords;
    private LanguageDetector languageDetector;
    private TextObjectFactory textObjectFactory;
    private Map<LdLocale, List<String>> linesPerLanguage;
    private Map<LdLocale, List<String>> linesWithoutStopWords;
    private Map<LdLocale, Set<String>> languagesWords;
    private Map<LdLocale, Set<String>> languagesWordsWithoutStopWords;


    //<editor-fold desc="Getters/setters">
    public Map<LdLocale, List<String>> getLinesPerLanguage() {
        return linesPerLanguage;
    }

    public Map<LdLocale, List<String>> getLinesWithoutStopWords() {
        return linesWithoutStopWords;
    }

    public Map<LdLocale, Set<String>> getLanguagesWords() {
        return languagesWords;
    }

    public Map<LdLocale, Set<String>> getLanguagesWordsWithoutStopWords() {
        return languagesWordsWithoutStopWords;
    }
    //</editor-fold>

    //<editor-fold desc="Initialization">
    public DataParser() throws IOException {
        textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        linesPerLanguage = new HashMap<>();
        linesWithoutStopWords = new HashMap<>();
        languagesWords = new HashMap<>();
        languagesWordsWithoutStopWords = new HashMap<>();
        buildLanguageDetector();
        loadStopWords();
    }

    private void loadStopWords() {
        stopWords = new HashMap<>();
        stopWords.put(LdLocale.fromString("pt"), PortugueseAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("es"), SpanishAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("ro"), RomanianAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("en"), EnglishAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("it"), ItalianAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("fr"), FrenchAnalyzer.getDefaultStopSet());
        stopWords.put(LdLocale.fromString("de"), GermanAnalyzer.getDefaultStopSet());
    }

    private void buildLanguageDetector() {
        //load all languages:
        List<LanguageProfile> languageProfiles = null;
        try {
            languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //build language detector:
        languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Data loader">
    public DataParser loadLine(String line) {
        TextObject textObject = textObjectFactory.forText(line);
        Optional<LdLocale> lang = languageDetector.detect(textObject);

        if (lang.isPresent() || languageDetector.getProbabilities(textObject).stream().findFirst().isPresent()) {

            LdLocale language;
            if (lang.isPresent()) {
                language = lang.get();
            } else {
                language = languageDetector.getProbabilities(textObject).stream().findFirst().get().getLocale();
            }

            if (language.equals(LdLocale.fromString("br")) || language.equals(LdLocale.fromString("gl"))) {
                language = LdLocale.fromString("pt");
            }

            if (! supportedLanguages.contains(language)) {
                System.err.println(String.format("Unsupported language (%s): %s",  language.getLanguage(), line));
                throw new UnsupportedLanguageException(String.format("Unsupported language (%s): %s",  language.getLanguage(), line));
            }

            List<String> languageLines = linesPerLanguage.get(language);

            if (languageLines == null) {
                languageLines = new LinkedList<>();
                linesPerLanguage.put(language, languageLines);
            }

            line = line.toLowerCase();
            languageLines.add(line.toLowerCase());
            loadWords(language, line);
            afterLineLoaded(language, line);
        } else {
            languageDetector.getProbabilities(textObject).stream().forEachOrdered(langd -> System.out.println(langd.getLocale().toString() + " => " + langd.getProbability()));

            throw new UnknownLanguage(String.format("Unknown language for text: %s", line));
        }

        return this;
    }

    public DataParser loadExpertiseEvidence(PhysicalEvidence evidence) {
        return loadLine(StringUtils.join(evidence.getKeywords(), " "));
    }

    public DataParser loadFile(String pathDocument) throws Exception {
        List<String> document = FileUtils.readLines(new File(pathDocument), Charset.defaultCharset());
        for (String line : document) {
            loadLine(line);
        }
        return this;
    }

    private void loadWords(LdLocale language, String line) {
        Set<String> languageWords = languagesWords.get(language);

        if (languageWords == null) {
            languageWords = new HashSet<>();
            languagesWords.put(language, languageWords);
        }

        languageWords.addAll(Arrays.asList(line.split(" ")));
    }
    //</editor-fold>

    //<editor-fold desc="Stop words removal">
    public DataParser removeStopWords() {
        for (LdLocale language : linesPerLanguage.keySet()) {
            List<String> cleanLines = removeStopWords(language, linesPerLanguage.get(language));
            linesWithoutStopWords.put(language, cleanLines);
        }
        return this;
    }

    private List<String> removeStopWords(LdLocale language, List<String> lines) {
        List<String> cleanLines = new LinkedList<String>();
        for (String line : lines) {
            String cleanLine = removeStopWordsFor(line, stopWords.get(language));
            cleanLines.add(cleanLine);
            loadStopWords(language, cleanLine);
            afterStopwordsRemoved(language, line, cleanLine);
        }
        return cleanLines;
    }

    private static String removeStopWordsFor(String texto, CharArraySet stopWords) {
        try {
        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_45, new StringReader(texto));
        tokenStream = new StopFilter(Version.LUCENE_45, tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term + " ");
        }
        return sb.toString().trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadStopWords(LdLocale language, String line) {
        Set<String> languageWords = languagesWordsWithoutStopWords.get(language);

        if (languageWords == null) {
            languageWords = new HashSet<>();
            languagesWordsWithoutStopWords.put(language, languageWords);
        }

        languageWords.addAll(Arrays.asList(line.split(" ")));
    }
    //</editor-fold>

    //<editor-fold desc="Visualization">
    public String describeLanguagesLines() {
        StringBuilder result = new StringBuilder();
        for (LdLocale language : linesPerLanguage.keySet()) {
            result.append(String.format("Original lines in %s => %d \n (\n\t%s\n)\n\n", language.getLanguage(), linesPerLanguage.get(language).size(),
                    StringUtils.join(linesPerLanguage.get(language), "\n\t\n")));
        }
        return result.toString();
    }

    public String describeLanguagesLinesWithoutStopWords() {
        StringBuilder result = new StringBuilder();
        for (LdLocale language : linesWithoutStopWords.keySet()) {
            result.append(String.format("Lines without stopwords in %s => %d \n (\n\t%s\n)\n\n", language.getLanguage(), linesWithoutStopWords.get(language).size(),
                    StringUtils.join(linesWithoutStopWords.get(language), "\n\t\n")));
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.describeLanguagesLines(), this.describeLanguagesLinesWithoutStopWords());
    }
    //</editor-fold>

    //<editor-fold desc="Hooks">
    protected void afterLineLoaded(LdLocale language, String line) {

    }

    protected void afterStopwordsRemoved(LdLocale language, String originalLine, String line) {

    }
    //</editor-fold>


}
