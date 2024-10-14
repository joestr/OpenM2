<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:java="http://xml.apache.org/xalan/java">

	<!-- The Java Multilanguage Provider -->
	<xsl:variable name="provider" select="java:ibs.ml.MultilingualTextProvider.new ()"/>
	
	<!--***********************************************************************
     * This template retrieves a message from a resource bundle for the given key
     * Based on the given parameters, it calls the getXsltMessage(provider, bundle, key, arguments) 
     * or the getXsltMessage(provider, bundle, key) method.
     *
     * @param   key     	the lookup key for the wanted entry
     * @param   bundle  	the bundle file in which the entry should be 
     * @param 	arguments	optional: the MessageArguments for the Message Format
     *-->
	<xsl:template name="getMultilingualMessage">
	    <xsl:param name="key"/>
		<xsl:param name="bundle"/>
		<xsl:param name="arguments" select="''"/>
		<xsl:param name="disabledOutputEscaping" select="false()"/>
		
		<xsl:variable name="modifiedKey">
			<select>
				<xsl:call-template name="replaceSubstring">
					<xsl:with-param name="text" select="$key"/>
				</xsl:call-template>
			</select>
		</xsl:variable>
		
		<xsl:choose>
		    <xsl:when test="$disabledOutputEscaping">
				<xsl:choose>
					<xsl:when test="$arguments != ''">
						<xsl:value-of select="java:getXsltMessage($provider, $bundle, $modifiedKey, $arguments)" disable-output-escaping="yes"/> 
				    </xsl:when>
				    <xsl:otherwise>
				    	<xsl:value-of select="java:getXsltMessage($provider, $bundle, $modifiedKey)" disable-output-escaping="yes"/> 		    	
				    </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$arguments != ''">
                        <xsl:value-of select="java:getXsltMessage($provider, $bundle, $modifiedKey, $arguments)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getXsltMessage($provider, $bundle, $modifiedKey)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
	</xsl:template>
	
	<!--***********************************************************************
     * This template retrieves a text from a resource bundle for the given key
     * It calls the getXsltText method.
     *
     * @param   key     	the lookup key for the wanted entry
     * @param   bundle  	the bundle file in which the entry should be 
     *-->
	<xsl:template name="getMultilingualText">
	    <xsl:param name="key"/>
		<xsl:param name="bundle"/>
		
		<xsl:variable name="modifiedKey">
			<select>
				<xsl:call-template name="replaceSubstring">
					<xsl:with-param name="text" select="$key"/>
				</xsl:call-template>
			</select>
		</xsl:variable>
		
	    <xsl:value-of select="java:getXsltText($provider, $bundle, $modifiedKey)"/>    
	</xsl:template>
	
	<!--***********************************************************************
	* This template takes a string and replaces all occurrences
	* of a specified substring with another substring
	* 
	* @param text				The text containing the substring which has to be replaced
	* @param substringToReplace	The substring which has to be replaces. Default: ' '
	* @param withSubstring		The new substring replacing the old one. Default: '_'
	*-->
	<xsl:template name="replaceSubstring">
		<xsl:param name="text"/>
		<xsl:param name="substringToReplace" select="' '"/>
		<xsl:param name="withSubstring" select="'_'"/>
		<xsl:choose>
			<xsl:when test="contains($text, $substringToReplace)">
				<xsl:value-of select="substring-before($text, $substringToReplace)"/>
				<xsl:value-of select="$withSubstring"/>
				<xsl:call-template name="replaceSubstring">
					<xsl:with-param name="text" select="substring-after($text, $substringToReplace)"/>
					<xsl:with-param name="substringToReplace" select="$substringToReplace"/>
					<xsl:with-param name="withSubstring" select="$withSubstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>