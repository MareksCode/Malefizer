package org.example.bot;

import java.util.*;


public class Tiefensuche {


    public static List<Integer> findeKuerzestenPfad(Map<Integer, List<Integer>> graph, int start, int ziel) {
        List<List<Integer>> allePfade = new ArrayList<>();
        Set<Integer> besucht = new HashSet<>();
        List<Integer> aktuellerPfad = new ArrayList<>();

        dfs(graph, start, ziel, besucht, aktuellerPfad, allePfade);

        List<Integer> kuerzesterPfad = null;
        for (List<Integer> pfad : allePfade) {
            if (kuerzesterPfad == null || pfad.size() < kuerzesterPfad.size()) {    //wenn Pfad kürzer als kuerzesterPfad dann kuerzesterPfad=pfad
                kuerzesterPfad = pfad;
            }
        }
        return kuerzesterPfad;
    }


    private static void dfs(Map<Integer, List<Integer>> graph, int aktueller, int ziel,
                            Set<Integer> besucht, List<Integer> aktuellerPfad, List<List<Integer>> allePfade) {
        besucht.add(aktueller);
        aktuellerPfad.add(aktueller);

        if (aktueller == ziel) {                                    //Ziel kann krone oder Gegner bzw. Sperrstein sein
            allePfade.add(new ArrayList<>(aktuellerPfad));          //wenn Ziel erreicht füge den Pfad zu allen hinzu

        } else {
            for (int nachbar : graph.getOrDefault(aktueller, Collections.emptyList())) {
                if (!besucht.contains(nachbar)) {
                    dfs(graph, nachbar, ziel, besucht, aktuellerPfad, allePfade);
                }
            }
        }

        besucht.remove(aktueller);
        aktuellerPfad.remove(aktuellerPfad.size() - 1);
    }
}