<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <!-- *************************** IMPORT END **************************** -->

    <!--*********************** VARIABLES BEGIN ****************************-->
    <xsl:variable name="includePath">include/</xsl:variable>
    <!--*********************** VARIABLES END ******************************-->

	<xsl:output method="html" encoding="ISO-8859-1"/>
	

	<!-- **************************************************************************
	 * name         startScripts
	 * description  Create start scripts.
	-->
	<xsl:template name="startScripts">
	
	<SCRIPT LANGUAGE="javascript" TYPE="text/javascript"
	        SRC="scripts/scriptDocument.js"></SCRIPT>
	<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
	<![CDATA[
	<!--
	top.showStylesheet (document, "styleWelcome.css");
	
	function buttonPressed (button)
	{
	    button.parentNode.className += '_pressed';
	} // buttonPressed
	//-->
	]]>
	</SCRIPT>
	</xsl:template> <!-- startScripts -->
	
	<!-- **************************************************************************
	 * name         finishScripts
	 * description  Add scripts at the end of the output.
	-->
	<xsl:template name="finishScripts">
	
	<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
	<![CDATA[
	<!--
	//-->
	]]>
	</SCRIPT>
	
	</xsl:template> <!-- finishScripts -->

</xsl:stylesheet>
