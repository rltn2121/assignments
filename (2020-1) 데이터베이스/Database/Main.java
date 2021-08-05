import java.sql.*;
public class Main {
	protected static Connection connection = null;
	protected static PreparedStatement pstat;
	protected static Statement stat;
	protected static ResultSet rs;
	protected static String sql;
	
	// select, insert, delete, update 등의 쿼리에 사용될 변수 선언
	protected static String directorID;
	protected static String actorID;
	protected static String movieID;
	protected static String customerID;
	protected static String awardID;
	
	protected static String directorName;
	protected static String movieName;
	protected static String awardName;
	protected static String genreName;
	protected static String customerName;
	protected static String publisherName;
	
	protected static String dateOfBirth;
	protected static String gender;
	protected static String year;
	protected static String role;
	protected static String releaseYear;
	protected static String releaseMonth;
	protected static String releaseDate;
	protected static double rate;
	
	// JDBC, PostgreSQL 연결
	public static void connect_to_PostgreSQL(){
		 try {
	            Class.forName("org.postgresql.Driver");
	        } catch (ClassNotFoundException e) {
	            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
	            e.printStackTrace();
	            return;
	        }
	        System.out.println("PostgreSQL JDBC Driver Registered!");
	        /// if you have a error in this part, check jdbc driver(.jar file)	
	        try {
	            connection = DriverManager.getConnection(
	                    "jdbc:postgresql://127.0.0.1:5432/project_movie", "postgres", "cse3207");
	        } catch (SQLException e) {
	            System.out.println("Connection Failed! Check output console");
	            e.printStackTrace();
	            return;
	        }
	        /// if you have a error in this part, check DB information (db_name, user name, password)

	        if (connection != null) {
	            System.out.println(connection);
	            System.out.println("You made it, take control your database now!");
	        } else {
	            System.out.println("Failed to make connection!");
	        }   
	}	
	// actorObtain 테이블에 tuple 삽입
	public static void insertTable_actorObtain(String actorID, String awardID, String year){
		try {
			pstat = connection.prepareStatement("insert into actorObtain values (?,?,?)");
			pstat.setString(1,  actorID);
			pstat.setString(2,  awardID);
			pstat.setString(3,  year);
			System.out.println("  -> " + pstat.toString());
			pstat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// movieObtain 테이블에 tuple 삽입
	public static void insertTable_movieObtain(String movieID, String awardID, String year){
		try {
			pstat = connection.prepareStatement("insert into movieObtain values (?,?,?)");
			pstat.setString(1,  movieID);
			pstat.setString(2,  awardID);
			pstat.setString(3,  year);
			System.out.println("  -> " + pstat.toString());
			pstat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// directorObtain 테이블에 tuple 삽입
	public static void insertTable_directorObtain(String directorID, String awardID, String year){
		try {
			pstat = connection.prepareStatement("insert into directorObtain values (?,?,?)");
			pstat.setString(1,  directorID);
			pstat.setString(2,  awardID);
			pstat.setString(3,  year);
			System.out.println("  -> " + pstat.toString());
			pstat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// customerRate 테이블에 tuple 삽입
	public static void insertTable_customerRate(String customerID, String movieID, double rate){
		try {
			pstat = connection.prepareStatement("insert into customerRate values (?,?,?)");
			pstat.setString(1,  customerID);
			pstat.setString(2,  movieID);
			pstat.setDouble(3,  rate);
			System.out.println("  -> " + pstat.toString());
			pstat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// award 테이블 출력
	public static void printTable_award(){
		System.out.println("\n--------------- <Award> ---------------");
		try {
			rs = stat.executeQuery("select * from award");
			System.out.println("awardID\t\tawardName");
			while(rs.next()) {
				awardID = rs.getString(1);
				awardName = rs.getString(2);
				System.out.println(awardID + "\t\t" + awardName);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	// actorObtain 테이블 출력
	public static void printTable_actorObtain(){
		System.out.println("\n--------------- <actorObtain> ---------------");
		try {
			rs = stat.executeQuery("select * from actorObtain");
			System.out.println("actorID\t\tawardID\t\tyear");
			while(rs.next()){
				actorID = rs.getString(1);
				awardID = rs.getString(2);
				year = rs.getString(3);
				System.out.println( actorID + "\t\t" + awardID + "\t\t" + year);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	// movieObtain 테이블 출력
	public static void printTable_movieObtain(){
		System.out.println("\n--------------- <movieObtain> ---------------");
		try {
			rs = stat.executeQuery("select * from movieObtain");
			System.out.println("movieID\t\tawardID\t\tyear");
			while(rs.next()){
				movieID = rs.getString(1);
				awardID = rs.getString(2);
				year = rs.getString(3);
				System.out.println( movieID + "\t\t" + awardID + "\t\t" + year);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	// directorObtain 테이블 출력
	public static void printTable_directorObtain(){
		System.out.println("\n--------------- <directorObtain> ---------------");
		try {
			rs = stat.executeQuery("select * from directorObtain");
			System.out.println("directorID\tawardID\t\tyear");
			while(rs.next()){
				directorID = rs.getString(1);
				awardID = rs.getString(2);
				year = rs.getString(3);
				System.out.println( directorID + "\t\t" + awardID + "\t\t" + year);
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	// customerRate 테이블 출력
	public static void printTable_customerRate(){
		System.out.println("\n--------------- <customerRate> ---------------");
		try {
			rs = stat.executeQuery("select * from customerRate");
			System.out.println("customerID\tmovieID\t\trate");
			while(rs.next()){
				customerID = rs.getString(1);
				movieID = rs.getString(2);
				rate = rs.getDouble(3);
				System.out.println( customerID + "\t\t" + movieID + "\t\t" + rate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// customer 테이블 출력
	public static void printTable_customer(){
		System.out.println("\n--------------- <customer> ---------------");
		try {
			rs = stat.executeQuery("select * from customer");
			System.out.println("customerID\tcustomerName\tdateOfBirth\tgender");
			while(rs.next()){
				customerID = rs.getString(1);
				customerName = rs.getString(2);
				dateOfBirth = rs.getString(3);
				gender = rs.getString(4);
				System.out.println(customerID + "\t\t" + customerName + "\t\t" + dateOfBirth + "\t\t" + gender);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// movie테이블의 movieID와 avgRate 출력
	public static void printTable_movieRate(){
		System.out.println("\n--------------- <movieRate> ---------------");
		try {
			// movie 테이블에서 moiveID, avgRate 선택
			rs = stat.executeQuery("select movieID, avgRate from movie order by movieID");
			System.out.println("movieID\t\trate");
			while(rs.next()){
				movieID = rs.getString(1);
				rate = rs.getDouble(2);
				System.out.println(movieID + "\t\t" + rate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// movieGenre 테이블 출력
	public static void printTable_movieGenre(){
		System.out.println("\n--------------- <movieGenre> ---------------");
		try {
			rs = stat.executeQuery("select * from movieGenre");
			System.out.println("movieID\t\tgenreName");
			while(rs.next()){
				movieID = rs.getString(1);
				genreName = rs.getString(2);
				System.out.println(movieID + "\t\t" + genreName);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// casting 테이블 출력
	public static void printTable_casting(){
		System.out.println("\n--------------- <casting> ---------------");
		try {
			rs = stat.executeQuery("select * from casting");
			System.out.println("movieID\t\tactorID\t\trole");
			while(rs.next()) {
				movieID = rs.getString(1);
				actorID = rs.getString(2);
				role = rs.getString(3);
				System.out.println(movieID + "\t\t" + actorID + "\t\t" + role);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// make 테이블 출력
	public static void printTable_make(){
		System.out.println("\n--------------- <make> ---------------");
		try {
			rs = stat.executeQuery("select * from make");
			System.out.println("movieID\t\tdirectorID");
			while(rs.next()){
				movieID = rs.getString(1);
				directorID = rs.getString(2);
				System.out.println(movieID + "\t\t" + directorID);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// movie 테이블 출력
	public static void printTable_movie(){
		System.out.println("\n--------------- <movie> ---------------");
		try {
			rs = stat.executeQuery("select * from movie");
			System.out.println("movieID\t\tmovieName\treleaseYear\treleaseMonth\treleaseDate\tpublisherName\tavgRate");
			while(rs.next()){
				movieID = rs.getString(1);
				movieName = rs.getString(2);
				releaseYear = rs.getString(3);
				releaseMonth = rs.getString(4);
				releaseDate = rs.getString(5);
				publisherName = rs.getString(6);
				rate = rs.getDouble(7);
				System.out.println(movieID + "\t\t" + movieName+ "\t\t" + releaseYear+ "\t\t" + releaseMonth+ "\t\t" + releaseDate+ "\t\t" + publisherName+ "\t\t" + rate);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// movie 테이블의 avgRate 갱신
	public static void updateTable_movieRate(){
		try {
			// 집계 함수 사용, customerRate 테이블에서 movieID와 해당 영화의 평점(avg(rate))을 선택하는 쿼리
			sql = "select movieID, avg(rate) as avg_rate"
					+"\nfrom customerRate"
					+"\ngroup by movieID";
			rs = stat.executeQuery(sql);
			
			while(rs.next()){
				movieID = rs.getString(1);
				rate = rs.getDouble(2);
				// 선택한 movieID와 avg(rate)를 이용  movie 테이블의 avgRate 갱신
				pstat = connection.prepareStatement("update movie set avgRate = " + rate + " where movie.movieID = ?");
				pstat.setString(1,  movieID);
				System.out.println("  -> " + pstat.toString());
				pstat.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 1번 쿼리 실행
	public static void do_1st_query(){
		System.out.println("\n1. Create the tables and insert the proper data based on the provided data. "
						+ "\nYou should make the movie, actor, director, and customer tables first and insert data into other related tables.");
			try{
				// director 테이블 생성 쿼리
				String create_table_director = "create table director("
						+ "directorID 	varchar(5) not null,"
						+ "directorName varchar(50) not null,"
						+ "dateOfBirth varchar(10) not null,"
						+ "dateOfDeath varchar(10),"
						+ "primary key (directorID));";
				// actor 테이블 생성 쿼리
				String create_table_actor = "create table actor("
						+ "actorID 	varchar(5) not null,"
						+ "actorName varchar(50) not null,"
						+ "dateOfBirth varchar(10) not null,"
						+ "dateOfDeath varchar(10),"
						+ "gender varchar(6) not null,"
						+ "primary key (actorID));";
				// movie 테이블 생성 쿼리
				String create_table_movie = "create table movie("
						+ "movieID 	varchar(5) not null,"
						+ "movieName varchar(100) not null,"
						+ "releaseYear char(4) not null,"
						+ "releaseMonth varchar(2),"
						+ "releaseDate varchar(2),"
						+ "publisherName varchar(50),"
						+ "avgRate numeric(2,1),"
						+ "primary key (movieID));";
				// award 테이블 생성 쿼리
				String create_table_award= "create table award("
						+ "awardID 	varchar(5) not null,"
						+ "awardName varchar(50) not null,"
						+ "primary key (awardID));";
				// genre 테이블 생성 쿼리
				String create_table_genre= "create table genre("
						+ "genreName varchar(50) not null,"
						+ "primary key (genreName));";
				// movieGenre 테이블 생성 쿼리
				String create_table_movieGenre= "create table movieGenre("
						+ "movieID 	varchar(5) not null,"
						+ "genreName varchar(50) not null,"
						+ "primary key (movieID, genreName),"
						+ "foreign key (movieID) references movie,"
						+ "foreign key (genreName) references genre);";
				// movieObtain 테이블 생성 쿼리
				String create_table_movieObtain= " create table movieObtain("
						+ " movieID 	varchar(5) not null,	"
						+ "awardID 	varchar(5) not null,"
						+ "year char(4),"
						+ "primary key(movieID, awardID),"
						+ "foreign key (movieID) references movie,"
						+ "foreign key (awardID) references award);";
				// actorObtain 테이블 생성 쿼리
				String create_table_actorObtain= "create table actorObtain("
						+ "actorID 	varchar(5) not null,	"
						+ "awardID 	varchar(5) not null,"
						+ "year char(4),"
						+ "primary key(actorID, awardID),"
						+ "foreign key (actorID) references actor,"
						+ "foreign key (awardID) references award);";
				// directorObtain 테이블 생성 쿼리
				String create_table_directorObtain= " create table directorObtain("
						+ "directorID 	varchar(5) not null,	"
						+ "awardID 	varchar(5) not null,"
						+ "year char(4),"
						+ "primary key(directorID, awardID),"
						+ "foreign key (directorID) references director,"
						+ "foreign key (awardID) references award);";
				// casting 테이블 생성 쿼리
				String create_table_casting= " create table casting("
						+ "movieID 	varchar(5) not null, "
						+ "actorID 	varchar(5) not null,"
						+ "role	varchar(50) not null,"
						+ "primary key(movieID, actorID),"
						+ "foreign key (movieID) references movie,"
						+ "foreign key (actorID) references actor);";
				// make 테이블 생성 쿼리
				String create_table_make= " create table make("
						+ "movieID	varchar(5) not null, "
						+ "directorID	varchar(5) not null, "
						+ "primary key (movieID, directorID),"
						+ "foreign key (movieID) references movie,"
						+ "foreign key (directorID) references director);";
				// customer 테이블 생성 쿼리
				String create_table_customer= " create table customer("
						+ "customerID		varchar(5) not null,"
						+ "customerName	varchar(5) not null,"
						+ "dateOfBirth	varchar(10),"
						+ "gender			varchar(6),"
						+ "primary key(customerID));";
				// customerRate 테이블 생성 쿼리
				String create_table_customerRate= " create table customerRate("
						+ "customerID		varchar(5) not null,"
						+ "movieID	 	varchar(5) not null, "
						+ "rate 			numeric(2,1),"
						+ "primary key (customerID, movieID),"
						+ "foreign key (movieID) references movie,"
						+ "foreign key (customerID) references customer);";
				// 테이블 생성 쿼리 실행
				stat.executeUpdate(create_table_director);
				stat.executeUpdate(create_table_actor);
				stat.executeUpdate(create_table_movie);
				stat.executeUpdate(create_table_award);
				stat.executeUpdate(create_table_genre);
				stat.executeUpdate(create_table_movieGenre);
				stat.executeUpdate(create_table_movieObtain);
				stat.executeUpdate(create_table_actorObtain);
				stat.executeUpdate(create_table_directorObtain);
				stat.executeUpdate(create_table_casting);
				stat.executeUpdate(create_table_make);
				stat.executeUpdate(create_table_customer);
				stat.executeUpdate(create_table_customerRate);
				System.out.println("  -> All tables have been created.");
				
				// director 테이블에 삽입 할 tuple 저장
				String[][] director_info = new String[][]{
					{"10001","Tim Burton", "1958.8.25", null},
					{"10002", "David Fincher", "1962.8.28", null},
					{"10003", "Christopher Nolan", "1970.7.30", null}
				};
				// actor 테이블에 삽입 할  tuple 저장
				String[][] actor_info = new String[][]{
					{"20001", "Johnny Depp", "1963.6.9", null, "Male"},
					{ "20002", "Winona Ryder", "1971.10.29", null, "Female" },
					{ "20003", "Anne Hathaway", "1982.11.12", null, "Female" },
					{ "20004", "Christian Bale", "1974.1.30", null, "Male" },
					{ "20005", "Heath Ledger", "1979.4.4", "2008.1.22", "Male" },
					{ "20006", "Jesse Eisenberg", "1983.10.5", null, "Male" },
					{ "20007", "Andrew Garfield", "1983.8.20", null, "Male" },
					{ "20008", "Fionn Whitehead", "1997.7.1", null, "Male" },
					{ "20009", "Tom Hardy", "1977.9.15", null, "Male" }
				};
				// movie 테이블에 삽입 할  tuple 저장
				String[][] movie_info = new String[][]{
					{"30001", "Edward Scissorhands", "1991", "06", "29", "20th Century Fox Presents", null},
					{"30002", "Alice In Wonderland", "2010", "03", "04", "Korea Sony Pictures", null},
					{"30003", "The Social Network", "2010", "11", "18", "Korea Sony Pictures", null},
					{"30004", "The Dark Knight", "2008", "08", "06", "Warner Brothers Korea", null},
					{"30005", "Dunkirk", "2017", "07", "13", "Warner Brothers Korea", null}
				};
				// customer 테이블에 삽입 할  tuple 저장
				String[][] customer_info = new String[][]{
					{"40001", "Bob", "1997.11.14", "Male"},
					{"40002", "John", "1978.01.23", "Male"},
					{"40003", "Jack", "1980.05.04", "Male"},
					{"40004", "Jill", "1981.04.17", "Female"},
					{"40005", "Bell", "1990.05.14", "Female"}	
				};
				// make 테이블에 삽입 할  tuple 저장
				String[][] make_info = new String[][]{
					{"30001", "10001"},
					{"30002", "10001"},
					{"30003", "10002"},
					{"30004", "10003"},
					{"30005", "10003"}
				};
				// casting 테이블에 삽입 할  tuple 저장
				String[][] casting_info = new String[][]{
					{"30001", "20001", "Main actor"},
					{"30001", "20002", "Main actor"},
					{"30002", "20001", "Main actor"},
					{"30002", "20003", "Main actor"},
					{"30003", "20006", "Main actor"},
					{"30003", "20008", "Supporting Actor"},
					{"30004", "20004", "Main actor"},
					{"30004", "20005", "Main actor"},
					{"30005", "20008", "Main actor"},
					{"30005", "20009", "Main actor"}
				};
				// genre 테이블에 삽입 할  tuple 저장
				String[][] genre_info = new String[][]{
					{"Fantasy"},
					{"Romance"},
					{"Adventure"},
					{"Family"},
					{"Drama"},
					{"Action"},
					{"Mystery"},
					{"Thriller"},
					{"War"}
				};
				// movieGenre 테이블에 삽입 할  tuple 저장
				String[][] movieGenre_info = new String[][]{
					{"30001", "Fantasy"},
					{"30001", "Romance"},
					{"30002", "Fantasy"},
					{"30002", "Adventure"},
					{"30002", "Family"},
					{"30003", "Drama"},
					{"30004", "Action"},
					{"30004", "Drama"},
					{"30004", "Mystery"},
					{"30004", "Thriller"},
					{"30005", "Action"},
					{"30005", "Drama"},
					{"30005", "Thriller"},
					{"30005", "War"}
				};
				// director_info에 저장된 tuple들을 director 테이블에 삽입
				pstat = connection.prepareStatement("insert into director values (?,?,?,?)");
				for(int i = 0; i< director_info.length; i++){
					for(int j = 0; j<director_info[i].length; j++) 
						pstat.setString(j+1,  director_info[i][j]);
					pstat.executeUpdate();
				}
				// actor_info에 저장된 tuple들을 actor 테이블에 삽입
				pstat = connection.prepareStatement("insert into actor values (?,?,?,?,?)");
				for (int i = 0; i < actor_info.length; i++) {
					for (int j = 0; j < actor_info[i].length; j++)
						pstat.setString(j + 1, actor_info[i][j]);
					pstat.executeUpdate();
				}
				// movie_info에 저장된 tuple들을 movie 테이블에 삽입
				pstat = connection.prepareStatement("insert into movie values (?,?,?,?,?,?,?)");
				for (int i = 0; i < movie_info.length; i++) {
					for (int j = 0; j < movie_info[i].length-1; j++)
						pstat.setString(j + 1, movie_info[i][j]);
					pstat.setInt(7, 0);
					pstat.executeUpdate();
				}
				// customer_info에 저장된 tuple들을 customer 테이블에 삽입
				pstat = connection.prepareStatement("insert into customer values (?,?,?,?)");
				for (int i = 0; i < customer_info.length; i++) {
					for (int j = 0; j < customer_info[i].length; j++)
						pstat.setString(j + 1, customer_info[i][j]);
					pstat.executeUpdate();
				}
				// make_info에 저장된 tuple들을 make 테이블에 삽입
				pstat = connection.prepareStatement("insert into make values (?,?)");
				for (int i = 0; i < make_info.length; i++) {
					for (int j = 0; j < make_info[i].length; j++)
						pstat.setString(j + 1, make_info[i][j]);
					pstat.executeUpdate();
				}
				// casting_info에 저장된 tuple들을 casting 테이블에 삽입
				pstat = connection.prepareStatement("insert into casting values (?,?,?)");
				for (int i = 0; i < casting_info.length; i++) {	
					for (int j = 0; j < casting_info[i].length; j++)
						pstat.setString(j + 1, casting_info[i][j]);
					pstat.executeUpdate();
				}
				// genre_info에 저장된 tuple들을 genre 테이블에 삽입
				pstat = connection.prepareStatement("insert into genre values (?)");
				for (int i = 0; i < genre_info.length; i++) {
					for (int j = 0; j < genre_info[i].length; j++)
						pstat.setString(j + 1, genre_info[i][j]);
					pstat.executeUpdate();
				}
				// movieGenre_info에 저장된 tuple들을 movieGerne 테이블에 삽입
				pstat = connection.prepareStatement("insert into movieGenre values (?,?)");
				for (int i = 0; i < movieGenre_info.length; i++) {
					for (int j = 0; j < movieGenre_info[i].length; j++)
						pstat.setString(j + 1, movieGenre_info[i][j]);
					pstat.executeUpdate();
				}
				System.out.println("  -> All informations have been inserted.");
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}	
	// 2번 쿼리 실행
	public static void do_2nd_query(){
		System.out.println("\n2. Insert the proper data from the following statements.");
		try {			
			// award 테이블에 삽입 할 tuple 저장
			String[][] award_info = new String[][]{
				{"50001", "Best supporting actor"},
				{"50002", "Best main actor"},
				{"50003", "Best villain actor"},
				{"50004", "Best fantasy movie"},
				{"50005", "Best picture"},
				{"50006", "Best director"},
			};
			// 처리할 statement와 actorObtain 테이블에 삽입 할 tuple 저장 
			String[][] actorObtain_info = new String[][]{
				{"2.1. Winona Ryder won the “Best supporting actor” award in 1994", "Winona Ryder", "Best supporting actor", "1994"},
				{"2.2. Andrew Garfield won the “Best supporting actor” award in 2011", "Andrew Garfield", "Best supporting actor", "2011"},
				{"2.3. Jesse Eisenberg won the “Best main actor” award in 2011", "Jesse Eisenberg", "Best main actor", "2011"},
				{"2.4. Johnny Depp won the “Best villain actor” award in 2011", "Johnny Depp", "Best villain actor", "2011"}
			};
			// 처리할 statement와 movieObtain 테이블에 삽입 할 tuple 저장
			String[][] movieObtain_info = new String[][]{
				{"2.5. Edward Scissorhands won the “Best fantasy movie” award in 1991", "Edward Scissorhands", "Best fantasy movie", "1991"},
				{"2.6. Alice In Wonderland won the “Best fantasy movie” award in 2011", "Alice In Wonderland", "Best fantasy movie", "2011"},
				{"2.7. The Dark Knight won the “Best picture” award in 2009", "The Dark Knight", "Best picture", "2009"}
			};
			// 처리할 statement와 directorObtain 테이블에 삽입 할 tuple 저장
			String[][] directorObtain_info = new String[][]{
				{"2.8. David Fincher won the “Best director” award in 2011", "David Fincher", "Best director", "2011"}
			};	
			
			// award_info에 저장된 tuple을 award 테이블에 삽입
			pstat = connection.prepareStatement("insert into award values (?,?)");
			for(int i = 0; i< award_info.length; i++){
				for(int j = 0; j<award_info[i].length; j++) 
					pstat.setString(j+1,  award_info[i][j]);
				pstat.executeUpdate();
			}
			System.out.println("  -> Award infomations have been inserted.");
			// award 테이블 출력
			printTable_award();
			
			// actorObtain_info에 저장된 tuple을 actorObtain 테이블에 삽입
			for(int i = 0; i<actorObtain_info.length; i++){
				// 처리할 statement 출력
				System.out.println("\n" + actorObtain_info[i][0]);
				
				// actorObtain_info에 저장된 actorID를 선택하는 쿼리
				sql = "select actorID from actor where actorName = '" + actorObtain_info[i][1] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					actorID = rs.getString(1);
				// actorObtain_info에 저장된 awardID를 선택하는 쿼리
				sql = "select awardID from award where awardName = '" + actorObtain_info[i][2] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					awardID = rs.getString(1);
				// actorObtain_info에 저장된 year 선택
				year = actorObtain_info[i][3];
				// actorObtain 테이블에 tuple 삽입 및 출력
				insertTable_actorObtain(actorID, awardID, year);
				printTable_actorObtain();
			}
			// movieObtain_info에 저장된 tuple을 movieObtain 테이블에 삽입
			for(int i = 0; i<movieObtain_info.length; i++){
				// 처리할 statement 출력
				System.out.println("\n" + movieObtain_info[i][0]);
				// movieObtain_info에 저장된 movieID를 선택하는 쿼리
				sql = "select movieID from movie where movieName = '" + movieObtain_info[i][1] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					movieID = rs.getString(1);
				// movieObtain_info에 저장된 awardID를 선택하는 쿼리
				sql = "select awardID from award where awardName = '" + movieObtain_info[i][2] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					awardID = rs.getString(1);
				// movieObtain_info에 저장된 year 선택
				year = movieObtain_info[i][3];
				// movieObtain 테이블에 tuple 삽입 및 출력
				insertTable_movieObtain(movieID, awardID, year);
				printTable_movieObtain();
			}
			// directorObtain_info에 저장된 tuple을 directorObtain 테이블에 삽입
			for(int i = 0; i<directorObtain_info.length; i++){
				// 처리할 statement 출력
				System.out.println("\n" + directorObtain_info[i][0]);
				// directorObtain_info에 저장된 directorID를 선택하는 쿼리
				sql = "select directorID from director where directorName = '" + directorObtain_info[i][1] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					directorID = rs.getString(1);
				// directorObtain_info에 저장된 awardID를 선택하는 쿼리
				sql = "select awardID from award where awardName = '" + directorObtain_info[i][2] + "'";
				System.out.println("  -> " + sql);
				rs = stat.executeQuery(sql);
				while(rs.next())
					awardID = rs.getString(1);
				// directorObtain_info에 저장된 year 서택
				year = directorObtain_info[i][3];
				// directorObtain 테이블에 tuple 삽입 및 출력
				insertTable_directorObtain(directorID, awardID, year);
				printTable_directorObtain();	
			}		
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 3번 쿼리 실행
	public static void do_3rd_query(){
		System.out.println("\n3. Insert data to the proper tables based on the following statements and update avgRate if necessary.");
		try {
			System.out.println("\n3.1 Bob rates 5 to “The Dark Knight”.");
			// customerName이 'Bob'인 사람의 customerID를 선택하는 쿼리
			sql = "select customerID from customer where customerName = 'Bob'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				customerID = rs.getString(1);
			// movieName이 'The Dark Knight'인 영화의 movieID를 선택하는 쿼리
			sql = "select movieID from movie where movieName = 'The Dark Knight'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				movieID = rs.getString(1);
			rate = 5;
			// customerRate에 tuple 추가
			insertTable_customerRate(customerID, movieID, rate);
			// movie 테이블의 avgRate 갱신
			updateTable_movieRate();
			// customerRate, movieRate 테이블 출력
			printTable_customerRate();
			printTable_movieRate();
			
			System.out.println("\n3.2 Bell rates 5 to the movies whose director is “Tim Burton”.");
			// customerName이 'Bell'인 사람의 customerID를 선택하는 쿼리
			sql = "select customerID from customer where customerName = 'Bell'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				customerID = rs.getString(1);
			// directorName이 'Tim Burton'인 사람의 directorID를 선택하는 쿼리
			sql = "select directorID from director where directorName = 'Tim Burton'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				directorID = rs.getString(1);
			// 감독의 ID가 directorID인 사람이 만든 영화의 movieID를 선택하는 쿼리
			sql = "select movieID from make where directorID = '" + directorID + "'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			rate = 5;
			while(rs.next()) {
				movieID = rs.getString(1);
				// customerRate에 tuple 추가
				insertTable_customerRate(customerID, movieID, rate);
			}
			// movie 테이블의 avgRate 갱신
			updateTable_movieRate();
			// customerRate, movieRate 테이블 출력
			printTable_customerRate();
			printTable_movieRate();
			
			System.out.println("\n3.3 Jill rates 4 to the movies whose main actor is female.");
			// customerName이 'Jill'인 사람의 customerID를 선택하는 쿼리
			sql = "select customerID from customer where customerName = 'Jill'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				customerID = rs.getString(1);
			// 주연 배우의 성별이 'Female'인 영화의 movieID를 선택하는 쿼리
			sql = "select movieID from actor natural join casting where actor.gender = 'Female' and casting.role = 'Main actor'; ";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			rate = 4;
			while(rs.next()) {
				movieID = rs.getString(1);
				// customerRate에 tuple 추가
				insertTable_customerRate(customerID, movieID, rate);
			}
			// movie 테이블의 avgRate 갱신
			updateTable_movieRate();
			// customerRate, movieRate 테이블 출력
			printTable_customerRate();
			printTable_movieRate();
			
			System.out.println("\n3.4 Jack rates 4 to the fantasy movies.");
			// customerName이 'Jack'인 사람의 customerID를 선택하는 쿼리
			sql = "select customerID from customer where customerName = 'Jack'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				customerID = rs.getString(1);
			// 장르가 'Fantasy'인 영화의 movieID를 선택하는 쿼리
			sql = "select movieID from movieGenre where genreName = 'Fantasy'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			rate = 4;
			while(rs.next()) {
				movieID = rs.getString(1);
				// customerRate에 tuple 추가
				insertTable_customerRate(customerID, movieID, rate);
			}
			// movie 테이블의 avgRate 갱신
			updateTable_movieRate();
			// customerRate, movieRate 테이블 출력
			printTable_customerRate();
			printTable_movieRate();
			
			System.out.println("\n3.5 John rates 5 to the movies whose director won the “Best director” award");
			// customerName이 'John'인 사람의 customerID를 선택하는 쿼리
			sql = "select customerID from customer where customerName = 'John'";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			while(rs.next())
				customerID = rs.getString(1);
			// 'Best director' 상을 수상한 감독의 directorID를 선택하고, 그 감독이 만든 영화의 movieID를 선택하는 쿼리
			sql = "select movieID"
				+ "\n     from make"
				+ "\n     where directorID in (select directorID"
				+ "\n\t\t\t from directorObtain natural join award "
				+ "\n\t\t\t where awardName = 'Best director')";
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			rate = 5;
			while(rs.next()) {
				movieID = rs.getString(1);	
				// customerRate에 tuple 추가
				insertTable_customerRate(customerID, movieID, rate);
			}
			// movie 테이블의 avgRate 갱신
			updateTable_movieRate();
			// customerRate, movieRate 테이블 출력
			printTable_customerRate();
			printTable_movieRate();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 4번 쿼리 실행
	public static void do_4th_query(){
		System.out.println("\n4. Select the names of the movies whose actor are dead.");
		try {
			// 사망한 배우가(dateOfDeath is not null) 캐스팅된 영화의 movieID를 선택하고, 해당 movieID의 movieName을 선택하는 쿼리
			sql = "select movieName "
					+ "\n     from movie "
					+ "\n     where movieID in (select movieID "
					+ "\n\t\t\tfrom casting natural join actor "
					+ "\n\t\t\twhere actor.dateOfDeath is not null);";	
			System.out.println("  -> " + sql);
			rs = stat.executeQuery(sql);
			
			// 선택된 movieName을 출력
			System.out.println("\n--------------- <result> ---------------");
			System.out.println("movieName");
			while(rs.next()){
				movieName = rs.getString(1);
				System.out.println(movieName);	
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 5번 쿼리 실행
	public static void do_5th_query(){
		System.out.println("\n5. Select the names of the directors who cast the same actor more than once.");
		try{
			// where절 subquery에서 같은 배우를 두 번 이상 캐스팅한 (count(actorID)) 감독의 directorID를 선택하고, select절에서 해당 directorID의 directorName을 선택하는 쿼리
			sql = "select directorName"
					+ "\n     from director"
					+ "\n     where directorID in (select directorID"
					+ "\n\t\t\tfrom casting natural join make"
					+ "\n\t\t\tgroup by directorID, actorID"
					+ "\n\t\t\thaving count(actorID) > 1);";			
			System.out.println("  -> " + sql);

			rs = stat.executeQuery(sql);
			// 선택된 감독의 이름을 출력
			System.out.println("\n--------------- <result> ---------------");
			System.out.println("directorName");
			while(rs.next()){
				directorName = rs.getString(1);
				System.out.println(directorName);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 6번 쿼리 실행
	public static void do_6th_query(){
		try{
			System.out.println("\n6. Select the names of the movies and the genres, where movies have the common genre.");
			// with 절에서 장르별 등장횟수 정보를 가진 genre_count 테이블을 임시로 생성, natural join을 통해서  공통된 장르르 가진 영화(count > 1)의 장르명과 영화명을 선택하는 쿼리
			sql = "with genre_count (genreName, count) as"
					+ "(\n\tselect genreName, count(*)"
					+ "\n\tfrom movieGenre"
					+ "\n\tgroup by genreName)"
					+ "\n     select genreName, movieName"
					+ "\n     from movie natural join movieGenre natural join genre_count"
					+ "\n     where count > 1"
					+ "\n     order by genreName;";
			System.out.println("  -> " + sql);

			rs = stat.executeQuery(sql);
			// 선택된 영화명과 장르명 출력
			System.out.println("\n--------------- <movieGenre> ---------------");
			System.out.println("movieName\tgenreName");
			while(rs.next()){
				genreName = rs.getString(1);
				movieName = rs.getString(2);
				System.out.println(genreName + "\t\t" + movieName);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 7번 쿼리 실행
	public static void do_7th_query(){
		System.out.println("\n7.  Delete the movies which did not get any award (including all directors and actors) and delete data from related tables.");
		try{
			// 관련된 테이블 목록
			String[] related_table_info = new String[]{"movieGenre", "movieObtain", "casting", "make", "customerRate", "movie"};
			// where절 subquery에서 상을 받지 못한 감독, 배우, 영화의 directorID, actorID, movieID를 찾고, 이와 관련된 영화의 movieID를 선택하는 쿼리
			String not_awarded_movieID_query = "select distinct movieID"
					+ "\n     from make natural join casting"
					+ "\n     where directorID not in (select directorID from directorObtain)"
					+ "\n\tor actorID not in (select actorID from actorObtain)"
					+ "\n\tor movieID not in (Select movieID from movieObtain)"
					+ "\n     order by movieID;";
			System.out.println("  -> " + not_awarded_movieID_query);
			rs = stat.executeQuery(not_awarded_movieID_query);
			
			while(rs.next()){
				movieID = rs.getString(1);
				// 선택된 movieID와 관련된 tuple을 관련된 테이블에서 삭제
				for(String str : related_table_info){
					pstat = connection.prepareStatement("delete from " + str + " where movieID = ?");
					pstat.setString(1,  movieID);
					System.out.println("  -> " + pstat.toString());
					pstat.executeUpdate();
				}
				System.out.println();
			}
			// 관련된 테이블 정보 출력
			printTable_movieGenre();
			printTable_movieObtain();
			printTable_casting();
			printTable_make();
			printTable_customerRate();
			printTable_movie();

		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 8번 쿼리 실행
	public static void do_8th_query(){
		System.out.println("\n8. Delete all customers and delete data from related tables.");
		try{
			// customer와 관련된 테이블 목록
			String[] related_table_info = new String[]{"customerRate", "customer"};
			// iterator를 이용하여 관련된 테이블의 tuple을 모두 삭제
			for(String str : related_table_info){
				sql = "delete from " + str;
				System.out.println("  -> " + sql);
				stat.executeUpdate(sql);
			}
			// customerRate 정보가 삭제되었으므로 movie테이블의 avgRate도 0으로 갱신
			sql = "update movie set avgRate = 0";
			System.out.println("  -> " + sql);
			stat.executeUpdate(sql);
			// 관련된 테이블 정보 출력
			printTable_customerRate();
			printTable_customer();
			printTable_movieRate();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// 9번 쿼리 실행
	public static void do_9th_query(){
		try{
			System.out.println("\n9. Delete all tables and data.");
			// 제거할 테이블 목록
			String[] table_info = new String[]{"directorObtain", "movieObtain", "actorObtain", "movieGenre", "casting", "make", "customerRate", "director", "actor", "movie", "award", "customer", "genre"};
			for (String str : table_info){
				// iterator를 이용하여 순서대로 테이블 제거
				sql = "drop table " + str;
				System.out.println("  -> " + sql);
				stat.executeUpdate(sql);
			}
			System.out.println("  -> All tables have been deleted.");
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// JDBC와 PostgreSQL 연결
		connect_to_PostgreSQL();
		try{
			// statement 객체 생성
			stat = connection.createStatement();
			// 1~9번 쿼리 실행
			do_1st_query();
			do_2nd_query();
			do_3rd_query();
			do_4th_query();
			do_5th_query();
			do_6th_query();
			do_7th_query();
			do_8th_query();
			do_9th_query();
			System.out.println("testestestestestes");
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		// 연결 해제
        connection.close();
    }
}