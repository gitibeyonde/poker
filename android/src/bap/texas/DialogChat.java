package bap.texas;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import bap.texas.util.Base64;


public class DialogChat extends Dialog {

    private ActivityTable _proxy;
    private Button _sendButton, _closeButton;
    private TextView _chatText;
    private EditText _sendText;
    private ScrollView _scrollview;


	public DialogChat(Context context, ActivityTable t) {
		super(context);
		_proxy = t;
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 setContentView(R.layout.chat);

    	 _sendText = (EditText) findViewById(R.id.chatSendText);
    	 _chatText = (TextView) findViewById(R.id.chatChatText);
    	 _scrollview = (ScrollView) findViewById(R.id.scrollview);
    	 
//    	 _scrollview.setScrollBarStyle(ScrollView.BUTTON1);
    	// _scrollview.fullScroll(ScrollView.FOCUS_UP);
    	 _chatText.setMovementMethod(new ScrollingMovementMethod());
    	 
    	 _sendButton = (Button)findViewById(R.id.chatSendB);
    	 _sendButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
            	 String cm = _sendText.getText().toString();
            	 if(cm.length()>0)
            	 {
        		_chatText.append(_proxy._user + ": " + cm+"\n");
        		_proxy.sendChat(Base64.encodeString(_proxy._user + ": " +cm));
        		_sendText.setText("");
            	 }
             }
         });
    	 _closeButton = (Button)findViewById(R.id.chatCloseB);
    	 _closeButton.setOnClickListener(new Button.OnClickListener() {
             public void onClick(View v) {
        		DialogChat.this.hide();
             }
         }); 
     }
    
    public void addChat(String s){
    	if (_chatText == null )return;
    	if(s.contains(_proxy._user+""))return;//to avoid printing the same message sent by user
    	_chatText.append(s+"\n");
    	
    }
    
    public void position(){
    	_chatText.forceLayout();
    }
   
     
 }
