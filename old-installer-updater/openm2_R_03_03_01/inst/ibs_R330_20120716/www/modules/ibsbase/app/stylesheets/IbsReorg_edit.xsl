<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="ibs_helpers.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="mode" select="$MODE_EDIT"/>
    <!-- ************************** VARIABLES END ************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>

    <!-- *************************** MAIN BEGIN **************************** -->
    <xsl:template match="/OBJECT">

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>
            <TR CLASS="infoRow1">
                <TD>
                    Datenbank:
                </TD>
                <TD>
                    Die Datenbank Reorganisation wird direkt in der Datenbank �ber
                    Stored Procedures aufgerufen.<BR/>
                    <U>Dazu sind folgende Schritte auszuf�hren:</U>
                    <UL>
                        <LI>Webserver stoppen</LI>
                        <LI>Query Analyzer oder �hnliches Tool starten um in der
                             Datenbank SQL Anweisung durchzuf�hren</LI>
                        <LI>Optional: Anzeige der Statistik vor der Reorganisation:
                             <CODE>EXEC p_reorgGetStats</CODE></LI>
                        <LI>Start der Reorganisation: <CODE>EXEC p_reorg</CODE></LI>
                        <LI>Optional: Anzeige der Statistik nach Reorganisation:
                             <CODE>EXEC p_reorgGetStats</CODE></LI>
                        <LI>Webserver starten.</LI>
                        <LI>Danach den Punkt "Dateisystem reorganisieren" durchf�hren.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Dateisystem:
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="reorgFileSystem"/>
                        Dateisystem reorganisieren<BR/>
                        
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Es wird das upload/files Verzeichnis nach xmldata.xml
                             Dateien durchsucht, deren zugeh�rige Objekte bereits
                             gel�scht wurden.</LI>
                        <LI>Die entsprechenden xmldata.xml Dateien werden gel�scht.</LI>
                        <LI>Das Verzeichnis der xmldata.xml Datei wird gel�scht,
                             sofern dieses leer ist.</LI>
                    </UL>
                 </TD>
            </TR>

            <xsl:call-template name="infoRow">
                <xsl:with-param name="tagName" select="'filesystem reorg log'"/>
                <xsl:with-param name="label" select="'Letzte Dateisystem Reorganisation'"/>
                <xsl:with-param name="localRow" select="'2'"/>
                <xsl:with-param name="mode" select="$MODE_VIEW"/>
            </xsl:call-template>
        </TABLE>

        <BR/>
        
        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Upload-Verzeichnis:
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="reorgUploadFiles"/>
                        Reorganisation der Upload Files.<BR/><BR/>

                    <INPUT TYPE="CHECKBOX" NAME="reorgUploadFiles_removeXMLDataFiles" VALUE="true">XML Data Files entfernen</INPUT><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Mit Hilfe dieser Funktion werden alle XML Data Files aus den Subverzeichnissen von upload/files gel�scht.</LI>
                    </UL>

                    <INPUT TYPE="CHECKBOX" NAME="reorgUploadFiles_removeEmptyFolders" VALUE="true">Leere Verzeichnisse unter upload/files l�schen.</INPUT><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Mit Hilfe dieser Funktion werden alle leeren Verzeichnissen unter upload/files gel�scht.</LI>
                    </UL>

                    <INPUT TYPE="CHECKBOX" NAME="reorgUploadFiles_setHasFileFlags" VALUE="true">hasFile Flag setzen</INPUT><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Mit Hilfe dieser Funktion wird das hasFile Flag bei allen BusinessObject Instanzen gesetzt.</LI>
                    </UL>
                </TD>
            </TR>              
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Formulare:
                </TD>
                <TD>
<!-- DEL KR 20090823: Because xmldata files are not longer existing this does not make sense
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="rewriteXMLData"/>
                        Daten-Dateien von Formularen wiederherstellen<BR/>

                    Gew�nschte Objekttypen (Typecodes, komma-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="rewriteXMLData_typecodes" SIZE="64"/><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <LI>Es werden die xmldata.xml Dateien aller Formulare der
                         angegebenen Typnamen wiederhergestellt.
                    <LI>Dabei werden die Daten der Formulare erneut gelesen und
                         ins Dateisystem geschrieben. Beachten Sie, dass eine
                         vollst�ndige xmldata Datei erzeugt wird, welche auch
                         die Systeminformationen beinhaltet.
                    <LI>Diese Funktion wird ben�tigt, wenn beim Strukturupdate
                         auf die Systeminformationen zugegriffen werden muss.
                         (Diese gehen durch die Transformation bei einem
                         Strukturupdate verloren!)
                    <LI>Mehrfachnennung ist m�glich. Typnamen sind mit Kommata
                         voneinander zu trennen!

                    <HR SIZE="1"/>
-->

                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="repairObjectref"/>
                        OBJECTREF Felder von Formularen reparieren wiederherstellen<BR/>
                        
                    Gew�nschte Objekttypen (Typecodes, komma-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="repairObjectref_typecodes" SIZE="64"/><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Es werden die OBJECTREF Felder aller Formulare mit den
                             angegebenen Typnames repariert.</LI>
                        <LI>Dabei wird gepr�ft ob OID und NAME und die OBJECTREF
                             Einstellungen mit einem Objekt �bereinstimmen.</LI>
                        <LI>Wird ein Objekt mit gleichem Namen gefunden, aber die
                             OIDs stimmen nicht �berein, wird das gefundene Objekt
                             gesetzt.</LI>
                        <LI>Wird ein Objekt mit der angegeben OID und den OBJECTREF
                             Einstellungen gefunden, aber der Name stimmt nicht
                             �berein, handelt es sich vermutlich um
                             eine �nderung des Namens des zugeordneten Objektes.
                             Es wird das gefundene Objekt neu gesetzt.</LI>
                        <LI>Wird das Objekt nicht gefunden, wird die Zuordnung gel�scht</LI>
                        <LI>Mehrfachnennung bei den Typnamen ist m�glich.
                             Typnamen sind mit Kommata voneinander zu trennen!</LI>
                    </UL>
<!-- DEL KR 20090823: Because xmldata files are not longer existing this does not make sense
                    <HR SIZE="1"/>

                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="syncXMLData"/>
                        Formulare mit Daten aus DB synchronisieren<BR/>
                    
                    Gew�nschte Objekttypen (Typecodes, komma-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="syncXMLData_typecodes" SIZE="64"/><BR/>
                    
                    Gew�nschte OIDs (Zeilenumbruch-getrennt):<BR/>
                    <TEXTAREA NAME="syncXMLData_oids" COLS="64" ROWS="5"/><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <LI>Die Funktion erm�glicht es, Updates direkt per SQL Script
                         in der Datenbank zu machen und diese Daten mit den xmldata.xml
                         Dateien der entsprechenden Objekte zu synchronisieren.
                    <LI>Es werden die Daten der Objekte der angegebenen Objekttypen
                         bzw. der angegebenen OIDs direkt aus der Datenbank gelesen.
                    <LI>Die Daten werden zur�ck in die xmldata.xml Dateien geschrieben.
                    <LI>Mehrfachnennung bei den Typnamen ist m�glich.
                         Typnamen sind mit Kommata voneinander zu trennen!
                    <LI>Tipp: Die gew�nschten OIDs k�nnen mit einer Query aus
                         der DB gelesen werden und per Copy+Paste in das Feld
                         "Gew�nschte OIDs" kopiert werden. In jeder Zeile muss
                         danach genau eine OID stehen.
-->
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Arbeitsbereiche (Privat):
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="setWorkspaces"/>
                        Arbeitsbereiche f�r Benutzer setzen.<BR/>
                        
                    Gew�nschte Benutzernamen (Vollst�ndiger Name, Strichpunkt-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="setWorkspaces_users" SIZE="64"/><BR/>
                        
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Es werden die Arbeitsbereiche (Privat-Ablagen) f�r
                             alle angegebenen Benutzer auf den aktuellen Stand gebracht.</LI>
                        <LI>Werden keine Benutzer oder '*' angegeben, so werden alle
                             alle Benutzer ber�cksichtigt.</LI>
                        <LI>Dabei werden f�r alle Benutzer s�mtliche
                             Arbeitsbereiche importiert.</LI>
                        <LI>Sind die Arbeitsbereiche bereits gesetzt,
                             so erfolgt kein Import.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Module:
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="reloadModules"/>
                        Modulinhalte neu laden.<BR/>
                        
                    Gew�nschte Module (komma-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="reloadModules_modules" SIZE="64"/><BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>Es werden die aktuellen Versionen aller XML, XSLT, etc.
                             Dateien aus den einzelnen Modulen in die Arbeitsversion
                             (Verzeichnis "<CODE>app</CODE>" und Unterverzeichnisse)
                             geladen.</LI>
                        <LI>Dabei werden die Konfigurationsvariablen durch ihre
                             Werte ersetzt.</LI>
                        <LI>Mit "*" werden alle Module neu geladen.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    DBMapping:
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="refreshDBMappings"/>
                        DBMapping Stored Procedures neu erstellen.<BR/>
                        
                    Gew�nschte Objekttypen (Typecodes, komma-getrennt):<BR/>
                    <INPUT TYPE="TEXT" NAME="refreshDBMappings_typecodes" SIZE="64"/><BR/>
                    
                    <INPUT TYPE="CHECKBOX" NAME="refreshDBMappings_createTable" VALUE="true"/>
                        DBMapping Tabelle neu erstellen<BR/>
                        
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                        <LI>F�r die gew�nschten Objekttypen werden die DBMapping
                             Stored Procedures neu erstellt.
                             Die Tabelle bleibt bestehen und die Daten werden nicht
                             neu in die Datenbank geschrieben.</LI>
                        <LI>Wird die Option "DBMapping Tabelle neu erstellen" aktiviert
                             wird die DBMapper Tabelle und die Stored Procedures
                             neu generiert und es werden
                             die Daten der entsprechenden Objekte komplett neu in
                             die Datenbank geschrieben.</LI>
                        <LI>Achtung: Je nach Gr��e der Tabelle ist die Option
                             "DBMapping Tabelle neu erstellen" sehr zeitaufw�ndig.</LI>
                        <LI>Mit "*" werden die Stored Procedures aller
                             Objekttypen neu generiert.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Multilang:
                </TD>
                <TD>
                    <INPUT TYPE="RADIO" NAME="reorgFct" VALUE="reloadMultilangTexts"/>
                        Texte neu laden.<BR/>
                    
                    <U>Beachten Sie bitte die folgenden Hinweise:</U>
                    <UL>
                    	<LI>Mit Hilfe dieser Funktion wird der Resource Bundle Cache
                         	geleert, wodurch Resource Bundles beim n�chsten Zugriff
                         	neu geladen werden.</LI>
                        <LI>Au�erdem wird der SQL und Java Script Preloading Mechanismus
                         	durchgef�hrt, wodurch ebenso die SQL und Java Script Texte
                         	neu geladen werden.</LI>
                    </UL>
                </TD>
            </TR>
        </TABLE>

        <BR/>

        <TABLE class="info" width="100%" cellspacing="0" cellpadding="5">
            <COLGROUP>
                <COL WIDTH="35%" CLASS="name"/>
                <COL WIDTH="65%" CLASS="value"/>
            </COLGROUP>

            <TR CLASS="infoRow1">
                <TD>
                    Als Simulation starten:
                </TD>
                <TD>
                    <INPUT TYPE="CHECKBOX" NAME="isSim" VALUE="true"/>
                </TD>
            </TR>
        </TABLE>

        <xsl:call-template name="createStandardValidationJS"/>

    </xsl:template> <!-- OBJECT -->

</xsl:stylesheet>