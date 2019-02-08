/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterGroup;
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
/*     */ public class ParameterGroupImpl
/*     */   extends ParameterImpl
/*     */   implements ParameterGroup
/*     */ {
/*     */   private String resource;
/*     */   private ParameterImpl[] parameters;
/*  38 */   private int num_columns = 1;
/*     */   
/*     */ 
/*     */   private ParameterTabFolderImpl tab_folder;
/*     */   
/*     */ 
/*     */ 
/*     */   public ParameterGroupImpl(String _resource, Parameter[] _parameters)
/*     */   {
/*  47 */     super(null, "", "");
/*     */     
/*  49 */     this.resource = _resource;
/*     */     
/*  51 */     if (_parameters != null)
/*     */     {
/*  53 */       this.parameters = new ParameterImpl[_parameters.length];
/*     */       
/*  55 */       for (int i = 0; i < _parameters.length; i++)
/*     */       {
/*  57 */         ParameterImpl parameter = (ParameterImpl)_parameters[i];
/*     */         
/*  59 */         this.parameters[i] = parameter;
/*     */         
/*  61 */         if (parameter != null)
/*     */         {
/*  63 */           parameter.setGroup(this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTabFolder(ParameterTabFolderImpl tf)
/*     */   {
/*  73 */     this.tab_folder = tf;
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterTabFolderImpl getTabFolder()
/*     */   {
/*  79 */     return this.tab_folder;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResourceName()
/*     */   {
/*  85 */     return this.resource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumberOfColumns(int num)
/*     */   {
/*  92 */     this.num_columns = num;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberColumns()
/*     */   {
/*  98 */     return this.num_columns;
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterImpl[] getParameters()
/*     */   {
/* 104 */     return this.parameters;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/ParameterGroupImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */