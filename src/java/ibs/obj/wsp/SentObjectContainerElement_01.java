/*
 * Class: SentObjectContainerElement_01.java
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
 * ReferenzContainer. <BR/>
 *
 * @version     1.10.0001, 30.07.1999
 *
 * @author      Klaus Reimüller (KR), 980430
 *
 * @see         ibs.obj.wsp.SentObjectContainer_01
 ******************************************************************************
 */
public class SentObjectContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SentObjectContainerElement_01.java,v 1.12 2010/04/07 13:37:04 rburgermann Exp $";


    /**
     * Oid of the BO we have sent. <BR/>
     */
    public OID distributeId = null;

    /**
     * The Oid of the recipientContainer. <BR/>
     */
    public OID recipientContainerId = null;


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
    public String distributeName = "PRSpielplan";

    /**
     * The Name of the icon of a type of BusinesObject. <BR/>
     */
    public String distributeIcon = null;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String activities = null;

    /**
     * Date when a BO ist distributed. <BR/>
     */
    public String recipients = "Empfängerliste ansehen";

    /**
     * Date of creaton. <BR/>
     */
    public Date creationDate = null;


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public SentObjectContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:

    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public SentObjectContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:

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
        RowElement tr = new RowElement (BOListConstants.LST_HEADINGS_SENTOBJECTCONTAINER.length + 1);
        TableDataElement td = null;
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

        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();

        // display the name of the sentObject instance
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
        td = new TableDataElement (
                new LinkElement (
                    nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ())));

        td.classId = classId;

        tr.addElement (td);
        text = new TextElement (this.activities);
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);
        text = new TextElement (
            DateTimeHelpers.dateTimeToString (this.creationDate));
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

/* Liste direct im Ausgangskorb anzeigen
        // display name of the object sent
        GroupElement recipientGroup = new GroupElement ();
        text = new TextElement (recipients);
        recipientGroup.addElement (text);
        td = new TableDataElement (new LinkElement (recipientGroup,
           IOHelpers.getShowObjectJavaScriptUrl (recipientContainerId.toString ())));
        tr.addElement (td);
*/
        return tr;
    } // show

} // class SendObjectContainerElement_01
