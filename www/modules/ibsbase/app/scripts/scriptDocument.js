/******************************************************************************
 * This file contains the JavaScript code which is used for document
 * manipulation purposes. <BR/>
 *
 * @version     $Id: scriptDocument.js,v 1.1 2005/05/04 16:06:46 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 20050430
 ******************************************************************************
 */

//============= necessary classes and variables ===============================
// classes:
//
// messages:
//
// tokens:


//============= declarations ==================================================


//============= common functions ==============================================

/**
 * void addAttribute (Document doc, ElementNode elem, String name, String value)
 * Add an attribute defined through its to an element and set the attribute's
 * value.
 */
function addAttribute (doc, elem, name, value)
{
    var attribute = doc.createAttribute (name);
    attribute.nodeValue = value;
    elem.setAttributeNode (attribute);
} // addAttribute


/**
 * ElementNode findTag (Document doc, String tagName, String tagAttributeName,
 *                      String tagAttributeValue)
 * Find a specific tag within the document tree.
 * The tag is defined through its name.
 * If a tagAttributeName is defined the tag must have an attribute with this
 * name which has the defined tagAttributeValue.
 * If a tagAttributeValue is defined, but empty, the tag content must be equal
 * to tagAttributeValue.
 */
function findTag (doc, tagName, tagAttributeName, tagAttributeValue)
{
    var tagList = doc.getElementsByTagName (tagName);
    var tag = null;
    var attribute = null;
    var found = false;

    // check if we found the tags:
    if (tagList != null)                // found tag list?
    {
        // check if a tag name was set:
        if (tagAttributeName == null)   // don't check attribute?
        {
            // use first tag in list:
            tag = tagList[0];
        } // if don't check attribute
        else if (tagAttributeName == "") // check tag content?
        {
            for (var i = 0; !found && i < tagList.length; i++)
            {
                if (tagList[i].innerHTML == tagAttributeValue)
                {
                    tag = tagList[i];
                    found = true;
                } // if
            } // for i
        } // else if check tag content
        else                            // check attribute
        {
            for (var i = 0; !found && i < tagList.length; i++)
            {
                attribute = tagList[i].getAttributeNode (tagAttributeName);

                if (attribute != null &&
                    attribute.nodeValue == tagAttributeValue)
                {
                    tag = tagList[i];
                    found = true;
                } // if
            } // for i
        } // else check attribute
    } // if found tag list

    // return the result:
    return tag;
} // findTag


/**
 * void insertTag (Document doc, ElementNode tag, String nextTagName,
 *                 String nextTagAttributeName, String nextTagAttributeValue)
 * Insert a tag before another one.
 * The tag where the new one shall  be inserted before is defined through
 * nextTagName, nextTagAttributeName, and nextTagAttributeValue.
 * For details see -> findTag.
 */
function insertTag (doc, tag, nextTagName,
                    nextTagAttributeName, nextTagAttributeValue)
{
    var nextTag =
        findTag (doc, nextTagName, nextTagAttributeName, nextTagAttributeValue);

    // check if we found the tag:
    if (nextTag != null)                // found tag?
    {
        nextTag.parentNode.insertBefore (tag, nextTag);
    } // if found tag
} // insertTag


/**
 * void replaceTag (Document doc, ElementNode tag, String oldTagName,
 *                  String oldTagAttributeName, String oldTagAttributeValue)
 * Replace a tag by another one.
 * The tag to be replaced is defined through oldTagName, oldTagAttributeName,
 * and oldTagAttributeValue.
 * For details see -> findTag.
 */
function replaceTag (doc, tag, oldTagName,
                     oldTagAttributeName, oldTagAttributeValue)
{
    var oldTag =
        findTag (doc, oldTagName, oldTagAttributeName, oldTagAttributeValue);

    // check if we found the tag:
    if (oldTag != null)                 // found tag?
    {
        oldTag.parentNode.replaceChild (tag, oldTag);
    } // if found tag
} // replaceTag


/**
 * void createStylesheetLink (Document doc, String filePath)
 * Create a link for including a stylesheet.
 * The stylesheet is included before the first already included stylesheet link.
 */
function createStylesheetLink (doc, filePath)
{
    var link = doc.createElement ("LINK");

    addAttribute (doc, link, "REL", "STYLESHEET");
    addAttribute (doc, link, "TYPE", "text/css");
    addAttribute (doc, link, "HREF", filePath);

    insertTag (doc, link, "LINK", null, null);
} // createStylesheetLink


/**
 * void createTitle (Document doc, String text)
 * Create a titl for the page.
 * The title replaces the already existing title within the document.
 */
function createTitle (doc, text)
{
    var newTdElem = doc.createElement ("TD");
//    addAttribute (doc, newTdElem, "CLASS", "rep_title");
    newTdElem.innerHTML =
        '<DIV CLASS="rep_heading">' + text + '</DIV>';
    replaceTag (doc, newTdElem, "TD", "CLASS", "header");
} // createTitle
