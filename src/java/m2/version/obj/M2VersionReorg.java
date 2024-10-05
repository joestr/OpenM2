/*
 * Class: M2VersionReorg.java
 */

// package:
package m2.version.obj;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.di.ValueDataElement;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.obj.reorg.Reorg;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.file.FileHelpers;

import m2.version.FileVersion_01;
import m2.version.Version_01;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type M2VersionReorg. <BR/>
 * The class handles the reorganisation functions for ProTime. <BR/>
 *
 * @version     $Id: M2VersionReorg.java,v 1.8 2009/08/28 16:47:06 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 20051007
 ******************************************************************************
 */
public class M2VersionReorg extends Reorg
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2VersionReorg.java,v 1.8 2009/08/28 16:47:06 kreimueller Exp $";


    /**
     * argument: Actualize the versions. <BR/>
     */
    private static final String ARG_ACTUALIZEVERSIONS = "actualizeVersions";

    /**
     * Field name: file name. <BR/>
     */
    private static final String FLD_FILENAME = "Dateiname";

    /**
     * activate debugging. <BR/>
     */
    public boolean p_isDebug = false;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class M2VersionReorg. <BR/>
     */
    public M2VersionReorg ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // M2VersionReorg


    /**************************************************************************
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     */
    public M2VersionReorg (OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
        this.initObject (oid, user, null, null, null);
    } // M2VersionReorg



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Perform reorganisation. <BR/>
     *
     * @param   isSimulate  Shall the reorganisation be really performed or
     *                      just simulated?
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected final void performReorg (boolean isSimulate)
        throws NoAccessException, NameAlreadyGivenException
    {
        // check if the time recording times shall be recumulated:
        if (this.env.getBoolParam (M2VersionReorg.ARG_ACTUALIZEVERSIONS) ==
            IOConstants.BOOLPARAM_TRUE)
        {
            this.startActualizeVersions (isSimulate);
        } // if
    } // performReorg


    /**************************************************************************
     * Start actualizing the versions. <BR/>
     * The operation can be started in simulation mode that does not
     * change any values. <BR/>
     *
     * @param   isSimulation    Indicates whether the simulation mode is turned
     *                          on.
     */
    public void startActualizeVersions (boolean isSimulation)
    {
        Vector<VersionData> versionList = new Vector<VersionData> (); // the version list
        int errorCounter = 0;           // error counter

        this.env.write (IE302.TAG_NEWLINE + "<DIV ALIGN=\"LEFT\">");
        this.env.write ("<FONT SIZE=\"2\">");

        if (isSimulation)
        {
            this.env.write ("&gt;&gt;&gt; Simulation von Aktulisierung der Versionen wird gestartet ..." + IE302.TAG_NEWLINE);
        } // if
        else
        {
            this.env.write ("&gt;&gt;&gt; Aktualisierung der Versionen wird gestartet ..." + IE302.TAG_NEWLINE);
        } // else

        // get the version data:
        errorCounter += this.getVersionData (versionList);

        // check if we found some version data:
        if (versionList != null && versionList.size () > 0)
                                        // at least one object?
        {
            // store the data in task resources:
            errorCounter += this.updateVersions (versionList, isSimulation);
        } // if at least one object

        if (isSimulation)
        {
            this.env.write ("<P/>... Simulation beendet!<P/>");
        } // if
        else                            // not a simulation
        {
            this.env.write ("<P/>... Aktualisierung der Versionen beendet!<P/>");
            // store the last date of recumulation and save the object:
            this.dataElement.changeValue ("Letzte Aktualisierung der Versionen",
                SQLHelpers.getDateTimeString (new Date ()).toString ());
        } // else

        // any errors found?
        if (errorCounter > 0)
        {
            this.env.write ("<B>Hinweis</B>: " + errorCounter +
                " Fehler gefunden!!!<P/>");
        } // if
        else
        {
            this.env.write ("<B>Hinweis</B>: Keine Fehler gefunden!<P/>");
        } // else

        this.env.write ("</FONT></DIV>");
    } // startActualizeVersions


    /**************************************************************************
     * Computes a Vector which contains all versions which shall be actualized.
     * <BR/>
     * The versions are added to the versionList vector.
     *
     * @param   versionList List where the versions shall be added.
     *                      If no versions where found the vector stays
     *                      unchanged.
     *
     * @return  The number of errors which occurred in the method.
     */
    private int getVersionData (Vector<VersionData> versionList)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        StringBuffer queryStr = null;   // the SQL String to select all tuples
        int rowCount = -1;              // row counter
        OID majorOid = null;            // the oid of the actual major object
        OID versOid = null;             // the oid of the actual version
        VersionData versData = null;    // data of actual task resource
        OID lastMajorOid = null;        // the oid of the last major object
        FileVersion_01 majorObj = null; // the actual major object
        int errorCounter = 0;           // error counter
        boolean isIgnoreObject = false; // shall the object be ignored?

        this.env.write ("<LI>Ermittlung der zu aktualisierenden Versionen ...</LI>" + IE302.TAG_NEWLINE);

        // create the query string:
        // Get the oids of all versions which do not contain copies of their
        // major objects and all master versions.
        // Also get the oids of the major objects. A major object is an object
        // being two (2) levels aove the version. This is because versions
        // should be within the VersionContainer tab of the major object.
        queryStr = new StringBuffer ()
            .append ("SELECT omajor.oid AS majorOid, overs.oid AS versOid")
            .append (" FROM ibs_Object overs, dbm_m2version vers,")
                .append (" ibs_Object omajor")
            .append (" WHERE overs.oid = vers.oid")
                .append (" AND overs.state = 2")
                .append (" AND overs.containerOid2 = omajor.oid")
                .append (" AND (vers.m_ismaster = ")
                    .append (SQLConstants.BOOL_TRUE)
                .append (" OR NOT EXISTS")
                    .append (" (")
                    .append (" SELECT oid")
                    .append (" FROM ibs_Object")
                    .append (" WHERE containerId = overs.oid")
                        .append (" AND state = ").append (States.ST_ACTIVE)
                        .append (" AND tVersionId = omajor.tVersionId")
                    .append ("))")
            .append (" ORDER BY overs.posNoPath ASC");

        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount > 0)
            {
                while (!action.getEOF ())
                {
                    // get the oid of the major object:
                    majorOid = SQLHelpers.getQuOidValue (action, "majorOid");
                    // get the oid of the version object:
                    versOid = SQLHelpers.getQuOidValue (action, "versOid");

                    // check if we have the same major object as in the
                    // last loop step:
                    if (!majorOid.equals (lastMajorOid))
                                        // another major object?
                    {
                        try
                        {
                            // get the major object:
                            majorObj = (FileVersion_01) BOHelpers.getObject (
                                majorOid, this.env, false, false, false);
                        } // try
                        catch (ClassCastException e)
                        {
                            this.env.write ("<LI><B>Hauptobjekt mit Oid " +
                                majorOid +
                                " ist keine Subklasse von FileVersion_01." +
                                " Das Objekt wird ignoriert.</B></LI>" + IE302.TAG_NEWLINE);
                            isIgnoreObject = true;
                        } // catch
                    } // if another major object

                    // check if the object shall be ignored:
                    if (!isIgnoreObject)
                    {
                        // create a new version data object:
                        versData = new VersionData (versOid, majorObj);
                        // add the data to the vector:
                        versionList.add (versData);

                        // remember the oid:
                        lastMajorOid = majorOid;
                    } // if
                    else
                    {
                        isIgnoreObject = false;
                    } // else

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
            } // if

            // end transaction:
            action.end ();

            // check if we found some versions:
            if (versionList.size () > 0)
            {
                this.env.write ("<LI>" + versionList.size () +
                    " Versionen gefunden.</LI>" + IE302.TAG_NEWLINE);
            } // if
            else
            {
                this.env
                    .write ("<LI><B>Hinweis</B>: Keine zu aktualisierenden Versionen gefunden!</LI>");
            } // else
        } // try
        catch (DBError e)
        {
            // show the message:
            IOHelpers.showMessage (
                "Exception in M2VersionReorg in getVersionData", e, this.app,
                this.sess, this.env, true);
            this.env
                .write ("<LI>Fehler beim Ermitteln der zu aktualisierenden Versionen: " +
                    e + "</LI>" + IE302.TAG_NEWLINE);
            errorCounter++;
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // return the error counter:
        return errorCounter;
    } // getVersionData


    /**************************************************************************
     * Actualize the versions. <BR/>
     *
     * @param   versionList     List of versions which shall be actualized.
     * @param   isSimulation    Indicates whether the simulation mode is turned
     *                          on.
     *
     * @return  The number of errors which occurred in the method.
     */
    private int updateVersions (Vector<VersionData> versionList, boolean isSimulation)
    {
        Version_01 version = null;      // the actual version object
        OID oid = null;                 // oid of the actual object
        int errorCounter = 0;           // error counter
        int objCounter = 0;             // object counter

        // loop through all version data:
        for (Iterator<VersionData> iter = versionList.iterator (); iter.hasNext ();)
        {
            // get the version data:
            VersionData versData = iter.next ();
            oid = versData.p_oid;
            // increment object counter:
            objCounter++;

/*
            this.env.write ("<LI>Erzeuge Objekt-Instanz für '" +
                String.valueOf (oid) + "' ...</LI>" + IE302.TAG_NEWLINE);
*/

            // get the object:
            version = (Version_01) BOHelpers.getObject (
                oid, this.env, false, false, false);

            // check if we got the object:
            if (version != null)
            {
                // actualize the version:
                // check if the version is the master:
                if (version.isMasterVersion ())
                {
                    // copy the file from the version to the major object:
                    this.copyFile (version, versData.p_majorObj, isSimulation,
                        objCounter);
                } // if

                // ensure that the version contains a copy of the major object:
                if (!version.hasVersionObject ())
                {
                    this.env.write ("<LI>[" + objCounter + "] Bearbeite " +
                        version.typeName + " '" + "<A HREF=\"" +
                        IOHelpers.getShowObjectJavaScriptUrl (String
                            .valueOf (version.oid)) + "\"><CODE>" +
                        versData.p_majorObj.name + "[" + version.name + "]" +
                        "</CODE>' (<CODE>" + String.valueOf (version.oid) +
                        "</CODE>)</A> ...</LI>" + IE302.TAG_NEWLINE);

                    if (!isSimulation)
                    {
                        // create a copy of the major object below the version:
                        versData.p_majorObj.createVersionObject (version);
                    } // if
                } // if
                else
                {
                    this.env.write ("<LI>[" + objCounter + "] " +
                        (version.isMasterVersion () ? "Master " : "") +
                        version.typeName + " '" + "<A HREF=\"" +
                        IOHelpers.getShowObjectJavaScriptUrl (String
                            .valueOf (version.oid)) + "\"><CODE>" +
                        versData.p_majorObj.name + "[" + version.name + "]" +
                        "</CODE>' (<CODE>" + String.valueOf (version.oid) +
                        "</CODE>)</A> wurde bereits aktualisiert.</LI>" + IE302.TAG_NEWLINE);

                } // else
            } // if
            else
            {
                this.env.write (
                    "<LI>FEHLER: Objekt-Instanz für '" + String.valueOf (oid) +
                    "' konnte nicht erzeugt werden.</LI>" + IE302.TAG_NEWLINE);
                errorCounter++;
            } // else
        } // for iter

        this.env.write (objCounter + " Objekte bearbeitet." + IE302.TAG_NEWLINE);

        // return the error counter:
        return errorCounter;
    } // updateVersions


    /**************************************************************************
     * Copy a file from a version to another object. <BR/>
     * The other object must be a subclass of FileVersion_01 and have a VALUE
     * called "Dateiname". If this field is empty, the file is copied from
     * the version to the target object. Otherwise the target object stays
     * unchanged.
     *
     * @param   versionObj      The version object from which to get the file.
     * @param   targetObj       The object into which to copy the file.
     * @param   isSimulation    Indicates whether the simulation mode is turned
     *                          on.
     * @param   objCounter      The object counter.
     *
     * @return  The number of errors which occurred in the method.
     */
    private int copyFile (Version_01 versionObj, FileVersion_01 targetObj,
                          boolean isSimulation, int objCounter)
    {
        int errorCounter = 0;           // error counter
        ValueDataElement fileField = null; // field which contains the file data
        String fileName = null;         // name of the file
        String sourcePath = null;       // path from where to copy the file
        String targetPath = null;       // path to copy the file to

        // check if the target object has a "Dateiname" field:
        if ((BusinessObject) versionObj != (BusinessObject) targetObj &&
            versionObj != null &&
            targetObj != null &&
            versionObj.oid != null &&
            !versionObj.oid.equals (targetObj.oid) &&
            (fileField = targetObj.dataElement
                .getValueElement (M2VersionReorg.FLD_FILENAME)) != null)
        {
            // check if the file entry within the target object is empty:
            if (fileField.value == null || fileField.value.length () == 0 ||
                fileField.p_size == 0)
            {
                this.env.write ("<LI>[" + objCounter + "] Kopiere Datei von " +
                    versionObj.typeName + " '" +
                    "<A HREF=\"" + IOHelpers.getShowObjectJavaScriptUrl (
                        String.valueOf (versionObj.oid)) + "\"><CODE>" +
                    targetObj.name + "[" + versionObj.name + "]" +
                    "</CODE>' (<CODE>" + String.valueOf (versionObj.oid) +
                    "</CODE>)</A> in das Hauptobjekt...</LI>" + IE302.TAG_NEWLINE);

                // get the file name:
                fileName = versionObj.dataElement
                    .getImportValue (M2VersionReorg.FLD_FILENAME);
                // get the source path:
                sourcePath = BOHelpers.getFilePath (versionObj.oid);
                // get the target path:
                targetPath = BOHelpers.getFilePath (targetObj.oid);

                if (!isSimulation)
                {
                    // copy the file from the version to the target object:
                    FileHelpers.copyFile (sourcePath + fileName,
                        targetPath + fileName);
                    // set file data within target object:
                    targetObj.dataElement.setExportFileValue (
                        M2VersionReorg.FLD_FILENAME, fileName, targetPath,
                        false);

                    try
                    {
                        // store the target object:
                        targetObj.performChange (Operations.OP_NONE);
                    } // try
                    catch (NoAccessException e)
                    {
                        // should not possible because of OP_NONE
                        // display error message:
                        IOHelpers.showMessage (e, this.app, this.sess, this.env,
                            true);
                    } // catch
                    catch (NameAlreadyGivenException e)
                    {
                        // should not occur for objects of this class
                        // display error message:
                        IOHelpers.showMessage (e, this.app, this.sess, this.env,
                            true);
                    } // catch
                } // if
            } // if
        } // if
        else
        {
            this.env.write ("<LI>FEHLER: Objekt '" + targetObj.name +
                "' hat kein Feld \"" + M2VersionReorg.FLD_FILENAME +
                "\".</LI>" + IE302.TAG_NEWLINE);
            errorCounter++;
        } // else

        // return the error counter:
        return errorCounter;
    } // copyFile


    /**************************************************************************
     * This class handles the relevant data of a version. <BR/>
     *
     * @version     $Id: M2VersionReorg.java,v 1.8 2009/08/28 16:47:06 kreimueller Exp $
     *
     * @author      Klaus Reimüller (KR) 20051007
     **************************************************************************
     */
    private class VersionData
    {
        /**
         * Oid of version. <BR/>
         */
        OID p_oid = null;

        /**
         * Major object where the version belongs to. <BR/>
         */
        FileVersion_01 p_majorObj = null;

        /**
         * The hash code. <BR/>
         */
        private int p_hashCode = Integer.MIN_VALUE;


        /**************************************************************************
         * Creates a VersionData object. <BR/>
         *
         * @param   oid             Oid of the version.
         * @param   majorObj        The major object.
         */
        VersionData (OID oid, FileVersion_01 majorObj)
        {
            this.p_oid = oid;
            this.p_majorObj = majorObj;
        } // VersionData


        /**************************************************************************
         * Compares this VersionData to other VersionData. <BR/>
         * The result is <CODE>true</CODE> if and only if the argument is not
         * <CODE>null</CODE> and is a VersionData object that has the
         * same oid as this object.
         *
         * @param   obj     The object to compare this object with.
         *
         * @return  <CODE>true</CODE> if the VersionData objects are equal;
         *          <CODE>false</CODE> otherwise.
         */
        public boolean equals (Object obj)
        {
            // check for null:
            if (obj == null)            // the other object is null?
            {
                return false;           // not equal
            } // if the other object is null

            if (this == obj)            // the same instance?
            {
                return true;            // always equal
            } // if the same instance

            if (obj instanceof VersionData) // VersionData object?
            {
                VersionData anotherObj = (VersionData) obj;

                // compare oid objects:
                if (this.p_oid == anotherObj.p_oid)
                {
                    return true;
                } // if

                // compare oid values:
                else if (this.p_oid != null)
                {
                    return this.p_oid.equals (anotherObj.p_oid);
                } // if

                // the oids are different:
                return false;
            } // if VersionData object

            // no VersionData object
            return false;               // not equal
        } // equals


        /**********************************************************************
         * Returns a hash code value for the object. <BR/>
         *
         * @return  A hash code value for this object.
         */
        public int hashCode ()
        {
            // check if a valid hash code was set:
            if (this.p_hashCode == Integer.MIN_VALUE)
            {
                // check if the oid is set:
                if (this.p_oid != null)
                {
                    // compute hash code from oid:
                    this.p_hashCode = this.p_oid.hashCode ();
                } // if
            } // if

            // return the result:
            return this.p_hashCode;
        } // hashCode

    } // class VersionData

} // class M2VersionReorg
