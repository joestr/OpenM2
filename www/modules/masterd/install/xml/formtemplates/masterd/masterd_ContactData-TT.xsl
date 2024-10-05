<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="ISO-8859-1" indent="yes" method="xml"/>
    <xsl:template match="/">
        <IMPORT VERSION="1.0">
            <OBJECTS>
                <OBJECT TYPECODE="masterd_ContactData">
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
                        <VALUE FIELD="Contact kind" OPTIONS="Business,Private,UnitContact" TYPE="SELECTIONBOX" EMPTYOPTION="NO">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Contact kind']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Email" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Email']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Phone 1" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Phone 1']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Phone 2" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Phone 2']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Fax" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Fax']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Sms address" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Sms address']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Language" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Language']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Website" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Website']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Accepts mailing" TYPE="BOOLEAN">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Accepts mailing']/child::node()"/>
                        </VALUE>
                    </VALUES>
                    <TABS>
                        <TABOBJECT TYPECODE="masterd_AddressContainer">
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
