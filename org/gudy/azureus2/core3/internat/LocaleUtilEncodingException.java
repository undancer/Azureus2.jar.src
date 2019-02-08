/*    */ package org.gudy.azureus2.core3.internat;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocaleUtilEncodingException
/*    */   extends Exception
/*    */ {
/*    */   protected String[] valid_charsets;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected String[] valid_names;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean abandoned;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public LocaleUtilEncodingException(String[] charsets, String[] names)
/*    */   {
/* 41 */     this.valid_charsets = charsets;
/* 42 */     this.valid_names = names;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public LocaleUtilEncodingException(Throwable cause)
/*    */   {
/* 49 */     super(cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public LocaleUtilEncodingException(boolean _abandoned)
/*    */   {
/* 56 */     super("Locale selection abandoned");
/*    */     
/* 58 */     this.abandoned = _abandoned;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean getAbandoned()
/*    */   {
/* 64 */     return this.abandoned;
/*    */   }
/*    */   
/*    */ 
/*    */   public String[] getValidCharsets()
/*    */   {
/* 70 */     return this.valid_charsets;
/*    */   }
/*    */   
/*    */ 
/*    */   public String[] getValidTorrentNames()
/*    */   {
/* 76 */     return this.valid_names;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/internat/LocaleUtilEncodingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */