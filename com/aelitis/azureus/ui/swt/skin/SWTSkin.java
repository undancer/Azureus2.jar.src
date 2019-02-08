/*      */ package com.aelitis.azureus.ui.swt.skin;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.ui.IUIIntializer;
/*      */ import com.aelitis.azureus.ui.skin.SkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Cursor;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Sash;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory.AEShell;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SWTSkin
/*      */ {
/*      */   public static final boolean DEBUG_VISIBILITIES = false;
/*   54 */   private static final SWTSkinObjectListener[] NOLISTENERS = new SWTSkinObjectListener[0];
/*      */   
/*      */   private static SWTSkin default_instance;
/*      */   
/*      */ 
/*      */   protected static synchronized SWTSkin getDefaultInstance()
/*      */   {
/*   61 */     if (default_instance == null)
/*      */     {
/*   63 */       default_instance = new SWTSkin();
/*      */     }
/*      */     
/*   66 */     return default_instance;
/*      */   }
/*      */   
/*   69 */   public boolean DEBUGLAYOUT = System.getProperty("debuglayout") != null;
/*      */   
/*   71 */   private Map<SkinProperties, ImageLoader> mapImageLoaders = new ConcurrentHashMap();
/*      */   
/*      */ 
/*      */   private SWTSkinProperties skinProperties;
/*      */   
/*      */ 
/*      */   private Listener handCursorListener;
/*      */   
/*      */ 
/*   80 */   private HashMap<String, SWTSkinObject[]> mapIDsToSOs = new HashMap();
/*      */   
/*   82 */   private AEMonitor mon_MapIDsToSOs = new AEMonitor("mapIDsToControls");
/*      */   
/*      */ 
/*   85 */   private HashMap<String, SWTSkinTabSet> mapTabSetToControls = new HashMap();
/*      */   
/*      */ 
/*   88 */   private HashMap<String, SWTSkinObject[]> mapPublicViewIDsToSOs = new HashMap();
/*      */   
/*   90 */   private AEMonitor mon_mapPublicViewIDsToSOs = new AEMonitor("mapPVIDsToSOs");
/*      */   
/*   92 */   private HashMap<String, ArrayList<SWTSkinObjectListener>> mapPublicViewIDsToListeners = new HashMap();
/*      */   
/*   94 */   private AEMonitor mapPublicViewIDsToListeners_mon = new AEMonitor("mapPVIDsToListeners");
/*      */   
/*      */ 
/*   97 */   private ArrayList<SWTSkinObjectBasic> ontopImages = new ArrayList();
/*      */   
/*      */   private Composite skinComposite;
/*      */   
/*  101 */   private boolean bLayoutComplete = false;
/*      */   
/*  103 */   private CopyOnWriteList<SWTSkinLayoutCompleteListener> listenersLayoutComplete = new CopyOnWriteList();
/*      */   
/*  105 */   private int currentSkinObjectcreationCount = 0;
/*      */   
/*      */   private ImageLoader imageLoader;
/*      */   
/*      */   private String startID;
/*      */   
/*  111 */   private boolean autoSizeOnLayout = true;
/*      */   
/*      */ 
/*      */ 
/*      */   protected SWTSkin()
/*      */   {
/*  117 */     init(new SWTSkinPropertiesImpl(), true);
/*      */   }
/*      */   
/*      */   protected SWTSkin(ClassLoader classLoader, String skinPath, String mainSkinFile) {
/*  121 */     init(new SWTSkinPropertiesImpl(classLoader, skinPath, mainSkinFile), false);
/*      */   }
/*      */   
/*      */   private void init(SWTSkinProperties skinProperties, boolean is_default)
/*      */   {
/*  126 */     this.skinProperties = skinProperties;
/*      */     
/*      */ 
/*      */ 
/*  130 */     if (is_default) {
/*  131 */       this.imageLoader = ImageLoader.getInstance();
/*  132 */       this.imageLoader.addSkinProperties(skinProperties);
/*      */     } else {
/*  134 */       this.imageLoader = new ImageLoader(Display.getDefault(), skinProperties);
/*      */       
/*  136 */       this.mapImageLoaders.put(skinProperties, this.imageLoader);
/*      */     }
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
/*      */   public ImageLoader getImageLoader(SkinProperties properties)
/*      */   {
/*  177 */     if (properties == this.skinProperties) {
/*  178 */       return this.imageLoader;
/*      */     }
/*  180 */     ImageLoader loader = (ImageLoader)this.mapImageLoaders.get(properties);
/*      */     
/*  182 */     if (loader != null) {
/*  183 */       return loader;
/*      */     }
/*      */     
/*  186 */     loader = new ImageLoader(Display.getDefault(), properties);
/*  187 */     this.mapImageLoaders.put(properties, loader);
/*      */     
/*  189 */     return loader;
/*      */   }
/*      */   
/*      */   public void addToControlMap(SWTSkinObject skinObject) {
/*  193 */     String sID = skinObject.getSkinObjectID();
/*  194 */     if (this.DEBUGLAYOUT) {
/*  195 */       System.out.println("addToControlMap: " + sID + " : " + skinObject);
/*      */     }
/*  197 */     addToSOArrayMap(this.mapIDsToSOs, this.mon_MapIDsToSOs, sID, skinObject);
/*      */     
/*      */ 
/*  200 */     Control control = skinObject.getControl();
/*  201 */     if (control != null) {
/*  202 */       control.setData("ConfigID", skinObject.getConfigID());
/*  203 */       control.setData("SkinID", sID);
/*      */     }
/*      */   }
/*      */   
/*      */   private void addToSOArrayMap(Map<String, SWTSkinObject[]> arrayMap, AEMonitor mon, String key, SWTSkinObject object)
/*      */   {
/*  209 */     if (mon != null) {
/*  210 */       mon.enter();
/*      */     }
/*      */     try {
/*  213 */       SWTSkinObject[] existingObjects = (SWTSkinObject[])arrayMap.get(key);
/*  214 */       if (existingObjects != null)
/*      */       {
/*  216 */         boolean bAlreadyPresent = false;
/*  217 */         for (int i = 0; i < existingObjects.length; i++)
/*      */         {
/*  219 */           if ((existingObjects[i] != null) && (existingObjects[i].equals(object))) {
/*  220 */             bAlreadyPresent = true;
/*  221 */             System.err.println("already present: " + key + "; " + object + "; existing: " + existingObjects[i] + " via " + Debug.getCompressedStackTrace());
/*      */             
/*      */ 
/*  224 */             break;
/*      */           }
/*      */         }
/*      */         
/*  228 */         if (!bAlreadyPresent) {
/*  229 */           int length = existingObjects.length;
/*  230 */           SWTSkinObject[] newObjects = new SWTSkinObject[length + 1];
/*  231 */           System.arraycopy(existingObjects, 0, newObjects, 0, length);
/*  232 */           newObjects[length] = object;
/*      */           
/*  234 */           arrayMap.put(key, newObjects);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  239 */         arrayMap.put(key, new SWTSkinObject[] { object });
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  244 */       if (mon != null) {
/*  245 */         mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private Object getFromSOArrayMap(Map<String, SWTSkinObject[]> arrayMap, Object key, SWTSkinObject parent)
/*      */   {
/*  252 */     if (parent == null) {
/*  253 */       return null;
/*      */     }
/*      */     
/*  256 */     SWTSkinObject[] objects = (SWTSkinObject[])arrayMap.get(key);
/*  257 */     if (objects == null) {
/*  258 */       return null;
/*      */     }
/*      */     
/*  261 */     for (int i = 0; i < objects.length; i++) {
/*  262 */       SWTSkinObject object = objects[i];
/*  263 */       SWTSkinObject thisParent = object;
/*  264 */       while (thisParent != null) {
/*  265 */         if (thisParent.equals(parent)) {
/*  266 */           return object;
/*      */         }
/*  268 */         thisParent = thisParent.getParent();
/*      */       }
/*      */     }
/*      */     
/*  272 */     return null;
/*      */   }
/*      */   
/*      */   private void setSkinObjectViewID(SWTSkinObject skinObject, String sViewID) {
/*  276 */     addToSOArrayMap(this.mapPublicViewIDsToSOs, this.mon_mapPublicViewIDsToSOs, sViewID, skinObject);
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
/*      */   public SWTSkinObject getSkinObjectByID(String sID)
/*      */   {
/*  311 */     SWTSkinObject[] objects = (SWTSkinObject[])this.mapIDsToSOs.get(sID);
/*  312 */     if ((objects == null) || (objects.length == 0)) {
/*  313 */       return null;
/*      */     }
/*      */     
/*  316 */     return objects[0];
/*      */   }
/*      */   
/*      */   public SWTSkinObject getSkinObjectByID(String sID, SWTSkinObject parent) {
/*  320 */     if (parent == null)
/*      */     {
/*  322 */       return getSkinObjectByID(sID);
/*      */     }
/*      */     
/*  325 */     return (SWTSkinObject)getFromSOArrayMap(this.mapIDsToSOs, sID, parent);
/*      */   }
/*      */   
/*      */   public SWTSkinObject getSkinObject(String sViewID) {
/*  329 */     SWTSkinObject[] objects = (SWTSkinObject[])this.mapPublicViewIDsToSOs.get(sViewID);
/*  330 */     if ((objects == null) || (objects.length == 0)) {
/*  331 */       return createUnattachedView(sViewID, null);
/*      */     }
/*      */     
/*  334 */     return objects[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private SWTSkinObject createUnattachedView(String viewID, SWTSkinObject parent)
/*      */   {
/*  344 */     String unattachedView = this.skinProperties.getStringValue("UnattachedView." + viewID);
/*      */     
/*  346 */     if (unattachedView != null) {
/*  347 */       if (!Utils.isThisThreadSWT()) {
/*  348 */         Debug.out("View " + viewID + " does not exist.  Skipping unattach check because not in SWT thread");
/*      */         
/*      */ 
/*  351 */         return null;
/*      */       }
/*  353 */       if (unattachedView.indexOf(',') > 0) {
/*  354 */         String[] split = Constants.PAT_SPLIT_COMMA.split(unattachedView);
/*  355 */         String parentID = split[1];
/*  356 */         SWTSkinObject soParent = getSkinObjectByID(parentID, parent);
/*  357 */         if (soParent != null) {
/*  358 */           String configID = split[0];
/*  359 */           return createSkinObject(configID, configID, soParent);
/*      */         }
/*      */       }
/*      */       
/*  363 */       SWTSkinObjectListener[] listeners = getSkinObjectListeners(viewID);
/*  364 */       for (int i = 0; i < listeners.length; i++) {
/*  365 */         SWTSkinObjectListener l = listeners[i];
/*  366 */         Object o = l.eventOccured(null, 5, new String[] { viewID, unattachedView });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  371 */         if ((o instanceof SWTSkinObject)) {
/*  372 */           return (SWTSkinObject)o;
/*      */         }
/*      */       }
/*      */     }
/*  376 */     return null;
/*      */   }
/*      */   
/*      */   public SWTSkinObject getSkinObject(String sViewID, SWTSkinObject parent) {
/*  380 */     if (parent == null)
/*      */     {
/*  382 */       return getSkinObject(sViewID);
/*      */     }
/*      */     
/*  385 */     String parentViewID = parent.getViewID();
/*  386 */     if ((parentViewID != null) && (parentViewID.equals(sViewID))) {
/*  387 */       return parent;
/*      */     }
/*      */     
/*  390 */     SWTSkinObject so = (SWTSkinObject)getFromSOArrayMap(this.mapPublicViewIDsToSOs, sViewID, parent);
/*      */     
/*  392 */     if (so == null) {
/*  393 */       so = createUnattachedView(sViewID, parent);
/*      */     }
/*      */     
/*  396 */     return so;
/*      */   }
/*      */   
/*      */   public SWTSkinTabSet getTabSet(String sID) {
/*  400 */     return (SWTSkinTabSet)this.mapTabSetToControls.get(sID);
/*      */   }
/*      */   
/*      */   public SWTSkinObjectTab activateTab(SWTSkinObject skinObjectInTab) {
/*  404 */     if (skinObjectInTab == null) {
/*  405 */       return null;
/*      */     }
/*      */     
/*  408 */     if ((skinObjectInTab instanceof SWTSkinObjectTab)) {
/*  409 */       SWTSkinObjectTab tab = (SWTSkinObjectTab)skinObjectInTab;
/*  410 */       tab.getTabset().setActiveTab(tab);
/*  411 */       return tab;
/*      */     }
/*      */     
/*  414 */     for (Iterator<SWTSkinTabSet> iter = this.mapTabSetToControls.values().iterator(); iter.hasNext();) {
/*  415 */       SWTSkinTabSet tabset = (SWTSkinTabSet)iter.next();
/*      */       
/*  417 */       SWTSkinObjectTab[] tabs = tabset.getTabs();
/*  418 */       boolean bHasSkinObject = false;
/*  419 */       for (int i = 0; i < tabs.length; i++) {
/*  420 */         SWTSkinObjectTab tab = tabs[i];
/*  421 */         SWTSkinObject[] activeWidgets = tab.getActiveWidgets(true);
/*  422 */         for (int j = 0; j < activeWidgets.length; j++) {
/*  423 */           SWTSkinObject object = activeWidgets[j];
/*      */           
/*  425 */           if (hasSkinObject(object, skinObjectInTab))
/*      */           {
/*  427 */             tabset.setActiveTab(tab);
/*  428 */             return tab;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  433 */     System.out.println("NOT FOUND" + skinObjectInTab);
/*  434 */     return null;
/*      */   }
/*      */   
/*      */   private boolean hasSkinObject(SWTSkinObject start, SWTSkinObject skinObject) {
/*  438 */     if ((start instanceof SWTSkinObjectContainer)) {
/*  439 */       SWTSkinObject[] children = ((SWTSkinObjectContainer)start).getChildren();
/*  440 */       for (int i = 0; i < children.length; i++) {
/*  441 */         SWTSkinObject object = children[i];
/*      */         
/*  443 */         if (hasSkinObject(object, skinObject)) {
/*  444 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  449 */     return skinObject.equals(start);
/*      */   }
/*      */   
/*      */   public SWTSkinTabSet getTabSet(SWTSkinObject skinObject) {
/*  453 */     String sTabSetID = skinObject.getProperties().getStringValue(skinObject.getConfigID() + ".tabset");
/*      */     
/*  455 */     return getTabSet(sTabSetID);
/*      */   }
/*      */   
/*      */   public boolean setActiveTab(String sTabSetID, String sTabViewID) {
/*  459 */     SWTSkinTabSet tabSet = getTabSet(sTabSetID);
/*  460 */     if (tabSet == null) {
/*  461 */       System.err.println(sTabSetID);
/*  462 */       return false;
/*      */     }
/*      */     
/*  465 */     return tabSet.setActiveTab(sTabViewID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialize(Composite skincomp, String startID)
/*      */   {
/*  476 */     initialize(skincomp, startID, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public void initialize(Composite skincomp, String startID, IUIIntializer uiInitializer)
/*      */   {
/*  482 */     this.skinComposite = skincomp;
/*  483 */     this.startID = startID;
/*  484 */     FormLayout layout = new FormLayout();
/*  485 */     this.skinComposite.setLayout(layout);
/*  486 */     this.skinComposite.setBackgroundMode(1);
/*      */     
/*  488 */     this.skinComposite.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  490 */         SWTSkin.this.disposeSkin();
/*      */       }
/*      */       
/*  493 */     });
/*  494 */     Listener l = new Listener() {
/*  495 */       Control lastControl = null;
/*      */       
/*      */       public void handleEvent(Event event) {
/*  498 */         if ((SWTSkin.this.skinComposite.isDisposed()) && (event.display != null)) {
/*  499 */           event.display.removeFilter(5, this);
/*  500 */           event.display.removeFilter(7, this);
/*  501 */           return;
/*      */         }
/*  503 */         Control cursorControl = SWTSkin.this.skinComposite.getDisplay().getCursorControl();
/*      */         
/*  505 */         if (cursorControl != this.lastControl) {
/*  506 */           Point cursorLocation = SWTSkin.this.skinComposite.getDisplay().getCursorLocation();
/*  507 */           while ((this.lastControl != null) && (!this.lastControl.isDisposed())) {
/*  508 */             Point cursorLocationInControl = this.lastControl.toControl(cursorLocation);
/*  509 */             Point size = this.lastControl.getSize();
/*  510 */             if (!new Rectangle(0, 0, size.x, size.y).contains(cursorLocationInControl)) {
/*  511 */               SWTSkinObjectBasic so = (SWTSkinObjectBasic)this.lastControl.getData("SkinObject");
/*  512 */               if (so != null) {
/*  513 */                 so.switchSuffix("", 3, false, false);
/*      */               }
/*      */             }
/*  516 */             this.lastControl = this.lastControl.getParent();
/*      */           }
/*  518 */           this.lastControl = cursorControl;
/*      */           
/*  520 */           while (cursorControl != null) {
/*  521 */             SWTSkinObjectBasic so = (SWTSkinObjectBasic)cursorControl.getData("SkinObject");
/*  522 */             if (so != null) {
/*  523 */               so.switchSuffix("-over", 3, false, false);
/*      */             }
/*      */             
/*  526 */             cursorControl = cursorControl.getParent();
/*      */           }
/*      */         }
/*      */       }
/*  530 */     };
/*  531 */     Display display = this.skinComposite.getDisplay();
/*  532 */     display.addFilter(5, l);
/*      */     
/*      */ 
/*  535 */     display.addFilter(7, l);
/*  536 */     this.skinComposite.addListener(27, l);
/*  537 */     this.skinComposite.addListener(26, l);
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
/*  577 */     Color bg = this.skinProperties.getColor(startID + ".color");
/*  578 */     if (bg != null) {
/*  579 */       this.skinComposite.setBackground(bg);
/*      */     }
/*      */     
/*  582 */     Color fg = this.skinProperties.getColor(startID + ".fgcolor");
/*  583 */     if (fg != null) {
/*  584 */       this.skinComposite.setForeground(fg);
/*      */     }
/*      */     
/*      */ 
/*  588 */     int width = this.skinProperties.getPxValue(startID + ".width", -1);
/*  589 */     int height = this.skinProperties.getPxValue(startID + ".height", -1);
/*  590 */     if ((width > 0) && (height > 0)) {
/*  591 */       this.skinComposite.setSize(width, height);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  596 */     if ((this.skinComposite instanceof Shell)) {
/*  597 */       Shell shell = (Shell)this.skinComposite;
/*  598 */       int minWidth = this.skinProperties.getPxValue(startID + ".minwidth", -1);
/*  599 */       int minHeight = this.skinProperties.getPxValue(startID + ".minheight", -1);
/*  600 */       if ((minWidth > 0) || (minHeight > 0)) {
/*  601 */         Point minimumSize = shell.getMinimumSize();
/*  602 */         shell.setMinimumSize(minWidth > 0 ? minWidth : minimumSize.x, minHeight > 0 ? minHeight : minimumSize.y);
/*      */       }
/*      */       
/*  605 */       String title = this.skinProperties.getStringValue(startID + ".title", (String)null);
/*      */       
/*  607 */       if (title != null) {
/*  608 */         ((Shell)this.skinComposite).setText(title);
/*      */       }
/*      */     }
/*      */     
/*  612 */     String[] sMainGroups = this.skinProperties.getStringArray(startID + ".widgets");
/*  613 */     if (sMainGroups == null) {
/*  614 */       System.out.println("NO " + startID + ".widgets!!");
/*  615 */       sMainGroups = new String[0];
/*      */     }
/*      */     
/*  618 */     for (int i = 0; i < sMainGroups.length; i++) {
/*  619 */       String sID = sMainGroups[i];
/*      */       
/*  621 */       if (this.DEBUGLAYOUT) {
/*  622 */         System.out.println("Container: " + sID);
/*      */       }
/*      */       
/*  625 */       if (uiInitializer != null) {
/*  626 */         uiInitializer.increaseProgress();
/*      */       }
/*      */       
/*  629 */       linkIDtoParent(this.skinProperties, sID, sID, null, false, true, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void disposeSkin()
/*      */   {
/*  639 */     for (Iterator<ImageLoader> iter = this.mapImageLoaders.values().iterator(); iter.hasNext();) {
/*  640 */       ImageLoader loader = (ImageLoader)iter.next();
/*  641 */       loader.unLoadImages();
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void layout(SWTSkinObject soStart)
/*      */   {
/*  666 */     if ((soStart instanceof SWTSkinObjectContainer)) {
/*  667 */       SWTSkinObjectContainer soContainer = (SWTSkinObjectContainer)soStart;
/*  668 */       SWTSkinObject[] children = soContainer.getChildren();
/*  669 */       for (SWTSkinObject so : children) {
/*  670 */         layout(so);
/*      */       }
/*      */     }
/*      */     
/*  674 */     if (this.DEBUGLAYOUT) {
/*  675 */       System.out.println("attachControl " + soStart.toString());
/*      */     }
/*  677 */     attachControl(soStart);
/*      */   }
/*      */   
/*      */   public void layout() {
/*  681 */     if (this.DEBUGLAYOUT) {
/*  682 */       System.out.println("==== Start Apply Layout");
/*      */     }
/*      */     
/*  685 */     Object[] values = this.mapIDsToSOs.values().toArray();
/*  686 */     for (int i = 0; i < values.length; i++) {
/*  687 */       SWTSkinObject[] skinObjects = (SWTSkinObject[])values[i];
/*  688 */       if (skinObjects != null) {
/*  689 */         for (int j = 0; j < skinObjects.length; j++) {
/*  690 */           SWTSkinObject skinObject = skinObjects[j];
/*  691 */           if (this.DEBUGLAYOUT) {
/*  692 */             System.out.println("Apply Layout for " + skinObject);
/*      */           }
/*  694 */           attachControl(skinObject);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  702 */     if (this.DEBUGLAYOUT) {
/*  703 */       System.out.println("====  Applied Layout");
/*      */     }
/*  705 */     this.bLayoutComplete = true;
/*      */     
/*  707 */     int width = this.skinProperties.getPxValue(this.startID + ".width", -1);
/*  708 */     int height = this.skinProperties.getPxValue(this.startID + ".height", -1);
/*  709 */     if ((this.skinComposite instanceof ShellFactory.AEShell)) {
/*  710 */       ((ShellFactory.AEShell)this.skinComposite).setAdjustPXforDPI(false);
/*      */     }
/*      */     
/*  713 */     if (this.autoSizeOnLayout) {
/*  714 */       if ((width > 0) && (height == -1)) {
/*  715 */         Point computeSize = this.skinComposite.computeSize(width, -1);
/*  716 */         this.skinComposite.setSize(computeSize);
/*  717 */       } else if ((height > 0) && (width == -1)) {
/*  718 */         Point computeSize = this.skinComposite.computeSize(-1, height);
/*  719 */         this.skinComposite.setSize(computeSize);
/*  720 */       } else if ((height > 0) && (width > 0)) {
/*  721 */         this.skinComposite.setSize(width, height);
/*      */       }
/*      */     }
/*      */     else {
/*  725 */       Point size = this.skinComposite.getSize();
/*  726 */       if (width > 0) {
/*  727 */         size.x = width;
/*      */       }
/*  729 */       if (height > 0) {
/*  730 */         size.y = height;
/*      */       }
/*  732 */       this.skinComposite.setSize(size);
/*      */     }
/*      */     
/*  735 */     for (SWTSkinLayoutCompleteListener l : this.listenersLayoutComplete) {
/*  736 */       l.skinLayoutCompleted();
/*      */     }
/*  738 */     this.listenersLayoutComplete.clear();
/*      */     
/*  740 */     if (this.DEBUGLAYOUT) {
/*  741 */       System.out.println("==== End Apply Layout");
/*      */     }
/*      */     
/*  744 */     this.skinProperties.clearCache();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void attachControl(SWTSkinObject skinObject)
/*      */   {
/*  752 */     if (skinObject == null) {
/*  753 */       return;
/*      */     }
/*      */     
/*  756 */     Control controlToLayout = skinObject.getControl();
/*      */     
/*  758 */     if ((controlToLayout == null) || (controlToLayout.isDisposed())) {
/*  759 */       return;
/*      */     }
/*      */     
/*  762 */     if (controlToLayout.getData("skin.layedout") != null) {
/*  763 */       return;
/*      */     }
/*      */     
/*  766 */     String sConfigID = skinObject.getConfigID();
/*  767 */     SWTSkinProperties properties = skinObject.getProperties();
/*      */     
/*  769 */     String[] sDirections = { "top", "bottom", "left", "right" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  779 */     Object data = controlToLayout.getLayoutData();
/*  780 */     if ((data != null) && (!(data instanceof FormData))) {
/*  781 */       return;
/*      */     }
/*  783 */     FormData oldFormData = (FormData)controlToLayout.getLayoutData();
/*  784 */     if (oldFormData == null) {
/*  785 */       oldFormData = new FormData();
/*      */     }
/*      */     
/*  788 */     FormData newFormData = new FormData(oldFormData.width, oldFormData.height);
/*      */     
/*  790 */     String templateID = properties.getStringValue(sConfigID + ".attach.template");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  796 */     boolean debugControl = (templateID != null) || (controlToLayout.getData("DEBUG") != null);
/*      */     
/*  798 */     for (int i = 0; i < sDirections.length; i++) {
/*  799 */       Control control = null;
/*  800 */       int offset = 0;
/*  801 */       int percent = 0;
/*  802 */       String sAlign = null;
/*  803 */       int align = -1;
/*      */       
/*      */       FormAttachment attachment;
/*      */       
/*  807 */       switch (i) {
/*      */       case 0: 
/*  809 */         attachment = oldFormData.top;
/*  810 */         break;
/*      */       
/*      */       case 1: 
/*  813 */         attachment = oldFormData.bottom;
/*  814 */         break;
/*      */       
/*      */       case 2: 
/*  817 */         attachment = oldFormData.left;
/*  818 */         break;
/*      */       
/*      */       case 3: 
/*  821 */         attachment = oldFormData.right;
/*  822 */         break;
/*      */       
/*      */       default: 
/*  825 */         attachment = null;
/*      */       }
/*      */       
/*  828 */       if (attachment != null) {
/*  829 */         control = attachment.control;
/*  830 */         offset = attachment.offset;
/*  831 */         align = attachment.alignment;
/*      */         
/*  833 */         percent = attachment.numerator;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  838 */       String suffix = ".attach." + sDirections[i];
/*  839 */       String prefix = sConfigID;
/*      */       
/*      */ 
/*  842 */       String[] sParams = properties.getStringArray(sConfigID + suffix);
/*  843 */       if ((sParams == null) && (templateID != null)) {
/*  844 */         sParams = properties.getStringArray(templateID + suffix);
/*  845 */         prefix = templateID;
/*      */       }
/*      */       
/*  848 */       if (sParams == null) {
/*  849 */         if (attachment != null) {
/*  850 */           if (control == null) {
/*  851 */             attachment = new FormAttachment(percent, offset);
/*      */           } else {
/*  853 */             attachment = new FormAttachment(control, offset, align);
/*      */           }
/*      */         }
/*      */       }
/*  857 */       else if ((sParams.length == 0) || ((sParams.length == 1) && (sParams[0].length() == 0)))
/*      */       {
/*  859 */         attachment = null;
/*      */ 
/*      */       }
/*  862 */       else if ((sParams[0].length() > 0) && (Character.isDigit(sParams[0].charAt(0))))
/*      */       {
/*      */         try {
/*  865 */           percent = Integer.parseInt(sParams[0]);
/*      */         }
/*      */         catch (Exception e) {}
/*      */         
/*  869 */         if (sParams.length > 1) {
/*      */           try {
/*  871 */             String value = sParams[1];
/*  872 */             if (value.endsWith("rem")) {
/*  873 */               float em = Float.parseFloat(value.substring(0, value.length() - 3));
/*      */               
/*  875 */               offset = (int)(properties.getEmHeightPX() * em);
/*      */             } else {
/*  877 */               offset = Integer.parseInt(value);
/*  878 */               offset = Utils.adjustPXForDPI(offset);
/*      */             }
/*      */           }
/*      */           catch (Exception e) {}
/*      */         }
/*      */         
/*  884 */         attachment = new FormAttachment(percent, offset);
/*      */       }
/*      */       else
/*      */       {
/*  888 */         String sWidget = sParams[0];
/*      */         
/*  890 */         SWTSkinObject configSkinObject = getSkinObjectByID(sWidget, skinObject.getParent());
/*      */         int iNextPos;
/*      */         int iNextPos;
/*  893 */         if (configSkinObject != null) {
/*  894 */           control = configSkinObject.getControl();
/*      */           
/*  896 */           iNextPos = 1;
/*      */         } else {
/*  898 */           iNextPos = 0;
/*      */           
/*  900 */           if (sWidget.length() != 0) {
/*  901 */             System.err.println("ERROR: Trying to attach " + sDirections[i] + " of widget '" + skinObject + "' to non-existant widget '" + sWidget + "'.  Attachment Parameters: " + properties.getStringValue(new StringBuilder().append(prefix).append(suffix).toString()));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  908 */         for (int j = iNextPos; j < sParams.length; j++) {
/*  909 */           if (sParams[j].length() > 0) {
/*  910 */             char c = sParams[j].charAt(0);
/*  911 */             if ((Character.isDigit(c)) || (c == '-')) {
/*      */               try {
/*  913 */                 String value = sParams[j];
/*  914 */                 if (value.endsWith("rem")) {
/*  915 */                   float em = Float.parseFloat(value.substring(0, value.length() - 3));
/*      */                   
/*  917 */                   offset = (int)(properties.getEmHeightPX() * em);
/*      */                 } else {
/*  919 */                   offset = Integer.parseInt(value);
/*  920 */                   offset = Utils.adjustPXForDPI(offset);
/*      */                 }
/*      */               }
/*      */               catch (Exception e) {}
/*      */             } else {
/*  925 */               sAlign = sParams[j];
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  930 */         if (sAlign != null) {
/*  931 */           align = SWTSkinUtils.getAlignment(sAlign, align);
/*      */         }
/*      */         
/*  934 */         attachment = new FormAttachment(control, offset, align);
/*      */       }
/*      */       
/*      */ 
/*  938 */       if ((debugControl) && (attachment != null) && 
/*  939 */         ((controlToLayout instanceof Group))) {
/*  940 */         Group group = (Group)controlToLayout;
/*  941 */         String sValue = properties.getStringValue(prefix + suffix);
/*  942 */         String sText = group.getText() + "; " + sDirections[i].substring(0, 1) + "=" + (sValue == null ? "(def)" : sValue);
/*      */         
/*      */ 
/*  945 */         if (sText.length() < 20) {
/*  946 */           group.setText(sText);
/*      */         }
/*  948 */         group.setToolTipText(sText);
/*      */       }
/*      */       
/*      */ 
/*  952 */       if (this.DEBUGLAYOUT) {
/*  953 */         System.out.println("Attach: " + sConfigID + suffix + ": " + properties.getStringValue(new StringBuilder().append(prefix).append(suffix).toString()) + "/" + attachment);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  958 */       switch (i) {
/*      */       case 0: 
/*  960 */         newFormData.top = attachment;
/*  961 */         break;
/*      */       
/*      */       case 1: 
/*  964 */         newFormData.bottom = attachment;
/*  965 */         break;
/*      */       
/*      */       case 2: 
/*  968 */         newFormData.left = attachment;
/*  969 */         break;
/*      */       
/*      */       case 3: 
/*  972 */         newFormData.right = attachment;
/*      */       }
/*      */       
/*      */     }
/*      */     
/*      */ 
/*  978 */     if (!skinObject.getDefaultVisibility()) {
/*  979 */       if (controlToLayout.getData("oldSize") == null) {
/*  980 */         controlToLayout.setData("oldSize", new Point(properties.getPxValue(sConfigID + ".width", -1), properties.getPxValue(sConfigID + ".height", -1)));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  988 */       newFormData.width = 0;
/*  989 */       newFormData.height = 0;
/*      */     } else {
/*  991 */       int h = properties.getPxValue(sConfigID + ".height", -2);
/*  992 */       int w = properties.getPxValue(sConfigID + ".width", -2);
/*  993 */       if (h != -2) {
/*  994 */         newFormData.height = h;
/*      */       }
/*  996 */       if (w != -2) {
/*  997 */         newFormData.width = w;
/*      */       }
/*      */     }
/* 1000 */     controlToLayout.setLayoutData(newFormData);
/* 1001 */     controlToLayout.setData("skin.layedout", "");
/* 1002 */     skinObject.layoutComplete();
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createContainer(SWTSkinProperties properties, String sID, String sConfigID, String[] sTypeParams, SWTSkinObject parentSkinObject, boolean bForceCreate, boolean bPropogate, SWTSkinObject intoSkinObject)
/*      */   {
/* 1008 */     String[] sItems = properties.getStringArray(sConfigID + ".widgets");
/* 1009 */     if ((sItems == null) && (!bForceCreate)) {
/* 1010 */       return null;
/*      */     }
/*      */     
/* 1013 */     if (this.DEBUGLAYOUT) {
/* 1014 */       System.out.println("createContainer: " + sID + ";" + properties.getStringValue(new StringBuilder().append(sConfigID).append(".widgets").toString()) + " on " + parentSkinObject);
/*      */     }
/*      */     
/*      */ 
/* 1018 */     SWTSkinObject skinObject = getSkinObjectByID(sID, parentSkinObject);
/*      */     
/* 1020 */     if (skinObject == null) {
/* 1021 */       if (intoSkinObject == null) {
/* 1022 */         skinObject = new SWTSkinObjectContainer(this, properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         
/* 1024 */         addToControlMap(skinObject);
/*      */       } else {
/* 1026 */         skinObject = intoSkinObject;
/*      */       }
/*      */     }
/* 1029 */     else if (!(skinObject instanceof SWTSkinObjectContainer)) {
/* 1030 */       return skinObject;
/*      */     }
/*      */     
/*      */ 
/* 1034 */     if (!bPropogate) {
/* 1035 */       bPropogate = properties.getIntValue(sConfigID + ".propogate", 0) == 1;
/*      */     }
/*      */     
/* 1038 */     if ((!bPropogate) && ((parentSkinObject instanceof SWTSkinObjectContainer))) {
/* 1039 */       bPropogate = ((SWTSkinObjectContainer)parentSkinObject).getPropogation();
/*      */     }
/* 1041 */     if (bPropogate) {
/* 1042 */       ((SWTSkinObjectContainer)skinObject).setPropogation(true);
/*      */     }
/*      */     
/* 1045 */     if (sItems != null) {
/* 1046 */       addContainerChildren(skinObject, sItems, properties);
/*      */     }
/*      */     
/* 1049 */     return skinObject;
/*      */   }
/*      */   
/*      */   private void addContainerChildren(SWTSkinObject skinObject, String[] sItems, SWTSkinProperties properties)
/*      */   {
/* 1054 */     String[] paramValues = null;
/* 1055 */     if ((properties instanceof SWTSkinPropertiesParam)) {
/* 1056 */       paramValues = ((SWTSkinPropertiesParam)properties).getParamValues();
/*      */     }
/*      */     
/*      */ 
/* 1060 */     if ((properties instanceof SWTSkinPropertiesClone)) {
/* 1061 */       properties = ((SWTSkinPropertiesClone)properties).getOriginalProperties();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1069 */     if (paramValues != null) {
/* 1070 */       properties = new SWTSkinPropertiesParamImpl(properties, paramValues);
/*      */     }
/*      */     
/* 1073 */     SWTSkinObject[] soChildren = new SWTSkinObject[sItems.length];
/* 1074 */     for (int i = 0; i < sItems.length; i++) {
/* 1075 */       String sItemID = sItems[i];
/* 1076 */       soChildren[i] = linkIDtoParent(properties, sItemID, sItemID, skinObject, false, true, null);
/*      */     }
/*      */     
/* 1079 */     if (this.bLayoutComplete)
/*      */     {
/* 1081 */       for (SWTSkinObject so : soChildren) {
/* 1082 */         if (so != null) {
/* 1083 */           attachControl(so);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private SWTSkinObject createSash(SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parentSkinObject, boolean bVertical)
/*      */   {
/* 1091 */     int style = bVertical ? 512 : 256;
/*      */     
/* 1093 */     String[] sItems = properties.getStringArray(sConfigID + ".widgets");
/*      */     
/*      */     Composite createOn;
/*      */     
/*      */     Composite createOn;
/* 1098 */     if (parentSkinObject == null) {
/* 1099 */       createOn = this.skinComposite;
/*      */     } else {
/* 1101 */       createOn = (Composite)parentSkinObject.getControl();
/*      */     }
/*      */     SWTSkinObject skinObject;
/* 1104 */     if (sItems == null)
/*      */     {
/* 1106 */       Sash sash = new Sash(createOn, style);
/* 1107 */       SWTSkinObject skinObject = new SWTSkinObjectBasic(this, properties, sash, sID, sConfigID, "sash", parentSkinObject);
/*      */       
/* 1109 */       addToControlMap(skinObject);
/*      */       
/* 1111 */       sash.setBackground(sash.getDisplay().getSystemColor(3));
/*      */       
/* 1113 */       sash.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/* 1115 */           Sash sash = (Sash)e.widget;
/* 1116 */           boolean FASTDRAG = true;
/*      */           
/* 1118 */           if (e.detail == 1) {
/* 1119 */             return;
/*      */           }
/*      */           
/* 1122 */           Rectangle parentArea = sash.getParent().getClientArea();
/*      */           
/* 1124 */           FormData formData = (FormData)sash.getLayoutData();
/* 1125 */           formData.left = new FormAttachment(e.x * 100 / parentArea.width);
/* 1126 */           sash.getParent().layout();
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 1131 */       SashForm sashForm = new SashForm(createOn, style);
/* 1132 */       skinObject = new SWTSkinObjectContainer(this, properties, sashForm, sID, sConfigID, "sash", parentSkinObject);
/*      */       
/* 1134 */       addToControlMap(skinObject);
/*      */       
/* 1136 */       int iSashWidth = properties.getIntValue(sConfigID + ".sash.width", -1);
/*      */       
/* 1138 */       if (iSashWidth > 0) {
/* 1139 */         sashForm.SASH_WIDTH = iSashWidth;
/*      */       }
/*      */       
/* 1142 */       for (int i = 0; i < sItems.length; i++) {
/* 1143 */         String sChildID = sItems[i];
/* 1144 */         linkIDtoParent(properties, sChildID, sChildID, skinObject, false, true, null);
/*      */       }
/*      */     }
/*      */     
/* 1148 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createMySash(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject, boolean bVertical)
/*      */   {
/* 1154 */     SWTSkinObject skinObject = new SWTSkinObjectSash(this, properties, sID, sConfigID, typeParams, parentSkinObject, bVertical);
/*      */     
/* 1156 */     addToControlMap(skinObject);
/*      */     
/* 1158 */     return skinObject;
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
/*      */   public SWTSkinObjectTab createTab(String sID, String sTemplateKey, SWTSkinObject tabHolder)
/*      */   {
/* 1174 */     String sTemplateID = SWTSkinTabSet.getTemplateID(this, tabHolder, sTemplateKey);
/*      */     
/*      */ 
/* 1177 */     if (sTemplateID == null) {
/* 1178 */       return null;
/*      */     }
/*      */     
/* 1181 */     SWTSkinObject skinObject = linkIDtoParent(this.skinProperties, sID, sTemplateID, tabHolder, true, true, null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1186 */     if ((this.bLayoutComplete) && (skinObject != null)) {
/* 1187 */       ((Composite)skinObject.getControl()).getParent().layout(true);
/*      */     }
/* 1189 */     if ((skinObject instanceof SWTSkinObjectTab)) {
/* 1190 */       return (SWTSkinObjectTab)skinObject;
/*      */     }
/*      */     
/* 1193 */     System.err.println(skinObject + " not a SWTSkinObjectTab! Template: " + sTemplateID);
/*      */     
/* 1195 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private SWTSkinObjectTab _createTab(SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parentSkinObject)
/*      */   {
/* 1205 */     SWTSkinObjectTab skinObjectTab = new SWTSkinObjectTab(this, properties, sID, sConfigID, parentSkinObject);
/*      */     
/* 1207 */     createContainer(properties, sID, sConfigID, null, parentSkinObject, true, true, skinObjectTab);
/*      */     
/*      */ 
/* 1210 */     addToControlMap(skinObjectTab);
/*      */     
/* 1212 */     String sTabSet = properties.getStringValue(sConfigID + ".tabset", "default");
/*      */     
/* 1214 */     SWTSkinTabSet tabset = (SWTSkinTabSet)this.mapTabSetToControls.get(sTabSet);
/* 1215 */     if (tabset == null) {
/* 1216 */       tabset = new SWTSkinTabSet(this, sTabSet);
/* 1217 */       this.mapTabSetToControls.put(sTabSet, tabset);
/* 1218 */       if (this.DEBUGLAYOUT) {
/* 1219 */         System.out.println("New TabSet: " + sTabSet);
/*      */       }
/*      */     }
/* 1222 */     tabset.addTab(skinObjectTab);
/* 1223 */     if (this.DEBUGLAYOUT) {
/* 1224 */       System.out.println("Tab " + sID + " added");
/*      */     }
/*      */     
/* 1227 */     return skinObjectTab;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createTextLabel(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1233 */     if (this.DEBUGLAYOUT) {
/* 1234 */       System.out.println("createTextLabel: " + sID + " on " + parentSkinObject);
/*      */     }
/*      */     
/* 1237 */     SWTSkinObject skinObject = new SWTSkinObjectText2(this, properties, sID, sConfigID, typeParams, parentSkinObject);
/*      */     
/* 1239 */     addToControlMap(skinObject);
/*      */     
/* 1241 */     return skinObject;
/*      */   }
/*      */   
/*      */   private SWTSkinObject createSlider(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1246 */     SWTSkinObject skinObject = new SWTSkinObjectSlider(this, properties, sID, sConfigID, typeParams, parentSkinObject);
/*      */     
/* 1248 */     addToControlMap(skinObject);
/*      */     
/* 1250 */     return skinObject;
/*      */   }
/*      */   
/*      */   public Composite getShell() {
/* 1254 */     return this.skinComposite;
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
/*      */   protected Listener getHandCursorListener(Display display)
/*      */   {
/* 1302 */     if (this.handCursorListener == null) {
/* 1303 */       final Cursor handCursor = new Cursor(display, 21);
/* 1304 */       this.handCursorListener = new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1306 */           if (event.type == 6) {
/* 1307 */             ((Control)event.widget).setCursor(handCursor);
/*      */           }
/* 1309 */           if (event.type == 7) {
/* 1310 */             ((Control)event.widget).setCursor(null);
/*      */           }
/*      */         }
/*      */       };
/*      */     }
/*      */     
/* 1316 */     return this.handCursorListener;
/*      */   }
/*      */   
/*      */   public SWTSkinObject createSkinObject(String sID, String sConfigID, SWTSkinObject parentSkinObject)
/*      */   {
/* 1321 */     return createSkinObject(sID, sConfigID, parentSkinObject, null);
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
/*      */   public SWTSkinObject createSkinObject(String sID, String sConfigID, SWTSkinObject parentSkinObject, Object datasource)
/*      */   {
/* 1335 */     SWTSkinObject skinObject = null;
/* 1336 */     Cursor cursor = this.skinComposite.getCursor();
/*      */     try {
/* 1338 */       this.skinComposite.setCursor(this.skinComposite.getDisplay().getSystemCursor(1));
/*      */       
/* 1340 */       skinObject = linkIDtoParent(this.skinProperties, sID, sConfigID, parentSkinObject, true, true, datasource);
/*      */       
/*      */ 
/* 1343 */       if (this.bLayoutComplete) {
/* 1344 */         layout(skinObject);
/*      */       }
/*      */     } catch (Exception e) {
/* 1347 */       Debug.out("Trying to create " + sID + "." + sConfigID + " on " + parentSkinObject, e);
/*      */     }
/*      */     finally {
/* 1350 */       this.skinComposite.setCursor(cursor);
/*      */     }
/*      */     
/* 1353 */     return skinObject;
/*      */   }
/*      */   
/*      */   public void addSkinObject(SWTSkinObject skinObject) {
/* 1357 */     String sViewID = skinObject.getViewID();
/* 1358 */     if (sViewID != null) {
/* 1359 */       setSkinObjectViewID(skinObject, sViewID);
/*      */     }
/*      */     
/* 1362 */     attachControl(skinObject);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeSkinObject(SWTSkinObject skinObject)
/*      */   {
/* 1371 */     skinObject.triggerListeners(3);
/*      */     
/* 1373 */     String id = skinObject.getSkinObjectID();
/* 1374 */     this.mon_MapIDsToSOs.enter();
/*      */     try {
/* 1376 */       SWTSkinObject[] objects = (SWTSkinObject[])this.mapIDsToSOs.get(id);
/* 1377 */       if (objects != null) {
/* 1378 */         int x = 0;
/* 1379 */         for (int i = 0; i < objects.length; i++) {
/* 1380 */           if (objects[i] != skinObject) {
/* 1381 */             objects[(x++)] = objects[i];
/*      */           }
/*      */         }
/*      */         
/* 1385 */         SWTSkinObject[] newObjects = new SWTSkinObject[x];
/* 1386 */         System.arraycopy(objects, 0, newObjects, 0, x);
/* 1387 */         this.mapIDsToSOs.put(id, newObjects);
/*      */       }
/*      */     } finally {
/* 1390 */       this.mon_MapIDsToSOs.exit();
/*      */     }
/*      */     
/* 1393 */     this.mon_mapPublicViewIDsToSOs.enter();
/*      */     try {
/* 1395 */       id = skinObject.getViewID();
/* 1396 */       SWTSkinObject[] objects = (SWTSkinObject[])this.mapPublicViewIDsToSOs.get(id);
/* 1397 */       if (objects != null) {
/* 1398 */         int x = 0;
/* 1399 */         for (int i = 0; i < objects.length; i++) {
/* 1400 */           if (objects[i] != skinObject) {
/* 1401 */             objects[(x++)] = objects[i];
/*      */           }
/*      */         }
/* 1404 */         SWTSkinObject[] newObjects = new SWTSkinObject[x];
/* 1405 */         System.arraycopy(objects, 0, newObjects, 0, x);
/* 1406 */         this.mapPublicViewIDsToSOs.put(id, newObjects);
/*      */       }
/*      */     } finally {
/* 1409 */       this.mon_mapPublicViewIDsToSOs.exit();
/*      */     }
/*      */     
/* 1412 */     skinObject.dispose();
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject linkIDtoParent(SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parentSkinObject, boolean bForceCreate, boolean bAddView, Object datasource)
/*      */   {
/* 1418 */     this.currentSkinObjectcreationCount += 1;
/*      */     
/* 1420 */     SWTSkinObject skinObject = null;
/*      */     try {
/* 1422 */       if (sConfigID == null) {
/* 1423 */         return null;
/*      */       }
/* 1425 */       String[] sTypeParams = properties.getStringArray(sConfigID + ".type");
/*      */       String sType;
/* 1427 */       String sWidgets; if ((sTypeParams != null) && (sTypeParams.length > 0)) {
/* 1428 */         String sType = sTypeParams[0];
/* 1429 */         bForceCreate = true;
/*      */       }
/*      */       else {
/* 1432 */         sType = null;
/*      */         
/* 1434 */         String sImageLoc = properties.getStringValue(sConfigID);
/* 1435 */         String sText; if (sImageLoc != null) {
/* 1436 */           sType = "image";
/*      */         } else {
/* 1438 */           sText = properties.getStringValue(sConfigID + ".text");
/* 1439 */           if (sText != null) {
/* 1440 */             sType = "text";
/*      */           } else {
/* 1442 */             sWidgets = properties.getStringValue(sConfigID + ".widgets");
/* 1443 */             if ((sWidgets != null) || (bForceCreate)) {
/* 1444 */               sType = "container";
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1449 */         if (sType == null) {
/* 1450 */           if (this.DEBUGLAYOUT) {
/* 1451 */             System.err.println("no type defined for " + sConfigID);
/*      */           }
/* 1453 */           return null;
/*      */         }
/*      */         
/* 1456 */         sTypeParams = new String[] { sType };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1461 */       int iMinUserMode = properties.getIntValue(sConfigID + ".minUserMode", -1);
/* 1462 */       int userMode; if (iMinUserMode > 0) {
/* 1463 */         userMode = COConfigurationManager.getIntParameter("User Mode");
/* 1464 */         if (userMode <= iMinUserMode) {
/* 1465 */           return null;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1470 */       if (sType.equals("image")) {
/* 1471 */         skinObject = createImageLabel(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */       }
/* 1473 */       else if (sType.equals("image2")) {
/* 1474 */         skinObject = createImageLabel2(properties, sID, parentSkinObject);
/* 1475 */       } else if (sType.equals("container2")) {
/* 1476 */         skinObject = createContainer2(properties, sID, sConfigID, parentSkinObject, bForceCreate, false, null);
/*      */       }
/* 1478 */       else if (sType.equals("container")) {
/* 1479 */         skinObject = createContainer(properties, sID, sConfigID, sTypeParams, parentSkinObject, bForceCreate, false, null);
/*      */       }
/* 1481 */       else if (sType.equals("text")) {
/* 1482 */         skinObject = createTextLabel(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */       }
/* 1484 */       else if (sType.equals("tab")) {
/* 1485 */         skinObject = _createTab(properties, sID, sConfigID, parentSkinObject);
/* 1486 */       } else if (sType.equals("v-sash")) {
/* 1487 */         skinObject = createSash(properties, sID, sConfigID, parentSkinObject, true);
/*      */       }
/* 1489 */       else if (sType.equals("h-sash")) {
/* 1490 */         skinObject = createSash(properties, sID, sConfigID, parentSkinObject, false);
/*      */       }
/* 1492 */       else if (sType.equals("v-mysash")) {
/* 1493 */         skinObject = createMySash(properties, sID, sConfigID, sTypeParams, parentSkinObject, true);
/*      */       }
/* 1495 */       else if (sType.equals("h-mysash")) {
/* 1496 */         skinObject = createMySash(properties, sID, sConfigID, sTypeParams, parentSkinObject, false);
/*      */       }
/* 1498 */       else if (sType.equals("clone")) {
/* 1499 */         skinObject = createClone(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */       }
/* 1501 */       else if (sType.equals("slider")) {
/* 1502 */         skinObject = createSlider(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */       } else {
/* 1504 */         if (sType.equals("hidden"))
/* 1505 */           return null;
/* 1506 */         if (sType.equals("browser")) {
/* 1507 */           skinObject = createBrowser(properties, sID, sConfigID, parentSkinObject);
/* 1508 */         } else if (sType.equals("separator")) {
/* 1509 */           skinObject = createSeparator(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1511 */         else if (sType.equals("button")) {
/* 1512 */           skinObject = createButton(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1514 */         else if (sType.equals("checkbox")) {
/* 1515 */           skinObject = createCheckbox(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1517 */         else if (sType.equals("toggle")) {
/* 1518 */           skinObject = createToggle(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1520 */         else if (sType.equals("textbox")) {
/* 1521 */           skinObject = createTextbox(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1523 */         else if (sType.equals("combo")) {
/* 1524 */           skinObject = createCombo(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1526 */         else if (sType.equals("tabfolder")) {
/* 1527 */           skinObject = createTabFolder(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1529 */         else if (sType.equals("expandbar")) {
/* 1530 */           skinObject = createExpandBar(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/* 1532 */         else if (sType.equals("expanditem")) {
/* 1533 */           skinObject = createExpandItem(properties, sID, sConfigID, sTypeParams, parentSkinObject);
/*      */         }
/*      */         else {
/* 1536 */           System.err.println(sConfigID + ": Invalid type of " + sType);
/* 1537 */           return null;
/*      */         }
/*      */       }
/* 1540 */       if (skinObject != null) {
/* 1541 */         skinObject.setData("CreationParams", datasource);
/* 1542 */         if (datasource != null) {
/* 1543 */           skinObject.triggerListeners(7, datasource);
/*      */         }
/*      */         
/*      */ 
/* 1547 */         if (bAddView) {
/* 1548 */           String sViewID = skinObject.getViewID();
/* 1549 */           if (sViewID != null) {
/* 1550 */             setSkinObjectViewID(skinObject, sViewID);
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Exception e) {
/* 1555 */       e.printStackTrace();
/*      */     } finally {
/* 1557 */       this.currentSkinObjectcreationCount -= 1;
/*      */     }
/*      */     
/* 1560 */     if (skinObject != null) {
/* 1561 */       skinObject.triggerListeners(4);
/*      */     }
/*      */     
/* 1564 */     return skinObject;
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
/*      */   private SWTSkinObject createButton(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1580 */     SWTSkinObject skinObject = new SWTSkinObjectButton(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1582 */     addToControlMap(skinObject);
/*      */     
/* 1584 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createCheckbox(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1590 */     SWTSkinObject skinObject = new SWTSkinObjectCheckbox(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1592 */     addToControlMap(skinObject);
/*      */     
/* 1594 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createToggle(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1600 */     SWTSkinObject skinObject = new SWTSkinObjectToggle(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1602 */     addToControlMap(skinObject);
/*      */     
/* 1604 */     return skinObject;
/*      */   }
/*      */   
/*      */   private SWTSkinObject createExpandBar(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1609 */     String[] sItems = properties.getStringArray(configID + ".widgets");
/*      */     
/* 1611 */     if (this.DEBUGLAYOUT) {
/* 1612 */       System.out.println("createExpandBar: " + id + ";" + properties.getStringValue(new StringBuilder().append(configID).append(".widgets").toString()));
/*      */     }
/*      */     
/*      */ 
/* 1616 */     SWTSkinObject skinObject = new SWTSkinObjectExpandBar(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1618 */     addToControlMap(skinObject);
/*      */     
/* 1620 */     if (sItems != null) {
/* 1621 */       addContainerChildren(skinObject, sItems, properties);
/*      */     }
/*      */     
/*      */ 
/* 1625 */     return skinObject;
/*      */   }
/*      */   
/*      */   private SWTSkinObject createExpandItem(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1630 */     String[] sItems = properties.getStringArray(configID + ".widgets");
/*      */     
/* 1632 */     if (this.DEBUGLAYOUT) {
/* 1633 */       System.out.println("createExpandItem: " + id + ";" + properties.getStringValue(new StringBuilder().append(configID).append(".widgets").toString()));
/*      */     }
/*      */     
/*      */ 
/* 1637 */     SWTSkinObject skinObject = new SWTSkinObjectExpandItem(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1639 */     addToControlMap(skinObject);
/*      */     
/* 1641 */     if (sItems != null) {
/* 1642 */       addContainerChildren(skinObject, sItems, properties);
/*      */     }
/*      */     
/* 1645 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private SWTSkinObject createTextbox(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1652 */     SWTSkinObject skinObject = new SWTSkinObjectTextbox(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1654 */     addToControlMap(skinObject);
/*      */     
/* 1656 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createCombo(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1662 */     SWTSkinObject skinObject = new SWTSkinObjectCombo(this, properties, id, configID, parentSkinObject);
/*      */     
/* 1664 */     addToControlMap(skinObject);
/*      */     
/* 1666 */     return skinObject;
/*      */   }
/*      */   
/*      */   private SWTSkinObject createTabFolder(SWTSkinProperties properties, String id, String configID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1671 */     String[] sItems = properties.getStringArray(configID + ".widgets");
/*      */     
/* 1673 */     if (this.DEBUGLAYOUT) {
/* 1674 */       System.out.println("createTabFolder: " + id + ";" + properties.getStringValue(new StringBuilder().append(configID).append(".widgets").toString()));
/*      */     }
/*      */     
/*      */ 
/* 1678 */     SWTSkinObject skinObject = getSkinObjectByID(id, parentSkinObject);
/*      */     
/* 1680 */     if (skinObject == null) {
/* 1681 */       skinObject = new SWTSkinObjectTabFolder(this, properties, id, configID, parentSkinObject);
/*      */       
/* 1683 */       addToControlMap(skinObject);
/*      */     }
/* 1685 */     else if (!(skinObject instanceof SWTSkinObjectContainer)) {
/* 1686 */       return skinObject;
/*      */     }
/*      */     
/*      */ 
/* 1690 */     if (sItems != null) {
/* 1691 */       addContainerChildren(skinObject, sItems, properties);
/*      */     }
/*      */     
/* 1694 */     return skinObject;
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
/*      */   private SWTSkinObject createBrowser(SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parentSkinObject)
/*      */   {
/* 1707 */     SWTSkinObject skinObject = new SWTSkinObjectBrowser(this, properties, sID, sConfigID, parentSkinObject);
/*      */     
/* 1709 */     addToControlMap(skinObject);
/*      */     
/* 1711 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createClone(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1717 */     if (sConfigID.length() == 0) {
/* 1718 */       System.err.println("XXXXXXXX " + sID + " has no config ID.." + Debug.getStackTrace(false, false));
/*      */     }
/*      */     
/*      */     String[] sCloneParams;
/*      */     
/* 1723 */     if (typeParams.length > 1) {
/* 1724 */       int size = typeParams.length - 1;
/* 1725 */       String[] sCloneParams = new String[size];
/* 1726 */       System.arraycopy(typeParams, 1, sCloneParams, 0, size);
/*      */     } else {
/* 1728 */       sCloneParams = properties.getStringArray(sConfigID + ".clone");
/* 1729 */       if ((sCloneParams == null) || (sCloneParams.length < 1)) {
/* 1730 */         return null;
/*      */       }
/*      */     }
/*      */     
/* 1734 */     if ((properties instanceof SWTSkinPropertiesClone)) {
/* 1735 */       properties = ((SWTSkinPropertiesClone)properties).getOriginalProperties();
/*      */     }
/*      */     
/*      */ 
/* 1739 */     SWTSkinPropertiesClone cloneProperties = new SWTSkinPropertiesClone(properties, sConfigID, sCloneParams);
/*      */     
/*      */ 
/* 1742 */     return linkIDtoParent(cloneProperties, sID, "", parentSkinObject, false, false, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private SWTSkinObject createImageLabel(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1749 */     if (typeParams.length > 1) {
/* 1750 */       properties.addProperty(sConfigID + ".image", typeParams[1]);
/*      */     }
/* 1752 */     SWTSkinObjectImage skinObject = new SWTSkinObjectImage(this, properties, sID, sConfigID, parentSkinObject);
/*      */     
/* 1754 */     addToControlMap(skinObject);
/*      */     
/* 1756 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createContainer2(SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parentSkinObject, boolean bForceCreate, boolean bPropogate, SWTSkinObject intoSkinObject)
/*      */   {
/* 1762 */     String[] sItems = properties.getStringArray(sConfigID + ".widgets");
/* 1763 */     if ((sItems == null) && (!bForceCreate)) {
/* 1764 */       return null;
/*      */     }
/*      */     
/* 1767 */     if (this.DEBUGLAYOUT) {
/* 1768 */       System.out.println("createContainer: " + sID + ";" + properties.getStringValue(new StringBuilder().append(sConfigID).append(".widgets").toString()));
/*      */     }
/*      */     
/*      */ 
/* 1772 */     SWTSkinObject skinObject = getSkinObjectByID(sID, parentSkinObject);
/*      */     
/* 1774 */     if (skinObject == null) {
/* 1775 */       if (intoSkinObject != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1780 */         skinObject = intoSkinObject;
/*      */       }
/*      */     }
/* 1783 */     else if (!(skinObject instanceof SWTSkinObjectContainer)) {
/* 1784 */       return skinObject;
/*      */     }
/*      */     
/*      */ 
/* 1788 */     if (!bPropogate) {
/* 1789 */       bPropogate = properties.getIntValue(sConfigID + ".propogate", 0) == 1;
/*      */     }
/*      */     
/* 1792 */     if ((!bPropogate) && ((parentSkinObject instanceof SWTSkinObjectContainer))) {
/* 1793 */       bPropogate = ((SWTSkinObjectContainer)parentSkinObject).getPropogation();
/*      */     }
/* 1795 */     if ((bPropogate) && 
/* 1796 */       (skinObject != null)) {
/* 1797 */       ((SWTSkinObjectContainer)skinObject).setPropogation(true);
/*      */     }
/*      */     
/*      */ 
/* 1801 */     if (sItems != null) {
/* 1802 */       String[] paramValues = null;
/* 1803 */       if ((properties instanceof SWTSkinPropertiesParam)) {
/* 1804 */         paramValues = ((SWTSkinPropertiesParam)properties).getParamValues();
/*      */       }
/*      */       
/*      */ 
/* 1808 */       if ((properties instanceof SWTSkinPropertiesClone)) {
/* 1809 */         properties = ((SWTSkinPropertiesClone)properties).getOriginalProperties();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1817 */       if (paramValues != null) {
/* 1818 */         properties = new SWTSkinPropertiesParamImpl(properties, paramValues);
/*      */       }
/*      */       
/* 1821 */       for (int i = 0; i < sItems.length; i++) {
/* 1822 */         String sItemID = sItems[i];
/* 1823 */         linkIDtoParent(properties, sItemID, sItemID, skinObject, false, true, null);
/*      */       }
/*      */     }
/*      */     
/* 1827 */     return skinObject;
/*      */   }
/*      */   
/*      */   private SWTSkinObject createImageLabel2(SWTSkinProperties properties, final String sConfigID, SWTSkinObject parentSkinObject) {
/*      */     Composite createOn;
/*      */     Composite createOn;
/* 1833 */     if (parentSkinObject == null) {
/* 1834 */       createOn = this.skinComposite;
/*      */     } else {
/* 1836 */       createOn = (Composite)parentSkinObject.getControl();
/*      */     }
/*      */     
/* 1839 */     Canvas drawable = new Canvas(createOn, 262144);
/* 1840 */     drawable.setVisible(false);
/*      */     
/* 1842 */     final ImageLoader imageLoader = getImageLoader(properties);
/* 1843 */     Image image = imageLoader.getImage(sConfigID);
/* 1844 */     if (ImageLoader.isRealImage(image)) {
/* 1845 */       imageLoader.releaseImage(sConfigID);
/* 1846 */       image = imageLoader.getImage(sConfigID + ".image");
/* 1847 */       drawable.addDisposeListener(new DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent e) {
/* 1849 */           imageLoader.releaseImage(sConfigID + ".image");
/*      */         }
/*      */       });
/*      */     } else {
/* 1853 */       drawable.addDisposeListener(new DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent e) {
/* 1855 */           imageLoader.releaseImage(sConfigID);
/*      */         }
/*      */       });
/*      */     }
/* 1859 */     drawable.setData("image", image);
/*      */     
/* 1861 */     SWTSkinObjectBasic skinObject = new SWTSkinObjectBasic(this, properties, drawable, sConfigID, sConfigID, "image", parentSkinObject);
/*      */     
/* 1863 */     addToControlMap(skinObject);
/*      */     
/* 1865 */     this.ontopImages.add(skinObject);
/*      */     
/* 1867 */     return skinObject;
/*      */   }
/*      */   
/*      */ 
/*      */   private SWTSkinObject createSeparator(SWTSkinProperties properties, String sID, String sConfigID, String[] typeParams, SWTSkinObject parentSkinObject)
/*      */   {
/* 1873 */     SWTSkinObject skinObject = new SWTSkinObjectSeparator(this, properties, sID, sConfigID, parentSkinObject);
/*      */     
/* 1875 */     addToControlMap(skinObject);
/*      */     
/* 1877 */     return skinObject;
/*      */   }
/*      */   
/*      */   public SWTSkinProperties getSkinProperties() {
/* 1881 */     return this.skinProperties;
/*      */   }
/*      */   
/*      */   public void addListener(String viewID, SWTSkinObjectListener listener) {
/* 1885 */     this.mapPublicViewIDsToListeners_mon.enter();
/*      */     try {
/* 1887 */       ArrayList<SWTSkinObjectListener> list = (ArrayList)this.mapPublicViewIDsToListeners.get(viewID);
/*      */       
/* 1889 */       if (list != null) {
/* 1890 */         list.add(listener);
/*      */       } else {
/* 1892 */         list = new ArrayList();
/* 1893 */         list.add(listener);
/* 1894 */         this.mapPublicViewIDsToListeners.put(viewID, list);
/*      */       }
/*      */     } finally {
/* 1897 */       this.mapPublicViewIDsToListeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(String viewID, SWTSkinObjectListener listener) {
/* 1902 */     this.mapPublicViewIDsToListeners_mon.enter();
/*      */     try {
/* 1904 */       List<SWTSkinObjectListener> list = (List)this.mapPublicViewIDsToListeners.get(viewID);
/*      */       
/* 1906 */       if (list != null) {
/* 1907 */         list.remove(listener);
/*      */       }
/*      */     } finally {
/* 1910 */       this.mapPublicViewIDsToListeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public SWTSkinObjectListener[] getSkinObjectListeners(String viewID) {
/* 1915 */     if (viewID == null) {
/* 1916 */       return NOLISTENERS;
/*      */     }
/*      */     
/* 1919 */     this.mapPublicViewIDsToListeners_mon.enter();
/*      */     try {
/* 1921 */       ArrayList<SWTSkinObjectListener> existing = (ArrayList)this.mapPublicViewIDsToListeners.get(viewID);
/*      */       SWTSkinObjectListener[] arrayOfSWTSkinObjectListener;
/* 1923 */       if (existing != null) {
/* 1924 */         return (SWTSkinObjectListener[])existing.toArray(NOLISTENERS);
/*      */       }
/* 1926 */       return NOLISTENERS;
/*      */     } finally {
/* 1928 */       this.mapPublicViewIDsToListeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isLayoutComplete() {
/* 1933 */     return this.bLayoutComplete;
/*      */   }
/*      */   
/*      */   public void addListener(SWTSkinLayoutCompleteListener l) {
/* 1937 */     if (!this.listenersLayoutComplete.contains(l)) {
/* 1938 */       this.listenersLayoutComplete.add(l);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void main(String[] args) {
/* 1943 */     Date d = new Date();
/* 1944 */     long t = d.getTime();
/*      */     
/* 1946 */     t -= 99878400000L;
/* 1947 */     t -= 21600000L;
/* 1948 */     t -= 1020000L;
/*      */     
/* 1950 */     Date then = new Date(t);
/*      */     
/* 1952 */     System.out.println(d + ";" + then);
/*      */   }
/*      */   
/*      */   public boolean isCreatingSO()
/*      */   {
/* 1957 */     return this.currentSkinObjectcreationCount > 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void triggerLanguageChange()
/*      */   {
/* 1966 */     Object[] values = this.mapIDsToSOs.values().toArray();
/* 1967 */     for (int i = 0; i < values.length; i++) {
/* 1968 */       SWTSkinObject[] skinObjects = (SWTSkinObject[])values[i];
/* 1969 */       if (skinObjects != null) {
/* 1970 */         for (int j = 0; j < skinObjects.length; j++) {
/* 1971 */           SWTSkinObject so = skinObjects[j];
/* 1972 */           so.triggerListeners(6);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoSizeOnLayout(boolean autoSizeOnLayout) {
/* 1979 */     this.autoSizeOnLayout = autoSizeOnLayout;
/*      */   }
/*      */   
/*      */   public boolean isAutoSizeOnLayout() {
/* 1983 */     return this.autoSizeOnLayout;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */