import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 풀이 시작 : 8:44
 * 풀이 완료 :
 * 풀이 시간 :
 *
 * 문제 해석
 * url은 도메인/문제ID 형태
 * 채점 task는 채점 대기 큐에 들어감
 *
 * init()
 * - 100 N u0
 * - N개의 채점기, 초기 채점 요청 들어온 url u0 주어짐
 *
 * insertQueue
 * - 200 t p u
 * - t초에 우선순위 p, url = u인 문제를 큐에 넣어야 함
 * - 이미 큐에 있는 링크라면 넣지 않음
 *
 * judgeTry()
 * - 300 t
 * - t초에 "채점 대기 큐에서 즉시 채점이 가능한 경우" 중 우선순위가 가장 높은 채점 태스크를 채점함
 * - 채점이 불가능한 경우
 *  - 해당 task의 도메인이 현재 채점중인 경우
 *  - 해당 task의 도메인이 가장 최근에 채점된 시간이 start, 끝난 시간이 start + gap라 할 때
 *    t < start + 3 * gap인 t
 *  - 전부 채점중이라 채점 가능한 채점기가 없는 경우
 * - 채점 대상 선전
 *  - 우선순위 p가 낮은 순
 *  - p가 같다면 채점 대기 큐에 들어온 t가 빠른 순
 *
 * finishJudge()
 * - 400 t Jid
 * - t초에 Jid번 채점기가 현재 채점하고 있는 문제 채점 완료
 * - Jid번 채점기가 채점중이지 않다면 무시
 *
 * getQueueSize()
 * - 500 t
 * - t초에 채점 대기 큐의 사이즈
 *
 * 구해야 하는 것
 * 500 t의 쿼리 결과
 *
 * 문제 입력
 * 첫째 줄 : 명령 수 Q
 * 둘째 줄 : init()
 * 셋째 줄 ~ Q - 1개 줄 : 명령
 *
 * 제한 요소
 * 1 <= Q <= 50000
 * 1 <= N <= 50000
 * 1 <= 도메인 길이 <= 19
 * 1 <= 서로 다른 도메인 수 <= 300
 * 1 <= 문제 ID <= 10억
 * 1 <= p <= N
 * 1 <= Jid <= N
 * 1 <= t <= 1_000_000
 *
 * 생각나는 풀이
 * 우선순위 큐
 * 도메인의 개수가 최대 300 -> 우선순위 큐 배열을 통해 도메인별로 우선순위 관리
 * 도메인 -> idx로 변경하는 HashMap
 * 큐에 들어있는 도메인 관리하기 위한 HashSet
 * 채점 현황을 2*300 배열로 관리
 *
 * 구현해야 하는 기능
 * 필요한 변수
 * 0. 채점할 문제 정보가 들어있는 클래스
 *  - 변수 : 채점 요청 시간 t, 도메인 to index 변환된 index, 전체 url
 *
 * 1. 채점 대기 큐 수행할 우선순위 큐 배열
 * 2. 채점 대기 큐에 있는 url을 저장할 HashSet
 * 3. domain to index용 HashMap
 * 4. 현재 몇 개의 서로 다른 도메인이 나왔는지 세는 카운트 변수
 * 5. 각 채점기의 채점 현황을 관리하는 2차원*300칸 배열
 * 6. 비어있는 채점기 저장용 우선순위 큐
 */
public class Codetree_코드트리채점기 {
    static int N, cnt; // 전체 채점기 수 N, 사용중인 도메인 수
    static HashSet<String> problemsInQueue = new HashSet<>(); // 채점 대기 큐에 들어 있는 문제들
    static PriorityQueue<Problem>[] problems = new PriorityQueue[301]; // 각 도메인 당 채점 대기 pq
    static HashMap<String, Integer> domainToIdx = new HashMap<>(); // 도메인 당 할당된 인덱스로 매핑하는 Map
    static PriorityQueue<Integer> judgeMachines = new PriorityQueue<>(); // 사용 가능한 채점기계 저장하는 pq
    static int[][] judgeTime = new int[2][301]; // judgeTime[0][i] = i번 도메인 채점 시작 시간, judgeTime[1][i] = i번 도메인 채점 종료 시간
    static int[] judgingProblemNumber; // 각 채점기가 채점중인 도메인 번호

    static class Problem implements Comparable<Problem> {
        int t, p, idx; // 대기 큐 들어온 시간, 우선순위, 도메인 인덱스
        String url; // 원본 url

        public Problem(int t, int p, int idx, String url) {
            this.t = t;
            this.p = p;
            this.idx = idx;
            this.url = url;
        }

        @Override
        public int compareTo(Problem o) {
            if (this.p == o.p) return this.t - o.t;
            return this.p - o.p;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int Q = Integer.parseInt(br.readLine()) - 1;
        StringBuilder sb = new StringBuilder();
        int t, p;
        String url;

        init(br);
        while (Q-- > 0) {
            st = new StringTokenizer(br.readLine());
            int query = Integer.parseInt(st.nextToken());
            t = Integer.parseInt(st.nextToken());

            if (query == 200) {
                p = Integer.parseInt(st.nextToken());
                url = st.nextToken();
                insertQueue(t, p, url);
            } else if (query == 300) {
                judgeTry(t);
            } else if (query == 400) {
                int jId = Integer.parseInt(st.nextToken());
                finishJudging(t, jId);
            } else {
                sb.append(problemsInQueue.size()).append('\n');
            }
        }

        System.out.println(sb);
    }

    // 초기 세팅 메서드
    private static void init(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        st.nextToken(); // 100 버림
        N = Integer.parseInt(st.nextToken());
        judgingProblemNumber = new int[N + 1];
        for (int i = 1; i <= N; i++) judgeMachines.offer(i);
        for (int i = 1; i <= 300; i++) problems[i] = new PriorityQueue<>();
        String url = st.nextToken();
        st = new StringTokenizer(url, "/"); // "/" 기준으로 자름
        int idx = getDomainIdx(st.nextToken());
        problemsInQueue.add(url);
        problems[idx].offer(new Problem(0, 1, idx, url)); // 할당된 도메인 인덱스 pq에 삽입
    }

    // 채점 대기 큐에 삽입하는 메서드
    private static void insertQueue(int t, int p, String url) {
        // 큐에 이미 같은 url 존재하면 못넣음
        if (problemsInQueue.contains(url)) return;
        StringTokenizer st = new StringTokenizer(url, "/");
        int idx = getDomainIdx(st.nextToken());
        problemsInQueue.add(url);
        problems[idx].offer(new Problem(t, p, idx, url)); // 할당된 도메인 인덱스 pq에 삽입
    }

    // 채점 시도 메서드
    private static void judgeTry(int t) {
        // 모든 채점기가 채점중이면 리턴
        if (judgeMachines.isEmpty()) return;
        int firstIdx = -1;
        Problem first = new Problem(0, 50001, -1, "");

        for (int i = 1; i <= cnt; i++) {
            // 현재 도메인 채점 종료 시간 = -1이라면 채점 진행중
            if (judgeTime[1][i] == -1) continue;
            // 현재 도메인이 없으면 건너뜀
            if (problems[i].isEmpty()) continue;
            int gap = judgeTime[1][i] - judgeTime[0][i];
            // 채점 불가능 조건 중 하나
            if (t < judgeTime[0][i] + 3 * gap) continue;

            // 가장 우선순위 높은 값으로 갱신
            if (first.compareTo(problems[i].peek()) > 0) {
                first = problems[i].peek();
                firstIdx = i;
            }
        }

        // 채점 가능한 문제가 없으면 리턴
        if (firstIdx == -1) return;
        // 채점 대기 큐에서 삭제
        int machineNum = judgeMachines.poll();
        // 채점기가 현재 채점하는 도메인 인덱스 저장
        judgingProblemNumber[machineNum] = firstIdx;
        // 채점 현황 갱신
        judgeTime[0][firstIdx] = t;
        judgeTime[1][firstIdx] = -1;
        // 대기 큐 url 목록에서 삭제
        problemsInQueue.remove(first.url);
    }

    // 채점 종료 메서드
    private static void finishJudging(int t, int jId) {
        // 채점중이지 않다면 리턴
        if (judgingProblemNumber[jId] == 0) return;
        // 채점중인 도메인 인덱스
        int domainIdx = judgingProblemNumber[jId];
        // 채점중이지 않다고 표시
        judgingProblemNumber[jId] = 0;
        // 채점 종료 시간 갱신
        judgeTime[1][domainIdx] = t;
        // 채점 가능한 채점기 목록에 삽입
        judgeMachines.offer(jId);
    }

    // 도메인에 매핑된 인덱스를 반환하는 메서드
    private static int getDomainIdx(String domain) {
        // 이미 매핑된 도메인이라면 매핑된 인덱스 리턴
        if (domainToIdx.containsKey(domain)) return domainToIdx.get(domain);
        // 처음 나오는 도메인이라면 매핑 후 그 값 리턴
        else {
            domainToIdx.put(domain, ++cnt);
            return cnt;
        }
    }

}