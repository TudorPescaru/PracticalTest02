package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;

    private ServerThread serverThread = null;

    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, serverPort);
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }
    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private EditText serverAddressEditText = null;

    private EditText clientServerPortEditText = null;

    private EditText pokemonNameEditText = null;

    private class GetPokemonInfoButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverAddress = serverAddressEditText.getText().toString();
            String serverPort = clientServerPortEditText.getText().toString();
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server address and port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            String pokemonName = pokemonNameEditText.getText().toString();
            if (pokemonName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Pokemon name should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, "[MAIN ACTIVITY] " + serverAddress + " " + serverPort + " " + pokemonName);
            pokemonAbilities.setText(Constants.EMPTY_STRING);
            pokemonTypes.setText(Constants.EMPTY_STRING);
            pokemonImage.setImageBitmap(null);

            ClientThread clientThread = new ClientThread(
                    serverAddress,
                    Integer.parseInt(serverPort),
                    pokemonName,
                    pokemonAbilities,
                    pokemonTypes,
                    pokemonImage,
                    false);
            clientThread.start();
        }
    }

    private final GetPokemonInfoButtonClickListener getPokemonInfoButtonClickListener = new GetPokemonInfoButtonClickListener();

    private class GetPokemonButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverAddress = serverAddressEditText.getText().toString();
            String serverPort = clientServerPortEditText.getText().toString();
            if (serverAddress.isEmpty() || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server address and port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(Constants.TAG, "[MAIN ACTIVITY] " + serverAddress + " " + serverPort + " ");
            pokemonAbilities.setText(Constants.EMPTY_STRING);
            pokemonTypes.setText(Constants.EMPTY_STRING);
            pokemonImage.setImageBitmap(null);

            ClientThread clientThread = new ClientThread(
                    serverAddress,
                    Integer.parseInt(serverPort),
                    "",
                    pokemonAbilities,
                    pokemonTypes,
                    pokemonImage,
                    true);
            clientThread.start();
        }
    }

    private final GetPokemonButtonClickListener getPokemonButtonClickListener = new GetPokemonButtonClickListener();

    private TextView pokemonAbilities = null;

    private TextView pokemonTypes = null;

    private ImageView pokemonImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method was invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        Button connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);
        serverAddressEditText = (EditText)findViewById(R.id.server_address_edit_text);
        clientServerPortEditText = (EditText)findViewById(R.id.client_server_port_edit_text);
        pokemonNameEditText = (EditText)findViewById(R.id.pokemon_name_edit_text);
        Button getPokemonInfoButton = (Button)findViewById(R.id.get_pokemon_info_button);
        getPokemonInfoButton.setOnClickListener(getPokemonInfoButtonClickListener);
        Button getPokemonButton = (Button)findViewById(R.id.get_pokemon_button);
        getPokemonButton.setOnClickListener(getPokemonButtonClickListener);
        pokemonAbilities = (TextView)findViewById(R.id.pokemon_abilities);
        pokemonTypes = (TextView)findViewById(R.id.pokemon_types);
        pokemonImage = (ImageView)findViewById(R.id.pokemon_image_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method was invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}