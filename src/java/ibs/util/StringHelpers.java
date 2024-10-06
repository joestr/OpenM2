/*
 * Class: StringHelpers.java
 */

// package:
package ibs.util;

// imports:
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64.Encoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;



/******************************************************************************
 * This class contains some helper methods for String manipulation. <BR/>
 *
 * @version     $Id: StringHelpers.java,v 1.5 2013/01/17 12:08:50 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 20070724
 ******************************************************************************
 */
public abstract class StringHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StringHelpers.java,v 1.5 2013/01/17 12:08:50 rburgermann Exp $";


    /**
     * trace
     */
    public static String trace = "";

    /**
     * Represents all known characters to be escaped. <BR/>
     */
    private static String[] p_charsToBeEscaped =
    {
        "\\", "\"", "'",
    };


    /**************************************************************************
     * Replace all occurrences of one String within another one. <BR/>
     * The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *
     * @deprecated Instead of this method use the standard JAVA method 
     *             {@link java.lang.String.replace()}. <BR/>
     *             This method will be removed in a future version of openM2!
     */
    public static String replace (String majorStr, String oldStr, String newStr)
    {
        if (majorStr == null || oldStr == null || newStr == null)
        {
            return majorStr;
        } // if

        return majorStr.replace (oldStr, newStr);
    } // replace


    /**************************************************************************
     * Replace all occurrences of string surrounded by a String pair within
     * another string. <BR/>
     * All parts of the original string starting with <CODE>oldBeginStr</CODE>
     * and ending with <CODE>oldEndStr</CODE> are replaced with the replacement
     * string. <CODE>oldBeginStr</CODE> and <CODE>oldEndStr</CODE> are part of
     * the replaced string. <BR/>
     * A new String object is created. The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldBeginStr Begin of string to be replaced.
     * @param   oldEndStr   End of string to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     */
    public static String replace (String majorStr, String oldBeginStr,
                                  String oldEndStr, String newStr)
    {
        int oldPos = 0;                 // actual position in old major string
        int pos = 0;                    // actual found position
        int pos2 = 0;                   // position of ending string
        int beginLength = oldBeginStr.length ();
        int endLength = oldEndStr.length ();
        StringBuilder str = new StringBuilder("");                // new String

        if (majorStr == null || oldBeginStr == null || newStr == null)
        {
            return majorStr;
        } // if

        // check if there is an end string set:
        if (oldEndStr == null || oldEndStr.length () == 0)
        {
            // call standard replacement function:
            return StringHelpers.replace (majorStr, oldBeginStr, newStr);
        } // if

        // loop through all found occurrences of the replacement string:
        while ((pos = majorStr.indexOf (oldBeginStr, oldPos)) >= 0)
        {
            // check if the ending string is found:
            if ((pos2 = majorStr.indexOf (oldEndStr, pos + beginLength)) >= 0)
            {
                // replace the string part:
                str.append (majorStr.substring (oldPos, pos) + newStr);
                oldPos = pos2 + endLength;
            } // if
        } // while

        // concatenate the rest of the major string not used yet:
        str.append (majorStr.substring (oldPos));

        return str.toString ();                     // return the new string
    } // replace


    /**************************************************************************
     * Replace all occurrences of one String pair within another string. <BR/>
     * All occurrencies of the string pair <CODE>oldBeginStr</CODE> and
     * <CODE>oldEndStr</CODE> are replaced with the replacement strings. The
     * String part between is left unchanged. <BR/>
     * A new String object is created. The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldBeginStr Begin of string to be replaced.
     * @param   oldEndStr   End of string to be replaced.
     * @param   newBeginStr String which shall replace the oldBeginStr.
     * @param   newEndStr   String which shall replace the oldEndStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     */
    public static String replace (String majorStr,
                                  String oldBeginStr,
                                  String oldEndStr,
                                  String newBeginStr,
                                  String newEndStr)
    {
        int oldPos = 0;                 // actual position in old major string
        int pos = 0;                    // actual found position
        int pos2 = 0;                   // position of ending string
        int beginLength = oldBeginStr.length ();
        int endLength = oldEndStr.length ();
        StringBuilder str = new StringBuilder("");                // new String

        if (majorStr == null || oldBeginStr == null || newBeginStr == null)
        {
            return majorStr;
        } // if

        // check if there is an end string set:
        if (oldEndStr == null || oldEndStr.length () == 0 || newEndStr == null)
        {
            // call standard replacement function:
            return StringHelpers.replace (majorStr, oldBeginStr, newBeginStr);
        } // if

        // loop through all found occurrences of the replacement string:
        while ((pos = majorStr.indexOf (oldBeginStr, oldPos)) >= 0)
        {
            // check if the ending string is found:
            if ((pos2 = majorStr.indexOf (oldEndStr, pos + beginLength)) >= 0)
            {
                // replace the string part:
                str.append (majorStr.substring (oldPos, pos) + newBeginStr +
                            majorStr.substring (pos + beginLength, pos2) + newEndStr);
                oldPos = pos2 + endLength;
            } // if
        } // while

        // concatenate the rest of the major string not used yet:
        str.append (majorStr.substring (oldPos));

        return str.toString ();                     // return the new string
    } // replace


    /**************************************************************************
     * Replace all occurrences of one String within another one. <BR/>
     * The original string is not changed.
     *
     * @param   majorStr    Major string containing the tags to be replaced.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr.
     */
    public static StringBuffer replace (StringBuffer majorStr, String oldStr,
                                        String newStr)
    {
        int oldPos = 0;                 // actual position in old major string
        int pos = 0;                    // actual found position
        int oldLength = oldStr.length (); // length of replaced string
        String localString = majorStr.toString ();
        StringBuffer retValue = new StringBuffer ();

        while ((pos = localString.indexOf (oldStr, oldPos)) >= 0)
        {
            retValue.append (localString.substring (oldPos, pos))
                .append (newStr);
            oldPos = pos + oldLength;
        } // while

        // append the rest of the string:
        retValue.append (localString.substring (oldPos));
/*
// KR HACK: This part is not used because of backwards compatibility to
// Java 1.3 which does not support the StringBuffer.indexOf () method.
        while ((pos = retValue.indexOf (oldStr, oldPos)) >= 0)
        {
            retValue.replace (pos, pos + oldLength - 1, newStr);
            oldPos = pos + newLength;
        } // while
*/

        return retValue;                // return the new string
    } // replace


    /**************************************************************************
     * Substitute characters within in a string. <BR/>
     * This method replaces all occurrences of characters of the first array
     * within the string with the corresponding characters of the second array.
     * <BR/>
     * So both arrays must have the same size.
     *
     * @param   str         The string to replace the characters in.
     * @param   oldChars    The characters to be replaced.
     * @param   newChars    The replacement characters.
     *
     * @return  The string with substituted characters.
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static final String replaceChars (String str,
                                             char[] oldChars,
                                             char[] newChars)
    {
        // check parameters for correctness:
        if (str == null || oldChars == null || newChars == null ||
            oldChars.length != newChars.length)
        {
            // indicate error:
            return null;
        } // if

        // check if there is only one character to be replaced:
        if (oldChars.length == 1)
        {
            // use java standard method:
            return str.replace (oldChars[0], newChars[0]);
        } // if

        // get all characters of the original string:
        char[] chars = str.toCharArray ();

        // loop through all characters:
        for (int i = 0; i < chars.length; i++)
        {
            // check if the current character shall be replaced:
            for (int j = 0; j < oldChars.length; j++)
            {
                if (chars[i] == oldChars[j])
                {
                    // replace the character:
                    chars[i] = newChars[j];
                    break;
                } // if
            } // for j
        } // for i

        // return the result:
        return new String (chars);
    } // replaceChars


    /**************************************************************************
     * Replace all occurrences of strings within in a string. <BR/>
     * This method replaces all occurrences of strings of the first array
     * within the string with the corresponding strings of the second array.
     * <BR/>
     * So both arrays must have the same size. <BR/>
     *
     * @param   majorStr    Major string containing the strings to be replaced.
     * @param   oldStr      Strings to be replaced.
     * @param   newStr      Strings which shall replace the oldStr.
     *
     * @return  String in which all occurrences of oldStr are replaced by
     *          newStr. In case any of the parameters is <CODE>null</CODE>
     *          the majorStr will be returned.
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static String replaceStrings (String majorStr,
                                         String[] oldStr,
                                         String[] newStr)
    {
        // check parameters for correctness:
        if (majorStr == null || oldStr == null || newStr == null ||
            oldStr.length != newStr.length)
        {
            // indicate error:
            return null;
        } // if

        if (majorStr == null || oldStr == null || newStr == null)
        {
            return majorStr;
        } // if

        // loop through the string arrays:
        for (int i = 0; i < oldStr.length; i++)
        {
            // check if both strings are set:
            if (oldStr[i] != null && newStr[i] != null)
            {
                majorStr.replace (oldStr[i], newStr[i]);
            } // if
        } // for i

        return majorStr;                     // return the new string
    } // replace


    /**************************************************************************
     * Changes the delimiter in a property string. <BR/>
     *
     * @param   input       The input property string.
     * @param   oldDelim    The old delimiter.
     * @param   newDelim    The new delimiter.
     *
     * @return  the converted property
     */
    public static String changeDelimiter (String input, String oldDelim,
                                          String newDelim)
    {
        String str = null;

        if (input != null)
        {
            StringTokenizer values = new StringTokenizer (input, oldDelim);
            try
            {
                str = values.nextToken ();
                while (values.hasMoreElements ())
                {
                    str = str + newDelim + values.nextToken ();
                } // while
            } // try
            catch (NoSuchElementException e)
            {
                str = input;
            } // catch
        } // if
        return str;
    } // changeDelimiter


    /**************************************************************************
     * Returns a string buffer consisting of the concatenated Strings of an
     * iteration, separated by a specific separator. <BR/>
     *
     * @param   iter        Iterator which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the iterator,
     *          separated by the separator.
     *          <CODE>null</CODE> if the iterator was <CODE>null</CODE>.
     */
    public static StringBuffer stringIteratorToStringBuffer (
                                                             Iterator<String> iter,
                                                             StringBuffer separator)
    {
        // check if the iterator was not null:
        if (iter == null)
        {
            return null;
        } // if

        StringBuffer retStringBuffer = new StringBuffer ();

        // check if a separator has to be used:
        if (separator == null || separator.length () == 0)
        {
            while (iter.hasNext ())
            {
                retStringBuffer.append (iter.next ());
            } // while
        } // if
        else
        {
            StringBuffer sep = new StringBuffer ();

            while (iter.hasNext ())
            {
                retStringBuffer.append (sep).append (iter.next ());
                sep = separator;
            } // while
        } // else

        // return the result:
        return retStringBuffer;
    } // stringIteratorToStringBuffer


    /**************************************************************************
     * Returns a string buffer consisting of the concatenated Strings of an
     * iteration, separated by a specific separator. <BR/>
     *
     * @param   iter        Iterator which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the iterator,
     *          separated by the separator.
     *          <CODE>null</CODE> if the iterator was <CODE>null</CODE>.
     */
    public static StringBuffer stringIteratorToStringBuffer (
                                                             Iterator<String> iter,
                                                             String separator)
    {
        // call common method:
        return StringHelpers.stringIteratorToStringBuffer (
            iter, new StringBuffer ().append (separator));
    } // stringIteratorToStringBuffer


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     * @param   bracket   	bracket character to enclose the strings 
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator and enclosed by the bracket
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     */
    public static String stringArrayToString (String[] strArray,
                                              StringBuffer separator,
                                              StringBuffer bracket)
    {
        // CONTRAINT: check if the array was not null:
        if (strArray == null || strArray.length == 0)
        {
            return null;
        } // if (strArray == null)

        // initialize the bracket
        if (bracket == null)
        {
        	bracket = new StringBuffer ();
        } // if (bracket == null)        

        // check if array is only one element=
        if (strArray.length == 1)
        {
            return (new StringBuffer().append (bracket)
    			.append (strArray[0])
    			.append (bracket)).toString();
        } // if (strArray.length == 1)        
        
        // initialize the separator
        if (separator == null)
        {
        	separator = new StringBuffer ();
        } // if (separator == null)        
        
        StringBuffer retStringBuffer = new StringBuffer ();
        StringBuffer sep = new StringBuffer ();
        
        for (int i = 0; i < strArray.length; i++)
        {
        	retStringBuffer.append (sep)
        		.append (bracket)
        		.append (strArray[i])
        		.append (bracket);        		
        	sep = separator;
        } // for i

        // return the result:
        return retStringBuffer.toString ();
    } // stringArrayToString
    
    
    
    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     */
    public static String stringArrayToString (String[] strArray,
                                              StringBuffer separator)
    {
        return StringHelpers.stringArrayToString (
                strArray, separator, null);    	
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     */
    public static String stringArrayToString (String[] strArray,
                                              String separator)
    {
        // call common method:
        return StringHelpers.stringArrayToString (
            strArray, new StringBuffer ().append (separator));
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array, separated
     * by a specific separator. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     * @param   separator   Separator to be set between these strings.
     *
     * @return  A string containing different strings from the string array,
     *          separated by the separator.
     *          <CODE>null</CODE> if the array was <CODE>null</CODE>.
     */
    public static String stringArrayToString (String[] strArray, char separator)
    {
        // call common method:
        return StringHelpers.stringArrayToString (
            strArray, new StringBuffer ().append (separator));
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     */
    public static String stringArrayToString (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, (StringBuffer) null);
    } // stringArrayToString


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     * Uses standard separator: comma (<CODE>", "</CODE>).
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     */
    public static String stringArrayToStringC (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, new StringBuffer (", "));
    } // stringArrayToStringSC


    /**************************************************************************
     * Returns a string consisting of the Strings of a string array. <BR/>
     * Uses standard separator: semicolon (<CODE>"; "</CODE>).
     *
     * @param   strArray    String array which contains the strings to be copied
     *                      into one string.
     *
     * @return  A string containing different strings from the string array
     */
    public static String stringArrayToStringSC (String[] strArray)
    {
        // call common method:
        return StringHelpers.stringArrayToString (strArray, new StringBuffer ("; "));
    } // stringArrayToStringSC


    /**************************************************************************
     * A wrapper method for the stringToStringArray method with the
     * includeEmpty lines deactivated. <BR/>
     *
     * @param str           String which contains different strings separated
     *                      by separator character.
     * @param separator     Separator between the different parts of the string.
     *
     * @return  A string array containing the elements from the string.
     */
    public static String[] stringToStringArray (String str, char separator)
    {
        return StringHelpers.stringToStringArray (str, separator, false);
    } // stringToStringArray


    /**************************************************************************
     * Returns a string containing the Strings of a stringarray, separated by
     * a separator character. <BR/>
     * Note that a separator at the beginning or at the end will be ignored. <BR/>
     * Trailing whitespaces will be trimmed. <BR/>
     * Multiple separators will also be ignored. <BR/>
     *
     * @param   str         String which contains different strings separated
     *                      by separator character.
     * @param   separator   Separator between the different parts of the string.
     * @param   includeEmpty    ??? => BB
     *
     * @return  A string array containing the elements from the string.
     */
    public static String[] stringToStringArray (String str, char separator,
                                                boolean includeEmpty)
    {
        Vector<String> strVec = new Vector<String> ();
/*
        Vector charVec = new Vector ();
        char aktChar;
*/
        int pos;
        int lastpos = 0;
        String entry;

        // first check if the string does contain the separator:
        if (str.indexOf (separator) == -1)
        {
            return new String [] {str};
        } // if (str.indexOf (separator) == -1)

        // go through all characters in string str:
        while ((pos = str.indexOf (separator, lastpos)) != -1)
        {
            //  ...<separator><separator>... will also produce an entry:
            if (pos >= lastpos)
            {
                entry = str.substring (lastpos, pos).trim ();
                // ignore the entry in case the includeEmpty option
                // has been deactivated and the entry is empty
                // if activated an ...<separator><separator> will not produce
                // an entry
                if (includeEmpty || entry.length () > 0)
                {
                    strVec.addElement (entry);
                } // if
            } // if (lastpos != pos -1)
            lastpos = pos + 1;
        } // while ((pos = str.indexOf (separator, lastpos)) != -1)

        // a <separator> at the end will not produce an entry:
        if (lastpos != str.length ())
        {
            entry = str.substring (lastpos).trim ();
            // ignore the entry in case the includeEmpty option
            // has been deactivated and the entry is empty
            // if activated an ...<separator><separator> will not produce
            // an entry
            if (includeEmpty || entry.length () > 0)
            {
                strVec.addElement (entry);
            } // if
        } // if (lastpos != str.length ())

        // convert vector to stringarray
        String [] retStrArray = new String [strVec.size ()];
        strVec.toArray (retStrArray);

        // return the result:
        return retStrArray;
    } // stringToStringArray


    /**************************************************************************
     * Compares two string arrays and creates two vectors. In one vector are the
     * strings wich are only in the first string array, and in the other vector
     * are the strings which are only in the second string array. <BR/>
     *
     * @param   strArray1   First string array to compare.
     * @param   strArray2   Second string array to compare.
     * @param   notInArray1 Vector which contains the strings wich are not in
     *                      strArray1 but in strArray2.
     * @param   notInArray2 Vector which contains the strings wich are not in
     *                      strArray2 but in strArray1.
     */
    public static void compareStringArrays (String[] strArray1,
                                            String[] strArray2,
                                            Vector<String> notInArray1,
                                            Vector<String> notInArray2)
    {
        int length1 = 0;
        int length2 = 0;

        // generate new stringarrays to do not change the parameter - stringarrays
        if (strArray1 != null)
        {
            length1 = strArray1.length;
        } // if

        if (strArray2 != null)
        {
            length2 = strArray2.length;
        } // if

        String[] compArray1 = new String [length1];
        for (int i = 0; i < length1; i++)
        {
            compArray1[i] = new String (strArray1[i].toString ());
        } // for i

        String[] compArray2 = new String [length2];
        for (int i = 0; i < length2; i++)
        {
            compArray2[i] = new String (strArray2[i].toString ());
        } // for i


        // compare the content of the two strings
        for (int i = 0; i < compArray1.length; i++)
        {
            int j;
            // go through all new joined groups
            for (j = 0; j < compArray2.length; j++)
            {
                if (compArray2[j] != null && compArray2[j].equals (compArray1[i]))
                {
                    break;
                } // if
            } // for

            // was String from compArray1 in compArray2
            if (j < compArray2.length)   // was current string from compArray1 in compArray2
            {
                compArray2 [j] = null;   //delete String in compArray2
            } // if
            else  // string is not in compArray2
            {
                notInArray2.addElement (new String (compArray1[i]));
                StringHelpers.trace += "\n oid notInArray2: " + compArray1[i];
            } // else
        } // for

        // Strings from compArray2 wich where not in compArray1
        for (int i = 0; i < compArray2.length; i++)
        {
            if (compArray2[i] != null)
            {
                notInArray1.addElement (new String (compArray2[i]));
                StringHelpers.trace += "\n oid notInArray1: " + compArray2[i];
            } // if
        } // for

        // copy 'notIn' Strings to Parameter - stringarrays
        StringHelpers.trace += "\n notInArray1.size = " + notInArray1.size ();
        StringHelpers.trace += "\n notInArray2.size = " + notInArray2.size ();
    } // compareStringArrays


    /**************************************************************************
     * Search for one string in a string array.
     *
     * @param   searchArray stringArray
     * @param   findString  String to search for.
     *
     * @return  If the findString was found, the function returns the index
     *          where searchArray contains the same string
     *          if not found =>   -1
     */
    public static int findString (String[] searchArray, String findString)
    {
        if (searchArray == null || findString == null)
        {
            return -1;
        } // if

        for (int i = 0; i < searchArray.length; i++)
        {
            if (searchArray[i] != null && searchArray[i].equals (findString))
            {
                return i;
            } // if
        } // for

        return -1;
    } // findString


    /**************************************************************************
     * Returns a stringArray containing the different Objects in the enumeration
     * as Strings
     * <BR/>
     *
     * @param   enumValue   Enumeration to be converted in a String array
     * @param   size        Number of elements in the Enumeration
     *
     * @return  a string containing different strings from the string array, separated
     *          by the separator.
     */
    public static String [] enumerationToStringArray (Enumeration<String> enumValue,
                                                      int size)
    {
        String [] strArray = new String [size];
        int i = 0;

        while (enumValue.hasMoreElements ())
        {
            strArray[i] = (enumValue.nextElement ()).toString ();
            i++;
        } // while

        return strArray;
    } // enumerationToStringArray


    /**************************************************************************
     * Substitute special characters in a string. <BR/>
     * These are: ' ', Ä, Ü, Ö, ö, ä, ü, ß. <BR/>
     * Note that this method is used for file names. <BR/>
     *
     * @param   str     The string to replace the characters in.
     *
     * @return  The string with substituted special characters.
     */
    public static final String substituteSpecialCharacters (String str)
    {
        String strLocal = str;          // variable for local assignments
//        strLocal = strLocal.toLowerCase ();
        strLocal = strLocal.replace (' ', '_');
        strLocal = strLocal.replace ('ä', 'a');
        strLocal = strLocal.replace ('ö', 'o');
        strLocal = strLocal.replace ('ü', 'u');
        strLocal = strLocal.replace ('Ä', 'A');
        strLocal = strLocal.replace ('Ö', 'O');
        strLocal = strLocal.replace ('Ü', 'U');
        strLocal = strLocal.replace ('ß', 's');

        return strLocal;
    } // substituteSpecialCharacters


    /**************************************************************************
     * Compute a cyphered hash of a given input. <BR/>
     *
     * @param   str     The string to cypher.
     *
     * @return  The resulting hash.
     */
    public static final String computeHash (String str)
    {
        String alg = "SHA-1";
        String base64cipher = null;

        try
        {
            byte[] messageBytes = str.getBytes ("UTF8");

            // get a MessageDigest Object with the Algorithm alg
            // MD5 (Output 128 bit) and SHA-1 (Output 160 bit)
            // are provided without integrating an extra provider
            MessageDigest md = MessageDigest.getInstance (alg);

            // calculate hash-value hash of the byte-value of the message
            md.update (messageBytes);
            byte[] hash = md.digest ();

            // Obtain a BASE64 output. Output is an integral of 24 bit
            Encoder encoder = java.util.Base64.getMimeEncoder();
            base64cipher = encoder.encodeToString (hash);
        } // try
        catch (UnsupportedEncodingException e)
        {
            // TODO currently no handling
        } // catch
        catch (NoSuchAlgorithmException e2)
        {
            // TODO currently no handling
        } // catch

        // base64cipher contains now the hash-value of the
        // given input:
        return base64cipher;
    } // computeHash


    /**************************************************************************
     * Computes a random number and returns the string-representation. Used
     * to add to the URLs to prevent caching. Cannot be used as a random number
     * used for security purposes. <BR/>
     *
     * @return  The string representing the random number.
     */
    public static String computeRandomString ()
    {
        String value = "";
        double dValue = Math.random () * 26; // the random number
        value += (char) ((dValue % 26) + 'a'); // the first character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character
        return value;
    } // computeRandomString


    /**************************************************************************
     * Escapes all occurences in the given string. <BR/>
     * All characters which are escaped are declared in the private property
     * p_charsToBeEscaped.
     *
     * @param   toEscape        String which should be escaped if necessary.
     *
     * @return  The escaped string.
     */
    public static String escape (String toEscape)
    {
        String tmp = toEscape;

        for (int i = 0; i < StringHelpers.p_charsToBeEscaped.length; i++)
        {
            tmp = StringHelpers.replace (tmp, StringHelpers.p_charsToBeEscaped[i],
                                   "\\" + StringHelpers.p_charsToBeEscaped[i]);
        } // for

        return tmp;
    } // escape

} // class StringHelpers
