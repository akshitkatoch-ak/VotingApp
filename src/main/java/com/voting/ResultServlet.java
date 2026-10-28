package com.voting;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/results")
public class ResultServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        boolean voted = session != null && Boolean.TRUE.equals(session.getAttribute(VoteServlet.VOTED_ATTR));

        Map<String, Integer> results = VoteStore.getResults();
        int total = 0;
        for (int c : results.values()) {
            total += c;
        }

        try (PrintWriter out = response.getWriter()) {
            Page.header(out, "Voting Results");
            out.println("<h1>Voting Results</h1>");

            if (request.getParameter("thanks") != null && voted) {
                out.println("<div class=\"ok\">&#10004; Thank you, "
                        + Page.escape((String) session.getAttribute(VoteServlet.VOTER_ATTR))
                        + "! Your vote has been recorded.</div>");
            }

            for (Map.Entry<String, Integer> e : results.entrySet()) {
                int count = e.getValue();
                int pct = total == 0 ? 0 : Math.round(count * 100f / total);
                out.println("<div class=\"row\"><div class=\"lbl\"><span>" + Page.escape(e.getKey())
                        + "</span><span>" + count + " vote" + (count == 1 ? "" : "s") + " (" + pct + "%)</span></div>");
                out.println("<div class=\"bar\"><div class=\"fill\" style=\"width:" + pct + "%\"></div></div></div>");
            }
            out.println("<p><b>Total votes:</b> " + total + "</p>");

            out.println("<p class=\"links\">"
                    + (voted ? "<a href=\"vote?new=1\">Vote as another user</a>" : "<a href=\"vote\">Cast your vote</a>")
                    + " &nbsp;|&nbsp; <a href=\"results\">Refresh</a></p>");
            Page.footer(out);
        }
    }
}