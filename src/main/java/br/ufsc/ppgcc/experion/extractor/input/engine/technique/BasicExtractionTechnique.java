package br.ufsc.ppgcc.experion.extractor.input.engine.technique;

import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.source.EvidenceSourceURL;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BasicExtractionTechnique implements ExtractionTechnique {

    protected String getOriginalRetrievedData(Collection<PhysicalEvidence> evidences) {
        return StringUtils.join(evidences.stream().filter(evidence -> evidence.getUrl() != null).map(evidence -> evidence.getUrl()).map(url -> url.getRetrievedData()).distinct().collect(Collectors.toList()), "\n");
    }

    protected String getOriginalURL(Collection<PhysicalEvidence> evidences) {
        return StringUtils.join(evidences.stream().filter(evidence -> evidence.getUrl() != null).map(evidence -> evidence.getUrl()).map(url -> url.getUrl()).distinct().collect(Collectors.toList()), "\n");
    }


    protected Class getOriginalURLClass(Collection<PhysicalEvidence> evidences) {
        Optional<PhysicalEvidence> evURL = evidences.stream().filter(evidence -> evidence.getUrl() != null).findAny();
        if (evURL.isPresent()) {
            return evURL.get().getUrl().getClass();
        } else {
            return null;
        }
    }

    protected void addOriginalURL(PhysicalEvidence ev, String originalData, String originalURL, Class evidenceURLSourceClass) {
        if (evidenceURLSourceClass != null) {
            EvidenceSourceURL url;
            try {
                url = (EvidenceSourceURL)evidenceURLSourceClass.getConstructor().newInstance();
                url.setUrl(originalURL);
                url.setRetrievedData(originalData);
                ev.setUrl(url);
            } catch (InstantiationException| InvocationTargetException |IllegalAccessException|NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

}
