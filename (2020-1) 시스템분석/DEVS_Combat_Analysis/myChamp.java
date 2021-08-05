package midterm;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;
import Lab6_HW.request;
import Lab6_HW.result;

// 일정 시간마다 공격 사용
// 기본 공격(A) : 데미지 80, 재사용 대기시간 없음
// 신비한 화살(Q): 데미지 120, 재사용 대기시간 없음
// 정수의 흐름(W): 데미지 300, 재사용 대기시간 100 sigma
// 비전 이동(E) : 데미지 280, 재사용 대기시간 없음
// 정조준 일격(R): 데미지 650, 재사용 대기시간 300 sigma
public class myChamp extends ViewableAtomic {
	protected attack atk_msg;        // 공격정보를 전달할 attack 객체
	protected char skill;            // 공격방법
	protected int damage;            // 공격방법의 데미지
	protected int R_cooling_time;    // 정조준 일격(R)의 재사용 대기시간
	protected int W_cooling_time;    // 정수의 흐름(W)의 재사용 대기시간
	protected double int_arr_time;   // 공격의 시간 간격
	
	// Constructor
	public myChamp() {
		// 첫 공격을 20 sigma에 사용
		this("genr", 20);
	}
	
	// Constructor
	public myChamp(String name, double Int_arr_time) {
		super(name);
   
		// 포트 이름 설정
		addOutport("out");
		addInport("in");
		// 공격의 시간 간격 설정
		int_arr_time = Int_arr_time;
	}
  
	// 초기화
	public void initialize() {
		// attack 객체 생성
		atk_msg = new attack("none",'-',0);
		// 첫 공격을 기본 공격(A)으로 설정
		skill = 'A';
		damage = 80;
		// 정수의 흐름(W), 정조준 일격(R)의 재사용 대기시간 설정
		R_cooling_time = 300;
		W_cooling_time = 100;
		holdIn("active", int_arr_time);
	}
	
	// 외부에서 들어온 message 처리
	public void deltext(double e, message x) {
		Continue(e);
		if (phaseIs("active"))	{
			for (int i = 0; i < x.getLength(); i++)	{
				if (messageOnPort(x, "in", i))
						holdIn("stop", INFINITY);
			}
		}
	}
	
	// 공격 사용
	public void deltint() {
		if (phaseIs("active")) {
			int skill_type = 0;
			if(R_cooling_time > 0){
				if(W_cooling_time > 0){
					// 정수의 흐름(W), 정조준 일격(R)의 재사용 대기시간이 0보다 클 경우
					// 기본 공격(A), 신비한 화살(Q), 비전 이동(E) 중 하나를 랜덤하게 사용
					skill_type = (int)(Math.random()*3);
					switch(skill_type){
					case 0:
						skill = 'A';
						damage = 80;
						break;
					case 1:
						skill = 'Q';
						damage = 120;
						break;
					case 2:
						skill = 'E';
						damage = 280;
						break;
					}
					
					R_cooling_time -= 20;  // 정수의 흐름(W)의 재사용 대기시간 감소
					W_cooling_time -= 20;  // 정조준 일격(R)의 재사용 대기시간 감소
				}
				// 정수의 흐름(W)의 재사용 대기시간이 0일 경우
				else {
					skill = 'W';
					damage = 300;
					
					R_cooling_time-= 20;   // 정조준 일격(R)의 재사용 대기시간 감소
					W_cooling_time = 100;  // 정수의 흐름(W)의 재사용 대기시간 초기화
				}
			}
			// 정조준 일격(R)의 재사용 대기시간이 0일 경우
			else{
				skill = 'R';
				damage = 650;
				
				R_cooling_time = 300;  // 정조준 일격(R)의 재사용 대기시간 초기화
				if(W_cooling_time!=0)  // 정수의 흐름(W)의 재사용 대기시간이 0보다 클 경우 재사용 대기시간 감소
					W_cooling_time -= 20;
			}
			// 20 sigma의 시전 시간
			holdIn("active", 20);
		}
	}

	// 사용한 공격의 정보를 외부로 전달
	public message out() {	
		message m = new message();
		String name = "";
		// 전달할 message의 이름 설정
		if(skill == 'A')
			name = "기본 공격(A)";
		else if(skill == 'Q')
			name = "신비한 화살(Q)";
		else if(skill == 'W' )
			name = "정수의 흐름(W)";
		else if(skill == 'E')
			name = "비전 이동(E)";
		else if(skill == 'R')
			name = "정조준 일격(R)";
		
		// 사용한 공격의 정보를 전달할 attack 객체 사용 및 전달
		atk_msg = new attack(name, skill, damage); 
		m.add(makeContent("out", atk_msg));
		
		// 사용한 공격, 정수의 흐름(W), 정조준 일격(R)의 재사용 대기시간 출력
		System.out.println("● 사용한 공격: " + name);
		System.out.println("  - R 재사용 대기시간: " + R_cooling_time);
		System.out.println("  - w 재사용 대기시간: " + W_cooling_time);
		return m;
	}
  
	public String getTooltipText() {
		return
        super.getTooltipText()
        + "\n" + " int_arr_time: " + int_arr_time;
	}
}
