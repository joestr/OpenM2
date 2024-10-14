<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="http://xml.apache.org/xalan/java">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="constants.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
    <xsl:variable name="oid" select="/OBJECT/SYSTEM/OID/child::text ()"/>
    <xsl:variable name="gp_showFilesInWindow" select="/OBJECT/@SHOWFILESINWINDOW"/>
    <xsl:variable name="mode" select="$MODE_UNKNOWN"/>
    <!--*********************** VARIABLES END ******************************-->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    
    <!--***********************************************************************
     * This template generates an info row.
     *
     * @param   tagName     the name of the value tag to create the info row for
     * @param   tag         the value tag to create the info row for
     * @param   label       the label to display (default is the tagName
     * @param   value       the value to display (instead of evaluating the
     *                      valuetag)
     * @param   info        info to display after the field
     * @param   localRow    the index of the row (used to generate the stripe
     *                      layout
     * @param   mode        the display mode (default is the mode set in the
     *                      global mode var)
     * @param   onChange    onChange event handler (passed to datatype)
     *-->
    <xsl:template name="infoRow">
        <xsl:param name="tagName"/>
        <xsl:param name="tag" select="/OBJECT/VALUES/VALUE[@FIELD = $tagName]"/>
        <xsl:param name="label" select="$tag/@NAME"/>
        <xsl:param name="title" select="$tag/@DESCRIPTION"/>
        <xsl:param name="value"/>
        <xsl:param name="info" select="''"/>
        <xsl:param name="localRow" select="'1'"/>
        <xsl:param name="mode" select="$mode"/>
        <xsl:param name="onChange"/>

        <TR CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
            <TD CLASS="name" TITLE="{$title}">
                <xsl:value-of select="$label"/>:
            </TD>
            <TD CLASS="value">
                <xsl:choose>
                    <xsl:when test="not ($value)">
                        <xsl:call-template name="getDatatype">
                            <xsl:with-param name="tagName" select="$tag"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="onChange" select="$onChange"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="$value"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$tag/@TYPE != 'FIELDREF' and $tag/@TYPE != 'OBJECTREF'">
                	<xsl:call-template name="createNBSP"/>
                </xsl:if>
                <xsl:if test="$info != ''">
                    <xsl:value-of select="$info"/>
                </xsl:if>
            </TD>
        </TR>
    </xsl:template> <!-- infoRow -->

    
	<!--*********************************************************** 
	* This template generates the HTML-form for the specified system-fields.
	*
	* @param localRow	the localRow that determines the zebra layout counter
	* @param showName 	Add the system name field
	* @param showType 	Add the system type field
	* @param showDesc 	Add the system  description field
	* @param showValidUntil Add the system valid until field
	* @param showShowInNews Add the system show in news field
	* @param addCheckedOut Add the system checkedOut field
	-->
    <xsl:template name="addSystemFields">
	<xsl:param name="localRow" select="0"/>
	<xsl:param name="showName" select="true()"/>
	<xsl:param name="showType" select="false()"/>
	<xsl:param name="showDesc" select="false()"/>
	<xsl:param name="showValidUntil" select="false()"/>
	<xsl:param name="showShowInNews" select="false()"/>
	<xsl:param name="showCheckedOut" select="true()"/>
		
		<xsl:if test="$showName">
			<xsl:call-template name="infoRow">
            	<xsl:with-param name="tag" select="/OBJECT/SYSTEM/NAME"/>
             	<xsl:with-param name="label" select="$TOK_NAME"/>
             	<xsl:with-param name="localRow" select="$localRow + number($showName)"/>
         	</xsl:call-template>
        </xsl:if>		

		<xsl:if test="$showType">
			<xsl:call-template name="infoRow">
             	<xsl:with-param name="label" select="$TOK_TYPE"/>
             	<xsl:with-param name="value">
					<xsl:value-of select="/OBJECT/@TYPE"/>				
				</xsl:with-param>				
             	<xsl:with-param name="localRow" select="$localRow + number($showName) + number($showType)"/>
         	</xsl:call-template>
        </xsl:if>		

		<xsl:if test="$showDesc">
			<xsl:call-template name="infoRow">
            	<xsl:with-param name="tag" select="/OBJECT/SYSTEM/DESCRIPTION"/>
             	<xsl:with-param name="label" select="$TOK_DESC"/>
             	<xsl:with-param name="localRow" select="$localRow + 
                                                        number($showName) + 
                                                        number($showType) + 
                                                        number($showDesc)"/>
         	</xsl:call-template>
        </xsl:if>		


		<xsl:if test="$showShowInNews">
			<xsl:call-template name="infoRow">
            	<xsl:with-param name="tag" select="/OBJECT/SYSTEM/SHOWINNEWS"/>
             	<xsl:with-param name="label" select="$TOK_INNEWS"/>
             	<xsl:with-param name="localRow" select="$localRow + 
                                                        number($showName) + 
                                                        number($showType) + 
                                                        number($showDesc) +  
                                                        number($showShowInNews)"/>
         	</xsl:call-template>
        </xsl:if>		

		<xsl:if test="$showValidUntil">
			<xsl:call-template name="infoRow">
            	<xsl:with-param name="tag" select="/OBJECT/SYSTEM/VALIDUNTIL"/>
             	<xsl:with-param name="label" select="$TOK_EXPON"/>
             	<xsl:with-param name="localRow" select="$localRow + 
                                                        number($showName) + 
                                                        number($showType) + 
														number($showDesc) +
                                                        number($showShowInNews) + 
                                                        number($showValidUntil)"/>
         	</xsl:call-template>
        </xsl:if>		                
        
		<xsl:if test="$showCheckedOut and /OBJECT/SYSTEM/CHECKEDOUT">
			<xsl:call-template name="infoRow">
             	<xsl:with-param name="label" select="$TOK_CHECKEDOUT"/>
             	<xsl:with-param name="value">
                    <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="/OBJECT/SYSTEM/CHECKEDOUT/@DATE"/>
             	</xsl:with-param>
             	<xsl:with-param name="localRow" select="$localRow + 
                                                        number($showName) + 
                                                        number($showType) + 
														number($showDesc) + 
                                                        number($showShowInNews) +
                                                        number($showValidUntil) + 
                                                        number($showCheckedOut)"/>
         	</xsl:call-template>
        </xsl:if>		
	    
	</xsl:template>
    
    
	<!-- *********************************************************** 
	* This template generates the HTML-form for the system info
	* that containts owner, creator, changer
	*
	* @param localRow	the localRow that determines the zebra layout counter
	-->
    <xsl:template name="addSystemInfo">
	<xsl:param name="localRow" select="0"/>
	<xsl:param name="showOwner" select="false()"/>
	<xsl:param name="showCreated" select="true()"/>
	<xsl:param name="showChanged" select="true()"/>
		
				
        <xsl:if test="$showOwner">
            <xsl:call-template name="infoRow">
                <xsl:with-param name="label" select="$TOK_OWNER"/>
                <xsl:with-param name="value">
                    <xsl:value-of select="/OBJECT/SYSTEM/OWNER/@USERNAME"/>
                </xsl:with-param>
                <xsl:with-param name="localRow" select="$localRow + number($showOwner)"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="$showCreated">
            <xsl:call-template name="infoRow">
                <xsl:with-param name="label" select="$TOK_CREATED"/>
                <xsl:with-param name="value">
                    <xsl:value-of select="/OBJECT/SYSTEM/CREATED/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="/OBJECT/SYSTEM/CREATED/@DATE"/>
                </xsl:with-param>
                <xsl:with-param name="localRow" select="$localRow + number($showOwner) + number($showCreated)"/>
            </xsl:call-template>
        </xsl:if>			

        <xsl:if test="$showChanged">
            <xsl:call-template name="infoRow">
                <xsl:with-param name="label" select="$TOK_CHANGED"/>
                <xsl:with-param name="value">
                    <xsl:value-of select="/OBJECT/SYSTEM/CHANGED/@USERNAME"/>
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="/OBJECT/SYSTEM/CHANGED/@DATE"/>
                </xsl:with-param>
                <xsl:with-param name="localRow" select="$localRow + number($showOwner) + number($showCreated) + number($showChanged)"/>
            </xsl:call-template>
        </xsl:if>			
            		
	</xsl:template>

    	
	<!-- *********************************************************** 
	* This template generates the standard form validation javascript
	* for the specified system-fields.
	*
	* @param checkName 	check System-Name with the default restrictions
	* @param checkDesc 	Check System-Description with the default restrictions
	* @param preScript 	Adds the given JavaScript-Code to the Function (above the default restrictions)
	* @param postScript Adds the given JavaScript-Code to the Function (under the default restrictions)
	-->
	<xsl:template name="addStandardValidationJS">
		<xsl:param name="checkName" select="true()"/>
		<xsl:param name="checkDesc" select="false()"/>
        <xsl:param name="checkValidUntil" select="false()"/>
		<xsl:param name="preScript" select="''"/>
		<xsl:param name="postScript" select="''"/>

        <SCRIPT LANGUAGE="JavaScript">
            function xslSubmitAllowed()
            {	
            	<xsl:copy-of select="$preScript"/>
            
				<xsl:if test="$checkName">
	                <xsl:call-template name="getRestrictions">
	                    <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/NAME"/>
	                </xsl:call-template>
                </xsl:if>

				<xsl:if test="$checkDesc">
	                <xsl:call-template name="getRestrictions">
	                    <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/DESCRIPTION"/>
	                </xsl:call-template>
				</xsl:if>            

				<xsl:if test="$checkValidUntil">                
	                <xsl:call-template name="getRestrictions">
	                    <xsl:with-param name="tagName" select="/OBJECT/SYSTEM/VALIDUNTIL"/>
	                </xsl:call-template>
				</xsl:if>                        
                        								
				<xsl:for-each select="/OBJECT/VALUES/VALUE">
                    <xsl:call-template name="getRestrictions">
                        <xsl:with-param name="tagName" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
				
				<xsl:copy-of select="$postScript"/>
				
                return true;
            }
            <!-- create the getdObjecRef Javascript function -->
            <xsl:call-template name="createGetdObjecRefJS"/>
        </SCRIPT>
		
	</xsl:template> <!-- addStandardValidationJS -->
    
    
    <!--***********************************************************************
     * This template generates the standard form validation javascript.
     *   
     * @deprecated Method is for downward compatibility   
     *-->
    <xsl:template name="createStandardValidationJS">
        
        <xsl:call-template name="addStandardValidationJS">
            <xsl:with-param name="checkName" select="false()"/>
            <xsl:with-param name="checkDesc" select="false()"/>
            <xsl:with-param name="checkValidUntil" select="false()"/>            
        </xsl:call-template>    

    </xsl:template> <!-- createStandardValidationJS -->


    <!--***********************************************************************
     * This template generates the getdObjecRef javascript function needed
     * for objectref fields 
     *-->
    <xsl:template name="createGetdObjecRefJS">

        <![CDATA[
        function getdObjecRef (incompleteURL, objectRef, objectRef_M, objectRefName)
        {
            var completeURL = incompleteURL;

            completeURL += '&' + objectRefName + '=' + escape (objectRef);
            completeURL += '&' + objectRefName + '_M=' + objectRef_M;

            return completeURL;
        } // getdObjecRef
        ]]><!--CDATA-->
    </xsl:template> <!-- createGetdObjecRefJS -->


    <!-- ***********************************************************************
     * This template generates a HTML non-breaking-space (nbsp).
     *
     * @param   count       The count for how much nbsp should be generated.
     * @param   actCount    The current count of generatd nbsp (only intern).
     *-->
    <xsl:template name="createNBSP">
        <xsl:param name="count" select="1"/>
        <xsl:param name="actCount" select="0"/>

        <xsl:if test="$actCount &lt; $count">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>

            <xsl:call-template name="createNBSP">
                <xsl:with-param name="count" select="$count"/>
                <xsl:with-param name="actCount" select="$actCount + 1"/>
            </xsl:call-template> 
        </xsl:if> 
    </xsl:template> <!-- createNBSP -->
    
    
    <!-- **************************************************************************
    * This is general template to display a field in the appropriate perspective
    *
    * @param tagName       the tag to handle
    * @param mode          edit or view mode
    * @param roundDigits   the round digits
    *
    * NEW PARAMS:
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param mandatory     mandatory option
    * @param readonly      readonly option
    * @param classId       css class
    * @param style         css style
    * @param size          size
    * @param maxlength     maxlength
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display
    * @param errortext     an error text to display
    -->
    <xsl:template name="getDatatype">
    <xsl:param name="tagName"/>
    <xsl:param name="mode" select="$mode"/>
    <xsl:param name="roundDigits"/>
    <!-- the new params -->
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="onClick"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly" select="$tagName/@READONLY"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="objOID"/>
    <xsl:param name="allowedFileTypes"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:choose>
            <!-- MODE_VIEW -->
            <xsl:when test="$mode = $MODE_VIEW">
                <xsl:call-template name="view">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="roundDigits" select="$roundDigits"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:if test="$tagName/@UNIT != ''">
                    <xsl:value-of select="$tagName/@UNIT"/>
                </xsl:if>
            </xsl:when><!--MODE_VIEW-->

            <!-- MODE_EDIT -->
            <xsl:when test="$mode = $MODE_EDIT">
                <xsl:call-template name="edit">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="onClick" select="$onClick"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                    <xsl:with-param name="objOID" select="$objOID"/>
                    <xsl:with-param name="allowedFileTypes" select="$allowedFileTypes"/>
                </xsl:call-template>
                <xsl:if test="$tagName/@TYPE != 'FIELDREF' and $tagName/@TYPE != 'OBJECTREF'">
                	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
               	</xsl:if>
                <xsl:if test="$tagName/@UNIT != ''">
                    <xsl:value-of select="$tagName/@UNIT"/>
                </xsl:if>
            </xsl:when><!--MODE_EDIT-->

            <!-- MODE_HIDDEN -->
            <xsl:when test="$mode = $MODE_HIDDEN">
                <xsl:call-template name="hidden">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--MODE_HIDDEN-->

            <xsl:otherwise>
                <xsl:value-of select="$tagName/child::text ()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> <!-- getDatatype -->


    <!-- **************************************************************************
     * Create a VIEW perspective of a given system or value tag
     *
     * @param tagName       	the tag to handle
     * @param roundDigits   	the round digits
     * @param datatype      	the datatype
     * @param input         	the input name
     * @param value         	the value of the tag
     *
    -->
    <xsl:template name="view">
	    <xsl:param name="tagName"/>
	    <xsl:param name="roundDigits" select="0"/>
	    <xsl:param name="datatype" select="$tagName/@TYPE"/>
	    <xsl:param name="input" select="$tagName/@INPUT"/>
	    <xsl:param name="value" select="$tagName/child::text ()"/>    
	    <!-- new param formName -->
        <xsl:param name="formName"/>
        <xsl:variable name="multiSelection" select ="translate ($tagName/@MULTISELECTION, 'YES', 'yes')"/>
	    <xsl:variable name="multiple" select="translate ($tagName/@MULTIPLE, 'YES', 'yes')"/>
        
        <!-- if no formName is given, set default name to sheetForm -->
        <xsl:variable name="form">
            <xsl:choose>
                <xsl:when test="$formName = ''">
                    <xsl:value-of select="$FORM_DEFAULT"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$formName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <!--*********************** SYSTEM  ****************************-->
            <!-- NAME -->
            <xsl:when test="$input = 'nomen'">
                <xsl:value-of select="$value"/>
            </xsl:when> <!-- NAME -->

            <!-- DESCRIPTION -->
            <xsl:when test="$input = 'desc'">
                <xsl:call-template name="dt_longtext">
                    <xsl:with-param name="tagName" select="$tagName"/>
                </xsl:call-template>
            </xsl:when> <!-- DESCRIPTION -->

            <!-- VALIDUNTIL -->
            <xsl:when test="$input = 'vu'">
                <xsl:value-of select="$value"/>
            </xsl:when> <!-- VALIDUNTIL -->

            <!-- SHOWINNEWS -->
            <xsl:when test="$input = 'innews'">
                <xsl:variable name="boolValue"
                              select="translate ($value, 'TRUEYSJA', 'trueysja')"/>

                <xsl:choose>
                    <xsl:when test="$boolValue = $BOOL_YES">
                        <xsl:value-of select="$TOK_YES"/>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:value-of select="$TOK_NO"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when> <!-- SHOWINNEWS -->

            <!--*********************** VALUES ***************************-->
            <xsl:when test="$datatype = 'TEXT' or $datatype = 'CHAR'">
                <xsl:value-of select="$value"/>
            </xsl:when><!-- TEXT, CHAR -->

            <!-- LONGTEXT -->
            <xsl:when test="$datatype ='LONGTEXT'">
                    <xsl:call-template name="dt_longtext">
                        <xsl:with-param name="tagName" select="$tagName"/>
                    </xsl:call-template>
            </xsl:when><!-- LONGTEXT -->

            <!-- HTMLTEXT -->
            <xsl:when test="$datatype ='HTMLTEXT'">
                <xsl:value-of disable-output-escaping="yes" select="$value"/>
            </xsl:when><!-- HTMLTEXT -->

            <!-- BOOLEAN -->
            <xsl:when test="$datatype = 'BOOLEAN'">
                <xsl:variable name="boolValue"
                              select="translate ($value, 'TRUEYSJA', 'trueysja')"/>

                <xsl:choose>
                    <xsl:when test="$boolValue = $BOOL_TRUE or
                                    $boolValue = $BOOL_JA or
                                    $boolValue = $BOOL_YES">
                        <xsl:value-of select="$TOK_YES"/>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:value-of select="$TOK_NO"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when> <!--BOOLEAN-->

            <!-- INTEGER, NUMBER, FLOAT,  DOUBLE -->
            <xsl:when test="$datatype = 'INTEGER' or
                            $datatype = 'NUMBER' or
                            $datatype = 'FLOAT' or
                            $datatype = 'DOUBLE'">
                <xsl:choose>
                    <xsl:when test="$roundDigits &gt; 0">
                        <xsl:value-of select="round ($value * $roundDigits) div $roundDigits"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$value"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when> <!-- INTEGER, NUMBER, FLOAT,  DOUBLE -->

            <!-- MONEY -->
            <xsl:when test="$datatype = 'MONEY'">
                <xsl:variable name="moneyBeforeSep" select="substring-before ($tagName, ',')"/>
                <xsl:variable name="moneyBeforeSepLen" select="string-length ($moneyBeforeSep)"/>
                <xsl:call-template name="getDotSeparatedMoney">
                    <xsl:with-param name="moneyValue" select="$moneyBeforeSep"/>
                    <xsl:with-param name="moneyValueAfter" select="substring ($tagName, $moneyBeforeSepLen + 1)"/>
                    <xsl:with-param name="curLen" select="$moneyBeforeSepLen"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- MONEY -->

            <!-- OBJECTREF -->
            <xsl:when test="$datatype = 'OBJECTREF'">
                <xsl:variable name="refOid" select="substring-before($value,',')"/>
                <xsl:variable name="refName" select="substring-after($value,',')"/>

                <xsl:if test="$refName != ''">
                    <IMG SRC="{$layoutPath}images/objectIcons/Referenz.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:if>
                <xsl:call-template name="internDt_href">
                    <xsl:with-param name="a_href">javascript:top.showObject('<xsl:value-of select="$refOid"/>');</xsl:with-param>
                    <xsl:with-param name="a_text" select="$refName"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- OBJECTREF -->

            <!-- FIELDREF -->
            <xsl:when test="$datatype = 'FIELDREF'">
                <xsl:call-template name="m2Dt_fieldref_view">
                    <xsl:with-param name="curTag" select="$tagName"/>
                    <xsl:with-param name="value" select="$value"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- FIELDREF -->
            
            <!-- VALUEDOMAIN -->
            <xsl:when test="$datatype = 'VALUEDOMAIN' and name ($tagName) != 'RESULTELEMENT'">
                <xsl:call-template name="m2Dt_valuedomain_view">
                    <xsl:with-param name="curTag" select="$tagName"/>
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when> <!-- VALUEDOMAIN -->
            
            <!-- Multiselectable SELECTIONBOX,QUERYSELECTIONBOX -->
            <xsl:when test="($datatype = 'SELECTIONBOX' or $datatype = 'QUERYSELECTIONBOX') and $multiSelection=$BOOL_YES">
				<xsl:for-each select="$tagName/OPTION[@SELECTED = 1]">
                	<xsl:value-of select="child::text ()"/>
                    <xsl:if test ="position()!=last()">
                       	<BR/>
                    </xsl:if>          
	            </xsl:for-each>
            </xsl:when> <!-- Multiselectable SELECTIONBOX,QUERYSELECTIONBOX -->

            <!-- QUERY -->
            <xsl:when test="$datatype ='QUERY'">
                <xsl:call-template name="dt_query">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--QUERY-->

            <!-- FILE -->
            <xsl:when test="$datatype = 'FILE'"><!--DT_FILE-->
                <xsl:if test="string-length($tagName/child::text ()) &gt; 0">
                    <xsl:call-template name="dt_file">
                        <xsl:with-param name="tagName" select="$tagName"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when><!--FILE-->


             <!-- REMINDER -->
            <xsl:when test="$datatype = 'REMINDER'">
                <xsl:call-template name="dt_reminder">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="mode" select="$MODE_VIEW"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- REMINDER -->

            <!-- OPTION -->
            <xsl:when test="$datatype = 'OPTION'">
                <xsl:value-of select="substring-before ($value, ',')"/>
            </xsl:when><!--OPTION-->

            <!-- EMAIL -->
            <xsl:when test="$datatype = 'EMAIL'">
                <xsl:call-template name="internDt_href">
                    <xsl:with-param name="a_href">mailto:<xsl:value-of select="$value"/></xsl:with-param>
                    <xsl:with-param name="a_text" select="$value"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- EMAIL -->

            <!-- LINK -->
            <xsl:when test="$datatype = 'LINK'">
                <xsl:choose>
                    <xsl:when test="boolean ($tagName/@PROTOCOL)">
                        <xsl:call-template name="internDt_href">
                            <xsl:with-param name="a_href">javascript:top.loadLink('<xsl:value-of select="$value"/>');</xsl:with-param>
                            <xsl:with-param name="a_text" select="$value"/>
                            <xsl:with-param name="formName" select="$form"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="internDt_href">
                            <xsl:with-param name="a_href">javascript:top.loadLink('http://<xsl:value-of select="$value"/>');</xsl:with-param>
                            <xsl:with-param name="a_text" select="$value"/>
                            <xsl:with-param name="formName" select="$form"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when><!--LINK-->

            <!-- IMAGE -->
            <xsl:when test="$datatype = 'IMAGE'">
                <xsl:if test="string-length ($tagName/child::text ()) &gt; 0">
                    <xsl:call-template name="dt_image">
                        <xsl:with-param name="tagName" select="$tagName"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when><!--IMAGE-->

            <!-- BUTTON -->
            <xsl:when test="$datatype = 'BUTTON'">
                <xsl:call-template name="internDt_button">
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="value" select="substring-before ($value, ',')"/>
                    <xsl:with-param name="onClick" select="substring-after ($value, ',')"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!--BUTTON-->

            <!-- RESULTELEMENT with multiple = true -->
            <xsl:when test="name ($tagName) = 'RESULTELEMENT' and $multiple = $BOOL_YES">
				<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
				var str = "<xsl:value-of select="$value"/>";
				<![CDATA[
				<!--
				//replace '|' with ', '
				document.write (str.replace (/\|/g, "<BR/>"));
				//-->
				]]>
				</SCRIPT>
            </xsl:when> <!--RESULTELEMENT with multiple = true -->

            <!-- ALL OTHER TYPES -->
            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>

        <!-- Sets a &nbsp; for all values except value domain, fieldref-->
        <xsl:if test="$datatype != 'VALUEDOMAIN' and $datatype != 'FIELDREF' and $datatype != 'OBJECTREF'">
	        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:if>
    </xsl:template> <!-- view -->


    <!-- **************************************************************************
    * Create a EDIT perspective of a given system or value tag
    *
    * @param tagName       the tag to handle
    * @param datatype      the datatype
    * @param input         the input name
    * @param value         the value of the tag
    *
    * NEW PARAMS:
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param mandatory     mandatory option
    * @param readonly      readonly option
    * @param classId       css class
    * @param style         css style
    * @param size          size
    * @param maxlength     maxlength
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display
    * @param errortext     an error text to display
    -->
    <xsl:template name="edit">
    <xsl:param name="tagName"/>
    <xsl:param name="datatype" select="$tagName/@TYPE"/>
    <xsl:param name="input" select="$tagName/@INPUT"/>
    <xsl:param name="value" select="$tagName/child::text ()"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="onClick"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly" select="$tagName/@READONLY"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="objOID"/>
    <xsl:param name="allowedFileTypes"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:choose>
            <!--*********************** SYSTEM  ****************************-->
            <!-- NAME -->
            <xsl:when test="$input = 'nomen'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="40"/>
                    <xsl:with-param name="maxlength" select="63"/>
                    <xsl:with-param name="mandatory" select="$BOOL_YES"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- NAME -->

            <!-- DESCRIPTION -->
            <xsl:when test="$input = 'desc'"> <!-- DESCRIPTION -->
                <xsl:call-template name="internDt_textarea">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="maxlength" select="255"/>
                    <xsl:with-param name="cols" select="50"/>
                    <xsl:with-param name="rows" select="2"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when>

            <!-- VALIDUNTIL -->
            <xsl:when test="$input = 'vu'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="10"/>
                    <xsl:with-param name="maxlength" select="10"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:value-of select="$TOK_DATEFORMAT"/>
            </xsl:when> <!-- VALIDUNTIL -->

            <!-- SHOWINNEWS -->
            <xsl:when test="$input = 'innews'">
                <xsl:call-template name="internDt_checkbox">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="value" select="$value"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_bool</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="$BOOL_FALSE"/>
                </xsl:call-template>
            </xsl:when> <!-- SHOWINNEWS -->


            <!--*********************** VALUES  ****************************-->
            <!-- TEXT or CHAR -->
            <xsl:when test="$datatype = 'TEXT' or $datatype = 'CHAR'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>40</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>255</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- TEXT or CHAR -->

            <!-- HTMLTEXT -->
            <xsl:when test="$datatype = 'HTMLTEXT'">
                <xsl:call-template name="internDt_textarea">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>65535</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="cols" select="60"/>
                    <xsl:with-param name="rows" select="12"/>
                    <xsl:with-param name="hasLines" select="false()"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- HTMLTEXT -->

            <!-- LONGTEXT -->
            <xsl:when test="$datatype = 'LONGTEXT'">
                <xsl:call-template name="internDt_textarea">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>65535</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="cols" select="60"/>
                    <xsl:with-param name="rows" select="2"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--LONGTEXT-->

            <!-- NAME -->
            <xsl:when test="$datatype = 'NAME'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>40</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>63</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- NAME -->

            <!-- BOOLEAN -->
            <xsl:when test="$datatype = 'BOOLEAN'">
                <xsl:call-template name="internDt_checkbox">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="value" select="$value"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="onClick" select="$onClick"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_bool</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="$BOOL_FALSE"/>
                </xsl:call-template>
            </xsl:when><!-- BOOLEAN -->

            <!-- TIME -->
            <xsl:when test="$datatype = 'TIME'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="5"/>
                    <xsl:with-param name="maxlength" select="5"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$TOK_TIMEFORMAT"/>
            </xsl:when><!--TIME-->

            <!-- DATE -->
            <xsl:when test="$datatype = 'DATE'">
                <xsl:call-template name="internDt_date">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                    <xsl:with-param name="objOID" select="$objOID"/>
                </xsl:call-template>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$TOK_DATEFORMAT"/>
            </xsl:when><!--DATE-->

            <!-- DATETIME -->
            <xsl:when test="$datatype = 'DATETIME'">

                <xsl:call-template name="internDt_date">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="fieldName">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_d</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="substring-before ($value, ' ')"/>
                    <xsl:with-param name="restriction" select="'iD'"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                    <xsl:with-param name="objOID" select="$objOID"/>
                </xsl:call-template>

                <xsl:call-template name="internDt_text_dateTime">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_t</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="substring-after ($value, ' ')"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="5"/>
                    <xsl:with-param name="maxlength" select="5"/>
                    <xsl:with-param name="restriction" select="'iT'"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <SPAN style="vertical-align: middle">
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="$TOK_DATEFORMAT"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="$TOK_TIMEFORMAT"/>
                </SPAN>
            </xsl:when> <!-- DATETIME -->

            <!-- not used:
            <xsl:when test="$datatype = 'DT_DATERANGE'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName/BEGIN"/>
                    <xsl:with-param name="size" select="10"/>
                    <xsl:with-param name="maxlength" select="10"/>
                </xsl:call-template>
                <xsl:value-of select="$TOK_DATEFORMAT"/>
                <xsl:text> - </xsl:text>
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName/END"/>
                    <xsl:with-param name="size" select="10"/>
                    <xsl:with-param name="maxlength" select="10"/>
                </xsl:call-template>
                <xsl:value-of select="$TOK_DATEFORMAT"/>
            </xsl:when>--> <!--DT_DATERANGE-->


            <!-- INTEGER or NUMBER-->
            <xsl:when test="$datatype = 'INTEGER' or $datatype = 'NUMBER'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>10</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>10</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--INTEGER or NUMBER-->

            <!-- MONEY or FLOAT or DOUBLE -->
            <xsl:when test="$datatype = 'MONEY' or
                            $datatype = 'FLOAT' or
                            $datatype = 'DOUBLE'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>20</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>255</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- MONEY or FLOAT or DOUBLE -->

            <!-- QUERYSELECTIONBOX, QUERYSELECTIONBOXNUM, QUERYSELECTIONBOXINT -->
            <!-- SELECTIONBOX, SELECTIONBOXNUM, SELECTIONBOXINT-->
            <!-- VALUEDOMAIN -->
            <xsl:when test="contains ($datatype,'SELECTIONBOX') or $datatype = 'VALUEDOMAIN'">
                <xsl:call-template name="dt_selectionbox">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="type" select="$datatype"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- *SELECTIONBOX-->

            <!-- OPTION -->
            <xsl:when test="$datatype = 'OPTION'">
                <xsl:call-template name="internDt_select">
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="optionString" select="$value"/>
                    <xsl:with-param name="selected" select="substring-before($value,',')"/>
                    <xsl:with-param name="isSystemSelect" select="0"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>1</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_op</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when><!-- OPTION -->

            <!-- FILE -->
            <xsl:when test="$datatype = 'FILE'">
                <xsl:variable name="fieldName">
                    <xsl:choose>
                        <xsl:when test="$objOID != '' and $objOID != ' '">
                            <xsl:value-of select="$tagName/@INPUT"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="$objOID"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$tagName/@INPUT"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="internDt_File">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="allowedFileTypes" select="$allowedFileTypes"/>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_path</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value">
                        <xsl:value-of select="$_PATH"/>
                        <xsl:value-of select="$FILE"/>
                        <xsl:text>\</xsl:text>
                        <xsl:value-of select="$oid"/>
                        <xsl:text>\</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_wwwp</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value">
                        <xsl:value-of select="$_WWWP"/>
                        <xsl:value-of select="$FILE"/>
                        <xsl:text>/</xsl:text>
                        <xsl:value-of select="$oid"/>
                        <xsl:text>/</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when><!-- FILE -->

            <!-- FIELDREF -->
            <xsl:when test="$datatype = 'FIELDREF'">
                <xsl:call-template name="m2Dt_fieldref_edit">
                    <xsl:with-param name="curTag" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- FIELDREF -->

            <!-- OBJECTREF -->
            <xsl:when test="$datatype = 'OBJECTREF'">
                <xsl:call-template name="m2Dt_objectref_edit">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- OBJECTREF -->

            <!-- IMAGE -->
            <xsl:when test="$datatype   = 'IMAGE'">
                <xsl:call-template name="internDt_File">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_path</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value">
                        <xsl:value-of select="$_PATH"/>
                        <xsl:value-of select="$FILE"/>
                        <xsl:text>\</xsl:text>
                        <xsl:value-of select="$oid"/>
                        <xsl:text>\</xsl:text>
<!-- KR 20060120 replaced by standard upload path
                        <xsl:value-of select="$_PATH"/>
                        <xsl:value-of select="$IMAGE"/>
                        <xsl:text>\</xsl:text>
<!- -
                        <xsl:value-of select="$oid"/>
                        <xsl:text>\</xsl:text>
- ->
-->
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name">
                        <xsl:value-of select="$tagName/@INPUT"/>
                        <xsl:text>_wwwp</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value">
                        <xsl:value-of select="$_WWWP"/>
                        <xsl:value-of select="$FILE"/>
                        <xsl:text>/</xsl:text>
                        <xsl:value-of select="$oid"/>
                        <xsl:text>/</xsl:text>
<!-- KR 20060120 replaced by standard upload path
                        <xsl:value-of select="$_WWWP"/>
                        <xsl:value-of select="$IMAGE"/>
                        <xsl:text>/</xsl:text>
<!- -
                        <xsl:value-of select="$oid"/>
                        <xsl:text>/</xsl:text>
- ->
-->
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when><!--IMAGE-->


            <!-- REMINDER -->
            <xsl:when test="$datatype = 'REMINDER'">
                <xsl:call-template name="dt_reminder">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="mode" select="$MODE_EDIT"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- REMINDER -->

            <!-- LINK -->
            <xsl:when test="$datatype = 'LINK' or $datatype = 'EMAIL'">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size">
                        <xsl:choose>
                        <xsl:when test="$size">
                            <xsl:value-of select="$size"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>40</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="maxlength">
                        <xsl:choose>
                        <xsl:when test="$maxlength">
                            <xsl:value-of select="$maxlength"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>255</xsl:text>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- LINK or EMAIL -->

            <!-- PASSWORD -->
            <xsl:when test="$datatype = 'PASSWORD'">
                <xsl:call-template name="internDt_password">
                    <xsl:with-param name="curTag" select="$tagName"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="errortext" select="$errortext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- PASSWORD -->

            <!-- BUTTON -->
            <xsl:when test="$datatype = 'BUTTON'">
                <xsl:call-template name="internDt_button">
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="value" select="substring-before ($value, ',')"/>
                    <xsl:with-param name="classId" select="$classId"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="onClick" select="substring-after ($value, ',')"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="helptext" select="$helptext"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!-- BUTTON -->

            <!-- REMARK -->
            <xsl:when test="$datatype = 'REMARK'">
                <xsl:call-template name="internDt_hidden">
                    <xsl:with-param name="name" select="$tagName/@INPUT"/>
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
                <xsl:value-of select="$tagName/child::text ()"/>
            </xsl:when><!-- REMARK -->

            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> <!-- edit -->


    <!-- **************************************************************************
     * Create a hidden element
     *
     * @param tagName       the tag to handle
     * @param value         the value for the inputtag
     * @param fieldname     the fieldname for the inputtag
    -->
    <xsl:template name="hidden">
    <xsl:param name="tagName"/>
    <xsl:param name="value" select="$tagName/child::text ()"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>

        <INPUT TYPE="HIDDEN" NAME="{$fieldName}" VALUE="{$value}"/>
    </xsl:template> <!-- hidden -->


    <!-- ***********************************************************************
    * Create an input tag with the given parameters.
    * This is a very common template that can be used everywhere an input tag
    * is needed.
    *
    * @param inputType      type of the input tag
    * @param fieldName      name of the input field
    * @param fieldId        id of the input field
    * @param datatype       datatype of the field determines the restriction if
    *                       restriction is not set
    * @param restriction    a specific restriction to set
    * @param value          value of the field
    * @param checked        checked Option of Checkboxes
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
    * @param hidden         hidden option
    * @param mandatory      mandatory option (true|false)
    * @param viewType       view type attribte
    * @param readonly       readonly option (true|false)
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param onKeyPress		onKeyPress event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_input">
    <xsl:param name="inputType" select="'TEXT'"/>
    <xsl:param name="fieldName"/>
    <xsl:param name="fieldId" select="$fieldName"/>
    <xsl:param name="datatype"/>
    <xsl:param name="restriction"/>
    <xsl:param name="value"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="'40'"/>
    <xsl:param name="maxlength" select="'63'"/>
    <xsl:param name="hidden" select="false()"/>
    <xsl:param name="mandatory" select="false ()"/>
    <xsl:param name="viewType"/>
    <xsl:param name="readonly" select="false ()"/>
    <xsl:param name="onClick"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="onKeyPress"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <xsl:param name="checked" select="false()"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="allowedFileTypes"/>
    
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:choose>
            <xsl:when test="$hidden">
                <INPUT TYPE="HIDDEN" ID="{$fieldName}"  NAME="{$fieldName}" VALUE="{$value}"/>
            </xsl:when>
            <xsl:otherwise>
                <INPUT TYPE="{$inputType}" VALUE="{$value}" STYLE="{$style}"
                       NAME="{$fieldName}" ID="{$fieldId}"
                       SIZE="{$size}" MAXLENGTH="{$maxlength}" TITLE="{$helptext}"
                       TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}">

                    <xsl:attribute name="ONCHANGE">
                        <xsl:value-of select="$onChange"/>
                        <xsl:choose>
                        <xsl:when test="not($datatype) and not($restriction)">
                            <!-- no restriction -->
                        </xsl:when>
                        <xsl:when test="$restriction">
                            <xsl:call-template name="buildRestriction">
                                <xsl:with-param name="restrictionName" select="$restriction"/>
                                <xsl:with-param name="fieldName" select="$fieldName"/>
                                <xsl:with-param name="mandatory" select="$mandatory"/>
                                <xsl:with-param name="formName" select="$form"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="getRestrictionForValue">
                                <xsl:with-param name="fieldName" select="$fieldName"/>
                                <xsl:with-param name="datatype" select="$datatype"/>
                                <xsl:with-param name="value" select="$value"/>
                                <xsl:with-param name="mandatory" select="$mandatory"/>
                                <xsl:with-param name="formName" select="$form"/>
                                <xsl:with-param name="viewType" select="$viewType"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>

                    <xsl:attribute name="ONSELECT">
                        <xsl:value-of select="$onSelect"/>
                    </xsl:attribute>

                    <xsl:attribute name="ONFOCUS">
                        <xsl:text>this.select ();</xsl:text>
                        <xsl:value-of select="$onFocus"/>
                    </xsl:attribute>

                    <xsl:attribute name="ONBLUR">
                        <xsl:value-of select="$onBlur"/>
                    </xsl:attribute>
                    
                    <xsl:attribute name="ONCLICK">
                        <xsl:value-of select="$onClick"/>
                    </xsl:attribute>
                    
                    <xsl:attribute name="ONKEYPRESS">
                        <xsl:value-of select="$onKeyPress"/>
                    </xsl:attribute>
                    
                    <xsl:attribute name="CLASS">
                        <xsl:value-of select="$classId"/>
                        <xsl:if test="$mandatory">
                            <xsl:text> mandatory</xsl:text>
                        </xsl:if>
                    </xsl:attribute>

                    <xsl:if test="$readonly">
                        <xsl:attribute name="READONLY"/>
                    </xsl:if>

                    <xsl:if test="$checked">
                        <xsl:attribute name="CHECKED"/>
                    </xsl:if>

                </INPUT>
                <xsl:if test="$allowedFileTypes != '' and $allowedFileTypes != ' '">
                    <NOBR>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;(</xsl:text>
                        <xsl:value-of select="$allowedFileTypes"/>
                        <xsl:text disable-output-escaping="yes">)</xsl:text>
                    </NOBR>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template><!-- internDt_input -->


    <!-- ***********************************************************************
    * Create a text field
    *
    * @param tagName       field tag
    * @param restriction   a specific restriction to set
    * @param classId       css class
    * @param style         css style
    * @param size          size
    * @param maxlength     maxlength
    * @param hidden        hidden option
    * @param mandatory     mandatory option
    * @param readonly      readonly option
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display (as title)
    * @param errortext     an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_text">
    <xsl:param name="tagName"/>
    <xsl:param name="restriction" select="'iTx'"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="hidden"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly" select="$tagName/@READONLY"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="value" select="$tagName/child::text ()"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
<!--
        <xsl:variable name="mandatory2">
            <xsl:value-of select="$mandatory"/>
            <xsl:if test="$mandatory=''">
                <xsl:value-of select="$tagName/@MANDATORY"/>
            </xsl:if>
        </xsl:variable>
-->
        <xsl:call-template name="internDt_input">
            <xsl:with-param name="fieldName" select="$tagName/@INPUT"/>
            <xsl:with-param name="datatype" select="$tagName/@TYPE"/>
            <xsl:with-param name="restriction" select="$restriction"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="$style"/>
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="maxlength" select="$maxlength"/>
            <xsl:with-param name="hidden" select="$hidden"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

    </xsl:template> <!-- internDt_text -->


    <!-- ***********************************************************************
    * Create a text field with the given parameter
    *
    * This template is deprecated and has been replaced by internDt_input
    *
    *
    * @param fieldName     field name
    * @param datatype      datatype
    * @param classId       css class
    * @param style         css style
    * @param size          size
    * @param maxlength     maxlength
    * @param hidden        hidden option
    * @param mandatory     mandatory option (YES|NO)
    * @param readonly      readonly option (YES|NO)
    * @param value         the value
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display (as title)
    * @param errortext     an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_textForValue">
    <xsl:param name="fieldName"/>
    <xsl:param name="datatype"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="hidden" select="false()"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="readonly"/>
    <xsl:param name="value"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:call-template name="internDt_input">
            <xsl:with-param name="fieldName" select="$fieldName"/>
            <xsl:with-param name="datatype" select="$datatype"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="$style"/>
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="maxlength" select="$maxlength"/>
            <xsl:with-param name="hidden" select="$hidden"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

    </xsl:template><!--internDt_textForValue-->


    <!--************************************************************************
    * Create a date field
    *
    * @param tagName       the field tag
    * @param fieldName     fieldName
    * @param value         value
    * @param restriction   a specific restriction to set
    * @param classId       css class
    * @param style         css style
    * @param mandatory     mandatory option
    * @param readonly      readonly option
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display (as title)
    * @param errortext     an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_date">
    <xsl:param name="tagName"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>
    <xsl:param name="value" select="$tagName/child::text ()"/>
    <xsl:param name="restriction" select="'iD'"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly"  select="$tagName/@READONLY"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="objOID"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:variable name="uniqueFieldName">
            <xsl:choose>
                <xsl:when test="$objOID != '' and $objOID != ' '">
                    <xsl:value-of select="$fieldName"/>
                    <xsl:text>_</xsl:text>
                    <xsl:value-of select="$objOID"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$fieldName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
    
        <!-- must be imported that way. when loaded via scripts.htm it does not work -->
        <script type="text/javascript" language="javascript" src="scripts/scriptCalendarPopup.js"></script>

        <SCRIPT type="text/javascript" language="javascript">
            if (! calPopup)
            {
                var calPopup;
                if (top.system.browser.type == "IE" &amp;&amp; top.system.browser.version &lt;= "6")
                {
                    calPopup = new CalendarPopup ();
                } // if
                else
                {
                    document.write ('<DIV ID="calPopup" STYLE="position:absolute;z-index:1;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>');
                    calPopup = new CalendarPopup ("calPopup");
                } // else
                calPopup.setWeekStartDay (1);

                calPopup.setMonthNames(<xsl:value-of select="$TOK_MONTH_NAMES"/>);
                calPopup.setDayHeaders(<xsl:value-of select="$TOK_DAY_HEADERS"/>);
                calPopup.setTodayText ('<xsl:value-of select="$TOK_TODAY"/>');
                calPopup.showNavigationDropdowns();
<!--> alternative view:
                calPopup.showYearNavigation();
                calPopup.showYearNavigationInput();
-->
                document.write (getCalendarStyles ());
             } // if
        </SCRIPT>

        <xsl:call-template name="internDt_input">
            <!--xsl:with-param name="fieldName" select="$fieldName"/-->
            <xsl:with-param name="fieldName" select="$uniqueFieldName"/>
            <xsl:with-param name="restriction" select="$restriction"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="concat ('vertical-align: middle;', $style)"/>
            <xsl:with-param name="size" select="10"/>
            <xsl:with-param name="maxlength" select="10"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

        <xsl:call-template name="createDateButton">
            <xsl:with-param name="fieldName" select="$uniqueFieldName"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>
    </xsl:template> <!-- internDt_date -->


    <!--************************************************************************
    * Create a date time field
    *
    * @param name           field name
    * @param value          the value
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
    * @param restriction    a restriction
    * @param mandatory      mandatory option
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_text_dateTime">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="10"/>
    <xsl:param name="maxlength" select="10"/>
    <xsl:param name="restriction"/>
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
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    

        <xsl:call-template name="internDt_input">
            <xsl:with-param name="fieldName" select="$name"/>
            <xsl:with-param name="restriction" select="$restriction"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="concat ('vertical-align: middle;', $style)"/>
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="maxlength" select="$maxlength"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

    </xsl:template> <!--internDt_text_dateTime-->

    <!-- ***********************************************************************
     * Create a text field with the given parameter
     *
     * @param curTag        th field tag
     * @param classId        css class
     * @param style          css style
     * @param size          size
     * @param maxlength     maxlength
     * @param mandatory      mandatory option
     * @param readonly       readonly option
     * @param onChange       onChange event handler
     * @param onSelect       onSelect event handler
     * @param onFocus        onFocus event handler
     * @param onBlur         onBlur event handler
     * @param tabindex       tabindex
     * @param accesskey      access key
     * @param helptext      a help text to display (as title)
     * @param errortext     an error text to display    NOT SUPPORTED
-->
    <xsl:template name="internDt_password">
    <xsl:param name="curTag"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="20"/>
    <xsl:param name="maxlength" select="255"/>
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
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:call-template name="internDt_input">
            <xsl:with-param name="inputType" select="'PASSWORD'"/>
            <xsl:with-param name="fieldName" select="$curTag/@INPUT"/>
            <xsl:with-param name="datatype" select="$curTag/@TYPE"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="$style"/>
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="maxlength" select="$maxlength"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$curTag/child::text ()"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

    </xsl:template><!--internDt_password-->


    <!--************************************************************************
    * Create a button
    *
    * @param name           field name
    * @param type           button type (default: BUTTON)
    * @param value          value
    * @param classId        class
    * @param style          style
    * @param title          title ==> helptext should be used instead
    * @param label          button label
    * @param onClick        on click event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       helptext
        *
    -->
    <xsl:template name="internDt_button">
    <xsl:param name="name"/>
    <xsl:param name="type" select="'BUTTON'"/>
    <xsl:param name="value"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="title"/>
    <xsl:param name="label" select="$value"/>
    <xsl:param name="onClick"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>

        <BUTTON TYPE="{$type}" STYLE="{$style}" CLASS="{$classId}"
                ID="{$name}" NAME="{$name}" VALUE="{$value}"
                ONCLICK="{$onClick}" TITLE="{$title}{$helptext}"
                TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}">
            <xsl:copy-of select="$label"/>
        </BUTTON>
    </xsl:template> <!--internDt_button-->


    <!--************************************************************************
    * Create a textarea field
    *
    * @param tagName        the field tag
    * @param maxlength      maxlength
    * @param cols           number of cols
    * @param rows           number of rows
    * @param colInc         the increment to resize the cols of hte textarea
    * @param rowInc         the increment to resize the rows of hte textarea
    * @param classId        css class
    * @param style          css style
    * @param hasLines       read the content from within <LINE> tags or as text
    * @param mandatory      mandatory option
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       abindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    * @param isResizable    option to activate dynamic resizing of the textarea
    -->
    <xsl:template name="internDt_textarea">
    <xsl:param name="tagName"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="cols"/>
    <xsl:param name="rows"/>
    <xsl:param name="rowInc" select="'5'"/>
    <xsl:param name="colInc" select="'10'"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="hasLines" select="false ()"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly" select="$tagName/@READONLY"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <xsl:param name="setSize" select="false()"/>
    <xsl:param name="isResizable" select="true()"/>
    <xsl:param name="showResizeBtns" select="true()"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
                
        <xsl:variable name="fieldName">
            <xsl:value-of select="$form"/>
            <xsl:text>.</xsl:text>
            <xsl:value-of select="$tagName/@INPUT"/>
        </xsl:variable>
    
        <xsl:variable name="rowIncAlt">
            <xsl:choose>
            <xsl:when test="$isResizable">
                <xsl:value-of select="'0'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$rowInc"/>
            </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <TEXTAREA ID="{$tagName/@INPUT}" NAME="{$tagName/@INPUT}"
                  MAXLENGTH="{$maxlength}" ROWS="{$rows}" COLS="{$cols}"
                  WRAP="VIRTUAL" TITLE="{$helptext}" STYLE="{$style}"
                  TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}">

            <xsl:attribute name="ONCHANGE">
                <xsl:call-template name="getRestriction">
                    <xsl:with-param name="tagName" select="$tagName/@INPUT"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
                <xsl:value-of select="$onChange"/>
            </xsl:attribute>

            <xsl:attribute name="ONSELECT">
                <xsl:value-of select="$onSelect"/>
            </xsl:attribute>

            <xsl:attribute name="ONFOCUS">
                <xsl:text>this.select ();</xsl:text>
                <xsl:value-of select="$onFocus"/>
            </xsl:attribute>

            <xsl:attribute name="ONBLUR">
                <xsl:value-of select="$onBlur"/>
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

            <xsl:if test="$isResizable">
                <xsl:attribute name="ONKEYUP">
                    <xsl:text>top.scripts.dynResizeTextArea (this);</xsl:text>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="normalize-space ($tagName) != ''">
                <xsl:choose>
                <xsl:when test="$hasLines">
                    <xsl:for-each select="$tagName/LINE">
                        <xsl:value-of select="./child::text()"/>
                        <xsl:if test="position() != last()">
                            <xsl:value-of select="$CR"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of disable-output-escaping="yes" select="$tagName/child::text()"/>
                </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </TEXTAREA>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>

        <xsl:if test="$isResizable and $showResizeBtns">
        <BUTTON ID="sizeBtn{$tagName/@INPUT}" TYPE="button"
                CLASS="sizeBtn"
                STYLE="vertical-align: top; width: 20px; padding : 0px;"
                ONCLICK="top.scripts.resizeTextArea (document.getElementById ('{$tagName/@INPUT}'),
                         -{$rowIncAlt}, -{$colInc}, {$rows}, {$cols});"
                TITLE="{$TOK_REDUCE}">&lt;</BUTTON>
        <BUTTON ID="sizeBtn{$tagName/@INPUT}" TYPE="button"
                CLASS="sizeBtn"
                STYLE="vertical-align: top; width: 20px; padding : 0px;"
                ONCLICK="top.scripts.resizeTextArea (document.getElementById ('{$tagName/@INPUT}'),
                         {$rowIncAlt}, {$colInc}, {$rows}, {$cols});"
                TITLE="{$TOK_ENLARGE}">&gt;</BUTTON>
        </xsl:if>
        
        <xsl:if test="$isResizable">
            <SCRIPT LANGUAGE="Javascript">
                <xsl:choose>
                    <xsl:when test="$setSize">
                        //top.scripts.resizeTextArea (document.getElementById ('<xsl:value-of select="$tagName/@INPUT"/>'),
                        //                               0, <xsl:value-of select="$cols"/>, 0, <xsl:value-of select="$cols"/>);
                        top.scripts.resizeTextArea (document.getElementById ('{$tagName/@INPUT}'),1,{$cols},1,{$cols});
                    </xsl:when>
                    <xsl:otherwise>
                        top.scripts.resizeTextArea (document.getElementById ('<xsl:value-of select="$tagName/@INPUT"/>'));
                    </xsl:otherwise>
                </xsl:choose>
            </SCRIPT>
        </xsl:if>

    </xsl:template> <!--internDt_textarea-->


    <!--************************************************************************
    * Create a selection box field
    *
    * @param name           the field tag
    * @param optionString   any option string to read the data from
    * @param selected       any selected element
    * @param isSystemSelect system select option
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param mandatory      mandatory option
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_select">
    <xsl:param name="name"/>
    <xsl:param name="optionString"/>
    <xsl:param name="selected"/>
    <xsl:param name="isSystemSelect"/>
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

            <xsl:call-template name="getOption">
                <xsl:with-param name="optionString" select="$optionString"/>
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="value" select="1"/>
                <xsl:with-param name="isSystemSelect" select="$isSystemSelect"/>
            </xsl:call-template>
        </SELECT>
    </xsl:template> <!--internDt_select-->


    <!--************************************************************************
    * Create the options for selection box
    *
    * @param optionString   any option string to read the data from
    * @param selected       any selected element
    * @param value          a counter value
    * @param isSystemSelect system select option
    -->
    <xsl:template name="getOption">
    <xsl:param name="optionString"/>
    <xsl:param name="selected"/>
    <xsl:param name="value"/>
    <xsl:param name="isSystemSelect"/>

        <xsl:variable name="actItem" select="substring-before($optionString, ',')"/>

        <xsl:if test="$actItem != ''"><!--there are more than one items left-->
            <OPTION>
                <xsl:attribute name="VALUE">
                    <xsl:if test="$isSystemSelect = '1'">
                        <xsl:value-of select="format-number ($value, '####')"/>
                    </xsl:if>
                    <xsl:if test="$isSystemSelect = '0'">
                        <xsl:value-of select="$actItem"/>
                    </xsl:if>
                </xsl:attribute>
                <xsl:if test="$actItem = $selected">
                    <xsl:attribute name="SELECTED"/>
                </xsl:if>
                <xsl:value-of select="$actItem"/>
            </OPTION>
            <xsl:call-template name="getOption">
                <xsl:with-param name="optionString" select="substring-after($optionString, ',')"/>
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="value" select="$value + 1"/>
                <xsl:with-param name="isSystemSelect" select="$isSystemSelect"/>
            </xsl:call-template>
        </xsl:if><!--there are more than one items left-->

        <xsl:if test="$actItem = ''"><!--there is only one item left-->
            <OPTION>
                <xsl:attribute name="VALUE">
                    <xsl:if test="$isSystemSelect = true ()">
                        <xsl:value-of select="format-number ($value, '####')"/>
                    </xsl:if>
                    <xsl:if test="$isSystemSelect = false ()">
                        <xsl:value-of select="$optionString"/>
                    </xsl:if>
                </xsl:attribute>
                <xsl:value-of select="$optionString"/>
            </OPTION>
        </xsl:if><!--there is only one item left-->
    </xsl:template> <!--getOption-->


    <!--************************************************************************
    * Create radio buttons
    *
    * @param tagName       field tag
    * @param preselected   any preselected value
    * @param vertical      option to place the radio buttons vertically
    * @param classId       css class
    * @param style         css style
    * @param mandatory     mandatory option
    * @param readonly      readonly option
    * @param onChange      onChange event handler
    * @param onSelect      onSelect event handler
    * @param onFocus       onFocus event handler
    * @param onBlur        onBlur event handler
    * @param tabindex      tabindex
    * @param accesskey     access key
    * @param helptext      a help text to display (as title)
    * @param errortext     an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="dt_radio">
    <xsl:param name="tagName"/>
    <xsl:param name="preselected"/>
    <xsl:param name="vertical" select="true()"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <xsl:param name="readonly" select="$tagName/@READONLY"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>

    <xsl:variable name="mandatoryBool" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
    <xsl:variable name="readonlyBool" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>

    <xsl:for-each select="$tagName/OPTION">

        <xsl:variable name="cont" select="child::text ()"/>
        <xsl:variable name="id">
            <xsl:value-of select="$tagName/@INPUT"/>
            <xsl:text>_</xsl:text>
            <xsl:value-of select="position()"/>
        </xsl:variable>
        <xsl:variable name="label_id">
            <xsl:value-of select="$id"/>
            <xsl:text>_LABEL</xsl:text>
        </xsl:variable>

        <xsl:call-template name="internDt_input">
            <xsl:with-param name="inputType" select="'RADIO'"/>
            <xsl:with-param name="fieldName" select="$tagName/@INPUT"/>
            <xsl:with-param name="fieldId" select="$id"/>
            <xsl:with-param name="value" select="$cont"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="$style"/>
            <xsl:with-param name="mandatory" select="$mandatoryBool"/>
            <xsl:with-param name="readonly" select="$readonlyBool"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="checked" select="($preselected = '' and @SELECTED = 1) or $preselected = @VALUE"/>
        </xsl:call-template>

        <label for="{$id}" id="{$label_id}">
            <xsl:value-of select="$cont"/>
        </label>

        <xsl:if test="$vertical">
            <BR/>
        </xsl:if>
    </xsl:for-each><!-- for-each -->

    </xsl:template><!--dt_radio-->


    <!--************************************************************************
    * Create a hidden field
    *
    * @param name           the field name
    * @param value          a value
    * @param onChange       on change event
    -->
    <xsl:template name="internDt_hidden">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="onChange"/>
        <INPUT TYPE="HIDDEN" ID="{$name}" NAME="{$name}" VALUE="{$value}" ONCHANGE="{$onChange}"/>
    </xsl:template> <!--internDt_hidden-->


    <!--************************************************************************
    * Create a checkbox field
    *
    * @param tagName        the field tag
    * @param name           the fieldname
    * @param value          a value for the checkbox
    * @param mandatory      mandatory option (true|false)
    * @param readonly       readonly option (true|false)
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_checkbox">
    <xsl:param name="tagName"/>
    <xsl:param name="name" select="$tagName/@INPUT"/>
    <xsl:param name="value"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="readonly"/>
    <xsl:param name="onClick"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
        <!-- if no formName is given, set default name to sheetForm -->
        <xsl:variable name="form">
            <xsl:choose>
                <xsl:when test="$formName = ''">
                    <xsl:value-of select="$FORM_DEFAULT"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$formName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="value" select="translate ($value, 'TRUEYSJA', 'trueysja')"/>
            
        <xsl:call-template name="internDt_input">
            <xsl:with-param name="inputType" select="'CHECKBOX'"/>
            <xsl:with-param name="fieldName" select="$name"/>
            <xsl:with-param name="datatype"/>
            <xsl:with-param name="classId" select="$classId"/>
            <xsl:with-param name="style" select="$style"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$BOOL_TRUE"/>
            <xsl:with-param name="checked"
                select="$value = $BOOL_TRUE or $value = $BOOL_JA or $value = $BOOL_YES"/>
            <xsl:with-param name="onClick" select="$onClick"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>

    </xsl:template> <!-- internDt_checkbox -->


    <!--************************************************************************
    * Create a file field
    *
    * @param tagName    the field tag
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
    * @param hidden         hidden option
    * @param mandatory      mandatory option (true|false)
    * @param readonly       readonly option (true|false)
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param helptext       a help text to display (as title)
    * @param errortext      an error text to display    NOT SUPPORTED
    -->
    <xsl:template name="internDt_File">
    <xsl:param name="tagName"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="'60'"/>
    <xsl:param name="maxlength" select="'500'"/>
    <xsl:param name="mandatory" select="false ()"/>
    <xsl:param name="readonly" select="false ()"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="helptext"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>
    <!-- e.g.: allowedFileTypes = '.gif,.jpg,.png' -->
    <xsl:param name="allowedFileTypes"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:if test="$tagName/child::text () != ''">
            <DIV ID="{$fieldName}_FILENAME">
                <xsl:value-of select="$tagName/child::text ()"/>
                <INPUT TYPE="HIDDEN" ID="{$fieldName}_FILE" NAME="{$fieldName}_FILE"
                       VALUE="{$tagName/child::text ()}"/>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                <BUTTON TYPE="BUTTON" ID="{$fieldName}_DELFILE">
                    <xsl:attribute name="ONCLICK">
                        <xsl:text>document.</xsl:text>
                        <xsl:value-of select="$form"/>
                        <xsl:text>.</xsl:text>
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>.value = ''; if (document.</xsl:text>
                        <xsl:value-of select="$form"/>
                        <xsl:text>.</xsl:text>
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_FILE != null) {document.</xsl:text>
                        <xsl:value-of select="$form"/>
                        <xsl:text>.</xsl:text>
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_FILE.value = ''; document.getElementById('</xsl:text>
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_FILENAME').style.display='none';}</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="$TOK_DELETE"/>
                </BUTTON>
            </DIV>
<!-- old implementation
            <INPUT BORDER="0" TYPE="TEXT" ONFOCUS="this.select ()"
                   SIZE="70" MAXLENGTH="255"
                   ID="{$tagName/@INPUT}_FILE" NAME="{$tagName/@INPUT}_FILE"
                   VALUE="{$tagName/child::text ()}">
                <xsl:attribute name="READONLY"/>
                <xsl:attribute name="CLASS">
                    <xsl:text>readonly</xsl:text>
                    <xsl:value-of select="format-number ((position() mod 2) + 1, '####')"/>
                </xsl:attribute>
            </INPUT>
            <BR/>
-->
        </xsl:if>
        
        <xsl:call-template name="internDt_input">
            <xsl:with-param name="inputType" select="'FILE'"/>
            <xsl:with-param name="fieldName" select="$fieldName"/>
            <xsl:with-param name="datatype" select="$tagName/@TYPE"/>
            <xsl:with-param name="size" select="$size"/>
            <xsl:with-param name="maxlength" select="$maxlength"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
            <xsl:with-param name="value" select="$tagName/child::text ()"/>
            <xsl:with-param name="onChange" select="$onChange"/>
            <xsl:with-param name="onSelect" select="$onSelect"/>
            <xsl:with-param name="onFocus" select="$onFocus"/>
            <xsl:with-param name="onBlur" select="$onBlur"/>
            <xsl:with-param name="tabindex" select="$tabindex"/>
            <xsl:with-param name="accesskey" select="$accesskey"/>
            <xsl:with-param name="helptext" select="$helptext"/>
            <xsl:with-param name="errortext" select="$errortext"/>
            <xsl:with-param name="formName" select="$form"/>
            <xsl:with-param name="allowedFileTypes" select="$allowedFileTypes"/>
        </xsl:call-template>
        <INPUT TYPE="HIDDEN" ID="{$fieldName}_FILETYPESALLOWED" NAME="{$fieldName}_FILETYPESALLOWED"
               VALUE="{$allowedFileTypes}"/>
    </xsl:template> <!-- internDt_File -->


    <!--************************************************************************
    * Create a href field
    *
    * @param a_href     the href
    * @param a_text     the link text
    -->
    <xsl:template name="internDt_href">
	    <xsl:param name="a_href"/>
	    <xsl:param name="a_text"/>
	    <xsl:param name="a_title"/>
        <A HREF="{$a_href}" TITLE="{$a_title}"><xsl:value-of select="$a_text"/></A>
    </xsl:template> <!-- dtIntern_href -->


    <!--************************************************************************
    * Create a longtext field
    *
    * @param tagName    the field tag
    -->
    <xsl:template name="dt_longtext">
    <xsl:param name="tagName"/>
        <xsl:for-each select="$tagName/LINE">
            <xsl:value-of select="child::text ()"/>
            <xsl:if test="position () &lt; last ()"><BR/></xsl:if>
        </xsl:for-each>
    </xsl:template> <!-- dt_longtext -->


    <!--************************************************************************
    * Create an image field
    *
    * @param tagName    the field tag
    -->
    <xsl:template name="dt_image">
    <xsl:param name="tagName"/>

        <xsl:variable name="fileName" select="$tagName/child::text ()"/>
        <xsl:variable name="url">
            <xsl:choose>
                <xsl:when test="boolean ($tagName/@URL)">
                    <xsl:value-of select="$tagName/@URL"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$oid"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$fileName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

<!--        <IMG BORDER="0" SRC="{$_WWWP}/images/{$oid}/{$tagName}"/> -->
        <IMG BORDER="0" SRC="{$_WWWP}{$FILE}/{$url}"/>
<!-- KR 20060120 replaced by standard upload directory
        <IMG BORDER="0" SRC="{$_WWWP}/images/{$tagName}"/>
-->
    </xsl:template> <!--dt_image-->

    <!--************************************************************************
    * Create an file field
    *
    * @param tagName    the field tag
    -->
    <xsl:template name="dt_file">
    <xsl:param name="tagName"/>
    <xsl:param name="objOid" select="$oid"/>

        <xsl:variable name="fileName" select="$tagName/child::text ()"/>
        <xsl:variable name="url">
            <xsl:choose>
                <xsl:when test="boolean ($tagName/@URL)">
                    <xsl:value-of select="$tagName/@URL"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$objOid"/>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="$fileName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="boolValue"
                      select="translate ($gp_showFilesInWindow, 'TRUEYSJA', 'trueysja')"/>

        <xsl:choose>
        <xsl:when test="$boolValue = $BOOL_YES">
            <A HREF="javascript:top.loadWindowFile ('{$url}', '{$fileName}');">
                <xsl:value-of select="$fileName"/>
            </A>
        </xsl:when>
        <xsl:otherwise>
            <A HREF="javascript:top.loadFile('{$url}');">
                <xsl:value-of select="$fileName"/>
            </A>
        </xsl:otherwise>
        </xsl:choose>

        <!-- check if we know the size: -->
        <xsl:if test="number ($tagName/@SIZE) >= 0">
            <xsl:text> (</xsl:text><xsl:value-of select="$tagName/@SIZE"/>
            <xsl:text> Bytes)</xsl:text>
        </xsl:if>

        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <A HREF="javascript:top.loadFileInNewWindow('{$url}');"
            ALT="{$TOK_OPENINNEWWINDOW}">
            <IMG SRC="{$layoutPath}images/global/newwindow.gif" BORDER="0"
                CLASS="filebutton" ALT="{$TOK_OPENINNEWWINDOW}" ALIGN="ABSMIDDLE"/>
        </A>

        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <A HREF="javascript:top.saveFile('{$url}');" ALT="{$TOK_DOWNLOAD}">
            <IMG SRC="{$layoutPath}images/objectIcons/Download.gif" BORDER="0"
                CLASS="filebutton" ALT="{$TOK_DOWNLOAD}" ALIGN="ABSMIDDLE"/>
        </A>

<!--
        <xsl:choose>
        <xsl:when test="$boolValue = $BOOL_TRUE or
                          $boolValue = $BOOL_JA or
                          $boolValue = $BOOL_YES">
            <A HREF="javascript:top.loadWindowFile(top.system.appDir + 'upload/files/{$url}', '{$fileName}');">
                <xsl:value-of select="$fileName"/>
            </A>
        </xsl:when>
        <xsl:otherwise>

            <A HREF="javascript:top.loadFile('{$_WWWP}/files/{$url}');">
                <xsl:value-of select="$fileName"/>
            </A>
        </xsl:otherwise>
        </xsl:choose>

        <A HREF="{$_WWWP}/files/{$url}">
            <IMG SRC="{$layoutPath}images/objectIcons/Download.gif" BORDER="0"/>
        </A>
-->
    </xsl:template> <!-- dt_file -->

    <!--************************************************************************
    * Create an query field
    *
    * @param tagName    the field tag
    -->
    <xsl:template name="dt_query">
    <xsl:param name="tagName"/>

        <TD WIDTH="100%" COLSPAN="2">
        <TABLE WIDTH="100%" CELLSPACING="1" CELLPADDING="0" BORDER="1">
        <TBODY>
        <TR VALIGN="TOP">
            <TD CLASS="spreadName"><xsl:value-of select="$tagName/@FIELD"/></TD>
        </TR>
        <TR>
        <TD>
            <!-- INNER TABLE -->
            <TABLE WIDTH="100%" CELLSPACING="0" FRAME="BOX"
                   CELLPADDING="{format-number (count ($tagName/RESULTROW/RESULTELEMENT), '####')}">
            <TBODY>
                <xsl:for-each select="$tagName/RESULTROW">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                        <!-- HEADER -->
                        <TR VALIGN="TOP">
                            <xsl:for-each select="RESULTELEMENT">
                            <TD CLASS="spreadHeader"><xsl:value-of select="@NAME"/></TD>
                            </xsl:for-each>
                        </TR>
                        <!-- FIRST ROW-->
                        <TR VALIGN="TOP">
                            <xsl:for-each select="RESULTELEMENT">
                            <TD CLASS="spreadRow"><xsl:value-of select="."/></TD>
                            </xsl:for-each>
                        </TR>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- FOLLOWING ROWS -->
                        <TR VALIGN="TOP">
                            <xsl:for-each select="RESULTELEMENT">
                            <TD CLASS="spreadRow"><xsl:value-of select="."/></TD>
                            </xsl:for-each>
                        </TR>
                    </xsl:otherwise>
                   </xsl:choose>
                </xsl:for-each>
            </TBODY></TABLE>
        </TD></TR>
        </TBODY></TABLE>
        </TD>
    </xsl:template><!-- dt_query -->

    <!--************************************************************************
    * Create a selection box field
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
    <xsl:template name="dt_selectionbox">
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
	    <xsl:param name="viewType" select ="$tagName/@VIEWTYPE"/>
	    <xsl:param name="cols" select ="$tagName/@COLS"/>
        <xsl:param name="type" select ="$tagName/@TYPE"/>
    	<xsl:param name="multiSelection" select ="translate ($tagName/@MULTISELECTION, 'YES', 'yes')"/>
     
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

		<xsl:choose>
			<!-- viewtype="CHECKLIST" -->
			<xsl:when test="$viewType='CHECKLIST'">
		
				<!-- Set the type depending on the multi selection attribute -->
				<xsl:variable name="renderType">
					<xsl:choose>
						<xsl:when test="$multiSelection=$BOOL_YES">
							<xsl:value-of select="'CHECKBOX'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'RADIO'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:if test="$multiSelection=$BOOL_YES">
					 <!-- Hidden field to force data to be submitted even if no checkbox is selected -->
					 <INPUT type="HIDDEN" id="{$name}_hidden" name="{$name}" value=""/>
				</xsl:if>
				 		
		        <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="5" FRAME="VOID" RULES="NONE">
	                <xsl:variable name="width" select="100 div $colsintern"/>
		    		<COLGROUP span="{$colsintern}" width="{$width}%"/>
		            <THEAD/>
		            <TBODY>
				         <xsl:for-each select="$tagName/OPTION[(position()-1) mod $colsintern = 0]">
				         		<TR>
						         	<xsl:variable name="thisrow" select="position()-1"/>
						         	<xsl:variable name="nextrow" select="position()"/>
	
						         	<xsl:for-each select="$tagName/OPTION[position() &gt; $thisrow * $colsintern and position() &lt;= $nextrow * $colsintern]">
						         		<TD style="border-left-width: 0px;">
										<xsl:if test="position() = last() and position() &lt; $colsintern and $thisrow!=0">
											<xsl:variable name="span" select="($colsintern - position()) + 1"/>
									        <xsl:attribute name="colspan"><xsl:value-of select="$span"/></xsl:attribute>
									    </xsl:if>					                
	
							            <xsl:variable name="cont" select="child::text ()"/>
				                        <xsl:variable name="oid">
			                          		<xsl:choose>
				                    	        <xsl:when test="$type = 'VALUEDOMAIN'">
				                    	            <xsl:value-of select="@VALUE"/>
				                    	        </xsl:when>
			                    			    <xsl:otherwise>
			                    			       <xsl:value-of select="$cont"/>
			                    	        	</xsl:otherwise>
			                    		  	</xsl:choose>
			                        	</xsl:variable>
			                        	<xsl:variable name="description" select="@DESCRIPTION"/>
	                        
						                <INPUT ID="{$name}{$thisrow}_{position()}" NAME="{$name}" TABINDEX="{$tabindex}" ACCESSKEY="{$accesskey}"
						                	STYLE="{$style}" TYPE="{$renderType}" VALUE="{$oid}">
						                		<xsl:if test="($preselected = '' and @SELECTED = 1) or $preselected = $cont">
							                        <xsl:attribute name="checked"/>
							                    </xsl:if>
										</INPUT>
										<LABEL for="{$name}{$thisrow}_{position()}" TITLE="{$description}">
							                <xsl:choose>
							                	<!-- Replace the empty label for the empty options with None (first row, first colum, multiselection=false, mandatory=no, value='') -->
								                <xsl:when test="$thisrow=0 and position() = 1 and $multiSelection != $BOOL_YES and translate ($mandatory, 'NO', 'no')=$BOOL_NO and $cont=' '">
													<xsl:text>None</xsl:text>
			 									</xsl:when>
			 									<xsl:otherwise>
			 										<xsl:value-of select="$cont"/>
			 									</xsl:otherwise>
		 									</xsl:choose>
				 						</LABEL>
					                </TD>
					                <xsl:if test="position() = last() and position() &lt; $colsintern and $thisrow=0">
							        	<TD style="border-left-width: 0px;"/>
							        </xsl:if>
								</xsl:for-each>
							</TR>
			            </xsl:for-each>
		            </TBODY>
		        </TABLE>
			</xsl:when>
			<!-- viewtype="SELECTIONBOX" (default) -->
			<xsl:otherwise>
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
		            
		            <xsl:choose>
			            <xsl:when test="$multiSelection = $BOOL_YES">
			                <xsl:attribute name="MULTIPLE">
			                    <xsl:text>MULTIPLE</xsl:text>
			                </xsl:attribute>
			            	<xsl:attribute name="SIZE">
			            		<xsl:text>3</xsl:text>
			                </xsl:attribute>
			            </xsl:when>
			            <xsl:otherwise>
			            	<xsl:attribute name="SIZE">
			            		<xsl:value-of select="$size"/>
			                </xsl:attribute>
			            </xsl:otherwise>
		            </xsl:choose>
		
		            <xsl:for-each select="$tagName/OPTION">
                        <xsl:variable name="cont" select="child::text ()"/>
                        <xsl:variable name="oid">
                          <xsl:choose>
                    	        <xsl:when test="$type = 'VALUEDOMAIN'">
                    	            <xsl:value-of select="@VALUE"/>
                    	        </xsl:when>
                    			    <xsl:otherwise>
                    			       <xsl:value-of select="$cont"/>
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
			</xsl:otherwise>
         </xsl:choose>
    </xsl:template><!-- dt_selectionbox -->


    <!--************************************************************************
    * Create a selection box field
    *
    * @param curTag        the field tag
    * @param name           field name
    * @param onChange       onChange ecent
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
    -->
    <xsl:template name="dt_selection">
    <xsl:param name="curTag"/>
    <xsl:param name="name" select="$curTag/@INPUT"/>
    <xsl:param name="onChange"/>
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

            <xsl:for-each select="$curTag/OPTION">
                <OPTION VALUE="{@VALUE}">
                    <xsl:if test="($preselected = '' and @SELECTED = 1) or $preselected = @VALUE">
                        <xsl:attribute name="SELECTED"/>
                    </xsl:if>
                    <xsl:value-of select="child::text ()"/>
                </OPTION>
            </xsl:for-each>
        </SELECT>
    </xsl:template> <!-- dt_selection -->

    <!--************************************************************************
    * Create an objectref edit field
    *
    * @param tagName        the field tag
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
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
    * @param showSearchParams defines if the extended search params should be display initially
    -->
    <xsl:template name="m2Dt_objectref_edit">
    <xsl:param name="tagName"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="25"/>
    <xsl:param name="maxlength" select="255"/>
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
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="showSearchParams" select="$BOOL_NO"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="input" select="$tagName/@INPUT"/>
    <xsl:variable name="value" select="substring-after ($tagName, ',')"/>
    <xsl:variable name="oid" select="substring-before ($tagName, ',')"/>

	<TABLE BORDERCOLOR="#FFFFFF" BORDER="0"
              CELLSPACING="0" CELLPADDING="0" FRAME="NONE" RULES="GROUPS" WIDTH="100%" class="objectRef">
		<TR>
			<TD COLSPAN="4">
				<TABLE class="objectRefMain">
					<TR>
						<TD>
							<IMG SRC="{$layoutPath}images/objectIcons/Referenz.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
						</TD>
						<TD>
						<!--old style Attribute: STYLE="background-color: transparent; border: 0px;" -->
				        <INPUT TYPE="TEXT" ID="{$input}" NAME="{$input}"
				               SIZE="60" MAXLENGTH="256" ONFOCUS="this.select ()"
				               VALUE="{$value}" READONLY="" STYLE="border: 0px;">
	
							<xsl:if test="$tagName/@MANDATORY='YES'">
								<xsl:attribute name="CLASS">
				                	<xsl:text>mandatory</xsl:text>
				            	</xsl:attribute>
				            </xsl:if>
	
							<!--                    
				            <xsl:attribute name="CLASS">
				               <xsl:choose>
				                    <xsl:when test="$tagName/@MANDATORY='YES'">
				                        <xsl:text>mandatory</xsl:text>
				                    </xsl:when>
				                    <xsl:otherwise>
				                        <xsl:text>readonly</xsl:text>
				                        <xsl:value-of select="format-number ((position() mod 2) + 1, '####')"/>
				                    </xsl:otherwise>
				                </xsl:choose>
				            </xsl:attribute>
							-->
				
	        			</INPUT>
	       				</TD>
	        			<TD>
							<TABLE>
							<xsl:variable name="assign">
							<xsl:text>javascript:top.scripts.toggleStyle (document, '</xsl:text>
							<xsl:value-of select="$input"/>
							<xsl:text>_div_search', 'objectRefSearch_open', 'objectRefSearch_closed', '</xsl:text>
							<xsl:value-of select="$input"/>
							<xsl:text>_search_img', '</xsl:text>
							<xsl:value-of select="$layoutPath"/>
							<xsl:text>images/global/elemOpen.gif', '</xsl:text>
							<xsl:value-of select="$layoutPath"/>
							<xsl:text>images/global/elemClosed.gif'); if(document.getElementById('</xsl:text>
							<xsl:value-of select="$input"/>
							<xsl:text>_div_search').className == 'objectRefSearch_open') {document.getElementById('</xsl:text>
							<xsl:value-of select="$input"/>
							<xsl:text>_NAME').focus();}</xsl:text>
							</xsl:variable>
								<TR>
									<TD>
										<NOBR>
											[<A TITLE="{$TOK_OBJECTREF_ASSIGN_TITLE}" HREF="{$assign}">
							                <IMG BORDER="0" ID="{$input}_search_img" SRC="{$layoutPath}images/global/elemClosed.gif" ALIGN="absmiddle"/></A>
							                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
							                <A TITLE="{$TOK_OBJECTREF_ASSIGN_TITLE}" HREF="{$assign}"><xsl:value-of select="$TOK_OBJECTREF_ASSIGN"/></A>]
							                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					                  	</NOBR>
					                </TD>
					                <TD>
								        <xsl:call-template name="internDt_button">
								            <xsl:with-param name="name">
								                <xsl:value-of select="$input"/>
								                <xsl:text>_BTD</xsl:text>
								            </xsl:with-param>
								            <xsl:with-param name="value" select="$TOK_DELETE"/>
								            <xsl:with-param name="onClick">
								                <xsl:text>document.</xsl:text>
								                <xsl:value-of select="$form"/>
								                <xsl:text>.</xsl:text>
								                <xsl:value-of select="$input"/>
								                <xsl:text>_OID.value = '';</xsl:text>
								                <xsl:text>document.</xsl:text>
								                <xsl:value-of select="$form"/>
								                <xsl:text>.</xsl:text>
								                <xsl:value-of select="$input"/>
								                <xsl:text>.value = '';</xsl:text>
								            </xsl:with-param>
								        </xsl:call-template>
			                  		</TD>
		                  		</TR>
	                  		</TABLE>
	                  	</TD>
					</TR>
				</TABLE>
			</TD>             
		</TR>
		<TR>
			<TD COLSPAN="4">
		        <xsl:variable name="div_search_class_name">
					<xsl:choose>
						<xsl:when test="translate ($showSearchParams, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
							<xsl:value-of select="'objectRefSearch_open'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'objectRefSearch_closed'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
		         
				<DIV id="{$input}_div_search" CLASS="{$div_search_class_name}"> 
		        <FIELDSET>
		        <LEGEND><xsl:value-of select="$TOK_SEARCH"/></LEGEND>
                <TABLE class="objectRefSearch" ALIGN="left">
                <TR>
				<TD>
		        <xsl:call-template name="internDt_select">
		            <xsl:with-param name="name">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_M</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="optionString">
		                <xsl:value-of select="$TOK_CONTAINS"/>
		                <xsl:text>,</xsl:text>
		                <xsl:value-of select="$TOK_EXACTLY"/>
		                <xsl:text>,</xsl:text>
		                <xsl:value-of select="$TOK_SIMILAR"/>
		            </xsl:with-param>
		            <xsl:with-param name="selected" select="''"/>
		            <xsl:with-param name="isSystemSelect" select="1"/>
					<xsl:with-param name="style" select="'vertical-align:middle'"/>
		        </xsl:call-template>
		
		        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		
			    <xsl:variable name="onKeyPress">
    				<xsl:text>if(event.keyCode == 13) {var elem = document.getElementById('</xsl:text>
   					<xsl:value-of select="$input"/>
   					<xsl:text>_BT'); elem.focus(); elem.click();}</xsl:text>
	    		</xsl:variable>
		
		        <xsl:call-template name="internDt_input">
		            <xsl:with-param name="fieldName">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_NAME</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="restriction" select="'iTx'"/>
		            <xsl:with-param name="classId" select="$classId"/>
		            <xsl:with-param name="style" select="$style"/>
		            <xsl:with-param name="size" select="$size"/>
		            <xsl:with-param name="maxlength" select="$maxlength"/>
		            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
		            <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
		            <xsl:with-param name="value" select="''"/>
		            <xsl:with-param name="onChange" select="$onChange"/>
		            <xsl:with-param name="onSelect" select="$onSelect"/>
		            <xsl:with-param name="onFocus" select="$onFocus"/>
		            <xsl:with-param name="onBlur" select="$onBlur"/>
		            <xsl:with-param name="onKeyPress" select="$onKeyPress"/>
		            <xsl:with-param name="tabindex" select="$tabindex"/>
		            <xsl:with-param name="accesskey" select="$accesskey"/>
		            <xsl:with-param name="helptext" select="$helptext"/>
		            <xsl:with-param name="errortext" select="$errortext"/>
		            <xsl:with-param name="helptext" select="$TOK_OBJECTREF_SEARCH_HELP"/>
					<xsl:with-param name="style" select="'vertical-align:middle'"/>
		        </xsl:call-template>
		
		<!--
		        <xsl:call-template name="internDt_text_dateTime">
		            <xsl:with-param name="name">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_NAME</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="value" select="''"/>
		            <xsl:with-param name="size" select="25"/>
		            <xsl:with-param name="maxlength" select="25"/>
		            <xsl:with-param name="restriction" select="'iTx'"/>
		        </xsl:call-template>
		-->
		
		        <xsl:call-template name="internDt_hidden">
		            <xsl:with-param name="name">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_OID</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="value" select="$oid"/>
		        </xsl:call-template>
		
		        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		
		        <xsl:call-template name="internDt_button">
		            <xsl:with-param name="name">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_BT</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="value" select="$TOK_SEARCH"/>
		            <xsl:with-param name="style" select="'vertical-align:middle'"/>
		            <xsl:with-param name="onClick">
		                <xsl:text>top.callUrl (top.scripts.getEscapedObjectRef ('</xsl:text>
		                <xsl:value-of select="$tagName/@URL"/>
		                <xsl:text>', document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$input"/>
		                <xsl:text>_NAME.value, document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$input"/>
		                <xsl:text>_M.options[document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$input"/>
		                <xsl:text>_M.selectedIndex].value, '</xsl:text>
		                <xsl:value-of select="$input"/>
                        <xsl:text>'), null, null, parent.sheet2.name);</xsl:text>
		                <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
		                <xsl:text>document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$input"/>
		                <xsl:text>_BTC.style.visibility='visible';</xsl:text>
		            </xsl:with-param>
		        </xsl:call-template>
		
		        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		
		        <xsl:call-template name="internDt_button">
		            <xsl:with-param name="name">
		                <xsl:value-of select="$input"/>
		                <xsl:text>_BTC</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="value" select="$TOK_CLOSE"/>
		            <xsl:with-param name="onClick">
		                <xsl:text>parent.document.body.rows='*,0';</xsl:text>
		                <xsl:text>this.style.visibility='hidden';</xsl:text>
		            </xsl:with-param>
		            <xsl:with-param name="style" select="'width: 30px; visibility: hidden; vertical-align:middle'"/>
		            <xsl:with-param name="title" select="$TOK_CLOSE_SEARCHRESULT"/>
		        </xsl:call-template>
		        </TD>
		        </TR>
		        <TR>
		        	<TD><xsl:value-of select="$TOK_OBJECTREF_SEARCH_HELP"/></TD>
		        </TR>
		        </TABLE>
		        </FIELDSET>
		        </DIV>
	        </TD>
        </TR>
	</TABLE>
    </xsl:template> <!-- m2Dt_objectref_edit -->

    
    <!--************************************************************************
    * Create an fieldref view field
    *
    * @param curTag         the field tag
    * @param value          value
    * @param sysFieldAText  text for the sys field
    * @param sysFieldName   name of the sysfield
    -->
    <xsl:template name="m2Dt_fieldref_view">
    <xsl:param name="curTag"/>
    <xsl:param name="value"/>
    <xsl:param name="sysFieldAText" select="''"/>
    <xsl:param name="sysFieldName" select="'Name'"/>
    
        <TABLE CLASS="fieldRefView" WIDTH="100%" CELLSPACING="0" CELLPADDING="0">
<!--
            <xsl:attribute name="onClick">
                <xsl:text>top.showObject('</xsl:text>
                <xsl:value-of select="$value"/>
                <xsl:text>');</xsl:text>
            </xsl:attribute>
-->
            <COLGROUP>
                <COL CLASS="name" STYLE="width:1%"/>
                <COL CLASS="value" STYLE="auto"/>
            </COLGROUP>
            <TBODY>
                <xsl:for-each select="$curTag/FIELDS/*">
                    <TR>
                        <xsl:variable name="fieldDescription" select="@DESCRIPTION"/>
                        <TD CLASS="name" TITLE="{$fieldDescription}">
                            <NOBR>
	                            <xsl:value-of select="@TOKEN"/>
	                            <xsl:text>:</xsl:text>
	                        </NOBR>
                        </TD>
                        <TD CLASS="value">
                            <xsl:variable name="a_text">
                                <xsl:choose>
                                    <xsl:when test="$sysFieldAText = ''">
                                        <xsl:value-of select="child::text()"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:choose>
                                            <xsl:when test="@NAME != $sysFieldName">
                                                <xsl:value-of select="child::text()"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="$sysFieldAText"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
<!--
                            <xsl:if test="child::text() != ''">
                                <IMG SRC="{$layoutPath}images/objectIcons/Referenz.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            </xsl:if>
-->
                            <xsl:call-template name="internDt_href">
                                <xsl:with-param name="a_href">javascript:top.showObject('<xsl:value-of select="$value"/>');</xsl:with-param>
                                <xsl:with-param name="a_text" select="$a_text"/>
                            </xsl:call-template>
                        </TD>
                    </TR>
                </xsl:for-each>
            </TBODY>
        </TABLE>
    </xsl:template> <!-- m2Dt_fieldref_view -->

    <!--************************************************************************
    * Create an value domain view field
    *
    * @param curTag     the field tag
    * @param value      value
    * @param showLink   flag to show a link to the value domain object
    *                   default: FALSE. Only show the value
    -->
    <xsl:template name="m2Dt_valuedomain_view">
	    <xsl:param name="curTag"/>
	    <xsl:param name="value"/>
        <xsl:param name="showLink" select="false()"/>

		<xsl:variable name="noOfFields" select="count ($curTag/FIELDS/*)"/>

		<!-- Show the values for all fields with its labels more than one field is defined. -->
		<xsl:choose>
			<xsl:when test="$noOfFields > 1">
		        <TABLE CLASS="info" WIDTH="100%" CELLSPACING="0" CELLPADDING="5">
		            <COLGROUP>
		                <COL CLASS="name"/>
		                <COL CLASS="value"/>
		            </COLGROUP>
		            <TBODY>
		                <xsl:for-each select="$curTag/FIELDS/*">
		                    <TR>
                                <xsl:variable name="fieldDescription" select="@DESCRIPTION"/>
		                        <TD WIDTH="1%" TITLE="{$fieldDescription}">
		   	                        <NOBR>
			                            <xsl:value-of select="@TOKEN"/>
			                            <xsl:text>:</xsl:text>
			                        </NOBR>
		                        </TD>
		                        <TD>
		                           	<xsl:for-each select="FIELDVALUE">
                                        <xsl:choose>
                                        <xsl:when test="$showLink">
                                            <xsl:variable name="description" select="@DESCRIPTION"/>
                                            <xsl:call-template name="internDt_href">
                                                <xsl:with-param name="a_href">javascript:top.showObject('<xsl:value-of select="@ID"/>');</xsl:with-param>
                                                <xsl:with-param name="a_text" select="child::text()"/>
                                                <xsl:with-param name="a_title" select="$description"/>
                                            </xsl:call-template>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="child::text()"/>
                                        </xsl:otherwise>                                         
                                        </xsl:choose>
		
		               					<!-- Add a separator -->
		                                <xsl:if test ="position()!=last()">
		                    							<xsl:text>, </xsl:text>
					                    </xsl:if>
		                            </xsl:for-each>
		                        </TD>
		                    </TR>
		                </xsl:for-each>
		            </TBODY>
		        </TABLE>
			</xsl:when> <!-- when: Show the values for all fields with its labels more than one field is defined. -->
			<xsl:otherwise>
	            <xsl:for-each select="$curTag/FIELDS/*/FIELDVALUE">
                    <xsl:choose>
                    <xsl:when test="$showLink">
                        <xsl:variable name="description" select="@DESCRIPTION"/>
                        <xsl:call-template name="internDt_href">
                            <xsl:with-param name="a_href">javascript:top.showObject('<xsl:value-of select="@ID"/>');</xsl:with-param>
                            <xsl:with-param name="a_text" select="child::text()"/>
                            <xsl:with-param name="a_title" select="$description"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="child::text()"/>
                    </xsl:otherwise>                                         
                    </xsl:choose>

                    <!-- Add a separator -->
                    <xsl:if test ="position()!=last()">
                         <xsl:text>, </xsl:text>
                    </xsl:if>
	            </xsl:for-each>
			</xsl:otherwise> <!-- otherwise: only show the values for the defined field -->
		</xsl:choose>
    </xsl:template> <!-- m2Dt_fieldref_view -->

    <!--************************************************************************
    * Create an fieldref edit field
    *
    * @param curTag         the field tag
    * @param destination    allows to define an alternative target
    *                       input field where the selected value should be set
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
    * @param mandatory      mandatory option (true|false)
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param errortext      an error text to display    NOT SUPPORTED
	* @param showSearchParams defines if the extended search params should be display initially
    -->
    <xsl:template name="m2Dt_fieldref_edit">
    <xsl:param name="curTag"/>
    <xsl:param name="fieldrefMode"/>
    <xsl:param name="preselectedValue"/>
    <xsl:param name="preselectedOID"/>
    <xsl:param name="target"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="readonly"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="showSearchParams"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:variable name="input" select="$curTag/@INPUT"/>
        <!-- set targetLocal: if a value is provided use it otherwise use the input -->
        <xsl:variable name="targetLocal">
            <xsl:choose>
	            <xsl:when test="$target != ''">
	                <xsl:value-of select="$target"/>
	            </xsl:when>
	            <xsl:otherwise>
	                <xsl:value-of select="$input"/>
	            </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="queryName" select="$curTag/@QUERYNAME"/>
        <xsl:variable name="sysFieldCount" select="count ($curTag/FIELDS/SYSFIELD)"/>
        <xsl:variable name="fieldCount" select="count ($curTag/FIELDS/FIELD)"/>
        <xsl:variable name="emptyArrayElems">
            <xsl:for-each select="$curTag/FIELDS/SYSFIELD">
                <xsl:text>, ''</xsl:text>
            </xsl:for-each>
            <xsl:for-each select="$curTag/FIELDS/FIELD">
                <xsl:text>, ''</xsl:text>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="emptyJSArray">
            <xsl:text>new Array(</xsl:text>
            <xsl:value-of select="substring-after ($emptyArrayElems, ', ')"/>
            <xsl:text>)</xsl:text>
        </xsl:variable>

        <TABLE CLASS="fieldRef" BORDERCOLOR="#FFFFFF" BORDER="0"
               CELLSPACING="0" CELLPADDING="0" FRAME="NONE" RULES="GROUPS" WIDTH="100%">
            <TBODY>
                <xsl:call-template name="createFieldrefStructure">
                    <xsl:with-param name="curTag" select="$curTag"/>
                    <xsl:with-param name="fieldrefMode" select="$fieldrefMode"/>
                    <xsl:with-param name="preselectedValue" select="$preselectedValue"/>
                    <xsl:with-param name="preselectedOID" select="$preselectedOID"/>
                    <xsl:with-param name="inp" select="$input"/>
                    <xsl:with-param name="target" select="$targetLocal"/>
                    <xsl:with-param name="queryName" select="$queryName"/>
                    <xsl:with-param name="emptyJSArray" select="$emptyJSArray"/>
                    <xsl:with-param name="size" select="$size"/>
                    <xsl:with-param name="maxlength" select="$maxlength"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="readonly" select="$readonly"/>
                    <xsl:with-param name="onChange" select="$onChange"/>
                    <xsl:with-param name="onSelect" select="$onSelect"/>
                    <xsl:with-param name="onFocus" select="$onFocus"/>
                    <xsl:with-param name="onBlur" select="$onBlur"/>
                    <xsl:with-param name="tabindex" select="$tabindex"/>
                    <xsl:with-param name="accesskey" select="$accesskey"/>
                    <xsl:with-param name="formName" select="$form"/>
                    <xsl:with-param name="showSearchParams" select="$showSearchParams"/>
                 </xsl:call-template>
            </TBODY>
        </TABLE>
    </xsl:template> <!-- m2Dt_fieldref_edit -->


    <!--************************************************************************
    * Create the fieldref structure
    *
    * @param curTag         the field tag
    * @param inp            the field name
    * @param target         allows to define an alternative target
    *                       input field where the selected value should be set
    * @param queryName      the name of the query
    * @param emptyJSArray   a javascript code to empty the array
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
    * @param mandatory      mandatory option (true|false)
    * @param readonly       readonly option
    * @param onChange       onChange event handler
    * @param onSelect       onSelect event handler
    * @param onFocus        onFocus event handler
    * @param onBlur         onBlur event handler
    * @param tabindex       tabindex
    * @param accesskey      access key
    * @param errortext      an error text to display    NOT SUPPORTED
    * @param showSearchParams defines if the extended search params should be display initially
    -->
    <xsl:template name="createFieldrefStructure">
    <xsl:param name="curTag"/>
    <xsl:param name="preselectedValue"/>
    <xsl:param name="preselectedOID"/>
    <xsl:param name="fieldrefMode"/>
    <xsl:param name="inp"/>
    <xsl:param name="target" select="$inp"/>
    <xsl:param name="queryName"/>
    <xsl:param name="emptyJSArray"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="23"/>
    <xsl:param name="maxlength" select="25"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="readonly"/>
    <xsl:param name="onChange"/>
    <xsl:param name="onSelect"/>
    <xsl:param name="onFocus"/>
    <xsl:param name="onBlur"/>
    <xsl:param name="tabindex"/>
    <xsl:param name="accesskey"/>
    <xsl:param name="errortext"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="showSearchParams" select="$BOOL_NO"/>
    
	    <!-- if no formName is given, set default name to sheetForm -->
	    <xsl:variable name="form">
	        <xsl:choose>
	            <xsl:when test="$formName = ''">
	                <xsl:value-of select="$FORM_DEFAULT"/>
	            </xsl:when>
	            <xsl:otherwise>
	                <xsl:value-of select="$formName"/>
	            </xsl:otherwise>
	        </xsl:choose>
	    </xsl:variable>
    
        <xsl:variable name="fieldSelName">
            <xsl:value-of select="$inp"/>
                <xsl:text>_S</xsl:text>
        </xsl:variable>
        <xsl:variable name="typeSelName">
            <xsl:value-of select="$inp"/>
                <xsl:text>_M</xsl:text>
        </xsl:variable>
        <xsl:variable name="curRow" select="position ()"/>
        <xsl:variable name="mode">                                                          
            <xsl:choose>                                                                       
                <xsl:when test="$fieldrefMode=''">                                    
                    <xsl:value-of select="$FIELDREF_MODE_DEFAULT"/>                                         
                </xsl:when>                                                                   
                <xsl:otherwise>                                                               
                    <xsl:value-of select="$fieldrefMode"/>   
                </xsl:otherwise>                                                              
            </xsl:choose>                                                                     
        </xsl:variable>   
        
		<TR>
			<TD COLSPAN="3">
				<TABLE CLASS="fieldRefMain">
				<TBODY>
		        	<xsl:for-each select="$curTag/FIELDS/*">
	            	<xsl:variable name="fieldName" select="concat ($inp, '_', format-number (position () - 1, '####'))"/>
	            	<TR>
                	<TD WIDTH="1%" CLASS="name">
	                    <LABEL FOR="{$fieldName}">
		                    <NOBR>
			                    <xsl:value-of select="@TOKEN"/>
			                    <xsl:text>:</xsl:text>
		                   	</NOBR>
	                    </LABEL>
	                </TD>
	                <xsl:if test="$mode != $FIELDREF_MODE_SEARCH">
						<TD WIDTH="20%">
								<xsl:variable name="selectedValue">                                                          
									<xsl:choose>                                                                       
										<xsl:when test="$preselectedValue=''">                                       
											 <xsl:value-of select="child::text ()"/>   
										</xsl:when>                                                                   
										<xsl:otherwise>                                                               
											<xsl:value-of select="$preselectedValue"/>   
										</xsl:otherwise>                                                              
									</xsl:choose>                                                                     
								</xsl:variable>
	<!--
						<xsl:if test="position() = 1">
							<IMG SRC="{$layoutPath}images/objectIcons/Referenz.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
							<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						</xsl:if>
	-->
								<!-- old: STYLE="background-color: transparent; border: 0px;" -->
								<INPUT TYPE="TEXT" ID="{$fieldName}" NAME="{$fieldName}"
									   STYLE="border: 0px;"
									   SIZE="60" MAXLENGTH="256" ONFOCUS="this.select ()"
									   VALUE="{$selectedValue}" READONLY="">
								
									<xsl:if test="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
										<xsl:attribute name="CLASS">
											<xsl:text>mandatory</xsl:text>
										</xsl:attribute>                                    
									</xsl:if>
		
								   <!-- 
								   <xsl:choose>
		
										<xsl:when test="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
											
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>readonly</xsl:text>
											<xsl:value-of select="format-number ((position() mod 2) + 1, '####')"/>
										</xsl:otherwise>
									</xsl:choose> -->
		
								</INPUT>
						</TD>
					</xsl:if>
	                <TD WIDTH="79%">
						<xsl:if test="position() = 1">
						<TABLE class="fieldRefAssign">
							<TR>
								<xsl:if test="$mode != $FIELDREF_MODE_VIEW">
									<xsl:variable name="assign">
										<xsl:text>javascript:top.scripts.toggleStyle (document, '</xsl:text>
										<xsl:value-of select="$inp"/>
										<xsl:text>_div_search', 'fieldRefSearch_open', 'fieldRefSearch_closed', '</xsl:text>
										<xsl:value-of select="$inp"/>
										<xsl:text>_search_img', '</xsl:text>
										<xsl:value-of select="$layoutPath"/>
										<xsl:text>images/global/elemOpen.gif', '</xsl:text>
										<xsl:value-of select="$layoutPath"/>
										<xsl:text>images/global/elemClosed.gif'); if(document.getElementById('</xsl:text>
										<xsl:value-of select="$inp"/>
										<xsl:text>_div_search').className == 'fieldRefSearch_open') document.getElementById('</xsl:text>
										<xsl:value-of select="$curTag/@INPUT"/>
										<xsl:text>').focus();</xsl:text>
									</xsl:variable>
									<TD>
										<NOBR>
										 [<A TITLE="{$TOK_FIELDREF_ASSIGN_TITLE}" HREF="{$assign}">
											<IMG BORDER="0" ID="{$inp}_search_img" SRC="{$layoutPath}images/global/elemClosed.gif" ALIGN="absmiddle"/></A>
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
											<A TITLE="{$TOK_FIELDREF_ASSIGN_TITLE}" HREF="{$assign}"><xsl:value-of select="$TOK_FIELDREF_ASSIGN"/></A>]
											<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
										</NOBR>
									</TD>
								</xsl:if>
								<xsl:if test="$mode != $FIELDREF_MODE_SEARCH">
			                    <TD>
				                    <xsl:call-template name="internDt_button">
				                        <xsl:with-param name="name">
				                            <xsl:value-of select="$inp"/>
				                            <xsl:text>_BTD</xsl:text>
				                        </xsl:with-param>
				                        <xsl:with-param name="value" select="$TOK_DELETE"/>
				                        <xsl:with-param name="onClick">
				                            <xsl:text>top.setFieldRef ('</xsl:text>
				                            <xsl:value-of select="$inp"/>
				                            <xsl:text>', '', </xsl:text>
				                            <xsl:value-of select="$emptyJSArray"/>
				                            <xsl:text>, true, true);</xsl:text>
				                        </xsl:with-param>
				                    </xsl:call-template>
			                    </TD>
								</xsl:if>
	                    	</TR>
						</TABLE>
						</xsl:if>
					</TD>
            	</TR>
        		</xsl:for-each>
        		</TBODY>
				</TABLE>
        	</TD>
        </TR>
        
        <xsl:variable name="div_search_class_name">
			<xsl:choose>
				<xsl:when test="translate ($showSearchParams, 'TRUEYSJA', 'trueysja')=$BOOL_YES">
					<xsl:value-of select="'fieldRefSearch_open'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'fieldRefSearch_closed'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Java script code for changing the search help text --> 
	    <xsl:variable name="sbOnChange">
    		<xsl:text>var target = top.sheet.sheet1; if(target == null) target = top.sheet; var elem = target.document.getElementById('</xsl:text>
   			<xsl:value-of select="$inp"/>
   			<xsl:text>_fieldRefSearchParamsHelpText'); var text = '</xsl:text>
   			<xsl:value-of select="$TOK_FIELDREF_SEARCH_HELP_1"/>
   			<xsl:text>' + this.options[this.selectedIndex].innerHTML + '</xsl:text>
   			<xsl:value-of select="$TOK_FIELDREF_SEARCH_HELP_2"/>
   			<xsl:text>'; if(elem!=null) elem.innerHTML = text; var elem2 = target.document.getElementById('</xsl:text>
   			<xsl:value-of select="$curTag/@INPUT"/>
   			<xsl:text>'); if(elem2!=null) elem2.title = text;</xsl:text>
	    </xsl:variable>

	    <xsl:variable name="onKeyPress">
    		<xsl:text>if(event.keyCode == 13) {var elem = document.getElementById('</xsl:text>
   			<xsl:value-of select="$inp"/>
   			<xsl:text>_BT'); elem.focus(); elem.click();}</xsl:text>
	    </xsl:variable>
	    
	    <!-- The intial search help text -->
		<xsl:variable name="searchHelpText">
			<xsl:value-of select="$TOK_FIELDREF_SEARCH_HELP_1"/>
			<xsl:choose>
               	<xsl:when test="$curTag/OPTIONS[@SELECTED = 1] != ''">
                   	<xsl:value-of select="$curTag/OPTIONS[@SELECTED = 1]"/>
                   </xsl:when>
                   <xsl:otherwise>
                   	<xsl:value-of select="$curTag/OPTIONS/OPTION"/>
                   </xsl:otherwise>
               </xsl:choose>
              	<xsl:value-of select="$TOK_FIELDREF_SEARCH_HELP_2"/>
        </xsl:variable>

		<xsl:choose>
			<xsl:when test="$mode != $FIELDREF_MODE_VIEW">
				<TR>
					<TD COLSPAN="3">
					<DIV id="{$inp}_div_search" CLASS="{$div_search_class_name}">
		                <FIELDSET style="margin-top:0px;">
		                	<LEGEND style="margin-top:0px;">
				            <TABLE class="fieldRefSearchParamsLegend">
					            <TBODY>
						            <TR>
							            <TD><xsl:value-of select="$TOK_SEARCH"/> [<A TITLE="{$TOK_FIELDREF_EXT_SEARCH_PARAMS_TITLE}" HREF="javascript:top.scripts.toggleStyle (document, '{$inp}_ext_search', 'fieldRefExtSearchParams_open', 'fieldRefExtSearchParams_closed', '{$inp}_ext_search_img', '{$layoutPath}images/global/elemOpen.gif', '{$layoutPath}images/global/elemClosed.gif');">
							            <IMG BORDER="0" ID="{$inp}_ext_search_img" SRC="{$layoutPath}images/global/elemClosed.gif" ALIGN="absmiddle"/></A>
							            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
							            <A TITLE="{$TOK_FIELDREF_EXT_SEARCH_PARAMS_TITLE}" HREF="javascript:top.scripts.toggleStyle (document, '{$inp}_ext_search', 'fieldRefExtSearchParams_open', 'fieldRefExtSearchParams_closed', '{$inp}_ext_search_img', '{$layoutPath}images/global/elemOpen.gif', '{$layoutPath}images/global/elemClosed.gif');"><xsl:value-of select="$TOK_FIELDREF_EXT_SEARCH"/></A>]
							            </TD>
						            </TR>
					            </TBODY>
				            </TABLE>
		                </LEGEND>
		                <TABLE class="fieldRefSearchParams" width="100%">
		                <TR>
		                <TD>
		                    <DIV CLASS="fieldRefExtSearchParams_closed" ID="{$inp}_ext_search">    
			                    <xsl:call-template name="dt_selection">
			                        <xsl:with-param name="curTag" select="$curTag/OPTIONS"/>
			                        <xsl:with-param name="name" select="$fieldSelName"/>
			                        <xsl:with-param name="onChange" select="$sbOnChange"/>
			                    </xsl:call-template>
			                    
								<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
							</DIV>			                    

			                <DIV style="float: left;">		        
			    <!-- <xsl:value-of select="$onChange" /> -->
			                    <xsl:call-template name="internDt_select">
			                        <xsl:with-param name="name" select="$typeSelName"/>
			                        <xsl:with-param name="optionString">
			                            <xsl:value-of select="$TOK_CONTAINS"/>
			                            <xsl:text>,</xsl:text>
			                            <xsl:value-of select="$TOK_EXACTLY"/>
			                            <xsl:text>,</xsl:text>
			                            <xsl:value-of select="$TOK_SIMILAR"/>
			                        </xsl:with-param>
			                        <xsl:with-param name="selected" select="'$div_search_class_name'"/>
			                        <xsl:with-param name="isSystemSelect" select="1"/>
			                    </xsl:call-template>
			                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		                
			                    <xsl:call-template name="internDt_input">
			                        <xsl:with-param name="fieldName" select="$curTag/@INPUT"/>
			                        <xsl:with-param name="restriction" select="'iTx'"/>
									<!-- <xsl:with-param name="style" select="'vertical-align: top;'"/> -->
			                        <xsl:with-param name="classId" select="$classId"/>
			                        <xsl:with-param name="style" select="$style"/>
			                        <xsl:with-param name="size" select="$size"/>
			                        <xsl:with-param name="maxlength" select="$maxlength"/>
			                        <!--  <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>-->
			                        <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
			                        <xsl:with-param name="value" select="''"/>
			                        <xsl:with-param name="onChange" select="$onChange"/>
			                        <xsl:with-param name="onSelect" select="$onSelect"/>
			                        <xsl:with-param name="onFocus" select="$onFocus"/>
			                        <xsl:with-param name="onBlur" select="$onBlur"/>
									<xsl:with-param name="onKeyPress" select="$onKeyPress"/>
			                        <xsl:with-param name="tabindex" select="$tabindex"/>
			                        <xsl:with-param name="accesskey" select="$accesskey"/>
			                        <xsl:with-param name="helptext" select="$searchHelpText"/>
			                        <xsl:with-param name="errortext" select="$errortext"/>
			                        <xsl:with-param name="formName" select="$form"/>
			                    </xsl:call-template>
			                    
			                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			                    
			                   <xsl:variable name="noOID" select="'0x0000000000000000'"/>
			                    <xsl:call-template name="internDt_button">
			                        <xsl:with-param name="name">
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>_BT</xsl:text>
			                        </xsl:with-param>
									<xsl:with-param name="style" select="'vertical-align: top;'"/>
			                        <xsl:with-param name="value" select="$TOK_SEARCH"/>
			                        <xsl:with-param name="onClick">
			    <!--
			                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
			    -->
			                            <xsl:text>top.scripts.showFieldRefQuery ('</xsl:text>
			                            <xsl:value-of select="$queryName"/>
			                            <xsl:text>', '</xsl:text>
			                            <xsl:value-of select="$noOID"/>
			                            <xsl:text>', document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$fieldSelName"/>
			                            <xsl:text>.options[document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$fieldSelName"/>
			                            <xsl:text>.selectedIndex].value, </xsl:text>
			                            <xsl:text>document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$typeSelName"/>
			                            <xsl:text>.options[document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$typeSelName"/>
			                            <xsl:text>.selectedIndex].value, </xsl:text>
			                            <xsl:text>document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>.value, '</xsl:text>
			                            <xsl:value-of select="$target"/>
			                            <xsl:text>');</xsl:text>
			                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
			                            <xsl:text>document.</xsl:text>
			                            <xsl:value-of select="$form"/>
			                            <xsl:text>.</xsl:text>
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>_BTC.style.visibility='visible';</xsl:text>
			                            <!--
			                            <xsl:text>top.scripts.showFieldRefQuery ('</xsl:text>
			                            <xsl:value-of select="$queryName"/>
			                            <xsl:text>', '</xsl:text>
			                            <xsl:value-of select="$noOID"/>
			                            <xsl:text>', document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$fieldSelName"/>
			                            <xsl:text>.options[document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$fieldSelName"/>
			                            <xsl:text>.selectedIndex].value, </xsl:text>
			                            <xsl:text>document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$typeSelName"/>
			                            <xsl:text>.options[document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$typeSelName"/>
			                            <xsl:text>.selectedIndex].value, </xsl:text>
			                            <xsl:text>document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>.value</xsl:text>
			                            <xsl:text>);</xsl:text>
			                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
			                            <xsl:text>document.sheetForm.</xsl:text>
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>_BTC.style.visibility='visible';</xsl:text>
			                            -->
			                        </xsl:with-param>
			                    </xsl:call-template>
			                    
			   <!--
			                    <xsl:call-template name="internDt_text_dateTime">
			                        <xsl:with-param name="name" select="$curTag/@INPUT"/>
			                        <xsl:with-param name="value" select="''"/>
			                        <xsl:with-param name="size" select="23"/>
			                        <xsl:with-param name="maxlength" select="25"/>
			                        <xsl:with-param name="restriction" select="'iTx'"/>
			                    </xsl:call-template>
			   -->
			                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			    			</DIV>                                   
							<DIV style="float: left;">
			                    <xsl:call-template name="internDt_button">
			                        <xsl:with-param name="name">
			                            <xsl:value-of select="$inp"/>
			                            <xsl:text>_BTC</xsl:text>
			                        </xsl:with-param>
			                        <xsl:with-param name="value" select="$TOK_CLOSE"/>
			                        <xsl:with-param name="onClick">
			                            <xsl:text>parent.document.body.rows='*,0';</xsl:text>
			                            <xsl:text>this.style.visibility='hidden';</xsl:text>
			                        </xsl:with-param>
			                        <xsl:with-param name="style" select="'width: 30px; visibility: hidden'"/>
			                    </xsl:call-template>
			     			</DIV>                                   
		
		                    <xsl:call-template name="internDt_hidden">
		                        <xsl:with-param name="name">
		                            <xsl:value-of select="$inp"/>
		                            <xsl:text>_OID</xsl:text>
		                        </xsl:with-param>
		                        <xsl:with-param name="value">
		                            <xsl:if test="starts-with ($curTag/child::text (),'0x')">
		                                <xsl:value-of select="$curTag/child::text ()"/>
		                            </xsl:if>
		                        </xsl:with-param>
		                    </xsl:call-template>
		                    
					       	</TD>
					       	</TR>
					       	<TR>
								<TD>
									<!-- Search help text -->
				                    <DIV id="{$inp}_fieldRefSearchParamsHelpText" class="fieldRefSearchParamsHelpText">
					                    <xsl:value-of select="$searchHelpText"/>
				                    </DIV>
			                    </TD>
		                    </TR>
		                    </TABLE>
		                    </FIELDSET>
						</DIV> <!-- id="{$inp}_div_search" -->
					</TD>
				</TR>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="selectedOID">                                                          
					<xsl:choose>                                                                       
						<xsl:when test="$preselectedOID=''">  
							<xsl:value-of select="$curTag/child::text ()"/>
						</xsl:when>                                                                   
					<xsl:otherwise>                                                               
							<xsl:value-of select="$preselectedOID"/>   
					</xsl:otherwise>                                                              
				   </xsl:choose>                                                                     
				</xsl:variable>
				<xsl:call-template name="internDt_hidden">
					<xsl:with-param name="name" select="$curTag/@INPUT"/>
					<xsl:with-param name="value" select="''"/>
				</xsl:call-template>	                    
				<xsl:call-template name="internDt_hidden">
					<xsl:with-param name="name">
						<xsl:value-of select="$inp"/>
						<xsl:text>_OID</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="value">
						<xsl:if test="starts-with ($selectedOID,'0x')">
							<xsl:value-of select="$selectedOID"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>                                                              
		</xsl:choose>
    </xsl:template> <!-- createFieldrefStructure -->

    <!--************************************************************************
    * Old implementation: Create the fieldref structure
    *
    * @param curTag         the field tag
    * @param inp            the field name
    * @param queryName      the name of the query
    * @param emptyJSArray   a javascript code to empty the array
    * @param classId        css class
    * @param style          css style
    * @param size           size
    * @param maxlength      maxlength
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

    <xsl:template name="createFieldrefStructureOld">
    <xsl:param name="curTag"/>
    <xsl:param name="preselectedValue"/>
    <xsl:param name="preselectedOID"/>
    <xsl:param name="fieldrefMode"/>
    <xsl:param name="inp"/>
    <xsl:param name="queryName"/>
    <xsl:param name="emptyJSArray"/>
    <xsl:param name="classId"/>
    <xsl:param name="style"/>
    <xsl:param name="size" select="23"/>
    <xsl:param name="maxlength" select="25"/>
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
    --><!-- new param formName --><!--
    <xsl:param name="formName"/>
    
    --><!-- if no formName is given, set default name to sheetForm --><!--
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:variable name="fieldSelName">
            <xsl:value-of select="$inp"/>
                <xsl:text>_S</xsl:text>
        </xsl:variable>
        <xsl:variable name="typeSelName">
            <xsl:value-of select="$inp"/>
                <xsl:text>_M</xsl:text>
        </xsl:variable>
        <xsl:variable name="curRow" select="position ()"/>
        <xsl:variable name="mode">                                                          
            <xsl:choose>                                                                       
                <xsl:when test="$fieldrefMode=''">                                    
                    <xsl:value-of select="$FIELDREF_MODE_DEFAULT"/>                                         
                </xsl:when>                                                                   
                <xsl:otherwise>                                                               
                    <xsl:value-of select="$fieldrefMode"/>   
                </xsl:otherwise>                                                              
            </xsl:choose>                                                                     
        </xsl:variable>   

        <xsl:for-each select="$curTag/FIELDS/*">
            <xsl:variable name="fieldName"
          select="concat ($inp, '_', format-number (position () - 1, '####'))"/>
            <TR>
                <TD WIDTH="1%">
                    <LABEL FOR="{$fieldName}">
	                    <NOBR>
		                    <xsl:value-of select="@TOKEN"/>
		                    <xsl:text>:</xsl:text>
	                   	</NOBR>
                    </LABEL>
                </TD>
                <TD>
                    <xsl:if test="$mode != $FIELDREF_MODE_SEARCH">
                        <xsl:variable name="selectedValue">                                                          
                            <xsl:choose>                                                                       
                                <xsl:when test="$preselectedValue=''">                                       
                                     <xsl:value-of select="child::text ()"/>   
                                </xsl:when>                                                                   
                                <xsl:otherwise>                                                               
                                    <xsl:value-of select="$preselectedValue"/>   
                                </xsl:otherwise>                                                              
                            </xsl:choose>                                                                     
                        </xsl:variable>
--><!--
                    <xsl:if test="position() = 1">
                        <IMG SRC="{$layoutPath}images/objectIcons/Referenz.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:if>
--><!--
                        <INPUT TYPE="TEXT" ID="{$fieldName}" NAME="{$fieldName}"
                               STYLE="background-color: transparent; border: 0px;"
                               SIZE="70" MAXLENGTH="256" ONFOCUS="this.select ()"
                               VALUE="{$selectedValue}" READONLY="">
--><!--
                        <xsl:attribute name="CLASS">
                           <xsl:choose>
                                <xsl:when test="$curTag/@MANDATORY='YES'">
                                    <xsl:text>mandatory</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>readonly</xsl:text>
                                    <xsl:value-of select="format-number ((position() mod 2) + 1, '####')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
--><!--
                        </INPUT>
                    </xsl:if>
                </TD>
            </TR>
        </xsl:for-each>
        <TR>
            <TD COLSPAN="2">
            <xsl:choose>
                <xsl:when test="$mode != $FIELDREF_MODE_VIEW">
                    <xsl:call-template name="dt_selection">
                        <xsl:with-param name="curTag" select="$curTag/OPTIONS"/>
                        <xsl:with-param name="name" select="$fieldSelName"/>
                    </xsl:call-template>
    
                    <xsl:call-template name="internDt_select">
                        <xsl:with-param name="name" select="$typeSelName"/>
                        <xsl:with-param name="optionString">
                            <xsl:value-of select="$TOK_CONTAINS"/>
                            <xsl:text>,</xsl:text>
                            <xsl:value-of select="$TOK_EXACTLY"/>
                            <xsl:text>,</xsl:text>
                            <xsl:value-of select="$TOK_SIMILAR"/>
                        </xsl:with-param>
                        <xsl:with-param name="selected" select="''"/>
                        <xsl:with-param name="isSystemSelect" select="1"/>
                    </xsl:call-template>
    
                    <xsl:call-template name="internDt_input">
                        <xsl:with-param name="fieldName" select="$curTag/@INPUT"/>
                        <xsl:with-param name="restriction" select="'iTx'"/>
                        <xsl:with-param name="classId" select="$classId"/>
                        <xsl:with-param name="style" select="$style"/>
                        <xsl:with-param name="size" select="$size"/>
                        <xsl:with-param name="maxlength" select="$maxlength"/>
                        <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
                        <xsl:with-param name="readonly" select="translate ($readonly, 'TRUEYSJA', 'trueysja')=$BOOL_YES"/>
                        <xsl:with-param name="value" select="''"/>
                        <xsl:with-param name="onChange" select="$onChange"/>
                        <xsl:with-param name="onSelect" select="$onSelect"/>
                        <xsl:with-param name="onFocus" select="$onFocus"/>
                        <xsl:with-param name="onBlur" select="$onBlur"/>
                        <xsl:with-param name="tabindex" select="$tabindex"/>
                        <xsl:with-param name="accesskey" select="$accesskey"/>
                        <xsl:with-param name="helptext" select="$helptext"/>
                        <xsl:with-param name="errortext" select="$errortext"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
     -->
    <!--
                    <xsl:call-template name="internDt_text_dateTime">
                        <xsl:with-param name="name" select="$curTag/@INPUT"/>
                        <xsl:with-param name="value" select="''"/>
                        <xsl:with-param name="size" select="23"/>
                        <xsl:with-param name="maxlength" select="25"/>
                        <xsl:with-param name="restriction" select="'iTx'"/>
                    </xsl:call-template>
    --><!--
    
                    <xsl:call-template name="internDt_hidden">
                        <xsl:with-param name="name">
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_OID</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="value">
                            <xsl:if test="starts-with ($curTag/child::text (),'0x')">
                                <xsl:value-of select="$curTag/child::text ()"/>
                            </xsl:if>
                        </xsl:with-param>
                    </xsl:call-template>
    
                    <xsl:variable name="noOID" select="'0x0000000000000000'"/>
                    <xsl:call-template name="internDt_button">
                        <xsl:with-param name="name">
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_BT</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="value" select="$TOK_SEARCH"/>
                        <xsl:with-param name="onClick">
    --><!--
                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
    --><!--
                            <xsl:text>top.scripts.showFieldRefQuery ('</xsl:text>
                            <xsl:value-of select="$queryName"/>
                            <xsl:text>', '</xsl:text>
                            <xsl:value-of select="$noOID"/>
                            <xsl:text>', document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$fieldSelName"/>
                            <xsl:text>.options[document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$fieldSelName"/>
                            <xsl:text>.selectedIndex].value, </xsl:text>
                            <xsl:text>document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$typeSelName"/>
                            <xsl:text>.options[document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$typeSelName"/>
                            <xsl:text>.selectedIndex].value, </xsl:text>
                            <xsl:text>document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$inp"/>
                            <xsl:text>.value</xsl:text>
                            <xsl:text>);</xsl:text>
                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
                            <xsl:text>document.</xsl:text>
                            <xsl:value-of select="$form"/>
                            <xsl:text>.</xsl:text>
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_BTC.style.visibility='visible';</xsl:text>-->
                            <!--
                            <xsl:text>top.scripts.showFieldRefQuery ('</xsl:text>
                            <xsl:value-of select="$queryName"/>
                            <xsl:text>', '</xsl:text>
                            <xsl:value-of select="$noOID"/>
                            <xsl:text>', document.sheetForm.</xsl:text>
                            <xsl:value-of select="$fieldSelName"/>
                            <xsl:text>.options[document.sheetForm.</xsl:text>
                            <xsl:value-of select="$fieldSelName"/>
                            <xsl:text>.selectedIndex].value, </xsl:text>
                            <xsl:text>document.sheetForm.</xsl:text>
                            <xsl:value-of select="$typeSelName"/>
                            <xsl:text>.options[document.sheetForm.</xsl:text>
                            <xsl:value-of select="$typeSelName"/>
                            <xsl:text>.selectedIndex].value, </xsl:text>
                            <xsl:text>document.sheetForm.</xsl:text>
                            <xsl:value-of select="$inp"/>
                            <xsl:text>.value</xsl:text>
                            <xsl:text>);</xsl:text>
                            <xsl:text>parent.document.body.rows='*,33%';</xsl:text>
                            <xsl:text>document.sheetForm.</xsl:text>
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_BTC.style.visibility='visible';</xsl:text>
                            --><!--
                        </xsl:with-param>
                    </xsl:call-template>
    
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    
                    <xsl:call-template name="internDt_button">
                        <xsl:with-param name="name">
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_BTD</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="value" select="$TOK_DELETE"/>
                        <xsl:with-param name="onClick">
                            <xsl:text>top.setFieldRef ('</xsl:text>
                            <xsl:value-of select="$inp"/>
                            <xsl:text>', '', </xsl:text>
                            <xsl:value-of select="$emptyJSArray"/>
                            <xsl:text>);</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
    
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    
                    <xsl:call-template name="internDt_button">
                        <xsl:with-param name="name">
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_BTC</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="value" select="$TOK_CLOSE"/>
                        <xsl:with-param name="onClick">
                            <xsl:text>parent.document.body.rows='*,0';</xsl:text>
                            <xsl:text>this.style.visibility='hidden';</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="style" select="'width: 30px; visibility: hidden'"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                <xsl:variable name="selectedOID">                                                          
                    <xsl:choose>                                                                       
                        <xsl:when test="$preselectedOID=''">  
                            <xsl:value-of select="$curTag/child::text ()"/>
                        </xsl:when>                                                                   
                    <xsl:otherwise>                                                               
                            <xsl:value-of select="$preselectedOID"/>   
                    </xsl:otherwise>                                                              
                   </xsl:choose>                                                                     
                </xsl:variable>
      
                    <xsl:call-template name="internDt_hidden">
                        <xsl:with-param name="name" select="$curTag/@INPUT"/>
                        <xsl:with-param name="value" select="''"/>
                    </xsl:call-template>
                    
                    <xsl:call-template name="internDt_hidden">
                        <xsl:with-param name="name">
                            <xsl:value-of select="$inp"/>
                            <xsl:text>_OID</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="value">
                            <xsl:if test="starts-with ($selectedOID,'0x')">
                                <xsl:value-of select="$selectedOID"/>
                            </xsl:if>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:otherwise>                                                              
            </xsl:choose>
            </TD>
        </TR>
    </xsl:template>  createFieldrefStructureOld -->


    <!--************************************************************************
    * Format a money value and insert the dots
    *
    * @param moneyValue         the money value
    * @param moneyValueAfter    the original money value
    * @param curLen             the current length
    -->
    <xsl:template name="getDotSeparatedMoney">
    <xsl:param name="moneyValue"/>
    <xsl:param name="moneyValueAfter"/>
    <xsl:param name="curLen"/>

        <xsl:if test="$curLen &gt; 3">
            <xsl:variable name="len">
                <xsl:choose>
                    <xsl:when test="($curLen mod 3) = 0">
                        <xsl:value-of select="number ('3')"/>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:value-of select="$curLen mod 3"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:value-of select="substring ($moneyValue, 1, $len)"/>
            <xsl:text>.</xsl:text>

            <xsl:call-template name="getDotSeparatedMoney">
                <xsl:with-param name="moneyValue" select="substring ($moneyValue, $len + 1)"/>
                <xsl:with-param name="moneyValueAfter" select="$moneyValueAfter"/>
                <xsl:with-param name="curLen" select="$curLen - $len"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="$curLen &lt;= 3">
            <xsl:value-of select="$moneyValue"/>
            <xsl:value-of select="$moneyValueAfter"/>
        </xsl:if>
    </xsl:template> <!-- getDotSeparatedMoney -->


    <!--************************************************************************
    * Create a reminder field
    *
    * @param tagName    the field tag
    * @param mode       edit or view mode
    -->
    <xsl:template name="dt_reminder">
    <xsl:param name="tagName"/>
    <xsl:param name="mode"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    <xsl:param name="showDetails" select="true()"/>
    <xsl:param name="format" select="' (tt.mm.jjjj)'"/>
    
        <!-- if no formName is given, set default name to sheetForm -->
        <xsl:variable name="form">
            <xsl:choose>
                <xsl:when test="$formName = ''">
                    <xsl:value-of select="$FORM_DEFAULT"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$formName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
    
        <xsl:variable name="fieldName" select="$tagName/@INPUT"/>
        <xsl:variable name="datatype" select="$tagName/@TYPE"/>
        <xsl:variable name="date" select="$tagName/VALUE/child::text ()"/>
        <xsl:variable name="displayType" select="$tagName/@DISPLAY"/>

        <!-- display the field value as standard date value: -->
        <xsl:choose>
            <xsl:when test="$mode = $MODE_VIEW">
                <xsl:value-of select="$date"/>
            </xsl:when><!--MODE_VIEW-->

            <xsl:when test="$mode = $MODE_EDIT">
                <xsl:call-template name="internDt_text">
                    <xsl:with-param name="tagName" select="$tagName"/>
                    <xsl:with-param name="size" select="10"/>
                    <xsl:with-param name="maxlength" select="10"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--MODE_EDIT-->
        </xsl:choose>
        <xsl:value-of select="$format"/>
        
        <xsl:if test="$showDetails = true()">
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        <A HREF="javascript:void (top.scripts.toggleStyle (document, '{$fieldName}_div', 'reminder', 'reminder_invisible', '{$fieldName}_img', '{$layoutPath}images/global/up.gif', '{$layoutPath}images/global/down.gif'))">
        <IMG ID="{$fieldName}_img" SRC="{$layoutPath}images/global/down.gif"
             ALT="Erinnerung" BORDER="0" HEIGHT="15" ALIGN="ABSMIDDLE"/></A>

        <DIV ID="{$fieldName}_div" CLASS="reminder_invisible">
        <TABLE BORDER="2" CLASS="reminder">
            <TR VALIGN="TOP" CLASS="reminder" TITLE="{$TOK_REMHEAD_REMIND1}">
                <TD>
                    <xsl:value-of select="$TOK_REMHEAD_REMIND1"/>
                    <xsl:text>:</xsl:text>
                    <xsl:call-template name="dt_reminderParams">
                        <xsl:with-param name="mode" select="$mode"/>
                        <xsl:with-param name="fieldName" select="$fieldName"/>
                        <xsl:with-param name="paramName" select="'REMIND1'"/>
                        <xsl:with-param name="paramDays"
                                        select="$tagName/@REMIND1DAYS"/>
                        <xsl:with-param name="paramText"
                                        select="$tagName/@REMIND1TEXT"/>
                        <xsl:with-param name="paramRecip"
                                        select="$tagName/@REMIND1RECIP"/>
                        <xsl:with-param name="recipQueryResult"
                                        select="$tagName/REMIND1RECIPQUERY"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <TR VALIGN="TOP" CLASS="reminder" TITLE="{$TOK_REMHEAD_REMIND2}">
                <TD>
                    <xsl:value-of select="$TOK_REMHEAD_REMIND2"/>
                    <xsl:text>:</xsl:text>
                    <xsl:call-template name="dt_reminderParams">
                        <xsl:with-param name="mode" select="$mode"/>
                        <xsl:with-param name="fieldName" select="$fieldName"/>
                        <xsl:with-param name="paramName" select="'REMIND2'"/>
                        <xsl:with-param name="paramDays"
                                        select="$tagName/@REMIND2DAYS"/>
                        <xsl:with-param name="paramText"
                                        select="$tagName/@REMIND2TEXT"/>
                        <xsl:with-param name="paramRecip"
                                        select="$tagName/@REMIND2RECIP"/>
                        <xsl:with-param name="recipQueryResult"
                                        select="$tagName/REMIND2RECIPQUERY"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
                </TD>
            </TR>
            <TR VALIGN="TOP" CLASS="reminder" TITLE="{$TOK_REMHEAD_ESCALATE}">
                <TD>
                    <xsl:value-of select="$TOK_REMHEAD_ESCALATE"/>
                    <xsl:text>:</xsl:text>
                    <xsl:call-template name="dt_reminderParams">
                        <xsl:with-param name="mode" select="$mode"/>
                        <xsl:with-param name="fieldName" select="$fieldName"/>
                        <xsl:with-param name="paramName" select="'ESCALATE'"/>
                        <xsl:with-param name="paramDays"
                                        select="$tagName/@ESCALATEDAYS"/>
                        <xsl:with-param name="paramText"
                                        select="$tagName/@ESCALATETEXT"/>
                        <xsl:with-param name="paramRecip"
                                        select="$tagName/@ESCALATERECIP"/>
                        <xsl:with-param name="recipQueryResult"
                                        select="$tagName/ESCALATERECIPQUERY"/>
                        <xsl:with-param name="formName" select="$form"/>
                    </xsl:call-template>
                </TD>
            </TR>
        </TABLE>
        </DIV>
        </xsl:if>
    </xsl:template> <!--dt_reminder-->


    <!--************************************************************************
    * Create the reminder parameter
    *
    * @param mode       edit or view mode
    * @param fieldName  the field name
    * @param paramName  the paramter name
    * @param paramDays  the days paramter
    * @param paramText  the text paramter
    * @param paramRecip the recipient parameter
    * @param recipQueryResult   the query result for the recipients
    -->
    <xsl:template name="dt_reminderParams">
    <xsl:param name="mode"/>
    <xsl:param name="fieldName"/>
    <xsl:param name="paramName"/>
    <xsl:param name="paramDays"/>
    <xsl:param name="paramText"/>
    <xsl:param name="paramRecip"/>
    <xsl:param name="recipQueryResult"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <TABLE WIDTH="100%" BORDER="1">
        <TR VALIGN="TOP" CLASS="infoRow1">
            <xsl:variable name="actFieldName"
                          select="concat($fieldName,$paramName,'DAYS')"/>
            <TD WIDTH="1%">
                <LABEL FOR="{$actFieldName}">
                <xsl:value-of select="$TOK_REM_DAYS"/>
                <xsl:text>:</xsl:text>
                </LABEL>
            </TD>
            <TD>
                <xsl:choose>
                    <xsl:when test="$mode = $MODE_VIEW">
                        <xsl:value-of select="$paramDays"/>
                    </xsl:when><!--MODE_VIEW-->

                    <xsl:when test="$mode = $MODE_EDIT">
                        <xsl:call-template name="internDt_textForValue">
                            <xsl:with-param name="fieldName"
                                            select="$actFieldName"/>
                            <xsl:with-param name="datatype"
                                            select="'INTEGER'"/>
                            <xsl:with-param name="size" select="3"/>
                            <xsl:with-param name="maxlength" select="3"/>
                            <xsl:with-param name="hidden" select="false ()"/>
                            <xsl:with-param name="mandatory" select="YES"/>
                            <xsl:with-param name="readonly"
                                            select="$BOOL_NO"/>
                            <xsl:with-param name="value"
                                            select="$paramDays"/>
                            <xsl:with-param name="formName" select="$form"/>
                        </xsl:call-template>
                    </xsl:when><!--MODE_EDIT-->
                </xsl:choose>
            </TD>
        </TR>
        <TR VALIGN="TOP" CLASS="infoRow2">
            <xsl:variable name="actFieldName"
                          select="concat($fieldName,$paramName,'RECIP')"/>
            <TD WIDTH="1%">
                <LABEL FOR="{$actFieldName}">
                <xsl:value-of select="$TOK_REM_RECIP"/>
                <xsl:text>:</xsl:text>
                </LABEL>
            </TD>
            <TD>
                <xsl:choose>
                    <xsl:when test="$mode = $MODE_VIEW">
                        <A HREF="javascript:top.showObject('{$paramRecip}');"><xsl:value-of select="$recipQueryResult/RESULTROW[RESULTELEMENT[@NAME='oid']/child::text () = $paramRecip]/RESULTELEMENT[@NAME='name']/child::text ()"/></A>
                    </xsl:when><!--MODE_VIEW-->

                    <xsl:when test="$mode = $MODE_EDIT">
                        <xsl:call-template name="dt_reminderSelection">
                            <xsl:with-param name="recipQueryResult"
                                            select="$recipQueryResult"/>
                            <xsl:with-param name="fieldName"
                                            select="$actFieldName"/>
                            <xsl:with-param name="datatype"
                                            select="'SELECTION'"/>
                            <xsl:with-param name="mandatory" select="YES"/>
                            <xsl:with-param name="selectedElem"
                                            select="$paramRecip"/>
                        </xsl:call-template>
                    </xsl:when><!--MODE_EDIT-->
                </xsl:choose>
            </TD>
        </TR>
        <TR VALIGN="TOP" CLASS="infoRow1">
            <xsl:variable name="actFieldName"
                          select="concat($fieldName,$paramName,'TEXT')"/>
            <TD WIDTH="1%">
                <LABEL FOR="{$actFieldName}">
                <xsl:value-of select="$TOK_REM_TEXT"/>
                <xsl:text>:</xsl:text>
                </LABEL>
            </TD>
            <TD>
                <xsl:choose>
                    <xsl:when test="$mode = $MODE_VIEW">
                        <xsl:value-of select="$paramText"/>
                    </xsl:when><!--MODE_VIEW-->

                    <xsl:when test="$mode = $MODE_EDIT">
                        <xsl:call-template name="internDt_textForValue">
                            <xsl:with-param name="fieldName"
                                            select="$actFieldName"/>
                            <xsl:with-param name="datatype"
                                            select="'DESCRIPTION'"/>
                            <xsl:with-param name="size" select="50"/>
                            <xsl:with-param name="maxlength" select="255"/>
                            <xsl:with-param name="hidden" select="false ()"/>
                            <xsl:with-param name="mandatory" select="YES"/>
                            <xsl:with-param name="readonly"
                                            select="$BOOL_NO"/>
                            <xsl:with-param name="value"
                                            select="$paramText"/>
                            <xsl:with-param name="formName" select="$form"/>
                        </xsl:call-template>
                    </xsl:when><!--MODE_EDIT-->
                </xsl:choose>
            </TD>
        </TR>
        </TABLE>
    </xsl:template>
    <!--dt_reminderParams-->

    <!--************************************************************************
    * Create the reminder seletion
    *
    * @param recipQueryResult   the query result for the recipients
    * @param fieldName          the field name
    * @param datatype           the datatype
    * @param mandatory          mandatory option
    * @param selectedElem       the selected element
    -->
    <xsl:template name="dt_reminderSelection">
    <xsl:param name="recipQueryResult"/>
    <xsl:param name="fieldName"/>
    <xsl:param name="datatype"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="selectedElem"/>

        <SELECT ID="{$fieldName}" NAME="{$fieldName}" SIZE="1">
            <xsl:if test="$mandatory='YES'">
                <xsl:attribute name="CLASS">
                    <xsl:text>mandatory</xsl:text>
                </xsl:attribute>
            </xsl:if>
<!--
            <xsl:attribute name="onChange">
                <xsl:value-of select="$onChange"/>
            </xsl:attribute>
-->
            <xsl:for-each select="$recipQueryResult/RESULTROW">
                <xsl:variable name="id"
                    select="RESULTELEMENT[@NAME='oid']/child::text ()"/>
                <xsl:variable name="value"
                    select="RESULTELEMENT[@NAME='name']/child::text ()"/>
                <OPTION VALUE="{$id}">
                    <xsl:if test="$selectedElem=$id or $selectedElem=$value">
                        <xsl:attribute name="SELECTED"/>
                    </xsl:if>
                    <xsl:value-of select="$value"/>
                </OPTION>
            </xsl:for-each>
        </SELECT>
    </xsl:template> <!-- dt_reminderSelection -->



    <!-- ***********************************************************************
     * Create the restrictions for a tag
     *
     * @param tagName       the tag to handle
     * @param formName      the name of the form
    -->
    <xsl:template name="getRestrictions">
    <xsl:param name="tagName"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:text>if (document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$tagName/@INPUT"/>
        <xsl:text>!= null) {</xsl:text>
        <xsl:text>if (!</xsl:text>
        <xsl:call-template name="getRestriction">
            <xsl:with-param name="tagName" select="$tagName"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>
        <xsl:text>) return false;} </xsl:text>
    </xsl:template> <!-- getRestrictions -->


    <!-- ***********************************************************************
     * Create the restrictions for a tag
     *
     * @param tagName       the tag to handle
     * @param datatype      datatype of tha tag
     * @param fieldName     fieldname of the tag
     * @param value         the value of the tag
     * @param mandatory     mandatory option
     * @param formName      the name of the form
     *
    -->
    <xsl:template name="getRestriction">
    <xsl:param name="tagName"/>
    <xsl:param name="datatype" select="$tagName/@TYPE"/>
    <xsl:param name="fieldName" select="$tagName/@INPUT"/>
    <xsl:param name="value" select="$tagName/child::text ()"/>
    <xsl:param name="mandatory" select="$tagName/@MANDATORY"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:call-template name="getRestrictionForValue">
            <xsl:with-param name="fieldName" select="$fieldName"/>
            <xsl:with-param name="datatype" select="$datatype"/>
            <xsl:with-param name="value" select="$value"/>
            <xsl:with-param name="mandatory" select="translate ($mandatory, 'TRUEYSJA', 'trueysja')= $BOOL_YES"/>
            <xsl:with-param name="viewType" select="$tagName/@VIEWTYPE"/>
            <xsl:with-param name="formName" select="$form"/>
        </xsl:call-template>
    </xsl:template> <!--getRestriction-->


    <!-- ***********************************************************************
     * Create the specific restriction for th given tag
     *
     * @param datatype      datatype of the tag
     * @param fieldName     fieldname of the tag
     * @param value         the value of the tag
     * @param mandatory     mandatory option
     * @param viewType      the view type attribute
     * @param formName      the name of the form
    -->
    <xsl:template name="getRestrictionForValue">
        <xsl:param name="fieldName"/>
        <xsl:param name="datatype"/>
        <xsl:param name="value"/>
        <xsl:param name="mandatory"/>
        <xsl:param name="viewType"/>
        <!-- new param formName -->
        <xsl:param name="formName"/>
        
        <!-- if no formName is given, set default name to sheetForm -->
        <xsl:variable name="form">
            <xsl:choose>
                <xsl:when test="$formName = ''">
                    <xsl:value-of select="$FORM_DEFAULT"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$formName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- Variables for ranges of the datatypes on the database (MS-SQL). -->
        <!-- DJHint: The name of the variable is the name of the datatype on
             the datatbase an 'Min' or 'Max'. -->
        <xsl:variable name="const_intMin" select="'-2147483648'"/>
        <xsl:variable name="const_intMax" select="'2147483647'"/>
        <!-- The database range of money on the database!! -->
<!--
        <xsl:variable name="const_moneyMin" select="'-922337203685477.5808'"/>
        <xsl:variable name="const_moneyMax" select="'922337203685477.5807'"/>
-->
        <!-- DJ HINT: The Range of money on the database (MS-SQL) is from
             -922337203685477.5808 to 922337203685477.5807 but because of
             problems with the formvalidation we don't have to set
             past-comma-digits, also because of java implementation of money we
             have to short the range by 4 because the money is multiplied with
             10000. If we set now the maximum range to the database maximum this
             is multiplied with 10000 and we get an error.
             The minimum range is only for future enhancments because the
             minimum money is 0. -->
        <xsl:variable name="const_moneyMin" select="'-922337203685477'"/>
        <xsl:variable name="const_moneyMax" select="'92233720368'"/>

        <xsl:choose>
        <!--************************ SYSTEM ************************-->
            <xsl:when test="$fieldName = 'nomen'">
            <!-- if (!top.iTx (document.sheetForm.nomen, false)) return false;
                 -->
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iTx'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="true()"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- NAME -->

            <xsl:when test="$fieldName = 'desc'">
            <!-- if (!top.iTx (document.sheetForm.desc, true) ||
                 !top.iLLE(document.sheetForm.desc, 255)) return false;
                 -->
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iTx'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="false()"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>

                <xsl:text> || !</xsl:text>

                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iLLE'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="value" select="'255'"/>
                    <xsl:with-param name="useMandatory" select="false()"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- DESCRIPTION -->

            <xsl:when test="$fieldName = 'vu'">
            <!-- if (!top.iDA (document.sheetForm.vu, '25.06.02', false)) return false;
                 -->
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iDA'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="true()"/>
                    <xsl:with-param name="value">
                        <xsl:text>"" + new Date().getDate() + "." + (new Date().getMonth()+1) + "." + (new Date().getYear()%100)</xsl:text>                        
                    </xsl:with-param> 
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- VALIDUNTIL -->

        <!--*********************** VALUES  *************************-->
            <!--CHAR, LINK, TEXT, PASSWORD -->
            <xsl:when test="$datatype = 'CHAR' or
                            $datatype = 'LINK' or
                            $datatype = 'TEXT' or
                            $datatype = 'PASSWORD'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iTx'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when><!--CHAR, LINK, TEXT, PASSWORD -->

            <!-- IMAGE, FILE -->
            <xsl:when test="$datatype = 'IMAGE' or
                            $datatype = 'FILE'">
                <xsl:call-template name="buildFileRestriction">
                    <xsl:with-param name="restrictionName" select="'iFile'"/>
                    <xsl:with-param name="restrictionName2" select="'iFileExt'"/>
                    <xsl:with-param name="fieldName1" select="$fieldName"/>
                    <xsl:with-param name="fieldName2"><xsl:value-of select="$fieldName"/>_FILE</xsl:with-param>
                    <xsl:with-param name="fieldName3"><xsl:value-of select="$fieldName"/>_FILETYPESALLOWED</xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- IMAGE, FILE -->

            <!--OBJECTREF, FIELDREF-->
            <xsl:when test="$datatype = 'OBJECTREF' or
                            $datatype = 'FIELDREF'">
                <xsl:call-template name="buildRefRestriction">
                    <xsl:with-param name="fieldName"><xsl:value-of select="$fieldName"/>_OID</xsl:with-param>
                    <xsl:with-param name="focusFieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!--OBJECTREF, FIELDREF-->

            <!-- DATE -->
            <xsl:when test="$datatype = 'DATE'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iD'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!--DATE-->

            <!-- EMAIL -->
            <xsl:when test="$datatype = 'EMAIL'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iEm'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when>

            <!-- INTEGER -->
            <xsl:when test="$datatype = 'INTEGER'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iIR'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="minVal" select="$const_intMin"/>
                    <xsl:with-param name="maxVal" select="$const_intMax"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when>

            <!-- LONGTEXT, HTMLETEXT -->
            <xsl:when test="$datatype = 'LONGTEXT' or
                            $datatype = 'HTMLTEXT'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iNE'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- LONGTEXT, HTMLETEXT -->

            <!-- MONEY -->
            <xsl:when test="$datatype = 'MONEY'">
                <xsl:text>top.iMR (document.</xsl:text>
                <xsl:value-of select="$form"/>
                <xsl:text>.</xsl:text>
                <xsl:value-of select="$fieldName"/>
                <xsl:text>, 0, </xsl:text>
                <xsl:value-of select="$const_moneyMax"/>
                <xsl:text>, </xsl:text>
                <xsl:choose>
                    <xsl:when test="$mandatory">
                        <xsl:value-of select="$BOOL_FALSE"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$BOOL_TRUE"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>)</xsl:text>
            </xsl:when> <!-- MONEY -->

            <!--NUMBER, FLOAT, DOUBLE -->
            <xsl:when test="$datatype = 'NUMBER' or
                            $datatype = 'FLOAT' or
                            $datatype = 'DOUBLE'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iNu'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!--NUMBER, FLOAT, DOUBLE -->

            <!-- TIME -->
            <xsl:when test="$datatype = 'TIME'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iT'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when>

            <!-- DATETIME -->
            <xsl:when test="$datatype = 'DATETIME'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iD'"/>
                    <xsl:with-param name="fieldName">
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_d</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>

                <xsl:text> || !</xsl:text>

                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iT'"/>
                    <xsl:with-param name="fieldName">
                        <xsl:value-of select="$fieldName"/>
                        <xsl:text>_t</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- DATETIME -->

            <!-- QUERYSELECTIONBOX, QUERYSELECTIONBOXNUM, QUERYSELECTIONBOXINT -->
            <!-- SELECTIONBOX, SELECTIONBOXNUM, SELECTIONBOXINT -->
            <xsl:when test="contains ($datatype,'SELECTIONBOX')">
                <xsl:call-template name="buildSelectionBoxRestriction">
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="viewType" select="$viewType"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when> <!-- *SELECTIONBOX* -->

            <!-- REMINDER is checked for date -->
            <xsl:when test="$datatype = 'REMINDER'">
                <xsl:call-template name="buildRestriction">
                    <xsl:with-param name="restrictionName" select="'iD'"/>
                    <xsl:with-param name="fieldName" select="$fieldName"/>
                    <xsl:with-param name="mandatory" select="$mandatory"/>
                    <xsl:with-param name="formName" select="$form"/>
                </xsl:call-template>
            </xsl:when>
            <!-- REMINDER -->

            <xsl:otherwise>
                <xsl:text>true</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template> <!-- getRestrictionForValue -->

    <!-- ***********************************************************************
     * Build the javascript restriction code
     *
     * @param restrictionName   the restriction name
     * @param fieldName         the field name
     * @param minVal            a min value
     * @param maxVal            a max value
     * @param mandatory         mandatory option
     * @param value             a value
     * @param useMandatory      switch to disable mandatory option
     * @param formName      the name of the form
    -->
    <xsl:template name="buildRestriction">
    <xsl:param name="restrictionName"/>
    <xsl:param name="fieldName"/>
    <xsl:param name="minVal" select="''"/>
    <xsl:param name="maxVal" select="''"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="value" select="''"/>
    <xsl:param name="useMandatory" select="$BOOL_TRUE"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <xsl:text>top.</xsl:text>
        <xsl:value-of select="$restrictionName"/>
        <xsl:text> (document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName"/>
        <xsl:if test="string-length ($value) &gt; 0">
            <xsl:text>, </xsl:text>
            <xsl:value-of select="$value"/>
        </xsl:if>
        <xsl:if test="string-length ($minVal) &gt; 0">
            <xsl:text>, </xsl:text>
            <xsl:value-of select="$minVal"/>
        </xsl:if>
        <xsl:if test="string-length ($maxVal) &gt; 0">
            <xsl:text>, </xsl:text>
            <xsl:value-of select="$maxVal"/>
        </xsl:if>
        <xsl:if test="$useMandatory">
            <xsl:text>, </xsl:text>
            <xsl:choose>
                <xsl:when test="$mandatory">
                    <xsl:value-of select="$BOOL_FALSE"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$BOOL_TRUE"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:text>)</xsl:text>
    </xsl:template> <!-- buildRestriction -->


    <!-- ***********************************************************************
     * Build the javascript restriction code for a file field
     *
     * @param restrictionName   the restriction name
     * @param fieldName1        the field name
     * @param fieldName2        the field name
     * @param mandatory         mandatory option
     * @param formName      the name of the form
    -->
    <xsl:template name="buildFileRestriction">
    <xsl:param name="restrictionName"/>
    <xsl:param name="restrictionName2"/>
    <xsl:param name="fieldName1"/>
    <xsl:param name="fieldName2"/>
    <xsl:param name="fieldName3"/>
    <xsl:param name="mandatory"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:text>top.</xsl:text>
        <xsl:value-of select="$restrictionName"/>
        <xsl:text> (document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName1"/>
        <xsl:text>, document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName2"/>
        <xsl:text>, </xsl:text>
            <xsl:choose>
                <xsl:when test="$mandatory">
                    <xsl:value-of select="$BOOL_FALSE"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$BOOL_TRUE"/>
                </xsl:otherwise>
            </xsl:choose>
        <xsl:text>)</xsl:text>
    
        <xsl:text> &amp;&amp; </xsl:text>
        
        <xsl:text>top.</xsl:text>
        <xsl:value-of select="$restrictionName2"/>
        <xsl:text> (document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName1"/>
        <xsl:text>, document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName3"/>
        <xsl:text>)</xsl:text>
        
    </xsl:template> <!-- buildFileRestriction -->


    <!-- ***********************************************************************
     * Build the javascript restriction code for a reference field
     *
     * @param restrictionName   the restriction name
     * @param focusFieldName    the field name of the focus field
     * @param mandatory         mandatory option
     * @param formName      the name of the form
    -->
    <xsl:template name="buildRefRestriction">
    <xsl:param name="fieldName"/>
    <xsl:param name="focusFieldName"/>
    <xsl:param name="mandatory"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:text>top.mRef (document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$fieldName"/>
        <xsl:text>, </xsl:text>
        <xsl:text>document.</xsl:text>
        <xsl:value-of select="$form"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="$focusFieldName"/>
        <xsl:text>, </xsl:text>
         <xsl:choose>
                <xsl:when test="$mandatory">
                    <xsl:value-of select="$BOOL_FALSE"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$BOOL_TRUE"/>
                </xsl:otherwise>
         </xsl:choose>
        <xsl:text>)</xsl:text>
    </xsl:template><!-- buildRefRestriction -->


    <!-- ***********************************************************************
     * Build the javascript restriction code for a selection box with different
     * view types.
     *
     * @param fieldName         the field name
     * @param mandatory         mandatory option
     * @param viewType          view type attribute
     * @param formName          the name of the form
    -->
    <xsl:template name="buildSelectionBoxRestriction">
    <xsl:param name="fieldName"/>
    <xsl:param name="mandatory"/>
    <xsl:param name="viewType"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$mandatory">
            	<xsl:choose>
            		<xsl:when test="$viewType != 'CHECKLIST'">
		                <xsl:text>top.iESlct (document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$fieldName"/>
		                <xsl:text>, "</xsl:text>
		                <xsl:value-of select="$TOK_NO_OPTION_SELECTED"/>
		                <xsl:text>")</xsl:text>
	                </xsl:when>
            		<xsl:when test="$viewType = 'CHECKLIST'">
		                <xsl:text>top.iERadio (document.</xsl:text>
		                <xsl:value-of select="$form"/>
		                <xsl:text>.</xsl:text>
		                <xsl:value-of select="$fieldName"/>
		                <xsl:text>, "</xsl:text>
		                <xsl:value-of select="$TOK_NO_OPTION_SELECTED"/>
		                <xsl:text>")</xsl:text>
	                </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$BOOL_TRUE"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template><!-- buildSelectionBoxRestriction -->


    <!-- **************************************************************************
     * Create the button for the date selection
     *
     * @param fieldname     the fieldname the button belongs to
     * @param format        the date format
    -->
    <xsl:template name="createDateButton">
    <xsl:param name="fieldName"/>
    <xsl:param name="format" select="'dd.MM.yyyy'"/>
    <!-- new param formName -->
    <xsl:param name="formName"/>
    
    <!-- if no formName is given, set default name to sheetForm -->
    <xsl:variable name="form">
        <xsl:choose>
            <xsl:when test="$formName = ''">
                <xsl:value-of select="$FORM_DEFAULT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
        <xsl:variable name="btnName" select="concat($fieldName,'_BTN')"/>

        <BUTTON ONCLICK="calPopup.select(document.getElementById ('{$fieldName}'),'{$btnName}','{$format}'); return false;"
            TITLE="{$TOK_PICKDATE}"
            CLASS="calPopup"
            NAME="{$btnName}" ID="{$btnName}"
            STYLE="height:24px; vertical-align: middle; padding-left: 1px; padding-right: 1px;"
            TYPE="BUTTON">
            <IMG SRC="{$layoutPath}/images/buttons/buttonCalendar.png"
                 ALT="{$TOK_PICKDATE}" BORDER="none" STYLE="vertical-align: bottom;" ALIGN="ABSMIDDLE"/>
        </BUTTON>

    </xsl:template> <!-- createDateButton-->
</xsl:stylesheet>