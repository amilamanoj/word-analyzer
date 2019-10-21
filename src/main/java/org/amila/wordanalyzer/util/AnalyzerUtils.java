package org.amila.wordanalyzer.util;

import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;

// https://www.sketchengine.eu/german-stts-part-of-speech-tagset/
public class AnalyzerUtils {
    public static PartOfSpeech toWktlPos(String openNlpPos) {
        switch (openNlpPos) {
            case "NE":
                return PartOfSpeech.PROPER_NOUN;
            case "NN":
            case "NA":
                return PartOfSpeech.NOUN;
            case "ADJA":
            case "ADJD":
                return PartOfSpeech.ADJECTIVE;
            case "ADV":
            case "PAV":
            case "PAVREL":
            case "PROAV":
                return PartOfSpeech.ADVERB;
            case "APPR":
            case "APZR":
            case "APPRART":
            case "KOUI":
                return PartOfSpeech.PREPOSITION;
            case "APPO":
                return PartOfSpeech.POSTPOSITION;
            case "VAFIN":
            case "VAIMP":
            case "VAPP":
            case "VAINF":
            case "VMPP":
                return PartOfSpeech.AUXILIARY_VERB;
            case "VMFIN":
            case "VMINF":
            case "VVIMP":
            case "VVFIN":
            case "VVINF":
            case "VVIZU":
            case "VVPP":
                return PartOfSpeech.VERB;

            case "ART":
                return PartOfSpeech.ARTICLE;
            case "CARD":
                return PartOfSpeech.NUMBER;
            case "KON":
            case "KOKOM":
            case "KOUS":
                return PartOfSpeech.CONJUNCTION;
            case "ITJ":
                return PartOfSpeech.INTERJECTION;
            case "PDAT":
            case "PPOSAT":
            case "PWAT":
                return PartOfSpeech.DETERMINER;
            case "PDS":
                return PartOfSpeech.DEMONSTRATIVE_PRONOUN;
            case "PIAT":
            case "PIS":
                return PartOfSpeech.INDEFINITE_PRONOUN;
            case "PPER":
                return PartOfSpeech.PERSONAL_PRONOUN;
            case "PRF":
                return PartOfSpeech.REFLEXIVE_PRONOUN;
            case "PPOSS":
                return PartOfSpeech.POSSESSIVE_PRONOUN;
            case "PRELAT":
            case "PRELS":
                return PartOfSpeech.RELATIVE_PRONOUN;
            case "PTKA":
            case "PTKANT":
                return PartOfSpeech.ANSWERING_PARTICLE;
            case "PTKNEG":
                return PartOfSpeech.NEGATIVE_PARTICLE;
            case "PTKREL":
            case "PTKZU":
            case "TRUNC":
                return PartOfSpeech.PARTICLE;
            case "PWS":
            case "PWREL":
                return PartOfSpeech.INTERROGATIVE_PRONOUN;
            case "PWAV":
            case "PWAVREL":
                return PartOfSpeech.INTERROGATIVE_ADVERB;
         case "PTKVZ":
                return PartOfSpeech.PREFIX;

            default:
                throw new RuntimeException("POS not found: " + openNlpPos);

        }
    }
}
