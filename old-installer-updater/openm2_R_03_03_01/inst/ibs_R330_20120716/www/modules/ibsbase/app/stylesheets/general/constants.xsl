<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="tokens.xsl"/>
    <xsl:import href="messages.xsl"/>       
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
    <!-- GLOBAL VARIABLES/CONSTANTS: -->
    <!-- language: -->
    <xsl:variable name="lang_en" select="'en'"/>
    <xsl:variable name="lang_de" select="'de'"/>
    <xsl:variable name="lang" select="'#CONFVAR.ibsbase.lang#'"/>
    <xsl:variable name="leadLangMLC" select="'#CONFVAR.ibsbase.leadLangMLC#'"/>
    <xsl:variable name="langsMLC" select="'#CONFVAR.ibsbase.langsMLC#'"/>
    <xsl:variable name="labelsMLC" select="'#CONFVAR.ibsbase.labelsMLC#'"/>
	
    <!-- display modes: -->
    <xsl:variable name="MODE_UNKNOWN" select="-1"/>
    <xsl:variable name="MODE_VIEW" select="0"/>
    <xsl:variable name="MODE_EDIT" select="1"/>
    <xsl:variable name="MODE_HIDDEN" select="2"/>

    <!-- object states: -->
    <xsl:variable name="STATE_UNKNOWN" select="-1"/>
    <xsl:variable name="STATE_DELETED" select="1"/>
    <xsl:variable name="STATE_ACTIVE" select="2"/>
    <xsl:variable name="STATE_CREATED" select="4"/>
        
    <!-- FIELDREF display modes: -->
    <xsl:variable name="FIELDREF_MODE_DEFAULT" select="0"/>
    <xsl:variable name="FIELDREF_MODE_VIEW" select="1"/>
    <xsl:variable name="FIELDREF_MODE_SEARCH" select="2"/>

    <!-- boolean values: -->
    <xsl:variable name="BOOL_TRUE" select="'true'"/>
    <xsl:variable name="BOOL_JA" select="'ja'"/>
    <xsl:variable name="BOOL_NEIN" select="'nein'"/>
    <xsl:variable name="BOOL_YES" select="'yes'"/>
    <xsl:variable name="BOOL_NO" select="'no'"/>
    <xsl:variable name="BOOL_FALSE" select="'false'"/>

    <!-- paths: -->
    <xsl:variable name="FILE" select="'files'"/>
    <xsl:variable name="IMAGE" select="'images'"/>

    <!-- formatting variables: -->
    <xsl:variable name="CR"><xsl:text>
</xsl:text></xsl:variable>
    <!-- date formatting for use by java.text.SimpleDateFormat -->
    <xsl:variable name="FORMAT_DATE" select="'dd.MM.yyyy'"/>
    <xsl:variable name="FORMAT_TIME" select="'HH:mm'"/>
    <xsl:variable name="FORMAT_TIMESTAMP" select="'yyyyMMddHHmmssSSS'"/>


    <!-- CONTEXT-SPECIFIC VARIABLES: -->
    <!-- root object in DOM tree: -->
    <xsl:variable name="root" select="/*"/>

    <!-- object paths: -->
    <xsl:variable name="layoutPath"
                  select="concat('layouts/',$root/@LAYOUT,'/')"/>
    <xsl:variable name="includePath" select="'include/'"/>
    <xsl:variable name="_PATH" select="$root/@UPLOADPATH"/>
    <xsl:variable name="_WWWP" select="$root/@UPLOADURL"/>

    <!-- Objektsuchen-Ablage: -->
    <xsl:variable name="OBJSEARCH" select="'0x0101731100000000'"/>
    
    <!-- Default form name 'sheetForm': -->
    <xsl:variable name="FORM_DEFAULT" select="'sheetForm'"/>
    <!--*********************** VARIABLES END ******************************-->
</xsl:stylesheet>
