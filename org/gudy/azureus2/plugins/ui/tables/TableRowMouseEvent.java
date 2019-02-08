package org.gudy.azureus2.plugins.ui.tables;

public class TableRowMouseEvent
{
  public static final int EVENT_MOUSEDOWN = 0;
  public static final int EVENT_MOUSEUP = 1;
  public static final int EVENT_MOUSEDOUBLECLICK = 2;
  public static final int EVENT_MOUSEMOVE = 3;
  public static final int EVENT_MOUSEENTER = 4;
  public static final int EVENT_MOUSEEXIT = 5;
  public int eventType;
  public int x;
  public int y;
  public int button;
  public int keyboardState;
  public boolean skipCoreFunctionality;
  public Object data;
  public TableRow row;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableRowMouseEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */