/*
 * Interface: DiscussionEntry.java
 */

// package:
package m2.bbd;

// imports:


/******************************************************************************
 * This interface defines common properties and methods for several different
 * discussion entries. <BR/>
 *
 * @version     $Id: DiscussionEntry.java,v 1.3 2007/07/23 08:21:29 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR)  001110
 ******************************************************************************
 */
public interface DiscussionEntry
{
    /**************************************************************************
     * Show the quickview of the object
     *
     * @param   representationForm  Kind of representation.
     */
    public void quickView (int representationForm);

} // interface DiscussionEntry
