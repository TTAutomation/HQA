'$Id: vbUtils.vbs 9/8/2014 1:22:42 PM
'Copyright 2014-2016 IGATE GROUP OF COMPANIES. All rights reserved
'(Subject to Limited Distribution and Restricted Disclosure Only.)
'THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
'information of IGATE GROUP OF COMPANIES AND IS INTENDED FOR USE
'ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
'INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
'DISCLOSURE UNDER APPLICABLE LAW.
'YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
'CONDITIONS OF AN AGREEMENT BETWEEN YOU AND IGATE GROUP OF COMPANIES.
'The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
'RESTRICTED AS SET FORTH THEREIN.

Dim toField , subject , body , attachments

toField = WScript.Arguments(0)
subject = WScript.Arguments(1)
body = WScript.Arguments(2)
attachments = WScript.Arguments(3)
call SendMail(toField, subject, body, attachments)
Public Function SendMail(emailAlertTo, subject, emailDetails, errorScreenshot)
	
	Set olApp = CreateObject("Outlook.Application")
	Set olMessage = olApp.CreateItem(0)'olMailItem)
	olMessage.Importance = 2 'olImportanceHigh
	olMessage.To = Trim(emailAlertTo)
	olMessage.Subject = Trim(subject)
	
	'add error screenshot as attachment to mail
	
	olMessage.HTMLBody = emailDetails
	
	if errorScreenshot <> "BLANK" then
		if instr(errorScreenshot,";") <> 0 then
			errorScreenshots = Split(errorScreenshot,";")
			for each ES in errorScreenshots
				olMessage.Attachments.Add(ES) 
			next
		else
			olMessage.Attachments.Add(errorScreenshot) 
		end if
	end if
	
	olMessage.Send
 End Function
 
