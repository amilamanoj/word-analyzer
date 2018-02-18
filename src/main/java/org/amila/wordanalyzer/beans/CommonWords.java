package org.amila.wordanalyzer.beans;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Amila on 7/27/2014.
 * http://en.wikipedia.org/wiki/Most_common_words_in_English
 */
public class CommonWords {
    private static final List<String> WORDS_EN = Arrays.asList(
            //CONJUNCTIONS
            "and",
            "that",
            "but",
            "or",
            "as",
            "if",
            "when",
            "than",
            "because",
            "while",
            "where",
            "after",
            "so",
            "though",
            "since",
            "until",
            "whether",
            "before",
            "although",
            "nor",
            "like",
            "once",
            "unless",
            "now",
            "except",
            "no",
            //NOUNS
            "time",
            "person",
            "year",
            "way",
            "day",
            "thing",
            "man",
            "world",
            "life",
            "hand",
            "part",
            "child",
            "eye",
            "woman",
            "place",
            "work",
            "week",
            "case",
            "point",
            "government",
            "company",
            "number",
            "group",
            "problem",
            "fact",
            //VERBS
            "be",
            "have",
            "having",
            "had",
            "do",
            "say",
            "said",
            "get",
            "make",
            "go",
            "know",
            "take",
            "see",
            "come",
            "think",
            "look",
            "want",
            "give",
            "use",
            "find",
            "tell",
            "ask",
            "work",
            "seem",
            "feel",
            "try",
            "leave",
            "call",
            //ADJECTIVES
            "good",
            "new",
            "first",
            "last",
            "long",
            "great",
            "little",
            "own",
            "other",
            "old",
            "right",
            "big",
            "high",
            "different",
            "small",
            "large",
            "next",
            "early",
            "young",
            "important",
            "few",
            "public",
            "bad",
            "same",
            "able",
            //PREPOSITIONS
            "to",
            "of",
            "in",
            "for",
            "on",
            "with",
            "at",
            "by",
            "from",
            "up",
            "about",
            "into",
            "over",
            "after",
            "beneath",
            "under",
            "above",
            //OTHER
            "the",
            "is",
            "was",
            "were",
            "and",
            "a",
            "that",
            "i",
            "it",
            "not",
            "he",
            "as",
            "you",
            "this",
            "but",
            "his",
            "they",
            "her",
            "she",
            "or",
            "an",
            "will",
            "my",
            "one",
            "all",
            "would",
            "there",
            "their",
            "this",
            "thi", // incorrect singular form returned for this
            //PRONOUNS
            "it",
            "i",
            "you",
            "he",
            "they",
            "we",
            "she",
            "who",
            "them",
            "me",
            "him",
            "one",
            "her",
            "us",
            "something",
            "nothing",
            "anything",
            "himself",
            "everything",
            "someone",
            "themselves",
            "everyone",
            "itself",
            "anyone",
            "myself",

            //SHORTENED
            "im",
            "doest",
            "dont",
            "didnt",
            "ive",
            "isnt",
            "wasnt",
            "werent",
            "wont",
            "wouldnt",
            "cant",
            "couldnt",
            "hasnt",
            "havent",
            "hadnt",
            "doesnt",
            "youre",
            "youll",
            "mustnt",
            "shant"

    );

    private static final List<String> WORDS_DE = Arrays.asList(
            "sein",
            "haben",
            "werden",

            "sollen",
            "wollen",
            "können",
            "müssen",
            "mögen",
            "dürfen",

            "sagen",
            "sehen",
            "gehen",
            "fragen"
    );

    public static List<String> getCommonWords(String language) {
        switch (language) {
            case "eng":
                return WORDS_EN;
            case "deu":
                return WORDS_DE;
            default:
                return WORDS_EN;
        }
    }


}


