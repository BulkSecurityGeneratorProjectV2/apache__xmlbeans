/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache
*    XMLBeans", nor may "Apache" appear in their name, without prior
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2000-2003 BEA Systems
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/

package org.apache.xmlbeans.impl.marshal;

import org.apache.xmlbeans.UnmarshalContext;
import org.apache.xmlbeans.Unmarshaller;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.binding.bts.BindingLoader;
import org.apache.xmlbeans.impl.binding.bts.BindingType;
import org.apache.xmlbeans.impl.binding.bts.BindingTypeName;
import org.apache.xmlbeans.impl.binding.bts.JavaName;
import org.apache.xmlbeans.impl.binding.bts.SimpleDocumentBinding;
import org.apache.xmlbeans.impl.binding.bts.XmlName;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A Unmarshaller knows how to convert xml to java objects.
 */
class UnmarshallerImpl
    implements Unmarshaller
{
    private final BindingLoader bindingLoader;
    private final RuntimeBindingTypeTable typeTable;


    /*package*/
    UnmarshallerImpl(BindingLoader bindingLoader,
                     RuntimeBindingTypeTable typeTable)
    {
        this.bindingLoader = bindingLoader;
        this.typeTable = typeTable;
    }

    public Object unmarshal(XMLStreamReader reader)
        throws XmlException
    {
        UnmarshalContextImpl context = createUnmarshallContext(reader,
                                                               new ArrayList());

        advanceToFirstItemOfInterest(reader);

        final BindingType bindingType = determineRootType(context);

        return unmarshalBindingType(bindingType, context);
    }

    private Object unmarshalBindingType(final BindingType bindingType,
                                        UnmarshalContext context)
        throws XmlException
    {
        TypeUnmarshaller um =
            typeTable.getTypeUnmarshaller(bindingType);

        if (um == null) {
            throw new XmlException("failed to lookup unmarshaller for " + bindingType);
        }

        final UnmarshalContextImpl our_context = (UnmarshalContextImpl)context;
        if (!our_context.hasXmlStream()) {
            throw new XmlException("UnmarshalContext must have a" +
                                   " non null XMLStreamReader");
        }
        Object type_instance = um.unmarshal(our_context);

        final Collection errors = our_context.getErrorCollection();
        if (!errors.isEmpty()) {
            //TODO: review this ctor
            System.out.println("errors = " + errors);
            String msg = errorsToMsg(errors);
            throw new XmlException(msg, null,
                                   Collections.unmodifiableCollection(errors));
        }

        return type_instance;
    }

    public Object unmarshallType(QName schemaType,
                                 String javaType,
                                 UnmarshalContext context)
        throws XmlException
    {
        BindingType btype = determineBindingType(schemaType, javaType);
        if (btype == null) {
            final String msg = "unable to find binding type for " +
                schemaType + " : " + javaType;
            throw new XmlException(msg);
        }
        return unmarshalBindingType(btype, context);
    }

    private BindingType determineBindingType(QName schemaType, String javaType)
    {
        XmlName xname = XmlName.forTypeNamed(schemaType);
        JavaName jname = JavaName.forString(javaType);
        BindingTypeName btname = BindingTypeName.forPair(jname, xname);
        return bindingLoader.getBindingType(btname);
    }

    private static String errorsToMsg(final Collection errors)
    {
        final int sz = errors.size();
        assert sz > 0;

        if (sz == 1) {
            return "unmarshalling error: " + errors.iterator().next();
        } else {
            return "unmarshalling errors (" + sz + ")";
        }
    }

    private void advanceToFirstItemOfInterest(XMLStreamReader rdr)
        throws XmlException
    {
        try {
            for (int state = rdr.getEventType(); rdr.hasNext(); state = rdr.next()) {
                switch (state) {

                    //these are things we can handle...
                    case XMLStreamReader.START_ELEMENT:
                        return;

                        //eventually we'll handle these...
                    case XMLStreamReader.ATTRIBUTE:
                    case XMLStreamReader.CHARACTERS:
                        throw new AssertionError("UNIMPLEMENTED TYPE: " + state);

                        //bad news in the xml stream
                    case XMLStreamReader.END_DOCUMENT:
                        throw new XmlException("unexpected end of XML");
                    case XMLStreamReader.END_ELEMENT:
                        throw new XmlException("unexpected end of XML");

                        //skip these and keep going
                    case XMLStreamReader.PROCESSING_INSTRUCTION:
                    case XMLStreamReader.COMMENT:
                    case XMLStreamReader.START_DOCUMENT:
                    case XMLStreamReader.SPACE:
                        break;

                    default:
                        //this case pretty much means malformed xml or a bug
                        throw new XmlException("unexpected xml state:" + state +
                                               "in" + rdr);
                }
            }
            throw new XmlException("unexpected end of xml stream");
        }
        catch (XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    private BindingType determineRootType(UnmarshalContextImpl context)
        throws XmlException
    {
        QName xsi_type = context.getXsiType();

        if (xsi_type == null) {
            QName root_elem_qname = new QName(context.getNamespaceURI(),
                                              context.getLocalName());
            final XmlName type_name = XmlName.forGlobalName(XmlName.ELEMENT, root_elem_qname);
            final BindingType doc_binding_type = getPojoBindingType(type_name);
            SimpleDocumentBinding sd = (SimpleDocumentBinding)doc_binding_type;
            return getPojoBindingType(sd.getTypeOfElement());
        } else {
            final XmlName type_name = XmlName.forTypeNamed(xsi_type);
            final BindingType pojoBindingType = getPojoBindingType(type_name);
            assert !(pojoBindingType instanceof SimpleDocumentBinding);
            return pojoBindingType;
        }
    }

    private BindingType getPojoBindingType(final XmlName type_name) throws XmlException
    {
        final BindingTypeName btName = bindingLoader.lookupPojoFor(type_name);
        if (btName == null) {
            throw new XmlException("failed to load BindingTypeName for " + type_name);
        }

        BindingType bt = bindingLoader.getBindingType(btName);

        if (bt == null) {
            throw new XmlException("failed to load BindingType for " + btName);
        }

        return bt;
    }


    private UnmarshalContextImpl createUnmarshallContext(XMLStreamReader reader,
                                                         Collection errors)
    {
        return new UnmarshalContextImpl(reader, bindingLoader, typeTable, errors);
    }


}
