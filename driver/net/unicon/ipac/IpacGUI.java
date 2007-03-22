/*
 *******************************************************************************
 *
 * File:       IpacGUI.java
 *
 * Copyright:  ©2003 Unicon, Inc. All Rights Reserved
 *
 * This source code is the confidential and proprietary information of Unicon.
 * No part of this work may be modified or used without the prior written
 * consent of Unicon.
 *
 *******************************************************************************
 */
package net.unicon.ipac;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;

import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import java.util.jar.*;
import java.util.zip.*;

public class IpacGUI extends JFrame implements ActionListener {
    public JButton fileButton;
    public JButton testButton;
    public JButton clearButton;
    public JButton saveButton;

    public JComboBox protocolCombo;
    public JComboBox portCombo;
    public JComboBox numExecCombo;

    public JTextField fileText;
    public JTextField serverAddrText;

    public JLabel protocolLabel;
    public JLabel serverAddrLabel;
    public JLabel portLabel;
    public JLabel numExecLabel;
    public JLabel fileLabel;

    public JTextArea textArea;
    public JScrollPane scrollPane;

    public JPanel topPanel;
    public JPanel middlePanel;
    public JPanel bottomPanel;

    private int windowWidth  = 1100;
    private int windowHeight = 700;
    private int columnWidth  = 40;

    private static final String APP_PATH = "/ipac";

    public IpacGUI() throws Exception {
        this.setTitle("IPAC GUI");
        this.setSize(windowWidth, windowHeight);
        this.setResizable(false);

        FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 5, 10);
        getContentPane().setLayout(flow);

        // Panels
        topPanel   = new JPanel();
        middlePanel = new JPanel();
        bottomPanel = new JPanel();

        // Labels
        protocolLabel   = new JLabel("   Protocol:");
        serverAddrLabel = new JLabel("   Server:");
        portLabel       = new JLabel("   Port:");
        numExecLabel    = new JLabel("   Repeat test this many times:");
        fileLabel       = new JLabel("   Input File (*.jar, *.xml):");

        // Text Fields
        fileText       = new JTextField(15);
        serverAddrText = new JTextField(15);
        serverAddrText.setText("localhost");

        // Combo Boxes
        protocolCombo = new JComboBox();
        protocolCombo.setEditable(false);
        protocolCombo.addItem("http://");
        protocolCombo.addItem("https://");
        protocolCombo.addActionListener(this);

        portCombo = new JComboBox();
        portCombo.setEditable(true);
        portCombo.addItem("8080");
        portCombo.addItem("80");
        portCombo.addActionListener(this);

        numExecCombo = new JComboBox();
        numExecCombo.setEditable(false);
        numExecCombo.addItem("1");
        numExecCombo.addItem("5");
        numExecCombo.addItem("10");
        numExecCombo.addItem("15");
        numExecCombo.addItem("20");
        numExecCombo.addItem("25");
        numExecCombo.addItem("50");
        numExecCombo.addItem("100");
        numExecCombo.addItem("150");
        numExecCombo.addItem("200");
        numExecCombo.addItem("250");
        numExecCombo.addItem("500");
        numExecCombo.addItem("1000");
        numExecCombo.addActionListener(this);

        // Text Area
        textArea = new JTextArea(35, 90);
        textArea.setLineWrap(true);
        scrollPane = new JScrollPane(textArea);
        middlePanel.add(scrollPane, "South");

        // Layout Code
        topPanel.add(protocolLabel);
        topPanel.add(protocolCombo);

        topPanel.add(serverAddrLabel);
        topPanel.add(serverAddrText);

        topPanel.add(portLabel);
        topPanel.add(portCombo);

        topPanel.add(fileLabel);
        topPanel.add(fileText);

        fileButton = new JButton("Browse");
        fileButton.addActionListener(this);
        topPanel.add(fileButton);

        testButton = new JButton("Test");
        testButton.addActionListener(this);

        clearButton = new JButton("Clear Screen");
        clearButton.addActionListener(this);

        saveButton = new JButton("Save Results");
        saveButton.addActionListener(this);

        bottomPanel.add(numExecLabel, "Center");
        bottomPanel.add(numExecCombo, "Center");
        bottomPanel.add(testButton, "Center");
        bottomPanel.add(clearButton, "Center");
        bottomPanel.add(saveButton, "Center");

        getContentPane().add(topPanel, "North");
        getContentPane().add(middlePanel, "Center");
        getContentPane().add(bottomPanel, "South");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(Window e) {
                System.exit(0);
            }
        });

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dem = tk.getScreenSize();
        int screenHeight = dem.height;
        int screenWidth  = dem.width;
        setLocation((screenWidth - windowWidth )/2,
                    (screenHeight - windowHeight)/2);

    } // default constructor

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        // Perform the test
        if(source == testButton) {
            StringBuffer resultBuffer = new StringBuffer();

            String protocolString = 
                ((String)protocolCombo.getSelectedItem()).trim();

            String serverAddrString = serverAddrText.getText().trim();

            String portString = 
                ((String)portCombo.getSelectedItem()).trim();

            int numExecs = Integer.parseInt(
                (String)numExecCombo.getSelectedItem());

            String fileString = fileText.getText().trim();

            if(serverAddrString.length() > 0 
                && protocolString.length() > 0
                && portString.length() > 0
                && fileString.length() > 0) {

                try {
                    for (int i = 0; i < numExecs; i++) { 
                        this.process(
                            protocolString, 
                            serverAddrString, 
                            portString,
                            fileString,
                            resultBuffer);
                    } // end for loop
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    resultBuffer.append("\n\nError: " + e.getMessage() + "\n");
                }

            } else {
                resultBuffer.append("\nPlease complete all fields!");
            }

            // add results to textarea.
            textArea.append(resultBuffer.toString());

            return;
        }
        // Clear the textarea of the test results
        if(source == clearButton) {
            textArea.setText("");
            return;
        }
        // Get the XML file to perform test against 
        if(source == fileButton) {
            JFileChooser chooser = new JFileChooser();

            XmlArchiveFileFilter filter = new XmlArchiveFileFilter();
            chooser.setFileFilter(filter);

            int selected = chooser.showOpenDialog(getContentPane());

            if (selected == JFileChooser.APPROVE_OPTION) {
                File xmlFile = chooser.getSelectedFile();
                String xmlPath = xmlFile.getAbsolutePath();
                fileText.setText(xmlPath);
                return;
            } else if (selected == JFileChooser.CANCEL_OPTION) {
                return;
            }

        }
        // Save results of test to file 
        if(source == saveButton) {
            JFileChooser chooser = new JFileChooser();

            TextFileFilter filter = new TextFileFilter();
            chooser.setFileFilter(filter);

            int selected = chooser.showOpenDialog(getContentPane());

            if (selected == JFileChooser.APPROVE_OPTION) {
                File textFile    = chooser.getSelectedFile();
                String savePath  = textFile.getAbsolutePath();
                String resultMsg = savePath + " saved successfully!";
                int messageType  = JOptionPane.INFORMATION_MESSAGE;
                int optionType   = JOptionPane.DEFAULT_OPTION;
                Object[] options = { "OK" };

                try {
                    this.writeFile(savePath, textArea.getText());
                } catch (Exception e) { 
                    resultMsg   = "Unable to save " + savePath;
                    messageType = JOptionPane.ERROR_MESSAGE;
                    e.printStackTrace(); 
                }

                // Show message for save results
                JOptionPane pane = new JOptionPane(
                                        resultMsg,
                                        messageType,
                                        optionType,
                                        null,
                                        options);

                JDialog dialog = pane.createDialog(
                                        this.getContentPane(),
                                        "File Save Results");

                dialog.show();

                return;
            } else if (selected == JFileChooser.CANCEL_OPTION) {
                return;
            }

        } // end if

    } // end actionPerformed

    private void process(
                        String protocol,
                        String server, 
                        String port,
                        String filePath, 
                        StringBuffer resultBuffer)
    throws Exception {

        /* ASSERTIONS */
        // check protocol 
        if (!(protocol != null && protocol.trim().length() > 0)) {
            throw new Exception("Protocol must be provided");
        }
        // check server 
        if (!(server != null && server.trim().length() > 0)) {
            throw new Exception("Server must be provided");
        }
        // check port 
        if (!(port != null && port.trim().length() > 0)) {
            throw new Exception("Port must be provided");
        }
        // check filePath 
        if (!(filePath != null && filePath.trim().length() > 0)) {
            throw new Exception("Input file must be provided");
        }

        StringBuffer postURL = new StringBuffer(protocol);
        postURL.append(server).append(":").append(port).append(APP_PATH);

        if (filePath.toLowerCase().endsWith(".jar")) {
            // input is an archive of XML files
            JarFile jarFile = null;

            try {
                jarFile = new JarFile(filePath);
                String[] entryNames = this.listEntryNames(jarFile); 

                
                for (int i = 0; i < entryNames.length; i++) {
                    String entryName = entryNames[i];

                    if (entryName.toLowerCase().endsWith(".xml")) {
                        resultBuffer.append(
                            "\n\nBEGIN Post to IPAC Servlet...\n");
                        resultBuffer.append("\nIPAC Response Message:\n");

                        String xmlString = 
                            this.getXmlContentFromEntry(jarFile, entryName);

                        // do the post and get the response message
                        long startTime = System.currentTimeMillis();
                        String resultString = null; 

                        if (protocol.equalsIgnoreCase("https://")) {
                            resultString = IPACServletDriver.securePost(
                                xmlString, postURL.toString());
                        } else if (protocol.equalsIgnoreCase("http://")) {
                            resultString = IPACServletDriver.post(
                                xmlString, postURL.toString());
                        }

                        long endTime = System.currentTimeMillis();
                        resultBuffer.append(resultString).append("\n");

                        // get process time in millis
                        resultBuffer.append("\nProcess Time in Milliseconds: ");
                        resultBuffer.append((endTime-startTime)).append("\n");

                        resultBuffer.append("\nEND Post to IPAC Servlet!\n");
                    }// end if

                } // end for loop

            } finally {
                try {
                    if (jarFile != null) {
                        jarFile.close();
                    }
                } catch (Exception e) {}
            } // end try block

        } else {
            // input is a single XML file
            resultBuffer.append("\n\nBEGIN Post to IPAC Servlet...\n");
            resultBuffer.append("\nIPAC Response Message:\n");

            String xmlString = this.getXmlContent(filePath);

            // do the post and get the response message
            long startTime = System.currentTimeMillis();
            String resultString = null; 

            if (protocol.equalsIgnoreCase("https://")) {
                resultString = IPACServletDriver.securePost(
                    xmlString, postURL.toString());
            } else if (protocol.equalsIgnoreCase("http://")) {
                resultString = IPACServletDriver.post(
                    xmlString, postURL.toString());
            }

            long endTime = System.currentTimeMillis();
            resultBuffer.append(resultString).append("\n");

            // get process time in millis
            resultBuffer.append("\nProcess Time in Milliseconds: ");
            resultBuffer.append((endTime-startTime)).append("\n");

            resultBuffer.append("\nEND Post to IPAC Servlet!\n");
        } // end if


    } // end process

    /* Persists test results to the file system */
    private void writeFile(String filePath, String contents) throws Exception {
        ByteArrayInputStream stream = null; 
        FileOutputStream fout       = null; 

        try {
            // write out the results content file 
            File file = new File(filePath);
            file.delete();

            int numRead = 0;
            byte[] b = new byte[4096];
            fout = new FileOutputStream(file);
            stream = new ByteArrayInputStream(contents.getBytes());

            while ((numRead = stream.read(b, 0, 4096)) != -1) {
                fout.write(b, 0, numRead);
            }

        } finally {

            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {}
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {}
            }

        } // end try block

    } // end writeFile

    private String getXmlContent(String xmlPath) throws Exception {
        // read in the XML file contents
        BufferedReader reader = null;
        try {
            StringBuffer buff = new StringBuffer();
            reader = new BufferedReader(
                new FileReader(new File(xmlPath)) );
            int numRead;
            char[] b = new char[4096];

            while ((numRead = reader.read(b, 0, 4096)) != -1) {
                buff.append(b, 0, numRead);
            }

            return buff.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {}
            }
        }
    } // end getXmlContent

    private String[] listEntryNames(JarFile jarFile) throws Exception {
        List entryNames = new ArrayList();
        ZipEntry entry = null;

        for (Enumeration e = jarFile.entries(); e.hasMoreElements();) {
            entry = (ZipEntry)e.nextElement();
            entryNames.add(entry.getName());
        }

        String[] results = (String[])entryNames.toArray(new String[0]);
        
        return results;
    } // end listEntryNames

    private String getXmlContentFromEntry(JarFile jarFile, String entryName)
    throws Exception {
        // if it isn't XML, then forget about it
        if (!entryName.toLowerCase().endsWith(".xml")) return null;

        BufferedInputStream bis  = null;
        try { 
            JarEntry entry = jarFile.getJarEntry(entryName);
            InputStream is = jarFile.getInputStream(entry);
            bis = new BufferedInputStream(is);

            int numRead = 0;
            byte[] b = new byte[4096];
            ByteArrayOutputStream out = 
                new ByteArrayOutputStream(4096);

            while ((numRead = bis.read(b, 0, 4096)) != -1) {
                out.write(b, 0, numRead);
            }

            String result = out.toString();

            return result;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {}
            }
        }
    } // end getXmlContentFromEntry 

    /**
     * A convenience implementation of FileFilter that filters out
     * all files except for xml and jar file extensions.
     */
    class XmlArchiveFileFilter extends javax.swing.filechooser.FileFilter {

        /** default constructor */
        public XmlArchiveFileFilter() {}
        /**
         * Return true if this file should be shown in the directory pane,
         * false if it shouldn't.
         */
        public boolean accept(File f) {
            if(f != null) {

                if(f.isDirectory()) {
                    return true;
                }

                String extension = getExtension(f);

                if(extension != null 
                        && ("xml".equalsIgnoreCase(extension)
                        || "jar".equalsIgnoreCase(extension))) {

                    return true;
                }

            } // end outer if

            return false;
        } // end accept

        /**
         * Return the extension portion of the file's name.
         */
        public String getExtension(File f) {
            if (f != null) {
                String filename = f.getName();
                int i = filename.lastIndexOf('.');

                if (i > 0 && i < filename.length()-1) {
                    return filename.substring(i+1).toLowerCase();
                }
            }

            return null;
        } // end getExtension

        /**
         * Returns the human readable description of this filter. For
         * example: "XML and Archive Files (*.xml, *.jar)";
         */
        public String getDescription() {
            return "XML and Archive Files (*.xml, *.jar)";
        }

    } // end XmlArchiveFileFilter class

    /**
     * A convenience implementation of FileFilter that filters out
     * all files except txt file extensions.
     */
    class TextFileFilter extends javax.swing.filechooser.FileFilter {

        /** default constructor */
        public TextFileFilter() {}
        /**
         * Return true if this file should be shown in the directory pane,
         * false if it shouldn't.
         */
        public boolean accept(File f) {
            if(f != null) {

                if(f.isDirectory()) {
                    return true;
                }

                String extension = getExtension(f);

                if(extension != null && "txt".equalsIgnoreCase(extension)) {
                    return true;
                }

            } // end outer if

            return false;
        } // end accept

        /**
         * Return the extension portion of the file's name.
         */
        public String getExtension(File f) {
            if (f != null) {
                String filename = f.getName();
                int i = filename.lastIndexOf('.');

                if (i > 0 && i < filename.length()-1) {
                    return filename.substring(i+1).toLowerCase();
                }
            }

            return null;
        } // end getExtension

        /**
         * Returns the human readable description of this filter. For
         * example: "Text Files (*.txt)"
         */
        public String getDescription() {
            return "Text Files (*.txt)";
        }

    } // end TextFileFilter class

    public static void main (String[] args) {

        try {
            JFrame f = new IpacGUI();
            f.show();

            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.addWindowListener(new WindowAdapter() {
                public void windowClosing(Window e) {
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    } // end main

} // end IpacGUI
