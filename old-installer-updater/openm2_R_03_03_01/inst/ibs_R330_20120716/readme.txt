ibs Release 2.5 - INSTALLATION
================================

Dieses Dokument enth�lt eine Kurzbeschreibung der T�tigkeiten, welche f�r eine
ibs-Installation n�tig sind.


Zu den verwendeten Begriffen:

<m2BaseDir> ... Basis m2-Verzeichnis
                Dieses Verzeichnis ist der vollst�ndige Pfad zum Webverzeichnis
                von m2.
                z.B.: d:\wwwroot\m2

<installDir> .. Basis Installationsverzeichnis
                In diesem Verzeichnis sind alle Dateien enthalten, welche
                w�hrend der Installation ben�tigt werden.
                z.B.: d:\install\ibsR25SQL_2720

<tomcatDir> ... tomcat-Verzeichnis
                Dies ist jenes Verzeichnis, in welches tomcat installiert wurde.
                z.B.: C:\Programme\Apache Group\Tomcat 4.1

<m2Base> ...... Basis m2-Verzeichnis
                Dieses Verzeichnis ist der Name des Webverzeichnisses von m2,
                bildet also den letzten Teil von <m2BaseDir>.
                Nachdem dieses Verzeichnis komplett zusammengestellt ist, kann
                es jederzeit dupliziert und als neue Web-Basis verwendet werden.
                z.B.: m2

<server> ...... Name des m2-Servers
                Dieser Name wird bei der Installation verwendet, um den Server
                zu adressieren.
                z.B.: m2server

<serverUrl> ... URL des Servers, auf welchem m2 l�uft
                Dies ist die URL, unter welcher der Server im Browser
                angesprochen werden kann.
                z.B.: m2server:8080

<m2BaseUrl> ... Basis Url von m2
                Dies ist das (virtuelle) Verzeichnis, unter welchem m2 am
                WebServer angesprochen werden kann. Meist ist dies der Name des
                Verzeichnisses, unter welchem m2 installiert wurde, also
                <m2Base>.
                z.B.: m2


Voraussetzungen:
----------------

Um die Installation ordnungsgem�� durchf�hren zu k�nnen wird vorausgesetzt,
dass die folgende Basissoftware bereits installiert ist:
- Windows 2000 oder XP
- SQL Server 2000 mit Service Pack 3a oder h�her
- Java 1.4.2
- tomcat 4.1


Schritt 0: Entpacken
--------------------

a) Lieferung als ZIP

Liegt die Software in Form von gepackten ZIP-Dateien vor, so m�ssen diese
zuerst entpackt werden, um anschlie�end den Installationsprozess zu beginnen.

Die Auslieferung erfolgt in folgenden Dateien:
- ibsR25SQL_2720.zip ..... Gesamte Installation

Diese Datei ist in ein Verzeichnis <installDir> zu entpacken.

Anschlie�end kann bereits mit der Installation begonnen werden.


b) Lieferung auf CD

Bei einer Lieferung auf CD sind die Dateien bereits entpackt im Verzeichnis
ibsR25SQL_2720 zu finden:
- ibsR25SQL_2720\www ..... WWW Dateien
- ibsR25SQL_2720\sql ..... Datenbankinstallation

Das Hauptverzeichnis ibsR25SQL_2720 sollte als Installationsverzeichnis
<installDir> auf die Festplatte kopiert werden.

Achtung: Danach sollte der Schreibschutz auf allen enthaltenen Dateien
aufgehoben werden, um unerw�nschte Seiteneffekte auszuschlie�en.


Schritt 1: Webinstallation
--------------------------

1a) Transfer
Das gesamte Webverzeichnis (<installDir>/www) ist auf die Zielmaschine �ber
das bereits bestehende Webverzeichnis <m2BaseDir> zu kopieren.

1b) Konfiguration ibs
In <m2BaseDir>/conf/ibs.xml sind die Konfigurationswerte einzustellen.

In <m2BaseDir>/WEB-INF/web.xml sind gegebenenfalls Parameter einzustellen.


Schritt 2: Java-Installation
----------------------------

Die Java-Klassen sind durch die Konfiguration des WebServers bereits komplett
einsatzbereit. Eine weitere Installation braucht nicht mehr zu erfolgen.


Schritt 3: Datenbankinstallation oder -update
---------------------------------------------

3a) Voraussetzungen Client
Am Client, von welchem aus die Installation erfolgt, muss Java installiert
sein, um die Installation durchf�hren zu k�nnen.
Es wird empfohlen, einen WindowsXP-Client zu verwenden, da hier die beste
Kompatibilit�t der Batch-Dateien gew�hrleistet ist.

3b) Vorbereitung der Konfigurationsdateien
Die folgenden Dateien m�ssen an die aktuelle Installationsumgebung angepasst
werden.

<installDir>\sql\conf\conf.bat
javaClasses ... Pfad zu den Standard-Java-Klassen
dispType ...... Art der Anzeige/Logging der ausgef�hrten Scripts
                dispno .... keine Anzeige
                dispone ... Anzeige der ersten Zeile jedes Statements (default)
                dispfull .. Anzeige des gesamten Statements
                Die Auswahl dispfull ist lediglich f�r Debugging-Zwecke
                vorgesehen, ansonsten sollte sie aufgrund der generierten
                Datenmenge nicht verwendet werden.
dbServer ...... Name des Servers mit der SQLServer-Datenbank
dbName ........ Name der Datenbank, in welcher m2 installiert werden soll.
                Diese muss bereits vorhanden sein.
dbType ........ Art der Datenbank, in welcher m2 installiert werden soll.
                MS-SQL, ORACLE, DB2, etc. Dieser Name entspricht dem Verzeichnis
                in den einzelnen Modulen, in welchem die Datenbankdateien
                enthalten sind.
dbUsername .... Name des Benutzers
dbPassword .... Kennwort f�r den Benutzer

<installDir>\sql\conf\installConfig.sql
Die folgenden Strings m�ssen durch die entsprechenden Realwerte ersetzt werden:
#DOMAINNAME# ...... Name der zu installierenden Dom�ne z.B. 'm2Dom1'
#ABSBASEPATH# ..... absoluter Pfad zum Webverzeichnis, also '<m2BaseDir>/'
#HOMEPAGEPATH# .... Einstiegspfad im Browser , z.B. '/m2/'
#UPLOADWWWPATH# ... Root f�r Uploadpfad im WWW, z.B. '/m2/'
#CUSTOMERNAME# .... Ihr Name: 'Kunde GmbH'
#SYSTEMNAME# ...... Name des Systems, z.B. 'm2Server1'
#BUILD_NUMBER# .... Buildnummer, z.B. '2720'
                    (bereits voreingestellt)
#VERSION_NAME# .... vollst�ndiger Versionsname, z.B.
                    'ibs Release 2.5 Beta2'
                    (bereits voreingestellt)
#LANGUAGE# ........ m2 Systemsprache, z.B. 'german'
                    (bereits voreingestellt)

In dieser Datei sind auch ein paar Beispielkonfigurationen enthalten.

3c) Durchf�hrung der Datenbankinstallation oder des -updates
Aufruf des Scripts
<installDir>\sql\bin\install.bat
oder
<installDir>\sql\bin\update.bat

Diese Datei f�hrt automatisch alle Installations- bzw. Updatescripts
entsprechend der vorgegebenen Konfiguration aus und erstellt ein entsprechendes
Log-Verzeichnis.

3d) �berpr�fung
Nach Abschluss der Installation k�nnen die Logs (in <installDir>\sql\log) recht
einfach �berpr�ft werden, indem mittels textueller Suche nach "==> SQL"
gesucht wird. An diesen Stellen stehen dann evtl. aufgetretene Fehler.
Achtung: Fehler in Views k�nnen vorerst ignoriert werden. Diese sind erst
nach Abschluss der Installation (siehe 4d) relevant.

Tritt eine Meldung auf, dass die Prozedur p_dropProc nicht gefunden wurde, so
ist diese zu ignorieren. Ansonsten d�rften hier keine Fehlermeldungen vorkommen.


Schritt 4: Start der Applikation
--------------------------------

4a) Start von tomcat (Server):
<tomcatDir>bin\startup.bat

4b) Einstieg im Browser (Client):
Die folgende Url im Browser aufrufen:
http://<serverUrl>/<m2BaseUrl>/

Nach dem Start des Servers werden die Module initialisiert und gegebenenfalls
ben�tigte Dateien (JAR libraries, XML Dateien, HTML Dateien, etc.) installiert.
Es werden entsprechende Fortschrittsmeldungen ausgegeben.
Werden JAR-Dateien installiert, so ist danach der WebServer neu zu starten.
Dies ist notwendig, da die Java-Engine im tomcat neu installierte JAR-Pakete
nicht automatisch erkennt.

Da in den Multilingualscripts einige Tags verwendet werden, welche in dieser
Version m�glicherweise nicht installiert sind, k�nnen nach dem Start der
Applikation einige diesbez�gliche Fehlermeldungen kommen. Diese treten
ausschlie�lich nach einem Neustart des WebServers auf und k�nnen ignoriert
werden. (Mit Refresh im Browser kann man sie sofort nach erfolgtem Start
wegbringen. Vorher aber warten bis die Seite im Browser komplett geladen ist.)

4c) XML-Installation (Client):
Zuerst mit dem Benutzer "Administrator" in die Standarddom�ne einloggen.
Als Kennwort ist "A.dmin#+" die Standardeinstellung.
�berpr�fen, ob die Installation bis hierhin funktioniert hat.
Zu diesem Zeitpunkt m�ssen dann auch die XML-Dateien installiert werden.
Die Dateien sind zu finden unter
  - <m2BaseDir>/app/install/xml_ibsbase

4d) Views (Server):
Zum Abschluss m�ssen noch einmal die Views eingespielt werden.
Dies erfolgt durch Aufruf des Scripts
<installDir>\sql\bin\installviews.bat

Diese Datei f�hrt automatisch alle View-Installationsscripts entsprechend der
vorgegebenen Konfiguration aus und erstellt ein entsprechendes Log-Verzeichnis.
Hier ist eine �berpr�fung analog 3d) durchzuf�hren.


Schritt 5: Abschluss
--------------------

Damit ist die Installation abgeschlossen und die Applikation kann in Betrieb
gehen.
