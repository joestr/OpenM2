/*
 * Class: FrameSetElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
//KR TODO: unsauber
import ibs.io.IOConstants;
import ibs.tech.html.BuildException;
import ibs.tech.html.Frame;
import ibs.tech.html.FrameElement;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the FrameSetElement Object, which builds a HTML-String
 * needed for a FrameSet to be displayed
 *
 * @version     $Id: FrameSetElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class FrameSetElement extends Frame
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FrameSetElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Sets the spacing between 2 frames, in pixels.
     */
    public int frameSpacing;

    /**
     * Sets if the frameset consists of rows or cols.
     * true = rows - false = cols
     */
    protected boolean direction;

    /**
     * Sets the dimension of the framerows
     */
    protected String[] rows;

    /**
     * Sets the dimension of the framecols
     */
    protected String[] cols;

    /**
     * Number of frames in the frameset
     */
    protected int frameNumber;

    /**
     * Frames in the FrameSet
     */
    protected Frame[] frames;

    /**
     * actual frame
     */
    protected int actual;


    /**************************************************************************
     * Creates a new instance of a FrameSetElement
     *
     * @param   pRows   Declares if frames are horizontal or vertikal
     * @param   pFrames How many frames are in the frameset?
     */
    public FrameSetElement (boolean pRows, int pFrames)
    {
        this.frames = null;
        this.direction = pRows;
        this.frameNumber = pFrames;
        if (this.direction)
        {
            // Rows
            this.rows = new String[pFrames];
            this.cols = null;
        } // if
        else
        {
            // Cols
            this.cols = new String[pFrames];
            this.rows = null;
        } // else
        this.frameborder = false;
        this.frameSpacing = 0;
        this.frames = new Frame[pFrames];
        this.actual = 0;
    } // FrameSetElement


    /**************************************************************************
     * Adds a Frame.
     *
     * @param   frame   Frame to add (or frameset)
     * @param   size    Size of the frame
     */
    public void addElement (Frame frame, String size)
    {
        if (this.actual > this.frames.length - 1)
        {
            return;
        } // if
        this.frames[this.actual] = frame;
        if (this.direction)
        {
            this.rows[this.actual] = size;
        } // if
        else
        {
            this.cols[this.actual] = size;
        } // else
        this.actual++;
    } //addElement


    /**************************************************************************
     * Clears the element.
     */
    public void clear ()
    {
        this.frames = new Frame[this.frameNumber];
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            buf.append (IE302.TAG_FRAMESETBEGIN);
            if (this.direction)
            {
                        // Rows
                buf.append (IE302.TA_FRAMEROWS);
                String temp = "";
                for (int i = 0; i < this.rows.length; i++)
                {
                    if (this.rows[i] != null)
                    {
                        temp += this.rows[i];
                    } // if
                    else
                    {
                        temp += "0";
                    } // else

                    if (i != this.rows.length - 1)
                    {
                        temp += ",";
                    } // if
                } // for i
                buf.append (this.inBrackets (temp));
            } // if
            else
            {
                        // Cols
                buf.append (IE302.TA_FRAMECOLS);
                String temp = "";
                for (int i = 0; i < this.cols.length; i++)
                {
                    if (this.cols[i] != null)
                    {
                        temp += this.cols[i];
                    } // if
                    else
                    {
                        temp += "0";
                    } // else

                    if (i != this.cols.length - 1)
                    {
                        temp += ",";
                    } // if
                } // for i
                buf.append (this.inBrackets (temp));
            } // else
            if (env.getBrowser ().equalsIgnoreCase (IOConstants.NS4))
            {
                if (this.frameborder)
                {
                    buf.append (IE302.TA_BORDER + this.inBrackets ("1"));
                } // if
                else
                {
                    buf.append (IE302.TA_BORDER + this.inBrackets ("0"));
                } // else
            } // if
            else
            {
                if (this.frameborder)
                {
                    buf.append (IE302.TA_FRAMEBORDER + this.inBrackets ("1"));
                } // if
                else
                {
                    buf.append (IE302.TA_FRAMEBORDER + this.inBrackets ("0"));
                } // else
            } // else

            buf.append (IE302.TA_FRAMESPACING +
                this.inBrackets ("" + this.frameSpacing));
            buf.append (IE302.TO_TAGEND + "\n");

            for (int i = 0; i < this.frameNumber; i++)
            {
                if (this.frames[i] != null)
                {
                    this.frames[i].build (env, buf);
                } // if
                else
                {
                    FrameElement dummy = new FrameElement ("");
                    dummy.build (env, buf);
                } // else
            } // for i
            buf.append (IE302.TAG_FRAMESETEND + "\n");
        } // if browser is supported
    } // build

} // class FrameSetElement
