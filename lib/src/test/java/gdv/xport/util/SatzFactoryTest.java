/*
 * Copyright (c) 2009 - 2012 by Oli B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 30.10.2009 by Oli B. (ob@aosd.de)
 */

package gdv.xport.util;

import gdv.xport.Datenpaket;
import gdv.xport.annotation.FeldInfo;
import gdv.xport.feld.Betrag;
import gdv.xport.feld.Bezeichner;
import gdv.xport.feld.Feld;
import gdv.xport.feld.NumFeld;
import gdv.xport.satz.Datensatz;
import gdv.xport.satz.Satz;
import gdv.xport.satz.Teildatensatz;
import gdv.xport.satz.Vorsatz;
import gdv.xport.satz.model.SatzX;
import gdv.xport.satz.xml.SatzXml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import patterntesting.runtime.junit.SmokeRunner;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * JUnit-Test fuer SatzFactory.
 *
 * @author oliver (ob@aosd.de)
 * @since 0.1.0 (30.10.2009)
 */
@RunWith(SmokeRunner.class)
public final class SatzFactoryTest extends AbstractTest {

    private static final Logger LOG = LogManager.getLogger(SatzFactoryTest.class);

    @Before
    public void resetSatzFactory() {
        SatzFactory.reset();
    }

    /**
     * Testet getSatz().
     */
    @Test
    public void testGetSatz() {
        Satz vorsatz = new Vorsatz();
        String content = vorsatz.toLongString();
        Satz satz = SatzFactory.getSatz(content);
        assertEquals(content, satz.toLongString());
        assertEquals(SatzTyp.of("0001"), satz.getSatzTyp());
    }

    /**
     * Testet getSatz().
     */
    @Test
    public void testGetSatzInt() {
        Satz satz = getSatz(1);
        assertNotNull(satz.getFeld(Bezeichner.VERSION_SATZART_0100));
    }

    /**
     * Satz 0342 ("Begleitdokumente und Signaturen") ist nur als
     * XML-Beschreibung vorhanden. Daher wird dieser Satz zum Testen verwendet.
     */
    @Test
    public void testGetSatz342() {
        Satz satz342 = getSatz(342);
        Feld dokumenttyp = satz342.getFeld("Dokumenttyp");
        assertEquals(2, dokumenttyp.getAnzahlBytes());
        assertEquals(46, dokumenttyp.getByteAdresse());
    }

    private static Satz getSatz(final int satzart) {
        Satz satz = SatzFactory.getSatz(new SatzTyp(satzart));
        assertEquals(satzart, satz.getSatzart());
        return satz;
    }

    /**
     * Testet getSatz() fuer eine Satzart, die (noch) nicht unterstuetzt wird.
     */
    @Test
    public void testGetUnsupportedSatz() {
        Datensatz unsupported = new Datensatz("0123");
        unsupported.setVuNummer("56789");
        unsupported.setSparte(88);
        unsupported.add(new NumFeld(new Bezeichner("zweiundvierzig"), 4, 200, 42));
        String content = unsupported.toLongString();
        Satz imported = SatzFactory.getSatz(content);
        assertEquals(content, imported.toLongString());
        assertTrue(imported + " should be valid", imported.isValid());
    }

    /**
     * Testet {@link SatzFactory#register(Class, int)}.
     */
    @Test
    public void testRegisterSatz() {
        int satzart = 47;
        SatzFactory.register(Datensatz.class, satzart);
        Satz satz = SatzFactory.getSatz(new SatzTyp(satzart));
        assertEquals(Datensatz.class, satz.getClass());
        assertEquals(satzart, satz.getSatzart());
        SatzFactory.unregister(new SatzTyp(satzart));
        try {
            satz = SatzFactory.getSatz(new SatzTyp(satzart));
            fail("unregister failed for " + satz);
        } catch (NotRegisteredException expected) {
            LOG.info(satz + " successful unregistered (" + expected + ")");
        }
    }

    /**
     * Testet {@link SatzFactory#register(Class, int, int)}.
     */
    @Test
    public void testRegisterDatensatz() {
        SatzTyp typ = SatzTyp.of("0047.11");
        SatzFactory.register(SatzX.class, typ);
        Satz satz = SatzFactory.getDatensatz(typ);
        assertEquals(SatzX.class, satz.getClass());
        SatzFactory.unregister(typ);
    }

    /**
     * Damit wird ueberprueft, ob Satzart 100 (Adressteil) bei der SatzFactory richtig registriert ist.
     */
    @Test
    public void testGetAdressteil() {
        checkGetDatensatz(100);
    }

    /**
     * Damit wird ueberprueft, ob die Satzart 200 (AllgemeinerVertragsteil oder Satz200) bei der SatzFactory registriert
     * ist.
     */
    @Test
    public void testAllgemeinerVertragsteil() {
        checkGetDatensatz(200);
    }

    private static void checkGetDatensatz(final int satzart) {
        Satz datensatz = SatzFactory.getDatensatz(new SatzTyp(satzart));
        assertEquals(satzart, datensatz.getSatzart());
    }

    /**
     * Damit wird ueberprueft, ob Satzart 220 mit Sparte 70 registriert ist.
     */
    @Test
    public void testGetSpartenspezifischerTeil70() {
        checkGetDatensatz(220, 70, gdv.xport.satz.feld.sparte70.Feld220.values());
    }

    /**
     * Damit wird ueberprueft, ob Satzart 210 mit Sparte 70 registriert ist.
     */
    @Test
    public void testGetVertragsspezifischerTeil70() {
        assertNotNull(getDatensatz(210, 70));
    }

    /**
     * Damit wird ueberprueft, ob Satzart 221 mit Sparte 30 registriert ist.
     */
    @Test
    public void testGetErweiterungssatz30() {
        assertNotNull(getDatensatz(221, 30));
    }

    /**
     * Test von Satzart 210.
     * <p>
     * Nach eine Hinweis von Frank Berger wird jetzt auch die Sparte 130
     * ueberprueft, da sie vorher gefehlt hatte.
     * </p>
     */
    @Test
    public void testGetSatzart210() {
        checkGetDatensatz(210, 50, gdv.xport.satz.feld.sparte50.Feld210.values(), "1");
    }

    /**
     * Falls der Satz vom XmlService kommt, gab es Probleme, dass die
     * allgemeine Satz fuer z.B. Satzart 210 zurueckkam, und nicht der
     * spezielle Satz fuer die entsprechende Sparte.
     */
    @Test
    public void testGetSatzart210Sparte30() {
        Datensatz satz210 = getDatensatz(210, 30);
        Feld vertragsstatus = satz210.getFeld(Bezeichner.VERTRAGSSTATUS);
        assertEquals(43, vertragsstatus.getByteAdresse());
    }

    /**
     * Test von Satzart 220.
     */
    @Test
    public void testGetSatzart220() {
        checkGetDatensatz(220, 10, gdv.xport.satz.feld.sparte10.Feld220Wagnis0.values(), "1");
        checkGetDatensatz(220, 140, gdv.xport.satz.feld.sparte140.Feld220.values(), "1");
    }

    private void checkGetDatensatz(final int satzart, final int sparte, final Enum[] felder, final String satzNr) {
        checkGetDatensatz(satzart, sparte, felder);
        Satz datensatz = getDatensatz(satzart, sparte);
        Feld satznummer = datensatz.getFeld(Bezeichner.SATZNUMMER, 1);
        assertEquals("falsche Satznummer", satzNr, satznummer.getInhalt());
    }

    private void checkGetDatensatz(final int satzart, final int sparte, final Enum[] felder) {
        Satz datensatz = getDatensatz(satzart, sparte);
        for (Enum feldInfo : felder) {
            if (feldInfo instanceof FeldInfo) {
                String inhalt = datensatz.get(feldInfo);
                assertNotNull("not found: " + feldInfo, inhalt);
            }
        }
    }

    private static Datensatz getDatensatz(final int satzart, final int sparte) {
        Datensatz datensatz = SatzFactory.getDatensatz(new SatzTyp(satzart, sparte));
        assertEquals(satzart, datensatz.getSatzart());
        assertEquals(sparte, datensatz.getSparte());
        return datensatz;
    }

    /**
     * Die Daten zu diesem Test stammen aus der Musterdatei.
     *
     * @throws IOException
     *             sollte eigentlich nicht vorkommen
     */
    @Test
    public void testImport() throws IOException {
        Datensatz datensatz = SatzFactory.getDatensatz(SatzTyp.of("210.30"));
        String s = "02109999  030      599999999990199990099991010520040105200901052"
                + "0040901 0000000000000000000 EUR000000000000000000000000000000041"
                + "1410000000000 0001000                                           "
                + "           000000                                               ";
        datensatz.importFrom(s);
        assertEquals(1, datensatz.getFolgenummer());
    }

    /**
     * Test-Methode fuer {@link SatzFactory#getAllSupportedSaetze()}.
     */
    @Test
    public void testGetAllSupportedSaetze() {
        Datenpaket all = SatzFactory.getAllSupportedSaetze();
        List<Datensatz> datensaetze = all.getDatensaetze();
        Set<Integer> supportedSatzarten = new TreeSet<>();
        for (Datensatz datensatz : datensaetze) {
            supportedSatzarten.add(datensatz.getSatzart());
        }
        int n = datensaetze.size();
        LOG.info(n + " Satzarten supported: " + supportedSatzarten);
        assertTrue("only " + n + " Datensaetze supported", n > 5);
        assertTrue("Satzart 342 expected to be supported", supportedSatzarten.contains(342));
    }

    /**
     * Hier testen wir mit Satz fuer die Kfz-Haftpflicht (0221.051), ob keine
     * Loecher im Datensatz sind. Problem bereiteten hier urspruenglich die
     * KH_DECKUNGSSUMMEN_IN_WAEHRUNGSEINHEITEN_TEIL#-Bezeichner.
     *
     * @throws XMLStreamException the xml stream exception
     * @throws IOException        the io exception
     */
    @Test
    public void testSatzart0221051() throws XMLStreamException, IOException {
        SatzTyp kfz = SatzTyp.of(221, 51);
        try {
            SatzRegistry.getInstance().register(SatzXml.of("Satz0221.051.xml"), kfz, SatzRegistry.NO_VALIDATOR);
            Datensatz satz = SatzFactory.getDatensatz(kfz);
            checkDatensatz(satz);
            checkDeckungssumme(satz, Bezeichner.KH_DECKUNGSSUMMEN_IN_WAEHRUNGSEINHEITEN_TEIL1);
            checkDeckungssumme(satz, Bezeichner.KH_DECKUNGSSUMMEN_IN_WAEHRUNGSEINHEITEN_TEIL2);
            checkDeckungssumme(satz, Bezeichner.KH_DECKUNGSSUMMEN_IN_WAEHRUNGSEINHEITEN_TEIL3);
        } finally {
            SatzRegistry.getInstance().unregister(kfz);
        }
    }

    private static void checkDeckungssumme(Datensatz satz, Bezeichner name) {
        Betrag betrag = (Betrag) satz.getFeld(name);
        assertEquals(14, betrag.getAnzahlBytes());
        assertEquals(2, betrag.getNachkommastellen());
    }

    /**
     * Hier testen wir, ob brav alle Felder ausgefuellt und keine Luecken
     * vorhanden sind. Bei Satzart 250 fehlt noch die Satznummer auf
     * Adresse 51, weswegen der Test diese Satzart (noch) ausblendet.
     */
    @Test
    public void testSatzarten() {
        List<SatzTyp> ignoredForTests = Arrays.asList(SatzTyp.of("0210.580"), SatzTyp.of("0220.570"), SatzTyp.of("0230.050"));
        Datenpaket datenpaket = SatzFactory.getAllSupportedSaetze();
        for (Datensatz datensatz : datenpaket.getDatensaetze()) {
            SatzTyp satzTyp = datensatz.getSatzTyp();
            if ((datensatz.getSatzart() <= 250) && !ignoredForTests.contains(satzTyp)) {
                checkDatensatz(datensatz);
            }
        }
    }

    private static void checkDatensatz(Datensatz satz) {
        for (Teildatensatz tds : satz.getTeildatensaetze()) {
            checkTeildatensatz(tds);
        }
    }

    private static void checkTeildatensatz(Teildatensatz tds) {
        int startByte = 1;
        for (Feld feld : tds.getFelder()) {
            assertEquals(tds + ": Feld missing before " + feld.getBezeichner(), startByte, feld.getByteAdresse());
            startByte = feld.getEndAdresse() + 1;
        }
    }

    /**
     * Wenn zweimal der gleiche Satz geholt wird, sollte nicht derselbe
     * zurueckgeliefert werden. Sonst kann es beim Auffuellen des Satzes
     * zu unerwuenschten Wechselwirkungen mit dem ersten Satz kommen.
     */
    @Test
    public void testGetSatzDifferent() {
        Satz one = SatzFactory.getSatz(new SatzTyp(100));
        Satz two = SatzFactory.getSatz(new SatzTyp(100));
        assertEquals(one, two);
        assertNotSame(one, two);
        for (int i = 1; i <= one.getNumberOfTeildatensaetze(); i++) {
            assertEquals(one.getTeildatensatz(i), two.getTeildatensatz(i));
            assertNotSame(one.getTeildatensatz(i), two.getTeildatensatz(i));
        }
    }
    
    /**
     * Der gleiche Test wie vorher, nur dass wir hier ncoh einen Schritte
     * tiefer gehen und zwei Felder vergleichen, ob sie nicht dieselben sind.
     */
    @Test
    public void testGetSatzManipulated() {
        Teildatensatz one = SatzFactory.getSatz(new SatzTyp(100)).getTeildatensatz(4);
        Teildatensatz two = SatzFactory.getSatz(new SatzTyp(100)).getTeildatensatz(4);
        assertNotSame(one, two);
        Feld oneIban = one.getFeld(Bezeichner.IBAN1);
        Feld twoIban = two.getFeld(Bezeichner.IBAN1);
        assertNotSame(oneIban, twoIban);
    }

    /**
     * Als ersten Test fuer Issue #33 probieren wir, ob wir Satz 350 erzeugen
     * koennen.
     */
    @Test
    public void testIssue33() {
        Datensatz satz350 = SatzFactory.getDatensatz(SatzTyp.of("350.30"));
        checkDatensatz(satz350);
        assertEquals(30, satz350.getSparte());
    }

    @Test
    public void testDatensatz200() {
        Datensatz satz200 = SatzFactory.getDatensatz(new SatzTyp(200));
        assertEquals(200, satz200.getSatzart());
    }

    /**
     * SatzTyp "0220.010.13" gibt es eigentlich nicht. Oft ist damit aber
     * eigentlich SatzTyp "0220.010.13.1" gemeint. Daher sollte auch dieser
     * Satz zurueckkommen.
     */
    @Test
    public void testGetWagnisart1u3() {
        Datensatz a = SatzFactory.getDatensatz(SatzTyp.of("0220.010.13.1"));
        Datensatz b = SatzFactory.getDatensatz(SatzTyp.of("0220.010.13"));
        assertEquals(a, b);
    }

    /**
     * Fuer den Import muss es moeglich sein, den Satz zu bekommen, der
     * am besten passt.
     */
    @Test
    public void testGetSatz100Sparte30() {
        Satz satz = SatzFactory.getSatz(SatzTyp.of("0100.030"));
        assertEquals(100, satz.getSatzart());
        assertEquals(30, satz.getSparte());
    }

    @Test
    public void testGetDatensatz100() {
        SatzTyp satzart100 = SatzTyp.of("0100");
        Satz a = SatzFactory.getDatensatz(satzart100);
        Satz b = SatzFactory.getSatz(satzart100);
        assertEquals(a, b);
    }

}
