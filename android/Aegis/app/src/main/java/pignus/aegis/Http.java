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
    private LoginActivity listener;
    private JSONObject postData;

    public Http(LoginActivity listener, String BufferAccel, String BufferGyro, String BufferMag , String BufferKeyPress, String BufferKeyboardTouch){
        this.listener = listener;
        Map<String, String> bodyData = new HashMap<String, String>();
        bodyData.put("Accelerometer", BufferAccel);
        bodyData.put("Gyroscope", BufferGyro);
        bodyData.put("Magnetometer", BufferMag);
        bodyData.put("KeyPress", BufferKeyPress);
        bodyData.put("KeyboardTouch", BufferKeyboardTouch);

        this.postData = new JSONObject(bodyData);
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
        String url = "http://8e585b19.ngrok.io/pignus/single_request_login";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        Log.i("Diego", "Teste2");

        con.setDoInput(true);
        con.setDoOutput(true);

        // optional default is GET
        //con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        Log.i("Diego", "Teste11");

        if (this.postData != null) {
            Log.i("Diego", postData.toString());
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(postData.toString());
            writer.flush();
        }

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);
        Log.i("Diego", "Teste12");

        int responseCode = con.getResponseCode();
        Log.i("Diego", "Teste4");
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        if(responseCode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                Log.i("Diego", "Teste5");
                response.append(inputLine);
            }
            in.close();

            Log.i("Diego", response.toString());
            Log.i("Diego", "Teste3");
            System.out.println(response.toString());
            return response.toString();
        }

    }catch(Exception e){
        e.printStackTrace();
    }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.Autenticate(result);
    }
}
