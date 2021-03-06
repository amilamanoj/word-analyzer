package org.amila.wordanalyzer.beans;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import org.amila.wordanalyzer.Analyzer;
import org.amila.wordanalyzer.util.AnalyzerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Amila on 7/27/2014.
 */
public class WordInfo {
    private String word;
    private int frequency;
    private Set<String> textVariations = new HashSet<>();
    private List<WordEntry> entryList = new ArrayList<>();
    private Set<PartOfSpeech> wiktionaryPartOfSpeechSet = new HashSet<>();
    private Set<String> sttsPartOfSpeechSet = new HashSet<>();
    //    protected static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{.+?\\}\\}");
    public static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{.*?\\}\\}");
    private int totalWordSenses = 0;
    private String stem;
    private ILanguage language;
    private static Logger logger = LoggerFactory.getLogger(Analyzer.class);

    public WordInfo(ILanguage language) {
        this.language = language;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "{" + frequency + ":" + word +
//                " - " + partOfSpeechSet.toString() +
//                                 " - "+ variations.toString() +
                " - " + entryList.toString() +
                " }";
    }

    public static class FrequencyComparator implements Comparator<WordInfo> {
        @Override
        public int compare(WordInfo o1, WordInfo o2) {
            return o2.getFrequency() - o1.getFrequency();
        }
    }

    public Set<String> getTextVariations() {
        return textVariations;
    }

    public void setTextVariations(Set<String> textVariations) {
        this.textVariations = textVariations;
    }

    public void processEntries(List<IWiktionaryEntry> entries, boolean crossCheckWithPosTagger) {

        List<PartOfSpeech> partOfSpeeches = entries.stream().map(IWiktionaryEntry::getPartOfSpeech).collect(Collectors.toList());
        if (crossCheckWithPosTagger) {
            for (String sttsPos : sttsPartOfSpeechSet) {
                try {
                    PartOfSpeech wktlPos = AnalyzerUtils.toWktlPos(sttsPos);
                    if (partOfSpeeches.contains(wktlPos)) {
                        wiktionaryPartOfSpeechSet.add(wktlPos);
                    } else {
                        logger.warn("Wiktionary entry doesn't contain POS for the word: {} {} {}", word, sttsPos, wktlPos);
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
        } else {
            wiktionaryPartOfSpeechSet = new HashSet<>(partOfSpeeches);
        }

        totalWordSenses = 0;
        Set<String> baseWords = new HashSet<>();

        if ("bleibt".equals(word)) {
            System.out.println("xxx");
        }

        for (IWiktionaryEntry entry : entries) {
            if (entry.getWordLanguage() != language) {
                continue;
            }

            if (!wiktionaryPartOfSpeechSet.contains(entry.getPartOfSpeech())) {
                logger.warn("Skipping non-used part of speech for word: {} {}", word, entry.getPartOfSpeech());
                continue;
            }

            WordEntry wordEntry = new WordEntry();
            wordEntry.setEntry(entry);
            boolean allDated = true;
            boolean allArchaic = true;
            boolean allObsolete = true;
            int totalEntrySenses = 0;

            for (IWiktionarySense sense : entry.getSenses()) {
                String wikiText = sense.getGloss().getText();
                Matcher matcher = TEMPLATE_PATTERN.matcher(wikiText);
                String templateText = "";
                if (matcher.find()) {
                    templateText = (matcher.group(0));
                }
                if (templateText.contains("dated")) {
                    wordEntry.addPeriod(WordEntry.Period.DATED);
                } else {
                    allDated = false;
                }
                if (templateText.contains("archaic")) {
                    wordEntry.addPeriod(WordEntry.Period.ARCHAIC);
                } else {
                    allArchaic = false;
                }
                if (templateText.contains("obsolete")) {
                    wordEntry.addPeriod(WordEntry.Period.OBSOLETE);
                } else {
                    allObsolete = false;
                }

                String plainText = sense.getGloss().getPlainText();
                if (plainText != null && !plainText.isEmpty()) {
                    totalEntrySenses++;
                    totalWordSenses++;
                }
                if (!templateText.isEmpty()) {
                    String[] content = templateText.replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .split("\\|");
                    if (content.length > 0 && (content[0].contains("form of"))) {
                        baseWords.add(content[1]);
                        // if no description, but still a form of a word, this should still count
                        if (plainText == null || plainText.isEmpty()) {
                            totalEntrySenses++;
                            totalWordSenses++;
                        }
                    } else if (content[0].contains("inflection of")) {
                        baseWords.add(content[2]);
                        // if no description, but still a form of a word, this should still count
                        if (plainText == null || plainText.isEmpty()) {
                            totalEntrySenses++;
                            totalWordSenses++;
                        }
                    }
                }
            }

//            if (entry.getPartOfSpeech() != null && totalEntrySenses > 0) {
//                wiktionaryPartOfSpeechSet.add(entry.getPartOfSpeech());
//            } else {
//                System.out.println("No part of speech with valid senses (word, pos, sense, senseswtext: " + word
//                        + "," + entry.getPartOfSpeech()
//                        + "," + entry.getSenseCount()
//                        + "," + totalEntrySenses
//                );
//            }

            if (allDated) {
                wordEntry.addAllSensesPeriod(WordEntry.Period.DATED);
            }
            if (allArchaic) {
                wordEntry.addAllSensesPeriod(WordEntry.Period.ARCHAIC);
            }
            if (allObsolete) {
                wordEntry.addAllSensesPeriod(WordEntry.Period.OBSOLETE);
            }


            entryList.add(wordEntry);
        }
        if (baseWords.size() == 1) {
            this.stem = baseWords.iterator().next();
        } else {
            this.stem = word;
        }

    }

    public Set<PartOfSpeech> getWiktionaryPartOfSpeechSet() {
        return wiktionaryPartOfSpeechSet;
    }

    public String getPartOfSpeechCommaSeparated() {
        String partOfS = "";
        if (wiktionaryPartOfSpeechSet != null && !wiktionaryPartOfSpeechSet.isEmpty()) {
            for (PartOfSpeech pos : wiktionaryPartOfSpeechSet) {
                partOfS = partOfS + "#" + pos.toString() + " ";
            }
        }
        return partOfS;
    }

    public List<WordEntry> getEntryList() {
        return entryList;
    }

    public int getTotalSenses() {
        return totalWordSenses;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public void addPartOfSpeech(String pos) {
        sttsPartOfSpeechSet.add(pos);
    }
}
