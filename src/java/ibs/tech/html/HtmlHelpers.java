/*
 * Class: HtmlHelpers.java
 */

// package:

package ibs.tech.html;

import ibs.di.DIConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

// imports:


/*******************************************************************************
 * This class defines some helper functions for HTML handling. <BR/>
 *
 * @version $Id: HtmlHelpers.java,v 1.2 2009/12/15 09:35:10 btatzmann Exp $
 *
 * @author Klaus Reimüller (KR) 20070705
 *         *****************************************************************************
 */
public abstract class HtmlHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HtmlHelpers.java,v 1.2 2009/12/15 09:35:10 btatzmann Exp $";

    
    /**************************************************************************
     * Encodes the given request parameter with the default character
     * encoding. 
     *
     * @param   param   The request parameter to encode.
     *
     * @return  encoded request parameter.
     */
    public static String encodeRequestParameter (String param)
    {
        try
        {
            param = URLEncoder.encode (param, DIConstants.CHARACTER_ENCODING);
        } // try
        catch (UnsupportedEncodingException e)
        {
            throw new UnsupportedCharacterEncodingException (e.getMessage (), e);
        } // catch
        
        return param;
    } // encodeRequestParameter
} // class HtmlHelpers
