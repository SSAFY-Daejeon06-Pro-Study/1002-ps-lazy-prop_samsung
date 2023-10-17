package kr.ac.lecture.baekjoon.Num1001_10000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
* [문제 요약]
* 특정 구간의 스위치를 키고 꺼서, 구간 내의 켜져있는 스위치 개수 응답
*
* [제약 조건]
* N(2 ≤ N ≤ 100,000)
* M(1 ≤ M ≤ 100,000)
*
* [문제 설명]
* 구간별로 업데이트 및 스위치의 상태를 변환해야 함
* 업데이트 역시 구간별로 해야하기 때문에, lazy propagation segment tree 사용
*
* 꺼진 상태를 0, 켜진 상태를 1로 놨을 때, 구간 합을 구하는 것과 같음
*
* left~right 구간의 상태를 변화 시키는 것은
* (right - left + 1) - tree[node]와 같음
*
* 5, 6, 7 구간에서 6만이 켜져있다고 했을 때,
* 해당 구간의 node는 tree[node] = 1임
* 상태를 변화하면
* (7-5+1) - 1 = 2
* 6이 꺼지고 5, 7이 켜진 것과 같은 상태로 변화됨
*
* lazy를 할 때, 짝수일 경우 상태가 변화되지 않음 -> 홀수 일 때만 변화됨
*
*
* */
public class Main_BJ_1395_스위치 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer stz = new StringTokenizer(br.readLine());
        
        int n = Integer.parseInt(stz.nextToken());
        int m = Integer.parseInt(stz.nextToken());
        
        SegmentTree segmentTree = new SegmentTree(n);
        
        while(m-- > 0) {
            stz = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(stz.nextToken());
            int b = Integer.parseInt(stz.nextToken());
            int c = Integer.parseInt(stz.nextToken());
            
            if(a == 0) { // 반전
                segmentTree.update(1, 1, n, b, c);
            }else { // 켜진 것의 갯수
                System.out.println(segmentTree.query(1, 1, n, b, c));
            }
        }

        br.close();
    }

    private static class SegmentTree{
        int[] tree;
        int[] lazy;
        
        SegmentTree(int n){
            tree = new int[n * 4];
            lazy = new int[n * 4];
        }
        
        void update(int node, int left, int right, int low, int high) {
            lazyUpdate(node, left, right);

            if(high < left || right < low) {
                return;
            }

            if(low <= left && right <= high) {
                tree[node] = (right - left + 1) - tree[node]; // 반전
                if(left != right) {
                    lazy[node << 1] += 1;
                    lazy[(node << 1) + 1] += 1;
                }
                return;
            }
            
            int mid = (left + right) >> 1;
            update(node << 1, left, mid, low, high);
            update((node << 1)+1, mid+1, right, low, high);
            
            tree[node] = tree[node << 1] + tree[(node << 1) + 1]; 
        }

        void lazyUpdate(int node, int left, int right) {
            if(lazy[node] == 0) return;

            if(lazy[node] % 2 != 0){ // 홀수일 때만 변경 발생
                tree[node] = (right - left + 1) - tree[node];
            }
            if(left != right) {
                lazy[node << 1] += lazy[node];
                lazy[(node << 1)+1] += lazy[node];
            }
            lazy[node] = 0;
        }
        
        int query(int node, int left, int right, int low, int high) {
            lazyUpdate(node, left, right);
            
            if(high < left || low > right) {
                return 0;
            }

            if(low <= left && right <= high){
                return tree[node];
            }
            
            int mid = (left + right) >> 1;
            return query(node << 1, left, mid, low, high) +
                    query((node << 1) + 1, mid+1, right, low, high);
        }
    }
}
