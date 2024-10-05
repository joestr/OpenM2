/*
 * Class: MenuData_01.java
 */

// package:
package ibs.obj.menu;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;


/******************************************************************************
 * This class represents all necessary properties of one element of a tab. <BR/>
 * This class is only a Dataclass and has no methods.
 *
 * @version     $Id: MenuData_01.java,v 1.9 2010/05/17 08:43:58 btatzmann Exp $
 *
 * @author      Daniel Janesch (DJ), 000621
 ******************************************************************************
 */
public class MenuData_01 extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuData_01.java,v 1.9 2010/05/17 08:43:58 btatzmann Exp $";


    /**
     * to set the sequence of the tabs in the left corner
     */
    public int priorityKey;

    /**
     * name of the root object
     */
    public String name;

    /**
     * OID of the object that the root of the menu describes
     */
    public OID oid;

    /**
     * oLevel of root object of menu tree. <BR/>
     */
    public int p_oLevel = 0;

    /**
     * posNoPath of root object of menu tree. <BR/>
     */
    public String p_posNoPath = null;

    /**
     * name of StyleSheet class (or image) which is behind the tab when not active
     */
    public String classFront;

    /**
     * name of StyleSheet class (or image) which is behind the tab when not active
     */
    public String classBack;

    /**
     * name of file, which would be called if the tab is clicked.
     * Contains HTML text and style
     */
    public String filename;

    /**
     * Number of levels to retrieve from the menu bar within one step. <BR/>
     * Default: <CODE>0</CODE> (means: "get all levels at once")
     */
    public  int p_levelStep = 0;

    /**
     * Maximum level upto which to work with levelStep. Starting from that level
     * we get all elements at once. <BR/>
     * Default: <CODE>0</CODE> (means: "get all levels at once")
     */
    public int p_levelStepMax = 0;

    /**
     * The ext id for the menu tab
     */
    public String p_extId = null;

    /**
     * The ext id domain for the menu tab
     */
    public String p_extIdDomain = null;

    
    /**************************************************************************
     * Creates a MenuData_01 object. <BR/>
     */
    public MenuData_01 ()
    {
        // nothing to do
    } // MenuData_01
    
    
    /***************************************************************************
     * Returns the multilang name for the current object.
     *
     * @param   env     The environment.
     * @return
     */
    public String getMlName (Environment env)
    {
        // Retrieve the multilang name for the menu tab object:
        // This is done dynamically, since the text retrieval
        // has to be done always for the user's current local.
        // Since the number of menu tab elements is generally low
        // no preloading is necessary. If a preloading is implemented
        // in future tough. The names for all locales have to be preloaded within
        // MenuContainer_01.getContainerElementData () and retrieved for
        // the current locale at this position.
        String mlName = MultilingualTextProvider.
            getMultilangObjectName (this.p_extId, this.p_extIdDomain,
                    this.name, env);
        
        return mlName;
    } // getMlName

} // class MenuData_01
