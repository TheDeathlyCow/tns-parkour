package com.github.thedeathlycow.tnsparkour;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ParkourArenaTest {

    @Test
    public void testOrderOfSidebarSmall() {
        int[] scoresArr = {1, 100, 1000, 10000, 15000};
        List<String> sortedScores = sortScores(scoresArr);
        assertProperOrder(scoresArr, sortedScores);
    }

    @Test
    public void testOrderOfSidebarAtUnsigned16BitLimit() {
        int[] scoresArr = {65535, 65536};

        List<String> sortedScores = sortScores(scoresArr);
        assertProperOrder(scoresArr, sortedScores);
    }

    @Test
    public void testOrderOfSidebarBig() {
        int[] scoresArr = {80000, 90000, 100000, 2000000, 300000000};
        List<String> sortedScores = sortScores(scoresArr);
        assertProperOrder(scoresArr, sortedScores);
    }

    @Test
    public void testOrderMix() {
        int[] scoresArr = {1, 100, 1000, 10000, 15000,
                65535, 65536,
                80000, 90000, 100000, 2000000, 300000000
        };
        List<String> sortedScores = sortScores(scoresArr);
        assertProperOrder(scoresArr, sortedScores);
    }

    @Test
    public void testRandomScores() {

        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        addScore(scores, "TheDeathlyCow", 997);
        addScore(scores, "TheDeathlyCow", 1002);
        addScore(scores, "TheDeathlyCow", 1003);
        addScore(scores, "TheDeathlyCow", 1035);
        addScore(scores, "TheDeathlyCow", 1045);
        addScore(scores, "TheDeathlyCow", 1051);
        addScore(scores, "TheDeathlyCow", 1091);
        addScore(scores, "TheDeathlyCow", 1099);
        addScore(scores, "TheDeathlyCow", 1297);

        List<String> sortedScores = scores.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println(sortedScores);
        assertProperOrder(scores, sortedScores);
    }

    @Test
    public void testNamedScores() {

        LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();
        addScore(scores, "Vaipereq", 17735);
        addScore(scores, "Shalamander", 25642);

        List<String> sortedScores = scores.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println(sortedScores);
        assertProperOrder(scores, sortedScores);
    }

    private void assertProperOrder(LinkedHashMap<String, Integer> scores, List<String> sortedScores) {
        List<Map.Entry<String, Integer>> scoresList = new ArrayList<>();
        scoresList.addAll(scores.entrySet());

        for (int i = 0; i < sortedScores.size(); i++) {
            assertEquals(sortedScores.get(i), scoresList.get(i).getKey());
        }
    }

    private void assertProperOrder(int[] scoresArr, List<String> sortedScores) {
        for (int i = 0; i < sortedScores.size(); i++) {
            assertTrue(sortedScores.get(i).endsWith(String.valueOf(scoresArr[i])));
        }
    }

    private void addScore(Map<String, Integer> scores, String name, int score) {
        scores.put(getKey(name, score), score);
    }

    private String getKey(String name, int score) {
        return String.format(ParkourArena.getSidebarOrder(score)
                + "%s - %.3fs", name, score / 1000D);
    }

    public List<String> sortScores(int[] scores) {
        List<String> strings = new ArrayList<>();
        for (int score : scores) {
            strings.add(ParkourArena.getSidebarOrder(score) + score);
        }
        return strings.stream().sorted().collect(Collectors.toList());
    }

}