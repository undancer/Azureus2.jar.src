/*    */ package com.aelitis.azureus.ui.swt.views;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*    */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*    */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*    */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*    */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*    */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*    */ import com.aelitis.azureus.util.JSONUtils;
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.util.FileUtil;
/*    */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*    */ import org.gudy.azureus2.core3.util.SystemTime;
/*    */ import org.gudy.azureus2.core3.util.TimerEvent;
/*    */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.utils.Utilities;
/*    */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*    */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*    */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ViewTitleInfoBetaP
/*    */   implements ViewTitleInfo
/*    */ {
/*    */   private static final String PARAM_LASTPOSTCOUNT = "betablog.numPosts";
/* 42 */   long numNew = 0L;
/*    */   
/* 44 */   private long postCount = 0L;
/*    */   
/*    */   public ViewTitleInfoBetaP()
/*    */   {
/* 48 */     SimpleTimer.addEvent("devblog", SystemTime.getCurrentTime(), new TimerEventPerformer()
/*    */     {
/*    */       public void perform(TimerEvent event) {
/* 51 */         long lastPostCount = COConfigurationManager.getLongParameter("betablog.numPosts", 0L);
/*    */         
/* 53 */         PluginInterface pi = PluginInitializer.getDefaultInterface();
/*    */         try {
/* 55 */           ResourceDownloader rd = pi.getUtilities().getResourceDownloaderFactory().create(new URL("http://api.tumblr.com/v2/blog/devblog.vuze.com/info?api_key=C5a8UGiSwPflOrVecjcvwGiOWVsLFF22pC9SgUIKSuQfjAvDAY"));
/*    */           
/*    */ 
/* 58 */           InputStream download = rd.download();
/* 59 */           Map json = JSONUtils.decodeJSON(FileUtil.readInputStreamAsString(download, 65535));
/*    */           
/* 61 */           Map mapResponse = MapUtils.getMapMap(json, "response", null);
/* 62 */           if (mapResponse != null) {
/* 63 */             Map mapBlog = MapUtils.getMapMap(mapResponse, "blog", null);
/* 64 */             if (mapBlog != null) {
/* 65 */               ViewTitleInfoBetaP.this.postCount = MapUtils.getMapLong(mapBlog, "posts", 0L);
/* 66 */               ViewTitleInfoBetaP.this.numNew = (ViewTitleInfoBetaP.this.postCount - lastPostCount);
/* 67 */               ViewTitleInfoManager.refreshTitleInfo(ViewTitleInfoBetaP.this);
/*    */             }
/*    */           }
/*    */         }
/*    */         catch (Exception e) {}
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public Object getTitleInfoProperty(int propertyID)
/*    */   {
/* 78 */     if ((propertyID == 0) && (this.numNew > 0L)) {
/* 79 */       return "" + this.numNew;
/*    */     }
/* 81 */     return null;
/*    */   }
/*    */   
/*    */   public void clearIndicator() {
/* 85 */     COConfigurationManager.setParameter("betablog.numPosts", this.postCount);
/* 86 */     this.numNew = 0L;
/*    */   }
/*    */   
/*    */   public static void setupSidebarEntry(MultipleDocumentInterface mdi) {
/* 90 */     mdi.registerEntry("BetaProgramme", new MdiEntryCreationListener()
/*    */     {
/*    */       public MdiEntry createMDiEntry(String id)
/*    */       {
/* 94 */         final ViewTitleInfoBetaP viewTitleInfo = new ViewTitleInfoBetaP();
/*    */         
/* 96 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.vuze", "BetaProgramme", "main.area.beta", "{Sidebar.beta.title}", viewTitleInfo, null, true, "");
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* :2 */         entry.setImageLeftID("image.sidebar.beta");
/*    */         
/* :4 */         entry.addListener(new MdiCloseListener() {
/*    */           public void mdiEntryClosed(MdiEntry entry, boolean userClosed) {
/* :6 */             viewTitleInfo.clearIndicator();
/*    */           }
/*    */           
/* :9 */         });
/* ;0 */         return entry;
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewTitleInfoBetaP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */