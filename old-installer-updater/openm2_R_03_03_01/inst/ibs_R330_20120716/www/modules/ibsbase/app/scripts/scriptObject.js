/******************************************************************************
 * This file contains the JavaScript code which is used for enhancing object
 * behaviour of JavaScript itself.
 * There should be no code which depends on browser functionality.
 *
 * @version     $Id: scriptObject.js,v 1.1 2006/04/11 15:52:08 klreimue Exp $
 *
 * @author      Klaus Reimüller (KR) 20060330
 ******************************************************************************
 */

//============= extended classes and functions ===============================
// class Object:
//      copyAttributes
//      _clone
//      inherit
//      instanceOf
// class Function
//      className
//      extend
//      isInherited
//      getInheritance


//============= declared classes and functions ================================
// function toBoolean
// function toNatural
// function toOid
// function escapeString
// function getProps
// function showProps
// function classOf
// function notImplemented
// function $A2
// function printInheritance
// function equals
// function equalsNotNull


//============= necessary classes and variables ===============================
// class System

// from prototype:
//      function $A
//      function Object.extend


//============= declarations ==================================================

// constants:

// variables:


//============= class Object ==================================================

/**
 * void Object.copyAttributes (Object dest)
 * Copy all attributes from one object to another.
 */
Object.prototype.copyAttributes = function (dest)
{
    // copy all attributes from the original object to the second one:
    for (property in this)
    {
        dest[property] = this[property];
    } // for property
} // Object.copyAttributes


/**
 * Object Object._clone ()
 * Create a clone of an object.
 * The new object has exactly the same attributes as the original one.
 */
Object.prototype._clone = function ()
{
    var obj = new Object ();            // create the new instance

    // copy all attributes from the original object to the clone:
    this.copyAttributes (obj);

    // return the result:
    return obj;
} // Object._clone


/**
 * void Object.inherit (Prototype superProto)
 * Inherit the properties and functions from another prototype.
 * The prototype is defined through a function.
 */
Object.prototype.inherit = function (superProto)
{
/*KR this is a performance killer
    // check if there is a valid function defined for being inherited:
    if (!superProto || superProto == null || classOf (superProto) != "function")
    {
        alert (classOf (this) +
               ": inheritance not possible, no valid prototype defined");
        // terminate the function:
        return;
    } // if
*/

    // analyse arguments:
    var otherArgs = $A2 (arguments, 1);
    var length = otherArgs.length;
    var oldPrototype = this._myPrototype || this.constructor;

    // perform inheritance:
    this._myPrototype = superProto;
    if (length > 0)                     // at least one argument?
    {
        // analyse class of first argument:
        var firstArg = otherArgs[0];
        var firstClass = classOf (firstArg).toLowerCase ();

        if (firstClass == "arguments" ||
            (length == 1 && firstClass == "array"))
        {
            // the first argument contains a list of arguments for constructor:
            superProto.apply (this, firstArg);
        } // if
        else
        {
            // use all arguments as arguments for constructor:
            superProto.apply (this, otherArgs);
        } // else
    } // if at least one argument
    else                                // no arguments
    {
        superProto.apply (this);
    } // else no arguments
    this._myPrototype = oldPrototype;

    // store the inheritance:
    var inheritance = this._myPrototype._inheritance;
    if (inheritance == null)
    {
        inheritance = new Array ();
        this._myPrototype._inheritance = inheritance;
    } // if

    if (superProto._inheritance == null)
    {
        superProto._inheritance = new Array ();
    } // if

    // check if inheritance was already done:
    for (var i = 0; i < inheritance.length; i++)
    {
        if (inheritance[i] == superProto)
        {
            break;
        } // if
    } // for i

    if (i == inheritance.length)        // prototype was not found?
    {
        inheritance[inheritance.length] = superProto;
    } // if prototype was not found
} // Object.inherit


/**
 * boolean Object.instanceOf (Prototype proto)
 * Check if the actual instance is an instance of a specific prototype.
 * The prototype is defined through a function.
 */
Object.prototype.instanceOf = function (proto)
{
    // check if there is a valid function defined for checking inheritance:
    if (!proto || proto == null || classOf (proto) != "function")
    {
        alert (classOf (this) +
               ": inheritance check not possible, no valid prototype defined");
        // terminate the function:
        return false;
    } // if

    // search through the inheritance tree for the prototype in question:
    return (this.constructor.isInherited (proto));
} // Object.instanceOf


//============= class Function ================================================

/**
 * void Function.className ()
 * Get the name of a function, which is also the name of a prototype.
 */
Function.prototype.className = function ()
{
    var className = this._className;

    if (!className || className == null)
    {
        var aMatch = ("" + this).match (/\s*function (\S*)\s*\(/);
        if (aMatch != null)
        {
            className = aMatch[1];
        } // if
        else
        {
            className = classOf (this);
        } // else
        this._className = className;
    } // if

    // return the result:
    return className;
} // Function.className


/**
 * void Function.extend (Prototype superProto)
 * Extend another prototype, i.e. inherit its properties and functions.
 * The prototype is defined through a function.
 */
Function.prototype.extend = function (superProto)
{
/*KR this is a performance killer
    // check if there is a valid function defined for being inherited:
    if (!superProto || superProto == null || classOf (superProto) != "function")
    {
        alert (classOf (this) +
               ": inheritance not possible, no valid prototype defined");
        // terminate the function:
        return;
    } // if
*/

    // perform extension:
    Object.extend (this.prototype, superProto.prototype);

    // store the inheritance:
    var inheritance = this._inheritance;
    if (inheritance == null)
    {
        inheritance = new Array ();
        this._inheritance = inheritance;
    } // if

    if (superProto._inheritance == null)
    {
        superProto._inheritance = new Array ();
    } // if

    // check if inheritance was already done:
    for (var i = 0; i < inheritance.length; i++)
    {
        if (inheritance[i] == superProto)
        {
            break;
        } // if
    } // for i

    if (i == inheritance.length)        // prototype was not found?
    {
        inheritance[inheritance.length] = superProto;
    } // if prototype was not found
} // Function.extend


/**
 * boolean Function.isInherited (Prototype proto)
 * Check if the actual instance is an instance of a specific prototype.
 * The prototype is defined through a function.
 * This is a recursive function.
 * Remark: Normally the property __proto__ would be enough for getting this
 *         information. Unfortunately this is not supported by MSIE.
 */
Function.prototype.isInherited = function (proto)
{
    // check actual prototype:
    if (this == proto)
    {
        return true;
    } // if

    // check if there is an inheritance defined:
    if (!this._inheritance)
    {
        return false;
    } // if

    var superList = this._inheritance;
    // check all other prototypes:
    for (var i = 0; i < superList.length; i++)
    {
        if (superList[i].isInherited (proto))
        {
            return true;
        } // if
    } // for i

    // no inheritance found:
    return false;
} // Function.isInherited


/**
 * String Function.getInheritance (String indent)
 * Get inheritance string for displaying.
 * This is a recursive function.
 */
Function.prototype.getInheritance = function (indent)
{
    var str = "";

    str += indent + this.className ();

    if (this._inheritance)
    {
        var superList = this._inheritance;

        // check all other prototypes:
        for (var i = 0; i < superList.length; i++)
        {
            str += superList[i].getInheritance (indent + "  ");
        } // for i
    } // if

    // return the result:
    return str;
} // Function.getInheritance


//============= constraint functions ==========================================

/**
 * boolean toBoolean (??? value)
 * Create a boolean value out of the value.
 * If the value is not a valid boolean it is set to false.
 */
function toBoolean (value)
{
    // check the value:
    if (value != true && value != false)// value not correct?
    {
        value = false;                  // set default value
    } // if value not correct

    return value;                       // return the result
} // toBoolean

/**
 * int toNatural (??? value)
 * Create a natural number (integer >= 0) out of the value.
 * If the value is not a valid natural number it is set to 0.
 */
function toNatural (value)
{
    // check the value:
    if (value == null || isNaN (value) || value < 0)
                                        // value not correct?
    {
        value = 0;                      // set default value
    } // if value not correct

    return value;                       // return the result
} // toNatural

/**
 * OID toOid (??? value)
 * Create an oid value out of the value.
 * If the value is not a valid oid it is set to 0x0000000000000000.
 */
function toOid (value)
{
    // check the value:
    if (value == null || value == "")   // value not correct?
    {
        value = NOOID;                  // set default value
    } // if value not correct

    return value;                       // return the result
} // toOid


//============= common functions ==============================================

/**
 * void createClass (Function constructor, Class parent,
 *                   Structure definitions)
 * Create a new class. The constructor must already be existing.
 */
function createClass (constructor, parent, definitions)
{
    // perform inheritance:
    if (parent != null)                 // parent defined?
    {
        // check if we have to inherit from one ore more classes:
        if (classOf (parent).toLowerCase () == "array") // more than one parent?
        {
            // loop through each parent and inherit the properties and methods
            // of each one:
            for (var i = 0; i < parent.length; i++)
            {
                constructor.extend (parent[i]);
            } // for i
        } // if more than one parent
        else                            // just one parent
        {
            // inherit properties an methods of parent:
            constructor.extend (parent);
        } // else just one parent
    } // if parent defined

    // define specific methods and properties:
    Object.extend (constructor.prototype, definitions);
} // createClass


/**
 * String escapeString (String str)
 * Escape the string. Each escapable character is escaped within the string.
 * The new string is returned.
 */
function escapeString (str)
{
    var newStr = "";                    // the new string
    var ch = '';                        // the actual character

    // loop through all characters of the original string:
    for (var i = 0; i < str.length; i++)
    {
        ch = str.charAt (i);            // get the actual character

        // check if the character shall be escaped:
        if (ch == '\\' || ch == '\'' || ch == '\"')
                                        // the character shall be escaped?
        {
            // write the escape character:
            newStr += '\\';
        } // if the character shall be escaped

        // add the character to the new string:
        newStr += ch;
    } // for

    // return the computed string:
    return newStr;
} // escapeString


/**
 * String getProps (Object obj[, String startString])
 * Get the properties of an object within the browser.
 * With startString it's possible to restrict the beginning of the properties'
 * names.
 */
function getProps (obj, startString)
{
    var result = '';
    var searchString = startString ? startString.toLowerCase () : "";
    var functionPos = 1;                // position of 'function' keyword
                                        // within property string

    if (top.system.browser.ie)
    {
        functionPos = 0;
    } // if

    // loop through all properties of the current object:
    // (note: this does not work for Opera!)
    for (var i in obj)
    {
        // display all properties starting with the required string and
        // which are no functions:
        if (i.toLowerCase ().indexOf (searchString) >= 0 &&
            ("" + obj[i]).indexOf ("function") != functionPos)
        {
            result += i + ' = ' + obj[i] + ';';
        } // if
    } // for

    return result;
} // getProps


/**
 * void showProps (Object obj[, String startString])
 * Display the properties of an object within the browser.
 * With startString it's possible to restrict the beginning of the properties'
 * names.
 */
function showProps (obj, startString)
{
    alert (getProps (obj, startString));
} // showProps


/**
 * String classOf (Object vExpression)
 * Get the class name of an object.
 * This function was copied from: http://www.webreference.com/dhtml/column68/
 * (it was there with the name "dltypeof")
 */
function classOf (vExpression)
{    
    var sTypeOf = typeof vExpression;
    if( sTypeOf == "function" )
    {
        var sFunction = vExpression.toString();
        if( ( /^\/.*\/$/ ).test( sFunction ) )
        {
            return "regexp";
        }
        else if( ( /^\[object.*\]$/i ).test( sFunction ) )
        {
            sTypeOf = "object"
        }
    }
    if( sTypeOf != "object" )
    {
        return sTypeOf;
    }
    
    switch( vExpression )
    {
        case null:
            return "null";
        case window:
            return "window";
        case window.event:    
            return "event";
    }
    
    if( window.event && ( event.type == vExpression.type ) )
    {
        return "event";
    }
    
    var fConstructor = vExpression.constructor;
    if( fConstructor != null )
    {
        switch( fConstructor )
        {                                                                    
            case Array:
                sTypeOf = "array";
                break;
            case Date:
                return "date";
            case RegExp:
                return "regexp";
            case Object:
                sTypeOf = "jsobject";
                break;
            case ReferenceError:
                return "error";
            default:
                var sConstructor = fConstructor.toString();
                // the following code part was changed because the original
                // code did not recognize functions having a space before
                // the opening bracket:
                var aMatch = sConstructor.match( /\s*function (\S*)\s*\(/ );
//                var aMatch = sConstructor.match( /\s*function (.*)\(/ );
                if( aMatch != null )
                {
                    return aMatch[ 1 ];
                }
            
        }
    }

    var nNodeType = vExpression.nodeType;
    if( nNodeType != null )
    {    
        switch( nNodeType )
        {
            case 1:
                if( vExpression.item == null )
                {
                    return "domelement";
                }
                break;
            case 3:
                return "textnode";
        }
    }
    
    if( vExpression.toString != null )
    {
        var sExpression = vExpression.toString();
        var aMatch = sExpression.match( /^\[object (.*)\]$/i );
        if( aMatch != null )    
        {
            var sMatch = aMatch[ 1 ];
            switch( sMatch.toLowerCase() )
            {
                case "event":
                    return "event";
                case "math":
                    return "math";
                case "error":    
                    return "error";
                case "mimetypearray":
                    return "mimetypecollection";
                case "pluginarray":
                    return "plugincollection";
                case "windowcollection":
                    return "window";
                case "nodelist":
                case "htmlcollection":
                case "elementarray":
                    return "domcollection";
            }
        }
    }
    
    if( vExpression.moveToBookmark && vExpression.moveToElementText )
    {
        return "textrange";
    }
    else if( vExpression.callee != null )
    {
        return "arguments";
    }
    else if( vExpression.item != null )    
    {
        return "domcollection";
    }
    
    return sTypeOf;
} // classOf


/**
 * Function notImplemented (String functionName)
 * Return a function that displays a not implemented message.
 */
function notImplemented (functionName)
{
    return (
        function ()
        {
            alert ("function \"" + functionName + "\" is not implemented");
        } // function
    );
} // notImplemented


/**
 * Array $A2 (Object iterable, [int startIndex], [int endIndex])
 * Convert an object to an array and drop elements from beginning and end.
 * This function is based upon $A from prototype.js.
 */
function $A2 (iterable, startIndex, endIndex)
{
    // check if start index is valid:
    if (isNaN (startIndex) || startIndex <= 0)
    {
        // check if the end index is valid:
        if (isNaN (endIndex))
        {
            // return all:
            return $A (iterable);
        } // else if
        else
        {
            // return all except last elements:
            return $A (iterable).slice (0, endIndex);
        } // else
    } // if
    // check if the end index is valid:
    else if (isNaN (endIndex))
    {
        // return all except first elements:
        return $A (iterable).slice (startIndex);
    } // else if
    else
    {
        // return all elements except first and last ones:
        return $A (iterable).slice (startIndex, endIndex);
    } // else
} // $A2


/**
 * void printInheritance (Object obj)
 * Display inheritance tree of object.
 */
function printInheritance (obj)
{
    var str = obj.constructor.getInheritance ("\n  ");

    alert (str);
} // printInheritance


/**
 * boolean equals (Object o1, Object o2)
 * Check if the two objects are equal.
 */
function equals (o1, o2)
{
    return (o1 == null ? o2 == null : o1.equals (o2));
} // equals


/**
 * boolean equalsNotNull (Object x, Object y)
 * Check for equality of non-null reference x and possibly-null y.
 */
function equalsNotNull (x, y)
{
    return (x == y || x.equals (y));
} // equalsNotNull
