package org.amila.wordanalyzer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by amila on 2/13/14.
 */
@Controller
@RequestMapping("/")
public class ConsolePageController {

    @RequestMapping({"/", "home"})
    protected ModelAndView home(HttpServletRequest request,
                                HttpServletResponse response, HttpSession session) {

        return new ModelAndView("index");
    }

    @RequestMapping("session-graph")
    protected ModelAndView sessionGraph(HttpServletRequest request,
                                        HttpServletResponse response) {
        return new ModelAndView("sessionCostGraph");
    }

    @RequestMapping("session-messages")
    protected ModelAndView messagesBySession(HttpServletRequest request,
                                             HttpServletResponse response, @RequestParam(required = true) String sessionId) {
        ModelAndView messages = new ModelAndView("messages");
        messages.addObject("msgUrl", "data/messages/" + sessionId);
        return messages;
    }

    @RequestMapping("mastered-words")
    protected ModelAndView masteredWords(HttpServletRequest request,
                                         HttpServletResponse response) {
        return new ModelAndView("masteredWords");
    }

    @RequestMapping("new-words")
    protected ModelAndView allSessions(HttpServletRequest request,
                                       HttpServletResponse response) {
        return new ModelAndView("newWords");
    }

    @RequestMapping("environment")
    protected ModelAndView environment(HttpServletRequest request,
                                       HttpServletResponse response) {
        return new ModelAndView("environment");
    }

    @RequestMapping("logout")
    protected void logout(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/");

    }


}
