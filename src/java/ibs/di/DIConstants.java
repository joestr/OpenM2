/*
 * Class: DIConstants.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.bo.BOPathConstants;
import ibs.di.DITokens;
import ibs.io.IOConstants;
import ibs.tech.html.IE302;
import ibs.util.UtilConstants;

import java.io.File;


/******************************************************************************
 *
 * Constants for ibs.di business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: DIConstants.java,v 1.105 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 990105
 ******************************************************************************
 */
public abstract class DIConstants extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIConstants.java,v 1.105 2013/01/15 14:48:28 rburgermann Exp $";


    /**
     * the HTTP protocol identifier in an url string. <BR/>
     */
    public static final String HTTP_PROTOCOL = "HTTP";

    /**
     * HEAD attribute: Content-type. <BR/>
     */
    public static final String CONTENT_TYPE         = "Content-type";
    /**
     * HEAD attribute: Content-length. <BR/>
     */
    public static final String CONTENT_LENGTH       = "Content-length";
    /**
     * MIMETYPE: multipart/form-data. <BR/>
     */
    public static final String MIMETYPE_FORMDATA    = "multipart/form-data";
    /**
     * MIMETYPE: text/plain. <BR/>
     */
    public static final String MIMETYPE_TEXTPLAIN   = "text/plain";

    /**
     * MIMETYPE: application/pdf. <BR/>
     */
    public static final String MIMETYPE_APPLICATION_PDF   = "application/pdf";

    /**
     * Current version for export documents. <BR/>
     */
    public static final String DOCUMENT_VERSION = "1.0";

    /**
     * general encoding setting. <BR/>
     */
    public static final String CHARACTER_ENCODING = "UTF-8";
    
    /**
     * general encoding setting. <BR/>
     */
    public static final String CHARACTER_ENCODING_XLS = "UTF-16LE";
    
    /**
     * Holds the UTF-16LE byte order mark.
     * @see <a href="http://de.wikipedia.org/wiki/Byte_Order_Mark">Byte Order Mark</a>.
     */
    public static final byte[] BYTE_ORDER_MARK_UTF_16LE = new byte[] {(byte) 0xff, (byte) 0xfe};

    /**
     * Holds the Byte order mark for XLS files.
     */
    public static final byte[] BYTE_ORDER_MARK_XLS = DIConstants.BYTE_ORDER_MARK_UTF_16LE;


    /**
     * standard indent for prettyprinting xml files. <BR/>
     */
    public static final int XML_INTEND = 4;

    /**
     * standard language to be used in xCML filter. <BR/>
     */
    public static final String XML_LANG = "ge";

    /**
     * file extenstion for xml files. <BR/>
     */
    public static final String FILEEXTENSION_XML = ".xml";

    /**
     * file extension for xsl files. <BR/>
     */
    public static final String FILEEXTENSION_XSL = ".xsl";

    /**
     * file extension for txt files. <BR/>
     */
    public static final String FILEEXTENSION_TXT = ".txt";

    /**
     * file extension for pdf files. <BR/>
     */
    public static final String FILEEXTENSION_PDF = ".pdf";

    //
    // ELEMENTS
    //
    /**
     * name of IMPORT element. <BR/>
     */
    public static final String ELEM_IMPORT      = "IMPORT";

    /**
     * name of OBJECTS element. <BR/>
     */
    public static final String ELEM_OBJECTS     = "OBJECTS";

    /**
     * name of OBJECT element. <BR/>
     */
    public static final String ELEM_OBJECT      = "OBJECT";

    /**
     * name of USER element. <BR/>
     */
    public static final String ELEM_USER      = "USER";

    /**
     * name of GROUPS element. <BR/>
     */
    public static final String ELEM_GROUPS      = "GROUPS";

    /**
     * name of GROUP element. <BR/>
     */
    public static final String ELEM_GROUP      = "GROUP";

    /**
     * name of TABS element. <BR/>
     */
    public static final String ELEM_TABS        = "TABS";

    /**
     * name of TABOBJECT element. <BR/>
     */
    public static final String ELEM_TABOBJECT   = "TABOBJECT";

    /**
     * name of QUERYCREATOR element. <BR/>
     */
    public static final String ELEM_QUERYCREATOR    = "QUERYCREATOR";


    // tags for CONFIG section:
    /**
     * name of CONFIG element. <BR/>
     */
    public static final String ELEM_CONFIG      = "CONFIG";

    /**
     * name of INFOBUTTONS element. <BR/>
     */
    public static final String ELEM_INFOBUTTONS      = "INFOBUTTONS";

    /**
     * name of CONTENTBUTTONS element. <BR/>
     */
    public static final String ELEM_CONTENTBUTTONS      = "CONTENTBUTTONS";

    /**
     * name of TRANSFORMATION element. <BR/>
     */
    public static final String ELEM_TRANSFORMATION      = "TRANSFORMATION";

    /**
     * name of NAMEVALUE element. <BR/>
     */
    public static final String ELEM_NAMETEMPLATE      = "NAMETEMPLATE";

    // tags for META section of type translators:
    /**
     * name of META element for the type translator. <BR/>
     */
    public static final String ELEM_META_TRANSLATOR      = "META";
    
    /**
     * name of TYPECODE element. <BR/>
     */
    public static final String ELEM_TYPECODE      = "TYPECODE";    
    
    /**
     * name of FROMVERSION element. <BR/>
     */
    public static final String ELEM_FROMVERSION      = "FROMVERSION";
    
    /**
     * name of TOVERSION element. <BR/>
     */
    public static final String ELEM_TOVERSION      = "TOVERSION";

    // tags for SYSTEM section:
    /**
     * name of SYSTEM element. <BR/>
     */
    public static final String ELEM_SYSTEM      = "SYSTEM";

    /**
     * name of OID element. <BR/>
     */
    public static final String ELEM_OID = "OID";

    /**
     * name of ID element. <BR/>
     */
    public static final String ELEM_ID          = "ID";

    /**
     * name of GROUPINGID element. <BR/>
     */
    public static final String ELEM_GROUPING_ID          = "GROUPINGID";

    /**
     * name of NAME element. <BR/>
     */
    public static final String ELEM_NAME        = "NAME";


    /**
     * name of STATE element. <BR/>
     * contents the state of the object. <BR/>
     */
    public static final String ELEM_STATE        = "STATE";

    /**
     * name of DESCRIPTION element. <BR/>
     */
    public static final String ELEM_DESCRIPTION = "DESCRIPTION";

    /**
     * name of METADATA element. <BR/>
     */
    public static final String ELEM_META = "METADATA";

    /**
     * name of ID element. <BR/>
     */
    public static final String ELEM_VALIDUNTIL  = "VALIDUNTIL";

    /**
     * showInNews element. <BR/>
     */
    public static final String ELEM_SHOWINNEWS  = "SHOWINNEWS";

    // tags for VALUES section:
    /**
     * name of VALUES element. <BR/>
     */
    public static final String ELEM_VALUES      = "VALUES";

    /**
     * name of VALUE element. <BR/>
     */
    public static final String ELEM_VALUE      = "VALUE";

    /**
     * name of RESULTS element. <BR/>
     */
    public static final String ELEM_RESULTS     = "RESULTS";

    /**
     * name of RESULTROW element. <BR/>
     */
    public static final String ELEM_RESULTROW   = "RESULTROW";

    /**
     * name of RESULTELEMENT element. <BR/>
     */
    public static final String ELEM_RESULTELEMENT = "RESULTELEMENT";

    /**
     * name of REFERENCES element. <BR/>
     */
    public static final String ELEM_REFERENCES  = "REFERENCES";

    // tags for RIGHTS section:
    /**
     * name of RIGHTS element. <BR/>
     */
    public static final String ELEM_RIGHTS      = "RIGHTS";

    /**
     * name of RIGHT element. <BR/>
     */
    public static final String ELEM_RIGHT      = "RIGHT";

    // tags for ATTACHMENTS section:
    /**
     * name of ATTACHMENTS element. <BR/>
     */
    public static final String ELEM_ATTACHMENTS = "ATTACHMENTS";

    /**
     * name of ATTACHMENT element. <BR/>
     */
    public static final String ELEM_ATTACHMENT = "ATTACHMENT";

    /**
     * name of IMPORTSCRIPT element. <BR/>
     */
    public static final String ELEM_IMPORTSCRIPT = "IMPORTSCRIPT";

    /**
     * name of OPERATION element. <BR/>
     */
    public static final String ELEM_OPERATION = "OPERATION";

    /**
     * name of CONTAINER element. <BR/>
     */
    public static final String ELEM_CONTAINER = "CONTAINER";

    /**
     * name of CONTAINER element. <BR/>
     */
    public static final String ELEM_IDSOURCE = "IDSOURCE";

    /**
     * name of XMLDATA element. <BR/>
     */
    public static final String ELEM_XMLDATA = "XMLDATA";

    // tags for CONTENT section:
    /**
     * name of CONTENT element. <BR/>
     */
    public static final String ELEM_CONTENT = "CONTENT";

    /**
     * name of MAYCONTAIN element. <BR/>
     */
    public static final String ELEM_MAYCONTAIN = "MAYCONTAIN";

    /**
     * name of EXTENSION element. <BR/>
     */
    public static final String ELEM_EXTENSION = "EXTENSION";

    /**
     * name of COLUMNS element. <BR/>
     */
    public static final String ELEM_COLUMNS = "COLUMNS";

    /**
     * name of COLUMN element. <BR/>
     */
    public static final String ELEM_COLUMN = "COLUMN";

    /**
     * name of SYSCOLUMN element. <BR/>
     */
    public static final String ELEM_SYSCOLUMN = "SYSCOLUMN";

    /**
     * name of EXTCOLUMN element. <BR/>
     */
    public static final String ELEM_EXTCOLUMN = "EXTCOLUMN";

    // tags for LOGIC section:
    /**
     * name of LOGIC element. <BR/>
     */
    public static final String ELEM_LOGIC = "LOGIC";

    /**
     * name of the LINE element in the DOM tree. <BR/>
     */
    public static final String ELEM_LINE = "LINE";

    /**
     * name of OWNER element. <BR/>
     */
    public static final String ELEM_OWNER = "OWNER";

    /**
     * name of CREATED element. <BR/>
     */
    public static final String ELEM_CREATED = "CREATED";

    /**
     * name of CHANGED element. <BR/>
     */
    public static final String ELEM_CHANGED = "CHANGED";

    /**
     * name of CHECKEDOUT element. <BR/>
     */
    public static final String ELEM_CHECKEDOUT = "CHECKEDOUT";

    /**
     * subtag FIELDS of VALUE FIELD="FIELDREF". <BR/>
     */
    public static final String ELEM_FIELDS = "FIELDS";

    /**
     * subtag SYSFIELD of VALUE FIELD="FIELDREF". <BR/>
     */
    public static final String ELEM_SYSFIELD = "SYSFIELD";

    /**
     * subtag FIELD of VALUE FIELD="FIELDREF". <BR/>
     */
    public static final String ELEM_FIELD = "FIELD";

    /**
     * subtag FIELDVALUE of FIELD or SYSFIELD of VALUE FIELD="FIELDREF". <BR/>
     */
    public static final String ELEM_FIELD_VALUE = "FIELDVALUE";

    /**
     * subtag OPTIONS of VALUE FIELD="FIELDREF" in DOM-Tree. <BR/>
     */
    public static final String ELEM_OPTIONS = "OPTIONS";

    /**
     * subtag OPTION of VALUE FIELD="FIELDREF" in DOM-Tree. <BR/>
     */
    public static final String ELEM_OPTION = "OPTION";

    /**
     * tag RETURN in an response xml structure. <BR/>
     */
    public static final String ELEM_RETURN = "RETURN";

    //
    // ATTRIBUTES
    //
    /**
     * attribute name for URL. <BR/>
     */
    public static final String ATTR_URL   = "URL";

    /**
     * attribute name for value content size. <BR/>
     */
    public static final String ATTR_SIZE   = "SIZE";


    /**
     * attribute name for ACTION. <BR/>
     */
    public static final String ATTR_ACTION   = "ACTION";

    /**
     * attribute name for VERSION. <BR/>
     */
    public static final String ATTR_VERSION   = "VERSION";

    /**
     * attribute name for DOMAIN within id. <BR/>
     */
    public static final String ATTR_DOMAIN   = "DOMAIN";

    /**
     * attribute name for CONTEXT. <BR/>
     */
    public static final String ATTR_CONTEXT   = "CONTEXT";

    /**
     * attribute name for ADDWSPUSER within id. <BR/>
     */
    public static final String ATTR_IDADDWSPUSER   = "ADDWSPUSER";

    /**
     * attribute name for TYPE. <BR/>
     */
    public static final String ATTR_TYPE   = "TYPE";

    /**
     * attribute name for REF_TYPE. <BR/>
     */
    public static final String ATTR_REF_TYPE   = "REFTYPE";
    
    /**
     * attribute value = "OID". <BR/>
     */
    public static final String ATTRVAL_REF_TYPE_OID   = "OID";
    
    /**
     * attribute value EXTKEY <BR/>
     */
    public static final String ATTRVAL_REF_TYPE_EXTKEY   = DIConstants.VTYPE_EXTKEY;

    /**
     * attribute value = "PATH". <BR/>
     */
    public static final String ATTRVAL_REF_TYPE_PATH   = "PATH";

    /**
     * attribute name for TYPECODE. <BR/>
     */
    public static final String ATTR_TYPECODE = "TYPECODE";

    /**
     * attribute name for SUPERTYPECODE. <BR/>
     */
    public static final String ATTR_SUPERTYPECODE = "SUPERTYPECODE";

    /**
     * attribute name for CLASS. <BR/>
     */
    public static final String ATTR_CLASS   = "CLASS";

    /**
     * attribute name for ICON. <BR/>
     */
    public static final String ATTR_ICON   = "ICON";

    /**
     * attribute name for MAYEXISTIN. <BR/>
     */
    public static final String ATTR_MAYEXISTIN = "MAYEXISTIN";

    /**
     * attribute name for ISSEARCHABLE. <BR/>
     */
    public static final String ATTR_ISSEARCHABLE = "ISSEARCHABLE";

    /**
     * attribute name for SHOWINMENU. <BR/>
     */
    public static final String ATTR_SHOWINMENU = "SHOWINMENU";

    /**
     * attribute name for SHOWINNEWS. <BR/>
     */
    public static final String ATTR_SHOWINNEWS = DIConstants.ELEM_SHOWINNEWS;

    /**
     * attribute name for ISINHERITABLE. <BR/>
     */
    public static final String ATTR_ISINHERITABLE = "ISINHERITABLE";

    /**
     * attribute name for FIELD. <BR/>
     */
    public static final String ATTR_FIELD       = DIConstants.ELEM_FIELD;

    /**
     * attribute name for NAME. <BR/>
     */
    public static final String ATTR_NAME        = DIConstants.ELEM_NAME;

    /**
     * attribute name for DESCRIPTION. <BR/>
     */
    public static final String ATTR_DESCRIPTION = DIConstants.ELEM_DESCRIPTION;
    
    /**
     * attribute name for PROFILE. <BR/>
     */
    public static final String ATTR_PROFILE     = "PROFILE";

    /**
     * attribute name for PROFILE. <BR/>
     */
    public static final String ATTR_ALIAS       = "ALIAS";

    /**
     * attribute name for SOURCE. <BR/>
     */
    public static final String ATTR_SOURCE      = "SOURCE";

    /**
     * attribute name for TYPEREF. <BR/>
     */
    public static final String ATTR_TYPEREF     = "TYPEREF";

    /**
     * attribute name for TYPECODEREF. <BR/>
     */
    public static final String ATTR_TYPECODEREF     = "TYPECODEREF";

    /**
     * attribute name for CUSTOM. <BR/>
     */
    public static final String ATTR_CUSTOM      = "CUSTOM";

    /**
     * attribute name for SCENARIO. <BR/>
     */
    public static final String ATTR_SCENARIO    = "SCENARIO";

    /**
     * attribute name for SCENARIO. <BR/>
     */
    public static final String ATTR_MANDATORY   = "MANDATORY";

    /**
     * Attribute: READONLY. <BR/>
     */
    public static final String ATTR_READONLY   = "READONLY";

    /**
     * attribute name for SCENARIO. <BR/>
     */
    public static final String ATTR_INFO        = "INFO";

    /**
     * attribute name for TYPEFILTER. <BR/>
     */
    public static final String ATTR_TYPEFILTER  = "TYPEFILTER";

    /**
     * attribute name for TYPECODEFILTER. <BR/>
     */
    public static final String ATTR_TYPECODEFILTER  = "TYPECODEFILTER";

    /**
     * attribute name for SEARCHROOT. <BR/>
     */
    public static final String ATTR_SEARCHROOT  = "SEARCHROOT";

    /**
     * attribute name for SEARCHROOTIDDOMAIN. <BR/>
     */
    public static final String ATTR_SEARCHROOTIDDOMAIN  = "SEARCHROOTIDDOMAIN";

    /**
     * attribute name for SEARCHROOTID. <BR/>
     */
    public static final String ATTR_SEARCHROOTID  = "SEARCHROOTID";

    /**
     * attribute name for SCENARIO. <BR/>
     */
    public static final String ATTR_SEARCHRECURSIVE = "SEARCHRECURSIVE";

    /**
     * attribute name for DISPLAY. <BR/>
     */
    public static final String ATTR_DISPLAY = "DISPLAY";

    /**
     * attribute name for ENCODING. <BR/>
     */
    public static final String ATTR_ENCODING = "ENCODING";
    
    /**
     * attribute name for MLKEY. <BR/>
     */
    public static final String ATTR_MLKEY   = "MLKEY";

    /**
     * attribute name for MLNAME. <BR/>
     */
    public static final String ATTR_MLNAME   = "MLNAME";

    /**
     * attribute name for MLDESCRIPTION. <BR/>
     */
    public static final String ATTR_MLDESCRIPTION   = "MLDESCRIPTION";

    /**
     * attribute name for SHOWINLINKS. <BR/>
     */
    public static final String ATTR_SHOWINLINKS   = "SHOWINLINKS";

    /**
     * no encoding. <BR/>
     */
    public static final String ENCODING_NO = "NO";

    /**
     * BASE64 encoding. <BR/>
     */
    public static final String ENCODING_BASE64 = "BASE64";

    /**
     * Default filename for BASE64 encoded files. <BR/>
     */
    public static final String BASE64_DEF_FILENAME = "base64upload";

    /**
     * Default extension for BASE64 encoded files. <BR/>
     */
    public static final String BASE64_DEF_EXTENSION =
        DIConstants.FILEEXTENSION_PDF;

    /**
     * attribute name for FILENAME. <BR/>
     */
    public static final String ATTR_FILENAME = "FILENAME";

    /**
     * attribute name for CONTENT_TYPE. <BR/>
     */
    public static final String ATTR_CONTENT_TYPE = "CONTENT_TYPE";

    /**
     * attribute name for EXTENSION. <BR/>
     */
    public static final String ATTR_EXTENSION = "EXTENSION";

    /**
     * attribute name for DBTABLE. <BR/>
     */
    public static final String ATTR_DBTABLE     = "DBTABLE";

    /**
     * attribute name for DBFIELD. <BR/>
     */
    public static final String ATTR_DBFIELD   = "DBFIELD";

    /**
     * attribute name for QUERYNAME. <BR/>
     */
    public static final String ATTR_QUERYNAME   = "QUERYNAME";

    /**
     * attribute name for EMPTYOPTION. <BR/>
     */
    public static final String ATTR_EMPTYOPTION   = "EMPTYOPTION";

    /**
     * attribute name for REFRESH. <BR/>
     */
    public static final String ATTR_REFRESH   = "REFRESH";

    /**
     * attribute name for OPTIONS. <BR/>
     */
    public static final String ATTR_OPTIONS   = DIConstants.ELEM_OPTIONS;

    /**
     * oid attribute. <BR/>
     */
    public static final String ATTR_OID   = DIConstants.ELEM_OID;

    /**
     * id attribute for OPTION and FIELDVALUE tags. <BR/>
     */
    public static final String ATTR_ID   = DIConstants.ELEM_ID;

    /**
     * grouping id attribute for OPTION tags. <BR/>
     */
    public static final String ATTR_GROUPING_ID   = DIConstants.ELEM_GROUPING_ID;

    /**
     * id attribute for OPTION tag. <BR/>
     */
    public static final String ATTR_VALUE   = DIConstants.ELEM_VALUE;

     /**
     * selected attribute for OPTION tag. <BR/>
     */
    public static final String ATTR_SELECTED   = "SELECTED";

    /**
     * attribute in XML-Tag &lt;VALUE> for the string to be displayed after
     * the value as unit od the value. <BR/>
     */
    public static final String ATTR_UNIT   = "UNIT";

    /**
     * the &lt;CONTAINER TABNAME=""> attribute. <BR/>
     */
    public static final String ATTR_TABNAME = "TABNAME";

    /**
     * Kind of TAB. Possible Values: VIEW, OBJECT, LINK, FCT. <BR/>
     */
    public static final String ATTR_KIND = "KIND";

    /**
     * Code of Tab. Should be Type code with Tabidentifier. <BR/>
     */
    public static final String ATTR_TABCODE = "TABCODE";

    /**
     * Priority of Tab. Only Integer values allowed. <BR/>
     */
    public static final String ATTR_PRIORITY = "PRIORITY";

    /**
     * attribute name for SHOWEXT. <BR/>
     */
    public static final String ATTR_SHOWEXT   = "SHOWEXT";

    /**
     * attribute name for DATE. <BR/>
     */
    public static final String ATTR_DATE   = "DATE";

    /**
     * attribute name for USERNAME. <BR/>
     */
    public static final String ATTR_USERNAME   = "USERNAME";

    /**
     * attribute name for TOKEN. <BR/>
     */
    public static final String ATTR_TOKEN   = "TOKEN";

    /**
     * attribute name for NAMETOKEN. <BR/>
     */
    public static final String ATTR_NAMETOKEN   = "NAMETOKEN";

    /**
     * Attribute name for reminder 1 day interval. <BR/>
     */
    public static final String ATTR_REMIND1DAYS = "REMIND1DAYS";

    /**
     * Attribute name for reminder 1 notification text. <BR/>
     */
    public static final String ATTR_REMIND1TEXT = "REMIND1TEXT";

    /**
     * Attribute name for reminder 1 recipient. <BR/>
     */
    public static final String ATTR_REMIND1RECIP = "REMIND1RECIP";

    /**
     * Attribute name for query which retrieves possible reminder 1 recipients.
     * <BR/>
     */
    public static final String ATTR_REMIND1RECIPQUERY = "REMIND1RECIPQUERY";

    /**
     * Attribute name for reminder 2 day interval. <BR/>
     */
    public static final String ATTR_REMIND2DAYS = "REMIND2DAYS";

    /**
     * Attribute name for reminder 2 notification text. <BR/>
     */
    public static final String ATTR_REMIND2TEXT = "REMIND2TEXT";

    /**
     * Attribute name for reminder 2 recipient. <BR/>
     */
    public static final String ATTR_REMIND2RECIP = "REMIND2RECIP";

    /**
     * Attribute name for query which retrieves possible reminder 2 recipients.
     * <BR/>
     */
    public static final String ATTR_REMIND2RECIPQUERY = "REMIND2RECIPQUERY";

    /**
     * Attribute name for escalation day interval. <BR/>
     */
    public static final String ATTR_ESCALATEDAYS = "ESCALATEDAYS";

    /**
     * Attribute name for escalation notification text. <BR/>
     */
    public static final String ATTR_ESCALATETEXT = "ESCALATETEXT";

    /**
     * Attribute name for escalation recipient. <BR/>
     */
    public static final String ATTR_ESCALATERECIP = "ESCALATERECIP";

    /**
     * Attribute name for query which retrieves possible escalation recipients.
     * <BR/>
     */
    public static final String ATTR_ESCALATERECIPQUERY = "ESCALATERECIPQUERY";

    /**
     * Attribute name for selections, which defines how to display the element.
     * <BR/>
     */
    public static final String ATTR_VIEWTYPE = "VIEWTYPE";

    /**
     * Attribute name for selections, which defines how many columns should be used
     * to render checklists.
     * <BR/>
     */
    public static final String ATTR_NO_COLUMNS = "COLS";

    /**
     * attribute value = "CHECKLIST". <BR/>
     */
    public static final String ATTRVAL_CHECKLIST   = "CHECKLIST";

    /**
     * attribute value = "SELECTIONBOX". <BR/>
     */
    public static final String ATTRVAL_SELECTIONBOX   = "SELECTIONBOX";

    /**
     * attribute name for MULTIPLE within result row elements. <BR/>
     */
    public static final String ATTR_MULTIPLE   = "MULTIPLE";

    /**
     * Attribute name for selections and value domains, which defines if multi selection is supported.
     * <BR/>
     */
    public static final String ATTR_MULTISELECTION = "MULTISELECTION";

    /**
     * Attribute name for an operation.
     * <BR/>
     */
    public static final String ATTR_OPERATION = "OPERATION";

    /**
     * idtype "STRING". <BR/>
     */
    public static final String IDTYPE_STRING   = "STRING";

    /**
     * idtype "NUMBER". <BR/>
     */
    public static final String IDTYPE_NUMBER   = "NUMBER";

    /**
     * idtype "OBJECTID". <BR/>
     */
    public static final String IDTYPE_OBJECTID   = "OBJECTID";

    /**
     * idtype "INTEGER". <BR/>
     */
    public static final String IDTYPE_INTEGER   = "INTEGER";

    /**
     * attribute value = "YES". <BR/>
     */
    public static final String ATTRVAL_YES   = "YES";

    /**
     * attribute value = "NO". <BR/>
     */
    public static final String ATTRVAL_NO   = "NO";

    /**
     * attribute value = "ALWAYS". <BR/>
     */
    public static final String ATTRVAL_ALWAYS   = "ALWAYS";

    //
    // TABKINDS
    //

    /**
     * Possible Value for Attribut KIND in Tag TAB. <BR/>
     */
    public static final String TABKIND_VIEW = "VIEW";

    /**
     * Possible Value for Attribut KIND in Tag TAB. <BR/>
     */
    public static final String TABKIND_OBJECT = DIConstants.ELEM_OBJECT;

    /**
     * Possible Value for Attribut KIND in Tag TAB. <BR/>
     */
    public static final String TABKIND_LINK = "LINK";

    /**
     * Possible Value for Attribut KIND in Tag TAB. <BR/>
     */
    public static final String TABKIND_FCT = "FCT";


    //
    // TYPE VALUES
    //
    /**
     * value type character. <BR/>
     */
    public static final String VTYPE_CHAR           = "CHAR";

    /**
     * value type text. <BR/>
     */
    public static final String VTYPE_TEXT           = "TEXT";

    /**
     * value type longtext. <BR/>
     */
    public static final String VTYPE_LONGTEXT       = "LONGTEXT";

    /**
     * value type html text. <BR/>
     */
    public static final String VTYPE_HTMLTEXT       = "HTMLTEXT";

    /**
     * value type date. <BR/>
     */
    public static final String VTYPE_DATE           = DIConstants.ATTR_DATE;

    /**
     * value type time. <BR/>
     */
    public static final String VTYPE_TIME           = "TIME";

    /**
     * value type datetime. <BR/>
     */
    public static final String VTYPE_DATETIME       = "DATETIME";

    /**
     * value type boolean. <BR/>
     */
    public static final String VTYPE_BOOLEAN        = "BOOLEAN";

    /**
     * value type integer. <BR/>
     */
    public static final String VTYPE_INT            = DIConstants.IDTYPE_INTEGER;

    /**
     * value type float. <BR/>
     */
    public static final String VTYPE_FLOAT          = "FLOAT";

    /**
     * value type double. <BR/>
     */
    public static final String VTYPE_DOUBLE         = "DOUBLE";

    /**
     * value type number. <BR/>
     */
    public static final String VTYPE_NUMBER         = DIConstants.IDTYPE_NUMBER;

    /**
     * value type money. <BR/>
     */
    public static final String VTYPE_MONEY          = "MONEY";

    /**
     * value type file. <BR/>
     */
    public static final String VTYPE_FILE           = "FILE";

    /**
     * value type link. <BR/>
     */
    public static final String VTYPE_URL            = DIConstants.TABKIND_LINK;

    /**
     * value type EMAIL. <BR/>
     */
    public static final String VTYPE_EMAIL          = "EMAIL";

    /**
     * value type OPTION. <BR/>
     */
    public static final String VTYPE_OPTION         = DIConstants.ELEM_OPTION;

    /**
     * value type SELECTION
     */
    public static final String VTYPE_SELECTION      = "SELECTION";

    /**
     * value type IMPORTFILTER
     */
    public static final String  VTYPE_IMPORTFILTER         = "IMPORTFILTER";

    /**
     * value type EXPORTFILTER
     */
    public static final String  VTYPE_EXPORTFILTER         = "EXPORTFILTER";

    /**
     * value type picture. <BR/>
     */
    public static final String VTYPE_IMAGE          = "IMAGE";

    /**
     * value type array. <BR/>
     * @deprecated Seems to be not used anymore.
     */
    public static final String VTYPE_ARRAY          = "ARRAY";

    /**
     * value type separator. <BR/>
     */
    public static final String VTYPE_SEPARATOR      = "SEPARATOR";

    /**
     * value type remark. <BR/>
     */
    public static final String VTYPE_REMARK         = "REMARK";

    /**
     * value type button. <BR/>
     */
    public static final String VTYPE_BUTTON         = "BUTTON";

    /**
     * value type objectref. <BR/>
     */
    public static final String VTYPE_OBJECTREF         = "OBJECTREF";

    /**
     *
     * value type query. <BR/>
     */
    public static final String VTYPE_QUERY         = "QUERY";

    /**
     * value TYPE = "QUERYSELECTIONBOXNUM". <BR/>
     */
    public static final String VTYPE_QUERYSELECTIONBOXNUM = "QUERYSELECTIONBOXNUM";

    /**
     * value TYPE = "QUERYSELECTIONBOXINT". <BR/>
     */
    public static final String VTYPE_QUERYSELECTIONBOXINT = "QUERYSELECTIONBOXINT";

    /**
     * value TYPE = "QUERYSELECTIONBOX". <BR/>
     */
    public static final String VTYPE_QUERYSELECTIONBOX = "QUERYSELECTIONBOX";

    /**
     * value TYPE = "SELECTIONBOXNUM". <BR/>
     */
    public static final String VTYPE_SELECTIONBOXNUM = "SELECTIONBOXNUM";

    /**
     * value TYPE = "SELECTIONBOXINT". <BR/>
     */
    public static final String VTYPE_SELECTIONBOXINT = "SELECTIONBOXINT";

    /**
     * value TYPE = "SELECTIONBOX". <BR/>
     */
    public static final String VTYPE_SELECTIONBOX = "SELECTIONBOX";

    /**
     * value TYPE = "FIELDREF". <BR/>
     */
    public static final String VTYPE_FIELDREF = "FIELDREF";

    /**
     * value TYPE = "VALUEDOMAIN". <BR/>
     */
    public static final String VTYPE_VALUEDOMAIN = "VALUEDOMAIN";

    /**
     * value TYPE = "EXTKEY". <BR/>
     */
    public static final String VTYPE_EXTKEY = "EXTKEY";

    /**
     * value type password. <BR/>
     */
    public static final String VTYPE_PASSWORD = "PASSWORD";

    /**
     * Value type reminder. <BR/>
     */
    public static final String VTYPE_REMINDER = "REMINDER";


    /**
     * delimiter character for option fields in xmldata files. <BR/>
     */
    public static final String OPTION_DELIMITER       = ",";

    /**
     * delimiter character for typeIds in ObjectSearchContainer_01. <BR/>
     */
    public static final String TYPE_DELIMITER       = ",";

    /**
     * standard delimiter character for filelists. <BR/>
     */
    public static final String FILE_DELIMITER       = ",";

    /**
     * delimiter for objectref field value, separating oid and name. <BR/>
     */
    public static final String OBJECTREF_DELIMITER = ",";

    /**
     * export value for boolean true. <BR/>
     */
    public static final String BOOL_TRUE        = "1";

    /**
     * export value for boolean false . <BR/>
     */
    public static final String BOOL_FALSE       = "0";

    /**
     * possible true values in an import. <BR/>
     */
    public static final String BOOL_TRUE_VALUES = "1|J|JA|Y|YES|TRUE";

    /**
     * standard URL for import files. <BR/>
     */
    public static final String URL_IMPORT       =
        IOConstants.URL_HTTP + "adam/m2/import/";

    /**
     * standard URL for import dtd file. <BR/>
     */
    public static final String URL_IMPORTDTD    = "/import/import.dtd";

    /**
     * standard URL for import. <BR/>
     */
    public static final String URL_IMPORTSCRIPTDTD = "/import/importScript.dtd";

    /**
     * standard URL for export. <BR/>
     */
    public static final String URL_EXPORT       =
        IOConstants.URL_HTTP + "adam/m2/export/";

    /**
     * Import path prefix. <BR/>
     */
    public static final String PATHPREFIX_IMPORT = "import";
    /**
     * Export path prefix. <BR/>
     */
    public static final String PATHPREFIX_EXPORT = "export";

    /**
     * standard path for import. <BR/>
     */
    public static final String PATH_IMPORT          =
        DIConstants.PATHPREFIX_IMPORT + File.separatorChar;

    /**
     * standard path for import. <BR/>
     */
    public static final String PATH_IMPORTSCRIPT = DIConstants.PATHPREFIX_IMPORT +
        File.separatorChar + "importscripts" + File.separatorChar;
    /**
     * standard path for import. <BR/>
     */
    public static final String PATH_EXPORT          =
        DIConstants.PATHPREFIX_EXPORT + File.separatorChar;

    /**
     * standard filename of an object export. <BR/>
     */
    public static final String PATH_EXPORTFILENAME  =
        DIConstants.PATHPREFIX_EXPORT;

    /**
     * standard filename of an importlog. <BR/>
     */
    public static final String PATH_IMPORTLOGFILENAME  = "importlog";

    /**
     * standard path of an importlog. <BR/>
     */
    public static final String PATH_IMPORTLOG = DIConstants.PATHPREFIX_IMPORT +
        File.separatorChar + BOPathConstants.PATHN_LOGS + File.separatorChar;

    /**
     * standard filename of an esportlog. <BR/>
     */
    public static final String PATH_EXPORTLOGFILENAME  = "exportlog";

    /**
     * standard path of an exportlog. <BR/>
     */
    public static final String PATH_EXPORTLOG = DIConstants.PATHPREFIX_EXPORT +
        File.separatorChar + BOPathConstants.PATHN_LOGS + File.separatorChar;

    /**
     * standard filepath for xml template files. <BR/>
     */
    public static final String PATH_VIEWERTEMPLATES = DIConstants.PATHPREFIX_IMPORT +
        File.separatorChar + "templates" + File.separatorChar;

    /**
     * root for temporary integrator directories. <BR/>
     */
    public static final String PATH_TEMPROOT = "upload" + File.separatorChar +
                                               "temp" + File.separatorChar;
    /**
     * standard name for temporary integrator directories. <BR/>
     */
    public static final String PATH_TEMPPATH = "tmp";


    /**
     * Import source: directory. <BR/>
     */
    public static final String SOURCE_DIR       = "1";
    /**
     * Import source: upload. <BR/>
     */
    public static final String SOURCE_UPLOAD    = "2";
    /**
     * Import source: connector. <BR/>
     */
    public static final String SOURCE_CONNECTOR = "3";
    /**
     * Import source: agent. <BR/>
     */
    public static final String SOURCE_AGENT     = "4";

    //
    // SCRIPT ELEMENT CONSTANTS FOR OPERATION TYPE
    //
    /**
     * Import operation: none. <BR/>
     */
    public static final String OPERATION_NONE       = "NONE";
    /**
     * Import operation: new. <BR/>
     */
    public static final String OPERATION_NEW        = "NEW";
    /**
     * Import operation: newonly. <BR/>
     */
    public static final String OPERATION_NEWONLY    = "NEWONLY";
    /**
     * Import operation: change. <BR/>
     */
    public static final String OPERATION_CHANGE     = "CHANGE";
    /**
     * Import operation: changeonly. <BR/>
     */
    public static final String OPERATION_CHANGEONLY = "CHANGEONLY";
    /**
     * Import operation: update. <BR/>
     * @deprecated BB0 20060323: not supported anymore and replaced by new concept
     */
    public static final String OPERATION_UPDATE     = "UPDATE";
    /**
     * Import operation: delete. <BR/>
     */
    public static final String OPERATION_DELETE     = "DELETE";
    /**
     * Import operation: custom. <BR/>
     */
    public static final String OPERATION_CUSTOM     = DIConstants.ATTR_CUSTOM;
    /**
     * Import operation: default operation <BR/>
     */
    public static final String OPERATION_DEFAULT    = DIConstants.OPERATION_NEW;

    //
    // IMPORTSCRIPT ELEMENT CONSTANTS FOR CONTAINER TYPE
    //

    /**
     * Container id source type: id. <BR/>
     */
    public static final String CONTAINER_ID         = DIConstants.ELEM_ID;
    /**
     * Container id source type: path. <BR/>
     */
    public static final String CONTAINER_PATH       = "PATH";
    /**
     * Container id source type: inherit. <BR/>
     */
    public static final String CONTAINER_INHERIT    = "INHERIT";
    /**
     * Container id source type: external key mapping. <BR/>
     */
    public static final String CONTAINER_EXTKEY     = DIConstants.VTYPE_EXTKEY;

    /**
     * Name of custom operation: user to group. <BR/>
     */
    public static final String COP_GROUPUSER = "USERTOGROUP";

    /**
     * valid value when right is set for user. <BR/>
     */
    public static final String RIGHT_ISUSER     = DIConstants.ELEM_USER;

    /**
     * valid value when right is set for group. <BR/>
     */
    public static final String RIGHT_ISGROUP    = DIConstants.ELEM_GROUP;

    /**
     * Rights profile: read. <BR/>
     */
    public static final String PROFILE_READ     = "READ";
    /**
     * Rights profile: write. <BR/>
     */
    public static final String PROFILE_WRITE    = "WRITE";
    /**
     * Rights profile: administration. <BR/>
     */
    public static final String PROFILE_ADMIN    = "ADMIN";

    /**
     * Character used to print a status bar. <BR/>
     */
    public static final String CHAR_STATUSBAR = ". ";

    //
    // LOG CONSTANTS
    //
    /**
     * unknown log entry type. <BR/>
     */
    public static final int LOG_NOTYPE = 0;

    /**
     * standard log entry. <BR/>
     */
    public static final int LOG_ENTRY = 1;

    /**
     * log entry for errors. <BR/>
     */
    public static final int LOG_ERROR = 2;

    /**
     * log entry for warnings. <BR/>
     */
    public static final int LOG_WARNING = 3;

    /**
     * log strategy type for no log. <BR/>
     */
    public static final int LOGTYPE_NONE = 0;

    /**
     * log strategy type for making always new logs. <BR/>
     */
    public static final int LOGTYPE_NEW = 1;

    /**
     * log strategy type for always append to existing log. <BR/>
     */
    public static final int LOGTYPE_APPEND = 2;

    /**
     * file extension for log files. <BR/>
     */
    public static final String LOGFILE_EXTENSION = ".txt";

    /**
     * Sufix for xml file on which translation is done. <BR/>
     */
    public static final String TRANSLATION_FILE_SUFFIX = "_orig";

    /**
     * Number for connector type: no connector. <BR/>
     */
    public static final int CONNECTORTYPE_NONE          = 0;
    /**
     * Number for connector type: file. <BR/>
     */
    public static final int CONNECTORTYPE_FILE          = 1;
    /**
     * Number for connector type: file. <BR/>
     */
    public static final int CONNECTORTYPE_FTP           = 2;
    /**
     * Number for connector type: e-mail connector. <BR/>
     */
    public static final int CONNECTORTYPE_EMAIL         = 3;
    /**
     * Number for connector type: DB connector. <BR/>
     */
    public static final int CONNECTORTYPE_DB            = 4;
    /**
     * Number for connector type: HTTP (only for reading). <BR/>
     */
    public static final int CONNECTORTYPE_HTTP          = 5;
    /**
     * Number for connector type: HTTP script connector. <BR/>
     */
    public static final int CONNECTORTYPE_HTTPSCRIPT    = 6;
    /**
     * Number for connector type: HTTP multipart connector. <BR/>
     */
    public static final int CONNECTORTYPE_HTTPMULTIPART = 7;
    /**
     * Number for connector type: EDI switch connector. <BR/>
     */
    public static final int CONNECTORTYPE_EDISWITCH     = 8;
    /**
     * Number for connector type: SAP XML RFC connector. <BR/>
     */
    public static final int CONNECTORTYPE_SAPBCXMLRFC   = 9;
    /**
     * Number for connector type: SAP business connector. <BR/>
     */
    public static final int CONNECTORTYPE_SAPBC         = 10;

     /**
     * value when no filter has been set. <BR/>
     */
    public static final int FILTER_NOFILTERID = -1;


    /**
     * array with available import filter types. <BR/>
     */
    public static final String[] IMPORTFILTER_CLASSES =
    {
        "ibs.di.filter.m2XMLFilter",
    };

    /**
     * array with available import filter names. <BR/>
     */
    public static final String[] IMPORTFILTER_NAMES =
    {
        DITokens.ML_FILTER_M2XML,
    };

    /**
     * array with available export filter types. <BR/>
     */
    public static final String[] EXPORTFILTER_CLASSES =
    {
        "",
//        "ibs.di.filter.m2XMLFilter",
        "ibs.di.filter.OagisPOFilter",
        "ibs.di.filter.CXMLPOFilter",
        "ibs.di.filter.XCBLPOFilter",
        "ibs.di.filter.XMLRFCSalesOrderFilter",
    };
    /**
     * array with available export filter names. <BR/>
     */
    public static final String[] EXPORTFILTER_NAMES =
    {
        "",
//        DITokens.ML_FILTER_M2XML,
        DITokens.ML_FILTER_OAGISPO,
        DITokens.ML_FILTER_CXMLPO,
        DITokens.ML_FILTER_XCBLPO,
        DITokens.ML_FILTER_SAPXMLRFC,
    };

    /**
     * tag for import script name. <BR/> To be replaced in
     * ibs.di.DIMessages.MSG_IMPORTSCRIPT_SET messages. <BR/>
     */
    public static final String TAG_IMPORTSCRIPTNAME = "<SCRIPT>";

    /**
     * tag for import scenario name. <BR/>
     * To be replaced in ibs.di.DIMessages.MSG_IMPORTSCENARIO_SET messages. <BR/>
     */
    public static final String TAG_IMPORTSCENARIONAME = "<SCENARIO>";

    /**
     * Tag for HTTPScriptConnector: request method. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String REQUEST_METHOD        = "POST";
    /**
     * Tag for HTTPScriptConnector: pre tag. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String PRE_TAG               = IE302.TAG_PRE;
    /**
     * Tag for HTTPScriptConnector: end of pre tag. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String END_PRE_TAG           = IE302.TAG_PREEND;
    /**
     * Tag for HTTPScriptConnector: equals sign. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String SCRIPT_EQUALS         = "=";
    /**
     * Tag for HTTPScriptConnector: error during performing operation. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String SCRIPT_ERROR          = "ERROR";
    /**
     * Tag for HTTPScriptConnector: operation successful. <BR/>
     * Used to parse content comming from the CGI script. <BR/>
     */
    public static final String SCRIPT_SUCCESS        = "SUCCESS";

    /**
     * Option for the DISPLAY attribute: no specific display option. <BR/>
     */
    public static final String DISPLAY_NO           = DIConstants.ATTRVAL_NO;
    /**
     * Option for the DISPLAY attribute: display at bottom. <BR/>
     */
    public static final String DISPLAY_BOTTOM       = "BOTTOM";

    /**
     * Option for the DISPLAY attribute: display as Popup. <BR/>
     */
    public static final String DISPLAY_POPUP       = "POPUP";

    /**
     * Option for the DISPLAY attribute: display inline. <BR/>
     */
    public static final String DISPLAY_INLINE       = "INLINE";

    /**
     *  Typename of the webserver to read the file names from: Apache. <BR/>
     */
    public static final String SERVERTYPENAME_APACHE    = "Apache";
    /**
     *  Typename of the webserver to read the file names from: IIS. <BR/>
     */
    public static final String SERVERTYPENAME_IIS       = "Internet Information Server";
    /**
     *  Typename of the webserver to read the file names from: Unix. <BR/>
     */
    public static final String SERVERTYPENAME_UNIX      = "UNIX";
    /**
     *  Typename of the webserver to read the file names from: Windows. <BR/>
     */
    public static final String SERVERTYPENAME_WINDOWS   = "WINDOWS";

    /**
     *  Constant to hold the values for the SELECTION: type filter. <BR/>
     */
    public static final String SEL_TYPEFILTER           = "_TF";
    /**
     *  Constant to hold the values for the SELECTION: search root. <BR/>
     */
    public static final String SEL_SEARCHROOT           = "_SRT";
    /**
     *  Constant to hold the values for the SELECTION: search recursive. <BR/>
     */
    public static final String SEL_SEARCHRECURSIVE      = "_SRC";

    /**
     *  Typecodes for XML-Viewer object: import interface. <BR/>
     */
    public static final String TC_IMPORTINTERFACE       = "ImportInterface";
    /**
     *  Typecodes for XML-Viewer object: export interface. <BR/>
     */
    public static final String TC_EXPORTINTERFACE       = "ExportInterface";

    /**
     * Constant for the interface: the connector. <BR/>
     */
    public static String INTERFACE_CONNECTOR        = "connector";
    /**
     * Constant for the interface: the translator. <BR/>
     */
    public static String INTERFACE_TRANSLATOR       = "translator";
    /**
     * Constant for the interface: the backup connector. <BR/>
     */
    public static String INTERFACE_BACKUPCONNECTOR        = "backup_connector";

    /**
     * Constant for the log: save log. <BR/>
     */
    public static String LOG_SAVE                   = "savelog";
    /**
     * Constant for the log: append to existing log. <BR/>
     */
    public static String LOG_APPEND                 = "appendlog";
    /**
     * Constant for the log: path of log file. <BR/>
     */
    public static String LOG_PATH                   = "logpath";
    /**
     * Constant for the log: name of log file. <BR/>
     */
    public static String LOG_NAME                   = "logname";
    /**
     * Constant for the log: display the log. <BR/>
     */
    public static String LOG_DISPLAY                = "displaylog";

    /**
     * Constant for the export interface: the export filter. <BR/>
     */
    public static String INTERFACE_EXPORT_FILTER    = "exportfilter";
    /**
     * Constant for the export interface: hierarchical export enabled? <BR/>
     */
    public static String INTERFACE_HIERARCHICAL     = "hierarchical";
    /**
     * Constant for the export interface: export the container? <BR/>
     */
    public static String INTERFACE_EXPORTCONTAINER  = "exportcontainer";
    /**
     * Constant for the export interface: do export recursively? <BR/>
     */
    public static String INTERFACE_RECURSIVE        = "recursive";
    /**
     * Constant for the export interface: export only a single file? <BR/>
     */
    public static String INTERFACE_SINGLEFILE      = "singlefile";
    /**
     * Constant for the export interface: pretty print the export result? <BR/>
     */
    public static String INTERFACE_PRETTYPRINT      = "prettyprint";
    /**
     * Constant for the export interface: perform keymapping? <BR/>
     */
    public static String INTERFACE_KEYMAPPING       = "keymapping";
    /**
     * Constant for the export interface: store hierarchical info? <BR/>
     */
    public static String INTERFACE_HIERARCHICALINFO = "hierarchicalinfo";

    /**
     * Constant for the import interface: filter. <BR/>
     */
    public static String INTERFACE_IMPORT_FILTER    = "importfilter";
    /**
     * Constant for the import interface: import script. <BR/>
     */
    public static String INTERFACE_IMPORTSCRIPT     = "importscript";
    /**
     * Constant for the import interface: delete the imported file (s) after
     * import? <BR/>
     */
    public static String INTERFACE_DELETE_IMPORT    = "deleteafterimport";
    /**
     * Constant for the import interface: enable workflow after import? <BR/>
     */
    public static String INTERFACE_ENABLE_WORKFLOW  = "enableworkflow";
    /**
     * Constant for the import interface: file filter for import. <BR/>
     */
    public static String INTERFACE_FILE_FILTER      = "filefilter";
    /**
     * Constant for the import interface: create key mapping? <BR/>
     */
    public static String INTERFACE_NAMETYPEMAPPING  = "nametypemapping";

    /**
     * default value for a file filter. <BR/>
     */
    public static final String DEFAULT_FILEFILTER = "*.xml";


    /**
     * Constant for response state: unknown. <BR/>
     */
    public static final int RESPONSE_UNKNOWN = -1;
    /**
     * Constant for response state: operation successful. <BR/>
     */
    public static final int RESPONSE_SUCCESS = 0;
    /**
     * Constant for response state: error. <BR/>
     */
    public static final int RESPONSE_ERROR   = 1;
    /**
     * Constant for response state: warning. <BR/>
     */
    public static final int RESPONSE_WARNING = 2;
    /**
     * Constant for response state: information. <BR/>
     */
    public static final int RESPONSE_INFO    = 3;
    /**
     * Constant for response state: debug. <BR/>
     */
    public static final int RESPONSE_DEBUG   = 4;

    /**
     * Constant for response string for state: unknown. <BR/>
     */
    public static final String RESPONSESTR_UNKNOWN = "?";
    /**
     * Constant for response string for state: operation successful. <BR/>
     */
    public static final String RESPONSESTR_SUCCESS = "S";
    /**
     * Constant for response string for state: error. <BR/>
     */
    public static final String RESPONSESTR_ERROR   = "E";
    /**
     * Constant for response string for state: warning. <BR/>
     */
    public static final String RESPONSESTR_WARNING = "W";
    /**
     * Constant for response string for state: information. <BR/>
     */
    public static final String RESPONSESTR_INFO    = "I";
    /**
     * Constant for response string for state: debug. <BR/>
     */
    public static final String RESPONSESTR_DEBUG   = "D";


    /**
     * Time in milliseconds until a query field result expires. <BR/>
     */
    public static int QUERYFIELDRESULT_EXPIRES = 300000;

    /**
     * Fieldname: conversion report. <BR/>
     */
    public static String FLD_CONVERSION_REPORT =
        "Konvertierung Statusreport";

    /**
     * Fieldname: forwarded to conversion date. <BR/>
     */
    public static String FLD_CONVERSIONDATE =
        "Zur Konvertierung weitergeleitet am";

    /**
     * Fieldname: transmission date. <BR/>
     */
    public static final String FLD_TRANSMISSIONDATE = "Übermittelt am";

    /*
     * All supported
     * SYSFIELD NAME="..." or
     * SYSCOLUMN NAME="..."
     * variations. <BR/>
     */
    /**
     * Sysfield or syscolumn for object's name. <BR/>
     */
    public static final String SYSFIELD_NAME = DIConstants.ELEM_NAME;
    /**
     * Sysfield or syscolumn for object's type name. <BR/>
     */
    public static final String SYSFIELD_TYPENAME = "TYPENAME";
    /**
     * Sysfield or syscolumn for description of object. <BR/>
     */
    public static final String SYSFIELD_DESCRIPTION = DIConstants.ELEM_DESCRIPTION;
    /**
     * Sysfield or syscolumn for the validUntil date. <BR/>
     */
    public static final String SYSFIELD_VALIDUNTIL = DIConstants.ELEM_VALIDUNTIL;
    /**
     * Sysfield or syscolumn for creation date. <BR/>
     */
    public static final String SYSFIELD_CREATIONDATE = "CREATIONDATE";
    /**
     * Sysfield or syscolumn for creator of object. <BR/>
     */
    public static final String SYSFIELD_CREATOR = "CREATOR";
    /**
     * Sysfield or syscolumn for lastChanged date. <BR/>
     */
    public static final String SYSFIELD_LASTCHANGED = "LASTCHANGED";
    /**
     * Sysfield or syscolumn for changer of object. <BR/>
     */
    public static final String SYSFIELD_CHANGER     = "CHANGER";
    /**
     * Sysfield or syscolumn for owen of object. <BR/>
     */
    public static final String SYSFIELD_OWNER       = DIConstants.ELEM_OWNER;


    // Reminder keys:
    /**
     * Reminder key for reminder 1. <BR/>
     */
    public static final String KEY_REMIND1 = "remind1";
    /**
     * Reminder key for reminder 2. <BR/>
     */
    public static final String KEY_REMIND2 = "remind2";
    /**
     * Reminder key for escalation. <BR/>
     */
    public static final String KEY_ESCALATE  = "escalate";

    // Reminder subjects:
    /**
     * Subject for reminder 1. <BR/>
     */
    public static final String SUBJECT_REMIND1 =
        "Erste Erinnerung zu " + UtilConstants.TAG_NAME;
    /**
     * Subject for reminder 2. <BR/>
     */
    public static final String SUBJECT_REMIND2 =
        "Zweite Erinnerung zu " + UtilConstants.TAG_NAME;
    /**
     * Subject for escalation. <BR/>
     */
    public static final String SUBJECT_ESCALATE  =
        "Eskalation zu " + UtilConstants.TAG_NAME;

    // Reminder methods:
    /**
     * Reminder method for reminder 1. <BR/>
     */
    public static final String METHOD_REMIND1  = DIConstants.KEY_REMIND1;
    /**
     * Reminder method for reminder 2. <BR/>
     */
    public static final String METHOD_REMIND2  = DIConstants.KEY_REMIND2;
    /**
     * Reminder method for escalation. <BR/>
     */
    public static final String METHOD_ESCALATE   = DIConstants.KEY_ESCALATE;


    /**
     * The standard header for generated emails. <BR/>
     */
    public static final String STANDARD_EMAIL_HEADER =
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">" +
            "<HTML><HEAD></HEAD><BODY>";

    /**
     * The standard footer for generated emails. <BR/>
     */
    public static final String STANDARD_EMAIL_FOOTER =
        "<FONT FACE=\"Verdana,Arial,sans-serif\" SIZE=\"2\">" +
        " <B>Hinweis</B>: Durch Klick auf den Link gelangen Sie direkt zum" +
        " jeweiligen Objekt." + IE302.TAG_NEWLINE +
        " Dies ist eine automatisch generierte Email des m2 Systems." +
        IE302.TAG_NEWLINE + "</FONT></BODY></HTML>";

    /**
     * Value within idDomain field. <BR/>
     * Indicates that the id is a PATH instead of a normal EXTKEY id.
     */
    public static final String PATH_IDDOMAIN =
        "?IDDOMAIN-" + DIConstants.ATTRVAL_REF_TYPE_PATH;

    /**
     * Name for the compare of a defined referenced object. <BR/>
     */
    public static final String TOK_NAME = "name";

    /**
     * Description for the compare of a defined referenced object. <BR/>
     */
    public static final String TOK_DESCRIPTION = "description";
    
    /**
     * German description for the compare of a defined referenced object. <BR/>
     * USED FOR BACKWARDS COMPATIBILITY!
     */
    public static final String TOK_DESCRIPTION_DE = "beschreibung";

    /**
     * valid until for the compare of a defined referenced object. <BR/>
     */
    public static final String TOK_VALIDUNTIL = "expires on";

    /**
     * German valid until for the compare of a defined referenced object. <BR/>
     * USED FOR BACKWARDS COMPATIBILITY!
     */
    public static final String TOK_VALIDUNTIL_DE = "verfällt am";

} // class DIConstants
