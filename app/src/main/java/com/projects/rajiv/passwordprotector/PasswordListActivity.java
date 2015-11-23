package com.projects.rajiv.passwordprotector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import Adapters.CategoryAdapter;
import Adapters.PasswordAdapter;
import Viewmodels.CategoryViewModel;
import Viewmodels.PasswordViewModel;

public class PasswordListActivity extends AppCompatActivity {
    ListView listview;
    ListView Searchclistview;
    ArrayList<CategoryViewModel> list;
    CategoryAdapter adapter;
    CategoryViewModel SelectedItem;


    ArrayList<CategoryViewModel> searchedList;
    CategoryAdapter searchedAdapter;
    CategoryViewModel searchedSelectedItem;

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
          db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);
        final Button button = (Button) findViewById(R.id.AddCategory);
        final TextView searchedLable = (TextView) findViewById(R.id.SearchedResultLabel);
        listview = (ListView) findViewById(R.id.passwordList);
        Searchclistview = (ListView) findViewById(R.id.SearchedPasswordList);
        final EditText searchEditText = (EditText) findViewById(R.id.SearchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before,
                                                                            int count) {
                String searchedText=s.toString();
                                                      if (!searchedText.isEmpty()) {
                                                         ArrayList<PasswordViewModel> searchedPasswordList = new ArrayList<PasswordViewModel>();

                                                          Cursor c=db.rawQuery("SELECT _id,description,password,categoryId,username FROM Passwords where description like '%"+searchedText+"%' or username like '%"+searchedText+"%'", null);
                                                          while(c.moveToNext())
                                                          {
                                                              String id=c.getString(0);
                                                              String description=c.getString(1);
                                                              String password=c.getString(2);
                                                              String categoryId=c.getString(3);
                                                              String uname=c.getString(4);

                                                              searchedPasswordList.add(new PasswordViewModel(id,description,password,categoryId,"",uname));
                                                          }

                                                          PasswordAdapter searchedAdapter =  new PasswordAdapter(PasswordListActivity.this,R.layout.passwordlist, searchedPasswordList);
                                                          searchedAdapter.notifyDataSetChanged();
                                                          Searchclistview.setAdapter(searchedAdapter);



                                                          Searchclistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                              @Override
                                                              public void onItemClick(AdapterView<?> parent, final View view,
                                                                                      int position, long id) {
                                                                  PasswordViewModel passwordViewModel = (PasswordViewModel) parent.getItemAtPosition(position);
                                                                  String details = "";
                                                                  if (passwordViewModel.Username == null || passwordViewModel.Username.equals("")) {
                                                                      details = "Password: " + passwordViewModel.Password;
                                                                  } else {
                                                                      details = "Username: " + passwordViewModel.Username + "\n\nPassword: " + passwordViewModel.Password;
                                                                  }
                                                                  new AlertDialog.Builder(PasswordListActivity.this)
                                                                          .setTitle(passwordViewModel.Description)
                                                                          .setMessage(details)
                                                                          .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                                              public void onClick(DialogInterface dialog, int which) {
                                                                                  // do nothing
                                                                              }
                                                                          })
                                                                          .setIcon(R.drawable.password_icon)
                                                                          .show();
                                                              }

                                                          });

                                                          listview.setVisibility(View.GONE);
                                                          Searchclistview.setVisibility(View.VISIBLE);
                                                          button.setVisibility(View.GONE);
                                                          searchedLable.setVisibility(View.VISIBLE);
                                                      }
                else
                                                      {
                                                          searchedLable.setVisibility(View.GONE);
                                                          button.setVisibility(View.VISIBLE);
                                                          listview.setVisibility(View.VISIBLE);
                                                          Searchclistview.setVisibility(View.GONE);
                                                      }

                                                  }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerForContextMenu(listview);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parentView, View childView, int position, long id) {
                // this will provide the value
                SelectedItem = (CategoryViewModel) listview.getItemAtPosition(position);
                return false;
            }

        });

          list = new ArrayList<CategoryViewModel>();

        Cursor c=db.rawQuery("SELECT _id,name FROM Category order by name", null);
        while(c.moveToNext())
        {
            String s0=c.getString(0);
            String name=c.getString(1);
           if(s0!=null) {
               String Id =s0;

               list.add(new CategoryViewModel(Id, name));
           }
        }

        adapter =  new CategoryAdapter(this,R.layout.passwordlist, list);
        listview.setAdapter(adapter);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(PasswordListActivity.this);
                input.setHint("Category for passwords");
                new AlertDialog.Builder(PasswordListActivity.this)
                        .setTitle(" ")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    String categoryName = input.getText().toString();
                                    db.execSQL("INSERT INTO Category(name)VALUES('" + categoryName + "')");
                                    Toast toast = Toast.makeText(PasswordListActivity.this, categoryName + " category added", Toast.LENGTH_LONG);
                                    toast.show();
                                    // deal with the editable
 list.clear();
                                    Cursor c=db.rawQuery("SELECT _id,name FROM Category", null);
                                    while(c.moveToNext())
                                    {
                                        String s0=c.getString(0);
                                        String name=c.getString(1);
                                        if(s0!=null) {
                                            String Id =s0;

                                            list.add(new CategoryViewModel(Id, name));
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    this.toString();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
              final CategoryViewModel item = (CategoryViewModel) parent.getItemAtPosition(position);
                Intent myIntent = new Intent(PasswordListActivity.this,PasswordListOfCatgoryActivity.class);
                myIntent.putExtra("selectedCategory",item.name);
                myIntent.putExtra("selectedCategoryId",item.Id);
                PasswordListActivity.this.startActivity(myIntent);
            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(SelectedItem.name);
        menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id=item.getItemId();

        if(item.getTitle()=="Edit"){
            showEditCategoryDialog();
        }
        else if(item.getTitle()=="Delete"){
            new AlertDialog.Builder(PasswordListActivity.this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete category '"+SelectedItem.name+"'?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                            db.execSQL("delete from Category where _id="+SelectedItem.Id);
                            db.execSQL("delete from Passwords where categoryId="+SelectedItem.Id);
                            list.remove(SelectedItem);
                            adapter.notifyDataSetChanged();
                            SelectedItem=null;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.password_icon)
                    .show();

        }else{
            return false;
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
          if(id == R.id.action_changePassword)
        {
            showChangePasswordDialog();
        }
        else if(id == R.id.action_Help)
        {
            showHelpDialog();
        }
        else if(id == R.id.action_uploadFile)
        {
            Intent myIntent = new Intent(PasswordListActivity.this,FileBrowser.class);
            PasswordListActivity.this.startActivity(myIntent);
        }
        else   {
              if (id == R.id.action_downloadFile) {
                  new AlertDialog.Builder(PasswordListActivity.this)
                          .setTitle("Confirm")
                          .setMessage("Are you sure you want to export all categories with passwords to file? \n\nIt will overwrite previous backup file if exist.\n\nFile will available under 'Download' folder with name PasswordProtectorBackup.txt")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //create text file of password  format "Category|Description|Password|Username" and save it into external storage
                                File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PasswordProtectorBackup.txt");
                                try {
                                    FileWriter out = new FileWriter(backupFile);
                                    BufferedWriter writer=new BufferedWriter(out);
                                    Cursor c=db.rawQuery("select cat.name,[pass].[description],[pass].[password],[pass].[username] from Category as cat,Passwords as pass where cat._id=pass.categoryId", null);
                                    while(c.moveToNext()) {
                                        String category=c.getString(0);
                                        String description=c.getString(1);
                                        String password=c.getString(2);
                                        String username=c.getString(3);
                                        String text=category+"|"+description+"|"+password+"|"+username;
                                        writer.write(text);
                                        writer.newLine();
                                    }
                                    Toast toast = Toast.makeText(PasswordListActivity.this, "Done!", Toast.LENGTH_LONG);
                                    toast.show();
                                    writer.close();
                                }
                                catch (Exception e)
                                {
                                    System.out.print("My Exception "+e.getMessage());
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          // do nothing
                      }
                  })
                          .setIcon(R.drawable.password_icon)
                          .show();


              }
          }

        return super.onOptionsItemSelected(item);
    }

    protected void showHelpDialog() {
        new AlertDialog.Builder(PasswordListActivity.this)
                .setTitle("Help")
                .setMessage("\"Treat your password like your toothbrush. Don't let anybody else use it, and get a new one every six months\"\n\n mail me your query/suggestion @  \ngopale.rajiv76@gmail.com")
                        /*.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })*/
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.password_icon)
                .show();
    }
    protected void showChangePasswordDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(PasswordListActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.change_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PasswordListActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText oldPasswordEdit = (EditText) promptView.findViewById(R.id.ChOldPassword);
        final EditText newPasswordEdit = (EditText) promptView.findViewById(R.id.ChPassword);
        final EditText newConfirmPasswordEdit = (EditText) promptView.findViewById(R.id.ChConfirmPassword);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Done
                     }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPasswordEdit.getText().toString();
                String newPass = newPasswordEdit.getText().toString();
                String newConfirmPass = newConfirmPasswordEdit.getText().toString();
                SharedPreferences mySharedPrefrence = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);
                String savePassword = mySharedPrefrence.getString("Password", "");
                final TextView incorrectPasswordValidation = (TextView) promptView.findViewById(R.id.IncorrectPasswordValidation);
                final TextView mismatchPasswordValidation = (TextView) promptView.findViewById(R.id.PasswordMismatchValidation);
                if (savePassword.equals(oldPass)) {
                    incorrectPasswordValidation.setVisibility(View.GONE);
                    if (newPass.equals(newConfirmPass)) {
                        mismatchPasswordValidation.setVisibility(View.GONE);
                        //TODO change Password
                        final SharedPreferences.Editor editor = mySharedPrefrence.edit();
                        editor.putString("Password", newPass);
                        editor.commit();
                        alert.dismiss();
                        Toast toast = Toast.makeText(PasswordListActivity.this, "Password changed successfully!", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        mismatchPasswordValidation.setVisibility(View.VISIBLE);
                    }
                } else {
                    incorrectPasswordValidation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void showEditCategoryDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(PasswordListActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.edit_category, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PasswordListActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editedCategoryTitle = (EditText) promptView.findViewById(R.id.EditCategoryTitle);

        editedCategoryTitle.setText(SelectedItem.name);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Done
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editedCategoryTitle.getText().toString();
                db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                db.execSQL("Update Category set name='" + categoryName + "' where _id=" + SelectedItem.Id);
                list.remove(SelectedItem);
                SelectedItem.name=categoryName;
                list.add(SelectedItem);
                adapter.notifyDataSetChanged();
                alert.dismiss();
            }
        });
    }
}



