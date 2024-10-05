<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/constants.xsl"/>
    <xsl:import href="general/form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
    <xsl:variable name="layoutPath">layouts/<xsl:value-of select="/OBJECTS/@LAYOUT"/>/</xsl:variable>
    <!--*********************** VARIABLES END ******************************-->

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/OBJECTS">
    
        <TABLE CLASS="list" WIDTH="100%" CELLSPACING="0" CELLPADDING="0" FRAME="VOID">
        <THEAD>
        
         <xsl:call-template name="showHeader"/>
        
        </THEAD>
        <TBODY>
          <xsl:for-each select="OBJECT">
          
            <xsl:call-template name="showObject"> 
                <xsl:with-param name="localRow" select="position ()"/>
                <xsl:with-param name="mode" select="$MODE_EDIT"/>
            </xsl:call-template>
          </xsl:for-each>
          
          <!-- create the form validation javascript -->
          <xsl:call-template name="createValidationJS"/>
          
        </TBODY>
        </TABLE>
        <xsl:call-template name="addScripts"/>
    </xsl:template>

<!-- ************************************************************
 * @name        showHeader
 * @description show header for the object list. 
 * get header names from first object
-->
<xsl:template name="showHeader">

<!-- initialize local variables -->
<xsl:variable name="orderBy" select="number(/OBJECTS/@ORDERBY)"/>
<!-- initialize order how for next sort of queryresult -->
<xsl:variable name="orderHow" select="/OBJECTS/@ORDERHOW"/>

<!-- HEADER -->
<xsl:variable name="headerPosition" select="number(0)"/>
    <TR CLASS="listheader" VALIGN="TOP">
    <TD CLASS="listheader"/>

<!-- NAME -->
    <TD CLASS="listheader">
    <xsl:choose>
    <xsl:when test="$headerPosition = $orderBy and $orderHow = 'ASC'">
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('0','DESC');
        </xsl:attribute>
        Name
        </A>
        <IMG SRC="{$layoutPath}images/global/OrderASC.gif" BORDER="0"/>
    </xsl:when>
    <xsl:when test="$headerPosition = $orderBy and $orderHow = 'DESC'">
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('0','ASC');
        </xsl:attribute>
        Name
        </A>
        <IMG SRC="{$layoutPath}images/global/OrderDESC.gif" BORDER="0"/>
    </xsl:when>
    <xsl:otherwise>
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('0','ASC');
        </xsl:attribute>
        Name
        </A>
    </xsl:otherwise>
    </xsl:choose>
    </TD>

<!-- VALUES -->
<xsl:for-each select="/OBJECTS/OBJECT[position()=1]/VALUES/VALUE">
    <xsl:variable name="headerPosition" select="position ()"/>

    <TD CLASS="listheader">
    <xsl:choose>
    <xsl:when test="$headerPosition = $orderBy and $orderHow = 'ASC'">
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('<xsl:value-of select="position ()"/>','DESC');
        </xsl:attribute>
        <xsl:value-of select="@FIELD"/>
        </A>
        <IMG SRC="{$layoutPath}images/global/OrderASC.gif" BORDER="0"/>
    </xsl:when>
    <xsl:when test="$headerPosition = $orderBy and $orderHow = 'DESC'">
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('<xsl:value-of select="position ()"/>','ASC');
        </xsl:attribute>
        <xsl:value-of select="@FIELD"/>
        </A>
        <IMG SRC="{$layoutPath}images/global/OrderDESC.gif" BORDER="0"/>
    </xsl:when>
    <xsl:otherwise>
        <A CLASS="listheader">
        <xsl:attribute name="HREF">javascript:orderBy ('<xsl:value-of select="position ()"/>','ASC');
        </xsl:attribute>
        <xsl:value-of select="@FIELD"/>
        </A>
    </xsl:otherwise>
    </xsl:choose>
    </TD>

</xsl:for-each>
    </TR>
