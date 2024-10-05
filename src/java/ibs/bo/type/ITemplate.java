/*
 * Class: ITemplate.java
 */

// package:
package ibs.bo.type;

//imports:
import java.util.Collection;
import java.util.Vector;

import ibs.bo.OID;
import ibs.di.ValueDataElement;
import ibs.io.Environment;
import ibs.obj.ml.Locale_01;


/******************************************************************************
 * This class defines a common type template definition. <BR/>
 * Specific type templates must implement this interface.
 *
 * @version     $Id: ITemplate.java,v 1.6 2013/01/15 14:48:29 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 22.07.2009
 ******************************************************************************
 */
public interface ITemplate
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ITemplate.java,v 1.6 2013/01/15 14:48:29 rburgermann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Check if the template was already loaded. <BR/>
     *
     * @return  <CODE>true</CODE> if the template was already loaded,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isLoaded ();


    /**************************************************************************
     * Check if the template is loadable, i.e. if all resources which are
     * necessary for loading the template are known and existing. <BR/>
     * These resources can be other objects, files, ftp servers, connections
     * to other applications, etc.
     *
     * @return  <CODE>true</CODE> if the template is loadable,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isLoadable ();


    /***************************************************************************
     * Get the buttons to be displayed in info view. <BR/>
     *
     * @return  The buttons.
     */
    public String getInfoButtons ();


    /***************************************************************************
     * Get the buttons to be displayed in content view. <BR/>
     *
     * @return  The buttons
     */
    public String getContentButtons ();


    /***************************************************************************
     * Get the transformation for instances of the template. <BR/>
     *
     * @return  The transformation information.
     *          <CODE>null</CODE> means that the value was not set.
     */
    public String getTransformation ();


    /***************************************************************************
     * Get the rule for creating the value of the object name. <BR/>
     *
     * @return  The rule.
     *          <CODE>null</CODE> means that no rule was defined.
     */
    public String getNameTemplate ();


    /***************************************************************************
     * Get the oid of the template. <BR/>
     *
     * @return  The oid.
     *          <CODE>null</CODE> if no oid was defined.
     */
    public OID getOid ();
    
    
    /***************************************************************************
     * Initializes the multilang template info for all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangInfo (Collection<Locale_01> locales, Environment env);
    
    
    /***************************************************************************
     * Initializes the multilang texts for the template's references for all
     * provided locales. <BR/>
     * 
     * This method has to be executed after initMultilangInfo ().
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The environment
     */
    public void initMultilangReferenceInfo (Collection<Locale_01> locales, Environment env);

    
    /***************************************************************************
     * Returns possible link fields of this template. <BR/> 
     * 
     * @return Returns possible link fields. If nothing is set <CODE>null</CODE>
     *         is returnd. <BR/>
     */
    public Vector<ValueDataElement> getLinkFields ();

    
    /***************************************************************************
     * Returns a string with a SQL query to retrieve the data of the link fields. <BR/> 
     * 
     * @return Returns a SQL String to retrieve the data of link fields. <BR/>
     */
    public String getLinkFieldQuery();
    
} // interface ITemplate