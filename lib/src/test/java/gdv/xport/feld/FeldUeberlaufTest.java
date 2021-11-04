package gdv.xport.feld;

import gdv.xport.config.Config;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit-Test fuer den Feld-Ueberlauf.
 * 
 * @author junke
 * @version $Revision:$<br/>
 *          $Date:$<br/>
 *          $Author:$
 */
public class FeldUeberlaufTest {

    private static final Config TRUNCATE = Config.getInstance().withProperty("gdv.feld.truncate", "true");

  /**
   * Test method for {@link Feld#setInhalt(String)} bei Align.RIGHT .
   */
  @Test
  public void testAlphaNumFeldRechtsbuendig()
  {
    AlphaNumFeld alphaNumFeldRechts =
        new AlphaNumFeld(new Bezeichner("alphaNumRechtsTestFeld"), 8, 1, Align.RIGHT).mitConfig(TRUNCATE);

    alphaNumFeldRechts.setInhalt("hello world helle lello");
    assertEquals("hello wo", alphaNumFeldRechts.getInhalt());

    alphaNumFeldRechts.setInhalt(1234567890);
    assertEquals("12345678", alphaNumFeldRechts.getInhalt());

    alphaNumFeldRechts.setInhalt(1234);
    assertEquals("    1234", alphaNumFeldRechts.getInhalt());

    alphaNumFeldRechts.setInhalt("hello");
    assertEquals("   hello", alphaNumFeldRechts.getInhalt());
    // wenn man Blanks reingibt, bleiben sie erhalten.
    alphaNumFeldRechts.setInhalt(" hello  ");
    assertEquals(" hello  ", alphaNumFeldRechts.getInhalt());
    // und auch hier bleiben die Blanks erhalten - hier gibt es nix zum Auffuellen!
    alphaNumFeldRechts.setInhalt("hello   ");
    assertEquals("hello   ", alphaNumFeldRechts.getInhalt());
  }

  /**
   * Test method for {@link Feld#setInhalt(String)} bei Align.LEFT .
   */
  @Test
  public void testAlphaNumFeldLinksbuendig()
  {
    AlphaNumFeld alphaNumFeldLinks =
        new AlphaNumFeld(new Bezeichner("alphaNumLinksTestFeld"), 9, 10, Align.LEFT).mitConfig(TRUNCATE);

    alphaNumFeldLinks.setInhalt("hello world helle lello");
    assertEquals("hello wor", alphaNumFeldLinks.getInhalt());

    alphaNumFeldLinks.setInhalt(1234567890);
    assertEquals("123456789", alphaNumFeldLinks.getInhalt());

    alphaNumFeldLinks.setInhalt(1234);
    assertEquals("1234     ", alphaNumFeldLinks.getInhalt());

    alphaNumFeldLinks.setInhalt("hello");
    assertEquals("hello    ", alphaNumFeldLinks.getInhalt());
    // wenn man Blanks reingibt, bleiben sie erhalten.
    alphaNumFeldLinks.setInhalt(" hello  ");
    assertEquals(" hello   ", alphaNumFeldLinks.getInhalt());
    // und auch hier bleiben die Blanks erhalten - hier gibt es nix zum Auffuellen!
    alphaNumFeldLinks.setInhalt(" hello  ");
    assertEquals(" hello   ", alphaNumFeldLinks.getInhalt());
  }

  /**
   * Test method for {@link Feld#setInhalt()} bei NumFeld .
   */
  @Test
  @Ignore // Validierung laesst sich noch nicht konfigierieren
  public void testNumFeld()
  {
    NumFeld numFeldOhneNachkomma = new NumFeld(new Bezeichner("numOhneNachkommaTestFeld"), 5, 1).mitConfig(TRUNCATE);

    numFeldOhneNachkomma.setInhalt(100000);
    assertEquals("99999", numFeldOhneNachkomma.getInhalt());

    numFeldOhneNachkomma.setInhalt(99999);
    assertEquals("99999", numFeldOhneNachkomma.getInhalt());

    numFeldOhneNachkomma.setInhalt(99);
    assertEquals("00099", numFeldOhneNachkomma.getInhalt());

    numFeldOhneNachkomma.setInhalt("123000");
    assertEquals("99999", numFeldOhneNachkomma.getInhalt());

    numFeldOhneNachkomma.setInhalt("99999");
    assertEquals("99999", numFeldOhneNachkomma.getInhalt());

    numFeldOhneNachkomma.setInhalt("99");
    assertEquals("00099", numFeldOhneNachkomma.getInhalt());

    /**
     * @Oli: ein numerisches Feld darf unter keinen Umständen einen nicht-numerischen Inhalt haben!
     *       Ansonsten kann es passieren, dass via "Betrag.Betrag(final Feld other)" ein Betrag-Feld
     *       auch einen nicht-numerischen Inhalt hat!
     * 
     *       Lege deine Stirn nicht in Sorgenfalen wg. des Imports. Dafuer habe ich in
     *       "Teildatensatz.ImportFrom(..)" gesorgt.
     */

    Exception exception = assertThrows(NumberFormatException.class, () ->
    {
      numFeldOhneNachkomma.setInhalt("AA");
    });

    assertTrue(exception.getMessage()
        .contains("For input string"));
  }

  /**
   * Test method for {@link gdv.xport.Betrag#setInhalt()} .
   */
  @Test
  @Ignore // Validierung laesst sich noch nicht konfigierieren
  public void testBetrag()
  {
    Betrag betrag = new Betrag(new Bezeichner("betragTest"), 10, 20);

    betrag.setInhalt("123");
    assertEquals("0000000123", betrag.getInhalt());

    betrag.setInhalt(123456);
    assertEquals("0012345600", betrag.getInhalt());

    betrag.setInhalt(1234567890L);
    assertEquals("1234567890", betrag.getInhalt());

    /**
     * @Oli: ein Betrag-Feld darf nicht negativ sein!
     */
    Exception exception1 = assertThrows(IllegalArgumentException.class, () ->
    {
      betrag.setInhalt("-123");
    });

    assertTrue(exception1.getMessage()
        .contains("Betrag can't store negative number"));

    /**
     * @Oli: ein Betrag-Feld darf unter keinen Umständen einen nicht-numerischen Inhalt haben!
     * 
     *       Lege deine Stirn nicht in Sorgenfalen wg. des Imports. Dafuer habe ich in
     *       "Teildatensatz.ImportFrom(..)" gesorgt.
     */
    Exception exception2 = assertThrows(NumberFormatException.class, () ->
    {
      betrag.setInhalt("1A3");
    });

    assertTrue(exception2.getMessage()
        .contains("For input string"));

    Exception exception3 = assertThrows(NumberFormatException.class, () ->
    {
      betrag.setInhalt("");
    });

    assertTrue(exception3.getMessage()
        .contains("For input string"));
  }

  /**
   * Test method for {@link gdv.xport.BetragMitVorzeichen#setInhalt(String)} .
   */
  @Test
  @Ignore // hier fehlt noch die Implementierung #mitConfig(Config)
  public void testBetragMitVorzeichen()
  {
    BetragMitVorzeichen betragMVz = new BetragMitVorzeichen(new Bezeichner("betragTEstVZ"), 10, 2);

    betragMVz.setInhalt("123");
    assertEquals("000000123+", betragMVz.getInhalt());

    betragMVz.setInhalt(123456);
    assertEquals("000123456+", betragMVz.getInhalt());

    betragMVz.setInhalt(1234567890);
    assertEquals("999999999+", betragMVz.getInhalt());

    betragMVz.setInhalt("-123");
    assertEquals("000000123-", betragMVz.getInhalt());

    Long wert = 12345678901L;
    betragMVz.setInhalt(wert);
    assertEquals("999999999+", betragMVz.getInhalt());

    /**
     * @Oli: ein BetragMitVorzeichen-Feld darf unter keinen Umständen einen nicht-numerischen Inhalt
     *       haben!
     * 
     *       Lege deine Stirn nicht in Sorgenfalen wg. des Imports. Dafuer habe ich in
     *       "Teildatensatz.ImportFrom(..)" gesorgt.
     */

    Exception exception1 = assertThrows(NumberFormatException.class, () ->
    {
      betragMVz.setInhalt("1A3");
    });

    assertTrue(exception1.getMessage()
        .contains("For input string"));

    Exception exception2 = assertThrows(NumberFormatException.class, () ->
    {
      betragMVz.setInhalt("");
    });

    assertTrue(exception2.getMessage()
        .contains("For input string"));
  }
}
