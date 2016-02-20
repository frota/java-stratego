package ifce.ppd.game;

import ifce.ppd.game.gfx.Screen;
import ifce.ppd.game.input.Mouse;
import ifce.ppd.game.net.StreamPPD;
import ifce.ppd.game.stratego.Stratego;
import ifce.ppd.game.stratego.StrategoClient;
import ifce.ppd.game.stratego.pieces.Flag;
import ifce.ppd.game.stratego.pieces.Bomb;
import ifce.ppd.game.stratego.pieces.Miner;
import ifce.ppd.game.stratego.pieces.Captain;
import ifce.ppd.game.stratego.pieces.Colonel;
import ifce.ppd.game.stratego.pieces.Spy;
import ifce.ppd.game.stratego.pieces.General;
import ifce.ppd.game.stratego.pieces.Major;
import ifce.ppd.game.stratego.pieces.Marshal;
import ifce.ppd.game.stratego.pieces.Piece;
import ifce.ppd.game.stratego.pieces.Sergeant;
import ifce.ppd.game.stratego.pieces.Scout;
import ifce.ppd.game.stratego.pieces.Lieutenant;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ClientWindow extends JFrame implements Runnable {

	private static final long serialVersionUID = 4029072019730363692L;

	private static final String title = "Combate v2.0";
	private static final int canvasWidth = 480;
	private static final int canvasHeight = 480;
	private static final int defaultPort = 8192;

	private String name, itsName;
	private String clientIp, serverIp;
	private int clientPort, serverPort;
	private String chat;

	private BufferedImage image =
			new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB); // alpha
	private int[] pixels =
			((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // buffer
	private Screen screen;
	private boolean running = false;
	private Thread thread;

	private StreamPPD stream; // Trata da conexão entre os clientes
	private StrategoClient stratego; // Trata de desenhar um Stratego

	private JPanel contentPane;
	private JPanel panelCanvas, panelConnect, panelStatus;
	private Canvas canvas;
	private JLabel lbl_GameInit1, lbl_GameInit2;
	private JLabel lbl_IP1, lbl_IP2;
	private JTextField txt_IP1, txt_IP2;
	private JLabel lbl_Port1, lbl_Port2;
	private JTextField txt_Port1, txt_Port2;
	private JButton btn_GameInit1, btn_GameInit2; // botões iniciar
	private JSeparator sep_Conn;
	private JLabel lbl_GameStatus;
	private JLabel lbl_PlayerStatus;
	private JPanel panelChat;
	private JScrollPane sp_Chat;
	private JLabel lbl_Chat;
	private JTextField txt_Chat;
	private JPanel panelGame;
	private JPanel panelPieces;
	private JButton btn_P_01, btn_P_02, btn_P_03, btn_P_04, btn_P_05, btn_P_06, btn_P_07,
			btn_P_08, btn_P_09, btn_P_10, btn_P_Bomb, btn_P_Flag; // botões peças
	private JPanel panelInfo;
	private JButton btn_Ready; // botão pronto
	private JButton btn_GiveUp; // botão desistir
	private JButton btn_About; // botão sobre
	private JButton btn_Help; // botão ajuda
	private JSeparator sep_Game1, sep_Game2;
	private JLabel lbl_EnemyPieces;
	private JLabel lbl_P_01, lbl_P_02, lbl_P_03, lbl_P_04, lbl_P_05, lbl_P_06, lbl_P_07,
			lbl_P_08, lbl_P_09, lbl_P_10, lbl_P_Bomb, lbl_P_Flag;

//	private JButton[] btns = {
//			btn_P_01, btn_P_02, btn_P_03, btn_P_04, btn_P_05, btn_P_06, btn_P_07,
//			btn_P_08, btn_P_09, btn_P_10, btn_P_Bomb, btn_P_Flag
//	};
//	private JLabel[] lbls = {
//			lbl_P_01, lbl_P_02, lbl_P_03, lbl_P_04, lbl_P_05, lbl_P_06, lbl_P_07,
//			lbl_P_08, lbl_P_09, lbl_P_10, lbl_P_Bomb, lbl_P_Flag
//	};

	public ClientWindow(String name) {
		this.name = name;
		itsName = "";
		try {
			clientIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			clientIp = "ERROR";
		}
		chat = "<html>";
		stream = new StreamPPD(this); // Prepara recebimento de pacotes
		stratego = new StrategoClient();

		/* Iniciando a janela do cliente */
		createWindow();
		setPieceButtonsEnabled(false);
		setVisible(true);
		txt_IP1.setText(clientIp);
		lbl_Chat.setText(chat);
		initPiecesButtons(); // minhas pecas
		initPiecesLabels(); // pecas dele

		/* Outros... */
		screen = new Screen(canvasWidth, canvasHeight);
		Mouse mouse = new Mouse();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);

		/* Eventos - Botões... */
		btn_GameInit1.addActionListener(new ActionListener() { // Iniciando partida
			public void actionPerformed(ActionEvent arg0) {
				try {
					clientPort = Integer.parseInt(txt_Port1.getText());
				} catch (Exception e) {
					clientPort = defaultPort;
					writeMessage(3, "INFO: Formato inválido, usando a porta \"" + clientPort + "\".");
				}
				if (clientPort < 1024 || clientPort > 65535) {
					clientPort = defaultPort;
					writeMessage(3, "INFO: Valor inválido, usando a porta \"" + clientPort + "\".");
				}
				txt_Port1.setText(clientPort + ""); // tenho meu ip:porta até aqui

				setPanelConnectEnabled(false);
				txt_IP2.setText("");
				txt_Port2.setText(""); // desativando panelConnect

				writeMessage(3, "INFO: Aguardando conexão em \"" + clientIp + ":" + clientPort + "\".");
				lbl_PlayerStatus.setText("Aguardando segundo jogador conectar!");
				stream.waitConnection(clientPort);

			}
		});
		btn_GameInit2.addActionListener(new ActionListener() { // Entrando em partida
			public void actionPerformed(ActionEvent arg0) {
				serverIp = txt_IP2.getText();
				if (serverIp.length() <= 0 || serverIp.length() > 35) {
					serverIp = "localhost";
					txt_IP2.setText(serverIp);
					writeMessage(3, "INFO: Formato inválido, usando o IP \"" + serverIp + "\".");
				}
				try {
					serverPort = Integer.parseInt(txt_Port2.getText());
				} catch (Exception e) {
					serverPort = defaultPort;
					writeMessage(3, "INFO: Formato inválido, usando a porta \"" + serverPort + "\".");
				}
				if (serverPort < 1024 || serverPort > 65535) {
					serverPort = defaultPort;
					writeMessage(3, "INFO: Valor inválido, usando a porta \"" + serverPort + "\".");
				}
				txt_Port2.setText(serverPort + ""); // tenho SEU ip:porta até aqui

				setPanelConnectEnabled(false);
				txt_Port1.setText(""); // desativando panelConnect

				writeMessage(3, "INFO: Entrando em \"" + serverIp + ":" + serverPort + "\"...");
				stream.initConnection(serverIp, serverPort);

			}
		});
		txt_Chat.addActionListener(new ActionListener() { // Enter no chat
			public void actionPerformed(ActionEvent e) {
				String msg = txt_Chat.getText();
				txt_Chat.setText("");
				if (msg.length() > 0) {
					if (msg.charAt(0) == '/') {
						if (msg.equals("/rand")) {
							int x = 0, y = 6;
							for (int i = 0; i < stratego.getStratego().getRedCollected().length; i++) { // percorre num
								while (stratego.getStratego().getRedCollected()[i] > 0) {
									Piece p;
									if (i + Piece.pSpy == Piece.pBomb) {
										p = new Bomb(Piece.cRed);
									} else if (i + Piece.pSpy == Piece.pFlag) {
										p = new Flag(Piece.cRed);
									} else if (i + Piece.pSpy == Piece.pScout) {
										p = new Scout(Piece.cRed);
									} else {
										p = new Piece();
										p.setColor(Piece.cRed);
										p.setRank(i + Piece.pSpy);
									}
									boolean b = stratego.getStratego().placePiece(p, x, y);
									if (b) {
										x++;
										if (x > 9) {
											x = 0;
											y++;
										}
									}
								}
							}
							stratego.getStratego().removePiece(9, 9);
							x = 0;
							y = 0;
							for (int i = 0; i < stratego.getStratego().getBlueCollected().length; i++) { // percorre num
								while (stratego.getStratego().getBlueCollected()[i] > 0) {
									Piece p;
									if (i + Piece.pSpy == Piece.pBomb) {
										p = new Bomb(Piece.cBlue);
									} else if (i + Piece.pSpy == Piece.pFlag) {
										p = new Flag(Piece.cBlue);
									} else if (i + Piece.pSpy == Piece.pScout) {
										p = new Scout(Piece.cBlue);
									} else {
										p = new Piece();
										p.setColor(Piece.cBlue);
										p.setRank(i + Piece.pSpy);
									}
									boolean b = stratego.getStratego().placePiece(p, x, y);
									if (b) {
										x++;
										if (x > 9) {
											x = 0;
											y++;
										}
									}
								}
							}
							stratego.getStratego().removePiece(9, 3);
						}
					} else {
						stream.sendCOMBX("COMB0" + msg);
						writePlayerMessage(stratego.getColor(), getMyName(), msg);
					}
				}
			}
		});

		btn_Ready.addActionListener(new ActionListener() { // Estou pronto
			public void actionPerformed(ActionEvent arg0) {
				boolean b = false;
				if (stratego.getColor() == Piece.cRed) { // sou red?
					b = stratego.getStratego().setReadyRed();
					if (b) {
						setGameStatus("<html><b>Sua vez de jogar!</b></html>");
					} else {
						setGameStatus("Aguardando adversário!");
					}
					stream.sendCOMBX("COMB5");
				} else if (stratego.getColor() == Piece.cBlue) { // sou blue?
					b = stratego.getStratego().setReadyBlue();
					if (b) {
						setGameStatus("Adversário jogando!");
					} else {
						setGameStatus("Aguardando adversário!");
					}
					stream.sendCOMBX("COMB5");
				}
				btn_Ready.setEnabled(false);
			}
		});
		btn_GiveUp.addActionListener(new ActionListener() { // Desistindo da partida
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		btn_P_01.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Spy(stratego.getColor()));
			}
		});
		btn_P_02.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Scout(stratego.getColor()));
			}
		});
		btn_P_03.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Miner(stratego.getColor()));
			}
		});
		btn_P_04.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Sergeant(stratego.getColor()));
			}
		});
		btn_P_05.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Lieutenant(stratego.getColor()));
			}
		});
		btn_P_06.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Captain(stratego.getColor()));
			}
		});
		btn_P_07.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Major(stratego.getColor()));
			}
		});
		btn_P_08.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Colonel(stratego.getColor()));
			}
		});
		btn_P_09.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new General(stratego.getColor()));
			}
		});
		btn_P_10.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Marshal(stratego.getColor()));
			}
		});
		btn_P_Bomb.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Bomb(stratego.getColor()));
			}
		});
		btn_P_Flag.addActionListener(new ActionListener() { // Peças
			public void actionPerformed(ActionEvent arg0) {
				stratego.setOnMouse(new Flag(stratego.getColor()));
			}
		});

		btn_Help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Não utilizado!", "Ajuda...", 1);
			}
		});
		btn_About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Não utilizado!", "Sobre...", 1);
			}
		});
	}

	public void run() {
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0; // 60 updates per second
		double delta = 0;
		requestFocus();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) { // updates determinado por tempo
				update();
				render(); // renders o quanto possivel
				delta--;
			}
		}
		stop();
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		stratego.update(this);
	}

	public void render() {
		BufferStrategy bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(2);
			return;
		}
		screen.clear();
		stratego.render(screen);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.getPixel(i);
		}
		Graphics graphics = bs.getDrawGraphics();
		graphics.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		graphics.dispose();
		bs.show();
	}

	public StreamPPD getStreamPpd() {
		return stream;
	}
	public void setBtnReadyEnabled(boolean b) {
		btn_Ready.setEnabled(b);
	}

	public StrategoClient getStrategoC() {
		return stratego;
	}

	private void initPiecesButtons() {
		btn_P_01.setText(Stratego.numPieces[0] + " de " + Stratego.numPieces[0]);
		btn_P_02.setText(Stratego.numPieces[1] + " de " + Stratego.numPieces[1]);
		btn_P_03.setText(Stratego.numPieces[2] + " de " + Stratego.numPieces[2]);
		btn_P_04.setText(Stratego.numPieces[3] + " de " + Stratego.numPieces[3]);
		btn_P_05.setText(Stratego.numPieces[4] + " de " + Stratego.numPieces[4]);
		btn_P_06.setText(Stratego.numPieces[5] + " de " + Stratego.numPieces[5]);
		btn_P_07.setText(Stratego.numPieces[6] + " de " + Stratego.numPieces[6]);
		btn_P_08.setText(Stratego.numPieces[7] + " de " + Stratego.numPieces[7]);
		btn_P_09.setText(Stratego.numPieces[8] + " de " + Stratego.numPieces[8]);
		btn_P_10.setText(Stratego.numPieces[9] + " de " + Stratego.numPieces[9]);
		btn_P_Bomb.setText(Stratego.numPieces[10] + " de " + Stratego.numPieces[10]);
		btn_P_Flag.setText(Stratego.numPieces[11] + " de " + Stratego.numPieces[11]);
	}

	public void setButtonsText(int[] values) {
		btn_P_01.setText(values[0] + " de " + Stratego.numPieces[0]);
		btn_P_02.setText(values[1] + " de " + Stratego.numPieces[1]);
		btn_P_03.setText(values[2] + " de " + Stratego.numPieces[2]);
		btn_P_04.setText(values[3] + " de " + Stratego.numPieces[3]);
		btn_P_05.setText(values[4] + " de " + Stratego.numPieces[4]);
		btn_P_06.setText(values[5] + " de " + Stratego.numPieces[5]);
		btn_P_07.setText(values[6] + " de " + Stratego.numPieces[6]);
		btn_P_08.setText(values[7] + " de " + Stratego.numPieces[7]);
		btn_P_09.setText(values[8] + " de " + Stratego.numPieces[8]);
		btn_P_10.setText(values[9] + " de " + Stratego.numPieces[9]);
		btn_P_Bomb.setText(values[10] + " de " + Stratego.numPieces[10]);
		btn_P_Flag.setText(values[11] + " de " + Stratego.numPieces[11]);

		btn_P_01.setEnabled(values[0] > 0);
		btn_P_02.setEnabled(values[1] > 0);
		btn_P_03.setEnabled(values[2] > 0);
		btn_P_04.setEnabled(values[3] > 0);
		btn_P_05.setEnabled(values[4] > 0);
		btn_P_06.setEnabled(values[5] > 0);
		btn_P_07.setEnabled(values[6] > 0);
		btn_P_08.setEnabled(values[7] > 0);
		btn_P_09.setEnabled(values[8] > 0);
		btn_P_10.setEnabled(values[9] > 0);
		btn_P_Bomb.setEnabled(values[10] > 0);
		btn_P_Flag.setEnabled(values[11] > 0);
	}

	private void initPiecesLabels() {
		lbl_P_01.setText("01: 0/" + Stratego.numPieces[0]);
		lbl_P_02.setText("02: 0/" + Stratego.numPieces[1]);
		lbl_P_03.setText("03: 0/" + Stratego.numPieces[2]);
		lbl_P_04.setText("04: 0/" + Stratego.numPieces[3]);
		lbl_P_05.setText("05: 0/" + Stratego.numPieces[4]);
		lbl_P_06.setText("06: 0/" + Stratego.numPieces[5]);
		lbl_P_07.setText("07: 0/" + Stratego.numPieces[6]);
		lbl_P_08.setText("08: 0/" + Stratego.numPieces[7]);
		lbl_P_09.setText("09: 0/" + Stratego.numPieces[8]);
		lbl_P_10.setText("10: 0/" + Stratego.numPieces[9]);
		lbl_P_Bomb.setText("BO: 0/" + Stratego.numPieces[10]);
		lbl_P_Flag.setText("BA: 0/" + Stratego.numPieces[11]);
	}

	public void setLabelsText(int[] values) {
		lbl_P_01.setText("01: " + values[0] + "/" + Stratego.numPieces[0]);
		lbl_P_02.setText("02: " + values[1] + "/" + Stratego.numPieces[1]);
		lbl_P_03.setText("03: " + values[2] + "/" + Stratego.numPieces[2]);
		lbl_P_04.setText("04: " + values[3] + "/" + Stratego.numPieces[3]);
		lbl_P_05.setText("05: " + values[4] + "/" + Stratego.numPieces[4]);
		lbl_P_06.setText("06: " + values[5] + "/" + Stratego.numPieces[5]);
		lbl_P_07.setText("07: " + values[6] + "/" + Stratego.numPieces[6]);
		lbl_P_08.setText("08: " + values[7] + "/" + Stratego.numPieces[7]);
		lbl_P_09.setText("09: " + values[8] + "/" + Stratego.numPieces[8]);
		lbl_P_10.setText("10: " + values[9] + "/" + Stratego.numPieces[9]);
		lbl_P_Bomb.setText("BO: " + values[10] + "/" + Stratego.numPieces[10]);
		lbl_P_Flag.setText("BA: " + values[11] + "/" + Stratego.numPieces[11]);
	}

	public void setPieceButtonsEnabled(boolean bool) {
		btn_P_01.setEnabled(bool);
		btn_P_02.setEnabled(bool);
		btn_P_03.setEnabled(bool);
		btn_P_04.setEnabled(bool);
		btn_P_05.setEnabled(bool);
		btn_P_06.setEnabled(bool);
		btn_P_07.setEnabled(bool);
		btn_P_08.setEnabled(bool);
		btn_P_09.setEnabled(bool);
		btn_P_10.setEnabled(bool);
		btn_P_Bomb.setEnabled(bool);
		btn_P_Flag.setEnabled(bool);
	}

	public void setPanelConnectEnabled(boolean bool) {
		txt_IP1.setEnabled(false);
		txt_Port1.setEnabled(bool);
		btn_GameInit1.setEnabled(bool);
		txt_IP2.setEnabled(bool);
		txt_Port2.setEnabled(bool);
		btn_GameInit2.setEnabled(bool);
	}

	public void writeMessage(int color, String message) {
		String col;
		if (color == 4) {
			col = "<font color=#ff0000>"; // vermelho
		} else if (color == 2) {
			col = "<font color=#339966>"; // verde
		} else if (color == 5) {
			col = "<font color=#0000ff>"; // azul
		} else if (color == 1) {
			col = "<font color=#ff6600>"; // erro
		} else if (color == 3) {
			col = "<font color=#991d99>"; // informação
		} else {
			col = "<font color=#000000>"; // preto
		}
		chat = chat + col + message + "</font><br>";
		lbl_Chat.setText(chat + "---</html>");
		JScrollBar bar = sp_Chat.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
	}

	public void setPlayerStatus(String status) {
		lbl_PlayerStatus.setText(status);
	}

	public void setGameStatus(String status) {
		lbl_GameStatus.setText(status);
	}

	public String getMyName() {
		return name;
	}

	public String getItsName() {
		return itsName;
	}

	public void setItsName(String iname) {
		itsName = iname;
	}

	public void writePlayerMessage(int p_col, String name, String msg) {
		String col;
		if (p_col == Piece.cRed) {
			col = "<font color=#ff0000>"; // vermelho
		} else if (p_col == Piece.cBlue) {
			col = "<font color=#0000ff>"; // azul
		} else {
			col = "<font color=#339966>"; // verde (neutro)
		}
		chat = chat + col + name + "</font>: " + msg + "<br>";
		lbl_Chat.setText(chat + "---</html>");
		JScrollBar bar = sp_Chat.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
	}

	private void createWindow() {
		setResizable(false);
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(904, 600);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelCanvas = new JPanel();
		panelCanvas.setBounds(307, 6, 480, 480);
		contentPane.add(panelCanvas);
		panelCanvas.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, canvasWidth, canvasHeight);
		panelCanvas.add(canvas);

		panelConnect = new JPanel();
		panelConnect.setBorder(new TitledBorder(null, "Bem-vindo(a), " + name, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelConnect.setBounds(3, 3, 300, 236);
		contentPane.add(panelConnect);
		panelConnect.setLayout(null);

		lbl_GameInit1 = new JLabel("Inicializar partida e aguardar segundo jogador:");
		lbl_GameInit1.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_GameInit1.setBounds(10, 16, 230, 14);
		panelConnect.add(lbl_GameInit1);

		lbl_IP1 = new JLabel("Meu IP*:");
		lbl_IP1.setBounds(20, 37, 140, 14);
		panelConnect.add(lbl_IP1);

		txt_IP1 = new JTextField();
		txt_IP1.setEnabled(false);
		txt_IP1.setBounds(20, 55, 140, 20);
		panelConnect.add(txt_IP1);
		txt_IP1.setColumns(10);

		lbl_Port1 = new JLabel("Minha porta:");
		lbl_Port1.setBounds(170, 37, 61, 14);
		panelConnect.add(lbl_Port1);

		txt_Port1 = new JTextField();
		txt_Port1.setBounds(170, 55, 61, 20);
		panelConnect.add(txt_Port1);
		txt_Port1.setColumns(10);

		btn_GameInit1 = new JButton("Iniciar partida e aguardar");
		btn_GameInit1.setToolTipText("Passe seus IP e porta para um segundo jogador para que voc\u00EAs se conectem e iniciem uma partida de Combate.");
		btn_GameInit1.setBounds(135, 86, 155, 23);
		panelConnect.add(btn_GameInit1);

		sep_Conn = new JSeparator();
		sep_Conn.setToolTipText("");
		sep_Conn.setBounds(10, 120, 280, 1);
		panelConnect.add(sep_Conn);

		lbl_GameInit2 = new JLabel("Entrar em partida j\u00E1 inicializada:");
		lbl_GameInit2.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_GameInit2.setBounds(10, 132, 230, 14);
		panelConnect.add(lbl_GameInit2);

		lbl_IP2 = new JLabel("IP de destino:");
		lbl_IP2.setBounds(20, 153, 140, 14);
		panelConnect.add(lbl_IP2);

		txt_IP2 = new JTextField();
		txt_IP2.setColumns(10);
		txt_IP2.setBounds(20, 171, 140, 20);
		panelConnect.add(txt_IP2);

		lbl_Port2 = new JLabel("Porta:");
		lbl_Port2.setBounds(170, 153, 61, 14);
		panelConnect.add(lbl_Port2);

		txt_Port2 = new JTextField();
		txt_Port2.setColumns(10);
		txt_Port2.setBounds(170, 171, 61, 20);
		panelConnect.add(txt_Port2);

		btn_GameInit2 = new JButton("Entrar em partida");
		btn_GameInit2.setToolTipText("Entre em uma partida j\u00E1 criada utilizando os IP e porta do cliente que criou a partida.");
		btn_GameInit2.setBounds(135, 202, 155, 23);
		panelConnect.add(btn_GameInit2);

		panelStatus = new JPanel();
		panelStatus.setBorder(new TitledBorder(null, "Estados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelStatus.setBounds(3, 245, 300, 55);
		contentPane.add(panelStatus);
		panelStatus.setLayout(null);

		lbl_GameStatus = new JLabel("---");
		lbl_GameStatus.setBounds(10, 16, 280, 14);
		panelStatus.add(lbl_GameStatus);

		lbl_PlayerStatus = new JLabel("---");
		lbl_PlayerStatus.setBounds(10, 34, 280, 14);
		panelStatus.add(lbl_PlayerStatus);

		panelChat = new JPanel();
		panelChat.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelChat.setBounds(3, 306, 300, 260);
		contentPane.add(panelChat);
		panelChat.setLayout(null);

		sp_Chat = new JScrollPane();
		sp_Chat.setBounds(8, 15, 284, 209);
		panelChat.add(sp_Chat);

		lbl_Chat = new JLabel("");
		//lbl_Chat.setFont(new Font("Arial", Font.PLAIN, 11));
		lbl_Chat.setVerticalAlignment(SwingConstants.TOP);
		lbl_Chat.setHorizontalAlignment(SwingConstants.LEFT);
		sp_Chat.setViewportView(lbl_Chat);

		txt_Chat = new JTextField();
		txt_Chat.setBounds(8, 229, 284, 20);
		panelChat.add(txt_Chat);
		txt_Chat.setColumns(10);

		panelGame = new JPanel();
		panelGame.setBorder(new TitledBorder(null, "Jogo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelGame.setBounds(307, 491, 480, 75);
		contentPane.add(panelGame);
		panelGame.setLayout(null);

		btn_Ready = new JButton("Pronto");
		btn_Ready.setFont(new Font("Tahoma", Font.BOLD, 16));
		btn_Ready.setEnabled(false);
		btn_Ready.setBounds(10, 19, 103, 45);
		panelGame.add(btn_Ready);

		btn_GiveUp = new JButton("Desistir");
		btn_GiveUp.setFont(new Font("Tahoma", Font.BOLD, 16));
		btn_GiveUp.setEnabled(false);
		btn_GiveUp.setBounds(367, 19, 103, 45);
		panelGame.add(btn_GiveUp);

		sep_Game1 = new JSeparator();
		sep_Game1.setOrientation(SwingConstants.VERTICAL);
		sep_Game1.setBounds(123, 19, 1, 45);
		panelGame.add(sep_Game1);

		sep_Game2 = new JSeparator();
		sep_Game2.setOrientation(SwingConstants.VERTICAL);
		sep_Game2.setBounds(356, 19, 1, 45);
		panelGame.add(sep_Game2);

		lbl_EnemyPieces = new JLabel("Pe\u00E7as inimigas capturadas:");
		lbl_EnemyPieces.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_EnemyPieces.setBounds(134, 11, 212, 14);
		panelGame.add(lbl_EnemyPieces);

		lbl_P_01 = new JLabel("01: 0/0");
		lbl_P_01.setBounds(134, 25, 44, 14);
		panelGame.add(lbl_P_01);

		lbl_P_02 = new JLabel("02: 0/0");
		lbl_P_02.setBounds(188, 25, 44, 14);
		panelGame.add(lbl_P_02);

		lbl_P_03 = new JLabel("03: 0/0");
		lbl_P_03.setBounds(242, 25, 44, 14);
		panelGame.add(lbl_P_03);

		lbl_P_04 = new JLabel("04: 0/0");
		lbl_P_04.setBounds(296, 25, 44, 14);
		panelGame.add(lbl_P_04);

		lbl_P_05 = new JLabel("05: 0/0");
		lbl_P_05.setBounds(134, 40, 44, 14);
		panelGame.add(lbl_P_05);

		lbl_P_06 = new JLabel("06: 0/0");
		lbl_P_06.setBounds(188, 40, 44, 14);
		panelGame.add(lbl_P_06);

		lbl_P_07 = new JLabel("07: 0/0");
		lbl_P_07.setBounds(242, 40, 44, 14);
		panelGame.add(lbl_P_07);

		lbl_P_08 = new JLabel("08: 0/0");
		lbl_P_08.setBounds(296, 40, 44, 14);
		panelGame.add(lbl_P_08);

		lbl_P_09 = new JLabel("09: 0/0");
		lbl_P_09.setBounds(134, 55, 44, 14);
		panelGame.add(lbl_P_09);

		lbl_P_10 = new JLabel("10: 0/0");
		lbl_P_10.setBounds(188, 55, 44, 14);
		panelGame.add(lbl_P_10);

		lbl_P_Bomb = new JLabel("BO: 0/0");
		lbl_P_Bomb.setBounds(242, 55, 44, 14);
		panelGame.add(lbl_P_Bomb);

		lbl_P_Flag = new JLabel("BA: 0/0");
		lbl_P_Flag.setBounds(296, 55, 44, 14);
		panelGame.add(lbl_P_Flag);

		panelPieces = new JPanel();
		panelPieces.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Suas pe\u00E7as", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPieces.setBounds(791, 3, 103, 384);
		contentPane.add(panelPieces);
		panelPieces.setLayout(null);

		btn_P_01 = new JButton("- de 0");
		btn_P_01.setToolTipText("Espi\u00E3o");
		btn_P_01.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/1.png")));
		btn_P_01.setBounds(10, 17, 83, 27);
		panelPieces.add(btn_P_01);

		btn_P_02 = new JButton("- de 0");
		btn_P_02.setToolTipText("Soldado");
		btn_P_02.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/2.png")));
		btn_P_02.setBounds(10, 47, 83, 27);
		panelPieces.add(btn_P_02);

		btn_P_03 = new JButton("- de 0");
		btn_P_03.setToolTipText("Cabo-armeiro");
		btn_P_03.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/3.png")));
		btn_P_03.setBounds(10, 77, 83, 27);
		panelPieces.add(btn_P_03);

		btn_P_04 = new JButton("- de 0");
		btn_P_04.setToolTipText("Sargento");
		btn_P_04.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/4.png")));
		btn_P_04.setBounds(10, 107, 83, 27);
		panelPieces.add(btn_P_04);

		btn_P_05 = new JButton("- de 0");
		btn_P_05.setToolTipText("Tenente");
		btn_P_05.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/5.png")));
		btn_P_05.setBounds(10, 137, 83, 27);
		panelPieces.add(btn_P_05);

		btn_P_06 = new JButton("- de 0");
		btn_P_06.setToolTipText("Capit\u00E3o");
		btn_P_06.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/6.png")));
		btn_P_06.setBounds(10, 167, 83, 27);
		panelPieces.add(btn_P_06);

		btn_P_07 = new JButton("- de 0");
		btn_P_07.setToolTipText("Major");
		btn_P_07.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/7.png")));
		btn_P_07.setBounds(10, 197, 83, 27);
		panelPieces.add(btn_P_07);

		btn_P_08 = new JButton("- de 0");
		btn_P_08.setToolTipText("Coronel");
		btn_P_08.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/8.png")));
		btn_P_08.setBounds(10, 227, 83, 27);
		panelPieces.add(btn_P_08);

		btn_P_09 = new JButton("- de 0");
		btn_P_09.setToolTipText("General");
		btn_P_09.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/9.png")));
		btn_P_09.setBounds(10, 257, 83, 27);
		panelPieces.add(btn_P_09);

		btn_P_10 = new JButton("- de 0");
		btn_P_10.setToolTipText("Marechal");
		btn_P_10.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/10.png")));
		btn_P_10.setBounds(10, 287, 83, 27);
		panelPieces.add(btn_P_10);

		btn_P_Bomb = new JButton("- de 0");
		btn_P_Bomb.setToolTipText("Bomba");
		btn_P_Bomb.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/b.png")));
		btn_P_Bomb.setBounds(10, 317, 83, 27);
		panelPieces.add(btn_P_Bomb);

		btn_P_Flag = new JButton("- de 0");
		btn_P_Flag.setToolTipText("Bandeira");
		btn_P_Flag.setIcon(new ImageIcon(ClientWindow.class.getResource("/icons/f.png")));
		btn_P_Flag.setBounds(10, 347, 83, 27);
		panelPieces.add(btn_P_Flag);

		panelInfo = new JPanel();
		panelInfo.setBorder(new TitledBorder(null, "Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInfo.setBounds(791, 389, 103, 177);
		contentPane.add(panelInfo);
		panelInfo.setLayout(null);

		btn_About = new JButton("Sobre...");
		btn_About.setBounds(10, 148, 83, 20);
		panelInfo.add(btn_About);

		btn_Help = new JButton("Ajuda...");
		btn_Help.setBounds(10, 123, 83, 20);
		panelInfo.add(btn_Help);

	}
}
