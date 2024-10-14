<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/genericview.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
 
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">
        <TABLE CLASS="info" WIDTH="100%" BORDER="0"
               CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
            <COLGROUP><COL CLASS="name"/></COLGROUP>
            <COLGROUP><COL CLASS="value"/></COLGROUP>
            <THEAD/>
            <TBODY>       
                     
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tag" select="."/>
                        <xsl:with-param name="localRow" select="position ()"/>
                        <xsl:with-param name="mode" select="$MODE_VIEW"/>
                    </xsl:call-template>
                </xsl:for-each>

                <xsl:if test="SYSTEM/@DISPLAY='BOTTOM'">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
            </TBODY>
        </TABLE>    

    </xsl:template><!--addJavascript-->
</xsl:stylesheet>