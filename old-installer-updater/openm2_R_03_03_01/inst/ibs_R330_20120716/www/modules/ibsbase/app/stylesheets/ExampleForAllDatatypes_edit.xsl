<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="general\form_datatypes.xsl"/>

<xsl:variable name="MODE_VIEW" select="0"/>
<xsl:variable name="MODE_EDIT" select="1"/>
<xsl:variable name="MODE_HIDDEN" select="2"/>

<xsl:output method="html" encoding="UTF-8" indent="yes"/>


    <!--********************* CALLED TEMPLATES BEGIN ***********************-->
    <xsl:template name="infoRow">
        <xsl:param name="tagName"/>
        <xsl:param name="localRow"/>
        <xsl:param name="mode"/>
        <xsl:choose>
            <xsl:when test="$tagName/@TYPE = 'SEPARATOR'">
			        <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
						<TD COLSPAN="2" ALIGN="MIDDLE">
							<FONT SIZE="-6">
								<HR/>
							</FONT>
						</TD>
				</TR>
            </xsl:when>
            <xsl:when test="$mode=$MODE_HIDDEN">
			       <TR VALIGN="TOP">
				<TD>
				</TD>
				<TD>
					<xsl:call-template name="getDatatype">
						<xsl:with-param name="tagName" select="$tagName"/>
						<xsl:with-param name="mode" select="$mode"/>
					</xsl:call-template>
				</TD>
			       </TR>
            </xsl:when>

            <xsl:otherwise>
			        <TR VALIGN="TOP" CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
				<TD>
					<xsl:value-of select="$tagName/@FIELD"/>
					<xsl:text>:</xsl:text>
				</TD>
				<TD>
					<xsl:call-template name="getDatatype">
						<xsl:with-param name="tagName" select="$tagName"/>
						<xsl:with-param name="mode" select="$mode"/>
					</xsl:call-template>
				</TD>
			        </TR>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!--infoRow-->
    <!--*********************** CALLED TEMPLATES END ***********************-->


<xsl:template match="/OBJECT">

	<TABLE CLASS="info" BORDERCOLOR="#FFFFFF" WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
		<COLGROUP><COL CLASS="name"/></COLGROUP>
		<COLGROUP><COL CLASS="value"/></COLGROUP>
		<THEAD/>
		<TBODY>
			<xsl:for-each select="VALUES/VALUE">
	        		<xsl:call-template name="infoRow">
	            			<xsl:with-param name="tagName" select="."/>
	            			<xsl:with-param name="localRow" select="position ()"/>
					<xsl:with-param name="mode" select="$MODE_EDIT"/>
	        		</xsl:call-template>
			</xsl:for-each>
		</TBODY>
	</TABLE>
<SCRIPT LANGUAGE="JavaScript"> 
function xslSubmitAllowed()
{
	<xsl:for-each select="VALUES/VALUE">
		<xsl:call-template name="getRestrictions">
			<xsl:with-param name="tagName" select="."/>
		</xsl:call-template>
	</xsl:for-each>

return true;
}</SCRIPT>
</xsl:template>
</xsl:stylesheet>

