/*
 * Copyright (c) 2009 - 2017 by Oli B.
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
 * (c)reated 09.10.2009 by Oli B. (ob@aosd.de)
 */
package gdv.xport.feld;

import gdv.xport.annotation.FeldInfo;
import gdv.xport.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static java.math.BigDecimal.ZERO;

/**
 * Klasse fuer numerische Zeichen. Die Default-Einstellung fuer die
 * Darstellung ist rechtsbuendig.
 * <p>
 * Siehe Broschuere_gdv-datensatz_vu-vermittler.pdf, Seite 16
 * ("Datenfelder/Feldformate").
 * </p>
 *
 * @author oliver
 */
public class NumFeld extends Feld {

    private static final Logger LOG = LogManager.getLogger(NumFeld.class);
    private static final Feld.Validator DEFAULT_VALIDATOR =new NumFeld.Validator(Config.getInstance());
    private final int nachkommastellen;

    /**
     * @param name Feld-Bezeichner (z.B. "Anzahl Saetze")
     * @param s z.B. "4"
     */
    public NumFeld(final String name, final String s) {
        this(new Bezeichner(name), s.length(), 1, 0, DEFAULT_VALIDATOR);
        this.setInhalt(s);
    }

    /**
     * Legt ein neues numerisches Feld an.
     *
     * @param bezeichner Feld-Bezeichner (z.B. "Anzahl Saetze")
     * @param length Anzahl Bytes
     * @param start Start-Byte (beginnend bei 1)
     * @since 1.0
     */
    public NumFeld(Bezeichner bezeichner, int length, int start) {
        this(bezeichner, length, start, 0, DEFAULT_VALIDATOR);
    }

    /**
     * Legt ein neues numerisches Feld an.
     *
     * @param bezeichner Feld-Bezeichner (z.B. "Anzahl Saetze")
     * @param length Anzahl Bytes
     * @param start Start-Byte (beginnend bei 1)
     * @param value z.B. "01"
     * @since 1.0
     */
    public NumFeld(final Bezeichner bezeichner, final int length, final int start, final int value) {
        this(bezeichner, length, start);
        this.setInhalt(value);
    }

    /**
     * @since 0.4
     * @param name Feld-Bezeichner (z.B. "Anzahl Saetze")
     * @param start Start-Byte (beginnend bei 1)
     * @param value z.B. "01"
     */
    public NumFeld(final String name, final int start, final String value) {
        this(new Bezeichner(name), value.length(), start);
        this.setInhalt(value);
    }

    /**
     * @since 0.4
     * @param name Feld-Bezeichner (z.B. "pi")
     * @param s der Inhalt (z.B. "314")
     * @param nachkommastellen Anzahl der Nachkommastellen (z.B. 2)
     */
    public NumFeld(final String name, final String s, final int nachkommastellen) {
        this(new Bezeichner(name), s.length(), 1, nachkommastellen, DEFAULT_VALIDATOR);
        this.setInhalt(s);
    }

    /**
     * Instantiiert ein neues numerisches Feld.
     *
     * @param name Feld-Bezeichner (z.B. "pi")
     * @param start Start-Byte (beginnend ab 1)
     * @param value der Inhalt (z.B. "314")
     * @param nachkommastellen Anzahl der Nachkommastellen (z.B. 2)
     * @since 4.0
     */
    public NumFeld(final Bezeichner name, final int start, final String value, final int nachkommastellen) {
        this(name, value.length(), start, nachkommastellen, DEFAULT_VALIDATOR);
        this.setInhalt(value);
    }

    /**
     * Legt ein neues numerisches Feld an.
     *
     * @param name Feld-Bezeichner (z.B. "pi")
     * @param length Gesamtlaenge
     * @param start Start-Byte (beginnend ab 1)
     * @param value der Inhalt (z.B. 314)
     * @param nachkommastellen Anzahl der Nachkommastellen (z.B. 2)
     * @since 1.0
     */
    public NumFeld(final Bezeichner name, final int length, final int start, final int value,
            final int nachkommastellen) {
        this(name, length, start, nachkommastellen, DEFAULT_VALIDATOR);
        this.setInhalt(value);
    }

    protected NumFeld(final Bezeichner name, final int length, final int start,
                      final int nachkommastellen, final Feld.Validator validator) {
        super(name, length, start, Align.RIGHT, validator);
        this.nachkommastellen = nachkommastellen;
        this.setInhalt(0);
    }

    /**
     * Dies ist der Copy-Constructor, mit dem man ein bestehendes Feld
     * kopieren kann.
     *
     * @param other das originale Feld
     */
    public NumFeld(final Feld other) {
        this(other, Config.getInstance());
    }

    private NumFeld(Feld other, Config config) {
        super(other, new Validator(config));
        if (other instanceof NumFeld) {
            this.nachkommastellen = ((NumFeld) other).nachkommastellen;
        } else {
            this.nachkommastellen = 0;
        }
    }

    /**
     * Die Default-Ausrichtung ist rechts-buendig fuer Zahlen.
     *
     * @return rechts-buendig
     */
    @Override
    protected Align getDefaultAlignment() {
        return Align.RIGHT;
    }

    /**
     * Liefert ein neues NumFeld mit der gewuenschten Anzahl von Nachkommastellen zurueck.
     *
     * @since 0.4
     * @param n Anzahl der Nachkommastellen
     * @return neues NumFeld mit n Nachkommastellen
     */
    public NumFeld mitNachkommastellen(final int n) {
        if (n > this.getAnzahlBytes()) {
            throw new IllegalArgumentException(n + " Nachkommastellen sind zuviel (max. "
                    + this.getAnzahlBytes() + " moeglich)");
        }
        return new NumFeld(this.getBezeichner(), this.getByteAdresse(), this.getInhalt(), n);
    }

    /**
     * Liefert eine neues NumFeld mit neuer Konfiguration
     *
     * @param c neue Konfiguration
     * @return neues NumFeld
     * @since 5.3
     */
    @Override
    public NumFeld mitConfig(Config c) {
        return new NumFeld(this, c);
    }

    /**
     * Liefert die Anzahl der Nachkommastellen.
     *
     * @return Anzahl der Nachkommastellen
     */
    public int getNachkommastellen() {
        return this.nachkommastellen;
    }

    /**
     * Setzt den Inhalt mit der uebergebenen Ziffer.
     *
     * @param c neuer Inhalt
     */
    @Override
    public void setInhalt(final char c) {
        this.setInhalt(Character.toString(c));
    }

    /**
     * Setzt den Inhalt mit der uebergebenen Zahl.
     *
     * @param n neuer Inhalt
     */
    @Override
    public void setInhalt(final int n) {
        this.setInhalt((long) n);
    }

    /**
     * Setzt den Inhalt mit der uebergebenen Zahl.
     *
     * @param n neuer Inhalt
     */
    public void setInhalt(final long n) {
        String pattern = StringUtils.repeat("0", this.getAnzahlBytes());
        NumberFormat format = new DecimalFormat(pattern);
        String formatted = format.format(n);
        this.setInhalt(formatted);
    }

    /**
     * Setzt den Inhalt mit der uebergebenen Zahl.
     *
     * @param n Zahl
     * @since 5.0
     */
    public void setInhalt(BigDecimal n) {
        setInhalt(n.movePointRight(this.nachkommastellen).setScale(0, RoundingMode.HALF_UP).toString());
    }

    @Override
    public void setInhalt(String value) {
        String s = validator.verify(value, this);
        if (config.getBool("gdv.numfeld.fill-blanks")) {
            super.setInhalt(s.trim());
        } else {
            super.setInhalt(s);
        }
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.Feld#resetInhalt()
     */
    @Override
    public void resetInhalt() {
        int anzahlBytes = this.getAnzahlBytes();
        for (int i = 0; i < anzahlBytes; i++) {
            this.setInhalt('0', i);
        }
    }

    @Override
    protected String truncate(String s) {
        if (s.startsWith("0") && (s.length() > 1)) {
            return truncate(s.substring(1));
        } else if (s.length() > getAnzahlBytes()) {
            LOG.info("NumFeld {} wird auf {} Ziffern gekuerzt", getBezeichner(), getAnzahlBytes());
            return StringUtils.repeat('9', getAnzahlBytes());
        }
        return s;
    }

    /**
     * Wandelt den Inhalt in einen Integer (ohne Beruecksichtigung etwaiger
     * Nachkommastellen).
     *
     * @return den Inhalt als int
     */
    public int toInt() {
        String s = this.getInhalt().trim();
        if (s.startsWith("+")) {
            return Integer.parseInt(s.substring(1));
        }
        return Integer.parseInt(s);
    }

    /**
     * Wandelt den Inhalt in einen Long (ohne Beruecksichtigung etwaiger
     * Nachkommastellen).
     *
     * @return den Inhalt als long
     */
    public long toLong() {
        String s = this.getInhalt().trim();
        if (s.startsWith("+")) {
            return Long.parseLong(s.substring(1));
        }
        return Long.parseLong(s);
    }

    /**
     * Wenn eine Zahl Nachkommastellen hat, sollte sie auch als Double
     * ausgegeben werden koennen.
     *
     * @since 0.4
     * @return die Zahl als Double
     */
    public double toDouble() {
        return toBigDecimal().doubleValue();
    }

    /**
     * Fuer grosse Zahlen kann auch schon mal ein {@link BigInteger} noetig
     * sein.
     *
     * @since 5.4
     * @return die Zahl als {@link BigInteger}
     */
    public BigInteger toBigInteger() {
        return new BigInteger(getInhalt().trim());
    }

    /**
     * Wenn eine Zahl Nachkommastellen hat, sollte sie auch als {@link BigDecimal}
     * ausgegeben werden koennen.
     *
     * @since 5.0
     * @return die Zahl als {@link BigDecimal}
     */
    public BigDecimal toBigDecimal() {
        BigDecimal d = new BigDecimal(getInhalt().trim());
        return d.movePointLeft(this.nachkommastellen);
    }

    /**
     * Addiert den Summand auf und liefert die Summe zurueck.
     *
     * @param summand der aufaddiert wird
     * @return Summe
     * @since 5.0
     */
    public BigDecimal add(BigDecimal summand) {
        BigDecimal summe = toBigDecimal().add(summand);
        setInhalt(summe);
        return summe.setScale(nachkommastellen, RoundingMode.UP);
    }

    /**
     * Dient zum Ermittel, ob ein Werte schon gesetzt wurde. Dabei werden
     * typische Initialisierungswerte wie "0" als "nicht gesetzt"
     * interpretiert.
     *
     * @return true, falls Feld mit einem Wert belegt ist
     * @since 3.1
     */
    @Override
    public boolean hasValue() {
        try {
            return super.hasValue() && !ZERO.equals(toBigDecimal());
        } catch (NumberFormatException ex) {
            LOG.debug("{} hat ungueltigen Wert:", this, ex);
            return false;
        }
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.Feld#format()
     */
    @Override
    public String format() {
        NumberFormat nf = NumberFormat.getInstance();
        if (this.nachkommastellen == 0) {
            return nf.format(this.toLong());
        }
        nf.setMinimumFractionDigits(this.nachkommastellen);
        nf.setMaximumFractionDigits(this.nachkommastellen);
        return nf.format(this.toDouble());
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.Feld#clone()
     */
    @SuppressWarnings("squid:S2975")
    @Override
    public Object clone() {
        return new NumFeld(this);
    }


    /**
     * Die Validierung von Werten wurde jetzt in einer eingenen Validator-
     * Klasse zusammengefasst. Damit kann die Validierung auch unabhaengig
     * von NumFeld-Klasse im Vorfeld eingesetzt werden, um Werte auf ihre
     * Gueltigkeit pruefen zu koennen.
     *
     * @since 5.3
     */
    public static class Validator extends Feld.Validator {

        public Validator() {
            super();
        }

        public Validator(Config config) {
            super(config);
        }

        @Override
        protected String validateLax(String value) {
            String nummer = super.validateLax(value);
            LOG.debug("{} wird als Zahl validiert.", nummer);
            if (StringUtils.isNotBlank(nummer)) {
                try {
                    BigInteger n = new BigInteger(nummer.trim());
                    if (n.compareTo(BigInteger.ZERO) < 0) {
                        throw new ValidationException(String.format("'%s' darf nicht negativ sein", nummer));
                    }
                } catch (NumberFormatException nfe) {
                    throw new ValidationException(String.format("'%s' ist keine Zahl", nummer), nfe);
                }
            }
            return nummer;
        }

        @Override
        protected String validateStrict(String value) {
            if (!StringUtils.trim(value).equals(value) || StringUtils.isBlank(value)) {
                throw new ValidationException("Zahl muss vorhanden sein und darf keine Leerzeichen enthalten");
            } else {
                return validateLax(value);
            }
        }

    }

}
