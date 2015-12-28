package com.projects.rajiv.passwordprotector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Constants.Constant;
import Helper.SecurityHelper;

public class FileBrowser extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
     SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        final Button browseFile = (Button)findViewById(R.id.browseFile);
        browseFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);


        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select text file"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(FileBrowser.this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    List<String> PreiousCategories=new ArrayList<String>();
                  db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                    Cursor c=db.rawQuery("select cat.name from Category as cat", null);
                    while(c.moveToNext()) {
                        String category=c.getString(0);
                        PreiousCategories.add(category);
                    }

                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = uri.getPath();
                    File input = new File(path);
                    try {

                        FileReader fileReader=new FileReader(input);
                        BufferedReader reader=new BufferedReader(fileReader);
                        String line=null;
                        String seedValue =Constant.EncryptionSeed;
                        String lastModified=new Date().toString();
                        while ((line=reader.readLine())!=null) {
                          String[] arr =split(line,'|');// line.split("|");
                            if (GetArrayLenght(arr) == 3 || GetArrayLenght(arr) == 4) {
                                String Cat=arr[0];
                                String Description=arr[1];
                                String Password=arr[2];
                                String Username="";
                                if(GetArrayLenght(arr) == 4)
                                {
                                    Username=arr[3];
                                }
                               
                               Cat= SecurityHelper.decrypt(seedValue,Cat);
                                    Description=SecurityHelper.decrypt(seedValue,Description);
                                    Password=SecurityHelper.decrypt(seedValue,Password);
                                    Username=SecurityHelper.decrypt(seedValue,Username);


                              if(PreiousCategories.contains(Cat))
                              {
                                  //add password in previous category
                                  Cursor catIdCursor=db.rawQuery("SELECT _id FROM Category where name='" + Cat + "'", null);
                                  catIdCursor.moveToNext();
                                  String CategoryId=catIdCursor.getString(0);

                                  db.execSQL("INSERT INTO Passwords(description,password,lastModifiedDate,username,categoryId)VALUES('" + Description + "','"+Password+"','"+lastModified+"','"+Username+"',"+CategoryId+")");
                              }
                                else
                              {
                                  //add new category
                                  db.execSQL("INSERT INTO Category(name)VALUES('" + Cat + "')");

                                  Cursor catIdCursor=db.rawQuery("SELECT _id FROM Category where name='"+Cat+"'", null);
                                  catIdCursor.moveToNext();
                                  String CategoryId=catIdCursor.getString(0);

                                  //Add password in added category
                                  db.execSQL("INSERT INTO Passwords(description,password,lastModifiedDate,username,categoryId)VALUES('" + Description + "','"+Password+"','"+lastModified+"','"+Username+"',"+CategoryId+")");

                                  PreiousCategories.add(Cat);
                              }
                               //Toast.makeText(FileBrowser.this, "Working ..." ,Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(FileBrowser.this, "Done",Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
            /* Exception handler must be here! */
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    int GetArrayLenght(String[] arr)
    {
        int i=0;
        for (String element: arr) {
            i++;
        }
        return i;
    }

    public class PreiousPasswords
    {
        public int Id;
        public String Password;

    }

    public static String[] split(String string, char separator) {
        int count = 1;
        for (int index = 0; index < string.length(); index++)
            if (string.charAt(index) == separator)
                count++;
        String parts[] = new String[count];
        int partIndex = 0;
        int startIndex = 0;
        for (int index = 0; index < string.length(); index++)
            if (string.charAt(index) == separator) {
                parts[partIndex++] = string.substring(startIndex, index);
                startIndex = index + 1;
            }
        parts[partIndex++] = string.substring(startIndex);
        return parts;
    }
}


