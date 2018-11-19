package pignus.aegis;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Http extends AsyncTask<String, String, String> {
    private HttpCallbackInterface listener;
    private int request;
    private JSONObject postData;
    private int responseCode;

    public Http(HttpCallbackInterface listener, String login, String BufferAccel, String BufferGyro, String BufferMag , String BufferKeyPress, String BufferKeyboardTouch, int request){
        this.listener = listener;

        Map<String, String> bodyData = new HashMap<String, String>();
        bodyData.put("Login", login);
        bodyData.put("Accelerometer", BufferAccel);
        bodyData.put("Gyroscope", BufferGyro);
        bodyData.put("Magnetometer", BufferMag);
        bodyData.put("KeyPress", BufferKeyPress);
        bodyData.put("KeyboardTouch", BufferKeyboardTouch);

        this.postData = new JSONObject(bodyData);

        this.request = request;
    }

    public Http(HttpCallbackInterface listener, int request){
        this.listener = listener;
        this.request = request;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // dialog init'd here
    }

    @Override
    protected String doInBackground(String... strings) {
    try {
        Log.i("Diego", "Teste1");
        //String url = "http://10.0.2.2:8000/getjson/";
        //String url = "https://aegisserver.herokuapp.com/pignus/test";
        //String url = "http://8e585b19.ngrok.io/pignus/test";
        String url = "";
        if (request == 0){
            url = "http://9ddee87f.ngrok.io/pignus/single_request_login";
        }else{
            url = "http://9ddee87f.ngrok.io/pignus/users";
        }

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        Log.i("Diego", "Teste2");

        con.setDoInput(true);
        con.setDoOutput(true);

        // optional default is GET
        //con.setRequestMethod("GET");
        if(request == 0){
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");

            if (this.postData != null) {
                Log.i("Diego", postData.toString());
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }
        }else{
            con.setRequestMethod("GET");
        }

        responseCode = con.getResponseCode();

        if(responseCode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.i("Diego", response.toString());
            System.out.println(response.toString());
            return response.toString();
        }
        con.disconnect();
    }catch(Exception e){
        e.printStackTrace();
    }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(responseCode == 200){
            listener.Callback(result);
        }else{
            listener.errorCallback();
        }
    }
}
