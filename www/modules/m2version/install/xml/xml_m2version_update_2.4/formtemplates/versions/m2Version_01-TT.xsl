<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="ISO-8859-1" indent="yes" method="xml"/>
    <xsl:template match="/">
        <IMPORT VERSION="1.0">
            <OBJECTS>
                <OBJECT TYPECODE="m2Version">
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
                        </DESCRIPTION>
                        <VALIDUNTIL>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/VALIDUNTIL/child::node()"/>
                        </VALIDUNTIL>
                        <SHOWINNEWS>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/SYSTEM/SHOWINNEWS/child::node()"/>
                        </SHOWINNEWS>
                    </SYSTEM>
                    <VALUES>
                        <VALUE FIELD="Dateiname" MANDATORY="NO" TYPE="FILE">
                            <xsl:attribute name="SIZE">
                                <xsl:choose>
                                    <xsl:when test="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/@SIZE">
                                        <xsl:value-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/@SIZE"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:choose>
                                            <xsl:when test="number(/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateigröße']/child::node())>=0">
                                                <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateigröße']/child::node()"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:text>-1</xsl:text>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateiname']/child::node()"/>
                        </VALUE>

                        <VALUE FIELD="Dateigröße" TYPE="INTEGER" UNIT="Bytes">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Dateigröße']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Version" TYPE="INTEGER">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Version']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Master" TYPE="BOOLEAN">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Master']/child::node()"/>
                        </VALUE>
                    </VALUES>
                    <TABS>
                        <TABOBJECT TYPECODE="m2Version">
                            <SYSTEM>
                                <OID/>
                                <ID DOMAIN=""/>
                                <NAME>Dokument</NAME>
                                <DESCRIPTION>Hier ist das versionierte Objekt zu finden.</DESCRIPTION>
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
