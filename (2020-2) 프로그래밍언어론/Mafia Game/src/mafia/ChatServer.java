package mafia;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer extends Thread {
	static Socket clientSocket;						// 클라이언트 소켓
	static HashMap<Integer, PrintWriter> clients;	// 연결된 클라이언트의 out 객체 저장
	static GameInfo gameInfo;						// 게임 정보 관리하는 객체

	public ChatServer(Socket clientSocket) {
		ChatServer.clientSocket = clientSocket;
	}

	public static void main(String[] args) {
		clients = new HashMap<Integer, PrintWriter>();
		Collections.synchronizedMap(clients);
		gameInfo = new GameInfo();
		ExecutorService eService = Executors.newFixedThreadPool(gameInfo.getMaxConnect()); // 고정된 크기
		System.out.println("채팅 서버 시작~");

	// 1. 클라이언트 연결 -> 쓰레드 실행
		try (ServerSocket sSocket = new ServerSocket(8000)) {
			while (true) {
				System.out.println("연결 대기 중 ......");
				clientSocket = sSocket.accept();
				ChatServer tes = new ChatServer(clientSocket);
				eService.submit(tes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	// 2. 쓰레드 종료
		System.out.println("채팅 서버 종료.");
		eService.shutdown();
	} // end main

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {

		// 1) 쓰레드 번호 부여 및 정보 출력
			int thread_number = gameInfo.getConnectCnt();	// 연결된 숫자 기준으로 쓰레드 번호 부여
			gameInfo.increaseConnectCnt();
			System.out.println("플레이어 " + thread_number + " 연결됨! (" + gameInfo.getConnectCnt() + " / "	+ (int) (gameInfo.getMaxConnect() - 1) + ")");
			System.out.println(clientSocket.getInetAddress() + ":" + clientSocket.getPort());
			
		// 2) hashMap에 client의 out 객체 추가
			clients.put(thread_number, out); 

		// 3) 플레이어 수 확인 (1초 간격)
			while (gameInfo.getConnectCnt() != gameInfo.getMaxConnect() - 1)
				Thread.sleep(1000);
			
			if (!gameInfo.getGameStart())		// sendGameStart()를 한 번만 실행시키기 위함
				sendGameStart();				// sendGameStart() 내부에서 gameInfo.setGameStart() 호출
			else
				Thread.sleep(6000);				// sendGameStart() 이미 호출했으면 다른 쓰레드는 일정 시간 대기

		// 4) 플레이어에게 역할 알려주기
			if (thread_number == gameInfo.getMafia())
				out.println("마피아");
			else
				out.println("시민");

		// 5) 플레이어 입장 알림
			Thread.sleep(1000);
			sendPlayerEntered(thread_number);	
				
		// 6) 채팅
			String readLine;
			while ((readLine = br.readLine()) != null && gameInfo.getConnectCnt() > 0) {
				System.out.println("[플레이어 " + thread_number + "] " + readLine);
				sendToAll("[플레이어 " + thread_number + "] " + readLine);
			}
			
		// 7) 종료
			System.out.println("클라이언트 " + thread_number + " 종료됨!");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 
	} // end run
	
// iterator 이용해서 client hashMap 탐색
	void sendToAll(String msg) {
		Iterator<Integer> it = clients.keySet().iterator();
		while (it.hasNext()) {
			PrintWriter out = (PrintWriter) clients.get(it.next());
			out.println(msg);
		}
	}

// 모든 플레이어에게 게임 시작을 알림
	void sendGameStart() throws InterruptedException {
		gameInfo.setGameStart();
		sendToAll("game start");
		sendToAll("모든 플레이어가 접속했습니다!");
		Thread.sleep(1000);
		for (int i = 5; i > 0; i--) {
			sendToAll(i + "초 뒤에 게임이 시작됩니다.");
			Thread.sleep(1000);
		}
	}

// 플레이어 입장을 알리는 메시지 전달
	void sendPlayerEntered(int player) {
		sendToAll("[플레이어 " + player + "] 입장.");
	}
}