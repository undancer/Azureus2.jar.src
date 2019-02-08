package com.aelitis.azureus.ui.swt.search;

public abstract interface SearchResultsTabAreaBase
{
  public abstract void anotherSearch(SearchResultsTabArea.SearchQuery paramSearchQuery);
  
  public abstract int getResultCount();
  
  public abstract void showView();
  
  public abstract void refreshView();
  
  public abstract void hideView();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/search/SearchResultsTabAreaBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */