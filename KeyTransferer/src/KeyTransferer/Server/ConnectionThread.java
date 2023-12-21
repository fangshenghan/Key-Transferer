package KeyTransferer.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import KeyTransferer.Main;
import KeyTransferer.Utils;
import KeyTransferer.Window;

public class ConnectionThread{
	
	public String name;
	public Socket socket;
	
	public BufferedInputStream in;
	public BufferedOutputStream out;
	public boolean exit = false;
	public boolean connected = false;
	
	public long registerTime;
	public ExecutorService es = Executors.newCachedThreadPool();
	
	public ConnectionThread subbed = null;
	public List<String> log = new ArrayList<String>();
	
	public ConnectionThread(Socket socket) {
		this.socket = socket;
		this.registerTime = System.currentTimeMillis();
		
		try{
			this.in = new BufferedInputStream(socket.getInputStream());
			this.out = new BufferedOutputStream(socket.getOutputStream());
			Utils.addLog("新的连接注册成功...");
			connected = true;
			processConnection();
		}catch(IOException e) {
			e.printStackTrace();
			Utils.addLog("新的连接注册失败!");
		}
	}
	
	public void processConnection() {
		es.submit(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						byte[] bytes = new byte[1024];
						in.read(bytes);
						if(!onMessageReceive(bytes)) {
							exit = true;
							connected = false;
							ServerMain.connections.remove(name);
							Utils.addLog("连接 " + name + " 已断开...");
							break;
						}
					}
				}catch(Exception ex) {
					exit = true;
					connected = false;
					ServerMain.connections.remove(name);
					Utils.addLog("连接 " + name + " 已断开...");
				}
			}
		});
		
		es.submit(new Runnable() {
			@Override
			public void run() {
				try{
					while(true) {
						if(name == null && System.currentTimeMillis() - registerTime >= 5000L){
							exit = true;
							connected = false;
							ServerMain.connections.remove(name);
							Utils.addLog("连接超时");
						}
						if(exit) {
							in.close();
							out.close();
							socket.close();
							connected = false;
							ServerMain.connections.remove(name);
							return;
						}
						Thread.sleep(10);
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	public boolean sendMessage(String data) {
		return ServerUtils.sendMessage(this, data);
	}
	
	public boolean sendMessage(byte[] data) {
		return ServerUtils.sendMessage(this, data);
	}
	
	public boolean onMessageReceive(byte[] b) {
		String msg = new String(b);
		if(msg.startsWith("ThreadName") && name == null) {
			name = msg.split("-S-")[1];
			ServerMain.connections.put(name, this);
			connected = true;
			Utils.addLog("连接 " + name + " 成功启动!");
			return true;
		}else if(name == null) {
			return true;
		}
		
		if(Window.mode.getSelectedIndex() == 0) {
			if(msg.startsWith("gameStart")) {
				Window.status.setText("游戏中");
				if(Main.active && Window.targetsBox.getSelectedItem() != null) {
					Utils.sendMessage(Window.targetsBox.getSelectedItem().toString(), "gameStart");
				}
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(20);
							Main.inGame = true;
						}catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				});
			}else if(msg.startsWith("gameQuit")) {
				Window.status.setText("未开始游戏");
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(20);
							Main.inGame = false;
						}catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				});
			}else if(msg.startsWith("gamePlaying")) {
				if(Window.targetsBox.getSelectedItem() != null) {
					if(Main.autoOffset) {
						Main.senderStartedTime = 0;
						for(char c : msg.toCharArray()) {
							if(Character.isDigit(c)) {
								Main.senderStartedTime *= 10;
								Main.senderStartedTime += Long.valueOf(String.valueOf(c));
							}
						}
						Main.senderTimeBeforeFirstKeyFlag = true;
					}
				}
			}
		}else {
			if(msg.startsWith("gameStart")) {
				Window.status.setText("游戏中");
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(100);
							Main.inGame = true;
						}catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				});
			}else if(msg.startsWith("gameQuit")) {
				Window.status.setText("未开始游戏");
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(100);
							Main.inGame = false;
						}catch(InterruptedException ex) {
							ex.printStackTrace();
						}
					}
				});
			}else if(msg.startsWith("gamePlaying")) {
				if(Main.autoOffset) {
					Main.receiverStartedTime = 0;
					for(char c : msg.toCharArray()) {
						if(Character.isDigit(c)) {
							Main.receiverStartedTime *= 10;
							Main.receiverStartedTime += Long.valueOf(String.valueOf(c));
						}
					}
					//Utils.addLog("------------Receive Started Time-------------");
					//Utils.addLog("receiverStartedTime: " + Main.receiverStartedTime);
					if(Main.receiverTimeBeforeFirstKeyFlag && Main.receiverFirstKeyFlag) {
						Main.receiverFirstKeyFlag = false;
						Main.receiverTimeBeforeFirstKeyFlag = false;
						Main.receiverStartedFlag = false;
						int offset = (int) (Main.receiverTimeBeforeFirstKey - (Main.receiverFirstKeyTime - Main.receiverStartedTime));
						Window.offset.setText(offset + "");
						Main.offset = offset;
						//Utils.addLog("offset2: " + offset);
						//Utils.addLog("receiverTimeBeforeFirstKey: " + Main.receiverTimeBeforeFirstKey);
						//Utils.addLog("receiverFirstKeyTime: " + Main.receiverFirstKeyTime);
						//Utils.addLog("receiverStartedTime: " + Main.receiverStartedTime);
						//Utils.addLog("---------------------------");
					}else {
						Main.receiverStartedFlag = true;
						//Utils.addLog("receiverStartedFlag: " + true);
					}
				}
			}
		}

		return true;
	}

}
