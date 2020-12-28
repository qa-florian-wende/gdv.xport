/*
 * Copyright (c) 2009 - 2019 by Oli B.
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
 * (c)reated 05.10.2009 by Oli B. (ob@aosd.de)
 */

package gdv.xport.satz;

import gdv.xport.config.Config;
import gdv.xport.feld.Bezeichner;
import gdv.xport.feld.Datum;
import gdv.xport.feld.Feld;
import gdv.xport.util.SatzFactory;
import gdv.xport.util.SatzTyp;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import static gdv.xport.feld.Bezeichner.ERSTELLUNGSDAT_ZEITRAUM_BIS;
import static gdv.xport.feld.Bezeichner.ERSTELLUNGSDAT_ZEITRAUM_VOM;

/**
 * Dies ist der erste Satz, der Vorsatz eben.
 * <p>
 * Da Vorsatz und Nachsatz von der Datenpaket-Klasse benoetigt werden, habe
 * ich das "deprecated" wieder entfernt (24-Nov-2012, oboehm).
 * </p>
 *
 * @author oliver
 * @since 0.0.1 (09-Okt-2009)
 */
public final class Vorsatz extends Satz {

    private static final Logger LOG = LogManager.getLogger(Vorsatz.class);
    private static Datensatz satz0001 = SatzFactory.getDatensatz(SatzTyp.of(
            "0001"));

    /**
     * Hiermit wird ein Vorsatz mit 3 Teildatensaetzen erstellt.
     */
    public Vorsatz() {
        super(1, satz0001.getTeildatensaetze(), satz0001.getSatzversion());
        setVuNummer(Config.getVUNummer().getInhalt());
    }

    /**
     * Legt einen Vorsatz mit dem angegebenen Inhalt an.
     *
     * @param content Inhalt des Vorsatzes
     */
    public Vorsatz(final String content) {
        this();
        try {
            this.importFrom(content);
            LOG.debug(this + " created from \"" + content + '"');
        } catch (IOException ioe) {
            throw new IllegalArgumentException("argument too short", ioe);
        }
    }

    /**
     * Dies ist der Copy-Constructor, mit dem man einen bestehenden Vorsatz
     * kopieren kann.
     *
     * @param other der originale Vorsatz
     */
    public Vorsatz(final Vorsatz other) {
        super(1, other.cloneTeildatensaetze(), other.getSatzversion());
    }

    private static String getVersionBezeichnung(final int art) {
        Formatter formatter = new Formatter();
        try  {
            return formatter.format("Version Satzart %04d", art).toString();
        } finally {
            formatter.close();
        }
    }

    private static String getVersionBezeichnung(final int art, final int sparte) {
        Formatter formatter = new Formatter();
        try  {
            return formatter.format("Version Satzart %04d %03d", art, sparte).toString();
        } finally {
            formatter.close();
        }
    }

    /**
     * Um die VU-Nummer (Byte 5 - 9) in allen Teildatensaetzen setzen zu koennen.
     *
     * @param s
     *            VU-Nummer (max. 5-stellig)
     */
    public void setVuNummer(final String s) {
        assert s.length() <= 5 : s + " darf nur max. 5 Zeichen lang sein";
        this.set(Bezeichner.VU_NUMMER, s);
    }

    /**
     * @return VU-Nummer
     */
    public String getVuNummer() {
        return this.getFeld(Bezeichner.VU_NUMMER)
                .getInhalt()
                .trim();
    }

    /**
     * Um Absender (Byte 10 - 39) in allen Teildatensaetzen setzen zu koennen.
     *
     * @param name Absender
     */
    public void setAbsender(final String name) {
        this.set(Bezeichner.ABSENDER, name);
    }

    /**
     * Liefert den Absender (Byte 10 - 39).
     *
     * @return Absender
     */
    public String getAbsender() {
        return this.getFeld(Bezeichner.ABSENDER).getInhalt().trim();
    }

    /**
     * Um Adressat (Byte 40 - 69) in allen Teildatensaetzen setzen zu koennen.
     *
     * @param name neuer Adressat
     */
    public void setAdressat(final String name) {
        this.set(Bezeichner.ADRESSAT, name);
    }

    /**
     * @return Adressat
     */
    public String getAdressat() {
        return this.getFeld(Bezeichner.ADRESSAT)
                .getInhalt()
                .trim();
    }

    /**
     * Um Erstellungs-Datum Zeitraum vom- Zeitraum bis (Byte 70 - 85) in allen
     * Teildatensaetzen setzen zu koennen.
     *
     * @param startDatum im Format "TTMMJJJJ"
     * @param endDatum   im Format "TTMMJJJJ"
     */
    public void setErstellungsZeitraum(final String startDatum, final String endDatum) {
        Datum von = new Datum(Bezeichner.ERSTELLUNGSDAT_ZEITRAUM_VOM, 70);
        Datum bis = new Datum(Bezeichner.ERSTELLUNGSDAT_ZEITRAUM_BIS, 78);
        von.setInhalt(startDatum);
        bis.setInhalt(endDatum);

        this.set(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS, (von
                .getInhalt()).concat(bis.getInhalt()));
    }

    /**
     * Um Erstellungs-Datum Zeitraum vom- Zeitraum bis (Byte 70 - 85) in allen
     * Teildatensaetzen setzen zu koennen.
     *
     * @param startDatum im Format "TTMMJJJJ"
     * @param endDatum   im Format "TTMMJJJJ"
     */
    public void setErstellungsZeitraum(final Datum startDatum,
                                       final Datum endDatum) {
        this.set(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS, (startDatum
                .getInhalt()).concat(endDatum.getInhalt()));
    }

    /**
     * @return Erstellungszeitraum (VonDatum, BisDatum)
     * TODO: Hier wuerde ich als Ergebnis eher eine Date-Range erwarten,
     *       z.B.  com.google.common.collect.Range<LocalDate> aus guava-Lib
     */
    public String getErstellungsZeitraum() {
        return this.getFeld(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS).getInhalt().trim();
    }

    public void setErstellungsZeitraumVon(Datum von) {
        setErstellungsZeitraum(von, getErstellungsZeitraumBis());
    }

    public Datum getErstellungsZeitraumVon() {
        Feld vonBis = super.getFeld(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS);
        Datum von = new Datum(ERSTELLUNGSDAT_ZEITRAUM_VOM, 8, vonBis.getByteAdresse());
        von.setInhalt(vonBis.getInhalt().substring(0, 8));
        return von;
    }

    public void setErstellungsZeitraumBis(Datum bis) {
        setErstellungsZeitraum(getErstellungsZeitraumVon(), bis);
    }

    public Datum getErstellungsZeitraumBis() {
        Feld vonBis = super.getFeld(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS);
        Datum bis = new Datum(ERSTELLUNGSDAT_ZEITRAUM_BIS, 8, vonBis.getByteAdresse() + 8);
        bis.setInhalt(vonBis.getInhalt().substring(8));
        return bis;
    }

    /**
     * Um Geschaeftsstelle/Vermittler (Byte 86 - 95) in allen Teildatensaetzen
     * setzen zu koennen.
     *
     * @param s neuer Vermittler
     */
    public void setVermittler(final String s) {
        this.set(Bezeichner.GESCHAEFTSSTELLE_VERMITTLER, s);
    }

    /**
     * @return Vermittler
     */
    public String getVermittler() {
        return this.getFeld(Bezeichner.GESCHAEFTSSTELLE_VERMITTLER)
                .getInhalt()
                .trim();
    }

    /**
     * Ermittelt die Version des uebergebenen Bezeichners.
     *
     * @param bezeichner z.B. VERSION_VORSATZ; hier koennen alle die
     *            Bezeichner-Konstanten gewaehlt werden, die mit "VERSION_"
     *            anfangen.
     * @return Version des gewuenschten Bezeichners
     * @since 2.0
     */
    public String getVersion(final Bezeichner bezeichner) {
        return this.getFeld(bezeichner).getInhalt();
    }

    /**
     * Ermittelt die Version des uebergebenen Bezeichners.
     *
     * @param bezeichner z.B. VERSION_VORSATZ; hier koennen alle die
     *            Bezeichner-Konstanten gewaehlt werden, die mit "VERSION_"
     *            anfangen.
     * @return Version des gewuenschten Bezeichners
     */
    public String getVersion(final String bezeichner) {
        return this.getFeld(bezeichner).getInhalt();
    }

    /**
     * @param art Satzart
     * @return z.B. 1.1
     */
    public String getVersion(final int art) {
        return this.getVersion(getVersionBezeichnung(art));
    }

    /**
     * @param art Satzart
     * @param sparte z.B. 70 (Rechtsschutz)
     * @return z.B. 1.1
     */
    public String getVersion(final int art, final int sparte) {
        return this.getVersion(getVersionBezeichnung(art, sparte));
    }

    /**
     * Liefert die Version zum gewuenschten SatzTyp.
     *
     * @param satzTyp z.B. SatzTyp.of("0100");
     * @return z.B. 2.3
     * @since 5.0
     */
    public String getVersion(SatzTyp satzTyp) {
        Bezeichner bezeichner = Bezeichner.of("Version Satzart " + satzTyp);
        return this.getVersion(bezeichner);
    }

    /**
     * Setzen der Satzart-Version eines Datensatzes.
     *
     * @param satz der Satz
     * @return <code>true</code> wenn im Vorsatz Version gesetzt wurde,
     * <code>false</code> wenn Satzart im Vorsatz unbekannt
     *
     * TODO: Setter sollten keinen Rückgabewert besitzen - lieber IllegalArgumentException werfen
     */
    public boolean setVersion(Satz satz) {
        boolean bReturn = false;
        StringBuilder buf = new StringBuilder();
        String[] parts = StringUtils.split(satz.getSatzTyp()
                .toString(), '.');

        buf.append("Satzart");
        buf.append(parts[0]);

        if (parts.length > 1)
            buf.append(parts[1]);

        Bezeichner bezeichner = new Bezeichner(buf.toString());

        if (this.hasFeld(bezeichner)) {
            this.set(bezeichner, satz.getSatzversion()
                    .getInhalt());
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * Setzen der Satzart-Version eines SatzTyps.
     *
     * @param satzTyp der Satztyp
     * @return <code>true</code> wenn im Vorsatz Version gesetzt wurde,
     * <code>false</code> wenn Satzart zum SatzTyp im Vorsatz unbekannt
     *
     * TODO: Setter sollten keinen Rückgabewert besitzen - lieber IllegalArgumentException werfen
     */
    public boolean setVersion(SatzTyp satzTyp) {
        boolean bReturn = false;
        StringBuilder buf = new StringBuilder();
        String[] parts = StringUtils.split(satzTyp.toString(), '.');

        buf.append("Satzart");
        buf.append(parts[0]);

        if (parts.length > 1)
            buf.append(parts[1]);

        Bezeichner bezeichner = new Bezeichner(buf.toString());

        if (this.hasFeld(bezeichner)) {
            this.set(bezeichner, SatzFactory.getDatensatz(satzTyp)
                    .getSatzversion()
                    .getInhalt());
            bReturn = true;
        }

        return bReturn;
    }



    /**
     * Da im Feld "Erstellungs-Datum Zeitraum vom- Zeitraum bis" (Adresse 70-85)
     * 2 Datumsfelder zusammengefasst sind, ist diese Methode ueberschrieben,
     * um diese beiden Felder auch einzeln abfragen zu koennen.
     *
     * @param bezeichner gesuchtes Field
     * @return Wert des Feldes als String
     */
    @Override
    public String get(Bezeichner bezeichner) {
        return getFeld(bezeichner).getInhalt();
    }

    /**
     * Da im Feld "Erstellungs-Datum Zeitraum vom- Zeitraum bis" (Adresse 70-85)
     * 2 Datumsfelder zusammengefasst sind, ist diese Methode ueberschrieben,
     * um diese beiden Felder auch einzeln abfragen zu koennen.
     *
     * @param bezeichner gesuchtes Field
     * @return Feld
     */
    @Override
    public Feld getFeld(Bezeichner bezeichner) throws IllegalArgumentException {
        if (bezeichner.equals(ERSTELLUNGSDAT_ZEITRAUM_BIS)) {
            return getErstellungsZeitraumBis();
        } else if (bezeichner.equals(ERSTELLUNGSDAT_ZEITRAUM_VOM)) {
            return getErstellungsZeitraumVon();
        }
        return super.getFeld(bezeichner);
    }

    /**
     * Hier wird {@link Satz#getFelder()} ueberschrieben, um das Feld
     * "Erstellungs-Datum, Zeitraum von, Zeitraum bis" in zwei Felder
     * aufzuteilen. Dies wird u.a. von den verschiedenen Formattern
     * (wie z.B. {@link gdv.xport.util.CsvFormatter} fuer die Aufbereitung
     * der Ausgabe verwendet.
     *
     * @return alle Felder in der richtigen Reihenfolge
     */
    @Override
    public Collection<Feld> getFelder() {
        List<Feld> felder = new ArrayList<>();
        for (Feld f : super.getFelder()) {
            if (f.getBezeichner().equals(Bezeichner.ERSTELLUNGS_DAT_ZEITRAUM_VOM_ZEITRAUM_BIS)) {
                felder.add(getErstellungsZeitraumVon());
                felder.add(getErstellungsZeitraumBis());
            } else {
                felder.add(f);
            }
        }
        return felder;
    }

}
