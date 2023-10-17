import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 풀이 시작 : 8:23
 * 풀이 완료 : 10:30
 * 풀이 시간 :
 *
 * 문제 해석
 * M명의 참가자가 미로 탈출 게임 참가
 * 미로의 구성
 * 1. N * N 크기 격자
 * 2. 각 칸은 다음 3가지 중 하나의 상태
 *  - 빈 칸 : 0
 *  - 벽 : 1 ~ 9
 *  - 출구 : 빈 칸 중 하나
 * 벽은 회전할 때 내구도 1 까임, 0되면 빈칸됨
 * 참가자가 출구에 도착하면 탈출
 *
 * 1초간 과정
 * 1. 모든 참가자 동시에 움직임
 *  - 인접한 벽이 없는 상하좌우로 움직임
 *  - 움직인 칸은 현재 칸보다 출구와 최단 거리가 가까워야 함
 *  - 가능한 칸이 2개 이상이라면 상하로 먼저 움직임
 *  - 움직일 수 없다면 움직이지 않음
 *  - 한 칸에 2명 이상 참가자 있을 수 있음
 * 2. 미로 회전
 *  - 한 명 이상의 참가자와 출구를 포함한 가장 작은 정사각형 찾음
 *  - 가장 작은 크기를 갖는 정사각형이 2개 이상이라면 시작점 r, c 좌표가 가장 작은 정사각형
 *  - 선택된 정사각형은 시계방향으로 90도 회전, 회전된 벽은 내구도 1씩 까임
 *
 * K초동안 위의 과정 반복
 * K초 내에 모든 참가자가 탈출했거나 K초가 지났을 때 모든 참가자들의 이동거리 합과 출구 좌표 출력
 *
 * 구해야 하는 것
 * K초 내에 모든 참가자가 탈출했거나 K초가 지났을 때 모든 참가자들의 이동거리 합과 출구 좌표 출력
 *
 * 문제 입력
 * 첫째 줄 : N, M, K
 * 둘째 줄 ~ N개 줄 : 지도 초기 상태
 * 다음 줄 ~ M개 줄 : 참가자 초기 위치
 * 다음 줄 : 미로 초기 출구 위치
 *
 * 제한 요소
 * 4 <= N <= 100
 * 1 <= M <= 10
 * 1 <= K <= 100
 *
 * 생각나는 풀이
 * 배열 돌리기가 관건
 * 사실 돌리기가 아니라 90도 회전임
 * 시계방향 90도 회전하는 방법은?
 * 1 2 3        7 4 1
 * 4 5 6    ->  8 5 2
 * 7 8 9        9 6 3
 * temp[i][j] =
 * 나머지는 그냥 쉬운 구현일듯?
 * 참가자 이동은 리스트에 담아서 뿌려줘야지
 *
 * 구현해야 하는 기능
 * 1. 맵 저장할 2차원 배열
 *
 * 2. 정사각형 판별
 *  - 이게 제일 문제네?
 *  - 그냥 무식하게 길이 2부터 완탐해도 될듯?
 *  - 시작점(왼쪽위)과 끝점(오른쪽 아래)가 벽 내부이면서 참가자의 좌표가 그 사각형 안인지
 *  - 반복 범위(시작점)는 (출구 좌표 - 길이 + 1, 출구 좌표 - 길이 + 1) ~ (출구 좌표, 출구좌표)까지
 *
 * 3. 돌리기 => 위 그림대로 하면서 Math.max(이전 값 - 1, 0)
 * 4. 출구 위치 갱신
 *  - 이것도 문제네 그럼 걍 벽보다 큰 값으로 만들어버리면 됨
 *  - 그러고나서 완탐돌고 벽보다 큰값 만나면 그 값으로 좌표 갱신
 *
 */
public class Codetree_메이즈러너 {
    static int N, M, moves, sqSize;
    static int[][] map, temp; // 원본맵, 임시맵
    static int[] dx = {-1, 1, 0, 0}; // 상하좌우
    static int[] dy = {0, 0, -1, 1}; // 상하좌우
    static Node start = new Node(-1, -1); // 사각형 회전하는 곳의 첫 지점(좌측상단)
    static ArrayList<Node> players = new ArrayList<>(); // 남은 플레이어 저장하는 리스트
    static Node exit; // 탈출구 저장
    static class Node {
        int x, y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getDist(int x, int y) {
            return Math.abs(this.x - x) + Math.abs(this.y - y);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());

        map = new int[N + 1][N + 1];
        temp = new int[N + 1][N + 1];
        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            players.add(new Node(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
        }

        st = new StringTokenizer(br.readLine());
        exit = new Node(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        map[exit.x][exit.y] = 98765; // 탈출구를 매우 큰 수로 잡음
        while (K-- > 0) {
            movePlayers();
            if (M == 0) break; // 전부 탈출했다면 break
            findSquare();
            rotate();
        }
        System.out.println(moves);
        System.out.println(exit.x + " " + exit.y);
    }

    // 플레이어 움직이는 메서드
    private static void movePlayers() {
        for (Node p : players) {
            int nowDist = exit.getDist(p.x, p.y);
            for (int i = 0; i < 4; i++) {
                int nextX = p.x + dx[i];
                int nextY = p.y + dy[i];

                if (!isInRange(nextX, nextY) || exit.getDist(nextX, nextY) >= nowDist) continue;
                if (1 <= map[nextX][nextY] && map[nextX][nextY] <= 9) continue;
                moves++; // 이동 횟수 증가
                p.x = nextX; // 좌표 갱신
                p.y = nextY; // 좌표 갱신
                // 탈출구라면 탈출하고 남은 인원수 줄여줌
                if (p.x == exit.x && p.y == exit.y) {
                    M--;
                    p.x = -1;
                }
                break;
            }
        }
        // 이번 움직임으로 탈출한 플레이어들 리스트에서 제거
        fo : while (true) {
            for (Node p : players) {
                if (p.x == -1) {
                    players.remove(p);
                    continue fo;
                }
            }
            break;
        }
        // 맵에 플레이어 위치 표시
        // 한 칸에 여러 명 들어갈 수 있기 때문에 인원수 구별 가능하도록 더해서 넣어줌
        for (Node p : players) {
            map[p.x][p.y] += 1234;
        }
    }

    // 조건에 맞는 사각형 찾는 메서드
    private static void findSquare() {
        // 사각형의 크기가 작은 것부터 완탐
        for (int length = 2; length <= N; length++) {
            // i, j = 사각형의 시작점 (좌측상단)
            for (int i = 1; i + length - 1 <= N; i++) {
                for (int j = 1; j + length - 1 <= N; j++) {
                    // 출구를 포함하지 않으면 continue
                    if (i > exit.x || j > exit.y) continue;
                    if (i + length - 1 < exit.x || j + length - 1 < exit.y) continue;
                    // endX, endY = 사각형의 끝점 (우측하단)
                    int endX = i + length - 1;
                    int endY = j + length - 1;
                    for (Node p : players) {
                        // 남아있는 플레이어 중 하나라도 사각형 범위 내라면 탈출
                        if (p.x >= i && p.x <= endX && p.y >= j && p.y <= endY) {
                            start.x = i;
                            start.y = j;
                            sqSize = length;
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void rotate() {
        // 임시 배열에 회전된 값 저장
        for (int i = 0; i < sqSize; i++) {
            for (int j = 0; j < sqSize; j++) {
                temp[start.x + i][start.y + j] = Math.max(0, map[start.x + sqSize - 1 - j][start.y + i] - 1);
            }
        }
        // 임시 배열의 값을 원본 배열에 옮겨 담음
        for (int i = start.x; i < start.x + sqSize; i++) {
            for (int j = start.y; j < start.y + sqSize; j++) {
                map[i][j] = temp[i][j];
            }
        }
        players.clear();
        // 미로의 칸 전부 순회함
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                // 탈출구라면 탈출구 좌표 갱신
                if (map[i][j] > 90000) {
                    exit.x = i;
                    exit.y = j;
                }
                // 플레이어 위치라면
                else if (map[i][j] > 1000) {
                    // 현재 칸의 플레이어 수만큼 리스트에 담아줌
                    while (map[i][j] > 1000) {
                        players.add(new Node(i, j));
                        map[i][j] -= 1233;
                    }
                    map[i][j] = 0; // 빈 칸으로 만들어줌
                }
            }
        }
    }

    private static boolean isInRange(int x, int y) {
        return x >= 1 && x <= N && y >= 1 && y <= N;
    }

    private static void print() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (map[i][j] > 90000) System.out.print("E ");
                else if (map[i][j] > 1000) System.out.print("P ");
                else System.out.print(map[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("----------------------");
    }

}