package mafia;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class test_client {
	public static void main(String[] args) {
		System.out.println("에코 클라이언트 시작~");
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			try (Socket cSocket = new Socket(localAddress, 20001);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true); // 송신용 버퍼
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream())) // 수신용 버퍼
			) {
				System.out.println("투표 서버에 연결됨!");
				Scanner scv = new Scanner(System.in);

				int start = 0;
				if (start == 0) {
					System.out.println("다른 사용자 연결 대기 중...");
					start = br.read();
				}
				br.readLine();
				String serverMessage = br.readLine();
				System.out.println(serverMessage);
				for (int i = 5; i > 0; i--) {
					serverMessage = br.readLine();
					System.out.println(serverMessage);

				}
				String role = br.readLine();
				System.out.println("당신의 역할은 \"" + role + "\" 입니다.");
				for(int i = 0; i<4; i++)
					System.out.println(br.readLine());
				
				while (true) {
					System.out.print("죽일 사람의 번호를 입력하세요 (기권: -1): ");
					String inputLine = scv.nextLine();
					out.println(inputLine);
					String result = br.readLine();

					while (result.equals("invalid number")) {
						System.out.println("존재하지 않거나 이미 죽은 플레이어입니다.");
						System.out.print("죽일 사람의 번호를 입력하세요: ");
						inputLine = scv.nextLine();
						out.println(inputLine);
						result = br.readLine();

					}
					System.out.println("투표 진행 중...");

					String response = br.readLine();
					System.out.println("서버 응답: " + response);
					// 생존 여부
					if (response.equals("die")) {
						System.out.println("당신은 투표로 인해서 죽었습니다.");
						break;
					}

					// 게임 진행 여부
					response = br.readLine();
					if (response.equals("finish")) {
						System.out.println("게임이 종료되었습니다.");
						break;
					} else if (response.equals("continue")) {
						System.out.println("다음 라운드 진행");
					}

				}
				scv.close();
			}
		} catch (IOException ex) {

		}
	}

	
}
