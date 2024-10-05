/*
 * Class: MultilingualTextInfo
 */

// package:
package ibs.ml;

import ibs.obj.ml.Locale_01;

// imports:


/******************************************************************************
 * This provides support for handling multilingual texts. <BR/>
 *
 * @version     $Id: MultilingualTextInfo.java,v 1.5 2010/04/22 11:34:57 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT)
 ******************************************************************************
 */
public class MultilingualTextInfo
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultilingualTextInfo.java,v 1.5 2010/04/22 11:34:57 btatzmann Exp $";
    
    /**
     * Holds the state info, wether the multilingual text has been found or not.
     */
    private int state = STATE_FOUND; 

    /**
     * Contains the locale
     */
    private Locale_01 locale;

    /**
     * Contains the name of the bundle
     */
    private String bundle;

    /**
     * Contains the key of this multilingual text
     */
    private String mlKey;

    /**
     * Contains the value of this multilingual text
     */
    private String mlValue;
    
    /**
     * State: found.
     */
    public static final int STATE_FOUND = 1;
    
    /**
     * State: not found.
     */
    public static final int STATE_NOT_FOUND = -1;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////
    
    /**************************************************************************
     * Creates a new MultilingualTextInfo.
     */
    public MultilingualTextInfo ()
    {
        setMLValue (mlValue);
    } // MultilingualTextInfo
    
    
    /**************************************************************************
     * Creates a new MultilingualTextInfo.
     *
     * @param mlValue  The multilingual text or message
     */
    public MultilingualTextInfo (String mlValue)
    {
        setMLValue (mlValue);
    } // MultilingualTextInfo
    
    
    /**************************************************************************
     * Creates a new MultilingualTextInfo.
     *
     * @param state             The state
     * @param mlValue           The multilingual text or message
     */
    public MultilingualTextInfo (int state, String mlValue)
    {
        setState (state);
        setMLValue (mlValue);
    } // MultilingualTextInfo
    

    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////
    
    /**************************************************************************
     * Returns the state.
     *
     * @return the state
     */
    public int getState ()
    {
        return state;
    } // getState

    
    /**************************************************************************
     * Returns if the text has been found.
     *
     * @return is found
     */
    public boolean isFound ()
    {
        return MultilingualTextInfo.STATE_FOUND == this.getState();
    } // isFound

    
    /**
     * Sets the state.
     *
     * @param state State to set
     */
    void setState (int state)
    {
        this.state = state;
    } // setState

    
    /**************************************************************************
     * Set the locale for this MultilingualText
     *
     * @param   locale    The locale which should be set for this MultilingualText
     */
    public void setLocale (Locale_01 locale) 
    {
        this.locale = locale;
    } // setLocale

    
    /**************************************************************************
     * Get the locale for this MultilingualText
     *
     * @return    The locale for this MultilingualText
     */
    public Locale_01 getLocale ()
    {
        return locale;
    } // getLocale

    
    /**************************************************************************
     * Set the bundle for this MultilingualText
     *
     * @param   bundle   The bundle name which should be set for this MultilingualText
     */
    public void setBundle (String bundle) 
    {
        this.bundle = bundle;
    }  // setBundle

    
    /**************************************************************************
     * Get the bundle for this MultilingualText
     *
     * @return  The bundle for this MultilingualText 
     */
    public String getBundle()
    {
        return bundle;
    }  // getBundle
    
    
    /**************************************************************************
     * Set the key for this MultilingualText
     *
     * @param   key        The key which should be set for this MultilingualText
     */
    public void setMLKey (String mlKey) 
    {
        this.mlKey = mlKey;
    }  // setMLKey

    
    /**************************************************************************
     * Get the key for this MultilingualText
     *
     * @return    The key for this MultilingualText 
     */
    public String getMLKey()
    {
        return mlKey;
    }  // getMLKey


    /**************************************************************************
     * Set the value/text for this MultilingualText
     *
     * @param   value    The value/text which should be set for this MultilingualText
     */
    public void setMLValue (String mlValue) 
    {
        this.mlValue = mlValue;
    } // setMLValue


    /**************************************************************************
     * Get the value/text for this MultilingualText
     *
     * @return    The value/text for this MultilingualText 
     */
    public String getMLValue () 
    {
        return mlValue;
    } // getMLValue
    
    
    /***************************************************************************
     * Returns the string representation of this object. <BR/>
     * The bundle, mlkey and mlvalue are concatenated to create a string
     * representation according to "bundle: mlKey = mlValue".
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return this.getBundle () + ": " + this.getMLKey () + " = " + this.getMLValue ();
    } // toString
} // MultilangTextInfo
