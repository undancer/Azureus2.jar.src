package org.gudy.azureus2.plugins.tag;

import java.util.List;

public abstract interface Tag
{
  public abstract String getTagName();
  
  public abstract List<Taggable> getTaggables();
  
  public abstract void addListener(TagListener paramTagListener);
  
  public abstract void removeListener(TagListener paramTagListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tag/Tag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */