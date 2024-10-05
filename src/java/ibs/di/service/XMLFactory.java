/*
 * Class: XMLFactory.java
 */

// package:
package ibs.di.service;

// imports:
import ibs.BaseObject;
import ibs.di.service.XMLDBFactory;
import ibs.util.DateTimeHelpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;


/******************************************************************************
 * The XMLFactory is a helper class to activate the XMLDBFactory class from
 * the command line of the operation system. <BR/>
 * The XMLFactory can handle the following command line options:
 *
 * <PRE>
 *  option                              description
 *  ---------------------------------------------------------------------------
 *  -import                             perform the import form the database
 *  -export                             perform the export to the database
 *  -verbose                            write configuration messages to the log file
 *  -debug                              write debug messages to the log file
 *  -param "var=value"                  defines a parameter for the import query
 *  -frequency {MINUTES|DAY|WEEK|MONTH} set the activation type
 *  -every {&lt;minutes>|&lt;day of month>|&lt;MO,TU,WE,TH,FR,SA,SU>}
 *                                      set the activation interval
 *  -time &lt;time>                        set the activation time
 *  -wait                               wait for the first activation trigger
 * </PRE>
 *
 * An example call looks like this:
 * java ibs.di.XMLFactory -import config.xml
 *
 * different frequency combinations:
 * <PRE>
 *  -frequency minutes -every 5
 *  -frequency day -time 10:30
 *  -frequency week -every TU,SO -time 23:00
 *  -frequency month -every 1 -time 3:00
 * </PRE>
 *
 * @version     $Id: XMLFactory.java,v 1.17 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Michael Steiner (MS)
 ******************************************************************************
 */
public class XMLFactory extends BaseObject implements Runnable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLFactory.java,v 1.17 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * Messages. <BR/>
     */
    static final String MSG_VERSION = "XMLFactory, Version 1.1, (c) 2003 - 2004 by tectum";

    /**
     * The usage message for the factory. <BR/>
     */
    static final String MSG_USAGE   =
        "Usage:\r\n" +
        "java ibs.di.XMLFactory\r\n" +
        "   {-import|-export} <config file>\r\n" +
        "   [-debug] [-verbose]\r\n" +
        "   [-param \"var=value\"]\r\n" +
        "   [-frequency {MINUTES|DAY|WEEK|MONTH}]\r\n" +
        "   [-every {<minutes>|<day of month>|<MO,TU,WE,TH,FR,SA,SU>}]\r\n" +
        "   [-time <time>]\r\n" +
        "   [-wait]\r\n";

    /**
     * Message for the factory command line menu. <BR/>
     */
    public static final String MSG_MENU =
        ">>> Menu:\r\n" +
        "'a' + <enter> ... print date of last and next activation.\r\n" +
        "'s' + <enter> ... print factory settings.\r\n" +
        "'h' + <enter> ... print factory usage.\r\n" +
        "'x' + <enter> ... terminate the factory.";

    /**
     * Message: factory is sleeping. <BR/>
     */
    static final String MSG_SLEEPING                = "The Factory is sleeping...";

    /**
     * Message: invalid parameter. <BR/>
     */
    static final String MSG_INVALID_PARAMETER       = "Invalid parameter: ";
    /**
     * Message: invalid query parameter. <BR/>
     */
    static final String MSG_INVALID_QUERY_PARAMETER = "Invalid query parameter: ";
    /**
     * Message: invalid option. <BR/>
     */
    static final String MSG_INVALID_OPTION          = "Invalid option: ";
    /**
     * Message: invalid argument value for EVERY. <BR/>
     */
    static final String MSG_INVALID_EVERY           = "Invalid every argument: ";
    /**
     * Message: invalid argument value for FREQUENCY. <BR/>
     */
    static final String MSG_INVALID_FREQUENCY       = "Invalid frequency argument: ";
    /**
     * Message: invalid time argument. <BR/>
     */
    static final String MSG_INVALID_TIME            = "Invalid time argument: ";

    // Command line options
    /**
     * Command line option: IMPORT. <BR/>
     */
    static final String OPT_IMPORT      = "-IMPORT";
    /**
     * Command line option: EXPORT. <BR/>
     */
    static final String OPT_EXPORT      = "-EXPORT";
    /**
     * Command line option: PARAM. <BR/>
     */
    static final String OPT_PARAM       = "-PARAM";
    /**
     * Command line option: VERBOSE. <BR/>
     */
    static final String OPT_VERBOSE     = "-VERBOSE";
    /**
     * Command line option: DEBUG. <BR/>
     */
    static final String OPT_DEBUG       = "-DEBUG";
    /**
     * Command line option: VALIDATE. <BR/>
     */
    static final String OPT_VALIDATE    = "-VALIDATE";
    /**
     * Command line option: FREQUENCY. <BR/>
     */
    static final String OPT_FREQUENCY   = "-FREQUENCY";
    /**
     * Command line option: TIME. <BR/>
     */
    static final String OPT_TIME        = "-TIME";
    /**
     * Command line option: EVERY. <BR/>
     */
    static final String OPT_EVERY       = "-EVERY";
    /**
     * Command line option: WAIT. <BR/>
     */
    static final String OPT_WAIT        = "-WAIT";

    // Frequency modifiers:
    /**
     * Frequency modifier: MINUTES. <BR/>
     */
    static final String FREQUENCY_MINUTES = "MINUTES";
    /**
     * Frequency modifier: DAY. <BR/>
     */
    static final String FREQUENCY_DAY     = "DAY";
    /**
     * Frequency modifier: WEEK. <BR/>
     */
    static final String FREQUENCY_WEEK    = "WEEK";
    /**
     * Frequency modifier: MONTH. <BR/>
     */
    static final String FREQUENCY_MONTH   = "MONTH";

    // Frequency types:
    /**
     * Frequency type: MINUTES. <BR/>
     */
    static final int FREQUENCYTYPE_MINUTES = 0;
    /**
     * Frequency type: DAY. <BR/>
     */
    static final int FREQUENCYTYPE_DAY     = 1;
    /**
     * Frequency type: WEEK. <BR/>
     */
    static final int FREQUENCYTYPE_WEEK    = 2;
    /**
     * Frequency type: MONTH. <BR/>
     */
    static final int FREQUENCYTYPE_MONTH   = 3;
    /**
     * Frequency type: NONE. <BR/>
     */
    static final int FREQUENCYTYPE_NONE    = 4;

    // Weekdays:
    /**
     * Weekday: MONDAY. <BR/>
     */
    static final String WEEKDAY_MONDAY    = "MO";
    /**
     * Weekday: TUESDAY. <BR/>
     */
    static final String WEEKDAY_TUESDAY   = "TU";
    /**
     * Weekday: WEDNESDAY. <BR/>
     */
    static final String WEEKDAY_WEDNESDAY = "WE";
    /**
     * Weekday: THURSDAY. <BR/>
     */
    static final String WEEKDAY_THURSDAY  = "TH";
    /**
     * Weekday: FRIDAY. <BR/>
     */
    static final String WEEKDAY_FRIDAY    = "FR";
    /**
     * Weekday: SATURDAY. <BR/>
     */
    static final String WEEKDAY_SATURDAY  = "SA";
    /**
     * Weekday: SUNDAY. <BR/>
     */
    static final String WEEKDAY_SUNDAY    = "SU";

    // factory parameters:
    /**
     * Factory parameter: import. <BR/>
     */
    private boolean doImport = false;
    /**
     * Factory parameter: export. <BR/>
     */
    private boolean doExport = false;
    /**
     * Factory parameter: verbose. <BR/>
     */
    private boolean isVerbose = false;
    /**
     * Factory parameter: debug. <BR/>
     */
    private boolean isDebug = false;
    /**
     * Factory parameter: validate. <BR/>
     */
    private boolean isValidate = false;
    /**
     * Factory parameter: configFile. <BR/>
     */
    private String configFile = null;
    /**
     * Factory parameter: query parameters. <BR/>
     */
    private Vector<String> queryParams = new Vector<String> ();

    /**
     * Factory parameter: frequency type. <BR/>
     */
    private int frequencyType = XMLFactory.FREQUENCYTYPE_NONE;
    /**
     * frequency to start the factory in minutes. <BR/>
     */
    private int everyMinutes = 0;

    /**
     * day of the month to start the factory (1..31). <BR/>
     */
    private int everyDay = 1;

    /**
     * array that represents the days of the week. <BR/>
     * for every day set [SU|MO|TU|WE|TH|FR|SA] the corresponding
     * array field is set. (SU sets everyWeek[0] = true). <BR/>
     */
    private boolean[] everyWeekday =
    {
        false, false, false, false, false, false, false,
    };

    /**
     * time to start the factory. <BR/>
     */
    private GregorianCalendar everyTime = new GregorianCalendar ();
    /**
     * flag to indicate that a weekday filter has been set. <BR/>
     */
    private boolean isWeekdaySet = false;
    /**
     * flag to wait before first activation. <BR/>
     * if set to false the factory will be activated instantly
     * after start ignoring the frequency settings. <BR/>
     */
    private boolean isWait = false;

    /**
     * time at which to start the agent. <BR/>
     * this property holds the setting in the arguments. <BR/>
     */
    private String timeStr = "";

    /**
     * value to start the agent. <BR/>
     * can be in minutes (1 ... one minute)
     * or reference a day of the month [1..31]
     * or it can be a weekday [MO|TU|WE|TH|FR|SA|SU].
     * this property holds the setting in the arguments. <BR/>
     */
    private String everyStr = "";

    /**
     * type of frequency to start the agent. <BR/>
     * allowed values are [MINUTES|DAY|WEEK|MONTH].
     * this property holds the setting in the arguments. <BR/>
     */
    private String frequencyStr = "";

    /**
     *  Date of last activation. <BR/>
     */
    private Date lastActivationDate;

    /**
     *  Date of scheduled next activation. <BR/>
     */
    private Date nextActivationDate;

    /**
     * Starting year. <BR/>
     */
    private static final String STARTING_YEAR = "01.01.1970 ";


    /**************************************************************************
     * This method is the program entry point for the java VM. <BR/>
     *
     * @param   args    The command line parameters.
     */
    public static void main (String[] args)
    {
        new XMLFactory ().doMain (args);
    } // main


    /**************************************************************************
     * This method evaluates all command line parameters and activates
     * the XMLDBFactory. <BR/>
     *
     * @param   args    The command line parameters.
     */
    public void doMain (String[] args)
    {
        // print the version message
        this.print (XMLFactory.MSG_VERSION);

        // if no parameters are given show the usage message
        if (args.length < 1)
        {
            this.print (XMLFactory.MSG_USAGE);
            System.exit (-1);
        } // if (args.length < 1)

        boolean paramOption = false;
        boolean paramFrequency = false;
        boolean paramTime = false;
        boolean paramEvery = false;

        // walk thru the parameter array
        for (int i = 0; i < args.length; i++)
        {
            // get the parameter from the array
            String arg = args[i];

            if (paramOption)
            {
                // the query parameter must have the form:
                // 'variable=value'
                if (arg.indexOf ('=') < 0)
                {
                    this.print (XMLFactory.MSG_INVALID_QUERY_PARAMETER + arg);
                    System.exit (-1);
                } // if
                // store the parameter.
                this.queryParams.addElement (arg);
                paramOption = false;
            } // if (paramOption)
            else if (paramFrequency)
            {
                this.frequencyStr = arg;
                paramFrequency = false;
            } // if (paramFrequency)
            else if (paramTime)
            {
                this.timeStr = arg;
                paramTime = false;
            } // if (paramTime)
            else if (paramEvery)
            {
                this.everyStr = arg;
                paramEvery = false;
            } // if (paramEvery)
            // a option starts with '-'
            else if (arg.startsWith ("-"))
            {
                if (arg.equalsIgnoreCase (XMLFactory.OPT_IMPORT))
                {
                    this.doImport = true;
                } // if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_EXPORT))
                {
                    this.doExport = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_VERBOSE))
                {
                    this.isVerbose = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_DEBUG))
                {
                    this.isDebug = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_VALIDATE))
                {
                    this.isValidate = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_PARAM))
                {
                    paramOption = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_FREQUENCY))
                {
                    paramFrequency = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_TIME))
                {
                    paramTime = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_EVERY))
                {
                    paramEvery = true;
                } // else if
                else if (arg.equalsIgnoreCase (XMLFactory.OPT_WAIT))
                {
                    this.isWait = true;
                } // else if
                else
                {
                    this.print (XMLFactory.MSG_INVALID_OPTION + arg);
                    System.exit (-1);
                } // unknown option
            } // if a option
            else if (this.configFile == null)
            {
                // the first parameter is the configuration file.
                this.configFile = arg;
            } // else if (this.configFile == null)
            else
            {
                // invalid parameter given
                this.print (XMLFactory.MSG_INVALID_PARAMETER + arg);
                System.exit (-1);
            } // else invalid parameter
        } // for i

        // check the activation time parameters
        if (!this.checkParameters ())
        {
            System.exit (-1);
        } // if (!checkParameters ())

        // if no activation time parameters are given
        // the action is perform immediately.
        if (this.frequencyType == XMLFactory.FREQUENCYTYPE_NONE)
        {
            // perform the action and exit with the return code.
            System.exit (this.performAction ());
        } // if (this.frequencyType == this.FREQUENCYTYPE_NONE)
        else
        {
            // if activation time parameters are given
            // a thread is started to trigger the XMLDBFactory.
            this.startDelayed ();
        } // else if (this.frequencyType == this.FREQUENCYTYPE_NONE)

        System.exit (0);
    } // doMain


    /**************************************************************************
     * Start the process delayed. <BR/>
     */
    public void startDelayed ()
    {
        // if activation time parameters are given
        // a thread is started to trigger the XMLDBFactory.
        Thread factoryThread = new Thread (this, "XML/DB-Factory");
        // do we need to start the agent as a demon thread???
        factoryThread.setDaemon (false);
        // now start the thread
        factoryThread.start ();

        // the main thread handles the user interaction.
        try
        {
            InputStreamReader reader =  new InputStreamReader (System.in);
            char i;
            while ((i = (char) reader.read ()) != 'x')
            {
                // check whether to print the agent setting
                if (i == 's' || i == 'S')
                {
                    this.printSettings ();
                } // if (i == 's' || i == 'S')
                else if (i == 'a' || i == 'A')
                {
                    this.print ("> " + "Last activation" + " " +
                           DateTimeHelpers.dateTimeToString (this.lastActivationDate) +
                           " ...");
                    this.print ("> " + "Next activation" + " " +
                           DateTimeHelpers.dateTimeToString (this.nextActivationDate) +
                           " ...");
                } // else if (i == 'a' || i == 'A')
                else if (i != '\r' && i != '\n')
                {
                    this.printMenu ();
                } // if (i == 'h' || i == 'H')
            } // while
        } // try
        catch (IOException e)
        {
            this.print ("\r\n" + e.toString ());
        } // catch
    } // startDelayed


    /**************************************************************************
     * This method startes the action. <BR/>
     *
     * @return  -1 on error otherwise 0.
     */
    private int performAction ()
    {
        XMLDBFactory xmlFactory = new XMLDBFactory ();
        xmlFactory.traceVerbose = this.isVerbose;
        xmlFactory.traceConfig = this.isVerbose;
        xmlFactory.traceQuery = this.isDebug;
        xmlFactory.traceDBFields = this.isDebug;
        xmlFactory.traceMapping = this.isDebug;

        try
        {
            // open the configuration file and read the content.
            FileInputStream cfgFile = new FileInputStream (this.configFile);
            byte [] cfg = new byte [cfgFile.available ()];
            cfgFile.read (cfg);

            // set the factory configuration.
            if (!xmlFactory.setConfiguration (new String (cfg), null))
            {
                this.print ("Configuration error!");
                return -1;
            } // if (!xmlFactory.setConfiguration (new String(cfg), System.out))

            // perform the import
            if (this.doImport)
            {
                if (!xmlFactory.performImport (this.queryParams, null))
                {
                    this.print ("Import error!");
                    return -1;
                } // if (!xmlFactory.performImport (queryParams, null))
            } // if (doImport)

            // perform the export
            if (this.doExport)
            {
                if (!xmlFactory.performExport (this.isValidate, null))
                {
                    this.print ("Export error!");
                    return -1;
                } // if (!xmlFactory.performExport (null))
            } // if (doExport)
        } // try
        catch (IOException e)
        {
            this.print ("Can't read configuration file '" + e.getMessage () + "'");
            return -1;
        } // catch

        this.print ("done.");
        return 0;
    } // performAction

    /**************************************************************************
     * Checks command line parameters. <BR/>
     *
     * @return  true if parameter ok or false otherwise
     */
    public boolean checkParameters ()
    {
        if (this.configFile == null)
        {
            this.print ("Configuration file missing.");
            return false;
        } // if (configFile == null)

        if (this.frequencyStr.length () > 0)
        {
            this.frequencyType = -1;
            // check frequency type
            if (this.frequencyStr.equalsIgnoreCase (XMLFactory.FREQUENCY_MINUTES))
            {
                // set frequency type to minutes
                this.frequencyType = XMLFactory.FREQUENCYTYPE_MINUTES;
                // check minutes value
                try
                {
                    this.everyMinutes = Integer.parseInt (this.everyStr);
                    // the value for everyMinutes must be at least 5 minutes
                    if (this.everyMinutes < 1)
                    {
                        this.everyMinutes = 1;
                    } // if
                } // try
                catch (NumberFormatException e)
                {
                    this.print (XMLFactory.MSG_INVALID_EVERY + this.everyMinutes);
                    return false;
                } // catch (NumberFormatException e)
            } // if (this.frequencyStr.equalsIgnoreCase ("MINUTES"))
            else if (this.frequencyStr.equalsIgnoreCase (XMLFactory.FREQUENCY_DAY))
            {
                // set frequency type to minutes
                this.frequencyType = XMLFactory.FREQUENCYTYPE_DAY;
                // check time value
                String dateTimeStr = XMLFactory.STARTING_YEAR + this.timeStr;
                try
                {
                    this.everyTime.setTime (new SimpleDateFormat ()
                        .parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    this.print (XMLFactory.MSG_INVALID_EVERY + this.everyMinutes);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("DAY"))
            else if (this.frequencyStr.equalsIgnoreCase (XMLFactory.FREQUENCY_WEEK))
            {

//print ("checking day name: " + this.everyStr);

                // set frequency type to minutes
                this.frequencyType = XMLFactory.FREQUENCYTYPE_WEEK;
                String everyStrUpper = this.everyStr.toUpperCase ();
                // set the weekdays
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_MONDAY) > -1)
                {
                    this.everyWeekday [1] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("MO") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_TUESDAY) > -1)
                {
                    this.everyWeekday [2] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TU") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_WEDNESDAY) > -1)
                {
                    this.everyWeekday [3] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("WE") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_THURSDAY) > -1)
                {
                    this.everyWeekday [4] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("TH") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_FRIDAY) > -1)
                {
                    this.everyWeekday [5] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("FR") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_SATURDAY) > -1)
                {
                    this.everyWeekday [6] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("SA") > -1)
                if (everyStrUpper.indexOf (XMLFactory.WEEKDAY_SUNDAY) > -1)
                {
                    this.everyWeekday [0] = true;
                    this.isWeekdaySet = true;
                } // if (this.everyStr.toUpperCase.indexOf ("SU") > -1)
                // check time value
                String dateTimeStr = XMLFactory.STARTING_YEAR + this.timeStr;
                try
                {
                    this.everyTime.setTime (new SimpleDateFormat ().parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    this.print (XMLFactory.MSG_INVALID_TIME + this.timeStr);
                    return false;
                } // catch (ParseException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("WEEK"))
            else if (this.frequencyStr.equalsIgnoreCase (XMLFactory.FREQUENCY_MONTH))
            {
                // set frequency type to minutes
                this.frequencyType = XMLFactory.FREQUENCYTYPE_MONTH;
                // check the day of the month
                // check time value
                String dateTimeStr = XMLFactory.STARTING_YEAR + this.timeStr;
                try
                {
                    this.everyDay = Integer.parseInt (this.everyStr);
                    if (this.everyDay < 1)
                    {
                        this.everyDay = 1;
                    } // if
                    else if (this.everyDay > 31)
                    {
                        this.everyDay = 31;
                    } // else if
                    // try to create the date
                    this.everyTime.setTime (new SimpleDateFormat ().parse (dateTimeStr));
                } // try
                catch (ParseException e)
                {
                    this.print (XMLFactory.MSG_INVALID_EVERY + this.everyStr);
                    return false;
                } // catch (ParseException e)
                catch (NumberFormatException e)
                {
                    this.print (XMLFactory.MSG_INVALID_TIME + this.timeStr);
                    return false;
                } // catch (NumberFormatException e)
            } // else if (this.frequencyStr.equalsIgnoreCase ("MONTH"))
        } // else if (!this.frequencyStr.length () == 0)
        if (this.frequencyType < 0)
        {
            this.print (XMLFactory.MSG_INVALID_FREQUENCY + this.frequencyStr);
            return false;
        } // else if (this.frequencyType < 0)

        return true;
    } // checkParameters



    /**************************************************************************
     * Implements the run method used for threads.
     * the date and time of the next activation is calculated and the factory
     * is send to sleep until the next activation date. <BR/>
     */
    public void run ()
    {
        boolean isFirstActivation = true;

        // endless loop for agent
        while (true)
        {
            // check if this is the first activation and if the agent
            // should be activated or should wait first
            // this can be switched on and off through the -WAIT parameter
            // in the command line
            if (!isFirstActivation || !this.isWait)
            {
                // activate the agent
                this.print (">>> " + "starting...");
                this.performAction ();
            } // if (! (isFirstActivation && this.isWait))
            isFirstActivation = false;

            // calculate the time the thread can sleep until next activation
            this.lastActivationDate = new Date ();
            this.nextActivationDate = this.getNextActivationDate ();

            // calculate the time to sleep for the agent
            long timeToSleep = this.nextActivationDate.getTime () -
                               this.lastActivationDate.getTime ();
            this.print ("> " + "Next activation at" + " " +
                   DateTimeHelpers.dateTimeToString (this.nextActivationDate) + " ...");
            // print the menu keys
            this.printMenu ();

            try
            {
                // send the thread to sleep ...
                Thread.sleep (timeToSleep);
            } // try
            catch (InterruptedException e)
            {
                System.exit (-1);
            } // catch
        } // while
    } // run

    /**************************************************************************
     * Calculates the next date to activate the factory. <BR/>
     *
     * @return      the next date to activate the factory
     */
    private Date getNextActivationDate ()
    {
        GregorianCalendar actualCalendar = new GregorianCalendar ();
        GregorianCalendar newCalendar = new GregorianCalendar ();

        // determine if the import ist done frequently or at a certain time
        switch (this.frequencyType)
        {
            case XMLFactory.FREQUENCYTYPE_MINUTES: // type is MINUTES
                newCalendar.add (Calendar.MINUTE, this.everyMinutes);
                break;
            case XMLFactory.FREQUENCYTYPE_DAY: // type is DAY
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.DAY_OF_MONTH, 1);
                } // if (newCalendar.after (actualCalendar))
                break;
            case XMLFactory.FREQUENCYTYPE_WEEK: // type is WEEK
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.DAY_OF_MONTH, 1);
                } // if (newCalendar.after (actualCalendar))#
                // check if a weekday filter has been set
                if (this.isWeekdaySet)
                {
                    // now test is this day is one of the weekdays that have been set
                    // BB HINT: because there must has been a weekday set we can assume
                    // that this loop comes to an end
                    while (!this.everyWeekday [newCalendar.get (Calendar.DAY_OF_WEEK) - 1])
                    {
                        newCalendar.add (Calendar.DATE, 1);
                    } // while (!this.everyWeekday [newCalendar.get(Calendar.DAY_OF_WEEK)-1])
                } // if (this.isWeekdaySet)
                break;
            case XMLFactory.FREQUENCYTYPE_MONTH: // type is MONTH
                // set the time
                newCalendar.set (Calendar.HOUR, this.everyTime.get (Calendar.HOUR));
                newCalendar.set (Calendar.MINUTE, this.everyTime.get (Calendar.MINUTE));
                newCalendar.set (Calendar.AM_PM, this.everyTime.get (Calendar.AM_PM));
                // set the day of the month
                newCalendar.set (Calendar.DAY_OF_MONTH, this.everyDay);
                // check if we need to add a day
                if (!actualCalendar.before (newCalendar))
                {
                    newCalendar.add (Calendar.MONTH, 1);
                } // if (newCalendar.after (actualCalendar))
                break;
            default: // nothing to do
        } // switch
        return newCalendar.getTime ();
    } // getNextActivationDate


    /**************************************************************************
     * Prints the settings of the factory. <BR/>
     */
    private void printSettings ()
    {
        // nothing to do
    } // printSettings


    /**************************************************************************
     * Prints the menu keys to control the agent. <BR/>
     */
    private void printMenu ()
    {
        this.print (XMLFactory.MSG_MENU);
        this.print ("> " + XMLFactory.MSG_SLEEPING);
    } // printMenu


    /**************************************************************************
     * Prints factory messages to System.err. <BR/>
     *
     * @param   msg     The message.
     */
    private void print (String msg)
    {
        System.err.println ("XMLFactory: " + msg);
    } // print

} // class XMLFactory
