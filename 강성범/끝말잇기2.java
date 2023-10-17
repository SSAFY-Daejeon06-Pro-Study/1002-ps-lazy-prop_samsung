package kr.ac.lecture.samsung.special_lecture.eight.tagwords2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/*
 * [문제 요약]
 *
 *
 * [제약 조건]
 * N명의 사용자 M개의 단어
 * (3 ≤ N ≤ 50,000, N ≤ M ≤ 50,000)
 * 플레이어 수 1~N
 * 단어의 길이는 2~10
 * 단어 리스트 내의 각 단어는 중복되지 않음
 * 주어지는 각 단어의 마지막은 \0가 붙어있음
 * 모든 단어는 소문자
 *
 * [문제 풀이]
 * 게임은 여러 라운드로 구성
 * ID를 기준으 1~N 순서로 진행
 * N이 끝나면 1이 다시 시작
 *
 * 플레이어는 자신의 턴이 되면 조건에 맞는 단어 선택
 * 선택된 단어는 해당 라운드에서 재사용 불가
 *
 * 단어 선택 조건
 * 1. 이전 턴에 선택된 단어의 마지막 문자로 시작하는 단어
 *   - 첫 번째 경우 시작단어
 * 2. 선택되지 않은 단어
 * 3. 여러개면 사전순
 *
 * 조건에 맞는 단어가 없으면 해당 플레이어는 탈락
 * 탈락한 플레이어는 다음 다운드에서 턴진행에서 제외
 *
 * 다음 라운드를 시작하기 전에 선택된 단어를 뒤집은 후 리스트에 추가
 * 뒤집은 단어가 이미 선택된 단어라면 단어 리스트에 추가하지 않음
 *
 * [구현해야 하는 API]
 * int playRound(int mID, char mCh)
 * mID : 첫 번째 턴을 진행할 플레이어 ID
 *   - 아직 탈락하지 않음을 보장함
 * mCh : 단어
 *   - 선택 가능한 단어들 중 mCh로 시작하는 단어 보장
 * 호출 횟수는 N을 넘지 않음
 *
 * 해당 라운드에서 탈락한 플레이어 ID 반환
 *
 * [만들어야 할 기능]
 * 1. 플레이어 정보
 * 이전에 탈락한 순서는 건너 뛰어야 함
 * 어떻게?
 * 환영 연결 리스트를 만든다?
 * init에서 N만큼 환영 연결 리스트를 만들고, 순서가 지날 때 까지 계속 포인터에 따라 이동
 * 단, 이 방법을 사용하게 되면
 * 게임을 처음 시작할 때, mID인 사람을 찾는 로직이 필요
 * set같은 곳에 담아서 pass를 계속 하는 것 보다는 좋은 방법이지 않을까?
 *
 * 2. 단어 선택
 * 단어는 우선순위에 따라서 선택
 * 처음에 a~z 배열을 만들고 해당 위치에서 시작하긴 해야함
 *
 * 각 단어는 10자리까지 존재 하고 소문자임
 * 27진법으로 해싱하고, 해싱된 값이 낮은 순부터 값을 넣음
 * 단어를 뒤집어서 넣을 때 해싱된 값의 위치를 찾아서 넣으면 됨
 * 단어는 하나의 연결 리스트임 -> 단방향
 * 단어, 해싱 값, 뒤집혔는지, 다음 포인터
 *
 * 처음으로 뒤집힌 단어는 해당 라운드에 추가해야 하므로 리스트에서 삭제 후 정보 저장
 * 만약 이미 뒤집힌 단어가 사용 되었으면 그냥 삭제
 *
 * 이러면
 * 삽입, 삭제 과정이 O(N) 이라서 안됨
 *
 * treeset을 이용하자
 * 삽입, 삭제, 검색 과정을 O(logN)으로 할 수 있음
 *
 * 사용된 단어를 어떻게 판별할 것인가?
 * 또, 앞 뒤가 같은 단어를 어떻게 확인할 것인가?
 *
 * 사용된 단어를 hashset에 저장
 * 만약 뒤집은 단어가 포함되어 있지 않으면 추가
 *
 *
 *
 * */
class 끝말잇기2 {

    static PlayerLinkedList playerList;
    static TreeSet<String>[] words = new TreeSet[26];
    static HashSet<String> used = new HashSet<>();

    public void init(int N, int M, char[][] mWords) {
        playerList = new PlayerLinkedList();
        used.clear();

        // treeset 초기화
        for(int i=0; i<26; i++){
            if(words[i] == null){
                words[i] = new TreeSet<>();
            }else{
                words[i].clear();
            }
        }

        for(int i=1; i<N+1; i++){
            playerList.add(i); // 선수 추가
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) { // m만큼 반복해서 단어 저장
            int len = 0;
            sb.setLength(0);

            while (mWords[i][len] != '\0'){
                sb.append(mWords[i][len++]);
            }
            words[mWords[i][0]-'a'].add(sb.toString());
        }
    }

    public int playRound(int mID, char mCh) {
        Player player = playerList.find(mID);
        List<String> tmp = new ArrayList<>(); // 반대로 뒤집은 단어를 추가할 때 사용할 임시 리스트

        // 게임 시작
        while (true){
            String select = words[mCh - 'a'].pollFirst(); // 사전 순으로 가장 앞선 단어 선택

            if(select == null){ // 선택할 단어가 없을 때
                break;
            }else{
                used.add(select);

                String reverse = new StringBuilder(select).reverse().toString();
                if(!used.contains(reverse)){ // 사용한 적 없는 단어이면 단어 추가
                    tmp.add(reverse);
                }

                mCh = reverse.charAt(0); // 다음으로 시작할 단어
                player = playerList.circleNext(player); // 환영으로 동작할 것임
            }
        }

        for(String t : tmp){
            if(used.contains(t)) continue; // 임시에 저장된 단어가 이미 사용되었을 경우 continue
            words[t.charAt(0)-'a'].add(t);
        }

        playerList.remove(player.number); // 플레이어 제거 후 반환
        return player.number;
    }

    private class PlayerLinkedList{
        Player head;
        Player tail;

        Player find(int num){
            Player x = head;

            while (true){
                if(x.number == num){
                    return x;
                }
                x = x.next;
            }
        }

        void addFirst(int num){ // 1번 선수 추가할 때 사용
            head = tail = new Player(num);
        }

        void add(int num){ // 마지막에 추가
            if(head == null){
                addFirst(num);
                return;
            }

            Player player = new Player(num);
            tail.next = player;
            tail = player;
        }

        void remove(int num){ // num인 선수 삭제
            Player x = head;

            // 처음 삭제
            if(x.number == num){
                if(x.next != null){
                    head = x.next;
                }
                return;
            }

            while (true){
                if(x.next.number == num){ // 삭제할 선수를 찾았으면
                    if(x.next.next == null){ // 마지막 삭제
                        x.next = null;
                        tail = x.next;
                    }else{
                        x.next = x.next.next;
                    }
                    return;
                }
                x = x.next;
            }
        }

        Player circleNext(Player currnet){ // 환영
            if(currnet.next == null){
                return head;
            }
            return currnet.next;
        }
    }

    private class Player{
        int number;
        Player next;

        public Player(int number) {
            this.number = number;
        }
    }

}
