<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output encoding="ISO-8859-1" indent="yes" method="xml"/>
    <xsl:template match="/">
        <IMPORT VERSION="1.0">
            <OBJECTS>
                <OBJECT TYPECODE="masterd_Person">
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
                        <VALUE FIELD="Salutation" OPTIONS="Mr.,Mrs.,Ms." TYPE="SELECTIONBOX" EMPTYOPTION="NO">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Salutation']/child::node()"/>
                        </VALUE>
                        <VALUE CONTEXT="Title" FIELD="Title" MANDATORY="NO" READONLY="NO" REFRESH="YES" TYPE="VALUEDOMAIN">
		    	        	<FIELDS>
		        		      <SYSFIELD NAME="Name" TOKEN="Name"/> 
				            </FIELDS>
                        </VALUE>
                        <VALUE FIELD="First name" MANDATORY="YES" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'First name']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Surname" MANDATORY="YES" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Surname']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Nationality" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Nationality']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Birhtdate" TYPE="DATE">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Birhtdate']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Gender" TYPE="TEXT">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Gender']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="Social Insurence No" TYPE="NUMBER">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'Social Insurence No']/child::node()"/>
                        </VALUE>
                        <VALUE FIELD="User" SEARCHRECURSIVE="YES" SEARCHROOT="Administration/user administration/USERS" TYPE="OBJECTREF" TYPECODEFILTER="User">
                            <xsl:copy-of select="/IMPORT/OBJECTS/OBJECT/VALUES/VALUE[@FIELD = 'User']/child::node()"/>
                        </VALUE>
                    </VALUES>
                    <TABS>
                        <TABOBJECT TYPECODE="masterd_ContactDataContainer">
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
