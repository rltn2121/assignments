package mafia;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class test_server implements Runnable {
	static HashMap<Integer, PrintWriter> clients;
	private static Socket clientSocket;
	static GameInfo gameInfo;
	static PrintWriter out_arr[];
	public test_server(Socket clientSocket) {
		test_server.clientSocket = clientSocket;
	}

	void sendToAll(String msg) {
		Iterator<Integer> it = clients.keySet().iterator();
		while (it.hasNext()) {
			PrintWriter out = (PrintWriter) clients.get(it.next());
			out.println(msg);
		}
	}

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

	void sendPlayerEntered(int player) {
		sendToAll("[플레이어 " + player + "] 입장.");
	}

	void sendVoteResult() {
		int vote_result = gameInfo.getVoteResult();
		if (vote_result >= 0)
			gameInfo.decreaseConnectCnt(); // 공유 객체 int connect_cnt 변경

		System.out.println("vote_result: " + vote_result);
		for (int i = 0; i < gameInfo.getMaxConnect() - 1; i++) {
			if (gameInfo.getAlive(i) == 0)
				continue;

			// 1. (게임 종료) 마피아 죽음
			if (vote_result == gameInfo.getMafia()) {
				// 마피아한테 알리기
				if (i == gameInfo.getMafia())
					clients.get(i).println("마피아가 죽었습니다. 마피아팀 패배!");
				// 시민한테 알리기
				else
					clients.get(i).println("마피아가 죽었습니다. 시민팀 승리!");
				clients.get(i).println("finish");
			}
			// 2. (게임 종료) 마피아 수 == 시민 수
			else if (gameInfo.getConnectCnt() == 2) {
				// 마피아한테 알리기
				if (i == gameInfo.getMafia())
					clients.get(i).println("시민이 한 명 남았습니다. 마피아팀 승리!");
				// 시민한테 알리기
				else
					clients.get(i).println("시민이 한 명 남았습니다. 시민팀 패배!");
				clients.get(i).println("finish");
			}
			// 3. (계속 진행) 다음 라운드 진행
			else {
				// 아무도 안죽음
				if (vote_result == -1)
					clients.get(i).println("아무도 죽지 않았습니다. ");

				// 죽은 사람에게 알리기
				else if (i == vote_result) {
					gameInfo.setAlive(i, 0); // 공유 객체 alive[i] 변경
					clients.get(i).println("die");
				}

				// 생존자에게
				else
					clients.get(i).println(vote_result + "번 플레이어가 투표로 죽었습니다.");

				if (gameInfo.getAlive(i) == 1)
					clients.get(i).println("continue");
			}
		}
		gameInfo.clearVoteArr();
	}

	public static void main(String[] args) {
		gameInfo = new GameInfo();
		clients = new HashMap<Integer, PrintWriter>();
		Collections.synchronizedMap(clients);
		ExecutorService eService = Executors.newFixedThreadPool(gameInfo.getMaxConnect()); // 고정된
																							// 크기
		System.out.println("투표 서버 시작~");

		// 플레이어 연결
		try (ServerSocket sSocket = new ServerSocket(20001)) {
			while (true) {
				System.out.println("연결 대기 중 ......");
				clientSocket = sSocket.accept();
				test_server tes = new test_server(clientSocket);
				eService.submit(tes);
				System.out.println(clientSocket.getInetAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("투표 서버 종료.");
		eService.shutdown();
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {

			// 1. 쓰레드 번호 부여
			int thread_number = gameInfo.getConnectCnt();
			gameInfo.increaseConnectCnt();
			System.out.println("클라이언트 " + thread_number + " 연결됨! (" + gameInfo.getConnectCnt() + " / "
					+ (int) (gameInfo.getMaxConnect() - 1) + ")");
			clients.put(thread_number, out); // hashMap에 client 추가

			// 2. 플레이어 수 확인 (1초 간격)
			while (gameInfo.getConnectCnt() != gameInfo.getMaxConnect() - 1)
				Thread.sleep(1000);

			if (!gameInfo.getGameStart())
				sendGameStart();
			else
				Thread.sleep(6000);

			// 3. 플레이어에게 역할 알려주기
			if (thread_number == gameInfo.getMafia())
				out.println("마피아");
			else
				out.println("시민");

			Thread.sleep(1000);
			sendPlayerEntered(thread_number); // 플레이어 입장 알림

			// 4. 투표 결과 전달 받음
			String readLine;
			while ((readLine = br.readLine()) != null) {
				if (gameInfo.getVoteCnt() == -1)
					gameInfo.setVoteCnt(0);
				int value = Integer.parseInt(readLine);
				System.out.println("[플레이어 " + thread_number + "] " + value);

				// 투표 유효성 검사
				while (true) {
					try {
						if (value < -1 || value >= gameInfo.getMaxConnect() - 1
								|| (value >= 0 && gameInfo.getAlive(value) == 0))
							throw new IllegalArgumentException();
						else
							break;

					} catch (IllegalArgumentException e) {
						out.println("invalid number");
						readLine = br.readLine();
						value = Integer.parseInt(readLine);
					}
				}

				out.println("valid number");
				// 투표 반영
				gameInfo.increaseVoteCnt();
				System.out.println("현재 투표 수: " + gameInfo.getVoteCnt());
				if (value != -1)
					gameInfo.setVoteArr(value, gameInfo.getVoteArr(value) + 1);

				while (true) {
					// 모두 다 투표했는지 1초 간격으로 확인
					Thread.sleep(1000);
					// 모두 다 투표했으면 투표 결과 전송
					if (gameInfo.getVoteCnt() == gameInfo.getConnectCnt()) {
						System.out.println("투표 다 함!!!!!!");
						sendVoteResult();
						gameInfo.setVoteCnt(-1);
					}

					if (gameInfo.getVoteCnt() == -1)
						break;
				}
			}
			System.out.println("클라이언트 " + thread_number + " 종료됨!");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}