package org.example;

import org.example.client.SocketService;
import org.example.stein.Spielstein;

import java.util.ArrayList;
import java.util.Map;

public class GameController {

    private final Runde runde;
    private final SocketService socket;               // Callback-Referenz

    public GameController(int spielerAnzahl, SocketService socket) {
        this.socket = socket;
        Map<Integer,Feld> spawns = SpielfeldHeinz.getSpawnFelder();
        this.runde  = new Runde(spielerAnzahl, spawns);
    }


    public void handleMoveRequest(int clientId,
                                  int figurIndex, int feldId) {

        SpielerObjekt spieler = runde.getSpieler(clientId);
        Spielstein figur = spieler.getFigur(figurIndex);

        int letzterWurf = /* merk dir das pro Spieler */;
        ArrayList<Feld> moegl = runde.findeMoeglicheZiele(figur, letzterWurf);

        Feld ziel = moegl.stream()
                .filter(f -> f.getId() == feldId)
                .findFirst().orElse(null);

        if (ziel == null) {
            socket.sendFail(clientId, "ILLEGAL_MOVE");
            return;
        }

        runde.ziehe(figur, ziel);
        socket.broadcastMove(clientId, figurIndex, feldId);

        if (/* Siegbedingung */) {
            runde.setGewonnen();
            socket.broadcastWin(clientId);
        }
    }
}

