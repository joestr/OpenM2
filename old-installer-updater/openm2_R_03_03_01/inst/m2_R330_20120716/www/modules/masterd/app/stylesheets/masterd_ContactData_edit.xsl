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
                     
        		<!-- Name als Hidden-Field hinzufügen -->
        		<INPUT type="HIDDEN" name="{SYSTEM/NAME/@INPUT}" value="{SYSTEM/NAME/child::text()}"/>
        
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tag" select="."/>
                        <xsl:with-param name="localRow" select="position ()"/>
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
                 var contactKind = '';
                 var email = '';
            
                <xsl:if test="not (boolean(SYSTEM/@DISPLAY)) or
                              SYSTEM/@DISPLAY='BOTTOM'">
                    if (!top.iTx (document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>, false)) return false;
                </xsl:if>

                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="getRestrictions">
                        <xsl:with-param name="tagName" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                
                contactKind = document.sheetForm.<xsl:value-of select="VALUES/VALUE[@FIELD='Contact kind']/@INPUT"/>.value;
                email = document.sheetForm.<xsl:value-of select="VALUES/VALUE[@FIELD='Email']/@INPUT"/>.value;

                document.sheetForm.<xsl:value-of select="SYSTEM/NAME/@INPUT"/>.value = contactKind+' ('+email+')';

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