<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general\form_datatypes.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- number format definitions: -->
    <xsl:decimal-format name="de" decimal-separator=","
                        grouping-separator="." NaN="0"/>
    <xsl:decimal-format name="money" decimal-separator=","
                        grouping-separator="." NaN="0"/>

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>


    <!--***********************************************************************
     * This template generates the EURO symbol.
     *-->
    <xsl:template name="createEURO">
        <xsl:text disable-output-escaping="yes">&amp;euro;</xsl:text>
    </xsl:template> <!-- createEURO -->


    <!--***********************************************************************
     * Create an input field that displays an upload button.
     * But instead of uploading the file the path of the file shall be submitted
     *
     * @param   tagName    the name of the value tag to create the info row for
     * @param   tag        the value tag to create the info row for
     *-->
    <xsl:template name="createLinkUpload">
    <xsl:param name="tagName"/>
    <xsl:param name="tag" select="/OBJECT/VALUES/VALUE[@FIELD=$tagName]"/>

        <xsl:variable name="input" select="$tag/@INPUT"/>
        <INPUT TYPE="FILE" NAME="{$input}_UPLOAD" STYLE="display:none"/>
        <INPUT TYPE="TEXT" NAME="{$input}" SIZE="50" VALUE="{$tag/child::text()}"/>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <INPUT TYPE="BUTTON" VALUE="$TOK_UPLOAD"
            ONCLICK="{$input}_UPLOAD.disabled=false;
                     {$input}_UPLOAD.click();
                     {$input}.value='file://' + {$input}_UPLOAD.value.replace(/\\/g,'/');
                     {$input}_UPLOAD.value='';
                     {$input}_UPLOAD.disabled=true;"/>
    </xsl:template> <!-- createLinkUpload -->


    <!--***********************************************************************
     * Get the current date.
     * The format is as defined in $FORMAT_DATE.
     *-->
    <xsl:template name="getCurrentDate">
        <xsl:call-template name="formatDate">
            <xsl:with-param name="date" select="java:java.util.Date.new ()"/>
            <xsl:with-param name="format" select="$FORMAT_TIME"/>
        </xsl:call-template>
    </xsl:template> <!-- getCurrentDate -->


    <!--***********************************************************************
     * Convert a date value into the specified format.
     *
     * @param   date    The date value.
     * @param   format  The format string for java.text.SimpleDateFormat.
     *-->
    <xsl:template name="formatDate">
        <xsl:param name="date"/>
        <xsl:param name="format"/>

        <xsl:variable name="formatter"       
            select="java:java.text.SimpleDateFormat.new ($format)"/>
        <xsl:value-of select="java:format ($formatter, $date)"/>
    </xsl:template> <!-- formatDate -->


    <!--***********************************************************************
     * Convert a date value into a string.
     * This function uses standard formatting.
     *
     * @param   date    The date value.
     *-->
    <xsl:template name="dateToString">
        <xsl:param name="date"/>

        <xsl:value-of select="java:ibs.util.Helpers.dateToString ($date)"/>
    </xsl:template> <!-- dateToString -->


    <!--***********************************************************************
     * Convert a date string to a valid java.util.Date.
     * The date string must be of format "[d]d.[m]m.[yy][y]y".
     *
     * @param   dateStr The date value.
     *-->
    <xsl:template name="stringToDate">
        <xsl:param name="dateStr"/>
        <xsl:value-of select="java:ibs.util.Helpers.stringToDate ($dateStr)"/>
    </xsl:template> <!-- stringToDate -->


    <!--***********************************************************************
     * Compare a date string with the current date.
     * The date string must be of format "[d]d.[m]m.[yy][y]y".
     *
     * @param   dateStr The date value.
     *
     * @return  if dateStr > currentDate then returnvalue > 0
     *          if dateStr = currentDate then returnvalue = 0
     *          if dateStr < currentDate then returnvalue < 0
     *-->
    <xsl:template name="compareToCurrentDate">
        <xsl:param name="dateStr"/>
        <xsl:value-of select="java:ibs.util.Helpers.compareToCurrentDate ($dateStr)"/>
    </xsl:template> <!-- compareWithCurrentDate -->


    <!--***********************************************************************
     * Compute the number of days from now to the given date.
     * The date string must be of format "[d]d.[m]m.[yy][y]y".
     *
     * @param   dateStr The date value.
     *
     * @return  number of days
     *-->
    <xsl:template name="getDaysToDate">
        <xsl:param name="dateStr"/>
        <xsl:value-of select="java:ibs.util.DateTimeHelpers.getDaysToDate ($dateStr)"/>
    </xsl:template> <!-- getDaysToDate -->


    <!--***********************************************************************
     * This template displays a standard info row with certain label
     * and a complex value that will be displayed using xsl:copy-of instead
     * xsl:of value-of
     *
     * @param   label      the label to display
     * @param   value      the value to display
     * @param   localRow   the index of the row (used to generate the stripe layout)
     * @param   hiddenField if set a hidden field that will be generated with
     *                      the hiddenField parameter as NAME attribute and the hiddenValue
     *                      parameter as VALUE attribute
     * @param   hiddenValue the value for the hidden field (default is $value)
     *-->
    <xsl:template name="showCopyInfo">
        <xsl:param name="label"/>
        <xsl:param name="value"/>
        <xsl:param name="localRow" select="'1'"/>
        <xsl:param name="hiddenField" select="''"/>
        <xsl:param name="hiddenValue" select="$value"/>

        <TR CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
            <TD CLASS="name">
                <xsl:value-of select="$label"/><xsl:text>:</xsl:text>
            </TD>
            <TD CLASS="value">
                <xsl:copy-of select="$value"/>
                <xsl:call-template name="createNBSP"/>
            </TD>
        </TR>
        <xsl:if test="$hiddenField != ''">
            <INPUT TYPE="HIDDEN" NAME="{$hiddenField}" VALUE="{$hiddenValue}"/>
        </xsl:if>
    </xsl:template> <!-- showCopyInfo -->
</xsl:stylesheet>