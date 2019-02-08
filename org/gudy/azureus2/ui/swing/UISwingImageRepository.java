/*    */ package org.gudy.azureus2.ui.swing;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.io.InputStream;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.ImageIcon;
/*    */ import org.gudy.azureus2.ui.common.UIImageRepository;
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
/*    */ public class UISwingImageRepository
/*    */ {
/*    */   public static Image getImage(String name)
/*    */   {
/*    */     try
/*    */     {
/* 47 */       return ImageIO.read(UIImageRepository.getImageAsStream(name));
/*    */ 
/*    */ 
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/*    */ 
/* 54 */       e.printStackTrace();
/*    */     }
/* 56 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static InputStream getImageAsStream(String name)
/*    */   {
/* 64 */     return UIImageRepository.getImageAsStream(name);
/*    */   }
/*    */   
/*    */ 
/*    */   public static Image getImage(InputStream is)
/*    */   {
/*    */     try
/*    */     {
/* 72 */       return ImageIO.read(is);
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 76 */       e.printStackTrace();
/*    */     }
/* 78 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Icon getIcon(String name)
/*    */   {
/* 86 */     Image image = getImage(name);
/*    */     
/* 88 */     return image == null ? null : new ImageIcon(image);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static Icon getIcon(InputStream is)
/*    */   {
/* 95 */     Image image = getImage(is);
/*    */     
/* 97 */     return image == null ? null : new ImageIcon(image);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swing/UISwingImageRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */