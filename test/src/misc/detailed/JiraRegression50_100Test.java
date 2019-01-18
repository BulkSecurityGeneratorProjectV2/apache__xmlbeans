/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package misc.detailed;

import dufourrault.DummyDocument;
import dufourrault.Father;
import misc.common.JiraTestBase;
import net.orthogony.xml.sample.structure.ARootDocument;
import net.orthogony.xml.sample.structure.ChildType;
import org.apache.beehive.netui.tools.testrecorder.x2004.session.RecorderSessionDocument;
import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.tool.SchemaCompiler;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import test.xbean.xmlcursor.purchaseOrder.PurchaseOrderDocument;
import testDateAttribute.TestDatewTZone;
import testDateAttribute.TestElementWithDateAttributeDocument;
import tools.util.JarUtil;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;


public class JiraRegression50_100Test extends JiraTestBase
{


    ///**
    // * [XMLBEANS-##]  <BUG TITLE>
    // */
    //public void test_jira_XmlBeans45() throws Exception
    //{
    //
    //}




    /**
     * [XMLBEANS-52]   Validator loops when schema has certain conditions
     */
    @Test
    public void test_jira_XmlBeans52() throws Exception{
     //reusing code from method test_jira_XmlBeans48()
     String correctXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
             "<!--Sample XML file generated by XMLSPY v5 rel. 4 U (http://www.xmlspy.com)--/> \n" +
             "<aList xmlns=\"http://pfa.dk/dummy/errorInXmlBeansValidation.xsd\" " +
             "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
             "xsi:schemaLocation=\"http://pfa.dk/dummy/errorInXmlBeansValidation.xsd \n" +
             "C:\\pfa\\techr3\\TransformationWorkbench\\schema\\errorInXmlBeansValidation.xsd\"> \n" +
             "<myListEntry> \n" +
             "<HelloWorld>Hello World</HelloWorld> \n" +
             "</myListEntry> \n" +
             "</aList> ";


     }

    /*
    * [XMLBEANS-54]: problem with default value
    */
    @Test
    public void test_jira_xmlbeans54() throws Exception {
        List errors = new ArrayList();

        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();
        params.setXsdFiles(new File[]{new File(scompTestFilesRoot + "xmlbeans_54.xsd_")});
        params.setErrorListener(errors);
        params.setSrcDir(schemaCompSrcDir);
        params.setClassesDir(schemaCompClassesDir);
        params.setDownload(true);
        params.setNoPvr(true);

        // Runs out of Heap Memory
        params.setMemoryMaximumSize("1024m");
        params.setMemoryInitialSize("512m");

        try {
            SchemaCompiler.compile(params);
        } catch (OutOfMemoryError ome) {
            System.out.println(ome.getStackTrace());
            System.out.println("test_jira_xmlbeans54() - out of Heap Memory");
        }

        if (printOptionErrMsgs(errors)) {
            fail("test_jira_xmlbeans54() : Errors found when executing scomp");
        }
    }

    /**
     * [XMLBEANS-56] samples issue with easypo schema and config file
     */
    @Test
    public void test_jira_XmlBeans56() throws Exception {
        String xsdConfig = "<xb:config " +
                " xmlns:xb=\"http://xml.apache.org/xmlbeans/2004/02/xbean/config\"\n" +
                "    xmlns:ep=\"http://openuri.org/easypo\">\n" +
                "    <xb:namespace uri=\"http://openuri.org/easypo\">\n" +
                "        <xb:package>com.easypo</xb:package>\n" +
                "    </xb:namespace>\n" +
                "    <xb:namespace uri=\"##any\">\n" +
                "        <xb:prefix>Xml</xb:prefix>\n" +
                "        <xb:suffix>Bean</xb:suffix>\n" +
                "    </xb:namespace>\n" +
                "    <xb:extension for=\"com.easypo.XmlCustomerBean\">\n" +
                "        <xb:interface name=\"myPackage.Foo\">\n" +
                "            <xb:staticHandler>myPackage.FooHandler</xb:staticHandler>\n" +
                "        </xb:interface>\n" +
                "    </xb:extension>\n" +
                "    <xb:qname name=\"ep:purchase-order\" javaname=\"purchaseOrderXXX\"/>\n" +
                "</xb:config> ";
        ConfigDocument config =
                ConfigDocument.Factory.parse(xsdConfig);
        xmOpts.setErrorListener(errorList);
        if (config.validate(xmOpts)) {
            System.out.println("Config Validated");
            return;
        } else {
            System.err.println("Config File did not validate");
            for (Iterator iterator = errorList.iterator(); iterator.hasNext();) {
                System.out.println("Error: " + iterator.next());
            }
            throw new Exception("Config File did not validate");
        }

    }

    /**
     * [XMLBEANS-57]   scomp failure for XSD namespace "DAV:"
     */
    @Test
    public void test_jira_XmlBeans57() throws Exception {
        String P = File.separator;
        String outputDir = OUTPUTROOT + P + "dav";

        File srcDir = new File(outputDir + P + "src");
        srcDir.mkdirs();
        File classDir = new File(outputDir + P + "classes");
        classDir.mkdirs();

        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();
        params.setXsdFiles(new File[]{new File(JIRA_CASES + "xmlbeans_57.xml")});
        params.setErrorListener(errorList);
        params.setSrcDir(srcDir);
        params.setClassesDir(classDir);
        SchemaCompiler.compile(params);
        Collection errs = params.getErrorListener();
        boolean outTextPresent = true;

        if (errs.size() != 0) {
            for (Iterator iterator = errs.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                String out = o.toString();
                System.out.println("Dav: " + out);
                if (out.startsWith("Compiled types to"))
                    outTextPresent = false;
            }
        }

        //cleanup gen'd dirs
        srcDir.deleteOnExit();
        classDir.deleteOnExit();

        if (outTextPresent)
            System.out.println("No errors when running schemacompiler with DAV namespace");
        else
            throw new Exception("There were errors while compiling XSD with DAV " +
                    "namespace. See sys.out for more info");
    }

    /*
    * [XMLBEANS-58]:resolving transitive <redefine>'d types...
    * This is realted to xmlbeans36 - its the same case but the schemas seem to have been updated at the w3c site.
    * Hence adding a new testcase with the new schemas
    */
    @Test
    @Ignore("the url doesn't exist anymore ...")
    public void test_jira_xmlbeans58() throws Exception {
        List errors = new ArrayList();
        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();

        // old url has been retired
        //params.setUrlFiles(new URL[]{new URL("http://devresource.hp.com/drc/specifications/wsrf/interfaces/WS-BrokeredNotification-1-0.wsdl")});
        // this seems to be a url for a WS-BrokeredNotification 1.0 wsdl
        params.setUrlFiles(new URL[]{new URL("http://www.ibm.com/developerworks/library/specification/ws-notification/WS-BrokeredN.wsdl")});
        params.setErrorListener(errors);
        params.setSrcDir(schemaCompSrcDir);
        params.setClassesDir(schemaCompClassesDir);
        params.setDownload(true);

        SchemaCompiler.compile(params);
        if (printOptionErrMsgs(errors)) {
            fail("test_jira_xmlbeans55() : Errors found when executing scomp");
        }

    }


    /**
     * [XMLBEANS-62]   Avoid class cast exception when compiling older schema namespace
     */
    @Test
    public void test_jira_XmlBeans62() throws Exception {
        String P = File.separator;
        String outputDir = System.getProperty("xbean.rootdir") + P + "build" +
                P + "test" + P + "output" + P + "x1999";

        File srcDir = new File(outputDir + P + "src");
        srcDir.mkdirs();
        File classDir = new File(outputDir + P + "classes");
        classDir.mkdirs();

        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();
        params.setWsdlFiles(new File[]{new File(JIRA_CASES + "xmlbeans_62.xml")});
        params.setErrorListener(errorList);
        params.setSrcDir(srcDir);
        params.setClassesDir(classDir);
        SchemaCompiler.compile(params);
        Collection errs = params.getErrorListener();
        boolean warningPresent = false;
        for (Iterator iterator = errs.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            String out = o.toString();
            if (out.endsWith("did not have any schema documents in namespace 'http://www.w3.org/2001/XMLSchema'")) ;
            warningPresent = true;
        }

        //cleanup gen'd dirs
        srcDir.deleteOnExit();
        classDir.deleteOnExit();

        //validate error present
        if (!warningPresent)
            throw new Exception("Warning for 1999 schema was not found when compiling srcs");
        else
            System.out.println("Warning Present, test Passed");
    }

    /**
     * [XMLBEANS-64] ArrayIndexOutOfBoundsException during validation
     */
    @Test
    public void test_jira_XmlBeans64() throws Exception {
        // load the document
        File inst = new File(JIRA_CASES + "xmlbeans_64.xml");
        XmlObject doc = RecorderSessionDocument.Factory.parse(inst);
        // validate
        XmlOptions validateOptions = new XmlOptions();
        validateOptions.setLoadLineNumbers();
        ArrayList errorList = new ArrayList();
        validateOptions.setErrorListener(errorList);
        boolean isValid = doc.validate(validateOptions);

        if (!isValid)
            throw new Exception("Errors: " + errorList);
    }

    /**
     * [XMLBEANS-66]   NullPointerException when restricting a union with one of the union members
     */
    @Test
    public void test_jira_XmlBeans66() throws Exception {
        String reproXsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                "<xsd:schema targetNamespace=\"http://www.w3.org/2003/12/XQueryX\" \n" +
                "      xmlns=\"http://www.w3.org/2003/12/XQueryX\" \n" +
                "      xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
                "      elementFormDefault=\"qualified\" \n" +
                "      attributeFormDefault=\"qualified\"> \n" +
                "  <!-- Kludge for anySimpleType --> \n" +
                "  <xsd:simpleType name=\"constantValueType\"> \n" +
                "    <xsd:union memberTypes=\"xsd:integer xsd:decimal xsd:string xsd:double\"/> \n" +
                "  </xsd:simpleType> \n" +
                "  <!-- constant expressions. We have 4 different subclasses for this --> \n" +
                "  <xsd:complexType name=\"constantExpr\"> \n" +
                "        <xsd:sequence> \n" +
                "          <xsd:element name=\"value\" type=\"constantValueType\"/> \n" +
                "        </xsd:sequence> \n" +
                "  </xsd:complexType> \n" +
                "  <xsd:complexType name=\"integerConstantExpr\"> \n" +
                "    <xsd:complexContent> \n" +
                "      <xsd:restriction base=\"constantExpr\"> \n" +
                "        <xsd:sequence> \n" +
                "          <xsd:element name=\"value\" type=\"xsd:integer\"/> \n" +
                "        </xsd:sequence> \n" +
                "      </xsd:restriction> \n" +
                "    </xsd:complexContent> \n" +
                "  </xsd:complexType>" +
                "<!-- added for element validation -->" +
                "<xsd:element name=\"Kludge\" type=\"integerConstantExpr\" />\n" +
                "</xsd:schema> ";

        SchemaTypeLoader stl = makeSchemaTypeLoader(new String[]{reproXsd});
        QName reproQName = new QName("http://www.w3.org/2003/12/XQueryX", "Kludge");
        SchemaGlobalElement elVal = stl.findElement(reproQName);
        assertTrue("Element is null or not found", (elVal != null));

        String reproInst = "<Kludge xmlns=\"http://www.w3.org/2003/12/XQueryX\"><value>12</value></Kludge>";
        validateInstance(new String[]{reproXsd}, new String[]{reproInst}, null);
    }

    /**
     * [XMLBEANS-68] GDateBuilder outputs empty string when used without time or timezone
     */
    @Test
    public void test_jira_XmlBeans68() throws Exception {
        Calendar cal = Calendar.getInstance();
        GDateBuilder gdateBuilder = new GDateBuilder(cal);
        gdateBuilder.clearTime();
        gdateBuilder.clearTimeZone();
        GDate gdate = gdateBuilder.toGDate();
        TestDatewTZone xdate = TestDatewTZone.Factory.newInstance();
        xdate.setGDateValue(gdate);
        TestElementWithDateAttributeDocument doc =
                TestElementWithDateAttributeDocument.Factory.newInstance();
        TestElementWithDateAttributeDocument.TestElementWithDateAttribute root =
                doc.addNewTestElementWithDateAttribute();

        root.xsetSomeDate(xdate);
        System.out.println("Doc: " + doc);
        System.out.println("Date: " + xdate.getStringValue());

        if (xdate.getStringValue().compareTo("") == 0 ||
                xdate.getStringValue().length() <= 1)
            throw new Exception("Date without TimeZone should not be empty");
        if (root.getSomeDate().getTimeInMillis() != gdate.getCalendar().getTimeInMillis())
            throw new Exception("Set Dates were not equal");
    }

    /**
     * This issue needed an elementFormDefault=qualified added to the schema
     * [XMLBEANS-71] when trying to retrieve data from a XMLBean with Input from a XML Document, we cannot get any data from the Bean.
     */
    @Test
    public void test_jira_XmlBeans71() throws Exception {
        //schema src lives in cases/xbean/xmlobject/xmlbeans_71.xsd
        abc.BazResponseDocument doc = abc.BazResponseDocument.Factory.parse(JarUtil.getResourceFromJarasFile("xbean/misc/jira/xmlbeans_71.xml"), xmOpts);
        xmOpts.setErrorListener(errorList);
        abc.BazResponseDocument.BazResponse baz = doc.getBazResponse();

        if (!doc.validate(xmOpts))
            System.out.println("DOC-ERRORS: " + errorList + "\n" + doc.xmlText());
        else
            System.out.println("DOC-XML:\n" + doc.xmlText());

        errorList.removeAll(errorList);
        xmOpts.setErrorListener(errorList);

        if (!baz.validate(xmOpts))
            System.out.println("BAZ-ERRORS: " + errorList + "\n" + baz.xmlText());
        //throw new Exception("Response Document did not validate\n"+errorList);
        else
            System.out.println("BAZ-XML:\n" + baz.xmlText());

        if (baz.getStatus().compareTo("SUCCESS") != 0)
            throw new Exception("Status was not loaded properly");
        else
            System.out.println("Sucess was recieved correctly");
    }


    /**
     * [XMLBEANS-72]   Document properties are lost
     */
    @Test
    @Ignore
    public void test_jira_XmlBeans72() throws Exception {
        String docTypeName = "struts-config";
        String docTypePublicID = "-//Apache Software Foundation//DTD Struts Configuration 1.1//EN";
        String docTypeSystemID = "http://jakarta.apache.org/struts/dtds/struts-config_1_1.dtd";
        String fileName = "xmlbeans72.xml";

        //create instance and set doc properties
        PurchaseOrderDocument po = PurchaseOrderDocument.Factory.newInstance();
        org.apache.xmlbeans.XmlDocumentProperties docProps = po.documentProperties();
        docProps.setDoctypeName(docTypeName);
        docProps.setDoctypePublicId(docTypePublicID);
        docProps.setDoctypeSystemId(docTypeSystemID);
        po.addNewPurchaseOrder();
        po.save(new File(fileName));

        //parse saved out file and verify values set above are present
        PurchaseOrderDocument po2 = PurchaseOrderDocument.Factory.parse(new File(fileName));
        //XmlObject po2 = XmlObject.Factory.parse(new File(fileName));

        org.apache.xmlbeans.XmlDocumentProperties doc2Props = po2.documentProperties();

        //verify information using DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));

        DocumentType docType = document.getDoctype();

        //System.out.println("Name: "+ doc2Props.getDoctypeName() +" = " + docType.getName());
        //System.out.println("System: "+ doc2Props.getDoctypeSystemId() + " = " + docType.getSystemId());
        //System.out.println("Public: "+ doc2Props.getDoctypePublicId()+ " = " + docType.getPublicId());

        StringBuffer compareText = new StringBuffer();
        //check values - compare to expected and DOM
        if (doc2Props != null) {
            if (doc2Props.getDoctypeName() == null ||
                    doc2Props.getDoctypeName().compareTo(docTypeName) != 0 ||
                    doc2Props.getDoctypeName().compareTo(docType.getName()) != 0)
                compareText.append("docTypeName was not as " +
                        "expected in the document properties " +
                        doc2Props.getDoctypeName()+"\n");

            if (doc2Props.getDoctypePublicId() == null ||
                    doc2Props.getDoctypePublicId().compareTo(docTypePublicID) != 0 ||
                    doc2Props.getDoctypePublicId().compareTo(docType.getPublicId()) != 0)
                compareText.append("docTypePublicID was not as " +
                        "expected in the document properties " +
                        doc2Props.getDoctypePublicId()+"\n");

            if (doc2Props.getDoctypeSystemId() == null ||
                    doc2Props.getDoctypeSystemId().compareTo(docTypeSystemID) != 0 ||
                    doc2Props.getDoctypeSystemId().compareTo(docType.getSystemId()) != 0)
                compareText.append("docTypeSystemID was not as " +
                        "expected in the document properties "+
                        doc2Props.getDoctypeSystemId()+"\n" );
        } else {
            compareText.append("Document Properties were null, should have been set");
        }

        //cleanup
        po2 = null;
        po = null;
        new File(fileName).deleteOnExit();

        if (compareText.toString().length() > 1)
            throw new Exception("Doc properties were not saved or read correctly\n" + compareText.toString());
    }

    /**
     * BUGBUG: XMLBEANS-78 - NPE when processing XMLStreamReader Midstream
     * XMLBEANS-78 - NPE when processing XMLStreamReader Midstream
     */
    @Test
    public void test_jira_xmlbeans78() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        FileInputStream fis = new FileInputStream(new File(JIRA_CASES+ "xmlbeans_78.xml"));
        XMLStreamReader reader = factory.createXMLStreamReader(fis);
        skipToBody(reader);
        XmlObject o = XmlObject.Factory.parse(reader);
    }

    /**
     * Move reader to element of SOAP Body
     *
     * @param reader
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    private void skipToBody(XMLStreamReader reader) throws javax.xml.stream.XMLStreamException {
        while (true) {
            int event = reader.next();
            switch (event) {
                case XMLStreamReader.END_DOCUMENT:
                    return;
                case XMLStreamReader.START_ELEMENT:
                    if (reader.getLocalName().equals("Body")) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Repro case for jira issue
     * XMLBEANS-80  problems in XPath selecting with namespaces and Predicates.
     */
    @Test
    public void test_jira_xmlbeans80() throws Exception {
        String xpathDoc = "<?xml version=\"1.0\"?> \n" +
                "<doc xmlns:ext=\"http://somebody.elses.extension\"> \n" +
                "  <ext:a test=\"test\" /> \n" +
                "  <b attr1=\"a1\" attr2=\"a2\" \n" +
                "  xmlns:java=\"http://xml.apache.org/xslt/java\"> \n" +
                "    <a> \n" +
                "    </a> \n" +
                "  </b> \n" +
                "</doc> ";
        XmlObject xb80 = XmlObject.Factory.parse(xpathDoc);
        // change $this to '.' to avoid XQuery syntax error for $this not being declared
        //XmlObject[] resSet = xb80.selectPath("declare namespace " +
        //        "ext='http://somebody.elses.extension'; $this//ext:a[@test='test']");

        XmlObject[] resSet = xb80.selectPath("declare namespace " +
                "ext='http://somebody.elses.extension'; .//ext:a[@test='test']");


        assertEquals(1, resSet.length);
        System.out.println("Result was: " + resSet[0].xmlText());
    }

    /**
     * Repro case for jira issue
     * XMLBEANS-81  Cursor selectPath() method not working with predicates
     */
    @Test
    public void test_jira_xmlbeans81() throws Exception {
        String xpathDoc = "<MatchedRecords>" +
                "  <MatchedRecord>" +
                "     <TableName>" +
                "ABC" +
                "</TableName>" +
                "  </MatchedRecord>" +
                "  <MatchedRecord>" +
                "     <TableName>\n" +
                "       BCD \n" +
                "     </TableName> \n" +
                "  </MatchedRecord> \n" +
                "</MatchedRecords> ";
        XmlObject xb81 = XmlObject.Factory.parse(xpathDoc);
        // change $this to '.' to avoid XQuery syntax error for $this not being declared
        //XmlObject[] resSet = xb81.selectPath("$this//MatchedRecord[TableName=\"ABC\"]/TableName");
        XmlObject[] resSet = xb81.selectPath(".//MatchedRecord[TableName=\"ABC\"]/TableName");
        assertEquals(resSet.length , 1);
        XmlCursor cursor = xb81.newCursor();
        //cursor.selectPath("$this//MatchedRecord[TableName=\"ABC\"]/TableName");
        cursor.selectPath(".//MatchedRecord[TableName=\"ABC\"]/TableName");
    }

    /**
     * XMLBeans-84 Cannot run XmlObject.selectPath using Jaxen in multi threaded environment
     */
    @Test
    public void test_jira_XmlBeans84() throws Exception {
        XPathThread[] threads = new XPathThread[15];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new XPathThread();
            System.out.println("Thread[" + i + "]-starting ");
            threads[i].start();
        }

        Thread.sleep(6000);
        System.out.println("Done with XPaths?...");

        for (int i = 0; i < threads.length; i++) {
            Assert.assertNull(threads[i].getException());
        }

    }

    /*
    * [XMLBEANS-88]:Cannot compile eBay schema
    */
    @Ignore
    @Test
    public void test_jira_xmlbeans88() throws Exception {
        List errors = new ArrayList();
        SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();

        params.setUrlFiles(new URL[]{new URL("http://developer.ebay.com/webservices/latest/eBaySvc.wsdl")});
        params.setErrorListener(errors);
        params.setSrcDir(schemaCompSrcDir);
        params.setClassesDir(schemaCompClassesDir);
        params.setDownload(true);

        // ignore unique particle rule in order to compile this schema
        params.setNoUpa(true);

        // runs out of memory..
        params.setMemoryMaximumSize("512m");

        try {
            SchemaCompiler.compile(params);
        } catch (java.lang.OutOfMemoryError ome) {
            System.out.println(ome.getCause());
            System.out.println(ome.getMessage());
            System.out.println(ome.getStackTrace());
            fail("test_jira_xmlbeans88(): Out Of Memory Error");
        } catch (Throwable t) {
            t.getMessage();
            System.out.println("Ok Some Exception is caught here");
        }

        if (printOptionErrMsgs(errors)) {
            fail("test_jira_xmlbeans88() : Errors found when executing scomp");
        }
    }

    /**
    * [XMLBEANS-96]:XmlDocumentProperties missing version and encoding
    */
    @Test
    @Ignore
    public void test_jira_xmlbeans96() throws Exception {
        StringBuffer xmlstringbuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xmlstringbuf.append("<test>");
        xmlstringbuf.append("<testchild attr=\"abcd\"> Jira02 </testchild>");
        xmlstringbuf.append("</test>");

        XmlObject doc = XmlObject.Factory.parse(xmlstringbuf.toString());
        XmlDocumentProperties props = doc.documentProperties();
        assertEquals("test_jira_xmlbeans96() : Xml Version is not picked up", "1.0", props.getVersion());
        assertEquals("test_jira_xmlbeans96() : Xml Encoding is not picked up", "UTF-8", props.getEncoding());

    }

    /**
     * [XMLBEANS-98]   setSaveSuggestedPrefixes doesn't
     * work for QName attribute values
     */
    @Test
    @Ignore
    public void test_jira_xmlbeans98() throws Exception {
        String outfn = outputroot + "xmlbeans_98.xml";
        String structnamespace = "http://www.orthogony.net/xml/sample/structure";
        String datanamespace = "http://www.orthogony.net/xml/sample/data";
        String schemaloc = "xmlbeans_98.xsd";
        String xsinamespace = "http://www.w3.org/2001/XMLSchema-instance";

        File out = new File(outfn);
        XmlOptions options = new XmlOptions();

        // associate namespaces with prefixes
        Map prefixes = new HashMap();
        prefixes.put(structnamespace, "s");
        prefixes.put(datanamespace, "d");
        prefixes.put(xsinamespace, "v");
        options.setSaveSuggestedPrefixes(prefixes);
        options.setSavePrettyPrint();

        // create a sample document
        ARootDocument doc = ARootDocument.Factory.newInstance();
        ARootDocument.ARoot root = doc.addNewARoot();
        ChildType child = root.addNewAChild();
        // This is where the prefix map should take effect
        child.setQualifiedData(new QName(datanamespace, "IAmQualified"));

        // Add a schema location attribute to the doc element
        XmlCursor c = root.newCursor();
        c.toNextToken();
        c.insertAttributeWithValue("schemaLocation", xsinamespace,
                structnamespace + " " + schemaloc);

        //String expXML = doc.xmlText(options.setSavePrettyPrint())
        // save as XML text using the options
        //System.out.println("OUT: \n"+doc.xmlText());
        //doc.save(out, options);
        doc.save(out, options);
        XmlObject xObj = XmlObject.Factory.parse(out);

        String expXText = "<s:a-root v:schemaLocation=\"http://www.orthogony.net/xml/sample/structure xmlbeans_98.xsd\" xmlns:s=\"http://www.orthogony.net/xml/sample/structure\" xmlns:v=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <s:a-child qualified-data=\"data:IAmQualified\" xmlns:data=\"http://www.orthogony.net/xml/sample/data\"/>\n" +
                "</s:a-root>";
        XmlObject txtXObj = XmlObject.Factory.parse(doc.xmlText());
        System.out.println("xObj: " + xObj.xmlText());
        //NamedNodeMap n = xObj.getDomNode().getAttributes();
        //Assert.assertTrue("Length was not as expected", n.getLength() == 3);
        Node no = xObj.getDomNode();//n.getNamedItem("a-root");
        Assert.assertTrue("Expected Prefix was not present: " + no.getPrefix(), no.getPrefix().compareTo("s") == 0);
        //Assert.assertTrue("s prefix was not found " + no.lookupPrefix(structnamespace), no.lookupPrefix(structnamespace).compareTo("s") == 0);
        //Assert.assertTrue("d prefix was not found " + no.lookupPrefix(datanamespace), no.lookupPrefix(datanamespace).compareTo("s") == 0);
        //Assert.assertTrue("v prefix was not found " + no.lookupPrefix(xsinamespace), no.lookupPrefix(xsinamespace).compareTo("s") == 0);


        // throw new Exception(out.getCanonicalPath());

    }

    /**
     * [XMLBEANS-99]   NPE/AssertionFailure in newDomNode()
     */
    @Test
    public void test_jira_xmlbeans99_a() throws Exception {
        //typed verification
        DummyDocument doc = DummyDocument.Factory.parse(new File(JIRA_CASES + "xmlbeans_99.xml"));
        org.w3c.dom.Node node = doc.newDomNode();
        System.out.println("node = " + node);
        //UnTyped Verification
        XmlObject xObj = XmlObject.Factory.parse(new File(JIRA_CASES +
                "xmlbeans_99.xml"));
        org.w3c.dom.Node xNode = xObj.newDomNode();
        System.out.println("xNode: " + xNode);
    }

    /*
    * [XMLBEANS-99]: NPE/AssertionFailure in newDomNode()
    * refer to [XMLBEANS-14]
    */
    @Test
    public void test_jira_xmlbeans99_b() throws Exception {
        StringBuffer xmlstringbuf = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n");
        xmlstringbuf.append("                <x:dummy xmlns:x=\"http://dufourrault\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\" xsi:SchemaLocation=\"dummy.xsd\">\n");
        xmlstringbuf.append("                    <x:father>\n");
        xmlstringbuf.append("                     <x:son>toto</x:son> \n");
        xmlstringbuf.append("                    </x:father>\n");
        xmlstringbuf.append("              </x:dummy>");

        try {
            //From empty instance
            DummyDocument newDoc = DummyDocument.Factory.newInstance();
            DummyDocument.Dummy newDummy = newDoc.addNewDummy();
            Node newNode = newDummy.newDomNode();
            System.out.println("New Node = " + newNode);

            //set Item
            DummyDocument new2Doc = DummyDocument.Factory.newInstance();
            DummyDocument.Dummy new2Dummy = new2Doc.addNewDummy();
            Father newFather= Father.Factory.newInstance();
            newFather.setSon("son");
            new2Dummy.setFather(newFather);
            Node new2Node = new2Dummy.newDomNode();
            System.out.println("SetFather Node = " + new2Node);

            //With Loaded instance Document
            DummyDocument doc = DummyDocument.Factory.parse(xmlstringbuf.toString());
            Node node = doc.newDomNode();
            System.out.println("node = " + node);
            //Just Element Type Node
            dufourrault.DummyDocument.Dummy dummy = doc.addNewDummy();
            Node typeNode = dummy.newDomNode();
            System.out.println("TypeNode = "+typeNode);

            dufourrault.Father fatherType = Father.Factory.newInstance();
            fatherType.setSon("son");
            Node fatherTypeNode = fatherType.newDomNode();
            System.out.println("New Father Type Node: "+ fatherTypeNode);

        } catch (NullPointerException npe) {
            fail("test_jira_xmlbeans99() : Null Pointer Exception when create Dom Node");
        } catch (Exception e) {
            fail("test_jira_xmlbeans99() : Exception when create Dom Node");
        }
    }



    /**
     * For Testing jira issue 84
     */
    public static class XPathThread extends TestThread
    {
        public XPathThread()
        {
            super();
        }

        public void run()
        {

            try {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    switch (i % 2) {
                        case 0:
                            runStatusXPath();
                            break;
                        case 1:
                            runDocXPath();
                            break;
                        default:
                            System.out.println("Val: " + i);
                            break;
                    }

                }
                _result = true;

            } catch (Throwable t) {
                _throwable = t;
                t.printStackTrace();
            }
        }

        public void runStatusXPath()
        {
            try {
                System.out.println("Testing Status");
                String statusDoc = "<statusreport xmlns=\"http://openuri.org/enumtest\">\n" +
                        "  <status name=\"first\" target=\"all\">all</status>\n" +
                        "  <status name=\"second\" target=\"all\">few</status>\n" +
                        "  <status name=\"third\" target=\"none\">most</status>\n" +
                        "  <status name=\"first\" target=\"none\">none</status>\n" +
                        "</statusreport>";
                XmlObject path = XmlObject.Factory.parse(statusDoc, xm);
                XmlObject[] resSet = path.selectPath("//*:status");
                Assert.assertTrue(resSet.length + "", resSet.length == 4);
                resSet = path.selectPath("//*:status[@name='first']");
                Assert.assertTrue(resSet.length == 2);

            } catch (Throwable t) {
                _throwable = t;
                t.printStackTrace();
            }
        }

        public void runDocXPath()
        {
            try {
                System.out.println("Testing Doc");
                String docDoc = "<?xml version=\"1.0\"?>\n" +
                        "<doc xmlns:ext=\"http://somebody.elses.extension\">\n" +
                        "  <a test=\"test\" />\n" +
                        "  <b attr1=\"a1\" attr2=\"a2\"   \n" +
                        "  xmlns:java=\"http://xml.apache.org/xslt/java\">\n" +
                        "    <a>\n" +
                        "    </a> \n" +
                        "  </b>\n" +
                        "</doc><!-- -->  ";
                XmlObject path = XmlObject.Factory.parse(docDoc, xm);
                XmlObject[] resSet = path.selectPath("//a");
                Assert.assertTrue(resSet.length == 2);
                resSet = path.selectPath("//b[@attr2]");
                Assert.assertTrue(resSet.length == 1);

            } catch (Throwable t) {
                _throwable = t;
                t.printStackTrace();
            }
        }
    }
}

