/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASNListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
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
/*     */ public class ASItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener
/*     */ {
/*     */   public static final String COLUMN_ID = "as";
/*     */   
/*     */   public ASItem(String table_id)
/*     */   {
/*  47 */     super("as", 1, -1, 100, table_id);
/*  48 */     setRefreshInterval(-2);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  52 */     info.addCategories(new String[] { "identification" });
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  58 */     final PEPeer peer = (PEPeer)cell.getDataSource();
/*     */     
/*  60 */     String text = "";
/*     */     
/*  62 */     if (peer != null)
/*     */     {
/*  64 */       text = (String)peer.getUserData(ASItem.class);
/*     */       
/*  66 */       if (text == null)
/*     */       {
/*  68 */         text = "";
/*     */         
/*  70 */         peer.setUserData(ASItem.class, text);
/*     */         
/*  72 */         String peer_ip = peer.getIp();
/*     */         
/*  74 */         if (AENetworkClassifier.categoriseAddress(peer_ip) == "Public") {
/*     */           try
/*     */           {
/*  77 */             NetworkAdmin.getSingleton().lookupASN(InetAddress.getByName(peer_ip), new NetworkAdminASNListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void success(NetworkAdminASN asn)
/*     */               {
/*     */ 
/*     */ 
/*  85 */                 peer.setUserData(ASItem.class, asn.getAS() + " - " + asn.getASName());
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void failed(NetworkAdminException error) {}
/*     */             });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 101 */     if ((!cell.setSortValue(text)) && (cell.isValid()))
/*     */     {
/* 103 */       return;
/*     */     }
/*     */     
/* 106 */     cell.setText(text);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/ASItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */