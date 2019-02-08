/*     */ package com.aelitis.azureus.ui.swt.columns.tag;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureExecOnAssign;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class ColumnTagProperties
/*     */   implements TableCellRefreshListener, TableColumnExtraInfoListener
/*     */ {
/*  41 */   public static String COLUMN_ID = "tag.properties";
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  47 */     info.addCategories(new String[] { "settings" });
/*     */     
/*  49 */     info.setProficiency((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ColumnTagProperties(TableColumn column)
/*     */   {
/*  56 */     column.setWidth(160);
/*  57 */     column.setRefreshInterval(-2);
/*  58 */     column.addListeners(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  65 */     Tag tag = (Tag)cell.getDataSource();
/*     */     
/*  67 */     String text = "";
/*     */     
/*  69 */     if ((tag instanceof TagFeatureProperties))
/*     */     {
/*  71 */       TagFeatureProperties tp = (TagFeatureProperties)tag;
/*     */       
/*  73 */       TagFeatureProperties.TagProperty[] props = tp.getSupportedProperties();
/*     */       
/*  75 */       if (props.length > 0)
/*     */       {
/*  77 */         for (TagFeatureProperties.TagProperty prop : props)
/*     */         {
/*  79 */           String prop_str = prop.getString();
/*     */           
/*  81 */           if (prop_str.length() > 0)
/*     */           {
/*  83 */             text = text + (text.length() == 0 ? "" : "; ") + prop_str;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  89 */     if ((tag instanceof TagFeatureExecOnAssign))
/*     */     {
/*  91 */       TagFeatureExecOnAssign eoa = (TagFeatureExecOnAssign)tag;
/*     */       
/*  93 */       int actions = eoa.getSupportedActions();
/*     */       
/*  95 */       if (actions != 0)
/*     */       {
/*  97 */         String actions_str = "";
/*     */         
/*  99 */         int[] action_ids = { 1, 2, 8, 16, 4, 32, 64, 128 };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */         String[] action_keys = { "v3.MainWindow.button.delete", "v3.MainWindow.button.start", "v3.MainWindow.button.forcestart", "v3.MainWindow.button.notforcestart", "v3.MainWindow.button.stop", "label.script", "v3.MainWindow.button.pause", "v3.MainWindow.button.resume" };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 119 */         for (int i = 0; i < action_ids.length; i++)
/*     */         {
/* 121 */           int action_id = action_ids[i];
/*     */           
/* 123 */           if (eoa.supportsAction(action_id))
/*     */           {
/* 125 */             boolean enabled = eoa.isActionEnabled(action_id);
/*     */             
/* 127 */             if (enabled)
/*     */             {
/* 129 */               if (action_id == 32)
/*     */               {
/* 131 */                 String script = eoa.getActionScript();
/*     */                 
/* 133 */                 if (script.length() > 63) {
/* 134 */                   script = script.substring(0, 60) + "...";
/*     */                 }
/*     */                 
/* 137 */                 actions_str = actions_str + (actions_str.length() == 0 ? "" : ",") + MessageText.getString(action_keys[i]) + "=" + script;
/*     */               }
/*     */               else
/*     */               {
/* 141 */                 actions_str = actions_str + (actions_str.length() == 0 ? "" : ",") + MessageText.getString(action_keys[i]) + "=Y";
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 148 */         if (actions_str.length() > 0)
/*     */         {
/* 150 */           text = text + (text.length() == 0 ? "" : "; ") + MessageText.getString("label.exec.on.assign") + ": ";
/*     */           
/* 152 */           text = text + actions_str;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 158 */     if ((!cell.setSortValue(text)) && (cell.isValid()))
/*     */     {
/* 160 */       return;
/*     */     }
/*     */     
/* 163 */     if (!cell.isShown())
/*     */     {
/* 165 */       return;
/*     */     }
/*     */     
/* 168 */     cell.setText(text);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/tag/ColumnTagProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */