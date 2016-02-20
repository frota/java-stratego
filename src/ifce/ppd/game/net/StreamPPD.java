package ifce.ppd.game.net;

import ifce.ppd.game.ClientWindow;
import ifce.ppd.game.stratego.pieces.Piece;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

// Conecta os dois ClientWindow
public class StreamPPD implements Runnable {

	private DataInputStream dataIn = null;
	private DataOutputStream dataOut = null;
	private ServerSocket serverSocket = null;
	private Socket socket = null;

	private ClientWindow client;
	private boolean running = false;
	private boolean waiting = false;
	private Thread connection;

	private int waitingPort;

	public StreamPPD(ClientWindow cw) {
		client = cw;
		connection = new Thread(this, "Stream");
		connection.start();
	}

	public void run() {
		while (true) { // sempre esperando
			if (waiting) {
				try {
					int myColor;
					int itsColor = Piece.cNeutral;

					serverSocket = new ServerSocket(waitingPort);
					socket = serverSocket.accept();
					client.writeMessage(3, "INFO: \"" + socket.getInetAddress() + ":" + socket.getPort() + "\" se conectou.");
					client.setGameStatus("Distribua suas peças!");
					myColor = randomPlayerID();
					client.getStrategoC().setColor(myColor);
					if (myColor == Piece.cRed) {
						itsColor = Piece.cBlue;
					} else if (myColor == Piece.cBlue) {
						itsColor = Piece.cRed;
					}
					dataOut = new DataOutputStream(socket.getOutputStream());
					dataOut.writeUTF("COMB2" + itsColor); // criei meu
					client.getStrategoC().setColor(myColor);
					client.getStrategoC().setPlaying(true);
					dataOut = new DataOutputStream(socket.getOutputStream());
					dataOut.writeUTF("COMB1" + client.getMyName());
					if (myColor == Piece.cRed) {
						client.writeMessage(2, "INFO: Você foi sorteado como <font color=#ff0000>1º player</font>!");
					} else if (myColor == Piece.cBlue) {
						client.writeMessage(2, "INFO: Você foi sorteado como <font color=#0000ff>2º player</font>!");
					} else {
						client.writeMessage(2, "ERRO: Você foi sorteado como <font color=#339966>0º player</font>!");
					}
					client.setPieceButtonsEnabled(true);

					running = true;
				} catch (IOException e) {
					client.writeMessage(1, "ERRO: Porta já usada.");
					client.setPanelConnectEnabled(true);
					running = false;
				}
				waiting = false;
			}
			while (running) { // fica inputando
				try {
					dataIn = new DataInputStream(socket.getInputStream());
					String data = dataIn.readUTF();
					receiveCOMBX(data);
				} catch (IOException e) {
					running = false;
					waiting = false;
					client.writeMessage(1, "ERRO: O adversário se desconectou.");
					//System.exit(0);
				}
			}
		}
	}

	public synchronized void initConnection(String ip, int port) { // entrando em um ip:port
		waiting = false;
		try {
			socket = new Socket(ip, port); // se conectou, começa a rodar
			client.writeMessage(3, "INFO: Conectado à \"" + socket.getInetAddress() + ":" + socket.getPort() + "\".");
			dataOut = new DataOutputStream(socket.getOutputStream());
			dataOut.writeUTF("COMB1" + client.getMyName());
			client.setPieceButtonsEnabled(true);
			client.setGameStatus("Distribua suas peças!");
			running = true;
			// CONECTEI AO SERVER
		} catch (Exception e) {
			client.writeMessage(1, "ERRO: Não foi possível conectar.");
			client.setPanelConnectEnabled(true);
			running = false;
		}
	}

	public synchronized void waitConnection(int port) { // "Hosteando"
		waitingPort = port;
		waiting = true;
	}

	private int randomPlayerID() { // gera 1 ou 2
		return (new Random().nextInt(2147483647) % 2) + 1;
	}

	public void sendCOMBX(String data) {
		try {
			if (running) { // se "conectado", envia...
				dataOut = new DataOutputStream(socket.getOutputStream());
				dataOut.writeUTF(data);
			}
		} catch (Exception e) {
			waiting = false;
			running = false;
			client.writeMessage(1, "ERRO: Falha no envio de COMBX!");
		}
	}

	private void receiveCOMBX(String data) {
		if (data.length() < 5) {
			return;
		} else {
			String tipo = data.substring(0, 5); // COMBX
			String msg = data.substring(5); // resto
			if (tipo.equals("COMB0")) {
				// mensagem no chat
				if (client.getStrategoC().getColor() == Piece.cRed) {
					client.writePlayerMessage(Piece.cBlue, client.getItsName(), msg); // ele é blue
				} else if (client.getStrategoC().getColor() == Piece.cBlue) {
					client.writePlayerMessage(Piece.cRed, client.getItsName(), msg); // ele é red
				} else {
					client.writePlayerMessage(Piece.cNeutral, client.getItsName(), msg); // eu sou nada
				}
				// fim - mensagem de chat
			} else if (tipo.equals("COMB1")) {
				// nome do inimigo
				if (msg.length() > 0) {
					client.setItsName(msg);
				} else {
					client.setItsName("[noname]");
				}
				if (client.getStrategoC().getColor() == Piece.cRed) {
					msg = "<font color=#0000ff>" + msg; // ele é blue
				} else if (client.getStrategoC().getColor() == Piece.cBlue) {
					msg = "<font color=#ff0000>" + msg; // ele é red
				} else {
					msg = "<font color=#339966>" + msg;
				}
				client.setPlayerStatus("<html>Jogando com " + msg + "</font>!</html>");
				// fim - nome do inimigo
			} else if (tipo.equals("COMB2")) {
				// determinação de meu id pelo host
				int myColor;
				if (msg.length() >= 1) {
					try {
						myColor = Integer.parseInt(msg.charAt(0) + "");
					} catch (NumberFormatException e) {
						myColor = 0;
					}
				} else {
					myColor = 0;
				}
				System.out.println(myColor);
				client.getStrategoC().setColor(myColor);
				client.getStrategoC().setPlaying(true);
				if (myColor == Piece.cRed) {
					client.writeMessage(2, "INFO: Você foi sorteado como <font color=#ff0000>1º player</font>!");
				} else if (myColor == Piece.cBlue) {
					client.writeMessage(2, "INFO: Você foi sorteado como <font color=#0000ff>2º player</font>!");
				} else {
					client.writeMessage(2, "ERRO: Você foi sorteado como <font color=#339966>0º player</font>!");
				}
				client.setPieceButtonsEnabled(true);
				// fim - determinação de meu id pelo host
			} else if (tipo.equals("COMB3")) {
				// pondo peca no pre jogo (inimigo)
				if (msg.length() >= 4) {
					int id = Integer.parseInt(msg.substring(0, 2));
					int posx = Integer.parseInt(msg.substring(2, 3));
					int posy = Integer.parseInt(msg.substring(3, 4));
					Piece p = new Piece();
					int color = client.getStrategoC().getColor();
					if (color == Piece.cRed) {
						color = Piece.cBlue;
					} else if (color == Piece.cBlue) {
						color = Piece.cRed;
					} else {
						color = Piece.cNeutral;
					}
					p.setColor(color);
					p.setRank(id);
					client.getStrategoC().getStratego().placePiece(p, posx, posy);
				}
				// fim - pondo peca no pre jogo (inimigo)
			} else if (tipo.equals("COMB4")) {
				// tirando peca no pre jogo
				if (msg.length() >= 2) {
					int posx = Integer.parseInt(msg.substring(0, 1));
					int posy = Integer.parseInt(msg.substring(1, 2));
					client.getStrategoC().getStratego().removePiece(posx, posy);
				}
				// fim - tirando peca no pre jogo
			} else if (tipo.equals("COMB5")) {
				// recebi pronto
				boolean b = false;
				if (client.getStrategoC().getColor() == Piece.cRed) { // sou red
					b = client.getStrategoC().getStratego().setReadyBlue();
					if (b) { // todos prontos
						client.setGameStatus("<html><b>Sua vez de jogar!</b></html>");
					} else { // 
						client.setGameStatus("Distribua suas peças!");
					}
				} else if (client.getStrategoC().getColor() == Piece.cBlue) { // sou blue
					b = client.getStrategoC().getStratego().setReadyRed();
					if (b) { // todos prontos
						client.setGameStatus("Adversário jogando!");
					} else { // 
						client.setGameStatus("Distribua suas peças!");
					}
				}
				// fim - recebi pronto
			} else if (tipo.equals("COMB6")) {
				// jogada
				if (msg.length() >= 5) {
					int color = Integer.parseInt(msg.substring(0, 1));
					int ox = Integer.parseInt(msg.substring(1, 2)); // origem
					int oy = Integer.parseInt(msg.substring(2, 3));
					int nx = Integer.parseInt(msg.substring(3, 4)); // destino
					int ny = Integer.parseInt(msg.substring(4, 5));

					client.getStrategoC().callAnim2(ox, oy, nx, ny);
					client.getStrategoC().getStratego().makeAMove(color, ox, oy, nx, ny);
					client.setGameStatus("<html><b>Sua vez de jogar!</b></html>");
					if (client.getStrategoC().getColor() == Piece.cRed) {
						// se sou red, atualiza blue
						client.setLabelsText(client.getStrategoC().getStratego().getBlueCollected());
					} else if (client.getStrategoC().getColor() == Piece.cBlue) {
						// se sou blue, atualiza red
						client.setLabelsText(client.getStrategoC().getStratego().getRedCollected());
					}
				}
				// fim - jogada
			} else {
				System.out.println("NEUTRAL: Fora do protocolo!");
			}
		}
	}

	public synchronized void start() {
		running = true;
	}

	public synchronized void stop() {
		running = false;
	}

}
