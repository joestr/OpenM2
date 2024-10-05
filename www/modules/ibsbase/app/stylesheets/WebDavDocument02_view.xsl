<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->
    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="MODE_VIEW" select="0"/>
    <xsl:variable name="MODE_EDIT" select="1"/>
    <xsl:variable name="showExt" select="/OBJECT/SYSTEM/@SHOWEXT"/>
    <xsl:variable name="checkedOut" select="number (boolean (/OBJECT/SYSTEM/CHECKEDOUT))"/>
    <!--german-->
    <xsl:variable name="TOK_NAME" select="'Name'"/>
    <xsl:variable name="TOK_TYPE" select="'Type'"/>
    <xsl:variable name="TOK_INNEWS" select="'In Neuigkeiten'"/>
    <xsl:variable name="TOK_DESC" select="'Beschreibung'"/>
    <xsl:variable name="TOK_EXPON" select="'Gültig bis'"/>
    <xsl:variable name="TOK_CHECKEDOUT" select="'Ausgecheckt'"/>
    <xsl:variable name="TOK_OWNER" select="'Eigentümer'"/>
    <xsl:variable name="TOK_CREATED" select="'Erstellt'"/>
    <xsl:variable name="TOK_CHANGED" select="'Geändert'"/>
    <!--english-->
    <!--
    <xsl:variable name="TOK_NAME" select="'name'"/>
    <xsl:variable name="TOK_TYPE" select="'type'"/>
    <xsl:variable name="TOK_INNEWS" select="'in news'"/>
    <xsl:variable name="TOK_DESC" select="'description'"/>
    <xsl:variable name="TOK_EXPON" select="'expires on'"/>
    <xsl:variable name="TOK_CHECKEDOUT" select="'checked out'"/>

    <xsl:variable name="TOK_OWNER" select="'owner'"/>
    <xsl:variable name="TOK_CREATED" select="'created'"/>
    <xsl:variable name="TOK_CHANGED" select="'changed'"/>
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
        <TABLE CLASS="info" BORDERCOLOR="#FFFFFF" WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
            <COLGROUP>
                <COL CLASS="name"/>
            </COLGROUP>
            <COLGROUP>
                <COL CLASS="value"/>
            </COLGROUP>
            <THEAD/>
            <TBODY>
                <xsl:if test="not (boolean(SYSTEM/@DISPLAY))">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tagName" select="."/>
                        <xsl:with-param name="localRow" select="position () + 6 + (number ($showExt) * 4) + $checkedOut"/>
                        <xsl:with-param name="mode" select="$MODE_VIEW"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:if test="SYSTEM/@DISPLAY='BOTTOM'">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
            </TBODY>
        </TABLE>
    </xsl:template>
    <!-- OBJECT -->
    <!-- **************************** MAIN END ***************************** -->
    <xsl:template match="SYSTEM">
        <TR VALIGN="TOP" CLASS="infoRow1">
            <TD>
                <xsl:value-of select="$TOK_NAME"/>
                <xsl:text>:</xsl:text>
            </TD>
            <TD>
                <xsl:value-of select="NAME"/>
            </TD>
        </TR>
        <!-- name -->
        <TR VALIGN="TOP" CLASS="infoRow2">
            <TD>
                <xsl:value-of select="$TOK_DESC"/>
                <xsl:text>:</xsl:text>
            </TD>
            <TD>
                <xsl:for-each select="DESCRIPTION/LINE">
                    <xsl:value-of select="child::text ()"/>
                    <xsl:if test="position () &lt; last ()">
                        <BR/>
                    </xsl:if>
                </xsl:for-each>
            </TD>
        </TR>
        <!-- description -->
        <xsl:if test="boolean (CHECKEDOUT)">
            <TR VALIGN="TOP" CLASS="infoRow1">
                <TD>
                    <xsl:value-of select="$TOK_CHECKEDOUT"/>
                    <xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:value-of select="CHECKEDOUT/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="CHECKEDOUT/@DATE"/>
                </TD>
            </TR>
            <!-- checked out -->
        </xsl:if>
        <!-- show the extended attributes? -->
        <xsl:if test="$showExt = '1'">
            <xsl:variable name="localRow" select="2 + $checkedOut"/>
            <TR VALIGN="TOP" CLASS="infoRow{format-number ((number($localRow) mod 2) + 1, '####')}">
                <TD COLSPAN="2" ALIGN="MIDDLE">
                    <FONT SIZE="-6">
                        <HR/>
                    </FONT>
                </TD>
            </TR>
            <!-- line -->
            <xsl:variable name="localRow" select="$localRow + 1"/>
            <TR VALIGN="TOP" CLASS="infoRow{format-number ((number($localRow) mod 2) + 1, '####')}">
                <TD>
                    <xsl:value-of select="$TOK_OWNER"/>
                    <xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:value-of select="OWNER/@USERNAME"/>
                </TD>
            </TR>
            <!-- owner -->
            <xsl:variable name="localRow" select="$localRow + 1"/>
            <TR VALIGN="TOP" CLASS="infoRow{format-number ((number($localRow) mod number(2)) + number(1), '####')}">
                <TD>
                    <xsl:value-of select="$TOK_CREATED"/>
                    <xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:value-of select="CREATED/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="CREATED/@DATE"/>
                </TD>
            </TR>
            <!-- created -->
            <xsl:variable name="localRow" select="$localRow + 1"/>
            <TR VALIGN="TOP" CLASS="infoRow{format-number ((number($localRow) mod number(2)) + number(1), '####')}">
                <TD>
                    <xsl:value-of select="$TOK_CHANGED"/>
                    <xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:value-of select="CHANGED/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="CHANGED/@DATE"/>
                </TD>
            </TR>
            <!-- changed -->
            <xsl:variable name="localRow" select="$localRow + 1"/>
        </xsl:if>
        <!-- show the extended attributes? -->
        <TR VALIGN="TOP" CLASS="infoRow2">
            <TD COLSPAN="2" ALIGN="MIDDLE">
                <FONT SIZE="-6">
                    <HR/>
                </FONT>
            </TD>
        </TR>
        <!-- line -->
    </xsl:template>
    <!-- SYSTEM -->
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
                        <xsl:with-param name="mode" select="$mode"/>
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
            <xsl:when test="$tagName/@TYPE = 'PASSWORD'">
                <!-- Show no value of tpye password to the user
                     because there is nothing to see. -->
            </xsl:when>
            <xsl:when test="$tagName[@FIELD = 'Vorlage']">
                <!-- Don't show the document tamplate field to the user. -->
            </xsl:when>
            <xsl:when test="$tagName[@FIELD = 'Dokument']">
                <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
                    <TD>
                        <xsl:value-of select="$tagName/@FIELD"/>
                        <xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:choose>
                            <xsl:when test="boolean (/OBJECT/SYSTEM/CHECKEDOUT)">
                                <xsl:choose>
                                    <xsl:when test="(/OBJECT/SYSTEM/CHECKOUTUSERID) = (/OBJECT/SYSTEM/USERID)">
                                        <xsl:variable name="href">
                                            <xsl:value-of select="/OBJECT/SYSTEM/WEBDAVURL"/>
                                            <xsl:text>?uid=</xsl:text>
                                            <xsl:value-of select="/OBJECT/SYSTEM/USERID"/>
                                            <xsl:text>&amp;oid=</xsl:text>
                                            <xsl:value-of select="/OBJECT/SYSTEM/OID"/>
                                            <xsl:text>&amp;file=</xsl:text>
                                            <xsl:value-of select="/OBJECT/SYSTEM/CHECKOUTKEY"/>
                                            <xsl:text>_</xsl:text>
                                            <xsl:value-of select="$tagName/child::text ()"/>
                                        </xsl:variable>
                                        <xsl:value-of select="$tagName/child::text ()"/>
                                        <xsl:text>  </xsl:text>
<!-- Select if you want a seperate window for WebDav or not!
                           with window:
                                        <input type="button" onclick="javascript:top.callUrl ('{$href}', null, 'empty', 'file', 'toolbar=no,scrollbars=no,directories=no,menubar=no,resizable=yes,width=400,height=200,screenX=,screenY=0');" value="Öffnen"/>
                        wuthiut window:
                                        <input type="button" onclick="javascript:top.callUrl ('{$href}', null, null, 'temp');" value="Öffnen"/>
-->
                                        <input type="button" onclick="javascript:top.callUrl ('{$href}', null, null, 'temp');" value="Öffnen"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:call-template name="getDatatype">
                                            <xsl:with-param name="tagName" select="$tagName"/>
                                            <xsl:with-param name="mode" select="$mode"/>
                                        </xsl:call-template>
                                        <xsl:text>   (ausgechecked von </xsl:text>
                                        <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@USERNAME"/>
                                        <xsl:text> am </xsl:text>
                                        <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@DATE"/>
                                        <xsl:text>)</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="getDatatype">
                                    <xsl:with-param name="tagName" select="$tagName"/>
                                    <xsl:with-param name="mode" select="$mode"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </TD>
                </TR>
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
    </xsl:template>
    <!--infoRow-->
    <!-- ********************** CALLED TEMPLATES END *********************** -->
</xsl:stylesheet>
