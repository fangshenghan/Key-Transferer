package KeyTransferer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keylogger implements NativeKeyListener, KeyListener {
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		//Utils.addLog("Press: " + e.getKeyText(e.getKeyCode()));
		//Utils.addLog(e.getKeyCode() + " -> " + Utils.getJavaKeyCode(e.getKeyCode()));
		if(e.getKeyCode() == 3657) { //Page Up Key
			Utils.addLog("按键传输已开启...");
			Main.active = true;
			Main.sent = 0;
			/*Utils.addLog("pcnt: " + Main.pcnt);
			Utils.addLog("rcnt: " + Main.rcnt);
			Utils.addLog("keys: " + Main.keys.size());
			Utils.addLog("received: " + Main.received);
			Utils.addLog("sent: " + Main.sent);
			Main.pcnt = 0;
			Main.rcnt = 0;
			Main.sent = 0;
			Main.received = 0;*/
		}else if(e.getKeyCode() == 3665) { //Page Down Key 3665     insert 3666
			Utils.addLog("按键传输已关闭");
			Main.active = false;
			int presses = 0, releases = 0;
			for(KeyPress kp : Main.keys){
				if(kp.release) {
					releases++;
				}else {
					presses++;
				}
			}
			Utils.addLog("接收按键数量: " + (presses + releases) / 2);
			//Utils.addLog("Received Releases: " + releases);
			Utils.addLog("发送按键数量: " + Main.sent);
		}else if(Main.active && Window.mode.getSelectedIndex() == 0) {
			//Main.pcnt++;
			if(Main.inGame) {
				if(Main.senderTimeBeforeFirstKeyFlag) {
					Main.senderTimeBeforeFirstKeyFlag = false;
					Main.senderTimeBeforeFirstKey = System.currentTimeMillis() - Main.senderStartedTime;
					if(Window.targetsBox.getSelectedItem() != null) {
						//Utils.addLog("tbfk" + Main.senderTimeBeforeFirstKey);
						Utils.sendMessage(Window.targetsBox.getSelectedItem().toString(), "tbfk" + Main.senderTimeBeforeFirstKey);
					}
					String data = "keyop1-" + e.getKeyCode() + "-" + (System.currentTimeMillis() + Long.valueOf(Window.sendDelay.getText()));
					Main.toSend.add(data);
				}else{
					String data = "keyop-" + e.getKeyCode() + "-" + (System.currentTimeMillis() + Long.valueOf(Window.sendDelay.getText()));
					Main.toSend.add(data);
				}
				//Utils.addLog(data);
			}else {
				String data = "keyp-" + e.getKeyCode() + "-" + (System.currentTimeMillis() + Long.valueOf(Window.sendDelay.getText()));
				Main.toSend.add(data);
				//Utils.addLog(data);
			}
			
			/*String to = Window.targetsBox.getSelectedItem().toString();
			//Utils.addLog("send press " + to + " " + data);
			for(int i = 0;i < 3;i++) {
				Utils.sendMessage(to, data);
			}*/
		}
	}
	
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		//Utils.addLog("Release: " + e.getKeyText(e.getKeyCode()));
		if(Main.active && Window.mode.getSelectedIndex() == 0 && e.getKeyCode() != 3657 && e.getKeyCode() != 3665) {
			//Main.rcnt++;
			if(Main.inGame) {
				String data = "keyor-" + e.getKeyCode() + "-" + (System.currentTimeMillis() + Long.valueOf(Window.sendDelay.getText()));
				Main.toSend.add(data);
			}else {
				String data = "keyr-" + e.getKeyCode() + "-" + (System.currentTimeMillis() + Long.valueOf(Window.sendDelay.getText()));
				Main.toSend.add(data);
			}
			//String to = Window.targetsBox.getSelectedItem().toString();
			//Utils.addLog("send release " + to + " " + data);
			/*for(int i = 0;i < 3;i++) {
				Utils.sendMessage(to, data);
			}*/
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
