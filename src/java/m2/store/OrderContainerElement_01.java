/*
 * Class: ContainerElement.java
 */

// package:
package m2.store;

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


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * OrderContainer. <BR/>
 *
 * @version     $Id: OrderContainerElement_01.java,v 1.11 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990113
 ******************************************************************************
 */
public class OrderContainerElement_01  extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderContainerElement_01.java,v 1.11 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * Creates a ContainerElement object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public OrderContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
    } // ContainerElement


    /**************************************************************************
     * Creates a ContainerElement object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     */
    public OrderContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // ContainerElement


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     class for the stylesheet.
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

        tr = new RowElement (OrderContainer_01.LST_HEADINGS_ORDERCONTAINER.length + 1);
        tr.classId = classId;

        // set text to be shown on mouse over:
        group = new GroupElement ();    // create new group element
        if (this.isNew)                 // the element is new?
        {
            // show "new" icon:
            group.addElement (new ImageElement (BOPathConstants.PATH_GLOBAL +
                "new.gif"));
        } // if
        else
        {
            // show blank space:
            group.addElement (new BlankElement ());
        } // else

        // compose table data element:
        td = new TableDataElement (group);
        td.width = BOListConstants.LST_NEWCOLWIDTH;

        td.classId = classId;

        tr.addElement (td);

        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.name));

        group = new GroupElement ();
        String oidStr = null;

        if (this.isLink)                // object is a link to another object?
        {
            oidStr = "" + this.linkedObjectId;
        } // if object is a link to another object
        else  // object is no link
        {
            oidStr = "" + this.oid;
        } // else object is no link
        group.addElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (oidStr)));

        td = new TableDataElement (group);

        td.classId = classId;

        tr.addElement (td);

        // state of order

        // Check if the processState is set after initialization 
        if (this.processState == null)
            this.processState = MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, BOTokens.ML_PST_NONE, env);
        
        text = new TextElement (this.processState);
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        // last change
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
        td = new TableDataElement (text);
        td.nowrap = true;

        td.classId = classId;

        tr.addElement (td);
        return tr;                      // return the constructed row
    } // show

} // class OrderContainerElement_01
