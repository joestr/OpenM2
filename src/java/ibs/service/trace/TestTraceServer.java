/*
 * Class: TestTraceServer.java
 */

// package:
package ibs.service.trace;

// imports:
import ibs.BaseObject;
import ibs.util.trace.TraceServer;
import ibs.util.trace.TracerConstants;


/******************************************************************************
 * Test the trace server by sending messages through the server. <BR/>
 *
 * @version     $Id: TestTraceServer.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR)  001103
 ******************************************************************************
 */
public class TestTraceServer extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TestTraceServer.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     *
     */
    TraceServer traceServer = null;


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   argv    The command line arguments.
     */
    public static void main (String[] argv)
    {
        String msg = "TestMessage";
        TestTraceServer test = null;

        TracerConstants.traceServerPort = TracerConstants.TRACESERVERPORT_DEFAULT;
        TracerConstants.traceServer = TracerConstants.TRACESERVER_DEFAULT;

        // initialize trace server:
        test = new TestTraceServer ();
        if (argv != null && argv.length > 0)
        {
            msg = argv[0];
        } // if
        test.run (msg);
//        System.exit (0);
    } // main


    /**************************************************************************
     * Creates a TestTraceServer object. <BR/>
     */
    public TestTraceServer ()
    {
        // start the trace server:
        this.traceServer = TraceServer.startTraceServer ();
    } // TestTraceServer


    /**************************************************************************
     * Runs the trace server test and displays a message. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    private void run (String msg)
    {
        String msgLocal = msg;          // variable for local assignments
        int count = 0;                  // number of bytes read from stream
        byte[] bytes = new byte[50];    // bytes to be read from stream
        String msgExit = "exit";

        System.out.println ("running the tester...");
        try
        {
            while (count >= 0 && !msgLocal.equals (msgExit))
            {
                // get the next bytes from input:
                count = System.in.read (bytes);

                // check the number of bytes:
                if (count >= 2)
                {
                    if (bytes [count - 2] == 13)
                    {
                        count -= 2;
                    } // if
                } // if

                // write the result to the trace server:
                if (count >= 0)
                {
                    msgLocal = new String (bytes, 0, count);
                    if (msgLocal != null && !msgLocal.equals (msgExit))
                    {
                        this.traceServer.trace ("TestTraceServer", msgLocal, null);
                    } // if
                } // if
            } // while
        } // try
        catch (Exception e)
        {
            System.out.println ("Exception while tracing: " + e);
        } // catch

        // stop the server:
        this.traceServer.stop ();
        System.out.println ("finished.");
    } // run

} // class TestTraceServer
