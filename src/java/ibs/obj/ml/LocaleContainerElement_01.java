/*
 * Class: LocaleContainerElement_01.java
 */

// package:
package ibs.obj.ml;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * LocaleContainer. <BR/>
 *
 * @version     $Id: LocaleContainerElement_01.java,v 1.2 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Bernhard Tatzmann 20100322
 ******************************************************************************
 */
public class LocaleContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LocaleContainerElement_01.java,v 1.2 2010/04/07 13:37:16 rburgermann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Elements of the Locale. <BR/>
     */
    public LayoutElement[] elems = new LayoutElement [LayoutConstants.STYLECOUNT];

    /**
     * The language of the locale <BR/>
     */
    protected String p_language = "";

    /**
     * The country of the locale <BR/>
     */
    protected String p_country = "";
    
    /**
     * Is this the default layout? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isDefault = false;

    /**
     * Number of specific attributes. <BR/>
     */
    private static final int SPECIFIC_ATTRIBUTES = 1;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Creates a LocaleContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public LocaleContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
//        elems = new LocaleElement[4];
    } // LocaleContainerElement_01


    /**************************************************************************
     * Creates a LocaleContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public LocaleContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // set the instance's public properties:
//        elems = new LocaleElement[4];

    } // LocaleContainerElement_01


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
        GroupElement nameGroup = null;  // the name group
        RowElement tr;                  // table row
        TableDataElement td = null;     // table cell

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (
                BOListConstants.LST_HEADINGS_LOCALECONTAINER.length + 1);
        } // if
        else
        {
            tr = new RowElement (
                BOListConstants.LST_HEADINGS_LOCALECONTAINERREDUCED.length + 1);
        } // else

        tr.classId = classId;

        // display the isNew flag:
        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        // create the name group:
        nameGroup = this.getNameGroup ();
        td = new TableDataElement (nameGroup);
        td.classId = classId;
        tr.addElement (td);

        if (this.p_isDefault)
        {
            ImageElement img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Master.gif");
            nameGroup.addElement (img);
        } // if

        // column for language
        td = new TableDataElement (new TextElement (this.p_language));
        td.classId = classId;

        tr.addElement (td);

        // column for country
        td = new TableDataElement (new TextElement (this.p_country));
        td.classId = classId;

        tr.addElement (td);

        // column for is default
        td = new TableDataElement (new TextElement ("" + this.p_isDefault));
        td.classId = classId;

        tr.addElement (td);
        
        if (this.showExtendedAttributes)
            // show extended attributes
        {
            // no additional fields
        } // if show extended attributes

        return tr;                      // return the constructed row
    } // show

} // class LocaleContainerElement_01
