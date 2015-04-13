package org.amila.wordanalyzer;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString;
import de.tudarmstadt.ukp.jwktl.api.filter.WiktionaryEntryFilter;
import org.amila.wordanalyzer.lemmatizer.StanfordLemmatizer;
import org.amila.wordanalyzer.stemmer.SnowballStemmer;
import org.amila.wordanalyzer.stemmer.englishStemmer;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Amila on 7/27/2014.
 */
public class Analyzer {
    public static final int WORD_LENGTH = 3;
    public static final int WORD_FREQ = 1;
    private static final boolean FILTER_BY_COMMON = true;
    //    private static final boolean FILTER_BY_MASTERED = true;
    private static final boolean FILTER_BY_DICTIONARY_WORD = true;
    private static final boolean ONLY_NOUNS = false;
    private static final boolean TOLOWERCASE = true;
    private static final boolean LEMMATIZE = true;
    private static final boolean SHORTEN = true;
    public static final String DEFAULT_FILE_LOC = "D:\\DB\\Dropbox\\";
    public static final String MASTERED_LIST_TXT = "masteredWordList.txt";
    public static final String INTEREST_LIST = "interestWordList.txt";
//    public static final String FREQ_LIST_5000 = "freq5000.csv";
    public static final String FREQ_LIST_100k = "top100k.txt";
    private static final String WIKTIONARY_DUMP = "D:\\WikitionaryDumpEn\\WikitionaryParsed";
    private Map<String, WordInfo> distinctWordMap = new HashMap<String, WordInfo>();
    private Map<Integer, List<WordInfo>> filteredWordMapGrouped = new TreeMap<>();
    private Map<String, WordInfo> filteredWordMap = new HashMap<>();
    WiktionaryEntryFilter filter = new WiktionaryEntryFilter();
    IWiktionaryEdition wkt;
    private List<List<WordInfo>> wordInfoListGrouped;
    private Set<String> knownWordList;
    private Set<String> mastered;
    private Set<String> interestList;
    private Map<String, Integer> frequencyList5k = new HashMap<>();
    private Map<String, Integer> frequencyList100k = new HashMap<>();
    private List<WordInfo> nonDictionary = new ArrayList<>();
    private JobInfo jobInfo = new JobInfo();
    private String text;
    private boolean initialized;
    SnowballStemmer stemmer = new englishStemmer();

    public static void main(String[] args) throws IOException, URISyntaxException {
//        String file = "./The Two Towers - J. R. R. Tolkien.txt";
//        System.out.println("Reading file...");
//        byte[] bytes = Files.readAllBytes(Paths.get(file));
//        String text = new String(bytes);
//        Analyzer analyzer = new Analyzer();
//        analyzer.analyze();

        Analyzer analyzer = new Analyzer();
        List<String> inp = Files.readAllLines(Paths.get("D:\\WordAnalyzer\\count_1w.txt"), Charset.defaultCharset());
        List<String> out = analyzer.filterDictionaryWords(inp);
        System.out.println(out.size());
        PrintWriter outW = new PrintWriter("D:\\WordAnalyzer\\out.txt");

        for (String w : out) {
            outW.append(w + System.lineSeparator() );
        }
        outW.close();
//            Files.write(, out, Charset.defaultCharset(), null);

    }

    public void initialize(String title, String text) {
//        filter.setAllowedWordLanguages(Language.ENGLISH);
//        filter.setAllowedWordLanguages();
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
                }
            }
        }).start();
    }

    public void analyze() throws IOException, URISyntaxException {
        if (!initialized) {
            initialize();
        }
        System.out.println("Parsing...");
        jobInfo.setStatus("Parsing...");

        String[] words;
        if (LEMMATIZE) {
            StanfordLemmatizer slem = new StanfordLemmatizer();
            // getting lemma of each word. this singularize the word. makes the word present tense, etc
            // sometimes this does not get the base word. for example: adverbs (quietly) and some past tense (worried)
            List<String> list = slem.lemmatize(text);
            words = list.toArray(new String[list.size()]);
        } else {
            text = text.replaceAll("\\r", "");
//        String[] words = text.split(" ");
            words = text.split("\\n+|\\s|—|-");
        }

        jobInfo.setOriginalWords(words.length);
//        for (String line : lines) {
//            String[] words = line.split(" ");
        for (String word : words) {
            String tWord = word.replaceAll("[^A-Za-z]", "");
            if (TOLOWERCASE) {
                tWord = tWord.toLowerCase();
            }
            if (SHORTEN) {
                // this is better done at filtering, because stemming algorithms are not perfect
//                tWord = Inflector.getInstance().singularize(tWord);
            }

            WordInfo wordInfo = distinctWordMap.get(tWord);
            if (wordInfo == null) {
                wordInfo = new WordInfo();
                wordInfo.setWord(tWord);
                wordInfo.setFrequency(1);
                wordInfo.getVariations().add(word);
                distinctWordMap.put(tWord, wordInfo);
            } else {
                wordInfo.setFrequency(wordInfo.getFrequency() + 1);
                wordInfo.getVariations().add(word);
            }
        }
        jobInfo.setDistinctWords(distinctWordMap.size());
//        }
        System.out.println("Filtering...");
        jobInfo.setStatus("Filtering...");
        int i = 0;
        for (WordInfo wordInfo : distinctWordMap.values()) {
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + " of " + distinctWordMap.size());
            }
            if (i % 100 == 0) {
                jobInfo.setProgress(i);
            }
            if (filterWord(wordInfo)) {
                filteredWordMap.put(wordInfo.getWord(), wordInfo);

                List<WordInfo> wordInfoList = filteredWordMapGrouped.get(wordInfo.getFrequency());
                if (wordInfoList == null) {
                    wordInfoList = new ArrayList<>();
                    wordInfoList.add(wordInfo);
                    filteredWordMapGrouped.put(wordInfo.getFrequency(), wordInfoList);
                } else {
                    wordInfoList.add(wordInfo);
                }
            }
        }

        jobInfo.setAfterFilter(filteredWordMap.size());

        wordInfoListGrouped = new ArrayList<>(filteredWordMapGrouped.values());
        List<WordInfo> wordInfoList = new ArrayList<>(filteredWordMap.values());

        jobInfo.setGroups(filteredWordMapGrouped.size());

        System.out.println("Sorting...");
        jobInfo.setStatus("Sorting...");
        Collections.reverse(wordInfoListGrouped);
        Collections.sort(wordInfoList, new WordInfo.FrequencyComparator());
        jobInfo.setStatus("Completed");
        for (List<WordInfo> list : wordInfoListGrouped) {
            System.out.println(list + "\n");
        }
