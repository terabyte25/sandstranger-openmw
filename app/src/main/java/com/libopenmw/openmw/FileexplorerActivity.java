package com.libopenmw.openmw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FileexplorerActivity
  extends Activity
{
  private static final int REQUEST_PATH = 1;
  String curFileName;
  EditText edittext;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
   // setContentView(R.layout.activity_explorer);
//    this.edittext = ((EditText)findViewById(R.id.editText));
  }
  
  public void getfile(View view)
  {
    Intent intent1 = new Intent(this, FileChooser.class);
    startActivityForResult(intent1, 1);
  }
  
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if ((requestCode == 1) && 
      (resultCode == -1))
    {
      this.curFileName = data.getStringExtra("GetFileName");
      this.edittext.setText(this.curFileName);
    }
  }
}
