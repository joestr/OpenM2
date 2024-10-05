<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_VIEW"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'Letzte Aktualisierung der Versionen'"/>
                <xsl:with-param name="localRow" select="'1'"/>
            </xsl:call-template>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <TR CLASS="infoRow2">
                <TD CLASS="value" ALIGN="CENTER">
                    <IMG src="{$layoutPath}images/messages/info.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                    Zum Aufruf der Verwaltungsfunktionen klicken sie bitte auf <B>Bearbeiten</B>!
                </TD>
            </TR>
        </TABLE>

    </xsl:template> <!-- OBJECT -->

</xsl:stylesheet>
