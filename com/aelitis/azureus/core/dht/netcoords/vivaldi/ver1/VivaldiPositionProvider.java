/*    */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*    */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionProvider;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VivaldiPositionProvider
/*    */   implements DHTNetworkPositionProvider
/*    */ {
/*    */   public byte getPositionType()
/*    */   {
/* 36 */     return 1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTNetworkPosition create(byte[] ID, boolean is_local)
/*    */   {
/* 44 */     return VivaldiPositionFactory.createPosition();
/*    */   }
/*    */   
/*    */ 
/*    */   public DHTNetworkPosition getLocalPosition()
/*    */   {
/* 50 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTNetworkPosition deserialisePosition(DataInputStream is)
/*    */     throws IOException
/*    */   {
/* 59 */     float[] data = new float[4];
/*    */     
/* 61 */     for (int i = 0; i < data.length; i++)
/*    */     {
/* 63 */       data[i] = is.readFloat();
/*    */     }
/*    */     
/* 66 */     VivaldiPosition pos = VivaldiPositionFactory.createPosition();
/*    */     
/* 68 */     pos.fromFloatArray(data);
/*    */     
/* 70 */     return pos;
/*    */   }
/*    */   
/*    */   public void serialiseStats(DataOutputStream os)
/*    */     throws IOException
/*    */   {}
/*    */   
/*    */   public void startUp(DataInputStream is) {}
/*    */   
/*    */   public void shutDown(DataOutputStream os) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/VivaldiPositionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */