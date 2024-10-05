<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="constants.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
    <xsl:variable name="thumbnailHeight" select="30"/>
    <xsl:variable name="animateSearchResult" select="'true'"/>
    <xsl:variable name="animateSearchForm" select="'true'"/>
    
    <xsl:variable name="CHECKBOX_NAME" select="'dlst'"/>
    <!--*********************** VARIABLES END ******************************-->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>


<!-- **************************************************************************
 * Standard match template for query root
 -->
<xsl:template match="/QUERY">

    <xsl:call-template name="showSwitchToSearchForm"/>

    <xsl:call-template name="showQueryResult"/>

</xsl:template> <!-- /QUERY -->


<!-- **************************************************************************
 * Show functionality for animated switching to search form.
 *
 * @param printLandscape    print the query result in landscape format.
 * @param showChangeSearch  option to show the change search button
 * @param showPrint         option to show the print button
 * @param buttons           any additional buttons to display
-->
<xsl:template name="showSwitchToSearchForm">
<xsl:param name="printLandscape" select="true ()"/>
<xsl:param name="showChangeSearch" select="/QUERY/@SHOWSEARCHFORM = 'TRUE'"/>
<xsl:param name="showPrint" select="true()"/>
<xsl:param name="buttons"/>


    <SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
    <![CDATA[
    <!--
    ]]>
    var v_animateSearchResult = <xsl:value-of select="$animateSearchResult"/>;
    var v_animateSearchForm = <xsl:value-of select="$animateSearchForm"/>;
    
    var currentFrame = top.sheet;
    <![CDATA[

    // set the global frame animation flag before setFrameHeight
    top.scripts.v_isFrameAnimationInProgress = false;

	// if the sheet frame has no frame 1 the current frame must be the search frame
	if(top.sheet.sheet1==null)
	{
		// set the search frame as current frame
		currentFrame = top.searchFrame;

	    // display only the search result:
	    top.scripts.setFrameHeight (
	        currentFrame.sheet1, null, 0, "<size>,*", v_animateSearchResult);
	} // if

    function removeResizeEvent ()
    {
        window.onresize = function () {};
    }
    
    /**
     * Close the serach frame.
     */
    function closeSearch ()
    {
    	// if the current frame is the sheet frame
    	if(currentFrame == top.sheet)
    	{
    		top.sheet.document.body.rows = "*,1";
	    } // if
	    else
	    {	
    		top.scripts.setFrameHeight (currentFrame.sheet2, null, 0, '*,<size>', v_animateSearchForm); removeResizeEvent ();
   		} // else
    }
    //-->
    ]]>
    </SCRIPT>

    <!-- functionality for switching to frame with search form: -->
<!-- ELAK-30: has been replaced by an BUTTON implementation
        <DIV CLASS="changesearch" ID="buttons">
            <A HREF="javascript:top.scripts.setFrameHeight (top.sheet.sheet2, null, 0, '*,&lt;size&gt;', v_animateSearchForm); removeResizeEvent ();"
               ONCLICK="this.className='changesearch_clicked';">
               <xsl:value-of select="$TOK_CHANGESEARCH"/>
            </A>
        </DIV>
-->
	<!-- Dummy DIV for CSS style position: absolute-->
    <DIV CLASS="" ID="tableContainer">
	    <DIV ALIGN="LEFT" ID="buttons" CLASS="searchButtons">
	        <xsl:if test="$showChangeSearch">
	            <BUTTON ID="changeSearchBtn" NAME="changeSearchBtn"
	                    TYPE="BUTTON" CLASS="searchButton"
	                    ONCLICK="closeSearch();">
	               <xsl:value-of select="$TOK_CHANGESEARCH"/>
	            </BUTTON>
	            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	        </xsl:if>
	        <xsl:if test="$showPrint">
	            <BUTTON ID="printBtn" NAME="printBtn"
	                    TYPE="BUTTON" CLASS="searchButton"
	                    ONCLICK="window.print ();">
	               <xsl:value-of select="$TOK_PRINT"/>
	            </BUTTON>
	            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	        </xsl:if>
	        
	        <xsl:if test="$buttons">
	            <xsl:copy-of select="$buttons"/>
	        </xsl:if>
	    </DIV>
    </DIV>
    
    <!-- add the CSS style for printing the query result -->
    <xsl:call-template name="createPrintCSS">
        <xsl:with-param name="printLandscape" select="$printLandscape"/>
    </xsl:call-template>


</xsl:template> <!-- showSwitchToSearchForm -->


<!-- **************************************************************************
 * Add stylesheet and add JS function to fix table header.
-->
<xsl:template name="addFixedTableHeaderJS">

<SCRIPT LANGUAGE="javascript" TYPE="text/javascript" SRC="scripts/scriptDocument.js"></SCRIPT>

<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
<![CDATA[

// set the original page height
// This is a tricky one, some browsers require scrollHeight, others offsetHeight,
// but all browsers support both properties. Therefore I see which property has the larger value.
// This means the page height the script below gives is never smaller than the window height.
var x,y;
var test1 = document.body.scrollHeight;
var test2 = document.body.offsetHeight
if (test1 > test2) // all but Explorer Mac
{
	x = document.body.scrollWidth;
	y = document.body.scrollHeight;
}
else // Explorer Mac;
     //would also work in Explorer 6 Strict, Mozilla and Safari
{
	x = document.body.offsetWidth;
	y = document.body.offsetHeight;
}
var origOffsetWidth = x + 'px';
var origOffsetHeight = y + 'px';
var v_tableContainer = null;


function getTableContainer ()
{
    if (v_tableContainer == null)
    {
        v_tableContainer = document.getElementById ("tableContainer");
    } // if

    return (v_tableContainer);
} // getTableContainer

// insert stylesheet to fix table header during scrolling
createStylesheetLink (document, top.system.browserDir + "styleTableNoScroll.css");

// add a style class of the table to fix the header
function addTableContainerClass ()
{
    var table = getTableContainer ();
    if (table)
    {
        table.className = "tableContainer";
        resizeTable ();
    } // if

} // addTableContainerClass

// remove the style class of the table to fix the header
function removeTableContainerClass ()
{
    var table = getTableContainer ();
    if (table)
    {
        table.style.height = origOffsetHeight;
//        table.style.width = origOffsetWidth;
        table.className = "";
    } // if
} // removeTableContainerClass

function resizeTable ()
{
    var table = getTableContainer ();
    if (table)
    {
        // cross browser script
        var x,y = null;
        if (self.innerHeight) // all except Explorer
        {
        	x = self.innerWidth;
        	y = self.innerHeight;
        }
        else if (document.documentElement && document.documentElement.clientHeight)
        	// Explorer 6 Strict Mode
        {
        	x = document.documentElement.clientWidth;
        	y = document.documentElement.clientHeight;
        }
        else if (document.body) // other Explorers
        {
        	x = document.body.clientWidth;
        	y = document.body.clientHeight;
        }
        
        table.style.height = y - 55 + 'px';
        table.style.width = 100 + '%' ;

    } // if (table)
} // resizeTable

// map to a common used function name
window.evtResizingDone = addTableContainerClass;

// Call function "evtResizingDone" if frames are not resized.
// Otherwise (if frames are currently resized) evtResizingDone
// is called during the callback function
// in function "resizeFramesRec" in scriptsCommon.js.
if (top.scripts.v_isFrameAnimationInProgress == false)
{
    evtResizingDone ();
} // if

// remove the special style class before printing.
window.onbeforeprint= removeTableContainerClass;

// add the special style class after printing.
window.onafterprint= addTableContainerClass;

// resize the table in case of resizing the window
window.onresize = resizeTable;

]]>
</SCRIPT>
</xsl:template> <!-- addFixedTableHeaderJS -->


<!-- **************************************************************************
 * Creates a HTML Table for the result of a Query
 *
 * @param data          the data to be displayed
 * @param addCheckBox   if 'YES', add a checkbox to each row with the
 *                      oid as value. there has to be an column with
 *                      the oid and the name has to be #OBJECTID.
 * @param fixedTaableHeader option to activate the fixed table header feature
-->
<xsl:template name="showQueryResult">
<xsl:param name="data" select="/QUERY"/>
<xsl:param name="addCheckBox"/>
<xsl:param name="fixedTableHeader" select="false()"/>

    <!-- compatibility -->
    <xsl:variable name="addCheckBox" select="$addCheckBox or $addCheckBox = 'YES'"/>

    <xsl:choose>
    <xsl:when test="$data/RESULTROW">
    
        <DIV CLASS="" ID="tableContainer">
        <FORM NAME="queryForm" ACTION="">
        <TABLE CLASS="list" WIDTH="100%" CELLSPACING="0" CELLPADDING="0" FRAME="VOID">

        <!-- display the table column definiton -->
        <xsl:call-template name="showColDefinition">
            <xsl:with-param name="headers" select="$data/RESULTROW[1]/RESULTELEMENT"/>
        </xsl:call-template>

        <!-- display the table headers -->
        <xsl:call-template name="showHeaders">
            <xsl:with-param name="addCheckBox" select="$addCheckBox"/>
            <xsl:with-param name="headers" select="$data/RESULTROW[1]/RESULTELEMENT"/>
        </xsl:call-template>

        <!-- display the data -->

        <xsl:call-template name="showData">
            <xsl:with-param name="addCheckBox" select="$addCheckBox"/>
            <xsl:with-param name="data" select="$data"/>
        </xsl:call-template>

        </TABLE>
        </FORM>
        </DIV>

        <!-- add the js to generate a fixed table header -->
        <xsl:if test="$fixedTableHeader">
            <xsl:call-template name="addFixedTableHeaderJS"/>
        </xsl:if>

    </xsl:when>
    <xsl:otherwise> <!-- no data found -->
        <xsl:value-of select="$MSG_NOSEARCHRESULTFOUND"/>
    </xsl:otherwise>
    </xsl:choose>

</xsl:template> <!-- showQueryResult -->

<!-- **************************************************************************
 * Create the table's colgroup defintion
 *
 * @param headers       the header data
-->
<xsl:template name="showColDefinition">
    <xsl:param name="headers" select="/QUERY/RESULTROW[1]/RESULTELEMENT"/>
    <!-- to be overwritten -->
</xsl:template>

<!-- **************************************************************************
 * Create the HTML for the header of a query
 *
 * @param headers       the header data
 * @param orderBy       orderBy value
 * @param orderHow      orderHow value
 * @param addCheckBox   option to add a check box
 * @param showIsNew     show the isNew column
-->
<xsl:template name="showHeaders">
<xsl:param name="title" select="/QUERY/@NAME"/>
<xsl:param name="headers" select="/QUERY/RESULTROW[1]/RESULTELEMENT"/>
<xsl:param name="orderBy" select="number(/QUERY/@ORDERBY)"/>
<xsl:param name="orderHow" select="/QUERY/@ORDERHOW"/>
<xsl:param name="addCheckBox" select="false()"/>
<xsl:param name="showIsNew" select="$headers[@NAME='#ISNEW']"/>

    <!-- display the title -->
    <xsl:if test="$title">
        <DIV ID="title" ALIGN="CENTER">
            <xsl:value-of select="$title"/><BR/><BR/>
        </DIV>
        <SCRIPT LANGUAGE="Javascript">
            this.document.title = "<xsl:value-of select="$title"/>";
        </SCRIPT>
    </xsl:if>

    <THEAD>
        <!-- HEADER -->
        <TR CLASS="listheader" VALIGN="TOP">
            <!-- CHECKBOX -->
            <xsl:if test="$addCheckBox">
                <TD CLASS="listheader" ALIGN="CENTER" VALIGN="MIDDLE">
                    <INPUT TYPE="CHECKBOX" NAME="toggleSelect" ONCLICK="toggleSelAll (this, '{$CHECKBOX_NAME}');"/>
                    <xsl:call-template name="addToggleSelectAllJS"/>
                </TD>
            </xsl:if>
            <!-- ISNEW -->
            <xsl:if test="$showIsNew">
                <TD CLASS="listheader"> </TD>
            </xsl:if>
            <!-- loop through headers -->
            <xsl:for-each select="$headers[@TYPE!='SYSVAR']">
                <TD CLASS="listheader">
                <xsl:choose>
                <xsl:when
                    test="@TYPE = 'IMAGE' or
                          starts-with (@TYPE, 'BUTTON') or
                          starts-with (@TYPE, 'INPUT')">
                    <xsl:value-of select="@MLNAME"/>
                </xsl:when>
                <xsl:when test="(position()-1) = $orderBy and $orderHow = 'ASC'">
                    <A CLASS="listheader">
                        <xsl:attribute name="HREF">javascript:orderBy('<xsl:value-of select="position()-1"/>','DESC');</xsl:attribute>
                        <xsl:value-of select="@MLNAME"/>
                    </A>
                    <IMG SRC="{$layoutPath}images/global/OrderASC.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                </xsl:when>
                <xsl:when test="(position()-1) = $orderBy and $orderHow = 'DESC'">
                    <A CLASS="listheader">
                        <xsl:attribute name="HREF">javascript:orderBy('<xsl:value-of select="position()-1"/>','ASC');</xsl:attribute>
                        <xsl:value-of select="@MLNAME"/>
                    </A>
                    <IMG SRC="{$layoutPath}images/global/OrderDESC.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                </xsl:when>
                <xsl:otherwise>
                    <A CLASS="listheader">
                    <xsl:attribute name="HREF">javascript:orderBy('<xsl:value-of select="position()-1"/>','ASC');</xsl:attribute>
                    <xsl:value-of select="@MLNAME"/>
                    </A>
                 </xsl:otherwise>
                </xsl:choose>
                </TD>
            </xsl:for-each>
        </TR>
    </THEAD>

    <SCRIPT LANGUAGE="JavaScript">
    <xsl:text>var v_quOid = "</xsl:text>
    <xsl:value-of select="$root/SYSTEM/OID/child::text()"/>
    <xsl:text>";</xsl:text>
<![CDATA[
    function orderBy (position, orderHow)
    {
        top.scripts.callUrl (21, '&oid=' + v_quOid +
            '&oBy=' + position +
            '&oHow=' + orderHow +
            '&rord=1&frs=false', null, self.name);
    }
]]>
    </SCRIPT>

</xsl:template> <!-- showHeaders -->

<!-- **************************************************************************
 * Create the HTML for the result of a query
 *
 * @param data          the result data
 * @param addCheckBox   option to add a check box
 * @param showIsNew     show the isNew column
-->
<xsl:template name="showData">
<xsl:param name="data" select="/QUERY"/>
<xsl:param name="addCheckBox" select="false()"/>
<xsl:param name="showIsNew" select="$data/RESULTROW[1]/RESULTELEMENT[@NAME='#ISNEW']"/>

    <TBODY>
    <!-- BODY -->
    <xsl:for-each select="$data/RESULTROW">

        <xsl:variable name="oid" select="RESULTELEMENT[@NAME='#OBJECTID']"/>
        <xsl:variable name="localRow" select="position() + 1"/>
        <xsl:variable name="listRow">
            <xsl:text>listRow</xsl:text>
            <xsl:value-of select="format-number ((number($localRow) mod 2) + 1, '####')"/>
        </xsl:variable>

        <TR CLASS="{$listRow}" VALIGN="TOP">
        
        <!-- CHECKBOX-->
        <xsl:if test="$addCheckBox">
            <TD CLASS="{$listRow}" ALIGN="CENTER" VALIGN="MIDDLE">
                <INPUT NAME="{$CHECKBOX_NAME}" TYPE="CHECKBOX" ONCLICK="swTA (this);">
                    <xsl:attribute name="VALUE">
                        <xsl:value-of select="RESULTELEMENT[@NAME='#OBJECTID']"/>
                    </xsl:attribute>
                </INPUT>
            </TD>
        </xsl:if>

        <!-- ISNEW -->
        <xsl:if test="$showIsNew">
            <TD CLASS="{$listRow}">
            <xsl:value-of select="$showIsNew"/>
            <xsl:if test="RESULTELEMENT[@NAME='#ISNEW']='1'">
                <IMG BORDER="0" SRC="images/global/new.gif" ALIGN="ABSMIDDLE"/>
            </xsl:if>
            </TD>
        </xsl:if>

        <!-- FIRST ELEMENT -->
        <TD CLASS="{$listRow}">

        <!-- OBJECTID -->
        <xsl:choose>
        <xsl:when test="RESULTELEMENT[@NAME='#OBJECTID'] != ''">
            <!-- ONCLICK must be used instead of HREF because the xml processor
                 generates predefined entities instead of umlaute and they get
                 urlencoded when used in a HREF and xsl:output method="HTML"
                 KR 20060707: Because the <A> tag does not work correctly we
                              use the <DIV> tag instead.
            -->
            <DIV CLASS="resultobjectlink">
            <xsl:attribute name="ONCLICK">
                <xsl:choose>
                <xsl:when test="RESULTELEMENT[@NAME='#SET'] != ''">
                    <xsl:text>top.</xsl:text>
                    <xsl:variable name="set" select="RESULTELEMENT[@NAME='#SET']"/>
                    <xsl:value-of select="substring-before ($set, '#SYSVAR.ELEMOID#')"/>
                    <xsl:value-of select="$oid"/>
                    <xsl:value-of select="substring-after ($set, '#SYSVAR.ELEMOID#')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>top.showObject ('</xsl:text>
                    <xsl:value-of select="RESULTELEMENT[@NAME='#OBJECTID']"/>
                    <xsl:text>')</xsl:text>
                </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <!-- TYPEIMAGE -->
            <xsl:if test="RESULTELEMENT[@NAME='#TYPEIMAGE'] != ''">
            <IMG BORDER="0" ALIGN="ABSMIDDLE">
                <xsl:attribute name="SRC">
                    <xsl:value-of select="$layoutPath"/><xsl:text>images/objectIcons/</xsl:text>
                    <xsl:value-of select="RESULTELEMENT[@NAME='#TYPEIMAGE']"/>
                </xsl:attribute>
            </IMG>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:if>
            <!-- VALUE -->
            <xsl:variable name="elem" select="RESULTELEMENT[@TYPE!='SYSVAR']"/>
			<xsl:choose>
            	<xsl:when test="translate ($elem/@MULTIPLE, 'YES', 'yes') = $BOOL_YES">
            		<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
						var str = "<xsl:value-of select="$elem/child::text()"/>";
						<![CDATA[
						<!--
						//replace '|' with ', '
						document.write (str.replace (/\|/g, ", "));
						//-->
						]]>
						</SCRIPT>
            	</xsl:when>
            	<xsl:otherwise>
		            <xsl:value-of select="$elem/child::text()"/>
		        </xsl:otherwise>
		    </xsl:choose>
            </DIV>
        </xsl:when>
        <xsl:otherwise>
            <xsl:variable name="elem" select="RESULTELEMENT[@TYPE!='SYSVAR']"/>
			<xsl:choose>
            	<xsl:when test="translate ($elem/@MULTIPLE, 'YES', 'yes') = $BOOL_YES">
            		<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
						var str = "<xsl:value-of select="$elem/child::text()"/>";
						<![CDATA[
						<!--
						//replace '|' with ', '
						document.write (str.replace (/\|/g, ", "));
						//-->
						]]>
						</SCRIPT>
            	</xsl:when>
            	<xsl:otherwise>
		            <xsl:value-of select="$elem/child::text()"/>
		        </xsl:otherwise>
		    </xsl:choose>
        </xsl:otherwise>
        </xsl:choose>
        </TD>

        <!-- FOLLOWING ELEMENTS -->

        <xsl:for-each select="RESULTELEMENT[@TYPE!='SYSVAR']">

            <xsl:variable name="typevalue" select="substring-before (substring-after (@TYPE,'('),')')"/>
            <xsl:variable name="fieldname">
                <xsl:text>f</xsl:text>
                <xsl:value-of select="$localRow"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="position()"/>
            </xsl:variable>

            <!-- do not show first column which is not a SYSVAR,
                 because it is already shown -->
            <xsl:if test="position() &gt; 1">
                <TD CLASS="{$listRow}">

                <!-- field type specific handling: -->
                <xsl:choose>
                    <xsl:when test="@TYPE = 'IMAGE'">

                        <xsl:variable name="fileName"
                                      select="./child::text ()"/>
                        <xsl:variable name="url">
                            <xsl:choose>
                                <xsl:when test="boolean (@URL)">
                                    <xsl:value-of select="@URL"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$oid"/>
                                    <xsl:text>/</xsl:text>
                                    <xsl:value-of select="$fileName"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
<!--
                        |<xsl:value-of select="@URL"/>|<xsl:value-of select="$oid"/>|
-->
<!--
                        <IMG BORDER="0" SRC="{$_WWWP}/images/{$oid}/{$tagName}" ALIGN="ABSMIDDLE"/>
-->
                        <IMG BORDER="0" HEIGHT="{$thumbnailHeight}" ALIGN="ABSMIDDLE"
                             SRC="{/QUERY/@UPLOADURL}../upload/files/{$url}"/>
<!-- KR 20060120 replaced by standard upload directory
                        <IMG BORDER="0" SRC="{$_WWWP}/images/{$tagName}" ALIGN="ABSMIDDLE"/>
-->
                    </xsl:when> <!-- IMAGE -->

                    <!-- BUTTON FIELDS -->
                    <xsl:when test="@TYPE = 'BUTTON_TEXT'">
                        <A>
                            <xsl:attribute name="HREF">
                                <xsl:value-of select="child::text()"/>
                            </xsl:attribute>
                            <xsl:value-of select="@NAME"/>
                        </A>
                    </xsl:when>
                    <xsl:when test="starts-with (@TYPE, 'BUTTON_IMAGE')">
                        <A>
                            <xsl:attribute name="HREF">
                                <xsl:value-of select="child::text()"/>
                            </xsl:attribute>
                            <IMG BORDER="0" ALIGN="ABSMIDDLE">
                                <xsl:attribute name="SRC">
                                    <xsl:value-of select="$layoutPath"/><xsl:text>images/buttons/</xsl:text>
                                    <xsl:value-of select="$typevalue"/>
                                </xsl:attribute>
                            </IMG>
                        </A>
                    </xsl:when>
                    <xsl:when test="starts-with (@TYPE, 'BUTTON')">
                        <INPUT TYPE="BUTTON" VALUE="{@NAME}" ONCLICK="{child::text()}"/>
                    </xsl:when>
                    <!-- INPUT FIELDS -->
                    <xsl:when test="starts-with (@TYPE, 'INPUT_STRING')">
                        <INPUT TYPE="TEXT" NAME="{$fieldname}">
                            <xsl:attribute name="SIZE">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="MAXLENGTH">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="ONCHANGE">
                                <xsl:text>top.iTx (document.queryForm.</xsl:text>
                                <xsl:value-of select="$fieldname"/>
                                <xsl:text>, true);</xsl:text>
                            </xsl:attribute>
                        </INPUT>
                    </xsl:when>
                    <xsl:when test="starts-with (@TYPE, 'INPUT_NUMBER')">
                        <INPUT TYPE="TEXT" NAME="{$fieldname}">
                            <xsl:attribute name="SIZE">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="MAXLENGTH">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="ONCHANGE">
                                <xsl:text>top.iI (document.queryForm.</xsl:text>
                                <xsl:value-of select="$fieldname"/>
                                <xsl:text>, true);</xsl:text>
                            </xsl:attribute>
                        </INPUT>
                    </xsl:when>
                    <xsl:when test="@TYPE = 'INPUT_DATE'">
                        <INPUT TYPE="TEXT" NAME="{$fieldname}" SIZE="10" MAXLENGTH="10">
                            <xsl:attribute name="ONCHANGE">
                                <xsl:text>top.iD (document.queryForm.</xsl:text>
                                <xsl:value-of select="$fieldname"/>
                                <xsl:text>, true);</xsl:text>
                            </xsl:attribute>
                        </INPUT>
                    </xsl:when>
                    <xsl:when test="starts-with (@TYPE, 'INPUT_MONEY')">
                        <INPUT TYPE="TEXT" NAME="{$fieldname}">
                            <xsl:attribute name="SIZE">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="MAXLENGTH">
                                <xsl:value-of select="$typevalue"/>
                            </xsl:attribute>
                            <xsl:attribute name="ONCHANGE">
                                <xsl:text>top.iMR (document.queryForm.</xsl:text>
                                <xsl:value-of select="$fieldname"/>
                                <xsl:text>, true);</xsl:text>
                            </xsl:attribute>
                        </INPUT>
                    </xsl:when>
		            <!-- multiple = true -->
		            <xsl:when test="translate (@MULTIPLE, 'YES', 'yes') = $BOOL_YES">
						<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
						var str = "<xsl:value-of select="child::text()"/>";
						<![CDATA[
						<!--
						//replace '|' with ', '
						document.write (str.replace (/\|/g, ", "));
						//-->
						]]>
						</SCRIPT>
		            </xsl:when> <!--multiple = true -->
                    <xsl:otherwise>

                        <xsl:call-template name="showStandardValue">
                            <xsl:with-param name="valueTag" select="."/>
                        </xsl:call-template>

                    </xsl:otherwise>
                </xsl:choose>
                </TD>
            </xsl:if><!-- if it is not the first nonsysvar - column -->
        </xsl:for-each>
        </TR>
    </xsl:for-each>

    </TBODY>
</xsl:template> <!-- showData -->


<!-- **************************************************************************
 * Creates a HTML Table for the result of a Query
 *
 * @param   valueTag    The current VALUE tag.
-->
<xsl:template name="showStandardValue">
<xsl:param name="valueTag"/>
<xsl:param name="value" select="$valueTag/child::text()"/>

    <xsl:choose>
        <!-- check for value #not defined#: -->
        <xsl:when test="$value = $TOK_NOTDEFINED">
            <!-- display list value for #not defined#: -->
            <xsl:value-of select="$TOK_NOTDEFINEDLIST"/>
        </xsl:when>
        <xsl:when test="$value = $TOK_NOTDEFINEDNUM">
            <!-- display list value for #not defined#: -->
            <xsl:value-of select="$TOK_NOTDEFINEDNUMLIST"/>
        </xsl:when>
        <xsl:otherwise>
            <!-- display the value as is: -->
            <xsl:value-of select="$value"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template> <!-- showStandardValue -->


<!-- **************************************************************************
 * Add the javascript for the toggle select all button
 *
 * @param   valueTag    The current VALUE tag.
-->
<xsl:template name="addToggleSelectAllJS">

    <SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
    <![CDATA[
        function toggleSelAll (elem, fieldName)
        {
            var checkboxes = this.document.getElementsByName (fieldName);
            for (var i = 0; i < checkboxes.length; i++)
            {
                checkboxes[i].checked = elem.checked;
            } // for
        } // toggleSelAll

        function swTA (elem)
        {
            if (! elem.checked)
            {
                this.document.getElementById ("toggleSelect").checked = false;
            } // if
        } // swTA
    ]]>
    </SCRIPT>
</xsl:template> <!-- addToggleSelectAllJS -->


    <!--***********************************************************************
     * Creates the CSS style for printing the page. <BR>
     *-->
    <xsl:template name="createPrintCSS">
    <xsl:param name="printLandscape" select="true ()"/>

<STYLE TYPE="text/css">
@media screen
{
    #title
    {
        display: none;
        text-align: center;
    }
}

@media print
{
    @page
    {
        <xsl:choose>
        <xsl:when test="printLandscape">
            <xsl:text>size:landscape;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
            <xsl:text>size:portrait;</xsl:text>
        </xsl:otherwise>
        </xsl:choose>
    }
    .header, #buttons
    {
        display:none;
    }
    #title
    {
        text-align: center;
        display: block;
        font-size: 16pt;
        color: #000000;
        background: transparent;
        font-weight: bold;
        page-break-after: avoid;
    }

    body
    {
      margin: 0px;
      padding: 0px;
      background: transparent;
    }

    .listheader
    {
        background: #000000;
        color: #FFFFFF;
    }

    TABLE, TR, TD
    {
        border: 1px solid #000000;
        color: #000000;
    }

    a, a:visited, a:hover, a:visited:hover
    {
        color: #000000;
        background: transparent;
        text-decoration: none;
    }
    
    html *{
		float:none !important;
	}
}

        </STYLE>
    </xsl:template> <!-- createPrintCSS -->

</xsl:stylesheet>
