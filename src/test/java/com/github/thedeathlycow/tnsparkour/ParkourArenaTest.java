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

    private void assertProperOrder(int[] scoresArr, List<String> sortedScores) {
        for (int i = 0; i < sortedScores.size(); i++) {
            assertTrue(sortedScores.get(i).endsWith(String.valueOf(scoresArr[i])));
        }
    }

    public List<String> sortScores(int[] scores) {
        List<String> strings = new ArrayList<>();
        for (int score : scores) {
            strings.add(ParkourArena.getSidebarOrder(score) + score);
        }
        return strings.stream().sorted().collect(Collectors.toList());
    }

}