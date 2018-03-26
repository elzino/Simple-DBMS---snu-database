# Simple-DBMS---snu-database
데이터베이스 과제 1



데이터베이스 Project 1-3 Report

공과대학 컴퓨터공학부

이진호



**1. 프로그래밍 환경**

  a) IDE 및 컴파일러

          - Eclipse Neon

  b) OS

        - MS windows 10 Pro


**2.**  **핵심 모듈과 알고리즘 및 코드에 대한 설명**

이번 프로젝트는 1-1 프로젝트, 1-2 프로젝트에서는 구현한 프로그램에 3 개의 DML 구문(insert, delete, select)을 추가로 구현할 것이다.

 먼저 DML 패키지를 만들고 안에 크게 Insert, Delete Select 클래스를 만들고 각 쿼리를 처리하도록 하였다. 또한 각 Value를 저장하기위해 Value 클래스를 만들고 int를 이용해 1은 integer, 2는 char, 3은 date, 4는 null을 가르키게 하였고 String으로 value를 저장하도록 하였다. Record 클래스는 Value들이 저장된 Arraylist를 갖고 참조되는지 여부를 알수 있는 ForeignRecordData 정보를 ArrayList로 갖고 있다.

Value는 실제로 파일에 저장될 때 type#value 형식으로 저장된다. 예를 들어 type이 char이고 &#39;test&#39;를 value로 가지면 2#test가 저장된다.



Record는 저장될 때 Value들을 &#39;(Single Quote)를 통해 구분한다. 예를 들어 다음과 같다.

[        2#test&#39;1#12312&#39;3#2016-11-12&#39;2#sfefsfesf#/@&#39;       ]

Char는 #을 가질 수 있지만 먼저 &#39;(Single Quote)를 이용해 Value 들을 구분해낸 뒤 처음나오는 #을 이용해 분리해서 값들을 정확히 인식 할 수 있었다. 이는 char값이 &#39;(Single Quote)를 가질 수 없음을 이용해 구현하였다.

Insert class 는 Insert 구문이 실행되면 tableName, ColumnNameList, ValueList를 입력 받는다. 먼저 tableName을 이용해 tableName의 DBSchema가 있는지 확인한다. 또한 Value들의 type과 Schema에서 정의된 type을 비교하고 오류가 있다면 적절한 오류메세지를 출력한다.

 또한 foreign key는 reference하는 table이 실제로 그 값들을 primary key가 있는 지 확인한다. 만약 없으면 오류를 출력하고 있다면 그 레코드에 referencingTableName, primaryKey, notNull, indexList정보를 &quot;(Double Quote)를 이용해서 구분하여 넣어줬다.

예를 들어table1이라는 table에 2#referenced&#39;1#123&#39;3#2016-11-11&#39; 이라는 record가 있었고 2#referenced가 primary key 라고 가정해보자.

이때   table2에 1#321&#39;2#refereneced&#39;3#2017-11-11&#39;이라는 1#321을 primary key로 갖고 2#referenced를 foreign key로 갖는 record가 들어오면 참조 당한 2#referenced&#39;1#123&#39;3#2016-11-11&#39;의 레코드 뒤에 다음과 같이 추가로 ForeignRecordData의 정보가 쓰여진다.

예) 2#referenced&#39;1#123&#39;3#2016-11-11&#39;&quot;table2&quot;1#321&quot;0&quot;1&quot;

이와 같이 참조당한 record들에 추가로 정보를 써 주었다.

Delete 문은 ReferentialIntegrity를 체크하고 지울 수 있는 경우 해당 Record를 Referencing하는 Record의 foreign key를 모두 null로 바꾸고 자신이 referencing하는 record들을 다 찾아가 ForeignRecordData에 해당되는 정보를 지웠다.

Where문도 따로 패키지와 클래스를 만들고 구현하였다. Parser에서 입력받는로 BooleanFactor, BooleanTerm, CompOperand등 클래스를 모두 만들었다. BooleanTest는  execute를 메소드를 갖는 추상클래스로 만들고 이를 Predicate라는 추상클래스와 BoolValExp클래스가 상속받게 하였다. 또한 Predicate는 ComparisionPredicate, NullPredicate가 상속받게 하였다.

Where가 execute되면 하위에 해당되는 클래스들이 차례로 execute가 된다. 그 후 제일 밑바닥에서 에러가 있으면 -1, false면 0, true면 1, unknown이면 2로 int 타입으로 리턴하게 하였다.

Select의 경우 from에 들어온 table마다 cursor를 열고 ArrayList로 관리한다. 이들 table마다 레코드를 한 개씩 돌면서 Where절을 체크한다. 그리고 Where절에서 참으로 나온 레코드들을 주어진 column에 맞게 출력한다.



**3. 구현한 내용에 대한 간략한 설명**

Select 문에서 출력할 때 각 column마다 가장 긴 길이를 갖는 Value를 기준으로 글자수를 세 &quot;-&quot; 개수를 출력해 더 보기 쉽도록 하였다.

테이블 이름과 컬럼 이름은 대소문자를 구분하지 않는다고 되어 있으므로 모두 lowerCase로 바꿔서 저장하고 처리하였다.



**4. 가정한 것들**

두가지 이상의 오류가 동시에 발생하지 않는다고 가정하였다.

Insert에서 들어오는 테이블의 컬럼 이름은 중복되지 않는다



**5. 에러 처리 방식에 관하여**

1)  Insert Query에서 column name list의 크기와 Table Schema의 Column Definition List 크기 다른경우

-&gt; Column name list 순서대로 Value들을 집어 넣고 Column name list에 명시되지 않은 값들에는 null 값을 넣었다.

    2) SelectTableExistenceError

- select \* from none as n; 라고 존재하지 않는 table을 참조한 경우 none(as 앞에 tablename) 을 이용해 오류를 출력하였다.

  3) SelectColumnResolveError

- select none as no from tableOne; 이라는 쿼리로 SelectColumnResolveError가 발생할 경우 none(as 앞에 쓴 columnName)을 이용해 오류를 출력하였다.



**6. 컴파일과 실행 방법**

 eclipse에서 컴파일 하였으며 윈도우 cmd에서 java –jar 명령어를 통해 실행시켰다. Exit;을 통해 프로그램을 종료한다.



**7. 느낀점**

Insert문에서 referential constraint를 어떻게 체크하는지 고민해볼 수 있었다.

Where 절에서 또한 오류가 없는지 체크하는 과정을 통해 실제 DBMS에서는 어떻게 구현되어 있을지 고민해 보았다.

프로젝트를 하며 전반적으로 DBMS안에 구현방식에 대해 더 고민해볼 수 있었다.
