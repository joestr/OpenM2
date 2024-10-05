/*
 * Class: WindowTraceClient.java
 */

// package:
package ibs.service.trace;

// imports:
import ibs.service.trace.TraceClient;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.awt.Button;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


/******************************************************************************
 * The client component for window tracing. <BR/>
 * This component acts as a trace client. This means that it opens a socket
 * to an already existing trace server. It the gets continously the tracing
 * data from that server until the client finished or the connection is closed.
 * The data is send to the output.
 *
 * @version     $Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)  001102
 ******************************************************************************
 */
public class WindowTraceClient extends TraceClient
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     *
     */
    public static final String MENU_FILE = "Datei";

    /**
     *
     */
    public static final String MENU_FILE_CONNECT = "Verbinden...";

    /**
     *
     */
    public static final String MENU_FILE_DISCONNECT = "Trennen";

    /**
     *
     */
    public static final String MENU_FILE_CLOSE = "Schließen";


    /**
     *
     */
    public static final String MENU_EDIT = "Bearbeiten";

    /**
     *
     */
    public static final String MENU_EDIT_CLEAR = "Löschen";


    /**
     *
     */
    public static final String BUTT_OK = WindowTraceClient.FCT_OK;

    /**
     *
     */
    public static final String BUTT_CANCEL = "Abbrechen";


    /**
     *
     */
    private static final String FCT_CONNECT = "CONNECT";

    /**
     *
     */
    private static final String FCT_DISCONNECT = "DISCONNECT";

    /**
     *
     */
    private static final String FCT_OK = "OK";

    /**
     *
     */
    private static final String FCT_CANCEL = "CANCEL";

    /**
     *
     */
    private static final String FCT_CLEAR = "CLEAR";

    /**
     * Short cut for connecting. <BR/>
     * Default: CTRL-C.
     */
    private static final int SC_FILE_CONNECT = 'o';

    /**
     * Short cut for disconnecting. <BR/>
     * Default: CTRL-C.
     */
    private static final int SC_FILE_DISCONNECT = 't';

    /**
     * Short cut for closing the trace client. <BR/>
     * Default: CTRL-E.
     */
    private static final int SC_FILE_CLOSE = 'e';

    /**
     * Short cut for closing the trace client. <BR/>
     * Default: CTRL-D.
     */
    private static final int SC_EDIT_CLEAR = 'd';

    /**
     * The base title of a tracer window. <BR/>
     * This title shall be concatenated by some actual info and shown in the
     * window title bar.
     */
    private static final String WINDOWTITLE_BASE = "Trace Window";

    /**
     * The title of a tracer window when there is a connection. <BR/>
     * This title shall be concatenated by some actual info and shown in the
     * window title bar.
     * The tag {@link UtilConstants#TAG_NAME} is a stub for the real hostName
     * and has to be replaced by that name.
     *
     * @see ibs.util.Helpers#replace (String, String, String)
     */
    private static final String WINDOWTITLE_CONNECTED =
        WindowTraceClient.WINDOWTITLE_BASE + " - connected to " + UtilConstants.TAG_NAME;

    /**
     * The title of a tracer window when there is no connection. <BR/>
     * This title shall be concatenated by some actual info and shown in the
     * window title bar.
     */
    private static final String WINDOWTITLE_NOTCONNECTED =
        WindowTraceClient.WINDOWTITLE_BASE + " - not connected";


    /**
     *
     */
    TextArea textArea = null;

    /**
     *
     */
    private Frame frame = null;

    /**
     * The server dialog for the trace client. <BR/>
     */
    ServerDialog dialog = null;

    /**
     * The connect menu item. <BR/>
     */
    MenuItem menuConnect = null;

    /**
     * The disconnect menu item. <BR/>
     */
    MenuItem menuDisconnect = null;

    /**
     * The title of the window. <BR/>
     * This title is shown in the title bar.
     */
    private String windowTitle = WindowTraceClient.WINDOWTITLE_NOTCONNECTED;

    /**
     * Message: connection was closed. <BR/>
     */
    private static final String MSG_CONNECTION_CLOSED = "Connection closed.";


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
     *
     * @author      Klaus, 13.10.2003
     ******************************************************************************
     */
    class WindowListener extends WindowAdapter
    {
        /**************************************************************************
         * This method ... <BR/>
         *
         *
         */
        public void closeWindow ()
        {
            System.exit (0);
        } // closeWindow


        /**********************************************************************
         * Invoked when a window is in the process of being closed. <BR/>
         *
         * @param   event   Event which closes the window.
         */
        public void windowClosing (WindowEvent event)
        {
            this.closeWindow ();
        } // windowClosing
    } // class WindowListener


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
     *
     * @author      Klaus, 13.10.2003
     ******************************************************************************
     */
    class MenuListener extends Object implements ActionListener
    {
        /**
         *
         */
        WindowListener listener = null;

        /**
         * The parent of this event handler. <BR/>
         */
        WindowTraceClient parent = null;


        /**********************************************************************
         * Invoked when an action occurs.
         *
         * @param   event   Event which triggers the action.
         */
        public void actionPerformed (ActionEvent event)
        {
            String cmd = event.getActionCommand ();

//this.parent.showSystemMessage ("cmd = " + cmd + "; source = " + eventSource);
            if (cmd != null && cmd.equals (WindowTraceClient.FCT_CONNECT))
            {
//this.parent.showSystemMessage ("origin = "+ this.origin);
                if (this.parent.dialog == null) // dialog not existing?
                {
                    // create a new dialog:
                    this.parent.dialog = new ServerDialog (this.parent);
                    this.parent.dialog.show ();
                } // if dialog not existing

                // display the dialog:
                this.parent.dialog.setVisible (true);
            } // if

            else if (cmd != null && cmd.equals (WindowTraceClient.FCT_DISCONNECT))
            {
                try
                {
                    // cancel the connection to the server:
                    this.parent.disconnect ();
                    this.parent
                        .showSystemMessage (WindowTraceClient.MSG_CONNECTION_CLOSED);
                } // try
                catch (IOException e)
                {
                    this.parent.showSystemMessage ("" + e);
                } // catch
            } // else if

            else if (cmd != null && cmd.equals (WindowTraceClient.FCT_CLEAR))
            {
                // just clear the frame content:
                this.parent.textArea.replaceRange ("", 0, this.parent.textArea
                    .getText ().length ());
            } // else if

            else if (this.listener != null)
            {
                this.listener.closeWindow ();
            } // else if
        } // actionPerformed
    } // class MenuListener


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
     *
     * @author      Klaus, 13.10.2003
     ******************************************************************************
     */
    class DialogListener extends Object implements ActionListener
    {
        /**
         *
         */
        Component origin = null;
        /**
         *
         */
        TextComponent text1 = null;
        /**
         *
         */
        TextComponent text2 = null;
        /**
         *
         */
        TextComponent text3 = null;

        /**
         * The parent of this event handler. <BR/>
         */
        protected WindowTraceClient parent = null;


        /**********************************************************************
         * Invoked when an action occurs.
         *
         * @param   event   Event which triggers the action.
         */
        public void actionPerformed (ActionEvent event)
        {
            String cmd = event.getActionCommand ();

//this.parent.showSystemMessage ("cmd = " + cmd + "; source = " + eventSource);
            if (cmd != null && cmd.equals (WindowTraceClient.FCT_OK))
            {
                String hostName;
                int port;
                byte[] password = null;

                hostName = this.text1.getText ();
//this.parent.showSystemMessage ("hostName = " + hostName);
                try
                {
                    port = Integer.parseInt (this.text2.getText ());
                    password = this.text3.getText ().getBytes ();
//this.parent.showSystemMessage ("Port = " + port);
                    try
                    {
//this.frame.remove (this.origin);
//                        this.origin.setVisible (false);
                        // establish a connection to the server:
                        if (this.parent.connect (hostName, port, password))
                        {
                            // don't display the dialog anymore:
                            this.origin.setVisible (false);
                        } // if
                        else
                        {
                            this.origin.setVisible (false);
                            this.parent.menuConnect.setEnabled (true);
                            this.parent.menuDisconnect.setEnabled (false);
                            this.parent.disconnect ();
                        } // else
                    } // try
                    catch (IOException e)
                    {
//                        this.origin.setVisible (true);
                        this.parent.showSystemMessage ("" + e);
                    } // catch
                } // try
                catch (NumberFormatException e)
                {
                    this.parent.showSystemMessage ("port error: " + e);
                    this.text2.requestFocus ();
                    this.text2.selectAll ();
                } // catch
                catch (NullPointerException e)
                {
                    this.parent.showSystemMessage ("getting password: " + e);
                    this.text3.requestFocus ();
                    this.text3.selectAll ();
                } // catch
            } // else if

            else if (cmd != null && cmd.equals (WindowTraceClient.FCT_CANCEL))
            {
                this.origin.setVisible (false);
            } // else if
        } // actionPerformed
    } // class DialogListener


    /******************************************************************************
     * This class.... <BR/>
     *
     * @version     $Id: WindowTraceClient.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
     *
     * @author      Klaus, 13.10.2003
     ******************************************************************************
     */
    class ServerDialog extends Object
    {
        /**
         * The parent of this dialog. <BR/>
         */
        private WindowTraceClient parent = null;

        /**
         * The frame of this dialog. <BR/>
         */
        private Frame frame = null;


        /**************************************************************************
         * Creates a ServerDialog object. <BR/>
         * This constructor calls the corresponding constructor of the super class.
         * <BR/>
         *
         * @param   parent  ???
         */
        public ServerDialog (WindowTraceClient parent)
        {
            super ();
            this.parent = parent;
        } // ServerDialog


        /**************************************************************************
         * This method ... <BR/>
         *
         *
         */
        protected void show ()
        {
            Frame frame = new Frame ();
            Panel panel = null;
            Label label;
            TextField text;
            Button button;
            GridBagLayout layout = new GridBagLayout ();
            GridBagConstraints c = new GridBagConstraints ();
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets (10, 5, 5, 5);
            c.weightx = 0.0;
            c.anchor = GridBagConstraints.WEST;

            frame.setLayout (new GridLayout (2, 1));
            frame.setSize (350, 200);

            DialogListener ml = new DialogListener ();
            ml.origin = frame;
            ml.parent = this.parent;

            panel = new Panel ();
            panel.setLayout (layout);

            label = new Label ("Hostname: ", Label.LEFT);
            label.setBounds (5, 30, 90, 25);
            c.gridwidth = GridBagConstraints.RELATIVE;
            layout.setConstraints (label, c);
            panel.add (label);

            text = new TextField (this.parent.hostName, 30);
            text.setVisible (true);
            text.setBounds (105, 30, 190, 25);
            c.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints (text, c);
            panel.add (text);
            ml.text1 = text;

            c.insets = new Insets (5, 5, 5, 5);

            label = new Label ("Port: ", Label.LEFT);
            label.setBounds (5, 70, 90, 25);
            c.gridwidth = GridBagConstraints.RELATIVE;
            layout.setConstraints (label, c);
            panel.add (label);

            text = new TextField ("" + this.parent.port, 4);
            text.setBounds (105, 70, 190, 25);
            c.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints (text, c);
            panel.add (text);
            ml.text2 = text;

            label = new Label ("Password: ", Label.LEFT);
            label.setBounds (5, 70, 90, 25);
            c.gridwidth = GridBagConstraints.RELATIVE;
            layout.setConstraints (label, c);
            panel.add (label);

            text = new TextField ("myPwd73", 5);
            text.setBounds (105, 70, 190, 25);
            text.setEchoChar ('*');
            c.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints (text, c);
            panel.add (text);
            ml.text3 = text;

            frame.add (panel);

            panel = new Panel ();
            panel.setLayout (layout);

            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets (10, 5, 5, 5);

            button = new Button (WindowTraceClient.BUTT_OK);
            button.setBounds (5, 110, 40, 20);
            button.setActionCommand (WindowTraceClient.FCT_OK);
            button.addActionListener (ml);
            c.gridwidth = GridBagConstraints.RELATIVE;
            layout.setConstraints (button, c);
            panel.add (button);

            button = new Button (WindowTraceClient.BUTT_CANCEL);
            button.setBounds (55, 110, 40, 20);
            button.setActionCommand (WindowTraceClient.FCT_CANCEL);
            button.addActionListener (ml);
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridx = 3;
            layout.setConstraints (button, c);
            panel.add (button);

            frame.add (panel);

            this.frame = frame;
        } // show


        /**********************************************************************
         * Set visibility of the window. <BR/>
         *
         * @param   isVisible   <CODE>true</CODE> if it shall be visible,
         *                      <CODE>false</CODE> otherwise.
         */
        protected void setVisible (boolean isVisible)
        {
            this.frame.setVisible (isVisible);
        } // setVisible
    } // class ServerDialog


    /**************************************************************************
     * The main method. <BR/>
     * This method is called if the class is called directly by the Java VM.
     *
     * @param   argv    Array containing the arguments. These arguments are
     *                  currently not evaluated.
     */
    public static void main (String[] argv)
    {
        // create a new client instance:
        WindowTraceClient client = new WindowTraceClient ();
        // start the client:
        client.run ();
    } // main


    /**************************************************************************
     * Display the client. <BR/>
     */
    protected void show ()
    {
        MenuBar mb = new MenuBar ();
        Menu m = null;
        MenuItem mi = null;
        MenuListener ml = null;
        WindowListener windowListener = new WindowListener ();

        // check if there exists already a frame for display:
        if (this.frame == null)         // no frame available?
        {
            // create a new frame for display:
            this.frame = new Frame ();
        } // if no frame available

        // menu file:
        m = new Menu (WindowTraceClient.MENU_FILE);
        mb.add (m);

        // menu file.connect:
        ml = new MenuListener ();
        ml.listener = windowListener;
        ml.parent = this;
        mi = new MenuItem (WindowTraceClient.MENU_FILE_CONNECT, new MenuShortcut (WindowTraceClient.SC_FILE_CONNECT));
        mi.addActionListener (ml);
        mi.setActionCommand (WindowTraceClient.FCT_CONNECT);
        m.add (mi);
        this.menuConnect = mi;

        // menu file.disconnect:
        mi = new MenuItem (WindowTraceClient.MENU_FILE_DISCONNECT, new MenuShortcut (WindowTraceClient.SC_FILE_DISCONNECT));
        mi.addActionListener (ml);
        mi.setActionCommand (WindowTraceClient.FCT_DISCONNECT);
        mi.setEnabled (false);
        m.add (mi);
        this.menuDisconnect = mi;

        // menu file.close:
        mi = new MenuItem (WindowTraceClient.MENU_FILE_CLOSE, new MenuShortcut (WindowTraceClient.SC_FILE_CLOSE));
        mi.addActionListener (ml);
        m.add (mi);

        // menu edit:
        m = new Menu (WindowTraceClient.MENU_EDIT);
        mb.add (m);

        // menu edit.clear:
        ml = new MenuListener ();
        ml.listener = windowListener;
        ml.parent = this;
        mi = new MenuItem (WindowTraceClient.MENU_EDIT_CLEAR, new MenuShortcut (WindowTraceClient.SC_EDIT_CLEAR));
        mi.addActionListener (ml);
        mi.setActionCommand (WindowTraceClient.FCT_CLEAR);
        m.add (mi);

        // the other layout data:
        this.frame.setMenuBar (mb);
        this.textArea = new TextArea ();
        this.frame.add (this.textArea);
        this.frame.addWindowListener (windowListener);
//        this.frame.setLocation (10, 10);
        this.frame.setSize (600, 400);
        this.frame.setTitle (this.windowTitle);
        this.frame.setVisible (true);
    } // show


    /**************************************************************************
     * Set the title of the window. <BR/>
     * This title is set to be the new title of the window.
     * There is some information concatenated representing the actual state.
     *
     * @param   windowTitle The title to be set.
     */
    protected void setWindowTitle (String windowTitle)
    {
        // store the new window title:
        this.windowTitle = windowTitle;
        this.frame.setTitle (windowTitle);
    } // setWindowTitle


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
        boolean isAuthorized = false; // is the client authorized?

        // establish the connection:
        isAuthorized = super.connect (hostName, port, password);

        if (isAuthorized)               // authorization ok?
        {
            // enable the correct menu item:
            this.menuConnect.setEnabled (false);
            this.menuDisconnect.setEnabled (true);

            // set the corresponding window title:
            this.setWindowTitle (StringHelpers.replace (
                WindowTraceClient.WINDOWTITLE_CONNECTED,
                UtilConstants.TAG_NAME, hostName + ":" + port));
// this.showSystemMessage ("Connection established.");
        } // if authorization ok

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
        // close the connection:
        super.disconnect ();

        // enable the correct menu item:
        this.menuDisconnect.setEnabled (false);
        this.menuConnect.setEnabled (true);

        // set the corresponding window title:
        this.setWindowTitle (WindowTraceClient.WINDOWTITLE_NOTCONNECTED);
        this.showSystemMessage (WindowTraceClient.MSG_CONNECTION_CLOSED);
    } // disconnect


    /**************************************************************************
     * Add a message to the current output. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected void showMessage (String msg)
    {
        String text = null;             // the actual text of the window

        // get the actual content:
        text = this.textArea.getText ();

        // set the window title:
        // (containing the number of bytes)
        this.frame.setTitle (this.windowTitle +
                             " (" + text.length () + " Bytes)");

        // check if the text exceeded the maximum length:
        if (text.length () > 28500)
        {
            // drop the first n characters:
            this.textArea.replaceRange (text.substring (2000), 0, 65000);
        } // if

        // append the new message to the content:
        this.textArea.append (msg);
    } // showMessage


    /**************************************************************************
     * Show message at system level. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected void showSystemMessage (String msg)
    {
        // display the message:
        this.showMessage ("=> " + msg + "\n");
//        super.showSystemMessage (msg);
    } // showMessage


    /**************************************************************************
     * Finish everything within the object. <BR/>
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize () throws Throwable
    {
        // call the corresponding method of the super class:
        super.finalize ();
    } // finalize

} // class WindowTraceClient
