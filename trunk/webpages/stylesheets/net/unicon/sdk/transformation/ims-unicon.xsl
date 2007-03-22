<?xml version="1.0"?>
<!-- 
	FILE: 	ims-unicon.xsl
	AUTHOR: Shawn Lonas
	DATE: 	9/8/03
	OWNER: 	UNICON, Inc
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href = "properties.xsl" />
<xsl:import href = "person.xsl" />
<xsl:import href = "group.xsl" />
<xsl:import href = "membership.xsl" />
<xsl:import href = "rolemapping.xsl" />
<xsl:import href = "common.xsl" />

<!-- Command to output XML with DOCTYPE for validation purposes:

<xsl:output method = "xml"  version="1.0" indent="yes" encoding="UTF-8" doctype-system="unicon7.dtd" /> 

or without it :

<xsl:output method = "xml"  version="1.0" indent="yes" encoding="UTF-8" />

-->
<xsl:output method = "xml"  version="1.0" indent="yes" encoding="UTF-8" />

<xsl:template match="/">
	<batch><xsl:apply-templates /></batch>
</xsl:template>

<!-- 
	Prevents text in unmatched elements from being written to the output. 
	To implement new elements a new template just needs to be added, instead
		of restraining all <xsl:apply-templates /> calls to specific supported
		elements.
-->
<xsl:template match="text()"></xsl:template>

</xsl:stylesheet>