package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.extractor.algorithm.keygraph.Keygraph;

public class KeygraphExtractionTechniqueTFIDF extends KeygraphExtractionTechnique {

    protected KeygraphExtractionTechniqueTFIDF(Keygraph graph) {
        super(graph);
        setFilteredByTFIDF(true);
    }

}
