package kr.ac.lecture.baekjoon.Num10001_20000;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
* [문제 요약]
* 트리구조에서 특정 노드에서 추가되는 점수가 아래방향으로 전파될 때, 각 노드의 점수
*
* [제약 조건]
* 2 ≤ n, m ≤ 100,000
* 2 ≤ i ≤ n, 1 ≤ w ≤ 1,000
* 1번은 상사가 없으므로 -1이 입력
*
* [문제 설명]
* dfs로 쭉 전파하면 될듯
*
* 시간 초과 발생
*
* 입력 받을 때 자기 자신에만 추가한 다음,
* 자식만큼 반복하면서 자신의 점수 만큼 추가
*
*
* */
public class Main_BJ_14267_회사문화1 {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer stz = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(stz.nextToken());
        int m = Integer.parseInt(stz.nextToken());

        List<Integer>[] grah = new List[n];
        for (int i = 0; i< n; i++){
            grah[i] = new ArrayList<>();
        }

        stz = new StringTokenizer(br.readLine());
        for(int i = 0; i< n; i++){
            int p = Integer.parseInt(stz.nextToken());

            if(p == -1) continue;

            grah[p-1].add(i); // 단방향. 상사 -> 부하
        }

        int[] scores = new int[n];

        while (m-- > 0){
            stz = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(stz.nextToken()) - 1;
            int b = Integer.parseInt(stz.nextToken());

            scores[a] += b; // 자기 자신에만 점수 추가
        }

        for(int i = 0; i< n; i++){
            for(int c : grah[i]){ // 부하들 만큼 반복
                scores[c] += scores[i]; // 부하들 점수에 상사의 점수 추가 -> 상사의 점수가 추가되면 부하도 점수가 증가 되므로
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int s : scores){
            sb.append(s).append(" ");
        }

        System.out.println(sb.toString());

        br.close();
    }


}
