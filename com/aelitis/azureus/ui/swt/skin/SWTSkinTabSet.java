/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class SWTSkinTabSet
/*     */ {
/*     */   private Listener tabMouseListener;
/*     */   private final SWTSkin skin;
/*     */   private final String sID;
/*     */   private SWTSkinObjectTab activeTab;
/*     */   private List tabs;
/*  50 */   private ArrayList listeners = new ArrayList();
/*     */   
/*     */   public SWTSkinTabSet(SWTSkin skin, String sID)
/*     */   {
/*  54 */     this.sID = sID;
/*  55 */     this.skin = skin;
/*  56 */     this.tabs = new ArrayList();
/*     */   }
/*     */   
/*     */   public void addTab(final SWTSkinObjectTab tab) {
/*  60 */     this.tabs.add(tab);
/*  61 */     tab.setTabset(this);
/*     */     
/*     */ 
/*  64 */     addMouseListener(tab, tab.getControl());
/*     */     
/*  66 */     this.skin.addListener(new SWTSkinLayoutCompleteListener() {
/*     */       public void skinLayoutCompleted() {
/*  68 */         SWTSkinTabSet.this.setTabVisible(tab, SWTSkinTabSet.this.activeTab == tab, null);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public SWTSkinObjectTab getActiveTab() {
/*  74 */     return this.activeTab;
/*     */   }
/*     */   
/*     */   public SWTSkinObjectTab[] getTabs() {
/*  78 */     return (SWTSkinObjectTab[])this.tabs.toArray(new SWTSkinObjectTab[0]);
/*     */   }
/*     */   
/*     */   public SWTSkinObjectTab getTabByID(String sID) {
/*  82 */     for (int i = 0; i < this.tabs.size(); i++) {
/*  83 */       SWTSkinObjectTab tab = (SWTSkinObjectTab)this.tabs.get(i);
/*  84 */       String sTabID = tab.getSkinObjectID();
/*     */       
/*  86 */       if (sTabID.equals(sID)) {
/*  87 */         return tab;
/*     */       }
/*     */     }
/*     */     
/*  91 */     return null;
/*     */   }
/*     */   
/*     */   public SWTSkinObjectTab getTab(String sViewID) {
/*  95 */     for (int i = 0; i < this.tabs.size(); i++) {
/*  96 */       SWTSkinObjectTab tab = (SWTSkinObjectTab)this.tabs.get(i);
/*  97 */       String sTabViewID = tab.getViewID();
/*     */       
/*  99 */       if (sTabViewID.equals(sViewID)) {
/* 100 */         return tab;
/*     */       }
/*     */     }
/*     */     
/* 104 */     return null;
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public boolean setActiveTabByID(String sID)
/*     */   {
/* 112 */     for (int i = 0; i < this.tabs.size(); i++) {
/* 113 */       SWTSkinObject tab = (SWTSkinObject)this.tabs.get(i);
/* 114 */       if ((tab instanceof SWTSkinObjectTab)) {
/* 115 */         String sTabID = tab.getSkinObjectID();
/*     */         
/* 117 */         if (sTabID.equals(sID)) {
/* 118 */           setActiveTab((SWTSkinObjectTab)tab);
/* 119 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   public boolean setActiveTab(String viewID) {
/* 128 */     SWTSkinObject skinObject = this.skin.getSkinObject(viewID);
/*     */     
/* 130 */     if (skinObject == null) {
/* 131 */       return false;
/*     */     }
/*     */     
/* 134 */     return this.skin.activateTab(skinObject) != null;
/*     */   }
/*     */   
/*     */   public void setActiveTab(SWTSkinObjectTab newTab)
/*     */   {
/* 139 */     setActiveTab(newTab, false);
/*     */   }
/*     */   
/*     */   private void setActiveTab(final SWTSkinObjectTab newTab, final boolean evenIfSame) {
/* 143 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 145 */         Composite shell = SWTSkinTabSet.this.skin.getShell();
/* 146 */         Cursor cursor = shell.getCursor();
/*     */         try {
/* 148 */           shell.setCursor(shell.getDisplay().getSystemCursor(1));
/* 149 */           SWTSkinTabSet.this.swtSetActiveTab(newTab, evenIfSame);
/*     */         } finally {
/* 151 */           shell.setCursor(cursor);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void swtSetActiveTab(SWTSkinObjectTab newTab, boolean evenIfSame)
/*     */   {
/* 160 */     if (!this.tabs.contains(newTab)) {
/* 161 */       System.err.println("Not contain in " + this.sID + ": " + newTab);
/* 162 */       return;
/*     */     }
/*     */     
/* 165 */     String sOldID = this.activeTab == null ? "" : this.activeTab.getSkinObjectID();
/*     */     
/* 167 */     if (newTab != this.activeTab) {
/* 168 */       SWTSkinObject[] objects = setTabVisible(newTab, true, null);
/* 169 */       if (this.activeTab != null) {
/* 170 */         setTabVisible(this.activeTab, false, objects);
/*     */       }
/*     */       
/* 173 */       this.activeTab = newTab;
/* 174 */     } else if (!evenIfSame) {
/* 175 */       return;
/*     */     }
/*     */     
/* 178 */     String sConfigID = this.activeTab.getConfigID();
/* 179 */     String sNewID = this.activeTab.getSkinObjectID();
/*     */     
/* 181 */     SWTSkinObject parent = this.skin.getSkinObject(this.activeTab.getProperties().getStringValue(sConfigID + ".activate"));
/*     */     
/* 183 */     if (parent != null) {
/* 184 */       parent.getControl().setFocus();
/*     */     }
/*     */     
/* 187 */     triggerChangeListener(sOldID, sNewID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void triggerChangeListener(String oldID, String newID)
/*     */   {
/* 195 */     for (Iterator iter = this.listeners.iterator(); iter.hasNext();) {
/*     */       try {
/* 197 */         SWTSkinTabSetListener l = (SWTSkinTabSetListener)iter.next();
/* 198 */         l.tabChanged(this, oldID, newID);
/*     */       } catch (Exception e) {
/* 200 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void addMouseListener(SWTSkinObject tab, Control control) {
/* 206 */     if (this.tabMouseListener == null) {
/* 207 */       this.tabMouseListener = new Listener() {
/* 208 */         boolean bDownPressed = false;
/*     */         
/*     */         public void handleEvent(Event event) {
/* 211 */           if (event.type == 3) {
/* 212 */             this.bDownPressed = true;
/* 213 */             return; }
/* 214 */           if (!this.bDownPressed) {
/* 215 */             return;
/*     */           }
/*     */           
/* 218 */           this.bDownPressed = false;
/*     */           
/* 220 */           Control control = (Control)event.widget;
/* 221 */           SWTSkinTabSet.this.setActiveTab((SWTSkinObjectTab)control.getData("Tab"), true);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/* 226 */     control.setData("Tab", tab);
/* 227 */     control.addListener(4, this.tabMouseListener);
/* 228 */     control.addListener(3, this.tabMouseListener);
/*     */     
/* 230 */     if ((control instanceof Composite)) {
/* 231 */       Control[] children = ((Composite)control).getChildren();
/* 232 */       for (int i = 0; i < children.length; i++) {
/* 233 */         addMouseListener(tab, children[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private SWTSkinObject[] setTabVisible(SWTSkinObjectTab tab, boolean visible, SWTSkinObject[] skipObjects)
/*     */   {
/* 240 */     String sSkinID = tab.getSkinObjectID();
/*     */     
/* 242 */     SWTSkinObject soTabContent = this.skin.getSkinObjectByID(sSkinID);
/* 243 */     if (soTabContent == null) {
/* 244 */       return null;
/*     */     }
/*     */     
/* 247 */     String suffix = visible ? "-selected" : "";
/*     */     
/* 249 */     soTabContent.switchSuffix(suffix, 1, true);
/*     */     
/* 251 */     SWTSkinObject[] activeWidgets = tab.getActiveWidgets(visible);
/*     */     
/* 253 */     for (int i = 0; i < activeWidgets.length; i++) {
/* 254 */       SWTSkinObject skinObject = activeWidgets[i];
/* 255 */       boolean ok = true;
/* 256 */       if (skipObjects != null) {
/* 257 */         for (int j = 0; j < skipObjects.length; j++) {
/* 258 */           if (skinObject.equals(skipObjects[j])) {
/* 259 */             ok = false;
/* 260 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 265 */       if (ok) {
/* 266 */         if (visible) {
/* 267 */           skinObject.setDefaultVisibility();
/*     */         } else {
/* 269 */           skinObject.setVisible(visible);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 274 */     tab.triggerListeners(2);
/*     */     
/* 276 */     return activeWidgets;
/*     */   }
/*     */   
/*     */   public void addListener(SWTSkinTabSetListener listener) {
/* 280 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */   public String getID() {
/* 284 */     return this.sID;
/*     */   }
/*     */   
/*     */   protected static String[] getTemplateInfo(SWTSkin skin, SWTSkinObject skinObject, String sTemplateKey)
/*     */   {
/* 289 */     SWTSkinProperties skinProperties = skin.getSkinProperties();
/* 290 */     String sID = skinObject.getConfigID() + ".view.template." + sTemplateKey;
/* 291 */     return skinProperties.getStringArray(sID);
/*     */   }
/*     */   
/*     */   protected static String getTemplateID(SWTSkin skin, SWTSkinObject skinObject, String sTemplateKey)
/*     */   {
/* 296 */     String[] templateInfo = getTemplateInfo(skin, skinObject, sTemplateKey);
/* 297 */     if (templateInfo != null) {
/* 298 */       return templateInfo[0];
/*     */     }
/* 300 */     return null;
/*     */   }
/*     */   
/*     */   public static String getTabSetID(SWTSkin skin, SWTSkinObject skinObject, String sTemplateKey)
/*     */   {
/* 305 */     String[] templateInfo = getTemplateInfo(skin, skinObject, sTemplateKey);
/* 306 */     if ((templateInfo != null) && (templateInfo.length > 1)) {
/* 307 */       return templateInfo[1];
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinTabSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */