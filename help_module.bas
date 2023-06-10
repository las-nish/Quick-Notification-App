B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=9.3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals

End Sub

Sub Globals
	
	Private btn_rate As Button
	Private btn_share As Button
	Private btn_feedback As Button
	Private Label_helpContent As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("help_layout")
	
	Activity.AddMenuItem("Privacy Policy","menu_privacy")
	Activity.AddMenuItem("Company Site","menu_company")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

'Menu Click
Sub menu_privacy_Click
	Try
		Private privacy As Intent
		privacy.Initialize(privacy.ACTION_VIEW,"https://www.clenontec.com/en-gb/android-quick/privacy.php") 'Privacy Policy URL
		StartActivity(privacy)
	Catch
		Log(LastException)
		ToastMessageShow("Somthings wrong!",True)
	End Try
End Sub

Sub menu_company_Click
	Try
		Private company As Intent
		company.Initialize(company.ACTION_VIEW,"https://www.clenontec.com") 'Company URL
		StartActivity(company)		
	Catch
		Log(LastException)
		ToastMessageShow("Somthings wrong!",True)
	End Try
End Sub

' Under Buttons
Sub btn_rate_Click
	If Msgbox2("If you enjoy using this app, would you mind taking a moment to rate it? It won't take more than a minute." & CRLF & CRLF &"Thank you for your support!","Rate / Edit this app","Rate / Edit Now !","","Cancel",Null)=DialogResponse.POSITIVE Then
		Private appreview As Intent
		appreview.Initialize(appreview.ACTION_VIEW,"https://play.google.com/store/apps/details?id=com.clenontec.quick") 'Playstore URL
		StartActivity(appreview)
	Else
	End If
End Sub

Sub btn_share_Click
	Try
		Dim shareweburlpk As Intent
		shareweburlpk.Initialize(shareweburlpk.ACTION_SEND,"")
		shareweburlpk.SetType("text/plain")
		shareweburlpk.PutExtra("android.intent.extra.TEXT","Make hard things simple. Search everything in one place. Never forget little things anymore. Get it on Google Play Now. http://play.google.com/store/apps/details?id=com.clenontec.quick")
		shareweburlpk.WrapAsIntentChooser("Share Quick")
		StartActivity(shareweburlpk)
	Catch
		Log(LastException)
	End Try
End Sub

Sub btn_feedback_Click
	Try
		Dim feedback As Email
		feedback.To.Add("contact.clenontec@gmail.com") 'Contact Email
		feedback.Subject="Feedback or Help - Quick App"
		StartActivity(feedback.GetIntent)
	Catch
		Log(LastException)
		ToastMessageShow("Help and Feedback Fail !",True)
	End Try
End Sub