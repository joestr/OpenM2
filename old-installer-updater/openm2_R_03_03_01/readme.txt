In diesem Paket sind mehrere Builds für die Installation zusammengefasst.

Folgende Inhalte sind verfügbar:
conf ... Konfiguration
inst ... Installationspaket
doc .... JavaDoc Dokumentation
src .... Java Quellcodes


Installationspaket
==================

Dieses enthält die verschiedenen Installationspakete.

Für die Durchführung der Installation siehe -> Installation.


Dokumentation
=============

Die Dokumentation beinhaltet die JavaDoc-Dokumentation zur Applikation.

Jedes JavaDoc-Paket kann über die darin vorhandene Datei index.html
aufgerufen werden.
Der Einstieg in die Dokumentation erfolgt mittels index.html im
Dokumentationsverzeichnis.


Quellcodes
==========

Das Verzeichnis src beinhaltet für alle installierten Features die
Java-Sourcen.


Installation
============

1.) Datenbankanlage
Anlegen einer leeren Datenbank auf dem Datenbankserver, sofern noch keine
entsprechende Datenbank vorhanden ist.


2.) Installationspaket auf Server kopieren
Umkopieren des gesamten Installationpaketes um die Installation am Server
durchzuführen 


3.) Konfiguration 
Für die Installation sind zuerst die Konfigurationsdateien an die Umgebung
anzupassen:
- conf/conf.bat ..... übernimmt die Steuerung des Installationsvorgangs
   Folgende Punkte sind auf jeden Fall zu überprüfen:
    * dbServer ...... Name des Datenbankservers
    * dbName ........ Name der Datenbank welche im Schritt 1.) angelegt wurde
    * dbUsername .... Benutzer für DB-Verbindung
    * dbPassword .... Password für DB-Verbindung
    * wwwDir ........ WEB-Verzeichnis des Web Servers

- conf/installConfig.sql .. für die Inhalte der Systemtabellen
   Folgende Puntke sind auf jeden Fall zu überprüfen:
    * @c_domainName ..... Name der Domain (eventuell zu ändern)
    * @c_absBasePath .... Gleicher Pfad wie bei wwwDir


4.) Erzeugen des WWW-Verzeichnisses
Aufruf des folgenden Scripts:
- createwww.bat ......... Erzeugen des www-Verzeichnisses. Im Verzeichnis
                          %wwwDir% wird der www-Inhalt erzeugt. Hier muss
                          dann lediglich noch die Konfiguration erfolgen.

  Folgende Punkte sind hier anzupassen:
   ** ibsbase.xml **
      - Anpassung der Sprache:
        <confvalue name="lang">en</confvalue>
      - Anpassung der Menüeinträge:
        <confvalue name="menuPublic">Administration</confvalue>
        <confvalue name="menuPrivate">Private</confvalue>

   ** ibssystem.xml **
      - Anpassung des Servereintrages:
        <server name="*" port="80" sslname="*" sslport="443" ssl="false"/>
      - Anpassung der SSI-URL:
        <ssiurl value="http://localhost:80/openm2/app/include/"/>
      - Anpassung des SMTP-Servers:
        <smtpserver name="mail.trinitec.at"/>
      - Anpassung der Datenbankverbindung:
        <database jdbcdriverclass="com.inet.tds.TdsDriver"
                  jdbcconnectionstring="jdbc:inetdae7a:localhost:1433"
                  user="sa" password="sa"
                  sid="casemmt"
                  type="sqls65"
                  logintimeout="45"
                  querytimeout="120"/>
      - Anpassung des TraceServers:
        <traceserver name="localhost" port="1734" active="true"
                       path="c:\wwwroot\openm2\logs\tracer"
                       password="myPwd73"/>
    
   ** observer.xml **
      - Anpassung der Base Settings:
        <BASE>
            .....
            <DOMAIN>openm2</DOMAIN>
            .....
        </BASE>
      - Anpassung der Notification Settings:
        <NOTIFICATION>
            <SMTPSERVER>mail.trinitec.at</SMTPSERVER>
            <RECEIVER>rburgermann@trinitec.at</RECEIVER>
            <SENDER>m2Observer@trinitec.at</SENDER>
            <SUBJECT>CaseMmt Observer Errormessage</SUBJECT>
        </NOTIFICATION>
      - Anpassung der Logging Settings:
        <LOGGING>
            <LOGDIR>c:\wwwroot\openm2\logs\observer</LOGDIR>
        </LOGGING>
      - Anpassung der M2Connection Settings:
        <M2CONNECTION>
            <!-- type can be: ASP(=default)|SERVLET -->
            <M2TYPE>SERVLET</M2TYPE>
            <!-- name can be: localhost (=default) or any url to m2-server -->
            <M2SERVER>localhost:80</M2SERVER>
            <!-- name can be: any url-subpath; default=/m2/app -->
            <M2APPPATH>/openm2/</M2APPPATH>
            <!-- domain can be: any m2-domain-id; default=1 -->
            <M2DOMAIN>1</M2DOMAIN>
            <M2USERNAME>Administrator</M2USERNAME>
            <M2PASSWORD>A.dmin#+</M2PASSWORD>
            <!-- timeout can be: any value in milliseconds (gt 0); default=30000 -->
            <M2TIMEOUT>300000</M2TIMEOUT>
        </M2CONNECTION>
      - Anpassung der Authentication Settings:
        <AUTHENTICATION>
            <DOMAIN>none</DOMAIN>
            <USERNAME>none</USERNAME>
            <PASSWORD>none</PASSWORD>
        </AUTHENTICATION>      


5.) Webverzeichnis am Web Server bekannt machen
Die Applikation muss dem WebServer bekannt gemacht werden.
Als Beispiel dient hier die Konfiguration der Test VMWare für einen Tomcat. 
Die Konfiguration am Webserver kann jedoch pro verwendetem Webserver 
unterschiedlich sein und muss dementsprechend angepasst werden.

Unter dem folgenden Verzeichnis muss ein OPENM2.XML File liegen:
    \Apache Software Foundation\Tomcat 6.0\conf\Catalina\localhost

Der Inhalt dieses Files sieht folgendermaßen aus:
    <Context path="/openm2" docBase="c:\wwwroot\openm2"
        debug="0" privileged="true">
    
    <!-- Uncomment this Valve to limit access to the Admin app to localhost
    for obvious security reasons. Allow may be a comma-separated list of
    hosts (or even regular expressions).
    <Valve className="org.apache.catalina.valves.RemoteAddrValve"
    allow="127.0.0.1"/>
    -->
    
    <Logger className="org.apache.catalina.logger.FileLogger"
          prefix="localhost_elak_log." suffix=".txt"
          timestamp="true"/>
    
    </Context>


6.) Initialisierung der Applikation:
Anschließend ist die Applikation im Browser über Link 'http://localhost/openm2/' 
zu starten. (Gilt wenn am Webserver als Applikationpfad OPENM2 eingetragen wurde)
Hier muss am ende die Meldung kommen, dass sich notwendige Dateien geändert
haben und der Server neu gestartet werden muss.

Hier ist der WebServer herunterzufahren und NICHT NEU ZU STARTEN.

7.) Datenbankinstallation
Anschließend erfolgt die Datenbankinstallion mit Aufruf des Scripts:
- install.bat ............. Datenbankinstallation

Dieses Script ist ZWEIMAL auszuführen, damit alle notwendige Datenbankobjekte
richtig angelegt werden. Notwendig da es innerhalb der Installation mehrere
Abhängigkeiten gibt.

Danach kann der WebServer und die Applikation wieder gestartet werden.
Einstieg mit Administrator/A.dmin#+

8.) XML-Installation
Es sind einige XML-Komponenten zu installieren.
Im Applikationsverzeichnis befindet sich ein Verzeichnis app\install.
In diesem sind mehrere Unterverzeichnisse "xml_*" zu finden. Diese sind nun
der Reihe nach zu installieren:
- xml_ibsbase
- xml_m2store
- xml_m2version

Nach der Anmeldung in der Applikation sind folgende URLs auszurufen:
- http://localhost/openm2/ApplicationServlet?fct=801&pkg=xml_ibsbase
- http://localhost/openm2/ApplicationServlet?fct=801&pkg=xml_m2version
- http://localhost/openm2/ApplicationServlet?fct=801&pkg=xml_m2store

Hier ist der WebServer herunterzufahren und NICHT NEU ZU STARTEN.

9.) Abschluss der DB-Installation
- installviews.bat ........ Installation der Views; ist aufzurufen nach der
                            Installation der xml-Dateien

Sicherheitshalber kann dieses Script auch ZWEIMAL ausgeführt werden, wäre hier
aber nicht unbedingt notwendig.

Danach kann der WebServer und die Applikation wieder gestartet werden.
Die Applikation kann dann verwendet werden.

10.) Zusätzliche Schritte nach der Installation
- Erstellung des Standardlayouts für EVN Macedonia
  Es ist innerhalb der Applikation ein neues Layout anzulegen
    Name des Layouts: evnmak
    Beschreibung: Standardlayout for EVN Macedonia
    Standardlayout: JA
    
  Danach ist der Webserver neu zu starten um das neue Layout in der Applikation
  verfügbar zu machen.  
  
  Auf Wunsch kann der Administration nun auf das neue Layout umgestellt werden.
