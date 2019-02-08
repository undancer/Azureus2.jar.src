/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
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
/*     */ public class TorrentFilter
/*     */ {
/*  37 */   private static final Pattern rangePattern = Pattern.compile("([0-9]+)\\s*((-)|(-\\s*([0-9]+)))?");
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
/*     */   private List matchRange(List torrents, String filter)
/*     */   {
/*  51 */     Matcher matcher = rangePattern.matcher(filter);
/*  52 */     List list = new ArrayList();
/*  53 */     if (matcher.matches())
/*     */     {
/*  55 */       int minId = Integer.parseInt(matcher.group(1));
/*  56 */       if (minId == 0)
/*  57 */         throw new AzureusCoreException("lower range must be greater than 0");
/*  58 */       if (minId > torrents.size())
/*  59 */         throw new AzureusCoreException("lower range specified (" + minId + ") is outside number of torrents (" + torrents.size() + ")");
/*  60 */       if (matcher.group(2) == null)
/*     */       {
/*     */ 
/*  63 */         list.add(torrents.get(minId - 1));
/*  64 */         return list; }
/*     */       int maxId;
/*     */       int maxId;
/*  67 */       if (matcher.group(3) == null)
/*     */       {
/*  69 */         maxId = Integer.parseInt(matcher.group(5));
/*     */       }
/*     */       else {
/*  72 */         maxId = torrents.size();
/*     */       }
/*  74 */       if (minId > maxId) {
/*  75 */         throw new AzureusCoreException("when specifying a range, the max value must be greater than or equal to the min value");
/*     */       }
/*  77 */       for (int i = minId - 1; (i < maxId) && (i < torrents.size()); i++)
/*     */       {
/*  79 */         list.add(torrents.get(i));
/*     */       }
/*     */     }
/*  82 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List matchWildcard(List torrents, String filter)
/*     */   {
/*  94 */     Pattern pattern = Pattern.compile(wildcardToPattern(filter), 2);
/*  95 */     List list = new ArrayList();
/*  96 */     for (Iterator iter = torrents.iterator(); iter.hasNext();) {
/*  97 */       DownloadManager dm = (DownloadManager)iter.next();
/*  98 */       if (pattern.matcher(dm.getDisplayName()).matches())
/*  99 */         list.add(dm);
/*     */     }
/* 101 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String wildcardToPattern(String wild)
/*     */   {
/* 111 */     if (wild == null) {
/* 112 */       return null;
/*     */     }
/* 114 */     StringBuilder buffer = new StringBuilder();
/*     */     
/* 116 */     char[] chars = wild.toCharArray();
/*     */     
/* 118 */     for (int i = 0; i < chars.length; i++) {
/* 119 */       if (chars[i] == '*') {
/* 120 */         buffer.append(".*");
/* 121 */       } else if (chars[i] == '?') {
/* 122 */         buffer.append(".");
/* 123 */       } else if ("+()^$.{}[]|\\".indexOf(chars[i]) != -1) {
/* 124 */         buffer.append('\\').append(chars[i]);
/*     */       }
/*     */       else
/* 127 */         buffer.append(chars[i]);
/*     */     }
/* 129 */     return buffer.toString().toLowerCase();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List getTorrents(List torrentsToMatch, String filter)
/*     */   {
/* 141 */     List torrents = new ArrayList();
/* 142 */     torrents.addAll(matchRange(torrentsToMatch, filter));
/* 143 */     torrents.addAll(matchWildcard(torrentsToMatch, filter));
/* 144 */     return torrents;
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
/*     */   public List getTorrents(List torrentsToMatch, List args)
/*     */   {
/* 157 */     StringBuilder allArgs = new StringBuilder();
/* 158 */     boolean first = true;
/* 159 */     for (Iterator iter = args.iterator(); iter.hasNext();) {
/* 160 */       if (!first) {
/* 161 */         allArgs.append(",");
/*     */       } else
/* 163 */         first = false;
/* 164 */       allArgs.append(iter.next());
/*     */     }
/*     */     
/* 167 */     List torrents = matchWildcard(torrentsToMatch, allArgs.toString());
/* 168 */     if (torrents.size() > 0)
/* 169 */       return torrents;
/* 170 */     torrents = matchRange(torrentsToMatch, allArgs.toString());
/* 171 */     if (torrents.size() > 0) {
/* 172 */       return torrents;
/*     */     }
/* 174 */     for (Iterator iter = args.iterator(); iter.hasNext();) {
/* 175 */       torrents.addAll(getTorrents(torrentsToMatch, (String)iter.next()));
/*     */     }
/* 177 */     return torrents;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/TorrentFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */