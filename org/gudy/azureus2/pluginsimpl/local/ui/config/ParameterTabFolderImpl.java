/*    */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.plugins.ui.config.ParameterGroup;
/*    */ import org.gudy.azureus2.plugins.ui.config.ParameterTabFolder;
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
/*    */ public class ParameterTabFolderImpl
/*    */   extends ParameterImpl
/*    */   implements ParameterTabFolder
/*    */ {
/* 36 */   private List<ParameterGroupImpl> groups = new ArrayList();
/*    */   
/*    */ 
/*    */   public ParameterTabFolderImpl()
/*    */   {
/* 41 */     super(null, "", "");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void addTab(ParameterGroup _group)
/*    */   {
/* 48 */     ParameterGroupImpl group = (ParameterGroupImpl)_group;
/*    */     
/* 50 */     this.groups.add(group);
/*    */     
/* 52 */     group.setTabFolder(this);
/*    */   }
/*    */   
/*    */   public void removeTab(ParameterGroup group) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ParameterTabFolderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */