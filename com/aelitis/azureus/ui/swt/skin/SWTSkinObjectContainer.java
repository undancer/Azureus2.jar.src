/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AERunnableObject;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.CompositeMinSize;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
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
/*     */ public class SWTSkinObjectContainer
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*  48 */   boolean bPropogate = false;
/*     */   
/*  50 */   boolean bPropogateDown = false;
/*     */   
/*  52 */   private String[] sTypeParams = null;
/*     */   
/*     */   private int minWidth;
/*     */   
/*     */   private int minHeight;
/*     */   
/*     */   public SWTSkinObjectContainer(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, String[] sTypeParams, SWTSkinObject parent)
/*     */   {
/*  60 */     super(skin, properties, sID, sConfigID, "container", parent);
/*  61 */     this.sTypeParams = sTypeParams;
/*     */     Composite createOn;
/*  63 */     Composite createOn; if (parent == null) {
/*  64 */       createOn = skin.getShell();
/*     */     } else {
/*  66 */       createOn = (Composite)parent.getControl();
/*     */     }
/*  68 */     createComposite(createOn);
/*     */   }
/*     */   
/*     */   public SWTSkinObjectContainer(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/*  73 */     super(skin, properties, sID, sConfigID, "container", parent);
/*     */     Composite createOn;
/*  75 */     Composite createOn; if (parent == null) {
/*  76 */       createOn = skin.getShell();
/*     */     } else {
/*  78 */       createOn = (Composite)parent.getControl();
/*     */     }
/*  80 */     createComposite(createOn);
/*     */   }
/*     */   
/*     */ 
/*     */   public SWTSkinObjectContainer(SWTSkin skin, SWTSkinProperties properties, Control control, String sID, String sConfigID, String type, SWTSkinObject parent)
/*     */   {
/*  86 */     super(skin, properties, sID, sConfigID, type, parent);
/*     */     
/*  88 */     if (control != null) {
/*  89 */       triggerListeners(4);
/*  90 */       setControl(control);
/*     */     }
/*     */   }
/*     */   
/*     */   protected Composite createComposite(Composite createOn) {
/*  95 */     int style = 0;
/*  96 */     if (this.properties.getIntValue(this.sConfigID + ".border", 0) == 1) {
/*  97 */       style = 2048;
/*     */     }
/*  99 */     if (this.properties.getBooleanValue(this.sConfigID + ".doublebuffer", false)) {
/* 100 */       style |= 0x20000000;
/*     */     }
/*     */     
/* 103 */     this.minWidth = this.properties.getPxValue(this.sConfigID + ".minwidth", -1);
/* 104 */     this.minHeight = this.properties.getPxValue(this.sConfigID + ".minheight", -1);
/*     */     
/*     */     final Composite parentComposite;
/* 107 */     if (this.skin.DEBUGLAYOUT) {
/* 108 */       System.out.println("linkIDtoParent: Create Composite " + this.sID + " on " + createOn);
/*     */       
/* 110 */       Composite parentComposite = new Group(createOn, style);
/* 111 */       ((Group)parentComposite).setText(this.sConfigID == null ? this.sID : this.sConfigID);
/* 112 */       parentComposite.setData("DEBUG", "1");
/*     */     }
/* 114 */     else if ((this.sTypeParams == null) || (this.sTypeParams.length < 2) || (!this.sTypeParams[1].equalsIgnoreCase("group")))
/*     */     {
/*     */ 
/*     */ 
/* 118 */       Composite parentComposite = new CompositeMinSize(createOn, style);
/* 119 */       ((CompositeMinSize)parentComposite).setMinSize(new Point(this.minWidth, this.minHeight));
/*     */     } else {
/* 121 */       parentComposite = new Group(createOn, style);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */     parentComposite.setLayout(new FormLayout());
/*     */     
/* 131 */     this.control = parentComposite;
/*     */     
/* 133 */     if (this.properties.getBooleanValue(this.sConfigID + ".auto.defer.layout", false))
/*     */     {
/* 135 */       Listener show_hide_listener = new Listener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 142 */           parentComposite.setLayoutDeferred(event.type == 23);
/*     */         }
/*     */         
/* 145 */       };
/* 146 */       parentComposite.addListener(22, show_hide_listener);
/* 147 */       parentComposite.addListener(23, show_hide_listener);
/*     */     }
/*     */     
/* 150 */     setControl(this.control);
/*     */     
/* 152 */     return parentComposite;
/*     */   }
/*     */   
/*     */   public void setControl(Control control)
/*     */   {
/* 157 */     this.bPropogateDown = (this.properties.getIntValue(this.sConfigID + ".propogateDown", 1) == 1);
/*     */     
/* 159 */     super.setControl(control);
/*     */   }
/*     */   
/*     */   protected void setViewID(String viewID) {
/* 163 */     super.setViewID(viewID);
/* 164 */     if ((this.skin.DEBUGLAYOUT) && (this.control != null)) {
/* 165 */       ((Group)this.control).setText("[" + viewID + "]");
/*     */     }
/*     */   }
/*     */   
/*     */   public SWTSkinObject[] getChildren() {
/* 170 */     if (isDisposed()) {
/* 171 */       return new SWTSkinObject[0];
/*     */     }
/* 173 */     SWTSkinObject[] so = (SWTSkinObject[])Utils.execSWTThreadWithObject("getChildren", new AERunnableObject()
/*     */     {
/*     */       public Object runSupport()
/*     */       {
/* 177 */         if (SWTSkinObjectContainer.this.control.isDisposed()) {
/* 178 */           return new SWTSkinObject[0];
/*     */         }
/* 180 */         Control[] swtChildren = ((Composite)SWTSkinObjectContainer.this.control).getChildren();
/* 181 */         ArrayList<SWTSkinObject> list = new ArrayList(swtChildren.length);
/* 182 */         for (int i = 0; i < swtChildren.length; i++) {
/* 183 */           Control childControl = swtChildren[i];
/* 184 */           SWTSkinObject so = (SWTSkinObject)childControl.getData("SkinObject");
/* 185 */           if (so != null) {
/* 186 */             list.add(so);
/*     */           }
/*     */         }
/*     */         
/* 190 */         return list.toArray(new SWTSkinObject[list.size()]); } }, 2000L);
/*     */     
/*     */ 
/* 193 */     if (so == null) {
/* 194 */       System.err.println("Tell Tux to fix this " + Debug.getCompressedStackTrace());
/* 195 */       return oldgetChildren();
/*     */     }
/* 197 */     return so;
/*     */   }
/*     */   
/*     */   public SWTSkinObject[] oldgetChildren()
/*     */   {
/* 202 */     String[] widgets = this.properties.getStringArray(this.sConfigID + ".widgets");
/* 203 */     if (widgets == null) {
/* 204 */       return new SWTSkinObject[0];
/*     */     }
/*     */     
/* 207 */     ArrayList list = new ArrayList();
/* 208 */     for (int i = 0; i < widgets.length; i++) {
/* 209 */       String id = widgets[i];
/* 210 */       SWTSkinObject skinObject = this.skin.getSkinObjectByID(id, this);
/* 211 */       if (skinObject != null) {
/* 212 */         list.add(skinObject);
/*     */       }
/*     */     }
/*     */     
/* 216 */     SWTSkinObject[] objects = new SWTSkinObject[list.size()];
/* 217 */     objects = (SWTSkinObject[])list.toArray(objects);
/*     */     
/* 219 */     return objects;
/*     */   }
/*     */   
/*     */   public Composite getComposite() {
/* 223 */     return (Composite)this.control;
/*     */   }
/*     */   
/*     */   public String switchSuffix(final String suffix, final int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 228 */     String sFullsuffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/* 230 */     if ((this.bPropogateDown) && (walkDown) && (suffix != null) && (this.control != null) && (!this.control.isDisposed()))
/*     */     {
/* 232 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 234 */           SWTSkinObject[] children = SWTSkinObjectContainer.this.getChildren();
/* 235 */           for (int i = 0; i < children.length; i++) {
/* 236 */             children[i].switchSuffix(suffix, level, false);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 241 */     return sFullsuffix;
/*     */   }
/*     */   
/*     */   public void setPropogation(boolean propogate) {
/* 245 */     this.bPropogate = propogate;
/* 246 */     if (this.skin.DEBUGLAYOUT) {
/* 247 */       ((Group)this.control).setText(((Group)this.control).getText() + (this.bPropogate ? ";P" : ""));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean getPropogation()
/*     */   {
/* 253 */     return this.bPropogate;
/*     */   }
/*     */   
/*     */   public void setDebugAndChildren(boolean b) {
/* 257 */     setDebug(true);
/* 258 */     SWTSkinObject[] children = getChildren();
/* 259 */     for (int i = 0; i < children.length; i++) {
/* 260 */       if ((children[i] instanceof SWTSkinObjectContainer)) {
/* 261 */         ((SWTSkinObjectContainer)children[i]).setDebugAndChildren(b);
/*     */       } else {
/* 263 */         children[i].setDebug(b);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean superSetIsVisible(boolean visible, boolean walkup) {
/* 269 */     boolean changed = super.setIsVisible(visible, walkup);
/* 270 */     return changed;
/*     */   }
/*     */   
/*     */   protected boolean setIsVisible(boolean visible, boolean walkup)
/*     */   {
/* 275 */     if ((Utils.isThisThreadSWT()) && (!this.control.isDisposed()) && (!this.control.getShell().isVisible()))
/*     */     {
/* 277 */       return false;
/*     */     }
/* 279 */     boolean changed = super.setIsVisible(visible, (walkup) && (visible));
/*     */     
/* 281 */     if (!changed) {
/* 282 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 287 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*     */       public void runSupport() {
/* 289 */         SWTSkinObject[] children = SWTSkinObjectContainer.this.getChildren();
/* 290 */         if (children.length == 0) {
/* 291 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 297 */         for (int i = 0; i < children.length; i++) {
/* 298 */           if ((children[i] instanceof SWTSkinObjectBasic)) {
/* 299 */             SWTSkinObjectBasic child = (SWTSkinObjectBasic)children[i];
/* 300 */             Control childControl = child.getControl();
/* 301 */             if ((childControl != null) && (!childControl.isDisposed()))
/*     */             {
/*     */ 
/* 304 */               child.setIsVisible(childControl.isVisible(), false);
/*     */             }
/*     */           }
/*     */         }
/* 308 */         SWTSkinObjectContainer.this.getComposite().layout();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 314 */     });
/* 315 */     return changed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void childAdded(SWTSkinObject soChild) {}
/*     */   
/*     */   public Image obfusticatedImage(Image image)
/*     */   {
/* 323 */     if (!isVisible()) {
/* 324 */       return image;
/*     */     }
/*     */     
/* 327 */     if ((getSkinView() instanceof ObfusticateImage)) {
/* 328 */       image = ((ObfusticateImage)getSkinView()).obfusticatedImage(image);
/*     */     }
/*     */     
/* 331 */     Control[] swtChildren = ((Composite)this.control).getChildren();
/* 332 */     for (int i = 0; i < swtChildren.length; i++) {
/* 333 */       Control childControl = swtChildren[i];
/*     */       
/* 335 */       SWTSkinObject so = (SWTSkinObject)childControl.getData("SkinObject");
/* 336 */       if ((so instanceof ObfusticateImage)) {
/* 337 */         ObfusticateImage oi = (ObfusticateImage)so;
/* 338 */         oi.obfusticatedImage(image);
/* 339 */       } else if (so == null) {
/* 340 */         ObfusticateImage oi = (ObfusticateImage)childControl.getData("ObfusticateImage");
/* 341 */         if (oi != null) {
/* 342 */           oi.obfusticatedImage(image);
/*     */ 
/*     */         }
/* 345 */         else if ((childControl instanceof Composite)) {
/* 346 */           obfusticatedImage((Composite)childControl, image);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 351 */     return super.obfusticatedImage(image);
/*     */   }
/*     */   
/*     */   private void obfusticatedImage(Composite c, Image image) {
/* 355 */     if ((c == null) || (c.isDisposed()) || (!c.isVisible())) {
/* 356 */       return;
/*     */     }
/* 358 */     Control[] children = c.getChildren();
/* 359 */     for (Control childControl : children) {
/* 360 */       if (childControl.isVisible())
/*     */       {
/*     */ 
/* 363 */         ObfusticateImage oi = (ObfusticateImage)childControl.getData("ObfusticateImage");
/* 364 */         if (oi != null) {
/* 365 */           oi.obfusticatedImage(image);
/*     */ 
/*     */         }
/* 368 */         else if ((childControl instanceof Composite)) {
/* 369 */           obfusticatedImage((Composite)childControl, image);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */