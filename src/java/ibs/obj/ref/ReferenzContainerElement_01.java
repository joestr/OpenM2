/*
 * Class: ReferenzContainerElement_01.java
 */

// package:
package ibs.obj.ref;

// imports:
import java.util.Iterator;
import java.util.Vector;

import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.di.ValueDataElement;
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
 * ReferenzContainer. <BR/>
 *
 * @version     $Id: ReferenzContainerElement_01.java,v 1.12 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980430
 ******************************************************************************
 */
public class ReferenzContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReferenzContainerElement_01.java,v 1.12 2013/01/15 14:48:28 rburgermann Exp $";


    /**
     * type of link -
     *      to or from other object (= BOConstants.LINKTYPE_PHYSICAL)<BR/>
     *      in tab (= BOConstants.LINKTYPE_INTAB)<BR/>
     *      in content (= BOConstants.LINKTYPE_INCONTENT)<BR/>
     */
    private int p_linkType = 0;

    /**
     * Holds the additional values which should be shown in the overview.
     */
    public Vector<ValueDataElement> values = null;

    /**************************************************************************
     * Creates a ReferenzContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public ReferenzContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Return the link type of the element. <BR/>
     *
     * Possible link types:
     *      to or from other object (= BOConstants.LINKTYPE_PHYSICAL)<BR/>
     *      in tab (= BOConstants.LINKTYPE_INTAB)<BR/>
     *      in content (= BOConstants.LINKTYPE_INCONTENT)<BR/>
     *
     * @return  the link type id of the element
     */
    public final int getLinkType ()
    {
        return this.p_linkType;
    } // getLinkType


    /**************************************************************************
     * Set the link type of the element. <BR/>
     *
     * Possible link types:
     *      to or from other object (= BOConstants.LINKTYPE_PHYSICAL)<BR/>
     *      in tab (= BOConstants.LINKTYPE_INTAB)<BR/>
     *      in content (= BOConstants.LINKTYPE_INCONTENT)<BR/>
     *
     * @param   linkType    the link type for the element
     */
    public final void setLinkType (int linkType)
    {
        this.p_linkType = linkType;
    } // setLinkType


    /**************************************************************************
     * Creates a ReferenzContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public ReferenzContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
    } // ReferenzContainerElement_01


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
        TableDataElement td = null;
        GroupElement group;             // group of layout elements

        // extended attributes enables?
        if (this.showExtendedAttributes)
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 1);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_REDUCED.length + 1);
        } // else

        // set class id for this element
        tr.classId = classId;

        // create new group element
        group = new GroupElement ();    // create new group element
        if (this.isNew)                      // the element is new?
        {
            group.addElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
                                        // show "new" icon
        } // if
        else
        {
            group.addElement (new BlankElement ()); // show blank space
        } // else


        // create td element
        td = new TableDataElement (group);
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;
        tr.addElement (td);


        // create icon element
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);


        // create new group element: name
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.p_linkType == BOConstants.LINKTYPE_PHYSICAL || this.isLink)
            // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if
        else if (this.p_linkType == BOConstants.LINKTYPE_INTAB)
            // object is a link from another object in tab?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "ReferenceFrom.gif");
            nameGroup.addElement (img);
        } // else if
        else if (this.p_linkType == BOConstants.LINKTYPE_INCONTENT)
            // object is a link from another object in content?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "ReferenceFrom.gif");
            nameGroup.addElement (img);
        } // else if

        nameGroup.addElement (new TextElement (this.name));

        // create new groupelement
        group = new GroupElement ();
        // add nameGroup to table data element
        group.addElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));

        td = new TableDataElement (group);
        td.classId = classId;
        tr.addElement (td);
        if (this.showExtendedAttributes)
        {
            // type of the object:
            GroupElement typeGroup = new GroupElement ();
            text = new TextElement (this.typeName);
            typeGroup.addElement (text);
            td = new TableDataElement (typeGroup);
            td.classId = classId;
            tr.addElement (td);

            // owner
            text = new TextElement ("" + this.owner);
            td = new TableDataElement (text);
            td.classId = classId;
            tr.addElement (td);

            // date/time
            text = new TextElement (
                DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;
            td.nowrap = true;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } //if
        // do we have additional data fields to be display?
        if (this.values != null)
        {
            // iterate over all additional fields and add them to the table
            Iterator<ValueDataElement> valuesIter = this.values.iterator ();
            while (valuesIter.hasNext ())
            {
                ValueDataElement oneValue = valuesIter.next ();

                text = new TextElement (oneValue.value);
                td = new TableDataElement (text);
                td.classId = classId;
                tr.addElement (td);
            } // while
        } // if
        
        return tr;                      // return the constructed row
    } // show

} // class ReferenzContainerElement_01
