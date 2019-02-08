/*     */ package com.aelitis.azureus.core.messenger;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import com.aelitis.azureus.util.JSONUtils;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.Timer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlatformMessenger
/*     */ {
/*  47 */   private static final boolean DEBUG_URL = System.getProperty("platform.messenger.debug.url", "0").equals("1");
/*     */   
/*     */ 
/*     */   private static final String URL_PLATFORM_MESSAGE = "?service=rpc";
/*     */   
/*     */   private static final String URL_POST_PLATFORM_DATA = "service=rpc";
/*     */   
/*     */   private static final int MAX_POST_LENGTH = 1572864;
/*     */   
/*  56 */   private static boolean USE_HTTP_POST = true;
/*     */   
/*  58 */   public static String REPLY_EXCEPTION = "exception";
/*     */   
/*  60 */   public static String REPLY_ACTION = "action";
/*     */   
/*  62 */   public static String REPLY_RESULT = "response";
/*     */   
/*     */ 
/*  65 */   private static Map<String, Map<PlatformMessage, PlatformMessengerListener>> mapQueues = new HashMap();
/*     */   
/*     */   private static final String QUEUE_NOAZID = "noazid.";
/*     */   
/*     */   private static final String QUEUE_NORMAL = "msg.";
/*     */   
/*  71 */   private static AEMonitor queue_mon = new AEMonitor("v3.PlatformMessenger.queue");
/*     */   
/*     */ 
/*  74 */   private static Timer timerProcess = new Timer("v3.PlatformMessenger.queue");
/*     */   
/*     */ 
/*     */ 
/*  78 */   private static Map<String, TimerEvent> mapTimerEvents = new HashMap();
/*     */   
/*  80 */   private static AEMonitor mon_mapTimerEvents = new AEMonitor("mapTimerEvents");
/*     */   
/*     */   private static boolean initialized;
/*     */   
/*     */   private static fakeContext context;
/*     */   
/*  86 */   private static boolean allowMulti = false;
/*     */   
/*  88 */   private static AsyncDispatcher dispatcher = new AsyncDispatcher(5000);
/*     */   
/*  90 */   private static Map<String, Object> mapExtra = new HashMap();
/*     */   
/*     */   public static synchronized void init() {
/*  93 */     if (initialized) {
/*  94 */       return;
/*     */     }
/*  96 */     initialized = true;
/*     */     
/*     */ 
/*  99 */     context = new fakeContext();
/*     */   }
/*     */   
/*     */   public static ClientMessageContext getClientMessageContext() {
/* 103 */     if (!initialized) {
/* 104 */       init();
/*     */     }
/* 106 */     return context;
/*     */   }
/*     */   
/*     */   public static void queueMessage(PlatformMessage message, PlatformMessengerListener listener)
/*     */   {
/* 111 */     queueMessage(message, listener, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void queueMessage(PlatformMessage message, PlatformMessengerListener listener, boolean addToBottom)
/*     */   {
/* 117 */     if (!initialized) {
/* 118 */       init();
/*     */     }
/*     */     
/* 121 */     if (message == null) {
/* 122 */       debug("fire timerevent");
/*     */     }
/* 124 */     queue_mon.enter();
/*     */     try { long fireBefore;
/*     */       String queueID;
/*     */       long fireBefore;
/* 128 */       if (message != null) { String queueID;
/* 129 */         String queueID; if (!message.sendAZID()) {
/* 130 */           queueID = "noazid.";
/*     */         } else {
/* 132 */           queueID = "msg.";
/*     */         }
/*     */         
/* 135 */         Map<PlatformMessage, PlatformMessengerListener> mapQueue = (Map)mapQueues.get(queueID);
/* 136 */         if (mapQueue == null) {
/* 137 */           mapQueue = new LinkedHashMap();
/* 138 */           mapQueues.put(queueID, mapQueue);
/*     */         }
/* 140 */         mapQueue.put(message, listener);
/*     */         
/* 142 */         debug("q " + queueID + "(" + mapQueue.size() + ") " + message.toShortString() + ": " + message + " @ " + new Date(message.getFireBefore()) + "; in " + (message.getFireBefore() - SystemTime.getCurrentTime()) + "ms");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 147 */         fireBefore = message.getFireBefore();
/*     */       } else {
/* 149 */         queueID = null;
/* 150 */         fireBefore = SystemTime.getCurrentTime();
/*     */       }
/*     */       
/* 153 */       if (queueID != null) {
/*     */         try {
/* 155 */           mon_mapTimerEvents.enter();
/*     */           
/* 157 */           TimerEvent timerEvent = (TimerEvent)mapTimerEvents.get(queueID);
/*     */           
/* 159 */           if ((timerEvent == null) || (timerEvent.hasRun()) || (fireBefore < timerEvent.getWhen())) {
/* 160 */             if (timerEvent != null) {
/* 161 */               mapTimerEvents.remove(queueID);
/* 162 */               timerEvent.cancel();
/*     */             }
/*     */             
/* 165 */             timerEvent = timerProcess.addEvent(fireBefore, new TimerEventPerformer()
/*     */             {
/*     */               public void perform(TimerEvent event) {
/*     */                 try {
/* 169 */                   PlatformMessenger.mon_mapTimerEvents.enter();
/*     */                   
/* 171 */                   if (PlatformMessenger.mapTimerEvents.get(this.val$queueID) == event) {
/* 172 */                     PlatformMessenger.mapTimerEvents.remove(this.val$queueID);
/*     */                   }
/*     */                 } finally {
/* 175 */                   PlatformMessenger.mon_mapTimerEvents.exit();
/*     */                 }
/*     */                 
/* 178 */                 Object mapQueue = (Map)PlatformMessenger.mapQueues.get(this.val$queueID);
/* 179 */                 while ((mapQueue != null) && (((Map)mapQueue).size() > 0)) {
/* 180 */                   PlatformMessenger.processQueue(this.val$queueID, (Map)mapQueue);
/*     */ 
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 192 */             });
/* 193 */             mapTimerEvents.put(queueID, timerEvent);
/*     */           }
/* 195 */           if (timerEvent != null) {
/* 196 */             debug(" next q process for  " + queueID + " in " + (timerEvent.getWhen() - SystemTime.getCurrentTime()));
/*     */           }
/*     */         }
/*     */         finally {}
/*     */       }
/*     */     }
/*     */     finally {
/* 203 */       queue_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void debug(String string)
/*     */   {
/* 211 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.PMsgr");
/* 212 */     diag_logger.log(string);
/* 213 */     if (ConstantsVuze.DIAG_TO_STDOUT) {
/* 214 */       System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + string);
/*     */     }
/*     */   }
/*     */   
/*     */   protected static void debug(String string, Throwable e)
/*     */   {
/* 220 */     debug(string + "\n\t" + e.getClass().getName() + ": " + e.getMessage() + ", " + Debug.getCompressedStackTrace(e, 1, 80));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void pushMessageNow(PlatformMessage message, PlatformMessengerListener listener)
/*     */   {
/* 233 */     debug("push " + message.toShortString() + ": " + message);
/*     */     
/* 235 */     Map map = new HashMap(1);
/* 236 */     map.put(message, listener);
/* 237 */     processQueue(null, map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void processQueue(String queueID, Map mapQueue)
/*     */   {
/* 245 */     if (!initialized) {
/* 246 */       init();
/*     */     }
/*     */     
/* 249 */     final Map mapProcessing = new HashMap();
/*     */     
/* 251 */     boolean sendAZID = true;
/* 252 */     long contentNetworkID = 1L;
/*     */     
/*     */ 
/* 255 */     boolean isMulti = false;
/* 256 */     StringBuilder urlStem = new StringBuilder();
/* 257 */     long sequenceNo = 0L;
/*     */     
/* 259 */     Map<String, Object> mapPayload = new HashMap();
/* 260 */     mapPayload.put("azid", ConstantsVuze.AZID);
/* 261 */     mapPayload.put("azv", "5.7.6.0");
/* 262 */     mapPayload.put("mode", FeatureUtils.getPlusMode());
/* 263 */     mapPayload.put("noadmode", FeatureUtils.getNoAdsMode());
/* 264 */     mapPayload.putAll(mapExtra);
/* 265 */     List<Map> listCommands = new ArrayList();
/* 266 */     mapPayload.put("commands", listCommands);
/*     */     
/* 268 */     boolean forceProxy = false;
/*     */     
/* 270 */     queue_mon.enter();
/*     */     try {
/* 272 */       lastServer = null;
/*     */       
/* 274 */       first = true;
/* 275 */       for (iter = mapQueue.keySet().iterator(); iter.hasNext();) {
/* 276 */         PlatformMessage message = (PlatformMessage)iter.next();
/* 277 */         Object value = mapQueue.get(message);
/*     */         
/* 279 */         Map<String, Object> mapCmd = new HashMap();
/*     */         
/* 281 */         boolean fp = message.isForceProxy();
/*     */         
/* 283 */         if (fp) {
/* 284 */           forceProxy = true;
/*     */         }
/*     */         
/* 287 */         if (first) {
/* 288 */           sendAZID = message.sendAZID();
/* 289 */           first = false;
/*     */         }
/*     */         
/*     */ 
/* 293 */         message.setSequenceNo(sequenceNo);
/*     */         
/* 295 */         if (urlStem.length() > 0) {
/* 296 */           urlStem.append('&');
/*     */         }
/*     */         
/* 299 */         String listenerID = message.getListenerID();
/* 300 */         String messageID = message.getMessageID();
/* 301 */         Map params = message.getParameters();
/*     */         try {
/* 303 */           urlStem.append("msg=");
/* 304 */           urlStem.append(URLEncoder.encode(listenerID, "UTF-8"));
/* 305 */           urlStem.append(":");
/* 306 */           urlStem.append(URLEncoder.encode(message.getOperationID(), "UTF-8"));
/*     */         }
/*     */         catch (UnsupportedEncodingException e) {}
/*     */         
/*     */ 
/* 311 */         mapCmd.put("seq-id", Long.valueOf(sequenceNo));
/* 312 */         mapCmd.put("listener-id", listenerID);
/* 313 */         mapCmd.put("op-id", message.getOperationID());
/* 314 */         if (params != null) {
/* 315 */           mapCmd.put("values", params);
/*     */         }
/* 317 */         listCommands.add(mapCmd);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 322 */         if (sequenceNo > 10L) {
/* 323 */           debug("breaking up batch at " + sequenceNo + " because max limit would be exceeded");
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 328 */           String curServer = messageID + "-" + listenerID;
/* 329 */           if ((lastServer != null) && (!lastServer.equals(curServer))) {
/* 330 */             isMulti = true;
/*     */           }
/* 332 */           lastServer = curServer;
/*     */           
/* 334 */           PlatformMessengerListener listener = (PlatformMessengerListener)mapProcessing.get(message);
/* 335 */           if (listener != null) {
/* 336 */             listener.messageSent(message);
/*     */           }
/* 338 */           sequenceNo += 1L;
/*     */           
/*     */ 
/* 341 */           mapProcessing.put(message, value);
/*     */           
/* 343 */           iter.remove();
/*     */           
/* 345 */           if (!getAllowMulti()) break;
/*     */         }
/*     */       } } finally { String lastServer;
/*     */       boolean first;
/*     */       Iterator iter;
/* 350 */       queue_mon.exit();
/*     */     }
/*     */     
/*     */ 
/* 354 */     if (mapProcessing.size() == 0) {
/* 355 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 362 */     ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetwork(contentNetworkID);
/*     */     
/* 364 */     if (cn == null) {
/* 365 */       cn = ConstantsVuze.getDefaultContentNetwork();
/*     */     }
/*     */     
/* 368 */     String sURL_RPC = ContentNetworkUtils.getUrl(cn, 3) + "&" + urlStem.toString();
/*     */     
/*     */ 
/* 371 */     if (forceProxy)
/*     */     {
/* 373 */       sendAZID = false;
/*     */       
/*     */ 
/*     */ 
/* 377 */       sURL_RPC = sURL_RPC.replaceAll("([\\?&])azid=.*?&", "$1");
/*     */       
/* 379 */       mapPayload.remove("azid");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 384 */     String sPostData = null;
/* 385 */     String sJSONPayload = UrlUtils.encode(JSONUtils.encodeToJSON(mapPayload));
/* 386 */     String sURL; if (USE_HTTP_POST) {
/* 387 */       String sURL = sURL_RPC;
/*     */       
/* 389 */       sPostData = "service=rpc&payload=" + sJSONPayload;
/* 390 */       sPostData = cn.appendURLSuffix(sPostData, true, sendAZID);
/*     */       
/* 392 */       if (DEBUG_URL) {
/* 393 */         debug("POST for " + mapProcessing.size() + ": " + sURL + "\n   DATA: " + sPostData);
/*     */       }
/*     */       else {
/* 396 */         debug("POST for " + mapProcessing.size() + ": " + sURL);
/*     */       }
/*     */     } else {
/* 399 */       sURL = sURL_RPC + "?service=rpc" + "&payload=" + sJSONPayload;
/*     */       
/* 401 */       sURL = cn.appendURLSuffix(sURL, false, sendAZID);
/*     */       
/* 403 */       if (DEBUG_URL) {
/* 404 */         debug("GET: " + sURL);
/*     */       } else {
/* 406 */         debug("GET: " + sURL_RPC + "?service=rpc");
/*     */       }
/*     */     }
/*     */     
/* 410 */     String fURL = sURL;
/* 411 */     final String fPostData = sPostData;
/* 412 */     final boolean fForceProxy = forceProxy;
/*     */     
/*     */ 
/*     */ 
/* 416 */     dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         Iterator iter;
/*     */         try
/*     */         {
/* 423 */           PlatformMessenger.processQueueAsync(this.val$fURL, fPostData, mapProcessing, fForceProxy);
/*     */         } catch (Throwable e) {
/* 425 */           e.printStackTrace();
/* 426 */           if ((e instanceof ResourceDownloaderException)) {
/* 427 */             PlatformMessenger.debug("Error while sending message(s) to Platform: " + e.toString());
/*     */           } else {
/* 429 */             PlatformMessenger.debug("Error while sending message(s) to Platform", e);
/*     */           }
/* 431 */           for (iter = mapProcessing.keySet().iterator(); iter.hasNext();) {
/* 432 */             PlatformMessage message = (PlatformMessage)iter.next();
/* 433 */             PlatformMessengerListener l = (PlatformMessengerListener)mapProcessing.get(message);
/* 434 */             if (l != null) {
/*     */               try {
/* 436 */                 HashMap map = new HashMap();
/* 437 */                 map.put("text", e.toString());
/* 438 */                 map.put("Throwable", e);
/* 439 */                 l.replyReceived(message, PlatformMessenger.REPLY_EXCEPTION, map);
/*     */               } catch (Throwable e2) {
/* 441 */                 PlatformMessenger.debug("Error while sending replyReceived", e2);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void processQueueAsync(String sURL, String sData, Map mapProcessing, boolean forceProxy)
/*     */     throws Throwable
/*     */   {
/* 459 */     URL url = new URL(sURL);
/*     */     
/* 461 */     Object[] result = downloadURL(url, sData, forceProxy);
/*     */     
/* 463 */     String s = (String)result[0];
/* 464 */     List listReplies = (List)result[1];
/*     */     
/* 466 */     if ((listReplies == null) || (listReplies.isEmpty())) {
/* 467 */       debug("Error while sending message(s) to Platform: reply: " + s + "\nurl: " + sURL + "\nPostData: " + sData);
/*     */       
/* 469 */       for (Iterator iter = mapProcessing.keySet().iterator(); iter.hasNext();) {
/* 470 */         PlatformMessage message = (PlatformMessage)iter.next();
/* 471 */         PlatformMessengerListener l = (PlatformMessengerListener)mapProcessing.get(message);
/* 472 */         if (l != null) {
/*     */           try {
/* 474 */             HashMap map = new HashMap();
/* 475 */             map.put("text", "result was " + s);
/* 476 */             l.replyReceived(message, REPLY_EXCEPTION, map);
/*     */           } catch (Throwable e2) {
/* 478 */             debug("Error while sending replyReceived\nurl: " + sURL + "\nPostData: " + sData, e2);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 483 */       return;
/*     */     }
/*     */     
/* 486 */     Map<Long, Map> mapOrder = new HashMap();
/* 487 */     for (Object reply : listReplies) {
/* 488 */       if ((reply instanceof Map)) {
/* 489 */         mapOrder.put(Long.valueOf(MapUtils.getMapLong((Map)reply, "seq-id", -1L)), (Map)reply);
/*     */       }
/*     */     }
/* 492 */     for (Iterator iter = mapProcessing.keySet().iterator(); iter.hasNext();) {
/* 493 */       PlatformMessage message = (PlatformMessage)iter.next();
/* 494 */       PlatformMessengerListener l = (PlatformMessengerListener)mapProcessing.get(message);
/* 495 */       if (l != null)
/*     */       {
/*     */ 
/* 498 */         Map mapReply = (Map)mapOrder.get(new Long(message.getSequenceNo()));
/* 499 */         if (mapReply == null) {
/* 500 */           debug("No reply for " + message.toShortString());
/*     */         }
/* 502 */         String replyType = MapUtils.getMapString(mapReply, "type", "payload");
/*     */         Map payload;
/* 504 */         Map payload; if (replyType.equalsIgnoreCase("payload"))
/*     */         {
/* 506 */           Object test = mapReply.get("payload");
/* 507 */           if ((test instanceof List)) {
/* 508 */             List temp = (List)test;
/* 509 */             Map payload = new HashMap();
/*     */             try {
/* 511 */               for (int i = 0; i < temp.size(); i += 2) {
/* 512 */                 String k = (String)temp.get(i);
/* 513 */                 Object v = temp.get(i + 1);
/* 514 */                 payload.put(k, v);
/*     */               }
/*     */             } catch (Throwable e) {
/* 517 */               Debug.out("invalid reply: " + mapReply, e);
/*     */             }
/*     */           } else {
/* 520 */             payload = MapUtils.getMapMap(mapReply, "payload", Collections.EMPTY_MAP);
/*     */           }
/*     */         } else {
/* 523 */           payload = new HashMap();
/* 524 */           payload.put("message", MapUtils.getMapString(mapReply, "message", "?"));
/*     */         }
/*     */         
/*     */ 
/* 528 */         if (mapReply != null) {
/* 529 */           String reply = JSONUtils.encodeToJSON(payload);
/* 530 */           debug("Got a reply for " + message.toShortString() + "\n\t" + reply.substring(0, Math.min(8192, reply.length())));
/*     */         }
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 536 */           l.replyReceived(message, replyType, payload);
/*     */         } catch (Exception e2) {
/* 538 */           debug("Error while sending replyReceived", e2);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private static Object[] downloadURL(URL rpc_url, String postData, boolean forceProxy)
/*     */     throws Throwable
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aconst_null
/*     */     //   1: astore_3
/*     */     //   2: iload_2
/*     */     //   3: ifne +37 -> 40
/*     */     //   6: aconst_null
/*     */     //   7: aconst_null
/*     */     //   8: aload_0
/*     */     //   9: aload_1
/*     */     //   10: invokestatic 714	com/aelitis/azureus/core/messenger/PlatformMessenger:downloadURLSupport	(Ljava/net/Proxy;Ljava/lang/String;Ljava/net/URL;Ljava/lang/String;)[Ljava/lang/Object;
/*     */     //   13: astore 4
/*     */     //   15: aload 4
/*     */     //   17: iconst_1
/*     */     //   18: aaload
/*     */     //   19: ifnonnull +13 -> 32
/*     */     //   22: new 432	java/lang/Exception
/*     */     //   25: dup
/*     */     //   26: ldc 35
/*     */     //   28: invokespecial 734	java/lang/Exception:<init>	(Ljava/lang/String;)V
/*     */     //   31: athrow
/*     */     //   32: aload 4
/*     */     //   34: areturn
/*     */     //   35: astore 4
/*     */     //   37: aload 4
/*     */     //   39: astore_3
/*     */     //   40: ldc 73
/*     */     //   42: aload_0
/*     */     //   43: iconst_1
/*     */     //   44: invokestatic 718	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;Z)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*     */     //   47: astore 4
/*     */     //   49: aload 4
/*     */     //   51: ifnonnull +19 -> 70
/*     */     //   54: aload_3
/*     */     //   55: ifnull +5 -> 60
/*     */     //   58: aload_3
/*     */     //   59: athrow
/*     */     //   60: new 432	java/lang/Exception
/*     */     //   63: dup
/*     */     //   64: ldc 34
/*     */     //   66: invokespecial 734	java/lang/Exception:<init>	(Ljava/lang/String;)V
/*     */     //   69: athrow
/*     */     //   70: aload 4
/*     */     //   72: invokeinterface 792 1 0
/*     */     //   77: astore 5
/*     */     //   79: aload 4
/*     */     //   81: invokeinterface 791 1 0
/*     */     //   86: astore 6
/*     */     //   88: iconst_0
/*     */     //   89: istore 7
/*     */     //   91: new 437	java/lang/StringBuilder
/*     */     //   94: dup
/*     */     //   95: invokespecial 747	java/lang/StringBuilder:<init>	()V
/*     */     //   98: aload_0
/*     */     //   99: invokevirtual 760	java/net/URL:getHost	()Ljava/lang/String;
/*     */     //   102: invokevirtual 753	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   105: aload_0
/*     */     //   106: invokevirtual 759	java/net/URL:getPort	()I
/*     */     //   109: iconst_m1
/*     */     //   110: if_icmpne +8 -> 118
/*     */     //   113: ldc 1
/*     */     //   115: goto +25 -> 140
/*     */     //   118: new 437	java/lang/StringBuilder
/*     */     //   121: dup
/*     */     //   122: invokespecial 747	java/lang/StringBuilder:<init>	()V
/*     */     //   125: ldc 21
/*     */     //   127: invokevirtual 753	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   130: aload_0
/*     */     //   131: invokevirtual 759	java/net/URL:getPort	()I
/*     */     //   134: invokevirtual 750	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   137: invokevirtual 748	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   140: invokevirtual 753	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   143: invokevirtual 748	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   146: astore 8
/*     */     //   148: aload 6
/*     */     //   150: aload 8
/*     */     //   152: aload 5
/*     */     //   154: aload_1
/*     */     //   155: invokestatic 714	com/aelitis/azureus/core/messenger/PlatformMessenger:downloadURLSupport	(Ljava/net/Proxy;Ljava/lang/String;Ljava/net/URL;Ljava/lang/String;)[Ljava/lang/Object;
/*     */     //   158: astore 9
/*     */     //   160: iconst_1
/*     */     //   161: istore 7
/*     */     //   163: aload 9
/*     */     //   165: astore 10
/*     */     //   167: aload 4
/*     */     //   169: iload 7
/*     */     //   171: invokeinterface 790 2 0
/*     */     //   176: aload 10
/*     */     //   178: areturn
/*     */     //   179: astore 11
/*     */     //   181: aload 4
/*     */     //   183: iload 7
/*     */     //   185: invokeinterface 790 2 0
/*     */     //   190: aload 11
/*     */     //   192: athrow
/*     */     //   193: astore 4
/*     */     //   195: aload_3
/*     */     //   196: ifnonnull +8 -> 204
/*     */     //   199: aload 4
/*     */     //   201: goto +4 -> 205
/*     */     //   204: aload_3
/*     */     //   205: athrow
/*     */     // Line number table:
/*     */     //   Java source line #551	-> byte code offset #0
/*     */     //   Java source line #553	-> byte code offset #2
/*     */     //   Java source line #556	-> byte code offset #6
/*     */     //   Java source line #558	-> byte code offset #15
/*     */     //   Java source line #560	-> byte code offset #22
/*     */     //   Java source line #564	-> byte code offset #32
/*     */     //   Java source line #566	-> byte code offset #35
/*     */     //   Java source line #568	-> byte code offset #37
/*     */     //   Java source line #573	-> byte code offset #40
/*     */     //   Java source line #575	-> byte code offset #49
/*     */     //   Java source line #577	-> byte code offset #54
/*     */     //   Java source line #579	-> byte code offset #58
/*     */     //   Java source line #582	-> byte code offset #60
/*     */     //   Java source line #586	-> byte code offset #70
/*     */     //   Java source line #587	-> byte code offset #79
/*     */     //   Java source line #589	-> byte code offset #88
/*     */     //   Java source line #592	-> byte code offset #91
/*     */     //   Java source line #594	-> byte code offset #148
/*     */     //   Java source line #596	-> byte code offset #160
/*     */     //   Java source line #598	-> byte code offset #163
/*     */     //   Java source line #602	-> byte code offset #167
/*     */     //   Java source line #605	-> byte code offset #193
/*     */     //   Java source line #607	-> byte code offset #195
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	206	0	rpc_url	URL
/*     */     //   0	206	1	postData	String
/*     */     //   0	206	2	forceProxy	boolean
/*     */     //   1	204	3	error	Throwable
/*     */     //   13	20	4	result	Object[]
/*     */     //   35	3	4	e	Throwable
/*     */     //   47	135	4	plugin_proxy	com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy
/*     */     //   193	7	4	f	Throwable
/*     */     //   77	76	5	url	URL
/*     */     //   86	63	6	proxy	Proxy
/*     */     //   89	95	7	ok	boolean
/*     */     //   146	5	8	proxy_host	String
/*     */     //   158	6	9	result	Object[]
/*     */     //   179	12	11	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	34	35	java/lang/Throwable
/*     */     //   91	167	179	finally
/*     */     //   179	181	179	finally
/*     */     //   40	176	193	java/lang/Throwable
/*     */     //   179	193	193	java/lang/Throwable
/*     */   }
/*     */   
/*     */   private static Object[] downloadURLSupport(Proxy proxy, String proxy_host, URL url, String postData)
/*     */     throws Throwable
/*     */   {
/* 620 */     ResourceDownloaderFactory rdf = StaticUtilities.getResourceDownloaderFactory();
/*     */     
/*     */     ResourceDownloader rd;
/*     */     
/* 624 */     if (proxy == null)
/*     */     {
/* 626 */       rd = rdf.create(url, postData);
/*     */     }
/*     */     else
/*     */     {
/* 630 */       rd = rdf.create(url, postData, proxy);
/*     */     }
/*     */     
/* 633 */     if (proxy_host != null)
/*     */     {
/* 635 */       rd.setProperty("URL_HOST", proxy_host);
/*     */     }
/*     */     
/* 638 */     rd.setProperty("URL_Connection", "Keep-Alive");
/*     */     
/* 640 */     ResourceDownloader rd = rdf.getRetryDownloader(rd, 3);
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
/* 661 */     InputStream is = rd.download();
/*     */     
/*     */     byte[] data;
/*     */     try
/*     */     {
/* 666 */       int length = is.available();
/*     */       
/* 668 */       data = new byte[length];
/*     */       
/* 670 */       is.read(data);
/*     */     }
/*     */     finally
/*     */     {
/* 674 */       is.close();
/*     */     }
/*     */     
/* 677 */     String s = new String(data, "UTF8");
/*     */     
/* 679 */     Map mapAllReplies = JSONUtils.decodeJSON(s);
/*     */     
/* 681 */     List listReplies = MapUtils.getMapList(mapAllReplies, "replies", null);
/*     */     
/* 683 */     return new Object[] { s, listReplies };
/*     */   }
/*     */   
/*     */   public static void setAllowMulti(boolean allowMulti) {
/* 687 */     allowMulti = allowMulti;
/*     */   }
/*     */   
/*     */   public static boolean getAllowMulti() {
/* 691 */     return allowMulti;
/*     */   }
/*     */   
/*     */   public static void addExtraParam(String key, Object value) {
/* 695 */     synchronized (mapExtra) {
/* 696 */       mapExtra.put(key, value);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class fakeContext
/*     */     extends ClientMessageContextImpl
/*     */   {
/* 703 */     private long contentNetworkID = 1L;
/*     */     
/*     */     private void log(String str) {
/* 706 */       if (System.getProperty("browser.route.all.external.stimuli.for.testing", "false").equalsIgnoreCase("true"))
/*     */       {
/*     */ 
/* 709 */         System.err.println(str);
/*     */       }
/* 711 */       debug(str);
/*     */     }
/*     */     
/*     */     public fakeContext() {
/* 715 */       super(null);
/*     */     }
/*     */     
/*     */     public void deregisterBrowser() {
/* 719 */       log("deregisterBrowser");
/*     */     }
/*     */     
/*     */     public void displayBrowserMessage(String message) {
/* 723 */       log("displayBrowserMessage - " + message);
/*     */     }
/*     */     
/*     */     public boolean executeInBrowser(String javascript) {
/* 727 */       log("executeInBrowser - " + javascript);
/* 728 */       return false;
/*     */     }
/*     */     
/*     */     public Object getBrowserData(String key) {
/* 732 */       log("getBrowserData - " + key);
/* 733 */       return null;
/*     */     }
/*     */     
/*     */     public boolean sendBrowserMessage(String key, String op) {
/* 737 */       log("sendBrowserMessage - " + key + "/" + op);
/* 738 */       return false;
/*     */     }
/*     */     
/*     */     public boolean sendBrowserMessage(String key, String op, Map params) {
/* 742 */       log("sendBrowserMessage - " + key + "/" + op + "/" + params);
/* 743 */       return false;
/*     */     }
/*     */     
/*     */     public void setBrowserData(String key, Object value) {
/* 747 */       log("setBrowserData - " + key + "/" + value);
/*     */     }
/*     */     
/*     */     public boolean sendBrowserMessage(String key, String op, Collection params) {
/* 751 */       log("sendBrowserMessage - " + key + "/" + op + "/" + params);
/* 752 */       return false;
/*     */     }
/*     */     
/*     */     public void setTorrentURLHandler(ClientMessageContext.torrentURLHandler handler) {
/* 756 */       log("setTorrentURLHandler - " + handler);
/*     */     }
/*     */     
/*     */     public long getContentNetworkID() {
/* 760 */       return this.contentNetworkID;
/*     */     }
/*     */     
/*     */     public void setContentNetworkID(long contentNetwork) {
/* 764 */       this.contentNetworkID = contentNetwork;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/PlatformMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */