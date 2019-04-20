package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.extractor.algorithm.tfidf.TFIDF;
import br.ufsc.ppgcc.experion.extractor.algorithm.support.DataParser;

import java.io.IOException;

public abstract class BasicExtractionTechniqueTFIDFFiltered extends BasicExtractionTechnique {

    private boolean filteredByTFIDF;
    private Double tfidfFilterDegree;

    public boolean isFilteredByTFIDF() {
        return filteredByTFIDF;
    }

    public void setFilteredByTFIDF(boolean filteredByTFIDF) {
        this.filteredByTFIDF = filteredByTFIDF;
    }

    public Double getTfidfFilterDegree() {
        return tfidfFilterDegree;
    }

    public void setTfidfFilterDegree(Double tfidfFilterDegree) {
        this.tfidfFilterDegree = tfidfFilterDegree;
    }

    protected DataParser createParser() {
        DataParser parser;

        try {
            if (this.isFilteredByTFIDF()) {
                parser = new TFIDF();
            } else {
                parser = new DataParser();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parser;
    }

}
