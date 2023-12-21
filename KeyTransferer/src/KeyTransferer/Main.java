package KeyTransferer;

import java.awt.EventQueue;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import com.github.kwhat.jnativehook.GlobalScreen;

import KeyTransferer.Server.ServerMain;

public class Main {
	
	public static Socket socket;
	public static ExecutorService es = Executors.newCachedThreadPool();
	
	public static String host = "192.168.0.100";
	public static int port = 7722;
	
	public static DataInputStream in;
	public static DataOutputStream out;
	
	public static boolean connected = false;
	public static boolean active = false;
	
	public static List<KeyPress> keys = new ArrayList<KeyPress>();
	public static Robot robot;
	
	public static List<String> toSend = new ArrayList<String>();
	
	public static int rcnt = 0;
	public static int pcnt = 0;
	public static int received = 0;
	public static int sent = 0;
	
	public static boolean autoOffset = true;
	public static boolean inGame = false;
	
	public static int senderOffset = 0;
	public static int offset = 0, offset2 = 0;
	
	public static long senderStartedTime = 0, senderTimeBeforeFirstKey = 0;
	public static boolean senderTimeBeforeFirstKeyFlag = false;
	
	public static long receiverStartedTime = 0, receiverFirstKeyTime = 0, receiverTimeBeforeFirstKey = 0;
	public static boolean receiverTimeBeforeFirstKeyFlag = false, receiverFirstKeyFlag = false, receiverStartedFlag = false;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try{
					Window window = new Window();
					window.frmKeyrecorder.setVisible(true);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new Keylogger());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		es.submit(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					ServerMain.startServer();
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		es.submit(new Runnable() {
			@Override
			public void run() {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						if(toSend.size() > 0) {
							try {
								List<String> temp = new ArrayList<String>(toSend);
								toSend.clear();
								String data = "keydata_";
								//sent = 0;
								for(String s : temp) {
									if(s.startsWith("keyp") || s.startsWith("keyop")) {
										//Utils.addLog("Send: " + s.split("-")[2]);
										sent++;
									}
									data += s + "_";
								}
								//Utils.addLog("Send Count: " + sent);
								String to = Window.targetsBox.getSelectedItem().toString();
								for(int i = 0;i < 1;i++) {
									Utils.sendMessage(to, data);
								}
							}catch(Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}, 500L, 500L);
			}
		});
		
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				this.cancel();
				Utils.addLog("按键监听器开始运行");
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							robot = new Robot();
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									long now = System.currentTimeMillis();
									List<KeyPress> toRemove = new ArrayList<KeyPress>();
									try {
										List<KeyPress> temp = new ArrayList<KeyPress>(keys);
										for(KeyPress kp : temp) {
											//System.out.println("123");
											if(kp.getTotalTime() <= now) {
												try {
													if(kp.release) {
														//Utils.addLog("release " + kp.key);
														robot.keyRelease(kp.key);
													}else {
														//Utils.addLog("press " + kp.key);
														robot.keyPress(kp.key);
													}
												}catch(Exception ex) {
													Utils.addLog("错误: 未知按键: " + kp.key);
												}
												toRemove.add(kp);
											}else {
												break;
											}
										}
										keys.removeAll(toRemove);
									}catch(Exception ex) {
										ex.printStackTrace();
									}
								}
							}, 1L, 1L);
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});
			}
		}, 1000L, 1000L);
	}
	
	public static void connect() {
		es.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Main.disconnect();
					
					host = Window.hostField.getText();
					port = Integer.valueOf(Window.portField.getText());
					socket = new Socket(host, port);
					
					connected = true;
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
					String name = "KeyTransferer-" + new Random().nextInt(1000000);
					Utils.sendMessage("", "ThreadName-S-" + name);
					Utils.sendMessage("", "getTargets");
					JOptionPane.showMessageDialog(null, "连接成功!", "连接", 1);
					Window.yourid.setText(name);
					
					try{
						while(true) {
							String sendTo = in.readUTF();
							int length = in.readInt();
							byte[] bytes = new byte[length];
							in.readFully(bytes);
							Main.onMessageReceive(sendTo, bytes);
						}
					}catch(Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "连接已断开!", "连接", 2);
						connected = false;
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "连接失败!", "连接", 0);
					connected = false;
					ex.printStackTrace();
				}
			}
		});
	}
	
	public static void disconnect() {
		if(socket != null) {
			Window.yourid.setText("未知");
			try{
				socket.close();
			}catch(IOException e) {
				JOptionPane.showMessageDialog(null, "断开连接失败!", "连接", 0);
				e.printStackTrace();
			}
		}
	}
	
	public static void onMessageReceive(String sendTo, byte[] b) {
		String msg = new String(b);
		if(msg.startsWith("keydata_") && Window.mode.getSelectedIndex() == 1) {
			String[] t = msg.split("_");
			received = 0;
			for(int i = 1;i < t.length;i++) {
				String s = t[i];
				if(s.startsWith("keyp")) {
					//System.out.println(s);
					String[] split = s.split("-");
					KeyPress k = new KeyPress(Utils.getJavaKeyCode(Integer.valueOf(split[1])), Long.valueOf(split[2]), false);
					k.needOffset = false;
					keys.add(k);
					//boolean flag = true;
					/*for(KeyPress kp : keys) {
						if(kp.id == k.id && kp.key == k.key) {
							flag = false;
							//Utils.addLog("flag1");
							break;
						}
					}
					if(flag) {
						received++;
						keys.add(k);
						//Utils.addLog("Receive: " + k.key);
					}*/
				}else if(s.startsWith("keyr")) {
					//System.out.println(s);
					String[] split = s.split("-");
					KeyPress k = new KeyPress(Utils.getJavaKeyCode(Integer.valueOf(split[1])), Long.valueOf(split[2]), true);
					k.needOffset = false;
					keys.add(k);
					//boolean flag = true;
					/*for(KeyPress kp : keys) {
						if(kp.id == k.id && kp.key == k.key) {
							flag = false;
							//Utils.addLog("flag2");
							break;
						}
					}
					if(flag) {
						//received++;
						keys.add(k);
					}*/
				}else if(s.startsWith("keyop")) {
					String[] split = s.split("-");
					KeyPress k = new KeyPress(Utils.getJavaKeyCode(Integer.valueOf(split[1])), Long.valueOf(split[2]), false);
					keys.add(k);
					if(s.startsWith("keyop1")) {
						receiverFirstKeyTime = k.time;
						//Utils.addLog("------------Receive First Key-------------");
						//Utils.addLog("receiverFirstKeyTime: " + receiverFirstKeyTime);
						if(receiverTimeBeforeFirstKeyFlag && receiverStartedFlag) {
							Main.receiverFirstKeyFlag = false;
							Main.receiverTimeBeforeFirstKeyFlag = false;
							Main.receiverStartedFlag = false;
							int offset = (int) (Main.receiverTimeBeforeFirstKey - (Main.receiverFirstKeyTime - Main.receiverStartedTime));
							Window.offset.setText(offset + "");
							Main.offset = offset;
							//Utils.addLog("offset: " + offset);
							//Utils.addLog("receiverTimeBeforeFirstKey: " + receiverTimeBeforeFirstKey);
							//Utils.addLog("receiverFirstKeyTime: " + receiverFirstKeyTime);
							//Utils.addLog("receiverStartedTime: " + receiverStartedTime);
							//Utils.addLog("---------------------------");
						}else {
							receiverFirstKeyFlag = true;
							//Utils.addLog("receiverFirstKeyFlag: " + true);
						}
					}
				}else if(s.startsWith("keyor")) {
					String[] split = s.split("-");
					KeyPress k = new KeyPress(Utils.getJavaKeyCode(Integer.valueOf(split[1])), Long.valueOf(split[2]), true);
					keys.add(k);
				}
			}
			//Utils.addLog("Receive Count: " + received);
			keys.sort(new Comparator<KeyPress>() {
				@Override
				public int compare(KeyPress k1, KeyPress k2) {
					if(k1.getTotalTime() < k2.getTotalTime()) {
						return 1;
					}else if(k1.getTotalTime() < k2.getTotalTime()) {
						return -1;
					}else{
						if(!k1.release && k2.release) {
							return 1;
						}
					}
					return 0;
				}
			});
		}else if(msg.startsWith("newOffset")){
			if(Main.autoOffset) {
				senderOffset = Integer.valueOf(msg.substring(9));
			}
		}else if(msg.startsWith("gameStart")){
			if(Main.autoOffset) {
				//Utils.addLog("--------Game Start--------");
				receiverTimeBeforeFirstKeyFlag = false;
				receiverStartedFlag = false;
				receiverFirstKeyFlag = false;
				Window.offset.setText(0 + "");
				Main.offset = 0;
			}
		}else if(msg.startsWith("tbfk")){
			if(Main.autoOffset) {
				receiverTimeBeforeFirstKey = Long.valueOf(msg.substring(4));
				receiverTimeBeforeFirstKeyFlag = true;
				//Utils.addLog("Receive TBFK: " + receiverTimeBeforeFirstKey);
			}
		}else if(msg.startsWith("targets")){
			String s = msg.replaceFirst("targets", "");
			String[] split = s.split("-T-");
			for(String t : split) {
				if(t.contains("KeyTransferer")) {
					Window.targetsBox.addItem(t);
				}
			}
		}
	}
	
}
