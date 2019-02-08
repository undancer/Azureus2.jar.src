package org.gudy.azureus2.plugins.ui.tables;

public abstract interface TableColumnInfo
{
  public static final byte PROFICIENCY_BEGINNER = 0;
  public static final byte PROFICIENCY_INTERMEDIATE = 1;
  public static final byte PROFICIENCY_ADVANCED = 2;
  
  public abstract String[] getCategories();
  
  public abstract void addCategories(String[] paramArrayOfString);
  
  public abstract byte getProficiency();
  
  public abstract void setProficiency(byte paramByte);
  
  public abstract TableColumn getColumn();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableColumnInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */