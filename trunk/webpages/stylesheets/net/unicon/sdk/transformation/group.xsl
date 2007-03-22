<?xml version="1.0"?>
<!-- 
    FILE:     group.xsl
    AUTHOR: Shawn Lonas
    DATE:     9/9/03
    OWNER:     UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method = "xml"  version="1.0" indent="yes"  />

<!-- GROUP -->
<xsl:template match="group" mode="element">
    <group id="{concat((./sourcedid/source),(./sourcedid/id)) }">
    	<xsl:attribute  name = "id" ><xsl:call-template name = "findGroupType" ><xsl:with-param name="groupType" select="./grouptype/scheme"/></xsl:call-template>_<xsl:value-of select="./sourcedid/id" /></xsl:attribute>
        <source-info>
            <source><xsl:value-of select="../properties/datasource" /></source>
            <id><xsl:call-template name = "findGroupType" ><xsl:with-param name="groupType" select="./grouptype/scheme"/></xsl:call-template>_<xsl:value-of select="./sourcedid/id" /></id>
        </source-info>

        <xsl:apply-templates select="." mode="specificGroup"/>
    </group>
</xsl:template> 

<!-- OFFERING -->
<xsl:template match="group[grouptype/typevalue = 'CLASSES']" mode="specificGroup">
    <offering>
        <xsl:apply-templates />
        <!-- static enrollment values -->
        <enrollment>
            <!-- enroll by Student Information System (as opposed to open, faculty, or "request and approve") -->
            <model>Student Information System</model>
            <!-- enrolled members default role is member -->
            <default-role>Member</default-role>
        </enrollment>

    </offering>
</xsl:template>

<!-- COURSE -->
<xsl:template match="group[grouptype/typevalue = 'COURSES']" mode="specificGroup">
    <topic>
        <xsl:apply-templates />
    </topic>
</xsl:template>

<!-- TERM -->
<!-- NOT CURRENTLY IMPLEMENTED
<xsl:template match="group[grouptype/typevalue = 'TERM']" mode="specificGroup">
    <term>
        <xsl:apply-templates />
    </term>
</xsl:template> 
-->

<!-- CALENDAR_EVENT -->
<!-- NOT CURRENTLY IMPLEMENTED
<xsl:template match="group[grouptype/typevalue = 'CALENDAR_EVENT']" mode="specificGroup">
    <calendar-event>
        <xsl:apply-templates />
    </calendar-event>
</xsl:template>
-->

<!-- CLUBS -->
<xsl:template match="group[grouptype/typevalue = 'CLUBS']" mode="specificGroup">
    <club>
        <xsl:apply-templates />
        <!-- static enrollment values -->
        <enrollment>
            <!-- enroll by Student Information System (as opposed to open, faculty, or "request and approve") -->
            <model>Student Information System</model>
            <!-- enrolled members default role is member -->
            <default-role>Member</default-role>
        </enrollment>
        
    </club>
</xsl:template>

<!-- DEPARTMENTS -->
<xsl:template match="group[grouptype/typevalue = 'DEPARTMENTS']" mode="specificGroup">
    <department>
        <xsl:apply-templates />
    </department>
</xsl:template>

<!-- DEPARTMENTS -->
<xsl:template match="group[grouptype/typevalue = 'DIVISIONS']" mode="specificGroup">
    <division>
        <xsl:apply-templates />
    </division>
</xsl:template>

<!-- description -->
<xsl:template match="group[grouptype/typevalue = 'CLASSES' or grouptype/typevalue = 'COURSES']/description">
	<xsl:apply-templates select = "short" />
	<xsl:apply-templates select = "long" />
	<!-- If CLASS or COURSE is missing <full/> use <short/> instead -->
	<xsl:choose>
		<xsl:when test="full[text() != '']">
			<xsl:apply-templates select = "full" />
		</xsl:when>
		<xsl:when test="short">
		 	<description><xsl:value-of select="short" /></description>
		</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
</xsl:template>
<xsl:template match="group/description/short">
    <shortname><xsl:value-of select="." /></shortname>
</xsl:template>
<xsl:template match="group/description/long">
    <title><xsl:value-of select="." /></title>
</xsl:template>
<xsl:template match="group/description/full">
    <description><xsl:value-of select="." /></description>
</xsl:template>

<!-- org -->
<!--
<xsl:template match="group/org">
    <affiliations>
        <xsl:apply-templates />
    </affiliations>
</xsl:template>
<xsl:template match="group/org/orgunit">
     <parent><xsl:value-of select="." /></parent>
</xsl:template>
-->
<!-- timeframe -->
<xsl:template match="group/timeframe">
    <timeframe>
        <xsl:apply-templates />
    </timeframe>
</xsl:template>
<xsl:template match="group/timeframe/adminperiod">
    <term><xsl:value-of select="." /></term>
</xsl:template>
<xsl:template match="group/timeframe/begin">
    <start>
        <date><xsl:value-of select="." /></date>
        <!-- CALENDAR IS NOT CURRENTLY IMPLEMENTED
        <xsl:if test = "../../relationship/label = 'CALENDAR_EVENT'">
            <time>
                <xsl:call-template name = "getEventStartTime" >
                    <xsl:with-param name="time" select="../../relationship[label = 'CALENDAR_EVENT']/extension" />
                </xsl:call-template>
            </time>
        </xsl:if>
        -->
    </start>
</xsl:template>
<xsl:template match="group/timeframe/end">
    <end>
        <date><xsl:value-of select="." /></date>
        <!-- CALENDAR IS NOT CURRENTLY IMPLEMENTED
        <xsl:if test = "../../relationship/label = 'CALENDAR_EVENT'">
            <time>
                <xsl:call-template name = "getEventEndTime" >
                    <xsl:with-param name="time" select="../../relationship[label = 'CALENDAR_EVENT']/extension" />
                </xsl:call-template>
            </time>
        </xsl:if>
        -->
    </end>
</xsl:template>
<!-- relationship -->
<xsl:template match="group/relationship[1]">
    <relationships>
    <xsl:attribute  name = "groupsource-ref" ><xsl:value-of select="../../properties/datasource" /></xsl:attribute>
    <xsl:attribute  name = "groupid-ref" ><xsl:call-template name = "findGroupType" ><xsl:with-param name="groupType" select="../grouptype/scheme"/></xsl:call-template>_<xsl:value-of select="../sourcedid/id" /></xsl:attribute>
        <xsl:apply-templates select="../relationship" mode="element"/>
    </relationships>
</xsl:template>
<xsl:template match="group/relationship" mode="element">
    <xsl:choose>
        <xsl:when test="@relation = '1'">
            <parent>
                <xsl:call-template name = "relationDetails" />
            </parent>
        </xsl:when>
        <xsl:when test="@relation = '2'">
            <child>
                <xsl:call-template name = "relationDetails" />
            </child>
        </xsl:when>
        <xsl:when test="@relation = '3'">
            <alias>
                <xsl:call-template name = "relationDetails" />
            </alias>
        </xsl:when>
        <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- Common details for relations -->
<xsl:template name="relationDetails">
    <xsl:attribute  name = "groupsource-ref" ><xsl:value-of select="./sourcedid/source" /></xsl:attribute>
    <xsl:attribute  name = "groupid-ref" ><xsl:call-template name = "findGroupType" ><xsl:with-param name="groupType" select="./sourcedid/source"/></xsl:call-template>_<xsl:value-of select="./sourcedid/id" /></xsl:attribute>
    <xsl:value-of select="label" />
</xsl:template>

<!-- Split Calendar Event Start / End Time -->
<!-- CALENDAR IS NOT CURRENTLY IMPLEMENTED
<xsl:template name="getEventStartTime">
    <xsl:param name="time" />
    
    <xsl:choose>
        <xsl:when test="contains($time,' ')"><xsl:value-of select="substring-before($time,' ')" /></xsl:when>
        <xsl:when test="contains($time,',')"><xsl:value-of select="substring-before($time,',')" /></xsl:when>
        <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
</xsl:template>
<xsl:template name="getEventEndTime">
    <xsl:param name="time" />
    
    <xsl:choose>
        <xsl:when test="contains($time,' ')"><xsl:value-of select="substring-after($time,' ')" /></xsl:when>
        <xsl:when test="contains($time,',')"><xsl:value-of select="substring-after($time,',')" /></xsl:when>
        <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
</xsl:template>
-->

</xsl:stylesheet>
