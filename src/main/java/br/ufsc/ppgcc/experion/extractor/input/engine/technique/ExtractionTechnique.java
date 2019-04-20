package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.model.expert.Expert;
import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import com.optimaize.langdetect.i18n.LdLocale;

import java.util.List;
import java.util.Set;

public interface ExtractionTechnique {
    public Set<PhysicalEvidence> generateEvidences(Expert expert, Set<PhysicalEvidence> evidences, String language);
}
