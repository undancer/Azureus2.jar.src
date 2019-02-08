/*    */ package com.aelitis.azureus.ui.swt.extlistener;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*    */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*    */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*    */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*    */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*    */ import com.aelitis.azureus.core.messenger.browser.BrowserMessageDispatcher;
/*    */ import com.aelitis.azureus.ui.UIFunctions;
/*    */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*    */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*    */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*    */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*    */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfoContentNetwork;
/*    */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*    */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*    */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*    */ import com.aelitis.azureus.ui.swt.shells.main.MainWindow;
/*    */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3;
/*    */ import com.aelitis.azureus.util.ConstantsVuze;
/*    */ import com.aelitis.azureus.util.ExternalStimulusHandler;
/*    */ import com.aelitis.azureus.util.ExternalStimulusListener;
/*    */ import com.aelitis.azureus.util.JSONUtils;
/*    */ import com.aelitis.azureus.util.MapUtils;
/*    */ import com.aelitis.azureus.util.UrlFilter;
/*    */ import java.io.PrintStream;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.util.Constants;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.ui.swt.donations.DonationWindow;
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
/*    */ public class StimulusRPC
/*    */ {
/*    */   public static void hookListeners(AzureusCore core, final MainWindow mainWindow)
/*    */   {
/* 63 */     ExternalStimulusHandler.addListener(new ExternalStimulusListener() {
/*    */       public boolean receive(String name, Map values) {
/*    */         try {
/* 66 */           if (values == null) {
/* 67 */             return false;
/*    */           }
/*    */           
/* 70 */           if (!name.equals("AZMSG")) {
/* 71 */             return false;
/*    */           }
/*    */           
/* 74 */           Object valueObj = values.get("value");
/* 75 */           if (!(valueObj instanceof String)) {
/* 76 */             return false;
/*    */           }
/*    */           
/* 79 */           String value = (String)valueObj;
/*    */           
/* 81 */           ClientMessageContext context = PlatformMessenger.getClientMessageContext();
/* 82 */           if (context == null) {
/* 83 */             return false;
/*    */           }
/*    */           
/*    */ 
/* 87 */           String[] splitVal = value.split(";", 5);
/* 88 */           if (splitVal.length != 5) {
/* 89 */             return false;
/*    */           }
/* 91 */           String lId = splitVal[2];
/* 92 */           String opId = splitVal[3];
/* 93 */           Map decodedMap = JSONUtils.decodeJSON(splitVal[4]);
/* 94 */           if (decodedMap == null) {
/* 95 */             decodedMap = Collections.EMPTY_MAP;
/*    */           }
/*    */           
/* 98 */           if (opId.equals("open-url")) {
/* 99 */             String url = MapUtils.getMapString(decodedMap, "url", null);
/* :0 */             if (!decodedMap.containsKey("target")) {
/* :1 */               context.debug("no target for url: " + url);
/* :2 */             } else if (UrlFilter.getInstance().urlIsBlocked(url)) {
/* :3 */               context.debug("url blocked: " + url);
/* :4 */             } else if (!UrlFilter.getInstance().urlCanRPC(url)) {
/* :5 */               context.debug("url not in whitelistL " + url);
/*    */             }
/*    */             else {
/* :8 */               UIFunctions functions = UIFunctionsManager.getUIFunctions();
/* :9 */               if (functions != null) {
/* ;0 */                 functions.bringToFront();
/*    */               }
/*    */               
/*    */ 
/*    */ 
/* ;5 */               BrowserMessageDispatcher dispatcher = context.getDispatcher();
/* ;6 */               if (dispatcher != null) {
/* ;7 */                 dispatcher.dispatch(new BrowserMessage(lId, opId, decodedMap));
/*    */               } else {
/* ;9 */                 context.debug("No dispatcher for StimulusRPC" + opId);
/*    */               }
/*    */               
/* <2 */               return true;
/*    */             }
/*    */           }
/* <5 */           else if (opId.equals("load-torrent")) {
/* <6 */             if (decodedMap.containsKey("b64")) {
/* <7 */               String b64 = MapUtils.getMapString(decodedMap, "b64", null);
/* <8 */               return TorrentListener.loadTorrentByB64(this.val$core, b64); }
/* <9 */             if (decodedMap.containsKey("url")) {
/* =0 */               String url = MapUtils.getMapString(decodedMap, "url", null);
/*    */               
/* =2 */               boolean blocked = UrlFilter.getInstance().urlIsBlocked(url);
/*    */               
/* =4 */               if (blocked) {
/* =5 */                 Debug.out("stopped loading torrent URL because it's not in whitelist");
/* =6 */                 return false;
/*    */               }
/*    */               
/* =9 */               boolean playNow = MapUtils.getMapBoolean(decodedMap, "play-now", false);
/*    */               
/* >1 */               boolean playPrepare = MapUtils.getMapBoolean(decodedMap, "play-prepare", false);
/*    */               
/* >3 */               boolean bringToFront = MapUtils.getMapBoolean(decodedMap, "bring-to-front", true);
/*    */               
/*    */ 
/*    */ 
/*    */ 
/* >8 */               long contentNetworkID = MapUtils.getMapLong(decodedMap, "content-network", ConstantsVuze.getDefaultContentNetwork().getID());
/*    */               
/* ?0 */               ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetwork(contentNetworkID);
/* ?1 */               if (cn == null) {
/* ?2 */                 cn = ConstantsVuze.getDefaultContentNetwork();
/*    */               }
/*    */               
/* ?5 */               DownloadUrlInfo dlInfo = new DownloadUrlInfoContentNetwork(url, cn);
/*    */               
/* ?7 */               dlInfo.setReferer(MapUtils.getMapString(decodedMap, "referer", null));
/*    */               
/*    */ 
/* @0 */               TorrentUIUtilsV3.loadTorrent(dlInfo, playNow, playPrepare, bringToFront);
/*    */               
/*    */ 
/* @3 */               return true;
/*    */             }
/* @5 */           } else { if (opId.equals("is-ready"))
/*    */             {
/*    */ 
/* @8 */               return mainWindow.isReady(); }
/* @9 */             if (opId.equals("is-version-ge")) {
/* A0 */               if (decodedMap.containsKey("version")) {
/* A1 */                 String id = MapUtils.getMapString(decodedMap, "id", "client");
/* A2 */                 String version = MapUtils.getMapString(decodedMap, "version", "");
/* A3 */                 if (id.equals("client")) {
/* A4 */                   return Constants.compareVersions("5.7.6.0", version) >= 0;
/*    */                 }
/*    */               }
/*    */               
/*    */ 
/* A9 */               return false;
/*    */             }
/* B1 */             if (opId.equals("is-active-tab")) {
/* B2 */               if (decodedMap.containsKey("tab")) {
/* B3 */                 String tabID = MapUtils.getMapString(decodedMap, "tab", "");
/* B4 */                 if (tabID.length() > 0)
/*    */                 {
/* B6 */                   MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* B7 */                   MdiEntry entry = mdi.getCurrentEntry();
/* B8 */                   if (entry != null) {
/* B9 */                     return entry.getId().equals(tabID);
/*    */                   }
/*    */                 }
/*    */               }
/*    */               
/* C4 */               return false;
/*    */             }
/* C6 */             if ("config".equals(lId)) {
/* C7 */               if ("is-new-install".equals(opId))
/* C8 */                 return COConfigurationManager.isNewInstall();
/* C9 */               if ("check-for-updates".equals(opId)) {
/* D0 */                 ConfigListener.checkForUpdates();
/* D1 */                 return true; }
/* D2 */               if ("log-diags".equals(opId)) {
/* D3 */                 ConfigListener.logDiagnostics();
/* D4 */                 return true;
/*    */               }
/* D6 */             } else if ("display".equals(lId)) {
/* D7 */               if ("refresh-browser".equals(opId)) {
/* D8 */                 DisplayListener.refreshTab(MapUtils.getMapString(decodedMap, "browser-id", ""));
/* D9 */               } else if ("switch-to-tab".equals(opId)) {
/* E0 */                 DisplayListener.switchToTab(MapUtils.getMapString(decodedMap, "target", ""), MapUtils.getMapString(decodedMap, "source-ref", null));
/*    */               }
/*    */               
/*    */ 
/*    */             }
/* E5 */             else if ("show-donation-window".equals(lId)) {
/* E6 */               DonationWindow.open(true, MapUtils.getMapString(decodedMap, "source-ref", "SRPC"));
/*    */             }
/*    */           }
/*    */           
/*    */ 
/* F1 */           if (System.getProperty("browser.route.all.external.stimuli.for.testing", "false").equalsIgnoreCase("true"))
/*    */           {
/*    */ 
/*    */ 
/* F5 */             BrowserMessageDispatcher dispatcher = context.getDispatcher();
/* F6 */             if (dispatcher != null) {
/* F7 */               dispatcher.dispatch(new BrowserMessage(lId, opId, decodedMap));
/*    */             }
/*    */           }
/*    */           else {
/* G1 */             System.err.println("Unhandled external stimulus: " + value);
/*    */           }
/*    */         } catch (Exception e) {
/* G4 */           Debug.out(e);
/*    */         }
/* G6 */         return false;
/*    */       }
/*    */       
/*    */       public int query(String name, Map values) {
/* H0 */         return Integer.MIN_VALUE;
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/extlistener/StimulusRPC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */