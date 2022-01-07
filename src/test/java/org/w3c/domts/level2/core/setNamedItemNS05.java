/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:



Copyright (c) 2001 World Wide Web Consortium,
(Massachusetts Institute of Technology, Institut National de
Recherche en Informatique et en Automatique, Keio University).  All
Rights Reserved.  This program is distributed under the W3C's Software
Intellectual Property License.  This program is distributed in the
hope that it will be useful, but WITHOUT ANY WARRANTY; without even
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.

See W3C License http://www.w3.org/Consortium/Legal/ for more details.


*/

package org.w3c.domts.level2.core;


import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.w3c.domts.DOMTest.load;


/**
 * The "setNamedItemNS(arg)" method for a
 * NamedNodeMap should replace an existing node n1 found in the map with arg if n1
 * has the same namespaceURI and localName as arg and return n1.
 * <p>
 * Create an attribute node in with namespaceURI "http://www.usa.com"
 * and qualifiedName "dmstc:domestic" whose value is "newVal".
 * Invoke method setNamedItemNS(arg) on the map of the first "address"
 * element. Method should return the old attribute node identified
 * by namespaceURI and qualifiedName from above,whose value is "Yes".
 *
 * @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAtNodeNS">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-ElSetAtNodeNS</a>
 */
public class setNamedItemNS05 {
    @Test
    public void testRun() throws Throwable {
        String namespaceURI = "http://www.usa.com";
        String qualifiedName = "dmstc:domestic";
        Document doc;
        Node arg;
        NodeList elementList;
        Node testAddress;
        NamedNodeMap attributes;
        Node retnode;
        String value;
        doc = load("staffNS", true);
        arg = doc.createAttributeNS(namespaceURI, qualifiedName);
        arg.setNodeValue("newValue");
        elementList = doc.getElementsByTagName("address");
        testAddress = elementList.item(0);
        attributes = testAddress.getAttributes();
        retnode = attributes.setNamedItemNS(arg);
        value = retnode.getNodeValue();
        assertEquals("throw_Equals", "Yes", value);

    }

    /**
     * Gets URI that identifies the test
     *
     * @return uri identifier of test
     */
    public String getTargetURI() {
        return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/setNamedItemNS05";
    }

}