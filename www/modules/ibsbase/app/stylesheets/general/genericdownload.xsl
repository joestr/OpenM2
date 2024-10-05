<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
<!--
    <xsl:import href=""/>
-->
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="tab" select="'&#9;'"/>
    <xsl:variable name="nl" select="'&#xa;'"/>
    <xsl:variable name="cr" select="'&#xd;'"/>

    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>


    <!-- ******************************************************************* -->
    <!-- Generate a tab delimited table with the data in the query -->
    <!-- ******************************************************************* -->
    <xsl:template match="/QUERY">

        <xsl:call-template name="createTable">
            <xsl:with-param name="rows" select="./RESULTROW"/>
        </xsl:call-template>

    </xsl:template> <!-- /QUERY -->

    <!-- ******************************************************************* -->
    <!-- Generate a tab delimited table with the data in the query -->
    <!-- with the given rows -->
    <!-- -->
    <!-- @param rows            the rows that contain the data -->
    <!-- @param colDelim        the delimiter character for columns separating -->
    <!-- @param lineDelim       the delimiter character for line separating -->
    <!-- @param includeHeader   include the header line -->
    <!-- @param includeOID      include the OID -->
    <!-- @param excludeRows     a comma separated list with rows to exclude -->
    <!-- ******************************************************************* -->
    <xsl:template name="createTable">
    <xsl:param name="rows" select="/QUERY/RESULTROW"/>
    <xsl:param name="colDelim" select="$tab"/>
    <xsl:param name="lineDelim" select="$nl"/>
    <xsl:param name="includeHeader" select="true()"/>
    <xsl:param name="includeOID" select="true()"/>
    <xsl:param name="excludeRows"/>

<!-- just display the input xml structure -->
<!-->
        <xsl:copy-of select="."/>
-->


        <xsl:if test="$rows">

            <!-- generate the header line -->
            <xsl:if test="$includeHeader">
            
                <xsl:choose>
                <!-- this is the standard case -->
                <xsl:when test="$excludeRows = ''">
                    <xsl:call-template name="createHeader">
                        <xsl:with-param name="rows"
                            select="$rows[1]/RESULTELEMENT
                                [@TYPE != 'SYSVAR']"/>
                        <xsl:with-param name="colDelim" select="$colDelim"/>
                        <xsl:with-param name="lineDelim" select="$lineDelim"/>
                        <xsl:with-param name="includeOID" select="$includeOID"/>
                    </xsl:call-template>
                </xsl:when>
                <!-- excludeRows activated -->
                <xsl:when test="$excludeRows != ''">
                    <xsl:call-template name="createHeader">
                        <xsl:with-param name="rows"
                            select="$rows[1]/RESULTELEMENT
                                [@TYPE != 'SYSVAR' and not (contains($excludeRows, @NAME))]"/>
                        <xsl:with-param name="colDelim" select="$colDelim"/>
                        <xsl:with-param name="lineDelim" select="$lineDelim"/>
                        <xsl:with-param name="includeOID" select="$includeOID"/>
                    </xsl:call-template>
                </xsl:when>
                </xsl:choose>

                <xsl:value-of select="$lineDelim"/>
            </xsl:if>

            <!-- loop through the resultrows and generate the table -->
            <xsl:choose>
            <!-- this is the standard case -->
            <xsl:when test="$excludeRows = ''">
                <xsl:for-each select="$rows">
                    <xsl:call-template name="createData">
                        <xsl:with-param name="rows"
                            select="./RESULTELEMENT[@TYPE != 'SYSVAR']"/>
                        <xsl:with-param name="colDelim" select="$colDelim"/>
                        <xsl:with-param name="includeOID" select="$includeOID"/>
                        <xsl:with-param name="oid" select="./RESULTELEMENT[@NAME='#OBJECTID']"/>
                    </xsl:call-template>
                    <xsl:if test="position() != last()">
                        <xsl:value-of select="$lineDelim"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:when>
            <!-- excludeRows activated -->
            <xsl:when test="$excludeRows != ''">
                <xsl:for-each select="$rows">
                    <xsl:call-template name="createData">
                        <xsl:with-param name="rows"
                            select="./RESULTELEMENT [@TYPE != 'SYSVAR' and
                                    not (contains($excludeRows, @NAME))]"/>
                        <xsl:with-param name="colDelim" select="$colDelim"/>
                        <xsl:with-param name="includeOID" select="$includeOID"/>
                        <xsl:with-param name="oid" select="./RESULTELEMENT[@NAME='#OBJECTID']"/>
                    </xsl:call-template>
                    <xsl:if test="position() != last()">
                        <xsl:value-of select="$lineDelim"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:when>
            </xsl:choose>

        </xsl:if>
    </xsl:template> <!-- createTable -->
    
    
    <!-- ******************************************************************* -->
    <!-- Generate the header line -->
    <!-- -->
    <!-- @param rows           the rows that contain the header data -->
    <!-- @param colDelim       the delimiter character for columns separating -->
    <!-- @param includeOID     include the OID -->
    <!-- ******************************************************************* -->
    <xsl:template name="createHeader">
    <xsl:param name="rows"/>
    <xsl:param name="colDelim" select="$tab"/>
    <xsl:param name="includeOID" select="true()"/>

        <xsl:if test="$includeOID">
            <xsl:value-of select="'OID'"/>
            <xsl:value-of select="$colDelim"/>
        </xsl:if>

        <xsl:for-each select="$rows">
            <xsl:value-of disable-output-escaping="yes" select="./@MLNAME"/>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$colDelim"/>
            </xsl:if>
        </xsl:for-each>

    </xsl:template> <!-- createHeader -->


    <!-- ******************************************************************* -->
    <!-- Generate a data line -->
    <!-- -->
    <!-- @param rows           the rows that contain the header data -->
    <!-- @param colDelim       the delimiter character for columns separating -->
    <!-- @param includeOID     include the OID -->
    <!-- @param oid            the OID -->
    <!-- ******************************************************************* -->
    <xsl:template name="createData">
    <xsl:param name="rows"/>
    <xsl:param name="colDelim" select="$tab"/>
    <xsl:param name="includeOID" select="true()"/>
    <xsl:param name="oid"/>

        <xsl:if test="$includeOID">
            <xsl:value-of select="$oid"/>
            <xsl:value-of select="$colDelim"/>
        </xsl:if>

        <xsl:for-each select="$rows">
            <xsl:value-of disable-output-escaping="yes"
                select="translate(./child::text(), '&#x9;&#xD;&#xA;', ' ')"/>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$colDelim"/>
            </xsl:if>
        </xsl:for-each>

    </xsl:template> <!-- createData -->

    
</xsl:stylesheet>
