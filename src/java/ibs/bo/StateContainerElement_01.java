/*
 * Class: StateContainerElement_01.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.workflow.WorkflowConstants;
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
 * StateContainer. <BR/>
 *
 * @version     $Id: StateContainerElement_01.java,v 1.9 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Thomas Joham   (TJ)    010323
 ******************************************************************************
 */
public class StateContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StateContainerElement_01.java,v 1.9 2010/04/07 13:37:08 rburgermann Exp $";


    /**
     * The name of the workflow. <BR/>
     */
    public String workflowState = null;

    /**
     * The oid from the workflow. <BR/>
     */
    public String workflowOid = null;

    /**
     * The date of the last change at the workflow. <BR/>
     */
    public Date stateChangeDate = null;


    /**************************************************************************
     * Creates a StateContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public StateContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
    } // StateContainerElement_01


    /**************************************************************************
     * Creates a StateContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public StateContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // StateContainerElement_01


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

        // set super attribute
        if (this.showExtendedAttributes)
        {
            tr = new RowElement (6);
        } // if
        else
        {
            tr = new RowElement (3);
        } // else

        tr.classId = classId;
        TableDataElement td = null;

        // group of layout elements
        GroupElement group;

        // set text to be shown on mouse over:
        group = new GroupElement ();    // create new group element
        group.addElement (new BlankElement ()); // show blank space

        // show is-New icon:
        if (this.isNew)                 // the element is new?
        {
            group.addElement (new ImageElement (
                             BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if show "new" icon
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
        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if icon is not set

        ImageElement img = new ImageElement (this.layoutpath +
                     BOPathConstants.PATH_OBJECTICONS + this.icon);

        // create new group element: name
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // create new group element: name
        nameGroup.addElement (new TextElement (this.name));

        group = new GroupElement ();    // create new group element

        // create new group element: link
        if (this.isLink)                // object is a link to another object?
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // create new group element: link
        else                            // object is no link
        {
            group.addElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
        } // else object is no link

        td = new TableDataElement (group);
        td.classId = classId;
        tr.addElement (td);

        // state of the workflow
        if (this.workflowOid != null)
        {
            text = new TextElement (this.workflowState);
            td = new TableDataElement (new LinkElement (text,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.workflowOid)));
        } // state of the workflow
        else                            // object is no link
        {
            text = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOWORKFLOWSTATE, env));
            td = new TableDataElement (text);
        } // else
        td.classId = classId;
        tr.addElement (td);

        // last change date of the workflow
        if (this.workflowOid == null ||
            this.workflowState.equals (WorkflowConstants.STATE_UNDEFINED))
        {
            text = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOWORKFLOWSTATE, env));
            td = new TableDataElement (text);

        } // last change date of the workflow
        else                            // is stateChangeDate
        {
            text = new TextElement (DateTimeHelpers
                .dateTimeToString (this.stateChangeDate));
            td = new TableDataElement (text);
        } // else
        td.classId = classId;
        tr.addElement (td);

        // show extended attributes
        if (this.showExtendedAttributes)
        {
            // type of the object:
            text = new TextElement (this.typeName);
            td = new TableDataElement (text);
            td.classId = classId;
            tr.addElement (td);

            // owner
            text = new TextElement ("" + this.owner);
            td = new TableDataElement (text);
            td.classId = classId;
            tr.addElement (td);

            // date/time
            text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;
            td.nowrap = true;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } // show extended attributes
        return tr;                      // return the constructed row
    } // show

} // class StateContainerElement_01
