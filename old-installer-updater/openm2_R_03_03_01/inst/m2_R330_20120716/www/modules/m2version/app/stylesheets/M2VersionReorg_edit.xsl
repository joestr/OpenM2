<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_EDIT"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5" border="1">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'Reorganisation simulieren'"/>
                <xsl:with-param name="label" select="'als Simulation starten'"/>
                <xsl:with-param name="localRow" select="'2'"/>
            </xsl:call-template>

        </TABLE>

        <BR/>


        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5" border="1">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>


            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'Letzte Aktualisierung der Versionen'"/>
                <xsl:with-param name="localRow" select="'1'"/>
                <xsl:with-param name="mode" select="$MODE_VIEW"/>
            </xsl:call-template>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5" border="1">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Aktualisierung der Versionen:
                </TD>
                <TD>
                    <INPUT TYPE="CHECKBOX" NAME="actualizeVersions" VALUE="true"/> Versionen aktualisieren<BR/>
                    Beachten Sie bitte die folgenden Hinweise:
                    <UL>
                        <LI><B>Achtung: Ein wiederholtes Ausführen der Funktion ist möglich, hat dann allerdings keine Änderung der Daten zur Folge.</B></LI>
                        <LI>Für sämtliche Objekte vom Typ "versionierte Datei" werden die Versionen aktualisiert.</LI>
                        <LI>Die versionierten Dateien werden um ein Feld "Dateiname" erweitert.</LI>
                        <LI>In das Feld "Dateiname" wird der Dateiname der Masterversion (sofern vorhanden) eingefügt. Die "Dateigröße" der Masterversion wird in das Dateiattribut "SIZE" eingetragen.</LI>
                        <LI>Für jede Version wird eine Kopie der versionierten Datei erstellt und direkt unter der jeweiligen Version gespeichert. Dabei werden die Datei und die Dateigröße der Version in die Kopie übernommen.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <xsl:call-template name="createStandardValidationJS"/>

    </xsl:template> <!-- OBJECT -->

</xsl:stylesheet>