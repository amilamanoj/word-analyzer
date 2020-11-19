package org.amila.wordanalyzer;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString;
import de.tudarmstadt.ukp.jwktl.api.filter.WiktionaryEntryFilter;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.Language;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.amila.wordanalyzer.beans.*;
import org.amila.wordanalyzer.cloud.GSheetsConnector;
import org.amila.wordanalyzer.nlp.Inflector;
import org.amila.wordanalyzer.util.AnalyzerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amila on 7/27/2014.
 */
public class Analyzer {
    public static final int WORD_LENGTH = 3;
    public static final int WORD_FREQ = 1;
    private static final boolean FILTER_BY_COMMON = false;
    private static final boolean FILTER_BY_MASTERED = false;
    private static final boolean FILTER_BY_DICTIONARY_WORD = true;
    private static final boolean TOLOWERCASE = false;
    private static final boolean LEMMATIZE = false;
    private static final boolean SHORTEN = true;
    private String defaultFileLoc;
    public static final String MASTERED_LIST_TXT = "masteredWordList.txt";
    public static final String INTEREST_LIST = "interestWordList.txt";
    //    public static final String FREQ_LIST_5000 = "freq5000.csv";
    public static final String FREQ_LIST_100k = "top100k.txt";
    private String wiktionaryDump;
    private Map<String, WordInfo> filteredWordMap = new HashMap<>();
    private WiktionaryEntryFilter filter = new WiktionaryEntryFilter();
    private IWiktionaryEdition wkt;
    private Set<String> knownWordList;
    private Set<String> mastered = new HashSet<>();
    private Set<String> interestList;
    private Map<String, Integer> frequencyList5k = new HashMap<>();
    private Map<String, Integer> frequencyList100k = new HashMap<>();
    private List<WordInfo> nonDictionary = new ArrayList<>();
    private JobInfo jobInfo = new JobInfo();
    private String text;
    private boolean initialized;
    private ILanguage language;
    private static Logger logger = LoggerFactory.getLogger(Analyzer.class);

    private boolean loadMastered = false;

    private String[] sentences;
    private List<String[]> tokensList = new ArrayList<>();
    private List<String[]> tagsList = new ArrayList<>();
    private List<double[]> probsList = new ArrayList<>();

    public static void main(String[] args) throws AnalyzerException, IOException {
//        String file = "./The Two Towers - J. R. R. Tolkien.txt";
        logger.info("Reading file...");
        byte[] bytes = Files.readAllBytes(Paths.get("lernkrimi Mord unter den Linden.txt"));
        String text = new String(bytes);
        Analyzer analyzer = new Analyzer("kri", text);
        analyzer.analyze();
        String[] sentences = analyzer.sentences;
        List<String[]> tokensList = analyzer.tokensList;
        List<String[]> tagsList = analyzer.tagsList;
        List<double[]> probsList = analyzer.probsList;

        StringBuilder builder = new StringBuilder();
        builder.append("<head><link rel=\"stylesheet\" href=\"pos.css\"></head>");
        for (int x = 0; x < sentences.length; x++) {
            String[] tokens = tokensList.get(x);
            String[] tags = tagsList.get(x);
            double[] probs = probsList.get(x);
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                String tag = tags[i];
               double prob = probs[i];

//               if (prob > 0.01) {
                   builder.append("<span class=").append(tag).append(">").append(token).append("</span>").append(" ");
//               } else {
//                   builder.append(token).append(" ");
//               }

            }
            builder.append("<br/>");
        }
        Files.write(Paths.get("out.html"), builder.toString().getBytes(StandardCharsets.UTF_8));


//            Files.write(, out, Charset.defaultCharset(), null);

    }

    public Analyzer(String title, String text) throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/application.properties"));
        defaultFileLoc = properties.getProperty("file.location");
        wiktionaryDump = properties.getProperty("wiktionary.dump");
        language = Language.get(properties.getProperty("language"));
        jobInfo.setTitle(title);
        this.text = text;
    }

    public void analyzeAsync() {
        jobInfo.setStatusCode(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    analyze();
                } catch (Exception e) {
                    e.printStackTrace();
                    jobInfo.setStatus("Failed: " + e.getMessage());
                }
            }
        }).start();
    }

    public void analyze() throws AnalyzerException, IOException {
        if (!initialized) {
            initialize();
        }
        tokensList = new ArrayList<>();
        tagsList = new ArrayList<>();
        probsList = new ArrayList<>();
        logger.info("Analyzing...");

        sentences = detectSentences(text);

        posTag(sentences);

        Map<String, WordInfo> distinctWordMap = mergeAndCount();
        filter(distinctWordMap);

//        List<List<WordInfo>> wordInfoListGrouped = groupWords();
//        sortAndPrint(wordInfoListGrouped);

        logger.info(jobInfo.toString());
        jobInfo.setStatus("Finished");
        jobInfo.setStatusCode(2);
    }

    private String[] detectSentences(String text) throws IOException {

        jobInfo.setStatus("Detecting sentences...");
        InputStream modelIn = getClass().getResourceAsStream("/de-sent.bin");
        final SentenceModel sentenceModel = new SentenceModel(modelIn);
        modelIn.close();

        SentenceDetector sentenceDetector = new SentenceDetectorME(sentenceModel);
        return sentenceDetector.sentDetect(text);
    }


    private void posTag(String[] sentences) throws IOException {

        final String POS_MODEL = "de-pos-maxent.bin";

        try (InputStream posModelIn = new FileInputStream(POS_MODEL)) {
            jobInfo.setStatus("Reading POS model...");
            // loading the parts-of-speech model from stream
            POSModel posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            POSTaggerME posTagger = new POSTaggerME(posModel);

            // tokenize the content
            jobInfo.setStatus("Tokenizing and POS tagging...");

            Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;

            int sentenceCount = sentences.length;
            for (int i = 0; i < sentenceCount; i++) {
                double progress = (double) i / sentenceCount * 100;
                jobInfo.setStatus(String.format( "%.1f", progress) + "% tagged");
                String sentence = sentences[i].replaceAll("[^\\p{L}\\p{Nd},.\\-;\"]+", " ").trim();
//                String sentence = sentences[i];
                String[] tokens = tokenizer.tokenize(sentence);
                tokensList.add(tokens);
                // Tagger tagging the tokens
                tagsList.add(posTagger.tag(tokens));
                // Getting the probabilities of the tags given to the tokens
                probsList.add(posTagger.probs());
            }

//            System.out.println("Token\t:\tTag\n---------------------------------------------");
//            for (int i = 0; i < tokens.length; i++) {
//                if (tags[i].startsWith("V"))
//                    System.out.println(tokens[i] + "\t:\t" + tags[i]);
//            }
        }
    }


    // this method tokenises the text, and returns a map of words and their frequencies
    private Map<String, WordInfo> mergeAndCount() throws IOException {
        jobInfo.setStatus("Parsing...");
        Map<String, WordInfo> distinctWordMap = new HashMap<>();

//            text = text.replaceAll("\\r", "");
//            words = text.split("\\n+|\\s|—|-"); //= text.split(" ");

//        jobInfo.setOriginalWords(tokens.length);
        for (int x = 0; x < sentences.length; x++) {
            String[] tokens = tokensList.get(x);
            String[] tags = tagsList.get(x);
            double[] probs = probsList.get(x);
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                String tWord = token;
                String tag = tags[i];
                double prob = probs[i];

                try {
                    PartOfSpeech wktlPos = AnalyzerUtils.toWktlPos(tag);
                } catch (Exception e) {
                    logger.warn("Skipping non-existing POS: {} {} {} {}", token, tag, prob, e.getMessage());
                    continue;
                }

                if (prob < 0.3) {
                    logger.warn("Skipping low confidence word {} {} {}: ", token, tag, prob);
                    continue;
                }
                if (TOLOWERCASE) {
                    tWord = tWord.toLowerCase();
                }
//                if (SHORTEN) {
                    // this is better done at filtering using the dictionary, because stemming algorithms are not perfect
//                tWord = Inflector.getInstance().singularize(tWord);
//                }

                WordInfo wordInfo = distinctWordMap.get(tWord);
                if (wordInfo == null) {
                    wordInfo = new WordInfo(language);
                    wordInfo.setWord(tWord);
                    wordInfo.setFrequency(1);
                    wordInfo.getTextVariations().add(token);
                    wordInfo.addPartOfSpeech(tag);
                    distinctWordMap.put(tWord, wordInfo);
                } else {
                    wordInfo.setFrequency(wordInfo.getFrequency() + 1);
                    wordInfo.addPartOfSpeech(tag);
                    wordInfo.getTextVariations().add(token);
                }
            }
        }
        jobInfo.setDistinctWords(distinctWordMap.size());
        return distinctWordMap;
    }


    private void filter(Map<String, WordInfo> distinctWordMap) {
        logger.info("Filtering...");
        jobInfo.setStatus("Filtering...");
        int i = 0;
        for (WordInfo wordInfo : distinctWordMap.values()) {
            i++;
            if (i % 1000 == 0) {
                logger.info(i + " of " + distinctWordMap.size());
            }
            if (i % 100 == 0) {
                jobInfo.setProgress(i);
            }
            if (filterWord(wordInfo)) {
                filteredWordMap.put(wordInfo.getWord(), wordInfo);

            }
        }

        jobInfo.setAfterFilter(filteredWordMap.size());

    }

    private List<List<WordInfo>> groupWords() {
        Map<Integer, List<WordInfo>> filteredWordMapGrouped = new TreeMap<>();

        for (WordInfo wordInfo : filteredWordMap.values()) {
            List<WordInfo> wordInfoList = filteredWordMapGrouped.get(wordInfo.getFrequency());
            if (wordInfoList == null) {
                wordInfoList = new ArrayList<>();
                wordInfoList.add(wordInfo);
                filteredWordMapGrouped.put(wordInfo.getFrequency(), wordInfoList);
            } else {
                wordInfoList.add(wordInfo);
            }
        }
        jobInfo.setGroups(filteredWordMapGrouped.size());
        return new ArrayList<>(filteredWordMapGrouped.values());
    }

    private void sortAndPrint(List<List<WordInfo>> wordInfoListGrouped) {
        logger.info("Sorting...");
        jobInfo.setStatus("Sorting...");
        Collections.reverse(wordInfoListGrouped);
        List<WordInfo> wordInfoList = new ArrayList<>(filteredWordMap.values());
        Collections.sort(wordInfoList, new WordInfo.FrequencyComparator());
        jobInfo.setStatus("Completed");
        for (List<WordInfo> list : wordInfoListGrouped) {
            logger.info(list + "\n");
        }
        for (WordInfo wordInfo : wordInfoList) {
            logger.info(wordInfo + "\n");
        }
        logger.info("======= non dic ============");
        logger.info(nonDictionary.toString());

    }

    public String getExampleSentence(String word_re) {
        List<String> allMatches = findExampleSentenses(word_re);
        StringBuilder res = new StringBuilder();
        res.append("<ol>");
        for (String line : allMatches) {
            res.append("<li>").append(line).append("</li>");
        }
        res.append("</ol>");
        return res.toString();
    }

    private List<String> findExampleSentenses(String word_re) {
        List<String> allMatches = new ArrayList<>();

        word_re = "[^.]*\\b(" + word_re + ")\\b[^.]*[.]";
        Pattern re = Pattern.compile(word_re,
                Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher match = re.matcher(text);
        while (match.find() && allMatches.size() < 10) {
            allMatches.add(match.group());
        }
        return allMatches;
    }

    private void initialize() throws AnalyzerException {
        logger.info("Loading Wiktionary...");
        jobInfo.setStatus("Loading Wiktionary...");
        try {
            wkt = JWKTL.openEdition(new File(wiktionaryDump));
        } catch (Exception e) {
            throw new AnalyzerException("Failed to load dictionary: " + e.getMessage(), e);
        }
        logger.info("Finished loading Wiktionary");
        jobInfo.setStatus("Loading lists...");
        loadTopLists();
        loadInterestList();
        if (loadMastered) {
            logger.info("Loading mastered list");
            mastered = GSheetsConnector.getVerbs();
        }
        knownWordList = new HashSet<>(interestList);
        knownWordList.addAll(mastered);
        initialized = true;
    }

    private void loadInterestList() {
        //interestList = (Files.readAllLines(Paths.get(getWordSetFilePath(ListType.INTEREST)), Charset.defaultCharset()));
        interestList = new HashSet<>();
    }

    private void loadTopLists() {
        logger.info("Loading top list");
        List<String> top100k = new ArrayList<>(); //Files.readAllLines(Paths.get(getWordSetFilePath(ListType.TOP100K)), Charset.defaultCharset());
        int x = 0;
        for (String freqEntry : top100k) {
            x++;
            String[] freqs = freqEntry.split("\t");
            frequencyList100k.put(freqs[0], x);
            if (x <= 5000) {
                frequencyList5k.put(freqs[0], x);
            }
        }
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    private boolean filterWord(WordInfo wordInfo) {
        if (wordInfo.getWord().length() >= WORD_LENGTH && wordInfo.getFrequency() >= WORD_FREQ) {
            if (FILTER_BY_MASTERED) {
                String word = wordInfo.getWord();
                if (knownWordList.contains(word)) {
                    jobInfo.setMasteredWords(jobInfo.getMasteredWords() + 1);
                    return false;
                } else if (SHORTEN) {
                    // this should be used even with stanford lemmatizer (it doesn't return base word of some words. eg: adverbs)
                    word = Inflector.getInstance().singularize(word);// buggy, eg: perhaps returns perhap.  maybe try : http://www.dzone.com/snippets/java-inflections
                    if (knownWordList.contains(word)) {
                        jobInfo.setMasteredWords(jobInfo.getMasteredWords() + 1);
                        return false;
                    }
                    // my simple shortener
                    //word = shortenWord(word);
                    // snowball nlp
//                nlp.setCurrent(word);
//                nlp.stem();
//                word = nlp.getCurrent();
//                if (knownWordList.contains(word)) {
//                    jobInfo.setMasteredWords(jobInfo.getMasteredWords() + 1);
//                    return false;
//                }
                    //wordInfo.setStem(word);
                }
            }

            if (FILTER_BY_COMMON) {
                if (CommonWords.getCommonWords(language.getCode()).contains(wordInfo.getWord())) {
                    jobInfo.setCommonWords(jobInfo.getCommonWords() + 1);
                    return false;
                }
            }

            if (FILTER_BY_DICTIONARY_WORD) {
                List<IWiktionaryEntry> entries = wkt.getEntriesForWord(wordInfo.getWord(), filter);
                if (entries.isEmpty()) {
                    nonDictionary.add(wordInfo);
                    jobInfo.setNonDictionaryWords(jobInfo.getNonDictionaryWords() + 1);
                    return false;
                }
                wordInfo.processEntries(entries, true);
                if (wordInfo.getTotalSenses() == 0) {
                    nonDictionary.add(wordInfo);
                    jobInfo.setNonDictionaryWords(jobInfo.getNonDictionaryWords() + 1);
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private String shortenWord(String word) {
        if (word.endsWith("ing")) {
            return word.substring(0, word.length() - 3);
        } else if (word.endsWith("ly")) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("ness")) {
            return word.substring(0, word.length() - 4);
        } else if (word.endsWith("less")) {
            return word.substring(0, word.length() - 4);
        } else if (word.endsWith("able")) {
            return word.substring(0, word.length() - 4);
        } else if (word.endsWith("ion")) {
            return word.substring(0, word.length() - 3);
        } else if (word.endsWith("er")) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("ed")) {
            return word.substring(0, word.length() - 1);
        } else {
            return word;
        }

    }

    public List<WordBean> getNewWords() {
        logger.info("Getting new words");
        long id = 0;
        List<WordBean> newWordList = new ArrayList<>();
        for (Map.Entry<String, WordInfo> entry : filteredWordMap.entrySet()) {
            WordBean bean = new WordBean();
            bean.setId(id++);
            WordInfo info = entry.getValue();
            bean.setWord(info.getWord());
            bean.setFrequency(info.getFrequency());
            bean.setPartsOfSpeech(info.getPartOfSpeechCommaSeparated());
            bean.setVariations(info.getTextVariations().toString());
            bean.setStem(info.getStem());
            bean.setSenses(info.getTotalSenses());
            bean.setSize(info.getWord().length());
            Integer rank100k = frequencyList100k.get(bean.getWord());
            if (rank100k != null && rank100k > 0) {
                bean.setRank100k(rank100k);
            }
            bean.setMastered(mastered.contains(info.getStem()));
            setRemarks(bean, info);
            newWordList.add(bean);
        }
        logger.info("Getting new words finished");
        return newWordList;
    }

    private void setRemarks(WordBean bean, WordInfo info) {
        if (CommonWords.getCommonWords(language.getCode()).contains(info.getStem())) {
            bean.setRemarks("COMMON");
        } else {
            bean.setRemarks("NEW");
        }
    }

    public String getWordDetails(String word) {
        WordInfo wordInfo = filteredWordMap.get(word);
        if (wordInfo == null) {
            List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word, filter);
            if (entries != null) {
                wordInfo = new WordInfo(language);
                wordInfo.setWord(word);
                wordInfo.processEntries(entries, false);
            } else {
                return "Not found";
            }
        }
        StringBuilder sb = new StringBuilder();
        for (WordEntry wordEntry : wordInfo.getEntryList()) {
            IWiktionaryEntry entry = wordEntry.getEntry();
            sb.append(entry.getPartOfSpeech());

            sb.append(":<br/>");
            for (IWiktionarySense sense : entry.getSenses()) {
                if (sense == null) continue;
                sb.append(sense.getIndex());
                sb.append(" - ");
                WikiString gloss = (WikiString) sense.getGloss();
                String wikiText = sense.getGloss().getText();
                String plainText = sense.getGloss().getPlainText();
                Matcher matcher = WordInfo.TEMPLATE_PATTERN.matcher(wikiText);
                String templateText = "";
                if (matcher.find()) {
                    templateText = (matcher.group(0));
                }
                if (templateText.contains("dated")) {
                    sb.append("[DATED] ");
                }
                if (templateText.contains("archaic")) {
                    sb.append("[ARCHAIC] ");
                    plainText = "<s>" + plainText + "</s>";
                }
                if (templateText.contains("obsolete")) {
                    sb.append("[OBSOLETE] ");
                    plainText = "<s>" + plainText + "</s>";
                }
                if (!plainText.isEmpty()) {
                    sb.append(plainText);
                } else {
                    sb.append(gloss.getTextIncludingWikiMarkup());
                }
                sb.append("<br/>");
            }
            sb.append("<hr/>");
        }
        return sb.toString();
    }

    public void addToList(String word, ListType listType) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getWordSetFilePath(listType), true)))) {
            out.println(word);
            out.close();
            getWordSet(listType).add(word);
            filteredWordMap.remove(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<WordBean> getListWords(ListType listType, boolean annotateMastered) {
        List<WordBean> mList = new ArrayList<>();
        long id = 0;
        try {

            for (String word : getWordSet(listType)) {
                if (word.length() < WORD_LENGTH) {
                    continue;
                }
                WordInfo wordInfo = new WordInfo(language);
                wordInfo.setWord(word);
                List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word, filter);
                if (entries != null) {
                    wordInfo.processEntries(entries, false);
                }
                WordBean bean = new WordBean();
                bean.setId(id++);
                bean.setWord(wordInfo.getWord());
                bean.setPartsOfSpeech(wordInfo.getPartOfSpeechCommaSeparated());
                bean.setSenses(wordInfo.getTotalSenses());
                Integer rank = frequencyList100k.get(word);
                if (rank != null && rank > 0) {
                    bean.setRank(rank);
                }
                if (annotateMastered) {
                    if (CommonWords.getCommonWords(language.getCode()).contains(word)) {
                        bean.setRemarks("Common");
                    } else if (mastered.contains(word)) {
                        bean.setRemarks("Mastered");
                    } else if (interestList.contains(word)) {
                        bean.setRemarks("Interest");
                    } else {
                        String wordSin = Inflector.getInstance().singularize(word);
                        if (CommonWords.getCommonWords(language.getCode()).contains(wordSin)) {
                            bean.setRemarks("Common");
                        } else if (mastered.contains(wordSin)) {
                            bean.setRemarks("Mastered");
                        } else if (interestList.contains(wordSin)) {
                            bean.setRemarks("Interest");
                        } else {
                            bean.setRemarks("New");
                        }
                    }

                }
                mList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mList;
    }

    public Set<String> getWordSet(ListType listType) {
        switch (listType) {
            case MASTERED:
                return mastered;
            case INTEREST:
                return interestList;
            case TOP5K:
                return frequencyList5k.keySet();
            default:
                return new HashSet<>();
        }
    }

    public String getWordSetFilePath(ListType listType) {
        switch (listType) {
            case MASTERED:
                return defaultFileLoc + MASTERED_LIST_TXT;
            case INTEREST:
                return defaultFileLoc + INTEREST_LIST;
            case TOP100K:
                return defaultFileLoc + FREQ_LIST_100k;
            default:
                return null;
        }
    }

    private List<String> filterDictionaryWords(List<String> input) {
        List<String> outList = new ArrayList<>();
        int i = 0;
        int j = 0;
        for (String word : input) {
            i++;
            if (i % 1000 == 0) {
                logger.info(i + "," + j);
            }

            List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word.split("\t")[0], filter);
            if (entries != null && !entries.isEmpty()) {
                j++;
                outList.add(word);
            }
        }
        return outList;
    }


    public enum ListType {
        MASTERED, INTEREST, TOP5K, TOP100K
    }


    public static class AnalyzerException extends Exception {
        public AnalyzerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
