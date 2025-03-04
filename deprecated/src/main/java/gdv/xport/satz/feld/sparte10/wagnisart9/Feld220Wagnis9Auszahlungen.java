/*
 * Copyright (c) 2011, 2012 by Oli B.
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
 * (c)reated 23.03.2011 by Oli B. (ob@aosd.de)
 */

package gdv.xport.satz.feld.sparte10.wagnisart9;

import gdv.xport.annotation.*;
import gdv.xport.feld.*;
import gdv.xport.satz.feld.common.Feld1bis7;

/**
 * Diese Enum-Klasse repraesentiert die Felder fuer Satzart 220, Sparte 10.
 * "Leben - Fondsgebundene Rentenversicherung = Wagnisart 9 - Auszahlungen" (Satzart 0220)
 *
 * @author ralfklemmer
 * @since 19.01.2013
 * @deprecated Enums mit Annotationen werden ab v6 nicht mehr unterstuetzt
 */
@Deprecated
public enum Feld220Wagnis9Auszahlungen {

    // /// Teildatensatz 1 /////////////////////////////////////////////////

    /** Feld 1 - 7 sind fuer jeden (Teil-)Datensatz identisch. */
    @FelderInfo(sparte = 10, teildatensatz = 1, type = Feld1bis7.class)
    INTRO1,

    /**
     * Lfd. Nummer der versicherten Person (VP).
     * lfd. Nr., die im VU geführt wird
     */
    @FeldInfo(teildatensatz = 1, nr = 8, type = AlphaNumFeld.class, anzahlBytes = 17, byteAdresse = 43)
    LFD_NUMMER_VP_PERSONENGRUPPE,

  /**
   * Wagnisart. 9 = Fondsgebundene Rentenversicherung
   */
  @FeldInfo(teildatensatz = 1, nr = 9, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 60, value = "9")
  WAGNISART,

    /**
     * Lfd Nummer zur Wagnisart.
     */
    @FeldInfo(teildatensatz = 1, nr = 10, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 61)
    LFD_NUMMER_ZUR_WAGNISART,

    /**
     * Lfd. Nummer der Satzart
     * Lfd. Nummer der Satzart 0220.010.2/6 innerhalb der gleichen Folgenummer
     * (z. B. n-fache hintereinanderfolgende Lieferung der Satzart 0220.010.2/6, wenn mehrere Bezugsrechte vorhanden)
     */
    @FeldInfo(teildatensatz = 1, nr = 11, type = AlphaNumFeld.class, anzahlBytes = 2, byteAdresse = 62)
    LFD_NUMMER_SATZART,

    /**
     * Nächste Auszahlungssumme in Währungseinheiten.
     * Vereinbarte Auszahlungssumme
     * (9,0 Stellen)
     * <p>
     * ACHTUNG: Dieser Name wurde umbenannt, da der alte Wert falsch geschrieben
     * wurde (NAECHSTE_AUSZAHLUNGSSUMMER_IN_WAEHRUNGSEINHEITEN).
     * </p>
     */
    @FeldInfo(teildatensatz = 1, nr = 12, type = NumFeld.class, anzahlBytes = 9, byteAdresse = 64)
    NAECHSTE_AUSZAHLUNGSSUMME_IN_WAEHRUNGSEINHEITEN,

    /**
     * Nächster Auszahlungstermin.
     * Sollten Tag und/oder Monat nicht vorhanden sein, muss "00" geschlüsselt werden
     * Tag/Monat/Jahr (TTMMJJJJ)
     */
    @FeldInfo(teildatensatz = 1, nr = 13, type = Datum.class, anzahlBytes = 8, byteAdresse = 73)
    NAECHSTER_AUSZAHLUNGSTERMIN,

    /**
     * Auszahlungsweise.
     * in Monaten bei periodischen Auszahlungen
     * in Monaten bei periodischen Auszahlungen
     * 000 = keine Änderungen/Auszahlungen
     * 999 = unregelmäßige Änderungen/Auszahlungen
     */
    @FeldInfo(teildatensatz = 1, nr = 14, type = NumFeld.class, anzahlBytes = 3, byteAdresse = 81)
    AUSZAHLUNGSWEISE,

    /**
     * Anzahl der Auszahlungen.
     * Anzahl der insgesamt noch möglichen Auszahlungen (99 = unbestimmt)
     */
    @FeldInfo(teildatensatz = 1, nr = 15, type = NumFeld.class, anzahlBytes = 2, byteAdresse = 84)
    ANZAHL_DER_AUSZAHLUNGEN,

    /**
     * Leerstellen.
     */
    @FeldInfo(teildatensatz = 1, nr = 16, type = AlphaNumFeld.class, anzahlBytes = 170, byteAdresse = 86)
    LEERSTELLEN

}
