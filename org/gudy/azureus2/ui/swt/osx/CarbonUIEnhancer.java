/*      */ package org.gudy.azureus2.ui.swt.osx;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.platform.macosx.access.jnilib.OSXAccess;
/*      */ import org.gudy.azureus2.ui.swt.UIExitUtilsSWT;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.config.wizard.ConfigureWizard;
/*      */ import org.gudy.azureus2.ui.swt.help.AboutWindow;
/*      */ import org.gudy.azureus2.ui.swt.nat.NatTestWindow;
/*      */ import org.gudy.azureus2.ui.swt.speedtest.SpeedTestWizard;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CarbonUIEnhancer
/*      */ {
/*      */   private static final int noErr = 0;
/*      */   private static final int eventNotHandledErr = -9874;
/*      */   private static final int kEventWindowToolbarSwitchMode = 150;
/*      */   private static final int kWindowToolbarButtonAttribute = 64;
/*      */   private static final int kEventAppleEvent = 1;
/*      */   private static final int kEventProcessCommand = 1;
/*      */   private static final int kCFAllocatorDefault = 0;
/*      */   private static final int kMenuItemAttrSeparator = 64;
/*      */   private static final int kCFURLPOSIXPathStyle = 0;
/*      */   private static final int kEventClassWindow = 2003398244;
/*      */   private static final int kAEQuitApplication = 1903520116;
/*      */   private static final int kEventClassAppleEvent = 1701867619;
/*      */   private static final int kEventParamDirectObject = 757935405;
/*      */   private static final int kEventClassCommand = 1668113523;
/*      */   private static final int kEventParamAEEventID = 1702261865;
/*      */   private static final int typeHICommand = 1751346532;
/*      */   private static final int typeFSRef = 1718841958;
/*      */   private static final int typeWindowRef = 2003398244;
/*      */   private static final int typeType = 1954115685;
/*      */   private static final int kHICommandPreferences = 1886545254;
/*      */   private static final int kHICommandAbout = 1633841013;
/*      */   private static final int kHICommandServices = 1936028278;
/*      */   private static final int kHICommandWizard = 1635410798;
/*      */   private static final int kHICommandNatTest = 1635413620;
/*      */   private static final int kHICommandSpeedTest = 1635414900;
/*      */   private static final int kHICommandRestart = 1635414643;
/*      */   private static final int typeAEList = 1818850164;
/*      */   private static final int kCoreEventClass = 1634039412;
/*      */   private static final int kAEOpenDocuments = 1868853091;
/*      */   private static final int kAEReopenApplication = 1918988400;
/*      */   private static final int kAEOpenContents = 1868787566;
/*      */   private static final int kURLEventClass = 1196773964;
/*      */   private static final int typeText = 1413830740;
/*      */   private static String fgAboutActionName;
/*      */   private static String fgWizardActionName;
/*      */   private static String fgNatTestActionName;
/*      */   private static String fgRestartActionName;
/*      */   private static String fgSpeedTestActionName;
/*  142 */   private static int memmove_type = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  149 */   private boolean isAZ3 = "az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"));
/*      */   
/*      */ 
/*      */   private static Class<?> claCallback;
/*      */   
/*      */   private static Constructor<?> constCallback3;
/*      */   
/*      */   private static Method mCallback_getAddress;
/*      */   
/*      */   private static Method mCallback_dispose;
/*      */   
/*      */   private static Class<?> claOS;
/*      */   
/*      */   private static Class<?> claHICommand;
/*      */   
/*      */   private static Class<?> claCFRange;
/*      */   
/*      */   private static Class<?> claAEDesc;
/*      */   
/*      */   private static Class<?> claEventRecord;
/*      */   
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  175 */       claCallback = Class.forName("org.eclipse.swt.internal.Callback");
/*      */       
/*  177 */       constCallback3 = claCallback.getConstructor(new Class[] { Object.class, String.class, Integer.TYPE });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  183 */       mCallback_getAddress = claCallback.getMethod("getAddress", new Class[0]);
/*      */       
/*  185 */       mCallback_dispose = claCallback.getMethod("dispose", new Class[0]);
/*      */       
/*  187 */       claOS = Class.forName("org.eclipse.swt.internal.carbon.OS");
/*      */       
/*  189 */       claHICommand = Class.forName("org.eclipse.swt.internal.carbon.HICommand");
/*      */       
/*  191 */       claCFRange = Class.forName("org.eclipse.swt.internal.carbon.CFRange");
/*      */       
/*  193 */       claAEDesc = Class.forName("org.eclipse.swt.internal.carbon.AEDesc");
/*      */       
/*  195 */       claEventRecord = Class.forName("org.eclipse.swt.internal.carbon.EventRecord");
/*      */     }
/*      */     catch (Exception e) {}
/*      */   }
/*      */   
/*      */   public CarbonUIEnhancer()
/*      */   {
/*  202 */     if (fgAboutActionName == null) {
/*  203 */       fgAboutActionName = MessageText.getString("MainWindow.menu.help.about").replaceAll("&", "");
/*      */     }
/*      */     
/*      */ 
/*  207 */     if (!this.isAZ3) {
/*  208 */       if (fgWizardActionName == null) {
/*  209 */         fgWizardActionName = MessageText.getString("MainWindow.menu.file.configure").replaceAll("&", "");
/*      */       }
/*      */       
/*  212 */       if (fgNatTestActionName == null) {
/*  213 */         fgNatTestActionName = MessageText.getString("MainWindow.menu.tools.nattest").replaceAll("&", "");
/*      */       }
/*      */       
/*      */ 
/*  217 */       if (fgSpeedTestActionName == null) {
/*  218 */         fgSpeedTestActionName = MessageText.getString("MainWindow.menu.tools.speedtest").replaceAll("&", "");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  223 */     if (fgRestartActionName == null) {
/*  224 */       fgRestartActionName = MessageText.getString("MainWindow.menu.file.restart").replaceAll("&", "");
/*      */     }
/*      */     
/*  227 */     earlyStartup();
/*  228 */     registerTorrentFile();
/*      */   }
/*      */   
/*      */   public static void registerToolbarToggle(Shell shell) {
/*      */     try {
/*  233 */       Object toolbarToggleCB = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "toolbarToggle", Integer.valueOf(3) });
/*      */       
/*  235 */       int toolbarToggle = ((Number)mCallback_getAddress.invoke(toolbarToggleCB, new Object[0])).intValue();
/*      */       
/*  237 */       if (toolbarToggle == 0) {
/*  238 */         Debug.out("OSX: Could not find callback 'toolbarToggle'");
/*  239 */         mCallback_dispose.invoke(toolbarToggleCB, new Object[0]);
/*  240 */         return;
/*      */       }
/*      */       
/*  243 */       shell.getDisplay().disposeExec(new Runnable() {
/*      */         public void run() {
/*      */           try {
/*  246 */             CarbonUIEnhancer.mCallback_dispose.invoke(this.val$toolbarToggleCB, new Object[0]);
/*      */ 
/*      */           }
/*      */           catch (Exception e) {}
/*      */         }
/*      */         
/*  252 */       });
/*  253 */       Object oHandle = shell.getClass().getField("handle").get(shell);
/*  254 */       int windowHandle = ((Number)invoke(claOS, null, "GetControlOwner", new Object[] { oHandle })).intValue();
/*      */       
/*      */ 
/*      */ 
/*  258 */       invoke(claOS, null, "ChangeWindowAttributes", new Object[] { Integer.valueOf(windowHandle), Integer.valueOf(64), Integer.valueOf(0) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  264 */       int[] mask = { 2003398244, 150 };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  269 */       int applicationEventTarget = ((Number)invoke(claOS, null, "GetApplicationEventTarget", new Object[0])).intValue();
/*      */       
/*      */ 
/*  272 */       invoke(claOS, null, "InstallEventHandler", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class }, new Object[] { Integer.valueOf(applicationEventTarget), Integer.valueOf(toolbarToggle), Integer.valueOf(mask.length / 2), mask, Integer.valueOf(0), null });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  288 */       Debug.out("RegisterToolbarToggle failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void registerTorrentFile()
/*      */   {
/*      */     try
/*      */     {
/*  296 */       Object clickDockIconCallback = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "clickDockIcon", Integer.valueOf(3) });
/*      */       
/*  298 */       int clickDocIcon = ((Number)mCallback_getAddress.invoke(clickDockIconCallback, new Object[0])).intValue();
/*      */       
/*  300 */       if (clickDocIcon == 0) {
/*  301 */         mCallback_dispose.invoke(clickDockIconCallback, new Object[0]);
/*      */       } else {
/*  303 */         int result = ((Number)invoke(claOS, null, "AEInstallEventHandler", new Object[] { Integer.valueOf(1634039412), Integer.valueOf(1918988400), Integer.valueOf(clickDocIcon), Integer.valueOf(0), Boolean.valueOf(false) })).intValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  312 */         if (result != 0) {
/*  313 */           Debug.out("OSX: Could Install ReopenApplication Event Handler. Error: " + result);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  318 */       Object openContentsCallback = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "openContents", Integer.valueOf(3) });
/*      */       
/*  320 */       int openContents = ((Number)mCallback_getAddress.invoke(openContentsCallback, new Object[0])).intValue();
/*      */       
/*  322 */       if (openContents == 0) {
/*  323 */         mCallback_dispose.invoke(openContentsCallback, new Object[0]);
/*      */       } else {
/*  325 */         int result = ((Number)invoke(claOS, null, "AEInstallEventHandler", new Object[] { Integer.valueOf(1634039412), Integer.valueOf(1868787566), Integer.valueOf(openContents), Integer.valueOf(0), Boolean.valueOf(false) })).intValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  334 */         if (result != 0) {
/*  335 */           Debug.out("OSX: Could Install OpenContents Event Handler. Error: " + result);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  340 */       Object openDocCallback = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "openDocProc", Integer.valueOf(3) });
/*      */       
/*  342 */       int openDocProc = ((Number)mCallback_getAddress.invoke(openDocCallback, new Object[0])).intValue();
/*      */       
/*  344 */       if (openDocProc == 0) {
/*  345 */         Debug.out("OSX: Could not find Callback 'openDocProc'");
/*  346 */         mCallback_dispose.invoke(openDocCallback, new Object[0]);
/*  347 */         return;
/*      */       }
/*      */       
/*  350 */       int result = ((Number)invoke(claOS, null, "AEInstallEventHandler", new Object[] { Integer.valueOf(1634039412), Integer.valueOf(1868853091), Integer.valueOf(openDocProc), Integer.valueOf(0), Boolean.valueOf(false) })).intValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  359 */       if (result != 0) {
/*  360 */         Debug.out("OSX: Could not Install OpenDocs Event Handler. Error: " + result);
/*      */         
/*  362 */         return;
/*      */       }
/*      */       
/*  365 */       result = ((Number)invoke(claOS, null, "AEInstallEventHandler", new Object[] { Integer.valueOf(1196773964), Integer.valueOf(1196773964), Integer.valueOf(openDocProc), Integer.valueOf(0), Boolean.valueOf(false) })).intValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  373 */       if (result != 0) {
/*  374 */         Debug.out("OSX: Could not Install URLEventClass Event Handler. Error: " + result);
/*      */         
/*  376 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  381 */       Object quitAppCallback = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "quitAppProc", Integer.valueOf(3) });
/*      */       
/*  383 */       int quitAppProc = ((Number)mCallback_getAddress.invoke(quitAppCallback, new Object[0])).intValue();
/*      */       
/*  385 */       if (quitAppProc == 0) {
/*  386 */         Debug.out("OSX: Could not find Callback 'quitApp'");
/*  387 */         mCallback_dispose.invoke(quitAppCallback, new Object[0]);
/*      */       } else {
/*  389 */         result = ((Number)invoke(claOS, null, "AEInstallEventHandler", new Object[] { Integer.valueOf(1634039412), Integer.valueOf(1903520116), Integer.valueOf(quitAppProc), Integer.valueOf(0), Boolean.valueOf(false) })).intValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  397 */         if (result != 0) {
/*  398 */           Debug.out("OSX: Could not install QuitApplication Event Handler. Error: " + result);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  405 */       int appTarget = ((Number)invoke(claOS, null, "GetApplicationEventTarget", new Object[0])).intValue();
/*      */       
/*  407 */       Object appleEventCallback = constCallback3.newInstance(new Object[] { this, "appleEventProc", Integer.valueOf(3) });
/*      */       
/*  409 */       int appleEventProc = ((Number)mCallback_getAddress.invoke(appleEventCallback, new Object[0])).intValue();
/*      */       
/*  411 */       int[] mask3 = { 1701867619, 1, 1196773964, 1918988400, 1868787566 };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  418 */       result = ((Number)invoke(claOS, null, "InstallEventHandler", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class }, new Object[] { Integer.valueOf(appTarget), Integer.valueOf(appleEventProc), Integer.valueOf(mask3.length / 2), mask3, Integer.valueOf(0), null })).intValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  434 */       if (result != 0) {
/*  435 */         Debug.out("OSX: Could Install Event Handler. Error: " + result);
/*  436 */         return;
/*      */       }
/*      */     } catch (Throwable e) {
/*  439 */       Debug.out("registerTorrentFile failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void earlyStartup()
/*      */   {
/*  447 */     final Display display = Display.getDefault();
/*  448 */     display.syncExec(new AERunnable() {
/*      */       public void runSupport() {
/*  450 */         CarbonUIEnhancer.this.hookApplicationMenu(display);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void hookApplicationMenu(Display display)
/*      */   {
/*      */     try
/*      */     {
/*  461 */       final Object commandCallback = constCallback3.newInstance(new Object[] { CarbonUIEnhancer.class, "commandProc", Integer.valueOf(3) });
/*      */       
/*  463 */       int commandProc = ((Number)mCallback_getAddress.invoke(commandCallback, new Object[0])).intValue();
/*      */       
/*  465 */       if (commandProc == 0) {
/*  466 */         mCallback_dispose.invoke(commandCallback, new Object[0]);
/*  467 */         return;
/*      */       }
/*      */       
/*      */ 
/*  471 */       int[] mask = { 1668113523, 1 };
/*      */       
/*      */ 
/*      */ 
/*  475 */       int appTarget = ((Number)invoke(claOS, null, "GetApplicationEventTarget", new Object[0])).intValue();
/*      */       
/*  477 */       invoke(claOS, null, "InstallEventHandler", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class }, new Object[] { Integer.valueOf(appTarget), Integer.valueOf(commandProc), Integer.valueOf(mask.length / 2), mask, Integer.valueOf(0), null });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  494 */       int[] outMenu = new int[1];
/*  495 */       short[] outIndex = new short[1];
/*      */       
/*  497 */       int ind = ((Number)invoke(claOS, null, "GetIndMenuItemWithCommandID", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, short[].class }, new Object[] { Integer.valueOf(0), Integer.valueOf(1886545254), Integer.valueOf(1), outMenu, outIndex })).intValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  511 */       if ((ind == 0) && (outMenu[0] != 0)) {
/*  512 */         int menu = outMenu[0];
/*      */         
/*  514 */         int l = fgAboutActionName.length();
/*  515 */         char[] buffer = new char[l];
/*  516 */         fgAboutActionName.getChars(0, l, buffer, 0);
/*  517 */         int str = CFStringCreateWithCharacters(0, buffer, l);
/*  518 */         InsertMenuItemTextWithCFString(menu, str, (short)0, 0, 1633841013);
/*  519 */         invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(str) });
/*      */         
/*      */ 
/*      */ 
/*  523 */         InsertMenuItemTextWithCFString(menu, 0, (short)1, 64, 0);
/*      */         
/*      */ 
/*      */ 
/*  527 */         invoke(claOS, null, "EnableMenuCommand", new Object[] { Integer.valueOf(menu), Integer.valueOf(1886545254) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  532 */         invoke(claOS, null, "DisableMenuCommand", new Object[] { Integer.valueOf(menu), Integer.valueOf(1936028278) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  537 */         if (!this.isAZ3)
/*      */         {
/*  539 */           l = fgWizardActionName.length();
/*  540 */           buffer = new char[l];
/*  541 */           fgWizardActionName.getChars(0, l, buffer, 0);
/*  542 */           str = CFStringCreateWithCharacters(0, buffer, l);
/*  543 */           InsertMenuItemTextWithCFString(menu, str, (short)3, 0, 1635410798);
/*      */           
/*  545 */           invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(str) });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  550 */           l = fgNatTestActionName.length();
/*  551 */           buffer = new char[l];
/*  552 */           fgNatTestActionName.getChars(0, l, buffer, 0);
/*  553 */           str = CFStringCreateWithCharacters(0, buffer, l);
/*  554 */           InsertMenuItemTextWithCFString(menu, str, (short)4, 0, 1635413620);
/*      */           
/*  556 */           invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(str) });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  561 */           l = fgSpeedTestActionName.length();
/*  562 */           buffer = new char[l];
/*  563 */           fgSpeedTestActionName.getChars(0, l, buffer, 0);
/*  564 */           str = CFStringCreateWithCharacters(0, buffer, l);
/*  565 */           InsertMenuItemTextWithCFString(menu, str, (short)5, 0, 1635414900);
/*      */           
/*  567 */           invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(str) });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  572 */         InsertMenuItemTextWithCFString(menu, 0, (short)6, 64, 0);
/*      */         
/*      */ 
/*      */ 
/*  576 */         l = fgRestartActionName.length();
/*  577 */         buffer = new char[l];
/*  578 */         fgRestartActionName.getChars(0, l, buffer, 0);
/*  579 */         str = CFStringCreateWithCharacters(0, buffer, l);
/*  580 */         InsertMenuItemTextWithCFString(menu, str, (short)7, 0, 1635414643);
/*      */         
/*  582 */         invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(str) });
/*      */         
/*      */ 
/*      */ 
/*  586 */         InsertMenuItemTextWithCFString(menu, 0, (short)8, 64, 0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  591 */       display.disposeExec(new AERunnable() {
/*      */         public void runSupport() {
/*      */           try {
/*  594 */             CarbonUIEnhancer.mCallback_dispose.invoke(commandCallback, new Object[0]);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e) {
/*  601 */       Debug.out("Failed hookApplicatioMenu", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void InsertMenuItemTextWithCFString(int mHandle, int sHandle, short index, int attributes, int commandID)
/*      */   {
/*  608 */     invoke(claOS, null, "InsertMenuItemTextWithCFString", new Class[] { Integer.TYPE, Integer.TYPE, Short.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { Integer.valueOf(mHandle), Integer.valueOf(sHandle), Short.valueOf(index), Integer.valueOf(attributes), Integer.valueOf(commandID) });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int CFStringCreateWithCharacters(int alloc, char[] buffer, int numChars)
/*      */   {
/*  626 */     return ((Number)invoke(claOS, null, "CFStringCreateWithCharacters", new Object[] { Integer.valueOf(alloc), buffer, Integer.valueOf(numChars) })).intValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   int appleEventProc(int nextHandler, int theEvent, int userData)
/*      */   {
/*      */     try
/*      */     {
/*  636 */       int eventClass = ((Number)invoke(claOS, null, "GetEventClass", new Object[] { Integer.valueOf(theEvent) })).intValue();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  647 */       if (eventClass == 1701867619) {
/*  648 */         int[] aeEventID = new int[1];
/*      */         
/*      */ 
/*      */ 
/*  652 */         int ret = ((Number)invoke(claOS, null, "GetEventParameter", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class, int[].class }, new Object[] { Integer.valueOf(theEvent), Integer.valueOf(1702261865), Integer.valueOf(1954115685), null, Integer.valueOf(4), null, aeEventID })).intValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  671 */         if (ret != 0) {
/*  672 */           return 55662;
/*      */         }
/*      */         
/*  675 */         if ((aeEventID[0] != 1868853091) && (aeEventID[0] != 1196773964) && (aeEventID[0] != 1918988400) && (aeEventID[0] != 1868787566) && (aeEventID[0] != 1903520116))
/*      */         {
/*      */ 
/*      */ 
/*  679 */           return 55662;
/*      */         }
/*      */         
/*      */ 
/*  683 */         Object eventRecord = claEventRecord.newInstance();
/*  684 */         invoke(claOS, null, "ConvertEventRefToEventRecord", new Class[] { Integer.TYPE, claEventRecord }, new Object[] { Integer.valueOf(theEvent), eventRecord });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  691 */         invoke(claOS, null, "AEProcessAppleEvent", new Object[] { eventRecord });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  696 */         return 0;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  700 */       Debug.out(e);
/*      */     }
/*  702 */     return 55662;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void memmove(byte[] dest, int src, int size)
/*      */   {
/*  717 */     switch (memmove_type) {
/*      */     case 0: 
/*      */       try {
/*  720 */         OSXAccess.memmove(dest, src, size);
/*  721 */         memmove_type = 0;
/*  722 */         return;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     
/*      */     case 1: 
/*      */       try
/*      */       {
/*  729 */         Class<?> cMemMove = Class.forName("org.eclipse.swt.internal.carbon.OS");
/*      */         
/*  731 */         Method method = cMemMove.getMethod("memmove", new Class[] { byte[].class, Integer.TYPE, Integer.TYPE });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  737 */         method.invoke(null, new Object[] { dest, new Integer(src), new Integer(size) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  742 */         memmove_type = 1;
/*  743 */         return;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     
/*      */     case 2: 
/*      */       try
/*      */       {
/*  750 */         Class<?> cMemMove = Class.forName("org.eclipse.swt.internal.carbon.OS");
/*      */         
/*  752 */         Method method = cMemMove.getMethod("memcpy", new Class[] { byte[].class, Integer.TYPE, Integer.TYPE });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  758 */         method.invoke(null, new Object[] { dest, new Integer(src), new Integer(size) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  764 */         memmove_type = 2;
/*  765 */         return;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  775 */     memmove_type = 3;
/*      */   }
/*      */   
/*      */   static final int commandProc(int nextHandler, int theEvent, int userData) {
/*      */     try {
/*  780 */       int kind = ((Number)invoke(claOS, null, "GetEventKind", new Object[] { Integer.valueOf(theEvent) })).intValue();
/*      */       
/*      */ 
/*  783 */       if (kind == 1) {
/*  784 */         Object command = claHICommand.newInstance();
/*      */         
/*      */ 
/*  787 */         invoke(claOS, null, "GetEventParameter", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class, claHICommand }, new Object[] { Integer.valueOf(theEvent), Integer.valueOf(757935405), Integer.valueOf(1751346532), null, Integer.valueOf(claHICommand.getField("sizeof").getInt(command)), null, command });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  804 */         int commandID = claHICommand.getField("commandID").getInt(command);
/*  805 */         switch (commandID) {
/*      */         case 1886545254: 
/*  807 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  808 */           if (uiFunctions != null) {
/*  809 */             uiFunctions.getMDI().showEntryByID("ConfigView");
/*      */           }
/*      */           
/*  812 */           return 0;
/*      */         
/*      */         case 1633841013: 
/*  815 */           AboutWindow.show();
/*  816 */           return 0;
/*      */         case 1635414643: 
/*  818 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  819 */           if (uiFunctions != null) {
/*  820 */             uiFunctions.dispose(true, false);
/*      */           }
/*  822 */           return 0;
/*      */         
/*      */         case 1635410798: 
/*  825 */           new ConfigureWizard(false, 0);
/*  826 */           return 0;
/*      */         case 1635413620: 
/*  828 */           new NatTestWindow();
/*  829 */           return 0;
/*      */         case 1635414900: 
/*  831 */           new SpeedTestWizard();
/*  832 */           return 0;
/*      */         
/*      */         case 1903520116: 
/*  835 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  836 */           if (uiFunctions != null) {
/*  837 */             uiFunctions.dispose(false, false);
/*  838 */             return 0;
/*      */           }
/*  840 */           UIExitUtilsSWT.setSkipCloseCheck(true);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  847 */       Debug.out(t);
/*      */     }
/*  849 */     return 55662;
/*      */   }
/*      */   
/*      */   static final int quitAppProc(int theAppleEvent, int reply, int handlerRefcon) {
/*  853 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  854 */     if (uiFunctions != null) {
/*  855 */       uiFunctions.dispose(false, false);
/*      */     } else {
/*  857 */       UIExitUtilsSWT.setSkipCloseCheck(true);
/*  858 */       Display.getDefault().dispose();
/*      */     }
/*  860 */     return 0;
/*      */   }
/*      */   
/*      */   static final int openDocProc(int theAppleEvent, int reply, int handlerRefcon) {
/*      */     try {
/*  865 */       Object aeDesc = claAEDesc.newInstance();
/*  866 */       Object eventRecord = claEventRecord.newInstance();
/*  867 */       invoke(claOS, null, "ConvertEventRefToEventRecord", new Class[] { Integer.TYPE, claEventRecord }, new Object[] { Integer.valueOf(theAppleEvent), eventRecord });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  875 */         int result = OSXAccess.AEGetParamDesc(theAppleEvent, 757935405, 1818850164, aeDesc);
/*      */         
/*  877 */         if (result != 0) {
/*  878 */           Debug.out("OSX: Could call AEGetParamDesc. Error: " + result);
/*  879 */           return 0;
/*      */         }
/*      */       } catch (UnsatisfiedLinkError e) {
/*  882 */         Debug.out("OSX: AEGetParamDesc not available.  Can't open sent file");
/*  883 */         return 0;
/*      */       }
/*      */       
/*  886 */       int[] count = new int[1];
/*  887 */       invoke(claOS, null, "AECountItems", new Class[] { claAEDesc, int[].class }, new Object[] { aeDesc, count });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  895 */       if (count[0] > 0) {
/*  896 */         String[] fileNames = new String[count[0]];
/*  897 */         int maximumSize = 80;
/*  898 */         int dataPtr = ((Number)invoke(claOS, null, "NewPtr", new Object[] { Integer.valueOf(maximumSize) })).intValue();
/*      */         
/*      */ 
/*  901 */         int[] aeKeyword = new int[1];
/*  902 */         int[] typeCode = new int[1];
/*  903 */         int[] actualSize = new int[1];
/*  904 */         for (int i = 0; i < count[0]; i++)
/*      */         {
/*      */           try
/*      */           {
/*  908 */             Class<?>[] sigAEGetNthPtr = { claAEDesc, Integer.TYPE, Integer.TYPE, int[].class, int[].class, Integer.TYPE, Integer.TYPE, int[].class };
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  918 */             int ret = ((Number)invoke(claOS, null, "AEGetNthPtr", sigAEGetNthPtr, new Object[] { aeDesc, Integer.valueOf(i + 1), Integer.valueOf(1718841958), aeKeyword, typeCode, Integer.valueOf(dataPtr), Integer.valueOf(maximumSize), actualSize })).intValue();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  929 */             if (ret == 0) {
/*  930 */               byte[] fsRef = new byte[actualSize[0]];
/*  931 */               memmove(fsRef, dataPtr, actualSize[0]);
/*  932 */               int dirUrl = ((Number)invoke(claOS, null, "CFURLCreateFromFSRef", new Object[] { Integer.valueOf(0), fsRef })).intValue();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  937 */               int dirString = ((Number)invoke(claOS, null, "CFURLCopyFileSystemPath", new Object[] { Integer.valueOf(dirUrl), Integer.valueOf(0) })).intValue();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  942 */               int length = ((Number)invoke(claOS, null, "CFStringGetLength", new Object[] { Integer.valueOf(dirString) })).intValue();
/*      */               
/*      */ 
/*      */ 
/*  946 */               char[] buffer = new char[length];
/*  947 */               Object range = claCFRange.newInstance();
/*  948 */               claCFRange.getField("length").setInt(range, length);
/*  949 */               invoke(claOS, null, "CFStringGetCharacters", new Class[] { Integer.TYPE, claCFRange, char[].class }, new Object[] { Integer.valueOf(dirString), range, buffer });
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  958 */               fileNames[i] = new String(buffer);
/*  959 */               invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(dirString) });
/*      */               
/*      */ 
/*  962 */               invoke(claOS, null, "CFRelease", new Object[] { Integer.valueOf(dirUrl) });
/*      */             }
/*      */             else
/*      */             {
/*  966 */               ret = ((Number)invoke(claOS, null, "AEGetNthPtr", sigAEGetNthPtr, new Object[] { aeDesc, Integer.valueOf(i + 1), Integer.valueOf(1413830740), aeKeyword, typeCode, Integer.valueOf(dataPtr), Integer.valueOf(2048), actualSize })).intValue();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  978 */               if (ret == 0) {
/*  979 */                 byte[] urlRef = new byte[actualSize[0]];
/*  980 */                 memmove(urlRef, dataPtr, actualSize[0]);
/*  981 */                 fileNames[i] = new String(urlRef);
/*      */               }
/*      */             }
/*      */           } catch (Throwable t) {
/*  985 */             Debug.out(t);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  990 */         AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */           public void azureusCoreRunning(AzureusCore core) {
/*  992 */             UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentOpenOptions(Utils.findAnyShell(), null, this.val$fileNames, false, false);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*  998 */       return 0;
/*      */     } catch (Throwable e) {
/* 1000 */       Debug.out(e);
/*      */     }
/* 1002 */     return 55662;
/*      */   }
/*      */   
/*      */   static final int clickDockIcon(int nextHandler, int theEvent, int userData) {
/* 1006 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1007 */     if (uiFunctions != null) {
/* 1008 */       uiFunctions.bringToFront();
/* 1009 */       return 0;
/*      */     }
/* 1011 */     return 55662;
/*      */   }
/*      */   
/*      */   static final int openContents(int nextHandler, int theEvent, int userData) {
/* 1015 */     Debug.out("openDocContents");
/* 1016 */     return 0;
/*      */   }
/*      */   
/*      */   static final int toolbarToggle(int nextHandler, int theEvent, int userData) {
/* 1020 */     int eventKind = ((Number)invoke(claOS, null, "GetEventKind", new Object[] { Integer.valueOf(theEvent) })).intValue();
/*      */     
/*      */ 
/* 1023 */     if (eventKind != 150) {
/* 1024 */       return 55662;
/*      */     }
/*      */     
/* 1027 */     int[] theWindow = new int[1];
/*      */     
/*      */ 
/* 1030 */     invoke(claOS, null, "GetEventParameter", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class, Integer.TYPE, int[].class, int[].class }, new Object[] { Integer.valueOf(theEvent), Integer.valueOf(757935405), Integer.valueOf(2003398244), null, Integer.valueOf(4), null, theWindow });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1048 */     int[] theRoot = new int[1];
/* 1049 */     invoke(claOS, null, "GetRootControl", new Object[] { Integer.valueOf(theWindow[0]), theRoot });
/*      */     
/*      */ 
/*      */ 
/* 1053 */     final Widget widget = Display.getCurrent().findWidget(theRoot[0]);
/*      */     
/* 1055 */     if (!(widget instanceof Shell)) {
/* 1056 */       return 55662;
/*      */     }
/* 1058 */     Shell shellAffected = (Shell)widget;
/*      */     
/* 1060 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1063 */         Long l = (Long)this.val$shellAffected.getData("OSX.ToolBarToggle");
/* 1064 */         int type; int type; if ((l == null) || (l.longValue() == 0L)) {
/* 1065 */           type = 18;
/*      */         } else {
/* 1067 */           type = 17;
/*      */         }
/*      */         
/* 1070 */         Event event = new Event();
/* 1071 */         event.type = type;
/* 1072 */         event.display = widget.getDisplay();
/* 1073 */         event.widget = widget;
/* 1074 */         this.val$shellAffected.notifyListeners(type, event);
/*      */         
/* 1076 */         this.val$shellAffected.setData("OSX.ToolBarToggle", new Long(type == 18 ? 1L : 0L));
/*      */       }
/*      */       
/*      */ 
/* 1080 */     });
/* 1081 */     return 0;
/*      */   }
/*      */   
/*      */   private static Object invoke(Class<?> clazz, Object target, String methodName, Object[] args)
/*      */   {
/*      */     try {
/* 1087 */       Class<?>[] signature = new Class[args.length];
/* 1088 */       for (int i = 0; i < args.length; i++) {
/* 1089 */         Class<?> thisClass = args[i].getClass();
/* 1090 */         if (thisClass == Integer.class) {
/* 1091 */           signature[i] = Integer.TYPE;
/* 1092 */         } else if (thisClass == Long.class) {
/* 1093 */           signature[i] = Long.TYPE;
/* 1094 */         } else if (thisClass == Byte.class) {
/* 1095 */           signature[i] = Byte.TYPE;
/* 1096 */         } else if (thisClass == Boolean.class) {
/* 1097 */           signature[i] = Boolean.TYPE;
/*      */         } else
/* 1099 */           signature[i] = thisClass;
/*      */       }
/* 1101 */       Method method = clazz.getMethod(methodName, signature);
/* 1102 */       return method.invoke(target, args);
/*      */     } catch (Exception e) {
/* 1104 */       throw new IllegalStateException(e);
/*      */     }
/*      */   }
/*      */   
/*      */   private static Object invoke(Class<?> clazz, Object target, String methodName, Class<?>[] signature, Object[] args)
/*      */   {
/*      */     try {
/* 1111 */       Method method = clazz.getDeclaredMethod(methodName, signature);
/* 1112 */       method.setAccessible(true);
/* 1113 */       return method.invoke(target, args);
/*      */     } catch (Exception e) {
/* 1115 */       throw new IllegalStateException(e);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/osx/CarbonUIEnhancer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */