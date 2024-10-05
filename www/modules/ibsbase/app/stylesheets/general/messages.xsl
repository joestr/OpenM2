<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java"> 
    <!-- ************************** IMPORT BEGIN *************************** -->
    <!-- *************************** IMPORT END **************************** -->

    <!-- mli provider -->
    <xsl:variable name="provider" select="java:ibs.ml.MultilingualTextProvider.new ()"/>
    <!--  resource bundle name -->
    <xsl:variable name="msgbundle" select="'ibs_ibsbase_messages'"/>
    
    <!--*********************** VARIABLES BEGIN ****************************-->
    <!-- messages: -->
    <xsl:variable name="MSG_NOSEARCHRESULTFOUND"
        select="java:getXsltMessage ($provider, $msgbundle, 'ML_MSG_NOSEARCHRESULTFOUND')"/>
    <!--*********************** VARIABLES END ******************************-->
</xsl:stylesheet>
