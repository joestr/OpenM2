<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general\form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
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
    <xsl:variable name="TOK_VALIDUNTIL" select="'Gültig bis'"/>


    <xsl:variable name="TOK_FILE" select="'Datei'"/>
    <xsl:variable name="TOK_FILESIZE" select="'Dateigröße'"/>
    <xsl:variable name="TOK_BYTES" select="'Bytes'"/>
    <xsl:variable name="TOK_VERSION" select="'Version'"/>
    <xsl:variable name="TOK_MASTER" select="'Master'"/>
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
    <xsl:variable name="TOK_VALIDUNTIL" select="'valid until'"/>

    <xsl:variable name="TOK_FILE" select="'file'"/>
    <xsl:variable name="TOK_FILESIZE" select="'filesize'"/>
    <xsl:variable name="TOK_BYTES" select="'Bytes'"/>
    <xsl:variable name="TOK_VERSION" select="'version'"/>
    <xsl:variable name="TOK_MASTER" select="'master'"/>
-->
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!--***********************************************************************
     * The main template. <BR>
     *-->
    <xsl:template match="/OBJECT">
        <!-- display the system specific rows -->
        <xsl:if test="not (boolean(OBJECT/SYSTEM/@DISPLAY))">
            <xsl:call-template name="createSystemInfoRow"/>
            <BR/>
        </xsl:if>
        <!-- display the document specific rows -->
        <xsl:call-template name="createSpecificInfoRows"/>
        <!-- display the system specific rows -->
        <xsl:if test="SYSTEM/@DISPLAY='OBJECT/BOTTOM'">
            <BR/>
            <xsl:call-template name="createSystemInfoRow"/>
        </xsl:if>
    </xsl:template> <!-- /OBJECT -->


    <!--***********************************************************************
     * Create the system rows. <BR>
     *-->
    <xsl:template name="createSystemInfoRow">
        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5" border="1">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>
            <!-- name -->
            <TR VALIGN="TOP" CLASS="infoRow1">
                <TD>
                    <xsl:value-of select="$TOK_NAME"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/NAME"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <!-- description -->
            <TR VALIGN="TOP" CLASS="infoRow2">
                <TD>
                    <xsl:value-of select="$TOK_DESC"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/DESCRIPTION"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <!-- in news -->
            <TR VALIGN="TOP" CLASS="infoRow1">
                <TD>
                    <xsl:value-of select="$TOK_INNEWS"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/INNEWS"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <!-- valid until -->
            <TR VALIGN="TOP" CLASS="infoRow2">
                <TD>
                    <xsl:value-of select="$TOK_VALIDUNTIL"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/VALIDUNTIL"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <!-- checked out -->
            <xsl:if test="boolean (/OBJECT/SYSTEM/CHECKEDOUT)">
                <TR VALIGN="TOP" CLASS="infoRow1">
                    <TD>
                        <xsl:value-of select="$TOK_CHECKEDOUT"/><xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@USERNAME"/>
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@DATE"/>
                    </TD>
                </TR>
            </xsl:if>
        </TABLE>
        <!-- show the extended attributes? -->
        <xsl:if test="$showExt = '1'">
            <TABLE class="info" width="100%" cellspacing="0" cellpadding="5" border="1">
                <COLGROUP>
                    <COL WIDTH="35%" CLASS="name"/>
                    <COL WIDTH="65%" CLASS="value"/>
                </COLGROUP>
                <!-- owner -->
                <TR VALIGN="TOP" CLASS="infoRow2">
                    <TD>
                        <xsl:value-of select="$TOK_OWNER"/><xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:call-template name="view">
                            <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/OWNER/@USERNAME"/>
                        </xsl:call-template>
                    </TD>
                </TR>
                <!-- created -->
                <TR VALIGN="TOP" CLASS="infoRow1">
                    <TD>
                        <xsl:value-of select="$TOK_CREATED"/><xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:value-of select="/OBJECT/SYSTEM/CREATED/@USERNAME"/>
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="/OBJECT/SYSTEM/CREATED/@DATE"/>
                    </TD>
                </TR>
                <!-- changed -->
                <TR VALIGN="TOP" CLASS="infoRow2">
                    <TD>
                        <xsl:value-of select="$TOK_CHANGED"/><xsl:text>:</xsl:text>
                    </TD>
                    <TD>
                        <xsl:value-of select="/OBJECT/SYSTEM/CHANGED/@USERNAME"/>
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="/OBJECT/SYSTEM/CHANGED/@DATE"/>
                    </TD>
                </TR>
            </TABLE>
        </xsl:if>
    </xsl:template> <!-- createSystemInfoRow -->


    <!--***********************************************************************
     * Create the document specific info rows . <BR>
     *-->
    <xsl:template name="createSpecificInfoRows">
        <TABLE CLASS="info" WIDTH="100%" CELLSPACING="0" CELLPADDING="5"
               BORDER="1">
             <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>
<!-- KR 20051007 the file field is not longer used.
            <!- - Dateiname - ->
            <TR VALIGN="TOP" CLASS="infoRow2">
                <TD>
                    <xsl:value-of select="$TOK_FILE"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:variable name="tag" select="/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']"/>
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
                                        <xsl:value-of select="$tag/child::text ()"/>
                                    </xsl:variable>
                                    <A HREF="javascript:top.callUrl ('{$href}', null, 'null', 'temp');" target="temp">
                                        <xsl:value-of select="$tag/child::text ()"/>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        <IMG src="{$layoutPath}images/objectIcons/OpenWebDavDoc.gif" BORDER="0" ALT="Dokument öffnen"/>
                                    </A>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:call-template name="view">
                                        <xsl:with-param name="tagName" select="$tag"/>
                                    </xsl:call-template>
                                    <xsl:text>   (ausgecheckt von </xsl:text>
                                    <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@USERNAME"/>
                                    <xsl:text> am </xsl:text>
                                    <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@DATE"/>
                                    <xsl:text>)</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="view">
                                <xsl:with-param name="tagName" select="$tag"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </TD>
            </TR>
            <!- - Dateigröße - ->
            <TR VALIGN="TOP" CLASS="infoRow1">
                <TD>
                    <xsl:value-of select="$TOK_FILESIZE"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/VALUES/VALUE[@FIELD='Dateigröße']"/>
                    </xsl:call-template>
                    <xsl:value-of select="$TOK_BYTES"/>
                </TD>
            </TR>
-->
            <!-- Version -->
            <TR VALIGN="TOP" CLASS="infoRow2">
                <TD>
                    <xsl:value-of select="$TOK_VERSION"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/VALUES/VALUE[@FIELD='Version']"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <!-- Master -->
            <TR VALIGN="TOP" CLASS="infoRow1">
                <TD>
                    <xsl:value-of select="$TOK_MASTER"/><xsl:text>:</xsl:text>
                </TD>
                <TD>
                    <xsl:call-template name="view">
                        <xsl:with-param name="tagName" select="/OBJECT/VALUES/VALUE[@FIELD='Master']"/>
                    </xsl:call-template>
                </TD>
            </TR>
        </TABLE>

    </xsl:template> <!-- createSpecificInfoRows -->

</xsl:stylesheet>
