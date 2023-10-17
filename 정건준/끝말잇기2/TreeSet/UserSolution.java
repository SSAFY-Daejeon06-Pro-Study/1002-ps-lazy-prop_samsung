package 끝말잇기2.TreeSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class UserSolution {
    static class Node {
        int id;
        Node prev;
        Node next;
        Node(int id) {
            this.id = id;
            this.prev = null;
            this.next = null;
        }
    }

    //이중 원형 연결 리스트(랜덤 엑세스 기법 적용)
    static class CircularLinkedList {
        Node[] nodes;

        //원형 연결 리스트 생성
        CircularLinkedList(int N) {
            nodes = new Node[N + 1];
            nodes[1] = new Node(1);

            Node prev = nodes[1];
            for (int i = 2; i <= N; i++) {
                Node cur = new Node(i);
                prev.next = cur;
                cur.prev = prev;
                prev = cur;
                nodes[i] = cur;
            }
            nodes[1].prev = nodes[N];
            nodes[N].next = nodes[1];
        }

        void unlink(Node node) {
            int id = node.id;
            nodes[id].prev.next = nodes[id].next;
            nodes[id].next.prev = nodes[id].prev;
        }

        Node getNextNode(int id) {
            return nodes[id].next;
        }
    }

    static TreeSet<String>[] wordSets; //wordSet[c] := c 로 시작하는 사용 가능한 단어 집합
    static CircularLinkedList playerList; //플레이어 목록
    static HashSet<String> select; // 단어 선택 여부 저장, set에 String이 없다면 단어가 아직 선택되지 않음
    static ArrayList<String> nextPushList; // 다음 라운드에 들어갈 문자열 목록

    void init(int N, int M, char mWords[][]) {
         playerList = new CircularLinkedList(N);
         select = new HashSet<>();
         wordSets = new TreeSet[26];

         for(int i=0; i<26; i++) wordSets[i] = new TreeSet<>();

         for(int i=0; i<M; i++) {
             StringBuilder sb = new StringBuilder();
             for(int j=0; j<mWords[i].length; j++) {
                 if(mWords[i][j] == '\0') break;
                 sb.append(mWords[i][j]);
             }
             wordSets[sb.charAt(0) - 'a'].add(sb.toString());
         }
    }

    int playRound(int mID, char mCh) {
        Node curPlayer = playerList.nodes[mID]; //현재 플레이어
        char curCh = mCh; //뽑을 단어의 앞글자
        nextPushList = new ArrayList<>();

        while(true) {
            //단어를 뽑음
            String word = wordSets[curCh - 'a'].pollFirst();
            select.add(word);

            //뽑을 단어가 없으면 라운드 종료
            if(word == null) {
                break;
            }

            //단어를 뒤집음
            String reverseWord = new StringBuilder(word).reverse().toString();

            //뒤집힌 단어가 아직 뽑히지 않았으면 이번 라운드가 끝나고 추가될 가능성이 있음
            if(!select.contains(reverseWord)) {
                nextPushList.add(reverseWord);
            }

            //다음 플레이어가 뽑을 준비를 함
            curPlayer = playerList.getNextNode(curPlayer.id);
            curCh = word.charAt(word.length()-1);
        }
        
        //라운드 끝, 뒤집은 단어를 추가
        for(int i=0; i<nextPushList.size(); i++) {
            String word = nextPushList.get(i);
            if(!select.contains(word)) wordSets[word.charAt(0) - 'a'].add(word);
        }
        playerList.unlink(curPlayer);
        return curPlayer.id;
    }
}
