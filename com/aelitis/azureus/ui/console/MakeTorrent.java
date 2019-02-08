/*     */ package com.aelitis.azureus.ui.console;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class MakeTorrent
/*     */   implements TOTorrentProgressListener
/*     */ {
/*     */   private boolean verbose;
/*  44 */   private static final String[] validKeys = { "comment", "announce-list", "target", "force_piece_size_pow2", "verbose" };
/*     */   
/*     */   public void reportCurrentTask(String task_description) {
/*  47 */     if (this.verbose) {
/*  48 */       System.out.println(task_description);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportProgress(int percent_complete) {
/*  53 */     if (this.verbose) {
/*  54 */       System.out.print("\r" + percent_complete + "%    ");
/*     */     }
/*     */   }
/*     */   
/*     */   public MakeTorrent(String file, URL url, Map parameters) {
/*  59 */     File fSrc = new File(file);
/*     */     
/*  61 */     String torrentName = (String)parameters.get("target");
/*  62 */     if (torrentName == null)
/*  63 */       torrentName = file + ".torrent";
/*  64 */     File fDst = new File(torrentName);
/*     */     
/*  66 */     if (parameters.get("verbose") != null) {
/*  67 */       this.verbose = true;
/*     */     }
/*  69 */     TOTorrent torrent = null;
/*  70 */     String pieceSizeStr = (String)parameters.get("force_piece_size_pow2");
/*  71 */     if (pieceSizeStr != null) {
/*     */       try {
/*  73 */         long pieceSize = 1L << Integer.parseInt(pieceSizeStr);
/*     */         
/*  75 */         TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(fSrc, url, pieceSize);
/*     */         
/*  77 */         creator.addListener(this);
/*     */         
/*  79 */         torrent = creator.create();
/*     */       } catch (Exception e) {
/*  81 */         Debug.printStackTrace(e);
/*  82 */         return;
/*     */       }
/*     */     } else {
/*     */       try {
/*  86 */         TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(fSrc, url);
/*     */         
/*  88 */         creator.addListener(this);
/*     */         
/*  90 */         torrent = creator.create();
/*     */       }
/*     */       catch (Exception e) {
/*  93 */         Debug.printStackTrace(e);
/*  94 */         return;
/*     */       }
/*     */     }
/*     */     
/*  98 */     String comment = (String)parameters.get("comment");
/*  99 */     if (comment != null) {
/* 100 */       torrent.setComment(comment);
/*     */     }
/*     */     
/* 103 */     String announceList = (String)parameters.get("announce-list");
/* 104 */     if (announceList != null) {
/* 105 */       StringTokenizer st = new StringTokenizer(announceList, "|");
/* 106 */       List list = new ArrayList();
/* 107 */       List urls = new ArrayList();
/* 108 */       while (st.hasMoreTokens()) {
/* 109 */         String _url = st.nextToken();
/* 110 */         urls.add(_url);
/*     */       }
/* 112 */       list.add(urls);
/* 113 */       torrent.setAdditionalListProperty("announce-list", list);
/*     */     }
/*     */     try
/*     */     {
/* 117 */       torrent.serialiseToBEncodedFile(fDst);
/*     */     } catch (Exception e) {
/* 119 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 125 */     if (args.length < 2) {
/* 126 */       usage();
/* 127 */       SESecurityManager.exitVM(0);
/*     */     }
/* 129 */     Map parameters = new HashMap();
/* 130 */     for (int i = 2; i < args.length; i++) {
/* 131 */       boolean ok = parseParameter(args[i], parameters);
/* 132 */       if (!ok) SESecurityManager.exitVM(-1);
/*     */     }
/* 134 */     File f = new File(args[1]);
/* 135 */     if (!f.exists()) {
/* 136 */       System.out.println(args[1] + " is not a valid file / directory");
/* 137 */       SESecurityManager.exitVM(-1);
/*     */     }
/* 139 */     URL url = null;
/*     */     try {
/* 141 */       url = new URL(args[0]);
/*     */     } catch (Exception e) {
/* 143 */       System.out.println(args[0] + " is not a valid url");
/* 144 */       SESecurityManager.exitVM(-1);
/*     */     }
/* 146 */     new MakeTorrent(args[1], url, parameters);
/*     */   }
/*     */   
/*     */   public static void usage() {
/* 150 */     System.out.println("Usage :");
/* 151 */     System.out.println("MakeTorrent <trackerurl> <file|dir> [options]");
/* 152 */     System.out.println("Options :");
/* 153 */     System.out.println("--comment=<comment>            Adds a comment to the torrent");
/* 154 */     System.out.println("--force_piece_size_pow2=<pow2> Specifies the piece size to use");
/* 155 */     System.out.println("--target=<target file>         Specifies a target torrent file");
/* 156 */     System.out.println("--verbose                      Verbose");
/* 157 */     System.out.println("--announce-list=url1[|url2|...] Use a list of trackers");
/*     */   }
/*     */   
/*     */   public static boolean parseParameter(String parameter, Map parameters) {
/* 161 */     if (parameter == null)
/* 162 */       return false;
/* 163 */     if ((parameter.equalsIgnoreCase("--v")) || (parameter.equalsIgnoreCase("--verbose"))) {
/* 164 */       parameters.put("verbose", new Integer(1));
/*     */     }
/* 166 */     if (parameter.startsWith("--")) {
/*     */       try {
/* 168 */         StringTokenizer st = new StringTokenizer(parameter.substring(2), "=");
/* 169 */         String key = st.nextToken();
/* 170 */         String value = "";
/* 171 */         String sep = "";
/* 172 */         while (st.hasMoreTokens()) {
/* 173 */           value = value + sep + st.nextElement();
/* 174 */           sep = "=";
/*     */         }
/* 176 */         boolean valid = false;
/* 177 */         for (int i = 0; i < validKeys.length; i++) {
/* 178 */           if (validKeys[i].equalsIgnoreCase(key)) {
/* 179 */             valid = true;
/* 180 */             break;
/*     */           }
/*     */         }
/* 183 */         if (!valid) {
/* 184 */           System.out.println("Invalid parameter : " + key);
/* 185 */           return false;
/*     */         }
/* 187 */         parameters.put(key, value);
/* 188 */         return true;
/*     */       } catch (Exception e) {
/* 190 */         System.out.println("Cannot parse " + parameter);
/* 191 */         return false;
/*     */       }
/*     */     }
/* 194 */     System.out.println("Cannot parse " + parameter);
/* 195 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/console/MakeTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */