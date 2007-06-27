<?xml version="1.0"?>
<!-- 
	FILE: 	common.xsl
	AUTHOR: Shawn Lonas
	DATE: 	9/8/03
	OWNER: 	UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method = "xml"  version="1.0" indent="yes"  />

<!-- Implement person match if it needs to vary from IMS -->
<xsl:template match="person | group | role">
	<detail>
		<!-- <action /> -->
		<xsl:call-template name = "actionElement" >
			<xsl:with-param name="type" select="./@recstatus" />
		</xsl:call-template>

		<payload>
			<xsl:apply-templates select = "." mode="element" />
		</payload>
	</detail>
</xsl:template> 

<xsl:template name="actionElement">
	<xsl:param name="type" />
	<action>
		<xsl:choose>
			<xsl:when test="$type='1'"><add /></xsl:when>
			<xsl:when test="$type='2'"><update /></xsl:when>
			<xsl:when test="$type='3'"><delete /></xsl:when>
			<xsl:otherwise><import/></xsl:otherwise>
		</xsl:choose>
	</action>
</xsl:template>

<xsl:template match="email">
	<email><xsl:value-of select="." /></email>
</xsl:template>
<xsl:template match="url">
	<url><xsl:value-of select="." /></url>
</xsl:template>

<!-- Filter group type names for making globally unique ids -->
<xsl:template name="findGroupType">
	<xsl:param name="groupType" />
	
	<xsl:choose>
		<xsl:when test="contains($groupType,'dep') or contains($groupType,'DEP') or contains($groupType,'Dep')">DEPARTMENT</xsl:when>
		<xsl:when test="contains($groupType,'div') or contains($groupType,'DIV') or contains($groupType,'Div')">DIVISION</xsl:when>
		<xsl:when test="contains($groupType,'cou') or contains($groupType,'COU') or contains($groupType,'Cou')">COURSE</xsl:when>
		<xsl:when test="contains($groupType,'clu') or contains($groupType,'CLU') or contains($groupType,'Clu')">CLUB</xsl:when>
		<xsl:when test="contains($groupType,'cla') or contains($groupType,'CLA') or contains($groupType,'Cla')">CLASS</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>

</xsl:template>

</xsl:stylesheet>
