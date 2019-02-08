/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
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
/*     */ public class AENetworkClassifier
/*     */ {
/*     */   public static final String AT_PUBLIC = "Public";
/*     */   public static final String AT_I2P = "I2P";
/*     */   public static final String AT_TOR = "Tor";
/*  47 */   public static final String[] AT_NETWORKS = { "Public", "I2P", "Tor" };
/*     */   
/*     */ 
/*  50 */   public static final String[] AT_NON_PUBLIC = { "I2P", "Tor" };
/*     */   
/*  52 */   private static final List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   public static String categoriseAddress(String str)
/*     */   {
/*  58 */     if (str == null)
/*     */     {
/*  60 */       return "Public";
/*     */     }
/*     */     
/*  63 */     int len = str.length();
/*     */     
/*  65 */     if (len < 7)
/*     */     {
/*  67 */       return "Public";
/*     */     }
/*     */     
/*  70 */     char[] chars = str.toCharArray();
/*     */     
/*  72 */     char last_char = chars[(len - 1)];
/*     */     
/*  74 */     if ((last_char >= '0') && (last_char <= '9'))
/*     */     {
/*  76 */       return "Public";
/*     */     }
/*  78 */     if ((last_char == 'p') || (last_char == 'P'))
/*     */     {
/*  80 */       if ((chars[(len - 2)] == '2') && (chars[(len - 4)] == '.'))
/*     */       {
/*     */ 
/*  83 */         char c = chars[(len - 3)];
/*     */         
/*  85 */         if ((c == 'i') || (c == 'I'))
/*     */         {
/*  87 */           return "I2P";
/*     */         }
/*     */       }
/*     */       
/*  91 */       return "Public";
/*     */     }
/*  93 */     if ((last_char == 'n') || (last_char == 'N'))
/*     */     {
/*  95 */       if (chars[(len - 6)] == '.')
/*     */       {
/*  97 */         String temp = new String(chars, len - 5, 4).toLowerCase(Locale.US);
/*     */         
/*  99 */         if (temp.equals("onio"))
/*     */         {
/* 101 */           return "Tor";
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 106 */     return "Public";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String internalise(String str)
/*     */   {
/* 113 */     if (str == null)
/*     */     {
/* 115 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 119 */     for (String net : AT_NETWORKS)
/*     */     {
/* 121 */       if (str.equalsIgnoreCase(net))
/*     */       {
/* 123 */         return net;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 128 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String categoriseAddress(InetSocketAddress isa)
/*     */   {
/* 135 */     return categoriseAddress(AddressUtils.getHostAddress(isa));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] getNetworks(TOTorrent torrent, String display_name)
/*     */   {
/* 145 */     List<URL> urls = new ArrayList();
/*     */     
/* 147 */     urls.add(torrent.getAnnounceURL());
/*     */     
/* 149 */     TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */     
/* 151 */     for (int i = 0; i < sets.length; i++)
/*     */     {
/* 153 */       URL[] u = sets[i].getAnnounceURLs();
/*     */       
/* 155 */       Collections.addAll(urls, u);
/*     */     }
/*     */     
/* 158 */     List<String> available_networks = new ArrayList();
/*     */     
/* 160 */     for (int i = 0; i < urls.size(); i++)
/*     */     {
/* 162 */       URL u = (URL)urls.get(i);
/*     */       
/* 164 */       String network = categoriseAddress(u.getHost());
/*     */       
/* 166 */       if (!available_networks.contains(network))
/*     */       {
/* 168 */         available_networks.add(network);
/*     */       }
/*     */     }
/*     */     
/* 172 */     if ((available_networks.size() == 1) && (available_networks.get(0) == "Public"))
/*     */     {
/* 174 */       return new String[] { "Public" };
/*     */     }
/*     */     
/*     */ 
/* 178 */     boolean prompt = COConfigurationManager.getBooleanParameter("Network Selection Prompt");
/*     */     
/* 180 */     List<String> res = new ArrayList();
/*     */     
/* 182 */     if ((prompt) && (listeners.size() > 0))
/*     */     {
/* 184 */       String[] t_nets = new String[available_networks.size()];
/*     */       
/* 186 */       available_networks.toArray(t_nets);
/*     */       
/* 188 */       for (int i = 0; i < listeners.size(); i++) {
/*     */         try
/*     */         {
/* 191 */           String[] selected = ((AENetworkClassifierListener)listeners.get(i)).selectNetworks(display_name, t_nets);
/*     */           
/*     */ 
/*     */ 
/* 195 */           if (selected != null)
/*     */           {
/* 197 */             Collections.addAll(res, selected);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 201 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 209 */       for (int i = 0; i < available_networks.size(); i++)
/*     */       {
/* 211 */         if (COConfigurationManager.getBooleanParameter("Network Selection Default." + (String)available_networks.get(i)))
/*     */         {
/* 213 */           res.add(available_networks.get(i));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 218 */     String[] x = new String[res.size()];
/*     */     
/* 220 */     res.toArray(x);
/*     */     
/* 222 */     return x;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String[] getDefaultNetworks()
/*     */   {
/* 228 */     List<String> res = new ArrayList();
/*     */     
/* 230 */     for (String net : AT_NETWORKS)
/*     */     {
/* 232 */       if (COConfigurationManager.getBooleanParameter("Network Selection Default." + net))
/*     */       {
/* 234 */         res.add(net);
/*     */       }
/*     */     }
/*     */     
/* 238 */     String[] x = new String[res.size()];
/*     */     
/* 240 */     res.toArray(x);
/*     */     
/* 242 */     return x;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(AENetworkClassifierListener l)
/*     */   {
/* 249 */     listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(AENetworkClassifierListener l)
/*     */   {
/* 256 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 263 */     String[] tests = { null, "12345", "192.168.1.2", "fred.i2p", "fred.i2", "bill.onion", "bill.onio" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 273 */     for (String str : tests)
/*     */     {
/* 275 */       System.out.println(str + " -> " + categoriseAddress(str));
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AENetworkClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */