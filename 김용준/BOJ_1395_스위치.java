import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
/**
 * 풀이 시작 : 
 * 풀이 완료 :
 * 풀이 시간 :
 *
 * 문제 해석
 * 총 N개의 스위치 1 ~ N번
 * 1. A ~ B번 스위치 상태 반전
 * 2. C ~ D번 스위치 중 켜져 있는 스위치 개수
 * 
 * 구해야 하는 것
 * 쿼리 결과
 * 
 * 문제 입력
 * 첫째 줄 : N, M
 * 둘째 줄 ~ M개 줄 : 쿼리
 * 	- 0 a b : a ~ b번 스위치 반전
 * 	- 1 a b : a ~ B번 스위치 중 켜진 스위치 개수
 * 
 * 제한 요소
 * 2 <= N <= 100000
 * 1 <= M <= 100000
 * 
 * 생각나는 풀이
 * segment tree with lazy propagation
 * 어떻게 반전 처리 할 것인가
 * 반전 = (구간 합 - 현재 값)
 * 반전 2번 수행하면 같아짐. 즉 횟수 % 2만큼 바뀐다는 거니까
 * lazy에 횟수를 저장하고 나중에 횟수 % 2만큼 수행하면 될듯
 * 
 * 구현해야 하는 기능
 * 1. 원본 배열
 * 2. 세그먼트 트리
 * 3. lazy 배열
 * 
 */
public class BOJ_1395_스위치 {
	static int N;
	static int[] bulbs;
	static int[] segTree;
	static int[] lazy;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());
		bulbs = new int[N + 1];
		int exp = (int) Math.ceil(Math.log(N) / Math.log(2)) + 1;
		int treeSize = 1 << exp;
		segTree = new int[treeSize];
		lazy = new int[treeSize];

		StringBuilder sb = new StringBuilder();
		while (M-- > 0) {
			st = new StringTokenizer(br.readLine());

			int query = Integer.parseInt(st.nextToken());
			int a = Integer.parseInt(st.nextToken());
			int b = Integer.parseInt(st.nextToken());

			if (query == 0) {
				updateRange(1, N, 1, a, b);
			} else {
				sb.append(findTurnedOn(1, N, 1, a, b)).append('\n');
			}
		}

		System.out.println(sb);
	}

	private static void updateRange(int start, int end, int node, int left, int right) {
		updateLazy(start, end, node);
		if (right < start || end < left) return;
		if (left <= start && end <= right) {
			// 반전되는 개수 = 전체 범위 - 현재 켜진 스위치 수
			segTree[node] = (end - start + 1) - segTree[node];
			if (start != end) {
				lazy[node << 1]++;
				lazy[(node << 1) + 1]++;
			}
			return;
		}

		int mid = (start + end) >> 1;
		updateRange(start, mid, node << 1, left, right);
		updateRange(mid + 1, end, (node << 1) + 1, left, right);
		segTree[node] = segTree[node << 1] + segTree[(node << 1) + 1];
	}
	
	private static int findTurnedOn(int start, int end, int node, int left, int right) {
		updateLazy(start, end, node);
		if (right < start || end < left) return 0;
		if (left <= start && end <= right) return segTree[node];
		int mid = (start + end) >> 1;
		return findTurnedOn(start, mid, node << 1, left, right) + findTurnedOn(mid + 1, end, (node << 1) + 1, left, right);
	}

	private static void updateLazy(int start, int end, int node) {
		lazy[node] &= 1; // 2번 껐다켜면 똑같음
		if (lazy[node] == 1) {
			segTree[node] = (end - start + 1) - segTree[node];
			if (start != end) {
				lazy[node << 1]++;
				lazy[(node << 1) + 1]++;
			}
			lazy[node] = 0;
		}
	}
}