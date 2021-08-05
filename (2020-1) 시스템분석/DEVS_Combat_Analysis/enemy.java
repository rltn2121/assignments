package midterm;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;
// myChamp.java에서 전송한 공격의 공격방법 별 누적 데미지와 총 데미지를 계산
// combat_result 객체를 생성하여 공격방법 별 사용횟수, 데미지 점유율과 총 데미지 계산 및 출력
public class enemy extends ViewableAtomic {
	protected Queue q;
	protected attack atk_msg;             // myChamp.java에서 전달된 attack 객체
	protected combat_result combat_msg;   // 전투 결과를 전달할 combat_result 객체
	
	// 공격방법 별 누적 데미지
	protected int A_sum;
	protected int Q_sum;
	protected int W_sum;
	protected int E_sum;
	protected int R_sum;
	
	// 공격방법 별 계산시간
	protected int A_proc_time;
	protected int Q_proc_time;
	protected int W_proc_time;
	protected int E_proc_time;
	protected int R_proc_time;
	
	// 총 데미지
	protected int total_damage;
	// combat_result 객체의 이름
	protected String name;

	// constructor
	public enemy() {
		this("procQ");
	}
	// constructor
	public enemy(String name) {
		super(name);
		// 포트 이름 설정
		addInport("in");
		addOutport("out");
	}
	// 초기화
	public void initialize() {
		// queue, attack, combat_result 객체 생성
		q = new Queue();
		atk_msg = new attack("none",'-',0);
		combat_msg = new combat_result("none", 0, 0, 0, 0, 0);
		
		// 공격방법 별 누적 데미지 초기화
		A_sum = 0;
		Q_sum = 0;
		W_sum = 0;
		E_sum = 0;
		R_sum = 0;
		
		// 공격방법 별 계산시간 초기화
		A_proc_time = 15;
		Q_proc_time = 15;
		W_proc_time = 20;
		E_proc_time = 25;
		R_proc_time = 30;
		total_damage = 0;
		name = null;
		holdIn("passive", INFINITY);
	}
	
	// myChamp로 부터 받은 공격의 누적 데미지, 총 데미지, 사용횟수, 데미지 점유율 계산
	public void deltext(double e, message x) {
		Continue(e);
		// 처리 중인 작업이 없을 경우
		if (phaseIs("passive"))	{
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// attack 객체 받기
					atk_msg = (attack)x.getValOnPort("in", i);
					// 총 데미지 계산
					total_damage += atk_msg.damage;
					// 공격방법 별 누적 데미지 계산
					switch(atk_msg.skill){
					case 'A':
						A_sum += atk_msg.damage;
						holdIn("busy", A_proc_time);
						break;
					case 'Q':
						Q_sum += atk_msg.damage;
						holdIn("busy", Q_proc_time);
						break;
					case 'E':
						E_sum += atk_msg.damage;
						holdIn("busy", E_proc_time);
						break;
					case 'W':
						W_sum += atk_msg.damage;
						holdIn("busy", W_proc_time);
						break;
					case 'R':
						R_sum += atk_msg.damage;
						holdIn("busy", R_proc_time);
						break;
					}		
					// combat_result 객체의 이름 설정
					name = "total damage: " + total_damage;
					// combat_result 객체 생성
					combat_msg = new combat_result (name, A_sum, Q_sum, W_sum, E_sum, R_sum);
					// 총 데미지, 공격횟수, 데미지 점유율 계산
					combat_msg.setTotalDamage();
					combat_msg.setSkillCount();
					combat_msg.setSkillShare();
				}
			}
		}
		// 처리 중인 작업이 있을 경우
		else if (phaseIs("busy")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "in", i)) {
					// attack 객체를 받아서 queue에 저장
					attack temp = (attack)x.getValOnPort("in", i);
					q.add(temp);
				}
			}
		}
	}
	
	// queue에 있는 작업 처리
	public void deltint() {
		if (phaseIs("busy")) {
			// queue에 처리하지 않은 객체가 남은 경우
			if (!q.isEmpty()) {
				// queue에서 attack 객체 꺼냄
				atk_msg = (attack) q.removeFirst();
				// 총 데미지 계산
				total_damage += atk_msg.damage;
				// 공격방법 별 누적 데미지 계산
				switch(atk_msg.skill){
				case 'A':
					A_sum += atk_msg.damage;
					holdIn("busy", A_proc_time);
					break;
				case 'Q':
					Q_sum += atk_msg.damage;
					holdIn("busy", Q_proc_time);
					break;
				case 'E':
					E_sum += atk_msg.damage;
					holdIn("busy", E_proc_time);
					break;
				case 'W':
					W_sum += atk_msg.damage;
					holdIn("busy", W_proc_time);
					break;
				case 'R':
					R_sum += atk_msg.damage;
					holdIn("busy", R_proc_time);
					break;
				}		
				// combat_result 객체의 이름 설정
				name = "total damage: " + total_damage;
				// combat_result 이름 설정
				combat_msg = new combat_result (name, A_sum, Q_sum, W_sum, E_sum, R_sum);
				// 총 데미지, 사용횟수, 데미지 점유율 계산
				combat_msg.setTotalDamage();
				combat_msg.setSkillCount();
				combat_msg.setSkillShare();
			}
			else {
				// queue, attack, combat_result 객체 초기화
				q = new Queue();
				atk_msg = new attack("none",'-',0);
				combat_msg = new combat_result("none", 0, 0, 0, 0, 0);
				holdIn("passive", INFINITY);
			}
		}
	}

	// combat_result 객체를 외부로 전달
	public message out() {
		message m = new message();
		if (phaseIs("busy")){
			combat_msg.printCombatResult();
			m.add(makeContent("out", combat_msg));
		}
		return m;
	}	
	
	public String getTooltipText()	{
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}
}