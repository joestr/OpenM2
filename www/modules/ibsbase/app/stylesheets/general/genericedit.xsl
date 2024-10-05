<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_EDIT"/>
    <xsl:variable name="showExt" select="/OBJECT/SYSTEM/@SHOWEXT"/>
    <xsl:variable name="checkedOut"
                  select="number (boolean (/OBJECT/SYSTEM/CHECKEDOUT))"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- ************************************************************************
    * Main templates to match the object
    * Note that the <SYSTEM DISPLAY="..." TAG is not supported anymore  
    * -->
    <xsl:template match="/OBJECT">
   
        <TABLE CLASS="info" WIDTH="100%" CELLSPACING="0" CELLPADDING="5">
            <COLGROUP><COL CLASS="name"/></COLGROUP>
            <COLGROUP><COL CLASS="value"/></COLGROUP>
            <THEAD/>
            <TBODY>

                <!-- Create the SYSTEM section -->
                <xsl:call-template name="createSystemSection"/>                
                          
                <!-- Process the VALUES section -->
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tag" select="."/>
                        <xsl:with-param name="localRow" select="position () + 6 + (number ($showExt) * 4) + $checkedOut"/>
                    </xsl:call-template>
                </xsl:for-each>

            </TBODY>
        </TABLE>
        
        <xsl:if test="$mode=$MODE_EDIT">
            <xsl:call-template name="addStandardValidationJS">
                <xsl:with-param name="checkName" select="true()"/>
                <xsl:with-param name="checkDesc" select="true()"/>
                <xsl:with-param name="checkValidUntil" select="true()"/>
            </xsl:call-template>
        </xsl:if>               
        
    </xsl:template> <!-- SYSTEM -->
    
    <!-- ************************************************************************
    * Main template to match the system
    * This is necessary since a lot of Stylesheets call
    * <xsl:apply-templates select="SYSTEM"/>.
    * -->
    <xsl:template match="SYSTEM">
        <!-- Create the SYSTEM section -->
        <xsl:call-template name="createSystemSection"/>
    </xsl:template> <!-- SYSTEM -->
    
    <!--***********************************************************************
     * Creates the complete SYSTEM section. <BR>
     *-->
    <xsl:template name="createSystemSection">

        <xsl:call-template name="addSystemFields">
            <xsl:with-param name="localRow" select="0"/>
            <xsl:with-param name="showName" select="true()"/>
            <xsl:with-param name="showType" select="true()"/>
            <xsl:with-param name="showDesc" select="true()"/>
            <xsl:with-param name="showValidUntil" select="true()"/>
            <xsl:with-param name="showShowInNews" select="true()"/>
            <xsl:with-param name="showCheckedOut" select="true()"/>
        </xsl:call-template> 
        
        <xsl:variable name="localRow" select="1 + $checkedOut"/>
                        
        <xsl:if test="boolean ($showExt)">
            <TR CLASS="infoRow{format-number ((number($localRow + 5) mod 2) + 1, '####')}">
                <TD COLSPAN="2" VALIGN="MIDDLE">
                    <HR SIZE="1"/>
                </TD>
            </TR>                

            <xsl:call-template name="addSystemInfo">
                <xsl:with-param name="localRow" select="$localRow + 1"/>
                <xsl:with-param name="showOwner" select="true()"/>
                <xsl:with-param name="showCreated" select="true()"/>
                <xsl:with-param name="showChanged" select="true()"/>
           </xsl:call-template>            
                                
        </xsl:if>                

        <TR CLASS="infoRow{format-number ((number($localRow + 5) mod 2) + 1, '####')}">
            <TD COLSPAN="2" VALIGN="MIDDLE">
                <HR SIZE="1"/>
            </TD>
        </TR>
    </xsl:template> <!-- createSystemSection -->

        
</xsl:stylesheet>