</xsl:template>


    <!-- ************************************************************
     * @name        showObject
     * @description show one object in list
     *
     * @param   localRow    the index of the row (used to generate the stripe
     *                      layout
     * @param   mode        the display mode (default is the mode set in the
     *                      global mode var)
    -->
    <xsl:template name="showObject">
        <xsl:param name="localRow"/>
        <xsl:param name="mode" select="$MODE_VIEW"/>

        <xsl:variable name="tdclass">listRow<xsl:value-of select="format-number (($localRow mod 2) + 1, '####')"/>
        </xsl:variable>
    
        <TR><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
        <TD width="13"><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
        <xsl:if test="SYSTEM/@ISNEW='YES'">
        <IMG BORDER="0" SRC="images/global/new.gif"/>
        </xsl:if>
        </TD>
    
    <!-- FIRST ELEMENT -->
        <TD><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
    
        <!-- OBJECTID -->
        <xsl:choose>
        <xsl:when test="SYSTEM/OID!=''">
        <A>
            <xsl:attribute name="HREF"><xsl:text>javascript:top.showObject ('</xsl:text><xsl:value-of select="SYSTEM/OID"/><xsl:text>');</xsl:text></xsl:attribute>
        <!-- TYPEIMAGE -->
            <IMG BORDER="0">
            <xsl:attribute name="SRC"><xsl:value-of select="$layoutPath"/><xsl:text>images/objectIcons/SubmissionOrder.gif</xsl:text></xsl:attribute>
            </IMG>
        <!-- VALUE -->
            <xsl:value-of select="SYSTEM/NAME"/>
            </A>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="SYSTEM/NAME"/>
        </xsl:otherwise>
        </xsl:choose>
        </TD>
    
        <!-- FOLLOWING ELEMENTS -->
        <xsl:call-template name="showValues">
            <xsl:with-param name="values" select="VALUES"/>
            <xsl:with-param name="tdClass" select="$tdclass"/>
            <xsl:with-param name="mode" select="$mode"/>
        </xsl:call-template>

        </TR>
    </xsl:template> <!-- showObject -->


    <!-- ************************************************************
     * @name        showValues
     * @description show all value nodes
    -->
    <xsl:template name="showValues">
        <xsl:param name="values"/>
        <xsl:param name="tdclass"/>
        <xsl:param name="mode"/>
            <xsl:for-each select="$values/VALUE">
        <TD><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
                    <xsl:call-template name="getDatatype">
                        <xsl:with-param name="tagName" select="."/>
                        <xsl:with-param name="mode" select="$MODE_EDIT"/>
                    </xsl:call-template>
        </TD>
        </xsl:for-each>
    </xsl:template>


<!-- ************************************************************
 * @name        addScripts
 * @description show one object in list
-->
    <xsl:template name="addScripts">
    
        <SCRIPT LANGUAGE="JavaScript">
        function orderBy (position, orderHow)
        {
            top.scripts.callUrl (154, '&amp;oid=' + top.oid + '&amp;oBy=' + position + '&amp;oHow=' + orderHow + '&amp;rord=1&amp;frs=false',    null, 'sheet');
        }
        </SCRIPT>
    
    </xsl:template> <!-- addScripts -->


    <!--***********************************************************************
     * This template generates the form validation javascript. <BR>
     *-->
    <xsl:template name="createValidationJS">
        <SCRIPT LANGUAGE="JavaScript">
            function xslSubmitAllowed()
            {
                
                  <xsl:for-each select="OBJECT">
                    <xsl:for-each select="VALUES/VALUE">
                        <xsl:call-template name="getRestrictions">
                            <xsl:with-param name="tagName" select="."/>
                        </xsl:call-template>
                    </xsl:for-each>

                  </xsl:for-each>

                return true;
            }
        </SCRIPT>
    </xsl:template> <!-- createValidationJS -->
    
</xsl:stylesheet>