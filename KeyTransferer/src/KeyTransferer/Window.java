package KeyTransferer;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Window {

	public JFrame frmKeyrecorder;
	public static JTextArea log;
	public static JTextField offset;
	public static JTextField hostField;
	public static JTextField portField;
	public static JComboBox targetsBox;
	public static JComboBox mode;
	public static JLabel yourid;
	public static JTextField sendDelay;
	public static JLabel status;
	public static JScrollPane scrollpane;
	public static JTextField offset2;

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKeyrecorder = new JFrame();
		frmKeyrecorder.setIconImage(Toolkit.getDefaultToolkit().getImage(Window.class.getResource("/image/kt.png")));
		frmKeyrecorder.setResizable(false);
		frmKeyrecorder.setTitle("KeyTransferer v1.2");
		frmKeyrecorder.setBounds(100, 100, 520, 543);
		frmKeyrecorder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmKeyrecorder.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("点击Page Up以 开始 按键传输\r\n");
		lblNewLabel.setFont(new Font("宋体", Font.BOLD, 18));
		lblNewLabel.setBounds(20, 11, 414, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel);
		
		JLabel lblpageDown = new JLabel("点击Page Down以 关闭 按键传输\r\n");
		lblpageDown.setFont(new Font("宋体", Font.BOLD, 18));
		lblpageDown.setBounds(20, 37, 414, 28);
		frmKeyrecorder.getContentPane().add(lblpageDown);
		
		JLabel lblNewLabel_1 = new JLabel("状态:");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(20, 62, 50, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1);
		
		scrollpane = new JScrollPane();
		scrollpane.setBounds(20, 90, 467, 251);
		frmKeyrecorder.getContentPane().add(scrollpane);
		scrollpane.setAutoscrolls(true);
		
		log = new JTextArea();
		log.setWrapStyleWord(true);
		log.setLineWrap(true);
		scrollpane.setViewportView(log);
		
		offset = new JTextField();
		offset.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					Main.offset = Integer.valueOf(offset.getText());
				}catch(Exception ex) {
					offset.setText("0");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					Main.offset = Integer.valueOf(offset.getText());
				}catch(Exception ex) {
					offset.setText("0");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					Main.offset = Integer.valueOf(offset.getText());
				}catch(Exception ex) {
					offset.setText("0");
				}
			}
		});
		offset.setEnabled(false);
		offset.setText("0");
		offset.setColumns(10);
		offset.setBounds(16, 473, 143, 21);
		frmKeyrecorder.getContentPane().add(offset);
		
		JLabel lblNewLabel_1_1_1_1 = new JLabel("偏移(毫秒):");
		lblNewLabel_1_1_1_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1_1_1_1.setBounds(16, 444, 111, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1_1_1_1);
		
		mode = new JComboBox();
		mode.setModel(new DefaultComboBoxModel(new String[] {"发送按键", "接收按键"}));
		mode.setBounds(66, 417, 95, 23);
		frmKeyrecorder.getContentPane().add(mode);
		
		JLabel lblNewLabel_1_1_2 = new JLabel("模式:");
		lblNewLabel_1_1_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1_1_2.setBounds(16, 414, 40, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1_1_2);
		
		JLabel hostLable = new JLabel("地址:");
		hostLable.setFont(new Font("宋体", Font.PLAIN, 14));
		hostLable.setBounds(16, 352, 67, 21);
		frmKeyrecorder.getContentPane().add(hostLable);
		
		hostField = new JTextField();
		hostField.setText("java.sharkmc.cn");
		hostField.setColumns(10);
		hostField.setBounds(64, 352, 218, 21);
		frmKeyrecorder.getContentPane().add(hostField);
		
		JLabel portLable = new JLabel("端口:");
		portLable.setFont(new Font("宋体", Font.PLAIN, 14));
		portLable.setBounds(16, 383, 67, 21);
		frmKeyrecorder.getContentPane().add(portLable);
		
		portField = new JTextField();
		portField.setText("47832");
		portField.setColumns(10);
		portField.setBounds(64, 383, 95, 21);
		frmKeyrecorder.getContentPane().add(portField);
		
		targetsBox = new JComboBox();
		targetsBox.setBounds(169, 383, 187, 22);
		frmKeyrecorder.getContentPane().add(targetsBox);
		
		JButton connectBtn = new JButton("连接");
		connectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!Main.connected) {
					Main.connect();
				}else {
					JOptionPane.showMessageDialog(null, "已经连接了", "连接", 2);
				}
			}
		});
		connectBtn.setBounds(169, 417, 145, 23);
		frmKeyrecorder.getContentPane().add(connectBtn);
		
		JButton btnDisconnect = new JButton("断开连接");
		btnDisconnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.disconnect();
			}
		});
		btnDisconnect.setBounds(324, 417, 163, 23);
		frmKeyrecorder.getContentPane().add(btnDisconnect);
		
		JButton refresh = new JButton("刷新目标");
		refresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Window.targetsBox.removeAllItems();
				Utils.sendMessage("", "getTargets");
			}
		});
		refresh.setBounds(366, 382, 121, 23);
		frmKeyrecorder.getContentPane().add(refresh);
		
		JLabel lblNewLabel_1_1_1_1_1 = new JLabel("你的ID:");
		lblNewLabel_1_1_1_1_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1_1_1_1_1.setBounds(290, 62, 75, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1_1_1_1_1);
		
		yourid = new JLabel("未知");
		yourid.setFont(new Font("宋体", Font.PLAIN, 14));
		yourid.setBounds(344, 62, 143, 28);
		frmKeyrecorder.getContentPane().add(yourid);
		
		JLabel lblNewLabel_1_1_1_1_2 = new JLabel("发送延迟(毫秒):");
		lblNewLabel_1_1_1_1_2.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1_1_1_1_2.setBounds(324, 444, 111, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1_1_1_1_2);
		
		sendDelay = new JTextField();
		sendDelay.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					if(Long.valueOf(sendDelay.getText()) < 1000) {
						sendDelay.setText("1000");
					}
				}catch(Exception ex) {
					sendDelay.setText("1000");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if(Long.valueOf(sendDelay.getText()) < 1000) {
						sendDelay.setText("1000");
					}
				}catch(Exception ex) {
					sendDelay.setText("1000");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					if(Long.valueOf(sendDelay.getText()) < 1000) {
						sendDelay.setText("1000");
					}
				}catch(Exception ex) {
					sendDelay.setText("1000");
				}
			}
		});
		sendDelay.setText("3000");
		sendDelay.setColumns(10);
		sendDelay.setBounds(324, 473, 163, 21);
		frmKeyrecorder.getContentPane().add(sendDelay);
		
		JLabel lblBysharky = new JLabel("By 鲨鱼君Sharky ");
		lblBysharky.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBysharky.setFont(new Font("微软雅黑", Font.BOLD | Font.ITALIC, 14));
		lblBysharky.setBounds(324, 10, 163, 28);
		frmKeyrecorder.getContentPane().add(lblBysharky);
		
		JCheckBox autoOffset = new JCheckBox("自动偏移");
		autoOffset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(autoOffset.isSelected()) {
					offset.setEnabled(false);
					offset2.setEnabled(true);
					Main.autoOffset = true;
				}else {
					offset.setEnabled(true);
					offset2.setEnabled(false);
					Main.autoOffset = false;
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(autoOffset.isSelected()) {
					offset.setEnabled(false);
					offset2.setEnabled(true);
					Main.autoOffset = true;
				}else {
					offset.setEnabled(true);
					offset2.setEnabled(false);
					Main.autoOffset = false;
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(autoOffset.isSelected()) {
					offset.setEnabled(false);
					offset2.setEnabled(true);
					Main.autoOffset = true;
				}else {
					offset.setEnabled(true);
					offset2.setEnabled(false);
					Main.autoOffset = false;
				}
			}
		});
		autoOffset.setSelected(true);
		autoOffset.setBounds(285, 351, 80, 23);
		frmKeyrecorder.getContentPane().add(autoOffset);
		
		status = new JLabel("未开始游戏");
		status.setFont(new Font("宋体", Font.PLAIN, 14));
		status.setBounds(61, 62, 143, 28);
		frmKeyrecorder.getContentPane().add(status);
		
		JButton clearLog = new JButton("清空日志");
		clearLog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				log.setText("");
			}
			@Override
			public void mousePressed(MouseEvent e) {
				log.setText("");
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				log.setText("");
			}
		});
		clearLog.setBounds(366, 351, 121, 23);
		frmKeyrecorder.getContentPane().add(clearLog);
		
		JLabel lblNewLabel_1_1_1_1_3 = new JLabel("微调(毫秒):");
		lblNewLabel_1_1_1_1_3.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1_1_1_1_3.setBounds(170, 444, 111, 28);
		frmKeyrecorder.getContentPane().add(lblNewLabel_1_1_1_1_3);
		
		offset2 = new JTextField();
		offset2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				try {
					Main.offset2 = Integer.valueOf(offset2.getText());
				}catch(Exception ex) {
					offset2.setText("10");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					Main.offset2 = Integer.valueOf(offset2.getText());
				}catch(Exception ex) {
					offset2.setText("10");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					Main.offset2 = Integer.valueOf(offset2.getText());
				}catch(Exception ex) {
					offset2.setText("10");
				}
			}
		});
		offset2.setText("0");
		offset2.setColumns(10);
		offset2.setBounds(169, 473, 145, 21);
		frmKeyrecorder.getContentPane().add(offset2);
	}
}
