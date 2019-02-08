package org.gudy.azureus2.plugins.utils.xml.rss;

public abstract interface RSSFeed
{
  public abstract boolean isAtomFeed();
  
  public abstract RSSChannel[] getChannels();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/xml/rss/RSSFeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */