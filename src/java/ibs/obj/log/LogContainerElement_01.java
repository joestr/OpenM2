/*
 * Class: LogContainerElement_01.java
 */

// package:
package ibs.obj.log;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.BlankElement;
import ibs.tech.html.DivElement;
import ibs.tech.html.Element;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;

import java.util.Date;
import java.util.List;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ReferenzContainer. <BR/>
 *
 * @version     $Id: LogContainerElement_01.java,v 1.15 2012/11/07 10:25:38 rburgermann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ), 980602
 ******************************************************************************
 */
public class LogContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LogContainerElement_01.java,v 1.15 2012/11/07 10:25:38 rburgermann Exp $";

    /**
     * The ID of the protocol of this element. <BR/>
     */
    public int  id;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String  fullName;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String  objectName;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public Date  actionDate;

    /**
     * Store if a attachment is a Master or not. <BR/>
     */
    public String  actionString;

    /**
     * A list with all container element entries. <BR/>
     */
    public List<LogContainerElementEntry_01>  entries;

    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public LogContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's public properties:
    } // LogContainerElement_01


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public LogContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
    } // LogContainerElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     The CSS class to be set for the actual element.
     * @param   env         The current environment
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        TextElement text = null;
        RowElement tr = new RowElement (BOListConstants.LST_HEADINGS_LOGCONTAINER.length + 1);
        TableDataElement td = null;

        // This is the element within the first column.
        // If sub entries exist it includes a toggle button otherwise it is blank.
        Element firstColumnElement;

        String localId = this.id + 
            Long.toString (this.actionDate.getTime ()); // Long.toString(Calendar.getInstance().getTimeInMillis());

        if (this.entries != null && this.entries.size () > 0)
        {
            // The table containing the element entries rows
            TableElement table = new TableElement ();
            table.width = HtmlConstants.TAV_FULLWIDTH;
            table.classId = CssConstants.CLASS_LIST_ENTRIES_TABLE;

            // The div element surround the entries table
            // necessary for being able to hide the table.
            DivElement entriesGroup = new DivElement ();
            String entriesSubId = localId + "_sub";
            entriesGroup.id = entriesSubId;
            entriesGroup.classId = CssConstants.CLASS_SUB_ENTRIES_TABLE_CLOSED;
            entriesGroup.addElement (table);

            // Create the header
            RowElement headerRow = new RowElement (
                BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY.length + 1);
            headerRow.classId = CssConstants.CLASS_LISTHEADER;

            TableDataElement headerTd = new TableDataElement (new TextElement (IE302.HCH_NBSP));
            headerRow.classId = CssConstants.CLASS_LISTHEADER;
            headerRow.addElement (headerTd);

            for (int i = 0; i < BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY.length; i++)
            {
                headerTd = new TableDataElement (new TextElement (
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY[i], env)));
                headerTd.classId = CssConstants.CLASS_LISTHEADER;
                headerRow.addElement (headerTd);
            } // for

            // add header to table
            table.addElement (headerRow, true);

            //Add the element entries
            for (int i = 0; i < this.entries.size (); i++)
            {
                LogContainerElementEntry_01 entry = this.entries.get (i);

                RowElement entryRow = entry.show (
                    BOListConstants.LST_CLASSSUBROWS[i %
                        BOListConstants.LST_CLASSSUBROWS.length], env);
                table.addElement (entryRow);
            } // for

            // styles
            String styleOpen = "'" + CssConstants.CLASS_SUB_ENTRIES_TABLE_OPEN + "'";
            String styleClosed = "'" + CssConstants.CLASS_SUB_ENTRIES_TABLE_CLOSED + "'";

            // images
            String imageOpen = "'" + this.layoutpath +
                BOPathConstants.PATH_IMAGE_MENU + "menu_open.gif'";
            String imageClosed = "'" + this.layoutpath +
                BOPathConstants.PATH_IMAGE_MENU + "menu_closed.gif'";

            // The span tag including the toggle button
            SpanElement spanElem = new SpanElement ();
            spanElem.id = "div_" + localId;
            spanElem.title = "Click here to show/hide field changes.";

            // The toggle button
            ImageElement image = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_IMAGE_MENU + "menu_closed.gif");
            image.id = localId + "_img";
            LinkElement href = new LinkElement (image,
                "javascript:top.scripts.toggleStyle (document, '" +
                    entriesSubId + "'," + styleOpen + ", " + styleClosed + "," +
                    "'" + image.id + "', " + imageOpen + ", " + imageClosed +
                    ");");

            spanElem.addElement (href);

            // Group element including the toggle button and the sub entries div
            GroupElement group = new GroupElement ();
            group.addElement (spanElem);
            group.addElement (entriesGroup);

            // Java script to move the sub entries div after the row which is returned by this method
            ScriptElement script = new ScriptElement ("JavaScript");

            // The move operation has to be performed with an onload function since the element to be moved
            // has to be rendered completely first.
            script.addScript ("if (window.onload == null) { window.onload = moveEntries; }");

            // Create a new list and add all element to move
            script.addScript ("if (!window.list || window.list == null) {window.list = new Array ()};");
            script.addScript ("window.list.push ('" + entriesSubId + "');");

            // Move function
            script.addScript ("function moveEntries () {" +
                "for (var i = 0; i < window.list.length; i++) {" +
                    "var elem = document.getElementById (window.list[i]);" +
                    "var tr = document.createElement (\"tr\");" +
                    "var td = document.createElement (\"td\");" +
                    "var prevSibling = elem.parentNode.parentNode;" +
                    "var parentNode = elem;" +
                    "while (parentNode != null && parentNode.nodeName != 'TBODY') {" +
                         "parentNode = parentNode.parentNode;" +
                    "}" +
                    "tr.appendChild (td);" +
                    "td.colSpan = prevSibling.childNodes.length;" +
                    "td.appendChild (elem);" +
                    "parentNode.insertBefore (tr, prevSibling);" +
                    "parentNode.insertBefore (prevSibling, tr);" +
                "}" +
                "}");
            group.addElement (script);


            firstColumnElement = group;
        } // if
        else
        {
            firstColumnElement = new BlankElement ();
        } // else

        // includ the first column into the first td of the returned row
        td = new TableDataElement (firstColumnElement);
        tr.classId = classId;
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = "tdOverflow";
        tr.addElement (td);

        // td including the object icon and the object name
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.isLink)                 // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if
        text = new TextElement (this.objectName);
        nameGroup.addElement (text);
        td = new TableDataElement (nameGroup);
        td.classId = classId;
        tr.addElement (td);

        // td including the username
        text = new TextElement (this.fullName);
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        // td including the action
        text = new TextElement (this.actionString);
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        // td including the action date
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.actionDate));
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        // return the constructed row:
        return tr;
    } // show

} // class LogContainerElement_01
