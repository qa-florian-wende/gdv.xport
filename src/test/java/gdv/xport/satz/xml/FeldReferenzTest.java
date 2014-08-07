/*
 * Copyright (c) 2014 by Oli B.
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
 * (c)reated 07.08.2014 by Oli B. (ob@aosd.de)
 */

package gdv.xport.satz.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

/**
 * Unit-Tests fuer {@link FeldReferenz}-Klasse.
 *
 * @author oliver (oliver.boehm@gmail.com)
 * @since 1.0 (07.08.2014)
 */
public class FeldReferenzTest {

    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    /**
     * Test-Methode fuer {@link FeldReferenz#FeldReferenz(XMLEventReader)}.
     *
     * @throws XMLStreamException the XML stream exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testSatzReferenz() throws XMLStreamException, IOException {
        InputStream istream = this.getClass().getResourceAsStream("feldreferenz.xml");
        assertNotNull("resource 'feldreferenz.xml' not found", istream);
        XMLEventReader parser = xmlInputFactory.createXMLEventReader(istream);
        try {
            FeldReferenz referenz = new FeldReferenz(parser);
            assertEquals("BN-2003.02.11.22.49.47.214", referenz.getId());
        } finally {
            istream.close();
        }
    }

}
