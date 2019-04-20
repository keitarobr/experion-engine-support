package br.ufsc.ppgcc.experion.tests;

import br.ufsc.ppgcc.experion.extractor.algorithm.lda.LDA;
import br.ufsc.ppgcc.experion.extractor.evidence.PhysicalEvidence;
import br.ufsc.ppgcc.experion.extractor.input.EvidenceSourceInput;
import br.ufsc.ppgcc.experion.extractor.input.engine.technique.LDAExtractionTechnique;
import br.ufsc.ppgcc.experion.model.expert.Expert;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.types.Instance;
import cc.mallet.types.SingleInstanceIterator;
import cc.mallet.types.TokenSequence;
import com.optimaize.langdetect.i18n.LdLocale;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestsLDA {

    @Test
    public void testTokenizer() throws IOException {


        String texto = "A mudança governamental em 1964 - o que ocorreu?";


        try {
            Pattern tokenPattern =
                    Pattern.compile("[\\p{L}\\p{N}_]+");

            Instance carrier = new Instance(texto, null, null, null);
            SerialPipes p = new SerialPipes(new Pipe[]{
                    new Input2CharSequence(),
                    new CharSequence2TokenSequence(tokenPattern)});
            carrier = p.newIteratorFrom(new SingleInstanceIterator(carrier)).next();
            TokenSequence ts = (TokenSequence) carrier.getData();
            System.out.println("===");
            System.out.println(ts.toString());

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTopics() throws IOException {
        List<String> linhas = FileUtils.readLines(new File(this.getClass().getResource("/perfil_teste.txt").getFile()), "UTF-8");
        Set<PhysicalEvidence> evidences = linhas.stream().map(linha -> {PhysicalEvidence ev = new PhysicalEvidence();
        ev.getKeywords().add(linha.toLowerCase());
        ev.setTimestamp(new Date());
        ev.setInput(new EvidenceSourceInput());
        return ev;}).collect(Collectors.toSet());

        LDAExtractionTechnique tech = new LDAExtractionTechnique();
        tech.generateEvidences(new Expert(), evidences, "en").stream().forEachOrdered(topico -> System.out.println(topico.getKeywords().stream().sorted().collect(Collectors.toList())));

//        LDAExtractionTechnique tech = new LDAExtractionTechnique();
//        tech.generateEvidences(new Expert(), evidences, "pt").stream().forEachOrdered(topico -> System.out.println(topico.getKeywords().stream().sorted().collect(Collectors.toList())));
    }
}
