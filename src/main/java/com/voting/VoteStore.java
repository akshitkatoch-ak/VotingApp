package com.voting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores votes in memory and saves each vote to a text file,
 * so the data is kept even after Tomcat restarts.
 * File format: one line per vote ->  voterName|option
 */
public final class VoteStore {

    public enum Result { OK, INVALID_OPTION, ALREADY_VOTED, SAVE_ERROR }

    private static final String[] OPTIONS = {
            "Advance Internet Programming",
            "Advance Database Management System",
            "Python Programming",
            "Design and Analysis of Algorithm",
            "Mathematics"
        };

    private static final Map<String, Integer> VOTES = new LinkedHashMap<>();
    private static final Set<String> VOTERS = new HashSet<>();
    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), "votingapp-data", "votes.txt");

    static {
        for (String o : OPTIONS) {
            VOTES.put(o, 0);
        }
        load();
        System.out.println("[VotingApp] Votes file: " + FILE);
    }

    private VoteStore() { }

    private static void load() {
        try {
            if (Files.exists(FILE)) {
                List<String> lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String[] p = line.split("\\|", 2);
                    if (p.length == 2 && VOTES.containsKey(p[1])) {
                        VOTES.put(p[1], VOTES.get(p[1]) + 1);
                        VOTERS.add(p[0]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Makes a name safe and comparable: trims, removes | and line breaks, lower-case. */
    static String normalize(String name) {
        if (name == null) {
            return "";
        }
        return name.replace("|", " ").replaceAll("[\\r\\n]+", " ").trim().replaceAll("\\s+", " ").toLowerCase();
    }

    public static synchronized List<String> getOptions() {
        return new ArrayList<>(VOTES.keySet());
    }

    public static synchronized Result addVote(String voterName, String option) {
        if (option == null || !VOTES.containsKey(option)) {
            return Result.INVALID_OPTION;
        }
        String voter = normalize(voterName);
        if (VOTERS.contains(voter)) {
            return Result.ALREADY_VOTED;
        }
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, (voter + "|" + option + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.SAVE_ERROR;
        }
        VOTERS.add(voter);
        VOTES.put(option, VOTES.get(option) + 1);
        return Result.OK;
    }

    public static synchronized Map<String, Integer> getResults() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(VOTES));
    }
}