<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="masterd_Container_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->
    
	<xsl:template match="/OBJECTS">
	    <!-- add primary script code: -->
	    <xsl:call-template name="startScripts"/>     
	    <!-- display content: -->
	    <xsl:call-template name="showContent"/>
	    <!-- add final script code: -->
	    <xsl:call-template name="finishScripts"/>
	</xsl:template> <!-- OBJECTS -->
	
	<!-- **************************************************************************
	 * name         showContent
	 * description  Show the container's content.
	-->
	<xsl:template name="showContent">
	
	<DIV ALIGN="CENTER">
	    <BR/>
	    <TABLE CLASS="welcome" BORDER="0" CELLSPACING="0" CELLPADDING="0"
	           FRAME="BOX" RULES="NONE" WIDTH="50%">
	        <TBODY>
	            <TR>
	            <TD>
	            <P><B>Please search for the customers of this company:</B></P>
	            </TD></TR>
		            <TR><TD CLASS="button">
		            <A HREF="javascript:top.showSearch ('masterd_customerSearch', this.containerId);"
		               ONCLICK="buttonPressed (this);">
		                <NOBR>Customer search</NOBR></A>
		            </TD></TR>
	        </TBODY>
	    </TABLE>
	</DIV>
	</xsl:template> <!-- showContent -->
	
</xsl:stylesheet>
