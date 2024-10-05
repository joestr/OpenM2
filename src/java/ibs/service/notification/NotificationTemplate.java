/*
 * Class: NotificationTemplate.java
 */

// package:
package ibs.service.notification;

// imports:
import ibs.BaseObject;
import ibs.service.email.EMailConstants;

import java.io.File;
import java.util.Vector;


/*******************************************************************************
 * In a NotificationTemplate object most of the parameters are saved which
 * are important for a notification . <BR/>
 *
 * @version     $Id: NotificationTemplate.java,v 1.6 2010/11/12 10:20:15 btatzmann Exp $
 *
 * @author      Monika Eisenkolb (ME), 001127
 *******************************************************************************
 */
public class NotificationTemplate extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotificationTemplate.java,v 1.6 2010/11/12 10:20:15 btatzmann Exp $";


    /**
     * subject text for an email or content text for a sms
     * or message text for m2 intern. <BR/>
     */
    protected String subject = "";

    /**
     * content text for an email
     * or message text for m2 intern. <BR/>
     */
    protected String content = "";

    /**
     * content type for an email. <BR/>
     */
    protected int emailContentType = EMailConstants.CONTENT_TYPE_PLAIN_TEXT;

    /**
     * used for m2 intern. <BR/>
     */
    protected String description = "";

    /**
     * used for m2 intern. <BR/>
     */
    protected String activities = "";

    /**
     * saving the attached files. <BR/>
     *
     */
    public Vector<File> attachments = null;



    /**************************************************************************
     * simple constructor. <BR/>
     */
    public NotificationTemplate ()
    {
        this.attachments = new Vector<File> ();
    } // NotificationTemplate


    /**************************************************************************
     * This constructor creates a new instance of the class NotificationTemplate and
     * sets the required attributes. <BR/>
     *
     * @param   subject        subject text.
     * @param   content        content text.
     * @param   description    description text.
     * @param   activities  activities text.
     */
    public NotificationTemplate (String subject, String content, String description,
                                 String activities)
    {
        // set the attributes
        this.setSubject (subject);
        this.setContent (content);
        this.setDescription (description);
        this.setActivities (activities);
        this.attachments = new Vector<File> ();
    } // NotificationTemplate


    /**************************************************************************
     * set the subject. <BR/>
     *
     * @param   value   subject text.
     */
    public void setSubject (String value)
    {
        // set the subject
        this.subject = value;
    } // setSubject


    /**************************************************************************
     * set the content. <BR/>
     *
     * @param   value    content text.
     */
    public void setContent (String value)
    {
        // set the content
        this.content = value;
    } // setContent


    /**************************************************************************
     * set the description. <BR/>
     *
     * @param   value    description text.
     */
    public void setDescription (String value)
    {
        // set the description
        this.description = value;
    } // setDescription


    /**************************************************************************
     * set the activities text. <BR/>
     *
     * @param   value    activities text.
     */
    public void setActivities (String value)
    {
        // set the activities text
        this.activities = value;
    } // setActivities


    /**************************************************************************
     * get the subject. <BR/>
     *
     * @return  the subject text
     */
    public String getSubject ()
    {
        return this.subject;
    } // getSubject


    /**************************************************************************
     * get the content. <BR/>
     *
     * @return  the content text
     */
    public String getContent ()
    {
        return this.content;
    } // getContent


    /**************************************************************************
     * get the description. <BR/>
     *
     * @return  the description text
     */
    public String getDescription ()
    {
        return this.description;
    } // getDescription


    /**************************************************************************
     * get the activities text. <BR/>
     *
     * @return  the activities text
     */
    public String getActivities ()
    {
        return this.activities;
    } // getActivities


    /**************************************************************************
     * add a file to the attachments. <BR/>
     *
     * @param   file        an attachment file.
     */
    public void addAttachment (File file)
    {
        this.attachments.addElement (file);
    } // addAttachments


    /**************************************************************************
     * get the email content type. <BR/>
     *
     * @return  the email content type
     */
    public int getEmailContentType ()
    {
        return emailContentType;
    } // getEmailContentType


    /**************************************************************************
     * set the email content type. <BR/>
     *
     * @param   emailContentType  the email content type
     */
    public void setEmailContentType (int emailContentType)
    {
        this.emailContentType = emailContentType;
    } // setEmailContentType    
} // class NotificationTemplate