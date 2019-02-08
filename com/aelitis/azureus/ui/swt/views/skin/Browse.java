/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MdiListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext;
/*     */ import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
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
/*     */ public class Browse
/*     */   extends SkinView
/*     */   implements MdiCloseListener
/*     */ {
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   private SWTSkinObject soMain;
/*     */   private MdiEntryVitalityImage vitalityImage;
/*     */   private ContentNetwork contentNetwork;
/*     */   
/*     */   public SWTSkinObjectBrowser getBrowserSkinObject()
/*     */   {
/*  52 */     return this.browserSkinObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectCreated(SWTSkinObject skinObject, Object params)
/*     */   {
/*  63 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  64 */     if (mdi != null) {
/*  65 */       MdiEntry entry = mdi.getEntryBySkinView(this);
/*  66 */       if (entry != null) {
/*  67 */         entry.addListener(this);
/*     */       }
/*     */     }
/*     */     
/*  71 */     return super.skinObjectCreated(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/*  78 */     this.soMain = skinObject;
/*  79 */     Object creationParams = skinObject.getData("CreationParams");
/*     */     
/*  81 */     if ((creationParams instanceof ContentNetwork)) {
/*  82 */       this.contentNetwork = ((ContentNetwork)creationParams);
/*     */     } else {
/*  84 */       this.contentNetwork = ConstantsVuze.getDefaultContentNetwork();
/*     */     }
/*     */     
/*  87 */     this.browserSkinObject = SWTSkinUtils.findBrowserSO(this.soMain);
/*     */     
/*  89 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  90 */     if (mdi != null) {
/*  91 */       final MdiEntry entry = mdi.getEntryBySkinView(this);
/*  92 */       if (entry != null) {
/*  93 */         this.vitalityImage = entry.addVitalityImage("image.sidebar.vitality.dots");
/*  94 */         this.vitalityImage.setVisible(false);
/*     */         
/*  96 */         mdi.addListener(new MdiListener() {
/*  97 */           long lastSelect = 0L;
/*     */           
/*     */           public void mdiEntrySelected(MdiEntry newEntry, MdiEntry oldEntry)
/*     */           {
/* 101 */             if (entry == newEntry) {
/* 102 */               if (entry == oldEntry) {
/* 103 */                 if ((this.lastSelect < SystemTime.getOffsetTime(-1000L)) && 
/* 104 */                   (Browse.this.browserSkinObject != null)) {
/* 105 */                   Browse.this.browserSkinObject.restart();
/*     */                 }
/*     */               }
/*     */               else {
/* 109 */                 this.lastSelect = SystemTime.getCurrentTime();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/* 117 */     this.browserSkinObject.addListener(new SWTSkinObjectListener()
/*     */     {
/*     */       public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */       {
/* 121 */         if (eventType == 0) {
/* 122 */           Browse.this.browserSkinObject.removeListener(this);
/*     */           
/* 124 */           Browse.this.browserSkinObject.addListener(new BrowserContext.loadingListener() {
/*     */             public void browserLoadingChanged(boolean loading, String url) {
/* 126 */               if (Browse.this.vitalityImage != null) {
/* 127 */                 Browse.this.vitalityImage.setVisible(loading);
/*     */               }
/*     */               
/*     */             }
/* 131 */           });
/* 132 */           Browse.this.browserSkinObject.getContext().setContentNetworkID(Browse.this.contentNetwork.getID());
/*     */           
/* 134 */           Browse.this.browserSkinObject.enablePluginProxy("VHDN");
/*     */           
/* 136 */           Browse.this.browserSkinObject.setStartURL(ContentNetworkUtils.getUrl(Browse.this.contentNetwork, 6));
/*     */         }
/*     */         
/* 139 */         return null;
/*     */       }
/*     */       
/* 142 */     });
/* 143 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 144 */     UIManager uim = pi.getUIManager();
/* 145 */     MenuManager menuManager = uim.getMenuManager();
/*     */     
/* 147 */     String menuID = "sidebar." + ContentNetworkUtils.getTarget(this.contentNetwork);
/*     */     
/*     */ 
/* 150 */     MenuItem item = menuManager.addMenuItem(menuID, "Button.reload");
/* 151 */     item.addListener(new MenuItemListener() {
/*     */       public void selected(MenuItem menu, Object target) {
/* 153 */         Browse.this.browserSkinObject.refresh();
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 158 */     if (Constants.isCVSVersion()) {
/* 159 */       MenuItem parent = menuManager.addMenuItem(menuID, "CVS Only");
/* 160 */       parent.setStyle(5);
/*     */       
/*     */ 
/* 163 */       MenuItem menuItem = menuManager.addMenuItem(parent, "Button.reset");
/* 164 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 166 */           Browse.this.browserSkinObject.getContext().executeInBrowser("sendMessage('display','reset-url', {});");
/*     */         }
/*     */         
/*     */ 
/* 170 */       });
/* 171 */       menuItem = menuManager.addMenuItem(parent, "Tux RPC Test");
/* 172 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 174 */           Browse.this.browserSkinObject.setURL("c:\\test\\BrowserMessaging.html");
/*     */         }
/*     */         
/* 177 */       });
/* 178 */       menuItem = menuManager.addMenuItem(parent, "URL..");
/* 179 */       menuItem.addListener(new MenuItemListener() {
/*     */         public void selected(MenuItem menu, Object target) {
/* 181 */           SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("", "!URL!");
/* 182 */           entryWindow.prompt(new UIInputReceiverListener() {
/*     */             public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 184 */               if (entryWindow.hasSubmittedInput()) {
/* 185 */                 Browse.this.browserSkinObject.setURL(entryWindow.getSubmittedInput());
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 193 */     return null;
/*     */   }
/*     */   
/*     */   public void mdiEntryClosed(MdiEntry entry, boolean userClosed) {
/* 197 */     this.contentNetwork.setPersistentProperty("active", Boolean.FALSE);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/Browse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */