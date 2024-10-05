/*
 * Class: ParticipantsContainerElement_01.java
 */

// package:
package m2.diary;

// imports:
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
 * ParticipantContainer. <BR/>
 *
 * @version     $Id: ParticipantContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP)    980430
 ******************************************************************************
 */
public class ParticipantContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ParticipantContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * Full name of participant. <BR/>
     */
    public String fullName;

    /**
     * Announcement date. <BR/>
     */
    public Date announcementDate;

    /**
     * Announcement date. <BR/>
     */
    public String announcer;


    /**************************************************************************
     * Creates a OverlapContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public ParticipantContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
        this.fullName = null;

        this.announcementDate = null;
        this.announcer = null;
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a OverlapContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public ParticipantContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
        this.fullName = null;

        this.announcementDate = null;
        this.announcer = null;
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
        RowElement tr = new RowElement (DiaryConstants.LST_HEADINGS_PARTICIPANTS.length + 1);

        tr.classId = classId;
        TableDataElement td = null;
/*
        ImageElement img = new ImageElement (AppConstants.PATH_OBJECTICONS + icon);
        td = new TableDataElement (img);
        tr.addElement (td);
*/

        td = new TableDataElement ();

        td.classId = classId;

        td.width = "30";
        tr.addElement (td);

        // objects name ( = participants full name) & icon
        ImageElement img = new ImageElement (this.layoutpath +
                                             BOPathConstants.PATH_OBJECTICONS +
                                             this.icon);
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                                    BOPathConstants.PATH_OBJECTICONS +
                                    "Referenz.gif");
            nameGroup.addElement (img);
        } // if object is a link to another object

        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.fullName));

        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));

        td.classId = classId;

        tr.addElement (td);

/*
        // full name of participant
        text = new TextElement (this.fullName);
        td = new TableDataElement (text);
        tr.addElement (td);
*/

        // announced on
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.announcementDate));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        // announcers name
        text = new TextElement (this.announcer);
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class ParticipantContainerElement_01
