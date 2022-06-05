<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacuorse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-path">
</p>

<br>

# 지하철 노선도 미션

스프링 과정 실습을 위한 지하철 노선도 애플리케이션

<br>

## 1단계 기능 요구 사항

### station controller 추가

- 이미 등록된 이름 요청시 에러 응답

### line controller 추가

- 노선 등록
- 노선 조회
- 노선 목록
- 노선 수정
- 노선 삭제

- [API 문서](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line) 참고

### line controller test 추가

## 2단계 기능 요구 사항

- H2에 지하철 데이터 저장하기
	- 기존 List 자료구조 H2 DB로 변경
	- sql문으로 기능 코드 대체
	- jdbcTemplate로 작성된 sql문 실행
	- H2 DB를 이용해 DB 저장, 확인, console 활용
- 스프링 빈을 활용하기
	- 객체와 싱글톤이나 static으로 구현 객체들을 스프링 빈으로 관리

## 3단계 기능 요구 사항

### 지하철 노선 추가 API 수정

- [x] 노선 추가 시 3가지 정보를 추가로 입력 받음
	- upStationId: 상행 종점
	- downStationId: 하행 종점
	- distance: 두 종점간의 거리
- [x] 두 종점간의 연결 정보를 이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
	- 변경된 API 스펙은 [API 문서v2](https://github.com/jinyoungchoi95/atdd-subway-map.git) 참고

### 구간 관리 API 구현

- [x] 노선에 구간정보 추가
- [x] 구간/구간들 도메인 생성
- [x] 구간 등록
	- [x] [예외]상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
	- [x] [예외]상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
	- [x] [예외] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
	- [x] 하나의 노선에는 갈래길이 허용되지 않기 때문에 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경
- [x] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
- [x] 구간 제거
	- [x] [예외] 구간내 존재하지 않는 지하철역은 구간 제거할 수 없음
	- [x] [예외] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없음
- 구간 관리 API
	- 스펙은 [API 문서v2](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#Line)
	  참고

- 테스트용 front
	- https://d2owgqwkhzq0my.cloudfront.net/

# 경로 추가 미션

- 테스트용 front:
	- https://d2owgqwkhzq0my.cloudfront.net/

## 1단계 기능 요구 사항

- api의 구현은 인수테스트부터 하향식으로 구현한다.
- [x] 출발역(source) 도착역(target) 나이(age)로 조회 요청(GET)시 아래 항목을 응답하는 api 구현
	- `GET /paths?source=1&target=5&age=15 HTTP/1.1`
	- [x] 경로(stations)
	- [x] 최단거리(distance) 계산
	- [x] 거리에 대한 요금(fare) 계산
		- [x] 10㎞ 이내: 기본운임 1,250원
		- [x] 10km~50km: 5km 까지 마다 100원 추가
		- [x] 50km 초과: 8km 까지 마다 100원 추가

- [x] 입력 값들에 대한 예외 구현
	- [ERROR] 조회시 출발역과 도착역이 같을 경우 예외를 발생시킨다.
	- [ERROR] 갈 수 없는 경로일 경우 예외를 발생시킨다.
- [x] Line 테이블 extraFare 필드 추가


- 경로 조회 API 구현하기
	- 경로 조회 API
	  스펙은 [API 문서v2](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c4c291f19953498e8eda8a38253eed51#Path)
	  참고
- 요금 계산 방법
	- 기본운임(10㎞ 이내): 기본운임 1,250원
	  이용 거리 초과 시 추가운임 부과
  ```
  10km~50km: 5km 까지 마다 100원 추가
  50km 초과: 8km 까지 마다 100원 추가
  9km = 1250원
  12km = 10km + 2km = 1350원
  16km = 10km + 6km = 1450원
  58km = 10km + 40km + 8km = 2150원
  ```

	- 5km 마다 100원 추가
	  ```java
	  private int calculateOverFare(int distance) {
	      return (int) ((Math.ceil((distance - 1) / 5) + 1) * 100);
	  }
	  ```
- 지하철 운임은 거리비례제로 책정됩니다. (실제 경로가 아닌 최단거리 기준)

<details>
<summmary>라이브러리 관련 힌트 </summmary>

- 라이브러리 활용 힌트
	- 최단 경로 라이브러리
	- jgrapht 라이브러리를 활용하면 간편하게 최단거리를 조회할 수 있음
	- 정점(vertex)과 간선(edge), 그리고 가중치 개념을 이용
	- 정점: 지하철역(Station)
	- 간선: 지하철역 연결정보(Section)
	- 가중치: 거리
	- 최단 거리 기준 조회 시 가중치를 거리로 설정
    ```java
    @Test
    public void getDijkstraShortestPath() {
    	WeightedMultigraph<String, DefaultWeightedEdge> graph
    	#ERROR!
    	graph.addVertex("v1");
    	graph.addVertex("v2");
    	graph.addVertex("v3");
    	graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
    	graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
    	graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
    	DijkstraShortestPath dijkstraShortestPath
    	#ERROR!
    	List<String> shortestPath
    	#ERROR!
    	assertThat(shortestPath.size()).isEqualTo(3);
    }
    ```

</details>

## 2단계 요구사항

### 노선별 추가 요금 및 연령별 요금 할인

- 환승 노선을 고려한, 노선 중 가장 비싼 금액의 추가 요금을 거리별 요금에 추가
    - [x] 추가 요금이 있는 노선을 이용할 경우 (기존 거리별)측정된 요금에 추가한다.
    - [x] 경로 중 추가요금이 있는 노선을 환승하여 이용할 경우 가장 높은 금액의 추가요금만 적용한다.
- 기본요금 => 거리별 요금 => 환승 고려 노선 추가요금 => **나이별 요금 공제 및 할인을 적용** 
    - [x] 청소년: 운임에서 350원 공제한 금액의 20% 할인
    - [x] 어린이: 운임에서 350원 공제한 금액의 50% 할인


### 노선별 추가요금 구현시 힌트
    ```
    - 청소년: 13세 이상~19세 미만
    - 어린이: 6세 이상~13세 미만
    ```

<details>
<summmary> db 필드 힌트 </summmary>

- LINE 테이블에 추가 요금 컬럼 추가 필요
  ```sql
  create table if not exists LINE
  (
  id bigint auto_increment not null,
  name varchar(255) not null unique,
  color varchar(20) not null,
  extraFare int,  <---- 컬럼 추가
  primary key(id)
  );
  ```

</details>
