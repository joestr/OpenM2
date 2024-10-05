/*
 * Class: DiscussionContainerElement_01.java
 */

// package:
package m2.bbd;

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
 * DiscussionContainer. <BR/>
 *
 * @version     $Id: DiscussionContainerElement_01.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980508
 ******************************************************************************
 */
public class DiscussionContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiscussionContainerElement_01.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $";


    /**
     * number of new entries in the discussion. <BR/>
     */
    public int newEntries = -1;


    /**************************************************************************
     * Creates a DiscussionContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public DiscussionContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
        this.newEntries = -1;
    } // DiscussionContainerObject_01


    /**************************************************************************
     * Creates a DiscussionContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public DiscussionContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
        this.newEntries = -1;
    } // DiscussionContainerElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     The CSS class id.
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
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 2);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length - 1);
        } // else

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

        if (this.icon == null)          // no icon provided?
        {
            // get icon from type:
            this.icon = this.typeName + ".gif";
        } // if
        ImageElement img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();

        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if object is a link to another object

        text = new TextElement (this.name);
        nameGroup.addElement (text);

        GroupElement group = new GroupElement ();
        if (this.isLink)                // object is a link to another object?
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // if
        else                            // object is no link
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
        } // else object is no link
        td = new TableDataElement (group);

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
        } //if

        // column of the  # of unread messages
        text = new TextElement ("" + this.newEntries);
        td = new TableDataElement (text);

        td.classId = classId;

        td.alignment = IOConstants.ALIGN_RIGHT;
        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class DiscussionContainerElement_01
