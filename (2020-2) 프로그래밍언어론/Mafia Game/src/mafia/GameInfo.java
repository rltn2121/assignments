package mafia;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class GameInfo {
	private final int MAX_CONNECT = 5;				// 최대 연결 가능 수 (메인 쓰레드 포함)
	private final Semaphore sem = new Semaphore(1);	// 쓰레드 동기화
	private int alive[];							// 플레이어 생존 여부
	private int vote_arr[];							// 플레이어별 득표 수
	private int mafia;								// 마피아 번호
	private int connect_cnt = 0;					// 현재 연결된 플레이어 수
	private int vote_cnt = 0;						// 현재 투표 수
	private boolean gameStart = false;				// 게임 시작 플래그
	
	// Constructor
	public GameInfo () {
		vote_arr = new int[MAX_CONNECT-1];
		alive = new int [MAX_CONNECT-1];
		initAlive();
		generateMafia();
		System.out.println("Mafia number in this game: " + getMafia());
	}
	
	// int MAX_CONNECT
	public int getMaxConnect(){
		return MAX_CONNECT;
	}
	
	// int alive[]
	public void initAlive(){
		for(int i = 0; i<MAX_CONNECT - 1; i++)
			alive[i] = 1;
	}
	public int getAlive(int i){
		return alive[i];
	}
	public void setAlive(int i, int value){
		try {
			sem.acquire();
			alive[i] = value;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// int vote_arr[]
	public void clearVoteArr(){
		for(int i = 0; i<MAX_CONNECT - 1; i++)
			vote_arr[i] = 0;
	}
	public int getVoteArr(int i){
		return vote_arr[i];
	}
	public void setVoteArr(int i, int value){
		try {
			sem.acquire();
			vote_arr[i] = value;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// int mafia
	public int getMafia(){
		return this.mafia;
	}
	public void generateMafia(){
		this.mafia = new Random().nextInt(MAX_CONNECT - 1);
	}
	
	// int connect_cnt
	public void decreaseConnectCnt(){
		try {
			sem.acquire();
			connect_cnt--;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void increaseConnectCnt(){
		try {
			sem.acquire();
			connect_cnt++;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	public int getConnectCnt(){
		return connect_cnt;
	}
	

	// int vote_cnt
	public void increaseVoteCnt(){
		try {
			sem.acquire();
			vote_cnt++;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public int getVoteCnt(){
		return vote_cnt;
	}
	public void setVoteCnt(int i){
		try {
			sem.acquire();
			vote_cnt = i;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// boolean gamestart
	public boolean getGameStart(){
		return gameStart;
	}
	public void setGameStart(){
		try {
			sem.acquire();
			gameStart = true;
			sem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// vote_result
	public int getVoteResult(){
		for(int i = 0; i<MAX_CONNECT - 1; i++)		// 플레이어별 득표 수 출력
			System.out.println("vote_arr[" + i + "] = " + vote_arr[i]);
		
		int max = -1;
		int ret = 0;
		boolean tie = false;						// 최다 득표 수 동점 여부
		for(int i = 0; i< MAX_CONNECT-1; i++){
			if(alive[i] == 0) continue;				// 해당 플레이어 이미 죽었으면 continue

			if(vote_arr[i] == max){					// 최다 득표 수 동점?
				tie = true;
				ret = -1;
			}
			else if(vote_arr[i] > max){				// 현재 플레이어 득표 수 > 최대 득표 수?
				tie = false;
				ret = i;
				max = vote_arr[i];
			}
		}
		return (tie ? -1 : ret);					// 동점(o) -> -1, 동점(x) -> 최다 득표 플레이어 번호
	}
}
