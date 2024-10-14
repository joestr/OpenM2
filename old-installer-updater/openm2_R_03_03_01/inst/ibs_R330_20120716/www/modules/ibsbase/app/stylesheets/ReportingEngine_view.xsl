<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <xsl:import href="general/tokens.xsl"/>
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
                <xsl:with-param name="tag" select="SYSTEM/NAME"/>
                <xsl:with-param name="label" select="$TOK_NAME"/>
                <xsl:with-param name="localRow" select="'1'"/>
            </xsl:call-template>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tag" select="SYSTEM/DESCRIPTION"/>
                <xsl:with-param name="label" select="$TOK_DESC"/>
                <xsl:with-param name="localRow" select="'2'"/>
            </xsl:call-template>

        </TABLE>

<!--
        <FIELDSET>
            <LEGEND>Info</LEGEND>
            <DIV ALIGN="LEFT">
                Hinweise zu den Feldern:<BR/>
                <LI/>Aktiviert: Aktiviert die Reporting Engine. Bei Aktivierung wird die Konfiguration auf Gültigkeit überprüft.<BR/>
                <LI/>URL für Aufruf: typischerweise: "http://host/BIRT/redirect.jsp"<BR/>
                <LI/>Reportdateien-Directory: Verzeichnis, in dem die Reportdateien für BIRT abgelegt werden. z.B. c:\wwwroot\BIRT\report<BR/>
                <LI/>Dateierweiterung: Für BIRT Reportdateien. Typischerweise "rptdesign"<BR/>
            </DIV>
        </FIELDSET>
-->
        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'activated'"/>
                <xsl:with-param name="label" select="$TOK_ACTIVATED"/>
                <xsl:with-param name="localRow" select="'1'"/>
            </xsl:call-template>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'invocationUrl'"/>
                <xsl:with-param name="label" select="$TOK_INVOCATIONURL"/>
                <xsl:with-param name="localRow" select="'2'"/>
            </xsl:call-template>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'reportDir'"/>
                <xsl:with-param name="label" select="$TOK_REPORTDIR"/>
                <xsl:with-param name="localRow" select="'3'"/>
            </xsl:call-template>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'fileextension'"/>
                <xsl:with-param name="label" select="$TOK_FILEEXTENSION"/>
                <xsl:with-param name="localRow" select="'4'"/>
            </xsl:call-template>

        </TABLE>

    </xsl:template> <!-- OBJECT -->

</xsl:stylesheet>
