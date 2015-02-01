package org.amila.wordanalyzer.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.amila.wordanalyzer.Analyzer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by amila on 2/13/14.
 */
@Controller
@RequestMapping("/")
public class ConsoleDataController {
    public static final char COMMA = ',';
    public static final char QUOTATION = '\"';
    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy/MM/dd/ HH:mm:ss").create();


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView handleFileUpload(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file, HttpSession session) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String text = new String(bytes);
                Analyzer analyzer = new Analyzer();
                session.setAttribute("analyzer", analyzer);
                analyzer.initialize(file.getOriginalFilename(), text);
                ModelAndView view = new ModelAndView("ready");
                view.addObject("title", file.getOriginalFilename());
//                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                return view;
            } catch (Exception e) {
//                return "You failed to upload " + name + " => " + e.getMessage();
                return null;
            }
        } else {
            return null;
        }

    }

    @RequestMapping(value = "analyze", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String analyze(HttpSession session) throws IOException, URISyntaxException {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        analyzer.analyzeAsync();
        return gson.toJson(analyzer.getJobInfo());
    }

    @RequestMapping(value = "summary", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String summary(HttpSession session) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return gson.toJson(analyzer.getJobInfo());
    }

    @RequestMapping(value = "progress", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String progress(HttpSession session) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return gson.toJson(analyzer.getJobInfo());
    }

    @RequestMapping(value = "newWords", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String newWords(HttpSession session) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return gson.toJson(analyzer.getNewWords());
    }

    @RequestMapping(value = "masteredWords", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String masteredWords(HttpSession session) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return gson.toJson(analyzer.getListWords(Analyzer.ListType.MASTERED));
    }
    @RequestMapping(value = "interestWords", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public
    @ResponseBody
    String interestWords(HttpSession session) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return gson.toJson(analyzer.getListWords(Analyzer.ListType.INTEREST));
    }

    @RequestMapping(value = "word", method = RequestMethod.GET,
            produces = "text/html; charset=utf-8")
    public
    @ResponseBody
    String getWord(HttpSession session, @RequestParam("word") String word) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        return analyzer.getWordDetails(word);
    }

    @RequestMapping(value = "addToMastered", method = RequestMethod.GET,
            produces = "text/html; charset=utf-8")
    public
    @ResponseBody
    void addToMastered(HttpSession session, @RequestParam("word") String word) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        analyzer.addToList(word,Analyzer.ListType.MASTERED);
    }
    @RequestMapping(value = "addToInterest", method = RequestMethod.GET,
            produces = "text/html; charset=utf-8")
    public
    @ResponseBody
    void addToInterest(HttpSession session, @RequestParam("word") String word) {

        Analyzer analyzer = (Analyzer) session.getAttribute("analyzer");
        analyzer.addToList(word, Analyzer.ListType.INTEREST);
    }


}
