package org.gudy.azureus2.plugins.ui.config;

public abstract interface PasswordParameter
  extends Parameter
{
  public static final int ET_PLAIN = 1;
  public static final int ET_SHA1 = 2;
  public static final int ET_MD5 = 3;
  
  public abstract byte[] getValue();
  
  public abstract void setValue(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/PasswordParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */