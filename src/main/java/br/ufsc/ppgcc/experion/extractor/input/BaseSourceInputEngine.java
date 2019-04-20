package br.ufsc.ppgcc.experion.extractor.input;

import br.ufsc.ppgcc.experion.extractor.input.engine.EvidenceSourceInputEngine;
import br.ufsc.ppgcc.experion.extractor.input.engine.technique.ExtractionTechnique;
import br.ufsc.ppgcc.experion.extractor.source.EvidenceSource;
import com.optimaize.langdetect.i18n.LdLocale;

import java.io.Serializable;

public abstract class BaseSourceInputEngine implements EvidenceSourceInputEngine, Serializable {

    private String language;
    private ExtractionTechnique extractionTechnique;
    private EvidenceSource evidenceSource;

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public ExtractionTechnique getExtractionTechnique() {
        return extractionTechnique;
    }

    public void setExtractionTechnique(ExtractionTechnique extractionTechnique) {
        this.extractionTechnique = extractionTechnique;
    }

    @Override
    public EvidenceSource getEvidenceSource() {
        return evidenceSource;
    }

    @Override
    public void setEvidenceSource(EvidenceSource evidenceSource) {
        this.evidenceSource = evidenceSource;
    }
}
