<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/constants.xsl"/>
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
    </xsl:call-template>
  </xsl:for-each>
</TBODY>
</TABLE>
<xsl:call-template name="addScripts"/>

</xsl:template>

<!-- ************************************************************
 * @name		showHeader
 * @description	show header for the object list. 
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
    <xsl:variable name="headerPosition" select="position()"/>

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
 * @name		showObject
 * @description	show one object in list
-->
<xsl:template name="showObject">
    <xsl:param name="localRow"/>
<xsl:variable name="tdclass">listRow<xsl:value-of select="format-number (($localRow mod 2) + 1, '####')"/>
</xsl:variable>

    <TR><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
    <TD><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
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
        <xsl:if test="@ICON!=''">
        <IMG BORDER="0">
        <xsl:attribute name="SRC"><xsl:value-of select="$layoutPath"/><xsl:text>images/objectIcons/</xsl:text><xsl:value-of select="@ICON"/></xsl:attribute>
        </IMG>
        </xsl:if>
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
    <xsl:for-each select="VALUES/VALUE">
    <TD><xsl:attribute name="CLASS"><xsl:value-of select="$tdclass"/></xsl:attribute>
    <xsl:call-template name="showValue">
        <xsl:with-param name="tag" select="."/>
    </xsl:call-template>
    </TD>
    </xsl:for-each>
    </TR>
</xsl:template>


<!-- ************************************************************
 * @name		showValue
 * @description	show one value in list
-->
<xsl:template name="showValue">
    <xsl:param name="tag"/>

    <xsl:variable name="valueType" select="$tag/@TYPE"/>
    <xsl:variable name="value" select="$tag/child::text()"/>
    <xsl:variable name="multiple" select="translate ($tag/@MULTIPLE, 'YES', 'yes')"/>

    <xsl:choose>
        <xsl:when test="$valueType='BOOLEAN'">
            <xsl:call-template name="showBoolValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='BUTTON'">
            <xsl:call-template name="showButtonValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='EMAIL'">
            <xsl:call-template name="showMailValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='FILE'">
            <xsl:call-template name="showFileValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='IMAGE'">
            <xsl:call-template name="showImageValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='LINK'">
            <xsl:call-template name="showLinkValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:when test="$valueType='OBJECTREF' or $valueType='FIELDREF' or ($valueType='VALUEDOMAIN' and $multiple!=$BOOL_YES)">
            <xsl:call-template name="showRefValue">
                <xsl:with-param name="tag" select="$tag"/>
            </xsl:call-template>
        </xsl:when>
		<xsl:when test="$multiple=$BOOL_YES and ($valueType='VALUEDOMAIN' or $valueType='SELECTIONBOX' or $valueType='QUERYSELECTIONBOX')">
            <SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
        				var str = "<xsl:value-of select="$value"/>";
        				<![CDATA[
        				<!--
        				//replace '|' with ', '
        				document.write (str.replace (/\|/g, ", "));
        				//-->
        				]]>
    				</SCRIPT>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$value"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<!-- ************************************************************
 * @name		showBoolValue
 * @description	show one value in list
-->
<xsl:template name="showBoolValue">
    <xsl:param name="tag"/>
BOOL
</xsl:template>


<!-- ************************************************************
 * @name		showButtonValue
 * @description	show one value in list
-->
<xsl:template name="showButtonValue">
    <xsl:param name="tag"/>

</xsl:template>


<!-- ************************************************************
 * @name		showMailValue
 * @description	show one value in list
-->
<xsl:template name="showMailValue">
    <xsl:param name="tag"/>
    <a><xsl:attribute name="href">mailto:<xsl:value-of select="$tag/child::text ()"/></xsl:attribute>
    <xsl:value-of select="$tag/child::text ()"/></a>
</xsl:template>

<!-- ************************************************************
 * @name		showFileValue
 * @description	show one value in list
-->
<xsl:template name="showFileValue">
    <xsl:param name="tag"/>

<!-- AJ  ->  M2 ROOT IS MISSING -->
    <a><xsl:attribute name="href">javascript:top.loadFile ('/mssql/upload/files/<xsl:value-of select="../../SYSTEM/OID"/>/<xsl:value-of  select="$tag/child::text ()"/>');</xsl:attribute>
    <xsl:value-of select="$tag/child::text ()"/></a>
</xsl:template>



<!-- ************************************************************
 * @name		showImageValue
 * @description	show one value in list
-->
<xsl:template name="showImageValue">
    <xsl:param name="tag"/>

<!-- AJ  ->  M2 ROOT IS MISSING -->
    <a><xsl:attribute name="href">javascript:top.showObject ('<xsl:value-of select="../../SYSTEM/OID"/>');</xsl:attribute>
    <IMG BORDER="0"><xsl:attribute name="SRC">/mssql/upload/images/<xsl:value-of select="$tag/child::text ()"/></xsl:attribute></IMG>
    </a>

</xsl:template>

<!-- ************************************************************
 * @name		showLinkValue
 * @description	show one value in list
-->
<xsl:template name="showLinkValue">
    <xsl:param name="tag"/>

    <a><xsl:attribute name="href"><xsl:value-of  select="$tag/child::text ()"/></xsl:attribute>
    <xsl:value-of select="$tag/child::text ()"/></a>

</xsl:template>


<!-- ************************************************************
 * @name		showRefValue
 * @description	show one value in list
-->
<xsl:template name="showRefValue">
    <xsl:param name="tag"/>

    <xsl:variable name="refOid" select="substring-before($tag/child::text(),',')"/>
    <xsl:variable name="refName" select="substring-after($tag/child::text(),',')"/>
    <a><xsl:attribute name="href">javascript:top.showObject('<xsl:value-of select="$refOid"/>');</xsl:attribute>
    <xsl:value-of select="$refName"/></a>

</xsl:template>


<!-- ************************************************************
 * @name		addScripts
 * @description	show one object in list
-->
<xsl:template name="addScripts">

	<SCRIPT LANGUAGE="JavaScript">
	function orderBy (position, orderHow)
	{
	top.scripts.callUrl (21, '&amp;oid=' + top.oid + '&amp;oBy=' + position + '&amp;oHow=' + orderHow + '&amp;rord=1&amp;frs=false', 	null, 'sheet');
	}
	</SCRIPT>

</xsl:template>


</xsl:stylesheet>
