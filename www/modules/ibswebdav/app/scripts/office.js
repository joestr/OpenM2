function openDocView (mimeType, docUrl)
{
    bOpen = openDocument (mimeType, docUrl);
    if (!bOpen)
    {
        alert ("Dokument konnte nicht geöffnet werden!\n\nVersuche Webordner zu öffnen ...");
        oDAV.style.behavior = "url('#default#httpFolder')";
        result = oDAV.navigateFrame (docUrl, "_self")
        if (result != "OK")
            alert ("Webordner konnte nicht geöffnet werden: " + result);
    }
}


