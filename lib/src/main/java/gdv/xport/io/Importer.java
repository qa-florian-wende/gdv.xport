/*
 * Copyright (c) 2021-2022 by Oli B.
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
 * (c)reated 20.10.2021 by Oli B. (ob@aosd.de)
 */
package gdv.xport.io;

import gdv.xport.feld.Satznummer;
import gdv.xport.satz.Datensatz;
import gdv.xport.satz.feld.common.WagnisartLeben;
import gdv.xport.util.SatzTyp;

import java.io.IOException;

/**
 * In der Klasse Importer sind einige (statische) Methoden zum Lesen von
 * (Teil)Datensaetzen zusammengefasst. Diese waren voher in der Datensatz-
 * oder Satzklasse zu finden.
 *
 * @author oboehm
 * @since 6.1 (20.10.21)
 */
public class Importer {

    private final PushbackLineNumberReader reader;

    private Importer(PushbackLineNumberReader reader) {
        this.reader = reader;
    }

    /**
     * Liefert einen Importer mit dem angegebenen Reader.
     *
     * @param reader zum Lesen
     * @return eine Importer
     */
    public static Importer of(PushbackLineNumberReader reader) {
        return new Importer(reader);
    }

    /**
     * Bestimmt den SatzTyp eines Datensatzes.
     *
     * @param satzart Satzart, z.B. 100
     * @return den ermittelten SatzTyp
     * @throws IOException bei Lesefehlern
     */
    public SatzTyp readSatzTyp(int satzart) throws IOException {
        int sparte = Datensatz.readSparte(reader);
        SatzTyp satzTyp = SatzTyp.of(satzart, sparte);
        if (satzart >= 210 && satzart < 300) {
            if (sparte == 10 && ((satzart == 220) || (satzart == 221))) {
                WagnisartLeben wagnisart = Datensatz.readWagnisart(reader);
                // wagnisart 0 hat immer ein Leerzeichen als teildatenSatzmummer.
                // Nur groesser 0 besitzt per Definition Werte.
                Satznummer satznr = Satznummer.readSatznummer(reader);
                satzTyp = SatzTyp.of(satzart, sparte, wagnisart.getCode(), satznr.toInt());
            } else if (sparte == 20 && satzart == 220) {
                // Fuer 0220.020.x ist die Krankenfolgenummer zur Identifikation der Satzart noetig
                int krankenFolgeNr = Datensatz.readKrankenFolgeNr(reader);
                satzTyp = SatzTyp.of(satzart, sparte, krankenFolgeNr);
            }  else if (sparte == 580 && satzart == 220) {
                // Fuer 0220.580.x ist die BausparArt zur Identifikation der Satzart
                // noetig
                // Fuer 0220.580.x ist die BausparArt zur Identifikation der Satzart noetig
                int bausparArt = Datensatz.readBausparenArt(reader);
                // BausparenArt nicht auslesbar -> Unbekannter Datensatz
                satzTyp = SatzTyp.of(satzart, sparte, bausparArt);
            }
        }
        return satzTyp;
    }

}
