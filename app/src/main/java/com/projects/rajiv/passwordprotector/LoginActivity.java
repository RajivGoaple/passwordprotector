package com.projects.rajiv.passwordprotector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         final Button signInButton = (Button)findViewById(R.id.signIn);
         signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences mySharedPrefrence = getApplicationContext().getSharedPreferences("PasswordProtector",MODE_PRIVATE);
                 String savePassword = mySharedPrefrence.getString("Password", "");
                final EditText Password = (EditText)findViewById(R.id.loginPassword);
                final String userPassword=Password.getText().toString();
                if(userPassword.equals(savePassword))
                {
                    finish();
                    Intent myIntent = new Intent(LoginActivity.this, PasswordListActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                }
                else
                {
                    Password.setText("");
                    Toast toast = Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_LONG);
                    toast.show();
                }



            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
