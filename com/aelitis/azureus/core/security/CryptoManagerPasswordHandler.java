package com.aelitis.azureus.core.security;

public abstract interface CryptoManagerPasswordHandler
{
  public static final int HANDLER_TYPE_UNKNOWN = 0;
  public static final int HANDLER_TYPE_USER = 1;
  public static final int HANDLER_TYPE_SYSTEM = 2;
  public static final int HANDLER_TYPE_ALL = 3;
  public static final int ACTION_ENCRYPT = 1;
  public static final int ACTION_DECRYPT = 2;
  public static final int ACTION_PASSWORD_SET = 3;
  
  public abstract int getHandlerType();
  
  public abstract passwordDetails getPassword(int paramInt1, int paramInt2, boolean paramBoolean, String paramString);
  
  public abstract void passwordOK(int paramInt, passwordDetails parampasswordDetails);
  
  public static abstract interface passwordDetails
  {
    public abstract char[] getPassword();
    
    public abstract int getPersistForSeconds();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoManagerPasswordHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */