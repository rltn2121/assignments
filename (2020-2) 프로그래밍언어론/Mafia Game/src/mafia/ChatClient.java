package mafia;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	static boolean stop = false;	// 쓰레드 실행을 멈추는 플래그
	static void setStop() {			
		stop = true;
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("채팅 클라이언트 시작~");
		try {
			InetAddress localAddress = InetAddress.getLocalHost();
			try (Socket cSocket = new Socket(localAddress, 8000);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream())) 
			) {
			// 1. 채팅 서버 연결, 다른 플레이어 연결 대기
				System.out.println("채팅 서버에 연결됨!");
				String start = "";
				if (start.equals("game start")) {
					System.out.println("다른 사용자 연결 대기 중...");
					start = br.readLine();
				}
				
			// 2. 플레이어 모두 입장, 게임 시작을 알리는 메시지 전달 받음
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

			// 4. sender, receiver 쓰레드 생성 및 실행
				Thread sender = new Thread(new ClientSender(cSocket));
				Thread receiver = new Thread(new ClientReceiver(cSocket));
				receiver.start();
				sender.start();

			// 5. 쓰레드 종료 대기
				sender.join();
				receiver.join();
				System.out.println("채팅 클라이언트 종료~");
			}
		} catch (IOException ex) {}
	} // end main

	static class ClientSender extends Thread {
		Socket socket;
		PrintWriter out;

		ClientSender(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {}
		}

		public void run() {
			Scanner scv = new Scanner(System.in);
			while (!stop && out != null) {					// 쓰레드 종료 조건 확인
				String inputLine = scv.nextLine();
				if (inputLine.equalsIgnoreCase("quit")) 	// quit 입력 -> sender 쓰레드 종료
					setStop();								// receiver 쓰레드 종료를 위해서 설정
				out.println(inputLine);						// ChatServer에 전송
			}
			scv.close();
		} 
	} // end ClientSender

	static class ClientReceiver extends Thread {
		Socket socket;
		BufferedReader br;

		ClientReceiver(Socket socket) {
			this.socket = socket;
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
			} catch (IOException e) {}
		}

		public void run() {
			try {
				while (!stop && br != null) 				// 쓰레드 종료 조건 확인 (sender 쓰레드에서 설정)
					System.out.println(br.readLine());		// ChatServer로부터 읽은 데이터 출력
			} catch (IOException e) {}
		}
	} // end ClientReceiver
}
