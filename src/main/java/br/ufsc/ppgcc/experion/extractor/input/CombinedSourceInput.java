package br.ufsc.ppgcc.experion.extractor.input;

import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.input.engine.EvidenceSourceInputEngine;
import br.ufsc.ppgcc.experion.model.expert.Expert;
import br.ufsc.ppgcc.experion.model.expert.ExpertMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CombinedSourceInput extends BaseSourceInputEngine {

    private ExpertMapper mapper;
    private Map<String, EvidenceSourceInput> sources = new HashMap<>();
    private String language;

    public ExpertMapper getMapper() {
        return mapper;
    }

    public void setMapper(ExpertMapper mapper) {
        this.mapper = mapper;
    }

    public void addSource(EvidenceSourceInput source) {
        sources.put(source.getName(), source);
    }

    public Map<String,EvidenceSourceInput> getSources() {
        return sources;
    }


    @Override
    public Set<Expert> getExpertEntities() {
        Set<Expert> experts = new HashSet<>();
        experts.add(mapper.getExpert());
        return experts;
    }

    @Override
    public Set<Expert> findExpertByName(String name) {
        Set<Expert> experts = new HashSet<Expert>();
        experts.add(mapper.getExpert());
        return experts;
    }

    public Iterable<PhysicalEvidence> getNewEvidences(EvidenceSourceInput input) {
        return getNewEvidences(mapper.getExpert(), input);
    }

    @Override
    public Set<PhysicalEvidence> getNewEvidences(Expert expert, EvidenceSourceInput input) {

        if (expert != mapper.getExpert()) {
            throw new RuntimeException("Invalid expert!");
        }

        Set<PhysicalEvidence> evidences = new HashSet<>();

        for (String id : this.sources.keySet()) {
            EvidenceSourceInputEngine sourceEngine = this.sources.get(id).getEngine();
            sourceEngine.setLanguage(this.language);
            sourceEngine.getNewEvidences(expert, input).forEach(ev -> evidences.add(ev));
        }

        return evidences;
    }

}
