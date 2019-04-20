package br.ufsc.ppgcc.experion.tests;

import br.ufsc.ppgcc.experion.extractor.algorithm.tfidf.TFIDF;
import com.optimaize.langdetect.i18n.LdLocale;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class TestTFIDF {

    @Test
    public void buildTFIDF() throws IOException {
        List<String> lines = new LinkedList<>();
        lines.add("new type test line");
        lines.add("old type test line");
        lines.add("same type test word");
        lines.add("new type or line");

        Set<String> words = new HashSet<>();
        lines.stream().forEach(line -> Arrays.stream(line.split(" ")).forEach(word -> words.add(word)));

        TFIDF tfidf = new TFIDF();
        tfidf.calculateTFIDF(LdLocale.fromString("en"), lines, words);

        System.out.println("TFIDF old = " + tfidf.getTFIDFForWord(LdLocale.fromString("en"), "old"));
        System.out.println("TFIDF word = " + tfidf.getTFIDFForWord(LdLocale.fromString("en"), "word"));
        System.out.println("TFIDF line = " + tfidf.getTFIDFForWord(LdLocale.fromString("en"), "line"));
        System.out.println("TFIDF new = " + tfidf.getTFIDFForWord(LdLocale.fromString("en"), "new"));
    }
}
