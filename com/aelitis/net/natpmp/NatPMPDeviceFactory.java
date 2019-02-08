/*    */ package com.aelitis.net.natpmp;
/*    */ 
/*    */ import com.aelitis.net.natpmp.impl.NatPMPDeviceImpl;
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
/*    */ 
/*    */ public class NatPMPDeviceFactory
/*    */ {
/*    */   public static NatPMPDevice getSingleton(NATPMPDeviceAdapter adapter)
/*    */     throws Exception
/*    */   {
/* 33 */     return NatPMPDeviceImpl.getSingletonObject(adapter);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/NatPMPDeviceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */