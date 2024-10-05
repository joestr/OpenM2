/*
 * Class: QueryAttributeMapper.java
 */

// package:
package ibs.obj.query;

// imports:
import java.util.Map;

import ibs.BaseObject;


/******************************************************************************
 * attributeMapper - dataclass for mapping between columnheaders and
 * queryattributes. <BR/>
 *
 * @version     $Id: QueryAttributeMapper.java,v 1.8 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 ******************************************************************************
 */
public class QueryAttributeMapper extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryAttributeMapper.java,v 1.8 2010/04/15 15:31:13 rburgermann Exp $";

    /**
     * Contains the original definition name from the query definition
     */
    public String defName = null;

    /**
     * Contains all texts in all locales for this single value
     */
    public Map<String, String> guiName = null;

    /**
     * Contains all descriptions in all locales for this single value
     */
    public Map<String, String> guiDescription = null;
    
    /**
     *
     */
    public String queryAttribute = null;

    /**
     *
     */
    public String queryAttributeType = null;

    /**
    *
    */
    public String queryAttributeTypeModifier = null;

    /******************************************************************************
     * This constructor creates a new instance of the class QueryAttributeMapper.
     * <BR/>
     */
    public QueryAttributeMapper ()
    {
        // nothing to do
    } // QueryAttributeMapper

    
    /******************************************************************************
     * Returns the definition name
     * 
     * @return  The definition name
     */
    public String getDefName()
    {
        return defName;
    } // getDefName

    
    /******************************************************************************
     * Returns the guiName for the given locale key
     * 
     * @param   key     The locale key to get the correct guiName for the user
     * 
     * @return  The guiName for the given locale
     */
    public String getMlGuiName(String key)
    {
        return guiName.get (key);
    } // getMlGuiName

    
    /******************************************************************************
     * Returns the guiDescription for the given locale key
     * 
     * @param   key     The locale key to get the correct guiDescription for the user
     * 
     * @return  The guiDescription for the given locale
     */
    public String getMlGuiDescription(String key)
    {
        return guiDescription.get (key);
    } // getMlGuiDescription
    
} // queryAttributeMapper
