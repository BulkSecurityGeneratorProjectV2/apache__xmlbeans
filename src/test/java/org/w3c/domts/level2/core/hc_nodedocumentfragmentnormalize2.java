/*
This Java source file was generated by test-to-java.xsl
and is a derived work from the source document.
The source document contained the following notice:


Copyright (c) 2004 World Wide Web Consortium,
(Massachusetts Institute of Technology, Institut National de
Recherche en Informatique et en Automatique, Keio University). All
Rights Reserved. This program is distributed under the W3C's Software
Intellectual Property License. This program is distributed in the
hope that it will be useful, but WITHOUT ANY WARRANTY; without even
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.
See W3C License http://www.w3.org/Consortium/Legal/ for more details.

*/

package org.w3c.domts.level2.core;


import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import static org.junit.Assert.assertNull;
import static org.w3c.domts.DOMTest.load;


/**
 * Create a document fragment with an empty text node, after normalization there should be no child nodes.
 * were combined.
 *
 * @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-F68D095">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-F68D095</a>
 * @see <a href="http://www.w3.org/TR/DOM-Level-2-Core/core#ID-B63ED1A3">http://www.w3.org/TR/DOM-Level-2-Core/core#ID-B63ED1A3</a>
 */

/**
 * TODO: add this back
 *
 * @ignore true
 */
public class hc_nodedocumentfragmentnormalize2 {
    @Test
    public void testRun() throws Throwable {
        Document doc;
        DocumentFragment docFragment;
        String nodeValue;
        Text txtNode;
        Node retval;
        doc = load("hc_staff", true);
        docFragment = doc.createDocumentFragment();
        txtNode = doc.createTextNode("");
        retval = docFragment.appendChild(txtNode);
        docFragment.normalize();
        txtNode = (Text) docFragment.getFirstChild();
        assertNull("There should be no child nodes after normalization", txtNode);

    }

    /**
     * Gets URI that identifies the test
     *
     * @return uri identifier of test
     */
    public String getTargetURI() {
        return "http://www.w3.org/2001/DOM-Test-Suite/level2/core/hc_nodedocumentfragmentnormalize2";
    }

}