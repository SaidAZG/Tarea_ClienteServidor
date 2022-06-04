package com.example.tarea_clienteservidor;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.tarea_clienteservidor.databinding.ActivityClientBinding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ClientActivity extends Activity {
    ActivityClientBinding binding;
    int port = 3030;
    String ip = "10.0.2.16";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStartClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread client = new Thread(new ClientActivity.Client());
                client.start();
            }
        });
    }

    public class Client extends Thread{
        @Override
        public void run() {
            super.run();

            if (Objects.requireNonNull(binding.etPort.getText()).length() > 0 && Objects.requireNonNull(binding.etIp.getText()).length() > 0){
                port = Integer.parseInt(binding.etPort.getText().toString());
                ip = binding.etIp.getText().toString();
            }
            try{
                Socket s1 = new Socket(ip, port);
                binding.btnMsgClient.setEnabled(true);
                binding.etMsgClient.setEnabled(true);
                TextView tv = new TextView(ClientActivity.this);
                tv.append("Conexion con el servidor iniciada...");
                binding.svChatServer.addView(tv);
                while (true) {
                    InputStream is = s1.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    Log.d("Con Status",dis.readUTF());
                    TextView tv2 = new TextView(ClientActivity.this);
                    tv2.append("Servidor: "+dis.readUTF());
                    binding.svChatServer.addView(tv2);
                }
                //dis.close();
                //s1.close();
            } catch(ConnectException ce){
                Log.d("Con Status C","Servidor no conectado: "+ ce.getMessage());
                ClientActivity.this.runOnUiThread(() -> {
                    TextView tv = new TextView(ClientActivity.this);
                    tv.append("Error: "+ce.getMessage());
                    tv.setTextColor(Color.RED);
                    binding.svChatServer.addView(tv);
                });
            } catch(IOException e){
                Log.d("Con Status C","IOException: "+e.getMessage());
                ClientActivity.this.runOnUiThread(() -> {
                    TextView tv = new TextView(ClientActivity.this);
                    tv.append("Error: "+e.getMessage());
                    tv.setTextColor(Color.RED);
                    binding.svChatServer.addView(tv);
                });

            }
        }
    }
}
