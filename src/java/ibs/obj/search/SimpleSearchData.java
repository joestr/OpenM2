/*
 * Class: SimpleSearchData.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;


/******************************************************************************
 * This class represents all necessary properties for performing a simple
 * search. <BR/>
 * This class is only a Dataclass and has no methods. <BR/>
 *
 * @version     $Id: SimpleSearchData.java,v 1.8 2009/07/24 08:26:44 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ), 010215
 ******************************************************************************
 */
public class SimpleSearchData extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SimpleSearchData.java,v 1.8 2009/07/24 08:26:44 kreimueller Exp $";


    /**
     * contains the string which had to be found on the database. <BR/>
     */
    private StringBuffer p_searchValue;

    /**
     * the oid of the container where the search should start if search is not
     * done globally (i.e. within the container). <BR/>
     */
    private OID p_searchRootContainerId;

    /**
     * defines where the search should be performed. <BR/>
     * <CODE>true</CODE>: the search has to be performed in whole m2. <BR/>
     * <CODE>false</CODE>: the search has to be performed only in a container
     * and its contents. <BR/>
     */
    private boolean p_searchGlobal;


    /**************************************************************************
     * Creates a SimpleSearchData object. <BR/>
     */
    public SimpleSearchData ()
    {
        // nothing to do
    } // SimpleSearchData


    /**************************************************************************
     * Set the searchValue of this class. <BR/>
     *
     * @param   searchValue The string which has to be found on the database.
     */
    public void setSearchValue (StringBuffer searchValue)
    {
        this.p_searchValue = searchValue;
    } // setSearchValue


    /**************************************************************************
     * Set the OID of the actual container in which the search should perform.
     * <BR/>
     *
     * @param   actualContainer The oid of the actual container.
     */
    public void setSearchRootContainerId (OID actualContainer)
    {
        this.p_searchRootContainerId = actualContainer;
    } // setSearchRootContainerId


    /**************************************************************************
     * Set a flag if the search should be performed in the actual container or
     * globally. <BR/>
     * <CODE>true</CODE> if the search should be performed in the whole system.
     * <BR/>
     * <CODE>false</CODE> if the search should be performed in the container,
     * whose OID is set via
     * {@link #setSearchRootContainerId setSearchRootContainerId}. <BR/>
     *
     * @param   searchGlobal    If this is <CODE>false</CODE> the search is
     *                          performed in the container, whose OID is set via
     *                          {@link #setSearchRootContainerId
     *                          setSearchRootContainerId}.
     */
    public void setSearchGlobal (boolean searchGlobal)
    {
        this.p_searchGlobal = searchGlobal;
    } // setSearchGlobal


    /**************************************************************************
     * Get the searchValue of this class. <BR/>
     *
     * @return  The searchValue which has to be found in the database.
     */
    public StringBuffer getSearchValue ()
    {
        return this.p_searchValue;
    } // getSearchValue


    /**************************************************************************
     * Get the OID of the actual container in which the search should be
     * performed if the search is not global.
     *
     * @return  The OID of the actual container.
     *
     * @see #setSearchGlobal
     */
    public OID getSearchRootContainerId ()
    {
        return this.p_searchRootContainerId;
    } // getSearchRootContainerId


    /**************************************************************************
     * Check if the search shall be performed globally. <BR/>
     *
     * @return  <CODE>true</CODE> if the search should be performed in
     *          the whole system. <BR/>
     *          <CODE>false</CODE> if the search should be performed in the
     *          container, which OID is set via
     *          {@link #setSearchRootContainerId setSearchRootContainerId}.
     */
    public boolean getSearchGlobal ()
    {
        return this.p_searchGlobal;
    } // getSearchGlobal

} // class SimpleSearchData
