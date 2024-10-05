/*
 * Class: IE302.java
 */

// package:
package ibs.tech.html;

import ibs.io.IOConstants;

// imports:


/******************************************************************************
 * This is the IE302 Object, which holds all Strings used to construct
 * a HTML-Page the browser Internet Explorer 3.02 understands.
 *
 * @version     $Id: IE302.java,v 1.20 2009/11/19 13:57:30 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980315
 ******************************************************************************
 */
public class IE302 extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IE302.java,v 1.20 2009/11/19 13:57:30 btatzmann Exp $";


    // HTML characters
    /**
     * HTML character &amp;. <BR/>
     */
    public static final String HCH_AMP       = new String ("&amp;");
    /**
     * HTML character &lt;. <BR/>
     */
    public static final String HCH_LT        = new String ("&lt;");
    /**
     * HTML character &gt;. <BR/>
     */
    public static final String HCH_GT        = new String ("&gt;");
    /**
     * HTML character &nbsp;. <BR/>
     */
    public static final String HCH_NBSP      = new String ("&nbsp;");
    /**
     * HTML character &apos;. <BR/>
     */
    public static final String HCH_APOS      = new String ("&apos;");
    /**
     * HTML character &quot;. <BR/>
     */
    public static final String HCH_QUOT      = new String ("&quot;");
    /**
     * HTML character &lsquo;. <BR/>
     */
    public static final String HCH_LSQUO     = new String ("&lsquo;");


    // JavaScript
    /**
     * JavaScript call: begin. <BR/>
     */
    protected static final String JS_BEGIN              =  IOConstants.URL_JAVASCRIPT;
    /**
     * JavaScript call: open window. <BR/>
     */
    protected static final String JS_OPENBEGIN          =  new String ("self.open ('");
    /**
     * JavaScript call: separator between function arguments. <BR/>
     */
    protected static final String JS_OPENBETWEEN        =  new String ("','");
    /**
     * JavaScript call: end of function call. <BR/>
     */
    protected static final String JS_OPENEND            =  new String ("');");
    /**
     * JavaScript: line comment. <BR/>
     */
    protected static final String JS_COMMENT            =  new String ("//");


    // NOCACHE
    /**
     * META tag: No caching. <BR/>
     */
//    protected static final String TAG_NO_CACHE          =  new String ("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");

    /**
     * META tag: No caching, text/html, encoding. <BR/>
     */
    protected static final String META_TAG              =  new String ("<META http-equiv=\"Content-Type\" CONTENT=\"no-cache; text/html; charset=UTF-8\">");

    // Essential
    /**
     * Basic constant: end of tag. <BR/>
     */
    protected static final String TO_TAGEND             =  new String (">");
    /**
     * Basic constant: auto scrolling. <BR/>
     */
    protected static final String TO_SCROLLING          =  new String ("AUTO");
    /**
     * Basic constant: no scrolling. <BR/>
     */
    protected static final String TO_NOSCROLLING        =  new String ("NO");
    /**
     * Basic constant: blank space. <BR/>
     */
    protected static final String TO_BLANK              =  IE302.HCH_NBSP;
    /**
     * Basic constant: selected (within selection box). <BR/>
     */
    protected static final String TO_SELECTED           =  new String (" SELECTED");
    /**
     * Basic constant: multiple selection (within selction box). <BR/>
     */
    protected static final String TO_MULTIPLE           =  new String (" MULTIPLE");
    /**
     * Basic constant: element is checked (within checkbox). <BR/>
     */
    protected static final String TO_CHECKED            =  new String (" CHECKED");
    /**
     * Basic constant: beginning of comment. <BR/>
     */
    protected static final String TAG_COMMENTBEGIN      =  new String (" <!--");
    /**
     * Basic constant: end of comment. <BR/>
     */
    protected static final String TAG_COMMENTEND        =  new String (" -->");


    // Used for more than one Tag
    /**
     * Tag attribute: name. <BR/>
     */
    protected static final String TA_NAME               =  new String (" NAME=");
    /**
     * Tag attribute: class id. <BR/>
     */
    protected static final String TA_CLASSID            =  new String (" CLASS=");
    /**
     * Tag attribute: title. <BR/>
     */
    protected static final String TA_TITLE              =  new String (" TITLE=");
    /**
     * Tag attribute: id. <BR/>
     */
    protected static final String TA_ID                 =  new String (" ID=");
    /**
     * Tag attribute: width. <BR/>
     */
    protected static final String TA_WIDTH              =  new String (" WIDTH=");
    /**
     * Tag attribute: height. <BR/>
     */
    protected static final String TA_HEIGHT             =  new String (" HEIGHT=");
    /**
     * Tag attribute: border. <BR/>
     */
    protected static final String TA_BORDER             =  new String (" BORDER=");
    /**
     * Tag attribute: src. <BR/>
     */
    protected static final String TA_SRC                =  new String (" SRC=");
    /**
     * Tag attribute: alignment. <BR/>
     */
    protected static final String TA_ALIGN              =  new String (" ALIGN=");
    /**
     * Tag attribute: vertical alignment. <BR/>
     */
    protected static final String TA_VALIGN             =  new String (" VALIGN=");
    /**
     * Tag attribute: background color. <BR/>
     */
    protected static final String TA_BGCOLOR            =  new String (" BGCOLOR=");
    /**
     * Tag attribute: background image. <BR/>
     */
    protected static final String TA_BGIMAGE            =  new String (" BACKGROUND=");
    /**
     * Tag attribute: scrolling. <BR/>
     */
    protected static final String TA_SCROLLING          =  new String (" SCROLLING=");
    /**
     * Tag attribute: border for frames. <BR/>
     */
    protected static final String TA_FRAMEBORDER        =  new String (" FRAMEBORDER=");
    /**
     * Tag attribute: number of columns. <BR/>
     */
    protected static final String TA_COLS               =  new String (" COLS=");
    /**
     * Tag attribute: number of rows. <BR/>
     */
    protected static final String TA_ROWS               =  new String (" ROWS=");
    /**
     * Tag attribute: readonly flag. <BR/>
     */
    protected static final String TA_READONLY           =  new String (" READONLY=");
    /**
     * Tag attribute: disabled. <BR/>
     */
    protected static final String TA_DISABLED           =  new String (" DISABLED=");
    /**
     * Tag attribute: style. <BR/>
     */
    protected static final String TA_STYLE           =  new String (" STYLE=");

    // <HTML>
    /**
     * DOCTYPE definition: start. <BR/>
     */
    protected static final String DOCTYPE_HTML_START = "<!DOCTYPE html PUBLIC ";

    /**
     * The doctype for strict html. <BR/>
     * <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
     *    "http://www.w3.org/TR/html4/strict.dtd">
     */
    protected static final String DOCTYPE_HTML41_STRICT_MODE = new String (
        IE302.DOCTYPE_HTML_START +
        " \"-//W3C//DTD HTML 4.01//EN\"" +
        " \"http://www.w3.org/TR/html4/strict.dtd\">");

    /**
     * The doctype for tranisational html. <BR/>
     * <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
     * "http://www.w3.org/TR/html4/loose.dtd">
     */
    protected static final String DOCTYPE_HTML41_TRANSITIONAL_MODE =  new String (
        IE302.DOCTYPE_HTML_START +
        " \"-//W3C//DTD HTML 4.01 Transitional//EN\"" +
        " \"http://www.w3.org/TR/html4/loose.dtd\">");

    /**
     * The doctype for frameset html. <BR/>
     * <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN"
     *  "http://www.w3.org/TR/html4/frameset.dtd">
     */
    protected static final String DOCTYPE_HTML41_FRAMESET_MODE =  new String (
        IE302.DOCTYPE_HTML_START +
        " \"-//W3C//DTD HTML 4.01 Frameset//EN\"" +
        " \"http://www.w3.org/TR/html4/frameset.dtd\">");

    /**
     * The doctype for frameset html. <BR/>
     * <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
     */
    protected static final String DOCTYPE_HTML32 =  new String (
        IE302.DOCTYPE_HTML_START +
        " \"-//W3C//DTD HTML 3.2 Final//EN\">");

    /**
     * HTML tag: page begin that enables the QUIRKS mode. <BR/>
     */
    protected static final String TAG_PAGEBEGIN         =  new String ("<HTML>");

    /**
     * HTML tag: page end. <BR/>
     */
    protected static final String TAG_PAGEEND           =  new String ("</HTML>");

    // <FRAME>
    /**
     * FRAME tag: begin. <BR/>
     */
    protected static final String TAG_FRAMEBEGIN        =  new String ("<FRAME");
    /**
     * FRAME tag: margin height. <BR/>
     */
    protected static final String TA_MARGINHEIGHT       =  new String (" MARGINHEIGHT=");
    /**
     * FRAME tag: margin width. <BR/>
     */
    protected static final String TA_MARGINWIDTH        =  new String (" MARGINWIDTH=");
    /**
     * FRAME tag: no resizing. <BR/>
     */
    protected static final String TA_NORESIZE           =  new String (" NORESIZE");

    // <FRAMESET>
    /**
     * FRAMESET tag: begin. <BR/>
     */
    protected static final String TAG_FRAMESETBEGIN     =  new String ("<FRAMESET");
    /**
     * FRAMESET tag: end tag. <BR/>
     */
    protected static final String TAG_FRAMESETEND       =  new String ("</FRAMESET>");
    /**
     * FRAMESET tag: rows. <BR/>
     */
    protected static final String TA_FRAMEROWS          =  IE302.TA_ROWS;
    /**
     * FRAMESET tag: columns. <BR/>
     */
    protected static final String TA_FRAMECOLS          =  IE302.TA_COLS;
    /**
     * FRAMESET tag: spacing between frames. <BR/>
     */
    protected static final String TA_FRAMESPACING       =  new String (" FRAMESPACING=");

    // <BODY>
    /**
     * BODY tag: begin. <BR/>
     */
    protected static final String TAG_BODYBEGIN         =  new String ("<BODY");
    /**
     * BODY tag: end tag. <BR/>
     */
    protected static final String TAG_BODYEND           =  new String ("</BODY>");
    /**
     * BODY tag: onload attribute. <BR/>
     */
    protected static final String TA_ONLOAD             =  new String (" ONLOAD=");
    /**
     * BODY tag: onunload attribute. <BR/>
     */
    protected static final String TA_ONUNLOAD           =  new String (" ONUNLOAD=");
    /**
     * BODY tag: text color. <BR/>
     */
    protected static final String TA_TEXTCOLOR          =  new String (" TEXT=");
    /**
     * BODY tag: link color. <BR/>
     */
    protected static final String TA_LINKCOLOR          =  new String (" LINK=");
    /**
     * BODY tag: a link color. <BR/>
     */
    protected static final String TA_ALINKCOLOR         =  new String (" ALINK=");
    /**
     * BODY tag: v link color. <BR/>
     */
    protected static final String TA_VLINKCOLOR         =  new String (" VLINK=");

    // <HEAD>
    /**
     * HEAD tag: begin. <BR/>
     */
    protected static final String TAG_HEADBEGIN         =  new String ("<HEAD>");
    /**
     * HEAD tag: end tag. <BR/>
     */
    protected static final String TAG_HEADEND           =  new String ("</HEAD>");

    // <TITLE>
    /**
     * TITLE tag: begin. <BR/>
     */
    protected static final String TAG_TITLEBEGIN        =  new String ("<TITLE>");
    /**
     * TITLE tag: end tag. <BR/>
     */
    protected static final String TAG_TITLEEND          =  new String ("</TITLE>");

    // <FONT>
    /**
     * FONT tag: begin. <BR/>
     */
    protected static final String TAG_FONTBEGIN         =  new String ("<FONT");
    /**
     * FONT tag: end tag. <BR/>
     */
    protected static final String TAG_FONTEND           =  new String ("</FONT>");
    /**
     * FONT tag: size. <BR/>
     */
    protected static final String TA_SIZE               =  new String (" SIZE=");
    /**
     * FONT tag: color. <BR/>
     */
    protected static final String TA_COLOR              =  new String (" COLOR=");
    /**
     * FONT tag: face. <BR/>
     */
    protected static final String TA_FACE               =  new String (" FACE=");
    /**
     * B tag: begin. <BR/>
     */
    public static final String TAG_BOLDBEGIN         =  new String ("<B>");
    /**
     * B tag: end tag. <BR/>
     */
    public static final String TAG_BOLDEND           =  new String ("</B>");
    /**
     * I tag: begin. <BR/>
     */
    protected static final String TAG_ITALICBEGIN       =  new String ("<I>");
    /**
     * I tag: end tag. <BR/>
     */
    protected static final String TAG_ITALICEND         =  new String ("</I>");
    /**
     * U tag: begin. <BR/>
     */
    protected static final String TAG_UNDERBEGIN        =  new String ("<U>");
    /**
     * U tag: end tag. <BR/>
     */
    protected static final String TAG_UNDEREND          =  new String ("</U>");

    // <IMAGE>
    /**
     * IMAGE tag: begin. <BR/>
     */
    protected static final String TAG_IMAGEBEGIN        =  new String ("<IMG");
    /**
     * IMAGE tag: alt text. <BR/>
     */
    protected static final String TA_ALT                =  new String (" ALT=");
    /**
     * IMAGE tag: vertical spacing. <BR/>
     */
    protected static final String TA_VSPACE             =  new String (" VSPACE=");
    /**
     * IMAGE tag: horizontal spacing. <BR/>
     */
    protected static final String TA_HSPACE             =  new String (" HSPACE=");

    // <COL>
    /**
     * COLGROUP tag: begin. <BR/>
     */
    protected static final String TAG_COLGROUPBEGIN     =  new String ("<COLGROUP>");
    /**
     * COLGROUP tag: end tag. <BR/>
     */
    protected static final String TAG_COLGROUPEND       =  new String ("</COLGROUP>");
    /**
     * COL tag: begin. <BR/>
     */
    protected static final String TAG_COLBEGIN2         =  new String ("<COL");
    /**
     * COLGROUP/COL tag: begin. <BR/>
     * BB TODO: This is not a proper solution and does lead to confusion because
     * is does not follow the standard name schema!
     */
    protected static final String TAG_COLBEGIN          =  IE302.TAG_COLGROUPBEGIN + IE302.TAG_COLBEGIN2;
    /**
     * COLGROUP tag: end tag. <BR/>
     * BB TODO: This is not a proper solution and does lead to confusion because
     * is does not follow the standard name schema!
     */
    protected static final String TAG_COLEND            =  IE302.TAG_COLGROUPEND;

    // <DIV>
    /**
     * DIV tag: begin. <BR/>
     */
    protected static final String TAG_DIVBEGIN          =  new String ("<DIV");
    /**
     * DIV tag: end tag. <BR/>
     */
    protected static final String TAG_DIVEND            =  new String ("</DIV>");

    // <P>
    /**
     * P tag: begin. <BR/>
     */
    protected static final String TAG_PBEGIN            =  new String ("<P");
    /**
     * P tag: end tag. <BR/>
     */
    protected static final String TAG_PEND              =  new String ("</P>");

    // <TEXTAREA>
    /**
     * TEXTAREA tag: begin. <BR/>
     */
    protected static final String TAG_TEXTAREABEGIN     =  new String ("<TEXTAREA");
    /**
     * TEXTAREA tag: end tag. <BR/>
     */
    protected static final String TAG_TEXTAREAEND       =  new String ("</TEXTAREA>");

    // <FORM>
    /**
     * FORM tag: begin. <BR/>
     */
    protected static final String TAG_FORMBEGIN         =  new String ("<FORM");
    /**
     * FORM tag: end tag. <BR/>
     */
    protected static final String TAG_FORMEND           =  new String ("</FORM>");
    /**
     * FORM tag: action. <BR/>
     */
    protected static final String TA_ACTION             =  new String (" ACTION=");
    /**
     * FORM tag: method. <BR/>
     */
    protected static final String TA_METHOD             =  new String (" METHOD=");
    /**
     * FORM tag: onsubmit. <BR/>
     */
    protected static final String TA_ONSUBMIT           =  new String (" OnSubmit=");

    // <INPUT>
    /**
     * INPUT tag: begin. <BR/>
     */
    protected static final String TAG_INPUTBEGIN        =  new String ("<INPUT");
    /**
     * BUTTON tag: begin. <BR/>
     */
    protected static final String TAG_BUTTONBEGIN        =  new String ("<BUTTON");
    /**
     * BUTTON tag: ed. <BR/>
     */
    protected static final String TAG_BUTTONEND        =  new String ("</BUTTON>");
    /**
     * INPUT tag: value. <BR/>
     */
    protected static final String TA_VALUE              =  new String (" VALUE=");
    /**
     * INPUT tag: type. <BR/>
     */
    protected static final String TA_TYPE               =  new String (" TYPE=");
    /**
     * INPUT tag: maximum length. <BR/>
     */
    protected static final String TA_MAXLENGTH          =  new String (" MAXLENGTH=");
    /**
     * INPUT tag: wrapping. <BR/>
     */
    protected static final String TA_WRAP               =  new String (" WRAP=");
    /**
     * INPUT tag: encryption type. <BR/>
     */
    protected static final String TA_ENCTYPE            =  new String (" ENCTYPE=");
    /**
     * INPUT tag: onblur event handler. <BR/>
     */
    protected static final String TA_ONBLUR             =  new String (" ONBLUR=");
    /**
     * INPUT tag: onchange event handler. <BR/>
     */
    protected static final String TA_ONCHANGE           =  new String (" ONCHANGE=");
    /**
     * INPUT tag: onclick event handler. <BR/>
     */
    protected static final String TA_ONCLICK            =  new String (" ONCLICK=");
    /**
     * INPUT tag: onfocus event handler. <BR/>
     */
    protected static final String TA_ONFOCUS            =  new String (" ONFOCUS=");
    /**
     * INPUT tag: onselect event handler. <BR/>
     */
    protected static final String TA_ONSELECT           =  new String (" ONSELECT=");
    /**
     * INPUT tag: onmouseover event handler. <BR/>
     */
    protected static final String TA_ONMOUSEOVER        =  new String (" ONMOUSEOVER=");
    /**
     * INPUT tag: onmouseover event handler. <BR/>
     */
    protected static final String TA_ONDBLCLICK        =  new String (" ONDBLCLICK=");

    // <BR/>
    /**
     * BR tag: begin + end. <BR/>
     */
    public static final String TAG_NEWLINE           =  new String ("<BR/>");

    // <TABLE>
    /**
     * TABLE tag: border color. <BR/>
     */
    protected static final String TA_BORDERCOLOR        =  new String (" BORDERCOLOR=");
    /**
     * TABLE tag: dark border color. <BR/>
     */
    protected static final String TA_BORDERCOLORDARK    =  new String (" BORDERCOLORDARK");
    /**
     * TABLE tag: light border color. <BR/>
     */
    protected static final String TA_BORDERCOLORLIGHT   =  new String (" BORDERCOLORLIGHT");
    /**
     * TABLE tag: begin. <BR/>
     */
    protected static final String TAG_TABLEBEGIN        =  new String ("<TABLE");
    /**
     * TABLE tag: begin with border, cellpadding and cellspacing set to
     * <CODE>0</CODE>. <BR/>
     */
    protected static final String TAG_DEFAULTTABLEBEGIN =
        "<TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\"";
    /**
     * TABLE tag: end tag. <BR/>
     */
    protected static final String TAG_TABLEEND          =  new String ("</TABLE>");
    /**
     * TABLE tag: frame. <BR/>
     */
    protected static final String TA_FRAMETYPE          =  new String (" FRAME=");
    /**
     * TABLE tag: cell padding. <BR/>
     */
    protected static final String TA_CELLPADDING        =  new String (" CELLPADDING=");
    /**
     * TABLE tag: cell spacing. <BR/>
     */
    protected static final String TA_CELLSPACING        =  new String (" CELLSPACING=");
    /**
     * THEAD tag: begin. <BR/>
     */
    protected static final String TAG_TABLEHEADBEGIN    =  new String ("<THEAD>");
    /**
     * THEAD tag: end tag. <BR/>
     */
    protected static final String TAG_TABLEHEADEND      =  new String ("</THEAD>");
    /**
     * TBODY tag: begin. <BR/>
     */
    protected static final String TAG_TABLEBODYBEGIN    =  new String ("<TBODY>");
    /**
     * TBODY tag: end tag. <BR/>
     */
    protected static final String TAG_TABLEBODYEND      =  new String ("</TBODY>");
    /**
     * TD tag: rules. <BR/>
     */
    protected static final String TA_RULETYPE           =  new String (" RULES=");
    /**
     * TD tag: colspan. <BR/>
     */
    protected static final String TA_COLSPAN            =  new String (" COLSPAN=");
    /**
     * TD tag: row span. <BR/>
     */
    protected static final String TA_ROWSPAN            =  new String (" ROWSPAN=");
    /**
     * TABLE tag: wrapping. <BR/>
     */
    protected static final String TA_NOWRAP             =  new String (" NOWRAP");

    // <SELECT>
    /**
     * SELECT tag: begin. <BR/>
     */
    protected static final String TAG_SELECTBEGIN       =  new String ("<SELECT");
    /**
     * SELECT tag: end tag. <BR/>
     */
    protected static final String TAG_SELECTEND         =  new String ("</SELECT>");

    // <OPTION>
    /**
     * OPTION tag: begin. <BR/>
     */
    protected static final String TAG_OPTIONBEGIN       =  new String ("<OPTION");
    /**
     * OPTION tag: end tag. <BR/>
     */
    protected static final String TAG_OPTIONEND         =  new String ("</OPTION>");

    // <TR>
    /**
     * TR tag: begin. <BR/>
     */
    protected static final String TAG_TABLEROWBEGIN     =  new String ("<TR");
    /**
     * TR tag: end tag. <BR/>
     */
    protected static final String TAG_TABLEROWEND       =  new String ("</TR>");

    // <TD>
    /**
     * TD tag: begin. <BR/>
     */
    protected static final String TAG_TABLECELLBEGIN    =  new String ("<TD");
    /**
     * TD tag: end tag. <BR/>
     */
    protected static final String TAG_TABLECELLEND      =  new String ("</TD>");

    // <SCRIPT>
    /**
     * SCRIPT tag: begin. <BR/>
     */
    protected static final String TAG_SCRIPTBEGIN       =  new String ("<SCRIPT");
    /**
     * SCRIPT tag: end tag. <BR/>
     */
    protected static final String TAG_SCRIPTEND         =  new String ("</SCRIPT>");
    /**
     * SCRIPT tag: language. <BR/>
     */
    protected static final String TA_LANGUAGE           =  new String (" LANGUAGE=");

    // <SPAN>
    /**
     * SPAN tag: begin. <BR/>
     */
    protected static final String TAG_SPANBEGIN       =  new String ("<SPAN");
    /**
     * SPAN tag: end tag. <BR/>
     */
    protected static final String TAG_SPANEND         =  new String ("</SPAN>");

    // <A HREF="">
    /**
     * A tag: begin. <BR/>
     */
    protected static final String TAG_LINKBEGIN         = new String ("<A");
    /**
     * A tag: end tag. <BR/>
     */
    protected static final String TAG_LINKEND           = new String ("</A>");
    /**
     * A tag: href. <BR/>
     */
    protected static final String TA_HREF               = new String (" HREF=");
    /**
     * A tag: target window or frame. <BR/>
     */
    protected static final String TA_TARGET             = new String (" TARGET=");

    // <CENTER>
    /**
     * CENTER tag: begin. <BR/>
     */
    protected static final String TAG_CENTERBEGIN       = new String ("<CENTER");
    /**
     * CENTER tag: end tag. <BR/>
     */
    protected static final String TAG_CENTEREND         = new String ("</CENTER>");


    // <APPLET>
    /**
     * APPLET tag: begin. <BR/>
     */
    protected static final String TAG_APPLETBEGIN       =  new String ("<APPLET");
    /**
     * APPLET tag: end tag. <BR/>
     */
    protected static final String TAG_APPLETEND         =  new String ("</APPLET>");
    /**
     * APPLET tag: code. <BR/>
     */
    protected static final String TA_CODEBEGIN          =  new String (" CODE=");
    /**
     * PARAM tag: begin. <BR/>
     */
    protected static final String TAG_PARAMBEGIN        =  new String ("<PARAM");


    // <OBJECT>
    /**
     * OBJECT tag: begin. <BR/>
     */
    protected static final String TAG_OBJECTBEGIN       =  new String ("<OBJECT");
    /**
     * OBJECT tag: end tag. <BR/>
     */
    protected static final String TAG_OBJECTEND         =  new String ("</OBJECT>");
    /**
     * OBJECT tag: code base. <BR/>
     */
    protected static final String TA_CODEBASE           =  new String (" CODEBASE=");
    /**
     * OBJECT tag: data. <BR/>
     */
    protected static final String TA_DATA               =  new String (" DATA=");
    /**
     * OBJECT tag: standby. <BR/>
     */
    protected static final String TA_STANDBY            =  new String (" STANDBY=");


    // <HR>
    /**
     * HR tag: begin. <BR/>
     */
    protected static final String TAG_HR                =  new String ("<HR");

    // <PRE>
    /**
     * PRE tag: begin. <BR/>
     */
    protected static final String TAG_PREBEGIN          =  new String ("<PRE");
    /**
     * PRE tag: begin. <BR/>
     */
    public static final String TAG_PRE =
        new String (IE302.TAG_PREBEGIN + IE302.TO_TAGEND);
    /**
     * PRE tag: end tag. <BR/>
     */
    public static final String TAG_PREEND            =  new String ("</PRE>");


    // <STYLE>
    /**
     * STYLE tag: begin. <BR/>
     */
    protected static final String TAG_STYLEBEGIN        =  new String ("<STYLE TYPE=\"text/css\"");
    /**
     * STYLE tag: end tag. <BR/>
     */
    protected static final String TAG_STYLEEND          =  new String ("</STYLE>");
    /**
     * STYLE tag: start style. <BR/>
     */
    protected static final String CSS_BEGIN             =  new String (" {");
    /**
     * STYLE tag: end style. <BR/>
     */
    protected static final String CSS_END               =  new String ("}; ");
    /**
     * LINK tag: import stylesheet. <BR/>
     */
    protected static final String CSS_IMPORTBEGIN       =
        new String ("<LINK REL=STYLESHEET TYPE=\"text/css\" HREF=\"");
    /**
     * LINK tag: end of import. <BR/>
     */
    protected static final String CSS_IMPORTEND         =  new String ("\">");


    // MENU
    /**
     * menu: beginning. <BR/>
     */
    protected static final String MENU_BEGIN = new String (
        "<TABLE CELLPADDING=\"0\" CELLSPACING=\"0\"");
    /**
     * menu: caption begin. <BR/>
     */
    protected static final String MENU_CAPBEGIN = new String (
        "<TR><TD COLSPAN=2>" + IE302.HCH_NBSP + IE302.TAG_BOLDBEGIN);
    /**
     * menu: caption end. <BR/>
     */
    protected static final String MENU_CAPEND = new String ("</B></TD></TR>");
    /**
     * menu: item begin. <BR/>
     */
    protected static final String ITEM_BEGIN = new String (
        "<TR><TD WIDTH=15>&nbsp</TD><TD");

    /**
     * menu: item end. <BR/>
     */
    protected static final String ITEM_END = new String ("</TD></TR>");

    /**
     * menu: menu end. <BR/>
     */
    protected static final String MENU_END = IE302.TAG_TABLEEND;


    // DISCUSSION
    /**
     * discussion: empty cell. <BR/>
     */
    protected static final String EMPTY_CELL = new String ("<TD WIDTH=\"5%\">" +
        IE302.HCH_NBSP + IE302.TAG_TABLECELLEND);

} // class IE302
