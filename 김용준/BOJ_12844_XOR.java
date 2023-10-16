import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * 풀이 시작 : 10:21
 * 풀이 완료 :
 * 풀이 시간 :
 *
 * 문제 해석
 * 크기 N인 수열이 주어졌을 때 쿼리 M번 수행
 * - 1 i j k : Ai ~ Ak에 k를 xor함
 * - 2 i j : Ai ~ Aj를 xor한 후 출력
 *
 * 구해야 하는 것
 * 모든 2번 쿼리의 결과
 *
 * 문제 입력
 * 첫째 줄 : N
 * 둘째 줄 : A1 ~ AN
 * 셋째 줄 : M
 * 넷째 줄 ~ M개 줄 : 쿼리
 *
 * 제한 요소
 * 1 <= N, M <= 500000
 * 0 <= Ai <= 100000
 * 0 <= k <= 100000
 *
 * 생각나는 풀이
 * segment tree with lazy propagation
 *
 * 구현해야 하는 기능
 * 1. init
 * 2. update
 * 3. xor
 * 4. lazy
 */
public class BOJ_12844_XOR {
    static int N;
    static int[] segTree, lazy, arr;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());
        int exp = (int) Math.ceil(Math.log(N) / Math.log(2)) + 1;
        int treeSize = 1 << exp;
        segTree = new int[treeSize];
        lazy = new int[treeSize];
        arr = new int[N + 1];
        st = new StringTokenizer(br.readLine());
        for (int i = 1; i <= N; i++) arr[i] = Integer.parseInt(st.nextToken());
        init(1, N, 1);

        int Q = Integer.parseInt(br.readLine());

        StringBuilder sb = new StringBuilder();
        while (Q-- > 0) {
            st = new StringTokenizer(br.readLine());
            int query = Integer.parseInt(st.nextToken());
            int a = Integer.parseInt(st.nextToken()) + 1;
            int b = Integer.parseInt(st.nextToken()) + 1;

            // 악질문제... 항상 a < b가 아님
            int temp = Math.min(a, b);
            b = Math.max(a, b);
            a = temp;
            if (query == 1) {
                int k = Integer.parseInt(st.nextToken());
                update(1, N, 1, a, b, k);
            } else {
                sb.append(findXor(1, N, 1, a, b)).append('\n');
            }
        }
        System.out.println(sb);
    }

    private static void init(int start, int end, int node) {
        if (start == end) {
            segTree[node] = arr[start];
            return;
        }
        int mid = (start + end) >> 1;
        init(start, mid, node << 1);
        init(mid + 1, end, (node << 1) + 1);
        segTree[node] = segTree[node << 1] ^ segTree[(node << 1) + 1];
    }

    private static void update(int start, int end, int node, int left, int right, int value) {
        updateLazy(start, end, node);
        if (right < start || end < left) return;
        if (left <= start && end <= right) {
            lazy[node] = value;
            updateLazy(start, end, node);
            return;
        }
        int mid = (start + end) >> 1;
        update(start, mid, node << 1, left, right, value);
        update(mid + 1, end, (node << 1) + 1, left, right, value);
        segTree[node] = segTree[node << 1] ^ segTree[(node << 1) + 1];
    }

    private static int findXor(int start, int end, int node, int left, int right) {
        updateLazy(start, end, node);
        if (right < start || end < left) return 0;
        if (left <= start && end <= right) return segTree[node];

        int mid = (start + end) >> 1;
        int lValue = findXor(start, mid, node << 1, left, right);
        int rValue = findXor(mid + 1, end, (node << 1) + 1, left, right);
        return lValue ^ rValue;
    }

    private static void updateLazy(int start, int end, int node) {
        if (lazy[node] != 0) {
            // XOR의 성질
            // 같은 수와 짝수번 xor연산하면 자기 자신이 됨
            // 0과 xor연산하면 자기 자신이 됨
            if (((end - start + 1) & 1) == 1) {
                segTree[node] ^= lazy[node];
            }
            if (start != end) {
                lazy[node << 1] ^= lazy[node];
                lazy[(node << 1) + 1] ^= lazy[node];
            }
            lazy[node] = 0;
        }
    }

}