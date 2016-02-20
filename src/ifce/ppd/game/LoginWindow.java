package ifce.ppd.game;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author Frota
 *
 */
public class LoginWindow extends JFrame {

	private static final long serialVersionUID = -6217287961352613987L;

	private JPanel contentPane;
	private JTextField tf_Name;
	private JLabel lbl_Name, lbl_Status;
	private JSeparator sep_1;
	private JButton btn_Login;

	private static final int maxNameLength = 15;
	private static final String[] randomNames = {
		"Cooper", "Floyd", "Kenny", "Morris", "Stanley", "Vern", "Quincy",
		"Gus", "Ben", "Eddie", "Kent", "Oscar", "Phil", "Leon",
		"Pete", "Roger", "Maverick", "Cole", "Igor", "Larry", "Ray",
		"Sam", "Travis", "Kirk", "Alex", "Brent", "Jake", "Tex",
		"Lou", "Ace", "Rooster", "Eagle", "Sandman", "Barney", "Steel",
		"Duke", "Bear", "Kennedy", "Bob", "Tom", "Garry", "Don",
		"John", "Jay", "Carl", "Victor", "Louis", "Kevin", "Frank",
		"Cold", "Jack", "Stinger", "Tonny", "Tony", "Muller"
	};
	private static final String lookAndFeel =
			"com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	private String playerName;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow frame = new LoginWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LoginWindow() {
		createWindow();

		playerName = randomNames[new Random().nextInt(randomNames.length)];
		tf_Name.setText(playerName);

		btn_Login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				playerName = tf_Name.getText();
				int nl = playerName.length();
				if (nl <= 0) {
					lbl_Status.setText("Você deve inserir um nome!!!");
				} else if (nl > maxNameLength) {
					lbl_Status.setText("<html>Nome muito grande!!!<br>" +
							"Possui " + nl + " caracteres de no<br>" +
							"máximo " + maxNameLength + "!</html>");
				} else {
					lbl_Status.setText("Logando...");
					dispose(); // fecha login e "abre" cliente
					ClientWindow cw = new ClientWindow(playerName);
					cw.start();
				}
			}
		});
	}

	private void createWindow() {
		setResizable(false);
		setTitle("Login");
		try {
			UIManager.setLookAndFeel(lookAndFeel); // .getSystemLookAndFeelClassName()
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(180, 270);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lbl_Name = new JLabel("Nome do jogador:");
		lbl_Name.setToolTipText("Digite um nome pelo o qual voc\u00EA ser\u00E1 identificado na partida.");
		lbl_Name.setVerticalAlignment(SwingConstants.TOP);
		lbl_Name.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_Name.setBounds(10, 11, 154, 14);
		contentPane.add(lbl_Name);

		tf_Name = new JTextField();
		tf_Name.setBounds(10, 30, 154, 20);
		contentPane.add(tf_Name);
		tf_Name.setColumns(10);

		sep_1 = new JSeparator();
		sep_1.setBounds(10, 61, 154, 2);
		contentPane.add(sep_1);

		lbl_Status = new JLabel("");
		lbl_Status.setVerticalAlignment(SwingConstants.TOP);
		lbl_Status.setBounds(10, 74, 154, 122);
		lbl_Status.setForeground(Color.DARK_GRAY);
		contentPane.add(lbl_Status);

		btn_Login = new JButton("Logar");
		btn_Login.setBounds(58, 207, 59, 23); // 58 + (58 - 1) + 59 + 6 = 180
		getRootPane().setDefaultButton(btn_Login);
		contentPane.add(btn_Login);
	}

}
