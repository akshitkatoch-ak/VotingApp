package com.voting;

import java.io.PrintWriter;

final class Page {

    private Page() { }

    static void header(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\"><head><meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("<title>" + escape(title) + "</title>");
        out.println("<style>"
                + "body{font-family:Segoe UI,Arial,sans-serif;background:#f0f2f5;margin:0;padding:40px 16px;}"
                + ".card{max-width:540px;margin:auto;background:#fff;padding:28px 32px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,.1);}"
                + "h1{margin-top:0;font-size:1.5rem;}"
                + ".opt{display:block;padding:12px 14px;margin:8px 0;border:1px solid #ddd;border-radius:6px;cursor:pointer;}"
                + ".opt:hover{background:#f5f8ff;border-color:#1a73e8;}"
                + "button{margin-top:16px;padding:10px 24px;background:#1a73e8;color:#fff;border:0;border-radius:6px;font-size:1rem;cursor:pointer;}"
                + "button:hover{background:#1558b0;}"
                + ".ok{background:#e6f4ea;color:#1e7b34;padding:12px 16px;border-radius:6px;margin-bottom:20px;font-weight:600;}"
                + ".warn{background:#fff4e5;color:#8a5300;padding:12px 16px;border-radius:6px;margin-bottom:20px;}"
                + ".err{background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:6px;margin-bottom:20px;font-weight:600;}"
                + ".row{margin:14px 0;}"
                + ".lbl{display:flex;justify-content:space-between;margin-bottom:4px;font-size:.95rem;}"
                + ".bar{background:#e8eaed;border-radius:6px;height:16px;overflow:hidden;}"
                + ".fill{background:#1a73e8;height:100%;}"
                + ".links{margin-top:24px;}"
                + "a{color:#1a73e8;text-decoration:none;}"
                + "</style></head><body><div class=\"card\">");
    }

    static void footer(PrintWriter out) {
        out.println("</div></body></html>");
    }

    static String escape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}