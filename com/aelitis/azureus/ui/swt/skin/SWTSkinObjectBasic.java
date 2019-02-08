/*      */ package com.aelitis.azureus.ui.swt.skin;
/*      */ 
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*      */ import com.aelitis.azureus.util.StringCompareUtils;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
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
/*      */ public class SWTSkinObjectBasic
/*      */   implements SWTSkinObject, PaintListener, ObfusticateImage
/*      */ {
/*      */   protected static final int BORDER_ROUNDED = 1;
/*      */   protected static final int BORDER_ROUNDED_FILL = 2;
/*      */   protected static final int BORDER_GRADIENT = 3;
/*      */   protected Control control;
/*      */   protected String type;
/*      */   protected String sConfigID;
/*      */   protected SWTBGImagePainter painter;
/*      */   protected SWTSkinProperties properties;
/*      */   protected String sID;
/*      */   protected SWTSkinObject parent;
/*      */   protected SWTSkin skin;
/*   75 */   protected String[] suffixes = null;
/*      */   
/*   77 */   protected ArrayList<SWTSkinObjectListener> listeners = new ArrayList();
/*      */   
/*   79 */   protected AEMonitor listeners_mon = new AEMonitor("SWTSkinObjectBasic::listener");
/*      */   
/*      */ 
/*      */   private String sViewID;
/*      */   
/*   84 */   private int isVisible = -1;
/*      */   
/*      */   protected Color bgColor;
/*      */   
/*      */   private Color colorBorder;
/*      */   
/*   90 */   private int[] colorBorderParams = null;
/*      */   
/*      */   private int[] colorFillParams;
/*      */   
/*      */   private int colorFillType;
/*      */   
/*   96 */   boolean initialized = false;
/*      */   
/*   98 */   boolean paintListenerHooked = false;
/*      */   
/*  100 */   boolean alwaysHookPaintListener = false;
/*      */   
/*  102 */   private Map mapData = Collections.EMPTY_MAP;
/*      */   
/*  104 */   private boolean disposed = false;
/*      */   
/*  106 */   protected boolean debug = false;
/*      */   
/*  108 */   private List<GradientInfo> listGradients = new ArrayList();
/*      */   
/*      */   private Image bgImage;
/*      */   
/*      */   private String tooltipID;
/*      */   
/*  114 */   protected boolean customTooltipID = false;
/*      */   
/*      */ 
/*      */   private Listener resizeGradientBGListener;
/*      */   
/*      */ 
/*      */   private SkinView skinView;
/*      */   
/*      */ 
/*      */   private Object datasource;
/*      */   
/*      */ 
/*      */   private boolean firstVisibility;
/*      */   
/*      */   private boolean layoutComplete;
/*      */   
/*      */   private ObfusticateImage obfusticatedImageGenerator;
/*      */   
/*      */ 
/*      */   public SWTSkinObjectBasic(SWTSkin skin, SWTSkinProperties properties, Control control, String sID, String sConfigID, String type, SWTSkinObject parent)
/*      */   {
/*  135 */     this(skin, properties, sID, sConfigID, type, parent);
/*  136 */     setControl(control);
/*      */   }
/*      */   
/*      */ 
/*      */   public SWTSkinObjectBasic(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, String type, SWTSkinObject parent)
/*      */   {
/*  142 */     this.skin = skin;
/*  143 */     this.properties = properties;
/*  144 */     this.sConfigID = sConfigID;
/*  145 */     this.sID = sID;
/*  146 */     this.type = type;
/*  147 */     this.parent = parent;
/*  148 */     setViewID(properties.getStringValue(sConfigID + ".view"));
/*  149 */     setDebug(properties.getBooleanValue(sConfigID + ".debug", false));
/*      */   }
/*      */   
/*      */   public void setControl(Control _control)
/*      */   {
/*  154 */     this.firstVisibility = this.properties.getBooleanValue(this.sConfigID + ".visible", true);
/*      */     
/*  156 */     if (!Utils.isThisThreadSWT()) {
/*  157 */       Debug.out("Warning: setControl not called in SWT thread for " + this);
/*  158 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  160 */           SWTSkinObjectBasic.this.setControl(SWTSkinObjectBasic.this.control);
/*      */         }
/*  162 */       });
/*  163 */       return;
/*      */     }
/*      */     
/*  166 */     this.resizeGradientBGListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  168 */         if ((SWTSkinObjectBasic.this.bgImage != null) && (!SWTSkinObjectBasic.this.bgImage.isDisposed())) {
/*  169 */           SWTSkinObjectBasic.this.bgImage.dispose();
/*      */         }
/*  171 */         Rectangle bounds = SWTSkinObjectBasic.this.control.getBounds();
/*  172 */         if (bounds.height <= 0) {
/*  173 */           return;
/*      */         }
/*  175 */         SWTSkinObjectBasic.this.bgImage = new Image(SWTSkinObjectBasic.this.control.getDisplay(), 5, bounds.height);
/*  176 */         GC gc = new GC(SWTSkinObjectBasic.this.bgImage);
/*      */         try {
/*      */           try {
/*  179 */             gc.setAdvanced(true);
/*  180 */             gc.setInterpolation(2);
/*  181 */             gc.setAntialias(1);
/*      */           }
/*      */           catch (Exception ex) {}
/*      */           
/*  185 */           SWTSkinObjectBasic.GradientInfo lastGradInfo = new SWTSkinObjectBasic.GradientInfo(SWTSkinObjectBasic.this.bgColor, 0.0D);
/*  186 */           for (SWTSkinObjectBasic.GradientInfo gradInfo : SWTSkinObjectBasic.this.listGradients) {
/*  187 */             if (gradInfo.startPoint != lastGradInfo.startPoint) {
/*  188 */               gc.setForeground(lastGradInfo.color);
/*  189 */               gc.setBackground(gradInfo.color);
/*      */               
/*  191 */               int y = (int)(bounds.height * lastGradInfo.startPoint);
/*  192 */               int height = (int)(bounds.height * gradInfo.startPoint) - y;
/*  193 */               gc.fillGradientRectangle(0, y, 5, height, true);
/*      */             }
/*  195 */             lastGradInfo = gradInfo;
/*      */           }
/*      */           
/*  198 */           if (lastGradInfo.startPoint < 1.0D) {
/*  199 */             gc.setForeground(lastGradInfo.color);
/*  200 */             gc.setBackground(lastGradInfo.color);
/*      */             
/*  202 */             int y = (int)(bounds.height * lastGradInfo.startPoint);
/*  203 */             int height = bounds.height - y;
/*  204 */             gc.fillGradientRectangle(0, y, 5, height, true);
/*      */           }
/*      */         } finally {
/*  207 */           gc.dispose();
/*      */         }
/*  209 */         if (SWTSkinObjectBasic.this.painter == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  214 */           SWTSkinObjectBasic.this.painter = new SWTBGImagePainter(SWTSkinObjectBasic.this.control, null, null, SWTSkinObjectBasic.this.bgImage, 3);
/*      */         }
/*      */         else {
/*  217 */           SWTSkinObjectBasic.this.painter.setImage(null, null, SWTSkinObjectBasic.this.bgImage);
/*      */         }
/*      */         
/*      */       }
/*  221 */     };
/*  222 */     this.control = _control;
/*  223 */     this.control.setData("ConfigID", this.sConfigID);
/*  224 */     this.control.setData("SkinObject", this);
/*      */     
/*  226 */     SWTSkinUtils.addMouseImageChangeListeners(this.control);
/*  227 */     switchSuffix(null, 0, false);
/*      */     
/*      */ 
/*  230 */     if (!this.properties.getBooleanValue(this.sConfigID + ".visible", true)) {
/*  231 */       setVisible(false);
/*      */     }
/*      */     
/*  234 */     final Listener lShowHide = new Listener() {
/*      */       public void handleEvent(final Event event) {
/*  236 */         final boolean toBeVisible = event.type == 22;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  244 */         Utils.execSWTThreadLater(0, new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*  249 */             if ((SWTSkinObjectBasic.this.control == null) || (SWTSkinObjectBasic.this.control.isDisposed())) {
/*  250 */               SWTSkinObjectBasic.this.setIsVisible(false, true);
/*  251 */               return;
/*      */             }
/*      */             
/*  254 */             if (toBeVisible == SWTSkinObjectBasic.this.control.isVisible()) { if ((SWTSkinObjectBasic.this.isVisible == 1) == toBeVisible) {
/*  255 */                 return;
/*      */               }
/*      */             }
/*  258 */             if (event.widget == SWTSkinObjectBasic.this.control) {
/*  259 */               SWTSkinObjectBasic.this.setIsVisible(toBeVisible, true);
/*  260 */               return;
/*      */             }
/*      */             
/*  263 */             if ((!toBeVisible) || (SWTSkinObjectBasic.this.control.isVisible())) {
/*  264 */               SWTSkinObjectBasic.this.setIsVisible(toBeVisible, true);
/*  265 */               return;
/*      */             }
/*  267 */             SWTSkinObjectBasic.this.setIsVisible(SWTSkinObjectBasic.this.control.isVisible(), true);
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  272 */     };
/*  273 */     this.control.addListener(22, lShowHide);
/*  274 */     this.control.addListener(23, lShowHide);
/*  275 */     final Shell shell = this.control.getShell();
/*  276 */     shell.addListener(22, lShowHide);
/*  277 */     shell.addListener(23, lShowHide);
/*      */     
/*  279 */     this.control.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  281 */         SWTSkinObjectBasic.this.disposed = true;
/*  282 */         shell.removeListener(22, lShowHide);
/*  283 */         shell.removeListener(23, lShowHide);
/*      */         
/*  285 */         SWTSkinObjectBasic.this.skin.removeSkinObject(SWTSkinObjectBasic.this);
/*      */       }
/*      */       
/*  288 */     });
/*  289 */     this.control.addListener(32, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  291 */         String id = SWTSkinObjectBasic.this.getTooltipID(true);
/*  292 */         if (id == null) {
/*  293 */           SWTSkinObjectBasic.this.control.setToolTipText(null);
/*  294 */         } else if ((id.startsWith("!")) && (id.endsWith("!"))) {
/*  295 */           SWTSkinObjectBasic.this.control.setToolTipText(id.substring(1, id.length() - 1));
/*      */         } else {
/*  297 */           SWTSkinObjectBasic.this.control.setToolTipText(MessageText.getString(id, (String)null));
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  302 */     if ((this.parent instanceof SWTSkinObjectContainer)) {
/*  303 */       ((SWTSkinObjectContainer)this.parent).childAdded(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean setIsVisible(boolean visible, boolean walkup)
/*      */   {
/*  313 */     if ((visible ? 1 : 0) == this.isVisible) {
/*  314 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  319 */     this.isVisible = (visible ? 1 : 0);
/*  320 */     switchSuffix(null, 0, false);
/*  321 */     triggerListeners(visible ? 0 : 1);
/*      */     
/*      */ 
/*      */ 
/*  325 */     if ((walkup) && (visible)) {
/*  326 */       SWTSkinObject p = this.parent;
/*      */       
/*  328 */       while ((p instanceof SWTSkinObjectBasic)) {
/*  329 */         ((SWTSkinObjectBasic)p).setIsVisible(visible, false);
/*  330 */         p = ((SWTSkinObjectBasic)p).getParent();
/*      */       }
/*      */     }
/*  333 */     return true;
/*      */   }
/*      */   
/*      */   public Control getControl() {
/*  337 */     return this.control;
/*      */   }
/*      */   
/*      */   public String getType() {
/*  341 */     return this.type;
/*      */   }
/*      */   
/*      */   public String getConfigID() {
/*  345 */     return this.sConfigID;
/*      */   }
/*      */   
/*      */   public String getSkinObjectID() {
/*  349 */     return this.sID;
/*      */   }
/*      */   
/*      */   public SWTSkinObject getParent()
/*      */   {
/*  354 */     return this.parent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBackground(String sConfigID, String sSuffix)
/*      */   {
/*  362 */     if (sConfigID == null) {
/*  363 */       return;
/*      */     }
/*      */     
/*  366 */     ImageLoader imageLoader = this.skin.getImageLoader(this.properties);
/*      */     
/*  368 */     String id = null;
/*  369 */     String idLeft = null;
/*  370 */     String idRight = null;
/*      */     
/*  372 */     String s = this.properties.getStringValue(sConfigID + sSuffix, (String)null);
/*  373 */     if ((s != null) && (s.length() > 0)) {
/*  374 */       Image[] images = imageLoader.getImages(sConfigID + sSuffix);
/*      */       try {
/*  376 */         if ((images.length == 1) && (ImageLoader.isRealImage(images[0]))) {
/*  377 */           id = sConfigID + sSuffix;
/*  378 */           idLeft = id + "-left";
/*  379 */           idRight = id + "-right";
/*  380 */         } else if ((images.length == 3) && (ImageLoader.isRealImage(images[2]))) {
/*  381 */           id = sConfigID + sSuffix;
/*  382 */           idLeft = id;
/*  383 */           idRight = id;
/*  384 */         } else if ((images.length == 2) && (ImageLoader.isRealImage(images[1]))) {
/*  385 */           id = sConfigID + sSuffix;
/*  386 */           idLeft = id;
/*  387 */           idRight = id + "-right";
/*      */         } else {
/*  389 */           id = sConfigID + sSuffix; return;
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*  396 */         imageLoader.releaseImage(sConfigID + sSuffix);
/*      */       }
/*      */     } else {
/*  399 */       if ((s != null) && (this.painter != null)) {
/*  400 */         this.painter.dispose();
/*  401 */         this.painter = null;
/*      */       }
/*  403 */       if (s == null) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  408 */       return;
/*      */     }
/*      */     
/*  411 */     if (this.painter == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  422 */       String sTileMode = this.properties.getStringValue(sConfigID + ".drawmode");
/*  423 */       int tileMode = SWTSkinUtils.getTileMode(sTileMode);
/*      */       
/*      */ 
/*  426 */       this.painter = new SWTBGImagePainter(this.control, imageLoader, idLeft, idRight, id, tileMode);
/*      */     }
/*      */     else
/*      */     {
/*  430 */       this.painter.setImage(imageLoader, idLeft, idRight, id);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  440 */     String s = "SWTSkinObjectBasic {" + this.sID;
/*      */     
/*  442 */     if (!this.sID.equals(this.sConfigID)) {
/*  443 */       s = s + "/" + this.sConfigID;
/*      */     }
/*      */     
/*  446 */     if (this.sViewID != null) {
/*  447 */       s = s + "/v=" + this.sViewID;
/*      */     }
/*      */     
/*  450 */     s = s + ", " + this.type + "; parent=" + (this.parent == null ? null : new StringBuilder().append(this.parent.getSkinObjectID()).append("}").toString());
/*      */     
/*      */ 
/*  453 */     return s;
/*      */   }
/*      */   
/*      */   public SWTSkin getSkin()
/*      */   {
/*  458 */     return this.skin;
/*      */   }
/*      */   
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  464 */     Debug.out("this should be implemented");
/*      */     
/*  466 */     return super.hashCode();
/*      */   }
/*      */   
/*      */   public boolean equals(Object obj)
/*      */   {
/*  471 */     if ((obj instanceof SWTSkinObject)) {
/*  472 */       SWTSkinObject skinObject = (SWTSkinObject)obj;
/*  473 */       boolean bEquals = skinObject.getSkinObjectID().equals(this.sID);
/*  474 */       if (this.parent != null) {
/*  475 */         return (bEquals) && (this.parent.equals(skinObject.getParent()));
/*      */       }
/*  477 */       return bEquals;
/*      */     }
/*      */     
/*  480 */     return super.equals(obj);
/*      */   }
/*      */   
/*      */   public void setVisible(final boolean visible)
/*      */   {
/*  485 */     if (!this.layoutComplete) {
/*  486 */       this.firstVisibility = visible;
/*  487 */       setIsVisible(visible, true);
/*  488 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  494 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  496 */         if ((SWTSkinObjectBasic.this.control != null) && (!SWTSkinObjectBasic.this.control.isDisposed())) {
/*  497 */           if (visible == SWTSkinObjectBasic.this.control.isVisible()) {} boolean changed = (visible ? 1 : 0) != SWTSkinObjectBasic.this.isVisible;
/*      */           
/*      */ 
/*  500 */           Object ld = SWTSkinObjectBasic.this.control.getLayoutData();
/*  501 */           if ((ld instanceof FormData)) {
/*  502 */             FormData fd = (FormData)ld;
/*  503 */             if (!visible) {
/*  504 */               if ((fd.width > 0) && (fd.height > 0)) {
/*  505 */                 SWTSkinObjectBasic.this.control.setData("oldSize", new Point(fd.width, fd.height));
/*  506 */                 changed = true;
/*      */               }
/*  508 */               fd.width = 0;
/*  509 */               fd.height = 0;
/*      */             } else {
/*  511 */               Object oldSize = SWTSkinObjectBasic.this.control.getData("oldSize");
/*  512 */               Point oldSizePoint = (oldSize instanceof Point) ? (Point)oldSize : new Point(-1, -1);
/*      */               
/*  514 */               if (fd.width <= 0) {
/*  515 */                 changed = true;
/*  516 */                 fd.width = oldSizePoint.x;
/*      */               }
/*  518 */               if (fd.height <= 0) {
/*  519 */                 changed = true;
/*  520 */                 fd.height = oldSizePoint.y;
/*      */               }
/*      */             }
/*  523 */             if (changed) {
/*  524 */               SWTSkinObjectBasic.this.control.setLayoutData(fd);
/*  525 */               SWTSkinObjectBasic.this.control.getParent().layout(true, true);
/*      */             }
/*  527 */           } else if ((ld == null) && (!visible)) {
/*  528 */             FormData fd = new FormData();
/*  529 */             fd.width = 0;
/*  530 */             fd.height = 0;
/*  531 */             SWTSkinObjectBasic.this.control.setLayoutData(fd);
/*      */           }
/*  533 */           if (!changed) {}
/*  534 */           SWTSkinObjectBasic.this.control.setVisible(visible);
/*      */           
/*      */ 
/*  537 */           SWTSkinObjectBasic.this.setIsVisible(visible, true);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setDefaultVisibility()
/*      */   {
/*  545 */     if (this.sConfigID == null) {
/*  546 */       return;
/*      */     }
/*      */     
/*  549 */     setVisible(getDefaultVisibility());
/*      */   }
/*      */   
/*      */   public boolean getDefaultVisibility() {
/*  553 */     return this.firstVisibility;
/*      */   }
/*      */   
/*      */   public boolean isVisible() {
/*  557 */     if ((this.control == null) || (this.control.isDisposed())) {
/*  558 */       return false;
/*      */     }
/*  560 */     if (!this.layoutComplete) {
/*  561 */       return this.firstVisibility;
/*      */     }
/*  563 */     return this.isVisible == 1 ? true : this.isVisible == -1 ? this.firstVisibility : false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String switchSuffix(String suffix)
/*      */   {
/*  570 */     return switchSuffix(suffix, 1, false);
/*      */   }
/*      */   
/*      */   public final String switchSuffix(String suffix, int level, boolean walkUp) {
/*  574 */     return switchSuffix(suffix, level, walkUp, true);
/*      */   }
/*      */   
/*      */   public String switchSuffix(String newSuffixEntry, int level, boolean walkUp, boolean walkDown) {
/*  578 */     if (walkUp) {
/*  579 */       SWTSkinObject parentSkinObject = this.parent;
/*  580 */       SWTSkinObject skinObject = this;
/*      */       
/*      */ 
/*      */ 
/*  584 */       while (((parentSkinObject instanceof SWTSkinObjectContainer)) && (((SWTSkinObjectContainer)parentSkinObject).getPropogation())) {
/*  585 */         skinObject = parentSkinObject;
/*  586 */         parentSkinObject = parentSkinObject.getParent();
/*      */       }
/*      */       
/*  589 */       if (skinObject != this)
/*      */       {
/*      */ 
/*  592 */         skinObject.switchSuffix(newSuffixEntry, level, false);
/*  593 */         return null;
/*      */       }
/*      */     }
/*  596 */     String old = getSuffix();
/*      */     
/*  598 */     if (level > 0)
/*      */     {
/*  600 */       if (this.suffixes == null) {
/*  601 */         old = null;
/*  602 */         this.suffixes = new String[level];
/*  603 */       } else if (this.suffixes.length < level) {
/*  604 */         String[] newSuffixes = new String[level];
/*  605 */         System.arraycopy(this.suffixes, 0, newSuffixes, 0, this.suffixes.length);
/*  606 */         this.suffixes = newSuffixes;
/*      */       }
/*  608 */       this.suffixes[(level - 1)] = newSuffixEntry;
/*      */     }
/*      */     
/*  611 */     String fullSuffix = getSuffix();
/*      */     
/*  613 */     if ((newSuffixEntry != null) && (
/*  614 */       (this.sConfigID == null) || (this.control == null) || (this.control.isDisposed()) || (!isVisible()) || ((newSuffixEntry != null) && (fullSuffix.equals(old)))))
/*      */     {
/*  616 */       return fullSuffix;
/*      */     }
/*      */     
/*      */ 
/*  620 */     final String sSuffix = fullSuffix;
/*      */     
/*  622 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  625 */         if ((SWTSkinObjectBasic.this.control == null) || (SWTSkinObjectBasic.this.control.isDisposed())) {
/*  626 */           return;
/*      */         }
/*      */         
/*  629 */         boolean needPaintHook = false;
/*      */         
/*  631 */         if (SWTSkinObjectBasic.this.properties.hasKey(SWTSkinObjectBasic.this.sConfigID + ".color" + sSuffix)) {
/*  632 */           SWTSkinObjectBasic.this.control.removeListener(11, SWTSkinObjectBasic.this.resizeGradientBGListener);
/*      */           
/*  634 */           Color color = SWTSkinObjectBasic.this.properties.getColor(SWTSkinObjectBasic.this.sConfigID + ".color" + sSuffix);
/*  635 */           SWTSkinObjectBasic.this.bgColor = color;
/*  636 */           String colorStyle = SWTSkinObjectBasic.this.properties.getStringValue(SWTSkinObjectBasic.this.sConfigID + ".color.style" + sSuffix);
/*      */           
/*  638 */           if (colorStyle != null) {
/*  639 */             String[] split = Constants.PAT_SPLIT_COMMA.split(colorStyle);
/*      */             
/*  641 */             if (split.length > 2) {
/*      */               try {
/*  643 */                 SWTSkinObjectBasic.this.colorFillParams = new int[] { Integer.parseInt(split[1]), Integer.parseInt(split[2]) };
/*      */               }
/*      */               catch (NumberFormatException e) {}
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  652 */             if (split[0].equals("rounded")) {
/*  653 */               SWTSkinObjectBasic.this.colorFillType = 1;
/*  654 */               needPaintHook = true;
/*  655 */             } else if (split[0].equals("rounded-fill")) {
/*  656 */               SWTSkinObjectBasic.this.colorFillType = 2;
/*  657 */               needPaintHook = true;
/*  658 */             } else if (split[0].equals("gradient")) {
/*  659 */               SWTSkinObjectBasic.this.colorFillType = 3;
/*      */               
/*  661 */               Device device = Display.getDefault();
/*  662 */               for (int i = 1; i < split.length; i += 2) {
/*  663 */                 Color colorStop = ColorCache.getSchemedColor(device, split[i]);
/*  664 */                 double posStop = 1.0D;
/*  665 */                 if (i != split.length - 1) {
/*      */                   try {
/*  667 */                     posStop = Double.parseDouble(split[(i + 1)]);
/*      */                   }
/*      */                   catch (Exception ignore) {}
/*      */                 }
/*  671 */                 SWTSkinObjectBasic.this.listGradients.add(new SWTSkinObjectBasic.GradientInfo(colorStop, posStop));
/*      */               }
/*      */               
/*  674 */               SWTSkinObjectBasic.this.control.addListener(11, SWTSkinObjectBasic.this.resizeGradientBGListener);
/*  675 */               SWTSkinObjectBasic.this.resizeGradientBGListener.handleEvent(null);
/*      */             }
/*      */             
/*  678 */             SWTSkinObjectBasic.this.control.redraw();
/*  679 */             SWTSkinObjectBasic.this.control.setBackground(null);
/*      */           } else {
/*  681 */             SWTSkinObjectBasic.this.control.setBackground(SWTSkinObjectBasic.this.bgColor);
/*      */           }
/*      */         }
/*      */         
/*  685 */         Color fg = SWTSkinObjectBasic.this.getColor_SuffixWalkback(SWTSkinObjectBasic.this.sConfigID + ".fgcolor");
/*  686 */         SWTSkinObjectBasic.this.control.setForeground(fg);
/*      */         
/*      */ 
/*  689 */         String sBorderStyle = SWTSkinObjectBasic.this.properties.getStringValue(SWTSkinObjectBasic.this.sConfigID + ".border" + sSuffix);
/*      */         
/*  691 */         SWTSkinObjectBasic.this.colorBorder = null;
/*  692 */         SWTSkinObjectBasic.this.colorBorderParams = null;
/*  693 */         if (sBorderStyle != null) {
/*  694 */           String[] split = Constants.PAT_SPLIT_COMMA.split(sBorderStyle);
/*  695 */           SWTSkinObjectBasic.this.colorBorder = ColorCache.getSchemedColor(SWTSkinObjectBasic.this.control.getDisplay(), split[0]);
/*  696 */           needPaintHook |= SWTSkinObjectBasic.this.colorBorder != null;
/*      */           
/*  698 */           if (split.length > 2) {
/*  699 */             SWTSkinObjectBasic.this.colorBorderParams = new int[] { Integer.parseInt(split[1]), Integer.parseInt(split[2]) };
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  706 */         SWTSkinObjectBasic.this.setBackground(SWTSkinObjectBasic.this.sConfigID + ".background", sSuffix);
/*      */         
/*  708 */         String sCursor = SWTSkinObjectBasic.this.properties.getStringValue(SWTSkinObjectBasic.this.sConfigID + ".cursor");
/*  709 */         if ((sCursor != null) && (sCursor.length() > 0) && 
/*  710 */           (sCursor.equalsIgnoreCase("hand"))) {
/*  711 */           Listener handCursorListener = SWTSkinObjectBasic.this.skin.getHandCursorListener(SWTSkinObjectBasic.this.control.getDisplay());
/*  712 */           SWTSkinObjectBasic.this.control.removeListener(6, handCursorListener);
/*  713 */           SWTSkinObjectBasic.this.control.removeListener(7, handCursorListener);
/*      */           
/*  715 */           SWTSkinObjectBasic.this.control.addListener(6, handCursorListener);
/*  716 */           SWTSkinObjectBasic.this.control.addListener(7, handCursorListener);
/*      */         }
/*      */         
/*      */ 
/*  720 */         if (!SWTSkinObjectBasic.this.customTooltipID) {
/*  721 */           String newToolTipID = SWTSkinObjectBasic.this.properties.getReferenceID(SWTSkinObjectBasic.this.sConfigID + ".tooltip" + sSuffix);
/*      */           
/*  723 */           if ((newToolTipID == null) && (sSuffix.length() > 0)) {
/*  724 */             newToolTipID = SWTSkinObjectBasic.this.properties.getReferenceID(SWTSkinObjectBasic.this.sConfigID + ".tooltip");
/*      */           }
/*  726 */           SWTSkinObjectBasic.this.tooltipID = newToolTipID;
/*      */         }
/*      */         
/*  729 */         if ((!SWTSkinObjectBasic.this.alwaysHookPaintListener) && (needPaintHook != SWTSkinObjectBasic.this.paintListenerHooked)) {
/*  730 */           if (needPaintHook) {
/*  731 */             SWTSkinObjectBasic.this.control.addPaintListener(SWTSkinObjectBasic.this);
/*      */           } else {
/*  733 */             SWTSkinObjectBasic.this.control.removePaintListener(SWTSkinObjectBasic.this);
/*      */           }
/*  735 */           SWTSkinObjectBasic.this.paintListenerHooked = needPaintHook;
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  740 */     });
/*  741 */     return fullSuffix;
/*      */   }
/*      */   
/*      */   public String getSuffix() {
/*  745 */     String suffix = "";
/*  746 */     if (this.suffixes == null) {
/*  747 */       return suffix;
/*      */     }
/*  749 */     for (int i = 0; i < this.suffixes.length; i++) {
/*  750 */       if (this.suffixes[i] != null) {
/*  751 */         suffix = suffix + this.suffixes[i];
/*      */       }
/*      */     }
/*  754 */     if (suffix.contains("-down-over")) {
/*  755 */       return suffix.replaceAll("-down-over", "-down");
/*      */     }
/*  757 */     return suffix;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SWTSkinProperties getProperties()
/*      */   {
/*  764 */     return this.properties;
/*      */   }
/*      */   
/*      */   public void setProperties(SWTSkinProperties skinProperties) {
/*  768 */     this.properties = skinProperties;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(final SWTSkinObjectListener listener)
/*      */   {
/*  775 */     int visibleStateAtAdd = this.isVisible;
/*  776 */     this.listeners_mon.enter();
/*      */     try {
/*  778 */       if (this.listeners.contains(listener)) {
/*  779 */         System.err.println("Already contains listener " + Debug.getCompressedStackTrace()); return;
/*      */       }
/*      */       
/*  782 */       this.listeners.add(listener);
/*      */     } finally {
/*  784 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  787 */     if (this.initialized) {
/*  788 */       listener.eventOccured(this, 4, null);
/*      */     }
/*      */     
/*  791 */     if (this.datasource != null) {
/*  792 */       listener.eventOccured(this, 7, this.datasource);
/*      */     }
/*      */     
/*      */ 
/*  796 */     if ((visibleStateAtAdd == 1) && (this.initialized)) {
/*  797 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  799 */           if (SWTSkinObjectBasic.this.isVisible != 1) {
/*  800 */             return;
/*      */           }
/*  802 */           listener.eventOccured(SWTSkinObjectBasic.this, 0, null);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeListener(SWTSkinObjectListener listener)
/*      */   {
/*  813 */     this.listeners_mon.enter();
/*      */     try {
/*  815 */       this.listeners.remove(listener);
/*      */     } finally {
/*  817 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public SWTSkinObjectListener[] getListeners() {
/*  822 */     return (SWTSkinObjectListener[])this.listeners.toArray(new SWTSkinObjectListener[0]);
/*      */   }
/*      */   
/*      */   public void triggerListeners(int eventType) {
/*  826 */     triggerListeners(eventType, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void triggerListeners(int eventType, Object params)
/*      */   {
/*  836 */     if ((eventType == 0) || (eventType == 1))
/*      */     {
/*  838 */       if (!this.initialized)
/*      */       {
/*  840 */         return;
/*      */       }
/*      */       
/*  843 */       if ((eventType == 0) && (!isVisible()))
/*      */       {
/*      */ 
/*      */ 
/*  847 */         return; }
/*  848 */       if ((eventType != 1) || (!isVisible())) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  854 */     else if (eventType == 4)
/*      */     {
/*  856 */       this.initialized = true;
/*  857 */     } else if (eventType == 7) {
/*  858 */       this.datasource = params;
/*  859 */     } else if ((eventType == 3) && (this.isVisible == 1)) {
/*  860 */       triggerListenersRaw(1, null);
/*      */     }
/*      */     
/*  863 */     triggerListenersRaw(eventType, params);
/*      */     
/*  865 */     if ((eventType == 4) && (this.isVisible >= 0)) {
/*  866 */       triggerListeners(isVisible() ? 0 : 1);
/*      */     }
/*      */     
/*      */ 
/*  870 */     if ((eventType == 0) && (this.skinView == null)) {
/*  871 */       String initClass = this.properties.getStringValue(this.sConfigID + ".onshow.skinviewclass");
/*  872 */       if (initClass != null) {
/*      */         try {
/*  874 */           String[] initClassItems = Constants.PAT_SPLIT_COMMA.split(initClass);
/*  875 */           ClassLoader claLoader = getClass().getClassLoader();
/*  876 */           if (initClassItems.length > 1) {
/*      */             try {
/*  878 */               PluginInterface pi = PluginInitializer.getDefaultInterface().getPluginManager().getPluginInterfaceByID(initClassItems[1]);
/*  879 */               if (pi != null) {
/*  880 */                 claLoader = pi.getPluginClassLoader();
/*      */               }
/*      */             } catch (Exception e) {
/*  883 */               Debug.out(e);
/*      */             }
/*      */           }
/*  886 */           Class<SkinView> cla = Class.forName(initClassItems[0], true, claLoader);
/*      */           
/*  888 */           setSkinView((SkinView)cla.newInstance());
/*  889 */           this.skinView.setMainSkinObject(this);
/*      */           
/*      */ 
/*  892 */           addListener(this.skinView);
/*      */         } catch (Throwable e) {
/*  894 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void triggerListenersRaw(int eventType, Object params)
/*      */   {
/*  902 */     SWTSkinObjectListener[] listenersArray = getListeners();
/*  903 */     if (listenersArray.length > 0)
/*      */     {
/*  905 */       for (SWTSkinObjectListener l : listenersArray) {
/*      */         try {
/*  907 */           l.eventOccured(this, eventType, params);
/*      */         } catch (Exception e) {
/*  909 */           Debug.out("Skin Event " + SWTSkinObjectListener.NAMES[eventType] + " caused an error for listener added locally", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  916 */     SWTSkinObjectListener[] listeners = this.skin.getSkinObjectListeners(this.sViewID);
/*  917 */     if (listeners.length > 0) {
/*  918 */       for (int i = 0; i < listeners.length; i++) {
/*      */         try {
/*  920 */           SWTSkinObjectListener l = listeners[i];
/*  921 */           l.eventOccured(this, eventType, params);
/*      */         } catch (Exception e) {
/*  923 */           Debug.out("Skin Event " + SWTSkinObjectListener.NAMES[eventType] + " caused an error for listener added to skin", e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void setViewID(String viewID)
/*      */   {
/*  931 */     this.sViewID = viewID;
/*      */   }
/*      */   
/*      */   public String getViewID() {
/*  935 */     return this.sViewID;
/*      */   }
/*      */   
/*      */   public void dispose()
/*      */   {
/*  940 */     if (this.disposed) {
/*  941 */       return;
/*      */     }
/*  943 */     Utils.disposeSWTObjects(new Object[] { this.control });
/*      */     
/*      */ 
/*      */ 
/*  947 */     if (this.skinView != null) {
/*  948 */       removeListener(this.skinView);
/*      */       
/*  950 */       if ((this.skinView instanceof UIUpdatable)) {
/*  951 */         UIUpdatable updateable = (UIUpdatable)this.skinView;
/*      */         try {
/*  953 */           UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(updateable);
/*      */         }
/*      */         catch (Exception e) {
/*  956 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isDisposed() {
/*  963 */     return this.disposed;
/*      */   }
/*      */   
/*      */   public void setTooltipID(String id)
/*      */   {
/*  968 */     if (isDisposed()) {
/*  969 */       return;
/*      */     }
/*  971 */     if (StringCompareUtils.equals(id, this.tooltipID)) {
/*  972 */       return;
/*      */     }
/*      */     
/*  975 */     this.tooltipID = id;
/*  976 */     this.customTooltipID = true;
/*      */   }
/*      */   
/*      */   public String getTooltipID(boolean walkup)
/*      */   {
/*  981 */     if ((this.tooltipID != null) || (!walkup)) {
/*  982 */       return this.tooltipID;
/*      */     }
/*  984 */     if (this.parent != null) {
/*  985 */       return this.parent.getTooltipID(true);
/*      */     }
/*  987 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public void paintControl(GC gc) {}
/*      */   
/*      */   public final void paintControl(PaintEvent e)
/*      */   {
/*  995 */     if (this.bgColor != null) {
/*  996 */       e.gc.setBackground(this.bgColor);
/*      */     }
/*      */     
/*  999 */     paintControl(e.gc);
/*      */     try
/*      */     {
/* 1002 */       e.gc.setAdvanced(true);
/* 1003 */       e.gc.setAntialias(1);
/*      */     }
/*      */     catch (Exception ex) {}
/*      */     
/* 1007 */     if (this.colorFillType > 0)
/*      */     {
/* 1009 */       Rectangle bounds = (this.control instanceof Composite) ? ((Composite)this.control).getClientArea() : this.control.getBounds();
/*      */       
/* 1011 */       if (this.colorFillParams != null) {
/* 1012 */         if (this.colorFillType == 2) {
/* 1013 */           e.gc.fillRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, this.colorFillParams[0], this.colorFillParams[1]);
/*      */           
/* 1015 */           e.gc.drawRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, this.colorFillParams[0], this.colorFillParams[1]);
/*      */         }
/* 1017 */         else if (this.colorFillType == 1) {
/* 1018 */           Color oldFG = e.gc.getForeground();
/* 1019 */           e.gc.setForeground(this.bgColor);
/* 1020 */           e.gc.drawRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, this.colorFillParams[0], this.colorFillParams[1]);
/*      */           
/* 1022 */           e.gc.setForeground(oldFG);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1033 */     if (this.colorBorder != null) {
/* 1034 */       e.gc.setForeground(this.colorBorder);
/* 1035 */       Rectangle bounds = (this.control instanceof Composite) ? ((Composite)this.control).getClientArea() : this.control.getBounds();
/*      */       
/* 1037 */       bounds.width -= 1;
/* 1038 */       bounds.height -= 1;
/* 1039 */       if (this.colorBorderParams == null) {
/* 1040 */         e.gc.drawRectangle(bounds);
/*      */       } else {
/* 1042 */         e.gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width, bounds.height, this.colorBorderParams[0], this.colorBorderParams[1]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isAlwaysHookPaintListener()
/*      */   {
/* 1049 */     return this.alwaysHookPaintListener;
/*      */   }
/*      */   
/*      */   public void setAlwaysHookPaintListener(boolean alwaysHookPaintListener) {
/* 1053 */     this.alwaysHookPaintListener = alwaysHookPaintListener;
/* 1054 */     if ((alwaysHookPaintListener) && (!this.paintListenerHooked)) {
/* 1055 */       this.control.addPaintListener(this);
/* 1056 */       this.paintListenerHooked = true;
/*      */     }
/*      */   }
/*      */   
/*      */   public Object getData(String id)
/*      */   {
/* 1062 */     return this.mapData.get(id);
/*      */   }
/*      */   
/*      */   public void setData(String id, Object data)
/*      */   {
/* 1067 */     if (this.mapData == Collections.EMPTY_MAP) {
/* 1068 */       this.mapData = new HashMap(1);
/*      */     }
/* 1070 */     this.mapData.put(id, data);
/*      */   }
/*      */   
/*      */   public Image obfusticatedImage(Image image)
/*      */   {
/* 1075 */     if (!isVisible()) {
/* 1076 */       return image;
/*      */     }
/* 1078 */     Point ourOfs = Utils.getLocationRelativeToShell(this.control);
/* 1079 */     if (this.obfusticatedImageGenerator == null) {
/* 1080 */       if ((this.skinView instanceof ObfusticateImage)) {
/* 1081 */         return ((ObfusticateImage)this.skinView).obfusticatedImage(image);
/*      */       }
/* 1083 */       return image;
/*      */     }
/* 1085 */     return this.obfusticatedImageGenerator.obfusticatedImage(image);
/*      */   }
/*      */   
/*      */   public void setObfusticatedImageGenerator(ObfusticateImage obfusticatedImageGenerator) {
/* 1089 */     this.obfusticatedImageGenerator = obfusticatedImageGenerator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDebug(boolean debug)
/*      */   {
/* 1096 */     this.debug = debug;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDebug()
/*      */   {
/* 1103 */     return this.debug;
/*      */   }
/*      */   
/*      */   public void relayout()
/*      */   {
/* 1108 */     if (!this.disposed) {
/* 1109 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 1111 */           if (SWTSkinObjectBasic.this.control.isDisposed()) {
/* 1112 */             return;
/*      */           }
/* 1114 */           SWTSkinObjectBasic.this.control.getShell().layout(new Control[] { SWTSkinObjectBasic.this.control });
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void layoutComplete()
/*      */   {
/* 1123 */     if (!this.layoutComplete) {
/* 1124 */       this.layoutComplete = true;
/* 1125 */       if ((this.control != null) && (!this.control.isDisposed())) {
/* 1126 */         this.control.setVisible(this.firstVisibility);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static class GradientInfo {
/*      */     public Color color;
/*      */     public double startPoint;
/*      */     
/*      */     public GradientInfo(Color c, double d) {
/* 1136 */       this.color = c;
/* 1137 */       this.startPoint = d;
/*      */     }
/*      */   }
/*      */   
/*      */   private Color getColor_SuffixWalkback(String id) {
/* 1142 */     int max = this.suffixes == null ? 0 : this.suffixes.length;
/* 1143 */     while (max >= 0) {
/* 1144 */       String suffix = "";
/* 1145 */       for (int i = 0; i < max; i++) {
/* 1146 */         if (this.suffixes[i] != null) {
/* 1147 */           suffix = suffix + this.suffixes[i];
/*      */         }
/*      */       }
/* 1150 */       Color color = this.properties.getColor(id + suffix);
/* 1151 */       if (color != null) {
/* 1152 */         return color;
/*      */       }
/* 1154 */       max--;
/*      */     }
/* 1156 */     return null;
/*      */   }
/*      */   
/*      */   public SkinView getSkinView() {
/* 1160 */     return this.skinView;
/*      */   }
/*      */   
/*      */   public void setSkinView(SkinView skinView) {
/* 1164 */     this.skinView = skinView;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectBasic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */