/*
 * Class: RigthsContainerElement_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.UserArguments;
import ibs.obj.user.UserConstants;
import ibs.obj.user.UserTokens;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;

import java.util.BitSet;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * RightsContainer. <BR/>
 *
 * @version     $Id: RightsContainerElement_01.java,v 1.15 2010/05/21 11:32:23 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980615
 ******************************************************************************
 */
public class RightsContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RightsContainerElement_01.java,v 1.15 2010/05/21 11:32:23 btatzmann Exp $";


    /**
     * interne Zahl (brauch ich das eigentlich?) . <BR/>
     */
    public int id = -1;

    /**
     * OID of the Object for that the right counts . <BR/>
     */
    public OID rOid = null;

    /**
     * id of the Person/Group/Role for that the right counts . <BR/>
     */
    public int rPersonId = -1;

    /**
     * Name of the Person/Group/Role for that the right counts . <BR/>
     */
    public String rPersonName = null;

    /**
     * Oid of the Person/Group/Role for that the right counts . <BR/>
     */
    public OID rPersonOid = null;

    /**
     * rights for the object. <BR/>
     */
    public int rights = -1;

    /**
     * rights for the object as BitSet. <BR/>
     */
    public BitSet bRights = null;

    /**
     * rights for the object as String. <BR/>
     */
    public String rightsString = null;

    /**
     * checkbox 'nochange' in showChangeForm with rightaliases checked ??. <BR/>
     */
    public boolean noChangeChecked = false;

    /**
     * Show only right aliases or all rights? <BR/>
     */
    public boolean showExtendedRights = false;


    /**************************************************************************
     * Creates a RightsContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public RightsContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        this.icon = null;

        // initialize the instance's public properties:
    } // RightsContainerObject_01


    /**************************************************************************
     * Creates a RightsContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public RightsContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // set the instance's public properties:
    } // RigthsContainerElement_01


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
        if (this.showExtendedRights)
        {
            tr = new RowElement (Operations.OP_SHORTNAMES.length + 2);
        } // if
        else
        {
            tr = new RowElement (UserTokens.ML_RIGHTALIASES.length + 2);
        } // else

        tr.classId = classId;
        TableDataElement td = null;

        td = new TableDataElement (new BlankElement ());
        td.classId = classId;

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
        text = new TextElement (this.rPersonName);
        nameGroup.addElement (text);

/*******************************************************************************
 * DJ HINT: This is done because of problems with the rights for only one group.
 * MR016, MR017
 ******************************************************************************/
/*
        td = new TableDataElement (new LinkElement (nameGroup,
            IOConstants.URL_JAVASCRIPT +
            "top.showRightsObject ('" + rOid.toString () + "', '" + rPersonId +"');"));
*/
        td = new TableDataElement (nameGroup);
        td.classId = classId;

        tr.addElement (td);

        // column of the  rights of the person displayed
        TableDataElement set = new TableDataElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FULLRIGHTSHORT, env)));
        set.classId = classId;

        TableDataElement notSet = new TableDataElement (new TextElement (UserTokens.TOK_NORIGHTSHORT));
        notSet.classId = classId;

        TableDataElement halfSet = new TableDataElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_HALFRIGHTSHORT, env)));
        halfSet.classId = classId;

        if (this.showExtendedRights)
        {
            for (int i = 0; i < Operations.OP_IDS.length; i++)
            {
                if ((this.rights & Operations.OP_IDS[i]) == Operations.OP_IDS[i])
                {
                    tr.addElement (set);
                } // if
                else
                {
                    tr.addElement (notSet);
                } // else
            } // for
        } // if show extended rights
        else
        {
            // right 'READ'
            if ((this.rights & UserConstants.RA_READ) == UserConstants.RA_READ)
            {
                tr.addElement (set);
            } // if
            else if ((this.rights & UserConstants.RA_READ) == 0)
            {
                tr.addElement (notSet);
            } // else if
            else
            {
                tr.addElement (halfSet);
            } // else

            // right 'WRITE'
            if ((this.rights & UserConstants.RA_WRITE) == UserConstants.RA_WRITE)
            {
                tr.addElement (set);
            } // if
            else if ((this.rights & UserConstants.RA_WRITE) == 0)
            {
                tr.addElement (notSet);
            } // else if
            else
            {
                tr.addElement (halfSet);
            } // else

            // right 'ADMIN'
            if ((this.rights & UserConstants.RA_ADMIN) == UserConstants.RA_ADMIN)
            {
                tr.addElement (set);
            } // if
            else if ((this.rights & UserConstants.RA_ADMIN) == 0)
            {
                tr.addElement (notSet);
            } // else if
            else
            {
                tr.addElement (halfSet);
            } // else
        } // show aliases

        return tr;                      // return the constructed row
    } // show


    /**************************************************************************
     * Represent the selectionList to the user. <BR/>
     *
     * @param   classId             class for the styleSheet
     * @param   env         The current environment 
     *
     * @return  The table row which represents this object.
     */
    public RowElement showSelection (String classId, Environment env)
    {
        RowElement tr;
        TableDataElement td = null;
        tr = this.show (classId, env);

        InputElement iE =
            new InputElement (BOArguments.ARG_DELLIST,
                              InputElement.INP_CHECKBOX, "" + this.rPersonId);
        iE.checked = this.checked;

        td = new TableDataElement (iE);

        td.width = BOListConstants.LST_NEWCOLWIDTH;

        // replace the first table column with the new element:
        tr.replaceElement (td, 0);
        return tr;                      // return the constructed row
    } // showSelect


    /**************************************************************************
     * Represent the selectionList in the changeListForm to the user. <BR/>
     *
     * @param   classId             class for the styleSheet
     *
     * @return  The row element with the selection list.
     */
    public RowElement showChangeFormSelection (String classId)
    {
        RowElement tr;
        TableDataElement td = null;
        tr = this.showChangeForm (classId);

        InputElement iE =
            new InputElement (BOArguments.ARG_DELLIST,
                              InputElement.INP_CHECKBOX, "" + this.rPersonId);
        iE.checked = this.checked;

        td = new TableDataElement (iE);

        td.width = BOListConstants.LST_NEWCOLWIDTH;

        // replace the first table column with the new element:
        tr.replaceElement (td, 0);
        return tr;                      // return the constructed row
    } // showChangeFormSelection


    /**************************************************************************
     * Represent this object with inupt fields for the attributes to the user.
     * <BR/>
     *
     * @param   classId classId for stylsheet of this row.
     *
     * @return  The row element with the change form
     */
    public RowElement showChangeForm (String classId)
    {
        TextElement text = null;
        RowElement tr;
        TableDataElement td = null;

        if (this.showExtendedRights)
        {
            tr = new RowElement (Operations.OP_SHORTNAMES.length + 2);
        } // if
        else
        {
            tr = new RowElement (UserTokens.ML_RIGHTALIASES.length + 2);
        } // else

        tr.classId = classId;

        td = new TableDataElement (new BlankElement ());
        td.classId = classId;

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
        text = new TextElement (this.rPersonName);
        nameGroup.addElement (text);

        td = new TableDataElement (nameGroup);
        td.classId = classId;

        tr.addElement (td);

        if (this.showExtendedRights)
        {
            for (int i = 0; i < Operations.OP_IDS.length; i++)
            {
                // TODO RB: Call
                //          MultilingualTextProvider.getText (Operations.TOK_BUNDLE, 
                //              Operations.OP_SHORTNAMES[i], env)));
                //          to get the text in the correct language
                
                // column of the  rights of the person displayed
                InputElement iE = new InputElement (BOArguments.ARG_CHANGELIST +
                    this.rPersonId, InputElement.INP_CHECKBOX,
                    Operations.OP_SHORTNAMES[i]);
                iE.checked = (this.rights & Operations.OP_IDS[i]) == Operations.OP_IDS[i];
                td = new TableDataElement (iE);
                td.classId = classId;
                tr.addElement (td);
            } // for
        } // if show extended rights
        else
        {
            for (int i = 0; i < UserConstants.RIGHTALIASES.length; i++)
            {
                // right 'READ', 'WRITE', 'ADMIN'
                InputElement iE = new InputElement (BOArguments.ARG_CHANGELIST +
                    this.rPersonId, InputElement.INP_CHECKBOX,
                    UserArguments.ARG_RIGHTALIASES[i]);
                iE.checked = (this.rights & UserConstants.RIGHTALIASES[i]) ==
                        UserConstants.RIGHTALIASES[i];
                // check if 'not change rights'-checkbox will be shown
                if (this.rightsString != null)
                {
                    // add javascript to uncheck - 'not change rights'-checkbox
                    iE.onClick =
                        "if(this.checked==true){" + HtmlConstants.JREF_SHEETFORM +
                        BOArguments.ARG_CHANGELIST + this.rPersonId +
                        "[" + UserConstants.RIGHTALIASES.length +
                        "].checked=false};";
                } // if

                td = new TableDataElement (iE);
                td.classId = classId;
                tr.addElement (td);
            } // for i

            // checkbox - DO NOT CHANGE RIGHTS on this rightelement
            // check if element has stored old rights - that means that partiell-rights occured
            if (this.rightsString != null)
            {
                InputElement iE = new InputElement (BOArguments.ARG_CHANGELIST + this.rPersonId, InputElement.INP_CHECKBOX, UserArguments.ARG_NOCHANGE);
                iE.checked = this.noChangeChecked;
                // add javascript to uncheck all rightalias-checkboxes if this checkbox is checked
                iE.onClick = "if(this.checked==true){";
                for (int i = 0; i < UserConstants.RIGHTALIASES.length; i++)
                {
                    iE.onClick += HtmlConstants.JREF_SHEETFORM +
                        BOArguments.ARG_CHANGELIST + this.rPersonId + "[" + i +
                        "].checked=false;";
                } // for i
                iE.onClick += "};";

                GroupElement ge = new GroupElement ();
                ge.addElement (iE);
                ge.addElement (new TextElement (this.rightsString));
                td = new TableDataElement (ge);
            } // if
            else
            {
                td = new TableDataElement (new BlankElement ());
            } // else

            td.classId = classId;
            tr.addElement (td);

        } // show aliases

        return tr;                      // return the constructed row
    } // showListChangeForm

} // class RightsContainerElement_01
