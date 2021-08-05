package mafia;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VoteServer implements Runnable {
	static Socket clientSocket;						// 클라이언트 소켓
	static HashMap<Integer, PrintWriter> clients;	// 연결된 클라이언트의 out 객체 저장
	static GameInfo gameInfo;						// 게임 정보 관리하는 객체
	
	public VoteServer(Socket clientSocket) {
		VoteServer.clientSocket = clientSocket;
	}
	
	public static void main(String[] args) {
		clients = new HashMap<Integer, PrintWriter>();
		Collections.synchronizedMap(clients);
		gameInfo = new GameInfo();
		ExecutorService eService = Executors.newFixedThreadPool(gameInfo.getMaxConnect()); // 고정된																							// 크기
		System.out.println("투표 서버 시작~");

	// 1. 클라이언트 연결 -> 쓰레드 실행
		try (ServerSocket sSocket = new ServerSocket(20001)) {
			while (true) {
				System.out.println("연결 대기 중 ......");
				clientSocket = sSocket.accept();
				VoteServer tes = new VoteServer(clientSocket);
				eService.submit(tes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	// 2. 쓰레드 종료	
		System.out.println("투표 서버 종료.");
		eService.shutdown();
	} // end main

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {

		// 1) 쓰레드 번호 부여 및 정보 출력
			int thread_number = gameInfo.getConnectCnt();
			gameInfo.increaseConnectCnt();
			System.out.println("플레이어 " + thread_number + " 연결됨! (" + gameInfo.getConnectCnt() + " / "
					+ (int) (gameInfo.getMaxConnect() - 1) + ")");
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

		// 6) 투표
			String readLine;
			while ((readLine = br.readLine()) != null) {
			// (1) 새 투표 시작하기 위해서 voteCnt 초기화
				if (gameInfo.getVoteCnt() == -1)
					gameInfo.setVoteCnt(0);
				
			// (2) 전달받은 값 출력	
				int value = Integer.parseInt(readLine);
				System.out.println("[플레이어 " + thread_number + "] " + value);

			// (3) 투표 유효성 검사
				while (true) {
					try {
						if (value < -1 || value >= gameInfo.getMaxConnect() - 1
								|| (value >= 0 && gameInfo.getAlive(value) == 0))
							throw new IllegalArgumentException();
						else
							break;

					} catch (IllegalArgumentException e) {
						out.println("invalid number");
						readLine = br.readLine();				// 유효하지 않으면 다시 전달 받음
						value = Integer.parseInt(readLine);
					}
				}

				out.println("valid number");
				
			// (4) 투표 반영
				gameInfo.increaseVoteCnt();
				System.out.println("현재 투표 수: " + gameInfo.getVoteCnt());
				if (value != -1)
					gameInfo.setVoteArr(value, gameInfo.getVoteArr(value) + 1);
				
			// (5) 결과 전송
				while (true) {
					Thread.sleep(1000);											// 모두 다 투표했는지 1초 간격으로 확인
					if (gameInfo.getVoteCnt() == gameInfo.getConnectCnt()) {	// 모두 다 투표했으면 투표 결과 전송
						sendVoteResult();
						gameInfo.setVoteCnt(-1);								// 투표 종료를 알림
					}

					if (gameInfo.getVoteCnt() == -1)							// 투표 종료 됐으면 처음으로 돌아감
						break;
				}
			}
			
		// 7) 종료	
			System.out.println("플레이어 " + thread_number + " 종료됨!");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
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
		
	// 투표 결과 전송	
		void sendVoteResult() {
		// 1. 투표 결과 받기
			int vote_result = gameInfo.getVoteResult();
			if (vote_result >= 0)				// 특정 플레이어 죽었으면
				gameInfo.decreaseConnectCnt(); 	// 연결된 수 1 감소
		// 2. 투표 결과 출력
			System.out.println("vote_result: " + vote_result);
			
		// 3. 투표 결과 전송
			for (int i = 0; i < gameInfo.getMaxConnect() - 1; i++) {
				if (gameInfo.getAlive(i) == 0)
					continue;

				// 1) 마피아 죽음 (게임 종료) 
				if (vote_result == gameInfo.getMafia()) {
					if (i == gameInfo.getMafia())	// 마피아한테 알리기
						clients.get(i).println("마피아가 죽었습니다. 마피아팀 패배!");
					
					else							// 시민한테 알리기
						clients.get(i).println("마피아가 죽었습니다. 시민팀 승리!");
					clients.get(i).println("finish");	// 게임 종료를 알림
				}
				
				// 2) 마피아 수 == 시민 수 (게임 종료) 
				else if (gameInfo.getConnectCnt() == 2) {
					if (i == gameInfo.getMafia())	// 마피아한테 알리기
						clients.get(i).println("시민이 한 명 남았습니다. 마피아팀 승리!");

					else							// 시민한테 알리기
						clients.get(i).println("시민이 한 명 남았습니다. 시민팀 패배!");
					clients.get(i).println("finish");	// 게임 종료를 알림
				}
				// 3) 다음 라운드 진행 (계속 진행) 
				else {
					if (vote_result == -1)			// 아무도 안죽음
						clients.get(i).println("아무도 죽지 않았습니다. ");

					else if (i == vote_result) {	// 죽은 사람에게 알리기
						gameInfo.setAlive(i, 0);	// 공유 객체 alive[i] 변경
						clients.get(i).println("die");
					}

					else							// 생존자에게
						clients.get(i).println(vote_result + "번 플레이어가 투표로 죽었습니다.");

					if (gameInfo.getAlive(i) == 1)	// 게임 진행을 알림
						clients.get(i).println("continue");
				}
			}
			gameInfo.clearVoteArr();
		}

}