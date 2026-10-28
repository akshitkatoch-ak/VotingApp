package com.voting;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/vote")
public class VoteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    static final String VOTED_ATTR = "hasVoted";
    static final String CHOICE_ATTR = "votedFor";
    static final String VOTER_ATTR = "voterName";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // "Vote as another user": end the current session and show a fresh form
        if (request.getParameter("new") != null) {
            HttpSession old = request.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/vote");
            return;
        }

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Page.header(out, "Online Voting");
            out.println("<h1>Vote: Favorite Subject</h1>");

            if (Boolean.TRUE.equals(session.getAttribute(VOTED_ATTR))) {
                out.println("<div class=\"warn\"><b>" + Page.escape((String) session.getAttribute(VOTER_ATTR))
                        + "</b>, you have already voted for <b>"
                        + Page.escape((String) session.getAttribute(CHOICE_ATTR))
                        + "</b>. Multiple votes are not allowed for the same user.</div>");
                out.println("<p><a href=\"vote?new=1\">&#10133; Vote as another user</a></p>");
            } else {
                String error = request.getParameter("error");
                if ("name".equals(error)) {
                    out.println("<div class=\"err\">Please enter your name.</div>");
                } else if ("choice".equals(error)) {
                    out.println("<div class=\"err\">Please select one option before submitting.</div>");
                } else if ("dup".equals(error)) {
                    out.println("<div class=\"err\">This name has already voted. Please use a different name.</div>");
                } else if ("save".equals(error)) {
                    out.println("<div class=\"err\">Could not save your vote. Please try again.</div>");
                }

                out.println("<form action=\"vote\" method=\"post\">");
                out.println("<p><label><b>Your Name</b><br>"
                        + "<input type=\"text\" name=\"voterName\" maxlength=\"50\" required "
                        + "style=\"width:100%;padding:10px;margin-top:6px;border:1px solid #ccc;border-radius:6px;box-sizing:border-box;font-size:1rem;\">"
                        + "</label></p>");
                for (String option : VoteStore.getOptions()) {
                    String safe = Page.escape(option);
                    out.println("<label class=\"opt\"><input type=\"radio\" name=\"choice\" value=\""
                            + safe + "\"> " + safe + "</label>");
                }
                out.println("<button type=\"submit\">Submit Vote</button>");
                out.println("</form>");
            }

            out.println("<p class=\"links\"><a href=\"results\">View Results</a></p>");
            Page.footer(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        synchronized (session) {
            // Session check: this session has already voted
            if (Boolean.TRUE.equals(session.getAttribute(VOTED_ATTR))) {
                response.sendRedirect(request.getContextPath() + "/vote");
                return;
            }

            String voterName = request.getParameter("voterName");
            String choice = request.getParameter("choice");
            String cleanName = voterName == null ? "" : voterName.trim();

            if (cleanName.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/vote?error=name");
                return;
            }
            if (choice == null || choice.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/vote?error=choice");
                return;
            }

            VoteStore.Result result = VoteStore.addVote(cleanName, choice);
            switch (result) {
                case OK:
                    session.setAttribute(VOTED_ATTR, Boolean.TRUE);
                    session.setAttribute(CHOICE_ATTR, choice);
                    session.setAttribute(VOTER_ATTR, cleanName);
                    break;
                case ALREADY_VOTED:
                    response.sendRedirect(request.getContextPath() + "/vote?error=dup");
                    return;
                case SAVE_ERROR:
                    response.sendRedirect(request.getContextPath() + "/vote?error=save");
                    return;
                default:
                    response.sendRedirect(request.getContextPath() + "/vote?error=choice");
                    return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/results?thanks=1");
    }
}