package com.libopenmw.openmw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser
  extends AppCompatActivity
{
  private File currentDir;
  public static boolean isDirMode;
  private FileArrayAdapter adapter;
  private static String CURRENT_DIR = "";
  private Toolbar toolbar;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filechoser);
    this.toolbar = ((Toolbar)findViewById(R.id.toolbar));
    setSupportActionBar(this.toolbar);
    this.currentDir = new File("/storage");
    fill(this.currentDir);
    Button useFolder = (Button)findViewById(R.id.buttonFolder);
    if (isDirMode)
    {
      ScreenScaler3232.changeTextSize(useFolder, 2.3F);
      useFolder.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View v)
        {
          try
          {
            FileChooser.this.onDirClick(FileChooser.CURRENT_DIR);
          }
          catch (Exception e) {}
        }
      });
    }
    else
    {
      useFolder.setVisibility(8);
    }
  }
  
  private void fill(File f)
  {
    File[] dirs = f.listFiles();
    this.toolbar.setTitle("Current Dir: " + f.getAbsolutePath());
    CURRENT_DIR = f.getAbsolutePath();
    List<Item> dir = new ArrayList();
    List<Item> fls = new ArrayList();
    try
    {
      for (File ff : dirs)
      {
        Date lastModDate = new Date(ff.lastModified());
        DateFormat formater = DateFormat.getDateTimeInstance();
        String date_modify = formater.format(lastModDate);
        if (ff.isDirectory())
        {
          File[] fbuf = ff.listFiles();
          int buf = 0;
          if (fbuf != null) {
            buf = fbuf.length;
          } else {
            buf = 0;
          }
          String num_item = String.valueOf(buf);
          if (buf == 0) {
            num_item = num_item + " item";
          } else {
            num_item = num_item + " items";
          }
          dir.add(new Item(ff.getName(), num_item, date_modify, ff
            .getAbsolutePath(), "directory_icon"));
        }
        else if ((ff.getName().endsWith(".json")) && (!isDirMode))
        {
          fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff
            .getAbsolutePath(), "file_icon"));
        }
      }
    }
    catch (Exception e) {}
    Collections.sort(dir);
    Collections.sort(fls);
    dir.addAll(fls);
    if (!f.getName().equalsIgnoreCase("/storage")) {
      dir.add(0, new Item("..", "Parent Directory", "", f.getParent(), "directory_up"));
    }
    this.adapter = new FileArrayAdapter(this, R.layout.file_view, dir);
    
    ListView listView = (ListView)findViewById(R.id.listView);
    listView.setAdapter(this.adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
      {
        try
        {
          Item o = FileChooser.this.adapter.getItem(position);
          if ((o.getImage().equalsIgnoreCase("directory_icon")) || 
            (o.getImage().equalsIgnoreCase("directory_up")))
          {
            FileChooser.this.currentDir = new File(o.getPath());
            FileChooser.this.fill(FileChooser.this.currentDir);
          }
          else
          {
            FileChooser.this.onFileClick(o);
          }
        }
        catch (Exception e) {}
      }
    });
  }
  
  private void onFileClick(Item o)
  {
    Intent intent = new Intent();
    
    intent.putExtra("GetFileName", o.getPath());
    setResult(-1, intent);
    finish();
  }
  
  private void onDirClick(String dirPath)
  {
    Intent intent = new Intent();
    intent.putExtra("GetDir", CURRENT_DIR);
    setResult(-1, intent);
    finish();
  }
}
