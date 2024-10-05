/******************************************************************************
 * All JavaScript code which is necessary for handling client-side form
 * validation. <BR/>
 *
 * @version     $Id: formvalidation.js,v 1.38 2012/07/18 09:21:11 btatzmann Exp $
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

//============= declarations ==================================================

var CL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
var CN = "0123456789";
var TNA = "<>" + "\'\"";
var NNA = TNA + "\"\\#";
var SNA = CL + CN + "_";
var IA = "+-" + CN;
var FA = IA + ".";
var DA = CN + SD;
var TA = CN + ST;
var MI = -2147483648;
var MA = 2147483647;
var _msg = "";
var _aF = null;
var _fV = false;
var _eC = 0;
var _aW = top;
/* The following pattern is used to check if the entered e-mail address
   fits the user@domain format.  It also is used to separate the username
   from the domain. */
var emailPat=/^(.+)@(.+)$/;
/* The following string represents the pattern for matching all special
   characters.  We don't want to allow special characters in the address. 
   These characters include ( ) < > @ , ; : \ " . [ ]    */
var specialChars="\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
/* The following string represents the range of characters allowed in a 
   username or domainname.  It really states which chars aren't allowed. */
var validChars="\[^\\s" + specialChars + "\]";
/* The following pattern applies if the "user" is a quoted string (in
   which case, there are no rules about which characters are allowed
   and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
   is a legal e-mail address. */
var quotedUser="(\"[^\"]*\")";
/* The following pattern applies for domains that are IP addresses,
   rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
   e-mail address. NOTE: The square brackets are required. */
var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
/* The following string represents an atom (basically a series of
   non-special characters.) */
var atom=validChars + '+';
/* The following string represents one word in the typical username.
   For example, in john.doe@somewhere.com, john and doe are words.
   Basically, a word is either an atom or quoted string. */
var word="(" + atom + "|" + quotedUser + ")";
// The following pattern describes the structure of the user
var userPat=new RegExp("^" + word + "(\\." + word + ")*$");
/* The following pattern describes the structure of a normal symbolic
   domain, as opposed to ipDomainPat, shown above. */
var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");


//============= common functions ==============================================

// strings
function mRef (oidf, ff, eA)
{
    if (!eA && (oidf.value=='' || oidf.value=='0x0000000000000000'))
    {
        alert (top.multilang.ibs_ibsbase_formvalidation_mRef);
        focus (ff);
        ff.select ();
        return false;
    }

    return true;
}

function co(s, cs)
{
    var i = 0;
    var fd = false;
    for (i = 0; !fd && i < cs.length; i++)
    {
        if (s.indexOf(cs.charAt(i)) >= 0)
            fd = true;
    }
    return fd;
}
function coO(s, cs)
{
    var i = 0;
    var ok = true;
    for (i = 0; ok && i < s.length; i++)
    {
        if (cs.indexOf(s.charAt(i)) < 0)
            ok = false;
    }
    return ok;
}

function _dOS(dS, of)
{
    var d = createDate ();
    if (_iDS(dS, d))
        d.setTime (d.getTime() + of * (24 * 60 * 60 * 1000));
    return (dateToString (d));
}
function _iDS(dS, d)
{
    var i = 0;
    var ok = true;
    var newDate = createDate ();
    ok = stringToDate (dS, newDate);
    if (ok)
    {
        d.setTime (newDate.getTime ());
    } // if
    return (ok);
}
function _iE(f, eA)
{
    if (f == null || f.value.length == 0)
    {
        if (eA == null || !eA)
            _msg += top.multilang.ibs_ibsbase_formvalidation_emp;
        return true;
    }
    return false;
}
function _iV(f, cs, mC, eA)
{
    if (_iE(f, eA))
        if (eA != null && eA == false)
            return false;
        else
            return true;
    if (mC)
        return coO(f.value, cs);
    else
        return !co(f.value, cs);
}

function _iTx(f, eA)
{
    return (_iV(f, TNA, false, eA));
}
function _iN(f, eA)
{
    trimField(f);
    return (_iV(f, NNA, false, eA));
}
function _iSN(f, eA)
{
    trimField(f);
    return (_iV(f, SNA, true, eA) && CL.indexOf(f.value.charAt(0)) >= 0);
}
function _iI(f, eA)
{
    trimField(f);
    if (_iE(f, eA))
        return eA;
    if (!_iV(f, IA, true, eA))
        return false;
    trimFieldZero(f);
    if ("" + parseInt (f.value, 10) != f.value)
        return false;
    return true;
}
function _iIR(f, mn, mx, eA)
{
    if (!_iI(f, eA))
        return false;
    else if (f.value.length == 0)       // empty allowed field is empty?
    {
        return true;
    } // else if empty allowed field is empty
    var v = parseInt (f.value, 10);
    if (isNaN (v) || (mn != null && v < mn) || (mx != null && v > mx))
        return false;
    return true;
}
function _iF(f, eA)
{
    trimFieldFloat (f);
    if (_iE(f, eA))
        return eA;
    if ("" + parseFloat(f.value) != f.value)
        return false;
    return true;
}
function _iFR(f, mn, mx, eA)
{
    if (!_iF(f, eA))
        return false;
    else if (f.value.length == 0)       // empty allowed field is empty?
    {
        return true;
    } // else if empty allowed field is empty
    var v = parseFloat(f.value);
    if (isNaN (v) || (mn != null && v < mn) || (mx != null && v > mx))
        return false;
    return true;
}

function _iM(f, eA)
{
    trimField (f);
    if (_iE(f, eA))
        return eA;
    var val = stringToMoney (f.value);

    if (!isNaN (val))
    {
        // convert money value back to string:
        f.value = moneyToString (val);
        return true;
    } // if
    else
    {
        return false;
    } // else
} // _iM
function _iD(f, eA)
{
    var d = createDate ();
    trimField (f);
    if (_iE (f, eA))
        return eA;
    if (!_iV (f, DA, true, eA) || !_iDS (f.value, d))
        return false;
    f.value = dateToString (d);
    return true;
0}

/**
 * void _iDBO (FormField f, String dS1, String dS2, float of, boolean eA)
 * _isDateBeforeOffset checks if the field content is a date before a specific
 * date plus the offset in days.
 */
function _iDBO(f, dS1, dS2, of, eA)
{
    var d1 = createDate ();
    var d2 = createDate ();
    if (_iE (f, eA))
        return eA;
    if (_iDS (dS1, d1))
    {
        if (!_iDS (dS2, d2))
            return a (false, f, top.multilang.ibs_ibsbase_formvalidation_dat);
    }
    else
        return a (false, f, top.multilang.ibs_ibsbase_formvalidation_dat);
    if (f.value == dS1)
        f.value = dateToString (d1);
    else
        f.value = dateToString (d2);
    return d1.getTime () <= (d2.getTime () + of * (24 * 60 * 60 * 1000));
}

function _iT(f, eA)
{
    var d = createDate ();
    trimField (f);
    if (_iE (f, eA))
        return eA;
    if (!_iV (f, TA, true, eA))
        return false;
    if (!stringToTime (f.value, d))
        return false;
    f.value = timeToString (d);
    return true;
}
function _iS(f, f2)
{
    return f.value == f2.value;
}
function _iLE(f, l)
{
    return f.value.length == l;
}
function _iLR(f, mn, mx)
{
    if ((mn != null && f.value.length < mn) || (mx != null && f.value.length > mx))
        return false;
    return true;
}
/*! function not needed now
function fIB(f)
{
    return f.value == null || f.value.length == 0;
}
*/
function iFile(f, ff, eA)
{
    var ok = (_iTx(f, eA) || _iTx(ff, eA));
    return a(ok, f, top.multilang.ibs_ibsbase_formvalidation_tex);
}

function iFileExt(f, fff)
{
    var ok = (_iFileExt(f, fff));
    return a(ok, f, top.multilang.ibs_ibsbase_formvalidation_iFta);
    //return true;
}

function _iFileExt(f, fff)
{
    // check if allowed file types is set
    if(fff.value.length <= 0 || f.value.length <= 0)
    {
        // no restriction
        return true;
    } // if
 
    // extArray = new Array(".gif";".jpg";".png");
    var extArray = fff.value.split(",");
    
    // get selected file
    var file = f.value;
    
    // while path
    while (file.indexOf("\\") != -1)
    {
        // get filename
        file = file.slice(file.indexOf("\\") + 1);
    } // while
    
    // get file extension
    var fExt = file.slice(file.indexOf(".")).toLowerCase();
    
    // loop through allowed file types
    for (var i = 0; i < extArray.length; i++) 
    {
        // replace whitespaces and '*'
        extArray[i] = (extArray[i].toString()).replace(/^\s*|\s*$/g,'').replace("*", "");
        
        // check if file type is allowed
        if (extArray[i] == fExt)
        {
            // file type is allowed
            return true;
        } // if
    } // for
    return false;
} // 

function a(ok, f, msg)
{
    if (!_fV && !ok)
//    if (!_fV && !ok && (_aF == null || _aF == f))
    {
        _aF = f;
        if (_msg != "")
            msg = _msg + "\n" + msg;
        if (_aW)
            _aW.alert (msg);
        else
            alert (msg);

        focus (f);
        f.select ();
    }
    else if (_aF == f)
        _aF = null;
    if (!_fV)
        _msg = "";
    return ok;
}

function iNE(f, eA)
{    
    if (!eA)
    {
        if (_iE(f, eA))
        {
            alert (_msg);
            focus (f);
            f.select ();
            return false;
        }
    }
    return true;
}

function iTx(f, eA)
{
    return a(_iTx(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_tex);
}
/*! function not needed now
function iN(f, eA)
{
    return a(_iN(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_nam);
}
function iSN(f, eA)
{
    return a(_iSN(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_sys);
}
*/
function iI(f, eA)
{
    return a(_iI(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_num);
}
function fVWiI (f, eA, win)
{
    var aWOld = _aW;
    var r = false;
    _aW = win;
    r = iI (f, eA);
    _aW = aWOld;
    return r;
} // fVWiI
function iIR(f, mn, mx, eA)
{
    if (mn < MI)
        mn = MI;
    if (mx > MA)
        mx = MA;
    return a(_iIR(f, mn, mx, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_nus + mn + 
    		 top.multilang.ibs_ibsbase_formvalidation_bis + mx + 
    		 top.multilang.ibs_ibsbase_formvalidation_con);
}
function iIGE(f, mn, eA)
{
    if (mn < MI)
       mn = MI;
   return a(_iIR(f, mn, null, eA), f, 
		    top.multilang.ibs_ibsbase_formvalidation_nbi + mn + 
		    top.multilang.ibs_ibsbase_formvalidation_be);
}
function iILE(f, mx, eA)
{
    if (mx > MA)
        mx = MA;
    return a(_iIR(f, null, mx, eA), f, 
             top.multilang.ibs_ibsbase_formvalidation_nsm + mx + 
    		 top.multilang.ibs_ibsbase_formvalidation_be);
}
function iNu(f, eA)
{
    return a(_iF(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_nde);
}
/*! function not needed now
function iNuR(f, mn, mx, eA)
{
    return a(_iFR(f, mn, mx, eA), f, 
             top.multilang.ibs_ibsbase_formvalidation_nes + mn + 
             top.multilang.ibs_ibsbase_formvalidation_be + mx + 
    	     top.multilang.ibs_ibsbase_formvalidation_con);
}
function iNuGE(f, mn, eA)
{
    return a(_iFR(f, mn, null, eA), f, 
             top.multilang.ibs_ibsbase_formvalidation_nbi + mn + 
             top.multilang.ibs_ibsbase_formvalidation_be);
}
function iNuLE(f, mx, eA)
{
    return a(_iFR(f, null, mx, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_nsm + mx + 
             top.multilang.ibs_ibsbase_formvalidation_be);
}
*/
function iM(f, eA)
{
    return a(_iM(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_mon);
} // iM
/**
 * void iD (FormField f)
 * isDate checks if the field content is a date.
 */
function iD(f, eA)
{
    return a(_iD(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_dat);
}
/**
 * void iDB (FormField f, String dS)
 * isDateBefore checks if the field content is a date before a specific date.
 */
function iDB(f, dS, eA)
{
    return iDBO(f, dS, 0, eA);
}
/**
 * void iDA (FormField f, String dS)
 * isDateAfter checks if the field content is a date after a specific date.
 */
function iDA(f, dS, eA)
{
    return iDAO(f, dS, 0, eA);
}
/**
 * void iDBO (FormField f, String dS, float of)
 * isDateBeforeOffset checks if the field content is a date before a specific
 * date plus the offset.
 */
function iDBO(f, dS, of, eA)
{
    return a(_iDBO(f, f.value, dS, of, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_fse + _dOS(dS, of) + 
    		 top.multilang.ibs_ibsbase_formvalidation_be2);
}
/**
 * void iDAO (FormField f, String dS, float of)
 * isDateAfterOffset checks if the field content is a date after a specific
 * date plus the offset.
 */
function iDAO(f, dS, of, eA)
{
    return a(_iDBO(f, dS, f.value, -of, eA), f, 
    		top.multilang.ibs_ibsbase_formvalidation_fbe + _dOS(dS, of) + 
    		top.multilang.ibs_ibsbase_formvalidation_be2);
}
function iT(f, eA)
{
    return a(_iT(f, eA), f, top.multilang.ibs_ibsbase_formvalidation_tim);
}
function iTA (f, tS, eA)
{
    return a (_iTA(f, tS, eA), f, 
    		  top.multilang.ibs_ibsbase_formvalidation_fta + tS + 
    		  top.multilang.ibs_ibsbase_formvalidation_hol);
}
/*! function not needed now
function iTB (f, tS, eA)
{
    return a (_iTB(f, tS, eA), f, 
              top.multilang.ibs_ibsbase_formvalidation_ftb + tS + 
              top.multilang.ibs_ibsbase_formvalidation_hol);
}
function _iTB (f, tS, eA)
{
    var d1 = createDate ();
    var d2 = createDate ();
    trimField (f);
    if (_iE(f, eA))
        return eA;
    if (!_iV(f, TA, true, eA))
        return false;
    if (!stringToTime(f.value, d1))
       return a(false, f, top.multilang.ibs_ibsbase_formvalidation_tim);
    f.value = timeToString (d1);
    if (!stringToTime(tS, d2))
       return a(false, f, top.multilang.ibs_ibsbase_formvalidation_pro);
    return (d1.getTime () < d2.getTime ());
}
*/
function _iTA (f, tS, eA)
{
    var d1 = createDate ();
    var d2 = createDate ();
    trimField (f);
    if (_iE (f, eA))
        return eA;
    if (!_iV (f, TA, true, eA))
        return false;
    if (!stringToTime (f.value, d1))
       return a (false, f, top.multilang.ibs_ibsbase_formvalidation_tim);
    f.value = timeToString (d1);
    if (!stringToTime (tS, d2))
       return a (false, f, top.multilang.ibs_ibsbase_formvalidation_pro);
    return (d1.getTime () > d2.getTime ());
}
function iDTA (dF, dS, tF, tS)
{
    var d1 = createDate ();
    var d2 = createDate ();
    if (!stringToDate (dF.value, d1))
        return a (false, dF, top.multilang.ibs_ibsbase_formvalidation_dat);
    if (!stringToTime (tF.value, d1))
        return a (false, tF, top.multilang.ibs_ibsbase_formvalidation_tim);
    if (!stringToDate (dS, d2))
        return a (false, dF, top.multilang.ibs_ibsbase_formvalidation_prd);
    if (!stringToTime (tS, d2))
        return a (false, tF, top.multilang.ibs_ibsbase_formvalidation_pro);
    if (d1.getTime () <= d2.getTime ())
    {   
        return a (false, dF, 
        		  top.multilang.ibs_ibsbase_formvalidation_fdt + dS + 
        		  top.multilang.ibs_ibsbase_formvalidation_spc + tS + 
        		  top.multilang.ibs_ibsbase_formvalidation_hol);
    }
    else
        return true;
}
/*! function not needed now
function iDTB (dF, dS, tF, tS)
{
    var d1 = createDate ();
    var d2 = createDate ();
    if (!stringToDate (dF.value, d1))
        return a(false, dF, top.multilang.ibs_ibsbase_formvalidation_dat);
    if (!stringToTime (tF.value, d1))
       return a(false, tF, top.multilang.ibs_ibsbase_formvalidation_tim);
    if (!stringToDate (dS, d2))
       return a(false, dF, top.multilang.ibs_ibsbase_formvalidation_prd);
    if (!stringToTime (tS, d2))
       return a(false, tF, top.multilang.ibs_ibsbase_formvalidation_pro);
    if (d1.getTime() >= d2.getTime())
    {
        return a (false, dF, 
                  top.multilang.ibs_ibsbase_formvalidation_fdt + dS + 
                  top.multilang.ibs_ibsbase_formvalidation_spc + tS + 
                  top.multilang.ibs_ibsbase_formvalidation_hol);
    }
    else
        return true;
}
function iS(f, mtF)
{
    var mt = null;
    if (mtF.substring(0, 2) != "d_")
        mtF = "d_" + mtF;
    eval("mt = f.form." + mtF + ";");
    return a(_iS(f, mt), f, 
             top.multilang.ibs_ibsbase_formvalidation_sam + mtF.name + 
             top.multilang.ibs_ibsbase_formvalidation_con2);
}
function rNEM(f, fMt, vS)
{
    if (fMt.value == vS)
        return iNE(f, eA);
    return true;
}
function iLE(f, l)
{
    return a(_iLE(f, l), f, 
             top.multilang.ibs_ibsbase_formvalidation_len + l + 
             top.multilang.ibs_ibsbase_formvalidation_len2);
}
function iLR(f, mn, mx)
{
    return a(_iLR(f, mn, mx), f, 
             top.multilang.ibs_ibsbase_formvalidation_bet + mn + 
             top.multilang.ibs_ibsbase_formvalidation_and + mx + 
             top.multilang.ibs_ibsbase_formvalidation_cco);
}
function iLGE(f, l)
{
    return a(_iLR(f, l, null), f, 
             top.multilang.ibs_ibsbase_formvalidation_fmi + l + 
             top.multilang.ibs_ibsbase_formvalidation_cco);
}
*/
function iLLE(f, l)
{
    return a(_iLR(f, null, l), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_fma + l + 
    		 top.multilang.ibs_ibsbase_formvalidation_cco);
}
/* ! function not needed now
function iLsE(f, mtF)
{
    return a((f.value <= mtF.value), f, 
    	     top.multilang.ibs_ibsbase_formvalidation_fse + mtF.value + 
    	     top.multilang.ibs_ibsbase_formvalidation_be2);
}
*/
function cTx (f1, n1, f2, n2, op)
{
    var cp = "'" + f1.value + "' " + op + " '" + f2.value + "'";
    return tA(eval(cp), f1, n1, f2, n2, op);
}
function cTxI (f1, n1, f2, n2, op)
{
    var one = f1.value.toUpperCase();
    var two = f2.value.toUpperCase(); 
    var cp = "'" + one + "' " + op + " '" + two + "'";
    return tA(eval(cp), f1, n1, f2, n2, op);
}
function cI (f1, n1, f2, n2, op)
{
    var cp = f1.value + " " + op + " " + f2.value;
    return tA(eval(cp), f1, n1, f2, n2, op);
}
function cD (f1, n1, f2, n2, op)
{
    var d1 = createDate ();
    var d2 = createDate ();
    var cp = "";
    if (f1.value == "" || f2.value == "")
    {
    	cp = true;
    }
    else
    {
	    stringToDate (f1.value, d1);
	    stringToDate (f2.value, d2);
	    cp = d1.getTime () + " " + op + " " + d2.getTime ();
    }
	return tA (eval (cp), f1, n1, f2, n2, op);
}
function cT (f1, n1, f2, n2, op)
{
    var d1 = createDate ();
    var d2 = createDate ();
    stringToTime (f1.value, d1);
    stringToTime (f2.value, d2);
    var cp = d1.getTime () + " " + op + " " + d2.getTime ();
    return tA (eval (cp), f1, n1, f2, n2, op);
}
function cDT (dF1, tF1, n1, dF2, tF2, n2, op)
{
    var d1 = createDate ();
    var d2 = createDate ();
    stringToDate (dF1.value, d1);
    stringToTime (tF1.value, d1);
    stringToDate (dF2.value, d2);
    stringToTime (tF2.value, d2);
    var cp = d1.getTime () + " " + op + " " + d2.getTime ();
    return tA (eval (cp), dF1, n1, dF2, n2, op);
}
function tA (ok, f1, n1, f2, n2, op)
{
    var mes = "";
    if (op == "==")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_equ;
    }
    if (op == "!=")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_neq;
    }
    if (op == ">")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_fbf;
    }
    if (op == ">=")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_fbq;
    }
    if (op == "<")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_fsf;
    }
    if (op == "<=")
    {
        mes = top.multilang.ibs_ibsbase_formvalidation_fsq;
    }
    mes = top.multilang.ibs_ibsbase_formvalidation_val + n1 + mes + n2 + 
          top.multilang.ibs_ibsbase_formvalidation_be2;
    return a(ok, f1, mes);
}

// additional money functions
function iMR(f, mn, mx, eA)
{
    return a(_iMR(f, mn, mx, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_mbe + mn + 
    		 top.multilang.ibs_ibsbase_formvalidation_bis + mx + 
    		 top.multilang.ibs_ibsbase_formvalidation_con);
}
function iMGE(f, mn, eA)
{
    return a(_iMR(f, mn, null, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_nbi + mn + 
    		 top.multilang.ibs_ibsbase_formvalidation_be);
}
function iMLE(f, mx, eA)
{
    return a(_iMR(f, null, mx, eA), f, 
    		 top.multilang.ibs_ibsbase_formvalidation_nsm + mx + 
    		 top.multilang.ibs_ibsbase_formvalidation_be);
}
// isMoneyRelation
function _iMR(f, mn, mx, eA)
{
    if (!_iM(f, eA))
        return false;
    // convert to float value        
    // search for ',-' or '.-'; replace with ',0' or '.0'
    var p = f.value.lastIndexOf ('-');
    if (p > 0 && (f.value.charAt (p-1) == '.' || f.value.charAt (p-1) == ','))
        f.value = f.value.substring (0, p) + '0' + f.value.substring (p + 1);
    // trim float value; leading and trailing zeros        
    trimFieldFloat (f);
    // check if the field is empty:
    if (f.value.length == 0)            // empty allowed field is empty?
    {
        return true;
    } // if empty allowed field is empty
    // parse float value
    var v = parseFloat(f.value);        
    // check bounds
    if (isNaN (v) || (mn != null && v < mn) || (mx != null && v > mx))
        return false;
    // reconvert value
    _iM (f, eA);        
    return true;
}

// isEmail
function iEm (f, eA) 
{
    var emailStr = f.value;
    _msg = "";
    var ok = iNE (f, eA);
    if (!ok)
        return false; 
    if (eA && _iE (f, eA))
        return true;
 
    /* Begin with the coarse pattern to simply break up user@domain into
       different pieces that are easy to analyze. */
    var matchArray=emailStr.match(emailPat)

    if (matchArray==null) 
    {
    /* Too many/few @'s or something; basically, this address doesn't
       even fit the general mould of a valid e-mail address. */
        alert(_iEm);
        focus (f);
        f.select ();
	return false;
    }

    var user=matchArray[1]
    var domain=matchArray[2]

    // See if "user" is valid 
    if (user.match(userPat)==null) 
    {
        // user is not valid
        alert (top.multilang.ibs_ibsbase_formvalidation_iEmU);
        focus (f);
        f.select ();
        return false;
    }

    /* if the e-mail address is at an IP address (as opposed to a symbolic
       host name) make sure the IP address is valid. */
    var IPArray=domain.match(ipDomainPat)
    if (IPArray!=null) 
    {
        // this is an IP address
	for (var i=1;i<=4;i++) {
	    if (IPArray[i]>255) {
                alert (_iEIP);
                focus (f);
                f.select ();
		return false;
	    }
        }
        return true;
    }

    // Domain is symbolic name
    var domainArray=domain.match(domainPat)
    if (domainArray==null) 
    {
        alert (top.multilang.ibs_ibsbase_formvalidation_iEmD1);
        focus (f);
        f.select ();
        return false;
    }

    /* domain name seems valid, but now make sure that it ends in a
       three-letter word (like com, edu, gov) or a two-letter word,
       representing country (uk, nl), and that there's a hostname preceding 
       the domain or country. */

    /* Now we need to break up the domain to get a count of how many atoms
       it consists of. */
    var atomPat=new RegExp(atom,"g")
    var domArr=domain.match(atomPat)
    var len=domArr.length
    if (domArr[domArr.length-1].length<2 || 
        domArr[domArr.length-1].length>3) 
    {
       // the address must end in a two letter or three letter word.
       alert(top.multilang.ibs_ibsbase_formvalidation_iEmD2)
        focus (f);
        f.select ();
       return false;
    }

    // Make sure there's a host name preceding the domain.
    if (len<2) 
    {
        alert(top.multilang.ibs_ibsbase_formvalidation_iEmD);
        focus (f);
        f.select ();
        return false;
    }

// If we've gotten this far, everything's valid!
return true;
}

// is empty selected
function iESlct (f, msg)
{
    var ind=f.selectedIndex;
    var val=f.options[ind].value;
    var escVal="";
    var iVal="";

    for(var i=0; i<val.length; i++)
    {
        iVal=val.charAt(i);
        if(iVal != ' ')
            escVal += iVal;
    }
    if (escVal.length == 0)
    {
        focus (f);
        alert (msg);
        return false;
    }

    return true;
}

// is empty selected
function iERadio (f, msg)
{
    var radioSel=false;
    
    for(var i=0; i<f.length; i++)
    {
		if(f[i].checked)
		{
			radioSel=true;
			break;
		}
	}
	
	if(!radioSel)
	{
		if(f.length > 0)
			focus(f[0]);
			
        alert (msg);
        return false;
	}
	
    return true;
}

/**
 * Sets the focus on the given element
 */
function focus (f)
{
    try
    {
        f.focus ();
    } // try
    catch (err)
    {
        // do not show an error message since browsers != IE8 ignore focus errors too
        // focus errors may occur if the target field is hidden
    } // catch
} // focus