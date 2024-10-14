<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="java">

    <!-- ************************** IMPORT BEGIN *************************** -->
    <xsl:import href="general/messages.xsl"/>
    <!-- *************************** IMPORT END **************************** -->

    <!-- if indent=no the carriage returns would not be put into the output -->
    <xsl:output method="html" encoding="ISO-8859-1" indent="yes"/>
    
    <!-- ************************* VARIABLES BEGIN ************************* -->
    <xsl:variable name="isCreated" select="/OBJECT/SYSTEM/STATE/child::text() = $ST_CREATED"/>
    <xsl:variable name="ST_CREATED" select="'4'"/>

    <xsl:variable name="objOID" select="/OBJECT/SYSTEM/OID/child::text()"/>

    <xsl:variable name="subobjElementsPrefix" select="'subobj_'"/>

    <!-- messages: -->
    <xsl:variable name="MSG_FORMS_SUBMITTING"
        select="java:getXsltMessage ($provider, $msgbundle, 'ML_MSG_FORMS_SUBMITTING')"/>

    <xsl:variable name="MSG_SUBMITTING_FORM_1"
        select="java:getXsltMessage ($provider, $msgbundle, 'ML_MSG_SUBMITTING_FORM_1')"/>

    <xsl:variable name="MSG_SUBMITTING_FORM_2"
        select="java:getXsltMessage ($provider, $msgbundle, 'ML_MSG_SUBMITTING_FORM_2')"/>

    <xsl:variable name="MSG_CREATE_OBJECT_ERROR"
        select="java:getXsltMessage ($provider, $msgbundle, 'ML_MSG_CREATE_OBJECT_ERROR')"/>
    <!-- ************************** VARIABLES END ************************** -->


    <!--***********************************************************************
        * This template provides the possibility to add support for creating
        * sub objects within the edit view of a parent object.<BR>
        *
        * To use this functionality integrate this template into the edit view.
        * Additionally add createSubobjectValidationJS into the validation
        * of the main form.  
        *
        * When using this template includeSpecificSubobjectHelperFunctions has
        * to overwritten and the following java script functions have to be
        * defined:
        * function performAddSubobject (reclContainerOID)
        * function performDeleteSubobject (oid)
        * function adjustFormFields (form, url)
        *
        * When using this template the following variables have to be
        * defined:
        * BUTTON_NAME_ADD_SUBOBJECT (e.g.:
        * <xsl:variable name="BUTTON_NAME_ADD_SUBOBJECT" select="'Add document'"/>)
        *-->
    <xsl:template name="addSubobjectHandlingSupport">
        <!-- ELAK-595 handling for creation of documents within cond fulfillments: -->
        <!-- include BO helper functions -->
        <xsl:call-template name="includeJSBObjHelperFunctions"/>

        <!-- include AJAX prototype functions -->
        <xsl:call-template name="includeAJAX"/>
        
        <!-- include some general jscript html helper functions: -->
        <xsl:call-template name="includeJSHTMLHelperFunctions"/>

        <xsl:call-template name="includeSubobjectHelperFunctions"/>
        
        <!-- include helper functions for creation of documents -->
        <xsl:call-template name="includeSpecificSubobjectHelperFunctions"/>
        
        <!-- add additional fields for creation of documents-->
        <xsl:call-template name="addSubobjectHelperFields"/>
    </xsl:template> <!-- addSubobjectHandlingSupport -->


    <!-- **************************************************************************
     *  Include AJAX prototyp functions
     ****************************************************************************
     -->
    <xsl:template name="includeAJAX">
        <SCRIPT LANGUAGE="Javascript">
        <![CDATA[
        // set script dir
        var scriptDir = top.system.baseDir + "scripts/";
        
        // load AJAX prototype script
        top.loadScript (document, scriptDir + "prototype-1.5.1.1.js");
        ]]><!--CDATA-->
        </SCRIPT>
    </xsl:template> <!-- includeJSReclamationDummyFunctions -->


    <!-- **************************************************************************
     *  Include JavaScript HTML helper functions
     ****************************************************************************
     * function String.checkfilter (filter)
     * function findHtmlListElem (HTMLNodeList list, String searchStr[, String attributeName])
     * function findHtmlElem(parentElem, elemType, searchStr, attributeName)
     ****************************************************************************
    -->
    <xsl:template name="includeJSHTMLHelperFunctions">
        <SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
        <![CDATA[
        <!--
        /**
         * boolean String.checkFilter (String str, String filter)
         * Check if a filter is part of a string.
         * Returns true if the filter is part of the string.
         */
        String.prototype.checkFilter = function (filter)
        {
            var result = false;
    
            // check if a filter was set:
            if (filter != null && filter.length > 0)
            {
                // perform the filter check:
                eval ("result = (this.search (/" + filter + "/) > -1);");
                
            } // if
            else
            {
                // empty filter is always true:
                result = true;
            } // else
    
            // return the result:
            return result;
        }; // String.checkFilter
    
        /**
         * HTMLNode findHtmlListElem (HTMLNodeList list, String searchStr[, String attributeName])
         * Find a specific element within a list.
         * The element must contain the search string (innerHTML).
         * Returns the element if found, otherwise null.
         */
        function findHtmlListElem(list, searchStr, attributeName)
        {
            // check if a search string was defined:
            if (searchStr == null)    
            {
                // the first found element is already the result:
                if (list.length > 0)
                {
                    return list[0];
                } // if
                else
                {
                    return null;
                } // else
            } // if
            else if (searchStr.length == 0)
            {
                // search for an empty string:
                searchStr = "^$";
            } // else if
    
            // loop through elements of the list and search for the first one, which
            // satisfies the search condition:
            for (var i = 0; i < list.length; i++)
            {
                // get element
                var elem = list[i];
                
                var value = (attributeName == null || attributeName == "innerHTML") ?
                    elem.innerHTML : elem.getAttribute (attributeName);
                
                // check if value is not null and the search string is found
                if (value != null && value.checkFilter (searchStr))
                {
                    // return found element
                    return elem;
                } // if
            } // for i
    
            // return default value:
            return null;
        } // findHtmlListElem
    
    
        /**
         * HTMLNode findHtmlElem (HTMLElement parentElem, String elemType, String searchStr[, String attributeName])
         * Find a specific HTML element.
         * The element must be of the defined type and the HTML content of the element
         * must contain the search string.
         * Returns the element if found, otherwise null.
         */
        function findHtmlElem(parentElem, elemType, searchStr, attributeName)
        {
            // check if parent element is not null
            if (parentElem != null)
            {
                return this.findHtmlListElem (parentElem.getElementsByTagName (elemType), searchStr, attributeName);
            } // if
    
            // return default value:
            return null;
        } // findHtmlElem
        
        /**
         * HTMLNode hideElementByID (HTMLDocument doc, String childElemID)
         * If no doc is given, the current document will be taken.
         * Hide a specific HTML element.
         * Hides the element if found, otherwise throws an error.
         */
        function hideShowElementByID(doc, childElemID, display)
        {
            // check if childElemID is not empty
            if(childElemID != null && childElemID.length > 0)
            {
                // check if doc is null
                if(doc == null)
                {
                    // get current document
                    doc = this.document;
                } // if
                
                // get child element
                var childElem = doc.getElementById(childElemID);
                
                // check if the child element is not null
                if(childElem != null)
                {
                    if(display == true)
                    {
                        // show element
                        childElem.style.display = "inline";
                    } // if
                    else
                    {
                        // hide element
                        childElem.style.display = "none";
                    } // else
                } // if
                else
                {
                    alert("Couldn't find child element");
                } // else
            } // if
        } // hideElementByID
    
        /**
         * HTMLNode removeElementByID (HTMLDocument doc, String parentElemID, String childElemID)
         * If no doc is given, the current document will be taken.
         * Remove a specific HTML element.
         * Removes the element if found, otherwise throws an error.
         */
        function removeElementByID(doc, parentElemID, childElemID)
        {
            // check if childElemID is not empty
            if(parentElemID != null && parentElemID.length > 0 &&
                childElemID != null && childElemID.length > 0)
            {
                // check if parent element is null
                if(doc == null)
                {
                    // get current document
                    doc = this.document;
                } // if
                
                // get parent element
                var parentElem = doc.getElementById(parentElemID);
                
                // get parent element
                var childElem = doc.getElementById(childElemID);
                
                // check if the child element is not null
                if(childElem != null && parentElem != null)
                {
                    // remove child element
                    doc.removeChild(childElem);
                } // if
                else
                {
                    alert("Couldn't remove child element");
                } // else
            } // if
        } // removeElementByID
    
        /**
         * HTMLNode removeElementByID (HTMLDocument doc, String parentElemID, String childElemID)
         * If no doc is given, the current document will be taken.
         * Add a specific HTML element.
         * Adds the element if found, otherwise throws an error.
         */
        function addElementByID(doc, parentElemID, childElemID)
        {
            // check if childElemID is not empty
            if(parentElemID != null && parentElemID.length > 0 &&
                childElemID != null && childElemID.length > 0)
            {
                // check if parent element is null
                if(doc == null)
                {
                    // get current document
                    doc = this.document;
                } // if
                
                // get parent element
                var parentElem = doc.getElementById(parentElemID);
                
                // get parent element
                var childElem = doc.getElementById(childElemID);
                
                // check if the child element is not null
                if(childElem != null && parentElem != null)
                {
                    // remove child element
                    parentElem.appendChild(childElem);
                } // if
                else
                {
                    alert("Couldn't append child element");
                } // else
            } // if
        } // addElementByID
    
    
        /**
         * void cloneAttribute (HTMLElement sourceElem, HTMLElement targetElem, String attributeName)
         * Copy an attribute from one html element to another.
         */
        function cloneAttribute (sourceElem, targetElem, attributeName)
        {
            var value = sourceElem.getAttribute (attributeName);
            if (value != null)
            {
                // copy the attribute to target element:
                targetElem.setAttribute (attributeName, value);
            } // if
        } // cloneAttribute
    
    
        /**
         * HTMLNode cloneFormAttributes (HTMLElement form, HTMLElement currentForm)
         * Clones all form attributes from a specific HTML form to another form.
         * Returns the currentForm.
         */
        function cloneFormAttributes (form, currentForm)
        {
            // check if childElems are not empty
            if(form != null && currentForm != null)
            {
                var attributes = form.attributes;
                for (var i = 0; i < attributes.length; i++)
                {
                    cloneAttribute (form, currentForm, attributes[i].nodeName);
                } // for i
    
                // copy enctype attribute to current form
                cloneAttribute (form, currentForm, "enctype");
                cloneAttribute (form, currentForm, "encoding");
            } // if
            else
            {
                alert("Couldn't get form.");
            } // else
                
            return currentForm;
        } // cloneFormAttributes
        
        /**
         * HTMLNode cloneTableAttributes (HTMLElement table, HTMLElement currentTable)
         * Clones all table attributes from a specific HTML table to another table.
         * Returns the currentTable.
         */
        function cloneTableAttributes(table)
        {
            var tagTable="<TABLE";
            
            // check if childElem is not empty
            if(table != null)
            {
                tagTable += " ";
                
                // copy style attribute to current table
                tagTable += "STYLE=\"" + table.getAttribute("style") + "\" ";
                
                // copy rules attribute to current table
                tagTable += "RULES=\"" + table.getAttribute("rules") + "\" ";
    
                // copy frame attribute to current table
                tagTable += "FRAME=\"" + table.getAttribute("frame") + "\" ";
                
                // copy cellpadding attribute to current table
                tagTable += "CELLPADDING=\"" + table.getAttribute("cellpadding") + "\" ";
                
                // copy cellspacing attribute to current table
                tagTable += "CELLSPACING=\"" + table.getAttribute("cellspacing") + "\" ";
                
                // copy border attribute to current table
                tagTable += "BORDER=\"" + table.getAttribute("border") + "\" ";
                
                // copy width attribute to current table
                tagTable += "WIDTH=\"" + table.getAttribute("width") + "\"";
            } // if
            else
            {
                alert("Couldn't get table.");
                return null;
            } // else
            
            tagTable += ">";
            
            return tagTable;
        } // cloneTableAttributes
        
        /**
         * HTMLNode hideShowElement (HTMLElement childElem)
         * Hide a specific HTML element.
         * Hides the element if found, otherwise throws an error.
         */
        function hideShowElement(childElem)
        {
            // check if the child element is not null
            if(childElem != null)
            {
                if(childElem.style.display == "inline")
                {
                    // show element
                    childElem.style.display = "none";
                } // if
                else if(childElem.style.display == "none")
                {
                    // hide element
                    childElem.style.display = "inline";
                } // else if
                else
                {
                    // hide element
                    childElem.style.display = "none";
                } // else
            } // if
            else
            {
                alert("Couldn't find child element");
            } // else
        } // hideShowElement
        //-->
        ]]>
        </SCRIPT>
    </xsl:template> <!-- includeJSHTMLHelperFunctions -->


    <!-- **************************************************************************
     *  Include JavaScript Object Helper functions
     ****************************************************************************
     * function objDelete ()oid
     * function showObject (oid)
     * function startWF ()
     ****************************************************************************
    -->
    <xsl:template name="includeJSBObjHelperFunctions">
        <SCRIPT LANGUAGE="Javascript">
        <![CDATA[
        // set object data to default
        function setContainerObject (oid, contOid)
        {
            top.containerId = contOid;
            top.oid = oid;
            top.containerId = contOid;
            top.majorOid = oid;
            top.isPhysical = true;
        } // setObject
        
        // edit business object
        function editBO (params)
        {
            // get bo via xml http request
            getBO (params, 71);
        } // editBO
        
        // create new business object
        function createBO (params)
        {
            // keep current oid and containerid
            var reclOID = top.oid;
            var reclContOID = top.containerId;
        
            // get bo via xml http request
            getBO (params, 62);
            
            // set object oid and containerid,
            // because they are overwritten within the sub objects
            setContainerObject(reclOID, reclContOID);
        } // createBO
        
        // create new business object
        function getBO (params, fct)
        {
            // compute url:
            var url = top.getBaseUrl () + "&fct=" + fct + params;
            
            // create new AJAX request
            new Ajax.Request (url,
                {
                    // set method
                    method: "get",
                    // set method
                    onSuccess: function (transport)
                    {
                        // create element for the loaded page:
                        var divElem = document.createElement ("div");
                        
                        //alert (transport.responseText);
                        // add the page content to the element:
                        divElem.innerHTML = transport.responseText;
                        
                        // get all forms within the loaded page:
                        var forms = divElem.getElementsByTagName ("FORM");
        
                        // find our required form:
                        var form = null;
                        
                        // loop through forms
                        for (var i = 0; i < forms.length; i++)
                        {
                            // is the name of the current form sheetForm
                            if (forms[i].name == "sheetForm")
                            {
                                // get current form
                                form = forms[i];
                                // exit for
                                break;
                            } // if
                        } // for i
        
                        // check if we found a form:
                        if (form != null)
                        {
                            // adjust the form's content to be compatible with the
                            // other forms and add it to the form list:
                            adjustFormFields (form, url);
                        } // if
                    }, // onSuccess
                    onComplete: function (transport)
                    {
        //                alert ("complete");
                    }, // onComplete
                    onCreate: function (transport)
                    {
        //                alert ("create");
                    }, // onCreate
                    onFailure: function (transport)
                    {
        //                alert ("failure");
                    }, // onFailure
                    onException: function (transport)
                    {
        //                alert ("Exception");
                    }, // onException
                    onLoaded: function (transport)
                    {
        //                alert ("loaded");
                    }, // onLoaded
                    onLoading: function (transport)
                    {
        //                alert ("loading");
                    } // onLoading
                }
            );
        } // createBO
        
        ]]><!--CDATA-->
        // delete business object
        function deleteBO (params)
        {
            // catch error
            var error = top.callUrl (62, params, null, "temp");
            
            // check if an error occured
            if(error==null)
            {
                // show error message
                alert("<xsl:value-of select="$MSG_CREATE_OBJECT_ERROR"/>");
            } // if
        } // deleteBO
        <![CDATA[
        
        // show business object
        function showBO (oid)
        {
            // show object
            top.showObject (oid);
        } // showBO
        
        
        // submit all forms
        function submitForms (searchForForm)
        {
            // search string set?
            if(searchForForm != null)
            {
                // loop through all forms
                for (var i = 0; i < document.forms.length; i++)
                {
                ]]><!--CDATA-->
                    // get current form
                    var formToSubmit = document.forms[i];
                    
                    // check if it is the first form
                    if(i == 0)
                    {
                        // inform user
                        var infoMessage = "<xsl:value-of select="$MSG_FORMS_SUBMITTING"/>";
                        
                        alert(infoMessage);
                        var divInfoUser = document.getElementById("informUser");
                        
                        // check if we get an element
                        if(divInfoUser != null)
                        {
                            divInfoUser.innerHTML = infoMessage;
                        } // if
                    } // if
                    else
                    {
                        // inform user
                        changeValueBtnSubmit("<xsl:value-of select="$MSG_SUBMITTING_FORM_1"/> " + i + " <xsl:value-of select="$MSG_SUBMITTING_FORM_2"/> " + document.forms.length + " ...", true);
                    } // else
        
                    // is subobject form?
                    if (formToSubmit.id.match (searchForForm) != null)
                    {
                        // submit subform
                        formToSubmit.submit();
                    } // if
                <![CDATA[
                } // for i
            } // if
        } // submitForms
        
        
        // Change description of submit button
        function changeValueBtnSubmit(value, disableButton)
        {
            // check if doc is not null
            if(document != null)
            {
                // get submit button
                var btnSubmit = document.getElementById ("BUTT_SUBMIT");
                
                // check if submit button is not null
                if(btnSubmit!=null)
                {
                    // check if a value is set
                    if(value != null)
                    {
                        // replace description
                        btnSubmit.childNodes[0].nodeValue = value;
                    } // if
                    
                    // check if submit button is not null
                    if(disableButton == true)
                    {
                        // disable button
                        btnSubmit.disabled = true;
                    } // if
                } // if
            } // if
        } // changeValuebtnOK
        ]]><!--CDATA-->
        </SCRIPT>
    </xsl:template> <!-- includeJSBObjHelperFunctions -->


    <!-- **************************************************************************
     * Create sub object onload.
     *
     * This template requires integration of template
     * includeJSBObjHelperFunctions.
     *
     * @param   subobjectTypecode       the typecode for the subobject to be
     *                                  created
     * @param   subobjectContainerName  the container name for the subojbect to be
     *                                  created
     -->
    <xsl:template name="createSubObjectOnLoad">
        <xsl:param name="subobjectTypecode"/>
        <xsl:param name="subobjectContainerName"/>
        
        <SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
        <![CDATA[
        // add one subobject on load
        document.getElementsByTagName ("body")[0].onload =
            function (e)
            {
                // check if object is currently created
                if(]]><xsl:value-of select="$isCreated"/><![CDATA[==true)
                {
                    // create a subobject
                    createBO ("&frs=true&ccl=N&oid=" + top.oid + "&cid=" + top.oid + "&nomen=&type=]]><xsl:value-of select="$subobjectTypecode"/><![CDATA[&tabc=]]><xsl:value-of select="$subobjectContainerName"/><![CDATA[&desc=");
                }
            }; // body.onload
        ]]>
        </SCRIPT>
    </xsl:template>


    <!-- **************************************************************************
     *  Add necessary helper fields for sub object handling.
     -->
    <xsl:template name="addSubobjectHelperFields">
        <HR size="1" width="90%" align="center"/>
        <DIV ID="additionalFields">
            <!--Required to dynamically add sub objects-->
            <DIV id="subobjects"/>
        </DIV>

        <TABLE WIDTH="100%" ID="tblBUTTONAREA">
            <!--Show button to add another subobject-->
            <BUTTON NAME="ADD_SUBOBJECT" ID="ADD_SUBOBJECT" ONCLICK="addSubobject('{$objOID}');" TITLE="{$BUTTON_NAME_ADD_SUBOBJECT}" TYPE="BUTTON"><xsl:value-of select="$BUTTON_NAME_ADD_SUBOBJECT"/></BUTTON>
        </TABLE>

        <A name="bottom" id="bottom"/>
    </xsl:template> <!-- addSubobjectHelperFields -->
    

    <!-- **************************************************************************
     *  Add necessary helper functions for sub object handling.
     -->
    <xsl:template name="includeSubobjectHelperFunctions">
	    <SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript">
	    <![CDATA[
	    <!--
	        // Adds a subobject
	        function addSubobject (reclContainerOID)
	        {
	            performAddSubobject (reclContainerOID);
	        } // addSubobject
	    
	        // Deletes a subobject
	        function deleteSubobject (oid)
	        {
	            performDeleteSubobject(oid);
	        } // deleteSubobject
	    //-->
	    ]]>
	    </SCRIPT>
    </xsl:template>


    <!-- **************************************************************************
     *  Add validition java script for validation of subobjects.
     * 
     *  This method has to be integrated into main object's xslSubmitAllowed().
     -->
    <xsl:template name="createSubobjectValidationJS">
        // submit each sub object form on submit main form
        // set count
        numSubObjects = 0;
        numForms = 0;
        
        var eventOnChange;
        var submitAllAllowed = true;
        
        // set search string for sub object forms
        var searchForForm = new RegExp("<xsl:value-of select="$subobjElementsPrefix"/>", "i");

        <![CDATA[       
        // set search string for mandatory fields
        var searchForField = new RegExp(" mandatory", "i");
        
        /*               
        // has sub forms?
        if (document.forms.length <= 0)
        {
            alert("At least one item is required!");
            submitAllAllowed = false;
            return false;
        } // if
        */
    
        // loop through all forms
        for (var i = 0; i < document.forms.length; i++)
        {
            // is sub object form?
            if (document.forms[i].id.match(searchForForm) != null)
            {                       
                // get Oid of current sub object object
                var searchForSubobjectOid = new RegExp(document.forms[i].name, "i");                           
                
                // loop through form elements
                for (var y = 0; y < document.forms[i].elements.length; y++)
                {
                    // is field mandatory?
                    if (document.forms[i].elements[y].className.match(searchForField) != null || document.forms[i].elements[y].type != "hidden")
                    {
                        // check if field has validation function
                        if(document.forms[i].elements[y].onchange != null)
                        {
                            // get validation function for field
                            // replace string "function anonymous(){}"
                            // otherwise eval does not work
                            eventOnChange = document.forms[i].elements[y].onchange.toString().replace(/function[^\)]*\)/, "");
                            eventOnChange = eventOnChange.replace("{", "");
                            eventOnChange = eventOnChange.replace("}", "");
                            // remove custom onchange handling
                            eventOnChange = document.forms[i].elements[y].onchange.toString().replace(/CUSTOM-BEGIN.+CUSTOM-END/, "");
    
                            // replace the form within the validation function with the current form
                            eventOnChange = eventOnChange.replace("document.sheetForm", "document." + document.forms[i].id);
    
                            // check if field is valid
                            if (eval(eventOnChange)==false)
                            {
                                // set focus for current field
                                document.forms[i].elements[y].focus();
                                
                                // evaluation failed, return false
                                submitAllAllowed = false;
                                return submitAllAllowed;
                            } // if
                        } // if
                    } // if
                    
                    // field id changed?
                    if(document.forms[i].elements[y].id.match(searchForSubobjectOid) != null)
                    {
                        // remove sub object oid from id
                        var fieldId = document.forms[i].elements[y].id.toString().replace(searchForSubobjectOid, "");
                        
                        // remove sub object oid from name
                        var fieldName = document.forms[i].elements[y].name.toString().replace(searchForSubobjectOid, "");
                        
                        // change id back to orginal id
                        document.forms[i].elements[y].setAttribute("id", fieldId);
                        
                        // change name back to orginal name
                        document.forms[i].elements[y].setAttribute("name", fieldName);
                    } // if
                } // for
    
                numSubObjects++;
            } // if
        } // for
        ]]>
        // all validation passed?
        if(submitAllAllowed == true)
        {
            // count all forms
            numForms = numSubObjects + 1;
            
            // submit sub forms
            submitForms (searchForForm);

            // disable all additional fields to avoid submitting the subforms with the main sheet form or
            // sbumitting the subforms twice when the main sheet form is not valid
            document.getElementById("additionalFields").disabled = true;
            document.getElementById("ADD_SUBOBJECT").disabled = true;

            // update submit button
            changeValueBtnSubmit("<xsl:value-of select="$MSG_SUBMITTING_FORM_1"/> " + document.forms.length + " <xsl:value-of select="$MSG_SUBMITTING_FORM_2"/> " + document.forms.length + " ...", true);
        } // if
        else
        {
            return submitAllAllowed;
        } // else
    </xsl:template>
    <!--END createSubobjectValidationJS-->
</xsl:stylesheet>