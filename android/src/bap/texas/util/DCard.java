package bap.texas.util;


import android.content.Context;
import android.graphics.drawable.Drawable;
import bap.texas.ActivitySettings;
import bap.texas.R;

public class DCard {
	
	public Drawable _cards[];
	public Drawable _pocketCards[];
	public Drawable _colnorcards[];
	public Drawable _rightCards[];
	public Drawable _leftCards[];
	
	public DCard(Context context){
//		_cards = new Drawable[55];
//		_cards[0] = context.getResources().getDrawable(R.drawable.card);
//		_cards[1] = context.getResources().getDrawable(R.drawable.card_2c);
//		_cards[2] = context.getResources().getDrawable(R.drawable.card_3c);
//		_cards[3] = context.getResources().getDrawable(R.drawable.card_4c);
//		_cards[4] = context.getResources().getDrawable(R.drawable.card_5c);
//		_cards[5] = context.getResources().getDrawable(R.drawable.card_6c);
//		_cards[6] = context.getResources().getDrawable(R.drawable.card_7c);
//		_cards[7] = context.getResources().getDrawable(R.drawable.card_8c);
//		_cards[8] = context.getResources().getDrawable(R.drawable.card_9c);
//		_cards[9] = context.getResources().getDrawable(R.drawable.card_10c);
//		_cards[10] = context.getResources().getDrawable(R.drawable.card_jc);
//		_cards[11] = context.getResources().getDrawable(R.drawable.card_qc);
//		_cards[12] = context.getResources().getDrawable(R.drawable.card_kc);
//		_cards[13] = context.getResources().getDrawable(R.drawable.card_ac);
//
//		_cards[14] = context.getResources().getDrawable(R.drawable.card_2d);
//		_cards[15] = context.getResources().getDrawable(R.drawable.card_3d);
//		_cards[16] = context.getResources().getDrawable(R.drawable.card_4d);
//		_cards[17] = context.getResources().getDrawable(R.drawable.card_5d);
//		_cards[18] = context.getResources().getDrawable(R.drawable.card_6d);
//		_cards[19] = context.getResources().getDrawable(R.drawable.card_7d);
//		_cards[20] = context.getResources().getDrawable(R.drawable.card_8d);
//		_cards[21] = context.getResources().getDrawable(R.drawable.card_9d);
//		_cards[22] = context.getResources().getDrawable(R.drawable.card_10d);
//		_cards[23] = context.getResources().getDrawable(R.drawable.card_jd);
//		_cards[24] = context.getResources().getDrawable(R.drawable.card_qd);
//		_cards[25] = context.getResources().getDrawable(R.drawable.card_kd);
//		_cards[26] = context.getResources().getDrawable(R.drawable.card_ad);
//
//		_cards[27] = context.getResources().getDrawable(R.drawable.card_2h);
//		_cards[28] = context.getResources().getDrawable(R.drawable.card_3h);
//		_cards[29] = context.getResources().getDrawable(R.drawable.card_4h);
//		_cards[30] = context.getResources().getDrawable(R.drawable.card_5h);
//		_cards[31] = context.getResources().getDrawable(R.drawable.card_6h);
//		_cards[32] = context.getResources().getDrawable(R.drawable.card_7h);
//		_cards[33] = context.getResources().getDrawable(R.drawable.card_8h);
//		_cards[34] = context.getResources().getDrawable(R.drawable.card_9h);
//		_cards[35] = context.getResources().getDrawable(R.drawable.card_10h);
//		_cards[36] = context.getResources().getDrawable(R.drawable.card_jh);
//		_cards[37] = context.getResources().getDrawable(R.drawable.card_qh);
//		_cards[38] = context.getResources().getDrawable(R.drawable.card_kh);
//		_cards[39] = context.getResources().getDrawable(R.drawable.card_ah);
//
//		_cards[40] = context.getResources().getDrawable(R.drawable.card_2s);
//		_cards[41] = context.getResources().getDrawable(R.drawable.card_3s);
//		_cards[42] = context.getResources().getDrawable(R.drawable.card_4s);
//		_cards[43] = context.getResources().getDrawable(R.drawable.card_5s);
//		_cards[44] = context.getResources().getDrawable(R.drawable.card_6s);
//		_cards[45] = context.getResources().getDrawable(R.drawable.card_7s);
//		_cards[46] = context.getResources().getDrawable(R.drawable.card_8s);
//		_cards[47] = context.getResources().getDrawable(R.drawable.card_9s);
//		_cards[48] = context.getResources().getDrawable(R.drawable.card_10s);
//		_cards[49] = context.getResources().getDrawable(R.drawable.card_js);
//		_cards[50] = context.getResources().getDrawable(R.drawable.card_qs);
//		_cards[51] = context.getResources().getDrawable(R.drawable.card_ks);
//		_cards[52] = context.getResources().getDrawable(R.drawable.card_as);
//
//		_cards[53] = context.getResources().getDrawable(R.drawable.cardbj);
//		_cards[54] = context.getResources().getDrawable(R.drawable.cardrj);
		

		_cards = new Drawable[55];
		_cards[0] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.card:R.drawable.card);
		_cards[1] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard2c:R.drawable.card_2c);
		_cards[2] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard3c:R.drawable.card_3c);
		_cards[3] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard4c:R.drawable.card_4c);
		_cards[4] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard5c:R.drawable.card_5c);
		_cards[5] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard6c:R.drawable.card_6c);
		_cards[6] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard7c:R.drawable.card_7c);
		_cards[7] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard8c:R.drawable.card_8c);
		_cards[8] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard9c:R.drawable.card_9c);
		_cards[9] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardtc:R.drawable.card_10c);
		_cards[10] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardjc:R.drawable.card_jc);
		_cards[11] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardqc:R.drawable.card_qc);
		_cards[12] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardkc:R.drawable.card_kc);
		_cards[13] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardac:R.drawable.card_ac);
		
		_cards[14] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard2d:R.drawable.card_2d);
		_cards[15] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard3d:R.drawable.card_3d);
		_cards[16] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard4d:R.drawable.card_4d);
		_cards[17] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard5d:R.drawable.card_5d);
		_cards[18] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard6d:R.drawable.card_6d);
		_cards[19] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard7d:R.drawable.card_7d);
		_cards[20] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard8d:R.drawable.card_8d);
		_cards[21] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard9d:R.drawable.card_9d);
		_cards[22] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardtd:R.drawable.card_10d);
		_cards[23] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardjd:R.drawable.card_jd);
		_cards[24] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardqd:R.drawable.card_qd);
		_cards[25] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardkd:R.drawable.card_kd);
		_cards[26] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardad:R.drawable.card_ad);
		
		_cards[27] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard2h:R.drawable.card_2h);
		_cards[28] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard3h:R.drawable.card_3h);
		_cards[29] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard4h:R.drawable.card_4h);
		_cards[30] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard5h:R.drawable.card_5h);
		_cards[31] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard6h:R.drawable.card_6h);
		_cards[32] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard7h:R.drawable.card_7h);
		_cards[33] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard8h:R.drawable.card_8h);
		_cards[34] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard9h:R.drawable.card_9h);
		_cards[35] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardth:R.drawable.card_10h);
		_cards[36] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardjh:R.drawable.card_jh);
		_cards[37] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardqh:R.drawable.card_qh);
		_cards[38] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardkh:R.drawable.card_kh);
		_cards[39] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardah:R.drawable.card_ah);

		_cards[40] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard2s:R.drawable.card_2s);
		_cards[41] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard3s:R.drawable.card_3s);
		_cards[42] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard4s:R.drawable.card_4s);
		_cards[43] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard5s:R.drawable.card_5s);
		_cards[44] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard6s:R.drawable.card_6s);
		_cards[45] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard7s:R.drawable.card_7s);
		_cards[46] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard8s:R.drawable.card_8s);
		_cards[47] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcard9s:R.drawable.card_9s);
		_cards[48] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardts:R.drawable.card_10s);
		_cards[49] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardjs:R.drawable.card_js);
		_cards[50] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardqs:R.drawable.card_qs);
		_cards[51] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardks:R.drawable.card_ks);
		_cards[52] = context.getResources().getDrawable(ActivitySettings.colorsdeck? R.drawable.colorcardas:R.drawable.card_as);
	
		_cards[53] = context.getResources().getDrawable(R.drawable.cardbj);
		_cards[54] = context.getResources().getDrawable(R.drawable.cardrj);
		
	}


}
