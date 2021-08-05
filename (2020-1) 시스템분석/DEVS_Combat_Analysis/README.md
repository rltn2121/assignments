# League of Legend의 챔피언 “이즈리얼”의 전투 분석 모델</h1>
시스템분석-002분반 12161567 박기수

<h3>1.	개요</h3>
본 모델은 게임 “League of Legend”의 챔피언 중 하나인 “이즈리얼(Izreal)”의 전투 능력을 분석하는 모델이다. 이즈리얼의 공격방법은 총 5개로, 기본 공격과 스킬 4개로 구성된다. 기본 공격(A)은 80의 데미지, 신비한 화살(Q)는 120의 데미지, 정수의 흐름(W)은 300의 데미지, 비전 이동(E)은 280의 데미지, 정조준 일격(R)은 650의 데미지를 입힌다. 정수의 흐름(W)과 정조준 일격(R)은 각각 100 sigma, 300 sigma의 재사용 대기 시간(cooling time)을 가진다. 모든 공격의 시전 시간은 20 sigma이다. 사용한 공격방법 별 누적 데미지, 사용횟수, 데미지 점유율을 확인할 수 있다.

<h3>2.	사용한 모델</h3>

-	attack.java (job.java)    

-	combat_result.java (job.java)   

-	myChamp.java (genr.java)    

-	enemy.java (procQ.java)    

-	transd.java

-	ef.java

-	efp.java

**1)	attack.java**: 
job.java를 수정한 모델. myChamp.java에서 전송하는 message에 쓰이며 내부에 사용한 기술의 종류(char skill)와 데미지(int damage)를 저장한다.

**2)	combat_result.java**: 
job.java를 수정한 모델. enemy.java에서 전송하는 message에 쓰이며 내부에 공격방법 별 누적 데미지를 저장하고 공격방법 별 사용 횟수(setSkillCount()), 데미지 점유율(setSkillShare())과 총 데미지(setTotalDamage())를 계산한다.

**3)	myChamp.java**: 
genr.java를 수정한 모델. out()에서 생성되는 message는 1)에서 제작한 attack을 이용한다. 20 sigma마다 기본 공격(A), 신비한 화살(Q), 비전 이동(E) 중 하나를 랜덤하게 사용한다. 정수의 흐름(W)과 정조준 일격(R)은 각각 100 sigma, 300 sigma의 재사용 대기 시간을 가지며, 재사용 대기 시간이 0이 되면 해당 스킬을 사용한다. 모든 공격의 시전 시간은 20 sigma이다. 공격을 사용하면 out()에서 attack 객체를 생성하여 enemy.java에게 전달하고, 사용한 공격의 정보(name)와 정수의 흐름(W), 정조준 일격(R)의 재사용 대기 시간(W_cooling_time, R_cooling_time)을 콘솔에 출력한다. 

**4)	enemy.java**: 
procQ.java를 수정한 모델. myChamp.java에서 전송한 공격의 공격방법 별 누적 데미지와 총 데미지를 계산한다. 기본 공격(A)은 15 time, 신비한 화살(Q)은 20 time, 정수의 흐름(W)은 25 time, 비전 이동(E)은 20 time, 정조준 일격(R)은 30 time의 계산 시간이 소요된다. deltext()에서 “passive”상태이거나 deltint()에서 !q.isEmpty()일 때 combat_result 객체를 생성하여 공격방법 별 누적 데미지, 사용 횟수, 데미지 점유율을 계산하고, out()에서 combat_result 객체를 외부로 전달하고 공격방법 별 누적 데미지, 사용 횟수, 데미지 점유율을 콘솔에 출력한다.

**5)	transd.java**: 
total turnaround time, average turnaround time, throughput을 계산한다.

**6)	ef.java**:
myChamp.java와 transd.java를 연결한 digraph 모델.

**7)	efp.java**: 
ef.java와 enemy.java를 연결한 digraph 모델.

<h3> 실행 화면 </h3>
<img src="https://user-images.githubusercontent.com/54628612/84042648-4d210700-a9e0-11ea-8a00-746366df16ff.JPG" width="90%"></img>
<img src="https://user-images.githubusercontent.com/54628612/84042651-4e523400-a9e0-11ea-8074-a56b690131be.JPG" width="90%"></img>
