package bap.texas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bap.texas.util.SoundManager;


public class ActivitySignUp extends Activity {

    
    private Button _submitButton;
    private Button _resetButton;
    private Button _backButton;
    
    private EditText _userText;
    private EditText _passText;
    private EditText _repPassText;
    private EditText _emailText;
    
    private TextView _statusText;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 setContentView(R.layout.register);

    	 _userText = (EditText)findViewById(R.id.regUser);
    	 _passText = (EditText)findViewById(R.id.regPass);
    	 _repPassText = (EditText)findViewById(R.id.regRep);
    	 _emailText = (EditText)findViewById(R.id.regEmail);
    	 
    	 _statusText = (TextView)findViewById(R.id.regStatus);

    	 _submitButton = (Button)findViewById(R.id.regSubmitB);
    	 _resetButton = (Button)findViewById(R.id.regCancelB);
    	 _backButton  = (Button)findViewById(R.id.regBackB);
    	 
         Bundle b = this.getIntent().getExtras(); 
    	 if (b != null){
	         String status = (String)b.get("status");
	         if (status != null)
	        	 _statusText.setText((String)b.get("status"));
    	 }
         
          
    	 _submitButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 Log.w("ActivityStart onCreate", _userText.getText().toString());
            	 Log.w("ActivityStart onCreate", _passText.getText().toString());
        		 if (!_passText.getText().toString().equals(_repPassText.getText().toString())){
        			 _statusText.setText(" (The passwords you provided are not matching, try again !)");
        			 return;
        		 }
        		 //validate email
        		 if (!emailFormat(_emailText.getText().toString())){
        			 _statusText.setText(" (Email is not formatted properly, try again !)");
        			 return;
        		 }
            	 Intent i = new Intent(ActivitySignUp.this, 
                         ActivityAgree.class); 
            	 	Bundle b = new Bundle(); 
     				b.putString("user", _userText.getText().toString()); 
     				b.putString("pass", _passText.getText().toString()); 
     				b.putString("email", _emailText.getText().toString()); 
     				b.putString("status", "Status: Registration successful, login to play"); 
    				i.putExtras(b); 
				ActivitySignUp.this.startActivity(i); 
                        
             }
         });
    	 
    	 _backButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 try{		
            	 	Intent i = new Intent(ActivitySignUp.this,ActivityLogin.class); 
            	 	Bundle b = new Bundle(); 
     				b.putString("status", "Status: Registration canceled."); 
    				i.putExtras(b); 
    				ActivitySignUp.this.startActivity(i); 
    				if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.fold);
            	 }catch(Exception e)
            	 {
            		 Log.e("ACTIVITY_SIGNUP_PROC", e.getStackTrace().toString());
            	 }
             }
         });
    	 
    	 _resetButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
            	 _userText.setText("");
            	 _passText.setText("");
            	 _repPassText.setText("");
            	 _emailText.setText("");
            	 
             }
         });
    	 
    	 
    	 
     }
 
    
    //Initialize reg ex for email. 
    static String email_expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

	  public static boolean emailFormat(String str){
	    boolean isValid = false;
	        //Make the comparison case-insensitive.
	    Pattern pattern = Pattern.compile(email_expression,Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(str);
	    if(matcher.matches()){
	        isValid = true;
	    }
	    return isValid; 
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
