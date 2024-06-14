package HOCC.FUN.FORGET;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import static android.content.Context.WINDOW_SERVICE;
import java.util.Locale;

public class Window {
    TextToSpeech tts;

    // declaring required variables
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;

    private ViewGroup floatView;

    TextView textView;
    int lan;
    int min;
    ImageView photo;

    String text;

    String list;

    public Window(Context context, String text , int lan){
        this.text=text;
        this.list=list;
        this.context=context;
        this.lan=lan;
        this.min=0;
        Log.d("a" , String.valueOf(min));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);

        }
        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (lan==1) {
                mView = layoutInflater.inflate(R.layout.popup_window_chinese, null);
                textView = (TextView) mView.findViewById(R.id.titleText);
                textView.setText(text);
            }else {
                mView = layoutInflater.inflate(R.layout.popup_window, null);
                textView = (TextView) mView.findViewById(R.id.titleText);
                textView.setText(text);
            }
        // inflating the view with the custom layout we created
        // set onClickListener on the remove button, which removes
        // the view from the window
        // Define the position of the
        // window within the screen
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
                    tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                if (lan == 1) {
                                    tts.setLanguage(new Locale("zh", "hk"));
                                }else {
                                    tts.setLanguage(Locale.US);
                                }
                                //re-enable tts here
                            }
                        }
                    });
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if (utteranceId.equals("silence")) {
                                tts.speak(textView.getText(), TextToSpeech.QUEUE_ADD, null, "test");
                            } else {
                                tts.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "silence");
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d("Error1", e.toString());
        }
    }

    public void close() {
        tts.stop();

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

