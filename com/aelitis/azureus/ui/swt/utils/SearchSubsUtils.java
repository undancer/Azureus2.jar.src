/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.program.Program;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.history.DownloadHistoryManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
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
/*     */ public class SearchSubsUtils
/*     */ {
/*     */   public static boolean addMenu(SearchSubsResultBase result, Menu menu)
/*     */   {
/*  59 */     byte[] hash = result.getHash();
/*     */     
/*  61 */     if (hash != null) {
/*  62 */       MenuItem item = new MenuItem(menu, 8);
/*  63 */       item.setText(MessageText.getString("searchsubs.menu.google.hash"));
/*  64 */       item.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/*  66 */           String s = ByteFormatter.encodeString(this.val$hash);
/*  67 */           String URL = "https://google.com/search?q=" + UrlUtils.encode(s);
/*  68 */           SearchSubsUtils.launchURL(URL);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*  73 */     MenuItem item = new MenuItem(menu, 8);
/*  74 */     item.setText(MessageText.getString("searchsubs.menu.gis"));
/*  75 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  77 */         String s = this.val$result.getName();
/*  78 */         s = s.replaceAll("[-_]", " ");
/*  79 */         String URL = "http://images.google.com/images?q=" + UrlUtils.encode(s);
/*  80 */         SearchSubsUtils.launchURL(URL);
/*     */       }
/*     */       
/*     */ 
/*  84 */     });
/*  85 */     item = new MenuItem(menu, 8);
/*  86 */     item.setText(MessageText.getString("searchsubs.menu.google"));
/*  87 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  89 */         String s = this.val$result.getName();
/*  90 */         s = s.replaceAll("[-_]", " ");
/*  91 */         String URL = "https://google.com/search?q=" + UrlUtils.encode(s);
/*  92 */         SearchSubsUtils.launchURL(URL);
/*     */       }
/*     */       
/*  95 */     });
/*  96 */     item = new MenuItem(menu, 8);
/*  97 */     item.setText(MessageText.getString("searchsubs.menu.bis"));
/*  98 */     item.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 100 */         String s = this.val$result.getName();
/* 101 */         s = s.replaceAll("[-_]", " ");
/* 102 */         String URL = "http://www.bing.com/images/search?q=" + UrlUtils.encode(s);
/* 103 */         SearchSubsUtils.launchURL(URL);
/*     */       }
/*     */       
/* 106 */     });
/* 107 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addMenu(SearchSubsResultBase[] results, Menu menu)
/*     */   {
/* 115 */     boolean has_hash = false;
/*     */     
/* 117 */     for (SearchSubsResultBase result : results)
/*     */     {
/* 119 */       byte[] hash = result.getHash();
/*     */       
/* 121 */       if (hash != null)
/*     */       {
/* 123 */         has_hash = true;
/*     */         
/* 125 */         break;
/*     */       }
/*     */     }
/*     */     
/* 129 */     MenuItem item = new MenuItem(menu, 8);
/* 130 */     item.setText(MessageText.getString("MagnetPlugin.contextmenu.exporturi"));
/* 131 */     item.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 134 */         StringBuffer buffer = new StringBuffer(1024);
/*     */         
/* 136 */         for (SearchSubsResultBase result : this.val$results)
/*     */         {
/* 138 */           byte[] hash = result.getHash();
/*     */           
/* 140 */           if (hash != null) {
/* 141 */             if (buffer.length() > 0) {
/* 142 */               buffer.append("\r\n");
/*     */             }
/*     */             
/* 145 */             String torrent_link = result.getTorrentLink();
/*     */             
/* 147 */             String str = UrlUtils.getMagnetURI(hash, result.getName(), null);
/*     */             
/* 149 */             if (torrent_link != null)
/*     */             {
/* 151 */               str = str + "&fl=" + UrlUtils.encode(torrent_link);
/*     */             }
/*     */             
/* 154 */             buffer.append(str);
/*     */           }
/*     */         }
/* 157 */         ClipboardCopy.copyToClipBoard(buffer.toString());
/*     */       }
/*     */       
/* 160 */     });
/* 161 */     item.setEnabled(has_hash);
/*     */   }
/*     */   
/*     */   private static void launchURL(String s) {
/* 165 */     Program program = Program.findProgram(".html");
/* 166 */     if ((program != null) && (program.getName().contains("Chrome"))) {
/*     */       try {
/* 168 */         Field field = Program.class.getDeclaredField("command");
/* 169 */         field.setAccessible(true);
/* 170 */         String command = (String)field.get(program);
/* 171 */         command = command.replaceAll("%[1lL]", s);
/* 172 */         command = command.replace(" --", "");
/* 173 */         PluginInitializer.getDefaultInterface().getUtilities().createProcess(command + " -incognito");
/*     */       } catch (Exception e1) {
/* 175 */         e1.printStackTrace();
/* 176 */         Utils.launch(s);
/*     */       }
/*     */     } else {
/* 179 */       Utils.launch(s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean filterCheck(SearchSubsResultBase ds, String filter, boolean regex)
/*     */   {
/* 189 */     if ((filter == null) || (filter.length() == 0))
/*     */     {
/* 191 */       return true;
/*     */     }
/*     */     try
/*     */     {
/* 195 */       boolean hash_filter = filter.startsWith("t:");
/*     */       
/* 197 */       if (hash_filter)
/*     */       {
/* 199 */         filter = filter.substring(2);
/*     */       }
/*     */       
/* 202 */       String s = "\\Q" + filter.replaceAll("[|;]", "\\\\E|\\\\Q") + "\\E";
/*     */       
/* 204 */       boolean match_result = true;
/*     */       
/* 206 */       if ((regex) && (s.startsWith("!")))
/*     */       {
/* 208 */         s = s.substring(1);
/*     */         
/* 210 */         match_result = false;
/*     */       }
/*     */       
/* 213 */       Pattern pattern = Pattern.compile(s, 2);
/*     */       
/* 215 */       if (hash_filter)
/*     */       {
/* 217 */         byte[] hash = ds.getHash();
/*     */         
/* 219 */         if (hash == null)
/*     */         {
/* 221 */           return false;
/*     */         }
/*     */         
/* 224 */         String[] names = { ByteFormatter.encodeString(hash), Base32.encode(hash) };
/*     */         
/* 226 */         for (String name : names)
/*     */         {
/* 228 */           if (pattern.matcher(name).find() == match_result)
/*     */           {
/* 230 */             return true;
/*     */           }
/*     */         }
/*     */         
/* 234 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 238 */       String name = ds.getName();
/*     */       
/* 240 */       return pattern.matcher(name).find() == match_result;
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/* 245 */     return true;
/*     */   }
/*     */   
/*     */ 
/* 249 */   private static final Object HS_KEY = new Object();
/*     */   
/*     */   public static final int HS_NONE = 0;
/*     */   
/*     */   public static final int HS_LIBRARY = 1;
/*     */   
/*     */   public static final int HS_ARCHIVE = 2;
/*     */   
/*     */   public static final int HS_HISTORY = 3;
/*     */   public static final int HS_UNKNOWN = 4;
/*     */   private static GlobalManager gm;
/*     */   private static DownloadManager dm;
/*     */   private static DownloadHistoryManager hm;
/*     */   
/*     */   public static int getHashStatus(SearchSubsResultBase result)
/*     */   {
/* 265 */     if (result == null)
/*     */     {
/* 267 */       return 0;
/*     */     }
/*     */     
/* 270 */     byte[] hash = result.getHash();
/*     */     
/* 272 */     if ((hash == null) || (hash.length != 20))
/*     */     {
/* 274 */       return 4;
/*     */     }
/*     */     
/* 277 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 279 */     Object[] entry = (Object[])result.getUserData(HS_KEY);
/*     */     
/* 281 */     if (entry != null)
/*     */     {
/* 283 */       long time = ((Long)entry[0]).longValue();
/*     */       
/* 285 */       if (now - time < 10000L)
/*     */       {
/* 287 */         return ((Integer)entry[1]).intValue();
/*     */       }
/*     */     }
/*     */     
/* 291 */     synchronized (HS_KEY)
/*     */     {
/* 293 */       if (gm == null)
/*     */       {
/* 295 */         AzureusCore core = AzureusCoreFactory.getSingleton();
/*     */         
/* 297 */         gm = core.getGlobalManager();
/* 298 */         dm = core.getPluginManager().getDefaultPluginInterface().getDownloadManager();
/* 299 */         hm = (DownloadHistoryManager)gm.getDownloadHistoryManager();
/*     */       }
/*     */     }
/*     */     
/*     */     int hs_result;
/*     */     int hs_result;
/* 305 */     if (gm.getDownloadManager(new HashWrapper(hash)) != null)
/*     */     {
/* 307 */       hs_result = 1;
/*     */     } else { int hs_result;
/* 309 */       if (dm.lookupDownloadStub(hash) != null)
/*     */       {
/* 311 */         hs_result = 2;
/*     */       } else { int hs_result;
/* 313 */         if (hm.getDates(hash) != null)
/*     */         {
/* 315 */           hs_result = 3;
/*     */         }
/*     */         else
/*     */         {
/* 319 */           hs_result = 0; }
/*     */       }
/*     */     }
/* 322 */     result.setUserData(HS_KEY, new Object[] { Long.valueOf(now + RandomUtils.nextInt(2500)), Integer.valueOf(hs_result) });
/*     */     
/* 324 */     return hs_result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/SearchSubsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */