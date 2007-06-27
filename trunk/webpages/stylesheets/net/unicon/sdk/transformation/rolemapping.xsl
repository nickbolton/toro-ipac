<?xml version="1.0"?>
<!-- 
    FILE:     roleMap.xsl
    AUTHOR: Shawn Lonas
    DATE:     9/3/03
    OWNER:     UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method = "xml"  version="1.0" indent="yes"  />


<xsl:template name="mapRole">
    <xsl:param name="inputRole" />

    <xsl:choose>
        <!-- System Roles -->
        <xsl:when test="$inputRole = 'SysAdmin'">Administrator</xsl:when>
        <xsl:when test="$inputRole = 'SysSupport'">Facilitator</xsl:when>
        <xsl:when test="$inputRole = 'Creator'">Administrator</xsl:when>
        <xsl:when test="$inputRole = 'User'">User</xsl:when>
        <xsl:when test="$inputRole = 'None'">Guest</xsl:when>
        <!-- Other Roles -->
        <xsl:when test="$inputRole = '01' or $inputRole = 'Learner'">Member</xsl:when>
        <xsl:when test="$inputRole = '02' or $inputRole = 'Instructor'">Sponsor</xsl:when>
        <xsl:when test="$inputRole = '03' or $inputRole = 'Content Developer'">Assistant</xsl:when>
        <xsl:when test="$inputRole = '04' or $inputRole = 'Member'">Member</xsl:when>
        <xsl:when test="$inputRole = '05' or $inputRole = 'Manager'">Sponsor</xsl:when>
        <xsl:when test="$inputRole = '06' or $inputRole = 'Mentor'">Assistant</xsl:when>
        <xsl:when test="$inputRole = '07' or $inputRole = 'Adminstrator'">Sponsor</xsl:when>
        <xsl:otherwise></xsl:otherwise>
    </xsl:choose>

</xsl:template>

</xsl:stylesheet>
