/*
 * Class: MasterDataContainerElement_01.java
 */

// package:
package m2.mad;

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
 * MasterDataContainer. <BR/>
 *
 * @version     $Id: MasterDataContainerElement_01.java,v 1.16 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980610
 ******************************************************************************
 */
public class MasterDataContainerElement_01 extends ContainerElement
{
    /**
     * email of a person. <BR/>
     */
    public String email = "";

    /**
     * owner of a company (hack for KW). <BR/>
     */
    public String compowner = "";

    /**
     * Icon for the object. <BR/>
     */
    private static final String CLASS_ICON = "Referenz.gif";



    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public MasterDataContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
        this.email = "";
        this.compowner = "";
    } // MasterDataContainerObject_01


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public MasterDataContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // set the instance's public properties:
        this.email = "";
        this.compowner = "";
    } // MasterDataContainerElement_01


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
        LinkElement link = null;

        RowElement tr;

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 3);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 1);
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

        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();

        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS +
                MasterDataContainerElement_01.CLASS_ICON);
            nameGroup.addElement (img);
        } // if

        text = new TextElement (this.name);
        nameGroup.addElement (text);

        GroupElement group = new GroupElement ();
        if (this.isLink)                // object is a link to another object?
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // if object is a link to another object
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

            if (this.isLink)            // object is a link?
            {
                img = new ImageElement (this.layoutpath +
                    BOPathConstants.PATH_OBJECTICONS +
                    MasterDataContainerElement_01.CLASS_ICON);
                typeGroup.addElement (img);
                text = new TextElement (this.typeName);
                typeGroup.addElement (text);
            } // if object is a link?
            else                            // object is not a link
            {
                text = new TextElement (this.typeName);
                typeGroup.addElement (text);
            } // else object is not a link
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


        // column of the owner of the company displayed
        text = new TextElement (this.compowner);
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        // column of the  email of the person displayed
        text = new TextElement (this.email);
        link = new LinkElement (text, "mailto:" + this.email);
        td = new TableDataElement (link);

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class MasterDataContainerElement_01
