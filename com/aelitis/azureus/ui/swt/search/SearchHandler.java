/*     */ package com.aelitis.azureus.ui.swt.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinViewManager;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateTab;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.webplugin.WebPlugin;
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
/*     */ public class SearchHandler
/*     */ {
/*     */   private static final class ViewTitleInfoImplementation
/*     */     implements ViewTitleInfo, ObfusticateTab
/*     */   {
/*     */     public Object getTitleInfoProperty(int propertyID)
/*     */     {
/*  59 */       if (propertyID == 5) {
/*  60 */         SearchResultsTabArea searchClass = (SearchResultsTabArea)SkinViewManager.getByClass(SearchResultsTabArea.class);
/*  61 */         if (searchClass != null) {
/*  62 */           SearchResultsTabArea.SearchQuery sq = searchClass.getCurrentSearch();
/*     */           
/*  64 */           if (sq != null)
/*     */           {
/*  66 */             return sq.term;
/*     */           }
/*     */         }
/*     */       }
/*  70 */       return null;
/*     */     }
/*     */     
/*     */     public String getObfusticatedHeader() {
/*  74 */       return "";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void handleSearch(String sSearchText, boolean toSubscribe)
/*     */   {
/*  83 */     if (!toSubscribe)
/*     */     {
/*     */       try
/*     */       {
/*  87 */         if ((COConfigurationManager.getBooleanParameter("rcm.overall.enabled", true)) && (COConfigurationManager.getBooleanParameter("Plugin.aercm.rcm.search.enable", false)) && (AzureusCoreFactory.isCoreRunning()))
/*     */         {
/*     */ 
/*     */ 
/*  91 */           PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("aercm");
/*     */           
/*  93 */           if ((pi != null) && (pi.getPluginState().isOperational())) if (pi.getIPC().canInvoke("lookupByExpression", new Object[] { "" }))
/*     */             {
/*     */ 
/*     */ 
/*  97 */               pi.getIPC().invoke("lookupByExpression", new Object[] { sSearchText });
/*     */             }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 102 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 106 */     boolean internal_search = !COConfigurationManager.getBooleanParameter("browser.external.search");
/*     */     
/* 108 */     if (internal_search)
/*     */     {
/* 110 */       SearchResultsTabArea.SearchQuery sq = new SearchResultsTabArea.SearchQuery(sSearchText, toSubscribe);
/*     */       
/*     */ 
/* 113 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 114 */       String id = "Search";
/* 115 */       MdiEntry existingEntry = mdi.getEntry(id);
/* 116 */       if ((existingEntry != null) && (existingEntry.isAdded())) {
/* 117 */         SearchResultsTabArea searchClass = (SearchResultsTabArea)SkinViewManager.getByClass(SearchResultsTabArea.class);
/* 118 */         if (searchClass != null) {
/* 119 */           searchClass.anotherSearch(sSearchText, toSubscribe);
/*     */         }
/* 121 */         existingEntry.setDatasource(sq);
/* 122 */         mdi.showEntry(existingEntry);
/* 123 */         return;
/*     */       }
/*     */       
/* 126 */       MdiEntry entry = mdi.createEntryFromSkinRef("header.discovery", id, "main.area.searchresultstab", sSearchText, null, sq, true, "");
/*     */       
/*     */ 
/* 129 */       if (entry != null) {
/* 130 */         entry.setImageLeftID("image.sidebar.search");
/* 131 */         entry.setDatasource(sq);
/* 132 */         entry.setViewTitleInfo(new ViewTitleInfoImplementation(null));
/*     */       }
/*     */       
/* 135 */       mdi.showEntryByID(id);
/*     */     }
/*     */     else
/*     */     {
/* 139 */       PluginInterface xmweb_ui = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("xmwebui");
/*     */       
/* 141 */       if ((xmweb_ui == null) || (!xmweb_ui.getPluginState().isOperational()))
/*     */       {
/* 143 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */         
/* 145 */         MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("external.browser.failed"), MessageText.getString("xmwebui.required"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 150 */         mb.setParent(uiFunctions.getMainShell());
/*     */         
/* 152 */         mb.open(null);
/*     */       }
/*     */       else
/*     */       {
/* 156 */         WebPlugin wp = (WebPlugin)xmweb_ui.getPlugin();
/*     */         
/* 158 */         String remui = wp.getProtocol().toLowerCase(Locale.US) + "://127.0.0.1:" + wp.getPort() + "/";
/*     */         
/* 160 */         String test_url = ConstantsVuze.getDefaultContentNetwork().getServiceURL(2, new Object[] { "", Boolean.valueOf(false) });
/*     */         
/* 162 */         int pos = test_url.indexOf('?');
/*     */         
/* 164 */         String mode = xmweb_ui.getUtilities().getFeatureManager().isFeatureInstalled("core") ? "plus" : "trial";
/*     */         
/* 166 */         String search_url = test_url.substring(0, pos + 1) + "q=" + UrlUtils.encode(sSearchText) + "&" + "mode=" + mode + "&" + "search_source=" + UrlUtils.encode(remui);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */         Utils.launch(search_url);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */