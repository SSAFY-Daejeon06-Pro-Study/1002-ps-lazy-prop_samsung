import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 풀이 시작 : 8:45
 * 풀이 완료 :
 * 풀이 시간 :
 *
 * 문제 해석
 * 트리형태
 * 칭찬받는 사람부터 끝까지 내리칭찬
 *
 * 구해야 하는 것
 * 관계가 주어지고 칭찬에 대한 정보가 주어질 때 각자 받은 칭찬
 *
 * 문제 입력
 * 첫째 줄 : 회사 직원 수 N, 최초 칭찬 횟수 M
 * 둘째 줄 : 직원 N명의 직속 상사 번호
 * 셋째 줄 ~ M개 줄 : 칭찬 받은 직원 번호 i, 칭찬 수치 w
 *
 * 제한 요소
 * 1번이 항상 사장 => 루트
 * 2 <= N, M <= 100000
 * 1 <= w <= 1000
 *
 * 생각나는 풀이
 * 초기 칭찬을 전부 저장 후 루트부터 아래로 dfs 탐색하며 값을 채움
 *
 * 구현해야 하는 기능
 * 1. 각 사람이 받은 칭찬 저장할 배열 => dp용
 * 2. 트리
 * 3. dfs
 *
 */
public class BOJ_14267_회사문화1 {
    static int N;
    static int[] compliment;
    static ArrayList<Integer>[] tree;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        compliment = new int[N + 1];
        tree = new ArrayList[N + 1];
        for (int i = 1; i <= N; i++) tree[i] = new ArrayList<>();
        st = new StringTokenizer(br.readLine());
        st.nextToken();
        for (int i = 2; i <= N; i++) {
            int parent = Integer.parseInt(st.nextToken());
            tree[parent].add(i);
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            compliment[num] += w;
        }

        dfs(1, 0);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= N; i++) {
            sb.append(compliment[i]).append(' ');
        }
        System.out.println(sb);

    }

    private static void dfs(int idx, int sum) {
        compliment[idx] += sum;
        for (int child : tree[idx]) {
            dfs(child, compliment[idx]);
        }
    }

}