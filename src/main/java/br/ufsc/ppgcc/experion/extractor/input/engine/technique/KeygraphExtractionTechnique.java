package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.model.expert.Expert;
import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.DataParser;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnknownLanguage;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.UnsupportedLanguageException;
import br.ufsc.ppgcc.experion.extractor.algorithm.tfidf.TFIDF;
import br.ufsc.ppgcc.experion.extractor.algorithm.keygraph.DocumentCluster;
import br.ufsc.ppgcc.experion.extractor.algorithm.keygraph.Keygraph;
import br.ufsc.ppgcc.experion.extractor.algorithm.keygraph.Node;
import com.optimaize.langdetect.i18n.LdLocale;

import java.util.*;

public class KeygraphExtractionTechnique extends BasicExtractionTechniqueTFIDFFiltered {

    private Keygraph graph;

    protected KeygraphExtractionTechnique(Keygraph graph) {
        this.graph = graph;
    }

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

        if (this.isFilteredByTFIDF()) {
            ((TFIDF) parser).calculateTFIDFs();
            graph.createGraph(((TFIDF) parser).getLanguagesWordsWithTopTFIDFWordsOnly(), ((TFIDF) parser).getLanguagesWithTopTFIDFWordsOnly());
        } else {
            graph.createGraph(parser.getLanguagesWordsWithoutStopWords(), parser.getLinesWithoutStopWords());
        }

        if (parser.getLanguagesWords().keySet().contains(LdLocale.fromString(language))) {
            evidences = new HashSet<>();

            if (graph.getGraphs().get(LdLocale.fromString(language)) != null) {
                for (DocumentCluster docs : graph.getGraphs().get(LdLocale.fromString(language))) {
                    PhysicalEvidence ev = new PhysicalEvidence();
                    ev.setTimestamp(new Date(0));
                    ev.setExpert(expert);
                    ev.setLanguage(language);

                    for (Node n : docs.keyGraph.values()) {
                        ev.addKeywords(new String[]{n.keyword.word});
                    }

//                    EvidenceSourceURLDBLP url = new EvidenceSourceURLDBLP();
//                    url.setOriginalEvidences(orEvidences);
//                    ev.setUrl(url);

                    addOriginalURL(ev, originalData, originalURL, evidenceURLSourceClass);
                    evidences.add(ev);
                }

            } else {
                return new HashSet<>();
            }
        } else {
            return new HashSet<>();
        }

        return evidences;
    }
}
