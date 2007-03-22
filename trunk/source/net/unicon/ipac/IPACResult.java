/*
 *******************************************************************************
 *
 * File:       IPACResult.java
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

class IPACResult {

    private boolean success;
    private String errorID;
    private String message;
    private Exception e;

    public IPACResult(String message, boolean success) {
        this.success = success;
        this.message = message;
        this.errorID = "NA";
    }

    public void setException(Exception e) {
        this.e = e;
    }

    public void setErrorID (String id) {
        this.errorID = id;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorID() {
        return errorID;
    }

    public Exception getException() {
        return e;
    }

    public String toXML() {
        StringBuffer rtnResponse = new StringBuffer();
        rtnResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        rtnResponse.append("<RESPONSE>\n");
        rtnResponse.append("<STATUS>");
        rtnResponse.append(success ? "Success" : "Error");
        rtnResponse.append("</STATUS>\n");
        if(!success) {
            rtnResponse.append("<ERRORLIST>\n");
            rtnResponse.append("<ERROR ERRORNUM=\"").append(errorID).append("\">");
            rtnResponse.append(message);
            rtnResponse.append("</ERROR>\n");
            rtnResponse.append("</ERRORLIST>\n");
        }
        rtnResponse.append("</RESPONSE>");

        return rtnResponse.toString();
    }
}
