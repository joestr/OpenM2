<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="ISO-8859-1" indent="yes" method="xml"/>
    <xsl:template match="/">
        <IMPORT VERSION="1.0">
            <OBJECTS>
                <OBJECT TYPECODE="DBConnector">
                    <SYSTEM>
                        <OID/>
                        <ID DOMAIN=""/>
                        <NAME/>
                        <DESCRIPTION/>
                        <VALIDUNTIL/>
                        <SHOWINNEWS/>
                    </SYSTEM>
                    <VALUES>
                        <VALUE FIELD="type" MANDATORY="YES" OPTIONS="ora805,sqls65,db2" TYPE="SELECTIONBOX">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;type&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="jdbcdriverclass" MANDATORY="YES" TYPE="CHAR">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;jdbcdriverclass&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="jdbcconnectionstring" MANDATORY="YES" TYPE="CHAR">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;jdbcconnectionstring&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="sid" MANDATORY="YES" TYPE="CHAR">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;sid&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="user" MANDATORY="NO" TYPE="CHAR">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;user&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="password" MANDATORY="NO" TYPE="PASSWORD">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;password&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="logintimeout" MANDATORY="YES" TYPE="INTEGER">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;logintimeout&apos;]/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="querytimeout" MANDATORY="YES" TYPE="INTEGER">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = &apos;querytimeout&apos;]/child::node()"/>
                        </VALUE>
                    </VALUES>
                </OBJECT>
            </OBJECTS>
        </IMPORT>
    </xsl:template>
</xsl:stylesheet>
