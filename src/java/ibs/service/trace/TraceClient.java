/*
 * Class: TraceClient.java
 */

// package:
package ibs.service.trace;

// imports:
import ibs.BaseObject;
import ibs.util.trace.Authentication;
import ibs.util.trace.TracerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;


/******************************************************************************
 * The client component for window tracing. <BR/>
 * This component acts as a trace client. This means that it opens a socket
 * to an already existing trace server. It the gets continously the tracing
 * data from that server until the client finished or the connection is closed.
 * The data is send to the output.
 *
 * @version     $Id: TraceClient.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)  001102
 ******************************************************************************
 */
public class TraceClient extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TraceClient.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * The actual thread handling messages from the server. <BR/>
     */
    protected ClientThread clientThread = null;

    /**
     * Name of host to which the last connection was established. <BR/>
     * If there is a connection open this is the name of the current host.
     */
    protected String hostName = TracerConstants.TRACESERVER_DEFAULT;

    /**
     * Port of host to which the last connection was established. <BR/>
     * If there is a connection open this is the port of the current host.
     */
    protected int port = TracerConstants.TRACESERVERPORT_DEFAULT;

    /**
     * Password with which the last connection was established. <BR/>
     * If there is a connection open this is the password which was used to
     * open that connection.
     */
    protected byte[] password = TracerConstants.TRACERPASSWORD_DEFAULT;


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: TraceClient.java,v 1.8 2007/07/24 21:27:33 kreimueller Exp $
     *
     * @author      Klaus, 13.10.2003
     ******************************************************************************
     */
    class ClientThread implements Runnable
    {
        /**
         * The actual socket connection to the server. <BR/>
         */
        private Socket socket = null;

        /**
         * Tells whether the client tracer is alive. <BR/>
         * Default: <CODE>false</CODE>.
         */
        private boolean isServiceAlive = false;

        /**
         * The caller of this thread. <BR/>
         */
        private TraceClient caller = null;


        /**********************************************************************
         * Create a new instance of the thread. <BR/>
         *
         * @param   caller  The object which created the thread.
         */
        public ClientThread (TraceClient caller)
        {
            this.caller = caller;
        } // ClientThread


        /**********************************************************************
         * Try to open a new socket connection to the required host. <BR/>
         *
         * @param   hostName    The name of the host to which the connection
         *                      shall be established.
         * @param   password    The password to use for authorization of the
         *                      client by the server.
         *
         * @return  <CODE>true</CODE> if the connection was correctly
         *          established, <CODE>false</CODE> otherwise.
         *
         * @throws  IOException
         *          There occurred an exception during connecting.
         */
        protected synchronized boolean connect (String hostName, byte[] password)
            throws IOException
        {
            // call common method:
            return this.connect (hostName,
                TracerConstants.TRACESERVERPORT_DEFAULT, password);
        } // connect


        /**********************************************************************
         * Try to open a new socket connection to the required host. <BR/>
         *
         * @param   hostName    The name of the host to which the connection
         *                      shall be established.
         * @param   port        The port number to connect to on the host.
         * @param   password    The password to use for authorization of the
         *                      client by the server.
         *
         * @return  <CODE>true</CODE> if the connection was correctly
         *          established, <CODE>false</CODE> otherwise.
         *
         * @throws  IOException
         *          There occurred an exception during connecting.
         */
        protected synchronized boolean connect (String hostName, int port,
                                                byte[] password)
            throws IOException
        {
            Authentication authentication = null;
                                        // object used to authorize the client
            boolean isAuthorized = false; // is the client authorized?

            try
            {
                // try to open a new socket connection:
                this.socket = new Socket (hostName, port);

                this.caller.showSystemMessage ("authenticating client ...");
                // check if the client is authorized:
                authentication = new Authentication (this.socket);
                isAuthorized = authentication.authorizeMe (password);

                if (isAuthorized)       // authorization ok?
                {
                    this.caller.showSystemMessage ("authorization successful; connection established; the socket: " + this.socket);
                } // if authorization ok
                else                    // authorization failed
                {
                    this.caller.showSystemMessage ("authorization failed; disconnecting ... ");
//caller.showSystemMessage ("authorization failed");
                } // else authorization failed
            } // try
            catch (NoSuchAlgorithmException n)
            {
                this.caller.showSystemMessage ("Exception when computing authentification string: " + n.getMessage ());
//caller.showSystemMessage ("authorization failed");
                isAuthorized = false;
            } // catch the exception if no supported algorithm used
            catch (Exception e)
            {
                isAuthorized = false;
                this.caller.showSystemMessage (e.getMessage ());
                throw new IOException ("exception during connect: " + e);
            } // catch

            // ensure that the listener knows what to do:
            this.notify ();
            return isAuthorized;
        } // connect


        /**********************************************************************
         * Close the current socket connection. <BR/>
         *
         * @throws  IOException
         *          There occurred an error during disconnecting.
         */
        protected synchronized void disconnect () throws IOException
        {
            try
            {
                // close the socket connection:
                if (this.socket != null) // connection exists?
                {
                    // close the connection:
                    this.socket.close ();

                    // socket not longer available:
                    this.socket = null;
                } // if connection exists
            } // try
            catch (Exception e)
            {
                System.out.println (e.getMessage ());
                throw new IOException ("exception during disconnect: " + e);
            } // catch

            // ensure that the listener knows what to do:
            this.notify ();
//caller.showSystemMessage ("end of disconnect");
        } // disconnect


        /**********************************************************************
         * Listen for events for this client. <BR/>
         */
        public void run ()
        {
            InputStream stream = null;      // the input stream
            BufferedReader reader = null;   // the reader

            // set thread to alive:
            this.isServiceAlive = true;

            // loop while the thread is alive:
            while (this.isServiceAlive)     // we're still alive
            {
                // ensure that there is a socket:
                this.waitForSocket ();

                // check if there exists a socket:
                if (this.isServiceAlive && this.socket != null)
                                        // the socket exists?
                {
                    try
                    {
                        // get the input stream from the socket connection:
                        stream = this.socket.getInputStream ();

                        // create a reader based on the stream:
                        reader =
                            new BufferedReader (new InputStreamReader (stream));

                        // read the data and write it to the output:
                        this.readWrite (reader);
                    } // try
                    catch (SocketException e)
                    {
                        // something happened with the socket connection;
                        // obviously the connection was reset
                        this.caller.showSystemMessage ("SocketException: " + e);
                        try
                        {
                            // ensure that the connection is clean:
                            this.disconnect ();
                            this.caller.showSystemMessage ("Connection closed.");
                        } // try
                        catch (IOException e2)
                        {
                            this.caller.showSystemMessage ("" + e2);
                        } // catch
                    } // catch
                    catch (Exception e)
                    {
                        this.caller.showSystemMessage ("" + e);
                    } // catch
                } // if the socket exists
                else                    // socket not found
                {
                    this.caller.showSystemMessage ("Socket not found.");
                } // else socket not found
            } // while we're still alive
            this.caller.showSystemMessage ("ClientThread finished.");
        } // run


        /**********************************************************************
         * Wait until there exists a socket connection or the client is dead.
         * <BR/>
         */
        public synchronized void waitForSocket ()
        {
            try
            {
                // wait until something happens:
                while (this.isServiceAlive && this.socket == null)
                {
                    // wait until the thread is notified:
                    this.wait ();
                } // while
            } // try
            catch (InterruptedException e)
            {
//caller.showSystemMessage ("Interrupted while waiting: " + e.getMessage ());
            } // catch
        } // waitForSocket


        /**********************************************************************
         * Read from the input and write to the output. <BR/>
         *
         * @param   reader  The reader from which to read the data.
         */
        public void readWrite (BufferedReader reader)
        {
            char[] buffer = new char [50];  // buffer for reading characters
            int count = 0;                  // number of characters read from
                                            // stream
            String msg = null;              // the actual message

            try
            {
                // check if the reader was successfully created:
                while (this.isServiceAlive && reader != null)
                                        // reader still exists
                {
                    // read characters from the stream:
                    count = reader.read (buffer);

                    // check if there was something read:
                    if (count > 0)      // read at least one byte?
                    {
                        // convert the buffer to string representation:
                        msg = new String (buffer, 0, count);

                        // add the message to the output:
                        this.caller.showMessage (msg);
                    } // if read at least one byte
                } // while reader still exists
            } // try
            catch (IOException e)
            {
                // through this exception the read process is stopped.
//caller.showSystemMessage ("IOException while reading: " + e);
            } // catch
        } // readWrite


        /**********************************************************************
         * Close the thread. <BR/>
         */
        protected synchronized void stop ()
        {
            // set the thread to be not longer alive:
            this.isServiceAlive = false;
//caller.showSystemMessage ("the thread is going to be stopped.");

            // ensure that the listener knows what to do:
            this.notify ();
//caller.showSystemMessage ("end of stop.");
        } // stop

    } // class ClientThread


    /**************************************************************************
     * The main method. <BR/>
     * This method is called if the class is called directly by the Java VM.
     *
     * @param   argv    Array containing the arguments. These arguments are
     *                  currently not evaluated.
     */
    public static void main (String[] argv)
    {
        TraceClient client = new TraceClient ();
        client.run ();

        try
        {
            client.connect (TracerConstants.TRACESERVER_DEFAULT,
                            TracerConstants.TRACESERVERPORT_DEFAULT,
                            TracerConstants.TRACERPASSWORD_DEFAULT);
        } // try
        catch (Exception e)
        {
            client.showSystemMessage ("Exception during connect: " + e);
        } // catch

        int x = 0;
        for (int i = 0; i < 2000000000; i = i + 1)
        {
            x = -x + i;
        } // for i
//client.showSystemMessage ("x = " + x);

        try
        {
            client.disconnect ();
        } // try
        catch (Exception e)
        {
            client.showSystemMessage ("Exception during disconnect: " + e);
        } // catch

//client.showSystemMessage ("cc");
        // set connection to being not alive:
        client.clientThread.stop ();
        client.showSystemMessage ("finished.");
    } // main


    /**************************************************************************
     * Create a new instance if this class. <BR/>
     */
    public TraceClient ()
    {
        // nothing to do
    } // TraceClient


    /**************************************************************************
     * Run the client. <BR/>
     */
    protected void run ()
    {
        this.startThread ();
        this.show ();
    } // run


    /**************************************************************************
     * Try to open a new socket connection to the required host. <BR/>
     *
     * @param   hostName    The name of the host to which the connection shall
     *                      be established.
     * @param   port        The port number to connect to on the host.
     * @param   password    The password to use for authorization of the
     *                      client by the server.
     *
     * @return  <CODE>true</CODE> if the connection was correctly
     *          established, <CODE>false</CODE> otherwise.
     *
     * @throws  IOException
     *          There occurred an exception during connecting.
     */
    protected boolean connect (String hostName, int port, byte[] password)
        throws IOException
    {
        // call the corresponding method of the thread:
        boolean isAuthorized =
            this.clientThread.connect (hostName, port, password);

        if (isAuthorized)               // authorization ok?
        {
            // remember the host name and port address:
            this.hostName = hostName;
            this.port = port;
        } // if authorization ok
        else                            // authorization failed
        {
            // cancel connection:
            this.clientThread.disconnect ();
        } // else authorization failed

        return isAuthorized;
    } // connect


    /**************************************************************************
     * Close the current socket connection. <BR/>
     *
     * @throws  IOException
     *          There occurred an error during disconnecting.
     */
    protected synchronized void disconnect () throws IOException
    {
        // call the corresponding method of the thread:
        this.clientThread.disconnect ();
    } // disconnect


    /**********************************************************************
     * Start the client thread. <BR/>
     * This thread manages connections with trace servers.
     */
    private void startThread ()
    {
        Thread thread = null;           // the actual thread

        try
        {
            // create the thread which is managing connections to trace servers:
            this.clientThread = new ClientThread (this);
            thread = new  Thread (this.clientThread);
            thread.start ();
        } // try
        catch (Exception e)
        {
            this.showSystemMessage ("" + e);
            System.exit (-1);
        } // catch
    } // startThread


    /**************************************************************************
     * Display the client. <BR/>
     */
    protected void show ()
    {
        // nothing to do
    } // show


    /**************************************************************************
     * Add a message to the current output. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected void showMessage (String msg)
    {
        // display the message:
        System.out.print (msg);
    } // showMessage


    /**************************************************************************
     * Show message at system level. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected void showSystemMessage (String msg)
    {
        // display the message:
        System.out.println ("=> " + msg);
    } // showMessage


    /**************************************************************************
     * Finish everything within the object. <BR/>
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize () throws Throwable
    {
        try
        {
            this.clientThread.disconnect ();
        } // try
        catch (Exception e)
        {
            this.showSystemMessage (e.getMessage ());
        } // catch

        // call common method:
        super.finalize ();
    } // finalize

} // class TraceClient
