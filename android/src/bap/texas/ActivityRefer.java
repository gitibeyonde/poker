package bap.texas;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bap.texas.common.message.Response;
import bap.texas.server.Proxy;
import bap.texas.util.SoundManager;


public class ActivityRefer extends Activity {

	private ProgressDialog m_ProgressDialog = null; 
    private Runnable viewOrders;
    private Proxy _proxy;
    String _user, _pass,_statusText;
    private Button _submitButton;
    private Button _cancelButton;
    private EditText _email1Text;
    private Float _uw,_urw;
    private int _plrs;
	private TextView _statusTV,_usernameView,_referafriend,_invitemessage;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 requestWindowFeature(Window.FEATURE_NO_TITLE);
    	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		         WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	 setContentView(R.layout.refer);
    	 _referafriend = (TextView)findViewById(R.id.referafriend);
    	 _invitemessage =(TextView)findViewById(R.id.invitemessage);
    	 _email1Text = (EditText)findViewById(R.id.inviteEmail1);
    	 _statusTV = (TextView)findViewById(R.id.statusTV);
    	 _usernameView = (TextView)findViewById(R.id.usernameRef);
    	_submitButton = (Button)findViewById(R.id.inviteSubmitB);
    	 _cancelButton = (Button)findViewById(R.id.inviteCancelB);

    	 _invitemessage.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _referafriend.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _usernameView.setTypeface(ActivitySplash.paintobjects.get("dotstypeface"));
    	 _email1Text.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _statusTV.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _submitButton.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _cancelButton.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 Bundle b = this.getIntent().getExtras();
 		_user = (String)b.get("user");
 		_pass = (String)b.get("pass");
 		_uw = (Float)b.getFloat("uw");
 		_urw = (Float)b.getFloat("urw");
 		_plrs = b.getInt("onlineplayers");
 		
 		
 		_usernameView.setText(_user);
 		Log.w("ActivityInvite", " Userid=" + _user + " Pass=" + _pass);
 		//_email1Text.setSelectAllOnFocus(true);
 		
 		
 		 /*_email1Text.setOnEditorActionListener(new OnEditorActionListener() {
 			
 			@Override
 			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
 				// TODO Auto-generated method stub
 				 	 if(_email1Text.getText().toString().equals(""))
						 _email1Text.setText("Friend's e-mail address");
 				return false;
 			}
 		 });
 		_email1Text.setOnTouchListener(new OnTouchListener() {
 			
			 @Override
			 public boolean onTouch(View arg0, MotionEvent arg1) {
				 // TODO Auto-generated method stub
				 _email1Text.setText("");
				 return false;
			 }
		 });*/

 		_submitButton.setOnClickListener(new Button.OnClickListener() {
 			public void onClick(View v) {
 				if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);
 				 Intent i = new Intent(ActivityRefer.this,ActivityRefer.class); 
 				_email1Text.getText(); 

 				String urlString = "http://67.211.101.97:8080/bluespade/ref/referafriend?tomailid="+_email1Text.getText().toString()+"&username="+_user.toUpperCase();
 				try {
 					HttpClient httpclient = new DefaultHttpClient();  
 					HttpPost httppost = new HttpPost(urlString); 
 					HttpResponse response = httpclient.execute(httppost);  
 					String responsestatulline = response.getStatusLine().toString();
 					if(responsestatulline.contains("OK"))
 					{
 						for(int j=0;j<response.getAllHeaders().length;j++)
 						{ 						
 						Log.e("Status", response.getAllHeaders()[j] +"");
 						}
 						_statusTV.setText(getString(R.string.emailsentsuccess));
 						_email1Text.setText("");
 					}
 					else
 					{
 						Log.e("Status",  response.getStatusLine().toString());
 						_statusTV.setText(getString(R.string.emailsendingfail));
 					}
 				} catch (ClientProtocolException e) {  
 					// TODO Auto-generated catch block  
 				} catch (IOException e) {  
 					// TODO Auto-generated catch block  
 				}  
 				Bundle b = new Bundle(); 
 				b.putString("user", _user); 
 				b.putString("pass", _pass); 
 				b.putFloat("uw", _uw);
 				b.putFloat("urw", _urw);
 				b.putInt("onlineplayers", _plrs);
 				i.putExtras(b); 
 				ActivityRefer.this.startActivity(i); 
 			}
 		});
    	 
    	 
    	 _cancelButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 if(ActivityLogin._soundAllowed)ActivityLogin.mSoundManager.playSound(SoundManager.click);

            	 Intent i = new Intent(ActivityRefer.this,ActivityMainLobby.class); 
            	 	Bundle b = new Bundle(); 
            	 	b.putString("user", _user); 
    				b.putString("pass", _pass); 
    				b.putFloat("uw", _uw);
    				b.putFloat("urw", _urw);
    				b.putInt("onlineplayers", _plrs);
    				i.putExtras(b); 
				// We use SUB_ACTIVITY_REQUEST_CODE as an 'identifier' 
    				ActivityRefer.this.startActivity(i); 
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
            _proxy = new Proxy("67.211.101.97",8985);
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
