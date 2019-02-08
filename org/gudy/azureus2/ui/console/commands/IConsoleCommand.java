/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class IConsoleCommand
/*     */ {
/*     */   private String main_name;
/*     */   private String short_name;
/*     */   private HashSet commands;
/*     */   
/*     */   protected static final class TorrentComparator
/*     */     implements Comparator<DownloadManager>
/*     */   {
/*     */     public final int compare(DownloadManager aDL, DownloadManager bDL)
/*     */     {
/*  33 */       boolean aIsComplete = aDL.getStats().getDownloadCompleted(false) == 1000;
/*  34 */       boolean bIsComplete = bDL.getStats().getDownloadCompleted(false) == 1000;
/*  35 */       if ((aIsComplete) && (!bIsComplete))
/*  36 */         return 1;
/*  37 */       if ((!aIsComplete) && (bIsComplete))
/*  38 */         return -1;
/*  39 */       return aDL.getPosition() - bDL.getPosition();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IConsoleCommand(String main_name)
/*     */   {
/*  47 */     this(main_name, null);
/*     */   }
/*     */   
/*     */   public IConsoleCommand(String main_name, String short_name) {
/*  51 */     this.commands = new HashSet();
/*  52 */     this.main_name = main_name;
/*  53 */     this.short_name = short_name;
/*     */     
/*  55 */     if (main_name != null) this.commands.add(main_name);
/*  56 */     if (short_name != null) { this.commands.add(short_name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void execute(String paramString, ConsoleInput paramConsoleInput, List<String> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getCommandDescriptions();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void printHelp(PrintStream out, List<String> args)
/*     */   {
/*  80 */     out.println(getCommandDescriptions());
/*  81 */     printHelpExtra(out, args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printHelpExtra(PrintStream out, List<String> args) {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected final void printHelp(PrintStream out, String arg)
/*     */   {
/*     */     List args;
/*     */     
/*     */ 
/*  96 */     if (arg != null)
/*     */     {
/*  98 */       List args = new ArrayList();
/*  99 */       args.add(arg);
/*     */     }
/*     */     else {
/* 102 */       args = Collections.EMPTY_LIST;
/*     */     }
/* 104 */     printHelp(out, args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set getCommandNames()
/*     */   {
/* 116 */     return Collections.unmodifiableSet(this.commands);
/*     */   }
/*     */   
/* 119 */   public final String getCommandName() { return this.main_name; }
/* 120 */   public final String getShortCommandName() { return this.short_name; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getTorrentSummary(DownloadManager dm)
/*     */   {
/* 132 */     StringBuilder tstate = new StringBuilder();
/* 133 */     String summaryFormat = getDefaultSummaryFormat();
/* 134 */     char lastch = '0';
/* 135 */     char[] summaryChars = summaryFormat.toCharArray();
/* 136 */     for (int i = 0; i < summaryChars.length; i++) {
/* 137 */       char ch = summaryChars[i];
/* 138 */       if ((ch == '%') && (lastch != '\\'))
/*     */       {
/* 140 */         i++;
/* 141 */         if (i >= summaryChars.length) {
/* 142 */           tstate.append('%');
/*     */         } else {
/* 144 */           tstate.append(expandVariable(summaryChars[i], dm));
/*     */         }
/*     */       } else {
/* 147 */         tstate.append(ch);
/*     */       }
/* 149 */       lastch = ch;
/*     */     }
/* 151 */     return tstate.toString();
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
/*     */   protected String expandVariable(char variable, DownloadManager dm)
/*     */   {
/* 179 */     switch (variable)
/*     */     {
/*     */     case 'a': 
/* 182 */       return getShortStateString(dm.getState());
/*     */     case 'c': 
/* 184 */       DecimalFormat df = new DecimalFormat("000.0%");
/* 185 */       return df.format(dm.getStats().getCompleted() / 1000.0D);
/*     */     case 't': 
/* 187 */       if (dm.getState() == 100) {
/* 188 */         return dm.getErrorDetails();
/*     */       }
/* 190 */       if (dm.getDisplayName() == null) {
/* 191 */         return "?";
/*     */       }
/* 193 */       return dm.getDisplayName();
/*     */     
/*     */     case 'z': 
/* 196 */       return DisplayFormatters.formatByteCountToKiBEtc(dm.getSize());
/*     */     case 'e': 
/* 198 */       return DisplayFormatters.formatETA(dm.getStats().getSmoothedETA());
/*     */     case 'r': 
/* 200 */       long to = 0L;
/* 201 */       long tot = 0L;
/* 202 */       if (dm.getDiskManager() != null) {
/* 203 */         DiskManagerFileInfo[] files = dm.getDiskManager().getFiles();
/* 204 */         if ((files != null) && 
/* 205 */           (files.length > 1)) {
/* 206 */           int c = 0;
/* 207 */           for (int i = 0; i < files.length; i++) {
/* 208 */             if ((files[i] != null) && 
/* 209 */               (!files[i].isSkipped())) {
/* 210 */               c++;
/* 211 */               tot += files[i].getLength();
/* 212 */               to += files[i].getDownloaded();
/*     */             }
/*     */           }
/*     */           
/* 216 */           if (c == files.length) {
/* 217 */             tot = 0L;
/*     */           }
/*     */         }
/*     */       }
/* 221 */       DecimalFormat df1 = new DecimalFormat("000.0%");
/* 222 */       if (tot > 0L) {
/* 223 */         return "      (" + df1.format(to * 1.0D / tot) + ")";
/*     */       }
/* 225 */       return "\t";
/*     */     case 'd': 
/* 227 */       return DisplayFormatters.formatByteCountToKiBEtcPerSec(dm.getStats().getDataReceiveRate());
/*     */     case 'u': 
/* 229 */       return DisplayFormatters.formatByteCountToKiBEtcPerSec(dm.getStats().getDataSendRate());
/*     */     case 'D': 
/* 231 */       return DisplayFormatters.formatDownloaded(dm.getStats());
/*     */     case 'U': 
/* 233 */       return DisplayFormatters.formatByteCountToKiBEtc(dm.getStats().getTotalDataBytesSent());
/*     */     case 's': 
/* 235 */       return Integer.toString(dm.getNbSeeds());
/*     */     case 'p': 
/* 237 */       return Integer.toString(dm.getNbPeers());
/*     */     case 'v': 
/* 239 */       return Integer.toString(dm.getMaxUploads());
/*     */     case 'I': 
/* 241 */       int downloadSpeed = dm.getStats().getDownloadRateLimitBytesPerSecond();
/* 242 */       if (downloadSpeed <= 0)
/* 243 */         return "";
/* 244 */       return "(max " + DisplayFormatters.formatByteCountToKiBEtcPerSec(downloadSpeed) + ")";
/*     */     case 'O': 
/* 246 */       int uploadSpeed = dm.getStats().getUploadRateLimitBytesPerSecond();
/* 247 */       if (uploadSpeed <= 0)
/* 248 */         return "";
/* 249 */       return "(max " + DisplayFormatters.formatByteCountToKiBEtcPerSec(uploadSpeed) + ")";
/*     */     
/*     */     case 'P': 
/*     */     case 'S': 
/* 253 */       TRTrackerScraperResponse hd = dm.getTrackerScrapeResponse();
/* 254 */       if ((hd == null) || (!hd.isValid())) {
/* 255 */         return "?";
/*     */       }
/*     */       
/* 258 */       if (variable == 'S') {
/* 259 */         return Integer.toString(hd.getSeeds());
/*     */       }
/* 261 */       return Integer.toString(hd.getPeers());
/*     */     }
/*     */     
/* 264 */     return "??" + variable + "??";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getDefaultSummaryFormat()
/*     */   {
/* 274 */     return "[%a] %c\t%t (%z) ETA: %e\r\n%r\tSpeed: %d%I / %u%O\tAmount: %D / %U\tConnections: %s(%S) / %p(%P)";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getShortStateString(int dmstate)
/*     */   {
/* 284 */     switch (dmstate)
/*     */     {
/*     */     case 5: 
/* 287 */       return "I";
/*     */     case 20: 
/* 289 */       return "A";
/*     */     case 30: 
/* 291 */       return "C";
/*     */     case 50: 
/* 293 */       return ">";
/*     */     case 100: 
/* 295 */       return "E";
/*     */     case 60: 
/* 297 */       return "*";
/*     */     case 70: 
/* 299 */       return "!";
/*     */     case 0: 
/* 301 */       return ".";
/*     */     case 40: 
/* 303 */       return ":";
/*     */     case 75: 
/* 305 */       return "-";
/*     */     }
/* 307 */     return "?";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/IConsoleCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */