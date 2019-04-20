package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.model.expert.Expert;
import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnknownLanguage;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnsupportedLanguageException;
import br.ufsc.ppgcc.experion.extractor.algorithm.tfidf.TFIDF;
import com.optimaize.langdetect.i18n.LdLocale;

import java.io.IOException;
import java.util.*;

public class TFIDFExtractionTechnique extends BasicExtractionTechnique {

    @Override
    public Set<PhysicalEvidence> generateEvidences(Expert expert, Set<PhysicalEvidence> evidences, String language) {
        TFIDF tfidf;

        String originalData = getOriginalRetrievedData(evidences);
        String originalURL = getOriginalURL(evidences);
        Class evidenceURLSourceClass = getOriginalURLClass(evidences);

        try {
            tfidf = new TFIDF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (PhysicalEvidence ev : evidences) {
            try {
                tfidf.loadExpertiseEvidence(ev);
            } catch (UnknownLanguage e) {
                System.err.println("Could not find language for: " + ev + " - ignoring");
            } catch (UnsupportedLanguageException e1) {
                System.err.println("Unsupported language for: " + ev + " - ignoring");
            }
        }

        tfidf.removeStopWords();
        tfidf.calculateTFIDFs();

        if (tfidf.getLanguagesWithTopTFIDFWordsOnly().keySet().contains(LdLocale.fromString(language))) {
            evidences = new HashSet<>();
            PhysicalEvidence ev = new PhysicalEvidence();
            ev.setTimestamp(new Date(0));
            ev.setExpert(expert);
            ev.setLanguage(language);
            ev.addKeywords(tfidf.getTopTFIDFWords(LdLocale.fromString(language), 8).toArray(new String[0]));
            addOriginalURL(ev, originalData, originalURL, evidenceURLSourceClass);
            evidences.add(ev);
        } else {
            return new HashSet<>();
        }

        return evidences;
    }
}
