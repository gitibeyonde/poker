package bap.texas;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ActivityImageUploader extends Activity {

public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imageuploader);

    GridView gridview = (GridView) findViewById(R.id.gridview);
    gridview.setAdapter(new ImageAdapter(this));
}
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.card_2c, R.drawable.card_2d,
            R.drawable.card_2h, R.drawable.card_2s,
            R.drawable.card_3c, R.drawable.card_3d,
            R.drawable.card_3h, R.drawable.card_3s,
            R.drawable.card_4c, R.drawable.card_4d,
            R.drawable.card_4h, R.drawable.card_4s,
            R.drawable.card_5c, R.drawable.card_5d,
            R.drawable.card_5h, R.drawable.card_5s,
            R.drawable.card_6c, R.drawable.card_6d,
            R.drawable.card_6h, R.drawable.card_6s,
            R.drawable.card_7c, R.drawable.card_7d
    };
}
}