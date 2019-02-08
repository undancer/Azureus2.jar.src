/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*     */ public class TRTrackerAnnouncerResponseImpl
/*     */   implements TRTrackerAnnouncerResponse
/*     */ {
/*     */   private final URL url;
/*     */   private final HashWrapper hash;
/*     */   private final int status;
/*     */   private final long time_to_wait;
/*     */   private String failure_reason;
/*  41 */   private boolean was_udp_probe = false;
/*  42 */   private int scrape_complete = -1;
/*  43 */   private int scrape_incomplete = -1;
/*  44 */   private int scrape_downloaded = -1;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRTrackerAnnouncerResponsePeer[] peers;
/*     */   
/*     */ 
/*     */   protected Map extensions;
/*     */   
/*     */ 
/*     */ 
/*     */   public TRTrackerAnnouncerResponseImpl(URL _url, HashWrapper _hash, int _status, long _time_to_wait)
/*     */   {
/*  57 */     this.url = _url;
/*  58 */     this.hash = _hash;
/*  59 */     this.status = _status;
/*  60 */     this.time_to_wait = _time_to_wait;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerAnnouncerResponseImpl(URL _url, HashWrapper _hash, int _status, long _time_to_wait, String _failure_reason)
/*     */   {
/*  71 */     this.url = _url;
/*  72 */     this.hash = _hash;
/*  73 */     this.status = _status;
/*  74 */     this.time_to_wait = _time_to_wait;
/*  75 */     this.failure_reason = _failure_reason;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerAnnouncerResponseImpl(URL _url, HashWrapper _hash, int _status, long _time_to_wait, TRTrackerAnnouncerResponsePeer[] _peers)
/*     */   {
/*  86 */     this.url = _url;
/*  87 */     this.hash = _hash;
/*  88 */     this.status = _status;
/*  89 */     this.time_to_wait = _time_to_wait;
/*  90 */     this.peers = _peers;
/*     */   }
/*     */   
/*     */ 
/*     */   public HashWrapper getHash()
/*     */   {
/*  96 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 102 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatusString()
/*     */   {
/* 108 */     String str = "";
/*     */     
/* 110 */     if (this.status == 0)
/*     */     {
/* 112 */       str = "Offline";
/*     */     }
/* 114 */     else if (this.status == 2)
/*     */     {
/* 116 */       str = "OK";
/*     */       
/* 118 */       if (this.was_udp_probe)
/*     */       {
/* 120 */         str = str + " (UDP Probe)";
/*     */       }
/*     */     }
/*     */     else {
/* 124 */       str = "Failed";
/*     */     }
/*     */     
/* 127 */     if ((this.failure_reason != null) && (this.failure_reason.length() > 0))
/*     */     {
/* 129 */       str = str + " - " + this.failure_reason;
/*     */     }
/*     */     
/* 132 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFailureReason(String reason)
/*     */   {
/* 139 */     this.failure_reason = reason;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setWasProbe()
/*     */   {
/* 145 */     this.was_udp_probe = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean wasProbe()
/*     */   {
/* 151 */     return this.was_udp_probe;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeToWait()
/*     */   {
/* 157 */     return this.time_to_wait;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAdditionalInfo()
/*     */   {
/* 163 */     return this.failure_reason;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPeers(TRTrackerAnnouncerResponsePeer[] _peers)
/*     */   {
/* 170 */     this.peers = _peers;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerAnnouncerResponsePeer[] getPeers()
/*     */   {
/* 176 */     return this.peers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExtensions(Map _extensions)
/*     */   {
/* 183 */     this.extensions = _extensions;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getExtensions()
/*     */   {
/* 189 */     return this.extensions;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 195 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getScrapeCompleteCount()
/*     */   {
/* 201 */     return this.scrape_complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getScrapeIncompleteCount()
/*     */   {
/* 207 */     return this.scrape_incomplete;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getScrapeDownloadedCount()
/*     */   {
/* 213 */     return this.scrape_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScrapeResult(int _complete, int _incomplete, int _downloaded)
/*     */   {
/* 222 */     this.scrape_complete = _complete;
/* 223 */     this.scrape_incomplete = _incomplete;
/*     */     
/* 225 */     if (_downloaded >= 0)
/*     */     {
/* 227 */       this.scrape_downloaded = _downloaded;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void print()
/*     */   {
/* 234 */     System.out.println("TRTrackerResponse::print");
/* 235 */     System.out.println("\tstatus = " + getStatus() + ", probe = " + this.was_udp_probe);
/* 236 */     System.out.println("\tfail msg = " + getAdditionalInfo());
/* 237 */     System.out.println("\tpeers:");
/*     */     
/* 239 */     if (this.peers != null)
/*     */     {
/* 241 */       for (int i = 0; i < this.peers.length; i++)
/*     */       {
/* 243 */         TRTrackerAnnouncerResponsePeer peer = this.peers[i];
/*     */         
/* 245 */         System.out.println("\t\t" + peer.getAddress() + ":" + peer.getPort());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 253 */     String str = "url=" + this.url + ", status=" + getStatus() + ", probe=" + this.was_udp_probe;
/*     */     
/* 255 */     if (getStatus() != 2)
/*     */     {
/* 257 */       str = str + ", error=" + getAdditionalInfo();
/*     */     }
/*     */     
/* 260 */     str = str + ", time_to_wait=" + this.time_to_wait;
/*     */     
/* 262 */     str = str + ", scrape_comp=" + this.scrape_complete + ", scrape_incomp=" + this.scrape_incomplete;
/*     */     
/* 264 */     return str;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerResponseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */