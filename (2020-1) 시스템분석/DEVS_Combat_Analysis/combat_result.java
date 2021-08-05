package midterm;
import GenCol.*;

// enemy.java에서 전송하는 message에 쓰이며 내부에 공격방법 별 누적 데미지를 저장
// 공격방법 별 사용 횟수, 데미지 점유율, 전체 데미지 계산
public class combat_result extends entity{
	// 공격방법의 정보를 저장하는 배열. (skill)_info[0]: 누적 데미지, (skill)_info[1]: 사용 횟수
	private int[] A_info;
	private int[] Q_info;
	private int[] W_info;
	private int[] E_info;
	private int[] R_info;
	
	// 공격방법의 데미지 점유율
	private double A_share;
	private double Q_share;
	private double W_share;
	private double E_share;
	private double R_share;
	
	// 총 데미지
	private double total_damage;
	
	// constructor
	public combat_result(String name, int _A_sum, int _Q_sum, int _W_sum, int _E_sum, int _R_sum)	{  
		super(name);  
		A_info = new int[2];
		Q_info = new int[2];
		W_info = new int[2];
		E_info = new int[2];
		R_info = new int[2];
		
		// 공격방법별 누적 데미지 저장
		A_info[0] = _A_sum;
		Q_info[0] = _Q_sum;
		W_info[0] = _W_sum;
		E_info[0] = _E_sum;
		R_info[0] = _R_sum;
	}
	
	// 총 데미지 계산
	protected void setTotalDamage(){
		total_damage =A_info[0] + Q_info[0] + W_info[0] + E_info[0] + R_info[0];
	}
	
	// 공격방법 별 사용 횟수 계산
	protected void setSkillCount(){
		// 사용 횟수 = (누적 데미지) / (1회 공격 시 데미지)
		A_info[1] = A_info[0] / 80;
		Q_info[1] = Q_info[0] / 120;
		W_info[1] = W_info[0] / 300;
		E_info[1] = E_info[0] / 280;
		R_info[1] = R_info[0] / 650;
	}
	
	// 공격방법 별 데미지 점유율 계산
	protected void setSkillShare(){
		if(total_damage != 0){
			// 데미지 점유율 = (누적 데미지) / (총 데미지) * 100
			A_share = A_info[0] / total_damage * 100;
			Q_share = Q_info[0] / total_damage * 100;
			W_share = W_info[0] / total_damage * 100;
			E_share = E_info[0] / total_damage * 100;
			R_share = R_info[0] / total_damage * 100;
		}
	}
	
	// 전투 분석 결과 출력
	public void printCombatResult(){
		System.out.println("● 전투 분석 결과");
		System.out.println("\t\t데미지\t사용횟수\t데미지 점유율");
		System.out.println("기본 공격   (A)\t" + A_info[0] + "\t" + A_info[1] + "\t" + String.format("%.2f", A_share) + "%");
		System.out.println("신비한 화살 (Q)\t" + Q_info[0] + "\t" + Q_info[1] + "\t" + String.format("%.2f", Q_share) + "%");
		System.out.println("정수의 흐름 (W)\t" + W_info[0] + "\t" + W_info[1] + "\t" + String.format("%.2f", W_share) + "%");
		System.out.println("비전 이동   (E)\t" + E_info[0] + "\t" + E_info[1] + "\t" + String.format("%.2f", E_share) + "%");
		System.out.println("정조준 일격 (R)\t" + R_info[0] + "\t" + R_info[1] + "\t" + String.format("%.2f", R_share) + "%");
	}
}
