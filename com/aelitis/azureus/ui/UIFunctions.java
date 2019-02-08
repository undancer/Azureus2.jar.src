/*    */ package com.aelitis.azureus.ui;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*    */ import com.aelitis.azureus.core.tag.Tag;
/*    */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*    */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public abstract interface UIFunctions
/*    */   extends AzureusCoreComponent
/*    */ {
/* 38 */   public static final String MAIN_WINDOW_NAME = System.getProperty("azureus.window.title", Constants.APP_NAME + " Bittorrent Client");
/* 39 */   public static final String MAIN_WINDOW_NAME_PLUS = System.getProperty("azureus.window.title", Constants.APP_PLUS_NAME + " Bittorrent Client");
/*    */   public static final int STATUSICON_NONE = 0;
/*    */   public static final int STATUSICON_WARNING = 1;
/*    */   public static final int STATUSICON_ERROR = 2;
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public static final int VIEW_MYTORRENTS = 8;
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public static final int VIEW_CONFIG = 4;
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public static final int VIEW_DM_DETAILS = 5;
/*    */   public static final int ACTION_FULL_UPDATE = 1;
/*    */   public static final int ACTION_UPDATE_RESTART_REQUEST = 2;
/*    */   public static final int VS_TRAY_ONLY = 1;
/*    */   public static final int VS_MINIMIZED_TO_TRAY = 2;
/*    */   public static final int VS_MINIMIZED = 3;
/*    */   public static final int VS_ACTIVE = 4;
/*    */   public static final String OTO_DEFAULT_TO_STOPPED = "defaultStopped";
/*    */   public static final boolean OTO_DEFAULT_TO_STOPPED_DEFAULT = false;
/*    */   public static final String OTO_FORCE_OPEN = "forceOpen";
/*    */   public static final boolean OTO_FORCE_OPEN_DEFAULT = false;
/*    */   public static final String OTO_SILENT = "silent";
/*    */   public static final boolean OTO_SILENT_DEFAULT = false;
/*    */   public static final String OTO_HIDE_ERRORS = "hideErrors";
/*    */   public static final boolean OTO_HIDE_ERRORS_DEFAULT = false;
/*    */   
/*    */   public abstract int getUIType();
/*    */   
/*    */   public abstract void bringToFront();
/*    */   
/*    */   public abstract void bringToFront(boolean paramBoolean);
/*    */   
/*    */   public abstract int getVisibilityState();
/*    */   
/*    */   public abstract void refreshLanguage();
/*    */   
/*    */   public abstract void refreshIconBar();
/*    */   
/*    */   public abstract void setStatusText(String paramString);
/*    */   
/*    */   public abstract void setStatusText(int paramInt, String paramString, UIStatusTextClickListener paramUIStatusTextClickListener);
/*    */   
/*    */   public abstract boolean dispose(boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract boolean viewURL(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract boolean viewURL(String paramString1, String paramString2, double paramDouble1, double paramDouble2, boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract void viewURL(String paramString1, String paramString2, String paramString3);
/*    */   
/*    */   public abstract UIFunctionsUserPrompter getUserPrompter(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt);
/*    */   
/*    */   public abstract void promptUser(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt1, String paramString3, String paramString4, boolean paramBoolean, int paramInt2, UserPrompterResultListener paramUserPrompterResultListener);
/*    */   
/*    */   public abstract UIUpdater getUIUpdater();
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract void openView(int paramInt, Object paramObject);
/*    */   
/*    */   public abstract void doSearch(String paramString);
/*    */   
/*    */   public abstract void doSearch(String paramString, boolean paramBoolean);
/*    */   
/*    */   public abstract void installPlugin(String paramString1, String paramString2, actionListener paramactionListener);
/*    */   
/*    */   public abstract void performAction(int paramInt, Object paramObject, actionListener paramactionListener);
/*    */   
/*    */   public abstract MultipleDocumentInterface getMDI();
/*    */   
/*    */   public abstract void forceNotify(int paramInt1, String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject, int paramInt2);
/*    */   
/*    */   public abstract void runOnUIThread(int paramInt, Runnable paramRunnable);
/*    */   
/*    */   public abstract boolean isProgramInstalled(String paramString1, String paramString2);
/*    */   
/*    */   public abstract void openRemotePairingWindow();
/*    */   
/*    */   public abstract void playOrStreamDataSource(Object paramObject, String paramString, boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract boolean addTorrentWithOptions(boolean paramBoolean, TorrentOpenOptions paramTorrentOpenOptions);
/*    */   
/*    */   public abstract boolean addTorrentWithOptions(TorrentOpenOptions paramTorrentOpenOptions, Map<String, Object> paramMap);
/*    */   
/*    */   public abstract void showErrorMessage(String paramString1, String paramString2, String[] paramArrayOfString);
/*    */   
/*    */   public abstract void showCreateTagDialog(TagReturner paramTagReturner);
/*    */   
/*    */   public abstract int adjustPXForDPI(int paramInt);
/*    */   
/*    */   public static abstract interface TagReturner
/*    */   {
/*    */     public abstract void returnedTags(Tag[] paramArrayOfTag);
/*    */   }
/*    */   
/*    */   public static abstract interface actionListener
/*    */   {
/*    */     public abstract void actionComplete(Object paramObject);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/UIFunctions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */