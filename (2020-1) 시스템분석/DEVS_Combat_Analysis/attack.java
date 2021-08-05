package midterm;
import GenCol.*;

// myChamp.java에서 전송하는 message에 쓰이며 내부에 사용한 기술의 종류(char skill)와 데미지(int damage)를 저장
public class attack extends entity {
	char skill;  // 사용한 공격방법
	int damage;  // 사용한 공격의 데미지
	
	// constructor
	public attack(String name, char _skill, int _damage)	{  
		super(name);  
		skill = _skill;
		damage = _damage;
	}
}
