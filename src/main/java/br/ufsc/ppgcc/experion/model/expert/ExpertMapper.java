package br.ufsc.ppgcc.experion.model.expert;

import br.ufsc.ppgcc.experion.extractor.source.EvidenceSource;

import java.util.HashMap;
import java.util.Map;

public class ExpertMapper {

    private Expert expert;
    private Map<String, String> expertMapper = new HashMap<>();

    public ExpertMapper addId(String expertId, EvidenceSource source) {
        expertMapper.put(source.getName(), expertId);
        return this;
    }

    public String idFor(EvidenceSource source) {
        return expertMapper.get(source.getId());
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }
}
