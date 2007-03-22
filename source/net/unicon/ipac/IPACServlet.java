/*
 * Copyright (C) 2007 Unicon, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this distribution.  It is also available here:
 * http://www.fsf.org/licensing/licenses/gpl.html
 *
 * As a special exception to the terms and conditions of version 
 * 2 of the GPL, you may redistribute this Program in connection 
 * with Free/Libre and Open Source Software ("FLOSS") applications 
 * as described in the GPL FLOSS exception.  You should have received
 * a copy of the text describing the FLOSS exception along with this
 * distribution.
 */

package net.unicon.ipac;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;

import net.unicon.sdk.FactoryCreateException;
import net.unicon.sdk.log.*;
import net.unicon.sdk.properties.*;
import net.unicon.sdk.api.ApiException;
import net.unicon.sdk.api.IApiController;
import net.unicon.sdk.api.IImportResult;
import net.unicon.sdk.transformation.*;
import net.unicon.sdk.transformation.input.TextTransformInput;

public class IPACServlet extends HttpServlet {

    private static final String TOPIC_NAME_PARAM     = "topicName";
    private static final String AUTHORIZATION_PARAM  = "Authorization";
    private static final String API_CONTROLLER_CLASS_NAME =
        "net.unicon.academus.api.AcademusApiController";

    private ILogService log   = null;
    private String doctypeURI = null;

    public void init() throws ServletException {
        try {
            // Get log service.
            log = LogServiceFactory.instance();

            // Get doctype URI for validation.
            doctypeURI =
                UniconPropertiesFactory.getManager(
                    CommonPropertiesType.SERVICE).getProperty(
                    "net.unicon.sdk.transformation.ITransformInput.defaultInputType");
                    //    "net.unicon.ipac.validation.doctypeURI");

        } catch (Throwable t) {
            throw new ServletException(t);
        }
    }

    public void doGet (HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        // logging the start of the transaction
        log.log(LogLevel.INFO, "\nSTART RECIEVING DATA");

        this.getHost(request);
        this.getContentType(request);
        this.getContentLength(request);

        // Authenticate User
        //
        String data     = this.getXML(request);
        IPACResult results  = this.processData(data);
        PrintWriter out = response.getWriter ();
        out.print(results.toXML());

        /* fake code
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = request.getParameter(name);
            out.println(name + " = " + value);
        }
        /* end fake code*/
        out.close();

        // logging the end of the transaction
        log.log(LogLevel.INFO, "\nFINISHED RECIEVING DATA\n");
    }

    public String getHost(HttpServletRequest request)
    throws IOException, ServletException {
        String host = request.getRemoteHost();
        int port    = request.getServerPort();

        // INFO logging
        log.log(LogLevel.INFO, "Host: " + host);
        log.log(LogLevel.INFO, "Port: " + port);
        return host;
    }

    public String getContentType(HttpServletRequest request)
    throws IOException, ServletException {
        String contentType = request.getContentType();
        log.log(LogLevel.INFO, "Content-Type: " + contentType);
        return contentType;
    }

    public int getContentLength(HttpServletRequest request)
    throws IOException, ServletException {
        int contentLength = request.getContentLength();
        log.log(LogLevel.INFO, "Content-Length: " + contentLength);
        return contentLength;
    }

    public String getTopicName(HttpServletRequest request)
    throws IOException, ServletException {
        String topicName = request.getHeader(TOPIC_NAME_PARAM);
        log.log(LogLevel.INFO, "Topic-Name: " + topicName);
        return topicName;
    }

    public String getAuthorization(HttpServletRequest request)
    throws IOException, ServletException {
        String userInfo = request.getHeader(AUTHORIZATION_PARAM);
        return userInfo;
    }

    public String getXML(HttpServletRequest request)
    throws IOException, ServletException {
        BufferedReader br = request.getReader();

        StringBuffer results = new StringBuffer();
        String oneline;
        while ( (oneline = br.readLine()) != null) {
            results.append(oneline);
        }
        br.close();

        // Debug logging configurable in unicon-logger.properties
        log.log(LogLevel.DEBUG, "XML-Body :");
        log.log(LogLevel.DEBUG, results.toString());
        return results.toString();
    }

    public String validateXML(String xml) throws DocumentValidationException {
        // check xml first
        if (!(xml != null && xml.trim().length() > 0)) {
            throw new IllegalArgumentException(
                "Argument 'xml [String]' must be provided.");
        }

        StringBuffer doctypeDecl = new StringBuffer("");
        doctypeDecl.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        doctypeDecl.append("<!DOCTYPE enterprise SYSTEM ");
        doctypeDecl.append("\"").append(doctypeURI).append("\"");
        doctypeDecl.append(">\n");

        String regex = "<\\?xml *version *= *\"1.0\" *[^?]*\\?>";
        xml = xml.replaceFirst(regex, doctypeDecl.toString());

        log.log(LogLevel.DEBUG, xml);
        try {
            // Validate the xml.
            XMLReader xmlReader =
                SAXParserFactory.newInstance().
                    newSAXParser().getXMLReader();

            xmlReader.setErrorHandler(new ValidationErrorHandler());
            xmlReader.setFeature(
                "http://xml.org/sax/features/namespaces", true);
            xmlReader.setFeature(
                "http://xml.org/sax/features/validation", true);

            StringReader strReader = new StringReader(xml);
            InputSource is = new InputSource(strReader);

            // If xml parsing fails, an exception will be thrown.
            xmlReader.parse(is);
        } catch (Throwable t) {
            throw new DocumentValidationException(
                "Error occured validating XML document.\n"+t.getMessage(), t);
        }
        return xml;
    } // end validateXML

    public IPACResult processData(String xml) {
        IPACResult results = null;
        try {
            xml     = this.transformData(validateXML(xml));
            results = this.importData(xml);
        } catch (IllegalArgumentException iae) {
            results = new IPACResult(iae.getMessage(), false);
            results.setException(iae);
            results.setErrorID("ipac-IAE");
        } catch (TransformException te) {
            results = new IPACResult(te.getMessage(), false);
            results.setException(te);
            results.setErrorID("ipac-TE");
        } catch (DocumentValidationException dve) {
            results = new IPACResult(dve.getMessage(), false);
            results.setException(dve);
            results.setErrorID("ipac-DVE");
        } catch (DocumentDeclarationException dde) {
            results = new IPACResult(dde.getMessage(), false);
            results.setException(dde);
            results.setErrorID("ipac-DTD");
        } catch (FactoryCreateException fce) {
            fce.printStackTrace();
            results = new IPACResult("Transformation Failed", false);
            results.setException(fce);
            results.setErrorID("ipac-FCE");
        }
        // Logging INFO of results
        log.log(LogLevel.INFO, results.getMessage());
        return results;
    }

    public String transformData(String data)
    throws IllegalArgumentException, TransformException,
    FactoryCreateException, DocumentDeclarationException {

        String result = null;

        ITransformInput transformInput =
                new TextTransformInput(data);

        ITransformationService transformService =
                TransformationServiceFactory.getService(
                    "/properties/TransformMappings.xml");

        TransformResult transformResult =
                transformService.transform(transformInput);

        result = transformResult.getContent();
        return result;
    }

    public IPACResult importData(String xml) {
        IPACResult status = null;
        try {
            DocumentBuilder builder
                = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document
                = builder.parse(new InputSource(new StringReader(xml)));

            // Building API Controller
            IApiController controller =  (IApiController)
                Class.forName(API_CONTROLLER_CLASS_NAME).newInstance();
            IImportResult rslt = controller.doImport(document);
            status = new IPACResult(rslt.getMessage(), rslt.isSuccessful());
            if(!rslt.isSuccessful()) {
                status.setErrorID("ipac-IMP");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            status = new IPACResult(IMPORT_ERR, false);
            status.setErrorID("ipac-IMP");
        }
        return status;
    }

    private static final String IMPORT_ERR =
        "Unable to import.  Import Component Failure. Unable to connect to Academus.";
}