//        for (WordInfo wordInfo : wordInfoList) {
//            System.out.println(wordInfo + "\n");
//        }

        System.out.println("======= non dic ============");
        System.out.println(nonDictionary);

        System.out.println(jobInfo);
        jobInfo.setStatusCode(2);
    }

    private void initialize() throws IOException {
        System.out.println("Loading Wiktionary...");
        jobInfo.setStatus("Loading Wiktionary...");
        wkt = JWKTL.openEdition(new File(WIKTIONARY_DUMP));
        System.out.println("Finished loading Wiktionary");
        jobInfo.setStatus("Loading lists...");
        List<String> top100k = Files.readAllLines(Paths.get(getWordSetFilePath(ListType.TOP100K)), Charset.defaultCharset());
        int x=0;
        for (String freqEntry : top100k) {
            x++;
            String[] freqs = freqEntry.split("\t");
            frequencyList100k.put(freqs[0], x);
            if (x<=5000) {
                frequencyList5k.put(freqs[0], x);
            }
        }
        System.out.println("Loading mastered list");
        interestList = new HashSet<>(Files.readAllLines(Paths.get(getWordSetFilePath(ListType.INTEREST)), Charset.defaultCharset()));
        mastered = new HashSet<>(Files.readAllLines(Paths.get(getWordSetFilePath(ListType.MASTERED)), Charset.defaultCharset()));
        knownWordList = new HashSet<>(interestList);
        knownWordList.addAll(mastered);
        initialized = true;
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    private boolean filterWord(WordInfo wordInfo) {
        if (wordInfo.getWord().length() >= WORD_LENGTH && wordInfo.getFrequency() >= WORD_FREQ) {
//            if (FILTER_BY_MASTERED) {
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
                word = shortenWord(word);
                // snowball stemmer
//                stemmer.setCurrent(word);
//                stemmer.stem();
//                word = stemmer.getCurrent();
                if (knownWordList.contains(word)) {
                    jobInfo.setMasteredWords(jobInfo.getMasteredWords() + 1);
                    return false;
                }
                wordInfo.setStem(word);
            }
//            }

            if (FILTER_BY_COMMON) {
                if (CommonWords.WORDS.contains(wordInfo.getWord())) {
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
                wordInfo.processEntries(entries);
            }

            if (ONLY_NOUNS) {
                if (!wordInfo.getPartOfSpeechSet().contains(PartOfSpeech.NOUN)) {
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
        System.out.println("Getting new words");
        long id = 0;
        List<WordBean> newWordList = new ArrayList<>();
        for (Map.Entry<String, WordInfo> entry : filteredWordMap.entrySet()) {
            WordBean bean = new WordBean();
            bean.setId(id++);
            WordInfo info = entry.getValue();
            bean.setWord(info.getWord());
            bean.setFrequency(info.getFrequency());
            bean.setPartsOfSpeech(info.getPartOfSpeechCommaSeparated());
            bean.setVariations(info.getVariations().toString());
            bean.setStem(info.getStem());
            bean.setSenses(info.getTotalSenses());
            bean.setSize(info.getWord().length());
            Integer rank100k = frequencyList100k.get(bean.getWord());
            if (rank100k != null && rank100k > 0) {
                bean.setRank100k(rank100k);
            }
            newWordList.add(bean);
        }
        System.out.println("Getting new words finished");
        return newWordList;
    }

    public String getWordDetails(String word) {
        WordInfo wordInfo = filteredWordMap.get(word);
        if (wordInfo == null) {
            List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word, filter);
            if (entries != null) {
                wordInfo = new WordInfo();
                wordInfo.setWord(word);
                wordInfo.processEntries(entries);
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
                WordInfo wordInfo = new WordInfo();
                wordInfo.setWord(word);
                List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word, filter);
                if (entries != null) {
                    wordInfo.processEntries(entries);
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
                    if (CommonWords.WORDS.contains(word)) {
                        bean.setRemarks("Common");
                    } else if (mastered.contains(word)) {
                        bean.setRemarks("Mastered");
                    } else if (interestList.contains(word)) {
                        bean.setRemarks("Interest");
                    } else {
                        String wordSin = Inflector.getInstance().singularize(word);
                        if (CommonWords.WORDS.contains(wordSin)) {
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
                return DEFAULT_FILE_LOC + MASTERED_LIST_TXT;
            case INTEREST:
                return DEFAULT_FILE_LOC + INTEREST_LIST;
            case TOP100K:
                return DEFAULT_FILE_LOC + FREQ_LIST_100k;
            default:
                return null;
        }
    }

    private List<String> filterDictionaryWords(List<String> input) {
        List<String> outList = new ArrayList<>();
        int i =0;
        int j=0;
        for (String word : input) {
            i++;
            if (i%1000==0) {
                System.out.println(i + ","+ j);
            }

            List<IWiktionaryEntry> entries = wkt.getEntriesForWord(word.split("\t")[0], filter);
            if (entries !=null && !entries.isEmpty() ){
                j++;
                outList.add(word);
            }
        }
        return outList;
    }


 public enum ListType {
     MASTERED, INTEREST, TOP5K, TOP100K
 }

}
