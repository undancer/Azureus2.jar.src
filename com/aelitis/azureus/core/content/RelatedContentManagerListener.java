package com.aelitis.azureus.core.content;

public abstract interface RelatedContentManagerListener
{
  public abstract void contentFound(RelatedContent[] paramArrayOfRelatedContent);
  
  public abstract void contentChanged(RelatedContent[] paramArrayOfRelatedContent);
  
  public abstract void contentRemoved(RelatedContent[] paramArrayOfRelatedContent);
  
  public abstract void contentChanged();
  
  public abstract void contentReset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/RelatedContentManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */