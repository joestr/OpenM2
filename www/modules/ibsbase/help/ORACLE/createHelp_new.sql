/**************************************************************************
 * Install Help.
 *
 * @version     2.2.0001, 000626
 * @author      Centner Martin (CM);  990719
 *
 * Necessary to do before installation:
 *   + Edit help path.
 *   + Edit name of the domain.
 *
 * Script can be used as update script -> deletes old help structure.
 *
 * <DT><B>Updates:</B>
 * <DD> MM 000626   created file from old CM file with new structure
 **************************************************************************/

-- prompt Creating help structure;

-- declare variables:
DECLARE /*----------------------CHANGEABLE PART BEGIN---------------------*/
        l_domainName        VARCHAR2(63)  := 'ibs';
        l_helpPath          VARCHAR2(255) := 'http://linux/m2/help/';
        /*----------------------CHANGEABLE PART END ----------------------*/
        l_domainPosNoPath   RAW(254);
        l_retVal            INTEGER;
        l_userid            INTEGER;
        l_op                INTEGER;
        l_public            RAW(8);
        l_public_s          VARCHAR2(18);
        l_oid				RAW(8);
        l_oid_s             VARCHAR2(18);
        c_TVHelpContainer   CONSTANT INTEGER := 16875265;  -- HelpContainer
        c_TVHelpObject      CONSTANT INTEGER := 16875281;  -- HelpObject
 
   l_1  VARCHAR2(18);	    -- Willkommen im System		ABLAGE
        l_2  VARCHAR2(18);     -- Das System
        l_3  VARCHAR2(18);	    -- Neu im Sytem		ABLAGE
            l_4  VARCHAR2(18);     -- Was ist neu in dieser Release?
        l_5  VARCHAR2(18);	    -- Die ersten Schritte	ABLAGE
            l_6  VARCHAR2(18);     -- Das Login
            l_7  VARCHAR2(18);     -- Die Willkommensseite
        l_8  VARCHAR2(18);     -- Die Hilfe zur Hilfe	ABLAGE
            l_9  VARCHAR2(18);     -- Wie bediene ich die Hilfe?
            l_10 VARCHAR2(18);     -- Wie finde ich was?
    
    l_11 VARCHAR2(18);     --  Grundlegendes
		l_12  VARCHAR2(18);	    -- 	Ansichten im System	ABLAGE		
			l_13  VARCHAR2(18);	    -- 	Welche Ansichten gibt es?
			l_14  VARCHAR2(18);	    -- 	Gruppe
			l_15  VARCHAR2(18);	    -- 	Privat
		l_19  VARCHAR2(18);	    -- 	Standardordner	ABLAGE		
			l_20  VARCHAR2(18);	    -- 	Welche Ordner gibt es im Basissystem?
		l_21  VARCHAR2(18);	    -- 	Basisbegriffe	ABLAGE		
			l_22  VARCHAR2(18);	    -- 	Die wichtigsten Begriffe
		l_23  VARCHAR2(18);	    -- 	Geschõftsobjekte	ABLAGE		
			l_24  VARCHAR2(18);	    -- 	Was ist ein Geschõftsobjekt?
				l_25  VARCHAR2(18);	    -- 	Welche Objekte gibt es?
				l_16  VARCHAR2(18);	    -- 	Sichten auf ein Objekt	ABLAGE		
				l_17  VARCHAR2(18);	    -- 	Welche Sichten bieten die Reiter?
				l_18  VARCHAR2(18);	    -- 	Aufteilung der Fenster
		l_26  VARCHAR2(18);	    -- 	Navigieren im System	ABLAGE		
			l_27  VARCHAR2(18);	    -- 	Wie gelange ich zu den Objekten und Ablagen?
		
		l_29  VARCHAR2(18);	    -- 	Grundfunktionen	ABLAGE		
			l_30  VARCHAR2(18);	    -- 	Welche Funktionen gibt es?
			l_31  VARCHAR2(18);	    -- 	Die Funktionsleiste
			l_32  VARCHAR2(18);	    -- 	Die Arbeit mit Listen
			l_33  VARCHAR2(18);	    -- 	Lesen von Objekten
			l_34  VARCHAR2(18);	    -- 	Erstellen von Objekten
			l_35  VARCHAR2(18);	    -- 	Bearbeiten von Objekten
			l_36  VARCHAR2(18);	    -- 	L÷schen von Objekten
			l_37  VARCHAR2(18);	    -- 	Ausschneiden und Verschieben von Objekten
			l_38  VARCHAR2(18);	    -- 	Objekte kopieren
			l_39  VARCHAR2(18);	    -- 	Verteilen von Objekten
			l_40	 VARCHAR2(18);	    -- 	Checkout
			l_41  VARCHAR2(18);	    -- 	Checkin
			l_42  VARCHAR2(18);	    -- 	Erstellen eines Links
			l_43  VARCHAR2(18);	    -- 	Suchen nach Objekten
			l_44  VARCHAR2(18);	    -- 	Drucken
			l_45  VARCHAR2(18);	    -- 	Speichern von Objekten
		l_46  VARCHAR2(18);	    -- 	Rechteverwaltung	ABLAGE		
			l_47  VARCHAR2(18);	    -- 	Was sind Rechte?
			l_48  VARCHAR2(18);	    -- 	Zuordnen von Rechten
			l_49  VARCHAR2(18);	    -- 	Rechtealiases
			l_50  VARCHAR2(18);	    -- 	Bearbeiten von Rechten
			l_51  VARCHAR2(18);	    -- 	Automatisches Zuordnen von Rechten auf ein Objekt
			l_52  VARCHAR2(18);	    -- 	Verõndern der Rechte

	l_53  VARCHAR2(18);	    -- 	Die Gruppenansicht	ABLAGE		
		l_54  VARCHAR2(18);	    -- 	Wozu eine Gruppenansicht?
		l_55  VARCHAR2(18);	    -- 	Benutzerverwaltung	ABLAGE		
			l_56  VARCHAR2(18);	    -- 	_berblick
			l_57  VARCHAR2(18);	    -- 	Anlegen eines neuen Benutzers
			l_58  VARCHAR2(18);	    -- 	L÷schen eines Benutzers
			l_59  VARCHAR2(18);	    -- 	Gruppen im System	ABLAGE		
				l_60  VARCHAR2(18);	    -- 	Info ³ber Gruppen im System
				l_61  VARCHAR2(18);	    -- 	Zuordnen eines Benutzers zu einer Gruppe
				l_62  VARCHAR2(18);	    -- 	L÷schen der Zuordnung
				l_63  VARCHAR2(18);	    -- 	Anlegen einer Gruppe
				l_64  VARCHAR2(18);	    -- 	L÷schen von Gruppen
		l_65  VARCHAR2(18);	    -- 	Diskussionen	ABLAGE		
			l_66  VARCHAR2(18);	    -- 	Was ist eine Diskussion?
			l_67  VARCHAR2(18);	    -- 	Ansehen einer Diskussion
			l_68  VARCHAR2(18);	    -- 	Beitrõge	ABLAGE		
				l_69  VARCHAR2(18);	    -- 	Erstellen eines neuen Beitrags
				l_70  VARCHAR2(18);	    -- 	Antworten auf einen Beitrag
				l_71  VARCHAR2(18);	    -- 	L÷schen von Beitrõgen
			l_72  VARCHAR2(18);	    -- 	Themen	ABLAGE		
				l_73  VARCHAR2(18);	    -- 	Erstellen von Themen
				l_74  VARCHAR2(18);	    -- 	L÷schen von Themen
			l_75  VARCHAR2(18);	    -- 	Erstellen einer Diskussion
			l_76  VARCHAR2(18);	    -- 	L÷schen der Diskussion
			l_77  VARCHAR2(18);	    -- 	Ausschliessen eines Benutzers
		l_78  VARCHAR2(18);	    -- 	Termine	ABLAGE		
			l_79  VARCHAR2(18);	    -- 	Termine planen und verwalten
			l_80  VARCHAR2(18);	    -- 	Den Terminkalender ansehen
			l_81  VARCHAR2(18);	    -- 	Einen Terminkalender anlegen
			l_82  VARCHAR2(18);	    -- 	Was ist ein Termin?
			l_83  VARCHAR2(18);	    -- 	Anlegen eines Termins
			l_84	 VARCHAR2(18);	    -- 	Einf³gen von Terminen
			l_85  VARCHAR2(18);	    -- 	L÷schen eines Termins
			l_86  VARCHAR2(18);	    -- 	Private Termine und Termine in der Gruppenansicht
			l_87  VARCHAR2(18);	    -- 	Termine mit mehreren Teilnehmern	ABLAGE		
				l_88  VARCHAR2(18);	    -- 	Anmelden zu einem Termin
				l_89  VARCHAR2(18);	    -- 	Abmelden von einem Termin
		l_90  VARCHAR2(18);	    -- 	Katalogverwaltung	ABLAGE		
			l_91  VARCHAR2(18);	    -- 	Was ist die Katalogverwaltung?
			l_92  VARCHAR2(18);	    -- 	Produktschl³sselkategorien	ABLAGE		
				l_93  VARCHAR2(18);	    -- 	Was ist eine Produktschl³sselkategorie?
				l_94  VARCHAR2(18);	    -- 	Anlegen einer Kategorie
			l_95  VARCHAR2(18);	    -- 	Produktschl³ssel	ABLAGE		
				l_96  VARCHAR2(18);	    -- 	Was ist ein Produktschl³ssel?
				l_97  VARCHAR2(18);	    -- 	Anlegen eines Schl³ssels
			l_98  VARCHAR2(18);	    -- 	Warengruppe	ABLAGE		
				l_99  VARCHAR2(18);	    -- 	Was ist eine Warengruppe?
				l_100    VARCHAR2(18)	    -- 	Anlegen einer Warengruppe
 			l_101    VARCHAR2(18);	    --	Produktmarken	ABLAGE		
				l_102    VARCHAR2(18);	    --	Was ist eine Produktmarke?
				l_103    VARCHAR2(18);	    --	Anlegen der Produktmarke
				l_104    VARCHAR2(18);	    --	L÷schen der Produktmarke
		l_105    VARCHAR2(18);	    --	Warenangebote	ABLAGE		
			l_106    VARCHAR2(18);	    --	Info ³ber Warenangebote
			l_107    VARCHAR2(18);	    --	Umgang mit einem bestehenden Warenkatalog	ABLAGE		
				l_108    VARCHAR2(18);	    --	Der Warenkorb
				l_109    VARCHAR2(18);	    --	Suchen von Waren	ABLAGE		
					l_110    VARCHAR2(18);	    --	Suchen ³ber die Baumstruktur
					l_111    VARCHAR2(18);	    --	Die Schaltflõche "Suchen"
				l_112    VARCHAR2(18);	    --	Bestellen von Waren	ABLAGE		
					l_113    VARCHAR2(18);	    --	Der Status meiner Bestellung
					l_114    VARCHAR2(18);	    --	Ausdrucken der Bestellung
			l_115    VARCHAR2(18);	    --	Anlegen eines Warenkatalogs	ABLAGE		
				l_116    VARCHAR2(18);	    --	Anlegen eines Warenkatalogs
				l_117    VARCHAR2(18);	    --	L÷schen des Warenkatalogs
			l_118    VARCHAR2(18);	    --	Anlegen von Waren	ABLAGE		
				l_119    VARCHAR2(18);	    --	Wie lege ich Waren an?
				l_120    VARCHAR2(18);	    --	Kopieren einer Ware
				l_121    VARCHAR2(18);	    --	Einf³gen einer Ware mit einem Link aus der Gruppenansicht
				l_122    VARCHAR2(18);	    --	L÷schen von Waren aus dem System
				l_123    VARCHAR2(18);	    --	Preise festlegen	ABLAGE		
					l_124    VARCHAR2(18);	    --	Wozu Preise definieren?
					l_125    VARCHAR2(18);	    --	Anlegen von Sortimenten mit Profil und Schl³ssel
					l_126    VARCHAR2(18);	    --	Anlegen von Waren mit Profil und Schl³ssel
		l_127    VARCHAR2(18);	    --	Stammdaten	ABLAGE		
			l_128    VARCHAR2(18);	    --	Wozu Stammdaten?
			l_129    VARCHAR2(18);	    --	Die Stammdatenablage	ABLAGE		
				l_130    VARCHAR2(18);	    --	Anlegen der Stammdatenablage
			l_131    VARCHAR2(18);	    --	Firma	ABLAGE		
				l_132    VARCHAR2(18);	    --	Eine Fima anlegen
			l_133    VARCHAR2(18);	    --	Person	ABLAGE		
				l_134    VARCHAR2(18);	    --	Eine Person anlegen
			l_135    VARCHAR2(18);	    --	Stammdaten und Benutzerverwaltung
			l_136    VARCHAR2(18);	    --	Stammdaten und Katalogverwaltung
		l_137    VARCHAR2(18);	    --	Data Interchange	ABLAGE		
			l_138    VARCHAR2(18);	    --	_berblick ³ber den Data Interchange Bereich
			l_139    VARCHAR2(18);	    --	Die Ablage "Integrationsverwaltung"
			l_140    VARCHAR2(18);	    --	Objekte im Data Interchange Bereich
			l_141    VARCHAR2(18);	    --	Konnektoren	ABLAGE		
				l_142    VARCHAR2(18);	    --	Was ist ein Konnektor?
				l_143    VARCHAR2(18);	    --	Datei Konnektor
				l_144    VARCHAR2(18);	    --	FTP Konnektor
				l_145    VARCHAR2(18);	    --	Email Konnektor
				l_146    VARCHAR2(18);	    --	Konnektoren anlegen
			l_147    VARCHAR2(18);	    --	Das Log	ABLAGE		
				l_148    VARCHAR2(18);	    --	Die Aufzeichnung des Datenmaustausches
			l_149    VARCHAR2(18);	    --	Agents	ABLAGE		
				l_150    VARCHAR2(18);	    --	Der automatisierte Datenaustausch
				l_151    VARCHAR2(18);	    --	Den Agent aufrufen
				l_152    VARCHAR2(18);	    --	Die Zeitsteuerung des Agents
			l_153    VARCHAR2(18);	    --	Der Import	ABLAGE		
				l_154    VARCHAR2(18);	    --	Der manuelle Datenaustausch
				l_155	VARCHAR2(18);	    --	Die Importablagen
				l_156    VARCHAR2(18);	    --	Die Importfunktion
				l_157    VARCHAR2(18);	    --	Das Importskript
				l_158    VARCHAR2(18);	    --	Die Importmaske
				l_159    VARCHAR2(18);	    --	Was ist der Mehrfachupload?
				l_160    VARCHAR2(18);	    --	Import als Geschõftsobjekt
				l_161    VARCHAR2(18);	    --	Import von Formularen
				l_162    VARCHAR2(18);	    --	Das Import Dokument
				l_163    VARCHAR2(18);	    --	Der hierarchische Import
				l_164    VARCHAR2(18);	    --	Das Importieren von Reitern
				l_165    VARCHAR2(18);	    --	Das Import DTD
				l_166    VARCHAR2(18);	    --	Spezielle Themen	ABLAGE		
					l_167    VARCHAR2(18);	    --	Das Import Szenario
					l_168    VARCHAR2(18);	    --	Key Mapper
					l_169    VARCHAR2(18);	    --	Key Domains
			l_170    VARCHAR2(18);	    --	Der Export	ABLAGE		
				l_171    VARCHAR2(18);	    --	Info ³ber den Export
				l_172    VARCHAR2(18);	    --	Die Exportablage
				l_173    VARCHAR2(18);	    --	Das Export Dokument
				l_174    VARCHAR2(18);	    --	Die Exportmaske
		l_175    VARCHAR2(18);	    --	Formularverwaltung	ABLAGE		
			l_176    VARCHAR2(18);	    --	Zum Thema Formulare
			l_177    VARCHAR2(18);	    --	Feldtypen f³r Formulare
			l_178    VARCHAR2(18);	    --	Die Formularvorlagenablage	ABLAGE		
				l_179    VARCHAR2(18);	    --	Formularvorlagenablage
				l_180    VARCHAR2(18);	    --	Anlegen einer Formularvorlagenablage
				l_181    VARCHAR2(18);	    --	Bearbeiten einer Formularvorlagenablage
				l_182    VARCHAR2(18);	    --	Die Formularvorlage
			l_183    VARCHAR2(18);	    --	Das Formular	ABLAGE		
				l_184    VARCHAR2(18);	    --	Was ist ein Formular?
				l_185    VARCHAR2(18);	    --	Anlegen eines Formulars
				l_186    VARCHAR2(18);	    --	Import eines Formulars
				l_187    VARCHAR2(18);	    --	Das Importieren von Reitern
		l_188    VARCHAR2(18);	    --	Workflow	ABLAGE		
			l_189    VARCHAR2(18);	    --	Wissenswertes zum Thema Workflow
			l_190    VARCHAR2(18);	    --	Grundbegriffe des Workflow
			l_191    VARCHAR2(18);	    --	Anlegen einer Workflowvorlagenablage
			l_192    VARCHAR2(18);	    --	Starten des Workflows
			l_193    VARCHAR2(18);	    --	Anlegen eines Workflows
		l_194    VARCHAR2(18);	    --	Domõnenverwaltung	ABLAGE		
			l_195    VARCHAR2(18);	    --	Was ist eine Domõne?
			l_196    VARCHAR2(18);	    --	Die Systemadministratorensicht
			l_197    VARCHAR2(18);	    --	Das Login
			l_198    VARCHAR2(18);	    --	Die Systemandmin-Gruppenansicht
			l_199    VARCHAR2(18);	    --	Anlegen eines Domõnenschemas
			l_200    VARCHAR2(18);	    --	Anlegen einer Standard Domõne
			l_201    VARCHAR2(18);	    --	Includes
			l_202    VARCHAR2(18);	    --	Die neue Domõne	ABLAGE		
				l_203    VARCHAR2(18);	    --	Erstes Login
				l_204    VARCHAR2(18);	    --	Ordnerstruktur in der Gruppenansicht anlegen
				l_205    VARCHAR2(18);	    --	Import von Strukturen
				l_206    VARCHAR2(18)	    --	Die Domõne l÷schen
 	l_207    VARCHAR2(18);	    --	Die Privatansicht	ABLAGE		
		l_208    VARCHAR2(18);	    --	Wozu eine Privatansicht?
		l_209    VARCHAR2(18);	    --	Arbeitskorb	ABLAGE		
			l_210    VARCHAR2(18);	    --	Ihre private Arbeitsablage
		l_211    VARCHAR2(18);	    --	Ausgangskorb	ABLAGE		
			l_212    VARCHAR2(18);	    --	Alles; was zur Gruppe geht...
		l_213    VARCHAR2(18);	    --	Benutzerprofil	ABLAGE		
			l_214    VARCHAR2(18);	    --	Info zum Benutzerprofil
			l_215    VARCHAR2(18);	    --	Das Kennwort õndern
		l_216    VARCHAR2(18);	    --	Bestellungen	ABLAGE		
			l_217    VARCHAR2(18);	    --	Hier werden die Bestellungen aufbewahrt
		l_218    VARCHAR2(18);	    --	Eingangskorb	ABLAGE		
			l_219    VARCHAR2(18);	    --	Rund um den Eingangskorb
		l_220    VARCHAR2(18);	    --	Hotlist	ABLAGE		
			l_221    VARCHAR2(18);	    --	Schneller Zugriff auf alle wichtigen Geschõftsobjekte
		l_222    VARCHAR2(18);	    --	Neuigkeiten	ABLAGE		
			l_223    VARCHAR2(18);	    --	Was gibt es Neues?
			l_224    VARCHAR2(18);	    --	Was sind Neuigkeiten?
			l_225    VARCHAR2(18);	    --	Erkennen von Neuigkeiten
			l_226    VARCHAR2(18);	    --	Die Neuigkeitenablage
			l_227    VARCHAR2(18);	    --	Ein Objekt als neu kennzeichnen
			l_228    VARCHAR2(18);	    --	Das Neuigkeitensignal
			l_229    VARCHAR2(18);	    --	Der Zeitraum f³r Neuigkeiten
		l_230    VARCHAR2(18);	    --	Warenkorb	ABLAGE		
			l_231    VARCHAR2(18);	    --	Rund um den Warenkorb
	
	l_232    VARCHAR2(18);	    --	Weitere Informationen	ABLAGE		
		l_233    VARCHAR2(18);	    --	Falls Sie noch Fragen haben...			
			l_234    VARCHAR2(18);	    --	Adressen
		l_235    VARCHAR2(18);	    --	Wenn Fehler auftreten...	ABLAGE		
			l_236    VARCHAR2(18);	    --	Supportformular

BEGIN
        
-- get help-tree root
  select oid  into l_oid
  from ibs_object 
  where name like 'hilfe';

  p_byteToString (l_oid, l_oid_s);

-- get domain data
  SELECT  d.adminId, d.publicOid INTO    l_uid, l_public
  FROM    ibs_Domain_01 d, ibs_Object o
  WHERE   d.oid = o.oid
          AND o.name LIKE ('%' || l_domainName || '%');

   p_byteToString (l_public, l_public_s);

Debug ('Deleting old help structure...');

-- delete old helpstructure
  -- helpfiles
  DELETE ibs_Object WHERE tVersionId = c_TVHelpObject;
  -- helpcontainer
  DELETE ibs_Object WHERE tVersionId = c_TVHelpContainer;

Debug ('Create new help structures...');
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das System', l_1, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_system.htm',
            'Verzeichnisbaum Gruppe Privat Gruppenansicht Privatansicht',
            'Was ist das Besondere an der Applikation m2?', l_2 );

-----------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Neu im System', l_1, 1, 0, '0x0000000000000000',
            'Alles Neue in dieser Release.',
            l_3 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist neu in dieser Release?', l_3, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_neu.htm',
            'Listen Funktionen Domõnenverwaltung Reiter Verzeichnisbaum Log Export Import hierarchischer Import 
            Mehrfachupload',
            'Die Neuigkeiten von Release 2.0', l_4 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die ersten Schritte', l_1, 1, 0, '0x0000000000000000',
            'Step by step durch die Applikation.',
            l_5 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Login', l_5, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_erste.htm',
            'Einstiegsseite Login Benutzername Kennwort Domõne System',
            'So betreten Sie das System.', l_6 );

-----------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Willkommenseite', l_5, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_willkommen.htm',
            'Willkommenseite Neuigkeiten Verzeichnisbaum Inhaltsansicht Pfad Reiter System Button Bereiche Gruppe Privat
            Gruppenansicht Privatansicht Benutzer Rechte',
            'Herzlich Willkommen in Ihrem System!', l_7 );


---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die Hilfe zur Hilfe', l_1, 1, 0, '0x0000000000000000',
            'Wie gehen Sie mit dieser Hilfeablage um, wie finden Sie was?',
            l_8 );


---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wie bediene ich die Hilfe?', l_8, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_hilfe1.htm',
            'Hilfe Suche Struktur',
            'Der Umgang mit der Hilfeablage im System.', l_9 );

-----------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wie finde ich was?', l_8, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'will_hilfe2.htm',
            'Hilfe Suche Struktur',
            'Wie suchen Sie nach den Themen, die Sie interessieren?', l_10 );
DEBUG ("some done(10)");

---------------------------------------------------Ebene1-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Grundlegendes', l_oid_s, 1, 0, '0x0000000000000000',
            'In dieser Ablage finden Sie die grundlegenden Informationen zu Ihrer Applikation.',
            l_11 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Ansichten im System', l_11, 1, 0, '0x0000000000000000',
            'Welche Sichten gibt es auf das System selbst und welche auf die Objekte im Speziellen? Diese Antworten finden Sie hier.',
            l_12 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------


l_helpPath || '.htm' = l_l_helpPath + 'ansichten_welche.htm'
    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Welche Ansichten gibt es?', l_12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'ansichten_welche.htm',
            'Gruppe Privat Gruppenansicht Privatansicht',
            'Grundlegendes zu Gruppe und Privat.', l_13 );

-----------------

l_helpPath || '.htm' = l_l_helpPath + 'ansichten_gruppe.htm'
    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Gruppe', l_12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'ansichten_gruppe.htm',
            'Benutzer Rechte Gruppenansicht Gruppe Reiter',
            'Was ist die Gruppenansicht?', l_14 );

-----------------

l_helpPath || '.htm' = l_l_helpPath + 'ansichten_privat.htm'
    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Privat', l_12, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'ansichten_privat.htm',
            'Privatansicht Benutzer Privat Benutzerprofil Arbeitskorb Eingangskorb Ausgangskorb Bestellungen Warenkorb
            Datei Ablage Objekt Login Neuigkeiten Hotlist',
            'Was ist die Privatansicht?', l_15 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Standardordner', l_11, 1, 0, '0x0000000000000000',
            'Die grundlegenden Ablagen, die in der Basisversion von m2 zu finden sind.',
            l_19 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Welche Ordner gibt es im Basissystem?', l_19, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'ordner_basis.htm',
            'Gruppe Benutzerverwaltung Data Interchange Export Import Diskussionen Hilfe Katalogverwaltung Layouts Stammdaten 
            Termine Warenangebote Privat Arbeitskorb Ausgangskorb Benutzerprofil Bestellungen Eingangskorb Neuigkeiten Warenkorb',
            'Hier finden Sie die Standardablagen der Basisapplikation.', l_20 );
DEBUG ("some done(20)");
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Basisbegriffe', l_11, 1, 0, '0x0000000000000000',
            'Hier finden Sie die Begriffe, die Sie auf jeden Fall kennen sollten.',
            l_21 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die wichtigsten Begriffe', l_21, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'basis_begr.htm',
            'Gruppe Privat Verzeichnisbaum Buttons Reiter List Objekte Navigation Funktionen  Ablage Objekt Rechte Sõubern',
            'Dies Begriffe sollten Sie kennen, um optimal mit dem System zu arbeiten.', l_22 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Geschõftsobjekte', l_11, 1, 0, '0x0000000000000000',
            'Diese Objekte finden Sie in Ihrem System.',
            l_23 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist ein Geschõftsobjekt?', l_23, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'gesch_geschaeftsobjekt.htm',
            'Objekt Datei Dokument Ablage Diskussion Dokumentablage Formulare Formularablage Hyperlink 
            Integrationsverwaltung Notiz Schwarzes Brett Stammdaten Terminplan Terminplõne Warenkatalog Firma Person',
            'Das versteht man unter einem Objekt im m2 framework.', l_24 );

-----------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Welche Objekte gibt es?', l_23, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'gesch_welche.htm',
            'Objekt Datei Dokument Ablage Diskussion Dokumentablage Formulare Formularablage Hyperlink 
            Integrationsverwaltung Notiz Schwarzes Brett Stammdaten Terminplan Terminplõne Warenkatalog Firma Person',
            'Die gesamte Palette der Geschõftsobjekte in Ihrer Applikation.', l_25 );

--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Sichten auf ein Objekt', l_23, 1, 0, '0x0000000000000000',
            'Von all diesen Spezialsichten aus kann ein Objekt betrachtet werden.',
            l_16 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Welche Sichten bieten die Reiter?', l_16, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'gesch_reiter.htm',
            'Inhalt Info Verweise Rechte Protokoll Terminplan',
            'Der Sinn und die Funktion der Reiter beim Objekt.', l_17 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Aufteilung der Fenster', l_16, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'gesch_aufteil.htm',
            'Funktionsleiste Inhalt Verzeichnisbaum Reiter Pfad Gruppe Privat',
            'Was sehen Sie, wenn Sie sich im System befinden?', l_18 );

DEBUG ("some done(25)");
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Navigieren im System', l_11, 1, 0, '0x0000000000000000',
            'So kommen Sie am schnellsten zu den Informationen, die Sie bearbeiten oder betrachten wollen.',
            l_26 );
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wie gelange ich zu den Objekten und Ablagen?', l_26, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'nav_allg.htm',
            'Navigation Verzeichnisbaum Pfad Reiter Zur³ck Ablage Inhaltsansicht',
            'Optimale Navigation mit Pfad, Verzeichnisbaum und "Zur³ck".', l_27 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Grundfunktionen', l_11, 1, 0, '0x0000000000000000',
            'All das kann man mit einem Objekt tun!',
            l_29 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Welche Funktionen gibt es?', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_funktionen.htm',
            'Neu Bearbeiten Funktionsleiste L÷schen Ausschneiden Kopieren Verteilen Einf³gen Link erstellen Suchen Drucken Sõubern
            Weiterleiten Checkout Checkin',
            'Was kann man mit einem Objekt im m2 framework alles machen?', l_30 );
DEBUG ("some done(30)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Funktionsleiste', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_leiste.htm',
            'Neu Bearbeiten Funktionsleiste L÷schen Ausschneiden Kopieren Verteilen Einf³gen Link erstellen Suchen Drucken Sõubern
            Weiterleiten Checkout Checkin',
            'Die Funktionen zum Bearbeiten der Objekte.', l_31 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Arbeit mit Listen', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_listen.htm',
            'Kopieren Verteilen Einf³gen Ausschneiden Funktionen Funktionsleiste Auswõhlen',
            'Was ist eine Liste?', l_32 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Lesen von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_lesen.htm',
            'Lesen Objekt Objekte Geschõftsobjekt Geschõftsobjekte Benutzerprofil',
            'So lesen Sie die Informationen in einem Objekt.', l_33 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstellen von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_erstellen.htm',
            'Benutzer Lesen Objekte Typ Objekttyp Verfallsdatum Speichern',
            'So erstellen Sie ein neues Objekt.', l_34 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Bearbeiten von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_bearbeiten.htm',
            'Bearbeiten Objekte Funtkionen wõhlen Eingabemaske Inhalt Inhaltsansicht',
            'Bearbeiten von Objekten leichtgemacht.', l_35 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_loeschen.htm',
            'L÷schen Objekt Objekte Geschõftsobjekt Funktionen Funktionsleiste',
            'So l÷schen Sie ein Objekt.', l_36 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ausschneiden und Verschieben von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_ausschneiden.htm',
            'Ausschneiden Verschieben Objekte Funktionen Funktionsleiste',
            'So k÷nnen Sie ein Objekt ausschneiden und/oder verschieben.', l_37 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Objekte kopieren', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_kopieren.htm',
            'Objekte kopieren Geschõftsobjekt',
            'So kopieren Sie ein beliebiges Objekt.', l_38 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Verteilen von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_verteilen.htm',
            'Verteilen Objekt Objekte Geschõftsobjekt Benutzer',
            'Wie k÷nnen Sie ein Objekt an andere Benutzer schicken?', l_39 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Checkout', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_checkout.htm',
            'Benutzer Bearbeiten Objekt auschecken Checkin Datei Objekttyp',
            'Damit niemand gleichzeitig mit Ihnen ein Objekt bearbeiten kann.', l_40 );

DEBUG ("some done(40)");
           
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Checkin', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_checkin.htm',
            'Benutzer Bearbeiten Objekt auschecken Checkin Datei Objekttyp',
            'So machen Sie Ihr ausgechecktes Objekt wieder allen Benutzern zugõnglich.', l_41 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstellen eines Links', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_link.htm',
            'Erstellen Objekt Objekte Datei Geschõftsobjekt',
            'So erstellen Sie einen Link im System.', l_42 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Suche nach Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_suchen.htm',
            'Suchen Objekte Verzeichnisbaum Button Pfad Zur³ck',
            'So suchen und finden Sie Objekte im System.', l_43 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Drucken', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_drucken.htm',
            'Drucken Objekt Objekte Bearbeiten Funktionen Funktionsleiste',
            'Das Drucken von Geschõftsobjekten.', l_44 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Speichern von Objekten', l_29, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'o_speichern.htm',
            'Speichern Objekte Objekt Geschõftsobjekt Funktion Funktionsleiste',
            'So bewahre ich meine Informationen f³r andere Benutzer auf.', l_45 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Rechteverwaltung', l_11, 1, 0, '0x0000000000000000',
            'Alles, was man im Umgang mit Rechten wissen muss.',
            l_46 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was sind Rechte?', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_rechte.htm',
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Alles Wissenswerte ³ber Rechte.', l_47 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Zuordnen von Rechten', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_zuordnen.htm',
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung zuordnen',
            'So Ordne ich Rechte zu.', l_48 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Rechtealiases', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_aliases.htm',
            'Recht Rechtealiases Alias Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Das Zusammenfassen der einzelnen Rechte zu Aliases.', l_49 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Bearbeiten von Rechten', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_bearbeiten.htm',
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung Bearbeiten',
            'So bearbeiten Sie die Rechte.', l_50 );
DEBUG ("some done(50)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Automatisches Zuordnen von Rechten auf ein Objekt', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_automatisch.htm',
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'Automatisches Zuordnen der Rechte auf ein Objekt.', l_51 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Verõndern der Rechte', l_46, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'r_veraendern.htm',
            'Recht Rechte Objekt Benutzer vergeben Rechteverwaltung',
            'So verõndern Sie die Rechte auf ein Objekt.', l_52 );

---------------------------------------------------Ebene1-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die Gruppenansicht', l_oid_s, 1, 0, '0x0000000000000000',
            'Alles, was Sie in der Gruppenansicht zu sehen bekommen.',
            l_53 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wozu eine Gruppenansicht?', l_53, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'gruppe_allg.htm',
            'Gruppe Gruppenansicht Verwaltung Ablage Data Interchange Benutzerverwaltung
            Termine Stammdaten Katalogverwaltung Warenangebote Warenkatalog',
            'Was unterscheidet Gruppen- und Privatansicht?', l_54 );

----------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Benutzerverwaltung', l_53, 1, 0, '0x0000000000000000',
            'Rund um den Benutzer.',
            l_55 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            '_berblick', l_55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_ueberblick.htm',
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe',
            'Generelle Informationen ³ber die Benutzerverwaltung.', l_56 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines neuen Benutzers', l_55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_anlegen.htm',
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe',
            'So legen Sie einen neuen Benutzer an.', l_57 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen eines Benutzers', l_55, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_loeschen.htm',
            'Benutzer anlegen Benutzerverwaltung Objekt Gruppe L÷schen',
            'So l÷schen Sie einen angelegten Benutzer.', l_58 );

--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Gruppen im System', l_55, 1, 0, '0x0000000000000000',
            'Alles ³ber Gruppen, die innerhalb des Systems angelegt werden k÷nnen.',
            l_59 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Info ³ber die Gruppen im System', l_59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_info.htm',
            'Benutzer anlegen Gruppen System Gruppe',
            'Diese Gruppen gibt es im System.', l_60 );
DEBUG ("some done(60)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Zuordnen eines Benutzers zu einer Gruppe', l_59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_zuordnen.htm',
            'Benutzer anlegen Gruppen System Gruppe',
            'So ordnen Sie einen Benutzer zu einer Gruppe zu.', l_61 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen einer Zuordnung', l_59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_zuord_loeschen.htm',
            'Benutzer anlegen Gruppen System Zuordnung l÷schen Gruppe',
            'So l÷schen Sie eine Zuordnung.', l_62 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Gruppe', l_59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_gr_anlegen.htm',
            'Benutzer anlegen Gruppen System Objekt Gruppe',
            'Anleitung zum Anlegen einer Gruppe.', l_63 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Gruppen', l_59, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'benutz_gr_loeschen.htm',
            'Benutzer anlegen Gruppen System Objekt Gruppe l÷schen',
            'Anleitung zum L÷schen von Gruppen.', l_64 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Diskussionen', l_53, 1, 0, '0x0000000000000000',
            'Alles ³ber Dikussionen, Themen, Beitrõge.',
            l_65 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist eine Diskussion?', l_65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_was.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer',
            'Wozu diskutieren?', l_66 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ansehen einer Diskussion', l_65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_ansehen.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer ansehen',
            'Anleitung zum Betrachten einer Diskussion.', l_67 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Beitrõge', l_65, 1, 0, '0x0000000000000000',
            'Rund um die Beitrõge von Diskussionen.',
            l_68 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstellen eines neuen Beitrags', l_68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_neu.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer',
            'Anleitung zur Erstellung von neuen Beitrõgen.', l_69 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Antworten auf einen Beitrag', l_68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_antworten.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer antworten',
            'So antworten Sie auf einen Beitrag.', l_70 );
DEBUG ("some done(70)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Beitrõgen', l_68, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_b_loeschen.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer l÷schen',
            'So l÷schen Sie einen Beitrag.', l_71 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Themen', l_65, 1, 0, '0x0000000000000000',
            'Wie kommen die Themen in die Diskussion?',
            l_72 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstellen von Themen', l_72, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_thema_neu.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer erstellen Themen',
            'So erstellen Sie neue Themen f³r eine Diskussion.', l_73 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Themen', l_72, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_thema_loesch.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer l÷schen',
            'So l÷schen Sie die Themen wieder.', l_74 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstellen einer Diskussion', l_65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_erstellen.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer erstellen',
            'Grundlegendes zur Erstellung einer Diskussion.', l_75 );
DEBUG ("some done(75)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen der Diskussion', l_65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_loeschen.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer l÷schen',
            'Das L÷schen einer Diskussion.', l_76 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ausschliessen eines Benutzers', l_65, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dis_ausschliessen.htm',
            'Diskussion Beitrag Thema anlegen Gruppe Gruppenansicht Benutzer ausschlie¯en',
            'Wenn ein Benutzer keinen Zugang zur Diskussion mehr haben soll...', l_77 );
            
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Termine', l_53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung von Terminen.',
            l_78 );
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Termine planen und verwalten', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_planen.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung',
            'Was zur Terminverwaltung n÷tig ist.', l_79 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Den Terminkalender ansehen', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_ansehen.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung ansehen',
            'So k÷nnen Sie den Kalender betrachten.', l_80 );
DEBUG ("some done(80)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Einen Terminkalender anlegen', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_kalender.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung anlegen',
            'Anleitung zur Erstellung eines neuen Kalenders.', l_81 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist ein Termin?', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_was.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung',
            'Zur genaueren Erklõrung des Begriffs Termin.', l_82 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Termins', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_anlegen.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung anlegen',
            'So legen Sie einen neuen Termin an.', l_83 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Einf³gen von Terminen', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_einfuegen.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung einf³gen',
            'So f³gen Sie einen Termin ein.', l_84 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Terminen', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_loeschen.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung l÷schen',
            'Anleitung zum L÷schen von Terminen aus dem Kalender.', l_85 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Private Termine und Termine in der Gruppenansicht', l_78, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_priv_gr.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung Privat 
            Gruppenansicht Privatansicht',
            'Der Unterscheid zwischen Terminen in der Gruppen- und der Privatansicht.', l_86 );
-------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Termine mit mehreren Teilnehmern', l_78, 1, 0, '0x0000000000000000',
            'Wenn sich Teilnehmer zu einem Termin anmelden...',
            l_87 );
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anmelden zu einem Termin', l_87, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_anmelden.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung Anmelden',
            'Die Anmeldung zu eimen gemeinsamen Termin.', l_88 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Abmelden von einem Termin', l_87, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'term_abmelden.htm',
            'Termine Termin Ablage Gruppe Terminkalender planen verwalten Terminverwaltung abmelden',
            'So melden Sie sich wieder von einem Termin ab.', l_89 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Katalogverwaltung', l_53, 1, 0, '0x0000000000000000',
            'Rund um Produktschl³ssel und Warengruppen.',
            l_90 );
DEBUG ("some done(90)");
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist die Katalogverwaltung?', l_90, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_was.htm',
            'Katalogverwaltung Prdoktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen Ware',
            'Alles ³ber das Anlegen von grundlegenden Determinanten f³r die Warenverwaltung.', l_91 );

--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Produktschl³sselkategorien', l_90, 1, 0, '0x0000000000000000',
            'Rund um das Produkt.',
            l_92 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist eine Produktschl³sselkategorie?', l_92, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_psk.htm',
            'Katalogverwaltung Produktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen Ware',
            'Was muss man sich unter Produktschl³sselkategorien vorstellen?', l_93 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Kategorie', l_92, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_psk_anlegen.htm',
            'Katalogverwaltung Produktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware',
            'Wie legen Sie die Kategorie an?', l_94 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Produktschl³ssel', l_90, 1, 0, '0x0000000000000000',
            'Alles zum Begriff Produktschl³ssel.',
            l_95 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist ein Produktschl³ssel?', l_95, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_ps.htm',
            'Katalogverwaltung Produktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware',
            'Was muss man sich unter Produktschl³sseln vorstellen?', l_96 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Schl³ssels', l_95, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_ps_anlegen.htm',
            'Katalogverwaltung Poduktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware anlegen',
            'So legen Sie einen Schl³ssel an.', l_97 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Warengruppe', l_90, 1, 0, '0x0000000000000000',
            'Rund um die Warengruppe...',
            l_98 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist eine Warengruppe?', l_98, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_wgr.htm',
            'Katalogverwaltung Poduktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware',
            'Das ist eine Warengruppe.', l_99 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Warengruppe', l_98, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_wgr_anlegen.htm',
            'Katalogverwaltung Produktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware anlegen Warengruppe',
            'So legen Sie eine Warengruppe an.', l_100 );

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Produktmarken', l_90, 1, 0, '0x0000000000000000',
            'Rund um das Anlegen von Produktmarken...',
            l_101 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist eine Produktmarke?', l_101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_pm.htm',
            'Katalogverwaltung Poduktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware',
            'Was muss man sich unter Produktmarke vorstellen?', l_102 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen der Produktmarke', l_101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_pm_anlegen.htm',
            'Katalogverwaltung Poduktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware anlegen',
            'Anleitung zum Anlegen der Produktmarke.', l_103 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen der Produktmarke', l_101, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'kat_pm_loeschen.htm',
            'Katalogverwaltung Prdoktschl³sselkategorien Produktschl³ssel Produktmarken Warengruppen 
            Ware l÷schen',
            'So l÷schen Sie die Produktmarke wieder.', l_104 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Warenangebote', l_53, 1, 0, '0x0000000000000000',
            'Rund um den Warenkatalog und die Ware...',
            l_105 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Info ³ber Warenangebote', l_105, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_info.htm',
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schl³ssel',
            'Was sind Warenangebote?', l_106 );
-----------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Umgang mit einem bestehenden Warenkorb', l_105, 1, 0, '0x0000000000000000',
            'Wenn schon ein Warenangebote existiert...',
            l_107 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der Warenkorb', l_107, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_warenkorb.htm',
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schl³ssel',
            'Was ist der Warenkorb?', l_108 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Suchen von Waren', l_107, 1, 0, '0x0000000000000000',
            'Anleitung zum Suchen von Waren ³ber Baum und Suchen-Schaltflõche...',
            l_109 );

---------------------------------------------------Ebene5-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Suchen ³ber die Baumstruktur', l_109, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_suche_baum.htm',
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schl³ssel Suchen Verzeichnisbaum',
            'Die Suche ³ber den Verzeichnisbaum.', l_110 );
DEBUG ("some done(110)");
--------------------------------


    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Schaltflõche "Suchen"', l_109, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_suche_button.htm',
            'Ware Warenangebote Warenkorb Sortiment Preise Profil Schl³ssel Suchen Schaltflõche',
            'Die Suche ³ber die Suchen-Funktion.', l_111 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Bestellen von Waren', l_107, 1, 0, '0x0000000000000000',
            'Alles zur Bestellung von Waren.',
            l_112 );

---------------------------------------------------Ebene5-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der Status meiner Bestellung', l_112, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_status.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment',
            'Was steht in Ihrer Warenbestellung?', l_113 );

--------------------------------


    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ausdrucken der Bestellung', l_112, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ausdruck.htm',
            'Bestellen Ware Einkaufswagen ausdrucken drucken Bestellung Sortiment',
            'So drucken Sie Ihre Bestellung aus.', l_114 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Anlegen eines Warenkatalogs', l_105, 1, 0, '0x0000000000000000',
            'Alles, was Sie zum Anlegen eines Warenkatalogs wissen m³ssen...',
            l_115 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Warenkatalogs', l_115, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_anlegen.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Warenkatalog anlegen',
            'So legen Sie einen Warenkatalog an.', l_116 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen eines Warenkatalogs', l_115, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_loeschen.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Warenkatalog l÷schen',
            'So l÷schen Sie einen Warenkatalog.', l_117 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Anlegen von Waren', l_105, 1, 0, '0x0000000000000000',
            'So legen Sie neue Waren an...',
            l_118 );

---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wie lege ich Waren an?', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ware.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment anlegen Waren',
            'Hilfe beim Anlegen neuer Waren.', l_119 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Kopieren einer Ware', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ware_kopieren.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Waren kopieren',
            'So kopieren Sie eine Ware.', l_120 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Einf³gen einer Ware mit einem Link aus der Gruppenansicht', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ware_einfueg.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment Link Gruppe Gruppenansicht',
            'Einf³gen einer Ware aus der Gruppenansicht in den Privatbereich.', l_121 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'L÷schen von Waren aus dem System', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ware_loesch.htm',
            'Bestellen Ware Einkaufswagen Status Bestellung Sortiment l÷schen System',
            'So l÷schen Sie die Waren aus dem System.', l_122 );

---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Preise festlegen', l_105, 1, 0, '0x0000000000000000',
            'Alles zum Festlegen der Preise.',
            l_123 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wozu Preise definieren?', l_123, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_preise.htm',
            'Preise definieren festlegen HEK VEK statt',
            'Wozu werden Preise gebraucht?', l_124 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen von Sortimenten mit Profil und Schl³ssel', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_sort.htm',
            'Bestellen Ware Einkaufswagen Profil Schl³ssel Prodoktschl³ssel Bestellung Sortiment',
            'Das Anlegen von Sortimenten.', l_125 );

--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen von Waren mit Profil und Schl³ssel', l_118, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'w_ware_ps.htm',
            'Bestellen Ware Einkaufswagen Profil Schl³ssel Produktschl³ssel Bestellung Sortiment',
            'Das Anlegen von Waren.', l_126 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Stammdaten', l_53, 1, 0, '0x0000000000000000',
            'Alles rund um die Stammdatenverwaltung.',
            l_127 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wozu Stammdaten?', l_127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_einf.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person',
            'Was sind Stammdaten und wozu muss man sie anlegen?', l_128 );

--------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die Stammdatenablage', l_127, 1, 0, '0x0000000000000000',
            'Die Ablage f³r die Stammdaten.',
            l_129 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen der Stammdatenablage', l_129, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_anlegen.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person 
            Stammdatenablage anlegen',
            'Wie legen Sie die Stammdatenablage an?', l_130 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Firma', l_127, 1, 0, '0x0000000000000000',
            'Alles rund um Firmen im System.',
            l_131 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Eine Firma anlegen', l_131, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_firma.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma anlegen',
            'So legen Sie eine Firma an.', l_132 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Person', l_127, 1, 0, '0x0000000000000000',
            'Alles rund um Personen im System.',
            l_133 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Eine Person anlegen', l_133, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_person.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma anlegen',
            'So legen Sie eine Person an.', l_134 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Stammdaten und Benutzerverwaltung', l_127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_benutzerverw.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person',
            'Der Zusammenhang zwischen Stammdaten und Benutzer.', l_135 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Stammdaten und Katalogverwaltung', l_127, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'st_katalogverw.htm',
            'Stammdaten Benutzer Katalogverwaltung Benutzerverwaltung Firma Person Bestellverantwortlicher
            Katalogverantwortlicher',
            'Der Zusammenhang zwischen Stammdaten und Katalog.', l_136 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Data Interchange', l_53, 1, 0, '0x0000000000000000',
            'Alles, rund um den Datenaustausch...',
            l_137 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            '_berblick ³ber den Data Interchange Bereich', l_137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_ueberblick.htm',
            'Data Interchange Import Export Integrationsverwaltung Formulare Workflow',
            'Was finden Sie im Data Interchange Bereich?', l_138 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Ablage "Integrationsverwaltung"', l_137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_integration.htm',
            'Data Interchange Import Export Integrationsverwaltung Formulare Workflow',
            'Was ist die Integrationsverwaltung?', l_139 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Objekte im Data Interchange Bereich', l_137, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_objekte.htm',
            'Objekte Data Interchange Ablage Exportablage Importablage Integrationsverwaltung 
            Formularvorlagenablage Importablage Importskriptablage Konnektorablage Workflowvorlagenablage',
            'Welche Objekte k÷nnen in Data Interchange angelegt werden?', l_140 );
DEBUG ("some done(140)");
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Konnektoren', l_137, 1, 0, '0x0000000000000000',
            'Rund um die Konnektoren...',
            l_141 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist ein Konnektor?', l_141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_konnektor.htm',
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Wie erklõrt sich der Begriff Konnektor?', l_142 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Datei Konnektor', l_141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_dateikonn.htm',
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der Datei Konnektor!', l_143 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'FTP Konnektor', l_141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_ftpkonn.htm',
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der FTP Konnektor!', l_144 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Email Konnektor', l_141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_emailkonn.htm',
            'Import Konnektor Datei Email FTP Importskript Importmaske',
            'Das ist der E-Mail Konnektor!', l_145 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Konnektoren anlegen', l_141, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_konn_anlegen.htm',
            'Import Konnektor Datei Email FTP Importskript Importmaske anlegen',
            'So legen Sie die Konnektoren an.', l_146 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Das Log', l_137, 1, 0, '0x0000000000000000',
            'Alles Wissenswerte zum Thema "Log"',
            l_147 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Aufzeichnung des Datenaustausches', l_147, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_log.htm',
            'Log Import Export Data Interchange Datenaustausch Aufzeichnung',
            'Das kann das Log des Datenaustausches.', l_148 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Agents', l_137, 1, 0, '0x0000000000000000',
            'Rund um den Agent...',
            l_149 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der automatische Datenaustausch', l_149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_agents.htm',
            'Agent Data Interchange automatisieren log Java zeitgesteuert',
            'Das ist automatischer Datenaustausch.', l_150 );
DEBUG ("some done(150)");
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Den Agent aufrufen', l_149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_ag_aufrufen.htm',
            'Agent Data Interchange automatisieren log Java zeitgesteuert Commandline Aufruf',
            'So k÷nnen Sie den Agent aufrufen.', l_151 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Zeitsteuerung des Agents', l_149, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_ag_zeit.htm',
            'Agent Data Interchange automatisieren log Java zeitgesteuert Zeitsteuerung periodisch
            Zeitpunkt Wochentag',
            'Wie Sie den Agent mittels einer Zeitsteuereung lenken.', l_152 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Der Import', l_137, 1, 0, '0x0000000000000000',
            'Rund um den Import...',
            l_153 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der manuelle Datenaustausch', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_import.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Beschreibung des Datenaustausches.', l_154 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Importablage', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_importablagen.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Importablage',
            'Was ist eine Importablage', l_155 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Importfunktion', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_importfunktion.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Importfunktion',
            'Was ist die Importfunktion?', l_156 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Importskript', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_importskript.htm',
            'Importskript Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Der Hauptbestandteil des manuellen Datenaustausches.', l_157 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Importmaske', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_importmaske.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Eingabe mittels Importmaske.', l_158 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist der Mehrfachupload?', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_mehrfach.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Was bedeutet Mehrfachupload?', l_159 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Import als Geschõftsobjekt', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_gesch.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Geschõftsobjekt',
            'Rund um den Import.', l_160 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Import von Formularen', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_formular.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Formulare',
            'So k÷nnen Sie Formular ins System bringen.', l_161 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Import Dokument', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_importdoku.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten DTD Dokument',
            'Was ist das Import Dokument?', l_162 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der hierarchische Import', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_hier.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten',
            'Wie f³hre ich einen hierarchischen Import durch?', l_163 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Importieren von Reitern', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_reiter.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Reiter',
            'So importiert man Reiter...', l_164 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Import DTD', l_153, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_dtd.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten DTD Dokument',
            'Die Document Type Definition.', l_165 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Spezielle Themen', l_153, 1, 0, '0x0000000000000000',
            'Weiterf³hrende Themen des Imports.',
            l_166 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Import Szenario', l_166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_szenario.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Szenario',
            'Informationen zum Import Szenario.', l_167 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Key Mapper', l_166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_mapper.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Key Mapper',
            'Das sind Key Mapper.', l_168 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Key Domains', l_166, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_domains.htm',
            'Data Interchange Datenaustausch Import Importskript Importmaske Konnektor hierarchisch
            Mehrfachupload Aufruf starten Key Domains',
            'Wissenswertes zu Key Domains.', l_169 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Der Export', l_137, 1, 0, '0x0000000000000000',
            'Rund um den Export...',
            l_170 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Info ³ber den Export', l_170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_export.htm',
            'Export Data Interchange Datenaustausch Konnektor hierarchisch',
            'Grundlegendes zum Thema Export.', l_171 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Exportablage', l_170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_exp_ablage.htm',
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportablage',
            'Was ist die Exportablage?', l_172 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Exportdokument', l_170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_exp_dokument.htm',
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportdokument',
            'Wissenswertes zum Thema Exportdokument.', l_173 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Exportmaske', l_170, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'di_exportmaske.htm',
            'Export Data Interchange Datenaustausch Konnektor hierarchisch Exportmaske',
            'Eingabe mit Hilfe der Exportmaske.', l_174 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Formularverwaltung', l_53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung von Formularen...',
            l_175 );
DEBUG ("some done(175)");
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Zum Thema Formulare', l_175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_allg.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung',
            'Grundlegendes zum Them Formulare.', l_176 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Feldtypen f³r Formulare', l_175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_typ.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen',
            'Dies Feldtypen gibt es.', l_177 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die Formularvorlagenablage', l_175, 1, 0, '0x0000000000000000',
            'Was kann die Formularvorlagenablage?',
            l_178 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Formularvorlagenablage', l_178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_fva.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Was muss man ³ber diese Ablage wissen?', l_179 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Formularvorlagenablage', l_178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_anlegen_fva.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Wie legen Sie diese Ablage an?', l_180 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Bearbeiten einer Formularvorlagenablage', l_178, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_bearb_fva.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlagenablage',
            'Wie bearbeiten Sie die Ablage?', l_181 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Formularvorlage', l_175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_vorlage.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            Formularvorlage',
            'Was ist die Formularvorlage?', l_182 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Das Formular', l_175, 1, 0, '0x0000000000000000',
            'Rund um das Formular...', l_183 );
---------------------------------------------------Ebene4-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist ein Formular?', l_183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_was.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen 
            ',
            'Was ist ein Formular eigentlich?', l_184 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Formulars', l_183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_anlegen.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen',
            'So legen Sie ein Formular an.', l_185 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Import eines Formulars', l_183, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_imp.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen Import',
            'So importieren Sie ein Formular.', l_186 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Importieren von Reitern', l_175, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'form_reiter.htm',
            'Formular Formulare Data Interchange Datenaustausch Formularverwaltung Feldtypen Reiter',
            'So importiert man Reiter.', l_187 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Workflow', l_53, 1, 0, '0x0000000000000000',
            'Wissenswertes zum Thema Workflow.',
            l_188 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wissenswertes zum Thema Workflow', l_188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'work_allg.htm',
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'Alles rund um den Workflow.', l_189 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Grundbegriffe des Workflow', l_188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'work_grund.htm',
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'Grundlegendes.', l_190 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Workflowvorlagenablage', l_188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'work_wfv_anlegen.htm',
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage 
            Workflowvorlagenablage',
            'So legen Sie eine Vorlage f³r den Workflow an.', l_191 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Starten des Workflow', l_188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'work_starten.htm',
            'Formular Formulare starten Data Interchange Datenaustausch Workflow Workflowvorlage Workflowvorlagenablage',
            'So starten Sie den Workflow.', l_192 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Workflow', l_188, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'work_anlegen.htm',
            'Formular Formulare Data Interchange Datenaustausch Workflow Workflowvorlage 
            Workflowvorlagenablage anlegen',
            'SO legen Sie den Workflow an.', l_193 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Domõnenverwaltung', l_53, 1, 0, '0x0000000000000000',
            'Rund um die Verwaltung der Domõnen.',
            l_194 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was ist eine Domõne?', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_was.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator',
            'Grundlegendes.', l_195 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Systemadministratorensicht', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_sysadmin.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator',
            'Was unterscheidet sie von der Gruppenansicht?', l_196 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Login', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_login.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Login',
            'Wie loggen Sie sich ein?', l_197 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Systemadmin-Gruppenansicht', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_sys_gr.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Systemadmin-Gruppenansicht',
            'Was sieht der Systemadministrator.', l_198 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen eines Domõnenschemas', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_anlegen.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema',
            'So legen Sie ein Schema f³r eine Domõne an.', l_199 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Anlegen einer Standard Domõne', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_anlegen_standard.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema',
            'So legen Sie eine Standard Domõne an.', l_200 );
 DEBUG ("some done(200)");
 --------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Includes', l_194, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_include.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema',
            'Das sind Includes.', l_201 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die neue Domõne', l_194, 1, 0, '0x0000000000000000',
            'Rund um die neu angelegte Domõne.',
            l_202 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erstes Login', l_202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_neue_dom.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema',
            'So loggen Sie sich das erste Mal auf der neuen Domõne ein.', l_203 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ordnerstrukturen in der Gruppenansicht anlegen', l_202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_ordner.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema Strukturen Ordner Struktur',
            'So legen Sie neue Strukturen fest.', l_204 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Import von Strukturen', l_202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_import.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema Struktur Import',
            'Wie funktioniert der Import von anderen Domõnenstrukturen?', l_205 );
--------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Domõne l÷schen', l_202, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'dom_loeschen.htm',
            'Domõne Systemadministrator Ansicht Domõnenverwaltung Includes neuen Domõne Administrator
            Domõnenschema Schema l÷schen',
            'So l÷schen Sie die Domõne.', l_206 );

    
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Die Privatansicht', l_oid_s, 1, 0, '0x0000000000000000',
            'Rund um die Privatansicht...',
            l_207 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wozu eine Privatansicht?', l_207, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_allg.htm',
            'Privat Privatansicht Ansichten Ansicht Eingangskorb Ausgangskorb Bestellungen Warenkorb 
            Neuigkeiten Kennwort Benutzerprofil Benutzer Arbeitskorb',
            'Was kann die Privatansicht?', l_208 );
--------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Arbeitskorb', l_207, 1, 0, '0x0000000000000000',
            'Rund um den Arbeitskorb...',
            l_209 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ihre private Arbeitsablage', l_209, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_arbeitskorb.htm',
            'Privat Privatansicht Arbeitskorb Objekte Objekt bearbeiten',
            'Was leistet der private Arbeitskorb?', l_210 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Ausgangskorb', l_207, 1, 0, '0x0000000000000000',
            'Rund um den Ausgangskorb...',
            l_211 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Alles, was zur Gruppe geht...', l_211, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_ausgangskorb.htm',
            'Privat Privatansicht Ausgangskorb Objekte Objekt bearbeiten',
            'Was leistet der Ausgangskorb?', l_212 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Benutzerprofil', l_207, 1, 0, '0x0000000000000000',
            'Rund um das Benutzerprofil...',
            l_213 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Info zum Benutzerprofil', l_213, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_benutzerprofil.htm',
            'Privat Privatansicht Benutzer Benutzerprofil Objekte Objekt bearbeiten',
            'Generelles.', l_214 );
---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Kennwort õndern', l_213, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_kennwort.htm',
            'Privat Privatansicht Benutzer Kennwort Benutzerprofil Objekte Objekt bearbeiten',
            'So õndern Sie Ihr Kennwort.', l_215 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Bestellungen', l_207, 1, 0, '0x0000000000000000',
            'Rund um die Bestellungen...',
            l_216 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Bestellungen aus dem Warenkorb', l_216, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_bestellungen.htm',
            'Privat Privatansicht Bestellung Bestellungen Warenkorb Objekte Objekt bearbeiten',
            'Hier werden Ihre Bestellungen aufbewahrt.', l_217 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Eingangskorb', l_207, 1, 0, '0x0000000000000000',
            'Rund um den Eingangskorb...',
            l_218 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Rund um den Eingangskorb', l_218, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_eingangskorb.htm',
            'Privat Privatansicht Eingengskorb Objekte Objekt bearbeiten',
            'Da leistet Ihr Eingangskorb.', l_219 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Hotlist', l_207, 1, 0, '0x0000000000000000',
            'Rund um die hottesten Objekte...',
            l_220 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Wichtige Objekte', l_220, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_hotlist.htm',
            'Privat Privatansicht Hotlist Objekte Objekt bearbeiten',
            'Schneller Zugriff auf alle pers÷nlich wichtigen Objekte.', l_221 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Neuigkeiten', l_207, 1, 0, '0x0000000000000000',
            'Rund um die Neuigkeiten...',
            l_222 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was gibt es Neues?', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_allg.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Generelles zu den Neuigkeiten.', l_223 );

---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Was sind Neuigkeiten?', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_was.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Alles zum Thema neue Objekte im System.', l_224 );

---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Erkennen von Neuigkeiten', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_erkennen.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten erkennen',
            'Wie erkennen Sie welche Objekte neu im System sind?', l_225 );
DEBUG ("some done(225)");
  
---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Die Neuigkeitenablage', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_neuigkeitenablage.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            Neuigkeitenablage',
            'Wo werden die Neuigkeiten gelagert?', l_226 );

---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Ein Objekt als neu kennzeichnen', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_kennzeichnen.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            kennzeichnen neu',
            'So wird das Objekt als ungelesen markiert.', l_227 );

---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Das Neuigkeitensignal', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_signal.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten 
            Neuigkeitensignal',
            'Wie sieht die Markierung eines neuen Objektes aus?', l_228 );

---------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Der Zeitraum f³r Neuigkeiten', l_222, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'neu_zeitraum.htm',
            'Privat Privatansicht Neuigkeiten Zeitlimit Zeitraum Objekte Objekt bearbeiten',
            'Wie lange gelten Objekte als neu?', l_229 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Warenkorb', l_207, 1, 0, '0x0000000000000000',
            'Rund um den Warenkorb in der Privatansicht...',
            l_230 );
---------------------------------------------------Ebene3-----------------------------------------------------------------------------

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Rund um den Warenkorb', l_230, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'priv_warenkorb.htm',
            'Privat Privatansicht Warenkorb Bestellung Bestellungen Ware Sortiment Objekte Objekt bearbeiten',
            'Das leistet Ihr privater Warenkorb.', l_231 );



---------------------------------------------------Ebene1-----------------------------------------------------------------------------

    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Weitere Informationen', l_oid_s, 1, 0, '0x0000000000000000',
            'Zur weiteren Information...',
            l_232 );
---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Falls Sie noch Fragen haben...', l_232, 1, 0, '0x0000000000000000',
            'Adressen und Wichtiges zum System.',
            l_233 );
            
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
           

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Adressen', l_233, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'weiter_adressen.htm',
            'Adressen Hilfe weiter',
            'So finden Sie uns.', l_234 );

---------------------------------------------------Ebene2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal :=  p_Object$CREATE ( l_userId, 0x00000001, c_TVHelpContainer,
            'Wenn Fehler auftreten...', l_232, 1, 0, '0x0000000000000000',
            'Schicken Sie uns Ihre Bemerkungen und Anregungen, sowie die Fehlermeldungen...',
            l_235 );
          
---------------------------------------------------Ebene3-----------------------------------------------------------------------------
           

    l_retVal :=  p_Help_01$createFast ( l_userId, 0x00000001, c_TVHelpObject,
            'Supportformular', l_230, 1, 0, '0x0000000000000000',
            '#CONFVAR.ibsbase.validUntilSql#', '', l_helpPath || 'weiter_supportformular.htm',
            'Anfragen Fehler Support Supportformular',
            'Tragen Sie hier Ihre Fehlermeldungen ein und schicken Sie sie an uns.', l_236 );

/**************************************************************************
*
*                          [**]  creating links  [**]
*
**************************************************************************/

DEBUG ('Help structure created...');

DEBUG ('Creating links...');

-- create links

	-- Neu im System
			--Was ist neu in dieser Release?
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_32, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_195, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_17, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_27, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_189, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_148, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_171, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_163, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_159, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_164, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_142, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_177, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_40, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_4, l_41, l_oid_s );

	-- Die ersten Schritte
			-- Das Login
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_6, l_7, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_6, l_2, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_6, l_4, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_6, l_13, l_oid_s );

			-- Die Willkommenseite
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_6, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_2, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_13, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_7, l_18, l_oid_s );

	-- Die Hilfe zur Hilfe
		-- Wie bediene ich Die Hilfe?
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_9, l_10, l_oid_s );
		-- Wie finde ich was?
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_10, l_9, l_oid_s );

		
-- Grundlegendes
	-- Ansichten im System
		-- Welche Ansichten gibt es? 
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_22, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_17, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_13, l_18, l_oid_s );
			-- Gruppe
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_14, l_13, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_14, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_14, l_22, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_14, l_54, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_14, l_56, l_oid_s );
DEBUG ('some 50 done...');

			-- Privat
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_15, l_13, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_15, l_208, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_15, l_18, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_15, l_22, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_15, l_20, l_oid_s );

	-- Standardordner
		-- Welche Ordner gibt es im Basissystem?
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_20, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_20, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_20, l_22, l_oid_s );
	
	-- Basisbegriffe
		-- Die wichtigesten Begriffe
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_7, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_27, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_54, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_208, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_24, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_17, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_32, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_21, l_47, l_oid_s );

  	-- Geschõftsobjekte
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_24, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_24, l_17, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_25, l_24, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_25, l_20, l_oid_s );

		-- Sichten auf ein Objekt
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_17, l_13, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_17, l_18, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_17, l_27, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_18, l_13, l_oid_s );

	-- Navigieren im System
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_27, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_27, l_15, l_oid_s );

DEBUG ('some 100 done...');

	-- Grundfunktionen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_30, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_30, l_24, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_30, l_22, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_31, l_37, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_31, l_38, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_31, l_39, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_31, l_40, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_31, l_41, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_32, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_32, l_35, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_32, l_43, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_32, l_32, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_33, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_33, l_36, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_33, l_62, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_33, l_45, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_34, l_34, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_34, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_34, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_34, l_36, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_34, l_43, l_oid_s );
		
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_35, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_35, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_35, l_44, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_35, l_43, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_36, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_36, l_32, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_37, l_38, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_37, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_37, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_37, l_43, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_37, l_32, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_38, l_37, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_38, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_38, l_42, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_39, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_39, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_39, l_35, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_39, l_43, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_40, l_41, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_40, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_40, l_31, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_41, l_40, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_41, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_41, l_31, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_42, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_42, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_42, l_38, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_42, l_42, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_42, l_44, l_oid_s );
DEBUG ('some 150 done...');

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_43, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_43, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_43, l_45, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_43, l_44, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_44, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_44, l_31, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_45, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_45, l_31, l_oid_s );
-------------------------------------------------------------------------

-- Rechteverwaltung
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_47, l_48, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_47, l_49, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_47, l_50, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_48, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_48, l_17, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_49, l_47, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_49, l_50, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_50, l_57, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_50, l_17, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_51, l_48, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_51, l_49, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_52, l_50, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_52, l_47, l_oid_s );

-- Die Gruppenansicht
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_54, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_54, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_54, l_13, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_54, l_20, l_oid_s );

DEBUG ('some 200 done...');

	-- Benutzerverwaltung
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_56, l_22, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_56, l_6, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_56, l_60, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_57, l_56, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_57, l_132, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_57, l_134, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_57, l_135, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_58, l_57, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_58, l_36, l_oid_s );

		-- Gruppen im System
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_60, l_56, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_61, l_56, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_61, l_60, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_62, l_58, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_62, l_60, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_63, l_60, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_63, l_57, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_63, l_61, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_64, l_60, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_64, l_62, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_64, l_58, l_oid_s );

	-- Diskussionen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_66, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_66, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_66, l_58, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_67, l_22, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_67, l_33, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_67, l_69, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_67, l_73, l_oid_s );

		-- Beitrõge
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_69, l_70, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_69, l_73, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_69, l_75, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_70, l_70, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_70, l_77, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_70, l_75, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_71, l_67, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_71, l_74, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_71, l_76, l_oid_s );
DEBUG ('some 250 done...');

		-- Themen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_73, l_69, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_73, l_75, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_73, l_66, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_74, l_71, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_74, l_76, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_75, l_69, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_75, l_77, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_75, l_66, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_75, l_67, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_76, l_72, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_76, l_74, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_76, l_77, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_77, l_66, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_77, l_70, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_77, l_79, l_oid_s );

	-- Termine
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_79, l_82, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_79, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_79, l_86, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_80, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_80, l_83, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_80, l_83, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_81, l_80, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_81, l_83, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_81, l_84, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_81, l_86, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_82, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_82, l_83, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_82, l_84, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_82, l_86, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_83, l_88, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_83, l_89, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_83, l_85, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_84, l_83, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_84, l_80, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_85, l_82, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_85, l_86, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_86, l_82, l_oid_s );

		-- Termine mit mehreren Teilnehmern
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_88, l_82, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_88, l_79, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_88, l_80, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_89, l_82, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_89, l_79, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_89, l_88, l_oid_s );
DEBUG ('some 300 done');

	-- Katalogverwaltung
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_91, l_106, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_91, l_136, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_91, l_20, l_oid_s );

		-- Produktschl³sselkategorien
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_93, l_96, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_93, l_99, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_93, l_102, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_94, l_93, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_94, l_91, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_94, l_96, l_oid_s );
	
		-- Produktschl³ssel
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_96, l_93, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_96, l_99, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_96, l_102, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_97, l_96, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_97, l_91, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_97, l_93, l_oid_s );

		-- Warengruppe
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_99, l_96, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_99, l_93, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_99, l_102, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_100, l_102, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_100, l_36, l_oid_s );

		-- Produktmarken
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_102, l_96, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_102, l_93, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_102, l_99, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_103, l_102, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_103, l_36, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_104, l_102, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_104, l_36, l_oid_s );

	-- Warenangebote
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_106, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_106, l_108, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_106, l_116, l_oid_s );

		-- Umgang mit einem bestehenden Warenkatalog
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_108, l_106, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_108, l_119, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_108, l_91, l_oid_s );
DEBUG ('some 350 done...');

			-- Suchen von Waren
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_110, l_43, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_110, l_27, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_111, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_111, l_31, l_oid_s );

			-- Bestellen von Waren
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_113, l_217, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_113, l_114, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_114, l_113, l_oid_s );

		-- Anlegen eines Warenkatalogs
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_116, l_108, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_116, l_117, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_117, l_106, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_117, l_36, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_117, l_122, l_oid_s );

DEBUG ('some 400 done...');

		-- Anlegen von Waren
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_108, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_116, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_125, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_126, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_93, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_96, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_99, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_119, l_102, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_120, l_38, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_120, l_32, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_120, l_108, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_121, l_42, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_121, l_120, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_121, l_217, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_122, l_36, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_122, l_117, l_oid_s );

		-- Preise festlegen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_124, l_108, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_124, l_119, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_125, l_124, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_125, l_119, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_126, l_124, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_126, l_119, l_oid_s );

	-- Stammdaten
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_128, l_20, l_oid_s );

		-- Die Stammdatenablage
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_130, l_34, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_130, l_128, l_oid_s );

		-- Firma
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_132, l_134, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_132, l_128, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_132, l_135, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_132, l_136, l_oid_s );

		-- Person
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_134, l_128, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_134, l_132, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_134, l_135, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_134, l_136, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_135, l_56, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_135, l_128, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_136, l_56, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_136, l_128, l_oid_s );

	-- Data Interchange
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_138, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_138, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_138, l_140, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_139, l_138, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_139, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_139, l_150, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_140, l_142, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_140, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_140, l_155, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_140, l_172, l_oid_s );

		-- Konnektoren
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_142, l_143, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_142, l_144, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_142, l_145, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_142, l_146, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_143, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_143, l_142, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_144, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_144, l_142, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_145, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_145, l_142, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_146, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_146, l_142, l_oid_s );
DEBUG ('some 450 done...');

		-- Das Log
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_148, l_150, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_148, l_154, l_oid_s );

		-- Agents
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_150, l_151, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_150, l_152, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_150, l_148, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_151, l_150, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_151, l_152, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_151, l_148, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_152, l_150, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_152, l_151, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_152, l_148, l_oid_s );

		-- Der Import
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_154, l_138, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_154, l_150, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_154, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_154, l_161, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_155, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_155, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_155, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_155, l_172, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_156, l_142, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_156, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_156, l_157, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_157, l_156, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_157, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_157, l_159, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_157, l_163, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_158, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_158, l_156, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_158, l_159, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_158, l_142, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_159, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_159, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_159, l_156, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_160, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_160, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_160, l_138, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_161, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_161, l_164, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_162, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_162, l_154, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_163, l_159, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_163, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_163, l_156, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_164, l_161, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_164, l_160, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_164, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_164, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_164, l_187, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_165, l_150, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_165, l_157, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_165, l_162, l_oid_s );
DEBUG ('some 500 done...');
			-- Spezielle Themen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_167, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_167, l_157, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_168, l_138, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_168, l_140, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_169, l_138, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_169, l_140, l_oid_s );

		-- Der Export
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_171, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_171, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_171, l_158, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_172, l_155, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_172, l_171, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_172, l_34, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_173, l_162, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_173, l_171, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_173, l_140, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_174, l_158, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_174, l_154, l_oid_s );

	-- Formularverwaltung
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_176, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_176, l_140, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_177, l_176, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_177, l_182, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_177, l_185, l_oid_s );

		-- Die Formularvorlagenablage
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_179, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_179, l_180, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_179, l_181, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_180, l_179, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_180, l_181, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_180, l_176, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_181, l_180, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_181, l_140, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_182, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_182, l_184, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_182, l_179, l_oid_s );

		-- Das Formular
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_184, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_184, l_182, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_184, l_179, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_185, l_180, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_185, l_177, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_186, l_161, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_186, l_154, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_186, l_157, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_187, l_164, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_187, l_157, l_oid_s );
 DEBUG ('some 550 done...');
	-- Workflow
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_189, l_139, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_189, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_189, l_190, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_190, l_140, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_190, l_192, l_oid_s );
		
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_191, l_180, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_191, l_189, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_192, l_193, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_192, l_189, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_193, l_192, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_193, l_190, l_oid_s );

	-- Domõnenverwaltung
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_195, l_6, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_195, l_196, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_196, l_198, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_196, l_196, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_197, l_203, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_197, l_196, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_197, l_198, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_198, l_196, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_198, l_195, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_198, l_200, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_199, l_195, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_199, l_200, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_199, l_201, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_200, l_195, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_200, l_199, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_200, l_201, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_201, l_195, l_oid_s );

		-- Die neue Domõne
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_203, l_200, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_203, l_197, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_204, l_198, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_204, l_203, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_205, l_163, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_205, l_154, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_206, l_36, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_206, l_195, l_oid_s );
DEBUG ('some 600 done...')
-- Die Privatansicht
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_208, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_208, l_14, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_208, l_13, l_oid_s );

	-- Arbeitskorb
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_210, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_210, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_210, l_25, l_oid_s );

	-- Ausgangskorb
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_212, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_212, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_212, l_20, l_oid_s );

	-- Benutzerprofil
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_214, l_56, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_214, l_215, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_215, l_6, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_215, l_214, l_oid_s );

	-- Bestellungen
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_217, l_113, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_217, l_106, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_217, l_231, l_oid_s );

	-- Eingangskorb
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_219, l_30, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_219, l_31, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_219, l_15, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_219, l_20, l_oid_s );

	-- Hotlist
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_221, l_24, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_221, l_27, l_oid_s );

	-- Neuigkeiten
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_223, l_7, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_223, l_228, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_224, l_25, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_224, l_223, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_225, l_7, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_225, l_224, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_226, l_20, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_226, l_224, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_227, l_224, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_227, l_223, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_228, l_225, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_228, l_229, l_oid_s );

   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_229, l_226, l_oid_s );
DEBUG ('some 650 done...');
	-- Warenkorb
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_231, l_217, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_231, l_113, l_oid_s );
   p_Help_01$CreateRef (l_userId, 0x00000001, 0x1010031, '#CONFVAR.ibsbase.validUntilSql#', l_231, l_106, l_oid_s );
-- Weitere Informationen
	-- Falls Sie noch Fragen haben...

	-- Wenn Fehler auftreten...

DEBUG ('Links created...');
DEBUG ('Perform help structure succeeded...');

END;
