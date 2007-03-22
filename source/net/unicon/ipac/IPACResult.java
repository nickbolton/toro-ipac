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
