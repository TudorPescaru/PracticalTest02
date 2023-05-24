package ro.pub.cs.systems.eim.practicaltest02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String pokemonName;
    private final TextView pokemonAbilities;
    private final TextView pokemonTypes;
    private final ImageView pokemonImage;

    private Socket socket;

    public ClientThread(String address, int port, String pokemonName, TextView pokemonAbilities, TextView pokemonTypes, ImageView pokemonImage) {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemonName;
        this.pokemonAbilities = pokemonAbilities;
        this.pokemonTypes = pokemonTypes;
        this.pokemonImage = pokemonImage;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(pokemonName);
            printWriter.flush();

            String pokemonInfoString;
            while ((pokemonInfoString = bufferedReader.readLine()) != null) {
                String[] pokemonInfo = pokemonInfoString.split(";");
                String pokemonAbilitiesString = pokemonInfo[0];
                String pokemonTypesString = pokemonInfo[1];
                String pokemonImageString = pokemonInfo[2];
                pokemonAbilities.post(() -> pokemonAbilities.setText(pokemonAbilitiesString));
                pokemonTypes.post(() -> pokemonTypes.setText(pokemonTypesString));
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(pokemonImageString);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(httpEntity.getContent());
                    pokemonImage.post(() -> pokemonImage.setImageBitmap(bitmap));
                }
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }
}
