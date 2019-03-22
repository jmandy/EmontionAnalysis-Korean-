/*
 * Twitter Korean Text - Scala library to process Korean text
 *
 * Copyright 2014 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;



public class JavaTwitterKoreanTextExample {
  public static void main(String[] args) throws GoogleAPIException, JWNLException {
	  IndexWord VERB;
	  IndexWord ADJECTIVE;
	  IndexWord ADVERB;
	  IndexWord NOUN;
	  try {
          // initialize JWNL (this must be done before JWNL can be used)
          JWNL.initialize();
         // new Examples().go();
          
      } catch (Exception ex) {
          ex.printStackTrace();
          System.exit(-1);
      }
	 
	  
	  
    String text = "오늘 친구랑 싸웠다...내 카톡을 무시한다";
    
    

    System.out.println("원본: "+text);
    
    // Normalize
    CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
    System.out.println("정규화: "+normalized);
 
     String key="AIzaSyCkEb0LyEkRkYgHIBRzKbaPf0Mg5Xop1l8";
  	  
  GoogleAPI.setHttpReferrer("https://developers.google.com/console/help/new/");
  GoogleAPI.setKey(key);
  String normalized2=normalized.toString();  

  String translatedText = Translate.DEFAULT.execute(normalized2, Language.KOREAN, Language.ENGLISH);
 System.out.println(translatedText);//번역된 문장
 
 //토큰화하여 words 배열에 저장
 String[] words2 = translatedText.split("\\s");

 //특수문자 제거
 int k=0;
 for (String wo : words2 ) {
	 words2[k]=getSTRFilter(wo);
	 k++;	 
	 }
  final ArrayList<String> list = new ArrayList<String>();
  Collections.addAll(list, words2);
  list.remove("");   
 
 //단어별 품사 속성 얻기
 for (int i = 0; i < words2.length; ++i) {
     System.out.print(words2[i]+":");
     int[] p = polysemy(words2[i]);
     for (int j = 0; j < p.length; ++j)
       System.out.print(p[j]+((j<p.length-1)?",":"\r\n"));
   } 
//동사원형 만들기
 k=0;
 VerbLemmatizer VerbLemmatizer = new VerbLemmatizer();
 for (String wo : words2 ) {
	 words2[k]=VerbLemmatizer.lemmatize(wo);	 
	 System.out.println(words2[k]);
	 k++;
	 }
 
 String result="";
 for (int i = 0; i < words2.length; i++) {
	 result=result.concat(words2[i]+" ");
 }
 System.out.println(result);
 
 
 
 
 //2차원 배열 생성
 String[][] word = null;
 //배열의 행을 동적 할당
 word = new String[1034][];
 
 try {
    //파일 객체 생성
    File file = new File("test.txt");
    //입력 스트림 생성
    FileReader fileReader = new FileReader(file);
    //입력 버퍼 생성
    BufferedReader bufReader = new BufferedReader(fileReader);
    String line = "";
    
    int row=0;
    
    while((line = bufReader.readLine()) != null) {
       //한 줄 읽어와서 띄어쓰기 단위 토큰화
       StringTokenizer st = new StringTokenizer(line);
       //토큰화 갯수만큼 열을 동적 할당
       word[row] = new String[st.countTokens()];
       int col=0;
       while(st.hasMoreTokens()) {
          //토큰화된 단어 2차원 배열에 넣기
          word[row][col] = st.nextToken();
          col++;
       }
       row++;
    }
       
    //.readLine()은 끝에 개행 문자를 읽지 않는다.
    bufReader.close();
 }catch(FileNotFoundException e) {
    
 }catch(IOException e) {
    System.out.println(e);
 }

/* //2차원 배열 출력
 for(int r=0; r<word.length; r++) {
    for(int c=0; c<word[r].length; c++) {
       System.out.print(word[r][c] + " ");
    }
    System.out.println();
 }*/
 

 StringTokenizer st = new StringTokenizer(result);
 String[] testWord = new String[st.countTokens()];
 int i=0;
 while(st.hasMoreTokens()) {
    //토큰화된 단어 배열에 넣기
    testWord[i] = st.nextToken();
    i++;
 }
 
 double valance=0, arousal=0, avgValance=0, avgArousal=0;
 int count=0;
 
 //break문 추가해야해
 for(i=0; i<testWord.length; i++) {
    for(int r=0; r<word.length; r++) {
       for(int c=2; c<word[r].length; c++) {
          if(testWord[i].equalsIgnoreCase(word[r][c])) {
             System.out.println(word[r][c]);
             valance += Double.parseDouble(word[r][0]);
             arousal += Double.parseDouble(word[r][1]);
             count++;
          }
       }
    }
 }
 
 avgValance = valance/count;
 avgArousal = arousal/count;
 
 System.out.println(avgValance);
 System.out.println(avgArousal);
 
 if(avgValance>=5 && avgArousal>=5) {
    System.out.println("Happy / Exciting");
 } else if (avgValance<5 && avgArousal>=5) {
    System.out.println("Anxious / Angry");
 } else if (avgValance<5 && avgArousal<5) {
    System.out.println("Sad / Depressed");
 } else {
    System.out.println("Relaxed / Calm");
 }

 


 
 
 
 
 
 
 }   


