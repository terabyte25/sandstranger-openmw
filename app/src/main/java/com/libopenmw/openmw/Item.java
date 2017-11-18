package com.libopenmw.openmw;

public class Item
  implements Comparable<Item>
{
  private String name;
  private String data;
  private String date;
  private String path;
  private String image;
  
  public Item(String n, String d, String dt, String p, String img)
  {
    this.name = n;
    this.data = d;
    this.date = dt;
    this.path = p;
    this.image = img;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getData()
  {
    return this.data;
  }
  
  public String getDate()
  {
    return this.date;
  }
  
  public String getPath()
  {
    return this.path;
  }
  
  public String getImage()
  {
    return this.image;
  }
  
  public int compareTo(Item o)
  {
    if (this.name != null) {
      return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
    }
    throw new IllegalArgumentException();
  }
}
