package com.libopenmw.openmw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class FileArrayAdapter
  extends ArrayAdapter<Item>
{
  private Context c;
  private int id;
  private List<Item> items;
  
  public FileArrayAdapter(Context context, int textViewResourceId, List<Item> objects)
  {
    super(context, textViewResourceId, objects);
    this.c = context;
    this.id = textViewResourceId;
    this.items = objects;
  }
  
  public Item getItem(int i)
  {
    return (Item)this.items.get(i);
  }
  
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View v = convertView;
    if (v == null)
    {
      LayoutInflater vi = (LayoutInflater)this.c.getSystemService("layout_inflater");
      v = vi.inflate(this.id, null);
    }
    Item o = (Item)this.items.get(position);
    if (o != null)
    {
      TextView t1 = (TextView)v.findViewById(R.id.TextView01);
      TextView t2 = (TextView)v.findViewById(R.id.TextView02);
      TextView t3 = (TextView)v.findViewById(R.id.TextViewDate);
      
      ImageView imageCity = (ImageView)v.findViewById(R.id.fd_Icon1);
      String uri = "drawable/" + o.getImage();
      int imageResource = this.c.getResources().getIdentifier(uri, null, this.c.getPackageName());
      Drawable image = this.c.getResources().getDrawable(imageResource);
      imageCity.setImageDrawable(image);
      if (t1 != null) {
        t1.setText(o.getName());
      }
      if (t2 != null) {
        t2.setText(o.getData());
      }
      if (t3 != null) {
        t3.setText(o.getDate());
      }
    }
    return v;
  }
}
