package org.example;
import org.example.stein.Stein;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FeldTest {

    @Test
    void resetTempVars() {
        Feld feld=new Feld(new ArrayList<>());
        feld.setTiefe(3);
        feld.setGefaerbt(true);
        feld.resetTempVars();
        assertEquals(0,feld.getTiefe());// Überprüfen, ob die Tiefe des Feldes auf 0 zurückgesetzt wurde
        assertEquals(false,feld.getGefaerbt());// Überprüfen, ob der Gefaerbt-Status des Feldes auf false zurückgesetzt wurde
    }

    @Test
    void setundgetPosition() {
        Feld feld=new Feld(new ArrayList<>());
        Position position=new Position(1,5);
        feld.setPosition(position);
        assertEquals(position,feld.getPosition());
    }

    @Test
    void setundgetTiefe() {
        Feld feld=new Feld(new ArrayList<>());
        feld.setTiefe(0);
        assertEquals(0,feld.getTiefe());
    }

    @Test
    void setundgetGefaerbt() {
        Feld feld=new Feld(new ArrayList<>());
        feld.setGefaerbt(true);
        assertTrue(feld.getGefaerbt());
    }

    @Test
    void setundistSpielerSpawn() {
        Feld feld=new Feld(new ArrayList<>());
        feld.setSpielerSpawn(true);
        assertTrue(feld.istSpielerSpawn());
    }

    @Test
   void setundgetBesetzung() {
        Feld feld = new Feld(new ArrayList<>());
        Stein stein = mock(Stein.class);
        feld.setBesetzung(stein);
        assertSame(stein, feld.getBesetzung());
    }

    @Test
    void removeBesetzung() {
        Feld feld = new Feld(new ArrayList<>());
        Stein stein = mock(Stein.class);
        feld.setBesetzung(stein);
        feld.removeBesetzung();
        assertNull(feld.getBesetzung());
    }


    @Test
    void addundgetNachbarn() {
        Feld feld=new Feld(new ArrayList<>());
        Feld nachbar = new Feld(new ArrayList<>());
        feld.addNachbarn(nachbar);
        assertEquals(1, feld.getNachbarn().size());
        assertSame(nachbar, feld.getNachbarn().get(0));
    }

    @Test
    void setandgetId() {
        Feld feld=new Feld(new ArrayList<>());
        feld.setId(-1);
        assertEquals(-1,feld.getId());
    }

    @Test
    void kannDrueber() {
        Feld feld=new Feld(new ArrayList<>());
        feld.kannDrueber();
        assertTrue(feld.kannDrueber());
    }

}