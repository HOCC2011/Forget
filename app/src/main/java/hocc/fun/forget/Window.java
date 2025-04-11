package  hocc.fun.forget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import static android.content.Context.WINDOW_SERVICE;

public class Window {
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    TextView textView;
    int min;
    String text;


    public Window(Context context, String text){
        this.text=text;
        this.context=context;
        this.min=0;
        Log.d("a" , String.valueOf(min));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

        }
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.popup_window, null);
        textView = (TextView) mView.findViewById(R.id.titleText);
        textView.setText(text);
        mParams.gravity = Gravity.TOP;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void close() {
        try {
            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup)mView.getParent()).removeAllViews();

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2",e.toString());
        }
    }

}

