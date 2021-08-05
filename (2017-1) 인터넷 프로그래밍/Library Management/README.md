# 도서 대출 관리 프로그램 / 12161567 박기수

<h2>1.	구성</h2>

**메인, 로그인:** Main.jsp, Login_judge.jsp, Main_login.jsp

**회원 가입:** Subscription.html, Subscription.jsp, Subscription_Result.jsp

**책 검색, 대출/반납:** BookSearch_by_DB.jsp, BorrowReturn.jsp, BorrowReturn_Result.jsp

**CSS:** Style.css   

**에러 페이지:** DBError.jsp


<h2>2.	작동 원리</h2>
 <img src="https://user-images.githubusercontent.com/54628612/83882177-b274be00-a77c-11ea-9253-4ceae9de23ef.png"></img>
 
<h2>3.	상세 설명</h2>

메인 화면(Main.jsp) 에서는 로그인, 회원가입을 할 수 있으며, 로그인 후에 Main_login.jsp로 이동하여 책 검색을 할 수 있다. 책 검색은 도서명 / 저자명 두 가지로 할 수 있고, 대출, 반납 또한 가능하다. 오류 발생 시 DBError.jsp 페이지로 이동하여 에러 메시지를 확인할 수 있다. 회원가입과 책 대출 / 반납을 완료하면 메인화면으로 이동 후에 다시 로그인하여 사용한다.

<h3> Used Queries </h3>

**책** 

**connection:** "jdbc:mysql://localhost:3306/booklist", "root", "1234"

<pre><code>
create table goodsinfo (
code char(5) not null,
title varchar(100) not null,
writer varchar(50),
available numeric(1,0) not null
);
 
insert into goodsinfo values ('10001', 'The Old Man and the Sea', '', 0);
insert into goodsinfo values ('10002', 'Harry Potter And The Order Of The Phoenix', 'Joan K. Rowling', 0);
insert into goodsinfo values ('10003', 'Harry Potter And The Deathly Hallows', 'Joan K. Rowling', 0);
insert into goodsinfo values ('10004', 'The Little Prince', 'Antoine Marie Jean-Baptiste Roger de Saint-Exupéry', 0);
insert into goodsinfo values ('10004', 'The Snows Of Kilimanjaro', 'Ernest Miller Hemingway', 0);

</code></pre>
**회원**

**connection:** "jdbc:mysql://localhost:3306/userlist", "root", "1234"
<pre><code>
create table userinfo(
id varchar(20) not null,
pw varchar(20) not null,
name varchar(10) not null,
email varchar(20),
bd char(10),
gender varchar(6),
primary key(id)
);
</code></pre>


