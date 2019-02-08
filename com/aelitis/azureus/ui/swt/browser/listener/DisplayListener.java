/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.AbstractBrowserMessageListener;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfoContentNetwork;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentV3;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.shells.BrowserWindow;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_BurnFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.donations.DonationWindow;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DisplayListener
/*     */   extends AbstractBrowserMessageListener
/*     */ {
/*     */   public static final String DEFAULT_LISTENER_ID = "display";
/*     */   public static final String OP_COPY_TO_CLIPBOARD = "copy-text";
/*     */   public static final String OP_OPEN_URL = "open-url";
/*     */   public static final String OP_RESET_URL = "reset-url";
/*     */   public static final String OP_SEND_EMAIL = "send-email";
/*     */   public static final String OP_IRC_SUPPORT = "irc-support";
/*     */   public static final String OP_BRING_TO_FRONT = "bring-to-front";
/*     */   public static final String OP_SWITCH_TO_TAB = "switch-to-tab";
/*     */   public static final String OP_REFRESH_TAB = "refresh-browser";
/*     */   public static final String VZ_NON_ACTIVE = "vz-non-active";
/*     */   public static final String OP_SET_SELECTED_CONTENT = "set-selected-content";
/*     */   public static final String OP_GET_SELECTED_CONTENT = "get-selected-content";
/*     */   public static final String OP_SHOW_DONATION_WINDOW = "show-donation-window";
/*     */   public static final String OP_OPEN_SEARCH = "open-search";
/*     */   public static final String OP_REGISTER = "open-register";
/*     */   private BrowserWrapper browser;
/*     */   
/*     */   public DisplayListener(String id, BrowserWrapper browser)
/*     */   {
/* 101 */     super(id);
/* 102 */     this.browser = browser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DisplayListener(BrowserWrapper browser)
/*     */   {
/* 109 */     this("display", browser);
/*     */   }
/*     */   
/*     */   public void handleMessage(BrowserMessage message) {
/* 113 */     String opid = message.getOperationId();
/*     */     
/* 115 */     if ("copy-text".equals(opid)) {
/* 116 */       Map decodedMap = message.getDecodedMap();
/* 117 */       copyToClipboard(MapUtils.getMapString(decodedMap, "text", ""));
/* 118 */     } else if ("open-url".equals(opid)) {
/* 119 */       Map decodedMap = message.getDecodedMap();
/* 120 */       String target = MapUtils.getMapString(decodedMap, "target", null);
/* 121 */       if (((target == null) || ("_blank".equals(target))) && (!decodedMap.containsKey("width")))
/*     */       {
/* 123 */         launchUrl(MapUtils.getMapString(decodedMap, "url", null), MapUtils.getMapBoolean(decodedMap, "append-suffix", false));
/*     */       }
/*     */       else {
/* 126 */         String ref = message.getReferer();
/* 127 */         if ((target != null) && (target.equals("browse")) && (ref != null))
/*     */         {
/*     */ 
/* 130 */           ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetworkForURL(ref);
/*     */           
/* 132 */           if (cn != null) {
/* 133 */             target = ContentNetworkUtils.getTarget(cn);
/* 134 */             System.err.println("TARGET REWRITTEN TO " + target);
/*     */           }
/*     */         }
/* 137 */         message.setCompleteDelayed(true);
/* 138 */         showBrowser(MapUtils.getMapString(decodedMap, "url", null), target, MapUtils.getMapInt(decodedMap, "width", 0), MapUtils.getMapInt(decodedMap, "height", 0), MapUtils.getMapBoolean(decodedMap, "resizable", false), message, MapUtils.getMapString(decodedMap, "source-ref", ref));
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 144 */     else if ("reset-url".equals(opid)) {
/* 145 */       resetURL();
/* 146 */     } else if ("send-email".equals(opid)) {
/* 147 */       Map decodedMap = message.getDecodedMap();
/*     */       
/* 149 */       String to = MapUtils.getMapString(decodedMap, "to", "");
/* 150 */       String subject = MapUtils.getMapString(decodedMap, "subject", "");
/*     */       
/* 152 */       String body = MapUtils.getMapString(decodedMap, "body", null);
/*     */       
/* 154 */       sendEmail(to, subject, body);
/*     */     }
/* 156 */     else if ("irc-support".equals(opid)) {
/* 157 */       Map decodedMap = message.getDecodedMap();
/* 158 */       openIrc(null, MapUtils.getMapString(decodedMap, "channel", ""), MapUtils.getMapString(decodedMap, "user", ""));
/*     */     }
/* 160 */     else if ("bring-to-front".equals(opid)) {
/* 161 */       bringToFront();
/* 162 */     } else if ("switch-to-tab".equals(opid)) {
/* 163 */       Map decodedMap = message.getDecodedMap();
/* 164 */       switchToTab(MapUtils.getMapString(decodedMap, "target", ""), MapUtils.getMapString(decodedMap, "source-ref", message.getReferer()));
/*     */     }
/* 166 */     else if ("refresh-browser".equals(opid)) {
/* 167 */       Map decodedMap = message.getDecodedMap();
/* 168 */       refreshTab(MapUtils.getMapString(decodedMap, "browser-id", ""));
/* 169 */     } else if ("set-selected-content".equals(opid)) {
/* 170 */       Map decodedMap = message.getDecodedMap();
/* 171 */       if (decodedMap != null) {
/* 172 */         setSelectedContent(message, decodedMap);
/*     */       }
/* 174 */     } else if ("get-selected-content".equals(opid)) {
/* 175 */       Map decodedMap = message.getDecodedMap();
/* 176 */       if (decodedMap != null) {
/* 177 */         getSelectedContent(message, decodedMap);
/*     */       }
/* 179 */     } else if ("show-donation-window".equals(opid)) {
/* 180 */       Map decodedMap = message.getDecodedMap();
/* 181 */       DonationWindow.open(true, MapUtils.getMapString(decodedMap, "source-ref", "RPC"));
/*     */     }
/* 183 */     else if ("open-search".equals(opid)) {
/* 184 */       Map decodedMap = message.getDecodedMap();
/* 185 */       UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 186 */       if (uif != null) {
/* 187 */         uif.doSearch(MapUtils.getMapString(decodedMap, "search-text", ""));
/*     */       }
/* 189 */     } else if ("open-register".equals(opid)) {
/* 190 */       FeatureManagerUI.openLicenceEntryWindow(false, null);
/*     */     } else {
/* 192 */       throw new IllegalArgumentException("Unknown operation: " + opid);
/*     */     }
/*     */   }
/*     */   
/*     */   private void getSelectedContent(BrowserMessage message, Map decodedMap) {
/* 197 */     String callback = MapUtils.getMapString(decodedMap, "callback", null);
/* 198 */     if (callback == null) {
/* 199 */       return;
/*     */     }
/*     */     
/* 202 */     List<Map> list = new ArrayList();
/*     */     
/* 204 */     DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/* 205 */     if (dms != null) {
/* 206 */       for (DownloadManager dm : dms) {
/* 207 */         if (dm != null) {
/* 208 */           Map<String, Object> mapDM = new HashMap();
/* 209 */           TOTorrent torrent = dm.getTorrent();
/* 210 */           if ((torrent != null) && (!TorrentUtils.isReallyPrivate(torrent)))
/*     */           {
/*     */             try
/*     */             {
/* 214 */               Map torrent_map = torrent.serialiseToMap();
/* 215 */               TOTorrent torrent_to_send = TOTorrentFactory.deserialiseFromMap(torrent_map);
/* 216 */               Map vuze_map = (Map)torrent_map.get("vuze");
/*     */               
/* 218 */               torrent_to_send.removeAdditionalProperties();
/* 219 */               torrent_map = torrent_to_send.serialiseToMap();
/* 220 */               if (vuze_map != null) {
/* 221 */                 torrent_map.put("vuze", vuze_map);
/*     */               }
/*     */               
/* 224 */               byte[] encode = BEncoder.encode(torrent_map);
/*     */               
/* 226 */               mapDM.put("name", PlatformTorrentUtils.getContentTitle2(dm));
/* 227 */               mapDM.put("torrent", Base32.encode(encode));
/*     */               
/* 229 */               list.add(mapDM);
/*     */             } catch (Throwable t) {
/* 231 */               Debug.out(t);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 238 */     if ((list.size() > 0) && (this.context != null)) {
/* 239 */       this.context.executeInBrowser(callback + "(" + JSONUtils.encodeToJSON(list) + ")");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setSelectedContent(BrowserMessage message, Map decodedMap)
/*     */   {
/* 250 */     String hash = MapUtils.getMapString(decodedMap, "torrent-hash", null);
/* 251 */     String displayName = MapUtils.getMapString(decodedMap, "display-name", null);
/* 252 */     String dlURL = MapUtils.getMapString(decodedMap, "download-url", null);
/* 253 */     String referer = MapUtils.getMapString(decodedMap, "referer", "displaylistener");
/*     */     
/*     */ 
/* 256 */     if ((hash == null) && (dlURL == null)) {
/* 257 */       SelectedContentManager.changeCurrentlySelectedContent(referer, null);
/*     */     }
/*     */     
/* 260 */     String callback = MapUtils.getMapString(decodedMap, "callback", null);
/* 261 */     if ((callback != null) && (this.context != null)) {
/* 262 */       DownloadUrlInfoSWT dlInfo = new DownloadUrlInfoSWT(this.context, callback, hash);
/*     */       
/* 264 */       boolean canPlay = MapUtils.getMapBoolean(decodedMap, "can-play", false);
/* 265 */       boolean isVuzeContent = MapUtils.getMapBoolean(decodedMap, "is-vuze-content", true);
/*     */       
/*     */ 
/* 268 */       SelectedContentV3 content = new SelectedContentV3(hash, displayName, isVuzeContent, canPlay);
/*     */       
/* 270 */       content.setDownloadInfo(dlInfo);
/*     */       
/* 272 */       SelectedContentManager.changeCurrentlySelectedContent(referer, new ISelectedContent[] { content });
/*     */       
/*     */ 
/*     */ 
/* 276 */       return;
/*     */     }
/*     */     
/* 279 */     if ((displayName != null) && (this.context != null)) {
/* 280 */       String dlReferer = MapUtils.getMapString(decodedMap, "download-referer", null);
/*     */       
/* 282 */       String dlCookies = MapUtils.getMapString(decodedMap, "download-cookies", null);
/*     */       
/* 284 */       Map dlHeader = MapUtils.getMapMap(decodedMap, "download-header", null);
/*     */       
/* 286 */       boolean canPlay = MapUtils.getMapBoolean(decodedMap, "can-play", false);
/* 287 */       boolean isVuzeContent = MapUtils.getMapBoolean(decodedMap, "is-vuze-content", true);
/*     */       
/* 289 */       SelectedContentV3 content = new SelectedContentV3(hash, displayName, isVuzeContent, canPlay);
/*     */       
/* 291 */       content.setThumbURL(MapUtils.getMapString(decodedMap, "thumbnail.url", null));
/*     */       
/*     */ 
/* 294 */       DownloadUrlInfo dlInfo = new DownloadUrlInfoContentNetwork(dlURL, ContentNetworkManagerFactory.getSingleton().getContentNetwork(this.context.getContentNetworkID()));
/*     */       
/*     */ 
/* 297 */       dlInfo.setReferer(dlReferer);
/* 298 */       if (dlCookies != null) {
/* 299 */         if (dlHeader == null) {
/* 300 */           dlHeader = new HashMap();
/*     */         }
/* 302 */         dlHeader.put("Cookie", dlCookies);
/*     */       }
/* 304 */       dlInfo.setRequestProperties(dlHeader);
/*     */       
/* 306 */       String subID = MapUtils.getMapString(decodedMap, "subscription-id", null);
/* 307 */       String subresID = MapUtils.getMapString(decodedMap, "subscription-result-id", null);
/*     */       
/*     */ 
/* 310 */       if ((subID != null) && (subresID != null)) {
/* 311 */         Subscription subs = SubscriptionManagerFactory.getSingleton().getSubscriptionByID(subID);
/*     */         
/* 313 */         if (subs != null) {
/* 314 */           subs.addPotentialAssociation(subresID, dlURL);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 320 */       dlInfo.setAdditionalProperties(decodedMap);
/*     */       
/* 322 */       content.setDownloadInfo(dlInfo);
/*     */       
/* 324 */       SelectedContentManager.changeCurrentlySelectedContent(referer, new ISelectedContent[] { content });
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 329 */       SelectedContentManager.changeCurrentlySelectedContent(referer, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void switchToTab(String tabID, String sourceRef)
/*     */   {
/* 339 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 340 */     if (mdi == null) {
/* 341 */       return;
/*     */     }
/* 343 */     if ((sourceRef != null) && (
/* 344 */       ("Plus".equals(tabID)) || ("BurnInfo".equals(tabID))))
/*     */     {
/* 346 */       Pattern pattern = Pattern.compile("http.*//[^/]+/([^.]+)");
/* 347 */       Matcher matcher = pattern.matcher(sourceRef);
/*     */       String sourceRef2;
/*     */       String sourceRef2;
/* 350 */       if (matcher.find()) {
/* 351 */         sourceRef2 = matcher.group(1);
/*     */       } else {
/* 353 */         sourceRef2 = sourceRef;
/*     */       }
/*     */       
/* 356 */       if ("Plus".equals(tabID)) {
/* 357 */         SBC_PlusFTUX.setSourceRef(sourceRef2);
/*     */       } else {
/* 359 */         SBC_BurnFTUX.setSourceRef(sourceRef2);
/*     */       }
/*     */     }
/*     */     
/* 363 */     mdi.showEntryByID(tabID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void bringToFront()
/*     */   {
/* 370 */     UIFunctions functions = UIFunctionsManager.getUIFunctions();
/* 371 */     if (functions != null) {
/* 372 */       functions.bringToFront();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void resetURL()
/*     */   {
/* 380 */     if ((this.browser == null) || (this.browser.isDisposed())) {
/* 381 */       return;
/*     */     }
/*     */     
/* 384 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 386 */         if ((DisplayListener.this.browser == null) || (DisplayListener.this.browser.isDisposed())) {
/* 387 */           return;
/*     */         }
/*     */         
/* 390 */         String sURL = (String)DisplayListener.this.browser.getData("StartURL");
/* 391 */         DisplayListener.this.context.debug("reset " + sURL);
/* 392 */         if ((sURL != null) && (sURL.length() > 0))
/*     */         {
/* 394 */           String sRand = "rand=" + SystemTime.getCurrentTime();
/* 395 */           String startURLUnique; String startURLUnique; if (sURL.indexOf("rand=") > 0) {
/* 396 */             startURLUnique = sURL.replaceAll("rand=[0-9.]+", sRand); } else { String startURLUnique;
/* 397 */             if (sURL.indexOf('?') > 0) {
/* 398 */               startURLUnique = sURL + "&" + sRand;
/*     */             } else
/* 400 */               startURLUnique = sURL + "?" + sRand;
/*     */           }
/* 402 */           DisplayListener.this.browser.setUrl(startURLUnique);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void refreshTab(String tabID)
/*     */   {
/* 414 */     if ((null == tabID) || (tabID.length() < 1)) {
/* 415 */       return;
/*     */     }
/*     */     
/* 418 */     SWTSkin skin = SWTSkinFactory.getInstance();
/*     */     
/*     */ 
/*     */     Iterator iterator;
/*     */     
/* 423 */     if ("vz-non-active".equals(tabID))
/*     */     {
/*     */ 
/* 426 */       List browserViewIDs = new ArrayList();
/*     */       
/*     */ 
/*     */ 
/* 430 */       for (iterator = browserViewIDs.iterator(); iterator.hasNext();) {
/* 431 */         refreshBrowser(iterator.next().toString());
/*     */       }
/*     */     }
/*     */     else {
/* 435 */       refreshBrowser(tabID);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void refreshBrowser(String browserID) {
/* 440 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 442 */         UIFunctionsSWT uiSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 443 */         MultipleDocumentInterfaceSWT mdi = uiSWT == null ? null : uiSWT.getMDISWT();
/*     */         
/* 445 */         BaseMdiEntry entry = mdi == null ? null : (BaseMdiEntry)mdi.getEntrySWT(this.val$browserID);
/*     */         
/* 447 */         if (entry != null) {
/* 448 */           SWTSkinObjectBrowser soBrowser = SWTSkinUtils.findBrowserSO(entry.getSkinObject());
/*     */           
/* 450 */           if (soBrowser != null) {
/* 451 */             soBrowser.refresh();
/* 452 */             return;
/*     */           }
/*     */         }
/* 455 */         SWTSkin skin = SWTSkinFactory.getInstance();
/* 456 */         SWTSkinObject skinObject = skin.getSkinObject(this.val$browserID);
/* 457 */         if ((skinObject instanceof SWTSkinObjectBrowser)) {
/* 458 */           BrowserWrapper browser = ((SWTSkinObjectBrowser)skinObject).getBrowser();
/* 459 */           if ((null != browser) && (!browser.isDisposed())) {
/* 460 */             browser.refresh();
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void launchUrl(String url, boolean appendSuffix) {
/* 468 */     ContentNetwork cn = ContentNetworkUtils.getContentNetworkFromTarget(null);
/* 469 */     if (url.startsWith("/")) {
/* 470 */       url = cn.getExternalSiteRelativeURL(url, appendSuffix);
/* 471 */     } else if (appendSuffix) {
/* 472 */       url = cn.appendURLSuffix(url, false, true);
/*     */     }
/* 474 */     if ((url.startsWith("http://")) || (url.startsWith("https://")) || (url.startsWith("mailto:")))
/*     */     {
/* 476 */       Utils.launch(url);
/*     */     }
/*     */   }
/*     */   
/*     */   private void sendEmail(String to, String subject, String body)
/*     */   {
/* 482 */     String url = "mailto:" + to + "?subject=" + UrlUtils.encode(subject);
/*     */     
/* 484 */     if (body != null) {
/* 485 */       url = url + "&body=" + UrlUtils.encode(body);
/*     */     }
/* 487 */     Utils.launch(url);
/*     */   }
/*     */   
/*     */   private void copyToClipboard(final String text) {
/* 491 */     if ((this.browser == null) || (this.browser.isDisposed())) {
/* 492 */       return;
/*     */     }
/*     */     
/* 495 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 497 */         if ((DisplayListener.this.browser == null) || (DisplayListener.this.browser.isDisposed())) {
/* 498 */           return;
/*     */         }
/*     */         
/* 501 */         Clipboard cb = new Clipboard(DisplayListener.this.browser.getDisplay());
/* 502 */         TextTransfer textTransfer = TextTransfer.getInstance();
/* 503 */         cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 508 */         cb.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void openIrc(final String server, final String channel, final String alias)
/*     */   {
/*     */     try {
/* 516 */       PluginManager pluginManager = PluginInitializer.getDefaultInterface().getPluginManager();
/* 517 */       PluginInterface piChat = pluginManager.getPluginInterfaceByID("azplugins");
/* 518 */       if (piChat == null) {
/* 519 */         debug("IRC plugin not found");
/*     */       } else {
/* 521 */         UIManager manager = piChat.getUIManager();
/* 522 */         manager.addUIListener(new UIManagerListener()
/*     */         {
/*     */           public void UIDetached(UIInstance instance) {}
/*     */           
/*     */           public void UIAttached(UIInstance instance) {
/* 527 */             if ((instance instanceof UISWTInstance)) {
/*     */               try {
/* 529 */                 DisplayListener.this.debug("Opening IRC channel " + channel + " on " + server + " for user " + alias);
/*     */                 
/* 531 */                 UISWTInstance swtInstance = (UISWTInstance)instance;
/* 532 */                 UISWTView[] openViews = swtInstance.getOpenViews("Main");
/* 533 */                 for (int i = 0; i < openViews.length; i++) {
/* 534 */                   UISWTView view = openViews[i];
/*     */                   
/* 536 */                   view.closeView();
/*     */                 }
/*     */                 
/* 539 */                 swtInstance.openView("Main", "IRC", new String[] { server, channel, alias });
/*     */ 
/*     */ 
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/*     */ 
/* 546 */                 DisplayListener.this.debug("Failure opening IRC channel " + channel + " on " + server, e);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 554 */       debug("Failure opening IRC channel " + channel + " on " + server, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void showBrowser(final String url, final String target, final int w, final int h, final boolean allowResize, final BrowserMessage message, final String sourceRef)
/*     */   {
/* 561 */     final UIFunctions functions = UIFunctionsManager.getUIFunctions();
/* 562 */     if (functions == null) {
/* 563 */       AEThread2 thread = new AEThread2("show browser " + url, true) {
/*     */         public void run() {
/* 565 */           final Display display = Display.getDefault();
/* 566 */           display.asyncExec(new AERunnable() {
/*     */             public void runSupport() {
/* 568 */               BrowserWindow window = new BrowserWindow(display.getActiveShell(), DisplayListener.5.this.val$url, DisplayListener.5.this.val$w, DisplayListener.5.this.val$h, DisplayListener.5.this.val$allowResize, false);
/*     */               
/* 570 */               window.waitUntilClosed();
/* 571 */               DisplayListener.5.this.val$message.complete(false, true, null);
/*     */             }
/*     */           });
/*     */         }
/* 575 */       };
/* 576 */       thread.start();
/* 577 */       return;
/*     */     }
/*     */     
/* 580 */     AEThread2 thread = new AEThread2("show browser " + url, true) {
/*     */       public void run() {
/* 582 */         if ((w == 0) && (target != null)) {
/* 583 */           functions.viewURL(url, target, sourceRef);
/*     */         } else {
/* 585 */           functions.viewURL(url, target, w, h, allowResize, false);
/*     */         }
/* 587 */         message.complete(false, true, null);
/*     */       }
/* 589 */     };
/* 590 */     thread.start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/DisplayListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */