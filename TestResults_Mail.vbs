strAttachment = "C:\Selenium\SeleniumFramework\res\TestResults\testReport.txt"
strAttachment2 = "C:\Selenium\SeleniumFramework\res\TestResults\Logs.log"
strMailFile = 2

sendMail "TEST_EXECUTION@gmail.com", _
"pranab.n@gmail.com", _
"UI Test Automation Results ", _
"Please find the attached log files and test execution reports for any test failures and execution status"

    '#-------------------------------------------------------------------------
    '#  FUNCTION.......:  sendMail(strFrom, strTo, strSubject, strMail)
    '#  ARGUMENTS......:  strFrom - Email Sender
    '#                    strTo - Email recipient
    '#                    strSubject - Email subject
    '#                    strMail - Email body
    '#  PURPOSE........:  Sends email from a script
    '#  EXAMPLE........:  sendMail "scriptTest@gmail.com", _
    '#                    "recipient@emailAddress.com", _
    '#                    "sendMail Function Test", _
    '#                    "This is a test of the sendMail Function."
    '#          
    '#  NOTES..........:  For this to work, you must define the variable 
    '#                    "oMailServer" on line 1 of the actual Function. This
    '#                    must be set to a valid email server that will accept
    '#                    unauthenticated email from your machine (if you host
    '#                    Exchange onsite, it will do this by default).
    '#
    '#                    To add an attachment add this code above the Function
    '#                    call:
    '#                    strAttachment = "c:\yourAttachment.txt"
    '#                    strMailFile = 1
    '#
    '#                    If there are two attachemnts, add this code before
    '#                    the Function call:
    '#                    strAttachment = "c:\yourAttachment.txt"
    '#                    strAttachment2 = "c:\yourAttachment2.txt"
    '#                    strMailFile = 2
    '# DATE CREATED....:  3-July-2013
	'# CREATED BY......:  Kush Bhatnagar
    '#-------------------------------------------------------------------------
       Function sendMail(strFrom, strTo, strSubject, strMail)
        oMailServer = "smtp.myOrg.com"
        Set objEmail = CreateObject("CDO.Message")
            objEmail.From = strFrom
            objEmail.To = strTo
            objEmail.Subject = strSubject & Now()
            objEmail.Textbody = strMail
            If strMailFile = 1 Then
                objEmail.AddAttachment strAttachment
            End If
            If strMailFile = 2 Then
                objEmail.AddAttachment strAttachment
                objEmail.AddAttachment strAttachment2
            End If
        objEmail.Configuration.Fields.Item _
    	    ("http://schemas.microsoft.com/cdo/configuration/sendusing") = 2
        objEmail.Configuration.Fields.Item _
    	    ("http://schemas.microsoft.com/cdo/configuration/smtpserver") = _
    	        oMailServer 
        objEmail.Configuration.Fields.Item _
    	    ("http://schemas.microsoft.com/cdo/configuration/smtpserverport") _
                = 25
        objEmail.Configuration.Fields.Update
        objEmail.Send
    End Function 
