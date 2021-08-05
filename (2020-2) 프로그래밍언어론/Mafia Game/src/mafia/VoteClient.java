package mafia;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class VoteClient {
	public static void main(String[] args) {
		System.out.println("투표 클라이언트 시작~");
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			try (Socket cSocket = new Socket(localAddress, 20001);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()))
			) {
			// 1. 채팅 서버 연결 -> 다른 플레이어 연결 대기
				System.out.println("투표 서버에 연결됨!");
				Scanner scv = new Scanner(System.in);
				String start = "";
				if (start.equals("game start")) {
					System.out.println("다른 사용자 연결 대기 중...");
					start = br.readLine();
				}
				
			// 2. 플레이어 모두 입장 -> 게임 시작을 알리는 메시지 전달 받음
				br.readLine();
				String serverMessage = br.readLine();
				System.out.println(serverMessage);
				for (int i = 5; i > 0; i--) {
					serverMessage = br.readLine();
					System.out.println(serverMessage);
				}
				
			// 3. 자신의 역할 전달 받음
				String role = br.readLine();
				System.out.println("당신의 역할은 \"" + role + "\" 입니다.");
				
			// 4. 플레이어 입장을 알리는 메시지 출력
				for(int i = 0; i<4; i++)
					System.out.println(br.readLine());
				
			// 5. 투표 전송 -> 결과 받음
				while (true) {
				// 1) 투표 전송
					System.out.print("죽일 사람의 번호를 입력하세요 (기권: -1): ");
					String inputLine = scv.nextLine();
					out.println(inputLine);
					
				// 2) 유효성 검사
					String isVoteValid = br.readLine();						// (valid number) or (invalid number)
					while (isVoteValid.equals("invalid number")) {			// invalid number 이면 다시 전송
						System.out.println("존재하지 않거나 이미 죽은 플레이어입니다.");
						System.out.print("죽일 사람의 번호를 입력하세요: ");
						inputLine = scv.nextLine();
						out.println(inputLine);
						isVoteValid = br.readLine();
					}
				// 3) 투표 완료 대기
					System.out.println("투표 진행 중...");

				// 4) 결과 확인
					String response = br.readLine();
					System.out.println("투표 결과: " + response);
					if (response.equals("die")) {				// 생존 여부 확인
						System.out.println("당신은 투표로 인해서 죽었습니다.");
						break;
					}

				// 5) 게임 진행 여부 확인
					response = br.readLine();
					if (response.equals("finish")) {			// 게임 종료
						System.out.println("게임이 종료되었습니다.");
						break;
					} 
					
					else if (response.equals("continue")) 		// 계속 진행
						System.out.println("다음 라운드 진행");
				} // end while (vote)
				scv.close();
			}
		} catch (IOException ex) {}
	} // end main
}
