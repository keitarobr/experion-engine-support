package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

public class LDAExtractionTechniqueTFIDF extends LDAExtractionTechnique {

    public LDAExtractionTechniqueTFIDF() {
        super();
        this.setFilteredByTFIDF(true);
        this.setTfidfFilterDegree(0.3);
    }
}
