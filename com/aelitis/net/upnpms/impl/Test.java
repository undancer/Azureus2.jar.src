/*    */ package com.aelitis.net.upnpms.impl;
/*    */ 
/*    */ import com.aelitis.net.upnpms.UPNPMSBrowser;
/*    */ import com.aelitis.net.upnpms.UPNPMSBrowserListener;
/*    */ import com.aelitis.net.upnpms.UPNPMSContainer;
/*    */ import com.aelitis.net.upnpms.UPNPMSNode;
/*    */ import java.io.PrintStream;
/*    */ import java.net.URL;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
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
/*    */ public class Test
/*    */ {
/*    */   private static void dump(UPNPMSContainer container, String indent)
/*    */     throws Exception
/*    */   {
/* 39 */     System.out.println(indent + container.getTitle() + " - " + container.getID());
/*    */     
/* 41 */     indent = indent + "    ";
/*    */     
/* 43 */     List<UPNPMSNode> kids = container.getChildren();
/*    */     
/* 45 */     for (UPNPMSNode kid : kids)
/*    */     {
/* 47 */       if ((kid instanceof UPNPMSContainer))
/*    */       {
/* 49 */         dump((UPNPMSContainer)kid, indent);
/*    */       }
/*    */       else {
/* 52 */         System.out.println(indent + kid.getTitle() + " - " + kid.getID());
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try
/*    */     {
/* 63 */       UPNPMSBrowser browser = new UPNPMSBrowserImpl("Vuze", Arrays.asList(new URL[] { new URL("http://192.168.1.5:2869/upnphost/udhisapi.dll?control=uuid:82aaab53-afaf-4d8f-bdd8-c1e438e7a348+urn:upnp-org:serviceId:ContentDirectory") }), new UPNPMSBrowserListener()
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         public void setPreferredURL(URL url) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 74 */       });
/* 75 */       UPNPMSContainer root = browser.getRoot();
/*    */       
/* 77 */       dump(root, "");
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 81 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/impl/Test.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */