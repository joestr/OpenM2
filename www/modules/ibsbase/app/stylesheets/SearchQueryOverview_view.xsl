<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_VIEW"/>
    <xsl:variable name="oid" select="/OBJECT/SYSTEM/OID"/>

    <!-- tokens german -->
    <xsl:variable name="TOK_SELECTSEARCH" select="'Schnellauswahl'"/>
    <xsl:variable name="TOK_SELECT" select="''"/>
    <!-- tokens english -->
    <!--
    <xsl:variable name="TOK_SELECTSEARCH" select="'Select a search query'"/>
    <xsl:variable name="TOK_SELECT" select="'Select ...'"/>
    -->

    <xsl:key name="queryCategories"
             match="/OBJECT/VALUES/VALUE[@FIELD='Searchqueries']/RESULTROW"
             use="RESULTELEMENT[@NAME='category']"/>
    
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
   
    
    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <xsl:call-template name="addStyle"/>
       
        <TABLE class="info" width="100%" CELLPADDING="5" CELLSPACING="0">
             <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <xsl:call-template name="showCopyInfo">
                <xsl:with-param name="label" select="$TOK_SELECTSEARCH"/>
                <xsl:with-param name="value">
                    <SELECT ID="searchlist" NAME="searchlist" SIZE="1"
                        ONCHANGE="top.scripts.showSearch (
                            this.options[this.selectedIndex].value, '{$oid}');">
                        <OPTION STYLE="font-style: italic;" DISABLED="TRUE" SELECTED="TRUE">
                            <xsl:value-of select="$TOK_SELECT"/>
                        </OPTION>
                    </SELECT> 
                </xsl:with-param>
                <xsl:with-param name="localRow" select="'1'"/>
            </xsl:call-template>
                
            <TR CLASS="infoRow1">
                <TD COLSPAN="2">

                  <!-- add the searchqueries -->
                  <xsl:call-template name="createOverview">
                      <xsl:with-param name="data" select="VALUES/VALUE[@FIELD='Searchqueries']"/>
                  </xsl:call-template>
                      
                </TD>
            </TR>
        </TABLE>    
        
    </xsl:template> <!-- OBJECT -->
    <!-- **************************** MAIN END ***************************** -->

            
    <!--***********************************************************************
     * This template adds the CSS style
     *-->               
    <xsl:template name="addStyle">        
        <STYLE>
            .query
            {
                display: block;
                float: left;
                overflow: hidden;
                width: 150px; height: 100px;
                border: 1px solid #E0E0E0;
                background-color: #FFFFFF;
                margin: 2px 2px 2px 2px;
                padding: 2px 2px 2px 2px;
                text-align: center;
            }
            .queryname
            {
                font-size: 10px;
                text-decoration: underline;
                font-weight: bold;
                text-align: center;
                margin-top: 2px;
                margin-bottom: 4px;
            }
            .querydesc
            {
                font-size: 9px;
                font-weight: normal;
                text-align: justify;
            }
            
            .query_in
            {
                border: 1px dashed #000000;
                background-color: #F8F8F8;
                cursor: hand;
                cursor: pointer;
            }
            .query_out
            {
                border: 1px solid #F0F0F0;
            }
            .query_image
            {
                width: 30px;
            }
        </STYLE>                    
    </xsl:template>            
    

    <!--***********************************************************************
     * Create the overview page.
     *
     * @param data the data nodes for the overview        
     *-->               
    <xsl:template name="createOverview">        
    <xsl:param name="data"/>
        
        <SCRIPT LANGUAGE="JavaScript">            
            var selectElem = this.document.getElementById ("searchlist");
        </SCRIPT>                        
        
        <!-- Grouping -->
        <xsl:for-each select="$data/RESULTROW[generate-id(.) = generate-id(
            key('queryCategories', RESULTELEMENT[@NAME='category'])[1])]">

                <xsl:call-template name="showCategory">
                    <xsl:with-param name="data"
                        select="key('queryCategories', RESULTELEMENT[@NAME='category'])"/>
                </xsl:call-template>
        </xsl:for-each>
        
    </xsl:template> <!-- createOverview -->
        
        
    <!--***********************************************************************
     * show a query category.
     *
     * @param data the data for the query category        
     *-->               
    <xsl:template name="showCategory">        
    <xsl:param name="data"/>

        <xsl:variable name="emptyCategory" select="boolean ($data/RESULTELEMENT[@NAME='category'] = '')"/>
                               
        <xsl:if test="not ($emptyCategory)">
            <SCRIPT LANGUAGE="JavaScript">
                var categoryElem = document.createElement ("optgroup");
                categoryElem.label = "<xsl:value-of select="$data/RESULTELEMENT[@NAME='category']" disable-output-escaping="yes"/>";
                selectElem.appendChild (categoryElem);
            </SCRIPT>
        </xsl:if>
                 
        <FIELDSET>
        <xsl:if test="not ($emptyCategory)">
            <LEGEND><xsl:value-of select="$data/RESULTELEMENT[@NAME='category']"/></LEGEND>
        </xsl:if>
        <DIV ALIGN="LEFT">    
            <xsl:for-each select="$data">
                <DIV class="query"
                     ONMOUSEOVER="this.className='query query_in';"
                     ONMOUSEOUT="this.className='query query_out';"
                     ONCLICK="top.scripts.showSearch ('{RESULTELEMENT[@NAME='name']}', '{$oid}');">
                    <xsl:attribute name="TITLE">
                        <xsl:value-of select="RESULTELEMENT[@NAME='name']"/>
                        <xsl:text>
</xsl:text>
                        <xsl:value-of select="RESULTELEMENT[@NAME='description']"/>                                                
                    </xsl:attribute>
                    <IMG SRC="{$layoutPath}images/objectIcons/{RESULTELEMENT[@NAME='icon']}" 
                         CLASS="query_image"/>
                    <DIV CLASS="queryname">
                        <xsl:value-of select="RESULTELEMENT[@NAME='name']"/>
                    </DIV>
                    <DIV CLASS="querydesc">
                        <xsl:value-of select="RESULTELEMENT[@NAME='description']"/>
                    </DIV>
                </DIV>

				<!-- prepare description text -->
				<xsl:variable name="descData" select="RESULTELEMENT[@NAME='description']"/>
				<xsl:variable name="str" select="java:java.lang.String.new (string ($descData))"/>
				<xsl:variable name="desc" select="java:replaceAll ($str, '\n', '\\n')"/>
				
                <SCRIPT LANGUAGE="JavaScript">            
                    var optionElem = document.createElement ("option");
                    optionElem.value = "<xsl:value-of select="RESULTELEMENT[@NAME='name']" disable-output-escaping="yes"/>";					
                    var title = "<xsl:value-of select="$desc" disable-output-escaping="yes"/>";
                    optionElem.title = (title != ' ') ? optionElem.title = title : "";
                    optionElem.innerHTML = "<xsl:value-of select="RESULTELEMENT[@NAME='name']" disable-output-escaping="yes"/>";
                    <xsl:choose>
                    <xsl:when test="$emptyCategory">
                        selectElem.appendChild (optionElem);
                    </xsl:when>
                    <xsl:otherwise>
                        categoryElem.appendChild (optionElem);
                    </xsl:otherwise>
                    </xsl:choose>
                </SCRIPT>                              
            </xsl:for-each>
        </DIV>
        </FIELDSET>
        
    </xsl:template> <!-- createOverview -->                       
                          
</xsl:stylesheet>

