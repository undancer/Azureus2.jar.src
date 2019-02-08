/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
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
/*    */ public class LimitControlSetting
/*    */ {
/*    */   float value;
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
/*    */   public LimitControlSetting(float startValue)
/*    */   {
/* 27 */     this.value = startValue;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public float getValue()
/*    */   {
/* 35 */     return this.value;
/*    */   }
/*    */   
/*    */   public void adjust(float increment) {
/* 39 */     this.value += increment;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/LimitControlSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */