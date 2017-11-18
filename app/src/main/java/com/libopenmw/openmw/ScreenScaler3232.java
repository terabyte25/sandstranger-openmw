package com.libopenmw.openmw;

import android.view.View;
import android.widget.TextView;

public class ScreenScaler3232
{
  public static int height;
  public static int width;
  private static ScreenScaler3232 _instance = null;
  private final int STANDARD_WIDTH = 1024;
  private final int STANDARD_HEIGHT = 768;
  private float scaleRatio_y = 0.0F;
  private float scaleRatio_x = 0.0F;
  private int marginX = 0;
  private int marginY = 0;
  
  private ScreenScaler3232()
  {
    float x = width / 1024.0F;
    float y = height / 768.0F;
    this.scaleRatio_x = Math.max(x, y);
    this.scaleRatio_y = Math.min(x, y);
    this.marginX = ((width - (int)(1024.0F * this.scaleRatio_x)) / 2);
    this.marginY = ((height - (int)(768.0F * this.scaleRatio_y)) / 2);
  }
  
  public static void textScaler(View v, float size)
  {
    float text_height = v.getHeight() / size;
    ((TextView)v).setTextSize(0, text_height);
  }
  
  public static void changeTextSize(View v, final float size)
  {
    v.post(new Runnable()
    {
      public void run()
      {
        ScreenScaler3232.textScaler(v, size);
      }
    });
  }
  
  public static ScreenScaler3232 getInstance()
  {
    if (_instance == null) {
      _instance = new ScreenScaler3232();
    }
    return _instance;
  }
  
  public int getScaledFontSize(int coordinate)
  {
    int result = (int)(coordinate * this.scaleRatio_y);
    return result;
  }
  
  public int getScaledCoordinateX(int coordinate)
  {
    float scaled = coordinate * this.scaleRatio_x;
    int result = this.marginX + (int)(scaled < 1.0F ? coordinate : scaled);
    return result;
  }
  
  public int getScaledCoordinateY(int coordinate)
  {
    float scaled = coordinate * this.scaleRatio_y;
    int result = this.marginY + (int)(scaled < 1.0F ? coordinate : scaled);
    return result;
  }
}
