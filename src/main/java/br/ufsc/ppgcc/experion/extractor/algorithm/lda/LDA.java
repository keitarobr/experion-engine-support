package br.ufsc.ppgcc.experion.extractor.algorithm.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import com.optimaize.langdetect.i18n.LdLocale;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LDA {

    private Pipe pipe;
    private InstanceList instances;
    private ParallelTopicModel model;
    private int topicNumber = 10;
    private int iterations = 1000;
    private int optimizeInterval = 2000;
    //    private int iterations = 100;
//    private int optimizeInterval = 20;
    private double alpha = 5.0;
    private double beta = 0.01;
    private int burnIn = 200;

    public static String defaultLineRegex = "^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$";
    public static String defaultTokenRegex = "\\p{L}[\\p{L}\\p{P}]+\\p{L}";

    public ParallelTopicModel getModel() {
        return model;
    }

//    public LDA(int topicNumber, int iterations, int optimizeInterval, double alpha, double beta) {
//        this.topicNumber = topicNumber;
//        this.iterations = iterations;
//        this.optimizeInterval = optimizeInterval;
//        this.alpha = alpha;
//        this.beta = beta;
//        init();
//    }

    public LDA() {
        init();
    }

    private void init() {
        pipe = buildPipeFull();
    }


    private Pipe buildPipeFull() {
        Pipe instancePipe;
        // Build a new pipe
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Convert the "target" object into a numeric index
        //  into a LabelAlphabet.
//            if (labelOption.value > 0) {
//                if (targetAsFeatures.value) {
//                    pipeList.add(new TargetStringToFeatures());
//                }
//                else {
        // If the label field is not used, adding this
        //  pipe will cause "Alphabets don't match" exceptions.
        pipeList.add(new Target2Label());
//                }
//            }

        //
        // Tokenize the input: first compile the tokenization pattern
        //

        Pattern tokenPattern = null;

//            if (keepSequenceBigrams.value) {
//                // We do not want to record bigrams across punctuation,
//                //  so we need to keep non-word tokens.
//                tokenPattern = CharSequenceLexer.LEX_NONWHITESPACE_CLASSES;
//            }
//            else {
        // Otherwise, try to compile the regular expression pattern.

        try {
            tokenPattern = Pattern.compile(defaultTokenRegex);
        } catch (PatternSyntaxException pse) {

        }
//            }

        // String replacements

//            if (! preserveCase.value()) {
        pipeList.add(new CharSequenceLowercase());
//            }

//            if (replacementFiles.value != null || deletionFiles.value != null) {
//                NGramPreprocessor preprocessor = new NGramPreprocessor();
//
//                if (replacementFiles.value != null) {
//                    for (String filename: replacementFiles.value) { preprocessor.loadReplacements(filename); }
//                }
//                if (deletionFiles.value != null) {
//                    for (String filename: deletionFiles.value) { preprocessor.loadDeletions(filename); }
//                }
//
//                pipeList.add(preprocessor);
//            }

        // Add the tokenizer
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        //
        // Normalize the input as necessary
        //

//            if (keepSequenceBigrams.value) {
        // Remove non-word tokens, but record the fact that they
        //  were there.
//                pipeList.add(new TokenSequenceRemoveNonAlpha(true));
//            }

        // Stopword removal.

//            if (stoplistFile.wasInvoked()) {
//
//                // The user specified a new list
//
//                TokenSequenceRemoveStopwords stopwordFilter =
//                        new TokenSequenceRemoveStopwords(stoplistFile.value,
//                                encoding.value,
//                                false, // don't include default list
//                                false,
//                                keepSequenceBigrams.value);
//
//                if (extraStopwordsFile.wasInvoked()) {
//                    stopwordFilter.addStopWords(extraStopwordsFile.value);
//                }
//
//                pipeList.add(stopwordFilter);
//            }
//            else if (removeStopWords.value) {

        // The user did not specify a new list, so use the default
        //  built-in English list, possibly adding extra words.

        TokenSequenceRemoveStopwords stopwordFilter =
                new TokenSequenceRemoveStopwords(false, false);

//                if (extraStopwordsFile.wasInvoked()) {
//                    stopwordFilter.addStopWords(extraStopwordsFile.value);
//                }

        pipeList.add(stopwordFilter);
//            }

//            if (stopPatternFile.wasInvoked()) {
//                TokenSequenceRemoveStopPatterns stopPatternFilter =
//                        new TokenSequenceRemoveStopPatterns(stopPatternFile.value);
//                pipeList.add(stopPatternFilter);
//            }

        //
        // Convert tokens to numeric indices into the Alphabet
        //

//            if (keepSequenceBigrams.value) {
//                // Output is feature sequences with bigram features
//                pipeList.add(new TokenSequence2FeatureSequenceWithBigrams());
//            }
//            else if (keepSequence.value) {
        // Output is unigram feature sequences
        pipeList.add(new TokenSequence2FeatureSequence());
//            }
//            else {
//                // Output is feature vectors (no sequence information)
//                pipeList.add(new TokenSequence2FeatureSequence());
//                pipeList.add(new FeatureSequence2AugmentableFeatureVector());
//            }

//            if (printOutput.value) {
//                pipeList.add(new PrintInputAndTarget());
//            }

        instancePipe = new SerialPipes(pipeList);

        return instancePipe;
    }

    private Pipe buildPipe() {
        ArrayList pipeList = new ArrayList();

        // Read data from File objects
        pipeList.add(new Input2CharSequence("UTF-8"));

        // Regular expression for what constitutes a token.
        //  This pattern includes Unicode letters, Unicode numbers,
        //   and the underscore character. Alternatives:
        //    "\\S+"   (anything not whitespace)
        //    "\\w+"    ( A-Z, a-z, 0-9, _ )
        //    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
        //                                    a group of only punctuation marks)
        Pattern tokenPattern =
                Pattern.compile("[\\p{L}\\p{N}_]+");

        // Tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Rather than storing tokens as strings, convert
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        // Do the same thing for the "target" field:
        //  convert a class label string to a Label object,
        //  which has an index in a Label alphabet.
        pipeList.add(new Target2Label());

        return new SerialPipes(pipeList);
    }

    public LDA loadData(LdLocale locale, List<String> lines) {
        instances = new InstanceList(pipe);
        ArrayIterator iterator = new ArrayIterator(lines, locale.getLanguage());
        instances.addThruPipe(iterator);
        return this;
    }

    public LDA saveData(File file) {
        instances.save(file);
        return this;
    }


    public LDA createTopics() throws IOException {
        model = new ParallelTopicModel(topicNumber, alpha, beta);
//        model = new ParallelTopicModel(topicNumber);
        model.addInstances(instances);
        model.setNumThreads(1);
        model.setNumIterations(iterations);
        model.setOptimizeInterval(optimizeInterval);
        model.setTopicDisplay(0, 0);
        model.setBurninPeriod(this.burnIn);
        model.setSymmetricAlpha(false);
        model.estimate();
        return this;
    }

    public List<Set<String>> getTopicsWords() {
        Object[][] topicWords = model.getTopWords(10);
        LinkedList<Set<String>> topics = new LinkedList<>();


        for (Object[] topic : topicWords) {
            if (topic.length != 0) {
                Set<String> topicList = new TreeSet<>();

                for (Object word : topic) {
                    topicList.add(word.toString());
                }
                topics.add(topicList);
            }
        }

        return topics;
    }

    public void printTopics(PrintWriter writer) throws IOException {
        writer.println(String.format("\nTopics (for topics=%d) for (iterations=%d, alpha=%f, interval=%d)\n", topicNumber, iterations, alpha, optimizeInterval));

        Object[][] topics = model.getTopWords(8);
        SortedSet<String> topicList = new TreeSet<>();
        for (Object[] topic : topics) {
            SortedSet<String> sortedWords = new TreeSet<>();

            if (topic.length != 0) {
                for (Object word : topic) {
                    sortedWords.add(word.toString());
                }

                topicList.add(StringUtils.join(sortedWords, " "));
            }

        }
        writer.println(StringUtils.join(topicList, "\n"));
        writer.flush();
    }

}
