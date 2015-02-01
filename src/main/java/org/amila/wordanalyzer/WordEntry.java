package org.amila.wordanalyzer;

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Amila on 8/17/2014.
 */
public class WordEntry {
    public enum Period {
        DATED, ARCHAIC, OBSOLETE
    }

    private IWiktionaryEntry entry;
    private Set<Period> periodAtLeastOneSenseSet = new HashSet<>();
    private Set<Period> periodAllSensesSet = new HashSet<>();

    public IWiktionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(IWiktionaryEntry entry) {
        this.entry = entry;
    }

    public void addPeriod(Period period) {
        periodAtLeastOneSenseSet.add(period);
    }

    public void addAllSensesPeriod(Period period) {
        periodAllSensesSet.add(period);
    }

    @Override
    public String toString() {
        return entry.getPartOfSpeech() +
                ", " + periodAllSensesSet;
    }
}
