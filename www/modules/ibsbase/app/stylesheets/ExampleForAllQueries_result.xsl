<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>
<xsl:import href="general\genericresult.xsl"/>

<xsl:template match="/QUERY">

    <xsl:call-template name="showQueryResult">
        <xsl:with-param name="addCheckBox">NO</xsl:with-param>
    </xsl:call-template>

</xsl:template>
</xsl:stylesheet>