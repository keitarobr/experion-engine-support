package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.extractor.source.EvidenceSourceURL;
import br.ufsc.ppgcc.experion.model.expert.Expert;
import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.algorithm.lda.LDA;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.DataParser;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnknownLanguage;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnsupportedLanguageException;
import br.ufsc.ppgcc.experion.extractor.algorithm.tfidf.TFIDF;
import com.optimaize.langdetect.i18n.LdLocale;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class LDAExtractionTechnique extends BasicExtractionTechniqueTFIDFFiltered {

    @Override
    public Set<PhysicalEvidence> generateEvidences(Expert expert, Set<PhysicalEvidence> evidences, String language) {
        DataParser parser = createParser();

        String originalData = getOriginalRetrievedData(evidences);
        String originalURL = getOriginalURL(evidences);
        Class evidenceURLSourceClass = getOriginalURLClass(evidences);

        for (PhysicalEvidence ev : evidences) {
            try {
                parser.loadExpertiseEvidence(ev);
            } catch (UnknownLanguage e) {
                System.err.println("Could not find language for: " + ev + " - ignoring");
            } catch (UnsupportedLanguageException e1) {
                System.err.println("Unsupported language for: " + ev + " - ignoring");
            }
        }

        parser.removeStopWords();

        LDA gen = new LDA();
        if (parser.getLinesWithoutStopWords().keySet().contains(LdLocale.fromString(language))) {

            if (this.isFilteredByTFIDF()) {
                ((TFIDF) parser).calculateTFIDFs();
                gen.loadData(LdLocale.fromString(language), ((TFIDF) parser).getLanguagesWithTopTFIDFWordsOnly().get(LdLocale.fromString(language)));
            } else {
                System.out.println("Evidence data for LDA from: " + expert.getName() + " ( " + (evidences.stream().findAny().get().getInput().getName()) + "):\n\n" + StringUtils.join(parser.getLinesWithoutStopWords().get(LdLocale.fromString(language)), "\n"));
                gen.loadData(LdLocale.fromString(language), parser.getLinesWithoutStopWords().get(LdLocale.fromString(language)));
            }

            try {
                gen.createTopics();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }

            evidences = new HashSet<>();

            for (Set<String> topic : gen.getTopicsWords()) {
                PhysicalEvidence ev = new PhysicalEvidence();
                ev.setTimestamp(new Date(0));
                ev.setExpert(expert);
                ev.setLanguage(language);
                ev.addKeywords(topic.toArray(new String[0]));
                addOriginalURL(ev, originalData, originalURL, evidenceURLSourceClass);
                evidences.add(ev);
            }

        } else {
            return new HashSet<>();
        }


        return evidences;
    }


}
