/*
 * Class: TypeConstants.java
 */

// package:
package ibs.bo.type;

// imports:

/******************************************************************************
 * Types for ibs applications. <BR/>
 * This abstract class contains all types which are necessary to deal with
 * the classes delivered within this package. <BR/>
 * Format of object types: <B>ddsstttv</B>. <BR/>
 * <UL>
 * <LI><B>dd</B> ... domain id (01 .. FF)
 * <LI><B>ss</B> ... server id (01 .. FF)
 * <LI><B>ttt</B> .. type id (001 .. FFF)
 * <LI><B>v</B> .... version id (0 .. F)
 * </UL> <BR/>
 * <B>ttt</B> has the following ranges depending on its use: <BR/>
 * <UL>
 * <LI>001 .. 00F: system defined object types
 * <LI>010 .. 0FF: base object types
 * <LI>110 .. FFF: derived object types.
 * </UL><P>
 *
 * @version     $Id: TypeConstants.java,v 1.48 2010/04/29 15:26:33 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980529
 ******************************************************************************
 */
public abstract class TypeConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TypeConstants.java,v 1.48 2010/04/29 15:26:33 btatzmann Exp $";


    /**
     * Type used to represent that there is no type. <BR/>
     */
    public static final int TYPE_NOTYPE                 = 0x0000;

    /**
     * Name of type used to represent that there is no type. <BR/>
     */
    public static String TN_NOTYPE            = "";


    // type codes:
    /**
     * Code of type Container. <BR/>
     */
    public static String TC_Container = "Container";

    /**
     * Code of type User. <BR/>
     */
    public static String TC_User = "User";

    /**
     * Code of type UserContainer. <BR/>
     */
    public static String TC_UserContainer = "UserContainer";

    /**
     * Code of type Group. <BR/>
     */
    public static String TC_Group = "Group";

    /**
     * Code of type GroupContainer. <BR/>
     */
    public static String TC_GroupContainer = "GroupContainer";

    /**
     * Code of type UserAdminContainer. <BR/>
     */
    public static String TC_UserAdminContainer = "UserAdminContainer";

    /**
     * Code of type UserGroupsContainer. <BR/>
     */
    public static String TC_UserGroupsContainer = "UserGroupsContainer";

    /**
     * Code of type Workspace. <BR/>
     */
    public static String TC_Workspace = "Workspace";

    /**
     * Code of type Rights. <BR/>
     */
    public static String TC_Rights = "Rights";

    /**
     * Code of type Domain. <BR/>
     */
    public static String TC_Domain = "Domain";

    /**
     * Code of type DocumentTemplate. <BR/>
     */
    public static String TC_DocumentTemplate = "DocumentTemplate";

    /**
     * Code of type DocumentTemplateContainer. <BR/>
     */
    public static String TC_DocumentTemplateContainer = "DocumentTemplateContainer";

    /**
     * Code of type Translator. <BR/>
     */
    public static String TC_Translator = "Translator";

    /**
     * Code of type Connector. <BR/>
     */
    public static String TC_Connector = "Connector";

    /**
     * Code of type ASCIITranslator. <BR/>
     */
    public static String TC_ASCIITranslator = "ASCIITranslator";

    /**
     * Code of type XMLViewer. <BR/>
     */
    public static String TC_XMLViewer = "XMLViewer";

    /**
     * Code of type XMLViewerContainer. <BR/>
     */
    public static String TC_XMLViewerContainer = "XMLViewerContainer";

    /**
     * Code of type ImportScript. <BR/>
     */
    public static String TC_ImportScript = "ImportScript";

    /**
     * Code of type Attachment. <BR/>
     */
    public static String TC_Attachment = "Attachment";

    /**
     * Code of type AttachmentContainer. <BR/>
     */
    public static String TC_AttachmentContainer = "AttachmentContainer";

    /**
     * Code of type File. <BR/>
     */
    public static String TC_File = "File";

    /**
     * Code of type Url. <BR/>
     */
    public static String TC_Url = "Url";

    /**
     * Code of type Reference. <BR/>
     */
    public static String TC_Reference = "Referenz";

    /**
     * Code of type ReferenceContainer. <BR/>
     */
    public static String TC_ReferenceContainer = "ReferenzContainer";

    /**
     * Code of type Workflow. <BR/>
     */
    public static String TC_Workflow = "Workflow";

    /**
     * Code of type WorkflowTemplate. <BR/>
     */
    public static String TC_WorkflowTemplate = "WorkflowTemplate";

    /**
     * Code of type QueryCreator. <BR/>
     */
    public static String TC_QueryCreator = "QueryCreator";

    /**
     * Code of type DBQueryCreator. <BR/>
     */
    public static String TC_DBQueryCreator = "DBQueryCreator";

    /**
     * Code of type QueryExecutive. <BR/>
     */
    public static String TC_QueryExecutive = "QueryExecutive";

    /**
     * Code of type SentObject. <BR/>
     */
    public static String TC_SentObject = "SentObject";

    /**
     * Code of type Recipient. <BR/>
     */
    public static String TC_Recipient = "Recipient";

    /**
     * Code of type ReceivedObject. <BR/>
     */
    public static String TC_ReceivedObject = "ReceivedObject";

    /**
     * Code of type PersonSearchContainer. <BR/>
     */
    public static String TC_PersonSearchContainer = "PersonSearchContainer";

    /**
     * Code of type ObjectSearchContainer. <BR/>
     */
    public static String TC_ObjectSearchContainer = "ObjectSearchContainer";

    /**
     * Code of type Layout. <BR/>
     */
    public static String TC_Layout = "Layout";

    /**
     * Code of type Locale. <BR/>
     */
    public static String TC_Locale = "Locale";
    
    /**
     * Code of type Person. <BR/>
     */
    public static String TC_Person = "Person";

    /**
     * Code of type Address. <BR/>
     */
    public static String TC_Address = "Address";

    /**
     * Code of type CleanContainer. <BR/>
     */
    public static String TC_CleanContainer = "CleanContainer";

    /**
     * Code of type SimpleSearchContainer. <BR/>
     */
    public static String TC_SimpleSearchContainer = "SimpleSearchContainer";

    /**
     * Code of type ServicePoint_01. <BR/>
     */
    public static String TC_ServicePoint = "ServicePoint";

    /**
     * Code of type WasteBasket_01. <BR/>
     */
    public static String TC_WasteBasket = "WasteBasket";

    /**
     * Code of type Menu_01. <BR/>
     */
    public static String TC_Menu = "Menu";

    /**
     * Code of type Workspacetemplate. <BR/>
     */
    public static String TC_WorkspaceTemplate = "WorkspaceTemplate";

    /**
     * Code of type QuerySelectContainer. <BR/>
     */
    public static String TC_QuerySelectContainer = "QuerySelectContainer";

    /**
     * Code of type QuerySelectContainer. <BR/>
     */
    public static String TC_IntegratorContainer = "IntegratorContainer";
    
    /**
     * Code of type Value domain element. <BR/>
     */
    public static String TC_ValueDomainElement = "vd_vDElement";

} // class TypeConstants
