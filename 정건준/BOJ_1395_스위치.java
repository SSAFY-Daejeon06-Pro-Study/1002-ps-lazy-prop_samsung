import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

/***
 * 구간에 대한 쿼리를 수행하므로 세그먼트 적용
 * 구간에 대한 업데이트를 수행하므로 lazy propagation 적용
 * 
 * 구간 노드가 가지는 값 = 켜진 스위치 개수
 *
 * updateRange
 * [left, right] 구간을 반전시키면 됨
 * 즉 nodes[idx] = 범위 크기 - nodes[idx]
 * lazy = 반전 횟수
 */

class SegmentTree {
	int[] nodes;
	int[] lazy;
	int n;
	
	SegmentTree(int n) {
		this.n = n;
		nodes = new int[n * 4];
		lazy = new int[n * 4];
		Arrays.fill(lazy, 2);
	}
	
	void lazyUpdate(int start, int end, int idx) {
		//lazy 값이 없음
		if(lazy[idx] == 0) return;

		//반전
		if(lazy[idx] % 2 == 1) {
			nodes[idx] = (end - start + 1) - nodes[idx];
		}

		//리프노드가 아니면 lazy 값을 물려줌
		if(start != end) {
			lazy[idx * 2] += (lazy[idx]);
			lazy[idx * 2 + 1] += (lazy[idx]);
		}
		lazy[idx] = 0;
	}
	
	//start..end 구간와 left..right 구간의 교집합 값 반환
	int query(int start, int end, int idx, int left, int right) {
		lazyUpdate(start, end, idx);

		if(end < left || right < start) return 0;
		
		if(left <= start && end <= right) {
			return nodes[idx];
		}
		
		int mid = (start + end) / 2;
		int leftNode = query(start, mid, idx * 2, left, right);
		int rightNode = query(mid+1, end, idx * 2 + 1, left, right);
		return leftNode + rightNode;
	}

	//left..right와의 교집합 크기가 1 이상인 모든 구간 노드 업데이트
	int update(int start, int end, int idx, int left, int right) {
		lazyUpdate(start, end, idx);

		if(end < left || right < start) return nodes[idx];

		//업데이트, lazy 값을 물려줌
		if(left <= start && end <= right) {
			nodes[idx] = (end - start + 1) - nodes[idx];
			if(start != end) {
				lazy[idx * 2] += 1;
				lazy[idx * 2 + 1] += 1;
			}
			return nodes[idx];
		}

		int mid = (start + end) / 2;
		int leftNode = update(start, mid, idx * 2, left, right);
		int rightNode = update(mid + 1, end, idx * 2 + 1, left, right);
		return nodes[idx] = leftNode + rightNode;
	}

	int query(int left, int right) {
		return query(0, n-1, 1, left, right);
	}

	void update(int left, int right) {
		update(0, n-1, 1, left, right);
	}
}

public class BOJ_1395_스위치 {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(br.readLine());

		int N = Integer.parseInt(st.nextToken());
		int M = Integer.parseInt(st.nextToken());
		SegmentTree segmentTree = new SegmentTree(N);

		for(int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			int type = Integer.parseInt(st.nextToken());
			int left = Integer.parseInt(st.nextToken()) - 1;
			int right = Integer.parseInt(st.nextToken()) - 1;

			if(type == 0) segmentTree.update(left, right);
			else sb.append(segmentTree.query(left, right)).append('\n');
		}
		System.out.print(sb);
	}
}
