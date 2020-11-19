package org.amila.wordanalyzer.nlp;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PosTagger {
    public static void main(String[] args) throws IOException {

        String input = "Ein hässlicher gewöhnlicher Name, wenn du mich fragst.";
                        final String POS_MODEL = "de-pos-maxent.bin";

        try (InputStream posModelIn = new FileInputStream(POS_MODEL)) {
            // tokenize the content
            Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(input);

            // loading the parts-of-speech model from stream
            POSModel posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            POSTaggerME posTagger = new POSTaggerME(posModel);
            // Tagger tagging the tokens
            String[] tags = posTagger.tag(tokens);
            // Getting the probabilities of the tags given to the tokens
            double[] probs = posTagger.probs();

            System.out.println("Token\t:\tTag\n---------------------------------------------");
            for (int i = 0; i < tokens.length; i++) {
//                if (tags[i].startsWith("V"))
                    System.out.println(tokens[i] + "\t:\t" + tags[i]);
            }
        }
    }
}
