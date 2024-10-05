<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="http://xml.apache.org/xalan/java">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="form_datatypes.xsl"/>    
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
	<xsl:variable name="alphaLower" select="'abcdefghijklmnopqrstuvwxyz'"/>
	<xsl:variable name="alphaUpper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
    <!--*********************** VARIABLES END ******************************-->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!--***********************************************************************
     * This template generates the necessary switch to enable and disable the translation mode
     *
     * @param   listOfLanguages     list of all multi lingual content languages
     * @param   labelsOfLanguages   list of all multi lingual content labels
     *-->
    <xsl:template name="createSwitchForMLC">
        <xsl:param name="listOfLanguages"/>
		<xsl:param name="labelsOfLanguages"/>

		<xsl:if test="$listOfLanguages != ''">
			<xsl:variable name="oneLang">
				<xsl:choose>
					<xsl:when test="contains($listOfLanguages,',')">
						<xsl:value-of select="substring-before($listOfLanguages,',')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$listOfLanguages"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="oneLabel">
				<xsl:choose>
					<xsl:when test="contains($labelsOfLanguages,',')">
						<xsl:value-of select="substring-before($labelsOfLanguages,',')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$labelsOfLanguages"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:if test="$oneLang != ''">
				<xsl:if test="$oneLabel = ''">
					<xsl:variable name="oneLabel" select="$oneLang"/>
				</xsl:if>
				<DIV>
					<xsl:call-template name="internDt_checkbox">
						<xsl:with-param name="tagName" select="$oneLang"/>
						<xsl:with-param name="name" select="concat('checkbox','_',$oneLang)"/>
						<xsl:with-param name="onClick" select="'changeFieldsForMLC ();'"/>
					</xsl:call-template>
                    <xsl:choose>
	                    <xsl:when test="$mode = $MODE_VIEW">
	                        <xsl:value-of select="concat($TOK_TRANSLATE_SHOW,' ')"/>
						</xsl:when>
						<xsl:otherwise>
						   <xsl:value-of select="concat($TOK_TRANSLATE_TO,' ')"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="$oneLabel"/>
				</DIV>
			</xsl:if>
				
			<xsl:call-template name="createSwitchForMLC">
				<xsl:with-param name="listOfLanguages" select="substring-after($listOfLanguages,',')"/>
				<xsl:with-param name="labelsOfLanguages" select="substring-after($labelsOfLanguages,',')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template> <!-- createSwitchForMLC -->
	
    <!--***********************************************************************
     * This template generates an info row with multi lingual content 
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
     *-->
    <xsl:template name="infoRowMLC">
        <xsl:param name="tagName"/>
        <xsl:param name="tag" select="/OBJECT/VALUES/VALUE[@FIELD = concat($tagName,'_',translate($leadLangMLC,$alphaLower,$alphaUpper))]"/>
        <xsl:param name="label" select="$tag/@NAME"/>
        <xsl:param name="title" select="$tag/@DESCRIPTION"/>
        <xsl:param name="value"/>
        <xsl:param name="info" select="''"/>
        <xsl:param name="localRow" select="'1'"/>
        <xsl:param name="mode" select="$mode"/>

        <TR CLASS="infoRow{format-number (($localRow mod 2) + 1, '####')}">
            <TD CLASS="name" TITLE="{$title}">
                <xsl:value-of select="$label"/>:
            </TD>
            <TD CLASS="value">
				<SPAN ID="blkDefault{$tag/@INPUT}" STYLE="display: block">
					<xsl:choose>
	                    <xsl:when test="not ($value)">
	                        <xsl:call-template name="getDatatype">
	                            <xsl:with-param name="tagName" select="$tag"/>
	                            <xsl:with-param name="mode" select="$mode"/>
	                        </xsl:call-template>
	                    </xsl:when>
	                    <xsl:otherwise>
	                        <xsl:copy-of select="$value"/>
	                    </xsl:otherwise>
	                </xsl:choose>
                </SPAN>
                <SPAN ID="blkExtend{$tag/@INPUT}" STYLE="display: none;">
                    <DIV CLASS="blkDefault_Extended">
                        <xsl:choose>
                            <xsl:when test="not ($value)">
                                <SPAN id="blkExtend{$tag/@INPUT}_Value">
									<xsl:call-template name="getDatatype">
										<xsl:with-param name="tagName" select="$tag"/>
										<xsl:with-param name="mode" select="$MODE_VIEW"/>
									</xsl:call-template>
								</SPAN>
							</xsl:when>
                            <xsl:otherwise>
                                <xsl:copy-of select="$value"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </DIV>
                    <xsl:call-template name="createMLCBlocks">
						<xsl:with-param name="listOfLanguages" select="$langsMLC"/>
						<xsl:with-param name="tagName" select="$tagName"/>
						<xsl:with-param name="value" select="$value"/>
                        <xsl:with-param name="mode" select="$mode"/>
                    </xsl:call-template>
                </SPAN>

                <xsl:if test="$tagName/@TYPE != 'FIELDREF' and $tagName/@TYPE != 'OBJECTREF'">
                	<xsl:call-template name="createNBSP"/>
                </xsl:if>
                <xsl:if test="$info != ''">
                    <xsl:value-of select="$info"/>
                </xsl:if>
            </TD>
        </TR>
    </xsl:template> <!-- infoRowMLC -->

    <!--***********************************************************************
     * This template iterates over all given multi lingual content languages and creates
     * a block wiht input field for each language
     *
     * @param   listOfLanguages  list of all multi lingual content languages
     * @param   tagName          the name tag of the tag without MLC postfix
     * @param   value            the value to display (instead of evaluating the
     *                           valuetag)
     * @param   mode             the display mode (default is the mode set in the
     *                           global mode var)
     *-->
    <xsl:template name="createMLCBlocks">
        <xsl:param name="listOfLanguages"/>
        <xsl:param name="tagName"/>
        <xsl:param name="value"/>
        <xsl:param name="mode" select="$mode"/>

        <xsl:if test="$listOfLanguages != ''">
            <xsl:variable name="oneLang">
                <xsl:choose>
                    <xsl:when test="contains($listOfLanguages,',')">
                        <xsl:value-of select="substring-before($listOfLanguages,',')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$listOfLanguages"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:if test="$oneLang != ''">
				<xsl:variable name="tag" select="/OBJECT/VALUES/VALUE[@FIELD = concat($tagName,'_',translate($oneLang,$alphaLower,$alphaUpper))]"/>
				<xsl:call-template name="createSingleMLCBlock">
                    <xsl:with-param name="language" select="translate($oneLang,$alphaLower,$alphaUpper)"/>
                    <xsl:with-param name="tagName" select="$tag"/>
					<xsl:with-param name="value" select="$value"/>
					<xsl:with-param name="mode" select="$mode"/>
                </xsl:call-template>
            </xsl:if>
                
            <xsl:call-template name="createMLCBlocks">
                <xsl:with-param name="listOfLanguages" select="substring-after($listOfLanguages,',')"/>
				<xsl:with-param name="tagName" select="$tagName"/>
				<xsl:with-param name="value" select="$value"/>
				<xsl:with-param name="mode" select="$mode"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template> <!--  createMLCBlocks -->

    <!--***********************************************************************
     * This template generates a single multi lingual content block for a given
     * language
     *
     * @param   language    Language for which this input should be created
     * @param   tagName     the name of the value tag to create the info row for
     * @param   value       the value to display (instead of evaluating the
     *                      valuetag)
     * @param   mode        the display mode (default is the mode set in the
     *                      global mode var)
     *-->
    <xsl:template name="createSingleMLCBlock">
        <xsl:param name="language" select="'EN'"/>
        <xsl:param name="tagName"/>
        <xsl:param name="value"/>
        <xsl:param name="mode" select="$mode"/>

		<DIV ID="blkExtend{$tagName/@INPUT}" STYLE="display: none">
        <BR/>
		<SPAN><B><xsl:value-of select="$language"/></B>> </SPAN>
            <xsl:choose>
                <xsl:when test="not ($value)">
                    <xsl:call-template name="getDatatype">
                        <xsl:with-param name="tagName" select="$tagName"/>
                        <xsl:with-param name="mode" select="$mode"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$value"/>
                </xsl:otherwise>
            </xsl:choose>
        </DIV>

    </xsl:template> <!--  createSingleMLCBlock -->

    <!--***********************************************************************
     * This template generates the necessary Javascript Code for enabling and disabling the
     * corresponding input fields and blocks for translation mode 
     *-->
    <xsl:template name="addJSForSwitchingTranslationMode">
        
        <xsl:variable name="leadingLanguageUpper" select ="translate($leadLangMLC,$alphaLower,$alphaUpper)"/>
		
		<SCRIPT LANGUAGE="JavaScript">
            function changeFieldsForMLC()
            {	
				if (document.getElementById &amp;&amp; 
					<xsl:call-template name="checkAllLangMLC">
						<xsl:with-param name="listOfLanguages" select="$langsMLC"/>
						<xsl:with-param name="addCodeFor" select="'createJSCheckboxCheckCodeForLanguages'"/>
						<xsl:with-param name="isFirst" select="1"/>
					</xsl:call-template>
					)
				{
					<xsl:for-each select="/OBJECT/VALUES/VALUE">
						<xsl:if test="substring-before(@FIELD,concat('_',$leadingLanguageUpper)) != ''">

							document.getElementById("blkDefault<xsl:value-of select="@INPUT"/>").style.display = "none";		
							document.getElementById("blkExtend<xsl:value-of select="@INPUT"/>").style.display = "block";
							<xsl:if test="$mode = $MODE_EDIT">
                                var convertedValue = convertLineFeeds(document.getElementById("<xsl:value-of select="@INPUT"/>").value);
								document.getElementById("blkExtend<xsl:value-of select="@INPUT"/>_Value").innerHTML = convertedValue;
							</xsl:if>
								
							<xsl:variable name="tagName" select="substring-before(substring-after(@INPUT,'_'),concat('_',$leadingLanguageUpper))"/>
							<xsl:call-template name="checkAllLangMLC">
								<xsl:with-param name="listOfLanguages" select="$langsMLC"/>
								<xsl:with-param name="tagName" select="$tagName"/>
								<xsl:with-param name="addCodeFor" select="'createJSEnableCodeForLanguages'"/>
							</xsl:call-template>
						</xsl:if>
   			        </xsl:for-each>					
				}
				else
				{
					<xsl:for-each select="/OBJECT/VALUES/VALUE">
						<xsl:if test="substring-before(@FIELD,concat('_',$leadingLanguageUpper)) != ''">

							document.getElementById("blkDefault<xsl:value-of select="@INPUT"/>").style.display = "block";		
							document.getElementById("blkExtend<xsl:value-of select="@INPUT"/>").style.display = "none";

							<xsl:variable name="tagName" select="substring-before(substring-after(@INPUT,'_'),concat('_',$leadingLanguageUpper))"/>
							<xsl:call-template name="checkAllLangMLC">
								<xsl:with-param name="listOfLanguages" select="$langsMLC"/>
								<xsl:with-param name="tagName" select="$tagName"/>
								<xsl:with-param name="addCodeFor" select="'createJSDisableCodeForLanguages'"/>
							</xsl:call-template>
						</xsl:if>
   			        </xsl:for-each>					
				}
            }

            function convertLineFeeds(valueToConvert)
            {   
                var newLines = /(\r\n|\n\r|\r|\n)/g;
                var convertedString = "&amp;nbsp;";

                if (valueToConvert != "") 
                    convertedString = valueToConvert.replace( newLines, "<br/>" );

                return convertedString;
            }
        </SCRIPT>
		
	</xsl:template> <!-- addJSForSwitchingTranslationMode -->
   	
    <!--***********************************************************************
     * This template iterates over all available multi lingual content languages and add
     * the specific code for the given "addCodeFor" part
     *
     * @param   listOfLanguages  list of all multi lingual content languages
     * @param   tagName          the name tag of the tag without MLC postfix
     * @param   addCodeFor       defines which code part should be generated
     *-->
    <xsl:template name="checkAllLangMLC">
        <xsl:param name="listOfLanguages"/>
		<xsl:param name="tagName"/>
		<xsl:param name="addCodeFor"/>
		<xsl:param name="isFirst" select="0"/>

        <xsl:if test="$listOfLanguages != ''">
			<xsl:variable name="oneLang">
	            <xsl:choose>
	                <xsl:when test="contains($listOfLanguages,',')">
	                    <xsl:value-of select="substring-before($listOfLanguages,',')"/>
	                </xsl:when>
	                <xsl:otherwise>
	                    <xsl:value-of select="$listOfLanguages"/>
					</xsl:otherwise>
	            </xsl:choose>
	        </xsl:variable>
			
			<xsl:if test="$oneLang != ''">
				<xsl:choose>
					<xsl:when test="$addCodeFor = 'createJSCheckboxCheckCodeForLanguages'">
						<xsl:choose>
							<xsl:when test="$isFirst = 1">
							( 
							<xsl:variable name="isFirst" select="0"/>
							</xsl:when>
							<xsl:when test="$isFirst = 0">
							|| 
							</xsl:when>
						</xsl:choose>
						(document.getElementById("checkbox_<xsl:value-of select="$oneLang"/>").checked == true)
						<xsl:if test="$oneLang = $listOfLanguages">
						)
						</xsl:if>
					</xsl:when>
					<xsl:when test="$addCodeFor = 'createJSEnableCodeForLanguages'">
						<xsl:variable name="oneLangUpper" select="translate($oneLang,$alphaLower,$alphaUpper)"/>
						document.getElementById("blkExtend_<xsl:value-of select="$tagName"/>_<xsl:value-of select="$oneLangUpper"/>").style.display = "none";
							
						if (document.getElementById("checkbox_<xsl:value-of select="$oneLang"/>").checked == true)
						{
							document.getElementById("blkExtend_<xsl:value-of select="$tagName"/>_<xsl:value-of select="$oneLangUpper"/>").style.display = "block";
						}
					</xsl:when>
					<xsl:when test="$addCodeFor = 'createJSDisableCodeForLanguages'">
						<xsl:variable name="oneLangUpper" select="translate($oneLang,$alphaLower,$alphaUpper)"/>
						document.getElementById("blkExtend_<xsl:value-of select="$tagName"/>_<xsl:value-of select="$oneLangUpper"/>").style.display = "none";
					</xsl:when>
				</xsl:choose>
			</xsl:if>
				
            <xsl:call-template name="checkAllLangMLC">
                <xsl:with-param name="listOfLanguages" select="substring-after($listOfLanguages,',')"/>
				<xsl:with-param name="tagName" select="$tagName"/>
				<xsl:with-param name="addCodeFor" select="$addCodeFor"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template> <!-- checkAllLangMLC -->
	
</xsl:stylesheet>

