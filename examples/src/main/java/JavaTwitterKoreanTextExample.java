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
import java.util.StringTokenizer;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;


public class JavaTwitterKoreanTextExample {
  public static void main(String[] args) throws GoogleAPIException {


          
     
     int k=0;
     int noCount=0;
     String[][] word = null;
      //배열의 행을 동적 할당
      word = new String[1042][];
      
      //2차원 배열 생성 - 문장
      String[][] sentence = null;
      //예상감정1, 예상감정2, 문장
      sentence = new String[300][3];
            
      //감정 사전 읽어오기
      try {
         //파일 객체 생성
         File file = new File("word.txt");
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
      //테스트 문장 읽어오기
            try {
               //파일 객체 생성
               File file = new File("sentence2.txt");
               //입력 스트림 생성
               FileReader fileReader = new FileReader(file);
               //입력 버퍼 생성
               BufferedReader bufReader = new BufferedReader(fileReader);
               String line = "";
                     
               int row=0;
                     
               while((line = bufReader.readLine()) != null) {
                  sentence[row][0] = line.substring(0,1);
                  sentence[row][1] = line.substring(2,3);
                  sentence[row][2] = line.substring(4);               
                  row++;
               }                        
               //.readLine()은 끝에 개행 문자를 읽지 않는다.
               bufReader.close();
            }catch(FileNotFoundException e) {
                     
            }catch(IOException e) {
               System.out.println(e);
            }
            
            //2차원 배열 출력 - 문장
            for(int r=0; r<sentence.length; r++) {
               for(int c=0; c<sentence[r].length; c++) {
                  System.out.print(sentence[r][c] + " ");
               }
               System.out.println();
            }            
            int countMatch = 0;
            
            //문장 테스트. 감정 매칭
            for(int row=0; row<sentence.length; row++) {
               String testSentence = sentence[row][2];
 
               
               
               //정규화
                CharSequence normalized = TwitterKoreanProcessorJava.normalize(testSentence);
                System.out.println("정규화: "+normalized);
               
                //번역
                String key="";       
                 GoogleAPI.setHttpReferrer("https://developers.google.com/console/help/new/");
                 GoogleAPI.setKey(key);
                 String normalized2=normalized.toString();  
                 String translatedText = Translate.DEFAULT.execute(normalized2, Language.KOREAN, Language.ENGLISH);
                 System.out.println(translatedText);//번역된 문장
                               
               
               StringTokenizer st = new StringTokenizer(translatedText);
               String[] testWord = new String[st.countTokens()];
               
               int i=0;
               
               while(st.hasMoreTokens()) {
                  //토큰화된 단어 배열에 넣기
                  testWord[i] = st.nextToken();
                  //특수문자제거
                  getSTRFilter(testWord[i]);      
                  i++;                  
                                    
               }
               k=0;
                VerbLemmatizer VerbLemmatizer = new VerbLemmatizer();
                for (String wo : testWord ) {
                   testWord[k]=VerbLemmatizer.lemmatize(wo);    
                   System.out.println(testWord[k]);
                   k++;
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
               
               int emotion=0;
                     
               if(avgValance>=5 && avgArousal>=5) {
                  System.out.println("Happy / Exciting");
                  emotion=1;
               } else if (avgValance<5 && avgArousal>=5) {
                  System.out.println("Anxious / Angry");
                  emotion=2;
               } else if (avgValance<5 && avgArousal<5) {
                  System.out.println("Sad / Depressed");
                  emotion=3;                  
               } else if (avgValance>=5 && avgArousal<5){
                     System.out.println("Relaxed / Calm");
                     emotion=4;
                  } else if ((avgValance==5 && avgArousal==5)||(avgValance==0 && avgArousal==0)){
                     System.out.println("nothing");
                     emotion=0;
                     noCount++;
                  }
               
               
               try {
                  int e1 = Integer.parseInt(sentence[row][0]);
                  int e2 = Integer.parseInt(sentence[row][1]);
                  if(emotion==e1) {
                     countMatch++;
                     System.out.println("correct! " + e1);
                  } else if(emotion==e2){
                     countMatch++;
                     System.out.println("correct! " + e2);
                  } 
                  else if(emotion==0){
                     countMatch++;
                     System.out.println("correct! " + emotion);
                  }
                  else {
                     System.out.println("wrong!");
                  }
               
               }catch(NumberFormatException e) {
                  
               }
               catch(Exception e) {
                  
               }            
            }
            
            double probability = countMatch / (300.0-noCount) * 100;
            
            System.out.print("Probability: ");
            System.out.println(probability);            
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
}