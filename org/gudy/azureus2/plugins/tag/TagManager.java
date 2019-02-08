package org.gudy.azureus2.plugins.tag;

import java.util.List;

public abstract interface TagManager
{
  public abstract List<Tag> getTags();
  
  public abstract Tag lookupTag(String paramString);
  
  public abstract Tag createTag(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tag/TagManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */