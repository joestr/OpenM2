/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 22.08.2002
 * Time: 18:13:28
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.service.conf.Configuration;
import ibs.service.conf.ConfigurationException;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJob;
import ibs.service.observer.ObserverJobData;
import ibs.service.observer.ObserverLoader;
import ibs.tech.sql.DBConnector;
import ibs.util.list.ListException;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: ObserverAdministrator.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      hpichler, 22.08.2002
 ******************************************************************************
 */
public class ObserverAdministrator extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverAdministrator.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $";

    /**
     * Output separator. <BR/>
     */
    private static final String OUTPUT_SEPARATOR =
        "----------------------------------------------------------------------------------";

    /**
     * Error message: message start. <BR/>
     */
    private static final String ERRM = "ERROR: ";


    /**************************************************************************
     * The main method. <BR/>
     *
     * @param   args    The program arguments.
     */
    public static void main (String[] args)
    {
        ObserverAdministrator.echo (ObserverAdministrator.OUTPUT_SEPARATOR);
        ObserverAdministrator.echo ("ObserverLoader Administration Version 1.0");
        ObserverAdministrator.echo (" (c) 2002 - 2004 tectum ");

        if (args.length != 1)
        {
            ObserverAdministrator.echo ("");
            ObserverAdministrator.echo ("Usage: ibs.observer.ObserverLoader <path-to-config-files> ");
            ObserverAdministrator.echo ("The config files 'observers.xml' and 'ibssystem.xml' must exist in this directory.");
            ObserverAdministrator.echo ("Please disable the m2-traceserver in 'ibssystem.xml', otherwise parallel execution with m2 is not possible.");
            ObserverAdministrator.echo ("");
            return;
        } // if

        int num = 0;
        ObserverLoader ol = ObserverLoader.instance ();
        Vector<String> vNames = null;
        Vector<String> vClassNames = null;
        byte[] b = new byte[64];
        int l = -1;
        String s = "";
        char c = '_';
        int n = -1;

        try
        {
            // initialize the database connector:
            DBConnector.init ();

            // create static db-connector with given configuration "ibssystem.xml"
            Configuration conf = new Configuration (args[0]);
            conf.readConfig ();
            DBConnector.setConfiguration (conf.getDbConf ());

            ol.setConfigPath (args[0] + "observers.xml");
            ol.loadConfigurations ();
            ObserverAdministrator.echo ("* Configuration '" + args[0] + "'.");

            vNames = ol.getObserverNames ();
            vClassNames = ol.getObserverClassNames ();
            num = vNames.size ();

            if (num != vClassNames.size ())
            {
                ObserverAdministrator.echo (ObserverAdministrator.ERRM +
                    "number of names is different to number of classnames.");
                return;
            } // if

            ObserverAdministrator.echo ("* Found " + num +  " observers.");
            for (int i = 0; i < num; i++)
            {
                ObserverAdministrator.echo ("* " + (i + 1) + ". " +
                    vClassNames.elementAt (i) + ": " + vNames.elementAt (i));
            } // for i
        } // try
        catch (ObserverException e)
        {
            ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
            return;
        } // catch
        catch (ConfigurationException e)
        {
            ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
            return;
        } // catch
        catch (ListException e)
        {
            ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
            return;
        } // catch

        ObserverAdministrator.echo (ObserverAdministrator.OUTPUT_SEPARATOR);

        // menu
        while (c != 'q')
        {
            System.out.println ("");
            System.out.print (">");
            try
            {
                System.in.read (b);
            } // try
            catch (IOException e)
            {
                ObserverAdministrator.echo (">>>");
                ObserverAdministrator.echo (">>> " + ObserverAdministrator.ERRM + e.toString ());
                ObserverAdministrator.echo (">>>");
                return;
            } // catch

            s = ObserverAdministrator.extractString (b);
            l = s.length ();

            /////////////////////////////////////
            //
            // check base format: c|c##
            //
            if (l == 1)
            {
                c = s.charAt (0); // get command
                n = -1;
            } // if
            else if (l == 3)     // get integer part
            {
                c = s.charAt (0); // get command
                try
                {
                    n = Integer.parseInt (s.substring (1, 3));
                } // try
                catch (NumberFormatException e)
                {
                    ObserverAdministrator.echo ("Command c must be of format 'c' or 'c##', where ## stands for the observer-number.");
                    continue;
                } // catch

                if (n <= 0 || n > num)
                {
                    ObserverAdministrator.echo ("Invalid observer number: " + n);
                    continue;
                } // else
            } // else if
            else
            {
                ObserverAdministrator
                    .echo ("Command c must be of format 'c' or 'c##', where ## stands for the observer-number.");
                ObserverAdministrator
                    .echo ("Possible commands [?|l|i##|u##|s##|j##|a##||g##|q]");
                continue;
            } // else
            //
            // check base format: c|c##
            //
            /////////////////////////////////////

            int x = -1;
            switch (c)
            {
                case 'q':
                    ObserverAdministrator.echo ("Quit");
                    ObserverAdministrator.echo ("Goodbye.");
                    return;
                case '?':
                    ObserverAdministrator.echo ("Help");
                    ObserverAdministrator.echo ("Not implemented");
                    break;
                case 'l':
                    ObserverAdministrator.echo ("List observers");
                    for (int i = 0; i < num; i++)
                    {
                        ObserverAdministrator.echo ((i + 1) + ". " +
                            vClassNames.elementAt (i) + ": " +
                            vNames.elementAt (i));
                    } // for i
                    break;
                case 's':
                    ObserverAdministrator.echo ("Show observers configuration ");
                    if (n == -1)
                    {
                        ObserverAdministrator.echo ("Please specifiy observer: s##");
                        continue;
                    } // if
                    x = n - 1;

                    try
                    {
                        ObserverAdministrator.echo ("* Name=" + vNames.elementAt (x));
                        ObserverAdministrator.echo ("* Class=" + vClassNames.elementAt (x));
                        ObserverAdministrator.echo ("* Configuration=" + ol.getObserverConfiguration (vNames.elementAt (x)).toString ());
                        ObserverAdministrator.echo ("* Context=" + ol.getObserverContext (vNames.elementAt (x)).toString ());
                        // check if structure exists
                        ObserverContext context = ol.getObserverContext (vNames.elementAt (x));
                        ObserverJobData jdata = new ObserverJobData (context);
                        jdata.checkStructure ();
                        ObserverAdministrator.echo ("* Base datastructures for ObserverJobs: exist");
                    } // try
                    catch (ObserverException e)
                    {
                        ObserverAdministrator.echo ("* Base datastructures for ObserverJobs: not found");
                    } // catch
                    break;
                case 'i':
                    ObserverAdministrator.echo ("Install observers base datastructures");
                    if (n == -1)
                    {
                        ObserverAdministrator.echo ("Please specifiy observer: s##");
                        continue;
                    } // if
                    x = n - 1;

                    try
                    {
                        ObserverContext context = ol.getObserverContext (vNames.elementAt (x));
                        ObserverJobData jdata = new ObserverJobData (context);
                        jdata.createStructure ();
                        ObserverAdministrator.echo ("... base datastructures for ObserverJobs installed.");
                    } // try
                    catch (ObserverException e)
                    {
                        ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
                    } // catch
                    break;
                case 'u':
                    ObserverAdministrator.echo ("Uninstall observers datastructures");
                    if (n == -1)
                    {
                        ObserverAdministrator.echo ("Please specifiy observer: s##");
                        continue;
                    } // if
                    x = n - 1;

                    try
                    {
                        ObserverContext context = ol.getObserverContext (vNames.elementAt (x));
                        ObserverJobData jdata = new ObserverJobData (context);
                        TreeSet<ObserverJobData> set = jdata.loadBaseDataOfExecuteableJobs ();
                        if (!(set.size () == 0))
                        {
                            int id = 0;
                            Iterator<ObserverJobData> it = set.iterator ();
                            // drop additional structures:
                            while (it.hasNext ())
                            {
                                ObserverJobData data = it.next ();
                                id = data.getId ();
                                ObserverAdministrator.echo ("* drop structure for job with id=" + id);
                                // create observer job - according to class given in jdata
                                try
                                {
                                    @SuppressWarnings ("unchecked") // suppress compiler warning
                                    Class<? extends ObserverJob> cl =
                                        (Class<? extends ObserverJob>) Class.forName (data.getClassName ());
                                    ObserverJob oj = cl.newInstance ();
                                    oj.setContext (context);
                                    oj.fetch (id);
                                    oj.getJdata ().dropAdditionalStructure ();
                                } // catch
                                catch (ClassNotFoundException e)
                                {
                                    System.out.println (e.toString ());
                                } // catch
                                catch (InstantiationException e)
                                {
                                    System.out.println (e.toString ());
                                } // catch
                                catch (IllegalAccessException e)
                                {
                                    System.out.println (e.toString ());
                                } // catch
                                catch (ObserverException e)
                                {
                                    // ignore exception
                                    System.out.println (e.toString ());
                                } // catch
                            } // while
                        } // else
                        // drop base structure (if exists)
                        try
                        {
                            jdata.dropStructure ();
                        } // try
                        catch (ObserverException e)
                        {
                            // ignore
                        } // catch
                        ObserverAdministrator.echo ("... datastructures for ObserverJobs uninstalled.");
                    } // try
                    catch (ObserverException e)
                    {
                        ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
                    } // catch
                    break;
                case 'j':
                    ObserverAdministrator.echo ("Show observers executable jobs");
                    if (n == -1)
                    {
                        ObserverAdministrator.echo ("Please specifiy observer: s##");
                        continue;
                    } // if
                    x = n - 1;

                    try
                    {
                        ObserverContext context = ol.getObserverContext (vNames.elementAt (x));
                        ObserverJobData jdata = new ObserverJobData (context);
                        TreeSet<ObserverJobData> set = jdata.loadBaseDataOfExecuteableJobs ();
                        if (set.size () == 0)
                        {
                            ObserverAdministrator.echo ("No jobs found.");
                        } // if
                        else
                        {
                            Iterator<ObserverJobData> it = set.iterator ();
                            while (it.hasNext ())
                            {
                                ObserverJobData data = it.next ();
                                ObserverAdministrator.echo ("* Job with id=" + data.getId ());
                                ObserverAdministrator.echo (data.toString ());
                                ObserverAdministrator.echo ("");
                            } // while
                        } // else
                    } // try
                    catch (ObserverException e)
                    {
                        ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
                    } // catch
                    break;
                case 'a':
                    ObserverAdministrator.echo ("Show observers jobs (all)");
                    if (n == -1)
                    {
                        ObserverAdministrator.echo ("Please specifiy observer: s##");
                        continue;
                    } // if
                    x = n - 1;

                    try
                    {
                        ObserverContext context = ol.getObserverContext (vNames.elementAt (x));
                        ObserverJobData jdata = new ObserverJobData (context);
                        TreeSet<ObserverJobData> set = jdata.loadBaseDataOfJobs ();
                        if (set.size () == 0)
                        {
                            ObserverAdministrator.echo ("No jobs found.");
                        } // if
                        else
                        {
                            Iterator<ObserverJobData> it = set.iterator ();
                            while (it.hasNext ())
                            {
                                ObserverJobData data = it.next ();
                                ObserverAdministrator.echo ("* Job with id=" + data.getId () + " of class " + data.getClassName ());
                                ObserverAdministrator.echo (data.toString ());
                                ObserverAdministrator.echo ("");
                            } // while
                        } // else
                    } // try
                    catch (ObserverException e)
                    {
                        ObserverAdministrator.echo (ObserverAdministrator.ERRM + e.toString ());
                    } // catch
                    break;
                default:
                    ObserverAdministrator.echo ("Unknown command: " + c);
                    ObserverAdministrator.echo ("Possible commands [?|l|i##|u##|s##|j##|a##|g##|q]");
                    break;
            } // switch

        } // while
    } // main


    /**************************************************************************
     * Extract string (ends with carriage return = 13). <BR/>
     *
     * @param   b       The byte array from which to extract the string.
     *
     * @return  The extracted string.
     */
    public static String extractString (byte[] b)
    {
        int l = 0;
        for (l = 1; l < b.length; l++)
        {
            if (b[l] == 13)
            {
                break;
            } // if
        } // for i
        return (new String (b)).substring (0, l);
    } // extractString


    /**************************************************************************
     * Write s to standard out. <BR/>
     *
     * @param   s       The string to be written to the output.
     */
    public static void echo (String s)
    {
        System.out.println (s);
    } // extractString

} // class ObserverAdministrator
