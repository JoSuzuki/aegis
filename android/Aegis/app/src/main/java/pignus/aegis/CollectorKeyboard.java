package pignus.aegis;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
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


    private boolean caps = false;

    private String BufferKeyPress = "";

    String Folder;
    File folder;

    private final static int STRING_MAX_SIZE = 10000;

    File KeyPressEventFile;


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
            int PhoneOrientation = 0; // 0 is portrait


            //Escrever no Arquivo
            BufferKeyPress = GravarArquivo(KeyPressEventFile, BufferKeyPress,unixTime + ',' + "123" + ',' + pressType + ','
                    + "123" + ',' + keyCode + ',' + PhoneOrientation + '\n', false);



        }

        @Override
        public void onRelease(int i) {
            Log.i("Jo", "onRelease");

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
                caps = !caps;
                mKeyboardView.getKeyboard().setShifted(caps);
                mKeyboardView.invalidateAllKeys();
            } else if (keyCode == DONE) {
                hideCollectorKeyboard();
            } else if (keyCode == ENTER) {
                editable.append("/n");
            } else {
                Character character;
                if (caps) {
                    character = Character.toUpperCase((char) keyCode);
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
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewId);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutId));
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Folder = folderName;
        folder = new File(Environment.getExternalStorageDirectory() + File.separator + Folder);
        KeyPressEventFile = new File("/sdcard/" + Folder + "/KeyPressEvent.csv");

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
