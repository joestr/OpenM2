<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java"> 
    <!-- ************************** IMPORT BEGIN *************************** -->
    <!-- *************************** IMPORT END **************************** -->

    <!-- mli provider -->
    <xsl:variable name="provider" select="java:ibs.ml.MultilingualTextProvider.new ()"/>
    <!--  resource bundle name -->
    <xsl:variable name="tokbundle" select="'ibs_ibsbase_tokens'"/>

    <!--*********************** VARIABLES BEGIN ****************************-->
    <!-- search form tokens: -->
    <xsl:variable name="TOK_CONTAINS" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CONTAINS')"/>
    <xsl:variable name="TOK_EXACTLY"
        select="java:getXsltText ($provider, $tokbundle, 'ML_EXACTLY')"/>
    <xsl:variable name="TOK_SIMILAR" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_SIMILAR')"/>
    <xsl:variable name="TOK_GREATER" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_GREATER')"/>
    <xsl:variable name="TOK_GREATER_EQUAL" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_GREATER_EQUAL')"/>
    <xsl:variable name="TOK_LESS" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_LESS')"/>
    <xsl:variable name="TOK_LESS_EQUAL" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_LESS_EQUAL')"/>

    <xsl:variable name="TOK_TOOLTIP_MULTISELECTION" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TOOLTIP_MULTISELECTION')"/>

    <xsl:variable name="TOK_TOOLTIP_MULTISELECTION_SELECTED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_SELECTED')"/>

    <xsl:variable name="TOK_OPEN_QUERY_DEF" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_OPEN_QUERY_DEF')"/>

    <!-- boolean values: -->
    <xsl:variable name="TOK_YES" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_YES')"/>
    <xsl:variable name="TOK_NO" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NO')"/>

    <!-- format definitions: -->
    <xsl:variable name="TOK_DATEFORMAT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_DATEFORMAT')"/>
    <xsl:variable name="TOK_TIMEFORMAT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TIMEFORMAT')"/>

    <!-- button texts: -->
    <xsl:variable name="TOK_SEARCH" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_SEARCH')"/>
    <xsl:variable name="TOK_DELETE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_DELETE')"/>
    <xsl:variable name="TOK_PRINT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_PRINT')"/>
    <xsl:variable name="TOK_CLOSE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CLOSE')"/>
    <xsl:variable name="TOK_CLOSE_SEARCHRESULT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CLOSE_SEARCHRESULT')"/>
    <xsl:variable name="TOK_CHANGESEARCH" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CHANGESEARCH')"/>
    <xsl:variable name="TOK_DOWNLOAD" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_DOWNLOAD')"/>
    <xsl:variable name="TOK_OPENINNEWWINDOW" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_OPENINNEWWINDOW')"/>
    <xsl:variable name="TOK_ENLARGE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_ENLARGE')"/>
    <xsl:variable name="TOK_REDUCE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REDUCE')"/>
    <xsl:variable name="TOK_PICKDATE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_PICKDATE')"/>
    <xsl:variable name="TOK_NO_OPTION_SELECTED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NO_OPTION_SELECTED')"/>
    <xsl:variable name="TOK_UPLOAD" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_UPLOAD')"/>

    <!-- default values: -->
    <xsl:variable name="TOK_NOTDEFINED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NOTDEFINED')"/>
    <xsl:variable name="TOK_NOTDEFINEDLIST" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NOTDEFINEDLIST')"/>
    <xsl:variable name="TOK_NOTDEFINEDNUM" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NOTDEFINEDNUM')"/>
    <xsl:variable name="TOK_NOTDEFINEDNUMLIST" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NOTDEFINEDNUMLIST')"/>

    <!-- system values: -->
    <xsl:variable name="TOK_NAME" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_NAME')"/>
    <xsl:variable name="TOK_TYPE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TYPE')"/>
    <xsl:variable name="TOK_INNEWS" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_INNEWS')"/>
    <xsl:variable name="TOK_DESC" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_DESC')"/>
    <xsl:variable name="TOK_EXPON" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_EXPON')"/>
    <xsl:variable name="TOK_CHECKEDOUT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CHECKEDOUT')"/>
    <xsl:variable name="TOK_OWNER" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_OWNER')"/>
    <xsl:variable name="TOK_CREATED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CREATED')"/>
    <xsl:variable name="TOK_CHANGED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_CHANGED')"/>

    <!-- values for reminder: -->
    <xsl:variable name="TOK_REMHEAD_REMIND1" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REMHEAD_REMIND1')"/>
    <xsl:variable name="TOK_REMHEAD_REMIND2" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REMHEAD_REMIND2')"/>
    <xsl:variable name="TOK_REMHEAD_ESCALATE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REMHEAD_ESCALATE')"/>
    <xsl:variable name="TOK_REM_DAYS" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REM_DAYS')"/>
    <xsl:variable name="TOK_REM_RECIP" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REM_RECIP')"/>
    <xsl:variable name="TOK_REM_TEXT" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REM_TEXT')"/>
    
    <!-- values for calendarPopup: -->
    <xsl:variable name="TOK_MONTH_NAMES" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_MONTH_NAMES')"/>
    <xsl:variable name="TOK_DAY_HEADERS" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_DAY_HEADERS')"/>
    <xsl:variable name="TOK_TODAY" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TODAY')"/>

    <!-- values for fieldref: -->  
    <xsl:variable name="TOK_FIELDREF_SEARCH_HELP_1" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_SEARCH_HELP_1')"/>
    <xsl:variable name="TOK_FIELDREF_SEARCH_HELP_2" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_SEARCH_HELP_2')"/>
    <xsl:variable name="TOK_FIELDREF_EXT_SEARCH" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_EXT_SEARCH')"/>
    <xsl:variable name="TOK_FIELDREF_EXT_SEARCH_PARAMS_TITLE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_EXT_SEARCH_PARAMS_TITLE')"/>
    <xsl:variable name="TOK_FIELDREF_ASSIGN" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_ASSIGN')"/>
    <xsl:variable name="TOK_FIELDREF_ASSIGN_TITLE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FIELDREF_ASSIGN_TITLE')"/>

    <!-- values for objectref: -->
    <xsl:variable name="TOK_OBJECTREF_ASSIGN"  
        select="java:getXsltText ($provider, $tokbundle, 'ML_OBJECTREF_ASSIGN')"/>
    <xsl:variable name="TOK_OBJECTREF_ASSIGN_TITLE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_OBJECTREF_ASSIGN_TITLE')"/>
    <xsl:variable name="TOK_OBJECTREF_SEARCH_HELP" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_OBJECTREF_SEARCH_HELP')"/>

    <!-- values for multi lingual content: -->
    <xsl:variable name="TOK_TRANSLATE" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TRANSLATE')"/>
    <xsl:variable name="TOK_TRANSLATE_TO" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TRANSLATE_TO')"/>
	<xsl:variable name="TOK_TRANSLATE_SHOW" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_TRANSLATE_SHOW')"/>

    <!-- values for reporting engine: -->
    <xsl:variable name="TOK_ACTIVATED" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_ACTIVATED')"/>
    <xsl:variable name="TOK_INVOCATIONURL" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_INVOCATIONURL')"/>
    <xsl:variable name="TOK_REPORTDIR" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_REPORTDIR')"/>
    <xsl:variable name="TOK_FILEEXTENSION" 
        select="java:getXsltText ($provider, $tokbundle, 'ML_FILEEXTENSION')"/>
    
    <!--*********************** VARIABLES END ******************************-->
</xsl:stylesheet>
