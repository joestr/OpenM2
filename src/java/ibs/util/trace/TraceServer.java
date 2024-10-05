/*
 * Class: TraceServer.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.trace.SocketThread;
import ibs.util.trace.Tracer;
import ibs.util.trace.TracerConstants;

import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;


/******************************************************************************
 * The server component for tracing. <BR/>
 * This component acts as a trace server. This means that it opens a socket
 * which waits for requests from clients which want to do something with the
 * tracing results. When a client requests a connection and this connection is
 * accepted the server sends the actual tracing data to that client.
 *
 * @version     $Id: TraceServer.java,v 1.13 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)  001102
 ******************************************************************************
 */
public class TraceServer extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TraceServer.java,v 1.13 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * Port for the trace server. <BR/>
     */
    public static int port = TracerConstants.traceServerPort;

    /**
     * The thread which is responsible for the basic server. <BR/>
     * That means it waits for requests coming from any client.
     */
    private ServerThread serverThread = null;

    /**
     * The thread which is handling all the current socket connections.
     * <BR/>
     */
    private SocketThread socketThread = null;


    /**************************************************************************
     * The server thread. <BR/>
     * This thread is responsible for handling client requests for connections.
     * It waits for connections and registers each connection to the
     * SocketThread.
     *
     * @version     $Id: TraceServer.java,v 1.13 2007/07/31 19:14:01 kreimueller Exp $
     *
     * @author      Klaus Reimüller (KR)  001112
     **************************************************************************
     */
    class ServerThread implements Runnable
    {
        /**
         * Is the service still alive?. <BR/>
         * If this property is false the service is stopped immediately.
         */
        private boolean isServiceAlive = false;

        /**
         * The thread which is handling all the current socket connections.
         * <BR/>
         */
        protected SocketThread socketThread = null;


        /**********************************************************************
         * Create a new instance of this class. <BR/>
         */
        public ServerThread ()
        {
//            this.srv = srv;
        } // ServerThread


        /**********************************************************************
         * The thread's main method. <BR/>
         * This method runs until the thread is terminated.
         * It listens to incoming events form clients which want to establish
         * connections to the server and establishes the connections.
         */
        public void run ()
        {
            ServerSocket serverSocket = null; // the server socket
            Socket socket = null;           // the actual socket
//            int timeoutIncr = 1000;         // the timeout increment

//            System.out.println ("Service running.");

            try
            {
//System.out.println ("port = " + TracerConstants.traceServerPort);
                // wait until the port has been read:
                while (TracerConstants.traceServerPort == -1)
                {
                    // check every 500 ms again to prevent from
                    // a 100% CPU load
                    Thread.sleep (500);
                } // while

//System.out.println ("port2 = " + TracerConstants.traceServerPort);

                TraceServer.port = TracerConstants.traceServerPort;

                // create a server socket:
                serverSocket = new ServerSocket (TraceServer.port);

                // set the service to be alive:
                this.isServiceAlive = true;

//System.out.println ("before while...");
                // do while the service is not terminated:
                while (this.isServiceAlive)
                {
                    try
                    {
//System.out.println ("wait " + (timeout/1000) + " seconds...");
//                        serverSocket.setSoTimeout (timeout);
                        socket = serverSocket.accept ();
//System.out.println ("set socket: " + socket + ";" + this);
//                        timeout = timeoutIncr;
                        this.socketThread.addSocket (socket);
                    } // try
                    catch (InterruptedIOException e)
                    {
//                        timeout += timeoutIncr;
//this.socketThread.sendMessage ("timeout");
                    } // catch
                    catch (Exception e)
                    {
                        System.out.println ("" + e);
//this.socketThread.sendMessage ("Exception: " + e);
                    } // catch
                } // while
            } // try
            catch (Exception e)
            {
                System.out.print ("" + e);
//this.socketThread.sendMessage ("Exception: " + e);
//this.socketThread.sendMessage ("terminating.");
                System.exit (-1);
            } // catch

//this.socketThread.sendMessage ("Service stopped.");
//System.out.println ("Service stopped.");
        } // run


        /**********************************************************************
         * Stop the thread. <BR/>
         */
        public synchronized void stop ()
        {
            this.isServiceAlive = false;
            this.notify ();
        } // stop

    } // class ServerThread


    /**************************************************************************
     * The main procedure which is called if this server is called for
     * standalone execution. <BR/>
     *
     * @param argv  The arguments.
     */
    public static void main (String[] argv)
    {
        TraceServer.startTraceServer ();
    } // main


    /**************************************************************************
     * Get a trace server which is not started. <BR/>
     * This method shall be called if the server is used for excecution
     * within the scope of an existing application.
     *
     * @return  The TraceServer instance which was started. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static TraceServer getTraceServer ()
    {
        TraceServer srv = new TraceServer ();
        return srv;
    } // getTraceServer


    /**************************************************************************
     * Start a trace server. <BR/>
     * This method shall be called if the server is used for excecution
     * within the scope of an existing application.
     *
     * @return  The TraceServer instance which was started. <BR/>
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static TraceServer startTraceServer ()
    {
        TraceServer srv = new TraceServer ();
        srv.start ();
        return srv;
    } // startTraceServer


    /**************************************************************************
     * Start the server. <BR/>
     * This methods starts all threads which are needed for the server
     * to fulfill its job.
     */
    public void start ()
    {
        Thread thread = null;           // the actual thread

        try
        {
            if (this.socketThread == null) // currently no socket thread?
            {
                // create the thread which is managing all existing socket
                // connections:
                this.socketThread = new SocketThread ();
                thread = new  Thread (this.socketThread);
                thread.start ();
            } // if curently no socket thread

            if (this.serverThread == null) // currently no server thread?
            {
                // create the thread which is responsible for the establishment
                // of connections;
                this.serverThread = new ServerThread ();
                this.serverThread.socketThread = this.socketThread;
                thread = new  Thread (this.serverThread);
                thread.start ();
            } // if currently no server thread
        } // try
        catch (Exception e)
        {
            System.out.print (e.getMessage ());
            System.exit (-1);
        } // catch
    } // start


    /**************************************************************************
     * Write a message to the tracer client (s). <BR/>
     *
     * @param   sourceName  The name of the event source.
     * @param   msg         The message to be written to the client.
     * @param   tracer      The tracer.
     */
    public void trace (String sourceName, String msg, Tracer tracer)
    {
        // check if there is a connection manager:
        if (this.socketThread != null)  // connection manager exists?
        {
            // send the message through the several connections:
            this.socketThread.sendMessage (sourceName + ";" + msg);
//tracer.print1 ("TraceServer", "###sent message");
        } // if connection manager exists
        else                            // no connection manager available
        {
            System.out.println ("no local thread.");
        } // else
    } // trace


    /**************************************************************************
     * Stop the trace server. <BR/>
     */
    public void stop ()
    {
        SocketThread socketThread = this.socketThread; // the socket thread
        ServerThread serverThread = this.serverThread; // the server thread

        // ensure that no more connections can be accepted:
        this.serverThread = null;
        // ensure that no more messages can be written to the thread:
        this.socketThread = null;

        // stop the threads:
        socketThread.stop ();
        serverThread.stop ();
    } // stop

} // class TraceServer
