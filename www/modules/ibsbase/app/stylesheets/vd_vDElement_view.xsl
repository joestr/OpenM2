<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/genericview.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="MODE" select="$MODE_VIEW"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>      
    
    
</xsl:stylesheet>

