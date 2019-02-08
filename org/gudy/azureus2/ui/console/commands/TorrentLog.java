/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*     */ import org.gudy.azureus2.core3.logging.ILogEventListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*     */ public class TorrentLog
/*     */   extends TorrentCommand
/*     */   implements ILogEventListener
/*     */ {
/*  45 */   private static int MODE_OFF = 0;
/*  46 */   private static int MODE_ON = 1;
/*  47 */   private static int MODE_FLIP = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */   private int mode = 0;
/*     */   
/*  55 */   private AEMonitor dms_mon = new AEMonitor("TorrentLog");
/*     */   
/*  57 */   private ArrayList dms = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  62 */   private static SimpleDateFormat dateFormatter = new SimpleDateFormat("[h:mm:ss.SSS] ");
/*  63 */   private static FieldPosition formatPos = new FieldPosition(0);
/*     */   
/*     */ 
/*     */   private boolean gm_listener_added;
/*     */   
/*     */ 
/*     */   public TorrentLog()
/*     */   {
/*  71 */     super("tlog", "tl", "Torrent Logging");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args) {
/*  75 */     this.mode = MODE_ON;
/*  76 */     Vector newargs = new Vector(args);
/*  77 */     if (newargs.isEmpty()) {
/*  78 */       this.mode = MODE_FLIP;
/*  79 */     } else if (newargs.contains("off")) {
/*  80 */       newargs.removeElement("off");
/*  81 */       this.mode = MODE_OFF;
/*  82 */     } else if (!newargs.contains("on")) {
/*  83 */       this.mode = MODE_FLIP;
/*     */     }
/*  85 */     super.execute(commandName, ci, args);
/*     */   }
/*     */   
/*     */   protected boolean performCommand(ConsoleInput ci, DownloadManager dm, List args)
/*     */   {
/*     */     try {
/*  91 */       this.dms_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*  95 */       if (!this.gm_listener_added)
/*     */       {
/*  97 */         this.gm_listener_added = true;
/*     */         
/*  99 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 100 */         gm.addListener(new GlobalManagerAdapter()
/*     */         {
/* 102 */           public void downloadManagerRemoved(DownloadManager dm) { TorrentLog.this.dms.remove(dm); } }, false);
/*     */       }
/*     */       
/*     */       boolean turnOn;
/*     */       
/*     */       boolean turnOn;
/* 108 */       if (this.mode == MODE_FLIP) {
/* 109 */         turnOn = !this.dms.contains(dm);
/*     */       } else {
/* 111 */         turnOn = this.mode == MODE_ON;
/*     */       }
/*     */       
/* 114 */       if (turnOn) {
/* 115 */         ci.out.print("->on] ");
/* 116 */         if (this.dms.contains(dm)) {
/* 117 */           return true;
/*     */         }
/* 119 */         this.dms.add(dm);
/* 120 */         if (this.dms.size() == 1) {
/* 121 */           Logger.addListener(this);
/*     */         }
/*     */       } else {
/* 124 */         ci.out.print("->off] ");
/* 125 */         this.dms.remove(dm);
/* 126 */         if (this.dms.size() == 0)
/* 127 */           Logger.removeListener(this);
/*     */       }
/*     */     } catch (Exception e) {
/*     */       boolean bool1;
/* 131 */       e.printStackTrace(ci.out);
/* 132 */       return false;
/*     */     } finally {
/* 134 */       this.dms_mon.exit();
/*     */     }
/* 136 */     return true;
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions() {
/* 140 */     return "tl [on|off]\tTorrentLogging";
/*     */   }
/*     */   
/*     */   public void log(LogEvent event) {
/* 144 */     boolean bMatch = false;
/*     */     
/* 146 */     if (event.relatedTo == null) {
/* 147 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 151 */       this.dms_mon.enter();
/*     */       
/* 153 */       for (int i = 0; (!bMatch) && (i < event.relatedTo.length); i++) {
/* 154 */         Object obj = event.relatedTo[i];
/*     */         
/* 156 */         if (obj != null)
/*     */         {
/*     */ 
/* 159 */           for (int j = 0; (!bMatch) && (j < this.dms.size()); j++) {
/* 160 */             if ((obj instanceof LogRelation))
/*     */             {
/*     */ 
/* 163 */               Object newObj = ((LogRelation)obj).queryForClass(DownloadManager.class);
/* 164 */               if (newObj != null) {
/* 165 */                 obj = newObj;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 170 */             if (obj == this.dms.get(j))
/* 171 */               bMatch = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 176 */       this.dms_mon.exit();
/*     */     }
/*     */     
/* 179 */     if (bMatch) {
/* 180 */       StringBuffer buf = new StringBuffer();
/* 181 */       dateFormatter.format(event.timeStamp, buf, formatPos);
/* 182 */       buf.append("{").append(event.logID).append("} ");
/*     */       
/* 184 */       buf.append(event.text);
/* 185 */       if (event.relatedTo != null) {
/* 186 */         buf.append("; \t| ");
/* 187 */         for (int j = 0; j < event.relatedTo.length; j++) {
/* 188 */           Object obj = event.relatedTo[j];
/* 189 */           if (j > 0)
/* 190 */             buf.append("; ");
/* 191 */           if ((obj instanceof LogRelation)) {
/* 192 */             buf.append(((LogRelation)obj).getRelationText());
/* 193 */           } else if (obj != null) {
/* 194 */             buf.append(obj.getClass().getName()).append(": '").append(obj.toString()).append("'");
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 199 */       System.out.println(buf.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */