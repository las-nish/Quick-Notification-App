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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.clenontec.quick", "com.clenontec.quick.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "com.clenontec.quick", "com.clenontec.quick.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.clenontec.quick.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
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
public anywheresoftware.b4a.objects.LabelWrapper _label_quicknotifiandsearch = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext_quicksearch = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_quickadd1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_quickadd2 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner_quicksearch = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_quicksearchandnotification = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radiobutton_url = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radiobutton_quick = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn_search = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn_notifications = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn_help = null;
public com.clenontec.quick.notify_module _notify_module = null;
public com.clenontec.quick.help_module _help_module = null;
public com.clenontec.quick.starter _v5 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (notify_module.mostCurrent != null);
vis = vis | (help_module.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="Activity.LoadLayout(\"main_layout\")";
mostCurrent._activity.LoadLayout("main_layout",mostCurrent.activityBA);
 //BA.debugLineNum = 36;BA.debugLine="Spinner_quicksearch.Add(\"Google\")";
mostCurrent._spinner_quicksearch.Add("Google");
 //BA.debugLineNum = 37;BA.debugLine="Spinner_quicksearch.Add(\"Yahoo\")";
mostCurrent._spinner_quicksearch.Add("Yahoo");
 //BA.debugLineNum = 38;BA.debugLine="Spinner_quicksearch.Add(\"Bing\")";
mostCurrent._spinner_quicksearch.Add("Bing");
 //BA.debugLineNum = 39;BA.debugLine="Spinner_quicksearch.Add(\"Duck Duck Go\")";
mostCurrent._spinner_quicksearch.Add("Duck Duck Go");
 //BA.debugLineNum = 40;BA.debugLine="Spinner_quicksearch.Add(\"Yandex\")";
mostCurrent._spinner_quicksearch.Add("Yandex");
 //BA.debugLineNum = 41;BA.debugLine="Spinner_quicksearch.Add(\"Baidu\")";
mostCurrent._spinner_quicksearch.Add("Baidu");
 //BA.debugLineNum = 42;BA.debugLine="Spinner_quicksearch.Add(\"Wolfram Alpha\")";
mostCurrent._spinner_quicksearch.Add("Wolfram Alpha");
 //BA.debugLineNum = 43;BA.debugLine="Spinner_quicksearch.Add(\"Excite\")";
mostCurrent._spinner_quicksearch.Add("Excite");
 //BA.debugLineNum = 44;BA.debugLine="Spinner_quicksearch.Add(\"Lycos\")";
mostCurrent._spinner_quicksearch.Add("Lycos");
 //BA.debugLineNum = 45;BA.debugLine="Spinner_quicksearch.Add(\"GigaBlast\")";
mostCurrent._spinner_quicksearch.Add("GigaBlast");
 //BA.debugLineNum = 46;BA.debugLine="Spinner_quicksearch.Add(\"AOL\")";
mostCurrent._spinner_quicksearch.Add("AOL");
 //BA.debugLineNum = 47;BA.debugLine="Spinner_quicksearch.Add(\"WebCrawler\")";
mostCurrent._spinner_quicksearch.Add("WebCrawler");
 //BA.debugLineNum = 48;BA.debugLine="Spinner_quicksearch.Add(\"Search Encrypt\")";
mostCurrent._spinner_quicksearch.Add("Search Encrypt");
 //BA.debugLineNum = 50;BA.debugLine="If RadioButton_quick.Checked=True Then";
if (mostCurrent._radiobutton_quick.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 51;BA.debugLine="Label_quickadd1.Enabled=False";
mostCurrent._label_quickadd1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 52;BA.debugLine="Label_quickadd2.Enabled=False";
mostCurrent._label_quickadd2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 53;BA.debugLine="Spinner_quicksearch.Enabled=True";
mostCurrent._spinner_quicksearch.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 54;BA.debugLine="EditText_quicksearch.Hint=\"Search Text\"";
mostCurrent._edittext_quicksearch.setHint("Search Text");
 }else {
 //BA.debugLineNum = 56;BA.debugLine="Label_quickadd1.Enabled=True";
mostCurrent._label_quickadd1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 57;BA.debugLine="Label_quickadd2.Enabled=True";
mostCurrent._label_quickadd2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 58;BA.debugLine="Spinner_quicksearch.Enabled=False";
mostCurrent._spinner_quicksearch.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 59;BA.debugLine="EditText_quicksearch.Hint=\"Type URL ( Ex: http:/";
mostCurrent._edittext_quicksearch.setHint("Type URL ( Ex: http://www.xxx.com )");
 };
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 68;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _btn_help_click() throws Exception{
 //BA.debugLineNum = 248;BA.debugLine="Sub btn_help_Click";
 //BA.debugLineNum = 249;BA.debugLine="StartActivity(help_module)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._help_module.getObject()));
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
public static String  _btn_notifications_click() throws Exception{
 //BA.debugLineNum = 244;BA.debugLine="Sub btn_notifications_Click";
 //BA.debugLineNum = 245;BA.debugLine="StartActivity(notify_module)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._notify_module.getObject()));
 //BA.debugLineNum = 246;BA.debugLine="End Sub";
return "";
}
public static String  _btn_search_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _u1 = null;
anywheresoftware.b4a.objects.IntentWrapper _u2 = null;
anywheresoftware.b4a.objects.IntentWrapper _u3 = null;
anywheresoftware.b4a.objects.IntentWrapper _u4 = null;
anywheresoftware.b4a.objects.IntentWrapper _u5 = null;
anywheresoftware.b4a.objects.IntentWrapper _u6 = null;
anywheresoftware.b4a.objects.IntentWrapper _u7 = null;
anywheresoftware.b4a.objects.IntentWrapper _u8 = null;
anywheresoftware.b4a.objects.IntentWrapper _u9 = null;
anywheresoftware.b4a.objects.IntentWrapper _u10 = null;
anywheresoftware.b4a.objects.IntentWrapper _u11 = null;
anywheresoftware.b4a.objects.IntentWrapper _u12 = null;
anywheresoftware.b4a.objects.IntentWrapper _u13 = null;
anywheresoftware.b4a.objects.IntentWrapper _uxx = null;
 //BA.debugLineNum = 94;BA.debugLine="Sub btn_search_Click";
 //BA.debugLineNum = 95;BA.debugLine="Try";
try { //BA.debugLineNum = 96;BA.debugLine="If RadioButton_quick.Checked=True Then";
if (mostCurrent._radiobutton_quick.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 97;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Google\" Th";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Google")) { 
 //BA.debugLineNum = 98;BA.debugLine="Try";
try { //BA.debugLineNum = 99;BA.debugLine="Private u1 As Intent";
_u1 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 100;BA.debugLine="u1.Initialize(u1.ACTION_VIEW,\"https://www.goo";
_u1.Initialize(_u1.ACTION_VIEW,"https://www.google.com/search?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 101;BA.debugLine="StartActivity(u1)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u1.getObject()));
 } 
       catch (Exception e9) {
			processBA.setLastException(e9); //BA.debugLineNum = 103;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589833",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 106;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Yahoo\" Th";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Yahoo")) { 
 //BA.debugLineNum = 107;BA.debugLine="Try";
try { //BA.debugLineNum = 108;BA.debugLine="Private u2 As Intent";
_u2 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 109;BA.debugLine="u2.Initialize(u2.ACTION_VIEW,\"https://www.ya";
_u2.Initialize(_u2.ACTION_VIEW,"https://www.yahoo.com/search?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 110;BA.debugLine="StartActivity(u2)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u2.getObject()));
 } 
       catch (Exception e18) {
			processBA.setLastException(e18); //BA.debugLineNum = 112;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589842",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 115;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Bing\" Th";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Bing")) { 
 //BA.debugLineNum = 116;BA.debugLine="Try";
try { //BA.debugLineNum = 117;BA.debugLine="Private u3 As Intent";
_u3 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 118;BA.debugLine="u3.Initialize(u3.ACTION_VIEW,\"https://www.b";
_u3.Initialize(_u3.ACTION_VIEW,"https://www.bing.com/search?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 119;BA.debugLine="StartActivity(u3)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u3.getObject()));
 } 
       catch (Exception e27) {
			processBA.setLastException(e27); //BA.debugLineNum = 121;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589851",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 124;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Duck Du";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Duck Duck Go")) { 
 //BA.debugLineNum = 125;BA.debugLine="Try";
try { //BA.debugLineNum = 126;BA.debugLine="Private u4 As Intent";
_u4 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 127;BA.debugLine="u4.Initialize(u4.ACTION_VIEW,\"https://www.";
_u4.Initialize(_u4.ACTION_VIEW,"https://www.duckduckgo.com/search?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 128;BA.debugLine="StartActivity(u4)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u4.getObject()));
 } 
       catch (Exception e36) {
			processBA.setLastException(e36); //BA.debugLineNum = 130;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589860",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 133;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Yandex";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Yandex")) { 
 //BA.debugLineNum = 134;BA.debugLine="Try";
try { //BA.debugLineNum = 135;BA.debugLine="Private u5 As Intent";
_u5 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 136;BA.debugLine="u5.Initialize(u5.ACTION_VIEW,\"https://www";
_u5.Initialize(_u5.ACTION_VIEW,"https://www.yandex.com/search/touch/?text="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 137;BA.debugLine="StartActivity(u5)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u5.getObject()));
 } 
       catch (Exception e45) {
			processBA.setLastException(e45); //BA.debugLineNum = 139;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589869",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 142;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Baidu";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Baidu")) { 
 //BA.debugLineNum = 143;BA.debugLine="Try";
try { //BA.debugLineNum = 144;BA.debugLine="Private u6 As Intent";
_u6 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 145;BA.debugLine="u6.Initialize(u6.ACTION_VIEW,\"https://ww";
_u6.Initialize(_u6.ACTION_VIEW,"https://www.baidu.com/s?word="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 146;BA.debugLine="StartActivity(u6)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u6.getObject()));
 } 
       catch (Exception e54) {
			processBA.setLastException(e54); //BA.debugLineNum = 148;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589878",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 151;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Wolf";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Wolfram Alpha")) { 
 //BA.debugLineNum = 152;BA.debugLine="Try";
try { //BA.debugLineNum = 153;BA.debugLine="Private u7 As Intent";
_u7 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 154;BA.debugLine="u7.Initialize(u7.ACTION_VIEW,\"https://w";
_u7.Initialize(_u7.ACTION_VIEW,"https://www.wolframalpha.com/input/?="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 155;BA.debugLine="StartActivity(u7)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u7.getObject()));
 } 
       catch (Exception e63) {
			processBA.setLastException(e63); //BA.debugLineNum = 157;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589887",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 160;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Exc";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Excite")) { 
 //BA.debugLineNum = 161;BA.debugLine="Try";
try { //BA.debugLineNum = 162;BA.debugLine="Private u8 As Intent";
_u8 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 163;BA.debugLine="u8.Initialize(u8.ACTION_VIEW,\"https://";
_u8.Initialize(_u8.ACTION_VIEW,"https://www.results.excite.com/serp?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(u8)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u8.getObject()));
 } 
       catch (Exception e72) {
			processBA.setLastException(e72); //BA.debugLineNum = 166;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589896",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 169;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"Ly";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Lycos")) { 
 //BA.debugLineNum = 170;BA.debugLine="Try";
try { //BA.debugLineNum = 171;BA.debugLine="Private u9 As Intent";
_u9 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 172;BA.debugLine="u9.Initialize(u9.ACTION_VIEW,\"https:/";
_u9.Initialize(_u9.ACTION_VIEW,"https://www.search.lycos.com/web?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 173;BA.debugLine="StartActivity(u9)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u9.getObject()));
 } 
       catch (Exception e81) {
			processBA.setLastException(e81); //BA.debugLineNum = 175;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589905",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 178;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"G";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("GigaBlast")) { 
 //BA.debugLineNum = 179;BA.debugLine="Try";
try { //BA.debugLineNum = 180;BA.debugLine="Private u10 As Intent";
_u10 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 181;BA.debugLine="u10.Initialize(u10.ACTION_VIEW,\"http";
_u10.Initialize(_u10.ACTION_VIEW,"https://www.gigablast.com/search?c=main&qlangcountry=en-us&q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 182;BA.debugLine="StartActivity(u10)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u10.getObject()));
 } 
       catch (Exception e90) {
			processBA.setLastException(e90); //BA.debugLineNum = 184;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589914",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 187;BA.debugLine="If Spinner_quicksearch.SelectedItem=\"";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("AOL")) { 
 //BA.debugLineNum = 188;BA.debugLine="Try";
try { //BA.debugLineNum = 189;BA.debugLine="Private u11 As Intent";
_u11 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 190;BA.debugLine="u11.Initialize(u11.ACTION_VIEW,\"htt";
_u11.Initialize(_u11.ACTION_VIEW,"https://www.search.aol.com/aol/search?s_chn=prt_bon-mobile&q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 191;BA.debugLine="StartActivity(u11)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u11.getObject()));
 } 
       catch (Exception e99) {
			processBA.setLastException(e99); //BA.debugLineNum = 193;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589923",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 196;BA.debugLine="If Spinner_quicksearch.SelectedItem=";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("WebCrawler")) { 
 //BA.debugLineNum = 197;BA.debugLine="Try";
try { //BA.debugLineNum = 198;BA.debugLine="Private u12 As Intent";
_u12 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 199;BA.debugLine="u12.Initialize(u12.ACTION_VIEW,\"ht";
_u12.Initialize(_u12.ACTION_VIEW,"https://www.webcrawler.com/serp?q="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 200;BA.debugLine="StartActivity(u12)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u12.getObject()));
 } 
       catch (Exception e108) {
			processBA.setLastException(e108); //BA.debugLineNum = 202;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589932",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 }else {
 //BA.debugLineNum = 205;BA.debugLine="If Spinner_quicksearch.SelectedItem";
if ((mostCurrent._spinner_quicksearch.getSelectedItem()).equals("Search Encrypt")) { 
 //BA.debugLineNum = 206;BA.debugLine="Try";
try { //BA.debugLineNum = 207;BA.debugLine="Private u13 As Intent";
_u13 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 208;BA.debugLine="u13.Initialize(u13.ACTION_VIEW,\"h";
_u13.Initialize(_u13.ACTION_VIEW,"https://www.searchencrypt.com/search?eq="+mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 209;BA.debugLine="StartActivity(u13)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_u13.getObject()));
 } 
       catch (Exception e117) {
			processBA.setLastException(e117); //BA.debugLineNum = 211;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589941",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
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
 };
 };
 };
 };
 }else {
 //BA.debugLineNum = 228;BA.debugLine="Try";
try { //BA.debugLineNum = 229;BA.debugLine="Private uxx As Intent";
_uxx = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 230;BA.debugLine="uxx.Initialize(u1.ACTION_VIEW,EditText_quickse";
_uxx.Initialize(_u1.ACTION_VIEW,mostCurrent._edittext_quicksearch.getText());
 //BA.debugLineNum = 231;BA.debugLine="StartActivity(uxx)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_uxx.getObject()));
 } 
       catch (Exception e138) {
			processBA.setLastException(e138); //BA.debugLineNum = 233;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589963",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 };
 } 
       catch (Exception e142) {
			processBA.setLastException(e142); //BA.debugLineNum = 238;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("6589968",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 240;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 19;BA.debugLine="Private Label_quicknotifiandsearch As Label";
mostCurrent._label_quicknotifiandsearch = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private EditText_quicksearch As EditText";
mostCurrent._edittext_quicksearch = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private Label_quickadd1 As Label";
mostCurrent._label_quickadd1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private Label_quickadd2 As Label";
mostCurrent._label_quickadd2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private Spinner_quicksearch As Spinner";
mostCurrent._spinner_quicksearch = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Panel_quicksearchandnotification As Panel";
mostCurrent._panel_quicksearchandnotification = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private RadioButton_URL As RadioButton";
mostCurrent._radiobutton_url = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private RadioButton_quick As RadioButton";
mostCurrent._radiobutton_quick = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private btn_search As Button";
mostCurrent._btn_search = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private btn_notifications As Button";
mostCurrent._btn_notifications = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private btn_help As Button";
mostCurrent._btn_help = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _label_quickadd1_click() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub Label_quickadd1_Click";
 //BA.debugLineNum = 91;BA.debugLine="EditText_quicksearch.Text=EditText_quicksearch.Te";
mostCurrent._edittext_quicksearch.setText(BA.ObjectToCharSequence(mostCurrent._edittext_quicksearch.getText()+"http://www."));
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _label_quickadd2_click() throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Sub Label_quickadd2_Click";
 //BA.debugLineNum = 87;BA.debugLine="EditText_quicksearch.Text=EditText_quicksearch.Te";
mostCurrent._edittext_quicksearch.setText(BA.ObjectToCharSequence(mostCurrent._edittext_quicksearch.getText()+".com"));
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
notify_module._process_globals();
help_module._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="End Sub";
return "";
}
public static String  _radiobutton_quick_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub RadioButton_quick_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 80;BA.debugLine="Label_quickadd1.Enabled=False";
mostCurrent._label_quickadd1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 81;BA.debugLine="Label_quickadd2.Enabled=False";
mostCurrent._label_quickadd2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 82;BA.debugLine="Spinner_quicksearch.Enabled=True";
mostCurrent._spinner_quicksearch.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 83;BA.debugLine="EditText_quicksearch.Hint=\"Search Text\"";
mostCurrent._edittext_quicksearch.setHint("Search Text");
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _radiobutton_url_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub RadioButton_URL_CheckedChange(Checked As Boole";
 //BA.debugLineNum = 73;BA.debugLine="Label_quickadd1.Enabled=True";
mostCurrent._label_quickadd1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 74;BA.debugLine="Label_quickadd2.Enabled=True";
mostCurrent._label_quickadd2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 75;BA.debugLine="Spinner_quicksearch.Enabled=False";
mostCurrent._spinner_quicksearch.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="EditText_quicksearch.Hint=\"Type URL ( Ex: http://";
mostCurrent._edittext_quicksearch.setHint("Type URL ( Ex: http://www.xxx.com )");
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
}
