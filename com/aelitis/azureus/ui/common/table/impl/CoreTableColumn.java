/*     */ package com.aelitis.azureus.ui.common.table.impl;
/*     */ 
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
/*     */ public abstract class CoreTableColumn
/*     */   extends TableColumnImpl
/*     */   implements TableColumnExtraInfoListener
/*     */ {
/*     */   public CoreTableColumn(String sName, int iAlignment, int iPosition, int iWidth, String sTableID)
/*     */   {
/*  57 */     super(sTableID, sName);
/*  58 */     super.initialize(iAlignment, iPosition, iWidth);
/*  59 */     setUseCoreDataSource(true);
/*  60 */     addListeners(this);
/*     */   }
/*     */   
/*     */   public CoreTableColumn(Class forDataSourceType, String sName, int iAlignment, int iWidth, String sTableID)
/*     */   {
/*  65 */     super(sTableID, sName);
/*  66 */     addDataSourceType(forDataSourceType);
/*  67 */     super.initialize(iAlignment, -1, iWidth);
/*  68 */     setUseCoreDataSource(true);
/*  69 */     addListeners(this);
/*     */   }
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
/*     */   public CoreTableColumn(String sName, int iPosition, int iWidth, String sTableID)
/*     */   {
/*  86 */     super(sTableID, sName);
/*  87 */     setPosition(iPosition);
/*  88 */     setWidth(iWidth);
/*  89 */     setUseCoreDataSource(true);
/*  90 */     addListeners(this);
/*     */   }
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
/*     */   public CoreTableColumn(String sName, int iWidth, String sTableID)
/*     */   {
/* 105 */     super(sTableID, sName);
/* 106 */     setWidth(iWidth);
/* 107 */     setUseCoreDataSource(true);
/* 108 */     addListeners(this);
/*     */   }
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
/*     */   public CoreTableColumn(String sName, String sTableID)
/*     */   {
/* 123 */     super(sTableID, sName);
/* 124 */     setUseCoreDataSource(true);
/* 125 */     addListeners(this);
/*     */   }
/*     */   
/*     */   public void initializeAsGraphic(int iWidth) {
/* 129 */     setWidth(iWidth);
/* 130 */     setType(2);
/* 131 */     setRefreshInterval(-1);
/* 132 */     setAlignment(3);
/*     */   }
/*     */   
/*     */   public void initializeAsGraphic(int iPosition, int iWidth)
/*     */   {
/* 137 */     setPosition(iPosition);
/* 138 */     setWidth(iWidth);
/* 139 */     setType(2);
/* 140 */     setRefreshInterval(-1);
/* 141 */     setAlignment(3);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/CoreTableColumn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */