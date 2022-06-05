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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ClientActivity extends Activity {
    ActivityClientBinding binding;
    int port = 3030;
    String ip = "10.0.2.16";
    Socket s1;
    Client client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStartClient.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.etPort.getText()).length() > 0 && Objects.requireNonNull(binding.etIp.getText()).length() > 0){
                port = Integer.parseInt(binding.etPort.getText().toString());
                ip = binding.etIp.getText().toString();
                Log.d("Socket Data","IP: "+ip);
                Log.d("Socket Data","Port:" +port);
            }
            try{
                s1 = new Socket(ip, port);
                ClientActivity.this.runOnUiThread(() -> {
                    binding.btnMsgClient.setEnabled(true);
                    binding.etMsgClient.setEnabled(true);
                });
                desplegarMensaje(0,"Conexion con servidor ("+ip+": "+port+")");
            } catch(ConnectException ce){
                Log.d("Con Status C","Servidor no conectado: "+ ce.getMessage());
                desplegarMensaje(0,ce.getMessage());
            } catch(IOException e){
                Log.d("Con Status C","IOException: "+e.getMessage());
                desplegarMensaje(0,e.getMessage());
            }
            client = new Client(s1);
            client.start();
        });

        binding.btnMsgClient.setOnClickListener(v->sendMsg(client));
    }

    private void sendMsg(Client client) {
        try{
            String msg = "Hola nuevamente server, soy el cliente";
            client.dos.writeUTF(msg);
            desplegarMensaje(2,msg);
        } catch (IOException e) {
            e.printStackTrace();
            desplegarMensaje(0,e.getMessage());
        }
    }

    public class Client extends Thread{
        Socket s1;
        OutputStream os;
        InputStream is;
        DataOutputStream dos;
        DataInputStream dis;

        Client(Socket s1){
            this.s1 = s1;
        }
        @Override
        public void run() {
            super.run();
            /*
            if (Objects.requireNonNull(binding.etPort.getText()).length() > 0 && Objects.requireNonNull(binding.etIp.getText()).length() > 0){
                port = Integer.parseInt(binding.etPort.getText().toString());
                ip = binding.etIp.getText().toString();
                Log.d("Socket Data","IP: "+ip);
                Log.d("Socket Data","Port:" +port);
            }
            try{
                Socket s1 = new Socket(ip, port);
                ClientActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnMsgClient.setEnabled(true);
                        binding.etMsgClient.setEnabled(true);
                    }
                });
                desplegarMensaje(0,"Conexion con servidor ("+ip+": "+port+")");

             */
            is = null;
            try {
                os = s1.getOutputStream();
                dos = new DataOutputStream(os);
                is = s1.getInputStream();
                dis = new DataInputStream(is);
                while (true) {
                    String msgRecieved = null;
                    try {
                        msgRecieved = dis.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Con Status",msgRecieved);
                    desplegarMensaje(1,msgRecieved);
                    //TODO Prueba para solo un echo de comunicacion, se cierran los flujos y el socket
                }
            } catch (IOException e) {
                e.printStackTrace();
                desplegarMensaje(0,e.getMessage());
            }
                /*dis.close();
                //s1.close();
            } catch(ConnectException ce){
                Log.d("Con Status C","Servidor no conectado: "+ ce.getMessage());
                desplegarMensaje(0,ce.getMessage());
            } catch(IOException e){
                Log.d("Con Status C","IOException: "+e.getMessage());
                desplegarMensaje(0,e.getMessage());
            }

                 */
        }
    }

    public void desplegarMensaje(int type,String msg){
        ClientActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(type){
                    case 0:
                        binding.tvChatClient.append("\nStatus: "+msg);
                        break;
                    case 1:
                        binding.tvChatClient.append("\nServer: "+msg);
                        break;
                    case 2:
                        binding.tvChatClient.append("\nClient: "+msg);
                }
            }
        });
    }
}
