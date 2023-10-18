import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/***
 * [문제]
 * 상사가 직속 부하를 칭찬하면, 그 부하가 직속 부하를 연쇄적으로 내리 칭찬
 * 칭찬에는 칭찬의 정도라는 값이 있으며, 이 값 또한 부하들에게 똑같이 적용
 * 각자 얼마의 칭찬을 받았는지 출력
 * 
 * n (직원 수, 2 <= n <= 100,000)
 * m (칭찬 횟수, 2 <= m <= 100,000)
 *
 * [변수]
 * wArr[i] = i번 직원의 칭찬 수치
 * List<Integer>[] children = 트리, children[i] = i번 직원의 직속 부하 번호
 *
 * [풀이]
 * 직속 상사, 부하 관계를 트리로 볼 수 있음
 * wArr[i]를 초기화하고 한 번의 트리 순회로 모든 직원의 칭찬 값을 구할 수 있음
 */


public class BOJ_14267_회사문화1 {
    static int[] wArr;
    static List<Integer>[] children;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        wArr = new int[N+1];
        children = new List[N+1];

        //-1을 빼줌
        st = new StringTokenizer(br.readLine());
        st.nextToken();
        children[1] = new ArrayList<>();

        for(int i=2; i<=N; i++) {
            int boss = Integer.parseInt(st.nextToken());
            children[i] = new ArrayList<>();
            children[boss].add(i);
        }

        for(int i=0; i<M; i++) {
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            wArr[num] += w;
        }

        traversal(1, 0);
        for(int i=1; i<=N; i++) {
            sb.append(wArr[i]).append(' ');
        }
        System.out.println(sb);
    }

    //root = root 노드
    //parentVal = 부모 노드의 칭찬 값
    static void traversal(int root, int parentVal) {
        wArr[root] += parentVal;
        for(int i=0; i<children[root].size(); i++) {
            traversal(children[root].get(i), wArr[root]);
        }
    }
}
