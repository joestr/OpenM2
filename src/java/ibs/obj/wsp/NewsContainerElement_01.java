/*
 * Class: NewsContainerElement_01.java
 */

// package:
package ibs.obj.wsp;

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
 * NewsContainer. <BR/>
 *
 * @version     $Id: NewsContainerElement_01.java,v 1.13 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author      Mario Stegbauer (MS), 990531
 ******************************************************************************
 */
public class NewsContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NewsContainerElement_01.java,v 1.13 2010/04/07 13:37:04 rburgermann Exp $";


    /**************************************************************************
     * Creates a NewsContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public NewsContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
    } // NewsContainerObject_01


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public NewsContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // NewsContainerElement_01


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
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 2);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_NEWSCONTAINERREDUCED.length + 2);
        } // else

        tr.classId = classId;

        TableDataElement td = null;

        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (
                BOPathConstants.PATH_GLOBAL + "new.gif"));
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

        // check if the object has an attachment defined (file or hyperlink):
        if ((this.masterSelect != MAS_HYPERLINK) &&
            (this.masterSelect != MAS_DOKUMENT))
                                        // no attachment defined?
        {
            if (this.isLink)                 // object is a link to another object?
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
            } // if
            else                        // object is no link
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // else object is no link
        } // end no attachment defined
        else                            // a attachment is defined
        {
            if (this.isLink)                 // is a linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                     IOConstants.URL_JAVASCRIPT +
                     "top.loadMaster ('" + this.linkedObjectId + "');"));
            } // if is a linkobject
            else                        // it is no linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                     IOConstants.URL_JAVASCRIPT +
                     "top.loadMaster ('" + this.oid + "');"));
            } // else it is no linkobject
        } // end of a attachment is defined

        // add infoicon and create a link
        // to the Info of a object
        if ((this.masterSelect == MAS_HYPERLINK) ||
            (this.masterSelect == MAS_DOKUMENT))
        {
            ImageElement imgFile =
                new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + "info.gif");

            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());

            if (this.isLink)  // object is a link to another object?
            {
                group.addElement (new LinkElement (imgFile,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
            } // if
            else  // object is no link
            {
                group.addElement (new LinkElement (imgFile,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // else object is no link
        } // if

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

        } // if

        // column of the last change of the userobject
        text = new TextElement (
            DateTimeHelpers.dateTimeToString (this.lastChanged));
        td = new TableDataElement (text);
        td.classId = classId;

        td.nowrap = true;
        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // NewsContainerElement_01
