Function openDocument(mimeType, docUrl)
    Select Case mimeType
        Case "application/msword"       openDocument = openWordDocument (docUrl)
        Case "application/vnd.ms-excel" openDocument = openExcelDocument (docUrl)
        'Case "application/pdf"          openDocument = openPDFDocument (docUrl)
        Case Else openDocument = False
    End Select
End Function


Function openWordDocument(docUrl)

Dim oApp
Dim oDoc

    On Error Resume Next
    Set oApp = GetObject(, "Word.Application")
    If Err <> 0 Then
        Err.Clear
        Set oApp = CreateObject("Word.Application")
        If Err <> 0 Then
            MsgBox ("MS Word konnte nicht gestartet werden!" & Chr(13) & Chr(13) & "Möglicherweisse verbieten die Sicherheitseinstellungen " & Chr(13) & "Ihres Browsers das Öffnen von ActiveX-Komponenten. " & Chr(13) & "Bitte wenden sie sich an ihren Administrator." & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
            openWordDocument = False
            oApp.Quit
            oApp = Nothing
            Return
        End If
    End If

    ' Parameters: Open(FileName, ConfirmConversions, ReadOnly, AddToRecentFiles, ...)
    Set oDoc = oApp.Documents.Open (docUrl, False, False ,False)

    If Err <> 0 Then
        MsgBox ("Word Dokument konnte nicht geladen werden!" & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
        openWordDocument = False
        oApp.Quit
    Else
        oApp.Activate
        oDoc.Activate
        oApp.Visible = True

        AppActivate "Microsoft Word", 1

        openWordDocument = True
    End If

    Set oApp = Nothing
    Set oDoc = Nothing
    On Error GoTo 0

End Function


Function openExcelDocument(docUrl)

Dim oApp
Dim oDoc

    On Error Resume Next
    Set oApp = GetObject(, "Excel.Application")
    If Err <> 0 Then
        Err.Clear
        Set oApp = CreateObject("Excel.Application")
        If Err <> 0 Then
            MsgBox ("MS Excel konnte nicht gestartet werden!" & Chr(13) & Chr(13) & "Möglicherweisse verbieten die Sicherheitseinstellungen " & Chr(13) & "Ihres Browsers das Öffnen von ActiveX-Komponenten. " & Chr(13) & "Bitte wenden sie sich an ihren Administrator." & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
            openExcelDocument = False
            oApp.Quit
            oApp = Nothing
            Return
        End If
    End If

    Set oDoc = oApp.Workbooks.Open(docUrl)
    If Err <> 0 Then
        MsgBox ("Excel Dokument konnte nicht geladen werden!" & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
        openExcelDocument = False
        oApp.Quit
        oApp = Nothing
        Return
    Else
        oApp.Activate
        oDoc.Activate
        oApp.Visible = True
        AppActivate "Microsft Excel", 1

        openExcelDocument = True
    End If

    Set oApp = Nothing
    Set oDoc = Nothing
    On Error GoTo 0

End Function


Function openPDFDocument(docUrl)

Dim oApp
Dim oDoc

    On Error Resume Next
    Set oApp = GetObject(, "AcroExch.App")
    If Err <> 0 Then
        Err.Clear
        Set oApp = CreateObject("AcroExch.App")
        If Err <> 0 Then
            MsgBox ("Adobe Acrobat konnte nicht gestartet werden!" & Chr(13) & Chr(13) & "Möglicherweisse verbieten die Sicherheitseinstellungen " & Chr(13) & "Ihres Browsers das Öffnen von ActiveX-Komponenten. " & Chr(13) & "Bitte wenden sie sich an ihren Administrator." & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
            openAdobeDocument = False
            oApp.Exit
            oApp = Nothing
            Return
        End If
    End If

    Set oDoc = CreateObject("AcroExch.AVDoc")
    openPDFDocument = oDoc.Open(docUrl, "")
    If Err <> 0 Then
        MsgBox ("PDF Dokument konnte nicht geladen werden!" & Chr(13) & Chr(13) & CStr(Err.Number) & " - " & Err.Description)
        openPDFDocument = False
        oApp.Exit
        oApp = Nothing
        Return
    End If

    oApp.Show
    AppActivate "Adobe Acrobat", 1

    openPDFDocument = True

    Set oApp = Nothing
    Set oDoc = Nothing
    On Error GoTo 0

End Function
