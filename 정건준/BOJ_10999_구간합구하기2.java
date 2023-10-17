import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class BOJ_10999_구간합구하기2 {

    static class SegmentTree {
        long[] nodes;
        long[] lazy;
        int n;

        SegmentTree(long[] arr) {
            n = arr.length;
            nodes = new long[4*n];
            lazy = new long[4*n];
            init(0, n-1, 1, arr);
        }

        long merge(long nodeA, long nodeB) {
            return nodeA + nodeB;
        }

        long init(int start, int end, int idx, long[] arr) {
            if(start == end) {
                return nodes[idx] = arr[start];
            }

            int mid = (start + end) / 2;
            long leftNode = init(start, mid, idx * 2, arr);
            long rightNode = init(mid+1, end, idx * 2 + 1, arr);
            return nodes[idx] = merge(leftNode, rightNode);
        }

        void updateLazy(int start, int end, int idx) {
            if(lazy[idx] == 0) return;

            //구간의 크기 만큼 반영
            nodes[idx] += (end - start + 1) * lazy[idx];

            //리프 노드가 아니면 양쪽 자식에 lazy 값을 물려줌
            if(start != end) {
                lazy[idx * 2] += lazy[idx];
                lazy[idx * 2 + 1] += lazy[idx];
            }
            lazy[idx] = 0;
        }

        long query(int start, int end, int idx, int left, int right) {
            updateLazy(start, end, idx);

            if(end < left || right < start) return 0;

            if(left <= start && end <= right) return nodes[idx];

            int mid = (start + end) / 2;
            long leftNode = query(start, mid, idx * 2, left, right);
            long rightNode = query(mid+1, end, idx * 2 + 1, left, right);
            return merge(leftNode, rightNode);
        }

        long updateRange(int start, int end, int idx, int left, int right, long newVal) {
            updateLazy(start, end, idx);
            
            if(end < left || right < start) return nodes[idx];
            
            //갱신
            if(left <= start && end <= right) {
                //구간의 크기 만큼 반영
                nodes[idx] += (end - start + 1) * newVal;
                //리프 노드가 아니면 양쪽 자식에 lazy값 추가
                if(start != end) {
                    lazy[idx * 2] += newVal;
                    lazy[idx * 2 + 1] += newVal;
                }
                return nodes[idx];
            }
            
            int mid = (start + end) / 2;
            long leftNode = updateRange(start, mid, idx * 2, left, right, newVal);
            long rightNode = updateRange(mid+1, end, idx * 2 + 1, left, right, newVal);
            return nodes[idx] = merge(leftNode, rightNode);
        }

        long query(int left, int right) {
            return query(0, n-1, 1, left, right);
        }

        void updateRange(int left, int right, long newVal) {
            updateRange(0, n-1, 1, left, right, newVal);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N, M, K;
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        long[] arr = new long[N];
        for(int i=0; i<N; i++) arr[i] = Long.parseLong(br.readLine());

        SegmentTree segmentTree = new SegmentTree(arr);

        for(int i=0; i<M + K; i++) {
            st = new StringTokenizer(br.readLine());
            int type = Integer.parseInt(st.nextToken());
            int left = Integer.parseInt(st.nextToken()) - 1;
            int right = Integer.parseInt(st.nextToken()) - 1;

            if(type == 1) {
                long newVal = Long.parseLong(st.nextToken());
                segmentTree.updateRange(left, right, newVal);
            }
            else {
                sb.append(segmentTree.query(left, right)).append('\n');
            }
        }
        System.out.print(sb);
    }
}
