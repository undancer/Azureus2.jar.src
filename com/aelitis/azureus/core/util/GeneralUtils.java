/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*     */ import com.aelitis.azureus.core.util.average.MovingImmediateAverage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GeneralUtils
/*     */ {
/*     */   private static final String REGEX_URLHTML = "<A HREF=\"(.+?)\">(.+?)</A>";
/*     */   
/*     */   public static String replaceAll(String str, String from_str, String replacement)
/*     */   {
/*  54 */     StringBuffer res = null;
/*     */     
/*  56 */     int pos = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/*  60 */       int p1 = str.indexOf(from_str, pos);
/*     */       
/*  62 */       if (p1 == -1)
/*     */       {
/*  64 */         if (res == null)
/*     */         {
/*  66 */           return str;
/*     */         }
/*     */         
/*  69 */         res.append(str.substring(pos));
/*     */         
/*  71 */         return res.toString();
/*     */       }
/*     */       
/*     */ 
/*  75 */       if (res == null)
/*     */       {
/*  77 */         res = new StringBuffer(str.length() * 2);
/*     */       }
/*     */       
/*  80 */       if (p1 > pos)
/*     */       {
/*  82 */         res.append(str.substring(pos, p1));
/*     */       }
/*     */       
/*  85 */       res.append(replacement);
/*     */       
/*  87 */       pos = p1 + from_str.length();
/*     */     }
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
/*     */   public static String replaceAll(String str, String[] from_strs, String[] to_strs)
/*     */   {
/* 106 */     StringBuffer res = null;
/*     */     
/* 108 */     int pos = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/* 112 */       int min_match_pos = Integer.MAX_VALUE;
/* 113 */       int match_index = -1;
/*     */       
/* 115 */       for (int i = 0; i < from_strs.length; i++)
/*     */       {
/* 117 */         int pt = str.indexOf(from_strs[i], pos);
/*     */         
/* 119 */         if (pt != -1)
/*     */         {
/* 121 */           if (pt < min_match_pos)
/*     */           {
/* 123 */             min_match_pos = pt;
/* 124 */             match_index = i;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 129 */       if (match_index == -1)
/*     */       {
/* 131 */         if (res == null)
/*     */         {
/* 133 */           return str;
/*     */         }
/*     */         
/* 136 */         res.append(str.substring(pos));
/*     */         
/* 138 */         return res.toString();
/*     */       }
/*     */       
/*     */ 
/* 142 */       if (res == null)
/*     */       {
/* 144 */         res = new StringBuffer(str.length() * 2);
/*     */       }
/*     */       
/* 147 */       if (min_match_pos > pos)
/*     */       {
/* 149 */         res.append(str.substring(pos, min_match_pos));
/*     */       }
/*     */       
/* 152 */       res.append(to_strs[match_index]);
/*     */       
/* 154 */       pos = min_match_pos + from_strs[match_index].length();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String stripOutHyperlinks(String message)
/*     */   {
/* 161 */     return Pattern.compile("<A HREF=\"(.+?)\">(.+?)</A>", 2).matcher(message).replaceAll("$2");
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
/*     */   public static String[] splitQuotedTokens(String str)
/*     */   {
/* 175 */     List<String> bits = new ArrayList();
/*     */     
/* 177 */     char quote = ' ';
/* 178 */     boolean escape = false;
/* 179 */     boolean bit_contains_quotes = false;
/*     */     
/* 181 */     String bit = "";
/*     */     
/* 183 */     char[] chars = str.toCharArray();
/*     */     
/* 185 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/* 187 */       char c = chars[i];
/*     */       
/* 189 */       if (Character.isWhitespace(c))
/*     */       {
/* 191 */         c = ' ';
/*     */       }
/*     */       
/* 194 */       if (escape)
/*     */       {
/* 196 */         bit = bit + c;
/*     */         
/* 198 */         escape = false;
/*     */ 
/*     */ 
/*     */       }
/* 202 */       else if (c == '\\')
/*     */       {
/* 204 */         escape = true;
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 209 */       else if ((c == '"') || ((c == '\'') && ((i == 0) || (chars[(i - 1)] != '\\'))))
/*     */       {
/* 211 */         if (quote == ' ')
/*     */         {
/* 213 */           bit_contains_quotes = true;
/*     */           
/* 215 */           quote = c;
/*     */         }
/* 217 */         else if (quote == c)
/*     */         {
/* 219 */           quote = ' ';
/*     */         }
/*     */         else
/*     */         {
/* 223 */           bit = bit + c;
/*     */         }
/*     */         
/*     */       }
/* 227 */       else if (quote == ' ')
/*     */       {
/* 229 */         if (c == ' ')
/*     */         {
/* 231 */           if ((bit.length() > 0) || (bit_contains_quotes))
/*     */           {
/* 233 */             bit_contains_quotes = false;
/*     */             
/* 235 */             bits.add(bit);
/*     */             
/* 237 */             bit = "";
/*     */           }
/*     */         }
/*     */         else {
/* 241 */           bit = bit + c;
/*     */         }
/*     */       }
/*     */       else {
/* 245 */         bit = bit + c;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 250 */     if (quote != ' ')
/*     */     {
/* 252 */       bit = bit + quote;
/*     */     }
/*     */     
/* 255 */     if ((bit.length() > 0) || (bit_contains_quotes))
/*     */     {
/* 257 */       bits.add(bit);
/*     */     }
/*     */     
/* 260 */     return (String[])bits.toArray(new String[bits.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ProcessBuilder createProcessBuilder(File workingDir, String[] cmd, String[] extra_env)
/*     */     throws IOException
/*     */   {
/* 273 */     Map<String, String> newEnv = new HashMap();
/* 274 */     newEnv.putAll(System.getenv());
/* 275 */     newEnv.put("LANG", "C.UTF-8");
/* 276 */     if ((extra_env != null) && (extra_env.length > 1)) {
/* 277 */       for (int i = 1; i < extra_env.length; i += 2) {
/* 278 */         newEnv.put(extra_env[(i - 1)], extra_env[i]);
/*     */       }
/*     */     }
/*     */     
/* 282 */     if (Constants.isWindows) {
/* 283 */       String[] i18n = new String[cmd.length + 2];
/* 284 */       i18n[0] = "cmd";
/* 285 */       i18n[1] = "/C";
/* 286 */       i18n[2] = escapeDosCmd(cmd[0]);
/* 287 */       for (int counter = 1; counter < cmd.length; counter++) {
/* 288 */         if (cmd[counter].length() == 0) {
/* 289 */           i18n[(counter + 2)] = "";
/*     */         } else {
/* 291 */           String envName = "JENV_" + counter;
/* 292 */           i18n[(counter + 2)] = ("%" + envName + "%");
/* 293 */           newEnv.put(envName, cmd[counter]);
/*     */         }
/*     */       }
/* 296 */       cmd = i18n;
/*     */     }
/*     */     
/* 299 */     ProcessBuilder pb = new ProcessBuilder(cmd);
/* 300 */     Map<String, String> env = pb.environment();
/* 301 */     env.putAll(newEnv);
/*     */     
/* 303 */     if (workingDir != null) {
/* 304 */       pb.directory(workingDir);
/*     */     }
/* 306 */     return pb;
/*     */   }
/*     */   
/*     */   private static String escapeDosCmd(String string) {
/* 310 */     String s = string.replaceAll("([&%^])", "^$1");
/* 311 */     s = s.replaceAll("'", "\"'\"");
/* 312 */     return s;
/*     */   }
/*     */   
/* 315 */   private static int SMOOTHING_UPDATE_WINDOW = 60;
/* 316 */   private static int SMOOTHING_UPDATE_INTERVAL = 1;
/*     */   
/*     */   static
/*     */   {
/* 320 */     COConfigurationManager.addAndFireParameterListener("Stats Smoothing Secs", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String xxx)
/*     */       {
/*     */ 
/*     */ 
/* 328 */         GeneralUtils.access$002(COConfigurationManager.getIntParameter("Stats Smoothing Secs"));
/*     */         
/* 330 */         if (GeneralUtils.SMOOTHING_UPDATE_WINDOW < 30)
/*     */         {
/* 332 */           GeneralUtils.access$002(30);
/*     */         }
/* 334 */         else if (GeneralUtils.SMOOTHING_UPDATE_WINDOW > 1800)
/*     */         {
/* 336 */           GeneralUtils.access$002(1800);
/*     */         }
/*     */         
/* 339 */         GeneralUtils.access$102(GeneralUtils.SMOOTHING_UPDATE_WINDOW / 60);
/*     */         
/* 341 */         if (GeneralUtils.SMOOTHING_UPDATE_INTERVAL < 1)
/*     */         {
/* 343 */           GeneralUtils.access$102(1);
/*     */         }
/* 345 */         else if (GeneralUtils.SMOOTHING_UPDATE_INTERVAL > 20)
/*     */         {
/* 347 */           GeneralUtils.access$102(20);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getSmoothUpdateWindow()
/*     */   {
/* 356 */     return SMOOTHING_UPDATE_WINDOW;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getSmoothUpdateInterval()
/*     */   {
/* 362 */     return SMOOTHING_UPDATE_INTERVAL;
/*     */   }
/*     */   
/*     */ 
/*     */   public static MovingImmediateAverage getSmoothAverage()
/*     */   {
/* 368 */     return AverageFactory.MovingImmediateAverage(SMOOTHING_UPDATE_WINDOW / SMOOTHING_UPDATE_INTERVAL);
/*     */   }
/*     */   
/*     */   public static String stringJoin(Collection list, String delim) {
/* 372 */     StringBuilder sb = new StringBuilder();
/* 373 */     for (Object s : list)
/* 374 */       if (s != null)
/*     */       {
/*     */ 
/* 377 */         if (sb.length() > 0) {
/* 378 */           sb.append(delim);
/*     */         }
/* 380 */         sb.append(s.toString());
/*     */       }
/* 382 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/GeneralUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */