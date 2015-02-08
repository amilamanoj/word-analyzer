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
    public static final String MASTERED_LIST_TXT = "D:\\Dropbox\\masteredWordList.txt";
    public static final String INTEREST_LIST = "D:\\Dropbox\\interestWordList.txt";
    public static final String FREQ_LIST_5000 = "D:\\Dropbox\\freq5000.csv";
    private Map<String, WordInfo> distinctWordMap = new HashMap<String, WordInfo>();
    private Map<Integer, List<WordInfo>> filteredWordMapGrouped = new TreeMap<>();
    private Map<String, WordInfo> filteredWordMap = new HashMap<>();
    WiktionaryEntryFilter filter = new WiktionaryEntryFilter();
    IWiktionaryEdition wkt;
    private List<List<WordInfo>> wordInfoListGrouped;
    private Set<String> knownWordList;
    private Set<String> mastered;
    private Set<String> interestList;
    private Map<String, Integer> frequencyList5000 = new HashMap<>();
    private List<WordInfo> nonDictionary = new ArrayList<>();
    private JobInfo jobInfo = new JobInfo();
    private String text;
    private boolean initialized;
    SnowballStemmer stemmer = new englishStemmer();

    public static void main(String[] args) throws IOException, URISyntaxException {
//        String file = "./The Two Towers - J. R. R. Tolkien.txt";
//        String file = "The Fellowship of the Ring - J. R. R. Tolkien.txt";
//        String file = "./The Return of the King - J. R. R. Tolkien.txt";
//        String file = "books/Space Chronicles_ Facing the Ul - Neil Degrasse Tyson.txt";
//        String file = "Maps of Time_ An Introduction t - David Christian.txt";
//        String file = "books/Rendezvous With Rama - Arthur C. Clarke.txt";
//        String file = "The Alchemist - Ben Jonson.txt";
//        String file = "Pride and Prejudice - Jane Austen.txt";
//        String file = "Adventures of Huckleberry Finn - Mark Twain.txt";
//        String file = "books/The Yellow Wallpaper - Charlotte Perkins Gilman.txt";
        String file = "timeMachine.txt";
//        String file = "books/The Hobbit_ Or There and Back A - J. R. R. Tolkien.txt";
        System.out.println("Reading file...");
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        String text = new String(bytes);
        Analyzer analyzer = new Analyzer();
        analyzer.initialize(file, text);
        analyzer.analyze();
    }

    public Analyzer() {
//        List<IWiktionaryEntry> entries = wkt.getEntriesForWord("whither", filter);
//        System.out.println(entries);
//        IWiktionaryPage page = wkt.getPageForWord("whither");
//        System.out.println(page);
//        IWiktionaryIterator<IWiktionaryEntry> itr = wkt.getAllEntries();
//        while (itr.hasNext()) {
//            IWiktionaryEntry entry = itr.next();
//            if (entry.getPartOfSpeech() == PartOfSpeech.PROPER_NOUN && entry.getWordLanguage() == Language.ENGLISH) {
//                System.out.println(entry);
//            }
//        }
        if (!initialized) {
            System.out.println("Loading Wiktionary...");
            wkt = JWKTL.openEdition(new File("D:\\WikitionaryDumpEn\\WikitionaryParsed"));
            System.out.println("Finished loading Wiktionary");
        }
    }

    public void initialize(String title, String text) {
//        filter.setAllowedWordLanguages(Language.ENGLISH);
//        filter.setAllowedWordLanguages();
        jobInfo.setTitle(title);
        this.text = text;
        initialized = true;
    }

    public void analyzeAsync() {
        jobInfo.setStatusCode(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    analyze();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void analyze() throws IOException, URISyntaxException {
        System.out.println("Loading frequency list...");
        List<String> freqList = Files.readAllLines(Paths.get(FREQ_LIST_5000), Charset.defaultCharset());
        for (String freqEntry : freqList) {
            String[] freqs = freqEntry.split(",");
            frequencyList5000.put(freqs[1], Integer.parseInt(freqs[0]));
        }
        System.out.println("Loading mastered list");
        jobInfo.setStatus("Loading mastered list...");
        interestList = new HashSet<>(Files.readAllLines(Paths.get(INTEREST_LIST), Charset.defaultCharset()));
        mastered = new HashSet<>(Files.readAllLines(Paths.get(MASTERED_LIST_TXT), Charset.defaultCharset()));
        knownWordList = new HashSet<>(interestList);

        knownWordList.addAll(mastered);
        System.out.println("Parsing...");
        jobInfo.setStatus("Parsing...");

        String[] words;
        if (LEMMATIZE) {
            StanfordLemmatizer slem = new StanfordLemmatizer();
            // getting lemma of each word. this singularize the word. makes the word present tense, etc
            // but sometimes this does not get the base word. for example: adverbs (quietly) and some past tense (worried)
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

            if (info.getTotalSenses() > 10) {
                bean.setRemarks(bean.getRemarks() + "10+ Senses");
            }

            Integer rank = frequencyList5000.get(bean.getWord());
            if (rank != null && rank > 0) {
                bean.setRank(rank);
            }
            newWordList.add(bean);
        }
        System.out.println("Getting new words finished");
        return newWordList;
    }

    public String getWordDetails(String word) {
        WordInfo wordInfo = filteredWordMap.get(word);
        if (wordInfo == null) {
            return "Not found";
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

    public List<WordBean> getListWords(ListType listType) {
        List<WordBean> mList = new ArrayList<>();
        long id = 0;
        try {

            for (String masteredWord : getWordSet(listType)) {
                WordInfo wordInfo = new WordInfo();
                wordInfo.setWord(masteredWord);
                List<IWiktionaryEntry> entries = wkt.getEntriesForWord(masteredWord, filter);
                if (entries != null) {
                    wordInfo.processEntries(entries);
                }
                WordBean bean = new WordBean();
                bean.setId(id++);
                bean.setWord(wordInfo.getWord());
                bean.setPartsOfSpeech(wordInfo.getPartOfSpeechCommaSeparated());
                if (wordInfo.getTotalSenses() > 10) {
                    bean.setRemarks(bean.getRemarks() + "10+ Senses");
                }
                Integer rank = frequencyList5000.get(masteredWord);
                if (rank != null && rank > 0) {
                    bean.setRank(rank);
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
            default:
                return new HashSet<>();
        }
    }
    public String getWordSetFilePath(ListType listType) {
        switch (listType) {
            case MASTERED:
                return MASTERED_LIST_TXT;
            case INTEREST:
                return INTEREST_LIST;
            default:
                return null;
        }
    }


 public enum ListType {
     MASTERED, INTEREST
 }

}
