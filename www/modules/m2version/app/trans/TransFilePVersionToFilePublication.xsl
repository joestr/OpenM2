<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="ISO-8859-1"/>
	<xsl:template match="/OBJECT">
	    <IMPORT VERSION="1.0">
		    <OBJECTS>
		        <OBJECT TYPECODE="m2FilePublication">
		            <SYSTEM>
		                <xsl:copy-of select="SYSTEM/ID"/>
		                <xsl:copy-of select="SYSTEM/NAME"/>
		                <xsl:copy-of select="SYSTEM/DESCRIPTION"/>
		                <xsl:copy-of select="SYSTEM/VALIDUNTIL"/>
                        <SHOWINNEWS>true</SHOWINNEWS>
		            </SYSTEM>
		            <VALUES>
		                <VALUE FIELD="OObjekt" TYPE="OBJECTREF">
		                    <xsl:value-of select="SYSTEM/OID"/><xsl:text>,</xsl:text>
		                    <xsl:value-of select="SYSTEM/NAME"/>
		               </VALUE>
		            </VALUES>
		        </OBJECT> 
			</OBJECTS>
		</IMPORT>
	</xsl:template>
</xsl:stylesheet>
