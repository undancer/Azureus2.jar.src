/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*     */ public class Priority
/*     */   extends OptionsConsoleCommand
/*     */ {
/*     */   private static final int NORMAL = 1;
/*     */   private static final int HIGH = 2;
/*     */   private static final int DONOTDOWNLOAD = 3;
/*     */   private static final int DELETE = 4;
/*     */   
/*     */   public Priority()
/*     */   {
/*  41 */     super("prio");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions() {
/*  45 */     return "prio [#torrent] [#file|range(i.e. 1-2,5)|all] [normal|high|dnd|del]";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  49 */     out.println("> -----");
/*  50 */     out.println("Usage: prio [torrent] [file(s)] [priority]");
/*  51 */     out.println("Options:");
/*  52 */     out.println("\t[torrent]\tThe torrent number from 'show torrents'");
/*  53 */     out.println("\t[file(s)] is one of:");
/*  54 */     out.println("\t\t\t#file:\tthe file number from 'show [#torrent]',");
/*  55 */     out.println("\t\t\trange:\ta range of file numbers, i.e. 1-3 or 1-10,12-15 or 1,3,5-8 ,");
/*  56 */     out.println("\t\t\tall:\t 'all' applies priority to all files of the torrent");
/*  57 */     out.println("\t[priority] is one of:");
/*  58 */     out.println("\t\t\tnormal\tNormal priority");
/*  59 */     out.println("\t\t\thigh  \tHigh priority");
/*  60 */     out.println("\t\t\tdnd   \tDo not download (skip)");
/*  61 */     out.println("\t\t\tdel   \tDo not download & delete file");
/*  62 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private static final String[] priostr = { "Normal", "High", "DoNotDownload", "Delete" };
/*     */   
/*     */ 
/*     */ 
/*     */   private int newprio;
/*     */   
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput console, CommandLine commandLine)
/*     */   {
/*  79 */     List args = commandLine.getArgList();
/*     */     
/*     */ 
/*     */ 
/*  83 */     if (args.isEmpty())
/*     */     {
/*  85 */       console.out.println("Torrent # required!");
/*  86 */       return;
/*     */     }
/*  88 */     String tnumstr = (String)args.remove(0);
/*     */     
/*  90 */     if (args.isEmpty())
/*     */     {
/*  92 */       console.out.println("File # required!");
/*  93 */       return;
/*     */     }
/*  95 */     String fnumstr = (String)args.remove(0);
/*     */     
/*     */ 
/*  98 */     if ((console.torrents == null) || (console.torrents.isEmpty())) {
/*  99 */       console.out.println("> Command 'prio': No torrents in list (try 'show torrents' first)."); return;
/*     */     }
/*     */     int tnumber;
/*     */     DownloadManager dm;
/*     */     DiskManagerFileInfo[] files;
/* 104 */     try { tnumber = Integer.parseInt(tnumstr);
/* 105 */       if ((tnumber == 0) || (tnumber > console.torrents.size())) {
/* 106 */         console.out.println("> Command 'prio': Torrent #" + tnumber + " unknown.");
/* 107 */         return;
/*     */       }
/*     */       
/* 110 */       dm = (DownloadManager)console.torrents.get(tnumber - 1);
/* 111 */       files = dm.getDiskManagerFileInfo();
/*     */     }
/*     */     catch (Exception e) {
/* 114 */       e.printStackTrace();
/* 115 */       console.out.println("> Command 'prio': Torrent # '" + tnumstr + "' unknown.");
/* 116 */       return;
/*     */     }
/*     */     
/* 119 */     if (args.isEmpty())
/*     */     {
/* 121 */       console.out.println("> Command 'prio': missing parameter for new priority");
/* 122 */       return;
/*     */     }
/* 124 */     String newpriostr = (String)args.remove(0);
/*     */     
/*     */ 
/* 127 */     if (newpriostr.equalsIgnoreCase("normal")) {
/* 128 */       this.newprio = 1;
/* 129 */     } else if (newpriostr.equalsIgnoreCase("high")) {
/* 130 */       this.newprio = 2;
/* 131 */     } else if (newpriostr.equalsIgnoreCase("dnd")) {
/* 132 */       this.newprio = 3;
/* 133 */     } else if (newpriostr.equalsIgnoreCase("del")) {
/* 134 */       this.newprio = 4;
/*     */     } else {
/* 136 */       console.out.println("> Command 'prio': unknown priority " + newpriostr); return;
/*     */     }
/*     */     
/*     */     String[] sections;
/*     */     
/* 141 */     if (fnumstr.equalsIgnoreCase("all")) {
/* 142 */       String[] sections = new String[1];
/* 143 */       sections[0] = ("1-" + files.length);
/*     */     } else {
/* 145 */       sections = fnumstr.split(",");
/*     */     }
/* 147 */     LinkedList fs = new LinkedList();
/* 148 */     LinkedList fe = new LinkedList();
/*     */     
/*     */ 
/* 151 */     for (int i = 0; i < sections.length; i++) {
/*     */       try { int dash;
/* 153 */         int end; int end; int start; if ((dash = sections[i].indexOf('-')) != -1) {
/* 154 */           int start = Integer.parseInt(sections[i].substring(0, dash));
/* 155 */           end = Integer.parseInt(sections[i].substring(dash + 1));
/*     */         } else {
/* 157 */           start = end = Integer.parseInt(sections[i]); }
/* 158 */         if ((start == 0) || (end > files.length)) {
/* 159 */           console.out.println("> Command 'prio': Invalid file range " + sections[i]);
/* 160 */           return;
/*     */         }
/* 162 */         if (start > end) {
/* 163 */           console.out.println("> Command 'prio': Invalid file range '" + sections[i] + "'");
/*     */         }
/*     */         
/*     */ 
/* 167 */         fs.add(new Integer(start - 1));
/* 168 */         fe.add(new Integer(end - 1));
/*     */       } catch (Exception e) {
/* 170 */         console.out.println("> Command 'prio': File # '" + sections[i] + "' unknown.");
/*     */         
/* 172 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 177 */     if ((this.newprio == 4) && (dm.getState() != 70)) {
/*     */       try {
/* 179 */         dm.stopIt(70, false, false);
/*     */       } catch (Exception e) {
/* 181 */         console.out.println("Failed to stop torrent " + tnumber);
/* 182 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 187 */     int nummod = 0;
/* 188 */     while (fs.size() > 0) {
/* 189 */       int start = ((Integer)fs.removeFirst()).intValue();
/* 190 */       int end = ((Integer)fe.removeFirst()).intValue();
/* 191 */       for (int i = start; i <= end; i++) {
/* 192 */         nummod++;
/*     */         
/*     */ 
/* 195 */         if (this.newprio == 1) {
/* 196 */           files[i].setPriority(0);
/* 197 */           files[i].setSkipped(false);
/* 198 */         } else if (this.newprio == 2) {
/* 199 */           files[i].setPriority(1);
/* 200 */           files[i].setSkipped(false);
/* 201 */         } else if (this.newprio == 3) {
/* 202 */           files[i].setPriority(0);
/* 203 */           files[i].setSkipped(true);
/* 204 */         } else if (this.newprio == 4) {
/* 205 */           int st = files[i].getStorageType();
/* 206 */           int target_st = -1;
/* 207 */           if (st == 1) {
/* 208 */             target_st = 2;
/* 209 */           } else if (st == 3) {
/* 210 */             target_st = 4;
/*     */           }
/* 212 */           if ((target_st != -1) && (files[i].setStorageType(target_st)))
/*     */           {
/* 214 */             files[i].setPriority(0);
/* 215 */             files[i].setSkipped(true);
/*     */           } else {
/* 217 */             console.out.println("> Command 'prio': Failed to delete file " + (i + 1));
/* 218 */             nummod--;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 223 */     if ((this.newprio == 4) && (dm.getState() == 70)) {
/*     */       try {
/* 225 */         dm.stopIt(75, false, false);
/*     */       } catch (Exception e) {
/* 227 */         console.out.println("Failed to restart torrent " + tnumber);
/* 228 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 234 */     console.out.println(nummod + " file(s) priority set to " + priostr[(this.newprio - 1)]);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Priority.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */