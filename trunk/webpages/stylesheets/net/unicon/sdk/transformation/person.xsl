<?xml version="1.0"?>
<!--
    FILE:     person.xsl
    AUTHOR: Shawn Lonas
    DATE:     9/3/03
    OWNER:     UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method = "xml"  version="1.0" indent="yes"  />

<!-- Implement person match if it needs to vary from IMS -->
<xsl:template match="person" mode="element">
  <xsl:choose>
    <xsl:when test="userid">
      <person id="{concat((./sourcedid/source),(./sourcedid/id)) }" username="{./userid}">
        <source-info>
            <source><xsl:value-of select="../properties/datasource" /></source>
            <id><xsl:value-of select="./sourcedid/id" /></id>
            <xsl:if test="userid"><foreign-id><xsl:value-of select="./userid" /></foreign-id></xsl:if>
        </source-info>

        <xsl:apply-templates select = "name | demographics" />

        <xsl:if test = "email | url | tel | adr">
            <contact-info>
                <xsl:apply-templates select = "email | url | tel | adr" />
            </contact-info>
        </xsl:if>

        <xsl:if test = "institutionrole | systemrole">
            <role>
                <xsl:apply-templates select = "institutionrole | systemrole" />
            </role>
        </xsl:if>
      </person>
    </xsl:when>
    <xsl:otherwise>
      <person id="{concat((./sourcedid/source),(./sourcedid/id)) }" username="{./sourcedid/id}">
        <source-info>
            <source><xsl:value-of select="../properties/datasource" /></source>
            <id><xsl:value-of select="./sourcedid/id" /></id>
            <xsl:if test="userid"><foreign-id><xsl:value-of select="./userid" /></foreign-id></xsl:if>
        </source-info>

        <xsl:apply-templates select = "name | demographics" />

        <xsl:if test = "email | url | tel | adr">
            <contact-info>
                <xsl:apply-templates select = "email | url | tel | adr" />
            </contact-info>
        </xsl:if>

        <xsl:if test = "institutionrole | systemrole">
            <role>
                <xsl:apply-templates select = "institutionrole | systemrole" />
            </role>
        </xsl:if>
      </person>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- name -->
<xsl:template match="person/name">
    <name>
        <xsl:apply-templates />
    </name>
</xsl:template>
<xsl:template match="name/fn">
    <formatted><xsl:value-of select="." /></formatted>
</xsl:template>
<xsl:template match="n/family">
    <last><xsl:value-of select="." /></last>
</xsl:template>
<xsl:template match="n/given">
    <first><xsl:value-of select="." /></first>
</xsl:template>
<xsl:template match="n/prefix">
    <prefix><xsl:value-of select="." /></prefix>
</xsl:template>
<xsl:template match="n/suffix">
    <suffix><xsl:value-of select="." /></suffix>
</xsl:template>

<!-- demographics -->
<xsl:template match="person/demographics">
    <demographics>
    <xsl:attribute name="gender">
        <xsl:choose>
            <xsl:when test="gender = '1'">female</xsl:when>
            <xsl:when test="gender = '2'">male</xsl:when>
            <xsl:otherwise>unknown</xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
        <xsl:apply-templates />
    </demographics>
</xsl:template>
<xsl:template match="demographics/bday">
    <birthday><xsl:value-of select="." /></birthday>
</xsl:template>
<xsl:template match="demographics/disability">
    <disability><xsl:value-of select="." /></disability>
</xsl:template>

<!-- contact-info -->
<xsl:template match="person/tel">
    <phone>
    <xsl:attribute  name = "type" >
        <xsl:choose>
            <xsl:when test="@teltype = '2' or @teltype = 'Fax'">fax</xsl:when>
            <xsl:when test="@teltype = '3' or @teltype = 'Mobile'">mobile</xsl:when>
            <xsl:when test="@teltype = '4' or @teltype = 'Pager'">pager</xsl:when>
            <xsl:otherwise>main</xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
        <xsl:value-of select="." />
    </phone>
</xsl:template>
<xsl:template match="person/adr">
    <address>
        <!-- concatenate the <pobox></pobox> and <street></street> elements into a single field -->
        <xsl:if test = "pobox | street">
            <line1>
                <xsl:for-each select = "pobox | street"><xsl:if test = "position() &gt; 1">, </xsl:if> <xsl:value-of select="." /></xsl:for-each>
            </line1>
        </xsl:if>
        <xsl:apply-templates />
    </address>
</xsl:template>
<xsl:template match="adr/extadd">
    <line2><xsl:value-of select="." /></line2>
</xsl:template>
<xsl:template match="adr/locality">
    <city><xsl:value-of select="." /></city>
</xsl:template>
<xsl:template match="adr/region">
    <state><xsl:value-of select="." /></state>
</xsl:template>
<xsl:template match="adr/pcode">
    <zip><xsl:value-of select="." /></zip>
</xsl:template>
<xsl:template match="adr/country">
    <country><xsl:value-of select="." /></country>
</xsl:template>

<!-- role -->
<xsl:template match="person/institutionrole">
    <organization>
    <xsl:attribute  name = "primary" >
        <xsl:choose>
            <xsl:when test="@primaryrole = 'Yes'">yes</xsl:when>
            <xsl:otherwise>no</xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
        <xsl:value-of select="@institutionroletype" />
    </organization>
</xsl:template>
<xsl:template match="person/systemrole">
    <system><xsl:call-template name="mapRole"><xsl:with-param name="inputRole" select="@systemroletype" /></xsl:call-template></system>
</xsl:template>


</xsl:stylesheet>
