/*
 * Class: SocketThread.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.trace.Authentication;
import ibs.util.trace.TracerConstants;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;


/******************************************************************************
 * The server component for tracing. <BR/>
 * This component acts as a trace server. This means that it opens a socket
 * which waits for requests from clients which want to do something with the
 * tracing results. When a client requests a connection and this connection is
 * accepted the server sends the actual tracing data to that client.
 *
 * @version     $Id: SocketThread.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)  001102
 ******************************************************************************
 */
class SocketThread implements Runnable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SocketThread.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * The first of all messages. <BR/>
     */
    private static final String MSG_START = "start";

    /**
     * The last of all messages. <BR/>
     *
     * @deprecated  This property is never used.
     */
/* KR never used
    private static final String MSG_END = "stop";
*/


    /**
     * The messages to be sent to the clients. <BR/>
     */
    public Vector<String> messages = new Vector<String> (10, 10);

    /**
     * Is the service still alive?. <BR/>
     * If this property is false the service is stopped immediately.
     */
    private boolean isServiceAlive = false;

    /**
     * A vector containing the objects for all current connections. <BR/>
     */
    private Vector<SocketObject> socketObjects = new Vector<SocketObject> (1, 1);



    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: SocketThread.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $
     *
     * @author      Klaus, 11.10.2003
     ******************************************************************************
     */
    public class DisconnectedException extends Throwable
    {
        /**
         * Version info of the actual class. <BR/>
         * This String contains the version number, date, and author of the last
         * check in to the code versioning system. This is implemented as CVS tag
         * to ensure that it is automatically updated by the cvs system.
         */
        public static final String VERSIONINFO =
            "SocketThread.DisconnectedException: $Id: SocketThread.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $";


        /**
         * Serializable version number. <BR/>
         * This value is used by the serialization runtime during deserialization
         * to verify that the sender and receiver of a serialized object have
         * loaded classes for that object that are compatible with respect to
         * serialization. <BR/>
         * If the receiver has loaded a class for the object that has a different
         * serialVersionUID than that of the corresponding sender's class, then
         * deserialization will result in an {@link java.io.InvalidClassException}.
         * <BR/>
         * This field's value has to be changed every time any serialized property
         * definition is changed. Use the tool serialver for that purpose.
         */
        static final long serialVersionUID = -66777585253859065L;
    } // DisconnectedException


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: SocketThread.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $
     *
     * @author      Klaus, 11.10.2003
     ******************************************************************************
     */
    class SocketObject extends Object
    {
        /**
         * The socket to which this object belongs. <BR/>
         */
        private Socket socket = null;

        /**
         * The writer used to put data onto the socket connection. <BR/>
         */
        private PrintWriter writer = null;


        /**************************************************************************
         * Creates a SocketObject object. <BR/>
         * This constructor calls the corresponding constructor of the super class.
         * <BR/>
         *
         * @param   socket  ???
         */
        protected SocketObject (Socket socket)
        {
            OutputStream stream = null; // the stream for writing files
            Authentication authentication = null;
                                        // object used to authorize the client
            boolean isAuthorized = false; // is the client authorized?

            // set the socket:
            this.socket = socket;

            // check if the socket is valid:
            if (socket != null)         // the socket is valid?
            {
                try
                {
                    // check if the client is authorized:
                    authentication = new Authentication (this.socket);
                    isAuthorized = authentication.authorizeClient (
                        TracerConstants.VERSION10, TracerConstants.password);

                    if (isAuthorized)   // authorization ok?
                    {
                        // get the data stream and a writer for putting data
                        // onto the stream:
                        stream = socket.getOutputStream ();
                        this.writer = new PrintWriter (stream, true);
                    } // if authorization ok
                    else                // authorization failed
                    {
                        // close the socket connection:
                        this.socket.close ();
                    } // else authorization failed
                } // try
                catch (Exception e)
                {
                    System.out.println ("Exception during getting stream: " + e.getMessage ());
//                throw new IOException ("Exception during getting stream: " + e);
                } // catch
            } // if the socket is valid
        } // SocketObject


        /**************************************************************************
         * Send some messages to the client. <BR/>
         *
         * @param   messages    The messages to be sent.
         *
         * @return  <CODE>true</CODE> if the client is active;
         *          <CODE>false</CODE> otherwise.
         */
        protected boolean sendMessagesToClient (Vector<String> messages)
        {
            boolean isClientAlive = false; // is the current client still alive?
//            System.out.println ("Connect from client.");

            // check if the socket is valid:
            if (this.socket != null)    // the socket is valid?
            {
                try
                {
                    // send the messages:
                    this.out (messages);
                    isClientAlive = true;
                } // try
                catch (DisconnectedException e)
                {
//System.out.println ("client disconnected");
                } // catch
                catch (Exception e)
                {
//System.out.println ("exception when writing: " + e);
                } // catch
            } // if the socket is valid

            // check if everything was o.k. with the client:
            if (!isClientAlive)         // client has died?
            {
                // disable further writing:
                this.close ();
            } // if client has died
            return isClientAlive;
        } // sendMessageToClient


        /**************************************************************************
         * This method ... <BR/>
         *
         * @param   messages    The messages to be displayed.
         *
         * @throws  SocketThread.DisconnectedException
         *          The output stream was disconnected.
         *          (cannot be correctly documented because of error within
         *          checkstyle)
         */
        private void out (Vector<String> messages)
            throws SocketThread.DisconnectedException
        {
            int i = 0;                  // loop counter
            String msg = null;          // the actual message

//System.out.println (":" + msg);
            // check if everything is o.k. with the connection:
            if (this.writer != null && !this.writer.checkError ())
            {
                try
                {
                    // loop through all messages and send them to the client:
                    for (i = 0; i < messages.size () &&
                        i < TracerConstants.maxMessages; i++)
                    {
                        // get the actual message:
                        msg = messages.elementAt (i);
                        // print the message to the stream:
                        this.writer.print (msg);
                    } // for i

                    if (i < messages.size () &&
                        i >= TracerConstants.maxMessages)
                                        // there were some messages discarded?
                    {
                        // send the corresponding message to the client:
                        this.writer
                            .print (StringHelpers
                                .replace (
                                    TracerConstants.MSG_TOOMANYMESSAGES,
                                    UtilConstants.TAG_NUMBER,
                                    "" +
                                        (messages.size () - TracerConstants.maxMessages)) +
                                "\n");
/*
System.out.println
                            (StringHelpers.replace (
                                TracerConstants.MSG_TOOMANYMESSAGES,
                                UtilConstants.TAG_NUMBER,
                                "" + (messages.size () -
                                    TracerConstants.maxMessages)) + "\n");
*/
                    } // if there were some messages discarded
                    this.writer.flush (); // important!!
                } // try
                catch (Exception e)
                {
                    System.out.println (e.getMessage ());
//                    throw new Exception ("exception during write: " + e);
                } // catch
            } // if

            // check if still everything is o.k. with the connection:
            if (this.writer.checkError ()) // there occurred an error?
            {
//System.out.println ("disconnected!");
                throw new DisconnectedException ();
            } // if there occurred an error
        } // out


        /**************************************************************************
         * This method ... <BR/>
         */
        protected void close ()
        {
            // set all connection objects to null:
            this.socket = null;
            this.writer = null;
        } // close
    } // class SocketObject


    /**************************************************************************
     * Creates a SocketThread object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public SocketThread ()
    {
        // nothing to do
    } // SocketThread


    /**************************************************************************
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run ()
     */
    public void run ()
    {
        boolean isClientAlive = false;  // is the current client still alive?
        SocketObject so = null;         // the actual connection object
        Vector<String> messages = null; // the actual messages

//System.out.println ("Socket service running.");
        // set the service to be alive:
        this.isServiceAlive = true;

        // initialize the message text:
        messages = new Vector<String> (10, 10);
        messages.addElement (SocketThread.MSG_START);

        // run indefinitely while the service is alive and there is a
        // message:
        while (messages.size () > 0 && this.isServiceAlive)
        {
            // initialize the messages before reading them:
            messages = this.getMessages ();

            try
            {
                synchronized (this)
                {
                    // check if the message is already read:
                    if (messages.size () == 0)
                    {
                        // wait until the thread is notified:
                        this.wait ();
                    } // if
                } // synchronized
            } // try
            catch (InterruptedException e)
            {
//System.out.println ("Interrupted while waiting: " + e.getMessage ());
            } // catch

            // get the actual messages:
            if (messages.size () == 0)  // currently no messages available?
            {
                messages = this.getMessages ();
            } // if currently no messages available

            // check if the service is still alive:
            this.isServiceAlive =
                this.isServiceAlive && (messages.size () != 0);
            if (this.isServiceAlive) // service alive?
            {
                try
                {
                    // loop through all connection objects:
                    for (int i = 0;
                        i < this.socketObjects.size ();
                        i++)
                    {
                        // the actual connection object:
                        so = this.socketObjects.elementAt (i);
                        // send the message to the client and check if the
                        // connection is still alive:
                        isClientAlive = so.sendMessagesToClient (messages);

                        // check if the client is alive:
                        if (!isClientAlive) // the client is not alive?
                        {
                            // remove the connection object:
                            this.socketObjects.removeElementAt (i);
                            // because all objects in the vector are moved
                            // down to this position decrement the actual
                            // index:
                            i--;
                        } // if the client is not alive
                    } // for
                } // try
/*
                catch (DisconnectedException e)
                {
System.out.println ("client disconnected");
                    isClientAlive = false;
                } // catch
*/
                catch (Exception e)
                {
//System.out.println ("exception when writing: " + e);
                    this.isServiceAlive = false;
                } // catch
            } // if service alive
        } // while

//            System.out.println ("Socket service stopped.");
    } // run


    /**************************************************************************
     * Stop the thread. <BR/>
     */
    public synchronized void stop ()
    {
        this.isServiceAlive = false;
        this.notifyAll ();
    } // stop


    /**************************************************************************
     * Add a new connection. <BR/>
     *
     * @param socket    ???
     */
    protected synchronized void addSocket (Socket socket)
    {
        // create a socket object:
        this.socketObjects.addElement (new SocketObject (socket));
    } // addSocket


    /**************************************************************************
     * Send a message through the socket to all connected clients. <BR/>
     *
     * @param   msg     ???
     */
    public synchronized void sendMessage (String msg)
    {
        // set the message and notify the thread:
        this.messages.addElement (msg + "\n");
//        this.msg = msg + "\n";
//System.out.println (":" + this.messages.size () + ":" + msg);
        this.notifyAll ();
    } // sendMessage


    /**************************************************************************
     * Send a message through the socket to all connected clients. <BR/>
     *
     * @return  The vector containing the messages to send.
     */
    protected synchronized Vector<String> getMessages ()
    {
        Vector<String> messages = null; // the actual messages

        // get the actual messages:
        messages = this.messages;
        // set the new messages:
        this.messages = new Vector<String> (10, 10);

        return messages;                // return the messages
    } // getMessages

} // class SocketThread
