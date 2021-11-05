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
 * (c)reated 11.10.2009 by Oli B. (ob@aosd.de)
 */

package gdv.xport.feld;

import gdv.xport.annotation.FeldInfo;
import gdv.xport.config.Config;

/**
 * Standardmaessig hat das Beitrags-Feld 12,2 Stellen (12 Vorkommastellen +
 * 2 Nachkommastellen.
 *
 * @author oliver
 * @since 11.10.2009
 * @version $Revision$
 */
public class Betrag extends NumFeld {

    private static final Feld.Validator DEFAULT_VALIDATOR =new NumFeld.Validator(Config.getInstance());

    /**
     * Legt ein neues Betrags-Feld an. Die Informationen dazu werden
     * aus der uebergebenen Enum bezogen.
     * <p>
     * TODO: Wird mit v6 entfernt.
     * </p>
     *
     * @param feldX Enum mit den Feldinformationen
     * @since 0.9
     * @deprecated Enums werden ab v6 nicht mehr unterstuetzt
     */
    @Deprecated
    public Betrag(final Enum feldX) {
        super(feldX);
    }

    /**
     * Instantiiert ein neuen Betrag.
     * <p>
     * TODO: Wird mit v6 entfernt.
     * </p>
     *
     * @param feldX Feld
     * @param info mit der Start-Adresse und weiteren Angaben
     * @since 0.6
     * @deprecated Enums werden ab v6 nicht mehr unterstuetzt
     */
    @Deprecated
    public Betrag(final Enum feldX, final FeldInfo info) {
        super(feldX, info);
    }

    /**
     * Instantiiert einen neuen Betrag.
     *
     * @param name Name des Feldes
     * @param length Laenge
     * @param start Start-Byte (beginnend bei 1)
     * @since 1.0
     */
    public Betrag(final Bezeichner name, final int length, final int start) {
        this(name, length, start, DEFAULT_VALIDATOR);
    }

    private Betrag(final Betrag other, final Config config) {
        this(other.getBezeichner(), other.getByteAdresse(), other.getInhalt(), new NumFeld.Validator(config));
    }

    private Betrag(final Bezeichner name, final int length, final int start, final Feld.Validator validator) {
        super(name, length, start, 2, validator);
    }

    protected Betrag(final Bezeichner name, final int start, final String value, final Feld.Validator validator) {
        this(name, value.length(), start, validator);
        this.setInhalt(value);
    }

    /**
     * Instantiiert einen neuen Betrag.
     * <p>
     * TODO: Wird mit v6 entfernt.
     * </p>
     *
     * @param name Bezeichner
     * @param info mit der Start-Adresse und weiteren Angaben
     * @since 1.0
     * @deprecated Enums werden ab v6 nicht mehr unterstuetzt
     */
    @Deprecated
    public Betrag(final Bezeichner name, final FeldInfo info) {
        super(name, info);
    }

    /**
     * Dies ist der Copy-Constructor, mit dem man ein bestehendes Feld
     * kopieren kann.
     *
     * @param other das originale Feld
     */
    public Betrag(final Feld other) {
        super(other);
    }

    /**
     * Liefert einen neuen Betrag mit neuer Konfiguration
     *
     * @param c neue Konfiguration
     * @return neuer Betrag
     * @since 5.3
     */
    @Override
    public Betrag mitConfig(Config c) {
        return new Betrag(this, c);
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.NumFeld#setInhalt(int)
     */
    @Override
    public void setInhalt(final int n) {
        assert n >= 0 : "Betrag can't store negative number (" + n + ")";
        super.setInhalt(n * 100);
    }

    /**
     * Setzt den Inhalt eines Feldes neu.
     * @param x der neue Inhalt
     */
    public void setInhalt(final double x) {
        assert x >= 0 : "Betrag can't store negative number (" + x + ")";
        long n = Math.round(x * 100);
        super.setInhalt(n);
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.NumFeld#toInt()
     */
    @Override
    public int toInt() {
        return super.toInt() / 100;
    }
    
    @Override
    public long toLong() {
        return super.toLong() / 100L;
    }

    @Override
    public double toDouble() {
        return super.toLong() / 100.0;
    }

    /* (non-Javadoc)
     * @see gdv.xport.feld.Feld#clone()
     */
    @SuppressWarnings("squid:S2975")
    @Override
    public Object clone() {
        return new Betrag(this);
    }

    public static Betrag of(Feld feld) {
        Betrag betrag = new Betrag(feld.getBezeichner(), feld.getAnzahlBytes(), feld.getByteAdresse());
        betrag.setInhalt(feld.getInhalt());
        return betrag;
    }

}

