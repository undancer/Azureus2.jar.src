/*     */ package org.gudy.azureus2.ui.swt.osx;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.internal.C;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.wizard.ConfigureWizard;
/*     */ import org.gudy.azureus2.ui.swt.help.AboutWindow;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper.IViewInfo;
/*     */ import org.gudy.azureus2.ui.swt.nat.NatTestWindow;
/*     */ import org.gudy.azureus2.ui.swt.speedtest.SpeedTestWizard;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CocoaUIEnhancer
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static Object callBack3;
/*     */   private static long callBack3Addr;
/*     */   private static Object callBack4;
/*     */   private static long callBack4Addr;
/*     */   private static CocoaUIEnhancer instance;
/*     */   private static final int kServicesMenuItem = 4;
/*  94 */   private static int NSWindowToolbarButton = 3;
/*     */   
/*     */ 
/*     */   private static long sel_application_openFile_;
/*     */   
/*     */ 
/*     */   private static long sel_application_openFiles_;
/*     */   
/*     */ 
/*     */   private static long sel_applicationShouldHandleReopen_;
/*     */   
/*     */   private static long sel_toolbarButtonClicked_;
/*     */   
/*     */   private static boolean alreadyHaveOpenDoc;
/*     */   
/* 109 */   static final byte[] SWT_OBJECT = { 83, 87, 84, 95, 79, 66, 74, 69, 67, 84, 0 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long delegateIdSWTApplication;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long delegateJniRef;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object delegate;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 129 */   private static boolean initialized = false;
/*     */   
/* 131 */   private static Class<?> osCls = classForName("org.eclipse.swt.internal.cocoa.OS");
/* 132 */   private static Class<?> nsmenuCls = classForName("org.eclipse.swt.internal.cocoa.NSMenu");
/* 133 */   private static Class<?> nsmenuitemCls = classForName("org.eclipse.swt.internal.cocoa.NSMenuItem");
/* 134 */   private static Class<?> nsapplicationCls = classForName("org.eclipse.swt.internal.cocoa.NSApplication");
/* 135 */   private static Class<?> nsarrayCls = classForName("org.eclipse.swt.internal.cocoa.NSArray");
/* 136 */   private static Class<?> nsstringCls = classForName("org.eclipse.swt.internal.cocoa.NSString");
/* 137 */   private static Class<?> nsidCls = classForName("org.eclipse.swt.internal.cocoa.id");
/* 138 */   private static Class<?> nsautoreleasepoolCls = classForName("org.eclipse.swt.internal.cocoa.NSAutoreleasePool");
/* 139 */   private static Class<?> nsworkspaceCls = classForName("org.eclipse.swt.internal.cocoa.NSWorkspace");
/* 140 */   private static Class<?> nsimageCls = classForName("org.eclipse.swt.internal.cocoa.NSImage");
/* 141 */   private static Class<?> nssizeCls = classForName("org.eclipse.swt.internal.cocoa.NSSize");
/* 142 */   private static Class<?> nsscreenCls = classForName("org.eclipse.swt.internal.cocoa.NSScreen");
/*     */   
/*     */   static
/*     */   {
/* 146 */     Class<CocoaUIEnhancer> clazz = CocoaUIEnhancer.class;
/* 147 */     Class<?> callbackCls = classForName("org.eclipse.swt.internal.Callback");
/*     */     try
/*     */     {
/* 150 */       SWT.class.getDeclaredField("OpenDocument");
/* 151 */       alreadyHaveOpenDoc = true;
/*     */     } catch (Throwable t) {
/* 153 */       alreadyHaveOpenDoc = false;
/*     */     }
/*     */     try
/*     */     {
/* 157 */       Method mGetAddress = callbackCls.getMethod("getAddress", new Class[0]);
/* 158 */       Constructor<?> consCallback = callbackCls.getConstructor(new Class[] { Object.class, String.class, Integer.TYPE });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 164 */       callBack3 = consCallback.newInstance(new Object[] { clazz, "actionProc", Integer.valueOf(3) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 169 */       Object object = mGetAddress.invoke(callBack3, (Object[])null);
/* 170 */       callBack3Addr = convertToLong(object);
/* 171 */       if (callBack3Addr == 0L) {
/* 172 */         SWT.error(3);
/*     */       }
/*     */       
/*     */ 
/* 176 */       callBack4 = consCallback.newInstance(new Object[] { clazz, "actionProc", Integer.valueOf(4) });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 181 */       object = mGetAddress.invoke(callBack4, (Object[])null);
/* 182 */       callBack4Addr = convertToLong(object);
/* 183 */       if (callBack4Addr == 0L) {
/* 184 */         SWT.error(3);
/*     */       }
/*     */     } catch (Throwable e) {
/* 187 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   static int actionProc(int id, int sel, int arg0)
/*     */   {
/* 193 */     return (int)actionProc(id, sel, arg0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static long actionProc(long id, long sel, long arg0)
/*     */   {
/* 202 */     if (sel == sel_toolbarButtonClicked_) {
/*     */       try {
/* 204 */         Field fldsel_window = osCls.getField("sel_window");
/* 205 */         Object windowId = invoke(osCls, "objc_msgSend", new Object[] { wrapPointer(arg0), fldsel_window.get(null) });
/*     */         
/*     */ 
/*     */ 
/* 209 */         Shell shellAffected = (Shell)invoke(Display.class, Display.getCurrent(), "findWidget", new Object[] { windowId });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 214 */         Utils.execSWTThread(new AERunnable()
/*     */         {
/*     */           public void runSupport() {
/* 217 */             Long l = (Long)this.val$shellAffected.getData("OSX.ToolBarToggle");
/* 218 */             int type; int type; if ((l == null) || (l.longValue() == 0L)) {
/* 219 */               type = 18;
/*     */             } else {
/* 221 */               type = 17;
/*     */             }
/*     */             
/* 224 */             Event event = new Event();
/* 225 */             event.type = type;
/* 226 */             event.display = this.val$shellAffected.getDisplay();
/* 227 */             event.widget = this.val$shellAffected;
/* 228 */             this.val$shellAffected.notifyListeners(type, event);
/*     */             
/* 230 */             this.val$shellAffected.setData("OSX.ToolBarToggle", new Long(type == 18 ? 1L : 0L));
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Throwable t) {
/* 235 */         Debug.out(t);
/*     */       }
/*     */     }
/*     */     
/* 239 */     return 0L;
/*     */   }
/*     */   
/*     */   static int actionProc(int id, int sel, int arg0, int arg1)
/*     */     throws Throwable
/*     */   {
/* 245 */     return (int)actionProc(id, sel, arg0, arg1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static long actionProc(long id, long sel, long arg0, long arg1)
/*     */     throws Throwable
/*     */   {
/* 255 */     Display display = Display.getCurrent();
/* 256 */     if (display == null) {
/* 257 */       return 0L;
/*     */     }
/* 259 */     if ((!alreadyHaveOpenDoc) && (sel == sel_application_openFile_)) {
/* 260 */       Constructor<?> conNSString = nsstringCls.getConstructor(new Class[] { Integer.TYPE });
/*     */       
/*     */ 
/* 263 */       Object file = conNSString.newInstance(new Object[] { Long.valueOf(arg1) });
/* 264 */       String fileString = (String)invoke(file, "getString");
/*     */       
/*     */ 
/*     */ 
/* 268 */       OSXFileOpen.fileOpen(fileString);
/* 269 */     } else if ((!alreadyHaveOpenDoc) && (sel == sel_application_openFiles_)) {
/* 270 */       Constructor<?> conNSArray = nsarrayCls.getConstructor(new Class[] { Integer.TYPE });
/*     */       
/*     */ 
/* 273 */       Constructor<?> conNSString = nsstringCls.getConstructor(new Class[] { nsidCls });
/*     */       
/*     */ 
/*     */ 
/* 277 */       Object arrayOfFiles = conNSArray.newInstance(new Object[] { Long.valueOf(arg1) });
/* 278 */       int count = ((Number)invoke(arrayOfFiles, "count")).intValue();
/*     */       
/* 280 */       String[] files = new String[count];
/* 281 */       for (int i = 0; i < count; i++) {
/* 282 */         Object fieldId = invoke(nsarrayCls, arrayOfFiles, "objectAtIndex", new Object[] { Integer.valueOf(i) });
/*     */         
/*     */ 
/*     */ 
/* 286 */         Object nsstring = conNSString.newInstance(new Object[] { fieldId });
/* 287 */         files[i] = ((String)invoke(nsstring, "getString"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 293 */       OSXFileOpen.fileOpen(files);
/* 294 */     } else if (sel == sel_applicationShouldHandleReopen_) {
/* 295 */       Event event = new Event();
/* 296 */       event.detail = 1;
/* 297 */       if (display != null) {
/* 298 */         invoke(Display.class, display, "sendEvent", new Class[] { Integer.TYPE, Event.class }, new Object[] { Integer.valueOf(26), event });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */     return 0L;
/*     */   }
/*     */   
/*     */   private static Class<?> classForName(String classname) {
/*     */     try {
/* 312 */       return Class.forName(classname);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/* 315 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static long convertToLong(Object object) {
/* 320 */     if ((object instanceof Integer)) {
/* 321 */       Integer i = (Integer)object;
/* 322 */       return i.longValue();
/*     */     }
/* 324 */     if ((object instanceof Long)) {
/* 325 */       Long l = (Long)object;
/* 326 */       return l.longValue();
/*     */     }
/* 328 */     return 0L;
/*     */   }
/*     */   
/*     */   public static CocoaUIEnhancer getInstance() {
/* 332 */     if (instance == null) {
/*     */       try {
/* 334 */         instance = new CocoaUIEnhancer();
/*     */       } catch (Throwable e) {
/* 336 */         Debug.out(e);
/*     */       }
/*     */     }
/* 339 */     return instance;
/*     */   }
/*     */   
/*     */   private static Object invoke(Class<?> clazz, Object target, String methodName, Object[] args)
/*     */   {
/*     */     try {
/* 345 */       Class<?>[] signature = new Class[args.length];
/* 346 */       for (int i = 0; i < args.length; i++) {
/* 347 */         Class<?> thisClass = args[i].getClass();
/* 348 */         if (thisClass == Integer.class) {
/* 349 */           signature[i] = Integer.TYPE;
/* 350 */         } else if (thisClass == Long.class) {
/* 351 */           signature[i] = Long.TYPE;
/* 352 */         } else if (thisClass == Byte.class) {
/* 353 */           signature[i] = Byte.TYPE;
/* 354 */         } else if (thisClass == Boolean.class) {
/* 355 */           signature[i] = Boolean.TYPE;
/*     */         } else
/* 357 */           signature[i] = thisClass;
/*     */       }
/* 359 */       Method method = clazz.getMethod(methodName, signature);
/* 360 */       return method.invoke(target, args);
/*     */     } catch (Exception e) {
/* 362 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Object invoke(Class<?> clazz, Object target, String methodName, Class[] signature, Object[] args)
/*     */   {
/*     */     try {
/* 369 */       Method method = clazz.getDeclaredMethod(methodName, signature);
/* 370 */       method.setAccessible(true);
/* 371 */       return method.invoke(target, args);
/*     */     } catch (Exception e) {
/* 373 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Object invoke(Class<?> clazz, String methodName, Object[] args) {
/* 378 */     return invoke(clazz, null, methodName, args);
/*     */   }
/*     */   
/*     */   private static Object invoke(Object obj, String methodName) {
/* 382 */     return invoke(obj, methodName, (Class[])null, (Object[])null);
/*     */   }
/*     */   
/*     */   private static Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object... arguments)
/*     */   {
/*     */     try {
/* 388 */       Method m = obj.getClass().getMethod(methodName, paramTypes);
/* 389 */       return m.invoke(obj, arguments);
/*     */     } catch (Exception e) {
/* 391 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static long registerName(Class<?> osCls, String name)
/*     */     throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
/*     */   {
/* 398 */     Object object = invoke(osCls, "sel_registerName", new Object[] { name });
/*     */     
/*     */ 
/* 401 */     return convertToLong(object);
/*     */   }
/*     */   
/*     */ 
/*     */   private static Object wrapPointer(long value)
/*     */   {
/* 407 */     Class<?> PTR_CLASS = C.PTR_SIZEOF == 8 ? Long.TYPE : Integer.TYPE;
/* 408 */     if (PTR_CLASS == Long.TYPE) {
/* 409 */       return new Long(value);
/*     */     }
/* 411 */     return new Integer((int)value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private CocoaUIEnhancer()
/*     */     throws Throwable
/*     */   {
/* 422 */     Object delegateObjSWTApplication = invoke(osCls, "objc_lookUpClass", new Object[] { "SWTApplicationDelegate" });
/*     */     
/*     */ 
/*     */ 
/* 426 */     this.delegateIdSWTApplication = convertToLong(delegateObjSWTApplication);
/*     */     
/*     */ 
/* 429 */     Class<?> swtapplicationdelegateCls = classForName("org.eclipse.swt.internal.cocoa.SWTApplicationDelegate");
/* 430 */     this.delegate = swtapplicationdelegateCls.newInstance();
/* 431 */     Object delegateAlloc = invoke(this.delegate, "alloc");
/* 432 */     invoke(delegateAlloc, "init");
/* 433 */     Object delegateIdObj = nsidCls.getField("id").get(this.delegate);
/* 434 */     this.delegateJniRef = ((Number)invoke(osCls, "NewGlobalRef", new Class[] { Object.class }, new Object[] { this })).longValue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 439 */     if (this.delegateJniRef == 0L) {
/* 440 */       SWT.error(2);
/*     */     }
/* 442 */     invoke(osCls, "object_setInstanceVariable", new Object[] { delegateIdObj, SWT_OBJECT, wrapPointer(this.delegateJniRef) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void hookApplicationMenu()
/*     */   {
/* 455 */     Display display = Display.getCurrent();
/*     */     try
/*     */     {
/* 458 */       initialize();
/*     */     } catch (Exception e) {
/* 460 */       throw new IllegalStateException(e);
/*     */     }
/*     */     
/*     */ 
/* 464 */     display.disposeExec(new Runnable() {
/*     */       public void run() {
/* 466 */         CocoaUIEnhancer.invoke(CocoaUIEnhancer.callBack3, "dispose");
/* 467 */         CocoaUIEnhancer.access$002(null);
/* 468 */         CocoaUIEnhancer.invoke(CocoaUIEnhancer.callBack4, "dispose");
/* 469 */         CocoaUIEnhancer.access$202(null);
/*     */         
/* 471 */         if (CocoaUIEnhancer.this.delegateJniRef != 0L)
/*     */         {
/* 473 */           CocoaUIEnhancer.invoke(CocoaUIEnhancer.osCls, "DeleteGlobalRef", new Object[] { CocoaUIEnhancer.wrapPointer(CocoaUIEnhancer.this.delegateJniRef) });
/*     */           
/*     */ 
/* 476 */           CocoaUIEnhancer.this.delegateJniRef = 0L;
/*     */         }
/*     */         
/* 479 */         if (CocoaUIEnhancer.this.delegate != null) {
/* 480 */           CocoaUIEnhancer.invoke(CocoaUIEnhancer.this.delegate, "release");
/* 481 */           CocoaUIEnhancer.this.delegate = null;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void hookDocumentOpen()
/*     */     throws Throwable
/*     */   {
/* 490 */     if (alreadyHaveOpenDoc) {
/* 491 */       return;
/*     */     }
/*     */     
/* 494 */     if (sel_application_openFile_ == 0L) {
/* 495 */       sel_application_openFile_ = registerName(osCls, "application:openFile:");
/*     */     }
/* 497 */     invoke(osCls, "class_addMethod", new Object[] { wrapPointer(this.delegateIdSWTApplication), wrapPointer(sel_application_openFile_), wrapPointer(callBack4Addr), "@:@:@" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 504 */     if (sel_application_openFiles_ == 0L) {
/* 505 */       sel_application_openFiles_ = registerName(osCls, "application:openFiles:");
/*     */     }
/* 507 */     invoke(osCls, "class_addMethod", new Object[] { wrapPointer(this.delegateIdSWTApplication), wrapPointer(sel_application_openFiles_), wrapPointer(callBack4Addr), "@:@:@" });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static MenuItem getItem(Menu menu, int id)
/*     */   {
/* 516 */     MenuItem[] items = menu.getItems();
/* 517 */     for (int i = 0; i < items.length; i++) {
/* 518 */       if (items[i].getID() == id) return items[i];
/*     */     }
/* 520 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private void initialize()
/*     */     throws Exception
/*     */   {
/* 527 */     Object sharedApplication = invoke(nsapplicationCls, "sharedApplication");
/* 528 */     Object mainMenu = invoke(sharedApplication, "mainMenu");
/* 529 */     Object mainMenuItem = invoke(nsmenuCls, mainMenu, "itemAtIndex", new Object[] { wrapPointer(0L) });
/*     */     
/*     */ 
/*     */ 
/* 533 */     Object appMenu = invoke(mainMenuItem, "submenu");
/*     */     
/*     */ 
/*     */ 
/* 537 */     Object servicesMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[] { wrapPointer(4L) });
/*     */     
/*     */ 
/*     */ 
/* 541 */     invoke(nsmenuitemCls, servicesMenuItem, "setEnabled", new Object[] { Boolean.valueOf(false) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 546 */     Menu systemMenu = Display.getCurrent().getSystemMenu();
/* 547 */     if (systemMenu != null)
/*     */     {
/* 549 */       MenuItem sysItem = getItem(systemMenu, -1);
/* 550 */       if (sysItem != null) {
/* 551 */         sysItem.addSelectionListener(new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {}
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 558 */       sysItem = getItem(systemMenu, -2);
/* 559 */       if (sysItem != null) {
/* 560 */         sysItem.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 562 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 563 */             if (uiFunctions != null) {
/* 564 */               uiFunctions.getMDI().showEntryByID("ConfigView");
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 571 */       int quitIndex = systemMenu.indexOf(getItem(systemMenu, -6));
/* 572 */       MenuItem restartItem = new MenuItem(systemMenu, 64, quitIndex);
/* 573 */       Messages.setLanguageText(restartItem, "MainWindow.menu.file.restart");
/* 574 */       restartItem.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 576 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 577 */           if (uiFunctions != null) {
/* 578 */             uiFunctions.dispose(true, false);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 583 */       });
/* 584 */       boolean isAZ3 = "az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"));
/*     */       
/* 586 */       if (!isAZ3)
/*     */       {
/*     */ 
/* 589 */         int prefIndex = systemMenu.indexOf(getItem(systemMenu, -2)) + 1;
/*     */         
/* 591 */         MenuItem wizItem = new MenuItem(systemMenu, 64, prefIndex);
/* 592 */         Messages.setLanguageText(wizItem, "MainWindow.menu.file.configure");
/* 593 */         wizItem.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 595 */             new ConfigureWizard(false, 0);
/*     */           }
/*     */           
/* 598 */         });
/* 599 */         MenuItem natMenu = new MenuItem(systemMenu, 64, prefIndex);
/* 600 */         Messages.setLanguageText(natMenu, "MainWindow.menu.tools.nattest");
/* 601 */         natMenu.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 603 */             new NatTestWindow();
/*     */           }
/*     */           
/* 606 */         });
/* 607 */         MenuItem netstatMenu = new MenuItem(systemMenu, 64, prefIndex);
/* 608 */         Messages.setLanguageText(netstatMenu, "MainWindow.menu.tools.netstat");
/* 609 */         netstatMenu.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 611 */             UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 612 */             if (uiFunctions != null)
/*     */             {
/* 614 */               PluginsMenuHelper.IViewInfo[] views = PluginsMenuHelper.getInstance().getPluginViewsInfo();
/*     */               
/* 616 */               for (PluginsMenuHelper.IViewInfo view : views)
/*     */               {
/* 618 */                 String viewID = view.viewID;
/*     */                 
/* 620 */                 if ((viewID != null) && (viewID.equals("aznetstatus")))
/*     */                 {
/* 622 */                   view.openView(uiFunctions);
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/* 628 */         });
/* 629 */         MenuItem speedMenu = new MenuItem(systemMenu, 64, prefIndex);
/* 630 */         Messages.setLanguageText(speedMenu, "MainWindow.menu.tools.speedtest");
/* 631 */         speedMenu.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 633 */             new SpeedTestWizard();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 641 */     if (sel_applicationShouldHandleReopen_ == 0L) {
/* 642 */       sel_applicationShouldHandleReopen_ = registerName(osCls, "applicationShouldHandleReopen:hasVisibleWindows:");
/*     */     }
/*     */     
/*     */ 
/* 646 */     invoke(osCls, "class_addMethod", new Object[] { wrapPointer(this.delegateIdSWTApplication), wrapPointer(sel_applicationShouldHandleReopen_), wrapPointer(callBack4Addr), "@:@c" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 653 */     initialized = true;
/*     */   }
/*     */   
/*     */   private Object invoke(Class<?> cls, String methodName)
/*     */   {
/* 658 */     return invoke(cls, methodName, (Class[])null, (Object[])null);
/*     */   }
/*     */   
/*     */   private Object invoke(Class<?> cls, String methodName, Class<?>[] paramTypes, Object... arguments)
/*     */   {
/*     */     try {
/* 664 */       Method m = cls.getMethod(methodName, paramTypes);
/* 665 */       return m.invoke(null, arguments);
/*     */     } catch (Exception e) {
/* 667 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void registerToolbarToggle(Shell shell)
/*     */     throws Throwable
/*     */   {
/* 674 */     if (sel_toolbarButtonClicked_ == 0L) {
/* 675 */       sel_toolbarButtonClicked_ = registerName(osCls, "toolbarButtonClicked:");
/*     */     }
/*     */     
/* 678 */     invoke(osCls, "class_addMethod", new Object[] { wrapPointer(this.delegateIdSWTApplication), wrapPointer(sel_toolbarButtonClicked_), wrapPointer(callBack3Addr), "@:@" });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 685 */     Class<?> nstoolbarCls = classForName("org.eclipse.swt.internal.cocoa.NSToolbar");
/* 686 */     Class<?> nsbuttonCls = classForName("org.eclipse.swt.internal.cocoa.NSButton");
/*     */     
/*     */ 
/* 689 */     Object dummyBar = nstoolbarCls.newInstance();
/*     */     
/* 691 */     invoke(dummyBar, "alloc");
/*     */     
/* 693 */     Object nsStrDummyToolbar = invoke(nsstringCls, "stringWith", new Object[] { "SWTToolbar" });
/*     */     
/*     */ 
/* 696 */     invoke(dummyBar, "initWithIdentifier", new Class[] { nsstringCls }, new Object[] { nsStrDummyToolbar });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 702 */     invoke(dummyBar, "setVisible", new Class[] { Boolean.TYPE }, new Object[] { Boolean.FALSE });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 710 */     Object view = shell.getClass().getField("view").get(shell);
/* 711 */     Object nsWindow = invoke(view, "window");
/*     */     
/* 713 */     invoke(nsWindow, "setToolbar", new Class[] { nstoolbarCls }, new Object[] { dummyBar });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 719 */     invoke(nsWindow, "setShowsToolbarButton", new Class[] { Boolean.TYPE }, new Object[] { Boolean.TRUE });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 726 */     Object toolbarButton = invoke(nsWindow, "standardWindowButton", new Class[] { Integer.TYPE }, new Object[] { new Integer(NSWindowToolbarButton) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 734 */     invoke(toolbarButton, "setTarget", new Class[] { nsidCls }, new Object[] { this.delegate });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 748 */     invoke(nsbuttonCls, toolbarButton, "setAction", new Object[] { wrapPointer(sel_toolbarButtonClicked_) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Image getFileIcon(String path, int imageWidthHeight)
/*     */   {
/* 755 */     Object pool = null;
/*     */     try
/*     */     {
/* 758 */       pool = nsautoreleasepoolCls.newInstance();
/* 759 */       Object delegateAlloc = invoke(pool, "alloc");
/* 760 */       invoke(delegateAlloc, "init");
/*     */       
/*     */ 
/* 763 */       Object workspace = invoke(nsworkspaceCls, "sharedWorkspace", new Object[0]);
/*     */       
/* 765 */       Object fullPath = invoke(nsstringCls, "stringWith", new Object[] { path });
/*     */       
/*     */ 
/* 768 */       if (fullPath != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 774 */         Object nsImage = invoke(workspace, "iconForFile", new Class[] { nsstringCls }, new Object[] { fullPath });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 779 */         if (nsImage != null)
/*     */         {
/* 781 */           Object size = nssizeCls.newInstance();
/*     */           
/* 783 */           nssizeCls.getField("width").set(size, Integer.valueOf(imageWidthHeight));
/* 784 */           nssizeCls.getField("height").set(size, Integer.valueOf(imageWidthHeight));
/*     */           
/* 786 */           invoke(nsImage, "setSize", new Class[] { nssizeCls }, new Object[] { size });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 792 */           invoke(nsImage, "retain");
/*     */           
/* 794 */           Image image = (Image)invoke(Image.class, null, "cocoa_new", new Class[] { Device.class, Integer.TYPE, nsimageCls }, new Object[] { Display.getCurrent(), Integer.valueOf(0), nsImage });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 804 */           return image;
/*     */         }
/*     */       }
/*     */     } catch (Throwable t) {
/* 808 */       Debug.printStackTrace(t);
/*     */     } finally {
/* 810 */       if (pool != null) {
/* 811 */         invoke(pool, "release");
/*     */       }
/*     */     }
/* 814 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isInitialized()
/*     */   {
/* 819 */     return initialized;
/*     */   }
/*     */   
/*     */   public boolean isRetinaDisplay()
/*     */   {
/*     */     try {
/* 825 */       Object screens = invoke(nsscreenCls, "screens");
/*     */       
/* 827 */       Object oCount = invoke(screens, "count");
/* 828 */       if (!(oCount instanceof Number)) {
/* 829 */         System.err.println("Can't determine Retina: count is " + oCount);
/*     */       }
/* 831 */       int count = ((Number)oCount).intValue();
/* 832 */       for (int i = 0; i < count; i++)
/*     */       {
/* 834 */         Object screenID = invoke(screens, "objectAtIndex", new Class[] { C.PTR_SIZEOF == 8 ? Long.TYPE : Integer.TYPE }, new Object[] { Integer.valueOf(i) });
/*     */         
/*     */ 
/*     */ 
/* 838 */         Object screen = nsscreenCls.getConstructor(new Class[] { screenID.getClass() }).newInstance(new Object[] { screenID });
/*     */         
/* 840 */         Object oBackingScaleFactor = invoke(screen, "backingScaleFactor");
/*     */         
/* 842 */         if (((oBackingScaleFactor instanceof Number)) && (((Number)oBackingScaleFactor).intValue() == 2))
/*     */         {
/* 844 */           return true;
/*     */         }
/*     */       }
/*     */     } catch (Throwable t) {
/* 848 */       Debug.out("Can't determine Retina", t);
/*     */     }
/* 850 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/osx/CocoaUIEnhancer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */