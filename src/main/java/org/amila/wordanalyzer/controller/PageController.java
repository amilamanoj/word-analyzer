package org.amila.wordanalyzer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amila on 2/13/14.
 */
@Controller
@RequestMapping("/")
public class PageController {

    @RequestMapping("/")
    protected String home() {
        return "index";
    }

    @RequestMapping("mastered-words")
    protected String masteredWords() {
        return "masteredWords";
    }

    @RequestMapping("interest-words")
    protected String interestWords() {
        return "interestWords";
    }

    @RequestMapping("frequency-list")
    protected String frequencyList() {
        return "frequencyList";
    }

    @RequestMapping("new-words")
    protected String allSessions() {
        return "newWords";
    }


    @RequestMapping("logout")
    protected void logout(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/");

    }


}
