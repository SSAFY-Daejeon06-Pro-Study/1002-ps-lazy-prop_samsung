package 끝말잇기2.Trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/***
 [문제]
 N명의 플레이어 (3 <= N <= 50000), M개의 단어(N <= M <= 50000)
 플레이어는 ID를 가짐
 각 단어는 길이가 2 이상 10 이하의 영어 알파벳 소문자로 구성되어 있고, '\0'문자로 끝나는 문자열
 주어진 단어 리스트 내에서 각 단어는 중복되지 않음

 끝말잇기 게임 시작 (int mID, char mCh)
 끝말잇기 게임은 여러 개의 라운드로 구성
 라운드를 시작하기 전 첫 턴을 진행할 ID(mId)와 첫 단어를 정하기 위한 하나의 문자(mCh)가 주어짐

 1. 플레이어는 자신의 턴이 되면 아래 조건에 맞는 단어를 단어 리스트에서 가져옴
 - 이전 턴에 선택한 단어의 마지막 문자로 시작하는 단어(첫 턴은 시작전 주어진 문자로 시작하는 단어)
 - 게임을 진행하는 동안 한 번도 선택된 적이 없는 단어
 - 1,2에 해당 하는 단어가 여러 개인 경우 사전 순으로 가장 빠른 단어

 2. 위 조건에 해당하는 단어가 있다면 가져온 단어를 단어 리스트에서 삭제
 다음 플레이어가 1 수행

 3. 만약 위 조건에 해당하는 단어가 없다면 해당 플레이어는 탈락하고 라운드 종료
 i) 남아 있는 플레이어가 1명이면 끝말잇기 게임 종료
 ii) 1명 이상이면 이번 라운드에서 삭제한 단어들을 뒤집은 후 단어 리스트에 추가 (뒤집한 단어가 이미 선택된 단어라면 단어 리스트에 넣지 않음)
 다음 라운드 시작, 첫 번째 플레이어가 1 수행

 트라이 적용

 [변수]
 ArraysList<Integer> playerList -> 남아 있는 참가자 목록
 HashSet<String> select -> 단어 선택 여부 저장, set에 String이 없다면 단어가 아직 선택되지 않음
 HashSet<String> trieString -> 트라이에 넣은 문자열 존재 유무 저장
 ArraysList<String> nextPushList -> 다음 라운드에 트라이에 들어갈 문자열 목록

 class Node {
 Node[26] children (0:a ~ 25:z)
 boolean terminal (터미널 노드, 즉 집합 내 문자열에 해당하는 노드는 true)
 int size (해당 노드를 루트하는 서브 트리의 사이즈)
 public Node() {
 children = new Node[26]
 size = 0
 terminal = true
 }
 }

 void init(int N, int M, char mWords[][])
 변수 초기화, Trie 생성
 mWords[][]에 저장된 문자열을 String으로 변환하고 Trie와 trieString에 넣음

 int playRound(int mID, char mCh)
 int playerIdx = Arryas.binarySearch(playerList, mID)
 char lastCh = mCh
 nextPushList = new ArrayList()


 while(true)
 단어를 뽑음
 String word = trie.getWord(lastCh)

 단어가 없으면 라운드 종료
 if(word == null)
 int playerID = playerList.get(playerIdx)
 playerList.remove(playerIdx)
 return break

 trieString.remove(word)
 select.add(word)

 단어를 뒤집음
 String reversedWord = reverse(word)

 뒤집은 단어가 선택되지 않았고, 트라이에 없으면 nextPushList에 넣음
 if(!select.containKey(reversedWord) && !trieString.containKey(reverseWord))
 nextPushList.add(reverseWord)

 다음 플레이어가 단어를 뽑을 준비를 함
 playerIdx = (playerIdx == playerList.length()) ? 0 : playserIdx + 1
 lastCh = reversedWord.charAt(0)

 nextPushList의 단어들을 트라이와 trieString에 넣음

 String reverse(int word)
 StringBulder sb = new StringBuilder(word)
 return sb.reverse()
 */

public class UserSolution {
    static ArrayList<Integer> playerList; //남아 있는 참가자 목록
    static HashSet<String> select; // 단어 선택 여부 저장, set에 String이 없다면 단어가 아직 선택되지 않음
    static HashSet<String> trieString; // 트라이에 넣은 문자열 존재 유무 저장
    static ArrayList<String> nextPushList; // 다음 라운드에 트라이에 들어갈 문자열 목록
    static Trie trie;

    static class Node {
        Node[] children; // (0:a ~ 25:z)
        boolean terminal; //(터미널 노드, 단어리스트 내 단어에 해당하는 노드는 true)
        int size; // (자식 개수)
        public Node() {
            children = new Node[26];
            size = 0;
            terminal = false;
        }
    }

    static class Trie {
        Node treeRoot = new Node();

        private int toNumber(char ch) {
            return ch - 'a';
        }

        //str 노드 추가 (중복으로 삽입되는 경우는 없음)
        //root - 트리의 루트
        //str - 삽입 문자열
        //strIdx - 삽입 문자열의 인덱스, 트라이 깊이
        private void pushNode(Node root, String str, int strIdx) {
            //문자열 삽입 완료
            if(str.length() == strIdx) {
                root.terminal = true;
                return;
            }

            int idx = toNumber(str.charAt(strIdx));
            if(root.children[idx] == null) {
                root.children[idx] = new Node();
                root.size += 1;
            }
            pushNode(root.children[idx], str, strIdx+1);
        }

        //사전 순으로 가장 앞선 단어 반환
        //root - 트리의 루트
        //result - root에 해당하는 문자열
        private String getNode(Node root, StringBuilder result) {
            //단어 찾음
            if (root.terminal) return result.toString();

            for (int i = 0; i < root.children.length; i++) {
                if (root.children[i] != null) {
                    result.append((char)(i + 'a'));
                    return getNode(root.children[i], result);
                }
            }

            //단어 못찾음 (null이 반환될 일은 없음)
            return null;
        }

        //str 노드 삭제 (str 노드는 반드시 존재)
        //root - 트리의 루트
        //str - 삭제 문자열
        //strIdx - 삭제 문자열의 인덱스, 트라이 깊이
        private Node removeNode(Node root, String str, int strIdx) {
            //str 노드로 옴 (str 노드는 터미널 노드)
            if(str.length() == strIdx) {
                //리프 노드이면 삭제
                if(root.size == 0) return null;

                //내부 노드이면 논리적 삭제(터미널 노드가 아니게 됨)
                root.terminal = false;
                return root;
            }

            int idx = toNumber(str.charAt(strIdx));
            root.children[idx] = removeNode(root.children[idx], str, strIdx+1);
            if(root.children[idx] == null) root.size--;

            //터미널 노드가 아닌 리프 노드이면 삭제
            if(root.size == 0 && !root.terminal) return null;
            return root;
        }

        public void push(String str) {
            pushNode(treeRoot, str, 0);
        }

        public String getWord(char ch) {
            int idx = toNumber(ch);
            //단어 못찾음
            if(treeRoot.children[idx] == null) return null;

            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            String word = getNode(treeRoot.children[idx], sb);

            //word 삭제
            treeRoot.children[idx] = removeNode(treeRoot.children[idx], word, 1);
            return word;
        }
    }

    void init(int N, int M, char mWords[][]) {
        playerList = new ArrayList<>();
        select = new HashSet<>();
        trieString = new HashSet<>();
        trie = new Trie();

        for(int i=1; i<=N; i++) {
            playerList.add(i);
        }

        for(int i=0; i<M; i++) {
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<mWords[i].length; j++) {
                if(mWords[i][j] == '\0') break;
                sb.append(mWords[i][j]);
            }
            String word = sb.toString();
            trie.push(word);
            trieString.add(word);
        }
    }

    int playRound(int mID, char mCh) {
        int playerIdx = Collections.binarySearch(playerList, mID);
        char lastCh = mCh;
        int returnID = -1;
        nextPushList = new ArrayList<>();

        while(true) {
            //단어를 뽑음
            String word = trie.getWord(lastCh);

            //뽑을 단어가 없으면 라운드 종료
            if(word == null) {
                returnID = playerList.get(playerIdx);
                playerList.remove(playerIdx);
                break;
            }

            trieString.remove(word);
            select.add(word);

            //단어를 뒤집음
            String reversedWord = new StringBuilder(word).reverse().toString();

            //뒤집힌 단어가 아직 뽑히지 않았고, 트라이에 없으면 nextPushList에 넣음
            if(!select.contains(reversedWord) && !trieString.contains(reversedWord)) {
                nextPushList.add(reversedWord);
            }

            //다음 플레이어가 뽑을 준비를 함
            playerIdx = (playerIdx == playerList.size() - 1) ? 0 : playerIdx + 1;
            lastCh = reversedWord.charAt(0);
        }

        //라운드 끝, 뒤집은 단어를 추가
        int nextPushListSize = nextPushList.size();
        for(int i=0; i<nextPushListSize; i++) {
            String word = nextPushList.get(i);
            trie.push(word);
            trieString.add(word);
        }
        return returnID;
    }
}