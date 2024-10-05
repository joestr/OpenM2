/*
 * Class: EMail.java
 */

// package:
package ibs.service.email;

// imports:
import ibs.BaseObject;

import java.io.File;
import java.util.Vector;


/******************************************************************************
 * An EMail object. <BR/>
 *
 * @version     $Id: EMail.java,v 1.9 2008/09/17 16:39:19 kreimueller Exp $
 *
 * @author      Monika Eisenkolb (ME), 001127
 ******************************************************************************
 */
public class EMail extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EMail.java,v 1.9 2008/09/17 16:39:19 kreimueller Exp $";


    /**
     * email address of the email sender. <BR/>
     */
    protected String sender = "";

    /**
     * email addresses of the receivers. <BR/>
     */
    protected String receiver = "";

    /**
     * email addresses of the ccreceivers. <BR/>
     */
    protected String ccReceiver = "";

    /**
     * email addresses of the bccreceivers. <BR/>
     */
    protected String bccReceiver = "";

    /**
     * the subject of the email. <BR/>
     */
    protected String subject = "";

    /**
     * the content of the email. <BR/>
     */
    protected String content = "";

    /**
     * the contenttype of the email. <BR/>
     */
    protected String p_contentType = "";

    /**
     * the attachments belonging to the email. <BR/>
     */
    public Vector<File> attachments = null;

    /**
     * the attachments belonging to the email. <BR/>
     */
    public Vector<String []> reportAttachments = null;


    /**************************************************************************
     * This constructor creates a new instance of the class EMail without
     * setting the specific attributes. <BR/>
     */
    public EMail ()
    {
        // nothing to do
    } // EMail


    /**************************************************************************
     * This constructor creates a new instance of the class EMail and
     * sets all required attributes. <BR/>
     *
     * @param   sender        email address of the sender.
     * @param   receiver      email addresses of the receivers.
     * @param   ccReceiver    email addresses of the ccReceivers.
     * @param   bccReceiver   email addresses of the bccReceivers.
     * @param   subject       subject of the email.
     * @param   content       content of the email.
     */
    public EMail (String sender, String receiver, String ccReceiver,
        String bccReceiver, String subject, String content)
    {
        // init specifics of actual class:
        this.setSender (sender);
        this.setReceiver (receiver);
        this.setCCReceiver (ccReceiver);
        this.setBCCReceiver (bccReceiver);
        this.setSubject (subject);
        this.setContent (content);
    } // EMail


    /**************************************************************************
     * The email address of the sender is set. <BR/>
     *
     * @param   value   email address of the sender.
     */
    public void setSender (String value)
    {
        // set the email address of the sender
        this.sender = value;
    } // setSender


    /**************************************************************************
     * Set the email addresses of the receivers. <BR/>
     *
     * @param   value        email addresses of the receivers.
     */
    public void setReceiver (String value)
    {
        // set the email addresses of the receivers
        this.receiver = value;
    } // setReceiver


    /**************************************************************************
     * Set the email addresses of the ccReceivers. <BR/>
     *
     * @param   value        email addresses of the ccReceivers.
     */
    public void setCCReceiver (String value)
    {
        // set the email addresses of the ccReceivers
        this.ccReceiver = value;
    } // setCCReceiver


    /**************************************************************************
     * Set the email addresses of the bccReceivers. <BR/>
     *
     * @param   value        email addresses of the bccReceivers.
     */
    public void setBCCReceiver (String value)
    {
        // set the email addresses of the bccReceivers
        this.bccReceiver = value;
    } // setBCCReceiver


    /**************************************************************************
     * Set the subject of the email. <BR/>
     *
     * @param   value   subject of the email.
     */
    public void setSubject (String value)
    {
        // set the subject of the email
        this.subject = value;
    } // setSubject


    /**************************************************************************
     * Set the content of the email. <BR/>
     *
     * @param   value   content of the email.
     */
    public void setContent (String value)
    {
        // set the content of the email
        this.content = value;
    } // setContent


    /**************************************************************************
     * Set the content type of the email. <BR/>
     *
     * @param   value   content type of the email.
     */
    public void setContentType (String value)
    {
        // set the content of the email
        if (value != null)
        {
            this.p_contentType = value.trim ();
        } // if
        else
        {
            this.p_contentType = "";
        } // if
    } // setContentType


    /**************************************************************************
     * Get the email address of the sender. <BR/>
     *
     * @return  the email address of the sender
     */
    public String getSender ()
    {
        // returns the email address of the sender
        return this.sender;
    } // getSender


    /**************************************************************************
     * Get the email addresses of the receivers. <BR/>
     *
     * @return  the email addresses of the receivers
     */
    public String getReceiver ()
    {
        // returns the email addresses of the receivers
        return this.receiver;
    } // getReceiver


    /**************************************************************************
     * Get the email addresses of the ccReceivers. <BR/>
     *
     * @return  the email addresses of ccReceivers
     */
    public String getCCReceiver ()
    {
        // returns the email addresses of the ccReceivers
        return this.ccReceiver;
    } // getCCReceiver


    /**************************************************************************
     * Get the email addresses of the bccReceivers. <BR/>
     *
     * @return  the email addresses of the bccReceivers
     */
    public String getBCCReceiver ()
    {
        // returns the email addresses of the bccReceivers
        return this.bccReceiver;
    } // getBCCReceiver


    /**************************************************************************
     * Get the subject of the email. <BR/>
     *
     * @return  the subject of the email
     */
    public String getSubject ()
    {
        // returns the subject of the email
        return this.subject;
    } // getSubject


    /**************************************************************************
     * Get the content of the email. <BR/>
     *
     * @return  the content of the email
     */
    public String getContent ()
    {
        // returns the content of the email
        return this.content;
    } // getContent


    /**************************************************************************
     * Get the content type of the email. <BR/>
     *
     * @return  the content type of the email
     */
    public String getContentType ()
    {
        // returns the content of the email
        return this.p_contentType;
    } // getContentType


    /**************************************************************************
     * Add attachments to the email. <BR/>
     *
     * @param   file        an attachment file.
     */
    public void addAttachment (File file)
    {
        if (this.attachments == null)
        {
            this.attachments = new Vector<File> ();
        } // if

        this.attachments.addElement (file);
    } // addAttachment

    /**************************************************************************
     * Add birtAttachments to the email. <BR/>
     *
     * @param   url             The url for reporting.
     * @param   requestString   A string to call the birt template file.
     * @param   filename        A string with holds the filename.
     * @param   outputFormat    A string with holds the output format.
     * @param   mimeType        Mimetype of the file.
     *
     */
    public void addReportAttachment (String url, String requestString,
                                     String filename, String outputFormat,
                                     String mimeType)
    {
        int num = 5;

        String [] fileInfo = new String[num];

        if (this.reportAttachments == null)
        {
            this.reportAttachments = new Vector<String[]> ();
        } // if

        fileInfo[0] = url;
        fileInfo[1] = requestString;
        fileInfo[2] = filename;
        fileInfo[3] = outputFormat;
        fileInfo[4] = mimeType;

        this.reportAttachments.addElement (fileInfo);
    } // addAttachment
} // class EMail
