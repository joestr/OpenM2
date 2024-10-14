<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/genericedit.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
 
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">
        <TABLE CLASS="info" WIDTH="100%" BORDER="0"
               CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
            <COLGROUP><COL CLASS="name"/></COLGROUP>
            <COLGROUP><COL CLASS="value"/></COLGROUP>
            <THEAD/>
            <TBODY>          
                 <xsl:call-template name="infoRow">
                        <xsl:with-param name="tag" select="VALUES/VALUE[@FIELD='Material data sheet']"/>
                        <xsl:with-param name="localRow" select="'1'"/>
                        <xsl:with-param name="mode" select="$MODE_EDIT"/>
                 </xsl:call-template>

                <xsl:if test="SYSTEM/@DISPLAY='BOTTOM'">
                    <xsl:apply-templates select="SYSTEM"/>
                </xsl:if>
                
        		<!-- Name als Hidden-Field hinzufügen -->
        		<INPUT type="HIDDEN" name="{SYSTEM/NAME/@INPUT}" value="{SYSTEM/NAME/child::text()}"/>

            </TBODY>
        </TABLE>    
        <SCRIPT LANGUAGE="JavaScript">
            function xslSubmitAllowed()
            {        

                <xsl:if test="not (boolean(SYSTEM/@DISPLAY)) or
                              SYSTEM/@DISPLAY='BOTTOM'">
                    if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>, false)) return false;
                </xsl:if>

                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="getRestrictions">
                        <xsl:with-param name="tagName" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                
                //Set the company name to the system name field. For displaying in the container content view.
                document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>.value =
	                document.sheetForm.<xsl:value-of select="VALUES/VALUE[@FIELD='Material data sheet']/@INPUT"/>_0.value
                
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
    </xsl:template><!--addJavascript-->
</xsl:stylesheet>