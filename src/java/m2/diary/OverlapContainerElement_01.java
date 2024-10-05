/*
 * Class: OverlapContainerElement_01_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.tech.html.ImageElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;

import m2.diary.DiaryConstants;

import java.util.Date;


/******************************************************************************
 * This class represents all necessary properties of one element of an
 * OverlapContainer. <BR/>
 *
 * @version     $Id: OverlapContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP)    980430
 ******************************************************************************
 */
public class OverlapContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OverlapContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * Start date of term. <BR/>
     */
    public Date startDate;

    /**
     * End date of term. <BR/>
     */
    public Date endDate;

    /**
     * Place where term happens. <BR/>
     */
    public String place;


    /**************************************************************************
     * Creates a OverlapContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public OverlapContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
        this.startDate = null;
        this.endDate = null;
        this.place = null;
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a OverlapContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public OverlapContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
        this.startDate = null;
        this.endDate = null;
        this.place = null;
    } // OverlapContainerElement_01_01


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
        RowElement tr = new RowElement (DiaryConstants.LST_HEADINGS_OVERLAP.length + 1);

        tr.classId = classId;
        TableDataElement td = null;

        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        td = new TableDataElement (img);

        td.classId = classId;

        td.width = BOListConstants.LST_NEWCOLWIDTH;
        tr.addElement (td);

        text = new TextElement (this.name);
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

/*
        GroupElement typeGroup = new GroupElement ();
        if (isLink)                     // object is a link?
        {
            img = new ImageElement (AppConstants.PATH_OBJECTICONS + "Referenz.gif");
            typeGroup.addElement (img);
            text = new TextElement (typeName);
            typeGroup.addElement (text);
        } // if object is a link?
        else                            // object is not a link
        {
            text = new TextElement (typeName);
            typeGroup.addElement (text);
        } // else object is not a link
        td = new TableDataElement (typeGroup);
        tr.addElement (td);
*/

        text = new TextElement (DateTimeHelpers.dateTimeToString (this.startDate));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (DateTimeHelpers.dateTimeToString (this.endDate));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (this.place + " ");
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class OverlapContainerElement_01
