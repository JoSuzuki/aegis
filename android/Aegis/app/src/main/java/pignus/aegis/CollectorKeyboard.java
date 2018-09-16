package pignus.aegis;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by jo123 on 12/08/2018.
 */

public class CollectorKeyboard {
    private KeyboardView mKeyboardView;

    private Activity mHostActivity;


    private boolean shift = false;

    private String BufferKeyPress = "";
    private String BufferKeyboardTouch = "";

    String Folder;
    File folder;

    private final static int STRING_MAX_SIZE = 10000;

    File KeyPressEventFile;
    File KeyboardTouchFile;

    int eventAction;
    String unixTime;
    String PosX;
    String PosY;
    String Press;
    String Area;
    String eventTime;
    String ActivityID;

    Display display;
    int phoneOrientation;

    private String GravarArquivo(File Arquivo, String bufferDados, String dados, boolean Buffer){

        if(bufferDados.length() + dados.length() > STRING_MAX_SIZE || Buffer == false) {
            try {
                FileOutputStream fOut = new FileOutputStream(Arquivo, true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(bufferDados + dados);
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();
                bufferDados = "";
                Log.i("Diego", "File Writen");
            } catch (Exception e) {
                Log.e("Diego", "Could not write on file");
            }
        } else {
            bufferDados = bufferDados + dados;
            Log.i("Diego", Integer.toString(bufferDados.length() + dados.length()));
        }
        return bufferDados;
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int keyCode) {
            Log.i("Jo", String.valueOf(keyCode));
            String unixTime = Long.toString(System.currentTimeMillis());
            String pressType = "0";
            display = ((WindowManager) mHostActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            phoneOrientation = display.getRotation();
            Log.i("Jo", String.valueOf(phoneOrientation));

            //Escrever no Arquivo
            BufferKeyPress = GravarArquivo(KeyPressEventFile, BufferKeyPress,unixTime + ',' + "" + ',' + pressType + ','
                    + ActivityID + ',' + keyCode + ',' + phoneOrientation + '\n', false);



        }

        @Override
        public void onRelease(int keyCode) {
            Log.i("Jo", "onRelease");
            String unixTime = Long.toString(System.currentTimeMillis());
            String pressType = "1";
            display = ((WindowManager) mHostActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            phoneOrientation = display.getRotation();
            Log.i("Jo", String.valueOf(phoneOrientation));

            //Escrever no Arquivo
            BufferKeyPress = GravarArquivo(KeyPressEventFile, BufferKeyPress,unixTime + ',' + "" + ',' + pressType + ','
                    + ActivityID + ',' + keyCode + ',' + phoneOrientation + '\n', false);
        }

        @Override
        public void onKey(int keyCode, int[] keyCodes) {
            int DONE = -3;
            int ENTER = 13;
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            EditText editText = (EditText) focusCurrent;
            Editable editable = editText.getText();
            int start = editText.getSelectionStart();
            if (keyCode == Keyboard.KEYCODE_DELETE) {
                if (editable != null && start > 0) editable.delete(start - 1, start);
            } else if (keyCode == Keyboard.KEYCODE_SHIFT) {
                shift = !shift;
                mKeyboardView.getKeyboard().setShifted(shift);
                mKeyboardView.invalidateAllKeys();
            } else if (keyCode == DONE) {
                hideCollectorKeyboard();
            } else if (keyCode == ENTER) {
                editable.append("/n");
            } else {
                Character character;
                if (shift) {
                    character = Character.toUpperCase((char) keyCode);
                    shift = !shift;
                    mKeyboardView.getKeyboard().setShifted(shift);
                    mKeyboardView.invalidateAllKeys();
                } else {
                    character = (char) keyCode;
                }
                editable.insert(start, character.toString(character));
            }
        }

        @Override
        public void onText(CharSequence charSequence) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    public CollectorKeyboard(Activity host, int viewId, int layoutId, String folderName) {
        ActivityID = folderName;
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewId);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutId));
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                eventAction = event.getAction();
                if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_UP ||
                        eventAction == MotionEvent.ACTION_POINTER_DOWN || eventAction == MotionEvent.ACTION_POINTER_UP ||
                        eventAction == MotionEvent.ACTION_MOVE){
                    unixTime = Long.toString(System.currentTimeMillis());
                    PosX = Float.toString(event.getX());
                    PosY = Float.toString(event.getY());
                    Press = Float.toString(event.getPressure());
                    Area = Float.toString(event.getSize());
                    eventTime = Long.toString(event.getEventTime());
                    display = ((WindowManager) mHostActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    phoneOrientation = display.getRotation(); // 0 is portrait
                    Log.i("Jo", String.valueOf(phoneOrientation));

                    //Escrever no Arquivo
                    BufferKeyboardTouch = GravarArquivo(KeyboardTouchFile, BufferKeyboardTouch,unixTime + ',' + eventTime + ',' + ActivityID + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + phoneOrientation + '\n', false);

                    Log.i("Diego", "Toque no teclado");
                }
                return false;
            }
        });
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Folder = folderName;
        folder = new File(Environment.getExternalStorageDirectory() + File.separator + "aegis" + File.separator + Folder);
        KeyPressEventFile = new File("/sdcard/aegis/" + Folder + "/KeyPressEvent.csv");
        KeyboardTouchFile = new File ("/sdcard/aegis/" + Folder + "/KeyboardTouchEvent.csv");

        if (!folder.exists()) {
            folder.mkdirs();
        }
        if(!KeyPressEventFile.exists()){
            try {
                KeyPressEventFile.createNewFile();
                Log.i("Diego", "KeyPressEventFile Created");
            } catch (Exception e) {
                Log.e("Diego", "Could not create KeyPressEventFile",e);
            }
        }
        if(!KeyboardTouchFile.exists()){
            try {
                KeyboardTouchFile.createNewFile();
                Log.i("Diego", "KeyboardTouchFile Created");
            } catch (Exception e) {
                Log.e("Diego", "Could not create KeyboardTouchFile",e);
            }
        }


    }

    public void updateCollectorKeyboardLayout(int configOrientation) {
        if (configOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.collector_keyboard_landscape));
        } else if (configOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.collector_keyboard));
        }
        mKeyboardView.invalidateAllKeys();
    }

    public boolean isCollectorKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    public void showCollectorKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if(v != null) {
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void hideCollectorKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void registerEditText(int resourceId) {
        // Find the editText resourceId
        EditText editText = (EditText) mHostActivity.findViewById(resourceId);
        editText.setShowSoftInputOnFocus(false);
        //editText.setRawInputType(InputType.TYPE_NULL);
        //editText.setTextIsSelectable(true);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    showCollectorKeyboard(view);
                } else {
                    hideCollectorKeyboard();
                }
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCollectorKeyboard(view);
            }
        });

    }

}
