/*    */ package com.aelitis.azureus.ui.swt.subscriptions;
/*    */ 
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*    */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*    */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
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
/*    */ public class SubscriptionView
/*    */   implements SubscriptionsViewBase, UISWTViewCoreEventListenerEx
/*    */ {
/*    */   private final SubscriptionsViewBase impl;
/*    */   
/*    */   public SubscriptionView()
/*    */   {
/* 48 */     this.impl = new SubscriptionViewInternalNative();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isCloneable()
/*    */   {
/* 54 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   public UISWTViewCoreEventListener getClone()
/*    */   {
/* 60 */     return new SubscriptionView();
/*    */   }
/*    */   
/*    */ 
/*    */   public void refreshView()
/*    */   {
/* 66 */     this.impl.refreshView();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean eventOccurred(UISWTViewEvent event)
/*    */   {
/* 73 */     return this.impl.eventOccurred(event);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */