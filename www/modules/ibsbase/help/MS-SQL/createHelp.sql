/**************************************************************************
 * Install Helpstructure.
 *
 * @version     1.01.0001, 990804
 *
 * @author      Agrinz Astrid (AA)  000606
 *   Old Helpstructure is deleted before new Helpstructure is created.
 *   U can use this file als updatescript as well.
 *   Before execution of this file:
 *      Edit help path.
 *      Edit name of the domain.
 *
 * <DT><B>Updates:</B>
 **************************************************************************/

-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š

-- don't show count messages:
SET NOCOUNT ON
GO

-- PRINT "vor Prc"
if exists (select * from sysobjects where id = object_id('dbo.p_retrieveVariables') and sysstat & 0xf = 4)
	drop procedure dbo.p_retrieveVariables
GO
create procedure p_retrieveVariables (
  --- setting debug to "true" results some debugging messages
  --- set debug to _whatever_ for removing the messages
      @debug varchar(5) OUTPUT,
  -- declare constants:
      @l_domainName   NAME OUTPUT,
      @l_helpPath     VARCHAR (255) OUTPUT,
  -- declare constants:
      @c_TVHelpContainer	 TVERSIONID OUTPUT,
      @c_TVHelpObject 	 TVERSIONID OUTPUT,
      -- declare variables:
      @domainPosNoPath POSNOPATH OUTPUT,
      @tmpUrl VARCHAR(255) OUTPUT,
      @retVal INT OUTPUT,
      @userId USERID OUTPUT, 
      @op RIGHTS OUTPUT,
      @public OBJECTID OUTPUT, 
      @public_s OBJECTIDSTRING OUTPUT,
      @oid_s OBJECTIDSTRING OUTPUT

)
AS
     -- get configuration for installation  (is set in file installConfig.sql)
     /*******************CHANGEABLE CODE BEGIN****************************/
     SELECT @l_domainName = 'ibs',
     		@l_helpPath = "http://m2test/m2/help/",
     		@debug = "false",
    /******************CHANGEABLE CODE END*******************************/
    -- set constants
     		@c_TVHelpContainer = 0x01017f01,    -- HelpContainer
            @c_TVHelpObject = 0x01017f11        -- HelpObject
    -- print "Leaving Procedure _retrieveVariables_..."
    -- get domain data:
    SELECT  @userId = d.adminId, @public = d.publicOid
    FROM    ibs_Domain_01 d, ibs_Object o
    WHERE   d.oid = o.oid
    AND o.name LIKE @l_domainName
    EXEC p_byteToString @public, @public_s OUTPUT

    if @debug = "true" print "**retrieving oid_s"
    declare @oid objectid
    -- get oid for help (if possible)
    select @oid = oid from ibs_object where name like 'hilfe'
    EXEC p_byteToString @oid, @oid_s OUTPUT
GO


-------------------------------------------------------------------------------------------
if exists (select * from sysobjects where id = object_id('dbo.p_retValue') and sysstat & 0xf = 4)
	drop procedure dbo.p_retValue
GO
 create procedure p_retValue (
     @input varchar (255),
     @oid_s OBJECTIDSTRING output
)
as
    DECLARE @oid OBJECTID

    --- get oid from desired object
    select @oid = oid
    FROM ibs_Object
    WHERE name = @input
    --- convert oid to oid_s

    EXEC p_byteToString @oid, @oid_s OUTPUT

    -- print "Leaving Procedure _retValue_..."
 GO

-- print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

Print "delete old helpstructure..."
-- delete old helpstructure
    -- helpfiles
    DELETE ibs_Object WHERE tVersionId = @c_TVHelpObject
    -- helpcontainer
    DELETE ibs_Object WHERE tVersionId = @c_TVHelpContainer

-- show domain info
DECLARE @info   VARCHAR (255)
SELECT @info = "Creating HELP-system for domain: " + @l_domainName
PRINT @info

if @debug = "true" print "declare variables..."

DECLARE
-- declare variables:

   @1  OBJECTIDSTRING,	    -- Willkommen im System		ABLAGE
        @2  OBJECTIDSTRING,     -- Das System
        @3  OBJECTIDSTRING,	    -- Neu im Sytem		ABLAGE
            @4  OBJECTIDSTRING,     -- Was ist neu in dieser Release?
        @5  OBJECTIDSTRING,	    -- Die ersten Schritte	ABLAGE
            @6  OBJECTIDSTRING,     -- Das Login
            @7  OBJECTIDSTRING,     -- Die Willkommensseite
        @8  OBJECTIDSTRING,     -- Die Hilfe zur Hilfe	ABLAGE
            @9  OBJECTIDSTRING,     -- Wie bediene ich die Hilfe?
            @10 OBJECTIDSTRING,     -- Wie finde ich was?
    
    @11 OBJECTIDSTRING,     --  Grundlegendes
		@12  OBJECTIDSTRING,	    -- 	Ansichten im System	ABLAGE		
			@13  OBJECTIDSTRING,	    -- 	Welche Ansichten gibt es?
			@14  OBJECTIDSTRING,	    -- 	Gruppe
			@15  OBJECTIDSTRING,	    -- 	Privat
		@19  OBJECTIDSTRING,	    -- 	Standardordner	ABLAGE		
			@20  OBJECTIDSTRING,	    -- 	Welche Ordner gibt es im Basissystem?
		@21  OBJECTIDSTRING,	    -- 	Basisbegriffe	ABLAGE		
			@22  OBJECTIDSTRING,	    -- 	Die wichtigsten Begriffe
		@23  OBJECTIDSTRING,	    -- 	Geschäftsobjekte	ABLAGE		
			@24  OBJECTIDSTRING,	    -- 	Was ist ein Geschäftsobjekt?
				@25  OBJECTIDSTRING,	    -- 	Welche Objekte gibt es?
				@16  OBJECTIDSTRING,	    -- 	Sichten auf ein Objekt	ABLAGE		
				@17  OBJECTIDSTRING,	    -- 	Welche Sichten bieten die Reiter?
				@18  OBJECTIDSTRING,	    -- 	Aufteilung der Fenster
		@26  OBJECTIDSTRING,	    -- 	Navigieren im System	ABLAGE		
			@27  OBJECTIDSTRING,	    -- 	Wie gelange ich zu den Objekten und Ablagen?
		
		@29  OBJECTIDSTRING,	    -- 	Grundfunktionen	ABLAGE		
			@30  OBJECTIDSTRING,	    -- 	Welche Funktionen gibt es?
			@31  OBJECTIDSTRING,	    -- 	Die Funktionsleiste
			@32  OBJECTIDSTRING,	    -- 	Die Arbeit mit Listen
			@33  OBJECTIDSTRING,	    -- 	Lesen von Objekten
			@34  OBJECTIDSTRING,	    -- 	Erstellen von Objekten
			@35  OBJECTIDSTRING,	    -- 	Bearbeiten von Objekten
			@36  OBJECTIDSTRING,	    -- 	Löschen von Objekten
			@37  OBJECTIDSTRING,	    -- 	Ausschneiden und Verschieben von Objekten
			@38  OBJECTIDSTRING,	    -- 	Objekte kopieren
			@39  OBJECTIDSTRING,	    -- 	Verteilen von Objekten
			@40	 OBJECTIDSTRING,	    -- 	Checkout
			@41  OBJECTIDSTRING,	    -- 	Checkin
			@42  OBJECTIDSTRING,	    -- 	Erstellen eines Links
			@43  OBJECTIDSTRING,	    -- 	Suchen nach Objekten
			@44  OBJECTIDSTRING,	    -- 	Drucken
			@45  OBJECTIDSTRING,	    -- 	Speichern von Objekten
		@46  OBJECTIDSTRING,	    -- 	Rechteverwaltung	ABLAGE		
			@47  OBJECTIDSTRING,	    -- 	Was sind Rechte?
			@48  OBJECTIDSTRING,	    -- 	Zuordnen von Rechten
			@49  OBJECTIDSTRING,	    -- 	Rechtealiases
			@50  OBJECTIDSTRING,	    -- 	Bearbeiten von Rechten
			@51  OBJECTIDSTRING,	    -- 	Automatisches Zuordnen von Rechten auf ein Objekt
			@52  OBJECTIDSTRING,	    -- 	Verändern der Rechte

	@53  OBJECTIDSTRING,	    -- 	Die Gruppenansicht	ABLAGE		
		@54  OBJECTIDSTRING,	    -- 	Wozu eine Gruppenansicht?
		@55  OBJECTIDSTRING,	    -- 	Benutzerverwaltung	ABLAGE		
			@56  OBJECTIDSTRING,	    -- 	_berblick
			@57  OBJECTIDSTRING,	    -- 	Anlegen eines neuen Benutzers
			@58  OBJECTIDSTRING,	    -- 	Löschen eines Benutzers
			@59  OBJECTIDSTRING,	    -- 	Gruppen im System	ABLAGE		
				@60  OBJECTIDSTRING,	    -- 	Info über Gruppen im System
				@61  OBJECTIDSTRING,	    -- 	Zuordnen eines Benutzers zu einer Gruppe
				@62  OBJECTIDSTRING,	    -- 	Löschen der Zuordnung
				@63  OBJECTIDSTRING,	    -- 	Anlegen einer Gruppe
				@64  OBJECTIDSTRING,	    -- 	Löschen von Gruppen
		@65  OBJECTIDSTRING,	    -- 	Diskussionen	ABLAGE		
			@66  OBJECTIDSTRING,	    -- 	Was ist eine Diskussion?
			@67  OBJECTIDSTRING,	    -- 	Ansehen einer Diskussion
			@68  OBJECTIDSTRING,	    -- 	Beiträge	ABLAGE		
				@69  OBJECTIDSTRING,	    -- 	Erstellen eines neuen Beitrags
				@70  OBJECTIDSTRING,	    -- 	Antworten auf einen Beitrag
				@71  OBJECTIDSTRING,	    -- 	Löschen von Beiträgen
			@72  OBJECTIDSTRING,	    -- 	Themen	ABLAGE		
				@73  OBJECTIDSTRING,	    -- 	Erstellen von Themen
				@74  OBJECTIDSTRING,	    -- 	Löschen von Themen
			@75  OBJECTIDSTRING,	    -- 	Erstellen einer Diskussion
			@76  OBJECTIDSTRING,	    -- 	Löschen der Diskussion
			@77  OBJECTIDSTRING,	    -- 	Ausschliessen eines Benutzers
		@78  OBJECTIDSTRING,	    -- 	Termine	ABLAGE		
			@79  OBJECTIDSTRING,	    -- 	Termine planen und verwalten
			@80  OBJECTIDSTRING,	    -- 	Den Terminkalender ansehen
			@81  OBJECTIDSTRING,	    -- 	Einen Terminkalender anlegen
			@82  OBJECTIDSTRING,	    -- 	Was ist ein Termin?
			@83  OBJECTIDSTRING,	    -- 	Anlegen eines Termins
			@84	 OBJECTIDSTRING,	    -- 	Einfügen von Terminen
			@85  OBJECTIDSTRING,	    -- 	Löschen eines Termins
			@86  OBJECTIDSTRING,	    -- 	Private Termine und Termine in der Gruppenansicht
			@87  OBJECTIDSTRING,	    -- 	Termine mit mehreren Teilnehmern	ABLAGE		
				@88  OBJECTIDSTRING,	    -- 	Anmelden zu einem Termin
				@89  OBJECTIDSTRING,	    -- 	Abmelden von einem Termin
		@90  OBJECTIDSTRING,	    -- 	Katalogverwaltung	ABLAGE		
			@91  OBJECTIDSTRING,	    -- 	Was ist die Katalogverwaltung?
			@92  OBJECTIDSTRING,	    -- 	Produktschlüsselkategorien	ABLAGE		
				@93  OBJECTIDSTRING,	    -- 	Was ist eine Produktschlüsselkategorie?
				@94  OBJECTIDSTRING,	    -- 	Anlegen einer Kategorie
			@95  OBJECTIDSTRING,	    -- 	Produktschlüssel	ABLAGE		
				@96  OBJECTIDSTRING,	    -- 	Was ist ein Produktschlüssel?
				@97  OBJECTIDSTRING,	    -- 	Anlegen eines Schlüssels
			@98  OBJECTIDSTRING,	    -- 	Warengruppe	ABLAGE		
				@99  OBJECTIDSTRING,	    -- 	Was ist eine Warengruppe?
				@100    OBJECTIDSTRING	    -- 	Anlegen einer Warengruppe

--- 101 - 263 can be found later....


if @debug = "true" PRINT "Create new help structures..."
/*
****** ABLAGE *********
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer, 
            '<Name in m2>', @oid_s, 1, 0, '0x0000000000000000',
            '<Beschreibung>',
            @<variable> OUTPUT


****** DATEI **********

    SELECT @tmpUrl = @l_helpPath + '<html-dateiname>'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            '<name in m2>', @1, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            '<schlagwörter>',
            '<Lernziele>', @11 OUTPUT

*/


PRINT "create help container..."
---------------------------------------------------Ebene0-----------------------------------------------------------------------------
-- create helpcontainer:
	EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer, 'Hilfe', 
            @public_s, 1, 0, '0x0000000000000000', 
            'Hier finden Sie alles, um Ihr System besser zu verstehen.', @oid_s OUTPUT

---------------------------------------------------Ebene1-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer, 
            'Willkommen im System', @oid_s, 1, 0, '0x0000000000000000',
            'Allgmeine Informationen und erste Schritte im System.',
            @1 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'will_system.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das System', @1, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Verzeichnisbaum Gruppe Privat Gruppenansicht Privatansicht',
            'Was ist das Besondere an der Applikation m2?', @2 OUTPUT

-----------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Neu im System', @1, 1, 0, '0x0000000000000000',
            'Alles Neue in dieser Release.',
            @3 OUTPUT
-- if @debug="true" Print "some done(3)..."
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'will_neu.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist neu in dieser Release?', @3, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Listen Funktionen Domänenverwaltung Reiter Verzeichnisbaum Log Export Import hierarchischer Import 
            Mehrfachupload',
            'Die Neuigkeiten von Release 2.0', @4 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die ersten Schritte', @1, 1, 0, '0x0000000000000000',
            'Step by step durch die Applikation.',
            @5 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------


    SELECT @tmpUrl = @l_helpPath + 'will_erste.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Login', @5, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Einstiegsseite Login Benutzername Kennwort Domäne System',
            'So betreten Sie das System.', @6 OUTPUT

-----------------

    SELECT @tmpUrl = @l_helpPath + 'will_willkommen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Willkommenseite', @5, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Willkommenseite Neuigkeiten Verzeichnisbaum Inhaltsansicht Pfad Reiter System Button Bereiche Gruppe Privat
            Gruppenansicht Privatansicht Benutzer Rechte',
            'Herzlich Willkommen in Ihrem System!', @7 OUTPUT


---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die Hilfe zur Hilfe', @1, 1, 0, '0x0000000000000000',
            'Wie gehen Sie mit dieser Hilfeablage um, wie finden Sie was?',
            @8 OUTPUT


---------------------------------------------------Ebene3-----------------------------------------------------------------------------


    SELECT @tmpUrl = @l_helpPath + 'will_hilfe1.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wie bediene ich die Hilfe?', @8, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Hilfe Suche Struktur',
            'Der Umgang mit der Hilfeablage im System.', @9 OUTPUT

-----------------

    SELECT @tmpUrl = @l_helpPath + 'will_hilfe2.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wie finde ich was?', @8, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Hilfe Suche Struktur',
            'Wie suchen Sie nach den Themen, die Sie interessieren?', @10 OUTPUT
IF @debug = "true" Print "some done(10)..."

---------------------------------------------------Ebene1-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Grundlegendes', @oid_s, 1, 0, '0x0000000000000000',
            'In dieser Ablage finden Sie die grundlegenden Informationen zu Ihrer Applikation.',
            @11 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Ansichten im System', @11, 1, 0, '0x0000000000000000',
            'Welche Sichten gibt es auf das System selbst und welche auf die Objekte im Speziellen? Diese Antworten finden Sie hier.',
            @12 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'ansichten_welche.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Welche Ansichten gibt es?', @12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Gruppe Privat Gruppenansicht Privatansicht',
            'Grundlegendes zu Gruppe und Privat.', @13 OUTPUT

-----------------

    SELECT @tmpUrl = @l_helpPath + 'ansichten_gruppe.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Gruppe', @12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer Rechte Gruppenansicht Gruppe Reiter',
            'Was ist die Gruppenansicht?', @14 OUTPUT

-----------------

    SELECT @tmpUrl = @l_helpPath + 'ansichten_privat.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Privat', @12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privatansicht Benutzer Privat Benutzerprofil Arbeitskorb Eingangskorb Ausgangskorb Bestellungen Warenkorb
            Datei Ablage Objekt Login Neuigkeiten Hotlist',
            'Was ist die Privatansicht?', @15 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Standardordner', @11, 1, 0, '0x0000000000000000',
            'Die grundlegenden Ablagen, die in der Basisversion von m2 zu finden sind.',
            @19 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'ordner_basis.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Welche Ordner gibt es im Basissystem?', @19, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Gruppe Benutzerverwaltung Data Interchange Export Import Diskussionen Hilfe Katalogverwaltung Layouts Stammdaten 
            Termine Warenangebote Privat Arbeitskorb Ausgangskorb Benutzerprofil Bestellungen Eingangskorb Neuigkeiten Warenkorb',
            'Hier finden Sie die Standardablagen der Basisapplikation.', @20 OUTPUT
if @debug = "true" Print "some done(20)..."
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Basisbegriffe', @11, 1, 0, '0x0000000000000000',
            'Hier finden Sie die Begriffe, die Sie auf jeden Fall kennen sollten.',
            @21 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'basis_begr.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die wichtigsten Begriffe', @21, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Gruppe Privat Verzeichnisbaum Buttons Reiter List Objekte Navigation Funktionen  Ablage Objekt Rechte Säubern',
            'Dies Begriffe sollten Sie kennen, um optimal mit dem System zu arbeiten.', @22 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Geschäftsobjekte', @11, 1, 0, '0x0000000000000000',
            'Diese Objekte finden Sie in Ihrem System.',
            @23 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'gesch_geschaeftsobjekt.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist ein Geschäftsobjekt?', @23, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Objekt Datei Dokument Ablage Diskussion Dokumentablage Formulare Formularablage Hyperlink 
            Integrationsverwaltung Notiz Schwarzes Brett Stammdaten Terminplan Terminpläne Warenkatalog Firma Person',
            'Das versteht man unter einem Objekt im m2 framework.', @24 OUTPUT

-----------------------------

    SELECT @tmpUrl = @l_helpPath + 'gesch_welche.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Welche Objekte gibt es?', @23, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Objekt Datei Dokument Ablage Diskussion Dokumentablage Formulare Formularablage Hyperlink 
            Integrationsverwaltung Notiz Schwarzes Brett Stammdaten Terminplan Terminpläne Warenkatalog Firma Person',
            'Die gesamte Palette der Geschäftsobjekte in Ihrer Applikation.', @25 OUTPUT

--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Sichten auf ein Objekt', @23, 1, 0, '0x0000000000000000',
            'Von all diesen Spezialsichten aus kann ein Objekt betrachtet werden.',
            @16 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'gesch_reiter.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Welche Sichten bieten die Reiter?', @16, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Inhalt Info Verweise Rechte Protokoll Terminplan',
            'Der Sinn und die Funktion der Reiter beim Objekt.', @17 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'gesch_aufteil.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Aufteilung der Fenster', @16, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Funktionsleiste Inhalt Verzeichnisbaum Reiter Pfad Gruppe Privat',
            'Was sehen Sie, wenn Sie sich im System befinden?', @18 OUTPUT

Print "some done(25)..."
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Navigieren im System', @11, 1, 0, '0x0000000000000000',
            'So kommen Sie am schnellsten zu den Informationen, die Sie bearbeiten oder betrachten wollen.',
            @26 OUTPUT
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'nav_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wie gelange ich zu den Objekten und Ablagen?', @26, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Navigation Verzeichnisbaum Pfad Reiter Zurück Ablage Inhaltsansicht',
            'Optimale Navigation mit Pfad, Verzeichnisbaum und "Zurück".', @27 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Grundfunktionen', @11, 1, 0, '0x0000000000000000',
            'All das kann man mit einem Objekt tun!',
            @29 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_funktionen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Welche Funktionen gibt es?', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Neu Bearbeiten Funktionsleiste Löschen Ausschneiden Kopieren Verteilen Einfügen Link erstellen Suchen Drucken Säubern
            Weiterleiten Checkout Checkin',
            'Was kann man mit einem Objekt in m2 alles machen?', @30 OUTPUT
IF @debug = "true" Print "some done(30)..."
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_leiste.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Funktionsleiste', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Neu Bearbeiten Funktionsleiste Löschen Ausschneiden Kopieren Verteilen Einfügen Link erstellen Suchen Drucken Säubern
            Weiterleiten Checkout Checkin',
            'Die Funktionen zum Bearbeiten der Objekte.', @31 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_listen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Arbeit mit Listen', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Kopieren Verteilen Einfügen Ausschneiden Funktionen Funktionsleiste Auswählen',
            'Was ist eine Liste?', @32 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_lesen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Lesen von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Lesen Objekt Objekte Geschäftsobjekt Geschäftsobjekte Benutzerprofil',
            'So lesen Sie die Informationen in einem Objekt.', @33 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_erstellen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstellen von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer Lesen Objekte Typ Objekttyp Verfallsdatum Speichern',
            'So erstellen Sie ein neues Objekt.', @34 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_bearbeiten.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Bearbeiten von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bearbeiten Objekte Funtkionen wählen Eingabemaske Inhalt Inhaltsansicht',
            'Bearbeiten von Objekten leichtgemacht.', @35 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Löschen Objekt Objekte Geschäftsobjekt Funktionen Funktionsleiste',
            'So löschen Sie ein Objekt.', @36 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_ausschneiden.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ausschneiden und Verschieben von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Ausschneiden Verschieben Objekte Funktionen Funktionsleiste',
            'So können Sie ein Objekt ausschneiden und/oder verschieben.', @37 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_kopieren.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Objekte kopieren', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Objekte kopieren Geschäftsobjekt',
            'So kopieren Sie ein beliebiges Objekt.', @38 OUTPUT

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_verteilen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Verteilen von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Verteilen Objekt Objekte Geschäftsobjekt Benutzer',
            'Wie können Sie ein Objekt an andere Benutzer schicken?', @39 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_checkout.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Checkout', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer Bearbeiten Objekt auschecken Checkin Datei Objekttyp',
            'Damit niemand gleichzeitig mit Ihnen ein Objekt bearbeiten kann.', @40 OUTPUT

if @debug = "true" Print "some done(40)..."
           
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_checkin.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Checkin', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer Bearbeiten Objekt auschecken Checkin Datei Objekttyp',
            'So machen Sie Ihr ausgechecktes Objekt wieder allen Benutzern zugänglich.', @41 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_link.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstellen eines Links', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Erstellen Objekt Objekte Datei Geschäftsobjekt',
            'So erstellen Sie einen Link im System.', @42 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_suchen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Suche nach Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Suchen Objekte Verzeichnisbaum Button Pfad Zurück',
            'So suchen und finden Sie Objekte im System.', @43 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_drucken.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Drucken', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Drucken Objekt Objekte Bearbeiten Funktionen Funktionsleiste',
            'Das Drucken von Geschäftsobjekten.', @44 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'o_speichern.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Speichern von Objekten', @29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Speichern Objekte Objekt Geschäftsobjekt Funktion Funktionsleiste',
            'So bewahre ich meine Informationen für andere Benutzer auf.', @45 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Rechteverwaltung', @11, 1, 0, '0x0000000000000000',
            'Alles, was man im Umgang mit Rechten wissen muss.',
            @46 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'r_rechte.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was sind Rechte?', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Alles Wissenswerte über Rechte.', @47 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'r_zuordnen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Zuordnen von Rechten', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung zuordnen',
            'So Ordne ich Rechte zu.', @48 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'r_aliases.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Rechtealiases', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechtealiases Alias Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Das Zusammenfassen der einzelnen Rechte zu Aliases.', @49 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'r_bearbeiten.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Bearbeiten von Rechten', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung Bearbeiten',
            'So bearbeiten Sie die Rechte.', @50 OUTPUT
Print "some done(50)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'r_automatisch.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Automatisches Zuordnen von Rechten auf ein Objekt', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Automatisches Zuordnen der Rechte auf ein Objekt.', @51 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'r_veraendern.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Verändern der Rechte', @46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'So verändern Sie die Rechte auf ein Objekt.', @52 OUTPUT

---------------------------------------------------Ebene1-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die Gruppenansicht', @oid_s, 1, 0, '0x0000000000000000',
            'Alles, was Sie in der Gruppenansicht zu sehen bekommen.',
            @53 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    SELECT @tmpUrl = @l_helpPath + 'gruppe_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wozu eine Gruppenansicht?', @53, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Gruppe Gruppenansicht Verwaltung Ablage Data Interchange Benutzerverwaltung
            Termine Stammdaten Katalogverwaltung Warenangebote Warenkatalog',
            'Was unterscheidet Gruppen- und Privatansicht?', @54 OUTPUT

----------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Benutzerverwaltung', @53, 1, 0, '0x0000000000000000',
            'Rund um den Benutzer.',
            @55 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_ueberblick.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            '_berblick', @55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe',
            'Generelle Informationen über die Benutzerverwaltung.', @56 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines neuen Benutzers', @55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe',
            'So legen Sie einen neuen Benutzer an.', @57 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen eines Benutzers', @55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe Löschen',
            'So löschen Sie einen angelegten Benutzer.', @58 OUTPUT

--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Gruppen im System', @55, 1, 0, '0x0000000000000000',
            'Alles über Gruppen, die innerhalb des Systems angelegt werden können.',
            @59 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_info.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Info über die Gruppen im System', @59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Gruppen System Gruppe',
            'Diese Gruppen gibt es im System.', @60 OUTPUT
IF @debug = "true" Print "some done(60)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_zuordnen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Zuordnen eines Benutzers zu einer Gruppe', @59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Gruppen System Gruppe',
            'So ordnen Sie einen Benutzer zu einer Gruppe zu.', @61 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_zuord_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen einer Zuordnung', @59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Gruppen System Zuordnung löschen Gruppe',
            'So löschen Sie eine Zuordnung.', @62 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_gr_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Gruppe', @59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Gruppen System Objekt Gruppe',
            'Anleitung zum Anlegen einer Gruppe.', @63 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'benutz_gr_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Gruppen', @59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Benutzer anlegen Gruppen System Objekt Gruppe löschen',
            'Anleitung zum Löschen von Gruppen.', @64 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Diskussionen', @53, 1, 0, '0x0000000000000000',
            'Alles über Dikussionen, Themen, Beiträge.',
            @65 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist eine Diskussion?', @65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer',
            'Wozu diskutieren?', @66 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_ansehen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ansehen einer Diskussion', @65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer ansehen',
            'Anleitung zum Betrachten einer Diskussion.', @67 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Beiträge', @65, 1, 0, '0x0000000000000000',
            'Rund um die Beiträge von Diskussionen.',
            @68 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_neu.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstellen eines neuen Beitrags', @68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer',
            'Anleitung zur Erstellung von neuen Beiträgen.', @69 OUTPUT

--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_antworten.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Antworten auf einen Beitrag', @68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer antworten',
            'So antworten Sie auf einen Beitrag.', @70 OUTPUT
IF @debug = "true" Print "some done(70)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_b_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Beiträgen', @68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer löschen',
            'So löschen Sie einen Beitrag.', @71 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Themen', @65, 1, 0, '0x0000000000000000',
            'Wie kommen die Themen in die Diskussion?',
            @72 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_thema_neu.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstellen von Themen', @72, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer erstellen Themen',
            'So erstellen Sie neue Themen für eine Diskussion.', @73 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_thema_loesch.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Themen', @72, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer löschen',
            'So löschen Sie die Themen wieder.', @74 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_erstellen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstellen einer Diskussion', @65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer erstellen',
            'Grundlegendes zur Erstellung einer Diskussion.', @75 OUTPUT
Print "some done(75)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen der Diskussion', @65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer löschen',
            'Das Löschen einer Diskussion.', @76 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dis_ausschliessen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ausschliessen eines Benutzers', @65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer ausschlieîen',
            'Wenn ein Benutzer keinen Zugang zur Diskussion mehr haben soll...', @77 OUTPUT
            
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Termine', @53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung von Terminen.',
            @78 OUTPUT
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_planen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Termine planen und verwalten', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung',
            'Was zur Terminverwaltung nötig ist.', @79 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_ansehen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Den Terminkalender ansehen', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung ansehen',
            'So können Sie den Kalender betrachten.', @80 OUTPUT
IF @debug = "true" Print "some done(80)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_kalender.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Einen Terminkalender anlegen', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung anlegen',
            'Anleitung zur Erstellung eines neuen Kalenders.', @81 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist ein Termin?', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung',
            'Zur genaueren Erklärung des Begriffs Termin.', @82 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Termins', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung anlegen',
            'So legen Sie einen neuen Termin an.', @83 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_einfuegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Einfügen von Terminen', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung einfügen',
            'So fügen Sie einen Termin ein.', @84 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Terminen', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung löschen',
            'Anleitung zum Löschen von Terminen aus dem Kalender.', @85 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_priv_gr.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Private Termine und Termine in der Gruppenansicht', @78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung Privat 
            Gruppenansicht Privatansicht',
            'Der Unterscheid zwischen Terminen in der Gruppen- und der Privatansicht.', @86 OUTPUT
-------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Termine mit mehreren Teilnehmern', @78, 1, 0, '0x0000000000000000',
            'Wenn sich Teilnehmer zu einem Termin anmelden...',
            @87 OUTPUT
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_anmelden.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anmelden zu einem Termin', @87, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung Anmelden',
            'Die Anmeldung zu eimen gemeinsamen Termin.', @88 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'term_abmelden.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Abmelden von einem Termin', @87, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung abmelden',
            'So melden Sie sich wieder von einem Termin ab.', @89 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Katalogverwaltung', @53, 1, 0, '0x0000000000000000',
            'Rund um Produktschlüssel und Warengruppen.',
            @90 OUTPUT
IF @debug = "true" Print "some done(90)..."
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist die Katalogverwaltung?', @90, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Prdoktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen Ware',
            'Alles über das Anlegen von grundlegenden Determinanten für die Warenverwaltung.', @91 OUTPUT

--------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Produktschlüsselkategorien', @90, 1, 0, '0x0000000000000000',
            'Rund um das Produkt.',
            @92 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_psk.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist eine Produktschlüsselkategorie?', @92, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Produktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen Ware',
            'Was muss man sich unter Produktschlüsselkategorien vorstellen?', @93 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_psk_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Kategorie', @92, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Produktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware',
            'Wie legen Sie die Kategorie an?', @94 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Produktschlüssel', @90, 1, 0, '0x0000000000000000',
            'Alles zum Begriff Produktschlüssel.',
            @95 OUTPUT

---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_ps.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist ein Produktschlüssel?', @95, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Produktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware',
            'Was muss man sich unter Produktschlüsseln vorstellen?', @96 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_ps_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Schlüssels', @95, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Poduktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware anlegen',
            'So legen Sie einen Schlüssel an.', @97 OUTPUT

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Warengruppe', @90, 1, 0, '0x0000000000000000',
            'Rund um die Warengruppe...',
            @98 OUTPUT

---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_wgr.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist eine Warengruppe?', @98, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Poduktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware',
            'Das ist eine Warengruppe.', @99 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_wgr_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Warengruppe', @98, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Produktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware anlegen Warengruppe',
            'So legen Sie eine Warengruppe an.', @100 OUTPUT

IF @debug = "true" Print "some done(100)..."

--- ****************************************************************************
--- ****************************************************************************
--- ****************************************************************************
Print "first 100 tuples succeeded, setting GO..."
GO
--- ****************************************************************************
--- ****************************************************************************
--- ****************************************************************************


--- restore needed variables, lost because of "GO" statement
print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT
-- print "domain variables restored..."
declare 			@101    OBJECTIDSTRING,	    --	Produktmarken	ABLAGE		
				@102    OBJECTIDSTRING,	    --	Was ist eine Produktmarke?
				@103    OBJECTIDSTRING,	    --	Anlegen der Produktmarke
				@104    OBJECTIDSTRING,	    --	Löschen der Produktmarke
		@105    OBJECTIDSTRING,	    --	Warenangebote	ABLAGE		
			@106    OBJECTIDSTRING,	    --	Info über Warenangebote
			@107    OBJECTIDSTRING,	    --	Umgang mit einem bestehenden Warenkatalog	ABLAGE		
				@108    OBJECTIDSTRING,	    --	Der Warenkorb
				@109    OBJECTIDSTRING,	    --	Suchen von Waren	ABLAGE		
					@110    OBJECTIDSTRING,	    --	Suchen über die Baumstruktur
					@111    OBJECTIDSTRING,	    --	Die Schaltfläche "Suchen"
				@112    OBJECTIDSTRING,	    --	Bestellen von Waren	ABLAGE		
					@113    OBJECTIDSTRING,	    --	Der Status meiner Bestellung
					@114    OBJECTIDSTRING,	    --	Ausdrucken der Bestellung
			@115    OBJECTIDSTRING,	    --	Anlegen eines Warenkatalogs	ABLAGE		
				@116    OBJECTIDSTRING,	    --	Anlegen eines Warenkatalogs
				@117    OBJECTIDSTRING,	    --	Löschen des Warenkatalogs
			@118    OBJECTIDSTRING,	    --	Anlegen von Waren	ABLAGE		
				@119    OBJECTIDSTRING,	    --	Wie lege ich Waren an?
				@120    OBJECTIDSTRING,	    --	Kopieren einer Ware
				@121    OBJECTIDSTRING,	    --	Einfügen einer Ware mit einem Link aus der Gruppenansicht
				@122    OBJECTIDSTRING,	    --	Löschen von Waren aus dem System
				@123    OBJECTIDSTRING,	    --	Preise festlegen	ABLAGE		
					@124    OBJECTIDSTRING,	    --	Wozu Preise definieren?
					@125    OBJECTIDSTRING,	    --	Anlegen von Sortimenten mit Profil und Schlüssel
					@126    OBJECTIDSTRING,	    --	Anlegen von Waren mit Profil und Schlüssel
		@127    OBJECTIDSTRING,	    --	Stammdaten	ABLAGE		
			@128    OBJECTIDSTRING,	    --	Wozu Stammdaten?
			@129    OBJECTIDSTRING,	    --	Die Stammdatenablage	ABLAGE		
				@130    OBJECTIDSTRING,	    --	Anlegen der Stammdatenablage
			@131    OBJECTIDSTRING,	    --	Firma	ABLAGE		
				@132    OBJECTIDSTRING,	    --	Eine Fima anlegen
			@133    OBJECTIDSTRING,	    --	Person	ABLAGE		
				@134    OBJECTIDSTRING,	    --	Eine Person anlegen
			@135    OBJECTIDSTRING,	    --	Stammdaten und Benutzerverwaltung
			@136    OBJECTIDSTRING,	    --	Stammdaten und Katalogverwaltung
		@137    OBJECTIDSTRING,	    --	Data Interchange	ABLAGE		
			@138    OBJECTIDSTRING,	    --	_berblick über den Data Interchange Bereich
			@139    OBJECTIDSTRING,	    --	Die Ablage "Integrationsverwaltung"
			@140    OBJECTIDSTRING,	    --	Objekte im Data Interchange Bereich
			@141    OBJECTIDSTRING,	    --	Konnektoren	ABLAGE		
				@142    OBJECTIDSTRING,	    --	Was ist ein Konnektor?
				@143    OBJECTIDSTRING,	    --	Datei Konnektor
				@144    OBJECTIDSTRING,	    --	FTP Konnektor
				@145    OBJECTIDSTRING,	    --	Email Konnektor
				@146    OBJECTIDSTRING,	    --	Konnektoren anlegen
			@147    OBJECTIDSTRING,	    --	Das Log	ABLAGE		
				@148    OBJECTIDSTRING,	    --	Die Aufzeichnung des Datenmaustausches
			@149    OBJECTIDSTRING,	    --	Agents	ABLAGE		
				@150    OBJECTIDSTRING,	    --	Der automatisierte Datenaustausch
				@151    OBJECTIDSTRING,	    --	Den Agent aufrufen
				@152    OBJECTIDSTRING,	    --	Die Zeitsteuerung des Agents
			@153    OBJECTIDSTRING,	    --	Der Import	ABLAGE		
				@154    OBJECTIDSTRING,	    --	Der manuelle Datenaustausch
				@155	OBJECTIDSTRING,	    --	Die Importablagen
				@156    OBJECTIDSTRING,	    --	Die Importfunktion
				@157    OBJECTIDSTRING,	    --	Das Importskript
				@158    OBJECTIDSTRING,	    --	Die Importmaske
				@159    OBJECTIDSTRING,	    --	Was ist der Mehrfachupload?
				@160    OBJECTIDSTRING,	    --	Import als Geschäftsobjekt
				@161    OBJECTIDSTRING,	    --	Import von Formularen
				@162    OBJECTIDSTRING,	    --	Das Import Dokument
				@163    OBJECTIDSTRING,	    --	Der hierarchische Import
				@164    OBJECTIDSTRING,	    --	Das Importieren von Reitern
				@165    OBJECTIDSTRING,	    --	Das Import DTD
				@166    OBJECTIDSTRING,	    --	Spezielle Themen	ABLAGE		
					@167    OBJECTIDSTRING,	    --	Das Import Szenario
					@168    OBJECTIDSTRING,	    --	Key Mapper
					@169    OBJECTIDSTRING,	    --	Key Domains
			@170    OBJECTIDSTRING,	    --	Der Export	ABLAGE		
				@171    OBJECTIDSTRING,	    --	Info über den Export
				@172    OBJECTIDSTRING,	    --	Die Exportablage
				@173    OBJECTIDSTRING,	    --	Das Export Dokument
				@174    OBJECTIDSTRING,	    --	Die Exportmaske
		@175    OBJECTIDSTRING,	    --	Formularverwaltung	ABLAGE		
			@176    OBJECTIDSTRING,	    --	Zum Thema Formulare
			@177    OBJECTIDSTRING,	    --	Feldtypen für Formulare
			@178    OBJECTIDSTRING,	    --	Die Formularvorlagenablage	ABLAGE		
				@179    OBJECTIDSTRING,	    --	Formularvorlagenablage
				@180    OBJECTIDSTRING,	    --	Anlegen einer Formularvorlagenablage
				@181    OBJECTIDSTRING,	    --	Bearbeiten einer Formularvorlagenablage
				@182    OBJECTIDSTRING,	    --	Die Formularvorlage
			@183    OBJECTIDSTRING,	    --	Das Formular	ABLAGE		
				@184    OBJECTIDSTRING,	    --	Was ist ein Formular?
				@185    OBJECTIDSTRING,	    --	Anlegen eines Formulars
				@186    OBJECTIDSTRING,	    --	Import eines Formulars
				@187    OBJECTIDSTRING,	    --	Das Importieren von Reitern
		@188    OBJECTIDSTRING,	    --	Workflow	ABLAGE		
			@189    OBJECTIDSTRING,	    --	Wissenswertes zum Thema Workflow
			@190    OBJECTIDSTRING,	    --	Grundbegriffe des Workflow
			@191    OBJECTIDSTRING,	    --	Anlegen einer Workflowvorlagenablage
			@192    OBJECTIDSTRING,	    --	Starten des Workflows
			@193    OBJECTIDSTRING,	    --	Anlegen eines Workflows
		@194    OBJECTIDSTRING,	    --	Domänenverwaltung	ABLAGE		
			@195    OBJECTIDSTRING,	    --	Was ist eine Domäne?
			@196    OBJECTIDSTRING,	    --	Die Systemadministratorensicht
			@197    OBJECTIDSTRING,	    --	Das Login
			@198    OBJECTIDSTRING,	    --	Die Systemandmin-Gruppenansicht
			@199    OBJECTIDSTRING,	    --	Anlegen eines Domänenschemas
			@200    OBJECTIDSTRING,	    --	Anlegen einer Standard Domäne
			@201    OBJECTIDSTRING,	    --	Includes
			@202    OBJECTIDSTRING,	    --	Die neue Domäne	ABLAGE		
				@203    OBJECTIDSTRING,	    --	Erstes Login
				@204    OBJECTIDSTRING,	    --	Ordnerstruktur in der Gruppenansicht anlegen
				@205    OBJECTIDSTRING,	    --	Import von Strukturen
				@206    OBJECTIDSTRING	    --	Die Domäne löschen

--- 207 - 236 can be found later

--- retrieving needed @90 and @53 from ibs_object
declare @90 OBJECTIDSTRING,
        @53 OBJECTIDSTRING
exec p_retValue 'Katalogverwaltung', @90 OUTPUT
EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT
print "***"
print @90
print @53
print "***"
print "variables restored..."

--- ****************************************************************************
--- ****************************************************************************
--- ****************************************************************************


---------------------------------------------------Ebene3-----------------------------------------------------------------------------
select @userId,@c_TVHelpContainer, @90 as userid_helpcontainer
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Produktmarken', @90, 1, 0, '0x0000000000000000',
            'Rund um das Anlegen von Produktmarken...',
            @101 OUTPUT
-- print "101 hat oid von"
 select @101 as oid_101
-- SELECT @101 AS "101"
-- print "ausgeschrieben"
-- exec p_retValue 'Produktmarken', @101 OUTPUT
-- print @101
-- print "**"
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_pm.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist eine Produktmarke?', @101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Poduktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware',
            'Was muss man sich unter Produktmarke vorstellen?', @102 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_pm_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen der Produktmarke', @101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Poduktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware anlegen',
            'Anleitung zum Anlegen der Produktmarke.', @103 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'kat_pm_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen der Produktmarke', @101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Katalogverwaltung Prdoktschlüsselkategorien Produktschlüssel Produktmarken Warengruppen 
            Ware löschen',
            'So löschen Sie die Produktmarke wieder.', @104 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Warenangebote', @53, 1, 0, '0x0000000000000000',
            'Rund um den Warenkatalog und die Ware...',
            @105 OUTPUT
-- print "105 hat oid von"
-- print @105
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_info.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Info über Warenangebote', @105, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schlüssel',
            'Was sind Warenangebote?', @106 OUTPUT
-----------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Umgang mit einem bestehenden Warenkorb', @105, 1, 0, '0x0000000000000000',
            'Wenn schon ein Warenangebote existiert...',
            @107 OUTPUT

---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_warenkorb.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der Warenkorb', @107, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schlüssel',
            'Was ist der Warenkorb?', @108 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Suchen von Waren', @107, 1, 0, '0x0000000000000000',
            'Anleitung zum Suchen von Waren über Baum und Suchen-Schaltfläche...',
            @109 OUTPUT

---------------------------------------------------Ebene5-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_suche_baum.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Suchen über die Baumstruktur', @109, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schlüssel Suchen Verzeichnisbaum',
            'Die Suche über den Verzeichnisbaum.', @110 OUTPUT
IF @debug = "true" Print "some done(110)..."
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_suche_button.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Schaltfläche "Suchen"', @109, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schlüssel Suchen Schaltfläche',
            'Die Suche über die Suchen-Funktion.', @111 OUTPUT
IF @debug = "true" Print "some done(111)..."
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Bestellen von Waren', @107, 1, 0, '0x0000000000000000',
            'Alles zur Bestellung von Waren.',
            @112 OUTPUT
IF @debug = "true" Print "some done(112)..."
---------------------------------------------------Ebene5-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_status.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der Status meiner Bestellung', @112, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment',
            'Was steht in Ihrer Warenbestellung?', @113 OUTPUT
IF @debug = "true" Print "some done(113)..."

--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_ausdruck.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ausdrucken der Bestellung', @112, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen ausdrucken drucken Bestellung Sortiment',
            'So drucken Sie Ihre Bestellung aus.', @114 OUTPUT
IF @debug = "true" PRINT "some done (114)..."
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Anlegen eines Warenkatalogs', @105, 1, 0, '0x0000000000000000',
            'Alles, was Sie zum Anlegen eines Warenkatalogs wissen müssen...',
            @115 OUTPUT
IF @debug = "true" PRINT "some done (115)..."
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Warenkatalogs', @115, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Warenkatalog anlegen',
            'So legen Sie einen Warenkatalog an.', @116 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen eines Warenkatalogs', @115, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Warenkatalog löschen',
            'So löschen Sie einen Warenkatalog.', @117 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Anlegen von Waren', @105, 1, 0, '0x0000000000000000',
            'So legen Sie neue Waren an...',
            @118 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_ware.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wie lege ich Waren an?', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment anlegen Waren',
            'Hilfe beim Anlegen neuer Waren.', @119 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_ware_kopieren.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Kopieren einer Ware', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Waren kopieren',
            'So kopieren Sie eine Ware.', @120 OUTPUT
IF @debug = "true" Print "some done(120)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_ware_einfueg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Link Gruppe Gruppenansicht',
            'Einfügen einer Ware aus der Gruppenansicht in den Privatbereich.', @121 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_ware_loesch.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Löschen von Waren aus dem System', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment löschen System',
            'So löschen Sie die Waren aus dem System.', @122 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Preise festlegen', @105, 1, 0, '0x0000000000000000',
            'Alles zum Festlegen der Preise.',
            @123 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_preise.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wozu Preise definieren?', @123, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Preise definieren festlegen HEK VEK statt',
            'Wozu werden Preise gebraucht?', @124 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'w_sort.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen von Sortimenten mit Profil und Schlüssel', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Profil Schlüssel Prodoktschlüssel Bestellung Sortiment',
            'Das Anlegen von Sortimenten.', @125 OUTPUT
PRINT "some done(125)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'w_ware_ps.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen von Waren mit Profil und Schlüssel', @118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Bestellen Ware Einkaufswagen Profil Schlüssel Produktschlüssel Bestellung Sortiment',
            'Das Anlegen von Waren.', @126 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Stammdaten', @53, 1, 0, '0x0000000000000000',
            'Alles rund um die Stammdatenverwaltung.',
            @127 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_einf.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wozu Stammdaten?', @127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person',
            'Was sind Stammdaten und wozu muss man sie anlegen?', @128 OUTPUT
--------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die Stammdatenablage', @127, 1, 0, '0x0000000000000000',
            'Die Ablage für die Stammdaten.',
            @129 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen der Stammdatenablage', @129, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person 
            Stammdatenablage anlegen',
            'Wie legen Sie die Stammdatenablage an?', @130 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Firma', @127, 1, 0, '0x0000000000000000',
            'Alles rund um Firmen im System.',
            @131 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_firma.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Eine Firma anlegen', @131, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma anlegen',
            'So legen Sie eine Firma an.', @132 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Person', @127, 1, 0, '0x0000000000000000',
            'Alles rund um Personen im System.',
            @133 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_person.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Eine Person anlegen', @133, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma anlegen',
            'So legen Sie eine Person an.', @134 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_benutzerverw.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Stammdaten und Benutzerverwaltung', @127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person',
            'Der Zusammenhang zwischen Stammdaten und Benutzer.', @135 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'st_katalogverw.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Stammdaten und Katalogverwaltung', @127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person Bestellverantwortlicher
            Katalogverantwortlicher',
            'Der Zusammenhang zwischen Stammdaten und Katalog.', @136 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Data Interchange', @53, 1, 0, '0x0000000000000000',
            'Alles, rund um den Datenaustausch...',
            @137 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_ueberblick.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            '_berblick über den Data Interchange Bereich', @137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Import Export Integrationsverwaltung Formulare Workflow',
            'Was finden Sie im Data Interchange Bereich?', @138 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_integration.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Ablage "Integrationsverwaltung"', @137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Import Export Integrationsverwaltung Formulare Workflow',
            'Was ist die Integrationsverwaltung?', @139 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_objekte.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Objekte im Data Interchange Bereich', @137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Objekte Data Interchange Ablage Exportablage Importablage Integrationsverwaltung 
            Formularvorlagenablage Importablage Importskriptablage Konnektorablage Workflowvorlagenablage',
            'Welche Objekte können in Data Interchange angelegt werden?', @140 OUTPUT
IF @debug = "true" Print "some done(140)..."
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Konnektoren', @137, 1, 0, '0x0000000000000000',
            'Rund um die Konnektoren...',
            @141 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_konnektor.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist ein Konnektor?', @141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Wie erklärt sich der Begriff Konnektor?', @142 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_dateikonn.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Datei Konnektor', @141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der Datei Konnektor!', @143 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_ftpkonn.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'FTP Konnektor', @141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der FTP Konnektor!', @144 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_emailkonn.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Email Konnektor', @141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der E-Mail Konnektor!', @145 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_konn_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Konnektoren anlegen', @141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Import Konnektor Datei Email FTP Importskript Importmaske anlegen',
            'So legen Sie die Konnektoren an.', @146 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Das Log', @137, 1, 0, '0x0000000000000000',
            'Alles Wissenswerte zum Thema "Log"',
            @147 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_log.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Aufzeichnung des Datenaustausches', @147, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Log Import Export Data Interchange Datenaustausch Aufzeichnung',
            'Das kann das Log des Datenaustausches.', @148 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Agents', @137, 1, 0, '0x0000000000000000',
            'Rund um den Agent...',
            @149 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_agents.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der automatische Datenaustausch', @149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Agent Data Interchange automatisieren log Java zeitgesteuert',
            'Das ist automatischer Datenaustausch.', @150 OUTPUT
Print "some done(150)..."
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_ag_aufrufen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Den Agent aufrufen', @149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Agent Data Interchange automatisieren log Java zeitgesteuert Commandline Aufruf',
            'So können Sie den Agent aufrufen.', @151 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_ag_zeit.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Zeitsteuerung des Agents', @149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Agent Data Interchange automatisieren log Java zeitgesteuert Zeitsteuerung periodisch
            Zeitpunkt Wochentag',
            'Wie Sie den Agent mittels einer Zeitsteuereung lenken.', @152 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Der Import', @137, 1, 0, '0x0000000000000000',
            'Rund um den Import...',
            @153 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_import.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der manuelle Datenaustausch', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Beschreibung des Datenaustausches.', @154 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_importablagen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Importablage', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Importablage',
            'Was ist eine Importablage', @155 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_importfunktion.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Importfunktion', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Importfunktion',
            'Was ist die Importfunktion?', @156 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_importskript.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Importskript', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Importskript Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Der Hauptbestandteil des manuellen Datenaustausches.', @157 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_importmaske.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Importmaske', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Eingabe mittels Importmaske.', @158 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_mehrfach.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist der Mehrfachupload?', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Was bedeutet Mehrfachupload?', @159 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_gesch.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Import als Geschäftsobjekt', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Geschäftsobjekt',
            'Rund um den Import.', @160 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_formular.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Import von Formularen', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Formulare',
            'So können Sie Formular ins System bringen.', @161 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_importdoku.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Import Dokument', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten DTD Dokument',
            'Was ist das Import Dokument?', @162 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_hier.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der hierarchische Import', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Wie führe ich einen hierarchischen Import durch?', @163 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_reiter.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Importieren von Reitern', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Reiter',
            'So importiert man Reiter...', @164 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_dtd.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Import DTD', @153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten DTD Dokument',
            'Die Document Type Definition.', @165 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Spezielle Themen', @153, 1, 0, '0x0000000000000000',
            'Weiterführende Themen des Imports.',
            @166 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_szenario.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Import Szenario', @166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Szenario',
            'Informationen zum Import Szenario.', @167 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_mapper.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Key Mapper', @166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Key Mapper',
            'Das sind Key Mapper.', @168 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_domains.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Key Domains', @166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Key Domains',
            'Wissenswertes zu Key Domains.', @169 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Der Export', @137, 1, 0, '0x0000000000000000',
            'Rund um den Export...',
            @170 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_export.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Info über den Export', @170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Export Data Interchange Datenaustausch Konnektor hierarchisch',
            'Grundlegendes zum Thema Export.', @171 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_exp_ablage.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Exportablage', @170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportablage',
            'Was ist die Exportablage?', @172 OUTPUT
--------------------------------

    SELECT @tmpUrl = @l_helpPath + 'di_exp_dokument.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Exportdokument', @170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportdokument',
            'Wissenswertes zum Thema Exportdokument.', @173 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'di_exportmaske.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Exportmaske', @170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportmaske',
            'Eingabe mit Hilfe der Exportmaske.', @174 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Formularverwaltung', @53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung von Formularen...',
            @175 OUTPUT
Print "some done(175)..."
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Zum Thema Formulare', @175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung',
            'Grundlegendes zum Them Formulare.', @176 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_typ.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Feldtypen für Formulare', @175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen',
            'Dies Feldtypen gibt es.', @177 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die Formularvorlagenablage', @175, 1, 0, '0x0000000000000000',
            'Was kann die Formularvorlagenablage?',
            @178 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_fva.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Formularvorlagenablage', @178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Was muss man über diese Ablage wissen?', @179 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_anlegen_fva.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Formularvorlagenablage', @178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Wie legen Sie diese Ablage an?', @180 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_bearb_fva.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Bearbeiten einer Formularvorlagenablage', @178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Wie bearbeiten Sie die Ablage?', @181 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_vorlage.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Formularvorlage', @175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlage',
            'Was ist die Formularvorlage?', @182 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Das Formular', @175, 1, 0, '0x0000000000000000',
            'Rund um das Formular...', @183 OUTPUT
---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist ein Formular?', @183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            ',
            'Was ist ein Formular eigentlich?', @184 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Formulars', @183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen',
            'So legen Sie ein Formular an.', @185 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_imp.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Import eines Formulars', @183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen Import',
            'So importieren Sie ein Formular.', @186 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'form_reiter.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Importieren von Reitern', @175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen Reiter',
            'So importiert man Reiter.', @187 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Workflow', @53, 1, 0, '0x0000000000000000',
            'Wissenswertes zum Thema Workflow.',
            @188 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'work_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wissenswertes zum Thema Workflow', @188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'Alles rund um den Workflow.', @189 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'work_grund.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Grundbegriffe des Workflow', @188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'Grundlegendes.', @190 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'work_wfv_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Workflowvorlagenablage', @188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage 
            Workflowvorlagenablage',
            'So legen Sie eine Vorlage für den Workflow an.', @191 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'work_starten.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Starten des Workflow', @188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare starten Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'So starten Sie den Workflow.', @192 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'work_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Workflow', @188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage 
            Workflowvorlagenablage anlegen',
            'SO legen Sie den Workflow an.', @193 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Domänenverwaltung', @53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung der Domänen.',
            @194 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was ist eine Domäne?', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator',
            'Grundlegendes.', @195 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_sysadmin.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Systemadministratorensicht', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator',
            'Was unterscheidet sie von der Gruppenansicht?', @196 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_login.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Login', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Login',
            'Wie loggen Sie sich ein?', @197 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_sys_gr.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Systemadmin-Gruppenansicht', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Systemadmin-Gruppenansicht',
            'Was sieht der Systemadministrator.', @198 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_anlegen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen eines Domänenschemas', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema',
            'So legen Sie ein Schema für eine Domäne an.', @199 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_anlegen_standard.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Anlegen einer Standard Domäne', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema',
            'So legen Sie eine Standard Domäne an.', @200 OUTPUT
 Print "some done(200)..."
 --------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_include.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Includes', @194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema',
            'Das sind Includes.', @201 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die neue Domäne', @194, 1, 0, '0x0000000000000000',
            'Rund um die neu angelegte Domäne.',
            @202 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_neue_dom.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erstes Login', @202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema',
            'So loggen Sie sich das erste Mal auf der neuen Domäne ein.', @203 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_ordner.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ordnerstrukturen in der Gruppenansicht anlegen', @202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema Strukturen Ordner Struktur',
            'So legen Sie neue Strukturen fest.', @204 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_import.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Import von Strukturen', @202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema Struktur Import',
            'Wie funktioniert der Import von anderen Domänenstrukturen?', @205 OUTPUT
--------------------------------
    SELECT @tmpUrl = @l_helpPath + 'dom_loeschen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Domäne löschen', @202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Domäne Systemadministrator Ansicht Domänenverwaltung Includes neuen Domäne Administrator
            Domänenschema Schema löschen',
            'So löschen Sie die Domäne.', @206 OUTPUT


--- ****************************************************************************
--- ****************************************************************************
--- ****************************************************************************
Print "first 207 tuples succeeded, setting GO..."
GO
--- ****************************************************************************
--- ****************************************************************************
--- ****************************************************************************


--- restore needed variables, lost because of "GO" statement
print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT
--- print "domain variables restored..."
print "variables restored..."
                        
DECLARE 	@207    OBJECTIDSTRING,	    --	Die Privatansicht	ABLAGE		
		@208    OBJECTIDSTRING,	    --	Wozu eine Privatansicht?
		@209    OBJECTIDSTRING,	    --	Arbeitskorb	ABLAGE		
			@210    OBJECTIDSTRING,	    --	Ihre private Arbeitsablage
		@211    OBJECTIDSTRING,	    --	Ausgangskorb	ABLAGE		
			@212    OBJECTIDSTRING,	    --	Alles, was zur Gruppe geht...
		@213    OBJECTIDSTRING,	    --	Benutzerprofil	ABLAGE		
			@214    OBJECTIDSTRING,	    --	Info zum Benutzerprofil
			@215    OBJECTIDSTRING,	    --	Das Kennwort ändern
		@216    OBJECTIDSTRING,	    --	Bestellungen	ABLAGE		
			@217    OBJECTIDSTRING,	    --	Hier werden die Bestellungen aufbewahrt
		@218    OBJECTIDSTRING,	    --	Eingangskorb	ABLAGE		
			@219    OBJECTIDSTRING,	    --	Rund um den Eingangskorb
		@220    OBJECTIDSTRING,	    --	Hotlist	ABLAGE		
			@221    OBJECTIDSTRING,	    --	Schneller Zugriff auf alle wichtigen Geschäftsobjekte
		@222    OBJECTIDSTRING,	    --	Neuigkeiten	ABLAGE		
			@223    OBJECTIDSTRING,	    --	Was gibt es Neues?
			@224    OBJECTIDSTRING,	    --	Was sind Neuigkeiten?
			@225    OBJECTIDSTRING,	    --	Erkennen von Neuigkeiten
			@226    OBJECTIDSTRING,	    --	Die Neuigkeitenablage
			@227    OBJECTIDSTRING,	    --	Ein Objekt als neu kennzeichnen
			@228    OBJECTIDSTRING,	    --	Das Neuigkeitensignal
			@229    OBJECTIDSTRING,	    --	Der Zeitraum für Neuigkeiten
		@230    OBJECTIDSTRING,	    --	Warenkorb	ABLAGE		
			@231    OBJECTIDSTRING,	    --	Rund um den Warenkorb
	
	@232    OBJECTIDSTRING,	    --	Weitere Informationen	ABLAGE		
		@233    OBJECTIDSTRING,	    --	Falls Sie noch Fragen haben...			
			@234    OBJECTIDSTRING,	    --	Adressen
		@235    OBJECTIDSTRING,	    --	Wenn Fehler auftreten...	ABLAGE		
			@236    OBJECTIDSTRING	    --	Supportformular

--- retrieving needed @90 and @53 from ibs_object

-- exec p_retValue 'Katalogverwaltung', @90 OUTPUT
-- EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT

select @userId,@c_TVHelpContainer as userid_helpcontainer

---------------------------------------------------Ebene1-----------------------------------------------------------------------------
    
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Die Privatansicht', @oid_s, 1, 0, '0x0000000000000000',
            'Rund um die Privatansicht...',
            @207 OUTPUT

select @207 as zweihundertsieben
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wozu eine Privatansicht?', @207, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Ansichten Ansicht Eingangskorb Ausgangskorb Bestellungen Warenkorb 
            Neuigkeiten Kennwort Benutzerprofil Benutzer Arbeitskorb',
            'Was kann die Privatansicht?', @208 OUTPUT
--------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Arbeitskorb', @207, 1, 0, '0x0000000000000000',
            'Rund um den Arbeitskorb...',
            @209 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_arbeitskorb.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ihre private Arbeitsablage', @209, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Arbeitskorb Objekte Objekt bearbeiten',
            'Was leistet der private Arbeitskorb?', @210 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Ausgangskorb', @207, 1, 0, '0x0000000000000000',
            'Rund um den Ausgangskorb...',
            @211 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_ausgangskorb.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Alles, was zur Gruppe geht...', @211, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Ausgangskorb Objekte Objekt bearbeiten',
            'Was leistet der Ausgangskorb?', @212 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Benutzerprofil', @207, 1, 0, '0x0000000000000000',
            'Rund um das Benutzerprofil...',
            @213 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_benutzerprofil.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Info zum Benutzerprofil', @213, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Benutzer Benutzerprofil Objekte Objekt bearbeiten',
            'Generelles.', @214 OUTPUT
---------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_kennwort.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Kennwort ändern', @213, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Benutzer Kennwort Benutzerprofil Objekte Objekt bearbeiten',
            'So ändern Sie Ihr Kennwort.', @215 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Bestellungen', @207, 1, 0, '0x0000000000000000',
            'Rund um die Bestellungen...',
            @216 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_bestellungen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Bestellungen aus dem Warenkorb', @216, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Bestellung Bestellungen Warenkorb Objekte Objekt bearbeiten',
            'Hier werden Ihre Bestellungen aufbewahrt.', @217 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Eingangskorb', @207, 1, 0, '0x0000000000000000',
            'Rund um den Eingangskorb...',
            @218 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_eingangskorb.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Rund um den Eingangskorb', @218, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Eingengskorb Objekte Objekt bearbeiten',
            'Da leistet Ihr Eingangskorb.', @219 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Hotlist', @207, 1, 0, '0x0000000000000000',
            'Rund um die hottesten Objekte...',
            @220 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_hotlist.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Wichtige Objekte', @220, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Hotlist Objekte Objekt bearbeiten',
            'Schneller Zugriff auf alle persönlich wichtigen Objekte.', @221 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Neuigkeiten', @207, 1, 0, '0x0000000000000000',
            'Rund um die Neuigkeiten...',
            @222 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_allg.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was gibt es Neues?', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Generelles zu den Neuigkeiten.', @223 OUTPUT

---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_was.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Was sind Neuigkeiten?', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Alles zum Thema neue Objekte im System.', @224 OUTPUT

---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_erkennen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Erkennen von Neuigkeiten', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten erkennen',
            'Wie erkennen Sie welche Objekte neu im System sind?', @225 OUTPUT
Print "some done(225)..."
  
---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_neuigkeitenablage.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Die Neuigkeitenablage', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            Neuigkeitenablage',
            'Wo werden die Neuigkeiten gelagert?', @226 OUTPUT

---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_kennzeichnen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Ein Objekt als neu kennzeichnen', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            kennzeichnen neu',
            'So wird das Objekt als ungelesen markiert.', @227 OUTPUT

---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_signal.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Das Neuigkeitensignal', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            Neuigkeitensignal',
            'Wie sieht die Markierung eines neuen Objektes aus?', @228 OUTPUT

---------------------------
    SELECT @tmpUrl = @l_helpPath + 'neu_zeitraum.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Der Zeitraum für Neuigkeiten', @222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Wie lange gelten Objekte als neu?', @229 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Warenkorb', @207, 1, 0, '0x0000000000000000',
            'Rund um den Warenkorb in der Privatansicht...',
            @230 OUTPUT
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    SELECT @tmpUrl = @l_helpPath + 'priv_warenkorb.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Rund um den Warenkorb', @230, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Privat Privatansicht Warenkorb Bestellung Bestellungen Ware Sortiment Objekte Objekt bearbeiten',
            'Das leistet Ihr privater Warenkorb.', @231 OUTPUT



---------------------------------------------------Ebene1-----------------------------------------------------------------------------

    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Weitere Informationen', @oid_s, 1, 0, '0x0000000000000000',
            'Zur weiteren Information...',
            @232 OUTPUT
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Falls Sie noch Fragen haben...', @232, 1, 0, '0x0000000000000000',
            'Adressen und Wichtiges zum System.',
            @233 OUTPUT
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
           
    SELECT @tmpUrl = @l_helpPath + 'weiter_adressen.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Adressen', @233, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Adressen Hilfe weiter',
            'So finden Sie uns.', @234 OUTPUT

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    EXEC p_Object$create @userId, 0x00000001, @c_TVHelpContainer,
            'Wenn Fehler auftreten...', @232, 1, 0, '0x0000000000000000',
            'Schicken Sie uns Ihre Bemerkungen und Anregungen, sowie die Fehlermeldungen...',
            @235 OUTPUT
          
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
           
    SELECT @tmpUrl = @l_helpPath + 'weiter_supportformular.htm'
    EXEC p_Help_01$createFast @userId, 0x00000001, @c_TVHelpObject,
            'Supportformular', @230, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', @tmpUrl,
            'Anfragen Fehler Support Supportformular',
            'Tragen Sie hier Ihre Fehlermeldungen ein und schicken Sie sie an uns.', @236 OUTPUT

--- commit and checkpoint
PRINT "** done(236)..."
GO

if exists (select * from sysobjects where id = object_id('dbo.p_retValue') and sysstat & 0xf = 4)
	drop procedure dbo.p_retValue
GO
 create procedure p_retValue (
     @input varchar (255),
     @oid_s OBJECTIDSTRING output
)
as
    DECLARE @oid OBJECTID

    --- get oid from desired object
    select @oid = oid
    FROM ibs_help_01
    WHERE goal like @input
    --- convert oid to oid_s

    if @oid = null select @oid = oid from ibs_object where name = @input
    IF @oid = null begin
       print "*** OID NOT RETRIEVEABLE***"
       print @input
    end

    EXEC p_byteToString @oid, @oid_s OUTPUT
    -- select @oid_s,@input as oid_input
    -- print "Leaving Procedure _retValue_..."
 GO

-- create References
-- retrieve Variables from database
PRINT "Creating new References..."

DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString

print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."
-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š
exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 

select @userid, @4, @32, @oid_s as p_help_01@createRef
			
	-- Neu im System
			--Was ist neu in dieser Release?
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @32, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @195, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @17, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @27, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @189, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @148, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @171, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @163, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @159, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @164, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @142, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @177, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @40, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @4, @41, @oid_s OUTPUT
print "vor login"		
	-- Die ersten Schritte
			-- Das Login
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @6, @7, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @6, @2, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @6, @4, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @6, @13, @oid_s OUTPUT
print "vor willkommen"
			-- Die Willkommenseite
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @6, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @2, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @13, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @7, @18, @oid_s OUTPUT

	-- Die Hilfe zur Hilfe
		-- Wie bediene ich Die Hilfe?
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @9, @10, @oid_s OUTPUT
		-- Wie finde ich was?
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @10, @9, @oid_s OUTPUT

		
-- Grundlegendes
	-- Ansichten im System
		-- Welche Ansichten gibt es? 
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @22, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @17, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @13, @18, @oid_s OUTPUT
			-- Gruppe
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @14, @13, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @14, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @14, @22, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @14, @54, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @14, @56, @oid_s OUTPUT
print "some 50 done...."

			-- Privat
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @15, @13, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @15, @208, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @15, @18, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @15, @22, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @15, @20, @oid_s OUTPUT

	-- Standardordner
		-- Welche Ordner gibt es im Basissystem?
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @20, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @20, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @20, @22, @oid_s OUTPUT
	
	-- Basisbegriffe
		-- Die wichtigesten Begriffe
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @7, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @27, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @54, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @208, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @24, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @17, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @32, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @21, @47, @oid_s OUTPUT

  	-- Geschäftsobjekte
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @24, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @24, @17, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @25, @24, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @25, @20, @oid_s OUTPUT

		-- Sichten auf ein Objekt
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @17, @13, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @17, @18, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @17, @27, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @18, @13, @oid_s OUTPUT

	-- Navigieren im System
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @27, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @27, @15, @oid_s OUTPUT

print "some 100 done..."

	-- Grundfunktionen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @30, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @30, @24, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @30, @22, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @31, @37, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @31, @38, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @31, @39, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @31, @40, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @31, @41, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @32, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @32, @35, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @32, @43, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @32, @32, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @33, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @33, @36, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @33, @62, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @33, @45, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @34, @34, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @34, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @34, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @34, @36, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @34, @43, @oid_s OUTPUT
		
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @35, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @35, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @35, @44, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @35, @43, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @36, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @36, @32, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @37, @38, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @37, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @37, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @37, @43, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @37, @32, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @38, @37, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @38, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @38, @42, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @39, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @39, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @39, @35, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @39, @43, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @40, @41, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @40, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @40, @31, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @41, @40, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @41, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @41, @31, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @42, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @42, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @42, @38, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @42, @42, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @42, @44, @oid_s OUTPUT
PRINT "some 150 done..."

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @43, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @43, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @43, @45, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @43, @44, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @44, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @44, @31, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @45, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @45, @31, @oid_s OUTPUT
-------------------------------------------------------------------------
-------------------------------------------------------------------------	
Print "**setting GO!"		
GO
-- retrieve Variables
-- if debug = "true" print "checkpoint!"
DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString

print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."

exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 

-- Rechteverwaltung
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @47, @48, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @47, @49, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @47, @50, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @48, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @48, @17, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @49, @47, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @49, @50, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @50, @57, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @50, @17, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @51, @48, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @51, @49, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @52, @50, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @52, @47, @oid_s OUTPUT

-- Die Gruppenansicht
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @54, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @54, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @54, @13, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @54, @20, @oid_s OUTPUT

print "some 200 done... "

	-- Benutzerverwaltung
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @56, @22, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @56, @6, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @56, @60, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @57, @56, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @57, @132, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @57, @134, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @57, @135, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @58, @57, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @58, @36, @oid_s OUTPUT

		-- Gruppen im System
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @60, @56, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @61, @56, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @61, @60, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @62, @58, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @62, @60, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @63, @60, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @63, @57, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @63, @61, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @64, @60, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @64, @62, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @64, @58, @oid_s OUTPUT

	-- Diskussionen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @66, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @66, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @66, @58, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @67, @22, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @67, @33, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @67, @69, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @67, @73, @oid_s OUTPUT

		-- Beiträge
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @69, @70, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @69, @73, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @69, @75, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @70, @70, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @70, @77, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @70, @75, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @71, @67, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @71, @74, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @71, @76, @oid_s OUTPUT
print "some 250 done..."
Print "**setting GO!"		
GO
-- retrieve Variables
-- if debug = "true" print "checkpoint!"
DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString

print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."

exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 

		-- Themen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @73, @69, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @73, @75, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @73, @66, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @74, @71, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @74, @76, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @75, @69, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @75, @77, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @75, @66, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @75, @67, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @76, @72, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @76, @74, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @76, @77, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @77, @66, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @77, @70, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @77, @79, @oid_s OUTPUT

	-- Termine
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @79, @82, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @79, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @79, @86, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @80, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @80, @83, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @80, @83, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @81, @80, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @81, @83, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @81, @84, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @81, @86, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @82, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @82, @83, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @82, @84, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @82, @86, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @83, @88, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @83, @89, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @83, @85, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @84, @83, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @84, @80, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @85, @82, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @85, @86, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @86, @82, @oid_s OUTPUT

		-- Termine mit mehreren Teilnehmern
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @88, @82, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @88, @79, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @88, @80, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @89, @82, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @89, @79, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @89, @88, @oid_s OUTPUT
print "some 300 done..."

	-- Katalogverwaltung
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @91, @106, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @91, @136, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @91, @20, @oid_s OUTPUT

		-- Produktschlüsselkategorien
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @93, @96, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @93, @99, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @93, @102, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @94, @93, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @94, @91, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @94, @96, @oid_s OUTPUT
	
		-- Produktschlüssel
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @96, @93, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @96, @99, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @96, @102, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @97, @96, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @97, @91, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @97, @93, @oid_s OUTPUT

		-- Warengruppe
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @99, @96, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @99, @93, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @99, @102, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @100, @102, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @100, @36, @oid_s OUTPUT

		-- Produktmarken
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @102, @96, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @102, @93, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @102, @99, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @103, @102, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @103, @36, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @104, @102, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @104, @36, @oid_s OUTPUT

	-- Warenangebote
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @106, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @106, @108, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @106, @116, @oid_s OUTPUT

		-- Umgang mit einem bestehenden Warenkatalog
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @108, @106, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @108, @119, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @108, @91, @oid_s OUTPUT
print "some 350 done..."
Print "**setting GO!"		
GO
-- retrieve Variables
-- if debug = "true" print "checkpoint!"
DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString


print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."

exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 


			-- Suchen von Waren
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @110, @43, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @110, @27, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @111, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @111, @31, @oid_s OUTPUT

			-- Bestellen von Waren
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @113, @217, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @113, @114, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @114, @113, @oid_s OUTPUT

		-- Anlegen eines Warenkatalogs
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @116, @108, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @116, @117, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @117, @106, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @117, @36, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @117, @122, @oid_s OUTPUT

print "some 400 done..."

		-- Anlegen von Waren
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @108, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @116, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @125, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @126, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @93, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @96, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @99, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @119, @102, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @120, @38, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @120, @32, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @120, @108, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @121, @42, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @121, @120, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @121, @217, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @122, @36, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @122, @117, @oid_s OUTPUT

		-- Preise festlegen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @124, @108, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @124, @119, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @125, @124, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @125, @119, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @126, @124, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @126, @119, @oid_s OUTPUT

	-- Stammdaten
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @128, @20, @oid_s OUTPUT

		-- Die Stammdatenablage
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @130, @34, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @130, @128, @oid_s OUTPUT

		-- Firma
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @132, @134, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @132, @128, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @132, @135, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @132, @136, @oid_s OUTPUT

		-- Person
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @134, @128, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @134, @132, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @134, @135, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @134, @136, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @135, @56, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @135, @128, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @136, @56, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @136, @128, @oid_s OUTPUT

	-- Data Interchange
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @138, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @138, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @138, @140, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @139, @138, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @139, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @139, @150, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @140, @142, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @140, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @140, @155, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @140, @172, @oid_s OUTPUT

		-- Konnektoren
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @142, @143, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @142, @144, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @142, @145, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @142, @146, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @143, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @143, @142, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @144, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @144, @142, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @145, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @145, @142, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @146, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @146, @142, @oid_s OUTPUT
print "some 450 done..."
Print "**setting GO!"		
GO
-- retrieve Variables
-- if debug = "true" print "checkpoint!"
DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString


print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."

exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 


		-- Das Log
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @148, @150, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @148, @154, @oid_s OUTPUT

		-- Agents
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @150, @151, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @150, @152, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @150, @148, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @151, @150, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @151, @152, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @151, @148, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @152, @150, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @152, @151, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @152, @148, @oid_s OUTPUT

		-- Der Import
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @154, @138, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @154, @150, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @154, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @154, @161, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @155, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @155, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @155, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @155, @172, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @156, @142, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @156, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @156, @157, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @157, @156, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @157, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @157, @159, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @157, @163, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @158, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @158, @156, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @158, @159, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @158, @142, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @159, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @159, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @159, @156, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @160, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @160, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @160, @138, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @161, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @161, @164, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @162, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @162, @154, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @163, @159, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @163, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @163, @156, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @164, @161, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @164, @160, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @164, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @164, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @164, @187, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @165, @150, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @165, @157, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @165, @162, @oid_s OUTPUT
print " some 500 done..."
			-- Spezielle Themen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @167, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @167, @157, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @168, @138, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @168, @140, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @169, @138, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @169, @140, @oid_s OUTPUT

		-- Der Export
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @171, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @171, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @171, @158, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @172, @155, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @172, @171, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @172, @34, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @173, @162, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @173, @171, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @173, @140, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @174, @158, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @174, @154, @oid_s OUTPUT

	-- Formularverwaltung
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @176, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @176, @140, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @177, @176, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @177, @182, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @177, @185, @oid_s OUTPUT

		-- Die Formularvorlagenablage
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @179, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @179, @180, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @179, @181, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @180, @179, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @180, @181, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @180, @176, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @181, @180, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @181, @140, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @182, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @182, @184, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @182, @179, @oid_s OUTPUT

		-- Das Formular
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @184, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @184, @182, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @184, @179, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @185, @180, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @185, @177, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @186, @161, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @186, @154, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @186, @157, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @187, @164, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @187, @157, @oid_s OUTPUT
print "some 550 done..."
Print "**setting GO!"		
GO
-- retrieve Variables
-- if debug = "true" print "checkpoint!"
DECLARE @1 ObjectIdString, @2 ObjectIdString, @3 ObjectIdString, @4 ObjectIdString, @5 ObjectIdString, @6 ObjectIdString, @7 ObjectIdString, @8 ObjectIdString, @9 ObjectIdString, @10 ObjectIdString, @11 ObjectIdString, @12 ObjectIdString, @13 ObjectIdString, @14 ObjectIdString, @15 ObjectIdString, @16 ObjectIdString, @17 ObjectIdString, @18 ObjectIdString, @19 ObjectIdString, 
@20 ObjectIdString, @21 ObjectIdString, @22 ObjectIdString, @23 ObjectIdString, @24 ObjectIdString, @25 ObjectIdString, @26 ObjectIdString, @27 ObjectIdString, @28 ObjectIdString, @29 ObjectIdString, 
@30 ObjectIdString, @31 ObjectIdString, @32 ObjectIdString, @33 ObjectIdString, @34 ObjectIdString, @35 ObjectIdString, @36 ObjectIdString, @37 ObjectIdString, @38 ObjectIdString, @39 ObjectIdString, 
@40 ObjectIdString, @41 ObjectIdString, @42 ObjectIdString, @43 ObjectIdString, @44 ObjectIdString, @45 ObjectIdString, @46 ObjectIdString, @47 ObjectIdString, @48 ObjectIdString, @49 ObjectIdString, 
@50 ObjectIdString, @51 ObjectIdString, @52 ObjectIdString, @53 ObjectIdString, @54 ObjectIdString, @55 ObjectIdString, @56 ObjectIdString, @57 ObjectIdString, @58 ObjectIdString, @59 ObjectIdString, 
@60 ObjectIdString, @61 ObjectIdString, @62 ObjectIdString, @63 ObjectIdString, @64 ObjectIdString, @65 ObjectIdString, @66 ObjectIdString, @67 ObjectIdString, @68 ObjectIdString, @69 ObjectIdString, 
@70 ObjectIdString, @71 ObjectIdString, @72 ObjectIdString, @73 ObjectIdString, @74 ObjectIdString, @75 ObjectIdString, @76 ObjectIdString, @77 ObjectIdString, @78 ObjectIdString, @79 ObjectIdString, 
@80 ObjectIdString, @81 ObjectIdString, @82 ObjectIdString, @83 ObjectIdString, @84 ObjectIdString, @85 ObjectIdString, @86 ObjectIdString, @87 ObjectIdString, @88 ObjectIdString, @89 ObjectIdString, 
@90 ObjectIdString, @91 ObjectIdString, @92 ObjectIdString, @93 ObjectIdString, @94 ObjectIdString, @95 ObjectIdString, @96 ObjectIdString, @97 ObjectIdString, @98 ObjectIdString, @99 ObjectIdString, 
@100 ObjectIdString, @101 ObjectIdString, @102 ObjectIdString, @103 ObjectIdString, @104 ObjectIdString, @105 ObjectIdString, @106 ObjectIdString, @107 ObjectIdString, @108 ObjectIdString, @109 ObjectIdString, 
@110 ObjectIdString, @111 ObjectIdString, @112 ObjectIdString, @113 ObjectIdString, @114 ObjectIdString, @115 ObjectIdString, @116 ObjectIdString, @117 ObjectIdString, @118 ObjectIdString, @119 ObjectIdString, 
@120 ObjectIdString, @121 ObjectIdString, @122 ObjectIdString, @123 ObjectIdString, @124 ObjectIdString, @125 ObjectIdString, @126 ObjectIdString, @127 ObjectIdString, @128 ObjectIdString, @129 ObjectIdString, 
@130 ObjectIdString, @131 ObjectIdString, @132 ObjectIdString, @133 ObjectIdString, @134 ObjectIdString, @135 ObjectIdString, @136 ObjectIdString, @137 ObjectIdString, @138 ObjectIdString, @139 ObjectIdString, 
@140 ObjectIdString, @141 ObjectIdString, @142 ObjectIdString, @143 ObjectIdString, @144 ObjectIdString, @145 ObjectIdString, @146 ObjectIdString, @147 ObjectIdString, @148 ObjectIdString, @149 ObjectIdString, 
@150 ObjectIdString, @151 ObjectIdString, @152 ObjectIdString, @153 ObjectIdString, @154 ObjectIdString, @155 ObjectIdString, @156 ObjectIdString, @157 ObjectIdString, @158 ObjectIdString, @159 ObjectIdString, 
@160 ObjectIdString, @161 ObjectIdString, @162 ObjectIdString, @163 ObjectIdString, @164 ObjectIdString, @165 ObjectIdString, @166 ObjectIdString, @167 ObjectIdString, @168 ObjectIdString, @169 ObjectIdString, 
@170 ObjectIdString, @171 ObjectIdString, @172 ObjectIdString, @173 ObjectIdString, @174 ObjectIdString, @175 ObjectIdString, @176 ObjectIdString, @177 ObjectIdString, @178 ObjectIdString, @179 ObjectIdString, 
@180 ObjectIdString, @181 ObjectIdString, @182 ObjectIdString, @183 ObjectIdString, @184 ObjectIdString, @185 ObjectIdString, @186 ObjectIdString, @187 ObjectIdString, @188 ObjectIdString, @189 ObjectIdString, 
@190 ObjectIdString, @191 ObjectIdString, @192 ObjectIdString, @193 ObjectIdString, @194 ObjectIdString, @195 ObjectIdString, @196 ObjectIdString, @197 ObjectIdString, @198 ObjectIdString, @199 ObjectIdString, 
@200 ObjectIdString, @201 ObjectIdString, @202 ObjectIdString, @203 ObjectIdString, @204 ObjectIdString, @205 ObjectIdString, @206 ObjectIdString, @207 ObjectIdString, @208 ObjectIdString, @209 ObjectIdString, 
@210 ObjectIdString, @211 ObjectIdString, @212 ObjectIdString, @213 ObjectIdString, @214 ObjectIdString, @215 ObjectIdString, @216 ObjectIdString, @217 ObjectIdString, @218 ObjectIdString, @219 ObjectIdString, 
@220 ObjectIdString, @221 ObjectIdString, @222 ObjectIdString, @223 ObjectIdString, @224 ObjectIdString, @225 ObjectIdString, @226 ObjectIdString, @227 ObjectIdString, @228 ObjectIdString, @229 ObjectIdString, 
@230 ObjectIdString, @231 ObjectIdString, @232 ObjectIdString, @233 ObjectIdString, @234 ObjectIdString, @235 ObjectIdString, @236 ObjectIdString


print "get parameters..."
declare @result int,
        @debug varchar(5),
        @l_domainName   NAME,
        @l_helpPath     VARCHAR (255),
        @c_TVHelpContainer	 TVERSIONID,
        @c_TVHelpObject 	 TVERSIONID,
        @domainPosNoPath POSNOPATH,
        @tmpUrl VARCHAR(255),
        @retVal INT,
        @userId USERID, 
        @op RIGHTS,
        @public OBJECTID, 
        @public_s OBJECTIDSTRING,
        @oid_s OBJECTIDSTRING
EXEC p_retrieveVariables 
		@debug OUTPUT, 
		@l_domainName OUTPUT, 
		@l_helpPath OUTPUT, 
		@c_TVHelpContainer OUTPUT, 
		@c_TVHelpObject OUTPUT,
		@domainPosNoPath OUTPUT,
      	@tmpUrl OUTPUT,
      	@retVal OUTPUT,
      	@userId OUTPUT, 
      	@op OUTPUT,
      	@public OUTPUT, 
      	@public_s OUTPUT,
        @oid_s OUTPUT

--IF @debug = "true" 
print "variable declaration succeeded..."
-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š
 exec p_retValue 'Willkommen im System', @1 OUTPUT 
 EXEC p_retValue 'Das System', @2 OUTPUT 
 EXEC p_retValue 'Neu im System', @3 OUTPUT 
 EXEC p_retValue 'Was ist neu in dieser Release?', @4 OUTPUT 
 EXEC p_retValue 'Die ersten Schritte', @5 OUTPUT 
 EXEC p_retValue 'Das Login', @6 OUTPUT 
 EXEC p_retValue 'Die Willkommenseite', @7 OUTPUT 
 EXEC p_retValue 'Die Hilfe zur Hilfe', @8 OUTPUT 
 EXEC p_retValue 'Wie bediene ich die Hilfe?', @9 OUTPUT 
 EXEC p_retValue 'Wie finde ich was?', @10 OUTPUT 
 EXEC p_retValue 'Grundlegendes', @11 OUTPUT 
 EXEC p_retValue 'Ansichten im System', @12 OUTPUT 
 EXEC p_retValue 'Welche Ansichten gibt es?', @13 OUTPUT 
 EXEC p_retValue 'Gruppe', @14 OUTPUT 
 EXEC p_retValue 'Privat', @15 OUTPUT 
 EXEC p_retValue 'Standardordner', @19 OUTPUT 
 EXEC p_retValue 'Welche Ordner gibt es im Basissystem?', @20 OUTPUT 
 EXEC p_retValue 'Basisbegriffe', @21 OUTPUT 
 EXEC p_retValue 'Die wichtigsten Begriffe', @22 OUTPUT 
 EXEC p_retValue 'Geschäftsobjekte', @23 OUTPUT 
 EXEC p_retValue 'Was ist ein Geschäftsobjekt?', @24 OUTPUT 
 EXEC p_retValue 'Welche Objekte gibt es?', @25 OUTPUT 
 EXEC p_retValue 'Sichten auf ein Objekt', @16 OUTPUT 
 EXEC p_retValue 'Welche Sichten bieten die Reiter?', @17 OUTPUT 
 EXEC p_retValue 'Aufteilung der Fenster', @18 OUTPUT 
 EXEC p_retValue 'Navigieren im System', @26 OUTPUT 
 EXEC p_retValue 'Wie gelange ich zu den Objekten und Ablagen?', @27 OUTPUT 
 EXEC p_retValue 'Grundfunktionen', @29 OUTPUT 
 EXEC p_retValue 'Welche Funktionen gibt es?', @30 OUTPUT 
 EXEC p_retValue 'Die Funktionsleiste', @31 OUTPUT 
 EXEC p_retValue 'Die Arbeit mit Listen', @32 OUTPUT 
 EXEC p_retValue 'Lesen von Objekten', @33 OUTPUT 
 EXEC p_retValue 'Erstellen von Objekten', @34 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Objekten', @35 OUTPUT 
 EXEC p_retValue 'Löschen von Objekten', @36 OUTPUT 
 EXEC p_retValue 'Ausschneiden und Verschieben von Objekten', @37 OUTPUT 
 EXEC p_retValue 'Objekte kopieren', @38 OUTPUT 
 EXEC p_retValue 'Verteilen von Objekten', @39 OUTPUT 
 EXEC p_retValue 'Checkout', @40 OUTPUT 
 EXEC p_retValue 'Checkin', @41 OUTPUT 
 EXEC p_retValue 'Erstellen eines Links', @42 OUTPUT 
 EXEC p_retValue 'Suche nach Objekten', @43 OUTPUT 
 EXEC p_retValue 'Drucken', @44 OUTPUT 
 EXEC p_retValue 'Speichern von Objekten', @45 OUTPUT 
 EXEC p_retValue 'Rechteverwaltung', @46 OUTPUT 
 EXEC p_retValue 'Was sind Rechte?', @47 OUTPUT 
 EXEC p_retValue 'Zuordnen von Rechten', @48 OUTPUT 
 EXEC p_retValue 'Rechtealiases', @49 OUTPUT 
 EXEC p_retValue 'Bearbeiten von Rechten', @50 OUTPUT 
 EXEC p_retValue 'Automatisches Zuordnen von Rechten auf ein Objekt', @51 OUTPUT 
 EXEC p_retValue 'Verändern der Rechte', @52 OUTPUT 
 EXEC p_retValue 'Die Gruppenansicht', @53 OUTPUT 
 EXEC p_retValue 'Wozu eine Gruppenansicht?', @54 OUTPUT 
 EXEC p_retValue 'Benutzerverwaltung', @55 OUTPUT 
 EXEC p_retValue '_berblick', @56 OUTPUT 
 EXEC p_retValue 'Anlegen eines neuen Benutzers', @57 OUTPUT 
 EXEC p_retValue 'Löschen eines Benutzers', @58 OUTPUT 
 EXEC p_retValue 'Gruppen im System', @59 OUTPUT 
 EXEC p_retValue 'Info über die Gruppen im System', @60 OUTPUT 
 EXEC p_retValue 'Zuordnen eines Benutzers zu einer Gruppe', @61 OUTPUT 
 EXEC p_retValue 'Löschen einer Zuordnung', @62 OUTPUT 
 EXEC p_retValue 'Anlegen einer Gruppe', @63 OUTPUT 
 EXEC p_retValue 'Löschen von Gruppen', @64 OUTPUT 
 EXEC p_retValue 'Diskussionen', @65 OUTPUT 
 EXEC p_retValue 'Was ist eine Diskussion?', @66 OUTPUT 
 EXEC p_retValue 'Ansehen einer Diskussion', @67 OUTPUT 
 EXEC p_retValue 'Beiträge', @68 OUTPUT 
 EXEC p_retValue 'Erstellen eines neuen Beitrags', @69 OUTPUT 
 EXEC p_retValue 'Antworten auf einen Beitrag', @70 OUTPUT 
 EXEC p_retValue 'Löschen von Beiträgen', @71 OUTPUT 
 EXEC p_retValue 'Themen', @72 OUTPUT 
 EXEC p_retValue 'Erstellen von Themen', @73 OUTPUT 
 EXEC p_retValue 'Löschen von Themen', @74 OUTPUT 
 EXEC p_retValue 'Erstellen einer Diskussion', @75 OUTPUT 
 EXEC p_retValue 'Löschen der Diskussion', @76 OUTPUT 
 EXEC p_retValue 'Ausschliessen eines Benutzers', @77 OUTPUT 
 EXEC p_retValue 'Termine', @78 OUTPUT 
 EXEC p_retValue 'Termine planen und verwalten', @79 OUTPUT 
 EXEC p_retValue 'Den Terminkalender ansehen', @80 OUTPUT 
 EXEC p_retValue 'Einen Terminkalender anlegen', @81 OUTPUT 
 EXEC p_retValue 'Was ist ein Termin?', @82 OUTPUT 
 EXEC p_retValue 'Anlegen eines Termins', @83 OUTPUT 
 EXEC p_retValue 'Einfügen von Terminen', @84 OUTPUT 
 EXEC p_retValue 'Löschen von Terminen', @85 OUTPUT 
 EXEC p_retValue 'Private Termine und Termine in der Gruppenansicht', @86 OUTPUT 
 EXEC p_retValue 'Termine mit mehreren Teilnehmern', @87 OUTPUT 
 EXEC p_retValue 'Anmelden zu einem Termin', @88 OUTPUT 
 EXEC p_retValue 'Abmelden von einem Termin', @89 OUTPUT 
 EXEC p_retValue 'Katalogverwaltung', @90 OUTPUT 
 EXEC p_retValue 'Was ist die Katalogverwaltung?', @91 OUTPUT 
 EXEC p_retValue 'Produktschlüsselkategorien', @92 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktschlüsselkategorie?', @93 OUTPUT 
 EXEC p_retValue 'Anlegen einer Kategorie', @94 OUTPUT 
 EXEC p_retValue 'Produktschlüssel', @95 OUTPUT 
 EXEC p_retValue 'Was ist ein Produktschlüssel?', @96 OUTPUT 
 EXEC p_retValue 'Anlegen eines Schlüssels', @97 OUTPUT 
 EXEC p_retValue 'Warengruppe', @98 OUTPUT 
 EXEC p_retValue 'Was ist eine Warengruppe?', @99 OUTPUT 
 EXEC p_retValue 'Anlegen einer Warengruppe', @100 OUTPUT 
 EXEC p_retValue 'Produktmarken', @101 OUTPUT 
 EXEC p_retValue 'Was ist eine Produktmarke?', @102 OUTPUT 
 EXEC p_retValue 'Anlegen der Produktmarke', @103 OUTPUT 
 EXEC p_retValue 'Löschen der Produktmarke', @104 OUTPUT 
 EXEC p_retValue 'Warenangebote', @105 OUTPUT 
 EXEC p_retValue 'Info über Warenangebote', @106 OUTPUT 
 EXEC p_retValue 'Umgang mit einem bestehenden Warenkorb', @107 OUTPUT 
 EXEC p_retValue 'Der Warenkorb', @108 OUTPUT 
 EXEC p_retValue 'Suchen von Waren', @109 OUTPUT 
 EXEC p_retValue 'Suchen über die Baumstruktur', @110 OUTPUT 
 EXEC p_retValue 'Die Schaltfläche "Suchen"', @111 OUTPUT 
 EXEC p_retValue 'Bestellen von Waren', @112 OUTPUT 
 EXEC p_retValue 'Der Status meiner Bestellung', @113 OUTPUT 
 EXEC p_retValue 'Ausdrucken der Bestellung', @114 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @115 OUTPUT 
 EXEC p_retValue 'Anlegen eines Warenkatalogs', @116 OUTPUT 
 EXEC p_retValue 'Löschen eines Warenkatalogs', @117 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren', @118 OUTPUT 
 EXEC p_retValue 'Wie lege ich Waren an?', @119 OUTPUT 
 EXEC p_retValue 'Kopieren einer Ware', @120 OUTPUT 
 EXEC p_retValue 'Einfügen einer Ware mit einem Link aus der Gruppenansicht', @121 OUTPUT 
 EXEC p_retValue 'Löschen von Waren aus dem System', @122 OUTPUT 
 EXEC p_retValue 'Preise festlegen', @123 OUTPUT 
 EXEC p_retValue 'Wozu Preise definieren?', @124 OUTPUT 
 EXEC p_retValue 'Anlegen von Sortimenten mit Profil und Schlüssel', @125 OUTPUT 
 EXEC p_retValue 'Anlegen von Waren mit Profil und Schlüssel', @126 OUTPUT 
 EXEC p_retValue 'Stammdaten', @127 OUTPUT 
 EXEC p_retValue 'Wozu Stammdaten?', @128 OUTPUT 
 EXEC p_retValue 'Die Stammdatenablage', @129 OUTPUT 
 EXEC p_retValue 'Anlegen der Stammdatenablage', @130 OUTPUT 
 EXEC p_retValue 'Firma', @131 OUTPUT 
 EXEC p_retValue 'Eine Firma anlegen', @132 OUTPUT 
 EXEC p_retValue 'Person', @133 OUTPUT 
 EXEC p_retValue 'Eine Person anlegen', @134 OUTPUT 
 EXEC p_retValue 'Stammdaten und Benutzerverwaltung', @135 OUTPUT 
 EXEC p_retValue 'Stammdaten und Katalogverwaltung', @136 OUTPUT 
 EXEC p_retValue 'Data Interchange', @137 OUTPUT 
 EXEC p_retValue '_berblick über den Data Interchange Bereich', @138 OUTPUT 
 EXEC p_retValue 'Die Ablage "Integrationsverwaltung"', @139 OUTPUT 
 EXEC p_retValue 'Objekte im Data Interchange Bereich', @140 OUTPUT 
 EXEC p_retValue 'Konnektoren', @141 OUTPUT 
 EXEC p_retValue 'Was ist ein Konnektor?', @142 OUTPUT 
 EXEC p_retValue 'Datei Konnektor', @143 OUTPUT 
 EXEC p_retValue 'FTP Konnektor', @144 OUTPUT 
 EXEC p_retValue 'Email Konnektor', @145 OUTPUT 
 EXEC p_retValue 'Konnektoren anlegen', @146 OUTPUT 
 EXEC p_retValue 'Das Log', @147 OUTPUT 
 EXEC p_retValue 'Die Aufzeichnung des Datenaustausches', @148 OUTPUT 
 EXEC p_retValue 'Agents', @149 OUTPUT 
 EXEC p_retValue 'Der automatische Datenaustausch', @150 OUTPUT 
 EXEC p_retValue 'Den Agent aufrufen', @151 OUTPUT 
 EXEC p_retValue 'Die Zeitsteuerung des Agents', @152 OUTPUT 
 EXEC p_retValue 'Der Import', @153 OUTPUT 
 EXEC p_retValue 'Der manuelle Datenaustausch', @154 OUTPUT 
 EXEC p_retValue 'Die Importablage', @155 OUTPUT 
 EXEC p_retValue 'Die Importfunktion', @156 OUTPUT 
 EXEC p_retValue 'Das Importskript', @157 OUTPUT 
 EXEC p_retValue 'Die Importmaske', @158 OUTPUT 
 EXEC p_retValue 'Was ist der Mehrfachupload?', @159 OUTPUT 
 EXEC p_retValue 'Import als Geschäftsobjekt', @160 OUTPUT 
 EXEC p_retValue 'Import von Formularen', @161 OUTPUT 
 EXEC p_retValue 'Das Import Dokument', @162 OUTPUT 
 EXEC p_retValue 'Der hierarchische Import', @163 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @164 OUTPUT 
 EXEC p_retValue 'Das Import DTD', @165 OUTPUT 
 EXEC p_retValue 'Spezielle Themen', @166 OUTPUT 
 EXEC p_retValue 'Das Import Szenario', @167 OUTPUT 
 EXEC p_retValue 'Key Mapper', @168 OUTPUT 
 EXEC p_retValue 'Key Domains', @169 OUTPUT 
 EXEC p_retValue 'Der Export', @170 OUTPUT 
 EXEC p_retValue 'Info über den Export', @171 OUTPUT 
 EXEC p_retValue 'Die Exportablage', @172 OUTPUT 
 EXEC p_retValue 'Das Exportdokument', @173 OUTPUT 
 EXEC p_retValue 'Die Exportmaske', @174 OUTPUT 
 EXEC p_retValue 'Formularverwaltung', @175 OUTPUT 
 EXEC p_retValue 'Zum Thema Formulare', @176 OUTPUT 
 EXEC p_retValue 'Feldtypen für Formulare', @177 OUTPUT 
 EXEC p_retValue 'Die Formularvorlagenablage', @178 OUTPUT 
 EXEC p_retValue 'Formularvorlagenablage', @179 OUTPUT 
 EXEC p_retValue 'Anlegen einer Formularvorlagenablage', @180 OUTPUT 
 EXEC p_retValue 'Bearbeiten einer Formularvorlagenablage', @181 OUTPUT 
 EXEC p_retValue 'Die Formularvorlage', @182 OUTPUT 
 EXEC p_retValue 'Das Formular', @183 OUTPUT 
 EXEC p_retValue 'Was ist ein Formular?', @184 OUTPUT 
 EXEC p_retValue 'Anlegen eines Formulars', @185 OUTPUT 
 EXEC p_retValue 'Import eines Formulars', @186 OUTPUT 
 EXEC p_retValue 'Das Importieren von Reitern', @187 OUTPUT 
 EXEC p_retValue 'Workflow', @188 OUTPUT 
 EXEC p_retValue 'Wissenswertes zum Thema Workflow', @189 OUTPUT 
 EXEC p_retValue 'Grundbegriffe des Workflow', @190 OUTPUT 
 EXEC p_retValue 'Anlegen einer Workflowvorlagenablage', @191 OUTPUT 
 EXEC p_retValue 'Starten des Workflow', @192 OUTPUT 
 EXEC p_retValue 'Anlegen eines Workflow', @193 OUTPUT 
 EXEC p_retValue 'Domänenverwaltung', @194 OUTPUT 
 EXEC p_retValue 'Was ist eine Domäne?', @195 OUTPUT 
 EXEC p_retValue 'Die Systemadministratorensicht', @196 OUTPUT 
 EXEC p_retValue 'Das Login', @197 OUTPUT 
 EXEC p_retValue 'Die Systemadmin-Gruppenansicht', @198 OUTPUT 
 EXEC p_retValue 'Anlegen eines Domänenschemas', @199 OUTPUT 
 EXEC p_retValue 'Anlegen einer Standard Domäne', @200 OUTPUT 
 EXEC p_retValue 'Includes', @201 OUTPUT 
 EXEC p_retValue 'Die neue Domäne', @202 OUTPUT 
 EXEC p_retValue 'Erstes Login', @203 OUTPUT 
 EXEC p_retValue 'Ordnerstrukturen in der Gruppenansicht anlegen', @204 OUTPUT 
 EXEC p_retValue 'Import von Strukturen', @205 OUTPUT 
 EXEC p_retValue 'Die Domäne löschen', @206 OUTPUT 
 EXEC p_retValue 'Die Privatansicht', @207 OUTPUT 
 EXEC p_retValue 'Wozu eine Privatansicht?', @208 OUTPUT 
 EXEC p_retValue 'Arbeitskorb', @209 OUTPUT 
 EXEC p_retValue 'Ihre private Arbeitsablage', @210 OUTPUT 
 EXEC p_retValue 'Ausgangskorb', @211 OUTPUT 
 EXEC p_retValue 'Alles, was zur Gruppe geht...', @212 OUTPUT 
 EXEC p_retValue 'Benutzerprofil', @213 OUTPUT 
 EXEC p_retValue 'Info zum Benutzerprofil', @214 OUTPUT 
 EXEC p_retValue 'Das Kennwort ändern', @215 OUTPUT 
 EXEC p_retValue 'Bestellungen', @216 OUTPUT 
 EXEC p_retValue 'Bestellungen aus dem Warenkorb', @217 OUTPUT 
 EXEC p_retValue 'Eingangskorb', @218 OUTPUT 
 EXEC p_retValue 'Rund um den Eingangskorb', @219 OUTPUT 
 EXEC p_retValue 'Hotlist', @220 OUTPUT 
 EXEC p_retValue 'Wichtige Objekte', @221 OUTPUT 
 EXEC p_retValue 'Neuigkeiten', @222 OUTPUT 
 EXEC p_retValue 'Was gibt es Neues?', @223 OUTPUT 
 EXEC p_retValue 'Was sind Neuigkeiten?', @224 OUTPUT 
 EXEC p_retValue 'Erkennen von Neuigkeiten', @225 OUTPUT 
 EXEC p_retValue 'Die Neuigkeitenablage', @226 OUTPUT 
 EXEC p_retValue 'Ein Objekt als neu kennzeichnen', @227 OUTPUT 
 EXEC p_retValue 'Das Neuigkeitensignal', @228 OUTPUT 
 EXEC p_retValue 'Der Zeitraum für Neuigkeiten', @229 OUTPUT 
 EXEC p_retValue 'Warenkorb', @230 OUTPUT 
 EXEC p_retValue 'Rund um den Warenkorb', @231 OUTPUT 
 EXEC p_retValue 'Weitere Informationen', @232 OUTPUT 
 EXEC p_retValue 'Falls Sie noch Fragen haben...', @233 OUTPUT 
 EXEC p_retValue 'Adressen', @234 OUTPUT 
 EXEC p_retValue 'Wenn Fehler auftreten...', @235 OUTPUT 
 EXEC p_retValue 'Supportformular', @236 OUTPUT 

 select @userid, @189, @139, @oid_s as info
	-- Workflow
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @189, @139, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @189, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @189, @190, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @190, @140, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @190, @192, @oid_s OUTPUT
		
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @191, @180, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @191, @189, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @192, @193, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @192, @189, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @193, @192, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @193, @190, @oid_s OUTPUT

	-- Domänenverwaltung
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @195, @6, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @195, @196, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @196, @198, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @196, @196, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @197, @203, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @197, @196, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @197, @198, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @198, @196, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @198, @195, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @198, @200, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @199, @195, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @199, @200, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @199, @201, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @200, @195, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @200, @199, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @200, @201, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @201, @195, @oid_s OUTPUT

		-- Die neue Domäne
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @203, @200, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @203, @197, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @204, @198, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @204, @203, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @205, @163, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @205, @154, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @206, @36, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @206, @195, @oid_s OUTPUT
print "some 600 done..."
-- Die Privatansicht
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @208, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @208, @14, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @208, @13, @oid_s OUTPUT

	-- Arbeitskorb
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @210, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @210, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @210, @25, @oid_s OUTPUT

	-- Ausgangskorb
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @212, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @212, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @212, @20, @oid_s OUTPUT

	-- Benutzerprofil
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @214, @56, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @214, @215, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @215, @6, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @215, @214, @oid_s OUTPUT

	-- Bestellungen
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @217, @113, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @217, @106, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @217, @231, @oid_s OUTPUT

	-- Eingangskorb
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @219, @30, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @219, @31, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @219, @15, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @219, @20, @oid_s OUTPUT

	-- Hotlist
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @221, @24, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @221, @27, @oid_s OUTPUT

	-- Neuigkeiten
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @223, @7, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @223, @228, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @224, @25, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @224, @223, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @225, @7, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @225, @224, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @226, @20, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @226, @224, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @227, @224, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @227, @223, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @228, @225, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @228, @229, @oid_s OUTPUT

		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @229, @226, @oid_s OUTPUT
print "some 650 done..."
	-- Warenkorb
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @231, @217, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @231, @113, @oid_s OUTPUT
		EXEC p_Help_01$createRef @userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', @231, @106, @oid_s OUTPUT
print "** succeeded and finished inserting refernces"
GO
-- Weitere Informationen
	-- Falls Sie noch Fragen haben...

	-- Wenn Fehler auftreten...

PRINT "Domain helpinstall performed!"

if exists (select * from sysobjects where id = object_id('dbo.p_retrieveVariables') and sysstat & 0xf = 4)
	drop procedure dbo.p_retrieveVariables
GO
if exists (select * from sysobjects where id = object_id('dbo.p_retValue') and sysstat & 0xf = 4)
	drop procedure dbo.p_retValue
GO

-- show count messages again:
SET NOCOUNT OFF
GO
