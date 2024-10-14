<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="form_datatypes.xsl"/>
    <xsl:import href="multilang.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <!-- Name of the user 'Administrator': -->
    <xsl:variable name="USER_ADMINISTRATOR" select="'Administrator'"/>
    <!-- QueryCreator variables: -->
    <!-- Name of the hidden input field of the query creator oid 'qcroid': -->
    <xsl:variable name="INPUT_QUERYCREATOROID" select="'qcroid'"/>
    <!-- Value of the query creator oid: -->
    <xsl:variable name="QUERYCREATOR_OID" select="QUERY/QUERYCREATOR/SYSTEM/OID/child::text()"/>
    <!-- String constants: -->
    <!-- MatchTypes: -->
    <!-- Text field default size: -->
    <xsl:variable name="TEXT_DEFAULT_SIZE" select="'30'"/>
    <!-- Text field default maxlength: -->
    <xsl:variable name="TEXT_DEFAULT_MAXLENGTH" select="'255'"/>
    <!-- DATE field default maxlength: -->
    <xsl:variable name="DATE_DEFAULT_MAXLENGTH" select="'10'"/>
    <!-- DATE field default size: -->
    <xsl:variable name="DATE_DEFAULT_SIZE" select="$DATE_DEFAULT_MAXLENGTH"/>
    <!-- Range field postfix: -->
    <xsl:variable name="RANGE_FIELD_POSTFIX" select="'_R'"/>
    <!-- MatchType postfix: -->
    <xsl:variable name="MATCHTYPE_FIELD_POSTFIX" select="'_M'"/>

    <!-- Multiselectionbox: -->
    <xsl:variable name="QUERYSELECTIONBOX_MULTIPLE" select="'MULTIPLE'"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>


    <!-- ********************* APPLIED TEMPLATES BEGIN ********************* -->
    <!-- *************************** MAIN BEGIN **************************** -->
    <!-- **************************************************************************
                * This is the general template for a query, which applies, when template
                * match /QUERY.
                * 
                -->
    <xsl:template match="/QUERY">
<!--
        <A CLASS="name">Generic m2 stylesheet</A>
-->
        <TABLE CLASS="info" WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="0" FRAME="VOID" RULES="NONE">
            <COLGROUP><COL CLASS="name"/></COLGROUP>
            <COLGROUP><COL CLASS="value"/></COLGROUP>
            <THEAD/>
            <TBODY>
                <!-- the system informations of the query object was never shown -->
                <!-- 
                                            <xsl:if test="not (boolean(SYSTEM/@DISPLAY))">
                                                <xsl:apply-templates select="SYSTEM"/>
                                            </xsl:if>
                                     -->
                <!-- show 'Open query definition' button, if user is of type administrator -->
                <xsl:call-template name="getQueryCreatorData"/>
                <!-- show field for each value -->
                <xsl:for-each select="VALUES/VALUE">
                    <xsl:call-template name="infoRow">
                        <xsl:with-param name="tagName" select="."/>
                        <xsl:with-param name="localRow" select="position ()"/>
                        <xsl:with-param name="mode" select="$MODE_EDIT"/>
                    </xsl:call-template>
                </xsl:for-each>
                <!-- the system informations of the query object was never shown -->
                <!-- 
                                            <xsl:if test="SYSTEM/@DISPLAY='BOTTOM'">
                                                <xsl:apply-templates select="SYSTEM"/>
                                            </xsl:if>
                                     -->
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
            }

            <![CDATA[
            function getdObjecRef (incompleteURL, objectRef,
                                   objectRef_M, objectRefName)
            {
                var completeURL = incompleteURL;

                completeURL += '&' + objectRefName + '=' + escape (objectRef);
                completeURL += '&' + objectRefName + '_M=' + objectRef_M;

                return completeURL;
            } // getEscapedObjecRef
            ]]><!--CDATA-->
        </SCRIPT>
    </xsl:template> <!-- QUERY -->
    <!-- **************************** MAIN END ***************************** -->

    <!-- ********************* CALLED TEMPLATES BEGIN ********************** -->
    <!-- **************************************************************************
             * This is the getQueryCreatorData template to display all the specific data 
             * of the QueryCreator_01.
             * 
             * @param callerTokenBundle		the token bundle to translate form labels, optional
		     * @param tokenHead				for translating terms taken from the DOM tree:
		   	 *								the first characters of the resource bundle keys *before* the DOM-term starts (e.g. 'stylesheet-name.QY_')
		   	 *								use is optional
		   	 * @param tokenTail				for translating terms taken from the DOM tree:
		   	 *								the characters *after* the DOM-term (default value: '.NAME')
		   	 *								optional, again
	-->
    <xsl:template name="getQueryCreatorData">
    	<xsl:param name="callerTokenBundle" select="''"/>
        <xsl:param name="tokenHead"/>
    	<xsl:param name="tokenTail" select="'.NAME'"/>
    	
    	<xsl:variable name="queryName" select="QUERYCREATOR/SYSTEM/NAME/child::text()"/>
    	<xsl:variable name="descriptionText" select="QUERYCREATOR/SYSTEM/DESCRIPTION/child::text()"/>
    	
        <TR VALIGN="TOP">
            <TD CLASS="name" COLSPAN="2" ALIGN="LEFT">
                <!-- show query name -->
                <SPAN CLASS="name">
                	<!-- Display the query name -->
                	<xsl:choose>
                		<xsl:when test="$callerTokenBundle = ''">
                			<xsl:value-of select="$queryName"/>
                		</xsl:when>
                		<xsl:otherwise>
                			<xsl:call-template name="getMultilingualText">
			                	<xsl:with-param name="key">
			                		<select>
				                		<xsl:value-of select="$tokenHead"/>
				                		<xsl:value-of select="$queryName"/>
				                		<xsl:value-of select="$tokenTail"/>
			                		</select>
			                	</xsl:with-param>
                                <xsl:with-param name="bundle" select="$callerTokenBundle"/>
                            </xsl:call-template>
                		</xsl:otherwise>
                	</xsl:choose>
                </SPAN>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                <!-- show button to open query definition -->
                <xsl:if test="USER/@NAME = $USER_ADMINISTRATOR">
                    <BUTTON ONCLICK="top.showObject ('{QUERYCREATOR/SYSTEM/OID/child::text()}');" TYPE="BUTTON">
                        <xsl:value-of select="$TOK_OPEN_QUERY_DEF"/>
                    </BUTTON>
                </xsl:if>
                <DIV CLASS="value" ALIGN="LEFT">
                	<!-- display the query description -->
                	<xsl:choose>
                		<xsl:when test="$callerTokenBundle = ''">
                			<xsl:value-of select="$descriptionText"/>
                		</xsl:when>
                		<xsl:otherwise>
                			<xsl:if test="$descriptionText != ''">
	                			<xsl:call-template name="getMultilingualText">
				                	<xsl:with-param name="key">
		                				<select>
		                					<xsl:value-of select="$tokenHead"/>
                							<xsl:value-of select="$queryName"/>
                							<xsl:value-of select="'.DESCRIPTION'"/>
		                				</select>
					                </xsl:with-param>
				                	<xsl:with-param name="bundle" select="$callerTokenBundle"/>
				                </xsl:call-template>
				        	</xsl:if>
                		</xsl:otherwise>
                	</xsl:choose>
                </DIV>
                <BR/>
                <!-- add hidden field for query creator oid -->
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name" select="$INPUT_QUERYCREATOROID"/>
                    <xsl:with-param name="value" select="QUERYCREATOR/SYSTEM/OID/child::text()"/>
                </xsl:call-template>
            </TD>
        </TR>
    </xsl:template> <!-- queryCreatorData -->

        <!--************************************************************************
                        * Create table and fields in the inforow layout.
                        *
                        * @param tagName            the field tag
                        * @param localRow            the current position
                        * @param mode                 the mode
                        * @param formName          the form name
                        *
                        -->
    <xsl:template name="infoRow">
        <xsl:param name="tagName"/>
        <xsl:param name="title" select="$tagName/@MLDESCRIPTION"/>        
        <xsl:param name="localRow"/>
        <xsl:param name="mode"/>
        <xsl:param name="formName"/>
        
        <TR CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
            <TD CLASS="name" TITLE="{$title}">
                <!-- show field label -->
                <LABEL FOR="{$tagName/@INPUT}">
                <xsl:value-of select="$tagName/@MLNAME"/>
                <xsl:text>:</xsl:text>
                </LABEL>
            </TD>
            <TD CLASS="value">
                <!-- map query field types with general types -->
                <xsl:choose>
                    <xsl:when test="$tagName/@TYPE = 'STRING' or $tagName/@TYPE = 'LONGTEXT'">
                        <!-- show match type -->
                        <xsl:call-template name="createMatchType">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="datatype" select="'TEXT'"/>
                        </xsl:call-template>
                        <!-- generate input field -->
                        <xsl:call-template name="internDt_text">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="datatype" select="'TEXT'"/>
                            <xsl:with-param name="size" select="$TEXT_DEFAULT_SIZE"/>
                            <xsl:with-param name="maxlength" select="$TEXT_DEFAULT_MAXLENGTH"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- is value of type NUMBER? -->
                    <xsl:when test="$tagName/@TYPE = 'NUMBER'">
                        <!-- show match type -->
                        <xsl:call-template name="createMatchType">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="datatype" select="$tagName/@TYPE"/>
                        </xsl:call-template>
                        <!-- generate input field -->
                        <xsl:call-template name="getDatatype">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="mode" select="$mode"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- is value of type DATERANGE? -->
                    <xsl:when test="$tagName/@TYPE = 'DATERANGE'">
                        <!-- show first date field -->
                        <xsl:call-template name="internDt_date">
                            <xsl:with-param name="tagName" select="$tagName"/>
                        </xsl:call-template>
                        <xsl:value-of select="$TOK_DATEFORMAT"/>
                        <xsl:text> - </xsl:text>
                        <!-- show second date field -->
                        <xsl:call-template name="internDt_date">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="fieldName" select="concat($tagName/@INPUT, $RANGE_FIELD_POSTFIX)"/>
                        </xsl:call-template>
                        <xsl:value-of select="$TOK_DATEFORMAT"/>
                    </xsl:when>
                    <!-- is value of type BOOLEAN? -->
                    <xsl:when test="$tagName/@TYPE = 'BOOLEAN'">
                        <!-- generate selection box, because on default a checkbox is shown -->
                        <xsl:call-template name="createBooleanSelBox">
                            <xsl:with-param name="tagName" select="$tagName"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- is value of type MULTIPLE - QUERYSELECTIONBOX? -->
                    <xsl:when test="$tagName/@TYPE = 'QUERYSELECTIONBOX'">
                    	<xsl:choose>
	                        <xsl:when test="contains ($tagName/@TYPEPARAM, $QUERYSELECTIONBOX_MULTIPLE)">
								<xsl:variable name="onFocus">
		                            <xsl:text>javascript: this.multiple = true;this.size = (this.length > 10 ) ? 10 : this.length;this.title = (this.length > 0) ? '</xsl:text>
									<xsl:value-of select="$TOK_TOOLTIP_MULTISELECTION"/>
									<xsl:text>' : ''</xsl:text>
		                        </xsl:variable>
		                        <!-- generate selection box -->
		                        <xsl:call-template name="dt_searchMultiSelectionbox">
		                            <xsl:with-param name="tagName" select="$tagName"/>
		                            <xsl:with-param name="onFocus" select="$onFocus"/>
		                            <xsl:with-param name="onBlur" select="'javascript: setSelectedElements (this);'"/>
		                            <xsl:with-param name="onDblClick" select="'javascript: this.blur();'"/>
		                            <xsl:with-param name="style" select="'vertical-align: middle;'"/>
		                            <xsl:with-param name="classId" select="'select'"/>
									<xsl:with-param name="helptext" select="$TOK_TOOLTIP_MULTISELECTION"/>
		                        </xsl:call-template>
		                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		                        <!-- add javascript for multiselection -->
		                        <xsl:call-template name="additionalMultiSelScript">
		                            <xsl:with-param name="tagName" select="$tagName"/>
		                        </xsl:call-template>
	                        </xsl:when>
	                        <xsl:otherwise>
	                        	<xsl:call-template name="dt_searchSelectionbox">
		                            <xsl:with-param name="tagName" select="$tagName"/>
		                        </xsl:call-template>
	                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <!-- is value of type VALUEDOMAIN? -->
					<xsl:when test="$tagName/@TYPE = 'VALUEDOMAIN'">
		                <xsl:call-template name="dt_searchSelectionbox">
		                    <xsl:with-param name="tagName" select="$tagName"/>
	                    </xsl:call-template>
                    </xsl:when>
                    <!-- The following types must be implemented, if required  -->
                    <!-- FIELDTYPE_OBJECTPATH
                                                    FIELDTYPE_MONEY  -> show match type
                                                    FIELDTYPE_<CONDITION>_MONEY -> show match type
                                                    FIELDTYPE_MONEYRANGE  -> show match type
                                                    FIELDTYPE_<CONDITION>_DATE -> show match type
                                                    FIELDTYPE_TIME  -> show match type
                                                    FIELDTYPE_<CONDITION>_TIME
                                                    FIELDTYPE_DATETIME  -> show match type
                                                    FIELDTYPE_TIMERANGE
                                                    FIELDTYPE_DATETIMERANGE
                                                    FIELDTYPE_INTEGER  -> show match type
                                                    FIELDTYPE_<CONDITION>_INTEGER -> show match type
                                                    FIELDTYPE_INTEGERRANGE
                                                    FIELDTYPE_<CONDITION>_NUMBER  -> show match type
                                                    FIELDTYPE_NUMBERRANGE
                                                    FIELDTYPE_VALUEDOMAIN
                                                    -->
                    <!-- call getDatatype to handle the field type -->
                    <xsl:otherwise>
                        <xsl:call-template name="getDatatype">
                            <xsl:with-param name="tagName" select="$tagName"/>
                            <xsl:with-param name="mode" select="$mode"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </TD>
        </TR>
    </xsl:template> <!--infoRow-->

    <!--************************************************************************
                * Create the required match type selection box for the given data type.
                *
                * @param tagName                    the field tag
                * @param datatype                    the datatype of the field
                * @param matchTypePostFix       the match type post fix
                *
                -->
    <xsl:template name="createMatchType">
    <xsl:param name="tagName"/>
    <xsl:param name="datatype"/>
    <xsl:param name="matchTypePostFix" select="$MATCHTYPE_FIELD_POSTFIX"/>
    <xsl:variable name="fieldName" select="concat($tagName/@INPUT, $matchTypePostFix)"/>

        <!--show match type selection box-->
        <xsl:choose>
            <xsl:when test="$datatype = 'TEXT'">
                <!-- show match type selection box for TEXT -->
                <SELECT NAME="{$fieldName}" SIZE="1">
                    <OPTION VALUE="1" SELECTED="TRUE">
                        <xsl:value-of select="$TOK_CONTAINS"/>
                    </OPTION>
                    <OPTION VALUE="2">
                        <xsl:value-of select="$TOK_EXACTLY"/>
                    </OPTION>
                    <OPTION VALUE="3">
                        <xsl:value-of select="$TOK_SIMILAR"/>
                    </OPTION>
                </SELECT>
            </xsl:when>
            <xsl:when test="$datatype = 'NUMBER'">
                <!-- show match type selection box for NUMBER -->
                <SELECT NAME="{$fieldName}" SIZE="1">
                    <OPTION VALUE="2" SELECTED="TRUE">
                        <xsl:value-of select="$TOK_EXACTLY"/>
                    </OPTION>
                    <OPTION VALUE="4">
                        <xsl:value-of select="$TOK_GREATER"/>
                    </OPTION>
                    <OPTION VALUE="6">
                        <xsl:value-of select="$TOK_GREATER_EQUAL"/>
                    </OPTION>
                    <OPTION VALUE="5">
                        <xsl:value-of select="$TOK_LESS"/>
                    </OPTION>
                    <OPTION VALUE="7">
                        <xsl:value-of select="$TOK_LESS_EQUAL"/>
                    </OPTION>
                </SELECT>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> <!-- createMatchType -->

    <!--************************************************************************
            * Create a boolean selection box, with the values yes/no
            *
            * @param tagName            the field tag
            * @param fieldName          the field name
            *
        -->
    <xsl:template name="createBooleanSelBox">
    <xsl:param name="tagName"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>

        <!-- show selection box for BOOLEAN -->
        <SELECT NAME="{$fieldName}" SIZE="1">
            <OPTION VALUE="" SELECTED="TRUE">
                <xsl:value-of select="''"/>
            </OPTION>
            <OPTION VALUE="{$BOOL_TRUE}">
                <xsl:value-of select="$TOK_YES"/>
            </OPTION>
            <OPTION VALUE="{$BOOL_FALSE}">
                <xsl:value-of select="$TOK_NO"/>
            </OPTION>
        </SELECT>
    </xsl:template> <!-- createBooleanSelBox -->

    <!--************************************************************************
            * Adds the required javascript source for the multiselection box
            *
            * @param tagName            the field tag
            * @param fieldName          the field name
            *
        -->
    <xsl:template name="additionalMultiSelScript">
    <xsl:param name="tagName"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>

        <!-- show informations 
        <SPAN STYLE="vertical-align: middle;">
            <IMG SRC="{$layoutPath}/images/global/info.gif" TITLE="{$TOK_TOOLTIP_MULTISELECTION}" BORDER="0"/>
        </SPAN>
        -->
        <SPAN ID="selected_{$fieldName}" STYLE="font-size: smaller;"></SPAN>
