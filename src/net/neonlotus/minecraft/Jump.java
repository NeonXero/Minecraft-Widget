package net.neonlotus.minecraft;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Jump extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button selectMob = (Button) findViewById(R.id.mobSelect);
        Button info = (Button) findViewById(R.id.infoButton);

        selectMob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent j = new Intent(Jump.this, MobPref.class);
                startActivity(j);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Questions or comments:\nEmail neonxero@gmail.com\nPlease put Minecraft Widget or similar in subject line, or use email link from market page\nImages from minecraftwiki.net - also thanks to Notch for making an amazing game!", Toast.LENGTH_LONG).show();
            }
        });

    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            //change of background
        }
        else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            //change the background
        }
        else
        { //do nothing, this might apply for the keyboard }


            super.onConfigurationChanged(newConfig);





        }}*/


    /*@Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
 //		case R.id.time:
 //			Intent i = new Intent(this, TimePref.class);
 //			startActivity(i);
 //			break;
             //Just testing git pushing
         case R.id.mob:
             Intent j = new Intent(this, MobPref.class);
             startActivity(j);
             break;
         }
         return true;
     }*/

    public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}