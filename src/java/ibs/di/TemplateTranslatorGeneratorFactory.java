/**
 * Class: TemplateTranslatorGeneratorFactory.java
 */

// package:
package ibs.di;

// imports:

/******************************************************************************
 * Factory for Template Translator Generator implementations. <BR/>
 *
 * @version     $Id: TemplateTranslatorGeneratorFactory.java,v 1.3 2009/09/04 18:21:10 kreimueller Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20090827
 ******************************************************************************
 */
public final class TemplateTranslatorGeneratorFactory
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TemplateTranslatorGeneratorFactory.java,v 1.3 2009/09/04 18:21:10 kreimueller Exp $";


    /**
     * Template translator type XSLT
     */
    protected static final int TEMPLATE_TRANSLATOR_TYPE_XSLT = 0;

    /**
     * Singleton instance Template Translator Generator of type XSLT. <BR/>
     */
    private static ITemplateTranslatorGenerator p_xsltTemplateTranslatorGenerator;

    // Add further Template Translator Generator types here
    // ...


    /**************************************************************************
     * Returns a template translator generator of the given type. <BR/>
     *
     * @param templateTranslatorType    The desired Template Translator Generator type
     *
     * @return  The template translator generator.
     */
    public static ITemplateTranslatorGenerator getTemplateTranslatorOfType (
                                                                            int templateTranslatorType)
    {
        ITemplateTranslatorGenerator templateTranslatorGenerator = null;

        switch (templateTranslatorType)
        {
            case TemplateTranslatorGeneratorFactory.TEMPLATE_TRANSLATOR_TYPE_XSLT:
                // check if the singleton instance already exists:
                if (TemplateTranslatorGeneratorFactory.p_xsltTemplateTranslatorGenerator == null)
                {
                    // instantiate a new XSLT Template Translator Generator:
                    TemplateTranslatorGeneratorFactory.p_xsltTemplateTranslatorGenerator =
                        new TemplateTranslatorGenerator ();
                } // if

                templateTranslatorGenerator =
                    TemplateTranslatorGeneratorFactory.p_xsltTemplateTranslatorGenerator;
                break;
            // Add handling for further Template Translator Generator types here
            // ...
            default:
                break;
        } // switch

        // Return the desired Template Translator Generator
        return templateTranslatorGenerator;
    } // getTemplateTranslatorOfType


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor is just to ensure that there is no default constructor
     * generated during compilation. <BR/>
     */
    private TemplateTranslatorGeneratorFactory ()
    {
        // nothing to do
    } // TemplateTranslatorGeneratorFactory

} // TemplateTranslatorGeneratorFactory
