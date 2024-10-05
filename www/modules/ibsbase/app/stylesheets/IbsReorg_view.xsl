<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>

    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_VIEW"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <xsl:call-template name="showInfo"/>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'filesystem reorg log'"/>
                <xsl:with-param name="label" select="'Letzte Dateisystem Reorganisation'"/>
                <xsl:with-param name="localRow" select="'2'"/>
            </xsl:call-template>

        </TABLE>

        <BR/>

        <!-- display the log is applicable -->
        <xsl:call-template name="showLog"/>

    </xsl:template> <!-- OBJECT -->

    <!--***********************************************************************
     * This some common info.<BR>
     *-->
    <xsl:template name="showInfo">

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <TR CLASS="infoRow2">
                <TD CLASS="value" ALIGN="CENTER">
                    <IMG src="{$layoutPath}images/messages/info.gif" BORDER="0" ALIGN="ABSMIDDLE"/>
                    Zum Aufruf der Verwaltungsfunktionen klicken sie bitte auf <B>Bearbeiten</B>!
                </TD>
            </TR>
        </TABLE>

    </xsl:template> <!-- showInfo -->

    <!--***********************************************************************
     * This template displays the log of the last action.<BR>
     *-->
    <xsl:template name="showLog">
    
        <xsl:if test="VALUES/VALUE[@FIELD='Log']">
    
            <SCRIPT LANGUAGE="JavaScript">

                function go (elemId)
                {
                    document.getElementById (elemId).scrollIntoView (true);
                }

                function show (oid)
                {
                    var isLinkinNewWindow = false;
                    var window;

                    if (document.getElementById ('isShowInNewWindow') != null)
                        isLinkinNewWindow = document.getElementById ('isShowInNewWindow').checked;

                    if (isLinkinNewWindow)
                    {
                        window = top.callUrl (666, "&amp;frame=true&amp;dom=1&amp;oid=" + oid, oid, "external",
                            "toolbar=yes,scrollbars=yes,directories=no,menubar=yes,resizable=yes,screenX=0,screenY=0");
                        window.focus();
                    }
                    else
                    {
                        top.showObject (oid);
                    }
                }

                function displayLog ()
                {
                    document.getElementById ("log").style.display = 'inline';
                    document.getElementById ("displayLogButton").value = 'Log ausblenden';
                    document.getElementById ("displayLogButton").onclick = hideLog;
                }

                function hideLog ()
                {
                    document.getElementById ("log").style.display = 'none';
                    document.getElementById ("displayLogButton").value = 'Log der letzten Aktion anzeigen';
                    document.getElementById ("displayLogButton").onclick = displayLog;
                }
            </SCRIPT>

            <FIELDSET STYLE="border: 1 solid #AAAAAA;">
            <LEGEND CLASS="name">Log</LEGEND>

                <DIV ALIGN="LEFT">
                    <INPUT ID="isShowInNewWindow" TYPE="CHECKBOX" CHECKED=""/> Links in neuem Fenster öffnen
                </DIV>

                <DIV ID="displayLog" CLASS="name" ALIGN="CENTER" STYLE="display:none">
                    <INPUT ID="displayLogButton" TYPE="BUTTON" ONCLICK="displayLog ();" VALUE="Log der letzten Aktion anzeigen"/>
                    <BR/><BR/>
                </DIV>

                <DIV ID="log_loading" ALIGN="CENTER">
                    <IMG src="{$layoutPath}images/menu/menu_loading.gif" BORDER="0" ALIGN="ABSMIDDLE"/> loading  ...
                </DIV>

                <DIV ID="log" CLASS="value" ALIGN="LEFT">
                </DIV>
            </FIELDSET>

            <SCRIPT LANGUAGE="JavaScript">

                if (typeof isAfterReorg == "undefined" || !isAfterReorg)
                {
                    var logItem = document.getElementById ("log");
                    if (logItem)
                    {
                        var log = '<xsl:value-of select="VALUES/VALUE[@FIELD='Log']"/>';
                        logItem.innerHTML += log;

/* BB20070829: does not work
                        var logArray = log.split ("&lt;BR/&gt;");
                        logItem.innerHTML = "Generate " + logArray.length + " log lines...&lt;BR/&gt;";

                        for (var i = 0; i &lt; logArray.length; i++)
                        {
                            logItem.innerHTML += (logArray[i] + '&lt;BR/&gt;');
                        } // for
*/
                    } // if
                } // if
                document.getElementById ("log_loading").style.display = 'none';
            </SCRIPT>


        </xsl:if>

    </xsl:template> <!-- showLog -->


</xsl:stylesheet>
