<?xml version="1.0"?>
<!--
    FILE:     membership.xsl
    AUTHOR: Shawn Lonas
    DATE:     9/23/03
    OWNER:     UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method = "xml"  version="1.0" indent="yes"  />

<xsl:template match="role" mode="element">
    <xsl:variable name = "memberType">
        <xsl:choose>
            <xsl:when test="../idtype = '2'">subgroup</xsl:when>

            <xsl:otherwise>member</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:element  name = "{$memberType}">
        <xsl:attribute  name = "source-ref" ><xsl:value-of select="../../../properties/datasource" /></xsl:attribute>
        <xsl:attribute  name = "groupid-ref" ><xsl:call-template name = "findGroupType" ><xsl:with-param name="groupType" select="../../sourcedid/source"/></xsl:call-template>_<xsl:value-of select="../../sourcedid/id" /></xsl:attribute>
        <xsl:choose>
            <xsl:when test="$memberType = 'subgroup'">
                <xsl:attribute  name = "subgroupid-ref" ><xsl:value-of select="../sourcedid/id" /></xsl:attribute>
            </xsl:when>

            <xsl:otherwise>
                <xsl:attribute  name = "personid-ref" ><xsl:value-of select="../sourcedid/id" /></xsl:attribute>
            </xsl:otherwise>
        </xsl:choose>
        <status>
            <xsl:choose>
                <xsl:when test="status = '1'">ENROLLED</xsl:when>
                <xsl:otherwise>inactive</xsl:otherwise>
            </xsl:choose>
        </status>
        <group-role>
            <xsl:call-template name = "mapRole" >
                <xsl:with-param name="inputRole"><xsl:value-of select="@roletype" /></xsl:with-param>
            </xsl:call-template>
        </group-role>
    </xsl:element>
</xsl:template>


</xsl:stylesheet>
