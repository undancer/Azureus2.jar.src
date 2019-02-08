/*     */ package org.gudy.azureus2.ui.console;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class MakeTorrent
/*     */   implements TOTorrentProgressListener
/*     */ {
/*     */   private boolean verbose;
/*  46 */   private static final String[] validKeys = { "comment", "announce-list", "target", "force_piece_size_pow2", "verbose" };
/*     */   
/*     */   public void reportCurrentTask(String task_description) {
/*  49 */     if (this.verbose) {
/*  50 */       System.out.println(task_description);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportProgress(int percent_complete) {
/*  55 */     if (this.verbose) {
/*  56 */       System.out.print("\r" + percent_complete + "%    ");
/*     */     }
/*     */   }
/*     */   
/*     */   public MakeTorrent(String file, URL url, Map parameters) {
/*  61 */     File fSrc = new File(file);
/*     */     
/*  63 */     String torrentName = (String)parameters.get("target");
/*  64 */     if (torrentName == null)
/*  65 */       torrentName = file + ".torrent";
/*  66 */     File fDst = new File(torrentName);
/*     */     
/*  68 */     if (parameters.get("verbose") != null) {
/*  69 */       this.verbose = true;
/*     */     }
/*  71 */     TOTorrent torrent = null;
/*  72 */     String pieceSizeStr = (String)parameters.get("force_piece_size_pow2");
/*  73 */     if (pieceSizeStr != null) {
/*     */       try {
/*  75 */         long pieceSize = 1L << Integer.parseInt(pieceSizeStr);
/*  76 */         TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(fSrc, url, pieceSize);
/*  77 */         creator.addListener(this);
/*  78 */         torrent = creator.create();
/*     */       } catch (Exception e) {
/*  80 */         e.printStackTrace();
/*  81 */         return;
/*     */       }
/*     */     } else {
/*     */       try {
/*  85 */         TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(fSrc, url);
/*  86 */         creator.addListener(this);
/*  87 */         torrent = creator.create();
/*     */       } catch (Exception e) {
/*  89 */         e.printStackTrace();
/*  90 */         return;
/*     */       }
/*     */     }
/*     */     
/*  94 */     String comment = (String)parameters.get("comment");
/*  95 */     if (comment != null) {
/*  96 */       torrent.setComment(comment);
/*     */     }
/*     */     
/*  99 */     String announceList = (String)parameters.get("announce-list");
/* 100 */     if (announceList != null) {
/* 101 */       StringTokenizer st = new StringTokenizer(announceList, "|");
/* 102 */       List list = new ArrayList();
/* 103 */       List urls = new ArrayList();
/* 104 */       while (st.hasMoreTokens()) {
/* 105 */         String _url = st.nextToken();
/* 106 */         urls.add(_url);
/*     */       }
/* 108 */       list.add(urls);
/*     */       
/* 110 */       TorrentUtils.listToAnnounceGroups(list, torrent);
/*     */     }
/*     */     try
/*     */     {
/* 114 */       torrent.serialiseToBEncodedFile(fDst);
/*     */     } catch (Exception e) {
/* 116 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 122 */     if (args.length < 2) {
/* 123 */       usage();
/* 124 */       System.exit(0);
/*     */     }
/* 126 */     Map parameters = new HashMap();
/* 127 */     for (int i = 2; i < args.length; i++) {
/* 128 */       boolean ok = parseParameter(args[i], parameters);
/* 129 */       if (!ok) System.exit(-1);
/*     */     }
/* 131 */     File f = new File(args[1]);
/* 132 */     if (!f.exists()) {
/* 133 */       System.out.println(args[1] + " is not a valid file / directory");
/* 134 */       System.exit(-1);
/*     */     }
/* 136 */     URL url = null;
/*     */     try {
/* 138 */       url = new URL(args[0]);
/*     */     } catch (Exception e) {
/* 140 */       System.out.println(args[0] + " is not a valid url");
/* 141 */       System.exit(-1);
/*     */     }
/* 143 */     new MakeTorrent(args[1], url, parameters);
/*     */   }
/*     */   
/*     */   public static void usage() {
/* 147 */     System.out.println("Usage :");
/* 148 */     System.out.println("MakeTorrent <trackerurl> <file|dir> [options]");
/* 149 */     System.out.println("Options :");
/* 150 */     System.out.println("--comment=<comment>            Adds a comment to the torrent");
/* 151 */     System.out.println("--force_piece_size_pow2=<pow2> Specifies the piece size to use");
/* 152 */     System.out.println("--target=<target file>         Specifies a target torrent file");
/* 153 */     System.out.println("--verbose                      Verbose");
/* 154 */     System.out.println("--announce-list=url1[|url2|...] Use a list of trackers");
/*     */   }
/*     */   
/*     */   public static boolean parseParameter(String parameter, Map parameters) {
/* 158 */     if (parameter == null)
/* 159 */       return false;
/* 160 */     if ((parameter.equalsIgnoreCase("--v")) || (parameter.equalsIgnoreCase("--verbose"))) {
/* 161 */       parameters.put("verbose", new Integer(1));
/*     */     }
/* 163 */     if (parameter.startsWith("--")) {
/*     */       try {
/* 165 */         StringTokenizer st = new StringTokenizer(parameter.substring(2), "=");
/* 166 */         String key = st.nextToken();
/* 167 */         String value = "";
/* 168 */         String sep = "";
/* 169 */         while (st.hasMoreTokens()) {
/* 170 */           value = value + sep + st.nextElement();
/* 171 */           sep = "=";
/*     */         }
/* 173 */         boolean valid = false;
/* 174 */         for (int i = 0; i < validKeys.length; i++) {
/* 175 */           if (validKeys[i].equalsIgnoreCase(key)) {
/* 176 */             valid = true;
/* 177 */             break;
/*     */           }
/*     */         }
/* 180 */         if (!valid) {
/* 181 */           System.out.println("Invalid parameter : " + key);
/* 182 */           return false;
/*     */         }
/* 184 */         parameters.put(key, value);
/* 185 */         return true;
/*     */       } catch (Exception e) {
/* 187 */         System.out.println("Cannot parse " + parameter);
/* 188 */         return false;
/*     */       }
/*     */     }
/* 191 */     System.out.println("Cannot parse " + parameter);
/* 192 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/MakeTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */