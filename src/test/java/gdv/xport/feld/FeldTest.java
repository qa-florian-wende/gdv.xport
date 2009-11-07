/*
 * $Id$
 *
 * Copyright (c) 2009 by agentes
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
 * (c)reated 05.10.2009 by Oli B. (oliver.boehm@agentes.de)
 */

package gdv.xport.feld;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.oval.ConstraintViolation;

import org.apache.commons.logging.*;
import org.junit.Test;

/**
 * @author oliver
 * @since 05.10.2009
 * @version $Revision$
 *
 */
public class FeldTest {

    private static final Log log = LogFactory.getLog(FeldTest.class);

    /**
     * Test method for {@link gdv.xport.feld.Feld#resetInhalt()}.
     */
    @Test
    public void testResetInhalt() {
        Feld feld = new Feld("testfeld", "abc", Align.LEFT);
        feld.resetInhalt();
        assertEquals("   ", feld.getInhalt());
    }

    @Test
    public void testSetInhalt() {
        Feld linksbuendig = new Feld("f1", "hello", Align.LEFT);
        linksbuendig.setInhalt("abc");
        assertEquals("abc  ", linksbuendig.getInhalt());
        Feld rechtsbuendig = new Feld("f2", "world", Align.RIGHT);
        rechtsbuendig.setInhalt("hi");
        assertEquals("   hi", rechtsbuendig.getInhalt());
    }

    @Test
    public void testFeld() {
        Feld zeichen = new Feld("Testfeld", 1, 1, 'x', Align.LEFT);
        assertEquals("x", zeichen.getInhalt());
    }

    @Test
    public void testOverlapsWith() {
        Feld a = new Feld("a", 2, 1, Align.LEFT);    // Byte 1-2
        Feld b = new Feld("b", 2, 3, Align.LEFT);    // Byte 3-4
        Feld c = new Feld("c", 2, 2, Align.LEFT);    // Byte 2-3
        assertFalse(a + " overlaps with " + b, a.overlapsWith(b));
        assertFalse(b + " overlaps with " + a, b.overlapsWith(a));
        assertTrue(b + " doesn't overlap with " + c, b.overlapsWith(c));
        assertTrue(c + " doesn't overlap with " + a, c.overlapsWith(a));
        assertTrue(c + " doesn't overlap with " + b, c.overlapsWith(b));
    }

    @Test
    public void testIsValid() {
        Feld a = new Feld("a", 257, -1, Align.UNKNOWN);
        assertFalse(a + " is not valid - to long, wrong start byte, unknow alignment", a.isValid());
        Feld b = new Feld("b", 2, 256, Align.LEFT);
        assertFalse(b + " geht ueber Satz-Grenze hinweg", b.isValid());
        Feld c = new Feld("c", 1, 'c');
        assertTrue(c + " should be valid", c.isValid());
    }

    @Test
    public void testValidate() {
        Feld a = new Feld("a", 10, -1, Align.UNKNOWN);
        List<ConstraintViolation> violations = a.validate();
        for (ConstraintViolation violation : violations) {
            log.info("ConstraintViolation: " + violation);
        }
        assertEquals(2, violations.size());
    }

    @Test
    public void testEquals() {
        Feld a = new Feld("x", 2, 1, Align.LEFT);
        Feld b = new Feld("x", 2, 1, Align.LEFT);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setInhalt('b');
        assertFalse(a + " differs from " + b, a.equals(b));
    }

}


/*
 * $Log$
 * $Source$
 */
