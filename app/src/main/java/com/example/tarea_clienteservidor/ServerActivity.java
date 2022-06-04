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
    Server server;
    ServerSocket s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.etPort.setText("3030");

        binding.btnStartServer.setOnClickListener(v -> {
            s = null;
            if (Objects.requireNonNull(binding.etPort.getText()).length() > 0){
                port = Integer.parseInt(binding.etPort.getText().toString());
            }
            try{
                s = new ServerSocket(port);
                Log.d("Con Status","Iniciado en puerto "+port);
                desplegarMensajeServer(0,"Server iniciado en puerto: "+port);
                binding.btnMsgServer.setEnabled(true);
                binding.etMsgServer.setEnabled(true);
            } catch(IOException e){
                Log.d("Con Status","No se puedo iniciar en puerto "+port);
                desplegarMensajeServer(0,e.getMessage());
            }
            server = new Server(s);
            server.start();
        });

        binding.btnMsgServer.setOnClickListener(v->sendMsg(server));
    }

    private void sendMsg(Server server) {
        try{
            String msg = "Hola nuevamente";
            server.dos.writeUTF(msg);
            desplegarMensajeServer(1,msg);
        } catch (IOException e) {
            e.printStackTrace();
            desplegarMensajeServer(0,e.getMessage());
        }
    }

    private void startServer(Server server) {
        ServerSocket s = null;

        if (Objects.requireNonNull(binding.etPort.getText()).length() > 0){
            port = Integer.parseInt(binding.etPort.getText().toString());
        }
        try{
            s = new ServerSocket(port);
            Log.d("Con Status","Iniciado en puerto "+port);
            desplegarMensajeServer(0,"Server iniciado en puerto: "+port);
            binding.btnMsgServer.setEnabled(true);
            binding.etMsgServer.setEnabled(true);
        } catch(IOException e){
            Log.d("Con Status","No se puedo iniciar en puerto "+port);
            desplegarMensajeServer(0,e.getMessage());
        }

        ServerSocket finalS = s;

        server = new Server(finalS);
        server.start();
    }

    public class Server extends Thread{
        ServerSocket s;
        Socket s1;
        OutputStream os;
        DataOutputStream dos;
        Server(ServerSocket s){
            this.s = s;
        }
        @Override
        public void run() {
            super.run();
            while(true){
                try{
                    s1 = s.accept();
                    Log.d("Con Status","Cliente conectado");
                    os = s1.getOutputStream();
                    dos = new DataOutputStream(os);
                    dos.writeUTF("Hola Cliente");
                    //TODO se cierran los flujos y el socket
                    //dos.close();
                    //s1.close();
                    //s.close();
                } catch(IOException e){
                    Log.d("Con Status","No se puede iniciar el socket "+e.getMessage());
                    break;
                }
            }
        }
    }

    public void desplegarMensajeServer(int type,String msg){
        ServerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(type){
                    case 0:
                        binding.tvChatServer.append("\nStatus: "+msg);
                        break;
                    case 1:
                        binding.tvChatServer.append("\nServer: "+msg);
                        break;
                    case 2:
                        binding.tvChatServer.append("\nClient: "+msg);
                }
            }
        });
    }
}
