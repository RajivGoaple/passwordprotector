package com.projects.rajiv.passwordprotector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapters.PasswordAdapter;
import Viewmodels.CategoryViewModel;
import Viewmodels.PasswordViewModel;

public class PasswordListOfCatgoryActivity extends AppCompatActivity {
    int catId=0;
     ListView listview=null;
    ArrayList<PasswordViewModel> list;
    SQLiteDatabase db;
    PasswordAdapter adapter;
    PasswordViewModel passwordViewModel;
    SimpleDateFormat formatter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list_of_catgory);
        final SQLiteDatabase db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
        Intent intent = getIntent();
        formatter = new SimpleDateFormat("d MMM yyyy h:mm a");
        String catname = intent.getStringExtra("selectedCategory");
         catId =Integer.parseInt(intent.getStringExtra("selectedCategoryId"));
        list = new ArrayList<PasswordViewModel>();
          adapter = new PasswordAdapter(this,
                R.layout.passwordlist, list);
        /*final TextView selectedCatgoryLable = (TextView) findViewById(R.id.SelectedCategoryLabel);
        selectedCatgoryLable.setText(catname);*/
        getSupportActionBar().setTitle(catname);
        final Button button = (Button) findViewById(R.id.AddPassword);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddPasswordDialog(list,adapter);
            }});

         listview = (ListView) findViewById(R.id.CategoryWisePasswordList);
        registerForContextMenu(listview);


        Cursor c=db.rawQuery("SELECT _id,description,password,categoryId,lastModifiedDate,username FROM Passwords where categoryId="+catId+"  order by description", null);



        while(c.moveToNext())
        {
            String id=c.getString(0);
            String description=c.getString(1);
            String password=c.getString(2);
            String categoryId=c.getString(3);
            String lastModified = formatter.format(Date.parse(c.getString(4)));
            String username=c.getString(5);

           list.add(new PasswordViewModel(id,description,password,categoryId,lastModified,username,""));

        }

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parentView, View childView, int position, long id) {
                // this will provide the value
                passwordViewModel = (PasswordViewModel) listview.getItemAtPosition(position);
                return false;
            }

        });

        listview.setAdapter(adapter);
adapter.notifyDataSetChanged();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                passwordViewModel = (PasswordViewModel) parent.getItemAtPosition(position);
                String details="";
                if(passwordViewModel.Username==null || passwordViewModel.Username.equals(""))
                {
                    details="Password: "+passwordViewModel.Password;
                }
                else
                {
                    details="Username: "+passwordViewModel.Username+"\n\nPassword: "+passwordViewModel.Password;
                }
                new AlertDialog.Builder(PasswordListOfCatgoryActivity.this)
                        .setTitle(passwordViewModel.Description)
                        .setMessage(details)
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

        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(passwordViewModel.Description);
        menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Last Modified");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id=item.getItemId();

        if(item.getTitle()=="Edit"){
            showEditPasswordDialog();
        }
        else if(item.getTitle()=="Delete"){
            new AlertDialog.Builder(PasswordListOfCatgoryActivity.this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete password '"+passwordViewModel.Description+"'?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                                db.execSQL("delete from Passwords where _id="+passwordViewModel.Id);
                                list.remove(passwordViewModel);
                                adapter.notifyDataSetChanged();
                                passwordViewModel=null;
                            }
                        })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.password_icon)
                    .show();

        }
        else if(item.getTitle()=="Last Modified")
        {
            new AlertDialog.Builder(PasswordListOfCatgoryActivity.this)
                    .setTitle(passwordViewModel.Description)
                    .setMessage("Last Modified: "+passwordViewModel.LastModified)
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

        else{
            return false;
        }
        return true;
    }


    protected void showAddPasswordDialog(final ArrayList<PasswordViewModel> list, final ArrayAdapter<PasswordViewModel> adapter) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(PasswordListOfCatgoryActivity.this);
        View promptView = layoutInflater.inflate(R.layout.add_password_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PasswordListOfCatgoryActivity.this);
        alertDialogBuilder.setView(promptView);
        final EditText usernameText = (EditText) promptView.findViewById(R.id.username);
        final CheckBox includeUsername = (CheckBox) promptView.findViewById(R.id.addUsername);
        includeUsername.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                    if(isChecked)
                                                    {
                                                    usernameText.setVisibility(View.VISIBLE);
                                                    }
                                                   else
                                                    {
                                                        usernameText.setVisibility(View.GONE);
                                                    }
                                               }
                                           }
        );
        final EditText description = (EditText) promptView.findViewById(R.id.description);
        final EditText password = (EditText) promptView.findViewById(R.id.password);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO add in database
                        String desc =description.getText().toString();
                        String pass =password.getText().toString();
                        String username=usernameText.getText().toString();
                        String LastModifiedDate= new Date().toString();
                          db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                        db.execSQL("INSERT INTO Passwords(description,password,lastModifiedDate,categoryId,username)VALUES('" + desc + "','" + pass + "','" + LastModifiedDate + "'," + catId +",'"+username+ "')");

                        Cursor c=db.rawQuery("SELECT _id,description,password,categoryId,lastModifiedDate,username FROM Passwords where categoryId=" + catId + " order by description", null);

                        list.clear();
                        while(c.moveToNext())
                        {
                            String id2=c.getString(0);
                            String description=c.getString(1);
                            String password=c.getString(2);
                            String categoryId=c.getString(3);
                            String lastModified = formatter.format(Date.parse(c.getString(4)));
                            String usernm=c.getString(5);

                            list.add(new PasswordViewModel(id2,description,password,categoryId,lastModified,usernm,""));
                        }

                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void showEditPasswordDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(PasswordListOfCatgoryActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.edit_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PasswordListOfCatgoryActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText usernameText = (EditText) promptView.findViewById(R.id.username);
        final CheckBox includeUsername = (CheckBox) promptView.findViewById(R.id.addUsername);
        includeUsername.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if (isChecked) {
                                                               usernameText.setVisibility(View.VISIBLE);
                                                           } else {
                                                               usernameText.setVisibility(View.GONE);
                                                           }
                                                       }
                                                   }
        );



        final EditText editedDescription = (EditText) promptView.findViewById(R.id.EditDescription);
        final EditText editedPassword = (EditText) promptView.findViewById(R.id.EditPassword);

        editedDescription.setText(passwordViewModel.Description);
        editedPassword.setText(passwordViewModel.Password);
        usernameText.setText(passwordViewModel.Username);

        if(!passwordViewModel.Username.equals(""))
        {
            usernameText.setVisibility(View.VISIBLE);
            includeUsername.setChecked(true);
        }
        usernameText.setText(passwordViewModel.Username);
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
                String decription = editedDescription.getText().toString();
                String password = editedPassword.getText().toString();
                String uname = usernameText.getText().toString();
                db = openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                String lastModified = new Date().toString();
                db.execSQL("Update Passwords set description='" + decription + "',password='" + password + "',lastModifiedDate='" + lastModified + "',username='"+uname+"' where _id=" + passwordViewModel.Id);
                list.remove(passwordViewModel);
                passwordViewModel.Description = decription;
                passwordViewModel.Password = password;
                passwordViewModel.Username = uname;
                passwordViewModel.LastModified = formatter.format(new Date());
                list.add(passwordViewModel);
                adapter.notifyDataSetChanged();
                alert.dismiss();
            }
        });
    }
}
