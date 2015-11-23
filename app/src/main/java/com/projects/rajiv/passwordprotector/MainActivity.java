package com.projects.rajiv.passwordprotector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mySharedPrefrence = getApplicationContext().getSharedPreferences("PasswordProtector",MODE_PRIVATE);
        final SharedPreferences.Editor editor = mySharedPrefrence.edit();
        Boolean passwordSetup = mySharedPrefrence.getBoolean("PasswordSetup",false);
        if(!passwordSetup)
        {
            setContentView(R.layout.activity_main);

            final Button signInButton = (Button)findViewById(R.id.btnSetPassword);

            signInButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText Password = (EditText)findViewById(R.id.etPassword);
                    final EditText ConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
                    String pass=Password.getText().toString();
                    String confirmPass=ConfirmPassword.getText().toString();
                    if(!pass.equals(confirmPass))
                    {
                        Toast toast = Toast.makeText(MainActivity.this, "Password should match", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else
                    {
                        final SQLiteDatabase db=openOrCreateDatabase("PasswordProtectorDatabase", Context.MODE_PRIVATE, null);
                        try {
                            db.execSQL("CREATE TABLE IF NOT EXISTS Category(_id INTEGER  ,name VARCHAR,CONSTRAINT [PK_Category] PRIMARY KEY ([_id]));");
                            db.execSQL("CREATE TABLE IF NOT EXISTS Passwords(_id INTEGER,description VARCHAR,password VARCHAR,lastModifiedDate VARCHAR,categoryId INTEGER,username VARCHAR,CONSTRAINT [PK_Password] PRIMARY KEY ([_id]));");
                            editor.putString("Password", pass);
                            editor.putBoolean("PasswordSetup", true);
                            editor.commit();
                            finish();
                            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(myIntent);

                        }
                        catch (Exception e)
                        {
                            System.out.print("Exception "+e.getMessage());
                        }
                    }
                }
            });

        }
        else
        {
            finish();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
