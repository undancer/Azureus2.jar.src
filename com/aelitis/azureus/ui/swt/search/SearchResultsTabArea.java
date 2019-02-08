/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class SearchResultsTabArea
/*     */   extends SkinView
/*     */   implements ViewTitleInfo
/*     */ {
/*  60 */   private boolean isBrowserView = COConfigurationManager.getBooleanParameter("Search View Is Web View", true);
/*  61 */   private boolean isViewSwitchHidden = COConfigurationManager.getBooleanParameter("Search View Switch Hidden", false);
/*     */   
/*     */   private SWTSkinObjectBrowser browserSkinObject;
/*     */   
/*     */   private SWTSkinObjectContainer nativeSkinObject;
/*     */   
/*     */   private SWTSkin skin;
/*     */   
/*     */   private MdiEntry mdi_entry;
/*     */   
/*     */   private MdiEntryVitalityImage vitalityImage;
/*     */   
/*     */   private boolean menu_added;
/*     */   
/*     */   private SearchQuery current_sq;
/*     */   
/*     */   private SearchQuery last_actual_sq;
/*     */   private SearchResultsTabAreaBase last_actual_sq_impl;
/*     */   private SearchResultsTabAreaBase activeImpl;
/*  80 */   private SearchResultsTabAreaBrowser browserImpl = new SearchResultsTabAreaBrowser(this);
/*  81 */   private SBC_SearchResultsView nativeImpl = new SBC_SearchResultsView(this);
/*     */   private SWTSkinObject soButtonWeb;
/*     */   private SWTSkinObject soButtonMeta;
/*     */   
/*     */   public static class SearchQuery
/*     */   {
/*     */     public String term;
/*     */     public boolean toSubscribe;
/*     */     
/*     */     public SearchQuery(String term, boolean toSubscribe)
/*     */     {
/*  92 */       this.term = term;
/*  93 */       this.toSubscribe = toSubscribe;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 105 */     this.skin = skinObject.getSkin();
/*     */     
/* 107 */     SWTSkinObjectContainer controlArea = (SWTSkinObjectContainer)this.skin.getSkinObject("searchresultstop", skinObject);
/*     */     
/* 109 */     if (controlArea != null)
/*     */     {
/* 111 */       if (this.isViewSwitchHidden)
/*     */       {
/* 113 */         controlArea.setVisible(false);
/*     */       }
/*     */       else {
/* 116 */         Composite control_area = controlArea.getComposite();
/*     */         
/* 118 */         this.soButtonWeb = this.skin.getSkinObject("searchresults-button-web", controlArea);
/* 119 */         this.soButtonMeta = this.skin.getSkinObject("searchresults-button-meta", controlArea);
/*     */         
/* 121 */         SWTSkinButtonUtility btnWeb = new SWTSkinButtonUtility(this.soButtonWeb);
/* 122 */         btnWeb.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */         {
/*     */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 125 */             SearchResultsTabArea.this.isBrowserView = true;
/* 126 */             COConfigurationManager.setParameter("Search View Is Web View", SearchResultsTabArea.this.isBrowserView);
/* 127 */             SearchResultsTabArea.this.selectView(skinObject);
/*     */           }
/*     */           
/* 130 */         });
/* 131 */         SWTSkinButtonUtility btnMeta = new SWTSkinButtonUtility(this.soButtonMeta);
/* 132 */         btnMeta.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */         {
/*     */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 135 */             SearchResultsTabArea.this.isBrowserView = false;
/* 136 */             COConfigurationManager.setParameter("Search View Is Web View", SearchResultsTabArea.this.isBrowserView);
/* 137 */             SearchResultsTabArea.this.selectView(skinObject);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 144 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*     */     
/* 146 */     if (mdi != null)
/*     */     {
/* 148 */       this.mdi_entry = mdi.getEntryBySkinView(this);
/*     */       
/* 150 */       if (this.mdi_entry != null)
/*     */       {
/* 152 */         this.mdi_entry.setViewTitleInfo(this);
/*     */         
/* 154 */         this.vitalityImage = this.mdi_entry.addVitalityImage("image.sidebar.vitality.dots");
/*     */         
/* 156 */         if (this.vitalityImage != null)
/*     */         {
/* 158 */           this.vitalityImage.setVisible(false);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 163 */     this.browserSkinObject = ((SWTSkinObjectBrowser)this.skin.getSkinObject("web-search-results", skinObject));
/*     */     
/* 165 */     this.browserImpl.init(this.browserSkinObject);
/*     */     
/* 167 */     this.nativeSkinObject = ((SWTSkinObjectContainer)this.skin.getSkinObject("meta-search-results", skinObject));
/*     */     
/* 169 */     this.nativeImpl.skinObjectInitialShow(skinObject, params);
/*     */     
/* 171 */     selectView(skinObject);
/*     */     
/* 173 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 175 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 177 */             SearchResultsTabArea.this.initCoreStuff(core);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 184 */     if (this.current_sq != null)
/*     */     {
/* 186 */       anotherSearch(this.current_sq);
/*     */     }
/*     */     
/* 189 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params) {
/* 193 */     if (this.activeImpl != null)
/*     */     {
/* 195 */       this.activeImpl.refreshView();
/*     */     }
/*     */     
/* 198 */     return super.skinObjectShown(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void selectView(SWTSkinObject parent)
/*     */   {
/* 205 */     SearchResultsTabAreaBase newImpl = this.isBrowserView ? this.browserImpl : this.nativeImpl;
/*     */     
/* 207 */     if (newImpl == this.activeImpl) {
/* 208 */       return;
/*     */     }
/*     */     
/* 211 */     Control[] kids = this.nativeSkinObject.getControl().getParent().getChildren();
/*     */     
/* 213 */     Control visible_parent = this.isBrowserView ? this.browserSkinObject.getControl() : this.nativeSkinObject.getControl();
/*     */     
/* 215 */     for (Control kid : kids) {
/* 216 */       kid.setVisible(kid == visible_parent);
/*     */     }
/*     */     
/* 219 */     this.browserSkinObject.setVisible(this.isBrowserView);
/* 220 */     this.nativeSkinObject.setVisible(!this.isBrowserView);
/*     */     
/* 222 */     if (this.soButtonWeb != null) {
/* 223 */       this.soButtonWeb.switchSuffix(this.isBrowserView ? "-selected" : "");
/*     */     }
/* 225 */     if (this.soButtonMeta != null) {
/* 226 */       this.soButtonMeta.switchSuffix(this.isBrowserView ? "" : "-selected");
/*     */     }
/*     */     
/*     */ 
/* 230 */     parent.relayout();
/*     */     
/* 232 */     if (this.activeImpl != null)
/*     */     {
/* 234 */       this.activeImpl.hideView();
/*     */     }
/*     */     
/* 237 */     this.activeImpl = newImpl;
/*     */     
/* 239 */     this.activeImpl.showView();
/*     */     
/* 241 */     if (this.current_sq != null)
/*     */     {
/* 243 */       anotherSearch(this.current_sq);
/*     */     }
/*     */   }
/*     */   
/*     */   private void initCoreStuff(AzureusCore core) {
/* 248 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/* 249 */     UIManager uim = pi.getUIManager();
/*     */     
/* 251 */     MenuManager menuManager = uim.getMenuManager();
/*     */     
/* 253 */     if (!this.menu_added)
/*     */     {
/* 255 */       this.menu_added = true;
/*     */       
/* 257 */       SearchUtils.addMenus(menuManager);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*     */   {
/* 266 */     if ((params instanceof SearchQuery))
/*     */     {
/* 268 */       anotherSearch((SearchQuery)params);
/*     */     }
/*     */     
/* 271 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void anotherSearch(String searchText, boolean toSubscribe)
/*     */   {
/* 279 */     anotherSearch(new SearchQuery(searchText, toSubscribe));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void anotherSearch(SearchQuery another_sq)
/*     */   {
/* 286 */     this.current_sq = another_sq;
/*     */     
/* 288 */     if (this.activeImpl != null)
/*     */     {
/* 290 */       if ((this.last_actual_sq != null) && (this.last_actual_sq.term.equals(this.current_sq.term)) && (this.last_actual_sq.toSubscribe == this.current_sq.toSubscribe) && (this.last_actual_sq_impl == this.activeImpl))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 297 */         return;
/*     */       }
/*     */       
/* 300 */       this.last_actual_sq = this.current_sq;
/* 301 */       this.last_actual_sq_impl = this.activeImpl;
/*     */       
/* 303 */       this.activeImpl.anotherSearch(this.current_sq);
/*     */       
/* 305 */       ViewTitleInfoManager.refreshTitleInfo(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SearchQuery getCurrentSearch()
/*     */   {
/* 312 */     return this.current_sq;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getTitleInfoProperty(int pid)
/*     */   {
/* 319 */     SearchQuery sq = this.current_sq;
/* 320 */     SearchResultsTabAreaBase impl = this.activeImpl;
/*     */     
/* 322 */     if (pid == 5)
/*     */     {
/* 324 */       if (sq != null)
/*     */       {
/* 326 */         return sq.term;
/*     */       }
/* 328 */     } else if (pid == 0)
/*     */     {
/* 330 */       if (impl != null)
/*     */       {
/* 332 */         int results = impl.getResultCount();
/*     */         
/* 334 */         if (results >= 0)
/*     */         {
/* 336 */           return String.valueOf(results);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 341 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setBusy(boolean busy)
/*     */   {
/* 348 */     if (this.vitalityImage != null)
/*     */     {
/* 350 */       this.vitalityImage.setVisible(busy);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resultsFound()
/*     */   {
/* 357 */     ViewTitleInfoManager.refreshTitleInfo(this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchResultsTabArea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */