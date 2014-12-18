package org.ssascaling.network;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.ssascaling.ControlBus;
import org.ssascaling.monitor.Monitor;
import org.ssascaling.util.Ssascaling;
import org.ssascaling.util.Timer;

public class Receiver {
	
	private int port;
	
	public Receiver(){
		 init();
	}
	
	public Receiver(int port){
		 this.port = port;
	}


	public void receive() {

		ServerSocket echoServer = null;

		// Try to open a server socket on port 9999
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)
		try {
			echoServer = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e);
		}
		// Create a socket object from the ServerSocket to listen and accept
		// connections.
		// Open input and output streams

		for (;;) {
			try {

				final Socket clientSocket = echoServer.accept();
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {

							DataInputStream is = new DataInputStream(
									clientSocket.getInputStream());
							ControlBus.begin(is);

						} catch (IOException e) {
							System.out.println(e);
						} finally {
							try {
								clientSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				}).start();

			} catch (IOException e) {
				System.out.println(e);
			}

		}

	}
	
	
	
	private void init(){  
		Properties configProp = new Properties();
		
		try {
			configProp.load(Ssascaling.class.getClassLoader().getResourceAsStream("dom0.properties"));
			port = Integer.parseInt(configProp.getProperty("port"));
			Timer.setThreshold(Integer.parseInt(configProp.getProperty("violation_threshold")));
			//Monitor.setSampleInterval(Integer.parseInt(configProp.getProperty("sampling_interval")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
