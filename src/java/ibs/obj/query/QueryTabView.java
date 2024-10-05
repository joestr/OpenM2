/*
 * Class: QueryTabView.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.di.XMLViewer_01;
import ibs.obj.query.QueryExecutive_01;
import ibs.service.user.User;


/******************************************************************************
 * This class could be used as TabView for an other object. <BR/>
 * It could be used in TABOBJECT definition of an XML-Type. <BR/>
 * definition in XML:
 * <PRE>
 * <TABOBJECT TABCODE="[code]" KIND="VIEW" CLASS="ibs.obj.query.QueryTabView">
 *   <SYSTEM>
 *     <NAME>[name]</NAME>
 *     <DESCRIPTION>[description]</DESCRIPTION>
 *   </SYSTEM>
 *   <VALUES>
 *     <VALUE FIELD="viewQuery" TYPE="TEXT">[queryname]</VALUE>
 *   </VALUES>
 * </TABOBJECT>
 * </PRE>
 *
 * @version     $Id: QueryTabView.java,v 1.6 2009/07/24 10:21:08 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 011115
 ******************************************************************************
 */
public class QueryTabView extends QueryExecutive_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryTabView.java,v 1.6 2009/07/24 10:21:08 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class Note_01. <BR/>
     */
    public QueryTabView ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // QueryTabView


    /**************************************************************************
     * This constructor creates a new instance of the class Note_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QueryTabView (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // QueryTabView


    /**************************************************************************
     * Set the specific properties for this specific tabview. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
//this.debug ("QueryTabView.setSpecificProperties (BusinessObject majorObject)");
        DataElement tabData =
            ((XMLViewer_01) majorObject).getTabData (this.p_tabId);

        // set system values
        this.name = tabData.name;
        this.description = tabData.description;

//this.debug (" tabname = " + tabData.name);
//this.debug (" tabdescription = " + tabData.description);

        // get the view specific values
        // viewQuery
        if (tabData.exists ("viewQuery"))
        {
            this.queryObjectName = tabData.getImportStringValue ("viewQuery");
        } // if

//this.debug (" queryname = " + this.queryObjectName);

        // currentObjectOid
        this.currentObjectOid = majorObject.oid;

        // prepare query creator with current parameters
        this.prepareQueryCreator ();
    } // setSpecificProperties

} // class QueryTabView
