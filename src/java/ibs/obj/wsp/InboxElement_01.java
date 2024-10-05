/*
 * Class: InboxElement_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;

import java.util.Date;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * Inbox. <BR/>
 *
 * @version     $Id: InboxElement_01.java,v 1.15 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980430
 ******************************************************************************
 */
public class InboxElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: InboxElement_01.java,v 1.15 2010/04/07 13:37:04 rburgermann Exp $";


    /**
     * Oid of the BO we have sent. <BR/>
     */
    public OID distributeId = null;

    /**
     * The Type of the distributed Object. <BR/>
     */
    public int distributeType = 0x00000000;

    /**
     * The Type of the distributed BusinesObject. <BR/>
     */
    public String distributeTypeName = null;

    /**
     * The Name of the distributed BusinesObject. <BR/>
     */
    public String distributeName = "";

    /**
     * The Name of the icon of a type of BusinesObject. <BR/>
     */
    public String distributeIcon = null;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String activities = null;

    /**
     * Date of creaton. <BR/>
     */
    public Date creationDate = null;

    /**
     * Date when a BO ist distributed. <BR/>
     */
    public String creator = "";


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public InboxElement_01 ()
    {
        // call constructor of super class:
        super ();
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public InboxElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // SendObjectContainerElement_01_01


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
        RowElement tr = new RowElement (BOListConstants.LST_HEADINGS_INBOX.length + 1);
        TableDataElement td = null;

        // display the new icon if nesseccary
        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        tr.classId = classId;
        tr.addElement (td);

        // create the object icon
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if

        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();

        // display the subject with a link to the inbox object:
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        text = new TextElement (this.name);
        nameGroup.addElement (text);
        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ())));

        td.classId = classId;
        tr.addElement (td);

        // display the name of the object that has been sent with a link:
        if (this.distributeId != null && !this.distributeId.isEmpty ())
        {
            GroupElement group = new GroupElement ();
            img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS +
                    this.distributeIcon);
            group.addElement (img);
            group.addElement (new BlankElement ());
            text = new TextElement (this.distributeName);
            group.addElement (text);
            td = new TableDataElement (new LinkElement (group,
                IOHelpers.getShowObjectJavaScriptUrl (this.distributeId.toString ())));
//            td.classId = classId;
            td.classId = classId + "_objRef";
        } // if
        else                            // display a blank element
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        tr.addElement (td);

        // display the activities:
        if (this.activities != null)
        {
            text = new TextElement (this.activities);
            td = new TableDataElement (text);
        } // if
        else                            // display a blank element
        {
            td = new TableDataElement (new BlankElement ());
        } // else display a blank element
        td.classId = classId;
        tr.addElement (td);

        // display the date and time when the object has been sent
        text = new TextElement (
            DateTimeHelpers.dateTimeToString (this.creationDate));
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        // display the creator = sender
        text = new TextElement (this.creator);
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        return tr;
    } // show

} // class SendObjectContainerElement_01
