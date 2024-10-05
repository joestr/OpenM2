/**************************************************************************
 * Install Help.
 *
 * @version     1.01.0001, 990804
 *
 * @author      Centner Martin (CM)  990719
 * Necessary to do befor installation:
 *   Remove existing help or else a nother help structure is created.
 *   Edit help path.
 *   Edit name of the domain.
 *
 * <DT><B>Updates:</B>
 * <DD> CM 990804   changed (corrected) helpUrl of 'information erstellen'
 * <DD> CM 990804   changed the query to get out the oid of the public 
 *                  container
 **************************************************************************/


-- declare variables:
DECLARE
        l_domainPosNoPath   RAW(254);
        l_retVal            INTEGER;
        l_uid               INTEGER;
        l_op                INTEGER;
        l_public            RAW(8);
        l_public_s          VARCHAR2(18);
        l_oid_s             VARCHAR2(18);
        l_domainName        VARCHAR2(63);
        l_helpPath          VARCHAR2(255);
        c_TVHelpContainer CONSTANT INTEGER := 16875265;    -- HelpContainer
        c_TVHelpObject    CONSTANT INTEGER := 16875281;    -- HelpObject
        

    -- local variables for helptreestructure
    l_10     VARCHAR2(18);     -- Einführung ins Basissystem
        l_11     VARCHAR2(18);     -- Systemaufbau
        l_12     VARCHAR2(18);     -- Privatansicht
        
        l_100    VARCHAR2(18);     -- Basisfunktionen
            l_101    VARCHAR2(18);     -- Informationen Lesen
            l_102    VARCHAR2(18);     -- Informationen Erstellen
            l_103    VARCHAR2(18);     -- Infromationen Bearbeiten
            l_104    VARCHAR2(18);     -- Informationen Löschen
            l_105    VARCHAR2(18);     -- Informationen Ausschneiden
            l_106    VARCHAR2(18);     -- Informationen Kopieren
            l_107    VARCHAR2(18);     -- Informationen Verteilen
            l_108    VARCHAR2(18);     -- Link erstellen
            l_109    VARCHAR2(18);     -- Informationen Suchen
            l_110    VARCHAR2(18);     -- Informationen Drucken
            l_111    VARCHAR2(18);     -- Informationen Speichern

    l_20     VARCHAR2(18);     -- Rechtemanagement
        l_21     VARCHAR2(18);     -- Rechte
        l_22     VARCHAR2(18);     -- Rechtealiases
        l_23     VARCHAR2(18);     -- Rechte Bearbeiten
        l_24     VARCHAR2(18);     -- Automatische Rechtekonfiguration
        l_25     VARCHAR2(18);     -- Rechteveränderungen Übertragen

    l_30     VARCHAR2(18);     -- Was gibt es neues
        l_31     VARCHAR2(18);     -- Neuigkeiten Erkennen
        l_32     VARCHAR2(18);     -- Zeitraum Einstellen
        l_33     VARCHAR2(18);     -- Neuigkeitensignal
        l_34     VARCHAR2(18);     -- Eingangskorp
        
    l_40     VARCHAR2(18);     -- Benutzerverwaltung
        l_41     VARCHAR2(18);     -- Benutzer Anlegen
        l_42     VARCHAR2(18);     -- Systemgruppen
        l_43     VARCHAR2(18);     -- Benutzer Zuordnen
        l_44     VARCHAR2(18);     -- Zuordnung Löschen
        l_45     VARCHAR2(18);     -- Gruppen Anlegen
        l_46     VARCHAR2(18);     -- Benutzer Löschen
        l_47     VARCHAR2(18);     -- Gruppen Löschen
        
    
    l_50     VARCHAR2(18);     -- Warenkatalog
        l_51     VARCHAR2(18);     -- Ware Bestellen
        l_52     VARCHAR2(18);     -- Bestellstatus Verfolgen
        l_53     VARCHAR2(18);     -- Bestellung Drucken
        l_54     VARCHAR2(18);     -- Warenkatalog Anlegen
        l_55     VARCHAR2(18);     -- Ware Anlegen
        l_56     VARCHAR2(18);     -- Preise Festlegen
        l_57     VARCHAR2(18);     -- PSK PS Anlegen
        l_58     VARCHAR2(18);     -- Warengruppen Anlegen
        l_59     VARCHAR2(18);     -- Produktmarken Anlegen
        l_510    VARCHAR2(18);     -- Warenprofil Schlüssel
        l_511    VARCHAR2(18);     -- Sortimentprofil Schlüssel

        l_500    VARCHAR2(18);     -- Wie Suche ich Waren
            l_501    VARCHAR2(18);     -- Waren Suchen Baum
            l_502    VARCHAR2(18);     -- Waren Suchen Button
        
    l_60     VARCHAR2(18);     -- Diskussionssystem
        l_61     VARCHAR2(18);     -- Diskussion Lesen
        l_62     VARCHAR2(18);     -- Diskussion Erstellen
        l_63     VARCHAR2(18);     -- Thema Erstellen
        l_64     VARCHAR2(18);     -- Beitrag Erstellen
        l_65     VARCHAR2(18);     -- Beitrag Antworten
        l_66     VARCHAR2(18);     -- Beiträge Löschen
        l_67     VARCHAR2(18);     -- Themen Löschen
        l_68     VARCHAR2(18);     -- Diskussionen Löschen
        l_69     VARCHAR2(18);     -- Diskussion Ausschließen

    l_70     VARCHAR2(18);     -- Stammdaten        
        l_71     VARCHAR2(18);     -- Stammdatenablage
        l_72     VARCHAR2(18);     -- Firma Anlegen
        l_73     VARCHAR2(18);     -- Person Anlegen
        l_74     VARCHAR2(18);     -- Stammdaten Benutzerverwaltung
        l_75     VARCHAR2(18);     -- Stammdaten Katalogverwaltung
     
    l_80     VARCHAR2(18);     -- Terminkalender        
        l_81     VARCHAR2(18);     -- Terminkalender Ansehen
        l_82     VARCHAR2(18);     -- Terminkalender Anlegen
        l_83     VARCHAR2(18);     -- Termin Anlegen
        l_84     VARCHAR2(18);     -- Termin Anmelden
        l_85     VARCHAR2(18);     -- Termin Abmelden

BEGIN

-- get configuration for installation  (is set in file installConfig.sql)
SELECT value
INTO   l_domainName
FROM   ibs_System
WHERE  name = 'DOMAIN_NAME';

SELECT value
INTO   l_helpPath
FROM   ibs_System
WHERE  name = 'WWW_HELP_PATH';

-- get domain data:
SELECT  d.adminId, d.publicOid
INTO    l_uid, l_public
FROM    ibs_Domain_01 d, ibs_Object o
WHERE   d.oid = o.oid
    AND o.name LIKE ('%' || l_domainName || '%');


p_byteToString (l_public, l_public_s);

DEBUG ('Create new help structures...');

---------------------------------------------------Level1-----------------------------------------------------------------------------
-- create helpcontainer:
l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, 'Hilfe', 
            l_public_s, 1, 0, '0x0000000000000000', '', l_oid_s);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, 
            'Einführung ins Basissystem', l_oid_s, 1, 0, '0x0000000000000000',
            'Nach der Eingabe der entsprechenden URL kommen Sie auf die Einstiegsseite des Systems. Dort geben Sie den jeweiligen Benutzernamen und das zugehörige Paßwort ein. Nach der Bestätigung des Paßwortes gelangen Sie zur Willkommensseite des Systems.',
            l_10);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject,
            'Wie ist das System aufgebaut', l_10, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '',
            l_helpPath || 'systemaufbau.htm',
            'Gruppe Privat Gruppenbaum Buttons Reiter Einstiegsseite Willkommensseite Navigation Säubern Listen Ablagen',
            'Das Basissystem und die Grundfunktionen verstehen', l_11);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was ist die Privatansicht', l_10, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'privatansicht.htm',   
            'Arbeitskorb Ausgangskorb Benutzerprofil Bestellungen Eingangskorb Hotlist Neuigkeiten Warenkorb',
            'Die Grundelemente der Privatansicht verstehen', l_12);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Basisfunktionen', l_10, 1, 0, '0x0000000000000000',
            'Die Basisfunktionen sind im gesamten System für alle Objekte (Informationen) verfügbar. Zu den Basisfunktionen zählen Neu, Bearbeiten, Löschen, Ausschneiden, Kopieren, Verteilen, Link erstellen, Suchen, Drucken und Säubern.',
            l_100);
---------------------------------------------------Level4-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lese ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_lesen.htm',   
            'Informationen lesen Benutzerprofil Zusatzinformationen Informationen sortieren',
            'Informationen können von Ihnen über den Gruppenbaum gelesen werden. Hier finden Sie zielgerichtete Informationen für Ihre Gruppe. ', l_101);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich eine neue Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
--! changed CM 990804 ... /////////////////////////////////////////////////////
            l_helpPath || 'informationen_erstellen.htm',   
--! ... changed CM 990804 ... /////////////////////////////////////////////////
/*
            l_helpPath || 'informtionen_erstellen.htm',   
*/
--! ... changed CM 990804 /////////////////////////////////////////////////////
            'Die Basisfunktion Neu dient zur Erstellen von neuen Informationen im System. Durch einen KLick auf den Button Neu kann über eine Eingabemaske von Ihnen eine neue Information in das System eingebracht werden',
            'Neue Informationen in das System einbringen', l_102);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Bearbeiten von Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_bearbeiten.htm',   
            'Informationen bearbeiten Information ändern',
            'Möchten Sie den Inhalt, den Namen oder das Verfallsdatum einer von Ihnen eingebrachten Information ändern, so können Sie dies über den Bearbeiten Button in der Infoansicht durchführen. ', l_103);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_loeschen.htm',   
            'Informationen löschen Löschen Liste löschen abgelaufene Informationen löschen Informationen suchen und löschen',
            'Möchten Sie eine von Ihnen eingebrachte Information, die noch nicht abgelaufen ist, löschen, so haben Sie dazu die Möglichkeit, dies über den Löschen Button in der Infoansicht in der entsprechenden Ablage zu tun. ', l_104);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie schneide ich Informatioen aus', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_ausschneiden.htm',   
            'Informationen ausschneiden Ausschneiden Informationen einfügen Einfügen',
            'Möchten Sie eine von Ihnen eingebrachte Information in eine andere Ablage verschieben, so haben Sie die Möglichkeit, dies über den Ausschneiden Button zu tun. ', l_105);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kopiere ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_kopieren.htm',   
            'Informationen kopieren Kopieren Einfügen Information einfügen',
            'Möchten Sie eine von Ihnen eingebrachte Information in eine andere Ablage kopieren, so können Sie dies über den Kopieren Button durchführen. ', l_106);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie verteile ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_verteilen.htm',   
            'Informationen verteilen Verteilen',
            'Möchten Sie Informationen, die von Ihnen in das System eingebracht wurden, speziell an andere Benutzer verteilen, so können Sie das über den Verteilen Button in der Infoansicht durchführe. ', l_107);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich einen Link', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'link_erstellen.htm',   
            'Link erstellen Verweise Link einfügen',
            'Möchten Sie nicht eine Information Kopieren, sondern nur auf sie verweisen, so haben Sie die Möglichkeit, dies über den Button Link einfügen durchzuführen. ', l_108);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie suche ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_suchen.htm',   
            'Informationen suchen Suchen Informationen finden Finden',
            'Möchten Sie eine Information suchen können Sie sie über den Button Suchen finden. ', l_109);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie drucke ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_drucken.htm',   
            'Informationen drucken Drucken',
            'Möchten Sie eine Information ausdrucken, so steht Ihnen diese Funktion derzeit für die Zusatzinformationen zur Verfügung, die über das Info-Icon angezeigt werden. Gedruckt werden können Dateien die beispielsweise die Endung *.doc, *.txt, oder *.pdf haben. ', l_110);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie speichere ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_speichern.htm',   
            'Informationen speichern Speichern',
            'Möchten Sie eine Information speichern, so steht Ihnen diese Funktion derzeit für die Zusatzinformationen zur Verfügung, die über das Info-Icon angezeigt werden. Gespeichert werden können Dateien die beispielsweise die Endung *.doc, *.txt, oder *.pdf haben. ', l_111);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Rechtemanagement', l_oid_s, 1, 0, '0x0000000000000000',
            'Das Rechtemanagement bietet die Möglichkeit, den verschiedenen Benutzern verschiedene Rechte am System zu verleihen. Das Rechtemangement ist persönlich auf jeden Benutzer zuschneidbar.', 
            l_20);
---------------------------------------------------Level3----------------------------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was sind Rechte', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechte.htm',   
            'Rechte',
            'Die Rechte eines Benutzers setzen sich aus vielen Einzelkomponenten zusammen. Durch die Rechtevergabe haben Sie die Möglichkeit, verschiedene Sichten für verschiedene Gruppen zu definieren. Über die Rechtevergabe können Sie definieren, welche Basisfunktionen die verschiedenen Benutzer am System durchführen dürfen.', l_21);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was sind Rechtealias', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechtealiases.htm',   
            'Rechtealiases',
            'Um das Rechtemanagement zu vereinfachen, werden einzelnen Rechte in Gruppen zusammengefaßt. Die so gebildeten Gruppen werden Rechtealiases genannt.', l_22);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie bearbeite ich Rechte', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechte_bearbeiten.htm',   
            'Rechte Bearbeiten Rechtealias Bearbeiten Rechte löschen Rechtealias löschen',
            'Um Benutzern andere Rechte zu verleihen bzw. um den Gruppen andere Rechte zuzuordnen, können Sie die Rechte auch bearbeiten. Um Rechte und Rechtealiases bearbeiten zu können, müssen Sie sich immer in dem Rechte Reiter der Ansicht befinden.', l_23);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Die automatische Rechtekonfiguration', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'automatische_rechtekonfiguration.htm',   
            'Rechte automatisch übertragen Rechte übertragen',
            'Die Zuordnung von Rechten auf neue Objekte erfolgt automatisch. Die neuen Objekte in einer Ablage erhalten automatisch die Rechte, die für die übergeordnete Ablage gelten', l_24);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Rechte ändern und auf untergeordnete übertragen', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechteveraenderung_uebertragen.htm',   
            'Rechte verändern Rechte übertragen veränderte Rechte übertragen Rechte vererben',
            'Werden die Rechte in einer übergeordneten Ablage verändert, können Sie die veränderten Rechte auch auf Objekte in dieser Ablage durch das Vererben von Rechten übertragen.', l_25);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Alles Neue im System', l_oid_s, 1, 0, '0x0000000000000000',
            'Neuigkeiten werden Ihnen auf der Willkommensseite des Systems gezeigt. Diese Neuigkeiten finden Sie durch die Funktion Neuigkeiten, dem Neuigkeiten-Signal und im privaten Eingangskorb', 
            l_30);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erkenne ich Neuigkeiten im System', l_30, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'neuigkeiten_erkennen.htm',   
            'Neuigkeiten Neuigkeiten ansehen Neuigkeiten erkennen',
            'Nach der Eingabe der entsprechenden URL, des Benutzernamens und des Paßwortes gelangen Sie auf die Willkommensseite, auf der die Neuigkeiten angezeigt werden. ', l_31);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie stelle ich den Zeitraum für Neuigkeiten ein', l_30, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'zeitraum_einstellen.htm',   
            'Zeitraum für Neuigkeiten einstellen Zeitraum einstellen Zeitlimit für Eingangskorb',
            'Sie haben die Möglichkeit, individuell einzustellen, welcher Zeitraum (Tage) für die Neuigkeiten für Sie gelten soll.', l_32);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was besagt das Neuigkeitensignal', l_30, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'neuigkeitensignal.htm',   
            'Neuigkeitensignal Neuigkeiten-Signal',
            'Das Neuigkeiten-Signal zeigt Ihnen an, welche Neuigkeiten von Ihnen noch nicht gelesen wurden.', l_33);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was befindet sich im Eingangskorb', l_30, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'eingangskorb.htm',   
            'Eingangskorb Einträge im Eingangskorb Verteiltes Objekt',
            'Nach der Eingabe der entsprechenden URL, des Benutzernamens und des Paßwortes gelangen Sie auf die Willkommensseite, auf der auch die Einträge im privaten Eingangskorb angezeigt werden. Diese Einträge wurden von anderen Benutzern des Systems direkt an Sie verteilt.', l_34);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Benutzerverwaltung', l_oid_s, 1, 0, '0x0000000000000000',
            'In der Benutzerverwaltung haben Sie die Möglichkeit, neue Benutzer anzulegen und sie einer bestehenden Gruppe zuzuordnen. Es können auch neue Gruppen angelegt werden', 
            l_40);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen Benutzer an', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'benutzer_anlegen.htm',   
            'neuen Benutzer anlegen Benutzer anlegen neuer Benutzer',
            'Sie haben die Möglichkeit, selbst neue Benutzer und neue Benutzergruppen für Ihr System zu generieren. Um einen neuen Benutzer anlegen zu können, müssen Sie immer in der Gruppenansicht sein.', l_41);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Welche Gruppen gibt es im System', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'systemgruppen.htm',   
            'Systemgruppen Gruppen',
            'Bestimmte Gruppen werden vom System beim Setup angelegt. Diese Gruppen sind mit speziellen Berechtigungen im System ausgestattet.', l_42);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie ordne ich einen Benutzer einer Gruppe zu', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'benutzer_zuordnen.htm',   
            'Benutzer einer Gruppe zuordnen Benutzer zuordnen Zuordnen',
            'Jeder Benutzer wird vom System automatisch der Gruppe jeder zugeordnet. Sie haben jedoch die Möglichkeit, die Zuordnung individuell vorzunehmen, um so eine geschlossene Struktur für ihre Benutzergruppen und Benutzer zu schaffen. Um einen Benutzer einer Gruppe zuordnen zu können, müssen Sie immer in der Gruppenansicht sein.', l_43);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich die Zuordnung eines Benutzers zu einer Gruppe', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'zuordnung_loeschen.htm',   
            'Zuordnung eines Benutzers zu einer Gruppe löschen Zuordnung löschen',
            'Soll ein Benutzer einer bestimmten Gruppe nicht mehr zugehören, können Sie Ihn aus dieser Gruppe löschen. Um die Zuordnung eines Benutzers aus einer Gruppe löschen zu können, müssen Sie immer in der Gruppenansicht sein.', l_45);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich neue Gruppen an', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'gruppen_anlegen.htm',   
            'neue Gruppen anlegen Gruppen anlegen Gruppe anlegen',
            'Um das System an Ihre persönlichen Anforderungen anzupassen, haben Sie die Möglichkeit, neue Gruppen anzulegen. Diese Gruppen bestehen dann neben jenen, die beim Setup automatisch angelegt werden.', l_45);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Benutzer aus dem System', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'benutzer_loeschen.htm',   
            'Benutzer aus dem System löschen Benutzer löschen',
            'Möchten Sie einen Benutzer aus dem System entfernen, haben Sie die Möglichkeit, dies über die Benutzerverwaltung zu löschen. Wenn das Gültigkeitsdatum des Benutzers abgelaufen ist, können Sie ihn über das Löschen von abgelaufenen Objekten entfernen.', l_46);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Gruppen aus dem System', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'gruppen_loeschen.htm',   
            'Gruppen aus dem System löschen Gruppen löschen',
            'Möchten Sie eine Gruppe aus dem System entfernen, haben Sie die Möglichkeit, dies über die Benutzerverwaltung zu löschen. Wenn das Gültigkeitsdatum der Gruppe abgelaufen ist, können Sie Sie über das Löschen von abgelaufenen Objekten entfernen', l_47);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Alles um die Ware', l_oid_s, 1, 0, '0x0000000000000000',
            'Die Struktur des Warenkatalogs besteht aus Warengruppen, Waren und Sortimenten. Im Warenkatalog können Sie entsprechende Waren finden und diese bestellen.', 
            l_50);
---------------------------------------------------Level2-----------------------------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie bestelle ich Waren', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'ware_bestellen.htm',   
            'Ware bestellen Waren bestellen Bestellen Merken',
            'Die Bestellung der Ware erfolgt, indem Sie die ausgewählten Waren vorab aus dem Warenkatalog auswählt. Die ausgewählten Waren werden dann automatisch im Warenkorb gesammelt. Durch die Funktion Bestellung können die Waren aus dem Warenkorb bestellt werden. ', l_51);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie verfolge ich den Status einer Bestellung', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'bestellstatus_verfolgen.htm',   
            'Bestellstatus Bestellstatus verfolgen Status einer Bestellung verfolgen',
            'Sie haben die Möglichkeit, den Status Ihrer bereits abgeschickten Bestellung zu verfolgen und so zu sehen, wie weit Ihre Bestellung fortgeschritten ist. ', l_52);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich meine Bestellung ausdrucken', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'bestellung_drucken.htm',   
            'Bestellung drucken Bestellung ausdrucken Drucken',
            'Möchte Sie Ihre Bestellung in Papierform ablegen, so haben Sie die Möglichkeit die Bestellung direkt über das System auszudrucken', l_53);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen Warenkatalog an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warenkatalog_anlegen.htm',   
            'Warenkatalog anlegen neuen Warenkatalog anlegen',
            'Um einen neuen Warenkatalog anzulegen können Sie über den Button Neu in der Ablage Warenangebot vorgehen.', l_54);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Ware an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'ware_anlegen.htm',   
            'neue Ware anlegen Ware anlegen',
            'Um eine nue Ware anzulegen, müssen Sie in der Warenablage in diejenige Warengruppe gehen, in der Sie die neue Waren anlegen möchten.', l_55);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Preise fest', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'preise_festlegen.htm',   
            'Preise festlegen Preise Preise definieren',
            'Nachdem die Ware definiert wurde geben Sie den zugehörigen Preis ein. Das System bietet Ihnen verschiedene Währungen, in denen Sie die Preise angeben können.', l_56);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Produktschlüsselkategorien und Produktschlüssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'psk_ps_anlegen.htm',   
            'Produktschlüsselkategorien anlegen Produktschlüssel anlegen Produktschlüsselkategorien definieren Produktschlüssel definieren Produktschlüsselkategorien erstellen Produktschlüssel erstellen',
            'Produktschlüsselkategorien und Produktschlüssel müssen erstellt werden, um einen Warenkatalog mit Profilen und Schlüsseln anlegen zu können.', l_57);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Warengruppen an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warengruppen_anlegen.htm',   
            'Warengruppen anlegen Warengruppen definieren Warengruppen erstellen neue Warengruppen Warengruppen',
            'Warengruppen müssen erstellt werden, um einen Warenkatalog mit Profilen und Schlüsseln anlegen zu können.', l_58);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Produktmarken an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'produktmarken_anlegen.htm',   
            'Produktmarken anlegen neue Produktmarken Produktmarke definieren Produktmarken',
            'Produktmarken bieten eine Möglichkeit, wie durch den gesamten Warenkatalog für ein Produkt eine Marke präsentiert werden kann.', l_59);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Waren mit Profil und Schlüssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warenprofil_schluessel.htm',   
            'Waren mit Profil und Schlüssel anlegen Profil Warenprofil Schlüssel Warenschlüssel',
            'Wenn Sie für eine Warengruppe Warenprogile und Schlüssel definiert haben, können Sie die Ware auf der Basis dieses Profils und dieses Schlüssels eingeben.', l_510);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Sortimente mit Profil und Schlüssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'sortimentprofil_schluessel.htm',   
            'Sortiment mit Profil und Schlüssel anlegen Sortiment Schlüssel Sortimentsschlüssel Sortiment Profil  Sortimentsprofil',
            'Möchten Sie, daß eine Ware nur im Sortiment bestellt werden kann, können Sie über das System direkt ein Sortiment anlegen. Eine neues Sortiment wird in der Warengruppe angelegt. Die Anlage zum Sortiment geschieht schon bei der Anlage der Ware.', l_511);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Wie suche ich nach Waren', l_50, 1, 0, '0x0000000000000000',
            '', 
            l_500);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Über die Baumstruktur', l_500, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'waren_suchen_baum.htm',   
            'Waren suchen Ware suchen Ware über Baumstruktur suchen Waren über Baumstruktur suchen',
            'Um im Warenkatalog nach einer bestimmten Ware zu suchen, können Sie über die Baumstruktur des Systems vorgehen', l_501);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Über den Button suchen', l_500, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'waren_suchen_button.htm',   
            'Waren suchen Ware suchen Ware über Button suchen Waren über Button suchen',
            'Um im Warenkatalog nach einer bestimmten Ware zu suchen, können Sie über den Suche Button vorgehen. ', l_502);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Diskussionssystem', l_oid_s, 1, 0, '0x0000000000000000',
            'Im Rahmen des Diskussionssystems können Sie eingebrachte Beiträge zu Themen lesen, selbst Beiträge und eventuell auch neue Themen erstellen', 
            l_60);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich mir den Inhalt einer Diskussion ansehen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_lesen.htm',   
            'Diskussion lesen Inhalt einer Diskussion ansehen Inhalt einer Diskussion lesen Beiträge lesen Themen lesen',
            'Das Diskussionssystem besteht aus Diskussionsablagen, Diskussionen, Themen und Beiträgen. Themen können den Diskussionen zugeordnet werden. Zu den Themen können Beiträge erstellt werden, auf die Antworten erstellt werden können. ', l_61);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich eine neue Diskussion', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_erstellen.htm',   
            'neue Diskussion erstellen neue Diskussion',
            'Möchten Sie aktiv mitdiskutieren, haben Sie Möglichkeit, eine neue Diskussion auf dem System anzuregen.', l_62);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich ein neues Thema', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'thema_erstellen.htm',   
            'neues Thema erstellen Thema erstellen neues Thema',
            'Möchten Sie aktiv mitdiskutieren, haben Sie Möglichkeit, neue Themen zu der Diskussion beizutragen. ', l_63);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich einen neuen Beitrag', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'beitrag_erstellen.htm',   
            'neuen Beitrag erstellen Beitrag erstellen neuer Beitrag',
            'Möchten Sie eine Anmerkung zu einem Thema machen, haben Sie die Möglichkeit, dies über einen Beitrag zu tun. ', l_64);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie antworte ich auf einen Beitrag', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '' , 
            l_helpPath || 'beitrag_antworten.htm',   
            'auf einen Beitrag antworten auf Beitrag antworten Antworten',
            'Möchten Sie zu einem Beitrag antworten, haben Sie direkt am System die Möglichkeit dazu.', l_65);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Beiträge', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'beitraege_loeschen.htm',   
            'Beitrag löschen Beiträge löschen',
            'Möchten Sie einzelne Beiträge löschen, so können Sie dies direkt über das System durchführen', l_66);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Themen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'themen_loeschen.htm',   
            'Thema löschen Themen löschen',
            'Möchten Sie Themen und die dazugehörigen Beiträge löschen, können Sie dies in einem Schritt durchführen, indem Sie das Thema löschen. ', l_67);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lösche ich Diskussionen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussionen_loeschen.htm',   
            'Diskussion löschen Diskussionen löschen',
            'Möchten Sie eine Diskussion, die dazugehörigen Themen und Beiträge auf einmal löschen, können Sie dies in einem Schritt durchführen, indem Sie die Diskussion löschen.', l_68);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich Benutzer von einer Diskussion ausschließen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_ausschliessen.htm',   
            'Benutzer aus Diskussion ausschließen',
            'Möchten Sie Benutzer oder Benutzergruppen von einer Diskussion ausschließen, so haben Sie über das Rechteprofil die Möglichkeit dazu.', l_69);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Stammdaten', l_oid_s, 1, 0, '0x0000000000000000',
            'Über die Stammdaten können Firmen und Personen im System abgespeichert werden.', 
            l_70);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine Stammdatenablage an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdatenablage.htm',   
            'Stammdatenablage neue Stammdatenablage Stammdatenablage anlegen',
            'Um Ihre Stammdaten alle in eine Ablage einfügen zu können, haben Sie die Möglichkeit, eine neue Stammdatenablage anzulegen.', l_71);
------------------ 
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Firma an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'firma_anlegen.htm',   
            'Firma anlegen neue Firma anlegen',
            'Um die Benutzer Ihres Systems eindeutig den Firmen zuordnen zu können, haben Sie die Möglichkeit, die Firma als Objekt anzulegen.', l_72);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Person an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'person_anlegen.htm',   
            'Person anlegen neue Person anlegen',
            'Um die Benutzer Ihres Systems eindeutig einer Person zuordnen zu können, haben Sie die Möglichkeit, die Person als Objekt anzulegen.', l_73);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Zusammenhang zwischen Stammdaten und Benutzerverwaltung', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdaten_benutzerverwaltung.htm',   
            'Stammdaten und Benutzerverwaltung Zusammenhang Stammdaten und Benutzerverwaltung',
            'Zwischen der Stammdatenverwaltung und der Benutzerverwaltung besteht ein enger Zusammenhang. Benutzer können eindeutig Personen zugeordnet werden, wobei eine Person mehrere Benutzer haben kann.', l_74);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Zusammenhang zwischen Stammdaten und Katalogverwaltung', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdaten_katalogverwaltung.htm',   
            'Stammdaten und Katalogverwaltung Zusammenhang Stammdaten und Katalogverwaltung',
            'Zwischen der Stammdatenverwaltung und der Katalogverwaltung besteht ein enger Zusammenhang. Benutzer können eindeutig als Bestellverantwortliche, als Katalogverantwortliche oder als Lieferant zugeordnet werden.', l_75);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Terminkalender', l_oid_s, 1, 0, '0x0000000000000000',
            'Über den Terminkalender haben Sie die Möglichkeit, sich Online zu Terminen und Seminaren anzumelden', 
            l_80);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich den Terminkalender ansehen', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'terminkalender_ansehen.htm',   
            'Terminkalender ansehen',
            'Sie haben die Möglichkeit, Ihre Termine direkt über das System zu verwalten. Der Vorteil liegt darin, daß Sie bei Terminüberschneidungen vom System sofort informiert werden.7', l_81);
------------------ 
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen neuen Terminkalender an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'terminkalender_anlegen.htm',   
            'Terminkalender anlegen neuen Terminkalender anlegen',
            'Benötigen Sie eine neuen Terminkalender, können Sie diesen in der Ablage einfügen.', l_82);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen neuen Termin/Seminar an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_anlegen.htm',   
            'neuen Termin anlege neues Seminar anlegen Termin anlegen Seminar anlegen',
            'Möchten Sie einen neuen Termin bzw. ein neues Seminar im System bekannt geben, haben Sie die Möglichkeit, dies über den Terminkalender durchzuführen.', l_83);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie melde ich mich zu einem Termin/Seminar an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_anmelden.htm',   
            'zu Termin anmelden zu Seminar anmelden Anmelden',
            'Gibt es im Terminkalender einen Termin oder ein Seminar, das Sie interessiert, können Sie sich direkt über das System zu einem Termin bzw. Seminar anmelden. ', l_84);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie melde ich mich von einem Termin/Seminar ab', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_abmelden.htm',   
            'von Termin abmelden von Seminar abmelden Abmelden',
            'Möchten Sie sich von einem Termin oder einem Seminar abmelden, haben Sie die Möglichkeit, die direkt über das System vorzunehmen. ', l_85);
-------------------

DEBUG ('Create new Referenzes!');

-- create Referenzes
-- 10 Einführung ins Basissytem
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_11, l_12, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_11, l_31, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_11, l_34, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_11, l_104, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_106, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_107, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_51, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_31, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_106, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_12, l_108, l_oid_s);

    -- 100 Basisfunktionen
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_101, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_101, l_12, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_102, l_11, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_103, l_11, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_104, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_104, l_12, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_104, l_109, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_105, l_11, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_106, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_106, l_108, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_107, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_107, l_12, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_108, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_108, l_106, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_109, l_11, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_110, l_101, l_oid_s);

    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_111, l_101, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_111, l_12, l_oid_s);

-- 20 Rechtemanagement
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_21, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_21, l_107, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_21, l_104, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_21, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_22, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_24, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_25, l_23, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_25, l_22, l_oid_s);

-- 30 Was gibt es neues
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_31, l_11, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_31, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_32, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_33, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_34, l_11, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_34, l_12, l_oid_s);

-- 40 Benutzerverwaltung
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_41, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_41, l_108, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_41, l_73, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_42, l_21, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_43, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_43, l_45, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_44, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_44, l_104, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_45, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_45, l_43, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_45, l_21, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_45, l_22, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_46, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_46, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_47, l_109, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_47, l_12, l_oid_s);

-- 50 Warenkatalog
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_51, l_501, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_51, l_502, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_51, l_511, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_51, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_52, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_53, l_12, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_54, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_54, l_73, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_54, l_108, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_55, l_510, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_55, l_511, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_55, l_59, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_56, l_511, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_57, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_57, l_11, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_57, l_54, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_58, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_58, l_501, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_58, l_502, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_58, l_57, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_59, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_59, l_501, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_59, l_502, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_510, l_102, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_510, l_501, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_510, l_502, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_511, l_55, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_511, l_57, l_oid_s);

    -- 500 Wie suche ich Waren
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_501, l_11, l_oid_s);
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_501, l_101, l_oid_s);
    
    l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_502, l_109, l_oid_s);

-- 60 Diskussionsystem
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_61, l_33, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_61, l_101, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_62, l_21, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_62, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_63, l_21, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_63, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_64, l_61, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_64, l_21, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_64, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_65, l_61, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_65, l_21, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_65, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_66, l_104, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_67, l_104, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_68, l_104, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_69, l_23, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_69, l_22, l_oid_s);
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_69, l_25, l_oid_s);

-- 70 Stammdaten
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_71, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_72, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_73, l_41, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_74, l_41, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_75, l_54, l_oid_s);

-- 80 Terminkalender
l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_81, l_101, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_82, l_102, l_oid_s);

l_retVal := p_Help_01$createRef (l_uid, 1,  16842801, to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), l_83, l_108, l_oid_s);

--

DEBUG ('Domain helpinstall performed.');

END;
/

EXIT;
