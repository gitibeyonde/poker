package bap.texas;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bap.texas.common.message.Command;
import bap.texas.server.Proxy;
import bap.texas.util.SoundManager;

public class ActivityChangePassword extends Activity{
	
	TextView _changepassword,_usernameCHANGEPWD,_verifystatus;
	EditText _entercurrentpassword,_enternewpassword,_verifynewpassword;
	Button   _cancelButton,_okButtton;
	Bundle tb;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
    	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	 setContentView(R.layout.changepassword);
    	 
    	 _changepassword = (TextView)findViewById(R.id.changepassword);
    	 _usernameCHANGEPWD = (TextView)findViewById(R.id.usernameCHANGEPWD);
    	 _entercurrentpassword = (EditText)findViewById(R.id.entercurrentpassword);
    	 _enternewpassword = (EditText)findViewById(R.id.enternewpassword);
    	 _verifynewpassword = (EditText)findViewById(R.id.verifynewpassword);
    	 _cancelButton = (Button)findViewById(R.id.cancelCHANGEPWD);
    	 _okButtton = (Button)findViewById(R.id.okCHANGEPWD);
    	 _verifystatus = (TextView)findViewById(R.id.statusCHANGEPWD);
    	 
    	 _usernameCHANGEPWD.setTypeface(ActivitySplash.paintobjects.get("dotstypeface")); 
    	 _changepassword.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _entercurrentpassword.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _enternewpassword.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 _verifynewpassword.setTypeface(ActivitySplash.paintobjects.get("myriadface"));
    	 
    	 tb=this.getIntent().getExtras();
    	 
    	 _usernameCHANGEPWD.setText(tb.getString("user"));
    	 
    	 
    	 _cancelButton.setOnClickListener(new OnClickListener() {
    		 @Override
    		 public void onClick(View arg0) {
    			 // TODO Auto-generated method stub
    			 if (ActivityLogin._soundAllowed)
    				 ActivityLogin.mSoundManager.playSound(SoundManager.click);
    			 Intent i = new Intent(ActivityChangePassword.this,
    					 ActivityAccount.class);
    			 Bundle b = new Bundle();
    			 b.putString("user", tb.getString("user"));
    			 b.putString("pass", tb.getString("pass"));
    			 b.putFloat("uw", tb.getFloat("uw"));
    			 b.putFloat("urw", tb.getFloat("urw"));
    			 i.putExtras(b);
    			 ActivityChangePassword.this.startActivity(i);

    		 }
    	 });
    	 
    	 _okButtton.setOnClickListener(new OnClickListener() {
    		 @Override
    		 public void onClick(View arg0) {
    			 // TODO Auto-generated method stub
    			 if (ActivityLogin._soundAllowed)
    				 ActivityLogin.mSoundManager.playSound(SoundManager.click);
    			 
//    			 boolean pwdstatus = true;
    			 if(_enternewpassword.getText().toString().equals(_verifynewpassword.getText().toString()))
    			 {
	    				//String url = "http://67.211.101.97:8080/bluespade/servlet/changePassword?uid="+tb.getString("user")+"&pwd="+_entercurrentpassword.getText().toString()+"&newPwd="+_verifynewpassword.getText().toString();
    				 String url = "http://www.blueacepoker.com/servlet/changeAvatar?uid="+tb.getString("user")+"&pwd="+_entercurrentpassword.getText().toString()+"&newPwd="+_verifynewpassword.getText().toString();
	    				try {
	    					HttpClient httpclient = new DefaultHttpClient();
	    					HttpGet httppost = new HttpGet(url);
	    					HttpResponse response = httpclient.execute(httppost);
	    					String responsestatulline = response.getStatusLine().toString();
	    					Log.i("response",responsestatulline);
	    				} catch (ClientProtocolException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    				Intent i = new Intent(ActivityChangePassword.this,
	        					 ActivityMainLobby.class);
	    				 Bundle b = new Bundle();
	        			 b.putString("user", tb.getString("user"));
	        			 b.putString("pass", _verifynewpassword.getText().toString());
	        			 b.putFloat("uw", tb.getFloat("uw"));
	        			 b.putFloat("urw", tb.getFloat("urw"));
	        			 i.putExtras(b);
	        			 ActivityChangePassword.this.startActivity(i);
    			 }
    			 else
    			 {
    				 _verifystatus.setText("Invalid Password");
    				
    			 }
    		 }
    	 });
	}
}
