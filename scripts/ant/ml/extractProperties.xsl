<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" indent="no" />
<xsl:template match="VALUE">
<xsl:text></xsl:text>
<xsl:value-of select="../../@TYPECODE"/>.VF_<xsl:value-of select="@FIELD"/>.NAME=<xsl:value-of select="@FIELD"/>
<!-- <xsl:text></xsl:text>
<xsl:value-of select="../../@TYPECODE"/>.VF_.<xsl:value-of select="@FIELD"/>.DESCRIPTION= -->
</xsl:template>
</xsl:stylesheet>