package org.gudy.azureus2.plugins.utils;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public abstract interface LocaleUtilities
{
  public abstract void integrateLocalisedMessageBundle(String paramString);
  
  public abstract void integrateLocalisedMessageBundle(ResourceBundle paramResourceBundle);
  
  public abstract void integrateLocalisedMessageBundle(Properties paramProperties);
  
  public abstract String getLocalisedMessageText(String paramString);
  
  public abstract String getLocalisedMessageText(String paramString, String[] paramArrayOfString);
  
  public abstract boolean hasLocalisedMessageText(String paramString);
  
  public abstract String localise(String paramString);
  
  public abstract LocaleDecoder[] getDecoders();
  
  public abstract void addListener(LocaleListener paramLocaleListener);
  
  public abstract void removeListener(LocaleListener paramLocaleListener);
  
  public abstract Locale getCurrentLocale();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/LocaleUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */