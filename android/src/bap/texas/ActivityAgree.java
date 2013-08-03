package bap.texas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import bap.texas.common.message.Command;
import bap.texas.common.message.Response;
import bap.texas.server.Proxy;
import bap.texas.util.SoundManager;


public class ActivityAgree extends Activity {

	private ProgressDialog m_ProgressDialog = null; 
    private Runnable viewOrders;
    private Proxy _proxy;
    String _user, _pass, _email;
    private Button _agreeButton;
    private Button _dontAgreeButton;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 setContentView(R.layout.agreement);

    	 _agreeButton = (Button)findViewById(R.id.agreeAgreeB);
    	 _dontAgreeButton = (Button)findViewById(R.id.agreeDontAgreeB);
    	
         Bundle b = this.getIntent().getExtras(); 
    	 if (b != null){
	         String status = (String)b.get("status");
	         _user = (String)b.getString("user");
	         _pass = (String)b.getString("pass");
	         _email = (String)b.getString("email");
    	 }
    	 
    	    	 
    	 _agreeButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 Log.w("ActivityStart onCreate", _user);
            	 Log.w("ActivityStart onCreate", _pass);
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 viewOrders = new Runnable(){
                     @Override
                     public void run() {
                    	 //String name, String passwd, String email, int gender, String bc, String dob
                         boolean res = register(_user, _pass, _email);
                         Log.i("registation value",""+res);
                         if (res){
                        	  m_ProgressDialog = ProgressDialog.show(ActivityAgree.this,    
                                      "Please wait...", "Validating...", true);
                        	 Intent i = new Intent(ActivityAgree.this, 
                                     ActivityLogin.class); 
                        	 
                        	 	Bundle b = new Bundle(); 
	             				b.putString("status", "Status: Registration successful, login to play"); 
	            				i.putExtras(b); 
            				// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier' 
	            				ActivityAgree.this.startActivity(i); 
                         }
                         else {
                        	 Intent i = new Intent(ActivityAgree.this, 
                        			 ActivitySignUp.class); 
             
            				Bundle b = new Bundle(); 
            				b.putString("status", "Status: Registration failed, try again"); 
            				i.putExtras(b); 
            				// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier' 
            				ActivityAgree.this.startActivity(i); 
                         }
                     }
                 };
                 Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
                 thread.start();
               
             }
         });
          
    	 _dontAgreeButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	try{
            		if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.fold);
            		Intent i = new Intent(ActivityAgree.this, 
            					ActivityLogin.class); 
                    ActivityAgree.this.startActivity(i);
                    if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.fold);
            	}catch(Exception e)
            	{
            		Log.e("ACTIVITY_AGREE_PROC", e.getStackTrace().toString());
            	}
                  
             }
         });
    	 
    	
     }
   
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            m_ProgressDialog.dismiss();
        }
    };
    
    private boolean register (String name, String passwd, String email){
        try{
        	_proxy = new Proxy(Command.IP,Command.PORT);
            //_proxy = new Proxy("66.220.9.100", 8985);
            //_proxy = new Proxy("67.211.101.97",8985);
            Response rtl = _proxy.registerReturn(name, passwd, email, 0, "", "");
            if (rtl != null){
                Log.w("Activity Start validate", rtl.toString());
                
            	return rtl.getResult() == 1 ? true : false;
            }
          } catch (Exception e) { 
            Log.e("BACKGROUND_PROC", e.getMessage());
          }
          runOnUiThread(returnRes);
          return false;
      }
	
     /**
      * Invoked when the Activity loses user focus.
      */
     @Override
     protected void onPause() {
         super.onPause();
     }
     
     @Override
     protected void onSaveInstanceState(Bundle outState) {
         // just have the View's thread save its state into our Bundle
         super.onSaveInstanceState(outState);
     }
     
 }
