package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");

            String pokemonName = bufferedReader.readLine();
            if (pokemonName == null || pokemonName.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (pokemon name)!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + pokemonName);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpGetResponse.getEntity();
            if (httpEntity != null) {
                pageSourceCode = EntityUtils.toString(httpEntity);
            }
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else {
                Log.i(Constants.TAG, pageSourceCode);
            }
            JSONObject content = new JSONObject(pageSourceCode);
            JSONArray abilitiesArray = content.getJSONArray(Constants.ABILITIES);
            StringBuilder abilities = new StringBuilder();
            for (int i = 0; i < abilitiesArray.length(); i++) {
                JSONObject ability = abilitiesArray.getJSONObject(i);
                JSONObject abilityObject = ability.getJSONObject(Constants.ABILITY);
                String abilityName = abilityObject.getString(Constants.NAME);
                abilities.append(abilityName);
                if (i < abilitiesArray.length() - 1) {
                    abilities.append(", ");
                }
            }
            JSONArray typesArray = content.getJSONArray(Constants.TYPES);
            StringBuilder types = new StringBuilder();
            for (int i = 0; i < typesArray.length(); i++) {
                JSONObject type = typesArray.getJSONObject(i);
                JSONObject typeObject = type.getJSONObject(Constants.TYPE);
                String typeName = typeObject.getString(Constants.NAME);
                types.append(typeName);
                if (i < typesArray.length() - 1) {
                    types.append(", ");
                }
            }
            JSONObject sprites = content.getJSONObject(Constants.SPRITES);
            String image = sprites.getString(Constants.FRONT_DEFAULT);

            printWriter.println(abilities.toString() + ';' + types + ';' + image);
            printWriter.flush();
        } catch (IOException | JSONException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
            }
        }
    }
}
