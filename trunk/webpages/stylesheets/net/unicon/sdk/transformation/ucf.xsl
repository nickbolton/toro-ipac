<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- This will "transform" a payload which is already in the
     Unicon canonical form (UCF)
-->
    <xsl:template match="/">
        <xsl:copy-of select="/"/>
    </xsl:template>
</xsl:stylesheet>
