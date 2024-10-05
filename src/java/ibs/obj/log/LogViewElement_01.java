/*
 * Class: LogViewElement_01.java
 */

// package:
package ibs.obj.log;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.io.Environment;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;

import java.util.Date;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * LogView. <BR/>
 *
 * @version     $Id: LogViewElement_01.java,v 1.10 2010/04/07 13:37:15 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 011219
 ******************************************************************************
 */
public class LogViewElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LogViewElement_01.java,v 1.10 2010/04/07 13:37:15 rburgermann Exp $";


    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String  fullName;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String  objectName;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public Date  actionDate;

    /**
     * Store if a attachment is a Master or not. <BR/>
     */
    public String  actionString;


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class.
     */
    public LogViewElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's public properties:
    } // LogViewElement_01


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
        RowElement tr = new RowElement (BOListConstants.LST_HEADINGS_LOGCONTAINER.length + 1);
        TableDataElement td = null;

        td = new TableDataElement (new BlankElement ());
        tr.classId = classId;
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
        text = new TextElement (this.objectName);
        nameGroup.addElement (text);
        td = new TableDataElement (nameGroup);
        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (this.fullName);
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (this.actionString);
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (DateTimeHelpers.dateTimeToString (this.actionDate));
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class LogViewElement_01