/*
    // Tokenize
    Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(tokens));
    // [한국어, 를, 처리, 하는, 예시, 입니, 다, ㅋㅋ, #한국어]
    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens));
    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


    // Stemming
    Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
    System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(stemmed));
    // [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ, #한국어]
    System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed));
    // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하다(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 이다(Adjective: 12, 3), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


    // Phrase extraction
    List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
    System.out.println(phrases);
    // [한국어(Noun: 0, 3), 처리(Noun: 5, 2), 처리하는 예시(Noun: 5, 7), 예시(Noun: 10, 2), #한국어(Hashtag: 18, 4)]
*/
  
  public static String getSTRFilter(String str){ 

	  int str_length = str.length(); 
	  String strlistchar   = ""; 
	  String str_imsi   = "";  
	   
	  String []filter_word={"\\p{Z}","","\\.","\\?","\\/",">\\~","\\!","\\@","\\#","\\$","\\%","\\^","\\&",
			  "\\*","\\(","\\)","\\_","\\+","\\=","\\|","\\\\","\\}","\\]","\\{","\\[","\\\"","\\'",
			  "\\:","\\;","\\<","\\,","\\>","\\.","\\?","\\/"};

	  for(int i=0;i<filter_word.length;i++){ 
	   //while(str.indexOf(filter_word[i]) >= 0){ 
	      str_imsi = str.replaceAll(filter_word[i],""); 
	      str = str_imsi; 
	      //} 
	  } 
	  return str; 
	 } 
  
  public static int[] polysemy(String word) throws JWNLException {
	    int[] polysemies = new int[4];
	    int poly=-1;
	    String wordclass="";	    
	    
	    Dictionary d = Dictionary.getInstance();
	    IndexWord noun_form = d.getIndexWord(POS.NOUN, word);
	    polysemies[0] = (noun_form==null)?0:noun_form.getSenses().length;
	    IndexWord verb_form = d.getIndexWord(POS.VERB, word);
	    polysemies[1] = (verb_form==null)?0:verb_form.getSenses().length;
	    IndexWord adj_form = d.getIndexWord(POS.ADJECTIVE, word);
	    polysemies[2] = (adj_form==null)?0:adj_form.getSenses().length;
	    IndexWord adv_form = d.getIndexWord(POS.ADVERB, word);
	    polysemies[3] = (adv_form==null)?0:adv_form.getSenses().length;
	    if((polysemies[0]>polysemies[1])&&(polysemies[0]>polysemies[2])&&(polysemies[0]>polysemies[3])) {
	    	poly=1;//poly=1 -> noun
	    	wordclass="noun";
	    }
	    else if((polysemies[1]>polysemies[0])&&(polysemies[1]>polysemies[2])&&(polysemies[1]>polysemies[3])) {
	    	poly=2;//poly=2 -> verb
	    	wordclass="verb";
	    }
	    else if((polysemies[2]>polysemies[0])&&(polysemies[2]>polysemies[1])&&(polysemies[2]>polysemies[3])) {
	    	poly=3;//poly=3 -> adjective
	    	wordclass="adjective";
	    }
	    else if((polysemies[3]>polysemies[0])&&(polysemies[3]>polysemies[1])&&(polysemies[3]>polysemies[2])) {
	    	poly=4;//poly=4 -> adverb
	    	wordclass="adverb";
	    }
	    System.out.print(wordclass+" ");
	   
	    
	    return polysemies;
	  }



	
}
