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
    l_10     VARCHAR2(18);     -- Einf�hrung ins Basissystem
        l_11     VARCHAR2(18);     -- Systemaufbau
        l_12     VARCHAR2(18);     -- Privatansicht
        
        l_100    VARCHAR2(18);     -- Basisfunktionen
            l_101    VARCHAR2(18);     -- Informationen Lesen
            l_102    VARCHAR2(18);     -- Informationen Erstellen
            l_103    VARCHAR2(18);     -- Infromationen Bearbeiten
            l_104    VARCHAR2(18);     -- Informationen L�schen
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
        l_25     VARCHAR2(18);     -- Rechtever�nderungen �bertragen

    l_30     VARCHAR2(18);     -- Was gibt es neues
        l_31     VARCHAR2(18);     -- Neuigkeiten Erkennen
        l_32     VARCHAR2(18);     -- Zeitraum Einstellen
        l_33     VARCHAR2(18);     -- Neuigkeitensignal
        l_34     VARCHAR2(18);     -- Eingangskorp
        
    l_40     VARCHAR2(18);     -- Benutzerverwaltung
        l_41     VARCHAR2(18);     -- Benutzer Anlegen
        l_42     VARCHAR2(18);     -- Systemgruppen
        l_43     VARCHAR2(18);     -- Benutzer Zuordnen
        l_44     VARCHAR2(18);     -- Zuordnung L�schen
        l_45     VARCHAR2(18);     -- Gruppen Anlegen
        l_46     VARCHAR2(18);     -- Benutzer L�schen
        l_47     VARCHAR2(18);     -- Gruppen L�schen
        
    
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
        l_510    VARCHAR2(18);     -- Warenprofil Schl�ssel
        l_511    VARCHAR2(18);     -- Sortimentprofil Schl�ssel

        l_500    VARCHAR2(18);     -- Wie Suche ich Waren
            l_501    VARCHAR2(18);     -- Waren Suchen Baum
            l_502    VARCHAR2(18);     -- Waren Suchen Button
        
    l_60     VARCHAR2(18);     -- Diskussionssystem
        l_61     VARCHAR2(18);     -- Diskussion Lesen
        l_62     VARCHAR2(18);     -- Diskussion Erstellen
        l_63     VARCHAR2(18);     -- Thema Erstellen
        l_64     VARCHAR2(18);     -- Beitrag Erstellen
        l_65     VARCHAR2(18);     -- Beitrag Antworten
        l_66     VARCHAR2(18);     -- Beitr�ge L�schen
        l_67     VARCHAR2(18);     -- Themen L�schen
        l_68     VARCHAR2(18);     -- Diskussionen L�schen
        l_69     VARCHAR2(18);     -- Diskussion Ausschlie�en

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
            'Einf�hrung ins Basissystem', l_oid_s, 1, 0, '0x0000000000000000',
            'Nach der Eingabe der entsprechenden URL kommen Sie auf die Einstiegsseite des Systems. Dort geben Sie den jeweiligen Benutzernamen und das zugeh�rige Pa�wort ein. Nach der Best�tigung des Pa�wortes gelangen Sie zur Willkommensseite des Systems.',
            l_10);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject,
            'Wie ist das System aufgebaut', l_10, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '',
            l_helpPath || 'systemaufbau.htm',
            'Gruppe Privat Gruppenbaum Buttons Reiter Einstiegsseite Willkommensseite Navigation S�ubern Listen Ablagen',
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
            'Die Basisfunktionen sind im gesamten System f�r alle Objekte (Informationen) verf�gbar. Zu den Basisfunktionen z�hlen Neu, Bearbeiten, L�schen, Ausschneiden, Kopieren, Verteilen, Link erstellen, Suchen, Drucken und S�ubern.',
            l_100);
---------------------------------------------------Level4-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lese ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_lesen.htm',   
            'Informationen lesen Benutzerprofil Zusatzinformationen Informationen sortieren',
            'Informationen k�nnen von Ihnen �ber den Gruppenbaum gelesen werden. Hier finden Sie zielgerichtete Informationen f�r Ihre Gruppe. ', l_101);
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
            'Die Basisfunktion Neu dient zur Erstellen von neuen Informationen im System. Durch einen KLick auf den Button Neu kann �ber eine Eingabemaske von Ihnen eine neue Information in das System eingebracht werden',
            'Neue Informationen in das System einbringen', l_102);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Bearbeiten von Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_bearbeiten.htm',   
            'Informationen bearbeiten Information �ndern',
            'M�chten Sie den Inhalt, den Namen oder das Verfallsdatum einer von Ihnen eingebrachten Information �ndern, so k�nnen Sie dies �ber den Bearbeiten Button in der Infoansicht durchf�hren. ', l_103);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_loeschen.htm',   
            'Informationen l�schen L�schen Liste l�schen abgelaufene Informationen l�schen Informationen suchen und l�schen',
            'M�chten Sie eine von Ihnen eingebrachte Information, die noch nicht abgelaufen ist, l�schen, so haben Sie dazu die M�glichkeit, dies �ber den L�schen Button in der Infoansicht in der entsprechenden Ablage zu tun. ', l_104);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie schneide ich Informatioen aus', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_ausschneiden.htm',   
            'Informationen ausschneiden Ausschneiden Informationen einf�gen Einf�gen',
            'M�chten Sie eine von Ihnen eingebrachte Information in eine andere Ablage verschieben, so haben Sie die M�glichkeit, dies �ber den Ausschneiden Button zu tun. ', l_105);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kopiere ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_kopieren.htm',   
            'Informationen kopieren Kopieren Einf�gen Information einf�gen',
            'M�chten Sie eine von Ihnen eingebrachte Information in eine andere Ablage kopieren, so k�nnen Sie dies �ber den Kopieren Button durchf�hren. ', l_106);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie verteile ich Informationen', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_verteilen.htm',   
            'Informationen verteilen Verteilen',
            'M�chten Sie Informationen, die von Ihnen in das System eingebracht wurden, speziell an andere Benutzer verteilen, so k�nnen Sie das �ber den Verteilen Button in der Infoansicht durchf�hre. ', l_107);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich einen Link', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'link_erstellen.htm',   
            'Link erstellen Verweise Link einf�gen',
            'M�chten Sie nicht eine Information Kopieren, sondern nur auf sie verweisen, so haben Sie die M�glichkeit, dies �ber den Button Link einf�gen durchzuf�hren. ', l_108);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie suche ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_suchen.htm',   
            'Informationen suchen Suchen Informationen finden Finden',
            'M�chten Sie eine Information suchen k�nnen Sie sie �ber den Button Suchen finden. ', l_109);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie drucke ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_drucken.htm',   
            'Informationen drucken Drucken',
            'M�chten Sie eine Information ausdrucken, so steht Ihnen diese Funktion derzeit f�r die Zusatzinformationen zur Verf�gung, die �ber das Info-Icon angezeigt werden. Gedruckt werden k�nnen Dateien die beispielsweise die Endung *.doc, *.txt, oder *.pdf haben. ', l_110);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie speichere ich eine Information', l_100, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'informationen_speichern.htm',   
            'Informationen speichern Speichern',
            'M�chten Sie eine Information speichern, so steht Ihnen diese Funktion derzeit f�r die Zusatzinformationen zur Verf�gung, die �ber das Info-Icon angezeigt werden. Gespeichert werden k�nnen Dateien die beispielsweise die Endung *.doc, *.txt, oder *.pdf haben. ', l_111);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Rechtemanagement', l_oid_s, 1, 0, '0x0000000000000000',
            'Das Rechtemanagement bietet die M�glichkeit, den verschiedenen Benutzern verschiedene Rechte am System zu verleihen. Das Rechtemangement ist pers�nlich auf jeden Benutzer zuschneidbar.', 
            l_20);
---------------------------------------------------Level3----------------------------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was sind Rechte', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechte.htm',   
            'Rechte',
            'Die Rechte eines Benutzers setzen sich aus vielen Einzelkomponenten zusammen. Durch die Rechtevergabe haben Sie die M�glichkeit, verschiedene Sichten f�r verschiedene Gruppen zu definieren. �ber die Rechtevergabe k�nnen Sie definieren, welche Basisfunktionen die verschiedenen Benutzer am System durchf�hren d�rfen.', l_21);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Was sind Rechtealias', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechtealiases.htm',   
            'Rechtealiases',
            'Um das Rechtemanagement zu vereinfachen, werden einzelnen Rechte in Gruppen zusammengefa�t. Die so gebildeten Gruppen werden Rechtealiases genannt.', l_22);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie bearbeite ich Rechte', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechte_bearbeiten.htm',   
            'Rechte Bearbeiten Rechtealias Bearbeiten Rechte l�schen Rechtealias l�schen',
            'Um Benutzern andere Rechte zu verleihen bzw. um den Gruppen andere Rechte zuzuordnen, k�nnen Sie die Rechte auch bearbeiten. Um Rechte und Rechtealiases bearbeiten zu k�nnen, m�ssen Sie sich immer in dem Rechte Reiter der Ansicht befinden.', l_23);
-----------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Die automatische Rechtekonfiguration', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'automatische_rechtekonfiguration.htm',   
            'Rechte automatisch �bertragen Rechte �bertragen',
            'Die Zuordnung von Rechten auf neue Objekte erfolgt automatisch. Die neuen Objekte in einer Ablage erhalten automatisch die Rechte, die f�r die �bergeordnete Ablage gelten', l_24);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Rechte �ndern und auf untergeordnete �bertragen', l_20, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'rechteveraenderung_uebertragen.htm',   
            'Rechte ver�ndern Rechte �bertragen ver�nderte Rechte �bertragen Rechte vererben',
            'Werden die Rechte in einer �bergeordneten Ablage ver�ndert, k�nnen Sie die ver�nderten Rechte auch auf Objekte in dieser Ablage durch das Vererben von Rechten �bertragen.', l_25);
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
            'Nach der Eingabe der entsprechenden URL, des Benutzernamens und des Pa�wortes gelangen Sie auf die Willkommensseite, auf der die Neuigkeiten angezeigt werden. ', l_31);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie stelle ich den Zeitraum f�r Neuigkeiten ein', l_30, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'zeitraum_einstellen.htm',   
            'Zeitraum f�r Neuigkeiten einstellen Zeitraum einstellen Zeitlimit f�r Eingangskorb',
            'Sie haben die M�glichkeit, individuell einzustellen, welcher Zeitraum (Tage) f�r die Neuigkeiten f�r Sie gelten soll.', l_32);
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
            'Eingangskorb Eintr�ge im Eingangskorb Verteiltes Objekt',
            'Nach der Eingabe der entsprechenden URL, des Benutzernamens und des Pa�wortes gelangen Sie auf die Willkommensseite, auf der auch die Eintr�ge im privaten Eingangskorb angezeigt werden. Diese Eintr�ge wurden von anderen Benutzern des Systems direkt an Sie verteilt.', l_34);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Benutzerverwaltung', l_oid_s, 1, 0, '0x0000000000000000',
            'In der Benutzerverwaltung haben Sie die M�glichkeit, neue Benutzer anzulegen und sie einer bestehenden Gruppe zuzuordnen. Es k�nnen auch neue Gruppen angelegt werden', 
            l_40);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen Benutzer an', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'benutzer_anlegen.htm',   
            'neuen Benutzer anlegen Benutzer anlegen neuer Benutzer',
            'Sie haben die M�glichkeit, selbst neue Benutzer und neue Benutzergruppen f�r Ihr System zu generieren. Um einen neuen Benutzer anlegen zu k�nnen, m�ssen Sie immer in der Gruppenansicht sein.', l_41);
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
            'Jeder Benutzer wird vom System automatisch der Gruppe jeder zugeordnet. Sie haben jedoch die M�glichkeit, die Zuordnung individuell vorzunehmen, um so eine geschlossene Struktur f�r ihre Benutzergruppen und Benutzer zu schaffen. Um einen Benutzer einer Gruppe zuordnen zu k�nnen, m�ssen Sie immer in der Gruppenansicht sein.', l_43);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich die Zuordnung eines Benutzers zu einer Gruppe', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'zuordnung_loeschen.htm',   
            'Zuordnung eines Benutzers zu einer Gruppe l�schen Zuordnung l�schen',
            'Soll ein Benutzer einer bestimmten Gruppe nicht mehr zugeh�ren, k�nnen Sie Ihn aus dieser Gruppe l�schen. Um die Zuordnung eines Benutzers aus einer Gruppe l�schen zu k�nnen, m�ssen Sie immer in der Gruppenansicht sein.', l_45);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich neue Gruppen an', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'gruppen_anlegen.htm',   
            'neue Gruppen anlegen Gruppen anlegen Gruppe anlegen',
            'Um das System an Ihre pers�nlichen Anforderungen anzupassen, haben Sie die M�glichkeit, neue Gruppen anzulegen. Diese Gruppen bestehen dann neben jenen, die beim Setup automatisch angelegt werden.', l_45);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Benutzer aus dem System', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'benutzer_loeschen.htm',   
            'Benutzer aus dem System l�schen Benutzer l�schen',
            'M�chten Sie einen Benutzer aus dem System entfernen, haben Sie die M�glichkeit, dies �ber die Benutzerverwaltung zu l�schen. Wenn das G�ltigkeitsdatum des Benutzers abgelaufen ist, k�nnen Sie ihn �ber das L�schen von abgelaufenen Objekten entfernen.', l_46);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Gruppen aus dem System', l_40, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'gruppen_loeschen.htm',   
            'Gruppen aus dem System l�schen Gruppen l�schen',
            'M�chten Sie eine Gruppe aus dem System entfernen, haben Sie die M�glichkeit, dies �ber die Benutzerverwaltung zu l�schen. Wenn das G�ltigkeitsdatum der Gruppe abgelaufen ist, k�nnen Sie Sie �ber das L�schen von abgelaufenen Objekten entfernen', l_47);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Alles um die Ware', l_oid_s, 1, 0, '0x0000000000000000',
            'Die Struktur des Warenkatalogs besteht aus Warengruppen, Waren und Sortimenten. Im Warenkatalog k�nnen Sie entsprechende Waren finden und diese bestellen.', 
            l_50);
---------------------------------------------------Level2-----------------------------------------------------------------------------------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie bestelle ich Waren', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'ware_bestellen.htm',   
            'Ware bestellen Waren bestellen Bestellen Merken',
            'Die Bestellung der Ware erfolgt, indem Sie die ausgew�hlten Waren vorab aus dem Warenkatalog ausw�hlt. Die ausgew�hlten Waren werden dann automatisch im Warenkorb gesammelt. Durch die Funktion Bestellung k�nnen die Waren aus dem Warenkorb bestellt werden. ', l_51);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie verfolge ich den Status einer Bestellung', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'bestellstatus_verfolgen.htm',   
            'Bestellstatus Bestellstatus verfolgen Status einer Bestellung verfolgen',
            'Sie haben die M�glichkeit, den Status Ihrer bereits abgeschickten Bestellung zu verfolgen und so zu sehen, wie weit Ihre Bestellung fortgeschritten ist. ', l_52);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich meine Bestellung ausdrucken', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'bestellung_drucken.htm',   
            'Bestellung drucken Bestellung ausdrucken Drucken',
            'M�chte Sie Ihre Bestellung in Papierform ablegen, so haben Sie die M�glichkeit die Bestellung direkt �ber das System auszudrucken', l_53);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen Warenkatalog an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warenkatalog_anlegen.htm',   
            'Warenkatalog anlegen neuen Warenkatalog anlegen',
            'Um einen neuen Warenkatalog anzulegen k�nnen Sie �ber den Button Neu in der Ablage Warenangebot vorgehen.', l_54);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Ware an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'ware_anlegen.htm',   
            'neue Ware anlegen Ware anlegen',
            'Um eine nue Ware anzulegen, m�ssen Sie in der Warenablage in diejenige Warengruppe gehen, in der Sie die neue Waren anlegen m�chten.', l_55);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Preise fest', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'preise_festlegen.htm',   
            'Preise festlegen Preise Preise definieren',
            'Nachdem die Ware definiert wurde geben Sie den zugeh�rigen Preis ein. Das System bietet Ihnen verschiedene W�hrungen, in denen Sie die Preise angeben k�nnen.', l_56);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Produktschl�sselkategorien und Produktschl�ssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'psk_ps_anlegen.htm',   
            'Produktschl�sselkategorien anlegen Produktschl�ssel anlegen Produktschl�sselkategorien definieren Produktschl�ssel definieren Produktschl�sselkategorien erstellen Produktschl�ssel erstellen',
            'Produktschl�sselkategorien und Produktschl�ssel m�ssen erstellt werden, um einen Warenkatalog mit Profilen und Schl�sseln anlegen zu k�nnen.', l_57);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Warengruppen an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warengruppen_anlegen.htm',   
            'Warengruppen anlegen Warengruppen definieren Warengruppen erstellen neue Warengruppen Warengruppen',
            'Warengruppen m�ssen erstellt werden, um einen Warenkatalog mit Profilen und Schl�sseln anlegen zu k�nnen.', l_58);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Produktmarken an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'produktmarken_anlegen.htm',   
            'Produktmarken anlegen neue Produktmarken Produktmarke definieren Produktmarken',
            'Produktmarken bieten eine M�glichkeit, wie durch den gesamten Warenkatalog f�r ein Produkt eine Marke pr�sentiert werden kann.', l_59);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Waren mit Profil und Schl�ssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'warenprofil_schluessel.htm',   
            'Waren mit Profil und Schl�ssel anlegen Profil Warenprofil Schl�ssel Warenschl�ssel',
            'Wenn Sie f�r eine Warengruppe Warenprogile und Schl�ssel definiert haben, k�nnen Sie die Ware auf der Basis dieses Profils und dieses Schl�ssels eingeben.', l_510);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich Sortimente mit Profil und Schl�ssel an', l_50, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'sortimentprofil_schluessel.htm',   
            'Sortiment mit Profil und Schl�ssel anlegen Sortiment Schl�ssel Sortimentsschl�ssel Sortiment Profil  Sortimentsprofil',
            'M�chten Sie, da� eine Ware nur im Sortiment bestellt werden kann, k�nnen Sie �ber das System direkt ein Sortiment anlegen. Eine neues Sortiment wird in der Warengruppe angelegt. Die Anlage zum Sortiment geschieht schon bei der Anlage der Ware.', l_511);
---------------------------------------------------Level3-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Wie suche ich nach Waren', l_50, 1, 0, '0x0000000000000000',
            '', 
            l_500);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            '�ber die Baumstruktur', l_500, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'waren_suchen_baum.htm',   
            'Waren suchen Ware suchen Ware �ber Baumstruktur suchen Waren �ber Baumstruktur suchen',
            'Um im Warenkatalog nach einer bestimmten Ware zu suchen, k�nnen Sie �ber die Baumstruktur des Systems vorgehen', l_501);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            '�ber den Button suchen', l_500, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'waren_suchen_button.htm',   
            'Waren suchen Ware suchen Ware �ber Button suchen Waren �ber Button suchen',
            'Um im Warenkatalog nach einer bestimmten Ware zu suchen, k�nnen Sie �ber den Suche Button vorgehen. ', l_502);
---------------------------------------------------Level2-----------------------------------------------------------------------------
    -- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer, -- helpcontainer
            'Diskussionssystem', l_oid_s, 1, 0, '0x0000000000000000',
            'Im Rahmen des Diskussionssystems k�nnen Sie eingebrachte Beitr�ge zu Themen lesen, selbst Beitr�ge und eventuell auch neue Themen erstellen', 
            l_60);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich mir den Inhalt einer Diskussion ansehen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_lesen.htm',   
            'Diskussion lesen Inhalt einer Diskussion ansehen Inhalt einer Diskussion lesen Beitr�ge lesen Themen lesen',
            'Das Diskussionssystem besteht aus Diskussionsablagen, Diskussionen, Themen und Beitr�gen. Themen k�nnen den Diskussionen zugeordnet werden. Zu den Themen k�nnen Beitr�ge erstellt werden, auf die Antworten erstellt werden k�nnen. ', l_61);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich eine neue Diskussion', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_erstellen.htm',   
            'neue Diskussion erstellen neue Diskussion',
            'M�chten Sie aktiv mitdiskutieren, haben Sie M�glichkeit, eine neue Diskussion auf dem System anzuregen.', l_62);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich ein neues Thema', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'thema_erstellen.htm',   
            'neues Thema erstellen Thema erstellen neues Thema',
            'M�chten Sie aktiv mitdiskutieren, haben Sie M�glichkeit, neue Themen zu der Diskussion beizutragen. ', l_63);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie erstelle ich einen neuen Beitrag', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'beitrag_erstellen.htm',   
            'neuen Beitrag erstellen Beitrag erstellen neuer Beitrag',
            'M�chten Sie eine Anmerkung zu einem Thema machen, haben Sie die M�glichkeit, dies �ber einen Beitrag zu tun. ', l_64);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie antworte ich auf einen Beitrag', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '' , 
            l_helpPath || 'beitrag_antworten.htm',   
            'auf einen Beitrag antworten auf Beitrag antworten Antworten',
            'M�chten Sie zu einem Beitrag antworten, haben Sie direkt am System die M�glichkeit dazu.', l_65);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Beitr�ge', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'beitraege_loeschen.htm',   
            'Beitrag l�schen Beitr�ge l�schen',
            'M�chten Sie einzelne Beitr�ge l�schen, so k�nnen Sie dies direkt �ber das System durchf�hren', l_66);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Themen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'themen_loeschen.htm',   
            'Thema l�schen Themen l�schen',
            'M�chten Sie Themen und die dazugeh�rigen Beitr�ge l�schen, k�nnen Sie dies in einem Schritt durchf�hren, indem Sie das Thema l�schen. ', l_67);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie l�sche ich Diskussionen', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussionen_loeschen.htm',   
            'Diskussion l�schen Diskussionen l�schen',
            'M�chten Sie eine Diskussion, die dazugeh�rigen Themen und Beitr�ge auf einmal l�schen, k�nnen Sie dies in einem Schritt durchf�hren, indem Sie die Diskussion l�schen.', l_68);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich Benutzer von einer Diskussion ausschlie�en', l_60, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'diskussion_ausschliessen.htm',   
            'Benutzer aus Diskussion ausschlie�en',
            'M�chten Sie Benutzer oder Benutzergruppen von einer Diskussion ausschlie�en, so haben Sie �ber das Rechteprofil die M�glichkeit dazu.', l_69);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Stammdaten', l_oid_s, 1, 0, '0x0000000000000000',
            '�ber die Stammdaten k�nnen Firmen und Personen im System abgespeichert werden.', 
            l_70);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine Stammdatenablage an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdatenablage.htm',   
            'Stammdatenablage neue Stammdatenablage Stammdatenablage anlegen',
            'Um Ihre Stammdaten alle in eine Ablage einf�gen zu k�nnen, haben Sie die M�glichkeit, eine neue Stammdatenablage anzulegen.', l_71);
------------------ 
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Firma an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'firma_anlegen.htm',   
            'Firma anlegen neue Firma anlegen',
            'Um die Benutzer Ihres Systems eindeutig den Firmen zuordnen zu k�nnen, haben Sie die M�glichkeit, die Firma als Objekt anzulegen.', l_72);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich eine neue Person an', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'person_anlegen.htm',   
            'Person anlegen neue Person anlegen',
            'Um die Benutzer Ihres Systems eindeutig einer Person zuordnen zu k�nnen, haben Sie die M�glichkeit, die Person als Objekt anzulegen.', l_73);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Zusammenhang zwischen Stammdaten und Benutzerverwaltung', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdaten_benutzerverwaltung.htm',   
            'Stammdaten und Benutzerverwaltung Zusammenhang Stammdaten und Benutzerverwaltung',
            'Zwischen der Stammdatenverwaltung und der Benutzerverwaltung besteht ein enger Zusammenhang. Benutzer k�nnen eindeutig Personen zugeordnet werden, wobei eine Person mehrere Benutzer haben kann.', l_74);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Zusammenhang zwischen Stammdaten und Katalogverwaltung', l_70, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'stammdaten_katalogverwaltung.htm',   
            'Stammdaten und Katalogverwaltung Zusammenhang Stammdaten und Katalogverwaltung',
            'Zwischen der Stammdatenverwaltung und der Katalogverwaltung besteht ein enger Zusammenhang. Benutzer k�nnen eindeutig als Bestellverantwortliche, als Katalogverantwortliche oder als Lieferant zugeordnet werden.', l_75);
---------------------------------------------------Level2-----------------------------------------------------------------------------
-- create helpcontainer:
    l_retVal := p_Object$create (l_uid, 1, c_TVHelpContainer,  --  helpcontainer
            'Terminkalender', l_oid_s, 1, 0, '0x0000000000000000',
            '�ber den Terminkalender haben Sie die M�glichkeit, sich Online zu Terminen und Seminaren anzumelden', 
            l_80);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie kann ich den Terminkalender ansehen', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'terminkalender_ansehen.htm',   
            'Terminkalender ansehen',
            'Sie haben die M�glichkeit, Ihre Termine direkt �ber das System zu verwalten. Der Vorteil liegt darin, da� Sie bei Termin�berschneidungen vom System sofort informiert werden.7', l_81);
------------------ 
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen neuen Terminkalender an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'terminkalender_anlegen.htm',   
            'Terminkalender anlegen neuen Terminkalender anlegen',
            'Ben�tigen Sie eine neuen Terminkalender, k�nnen Sie diesen in der Ablage einf�gen.', l_82);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie lege ich einen neuen Termin/Seminar an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_anlegen.htm',   
            'neuen Termin anlege neues Seminar anlegen Termin anlegen Seminar anlegen',
            'M�chten Sie einen neuen Termin bzw. ein neues Seminar im System bekannt geben, haben Sie die M�glichkeit, dies �ber den Terminkalender durchzuf�hren.', l_83);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie melde ich mich zu einem Termin/Seminar an', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_anmelden.htm',   
            'zu Termin anmelden zu Seminar anmelden Anmelden',
            'Gibt es im Terminkalender einen Termin oder ein Seminar, das Sie interessiert, k�nnen Sie sich direkt �ber das System zu einem Termin bzw. Seminar anmelden. ', l_84);
------------------
    l_retVal := p_Help_01$createFast (l_uid, 1, c_TVHelpObject, 
            'Wie melde ich mich von einem Termin/Seminar ab', l_80, 1, 0, '0x0000000000000000',
            to_Date ('#CONFVAR.ibsbase.validUntilSql#', 'DD.MM.YYYY'), '', 
            l_helpPath || 'termin_abmelden.htm',   
            'von Termin abmelden von Seminar abmelden Abmelden',
            'M�chten Sie sich von einem Termin oder einem Seminar abmelden, haben Sie die M�glichkeit, die direkt �ber das System vorzunehmen. ', l_85);
-------------------

DEBUG ('Create new Referenzes!');

-- create Referenzes
-- 10 Einf�hrung ins Basissytem
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
