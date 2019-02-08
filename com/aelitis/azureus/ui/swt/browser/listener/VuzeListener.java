/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.AbstractBrowserMessageListener;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import com.aelitis.azureus.util.FeatureUtils.licenceDetails;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.swt.speedtest.SpeedTestSelector;
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
/*     */ public class VuzeListener
/*     */   extends AbstractBrowserMessageListener
/*     */ {
/*     */   public static final String DEFAULT_LISTENER_ID = "vuze";
/*     */   public static final String OP_LOAD_VUZE_FILE = "load-vuze-file";
/*     */   public static final String OP_INSTALL_TRIAL = "install-trial";
/*     */   public static final String OP_GET_MODE = "get-mode";
/*     */   public static final String OP_GET_REMAINING = "get-plus-remaining";
/*     */   public static final String OP_RUN_SPEED_TEST = "run-speed-test";
/*     */   
/*     */   public VuzeListener()
/*     */   {
/*  54 */     super("vuze");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void handleMessage(BrowserMessage message)
/*     */   {
/*  61 */     String opid = message.getOperationId();
/*     */     
/*  63 */     if ("load-vuze-file".equals(opid))
/*     */     {
/*  65 */       Map decodedMap = message.getDecodedMap();
/*     */       
/*  67 */       String content = MapUtils.getMapString(decodedMap, "content", null);
/*     */       
/*  69 */       if (content == null)
/*     */       {
/*  71 */         throw new IllegalArgumentException("content missing");
/*     */       }
/*     */       
/*     */ 
/*  75 */       byte[] bytes = Base32.decode(content);
/*     */       
/*  77 */       VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */       
/*  79 */       VuzeFile vf = vfh.loadVuzeFile(bytes);
/*     */       
/*  81 */       if (vf == null)
/*     */       {
/*  83 */         throw new IllegalArgumentException("content invalid");
/*     */       }
/*     */       
/*     */ 
/*  87 */       vfh.handleFiles(new VuzeFile[] { vf }, 0);
/*     */ 
/*     */     }
/*  90 */     else if ("install-trial".equals(opid)) {
/*  91 */       FeatureManagerUI.createTrial();
/*     */     }
/*  93 */     else if ("run-speed-test".equals(opid))
/*     */     {
/*  95 */       SpeedTestSelector.runMLABTest(null);
/*     */     }
/*  97 */     else if ("get-mode".equals(opid)) {
/*  98 */       Map decodedMap = message.getDecodedMap();
/*     */       
/* 100 */       String callback = MapUtils.getMapString(decodedMap, "callback", null);
/*     */       
/* 102 */       if (callback != null)
/*     */       {
/* 104 */         this.context.executeInBrowser(callback + "('" + FeatureUtils.getPlusMode() + "')");
/*     */       }
/*     */       else
/*     */       {
/* 108 */         message.debug("bad or no callback param");
/*     */       }
/* 110 */     } else if ("get-plus-remaining".equals(opid)) {
/* 111 */       Map decodedMap = message.getDecodedMap();
/*     */       
/* 113 */       String callback = MapUtils.getMapString(decodedMap, "callback", null);
/*     */       
/* 115 */       if (callback != null)
/*     */       {
/* 117 */         FeatureUtils.licenceDetails fd = FeatureUtils.getPlusFeatureDetails();
/* 118 */         if ((fd == null) || (fd.getExpiryTimeStamp() == 0L)) {
/* 119 */           this.context.executeInBrowser(callback + "()");
/*     */         } else {
/* 121 */           long ms1 = fd.getExpiryTimeStamp() - SystemTime.getCurrentTime();
/* 122 */           long ms2 = fd.getExpiryDisplayTimeStamp() - SystemTime.getCurrentTime();
/* 123 */           this.context.executeInBrowser(callback + "(" + ms1 + "," + ms2 + ")");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 128 */         message.debug("bad or no callback param");
/*     */       }
/*     */     }
/*     */     else {
/* 132 */       throw new IllegalArgumentException("Unknown operation: " + opid);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/VuzeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */