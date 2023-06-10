B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=9.3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	
End Sub

Sub Globals

	Private btn_search As Button
	Private Button_notifyadd As Button
	Private Spinner_notifyselect As Spinner
	Private Panel_notify As Panel
	Private EditText_notifyintro As EditText
	Private EditText_notifyhead As EditText
	Private lbl_notifihead As Label
	Private ToggleButton2 As ToggleButton
	Private ToggleButton1 As ToggleButton
	Private txtnotify As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("notify_layout")
	
	'Add Items to Spinner
	Spinner_notifyselect.Add("Notification 1")
	Spinner_notifyselect.Add("Notification 2")
	Spinner_notifyselect.Add("Notification 3")
	Spinner_notifyselect.Add("Notification 4")
	Spinner_notifyselect.Add("Notification 5")
	Spinner_notifyselect.Add("Notification 6")
	Spinner_notifyselect.Add("Notification 7")
	Spinner_notifyselect.Add("Notification 8")
	Spinner_notifyselect.Add("Notification 9")
	Spinner_notifyselect.Add("Notification 10")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub btn_search_Click
	Activity.Finish
End Sub

'Notification
Sub btn_notify_Click
	If ToggleButton1.Checked=True Then
		ToggleButton1.Checked=False
		Panel_notify.BringToFront
		Panel_notify.Visible=True
		Button_notifyadd.Visible=True
		btn_search.Visible=False
	Else
		ToggleButton1.Checked=True
		Panel_notify.Visible=False
		Button_notifyadd.Visible=False
		btn_search.Visible=True
	End If
End Sub

Sub Button_notifyadd_Click
	Try
		If Spinner_notifyselect.SelectedItem="Notification 1" Then
			Dim n As Notification
			n.Initialize
			n.Vibrate=True
			n.Sound=False
			n.Icon = "icon"
			n.Light=True
			n.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
			n.Notify(1)
		Else
			If Spinner_notifyselect.SelectedItem="Notification 2" Then
				Dim n1 As Notification
				n1.Initialize
				n1.Vibrate=True
				n1.Sound=False
				n1.Icon = "icon"
				n1.Light=True
				n1.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
				n1.Notify(2)
			Else
				If Spinner_notifyselect.SelectedItem="Notification 3" Then
					Dim n11 As Notification
					n11.Initialize
					n11.Vibrate=True
					n11.Sound=False
					n11.Icon = "icon"
					n11.Light=True
					n11.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
					n11.Notify(3)
				Else
					If Spinner_notifyselect.SelectedItem="Notification 4" Then
						Dim n111 As Notification
						n111.Initialize
						n111.Vibrate=True
						n111.Sound=False
						n111.Icon = "icon"
						n111.Light=True
						n111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
						n111.Notify(4)
					Else
						
						If Spinner_notifyselect.SelectedItem="Notification 5" Then
						
							Dim n1111 As Notification
							n1111.Initialize
							n1111.Vibrate=True
							n1111.Sound=False
							n1111.Icon = "icon"
							n1111.Light=True
							n1111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
							n1111.Notify(5)
						Else
							If Spinner_notifyselect.SelectedItem="Notification 6" Then
								Dim n11111 As Notification
								n11111.Initialize
								n11111.Vibrate=True
								n11111.Sound=False
								n11111.Icon = "icon"
								n11111.Light=True
								n11111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
								n11111.Notify(6)
							
							Else
	
								If Spinner_notifyselect.SelectedItem="Notification 7" Then
									Dim n111111 As Notification
									n111111.Initialize
									n111111.Vibrate=True
									n111111.Sound=False
									n111111.Icon = "icon"
									n111111.Light=True
									n111111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
									n111111.Notify(7)
							
								Else
									If Spinner_notifyselect.SelectedItem="Notification 8" Then
										Dim n1111111 As Notification
										n1111111.Initialize
										n1111111.Vibrate=True
										n1111111.Sound=False
										n1111111.Icon = "icon"
										n1111111.Light=True
										n1111111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
										n1111111.Notify(8)
							
									Else
										If Spinner_notifyselect.SelectedItem="Notification 9" Then
											Dim n11111111 As Notification
											n11111111.Initialize
											n11111111.Vibrate=True
											n11111111.Sound=False
											n11111111.Icon = "icon"
											n11111111.Light=True
											n11111111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
											n11111111.Notify(9)
										Else
											If Spinner_notifyselect.SelectedItem="Notification 10" Then
						
												Dim n111111111 As Notification
												n111111111.Initialize
												n111111111.Vibrate=True
												n111111111.Sound=False
												n111111111.Icon = "icon"
												n111111111.Light=True
												n111111111.SetInfo(EditText_notifyhead.Text , EditText_notifyintro.Text,Me)
												n111111111.Notify(10)
											Else
											End If
										End If
									End If
								End If
							End If
						End If
					End If
				End If
			
			End If
		End If
	Catch
		Log(LastException)
	End Try
End Sub