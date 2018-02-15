package org.amila.wordanalyzer.beans;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionarySense;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage;
import de.tudarmstadt.ukp.jwktl.api.util.Language;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amila on 7/27/2014.
 */
public class WordInfo {
    private String word;
    private int frequency;
    private Set<String> variations = new HashSet<String>();
    private List<WordEntry> entryList = new ArrayList<>();
    private Set<PartOfSpeech> partOfSpeechSet = new HashSet<>();
    //    protected static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{.+?\\}\\}");
    public static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{.*?\\}\\}");
    private int totalSenses = 0;
    private String stem;
    private ILanguage language;

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

    public Set<String> getVariations() {
        return variations;
    }

    public void setVariations(Set<String> variations) {
        this.variations = variations;
    }

    public void processEntries(List<IWiktionaryEntry> entries) {
        for (IWiktionaryEntry entry : entries) {
            if (entry.getWordLanguage() != language) {
                continue;
            }
            if (entry.getPartOfSpeech() != null) {
                partOfSpeechSet.add(entry.getPartOfSpeech());
            }
            WordEntry wordEntry = new WordEntry();
            wordEntry.setEntry(entry);
            boolean allDated = true;
            boolean allArchaic = true;
            boolean allObsolete = true;

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
                totalSenses++;
            }
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
    }

    public Set<PartOfSpeech> getPartOfSpeechSet() {
        return partOfSpeechSet;
    }

    public String getPartOfSpeechCommaSeparated() {
        String partOfS = "";
        if (partOfSpeechSet != null && !partOfSpeechSet.isEmpty()) {
            for (PartOfSpeech pos : partOfSpeechSet) {
                partOfS = partOfS + "#" + pos.toString() + " ";
            }
        }
        return partOfS;
    }

    public List<WordEntry> getEntryList() {
        return entryList;
    }

    public int getTotalSenses() {
        return totalSenses;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }
}
