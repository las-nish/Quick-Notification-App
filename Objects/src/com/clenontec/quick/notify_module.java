package com.clenontec.quick;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class notify_module extends Activity implements B4AActivity{
	public static notify_module mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.clenontec.quick", "com.clenontec.quick.notify_module");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (notify_module).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.clenontec.quick", "com.clenontec.quick.notify_module");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.clenontec.quick.notify_module", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (notify_module) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (notify_module) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return notify_module.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (notify_module) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            notify_module mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (notify_module) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn_search = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button_notifyadd = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner_notifyselect = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_notify = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext_notifyintro = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext_notifyhead = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_notifihead = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _togglebutton2 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _togglebutton1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtnotify = null;
public com.clenontec.quick.main _v6 = null;
public com.clenontec.quick.help_module _help_module = null;
public com.clenontec.quick.starter _v5 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 25;BA.debugLine="Activity.LoadLayout(\"notify_layout\")";
mostCurrent._activity.LoadLayout("notify_layout",mostCurrent.activityBA);
 //BA.debugLineNum = 28;BA.debugLine="Spinner_notifyselect.Add(\"Notification 1\")";
mostCurrent._spinner_notifyselect.Add("Notification 1");
 //BA.debugLineNum = 29;BA.debugLine="Spinner_notifyselect.Add(\"Notification 2\")";
mostCurrent._spinner_notifyselect.Add("Notification 2");
 //BA.debugLineNum = 30;BA.debugLine="Spinner_notifyselect.Add(\"Notification 3\")";
mostCurrent._spinner_notifyselect.Add("Notification 3");
 //BA.debugLineNum = 31;BA.debugLine="Spinner_notifyselect.Add(\"Notification 4\")";
mostCurrent._spinner_notifyselect.Add("Notification 4");
 //BA.debugLineNum = 32;BA.debugLine="Spinner_notifyselect.Add(\"Notification 5\")";
mostCurrent._spinner_notifyselect.Add("Notification 5");
 //BA.debugLineNum = 33;BA.debugLine="Spinner_notifyselect.Add(\"Notification 6\")";
mostCurrent._spinner_notifyselect.Add("Notification 6");
 //BA.debugLineNum = 34;BA.debugLine="Spinner_notifyselect.Add(\"Notification 7\")";
mostCurrent._spinner_notifyselect.Add("Notification 7");
 //BA.debugLineNum = 35;BA.debugLine="Spinner_notifyselect.Add(\"Notification 8\")";
mostCurrent._spinner_notifyselect.Add("Notification 8");
 //BA.debugLineNum = 36;BA.debugLine="Spinner_notifyselect.Add(\"Notification 9\")";
mostCurrent._spinner_notifyselect.Add("Notification 9");
 //BA.debugLineNum = 37;BA.debugLine="Spinner_notifyselect.Add(\"Notification 10\")";
mostCurrent._spinner_notifyselect.Add("Notification 10");
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _btn_notify_click() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub btn_notify_Click";
 //BA.debugLineNum = 55;BA.debugLine="If ToggleButton1.Checked=True Then";
if (mostCurrent._togglebutton1.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 56;BA.debugLine="ToggleButton1.Checked=False";
mostCurrent._togglebutton1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 57;BA.debugLine="Panel_notify.BringToFront";
mostCurrent._panel_notify.BringToFront();
 //BA.debugLineNum = 58;BA.debugLine="Panel_notify.Visible=True";
mostCurrent._panel_notify.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 59;BA.debugLine="Button_notifyadd.Visible=True";
mostCurrent._button_notifyadd.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 60;BA.debugLine="btn_search.Visible=False";
mostCurrent._btn_search.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 62;BA.debugLine="ToggleButton1.Checked=True";
mostCurrent._togglebutton1.setChecked(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 63;BA.debugLine="Panel_notify.Visible=False";
mostCurrent._panel_notify.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 64;BA.debugLine="Button_notifyadd.Visible=False";
mostCurrent._button_notifyadd.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 65;BA.debugLine="btn_search.Visible=True";
mostCurrent._btn_search.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _btn_search_click() throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub btn_search_Click";
 //BA.debugLineNum = 50;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _button_notifyadd_click() throws Exception{
anywheresoftware.b4a.objects.NotificationWrapper _n = null;
anywheresoftware.b4a.objects.NotificationWrapper _n1 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n11 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n1111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n11111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n111111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n1111111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n11111111 = null;
anywheresoftware.b4a.objects.NotificationWrapper _n111111111 = null;
 //BA.debugLineNum = 69;BA.debugLine="Sub Button_notifyadd_Click";
 //BA.debugLineNum = 70;BA.debugLine="Try";
try { //BA.debugLineNum = 71;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notificati";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 1")) { 
 //BA.debugLineNum = 72;BA.debugLine="Dim n As Notification";
_n = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 73;BA.debugLine="n.Initialize";
_n.Initialize();
 //BA.debugLineNum = 74;BA.debugLine="n.Vibrate=True";
_n.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 75;BA.debugLine="n.Sound=False";
_n.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="n.Icon = \"icon\"";
_n.setIcon("icon");
 //BA.debugLineNum = 77;BA.debugLine="n.Light=True";
_n.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 78;BA.debugLine="n.SetInfo(EditText_notifyhead.Text , EditText_n";
_n.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 79;BA.debugLine="n.Notify(1)";
_n.Notify((int) (1));
 }else {
 //BA.debugLineNum = 81;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notificat";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 2")) { 
 //BA.debugLineNum = 82;BA.debugLine="Dim n1 As Notification";
_n1 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 83;BA.debugLine="n1.Initialize";
_n1.Initialize();
 //BA.debugLineNum = 84;BA.debugLine="n1.Vibrate=True";
_n1.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 85;BA.debugLine="n1.Sound=False";
_n1.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 86;BA.debugLine="n1.Icon = \"icon\"";
_n1.setIcon("icon");
 //BA.debugLineNum = 87;BA.debugLine="n1.Light=True";
_n1.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 88;BA.debugLine="n1.SetInfo(EditText_notifyhead.Text , EditText";
_n1.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 89;BA.debugLine="n1.Notify(2)";
_n1.Notify((int) (2));
 }else {
 //BA.debugLineNum = 91;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notifica";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 3")) { 
 //BA.debugLineNum = 92;BA.debugLine="Dim n11 As Notification";
_n11 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 93;BA.debugLine="n11.Initialize";
_n11.Initialize();
 //BA.debugLineNum = 94;BA.debugLine="n11.Vibrate=True";
_n11.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 95;BA.debugLine="n11.Sound=False";
_n11.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="n11.Icon = \"icon\"";
_n11.setIcon("icon");
 //BA.debugLineNum = 97;BA.debugLine="n11.Light=True";
_n11.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 98;BA.debugLine="n11.SetInfo(EditText_notifyhead.Text , EditTe";
_n11.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 99;BA.debugLine="n11.Notify(3)";
_n11.Notify((int) (3));
 }else {
 //BA.debugLineNum = 101;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notific";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 4")) { 
 //BA.debugLineNum = 102;BA.debugLine="Dim n111 As Notification";
_n111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 103;BA.debugLine="n111.Initialize";
_n111.Initialize();
 //BA.debugLineNum = 104;BA.debugLine="n111.Vibrate=True";
_n111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 105;BA.debugLine="n111.Sound=False";
_n111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 106;BA.debugLine="n111.Icon = \"icon\"";
_n111.setIcon("icon");
 //BA.debugLineNum = 107;BA.debugLine="n111.Light=True";
_n111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 108;BA.debugLine="n111.SetInfo(EditText_notifyhead.Text , Edit";
_n111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 109;BA.debugLine="n111.Notify(4)";
_n111.Notify((int) (4));
 }else {
 //BA.debugLineNum = 112;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notifi";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 5")) { 
 //BA.debugLineNum = 114;BA.debugLine="Dim n1111 As Notification";
_n1111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 115;BA.debugLine="n1111.Initialize";
_n1111.Initialize();
 //BA.debugLineNum = 116;BA.debugLine="n1111.Vibrate=True";
_n1111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 117;BA.debugLine="n1111.Sound=False";
_n1111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 118;BA.debugLine="n1111.Icon = \"icon\"";
_n1111.setIcon("icon");
 //BA.debugLineNum = 119;BA.debugLine="n1111.Light=True";
_n1111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 120;BA.debugLine="n1111.SetInfo(EditText_notifyhead.Text , Ed";
_n1111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 121;BA.debugLine="n1111.Notify(5)";
_n1111.Notify((int) (5));
 }else {
 //BA.debugLineNum = 123;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Notif";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 6")) { 
 //BA.debugLineNum = 124;BA.debugLine="Dim n11111 As Notification";
_n11111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 125;BA.debugLine="n11111.Initialize";
_n11111.Initialize();
 //BA.debugLineNum = 126;BA.debugLine="n11111.Vibrate=True";
_n11111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 127;BA.debugLine="n11111.Sound=False";
_n11111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 128;BA.debugLine="n11111.Icon = \"icon\"";
_n11111.setIcon("icon");
 //BA.debugLineNum = 129;BA.debugLine="n11111.Light=True";
_n11111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="n11111.SetInfo(EditText_notifyhead.Text ,";
_n11111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 131;BA.debugLine="n11111.Notify(6)";
_n11111.Notify((int) (6));
 }else {
 //BA.debugLineNum = 135;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Noti";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 7")) { 
 //BA.debugLineNum = 136;BA.debugLine="Dim n111111 As Notification";
_n111111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 137;BA.debugLine="n111111.Initialize";
_n111111.Initialize();
 //BA.debugLineNum = 138;BA.debugLine="n111111.Vibrate=True";
_n111111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 139;BA.debugLine="n111111.Sound=False";
_n111111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 140;BA.debugLine="n111111.Icon = \"icon\"";
_n111111.setIcon("icon");
 //BA.debugLineNum = 141;BA.debugLine="n111111.Light=True";
_n111111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 142;BA.debugLine="n111111.SetInfo(EditText_notifyhead.Text";
_n111111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 143;BA.debugLine="n111111.Notify(7)";
_n111111.Notify((int) (7));
 }else {
 //BA.debugLineNum = 146;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"Not";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 8")) { 
 //BA.debugLineNum = 147;BA.debugLine="Dim n1111111 As Notification";
_n1111111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 148;BA.debugLine="n1111111.Initialize";
_n1111111.Initialize();
 //BA.debugLineNum = 149;BA.debugLine="n1111111.Vibrate=True";
_n1111111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 150;BA.debugLine="n1111111.Sound=False";
_n1111111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 151;BA.debugLine="n1111111.Icon = \"icon\"";
_n1111111.setIcon("icon");
 //BA.debugLineNum = 152;BA.debugLine="n1111111.Light=True";
_n1111111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 153;BA.debugLine="n1111111.SetInfo(EditText_notifyhead.Tex";
_n1111111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 154;BA.debugLine="n1111111.Notify(8)";
_n1111111.Notify((int) (8));
 }else {
 //BA.debugLineNum = 157;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"No";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 9")) { 
 //BA.debugLineNum = 158;BA.debugLine="Dim n11111111 As Notification";
_n11111111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 159;BA.debugLine="n11111111.Initialize";
_n11111111.Initialize();
 //BA.debugLineNum = 160;BA.debugLine="n11111111.Vibrate=True";
_n11111111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 161;BA.debugLine="n11111111.Sound=False";
_n11111111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 162;BA.debugLine="n11111111.Icon = \"icon\"";
_n11111111.setIcon("icon");
 //BA.debugLineNum = 163;BA.debugLine="n11111111.Light=True";
_n11111111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 164;BA.debugLine="n11111111.SetInfo(EditText_notifyhead.T";
_n11111111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 165;BA.debugLine="n11111111.Notify(9)";
_n11111111.Notify((int) (9));
 }else {
 //BA.debugLineNum = 167;BA.debugLine="If Spinner_notifyselect.SelectedItem=\"N";
if ((mostCurrent._spinner_notifyselect.getSelectedItem()).equals("Notification 10")) { 
 //BA.debugLineNum = 169;BA.debugLine="Dim n111111111 As Notification";
_n111111111 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 170;BA.debugLine="n111111111.Initialize";
_n111111111.Initialize();
 //BA.debugLineNum = 171;BA.debugLine="n111111111.Vibrate=True";
_n111111111.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 172;BA.debugLine="n111111111.Sound=False";
_n111111111.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 173;BA.debugLine="n111111111.Icon = \"icon\"";
_n111111111.setIcon("icon");
 //BA.debugLineNum = 174;BA.debugLine="n111111111.Light=True";
_n111111111.setLight(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 175;BA.debugLine="n111111111.SetInfo(EditText_notifyhead";
_n111111111.SetInfoNew(processBA,BA.ObjectToCharSequence(mostCurrent._edittext_notifyhead.getText()),BA.ObjectToCharSequence(mostCurrent._edittext_notifyintro.getText()),notify_module.getObject());
 //BA.debugLineNum = 176;BA.debugLine="n111111111.Notify(10)";
_n111111111.Notify((int) (10));
 }else {
 };
 };
 };
 };
 };
 };
 };
 };
 };
 };
 } 
       catch (Exception e113) {
			processBA.setLastException(e113); //BA.debugLineNum = 190;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("61245305",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 10;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 12;BA.debugLine="Private btn_search As Button";
mostCurrent._btn_search = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Private Button_notifyadd As Button";
mostCurrent._button_notifyadd = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Private Spinner_notifyselect As Spinner";
mostCurrent._spinner_notifyselect = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Private Panel_notify As Panel";
mostCurrent._panel_notify = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Private EditText_notifyintro As EditText";
mostCurrent._edittext_notifyintro = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private EditText_notifyhead As EditText";
mostCurrent._edittext_notifyhead = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private lbl_notifihead As Label";
mostCurrent._lbl_notifihead = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private ToggleButton2 As ToggleButton";
mostCurrent._togglebutton2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private ToggleButton1 As ToggleButton";
mostCurrent._togglebutton1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private txtnotify As EditText";
mostCurrent._txtnotify = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="End Sub";
return "";
}
}
