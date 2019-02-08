/*     */ package org.gudy.azureus2.ui.swt.views.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.plugins.PluginEvent;
/*     */ import org.gudy.azureus2.plugins.PluginEventListener;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.plugins.utils.LocationProvider;
/*     */ import org.gudy.azureus2.plugins.utils.LocationProviderListener;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
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
/*     */ public class LocProvUtils
/*     */   implements LocationProviderListener
/*     */ {
/*     */   private static LocProvUtils singleton;
/*     */   private AzureusCore core;
/*     */   private LocationProvider active_provider;
/*     */   private boolean cl_installed;
/*     */   
/*     */   public static void initialise(AzureusCore core)
/*     */   {
/*  55 */     synchronized (LocProvUtils.class)
/*     */     {
/*  57 */       if (singleton == null)
/*     */       {
/*  59 */         singleton = new LocProvUtils(core);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private List<TableColumn> columns = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private LocProvUtils(AzureusCore _core)
/*     */   {
/*  76 */     this.core = _core;
/*     */     
/*  78 */     PluginManager pm = this.core.getPluginManager();
/*     */     
/*  80 */     PluginInterface pi = pm.getDefaultPluginInterface();
/*     */     
/*  82 */     pi.getUtilities().addLocationProviderListener(this);
/*     */     
/*  84 */     PluginInterface cl_pi = pm.getPluginInterfaceByID("CountryLocator");
/*     */     
/*  86 */     if ((cl_pi != null) && (cl_pi.getPluginState().isOperational()))
/*     */     {
/*  88 */       this.cl_installed = true;
/*     */     }
/*     */     
/*  91 */     pi.addEventListener(new PluginEventListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(PluginEvent ev)
/*     */       {
/*     */ 
/*  98 */         if (ev.getType() == 10)
/*     */         {
/* 100 */           String id = (String)ev.getValue();
/*     */           
/* 102 */           if (id.equals("CountryLocator"))
/*     */           {
/* 104 */             LocProvUtils.this.cl_installed = true;
/*     */             
/* 106 */             LocProvUtils.this.removeColumns();
/*     */           }
/* 108 */         } else if (ev.getType() == 12)
/*     */         {
/* 110 */           String id = (String)ev.getValue();
/*     */           
/*     */ 
/*     */ 
/* 114 */           if (id.equals("CountryLocator"))
/*     */           {
/* 116 */             LocProvUtils.this.cl_installed = false;
/*     */             
/* 118 */             LocProvUtils.this.addColumns();
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void locationProviderAdded(LocationProvider lp)
/*     */   {
/* 129 */     synchronized (this)
/*     */     {
/* 131 */       if (this.active_provider == null)
/*     */       {
/* 133 */         if (lp.hasCapabilities(7L))
/*     */         {
/* 135 */           this.active_provider = lp;
/*     */           
/* 137 */           addColumns();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void locationProviderRemoved(LocationProvider lp)
/*     */   {
/* 147 */     synchronized (this)
/*     */     {
/* 149 */       if (lp == this.active_provider)
/*     */       {
/* 151 */         this.active_provider = null;
/*     */         
/* 153 */         removeColumns();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getCountryCode(Peer peer)
/*     */   {
/* 162 */     String[] details = PeerUtils.getCountryDetails(peer);
/*     */     
/* 164 */     return (details == null) || (details.length < 1) ? "" : details[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getCountryName(Peer peer)
/*     */   {
/* 171 */     String[] details = PeerUtils.getCountryDetails(peer);
/*     */     
/* 173 */     return (details == null) || (details.length < 1) ? "" : details[1];
/*     */   }
/*     */   
/*     */ 
/*     */   private void addColumns()
/*     */   {
/* 179 */     synchronized (this)
/*     */     {
/* 181 */       if ((this.cl_installed) || (this.active_provider == null))
/*     */       {
/* 183 */         return;
/*     */       }
/*     */       
/* 186 */       TableManager tm = this.core.getPluginManager().getDefaultPluginInterface().getUIManager().getTableManager();
/*     */       
/*     */ 
/* 189 */       String[] peer_tables = { "Peers", "AllPeers" };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 196 */       for (int i = 0; i < peer_tables.length; i++)
/*     */       {
/* 198 */         TableColumn iso3166Column = tm.createColumn(peer_tables[i], "CountryCode");
/*     */         
/* 200 */         iso3166Column.initialize(1, -2, 30, -3);
/*     */         
/* 202 */         iso3166Column.addCellRefreshListener(new TableCellRefreshListener() {
/*     */           public void refresh(TableCell cell) {
/* 204 */             Peer peer = (Peer)cell.getDataSource();
/*     */             
/* 206 */             String s = LocProvUtils.this.getCountryCode(peer);
/*     */             
/* 208 */             if ((!cell.setSortValue(s)) && (cell.isValid()))
/*     */             {
/* 210 */               return;
/*     */             }
/*     */             
/* 213 */             cell.setText(s);
/*     */           }
/*     */           
/* 216 */         });
/* 217 */         tm.addColumn(iso3166Column);
/*     */         
/* 219 */         this.columns.add(iso3166Column);
/*     */         
/*     */ 
/* 222 */         TableColumn countryColumn = tm.createColumn(peer_tables[i], "Country");
/*     */         
/* 224 */         countryColumn.initialize(1, -1, 80, -3);
/*     */         
/* 226 */         countryColumn.addCellRefreshListener(new TableCellRefreshListener() {
/*     */           public void refresh(TableCell cell) {
/* 228 */             Peer peer = (Peer)cell.getDataSource();
/*     */             
/* 230 */             String s = LocProvUtils.this.getCountryName(peer);
/*     */             
/* 232 */             if ((!cell.setSortValue(s)) && (cell.isValid())) {
/* 233 */               return;
/*     */             }
/*     */             
/* 236 */             cell.setText(s);
/*     */           }
/*     */           
/* 239 */         });
/* 240 */         tm.addColumn(countryColumn);
/*     */         
/* 242 */         this.columns.add(countryColumn);
/*     */         
/*     */ 
/*     */ 
/* 246 */         TableColumn flagsColumn = tm.createColumn(peer_tables[i], "CountryFlagSmall");
/*     */         
/* 248 */         flagsColumn.initialize(1, -1, 25, -3);
/*     */         
/* 250 */         flagsColumn.setType(2);
/*     */         
/* 252 */         FlagListener flagListener = new FlagListener(true);
/*     */         
/* 254 */         flagsColumn.addCellRefreshListener(flagListener);
/*     */         
/* 256 */         flagsColumn.addCellToolTipListener(flagListener);
/*     */         
/* 258 */         tm.addColumn(flagsColumn);
/*     */         
/* 260 */         this.columns.add(flagsColumn);
/*     */         
/*     */ 
/*     */ 
/* 264 */         flagsColumn = tm.createColumn(peer_tables[i], "CountryFlag");
/*     */         
/* 266 */         flagsColumn.initialize(1, -2, 25, -3);
/*     */         
/* 268 */         flagsColumn.setType(2);
/*     */         
/* 270 */         flagListener = new FlagListener(false);
/*     */         
/* 272 */         flagsColumn.addCellRefreshListener(flagListener);
/*     */         
/* 274 */         flagsColumn.addCellToolTipListener(flagListener);
/*     */         
/* 276 */         tm.addColumn(flagsColumn);
/*     */         
/* 278 */         this.columns.add(flagsColumn);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void removeColumns()
/*     */   {
/* 286 */     synchronized (this)
/*     */     {
/* 288 */       for (TableColumn c : this.columns)
/*     */       {
/* 290 */         c.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class FlagListener
/*     */     implements TableCellRefreshListener, TableCellToolTipListener
/*     */   {
/*     */     private final boolean small;
/*     */     
/*     */ 
/*     */     public FlagListener(boolean _small)
/*     */     {
/* 305 */       this.small = _small;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void refresh(TableCell cell)
/*     */     {
/* 312 */       Peer peer = (Peer)cell.getDataSource();
/*     */       
/* 314 */       Image image = ImageRepository.getCountryFlag(peer, this.small);
/*     */       
/* 316 */       String cc = LocProvUtils.this.getCountryCode(peer);
/*     */       
/* 318 */       if ((!cell.setSortValue(cc)) && (cell.isValid()))
/*     */       {
/* 320 */         return;
/*     */       }
/*     */       
/* 323 */       cell.setGraphic(new UISWTGraphicImpl(image));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void cellHover(TableCell cell)
/*     */     {
/* 330 */       Peer peer = (Peer)cell.getDataSource();
/*     */       
/* 332 */       String[] details = PeerUtils.getCountryDetails(peer);
/*     */       
/* 334 */       if ((details == null) || (details.length < 2))
/*     */       {
/* 336 */         cell.setToolTip("");
/*     */       }
/*     */       else
/*     */       {
/* 340 */         cell.setToolTip(details[0] + " - " + details[1]);
/*     */       }
/*     */     }
/*     */     
/*     */     public void cellHoverComplete(TableCell cell) {
/* 345 */       cell.setToolTip(null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/utils/LocProvUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */