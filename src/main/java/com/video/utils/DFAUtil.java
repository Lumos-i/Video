package com.video.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DFAUtil {
    private HashMap<String, Object> dfaMap=new HashMap<>();

    private static HashMap<Boolean,String> result=new HashMap<>();

    private static final int minMatchType=1;

    private static final int maxMatchType=2;

    public DFAUtil() throws IOException {
        begin();
    }

    /**set作为敏感词，创建出对应的dfa的Map，以供检验敏感词
     * @param set
     */
    public void createDFAHashMap(Set<String> set){
        HashMap<String, Object> nowMap;
        for(String key:set){
            //对每个字符串最初，nowMap就是dfaMap
            nowMap=dfaMap;
            for(int i=0;i<key.length();i++){
                //一个个字符循环
                String nowChar=String.valueOf(key.charAt(i));
                //根据nowChar得到nowMap里面对应的value
                HashMap<String, Object> map=(HashMap<String, Object>)nowMap.get(nowChar);
                //如果map为空，则说明nowMap里面没有以nowChar开头的东西，则创建一个新的hashmap，
                //以nowChar为key，新的map为value，放入nowMap
                if(map==null){
                    map=new HashMap<>();
                    nowMap.put(nowChar, map);
                }
                //nowMap=map，就是nowChar对应的对象
                nowMap=map;
                //最后在nowMap里设置isEnd
                //如果nowMap里面已经有isEnd，并且为1，说明以前已经有关键字了，就不再设置isEnd
                //因为如果没有这一步，大中华和大中华帝国，先设置大中华
                //在大中华帝国设置的时候，华对应的map有isEnd=1，如果这时对它覆盖，就会isEnd=0，导致大中华这个关键字失效
                if(nowMap.containsKey("isEnd") && nowMap.get("isEnd").equals("1")){
                    continue;
                }
                if(i!=key.length()-1){
                    nowMap.put("isEnd", "0");
                }else{
                    nowMap.put("isEnd", "1");
                }
            }
        }
        System.out.println(dfaMap);
    }

    /** 用创建的dfaMap，根据matchType检验字符串string是否包含敏感词，返回包含所有对于敏感词的set
     * @param string 要检查是否有敏感词在内的字符串
     * @param matchType 检查类型，如大中华帝国牛逼对应大中华和大中华帝国两个关键字，1为最小检查，会检查出大中华，2位最大，会检查出大中华帝国
     * @return
     */
    public HashMap<Boolean,String> getSensitiveWordByDFAMap(String string,int matchType){
        StringBuilder stringBuilder=new StringBuilder(string);
        boolean on=false;
        for(int i=0;i<string.length();i++){
            //matchType是针对同一个begin的后面，在同一个begin匹配最长的还是最短的敏感词
            int length=getSensitiveLengthByDFAMap(string,i,matchType);
            if(length>0){
                on=true;
                for (int j = 0; j < length; j++) {
                    stringBuilder.setCharAt(i+j,'*');
                }
                i=i+length-1;
            }
        }
        result.clear();
        result.put(on,stringBuilder.toString());
        return result;
    }

    /**如果存在，则返回敏感词字符的长度，不存在返回0
     * @param string
     * @param beginIndex
     * @param matchType  1：最小匹配规则，2：最大匹配规则
     * @return
     */
    public int getSensitiveLengthByDFAMap(String string,int beginIndex,int matchType){
        //当前匹配的长度
        int nowLength=0;
        //最终匹配敏感词的长度，因为匹配规则2，如果大中华帝，对应大中华，大中华帝国，在华的时候，nowLength=3，因为是最后一个字，将nowLenth赋给resultLength
        //然后在帝的时候，now=4，result=3，然后不匹配，resultLength就是上一次最大匹配的敏感词的长度
        int resultLength=0;
        HashMap<String, Object> nowMap=dfaMap;
        for(int i=beginIndex;i<string.length();i++){
            String nowChar=String.valueOf(string.charAt(i));
            //根据nowChar得到对应的map，并赋值给nowMap
            nowMap=(HashMap<String, Object>)nowMap.get(nowChar);
            //nowMap里面没有这个char，说明不匹配，直接返回
            if(nowMap==null){
                break;
            }else{
                nowLength++;
                //如果现在是最后一个，更新resultLength
                if("1".equals(nowMap.get("isEnd"))){
                    resultLength=nowLength;
                    //如果匹配模式是最小，直接匹配到，退出
                    //匹配模式是最大，则继续匹配，resultLength保留上一次匹配到的length
                    if(matchType==minMatchType){
                        break;
                    }
                }
            }
        }
        return resultLength;
    }

    public void begin() throws IOException {
        Set<String> set=new HashSet<>();
        BufferedReader br=null;
        try {
            String path= System.getProperty("user.dir") + "/word.txt";
            br = new BufferedReader
                    (new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String s;
            while ((s = br.readLine()) != null) {
                set.add(s);
            }
            createDFAHashMap(set);
            log.info("初始化敏感词文件成功");
        }catch (Exception e){
            log.info("初始化敏感词文件出错");
        }finally {
            if(br!=null)
            br.close();
        }
    }
}
