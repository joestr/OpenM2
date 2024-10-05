<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" indent="no" />
<xsl:template match="TABOBJECT">
<xsl:text></xsl:text>
TA_<xsl:value-of select="@TABCODE"/>.NAME=<xsl:value-of select="SYSTEM/NAME/child::text ()"/>
</xsl:template>
</xsl:stylesheet>