Release Notes - openM2 - Version Release 3.4.0
upd_20130118_openm2_R_03_04_00

Allgemeines
===========

Verzeichnisse
-------------
<appDir> ... Applikationsverzeichnis, z.B.: c:\wwwroot\m2
<appUrl> ... Applikations-URL, z.B.: http://m2server:8080/m2
<instDir> .. Installationsverzeichnis, z.B.: upd_20100212_openm2


Tasks
=====
[IBS-647]	Entries in the protocol tab are not shown correctly in all cases
[IBS-828]	Closing text is not shown within pop-up windows
[IBS-831]	containerOid2 is not updated on an object OID change when using the provided stored procedure
[IBS-834]	f_getValuesFromOids - values cannot have more than 18 characters and result starts always with a space
[IBS-835]	Value for Multiselection QUERYSELECTIONBOX field disappears after reloading
[IBS-610]	Try reconnect after loosing database connection in Observer
[IBS-685]	Support type specific fields within link tab
[IBS-704]	Remove deprecated parts from 'BusinessObject'
[IBS-739]	Add the possibility to add credentials to the SMTP server within the configuration
[IBS-781]	Adaption of notification service to update name of received object
[IBS-783]	Highlighning of search fields for fieldref and objectref fields
[IBS-830]	Define OBJECTREF SEARCHROOT via Ext Keys
[IBS-832]	Create a new conf var pointing to the users container within user administration
[IBS-841]	Add the possibility to show the DocumentTemplate link without showing the DOM tree section
[IBS-842]	XSLT library update
[IBS-848]	Update current jTDS database driver to new version
[IBS-850]	Replace string concatenation with StringBuilder usage
[IBS-782]	Creation of method to check group membership of user
[IBS-788]	Subobject creation via ajax
[IBS-838]	Support dates back to 1.1.1753 for field type DATE
[IBS-839]	Add month preview support to Calendar Popup component
[IBS-840]	Confvar for system language dependent standard group names
[IBS-851]	Packaging 3.4.0
[IBS-843]	XSLT library update - Analysis
[IBS-844]	XSLT library update - Implementation

Voraussetzungen
===============

Es werden folgende Pakete vorausgesetzt:
- ibs Release 3.3.1  : bis inkl. upd_20120809_openm2_R331

SQLServer Service Pack 3a:        Um diverse Probleme beim SQL Server zu vermeiden ist sicherzustellen,        dass das Service Pack 3a von SQL Server installiert ist.


Das Updatepaket
===============

Inhalt
------
Das Update beinhaltet sämtliche Änderungen und die zugehörigen
Updatemechanismen, welche für diesen Task relevant sind.

- [IBS-647]	Entries in the protocol tab are not shown correctly in all cases
    + type:		Bug
    + created:	2011-05-19 10:38:32.0
    ---------------------------------
         
	Not all entries are shown correctly in the protocol container when they are expanded through the '+' - Symbol.
	Rendering of entries are wrong in IE6, IE8 and IE8 - Compatible mode
	
	After analysis of this issue 
	>> It is NOT an Internet Explorer problem!!
    
- [IBS-828]	Closing text is not shown within pop-up windows
    + type:		Bug
    + created:	2012-10-02 09:48:37.0
    ---------------------------------
         
	Closing text is not shown within pop-up windows.
	E.g. when opening a document and the config parameter 'Opening in a separate window' is selected.
	The necessary closing text 'Close window...' is not shown and therefore the link to close the window is not shown too.
	Further an JS exception is thrown that a text is not accessable.
    
- [IBS-831]	containerOid2 is not updated on an object OID change when using the provided stored procedure
    + type:		Bug
    + created:	2012-10-03 10:32:52.0
    ---------------------------------
         
	When using the stored procedure 'p_changeOid' the OID within all fields and objects should be change to the new OID.
	Depending on a comment the field 'ibs_object.containerOid2' should be changed with the update trigger on the table ibs_object.
	This is not working in all cases due to some checks and ifs within the trigger for an update of the containerOid2.
    
- [IBS-834]	f_getValuesFromOids - values cannot have more than 18 characters and result starts always with a space
    + type:		Bug
    + created:	2012-10-29 11:40:44.0
    ---------------------------------
         
	Two bugs were found within the provided function 'f_getValuesFromOids'
	1.) The existing function 'f_getValuesFromOids' returns only 18 characters of a resolved value domain value, but entries can have up to 63 characters
	2.) The return value from the function 'f_getValuesFromOids' contains always a space at the beginning of the result
	
    
- [IBS-835]	Value for Multiselection QUERYSELECTIONBOX field disappears after reloading
    + type:		Bug
    + created:	2012-11-14 17:42:45.0
    ---------------------------------
         
	Value for Multiselection QUERYSELECTIONBOX field disappears after reloading.
    
- [IBS-610]	Try reconnect after loosing database connection in Observer
    + type:		Improvement
    + created:	2011-03-10 15:22:22.0
    ---------------------------------
         
	Sometimes the connection to the database gets lost.
	If this occurs within Observer an error message is thrown and the observer stops working.
	-------------------------
	Message from observer 'standard'
	
	**** ERROR: ERROR in THREAD observer 'standard'.ibs.service.observer.ObserverException: Error while loading ObserverJobs: java.sql.SQLException: I/O Error: Connection reset by peer: socket write error (SQLState=08S01) Query: SELECT id, name, created, nextExecution, prevExecution, executionCount, state, retryOnError, retryAttempts, className FROM obs_standard WHERE 0 = 0 AND (state = 1 OR state = 3) AND nextExecution < CONVERT (DATETIME, '19.02.2011 15:42')
	-------------------------
	
	The target of this issue is to find a way that the observer recognizes the connection reset and retries to perform its work.
	The connection reset shall be logged for later traceability.
    
- [IBS-685]	Support type specific fields within link tab
    + type:		Improvement
    + created:	2011-08-02 08:43:17.0
    ---------------------------------
         
	Support type specific fields within link tab
    
- [IBS-704]	Remove deprecated parts from 'BusinessObject'
    + type:		Improvement
    + created:	2011-08-22 10:27:17.0
    ---------------------------------
         
	Remove deprecated and unnecessary parts from the 'BusinessObject' (e.g. Deprecated Stored Procedures)
    
- [IBS-739]	Add the possibility to add credentials to the SMTP server within the configuration
    + type:		Improvement
    + created:	2011-12-22 13:13:50.0
    ---------------------------------
         
	Reported internally after switching office to the new location at 06.12.2011:
	------------------------------------------------------------------------------------------------------
	After swichting the office and the internet provider it is not possible to send e-mails within the local test environment
	When within the same domain it is not a problem to use the SMTP from the internet provider
	Due to the swich we have now XPIRIO as provider and INODE for mails
	Normally this can be solved due to add credentials to the SMTP server
	Within openm2 it is not possible to set credentials for the SMTP server
    
- [IBS-781]	Adaption of notification service to update name of received object
    + type:		Improvement
    + created:	2012-05-30 13:43:28.0
    ---------------------------------
         
	When a object is distributed by the notification service and afterwards the object name is changed, the change has no effect on the displayed name in the reciever' inbox. This improvement provides a method for updating the displayed name so that it is conform to the object name again.
    
- [IBS-783]	Highlighning of search fields for fieldref and objectref fields
    + type:		Improvement
    + created:	2012-06-04 16:55:19.0
    ---------------------------------
         
	The following change on fieldref and objectref input fields was requested:
	
	When a user allocates an object via a fieldref or objectref to an other object, he clicks on the "allocate" link to show the search fields. Despite the input focus jumps on the input field for search strings, some users don't follow it and leave the search field empty. So the input field shall be highlighted in the style of a mandatory field to point the user to this input field.
	
	This change request makes it necessary to pass an additional CSS class to search fields of fieldrefs and objectrefs to hightlight them. This makes it necessary to augment the signature of the templates "m2Dt_objectref_edit", "m2Dt_fieldref_edit" and "createFieldrefStructure" with the CSS Class of the search field.
    
- [IBS-830]	Define OBJECTREF SEARCHROOT via Ext Keys
    + type:		Improvement
    + created:	2012-10-03 09:39:55.0
    ---------------------------------
         
	Currently an OBJECTREF SEARCHROOT is defined via the path.
	
	Example:
	                <VALUE FIELD="Benutzerzuordnung" TYPE="OBJECTREF"
	                       TYPECODEFILTER="User"
	                       SEARCHROOT="#CONFVAR.ibsbase.menuPublic#/user administration/users"
	                       SEARCHRECURSIVE="YES"
	                       DBFIELD="m_user"/>
	
	Within system where the user administration container is positioned within another location or has a different name (e.g. Benutzerverwaltung) this approach does not work without using CONFVARs.
	
	Provide a possibility to define the SEARCHROOT by setting the Ext Key of the search root container.
    
- [IBS-832]	Create a new conf var pointing to the users container within user administration
    + type:		Improvement
    + created:	2012-10-17 11:14:40.0
    ---------------------------------
         
	The path to the users container is sometimes needed within object ref fields having this container as search root.
	
	To support systems having this container within another location than the standard location (Administration/user administration/users) a conf var holding this path should be created.
	
	The long term solution should be to provide the possibility of using extKeys within the SEARCHROOT (see IBS-830)
    
- [IBS-841]	Add the possibility to show the DocumentTemplate link without showing the DOM tree section
    + type:		Improvement
    + created:	2012-12-20 17:15:51.0
    ---------------------------------
         
	Add the possibility to to show the DocumentTemplate link without showing the DOM tree section
    
- [IBS-842]	XSLT library update
    + type:		Improvement
    + created:	2012-12-21 13:20:39.0
    ---------------------------------
         
	An update of the used XSLT library should be performed for beeing able to used advanced features and enhance performance.
	Currently XALAN 2.7.0 is used.
    
- [IBS-848]	Update current jTDS database driver to new version
    + type:		Improvement
    + created:	2013-01-16 09:05:30.0
    ---------------------------------
         
	Currently openM2 uses the jTDS database driver in version 1.2.4
	Based on the homepage the new version of jTDS resolves some bugs and has a better performance.
	Additional information --> http://sourceforge.net/p/jtds/news/2012/10/jtds-jdbc-driver-127-and-130-released/
    
- [IBS-850]	Replace string concatenation with StringBuilder usage
    + type:		Improvement
    + created:	2013-01-17 11:50:44.0
    ---------------------------------
         
	String concatenation with + or += is far slower than String concatenation when using StringBuffer or StringBuilder.
	
	String concatenation with + or += is used within central openM2 methods which frequently used.
	
	Examples:
	* String Helper methods
	* SQL Helper methods
	* Stored Procedure call generation
	* ...
	
	These String concatenations especially when used within loops should be replaced by usage of StringBuilder.
    
- [IBS-782]	Creation of method to check group membership of user
    + type:		New Feature
    + created:	2012-05-31 11:52:17.0
    ---------------------------------
         
	The need arouse to check if a known user (represented by its user object of type "ibs.service.user.User") is member of a user group. The user group is only known by group name.
	
	The task in this issue is to create a method in the user object checking if the user is member in a group specified by the group name.
    
- [IBS-788]	Subobject creation via ajax
    + type:		New Feature
    + created:	2012-06-05 11:37:58.0
    ---------------------------------
         
	Sometimes it is required to create an object as subobject from within another objects edit view.
	
	Create necessary helper functionality based on ajax which can be integrated into an objects edit stylesheet for being able to create subobjects on demand via Ajax.
    
- [IBS-838]	Support dates back to 1.1.1753 for field type DATE
    + type:		New Feature
    + created:	2012-12-05 11:12:56.0
    ---------------------------------
         
	Currently the client side java script validation restricts DATE fields to 1.1.1900.
	This restriction was based on the old database date types which were restricted to 1.1.1900.
	
	The currently used MSSQL type DATETIME supports dates from 1.1.1753 (siehe http://msdn.microsoft.com/en-us/library/ms187819.aspx and http://msdn.microsoft.com/en-us/library/ms187819%28v=sql.90%29.aspx)
	
	So the lower bound restriction for DATE fields should be changed from 1.1.1900 to 1.1.1753
    
- [IBS-839]	Add month preview support to Calendar Popup component
    + type:		New Feature
    + created:	2012-12-17 11:11:58.0
    ---------------------------------
         
	Currently there is only one full month displayed within Calendar Popup.
	
	The Calendar Popup functionality should be extended by month preview handling.
	This handling should display a defined number of preview months additionally to the current month selected within the months selectionbox.
	
	The number of preview months should be configurable so that it can changed by the component using the Calendar Popup.
    
- [IBS-840]	Confvar for system language dependent standard group names
    + type:		New Feature
    + created:	2012-12-20 09:20:03.0
    ---------------------------------
         
	In the past openM2 systems could be installed in different system languages (en/de).
	Since ML version this has feature is not necessary anymore and has been removed.
	Since then there is only one system installation language anymore which is en.
	
	Though there are some olders systems with system installation language de.
	For those systems it is sometimes necessary to know the system language dependent name of standard groups like admin group (de: Administratoren, en: administrators) and all group (de: Jeder, en: all).
	
	So a new CONFVAR holding the system language dependent name of the admin and all group should be created.
    
- [IBS-851]	Packaging 3.4.0
    + type:		Task
    + created:	2013-01-20 19:43:56.0
    ---------------------------------
         
	* Update module versions from 3.3.0/3.3.1 to 3.4.0
	* Create ibs.jar, m2bbd.jar, m2diary.jar, m2doc.jar, m2mad.jar, m2store.jar
	* Create update package 
    
- [IBS-843]	XSLT library update - Analysis
    + type:		Sub-task
    + created:	2012-12-21 13:27:44.0
    ---------------------------------
         
	Analysis concerning possible updates of used XSLT library.
    
- [IBS-844]	XSLT library update - Implementation
    + type:		Sub-task
    + created:	2013-01-08 11:16:38.0
    ---------------------------------
         
	Update XSLT library Xalan 2.7.0 to most recent version 2.7.1.
	
	This has been decided based on the analysis performed within IBS-843.
    

Installation
============

0. Besondere Hinweise
---------------------
- Es sind rechtzeitig die Backups zu starten am Test- und am Produktivserver, da diese erfahrungsgemäß lange brauchen können.
- Die Konfigurationsdateien sollten nochmals getrennt gesichert werden, da sie während des Updates u.U. benötigt werden.


1. Einspielen des WWW-Update-Pakets
-----------------------------------
Das Update-Paket für die WWW-Dateien ist im Installations-Verzeichnis unter
www zu finden.

1a. Web-Server herunterfahren

1b. Entfernen der alten Pakete vom Server:
- Die folgenden Dateien und Verzeichnisse sind zu löschen oder zumindest aus der wwwroot wegzuverschieben (sofern vorhanden):
  + <appDir>\app\install\*
  + <appDir>\WEB-INF\classes\ibs
  + <appDir>\WEB-INF\lib\ibsbase*.jar
  !!! ACHTUNG: !!! Die Datei 'ibsbase_3.3.0_mail.jar' darf NICHT gelöscht werden, da sonst ein Neustart nicht möglich !!!
  + <appDir>\WEB-INF\lib\m2*.jar

1c. Dateien löschen/umbenennen/verschieben:
- Die folgenden Dateien und Verzeichnisse sind zu löschen:
  + (keine)
- Die folgenden Dateien und Verzeichnisse sind umbzubenennen:
  + (keine)
- Die folgenden Dateien und Verzeichnisse sind zu verschieben:
  + <appDir>\conf\ibsbase.xml ==> Sicherungskopie machen
  + !!! <appDir>\conf\ibssystem.xml ==> Sicherungskopie machen !!!
  + !!! <appDir>\conf\observer.xml ==> Sicherungskopie machen !!!

1d. Installation:
- Am Server sind in das Verzeichnis <appDir> die Verzeichnisse aus dem
  folgenden Installationspaket zu kopieren. Dabei können die bestehenden
  Dateien und Verzeichnisse überschrieben werden.
  + <instDir>\www

1e. Konfiguration
- Die Werte der alten CONFVARS sind aus der Sicherungskopie von
  <appDir>\conf\ibsbase.xml wiederherzustellen
  <appDir>\conf\ibssystem.xml wiederherzustellen
  <appDir>\conf\observer.xml wiederherzustellen

1f. !!!!! ZUSÄTZLICHE TÄTIGKEITEN VOR DEM SERVERNEUSTART !!!!!
- [IBS-848]	Update current jTDS database driver to new version 
	Remove the old jTDS database driver (jtds-1.2.4.jar) within WEB-INF/lib

- [IBS-685] Support type specific fields within link tab
    It is necessary to install the views previously, otherwise a start of the application is not possible.
    !!! Change possible CONFVARs within the SQL-Files with the corresponding values from the CONFVAR files !!!
    - ibsbase/install/sql/MS-SQL/update/3.3/U331003v_ContainerViews.sql
    - ibsbase/install/sql/MS-SQL/update/3.3/U331004v_MemberShipViews.sql
    - ibsbase/install/sql/MS-SQL/update/3.3/U331005v_RefContainerViews.sql
    - ibsbase/install/sql/MS-SQL/update/3.3/U331006v_SentObjectContainer_01Views.sql
    Execute the 'SQL_refreshViews.sql' several times (3x) to update all refrencing views.
    
1g. Web-Server neu starten.


2. Java
-------
Es wurden einige Java-Klassen geändert.
Dies betrifft die folgenden Module:
- ibs
- m2bbd
- m2diary
- m2doc
- m2mad
- m2store

2a. Java Klassenpakete
Die Java-Klassen wurden zu einem neuen Gesamtpaket kompiliert und sind in Form
von JAR-Dateien verfügbar.
Eine spezifische Installation dieser Klassen ist nicht erforderlich, sie werden
mit dem Paket automatisch installiert.

2b. Durchführung der automatischen Installation
- Start des WebServers (siehe 1f.).
- Aufruf der Url im Browser.
- Es muss die Meldung kommen, dass sich Ressourcen geändert haben und der Server
  neu zu starten ist.
- Server neu starten.
=> Java-Installation abgeschlossen


3. SQL
------
Sämtliche SQL-Updates erfolgen automatisch im ApplicationInstaller.

Anmerkung: Einspielen potenzieller weiterer Scripts siehe Punkt 6.


4. Typupdate
------------
Die Typupdates erfolgen automatisch im ApplicationInstaller.


5. XML
------
Es wurden einige XML-Dateien geändert, welche aktualisiert werden müssen.
Die Installation erfolgt unter Zuhilfenahme des ApplicationInstallers.

5a. Einloggen
Nach dem Neustart des Servers Einloggen als Administrator.

5b. Durchführung der Installation
- Die Installationsverzeichnisse befinden sich unter <appDir>\app\install
- Kopieren Sie nacheinander folgende URL(s) in die Adressleiste des Browser
  und starten die Installationen durch Aufruf der URL(s)

 <appUrl>/ApplicationServlet?fct=801&pkg=xml_ibsbase_update_20130118

- ACHTUNG: Stellen Sie sicher, dass der Browser die Seite bei jedem Zugriff
           aktualisiert! (F5 für Aktualisieren drücken)
- Am Ende der Installation ist das Bildschirmlog auf Fehler zu kontrollieren.
- Die Installationslogdatei finden Sie zusätzlich unter <appDir>\logs\install
- Ein Installationsvorgang kann unter Umständen lange dauern.


6. Typabhängiges SQL
--------------------
Sämtliche SQL-Updates erfolgen automatisch im ApplicationInstaller.


7. Datenupdates
---------------
Es sind keine weiteren Updates erforderlich.


8. Weitere Arbeiten:
--------------------
Folgende Arbeiten sind noch im Rahmen der Installation durchzuführen:

- [IBS-840]	Confvar for system language dependent standard group names 
	Set system installation language dependent name for administrators group to new CONFVAR "ibsbase.adminGroupName"
	Set system installation language dependent name for all group to new CONFVAR "ibsbase.allGroupName"
 
- [IBS-739] Add the possibility to add credentials to the SMTP server within the configuration
    Delete the file 'ibsbase_3.3.0_mail.jar' within WEB-INF/lib.
