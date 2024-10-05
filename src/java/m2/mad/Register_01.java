/*
 * Class: Register_01.java
 * this is a BO created for KW
 * 13.04.2000 CK
 */

// package:
package m2.mad;

// imports:
import ibs.app.AppMessages;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.io.IOConstants;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.FormFieldRestriction;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class shows the form to the Registerlogin for KW with version 01. <BR/>
 *
 * @version     $Id: Register_01.java,v 1.15 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 000413
 ******************************************************************************
 */
public class Register_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Register_01.java,v 1.15 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Region where the company is. <BR/>
     */
    public String[] regions =
    {
        "Großglockner",
        "Mölltal",
        "Oberdrautal",
        "Kärntens Naturarena Gail-, Gitsch-, Lesachtal, Weissensee",
        "Lieser-Maltatal",
        "Millstätter See",
        "Villach-Warmbad/Faaker See/Ossiacher See",
        "Nockberge/Bad Kleinkirchheim",
        "Mittelkärnten",
        "Wörthersee",
        "Rosental",
        "Lavanttal",
        "Südkärnten/Klopeiner See",
//        "Italien",
    }; // regions


    /**
     * Region where the company is. <BR/>
     */
    public String region = null;

    /**
     * MwSt. <BR/>
     */
    public int mwSt = 0;

    /**
     * Street. <BR/>
     */
    public String street = "";

    /**
     * Zip. <BR/>
     */
    public String zip = "";

    /**
     * Town. <BR/>
     */
    public String town = "";

    /**
     * Tel. <BR/>
     */
    public String tel = "";

    /**
     * Fax. <BR/>
     */
    public String fax = "";

    /**
     * Email. <BR/>
     */
    public String email = "";

    /**
     * Homepage. <BR/>
     */
    public String homepage = "";

    /**
     * Name of the Contact of the company. <BR/>
     */
    public String aname = "";


    /**************************************************************************
     * This constructor creates a new instance of the class Register_01.
     * <BR/>
     */
    public Register_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Register_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.name = "Firma";
        this.containerName = "Stammdaten";
        this.description = "";

        // set the class-procedureNames
        this.procCreate = "p_Register_01$createObjects";
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = "";
        int number = -1;

       // get name:
        if ((text = this.env.getParam (BOArguments.ARG_NAME)) != null)
        {
            this.name = text;
        } // if

        // description
        if ((text = this.env.getStringParam (BOArguments.ARG_DESCRIPTION)) != null)
        {
            this.description = text;
        } // if

        // get region:
        if ((text = this.env.getParam (MadArguments.ARG_COMPOWNER)) != null)
        {
            this.region = text;
        } // if

        // get street:
        if ((text = this.env.getParam (MadArguments.ARG_STREET)) != null)
        {
            this.street = text;
        } // if

        // get zip:
        if ((text = this.env.getParam (MadArguments.ARG_ZIP)) != null)
        {
            this.zip = text;
        } // if

        // get town:
        if ((text = this.env.getParam (MadArguments.ARG_TOWN)) != null)
        {
            this.town = text;
        } // if

        // get tel:
        if ((text = this.env.getParam (MadArguments.ARG_TEL)) != null)
        {
            this.tel = text;
        } // if

        // get fax:
        if ((text = this.env.getParam (MadArguments.ARG_FAX)) != null)
        {
            this.fax = text;
        } // if

        // get email:
        if ((text = this.env.getParam (MadArguments.ARG_EMAIL)) != null)
        {
            this.email = text;
        } // if

        // get homepage:
        if ((text = this.env.getParam (MadArguments.ARG_HOMEPAGE)) != null)
        {
            this.homepage = text;
        } // if

        // get mwSt:
        if ((number = this.env.getIntParam (MadArguments.ARG_MWST)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.mwSt = number;
        } // if

        // get name:
        if ((text = this.env.getParam (MadArguments.ARG_NAME)) != null)
        {
            this.aname = text;
        } // if
    } // getParameters


    /***************************************************************************
     * Show the object, i.e. its properties within a form. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  <CODE>true</CODE> if the change form or its frame set was
     *          displayed, <CODE>false</CODE> otherwise.
     */
    public boolean showChangeForm (int representationForm)
    {
        // show the object's data:
        this.performShowChangeForm (representationForm, MadFunctions.FCT_CREATEOBJECTS);
        return true;
    } // showChangeForm


    /***************************************************************************
     * Represent the properties of a Register_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see BusinessObject#showFormProperties
     * @see BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        this.addMessage (table, "Bitte registrieren Sie Ihre Firma!");

        // FirmenName - MUSSFELD!
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_NAME, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);

        this
            .addMessage (
                table,
                "Geben Sie hier die Öffnungszeiten Ihrer Firma ein" +
                " (Vor-, Haupt- und Nachsaison)!");

        // Öffnungszeiten (Beschreibung)
        this.formFieldRestriction = new FormFieldRestriction (false,
            BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), 
            Datatypes.DT_DESCRIPTION, this.description);

        // Region - MUSSFELD! (SELECTIONBOX)
        this.showFormProperty (table, MadArguments.ARG_COMPOWNER, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_COMPOWNER, env),
            Datatypes.DT_SELECT, (this.region != null) ?
                this.region : this.regions[0], this.regions, this.regions, 0);

        // MWST
        this.formFieldRestriction = new FormFieldRestriction (false, 3, 3, "0",
            "100");
        this.showFormProperty (table, MadArguments.ARG_MWST, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_MWST, env),
            Datatypes.DT_INTEGER, this.mwSt);

        this.addMessage (table,
            "Bitte geben Sie hier die Adresse Ihrer Firma ein!");

        // Firmenadresse...
        // Strasse - MUSS
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, MadArguments.ARG_STREET,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_STREET, env),
            Datatypes.DT_TEXT, this.street);
        // PLZ - MUSS
        this.formFieldRestriction = new FormFieldRestriction (false, 15, 15);
        this.showFormProperty (table, MadArguments.ARG_ZIP, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ZIP, env),
            Datatypes.DT_TEXT, this.zip);

        // Ort - MUSS
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, MadArguments.ARG_TOWN, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TOWN, env),
            Datatypes.DT_TEXT, this.town);

        // Telefon
        this.showFormProperty (table, MadArguments.ARG_TEL, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TEL, env),
            Datatypes.DT_TEXT, this.tel);
        // Fax
        this.showFormProperty (table, MadArguments.ARG_FAX, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FAX, env),
            Datatypes.DT_TEXT, this.fax);
        // Email
        this.showFormProperty (table, MadArguments.ARG_EMAIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EMAIL, env), 
            Datatypes.DT_TEXT, this.email);
        // Internetadresse
        this.showFormProperty (table, MadArguments.ARG_HOMEPAGE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HOMEPAGE, env), 
            Datatypes.DT_TEXT, this.homepage);


        this.addMessage (table, "Bitte geben Sie hier den Namen des" +
            " Ansprechpartners der Firma ein!");

        // Ansprechpartner...
        // Ansprechpartnername - MUSS
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, "aname", "Ansprechpartner",
            Datatypes.DT_NAME, this.aname);

        // Separator
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
            (String) null);

        // Link zu Vertrag !!!!
        // mit Text, Sind Sie einverstanden, ...
        String path = "";
        path += this.getUserInfo ().homepagePath;

        this.addMessage (table, "Mit der Bestätigung dieses Formulars stimmen" +
            " Sie den <A HREF=\"" + IOConstants.URL_HTTP + path +
            "include/VereinBarungenKC.htm\" TARGET=\"info\">" +
            "<U><BIG><STRONG>Vertragsvereinbarungen</U></BIG></STRONG></A> zu.");
//        showProperty (table, BOArguments.ARG_HYPERLINK, "VertragsVereinbarungen", 
//            Datatypes.DT_URL, path + "include/VereinBarungenKC.htm");
    } // showFormProperties


    /**************************************************************************
     * Store a new business object in the database. <BR/>
     * <B>THIS METHOD MUST NOT BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * This method tries to store the new object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates,
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     * @throws  NameAlreadyGivenException
     *          An object with this name already exists. This exception is
     *          only raised by some specific object types which don't allow
     *          more than one object with the same name.
     */
    protected OID performCreateData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        OID newOid = null;              // oid of the newly created object

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procCreate,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.name);
        // description
        if (this.description != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING, this.description);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING, "");
        } // else

        // region
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.region);
        // mwSt
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.mwSt);
        // street
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.street);
        // zip
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.zip);
        // town
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.town);
        // tel
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.tel);
        // fax
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.fax);
        // email
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.email);
        // homepage
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.homepage);
        // aname
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.aname);

        // set type specific parameters:
        this.setSpecificCreateParameters (sp);

        // output parameters
        // oid
        Parameter oidParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        OID oid = this.oid;         // store old oid

        try
        {
            // set the new oid
            this.oid = new OID (oidParam.getValueString ());
            newOid = this.oid;
        } // try
        catch (IncorrectOidException e)
        {
            this.oid = oid;             // reset oid
            newOid = null;              // reset new oid
        } // catch

        return newOid;                  // return the oid of the new object
    } // performCreateData


    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * @param   form    Form to add the footer.
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    // necessary for not showing the Cancel-Button
    protected void createFormFooter (FormElement form)
    {
        this.createFormFooter (form, null, null, null, null, false, true);
    } // createFormFooter


    /**************************************************************************
     * This method is necessary to add the messages in the right place. <BR/>
     *
     * @param   table   The table to add the message to.
     * @param   message The message to be added to the table.
     */
    public void addMessage (TableElement table, String message)
    {
        RowElement tr;
        TableDataElement td;
        ImageElement img;
        GroupElement group = new GroupElement ();

        tr = new RowElement (2);
        tr.classId = CssConstants.CLASS_MESSAGE;

        // build the message icon
        String path = "";

        if (this.sess.activeLayout != null)
        {
            path = this.sess.activeLayout.path + this.sess.activeLayout.elems[LayoutConstants.MESSAGE].images;
        } // if
        else
        {
            path = BOPathConstants.PATH_MESSAGEICONS;
        } // else

        img = new ImageElement (path + AppMessages.MST_IMAGES[AppMessages.MST_INFO]);

        group.addElement (img);
        group.addElement (new TextElement (IE302.HCH_NBSP + message));

        td = new TableDataElement (group);
        td.classId = CssConstants.CLASS_MESSAGE;
        td.colspan = 2;
        td.valign = IOConstants.ALIGN_MIDDLE;
        // ad TavleDataelement to TableRow
        tr.addElement (td);

        // add message to table
        table.addElement (tr);
    } // addMessage

} // class Register_01
