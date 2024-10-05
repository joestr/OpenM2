/*
 * Class: RecipientContainerElement_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
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
 * @version     $Id: RecipientContainerElement_01.java,v 1.13 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980430
 *
 * @see         ibs.obj.wsp.RecipientContainer_01
 ******************************************************************************
 */
public class RecipientContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RecipientContainerElement_01.java,v 1.13 2010/04/07 13:37:04 rburgermann Exp $";


    /**
     * The Id of the recipient
     */
    public OID recipientId = null;
    /**
     * The Id of the recipient
     */
    public String recipientName = "";
    /**
     * The recipiant has read at this time the the message
     */
    public Date readDate = null;
    /**
     * A Sting for show Date to the User
     */
    public String showDate = "";

    /**
     * Date of creation. <BR/>
     */
    public Date creationDate = null;

    /**
     * The Id of the sent object
     */
    public OID sentObjectId = null;

    /**
     * The name of the distributed Object
     */
    public String distributeName = "Verteiltes Objekt";

    /**
     * The Id of the sent object
     */
    public OID distributeId = null;

    /**
     * The Name of the icon of a type of BusinesObject. <BR/>
     */
    public String distributeIcon = null;


    /**************************************************************************
     * Creates a RecipientContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public RecipientContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:

    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a RecipientContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public RecipientContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:

    } // RecipientContainerElement_01_01


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
        RowElement tr = new RowElement (BOListConstants.LST_HEADINGS_RECIPIENT.length + 1);
        TableDataElement td = null;

        if (this.readDate == null)
        {
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else

        tr.classId = classId;

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
        text = new TextElement (this.recipientName);
        nameGroup.addElement (text);

        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (this.recipientId.toString ())));
        td.classId = classId;

        tr.addElement (td);
        GroupElement typeGroup = new GroupElement ();

        if (this.distributeIcon == null)               // no icon provided?
        {
            this.icon = "Referenz.gif";   // get icon from type
        } // if
        else
        {
            this.icon = this.distributeIcon;
        } // else
        img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        typeGroup.addElement (img);
        text = new TextElement (this.distributeName);
        typeGroup.addElement (text);
        td = new TableDataElement (new LinkElement (typeGroup,
            IOHelpers.getShowObjectJavaScriptUrl (this.distributeId.toString ())));

        td.classId = classId;

        tr.addElement (td);

        // the time the distribution is done
        text = new TextElement (
            DateTimeHelpers.dateTimeToString (this.creationDate));
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        if (this.readDate == null)
        {
            this.showDate = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTREAD, env);
        } // if
        else
        {
            this.showDate = DateTimeHelpers.dateTimeToString (this.readDate);
        } // else

        text = new TextElement (this.showDate);
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);
        return tr;                      // return the constructed row

    } // show
/*
    public RowElement show (int representationForm, String bgcolor)
    {

        TextElement text = null;
        RowElement tr = new RowElement BOListConstants.LST_HEADINGS_RECIPIENT.length + 1);
        tr.bgcolor = bgcolor;
        TableDataElement td = null;

        if (readDate == null)
            td = new TableDataElement (new ImageElement (PATH_GLOBAL + "new.gif"));
        else
            td = new TableDataElement (new BlankElement ());
        tr.addElement (td);

        if (icon == null)               // no icon provided?
            icon = typeName + ".gif";   // get icon from type
        ImageElement img = new ImageElement (PATH_OBJECTICONS + icon);
//        img.alt = "Info zu " + recipientName;

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        text = new TextElement (recipientName);
        nameGroup.addElement (text);
        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (oid.toString ())));
        tr.addElement (td);

       if (readDate == null)  showDate = "noch nicht gelesen";
            else showDate = Helpers.dateTimeToString (readDate);

        text = new TextElement (showDate);
        td = new TableDataElement (text);
        tr.addElement (td);
        return (tr);                    // return the constructed row

    } // show
*/
} // class RecipientContainerElement_01
