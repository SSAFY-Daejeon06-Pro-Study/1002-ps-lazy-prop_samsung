package kr.ac.lecture.samsung.codetree;

import java.io.*;
import java.util.*;

/*
 * [문제 요약]
 * 출구로 이동 하는 거리
 *
 * [제약 사항]
 * N: 미로의 크기 (4≤N≤10)
 * M: 참가자 수 (1≤M≤10)
 * K: 게임 시간 (1≤K≤100)
 * 게임이 끝났을 때, 모든 참가자들의 이동 거리 합과, 출구 좌표
 *
 * [문제 설명]
 * 미로는 N*N 좌측 상단이 (1,1)
 *
 * 빈칸
 *   - 참가자가 이동 가능
 * 벽
 *   - 이동 불가
 *   - 1~9
 *   - 회전할 때마다 내구도 깍임
 *   - 내구도 0으로 변경시 빈칸
 * 출구
 *   - 참가자가 해당 칸에 도달하면 즉시 탈출
 *
 * 1초마다 모든 참가자는 한 칸씩 움직임
 *   - 움직일 수 없으면 움직이지 않음
 *   - 한 칸에 2명 이상의 참가자가 있을 수 있음
 * 두 위치의 최단거리는 멘하탄 거리
 * 모든 참가자는 동시에 움직임
 * 상,하,좌,우 벽이 없는 위치로 이동 가능
 * 움직일 수 있는 칸이 2칸 이상이라면 상, 하가 우선
 *
 * '움직이는 칸은 형캐 머물어 있는 칸 보다 출구 까지의 최단 거리가 더 가까워야 함'
 * if(dis[cn] >= dis[nn])이라면 불가능
 *
 * 모든 참가자가 이동을 끝냈으면 미로가 회전함
 *   - 회전은 한 명 이상의 참자자와 출구를 포함한 가장 작은 정사각형
 *   - 정사각형이 여러개라면 r,c 순으로 작은 것을 우선시
 *   - 선택된 정사각형은 90도 회전
 *
 * 만들어야 하는 것
 * 1. 참가자들 이동
 *   - 이동 거리 저장
 *   - 출구까지의 이동 거리가 더짧은 쪽으로만 이동하기 때문에, 좌 우측으로만 갈 수 있는 경우는 없음
 * 2. 미로 구하기
 *   - 크기가 2인 모든 정사각형을 확인
 * 3. 미로 회전
 *
 * */
public class MazeRunner {

    private static final int[] dx = {-1, 1, 0, 0};
    private static final int[] dy = {0, 0, -1, 1};

    private static int n;
    private static int[][] map;
    private static final Map<Integer, Integer> people = new HashMap<>();
    private static Point exit;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer stz = new StringTokenizer(br.readLine());

        n = Integer.parseInt(stz.nextToken());
        int m = Integer.parseInt(stz.nextToken());
        int k = Integer.parseInt(stz.nextToken());

        map = new int[n][n];

        for (int i = 0; i < n; i++) {
            stz = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(stz.nextToken());
            }
        }

        int xy = 0;
        for (int i = 0; i < m; i++) {
            stz = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(stz.nextToken()) - 1;
            int y = Integer.parseInt(stz.nextToken()) - 1;

            xy = (x * n) + y;
            people.put(xy, people.getOrDefault(xy, 0) + 1);
        }

        stz = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(stz.nextToken()) - 1;
        int y = Integer.parseInt(stz.nextToken()) - 1;
        exit = new Point(x, y);

        int len = 0;
        while (k-- > 0 && people.size() > 0) {

            // 1. 참가자들 이동
            len += move();

            // 이동 후 전부 밖으로 나갈 경우
            if(people.size() == 0){
                continue;
            }

            // 2. 정사각형 구하기
            Point[] rectangle = getSquare();

            // 3. 회전
            rotate(rectangle[0], rectangle[1]);

//            for(int i=0; i<n; i++) {
//                for(int j=0; j<n; j++) {
//                    System.out.print(map[i][j] + " ");
//                }
//                System.out.println();
//            }
//            for(int p : people.keySet()) {
//                System.out.println((p/n) + " " +(p%n));
//            }
//            System.out.println("-----------");
        }

        System.out.println(len);
        System.out.println((exit.x + 1) + " " + (exit.y + 1));

        br.close();
    }

    private static int move() {
        Map<Integer, Integer> afterMove = new HashMap<>();
        int len = 0;

        Loop1:
        for(int person : people.keySet()){
            int cx = person / n;
            int cy = person % n;

            int dis = distance(cx, cy);

            for(int d=0; d<4; d++){
                int nx = cx + dx[d];
                int ny = cy + dy[d];

                if(isOutOfRange(nx, ny) || map[nx][ny] > 0 || dis <= distance(nx, ny)) continue;

                // 거리가 더 짧아지고, 이동할 수 있는 위치
                len += (people.get(person));

                // exit 지점이 아닐 경우 해당 위치의 사람 인원 수 만큼 이동
                if(!(nx == exit.x && ny == exit.y)){
                    int xy = nx*n + ny;
                    afterMove.put(xy, afterMove.getOrDefault(xy, 0) + people.get(person));
                }
                continue Loop1;
            }

            // 이동할 수 없으면 현재 위치 저장
            afterMove.put(person, afterMove.getOrDefault(person, 0) + people.get(person));
        }

        people.clear(); // 모든 데이터 삭제. exit에 도착한 정보는 밑에서 추가되지 않음
        people.putAll(afterMove);
        return len;
    }

    private static int distance(int x, int y){
        return Math.abs(x - exit.x) + Math.abs(y - exit.y);
    }

    private static boolean isOutOfRange(int x, int y) {
        return x < 0 || y < 0 || x == n || y == n;
    }

    private static Point[] getSquare() {
        for (int k = 2; k <= 10; k++) { // 크기

            for (int i = 0; i + k <= n; i++) { // 시작 위치
                for (int j = 0; j + k <= n; j++) {
                    boolean isExit = false;
                    boolean isPerson = false;

                    for (int a = 0; a < k; a++) { // 크기만큼 정사각형 확인
                        for (int b = 0; b < k; b++) {
                            if (a+i == exit.x && b+j == exit.y) {
                                isExit = true;
                            } else if (people.containsKey(((a+i) * n) + (b+j))) {
                                isPerson = true;
                            }
                        }
                    }

                    // 정사각형을 찾았으면 반환
                    if (isExit && isPerson) {
                        return new Point[]{
                                new Point(i, j),
                                new Point(i + k - 1, j + k - 1)
                        };
                    }
                }
            }
        }
        return new Point[0];
    }

    private static void rotate(Point start, Point end) {
        int[][] ro = new int[n][n];
        Map<Integer, Integer> afterRotatePeople = new HashMap<>();
        boolean isRotateExit = false;

        for (int i = start.x; i <= end.x; i++) {
            for (int j = start.y; j <= end.y; j++) {

                int nextR = start.x + (j - start.y);
                int nextC = start.y + (end.x - i);

                int xy = i*n + j;

                if(i == exit.x && j == exit.y && !isRotateExit){ // 출구일 경우
                    exit.x = nextR;
                    exit.y = nextC;
                    isRotateExit = true;
                }else if(people.containsKey(xy)){ // 사람의 경우

                    afterRotatePeople.put(nextR*n + nextC, people.get(xy)); // 새로운 좌표로 이동
                    people.remove(xy);
                }

                ro[nextR][nextC] = (map[i][j] > 0) ? (map[i][j] - 1) : 0;
            }
        }

        for (int i = start.x; i <= end.x; i++) {
            for (int j = start.y; j <= end.y; j++) {
                map[i][j] = ro[i][j];
            }
        }

        people.putAll(afterRotatePeople);
    }

    private static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
