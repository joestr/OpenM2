<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="ISO-8859-1" indent="yes" method="xml"/>
    <xsl:template match="/">
        <IMPORT VERSION="1.0">
            <OBJECTS>
                <OBJECT TYPECODE="m2FileVersion">
                    <SYSTEM>
                        <OID>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/OID/child::node()"/>
                        </OID>
                        <ID DOMAIN="">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/ID/child::node()"/>
                        </ID>
                        <NAME>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/NAME/child::node()"/>
                        </NAME>
                        <DESCRIPTION>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/DESCRIPTION/child::node()"/>
<xsl:value-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/@SIZE"/>
                        </DESCRIPTION>
                        <VALIDUNTIL>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/VALIDUNTIL/child::node()"/>
                        </VALIDUNTIL>
                        <SHOWINNEWS>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/SHOWINNEWS/child::node()"/>
                        </SHOWINNEWS>
                    </SYSTEM>
                    <VALUES>
                        <VALUE FIELD="Dateiname" MANDATORY="YES" TYPE="FILE">
                            <xsl:choose>
                                <xsl:when test="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']">
                                    <xsl:attribute name="SIZE"><xsl:value-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/@SIZE"/></xsl:attribute>
                                    <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/child::node()"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!-- set any default value here -->
                                    <xsl:value-of select="''"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </VALUE>
                    </VALUES>
                    <TABS>
                        <TABOBJECT TYPECODE="m2VersionContainer">
                            <SYSTEM>
                                <OID/>
                                <ID DOMAIN=""/>
                                <NAME/>
                                <DESCRIPTION/>
                                <VALIDUNTIL/>
                                <SHOWINNEWS/>
                            </SYSTEM>
                        </TABOBJECT>
                    </TABS>
                </OBJECT>
            </OBJECTS>
        </IMPORT>
    </xsl:template>
</xsl:stylesheet>
