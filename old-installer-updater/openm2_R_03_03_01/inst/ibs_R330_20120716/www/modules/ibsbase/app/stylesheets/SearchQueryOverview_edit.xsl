<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_EDIT"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <TABLE class="info" width="100%" CELLPADDING="5" CELLSPACING="0">
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
                <xsl:with-param name="label" select="$TOK_TYPE"/>
                <xsl:with-param name="value" select="@TYPE"/>
                <xsl:with-param name="localRow" select="'2'"/>                
            </xsl:call-template>
            
            <xsl:call-template name="infoRow">
                <xsl:with-param name="tag" select="SYSTEM/DESCRIPTION"/>
                <xsl:with-param name="label" select="$TOK_DESC"/>
                <xsl:with-param name="localRow" select="'3'"/>
            </xsl:call-template>

            <!--
            <xsl:call-template name="infoRow">
                <xsl:with-param name="tag" select="SYSTEM/VALIDUNTIL"/>
                <xsl:with-param name="label" select="$TOK_NAME"/>
                <xsl:with-param name="localRow" select="'4'"/>
            </xsl:call-template>
            -->                        
            
        </TABLE>

        <SCRIPT>
            function xslSubmitAllowed ()
            {
                if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>, false)) return false;
                if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/DESCRIPTION/@INPUT"/>, true) ||
                    !top.iLLE(document.sheetForm.<xsl:value-of select="SYSTEM/DESCRIPTION/@INPUT"/>, 255)) return false;
                <!--
                if (!top.iDA (document.sheetForm.<xsl:value-of select="SYSTEM/VALIDUNTIL/@INPUT"/>, todayStr, false)) return false;
                -->                
                return true;
            }                      
        </SCRIPT>

    </xsl:template> <!-- OBJECT -->
    <!-- **************************** MAIN END ***************************** -->

</xsl:stylesheet>

