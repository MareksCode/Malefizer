package org.example.bot;


import org.example.Feld;

import java.util.*;


public class Tiefensuche {


    public static List<Integer> findeKuerzestenPfad(Map<String, Feld> graph, int start, int ziel) {
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


    private static void dfs(Map<String, Feld> graph, int aktueller, int ziel,
                            Set<Integer> besucht, List<Integer> aktuellerPfad, List<List<Integer>> allePfade) {
        besucht.add(aktueller);
        aktuellerPfad.add(aktueller);

        if (aktueller == ziel) {                                    //Ziel kann krone oder Gegner bzw. Sperrstein sein
            allePfade.add(new ArrayList<>(aktuellerPfad));          //wenn Ziel erreicht füge den Pfad zu allen hinzu

        } else {
            List<Feld> nachbarn = graph.get(String.valueOf(aktueller)) != null ?
                    graph.get(String.valueOf(aktueller)).getNachbarn() :
                    new ArrayList<>();
            for (Feld nachbar : nachbarn) {
                if (!besucht.contains(nachbar.getId())) {
                    dfs(graph, nachbar.getId(), ziel, besucht, aktuellerPfad, allePfade);
                }
            }
        }

        besucht.remove(aktueller);
        aktuellerPfad.remove(aktuellerPfad.size() - 1);
    }
}