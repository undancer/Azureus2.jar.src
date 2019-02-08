/*    */ package com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.HeightCoordinatesImpl;
/*    */ import com.aelitis.azureus.core.dht.netcoords.vivaldi.ver1.impl.VivaldiPositionImpl;
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
/*    */ public class VivaldiPositionFactory
/*    */ {
/*    */   public static VivaldiPosition createPosition()
/*    */   {
/* 31 */     return new VivaldiPositionImpl(new HeightCoordinatesImpl(0.0F, 0.0F, 0.0F));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static VivaldiPosition createPosition(float error)
/*    */   {
/* 38 */     VivaldiPositionImpl np = new VivaldiPositionImpl(new HeightCoordinatesImpl(0.0F, 0.0F, 0.0F));
/*    */     
/* 40 */     np.setErrorEstimate(error);
/*    */     
/* 42 */     return np;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/vivaldi/ver1/VivaldiPositionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */