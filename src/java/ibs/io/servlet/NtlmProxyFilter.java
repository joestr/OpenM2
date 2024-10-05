/*
 * Class NtlmProxyFilter
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.di.DIConstants;
import ibs.io.IOConstants;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/******************************************************************************
 * This filter servlet works as proxy concerning NTLM.
 * Depending on a provided request parameter NTLM is activated or deactivated
 * for the current session.<BR/>
 *
 * @version     $Id: NtlmProxyFilter.java,v 1.3 2012/05/10 07:08:38 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann 20101109
 ******************************************************************************
 */
public class NtlmProxyFilter implements Filter
{
    /**
     * Request parameter and session attribute indicating, that NTLM should
     * not be used.
     */
    private static final String ATTRIBUTE_NO_NTLM = "noNtlm";

    
    /**
     * Name of the application servlet
     */
    private static String applicationServlet = "ApplicationServlet";


    /**
     * This method contains the proxy logic, which forwards either to the next filter,
     * within the filter chain, which has to be the NTLM filter servlet or directly
     * to the ApplicationServlet.
     *
     * @param request   The request object.
     * @param response  The response object.
     * @param chain     The filter chain
     * 
     * @throws IOException
     * @throws ServletException
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
          FilterChain chain) throws IOException, ServletException
    {
        // IBS-778 Set the request encoding first
        // see also http://java.sun.com/j2ee/1.4/docs/tutorial/doc/WebI18N5.html
        // see also ibs.io.BaseServlet.doGet ()
        request.setCharacterEncoding (DIConstants.CHARACTER_ENCODING);

        // is ntlm activated
        boolean ntlm = true;

        // retrieve the ntlm parameter from the request
        String noNtlmParameter = request.getParameter (ATTRIBUTE_NO_NTLM);
        
        // check if the no ntlm request parameter has been set
        if (noNtlmParameter != null && new Integer (IOConstants.BOOLPARAM_TRUE).toString ().equals (noNtlmParameter))
        {
            ntlm = false;
            
            ((HttpServletRequest) request).getSession ().
                setAttribute (ATTRIBUTE_NO_NTLM, new Integer (IOConstants.BOOLPARAM_TRUE).toString ());
        } // if
        else
        {
            // retrieve the ntlm session attribute
            noNtlmParameter = (String) ((HttpServletRequest) request).getSession ().
                getAttribute (ATTRIBUTE_NO_NTLM);
            
            // check if the no ntlm session has been set
            if (noNtlmParameter != null && new Integer (IOConstants.BOOLPARAM_TRUE).toString ().equals (noNtlmParameter))
            {
                ntlm = false;
            } // if
        } // else
        
        // check if ntlm should be used
        if (ntlm)
        {
            // proceed along the chain to the NTLM filter
            chain.doFilter (request, response);
        } // if
        else
        {
            // dispatch directly to the Application Servlet
            this.dispatch (request, response);   
        } // else
    } // doFilter
    
    
    /**
     * This method dispatches the request directly to the ApplicationServlet. 
     *
     * @param request   The request object.
     * @param response  The response object.
     */
    private void dispatch (ServletRequest request, ServletResponse response)
    {
        // create a dispatcher to the application servlet
        RequestDispatcher dispatcher = request.getRequestDispatcher(applicationServlet);
        
        if (dispatcher != null)
        {
             try
             {
                 dispatcher.forward(request, response);
             } // try
             catch (ServletException e)
             {
                 e.printStackTrace();
             } // catch
             catch (IOException e)
             {
                 e.printStackTrace();
             } // catch
        } // if
    } // dispatch


    public void destroy ()
    {
        // nothing to do
    } // destroy


    public void init (FilterConfig arg0) throws ServletException
    {
        // nothing to do
    } // init
 } // NtlmProxyFilter