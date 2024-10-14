/******************************************************************************
 * Java script functions for BIRT multilang support. <BR>
 *
 * @version     $Id: scriptBirtMultilang.js,v 1.3 2012/04/23 11:34:51 btatzmann Exp $
 *
 * @author      Gottfried Weiﬂ (GW)
 ******************************************************************************
 */

function getPropertyValue(key, reportContext, defaultValue)
{
	importPackage(Packages.java.lang);
	
	var value = reportContext.getMessage(key, reportContext.getLocale());

	if ((value != null) && (!value.equals("")))
	{
		return value;
	}
	
	return defaultValue;
}