<SCRIPT TYPE="TEXT/JAVASCRIPT" LANGUAGE="JavaScript">
<![CDATA[
<!--
if (typeof printFocus == "undefined")
{
    // printFocus
    function printFocus (elem)
    {
        var infoElem = this.document.getElementById ("selected_" + elem.name);
        infoElem.innerHTML += "focus= " + elem.name +
                              " multiple= " + elem.multiple + "<BR>";
    } // printFocus
    
    // printBlur
    function printBlur (elem)
    {
        var infoElem = this.document.getElementById ("selected_" + elem.name);
        infoElem.innerHTML += "blur= " + elem.name +
                              " multiple= " + elem.multiple + "<BR>";
    } // printBlur
    function setSelectedElements (elem)
    {
        var infoElem = this.document.getElementById ("selected_" + elem.name);
        var text = "";
        var titletext = "";
        var comma = "";
        var titlecomma = "";
        var selectedLines = 0;
        var emptySelected = false;
        
        for (var i = 0; i < elem.length  ;i++)
        {
            if (elem.options [i].selected)
            {
                if (elem.options [i].text.length > 0)
                {
                    text += comma + elem.options [i].text;
                    comma = ", ";
                    selectedLines++;
                    titletext += titlecomma + selectedLines + ": " + elem.options [i].text;
                    titlecomma = "\n";
                } // if
                else
                {
                    emptySelected = true;
                } // else
            } // if
        } // for
        if (selectedLines == 1)
        {
            elem.size = 1;
            elem.multiple = false;
//-->
]]>
            elem.title = "<xsl:value-of select="$TOK_TOOLTIP_MULTISELECTION_SELECTED"/>:\n" + text;
<![CDATA[
<!--
            infoElem.innerHTML = "";
            infoElem.title = "";
        } // if
        else if (selectedLines > 0)
        {
            if (text.length > 80)
            {
                text = "[" + selectedLines + "] " + text.substring (0,80) + "...";
            } // if
            else
            {
                text = "[" + selectedLines + "] " + text;
            } // else
            text = "<br>" + text;
//-->
]]>
            titletext = "<xsl:value-of select="$TOK_TOOLTIP_MULTISELECTION_SELECTED"/>:\n" + titletext;
<![CDATA[
<!--
            elem.size = 2;
            infoElem.innerHTML = text;
            elem.title = titletext;
            infoElem.title = titletext;
        } // else if
        else
        {
            elem.size = 1;
            if (emptySelected)
            {
                elem.multiple = false;
            } // if
//-->
]]>
            elem.title = "<xsl:value-of select="$TOK_TOOLTIP_MULTISELECTION"/>";
<![CDATA[
<!--
            infoElem.innerHTML = "";
            infoElem.title = "";
        } // else
    } // setSelectedElements
    function clearSelectedElements (id)
    {
        var infoElem = this.document.getElementById ("selected_" + id);
        var elem = this.document.getElementById (id);
        
        elem.size = 1;
        elem.title = "";
        infoElem.innerHTML = "";
        infoElem.title = "";
    } // clearSelectedElements
} // if
//-->
]]>
</SCRIPT>
    </xsl:template> <!-- additionalMultiSelScript -->

    <!--************************************************************************
    * Create a selection box search field. The template dt_selectionbox from
    * form_datatypes.xsl can not be used for searches since for searches the
    * DOM tree option's ID attribute has to be used for the HTML option's value
    * attribute. This is necessary since the id has to be integrated into the
    * query.
    *
    * @param tagName        the field tag
    * @param name           field name
    * @param preselected    a preselection value
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param mandatory      mandatory option (true|false)
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    *
    -->
    <xsl:template name="dt_searchSelectionbox">
	    <xsl:param name="tagName"/>
	    <xsl:param name="name" select ="$tagName/@INPUT"/>
        <xsl:param name="type" select ="$tagName/@TYPE"/>
	    <xsl:param name="preselected"/>
	    <xsl:param name="classId"/>
	    <xsl:param name="style"/>
	    <xsl:param name="size" select="1"/>
	    <xsl:param name="mandatory"/>
	    <xsl:param name="readonly"/>
	    <xsl:param name="onChange"/>
	    <xsl:param name="onSelect"/>
	    <xsl:param name="onFocus"/>
	    <xsl:param name="onBlur"/>
	    <xsl:param name="tabindex"/>
	    <xsl:param name="accesskey"/>
	    <xsl:param name="helptext"/>
	    <xsl:param name="errortext"/>

        <SELECT ID="{$name}" NAME="{$name}" SIZE="{$size}"
                TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}"
                TITLE="{$helptext}" STYLE="{$style}">
            <xsl:attribute name="ONCHANGE">
                <xsl:value-of select="$onChange"/>
            </xsl:attribute>
            <xsl:attribute name="ONSELECT">
                <xsl:value-of select="$onSelect"/>
            </xsl:attribute>
            <xsl:attribute name="ONFOCUS">
                <xsl:value-of select="$onFocus"/>
            </xsl:attribute>
            <xsl:attribute name="ONBLUR">
                <xsl:value-of select="$onBlur"/>
            </xsl:attribute>
           	<xsl:attribute name="SIZE">
           		<xsl:value-of select="$size"/>
            </xsl:attribute>
	                
            <xsl:attribute name="CLASS">
                <xsl:value-of select="$classId"/>
                <xsl:if test="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
                    <xsl:text> mandatory</xsl:text>
                </xsl:if>
            </xsl:attribute>

            <xsl:if test="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
                <xsl:attribute name="READONLY">
                    <xsl:text>YES</xsl:text>
                </xsl:attribute>
            </xsl:if>
            
            <xsl:for-each select="$tagName/OPTION">
				<xsl:variable name="cont" select="child::text ()"/>
				<xsl:variable name="oid">
				<xsl:choose>
             		<xsl:when test="$type = 'VALUEDOMAIN'">
             	    	<xsl:value-of select="@VALUE"/>
             	    </xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@ID"/>
					</xsl:otherwise>
				</xsl:choose>
				</xsl:variable>
                 
                <xsl:variable name="description" select="@DESCRIPTION"/>
                 
                <OPTION VALUE="{$oid}" TITLE="{$description}">
                     <xsl:if test="($preselected = '' and @SELECTED = 1) or $preselected = $cont">
                         <xsl:attribute name="SELECTED"/>
                     </xsl:if>
                     <xsl:value-of select="$cont"/>
                </OPTION>
            </xsl:for-each>
        </SELECT>
    </xsl:template><!-- dt_searchSelectionbox -->

    <!--************************************************************************
            * Create a multi selection box field
            *
            * @param tagName            the field tag
            * @param name               field name
            * @param type               type name
            * @param cols               column size
            * @param preselected        a preselection value
            * @param classId            css class
            * @param style              css style
            * @param size               size
            * @param mandatory          mandatory option (true|false)
            * @param readonly           readonly option
            * @param onChange           onChange event handler
            * @param onSelect           onSelect event handler
            * @param onFocus            onFocus event handler
            * @param onBlur             onBlur event handler
            * @param tabindex           tabindex
            * @param accesskey          access key
            * @param helptext           a help text to display (as title)
            * @param errortext          an error text to display    NOT SUPPORTED
            * @param viewType           the view type
            * @param helptext           a help text to display (as title)
            * @param isCreateOptions    create option fields?
            *
            -->
    <xsl:template name="dt_searchMultiSelectionbox">
        <xsl:param name="tagName"/>
        <xsl:param name="name" select ="$tagName/@INPUT"/>
        <xsl:param name="type" select ="$tagName/@TYPE"/>
        <xsl:param name="cols" select ="$tagName/@COLS"/>
        <xsl:param name="preselected"/>
        <xsl:param name="classId"/>
        <xsl:param name="style"/>
        <xsl:param name="size" select="1"/>
        <xsl:param name="mandatory"/>
        <xsl:param name="readonly"/>
        <xsl:param name="onChange"/>
        <xsl:param name="onSelect"/>
        <xsl:param name="onFocus"/>
        <xsl:param name="onBlur"/>
        <xsl:param name="onDblClick"/>
        <xsl:param name="tabindex"/>
        <xsl:param name="accesskey"/>
        <xsl:param name="helptext"/>
        <xsl:param name="errortext"/>
        <xsl:param name="viewType" select ="$tagName/@VIEWTYPE"/>
        <xsl:param name="isCreateOptions" select ="''"/>
     
        <!-- Set the default number of cols for viewtype=checklist -->
        <xsl:variable name="colsintern">
            <xsl:choose>
                <!-- Check if a number of columns is defined -->
                <xsl:when test="$cols != ''">
                    <xsl:value-of select="$cols"/>
                </xsl:when>
                <!-- Otherwise take the default value -->
                <xsl:otherwise>
                    <xsl:value-of select="3"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- viewtype="SELECTIONBOX" (default) -->
        <SELECT ID="{$name}" NAME="{$name}" SIZE="{$size}"
                TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}"
                TITLE="{$helptext}" STYLE="{$style}">
            <xsl:attribute name="MULTIPLE">
                <xsl:text>MULTIPLE</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="ONCHANGE">
                <xsl:value-of select="$onChange"/>
            </xsl:attribute>
            <xsl:attribute name="ONSELECT">
                <xsl:value-of select="$onSelect"/>
            </xsl:attribute>
            <xsl:attribute name="ONFOCUS">
                <xsl:value-of select="$onFocus"/>
            </xsl:attribute>
            <xsl:attribute name="ONBLUR">
                <xsl:value-of select="$onBlur"/>
            </xsl:attribute>
            <xsl:attribute name="ONDBLCLICK">
                <xsl:value-of select="$onDblClick"/>
            </xsl:attribute>
            <xsl:attribute name="SIZE">
                <xsl:value-of select="$size"/>
            </xsl:attribute>

            <xsl:attribute name="CLASS">
                <xsl:value-of select="$classId"/>
                <xsl:if test="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
                    <xsl:text> mandatory</xsl:text>
                </xsl:if>
            </xsl:attribute>

            <xsl:if test="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
                <xsl:attribute name="READONLY">
                    <xsl:text>YES</xsl:text>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="$isCreateOptions = ''">
                <xsl:for-each select="$tagName/OPTION">
                    <xsl:variable name="cont" select="child::text ()"/>
                    <xsl:variable name="oid">
                      <xsl:choose>
                            <xsl:when test="$type = 'VALUEDOMAIN'">
                                <xsl:value-of select="@VALUE"/>
                            </xsl:when>
                                <xsl:otherwise>
                                   <xsl:value-of select="@ID"/>
                            </xsl:otherwise>
                          </xsl:choose>
                    </xsl:variable>
                    
                    <xsl:variable name="description" select="@DESCRIPTION"/>
                    
                    <OPTION VALUE="{$oid}" TITLE="{$description}">
                        <xsl:if test="$preselected = $cont">
                            <xsl:attribute name="SELECTED"/>
                        </xsl:if>
                        <xsl:value-of select="$cont"/>
                    </OPTION>
                </xsl:for-each>
            </xsl:if>
        </SELECT>
    </xsl:template><!-- dt_searchMultiSelectionbox -->
    
    <!-- ********************** CALLED TEMPLATES END *********************** -->
</xsl:stylesheet>