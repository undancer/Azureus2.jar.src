/*    */ package org.gudy.azureus2.pluginsimpl.local.dht.mainline;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHandshake;
/*    */ import org.gudy.azureus2.core3.global.GlobalManager;
/*    */ import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
/*    */ import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
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
/*    */ public class MainlineDHTManagerImpl
/*    */   implements MainlineDHTManager
/*    */ {
/*    */   private AzureusCore core;
/*    */   
/*    */   public MainlineDHTManagerImpl(AzureusCore core)
/*    */   {
/* 36 */     this.core = core;
/*    */   }
/*    */   
/*    */   public void setProvider(MainlineDHTProvider provider) {
/* 40 */     MainlineDHTProvider old_provider = this.core.getGlobalManager().getMainlineDHTProvider();
/* 41 */     this.core.getGlobalManager().setMainlineDHTProvider(provider);
/*    */     
/*    */ 
/* 44 */     if ((old_provider == null) && (provider != null)) {
/* 45 */       BTHandshake.setMainlineDHTEnabled(true);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     }
/* 56 */     else if ((old_provider != null) && (provider == null)) {
/* 57 */       BTHandshake.setMainlineDHTEnabled(false);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public MainlineDHTProvider getProvider()
/*    */   {
/* 64 */     return this.core.getGlobalManager().getMainlineDHTProvider();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/dht/mainline/MainlineDHTManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */