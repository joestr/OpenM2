/*
 * Class: HelpContainerElement_01.java
 */

//package:
package ibs.obj.help;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * HelpContainer. <BR/>
 *
 * @version     $Id: HelpContainerElement_01.java,v 1.13 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Mario Stegbauer (MS), 990608
 ******************************************************************************
 */
public class HelpContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HelpContainerElement_01.java,v 1.13 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * goal, which should be reached. <BR/>
     */
    public String goal = "";


    /**************************************************************************
     * Creates a HelpContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public HelpContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        this.goal = "";
    } // HelpContainerObject_01


    /**************************************************************************
     * Creates a HelpContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public HelpContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
        this.goal = "";
    } // HelpContainerElement_01


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
        RowElement tr;

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_HELPCONTAINER.length + 1);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_HELPCONTAINERREDUCED.length + 1);
        } // if

        tr.classId = classId;

        TableDataElement td = null;

        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else

        td.classId = classId;

        td.width = BOListConstants.LST_NEWCOLWIDTH;
        tr.addElement (td);

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
        text = new TextElement (this.name);
        nameGroup.addElement (text);

        GroupElement group = new GroupElement ();

        if (this.isLink)  // object is a link to another object?
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // if
        else  // object is no link
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
        } // else object is no link

        td = new TableDataElement (group);
        td.classId = classId;
        tr.addElement (td);

        // column of the goals, which should be reached after reading the help
        GroupElement gr = new GroupElement ();
        if (this.goal == null)
        {
            group.addElement (new BlankElement (1));
            this.goal = "";
        } // if
        else if (this.goal.isEmpty ())
        {
            group.addElement (new BlankElement (1));
        } // if
        else
        {
            gr.addElement (IOHelpers.getTextField (this.goal));
        } // else
        td = new TableDataElement (gr);

        td.classId = classId;
        tr.addElement (td);

        if (this.showExtendedAttributes)
        {
            GroupElement typeGroup = new GroupElement ();
            text = new TextElement (this.typeName);
            typeGroup.addElement (text);
            td = new TableDataElement (typeGroup);
            td.classId = classId;

            tr.addElement (td);

            // column of the owner of the userobject
            text = new TextElement ("" + this.owner);
            td = new TableDataElement (text);
            td.classId = classId;
            tr.addElement (td);

            // column of the last change of the userobject
            text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;
            td.nowrap = true;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } // if

        return tr;                      // return the constructed row
    } // show

} // HelpContainerElement_01
