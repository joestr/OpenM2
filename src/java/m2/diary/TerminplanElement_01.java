/*
 * Class: TerminplanElement_01.java
 */

// package:
package m2.diary;

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

import m2.diary.DiaryConstants;

import java.util.Date;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ReferenzContainer. <BR/>
 *
 * @version     $Id: TerminplanElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP), 980430
 ******************************************************************************
 */
public class TerminplanElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TerminplanElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $";


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
     * Creates a TerminplanElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public TerminplanElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a TerminplanElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public TerminplanElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
    } // TerminplanElement_01


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
        RowElement tr = new RowElement (DiaryConstants.LST_HEADINGS_TERMINPLAN.length + 1);

        tr.classId = classId;

        TableDataElement td = null;

        // is object new?
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

        // objects name & icon
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if object is a link to another object
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.name));
        if (this.isLink)                // object is a link to another object?
        {
            td = new TableDataElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // if object is a link to another object
        else                            // object is no link
        {
            td = new TableDataElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
        } // else object is no link

        td.classId = classId;

        tr.addElement (td);

        // startdate
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.startDate));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        // enddate
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.endDate));
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        // place
        text = new TextElement (this.place + " ");
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        // owner
        text = new TextElement ("" + this.owner);
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class TerminplanElement_01
