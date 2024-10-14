<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="MODE_VIEW" select="0"/>
    <xsl:variable name="MODE_EDIT" select="1"/>

    <!--german-->
    <xsl:variable name="TOK_NAME" select="'Name'"/>
    <xsl:variable name="TOK_TYPE" select="'Type'"/>
    <xsl:variable name="TOK_INNEWS" select="'In Neuigkeiten'"/>
    <xsl:variable name="TOK_DESC" select="'Beschreibung'"/>
    <xsl:variable name="TOK_EXPON" select="'Gültig bis'"/>

    <xsl:variable name="TOK_OWNER" select="'Eigentümer'"/>
    <xsl:variable name="TOK_CREATED" select="'Erstellt'"/>
    <xsl:variable name="TOK_CHANGED" select="'Geändert'"/>
    <xsl:variable name="TOK_DATEFORMAT" select="'(tt.mm.jjjj)'"/>

    <!--english-->
<!--
    <xsl:variable name="TOK_NAME" select="'name'"/>
    <xsl:variable name="TOK_TYPE" select="'type'"/>
    <xsl:variable name="TOK_INNEWS" select="'in news'"/>
    <xsl:variable name="TOK_DESC" select="'description'"/>
    <xsl:variable name="TOK_EXPON" select="'expires on'"/>

    <xsl:variable name="TOK_OWNER" select="'owner'"/>
    <xsl:variable name="TOK_CREATED" select="'created'"/>
    <xsl:variable name="TOK_CHANGED" select="'changed'"/>
    <xsl:variable name="TOK_DATEFORMAT" select="'(dd.mm.yyyy)'"/>
-->
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>


    <!-- ********************* APPLYED TEMPLATES BEGIN ********************* -->
    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">
<!--
        <A CLASS="name">Generic m2 stylesheet</A>
-->
        <TABLE CLASS="info" BORDERCOLOR="#FFFFFF" WIDTH="100%" BORDER="0"
               CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
            <COLGROUP><COL CLASS="name"/></COLGROUP>
            <COLGROUP><COL CLASS="value"/></COLGROUP>
            <THEAD/>
            <TBODY>
                <xsl:if test="not (boolean(SYSTEM/@DISPLAY))">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tagName" select="."/>
                        <xsl:with-param name="localRow" select="position () + 6"/>
                        <xsl:with-param name="mode" select="$MODE_EDIT"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:if test="SYSTEM/@DISPLAY='BOTTOM'">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
            </TBODY>
        </TABLE>

        <SCRIPT LANGUAGE="JavaScript">
            function xslSubmitAllowed()
            {
                <xsl:if test="not (boolean(SYSTEM/@DISPLAY)) or
                              SYSTEM/@DISPLAY='BOTTOM'">
                    var today = new Date ();
                    var todayStr = "" + today.getDate() + "." + (today.getMonth()+1) + "." + (today.getYear()%100);

                    if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>, false)) return false;
                    if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/DESCRIPTION/@INPUT"/>, true) ||
                        !top.iLLE(document.sheetForm.<xsl:value-of select="SYSTEM/DESCRIPTION/@INPUT"/>, 255)) return false;
                    if (!top.iDA (document.sheetForm.<xsl:value-of select="SYSTEM/VALIDUNTIL/@INPUT"/>, todayStr, false)) return false;
                </xsl:if>

                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="getRestrictions">
                        <xsl:with-param name="tagName" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                return true;
            }

            <![CDATA[
            function getdObjecRef (incompleteURL, objectRef,
                                   objectRef_M, objectRefName)
            {
                var completeURL = incompleteURL;

                completeURL += '&' + objectRefName + '=' + escape (objectRef);
                completeURL += '&' + objectRefName + '_M=' + objectRef_M;

                return completeURL;
            } // getEscapedObjecRef
            ]]><!--CDATA-->
        </SCRIPT>
    </xsl:template> <!-- OBJECT -->
    <!-- **************************** MAIN END ***************************** -->

    <xsl:template match="SYSTEM">
        <TR VALIGN="TOP" CLASS="infoRow1">
            <TD><xsl:value-of select="$TOK_NAME"/><xsl:text>:</xsl:text></TD>
            <TD>
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="NAME"/>
                    <xsl:with-param name="size" select="40"/>
                    <xsl:with-param name="maxlength" select="63"/>
                </xsl:call-template>
            </TD>
        </TR> <!-- name -->

        <TR VALIGN="TOP" CLASS="infoRow2">
            <TD><xsl:value-of select="$TOK_DESC"/><xsl:text>:</xsl:text></TD>
            <TD>
                <xsl:call-template name="internDt_textarea">
                    <xsl:with-param name="tagName" select="DESCRIPTION"/>
                    <xsl:with-param name="maxlength" select="255"/>
                    <xsl:with-param name="cols" select="40"/>
                    <xsl:with-param name="rows" select="5"/>
                </xsl:call-template>
            </TD>
        </TR> <!-- description -->

        <TR VALIGN="TOP" CLASS="infoRow1">
            <TD COLSPAN="2" ALIGN="MIDDLE"><FONT SIZE="-6"><HR/></FONT></TD>
        </TR> <!-- line -->
    </xsl:template> <!-- SYSTEM -->
    <!-- ********************** APPLYED TEMPLATES END ********************** -->


    <!-- ********************* CALLED TEMPLATES BEGIN ********************** -->
    <xsl:template name="infoRow">
        <xsl:param name="tagName"/>
        <xsl:param name="localRow"/>
        <xsl:param name="mode"/>
        <xsl:choose>
            <xsl:when test="$tagName/@TYPE = 'SEPARATOR'">
                <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                    <TD COLSPAN="2" ALIGN="MIDDLE">
                        <FONT SIZE="-6">
                            <HR/>
                        </FONT>
                    </TD>
                </TR>
            </xsl:when>

            <xsl:when test="$tagName/@TYPE = 'QUERY'">
                <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                    <xsl:call-template name="getDatatype">
                        <xsl:with-param name="tagName" select="$tagName"/>
                        <xsl:with-param name="mode" select="$MODE_VIEW"/>
                    </xsl:call-template>
                </TR>
            </xsl:when>

            <xsl:when test="$tagName/@TYPE = 'FIELDREF'">
                <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                    <TD WIDTH="100%" COLSPAN="2">
                        <xsl:call-template name="getDatatype">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="mode" select="$mode"/>
                        </xsl:call-template>
                    </TD>
                </TR>
            </xsl:when>

            <xsl:when test="$tagName[@FIELD = 'Vorlage']">
                <xsl:choose>
                    <xsl:when test="/OBJECT/SYSTEM/STATE = '2'">
                        <!-- Don't show the document tamplate field to the user. -->
                    </xsl:when>

                    <xsl:otherwise>
                        <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                            <TD>
                                <xsl:value-of select="$tagName/@FIELD"/>
                                <xsl:text>:</xsl:text>
                            </TD>
                            <TD>
                                <xsl:call-template name="getDatatype">
                                    <xsl:with-param name="tagName" select="$tagName"/>
                                    <xsl:with-param name="mode" select="$mode"/>
                                </xsl:call-template>
                            </TD>
                        </TR>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:otherwise>
                <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                    <TD>
                        <xsl:value-of select="$tagName/@FIELD"/>
                        <xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:call-template name="getDatatype">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="mode" select="$mode"/>
                        </xsl:call-template>
                    </TD>
                </TR>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> <!--infoRow-->
    <!-- ********************** CALLED TEMPLATES END *********************** -->
</xsl:stylesheet>



