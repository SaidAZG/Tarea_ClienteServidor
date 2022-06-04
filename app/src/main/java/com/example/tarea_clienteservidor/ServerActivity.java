package com.example.tarea_clienteservidor;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Dimension;

import com.example.tarea_clienteservidor.databinding.ActivityServerBinding;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerActivity extends Activity {
    ActivityServerBinding binding;
    int port = 3030;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.etPort.setText("3030");


        binding.btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });
    }

    private void startServer() {
        ServerSocket s = null;

        if (Objects.requireNonNull(binding.etPort.getText()).length() > 0){
            port = Integer.parseInt(binding.etPort.getText().toString());
        }
        try{
            s = new ServerSocket(port);
            Log.d("Con Status","Iniciado en puerto "+port);
            TextView tv = new TextView(this);
            tv.append("Servidor iniciado en puerto "+port);
            binding.svChatServer.addView(tv);
            binding.btnMsgServer.setEnabled(true);
            binding.etMsgServer.setEnabled(true);
            //tv.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            //binding.text.append("\nServidor iniciado en puerto"+port);
        } catch(IOException e){
            Log.d("Con Status","No se puedo iniciar en puerto "+port);
            TextView tv = new TextView(this);
            tv.append("Servidor no se puede iniciar en puerto "+port);
            //binding.text.append("\nServidor no se puede iniciar en puerto "+port);
        }

        ServerSocket finalS = s;

        Thread server = new Thread(new Server(finalS));
        server.start();
    }

    public static class Server extends Thread{
        ServerSocket s;
        Server(ServerSocket s){
            this.s = s;
        }

        @Override
        public void run() {
            super.run();
            while(true){
                try{
                    Socket s1 = s.accept();
                    OutputStream os = s1.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    dos.writeUTF("Hola ESCOM");
                    dos.close();
                    s1.close();
                    //s.close();
                } catch(IOException e){
                    Log.d("Con Status","No se puede iniciar el socket "+e.getMessage());
                }
            }
        }
    }
}