/*
 * Publication_01.java
 */

// package:
package m2.version.publish;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.Buttons;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DataElement;
import ibs.di.ValueDataElement;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;

import m2.version.Version_01;
import m2.version.publish.PVersion_01;


/******************************************************************************
 * This class represents one object for a file which can be versioned.
 * The class contains a version number. This number is gathered from
 * the VersionContainer who is responsible for the versioning itself. <BR/>
 *
 * @version     $Id: Publication_01.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class Publication_01 extends Version_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Publication_01.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The property contains the name of the form template for the property
     * original object. <BR/>
     */
    protected static final String ATTR_ORIGINALOBJECT = "OObjekt";

    /**
     * The property contains the name of the database field of the original
     * Object from the form template. <BR/>
     */
    protected static final String DBFIELD_ORIGINALOBJECT = "m_oObject";


    /**************************************************************************
     * This constructor creates a new instance of the class Version. <BR/>
     */
    public Publication_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Publication


    /**************************************************************************
     * This constructor creates a new instance of the class Version. <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Publication_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Publication

    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_DELETE,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Overwritten method. It sets the showAsFormFrameset to false when the
     * container is given already where the published objects should be stored
     * in. <BR/>
     * Then the super method is called.
     *
     * @param   representationForm  The representation form for display.
     * @param   function            The function (for rights check).
     *
     * @return  The operation succeeded successfully.
     */
    public boolean showChangeForm (int representationForm, int function)
    {
        this.showChangeFormAsFrameset = false;

        // return the super methods return value
        return super.showChangeForm (representationForm, function);
    } // showChangeForm


    /**************************************************************************
     * Reads the data of the object from an import element. <BR/>
     *
     * @param   dataElement The importElement to read the data from.
     */
    public void readImportData (DataElement dataElement)
    {
        super.readImportData (dataElement);

        // this method is overwritten to tell the original object the oid of
        // the new created published object.
        // the oid of the original object is stored in the value ATTR_ORIGINALOBJECT

        ValueDataElement val = this.dataElement
            .getValueElement (Publication_01.ATTR_ORIGINALOBJECT);

        if (val != null && val.value != null)
        {
            // get the oid of the fieldref value
            String oidStr = val.value.substring (0, val.value.indexOf (','));
            if (oidStr != null)
            {
                try
                {
                    // load the original PVersion_01 object.
                    PVersion_01 v = (PVersion_01) this.getObjectCache ().fetchObject
                        (new OID (oidStr), this.user, this.sess, this.env, false);
                    // set the oid of the published object
                    v.setPublishedObjectOid (this.oid);
                } // try
                catch (IncorrectOidException e)
                {
                    this.showIncorrectOidMessage (oidStr);
                } // catch
                catch (ObjectNotFoundException e)
                {
                    IOHelpers.showMessage (e.toString (),
                        this.app, this.sess, this.env);
                } // catch
                catch (TypeNotFoundException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // catch
                catch (ObjectClassNotFoundException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // catch
                catch (ObjectInitializeException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_INITIALIZATIONFAILED, this.env),
                        this.app, this.sess, this.env);
                } // catch
            } // if oidStr given
        } // if field found
        else
        {
            IOHelpers.showMessage (
                "Value field '" + Publication_01.ATTR_ORIGINALOBJECT + "' not found!",
                this.app, this.sess, this.env);
        } // else if field not found
    } // readImportData

} // class Publication_01
