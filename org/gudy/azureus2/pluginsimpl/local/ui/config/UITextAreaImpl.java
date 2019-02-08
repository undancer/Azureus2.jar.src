/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeListener;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
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
/*     */ public class UITextAreaImpl
/*     */   extends ParameterImpl
/*     */   implements UITextArea
/*     */ {
/*     */   private org.gudy.azureus2.pluginsimpl.local.ui.components.UITextAreaImpl text_area;
/*     */   
/*     */   public UITextAreaImpl(PluginConfigImpl config, String resource_name)
/*     */   {
/*  42 */     super(config, resource_name, resource_name);
/*     */     
/*  44 */     this.text_area = new org.gudy.azureus2.pluginsimpl.local.ui.components.UITextAreaImpl();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/*  51 */     this.text_area.setText(text);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void appendText(String text)
/*     */   {
/*  58 */     this.text_area.appendText(text);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getText()
/*     */   {
/*  64 */     return this.text_area.getText();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaximumSize(int max_size)
/*     */   {
/*  71 */     this.text_area.setMaximumSize(max_size);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/*  78 */     super.setEnabled(enabled);
/*     */     
/*  80 */     this.text_area.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getEnabled()
/*     */   {
/*  86 */     return super.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setVisible(boolean visible)
/*     */   {
/*  93 */     super.setEnabled(visible);
/*     */     
/*  95 */     this.text_area.setEnabled(visible);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getVisible()
/*     */   {
/* 101 */     return super.isVisible();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String property_type, Object property_value)
/*     */   {
/* 109 */     this.text_area.setProperty(property_type, property_value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(String property_type)
/*     */   {
/* 116 */     return this.text_area.getProperty(property_type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPropertyChangeListener(UIPropertyChangeListener l)
/*     */   {
/* 123 */     this.text_area.addPropertyChangeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePropertyChangeListener(UIPropertyChangeListener l)
/*     */   {
/* 130 */     this.text_area.removePropertyChangeListener(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/UITextAreaImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */