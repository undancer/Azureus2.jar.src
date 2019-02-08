/*     */ package org.gudy.azureus2.core3.peer.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ public class PeerIdentityManager
/*     */ {
/*  36 */   private static final boolean MUTLI_CONTROLLERS = COConfigurationManager.getBooleanParameter("peer.multiple.controllers.per.torrent.enable", false);
/*     */   
/*  38 */   private static final AEMonitor class_mon = new AEMonitor("PeerIdentityManager:class");
/*     */   
/*  40 */   private static final Map<PeerIdentityDataID, DataEntry> dataMap = new LightHashMap();
/*     */   
/*  42 */   private static int totalIDs = 0;
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
/*     */   public static PeerIdentityDataID createDataID(byte[] data)
/*     */   {
/*  96 */     PeerIdentityDataID data_id = new PeerIdentityDataID(data);
/*     */     
/*     */     DataEntry dataEntry;
/*     */     try
/*     */     {
/* 101 */       class_mon.enter();
/*     */       
/* 103 */       dataEntry = (DataEntry)dataMap.get(data_id);
/*     */       
/* 105 */       if (dataEntry == null)
/*     */       {
/* 107 */         dataEntry = new DataEntry();
/*     */         
/* 109 */         dataMap.put(data_id, dataEntry);
/*     */       }
/*     */     }
/*     */     finally {
/* 113 */       class_mon.exit();
/*     */     }
/*     */     
/* 116 */     data_id.setDataEntry(dataEntry);
/*     */     
/* 118 */     return data_id;
/*     */   }
/*     */   
/*     */ 
/*     */   private static class PeerIdentity
/*     */   {
/*     */     private final byte[] id;
/*     */     private final short port;
/*     */     private final int hashcode;
/*     */     
/*     */     private PeerIdentity(byte[] _id, int local_port)
/*     */     {
/* 130 */       this.id = _id;
/* 131 */       this.port = ((short)local_port);
/* 132 */       this.hashcode = new String(this.id).hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 136 */       if (this == obj) return true;
/* 137 */       if ((obj != null) && ((obj instanceof PeerIdentity))) {
/* 138 */         PeerIdentity other = (PeerIdentity)obj;
/* 139 */         if ((PeerIdentityManager.MUTLI_CONTROLLERS) && 
/* 140 */           (this.port != other.port)) {
/* 141 */           return false;
/*     */         }
/*     */         
/* 144 */         return Arrays.equals(this.id, other.id);
/*     */       }
/* 146 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 150 */       return this.hashcode;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getString()
/*     */     {
/* 156 */       return ByteFormatter.encodeString(this.id);
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
/*     */   public static boolean addIdentity(PeerIdentityDataID data_id, byte[] peer_id, int local_port, String ip)
/*     */   {
/* 169 */     PeerIdentity peerID = new PeerIdentity(peer_id, local_port, null);
/*     */     try
/*     */     {
/* 172 */       class_mon.enter();
/*     */       
/* 174 */       DataEntry dataEntry = (DataEntry)dataMap.get(data_id);
/*     */       
/* 176 */       if (dataEntry == null)
/*     */       {
/* 178 */         dataEntry = new DataEntry();
/*     */         
/* 180 */         dataMap.put(data_id, dataEntry);
/*     */       }
/*     */       
/* 183 */       String old = dataEntry.addPeer(peerID, ip);
/*     */       boolean bool;
/* 185 */       if (old == null)
/*     */       {
/* 187 */         totalIDs += 1;
/*     */         
/* 189 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 193 */       return false;
/*     */     }
/*     */     finally {
/* 196 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removeIdentity(PeerIdentityDataID data_id, byte[] peer_id, int local_port)
/*     */   {
/*     */     try
/*     */     {
/* 209 */       class_mon.enter();
/*     */       
/* 211 */       DataEntry dataEntry = (DataEntry)dataMap.get(data_id);
/*     */       
/* 213 */       if (dataEntry != null)
/*     */       {
/* 215 */         PeerIdentity peerID = new PeerIdentity(peer_id, local_port, null);
/*     */         
/* 217 */         String old = dataEntry.removePeer(peerID);
/*     */         
/* 219 */         if (old != null)
/*     */         {
/* 221 */           totalIDs -= 1;
/*     */         }
/*     */         else
/*     */         {
/* 225 */           Debug.out("id not present: id=" + peerID.getString());
/*     */         }
/*     */       }
/*     */     } finally {
/* 229 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean containsIdentity(PeerIdentityDataID data_id, byte[] peer_id, int local_port)
/*     */   {
/* 241 */     PeerIdentity peerID = new PeerIdentity(peer_id, local_port, null);
/*     */     try
/*     */     {
/* 244 */       class_mon.enter();
/*     */       
/* 246 */       DataEntry dataEntry = (DataEntry)dataMap.get(data_id);
/*     */       
/* 248 */       if (dataEntry != null)
/*     */       {
/* 250 */         if (dataEntry.hasPeer(peerID))
/*     */         {
/* 252 */           return true;
/*     */         }
/*     */       }
/*     */     } finally {
/* 256 */       class_mon.exit();
/*     */     }
/*     */     
/* 259 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getTotalIdentityCount()
/*     */   {
/* 268 */     return totalIDs;
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
/*     */   public static int getIdentityCount(PeerIdentityDataID data_id)
/*     */   {
/* 281 */     return data_id.getDataEntry().getPeerCount();
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
/*     */   public static boolean containsIPAddress(PeerIdentityDataID data_id, String ip)
/*     */   {
/*     */     try
/*     */     {
/* 296 */       class_mon.enter();
/*     */       
/* 298 */       DataEntry dataEntry = (DataEntry)dataMap.get(data_id);
/*     */       
/* 300 */       if (dataEntry != null)
/*     */       {
/* 302 */         if (dataEntry.hasIP(ip))
/*     */         {
/* 304 */           return true;
/*     */         }
/*     */       }
/*     */     } finally {
/* 308 */       class_mon.exit();
/*     */     }
/*     */     
/* 311 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static final class DataEntry
/*     */   {
/* 317 */     private final Map<PeerIdentityManager.PeerIdentity, String> _peerMap = new LightHashMap();
/*     */     
/*     */ 
/*     */ 
/*     */     private final boolean hasIP(String ip)
/*     */     {
/* 323 */       return this._peerMap.containsValue(ip);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private final boolean hasPeer(PeerIdentityManager.PeerIdentity peer)
/*     */     {
/* 330 */       return this._peerMap.containsKey(peer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private final String addPeer(PeerIdentityManager.PeerIdentity peer, String ip)
/*     */     {
/* 338 */       return (String)this._peerMap.put(peer, ip);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private final String removePeer(PeerIdentityManager.PeerIdentity peer)
/*     */     {
/* 345 */       return (String)this._peerMap.remove(peer);
/*     */     }
/*     */     
/*     */ 
/*     */     private final int getPeerCount()
/*     */     {
/* 351 */       return this._peerMap.size();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/util/PeerIdentityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */