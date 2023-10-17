package kr.ac.lecture.baekjoon.Num10001_20000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
 * [문제 요약]
 * 두 가지 쿼리
 * 
 * 
 * [제약 조건]
 * 수열의 크키 1 ≤ N ≤ 500,000
 * 쿼리 개수 1 ≤ N≤ 500,000
 * 원소 0 ≤ Ai ≤ 100,000
 * 쿼리 1, 2
 * 0 ≤ i ≤ j < N
 * 쿼리 1
 * 0 ≤ k ≤ 100,000
 * 
 * 
 * [문제 설명]
 * 부분 xor을 하는 세그먼트트트리 구현
 * 범위 모두 update가 발생되어야 하기 때문에 lazy 사용
 * 
 * */
public class Main_BJ_12844_XOR {

	static int[] tree;
	static int[] lazy;

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer stz;
		int n = Integer.parseInt(br.readLine());

		int[] arr = new int[n + 1];
		stz = new StringTokenizer(br.readLine());
		for (int i = 1; i < n + 1; i++) {
			arr[i] = Integer.parseInt(stz.nextToken());
		}

		int h = (int) Math.ceil(Math.log(n) / Math.log(2));
		int size = (int) Math.pow(2, (h + 1));

		tree = new int[size];
		lazy = new int[size];

		init(arr, 1, 1, n);
		
		int m = Integer.parseInt(br.readLine());

		StringBuilder sb = new StringBuilder();

		while (m-- > 0) {
			stz = new StringTokenizer(br.readLine());

			int a = Integer.parseInt(stz.nextToken());
			int b = Integer.parseInt(stz.nextToken()) + 1;
			int c = Integer.parseInt(stz.nextToken()) + 1;

			if (b > c) {
				int tmp = b;
				b = c;
				c = tmp;
			}

			if (a == 1) {
				int k = Integer.parseInt(stz.nextToken());
				update(1, 1, n, b, c, k);
			} else {
				sb.append(query(1, 1, n, b, c)).append(System.lineSeparator());
			}
		}

		System.out.println(sb.toString());

		br.close();
	}

	static int init(int[] arr, int node, int left, int right) {
		if (left == right) {
			return tree[node] = arr[left];
		}

		int mid = (left + right) >> 1;
		init(arr, node << 1, left, mid);
		init(arr, (node << 1) + 1, mid + 1, right);

		return tree[node] = init(arr, node << 1, left, mid) ^ init(arr, (node << 1) + 1, mid + 1, right);
	}

	static void update(int node, int left, int right, int low, int high, int k) {
		propagation(node, left, right);
		if (right < low || left > high) {
			return;
		}

		if (low <= left && right <= high) {
			lazy[node] = k;
			propagation(node, left, right);
			return;
		}

		int mid = (left + right) >> 1;
		update(node << 1, left, mid, low, high, k);
		update((node << 1) + 1, mid + 1, right, low, high, k);

		tree[node] = (tree[node << 1] ^ tree[(node << 1) + 1]);
	}

	static void propagation(int node, int left, int right) {
		if (lazy[node] != 0) {
			if ((right - left + 1) % 2 == 1) {
				tree[node] ^= lazy[node];
			}

			if (left != right) {
				tree[node << 1] ^= lazy[node];
				tree[(node << 1) + 1] ^= lazy[node];
			}
			lazy[node] = 0;
		}
	}

	static int query(int node, int left, int right, int low, int high) {
		propagation(node, left, right);

		if (right < low || left > high) {
			return 0;
		}

		if (low <= left && right <= high) {
			return tree[node];
		}

		int mid = (left + right) >> 1;
		int leftChild = query(node << 1, left, mid, low, high);
		int rightChild = query((node << 1) + 1, mid + 1, right, low, high);

		return leftChild ^ rightChild;
	}
}

