import java.io.FileReader;
import java.util.*;
import java.io.IOException;
import java.io.BufferedReader;

/**
 * @author ltt
 * @date 2020/3/21 20:25
 */
public class lexAnalysis {
    static char SingleOP[]={'+','-','*','/','>','<','=','%','!',',','[',']','#','&','\'','\"'};//单目操作符
    static String DoubleOP[]={"++","--","==","<=",">=","+=","-=","*=","/="};//双目操作符
    static char DL[]={'{','}',';','(',')'};//界符
    static String KeyWord[]={"printf","scanf","if","else","while","int","switch","break","float","string","return","struct","for","void","char","double"};//关键字
    public List<String> ID =new ArrayList<String>();//存储标识符
    public List<String> C =new ArrayList<String>();//存储字符
    public List<String> S =new ArrayList<String>();//存储字符串
    public List<String> Number =new ArrayList<String>();//存储数字
    public List<String> OP =new ArrayList<String>();//存储符号
    public List<String> DouOP =new ArrayList<String>();//存储双目运算符
    public List<Map> lexResult=new ArrayList<Map>();//存储词法分析结果

    /**读取源程序
    * @return List<String>
     */
    public List<String> readFile(String fileName) throws IOException{
        List<String> fileList = new ArrayList<String>();
        FileReader fread =new FileReader(fileName);
        BufferedReader bfr=new BufferedReader(fread);
        String line =bfr.readLine();
        int i=1;
        while (line!=null){
            //System.out.println("第"+i+"行"+line);
            fileList.add(line);
            line=bfr.readLine();
            i++;
        }
        bfr.close();
        fread.close();
        return  fileList;
    }
    /**判断是不是关键字
     * @return int
    */
    public int isKeyWord(String word) {
        int isKW = -1;
        switch (word) {
            case "printf":
                isKW = 0;
                break;

            case "scanf":
                isKW = 1;
                break;

            case "if":
                isKW = 2;
                break;

            case "else":
                isKW = 3;
                break;

            case "while":
                isKW = 4;
                break;

            case "int":
                isKW = 5;
                break;

            case "switch":
                isKW = 6;
                break;

            case "break":
                isKW = 7;
                break;

            case "float":
                isKW = 8;
                break;

            case "string":
                isKW = 9;
                break;

            case "return":
                isKW = 10;
                break;

            case "struct":
                isKW = 11;
                break;

            case "for":
                isKW = 12;
                break;

            case "void":
                isKW = 13;
                break;
            case "char":
                isKW = 14;
                break;

            case "double":
                isKW = 15;
                break;
        }
        return isKW;
    }

    /**
     * 判断是不是单目操作符
     * @param opt
     * @return
     */
    public int isOpt(char opt) {
        //返回1表示是关键字  返回0不是关键字
        int isOP = -1;
        switch (opt) {
            case '+'://如果处理的单词是+
                isOP = 0;
                break;

            case '-'://如果处理的单词是-
                isOP = 1;
                break;

            case '*'://如果处理的单词是*
                isOP = 2;
                break;

            case '/'://如果处理的单词是/
                isOP = 3;
                break;

            case '>'://如果处理的单词是>
                isOP = 4;
                break;

            case '<'://如果处理的单词是<
                isOP = 5;
                break;

            case '='://如果处理的单词是=
                isOP = 6;
                break;

            case '%'://如果处理的单词是%
                isOP = 7;
                break;

            case '!'://如果处理的单词是!
                isOP = 8;
                break;

            case ';'://如果处理的单词是;
                isOP = 9;
                break;

            case '('://如果处理的单词是;
                isOP = 10;
                break;

            case ')'://如果处理的单词是;
                isOP = 11;
                break;

            case '{'://如果处理的单词是;
                isOP = 12;
                break;

            case '}'://如果处理的单词是;
                isOP = 13;
                break;

            case ','://如果处理的单词是，
                isOP = 14;
                break;

            case '['://如果处理的单词是[
                isOP = 15;
                break;

            case ']'://如果处理的单词是[
                isOP = 16;
                break;

            case '#'://如果处理的单词是[
                isOP = 17;
                break;
            case '&'://如果处理的单词是[
                isOP = 18;
                break;
            case '"'://如果处理的单词是[
                isOP = 19;
                break;
            case '\''://如果处理的单词是[
                isOP = 20;
                break;
        }
        return isOP;
    }

    /**
     * 判断是不是双目操作符
     * @param word
     * @return
     */
    public int isDoubleOpt(String word) {
        int isDoubleOpt = -1;
        switch (word) {
            case "++":
                isDoubleOpt = 0;
                break;

            case "--":
                isDoubleOpt = 1;
                break;
            case "+=":
                isDoubleOpt = 2;
                break;

            case "-=":
                isDoubleOpt = 3;
                break;
            case "*=":
                isDoubleOpt = 4;
                break;

            case "/=":
                isDoubleOpt = 5;
                break;

            case ">=":
                isDoubleOpt = 6;
                break;

            case "<=":
                isDoubleOpt = 7;
                break;

            case "==":
                isDoubleOpt = 8;
                break;
            case "%d":
                isDoubleOpt = 9;
                break;
            case "%s":
                isDoubleOpt = 10;
                break;
            case "%c":
                isDoubleOpt = 11;
                break;
        }
        return isDoubleOpt;
    }

    public boolean isANum(String word) {
        int isFloat = 0;//用来记录是不是小数，如果有两个小数点则表明是数据有误
        for (int i = 1; i < word.length(); i++) {
            //是数字的情况
            if (word.charAt(i) >= '0' && word.charAt(i) <= '9') {
                continue;
            } else if (word.charAt(i) == '.') {
                if (isFloat == 0) {//如果之前没出现过小数点
                    isFloat++;
                    continue;
                } else {
                    return false;
                }//出现过小数点后又出现了一遍则直接报错
            } else {
                return false;
            }//出现除数字和小数点之外的符号
        }
        return true;
    }
    /**
     * 核心代码：词法分析
     * @throws IOException
     */
    public void LexAnaly() throws IOException{
        List<String> fileList=this.readFile("C.txt");
        String firstType;
        String word;
        for (String e: fileList) {
            for (int i=0;i<e.length();i++){
                if(e.charAt(i)==' ')
                    continue;
                else if((e.charAt(i)>='a' && e.charAt(i)<='z')||(e.charAt(i)>='A' && e.charAt(i)<='Z'))
                    firstType="KeyOrID";//可能是关键字或标识符
                else if(e.charAt(i)>='0' && e.charAt(i)<='9')
                    firstType="Number";//可能是数字
                else if(e.charAt(i)=='"')
                    firstType="StringOr%A";//可能是字符串或者%A
                else if(e.charAt(i)=='\'')
                    firstType="Char";//可能是字符
                else if(e.charAt(i)=='/'){
                    if(i<e.length()-1 && e.charAt(i+1)=='/')
                        firstType="Comment";//可能是注释
                    else firstType="Opt";//可能是操作符

                }
                else firstType="Opt";//可能是操作符

                if(firstType=="KeyOrID"){
                    Map<String,String> result =new HashMap<String,String>();//存储结果
                    //先判断是不是关键字，再判断是不是标识符
                    int type = 1;//1表示关键字，2表示ID，-1表示报错
                    int j=i;
                    for(;j<e.length();j++){
                        if ((e.charAt(j) >= '0' && e.charAt(j) <= '9') || e.charAt(j) == '_' || e.charAt(j) == '$') {
                            //出现数字以及_ $说明很可能是ID
                            type = 2;
                        } else if (e.charAt(j) == ' ' || e.charAt(j) == '(' || e.charAt(j) == '{' || e.charAt(j) == '+' || e.charAt(j) == '-' || e.charAt(j) == '*' || e.charAt(j) == '/' ||
                                e.charAt(j) == '%' || e.charAt(j) == '=' || e.charAt(j) == '>' || e.charAt(j) == '<' || e.charAt(j) == ')' || e.charAt(j) == '}' || e.charAt(j) == ';' ||
                                e.charAt(j) == ',' || e.charAt(j) == '[' || e.charAt(j) == '#') {
                            //遇到空格或者界符就说明单词读完了
                            break;
                        } else if ((e.charAt(j) >= 'a' && e.charAt(j) <= 'z') || (e.charAt(j) >= 'A' && e.charAt(j) <= 'Z')) {
                            //读到字母则继续
                            continue;
                        } else {//否则出错
                            type = -1;
                        }
                    }
                    word=e.substring(i,j);
                    if(type == 1){
                        if (isKeyWord(word) != -1) {
                            result.put("type", "keyword");
                            result.put("index", Integer.toString(isKeyWord(word)));
                            result.put("value", word);
                            lexResult.add(result);
                        }
                        else if (isKeyWord(word) == -1) {//如果不是关键字
                            if (!ID.contains(word)) {
                                ID.add(word);
                            }
                            result.put("type", "id");
                            result.put("index", Integer.toString(ID.indexOf(word)));
                            result.put("value", word);
                            lexResult.add(result);
                        }

                    }
                    else if (type == 2) {
                        if (!ID.contains(word)) {
                            ID.add(word);
                        }
                        result.put("type", "id");
                        result.put("index", Integer.toString(ID.indexOf(word)));
                        result.put("value", word);
                        lexResult.add(result);
                    }
                    else if (type == -1) {
                        System.out.println(word + " 有误");
                    }
                    i=j-1;
                }
                else if(firstType=="Number"){
                    Map<String, String> result = new HashMap<String, String>();//用来存储结果
                    int j = i;
                    int type = 1;//-1有误
                    for (; j < e.length(); j++) {
                        if (e.charAt(j) == ';' || e.charAt(j) == ' ' || e.charAt(j) == '+' || e.charAt(j) == '-' || e.charAt(j) == '*' || e.charAt(j) == '/' ||
                                e.charAt(j) == '%' || e.charAt(j) == '>' || e.charAt(j) == '<' || e.charAt(j) == '=' || e.charAt(j) == ',' || e.charAt(j) == ']'
                                || e.charAt(j) == '#' || e.charAt(j) == ')' || e.charAt(j) == '(' || e.charAt(j) == '{' || e.charAt(j) == '}') {
                            break;
                        } else if (e.charAt(j) == '.') {
                            continue;
                        } else {
                            type = -1;
                        }
                    }
                    word = e.substring(i, j);//获取读取到的单词
                    if (isANum(word)) {
                        if (!Number.contains(word)) {
                            Number.add(word);
                        }
                        result.put("type", "num");
                        result.put("index", Integer.toString(Number.indexOf(word)));
                        result.put("value", word);
                        lexResult.add(result);
                    } else {
                        System.out.println(word + " 有误");
                    }
                    i = j - 1;
                }

                else if (firstType == "StringOr%A") {
                    Map<String, String> result = new HashMap<String, String>();//用来存储结果
                    int j = i + 1;
                        for (; j < e.length(); j++) {
                            if (e.charAt(j) == '"') {
                                break;
                            }
                        }
                        word = e.substring(i + 1, j);
                        if(isDoubleOpt(word)!=-1){
                            Map<String, String> result1 = new HashMap<String, String>();//用来存储结果
                            S.add("\"");
                            result1.put("type", "opt");
                            result1.put("index", Integer.toString(isOpt('"')));
                            result1.put("value", "\"" );
                            lexResult.add(result1);

                            DouOP.add(word);
                            result.put("type", "dopt");
                            result.put("index", Integer.toString(isDoubleOpt(word)));
                            result.put("value", word );

                            lexResult.add(result);
                            lexResult.add(result1);
                            i=j;
                        }else {
                            if (!S.contains(word)) {
                                S.add(word);
                            }
                            result.put("type", "string");
                            result.put("index", Integer.toString(S.indexOf(word)));
                            result.put("value", "\""+word+"\"");
                            lexResult.add(result);
                            i = j;
                        }
                }
                else if (firstType == "Char") {
                    Map<String, String> result = new HashMap<String, String>();//用来存储结果
                    int j = i + 1;
                    for (; j < e.length(); j++) {
                        if (e.charAt(j) == '\'') {
                            break;
                        }
                    }
                    word = e.substring(i + 1, j);
                    if (word.length() > 1) {
                        System.out.println(word + "是非法字符");
                    } else {
                        if (!C.contains(word)) {
                            C.add(word);
                        }
                        result.put("type", "char");
                        result.put("index", Integer.toString(C.indexOf(word)));
                        result.put("value", "'" +word+ "'");
                        lexResult.add(result);
                    }
                    i = j;
                }
                else if (firstType == "Opt") {
                    Map<String, String> result = new HashMap<String, String>();//用来存储结果
                    if (i < e.length() - 1) {
                        if ((e.charAt(i + 1) == '+' && e.charAt(i) == '+') || (e.charAt(i + 1) == '-' && e.charAt(i) == '-') ||(e.charAt(i + 1) == '=' && e.charAt(i) == '+')
                                ||(e.charAt(i + 1) == '=' && e.charAt(i) == '-')||(e.charAt(i + 1) == '=' && e.charAt(i) == '*') ||(e.charAt(i + 1) == '=' && e.charAt(i) == '/')
                                || (e.charAt(i + 1) == '=' && e.charAt(i) == '=')
                                || (e.charAt(i + 1) == '=' && (e.charAt(i) == '>' || e.charAt(i) == '<')) ) {
                            word = e.substring(i, i + 2);
                        } else {
                            word = e.substring(i, i + 1);
                        }
                    } else {
                        word = e.substring(i, i + 1);
                    }
                    if (isOpt(e.charAt(i)) != -1 && word.length() == 1) {
                        OP.add(word);
                        //result.put("opt",Integer.toString(isOpt(ele.charAt(i))));
                        result.put("type", "opt");
                        result.put("index", Integer.toString(isOpt(e.charAt(i))));
                        result.put("value", word);
                        lexResult.add(result);
                    } else if (word.length() == 2 && isDoubleOpt(word) != -1) {
                        DouOP.add(word);
                        result.put("type", "dopt");
                        result.put("index", Integer.toString(isDoubleOpt(word)));
                        result.put("value", word);
                        lexResult.add(result);
                        i++;

                    } else {
                        System.out.println(word + " 有误");
                    }
                }

            }
        }
        Map<String, String> TheLast = new HashMap<String, String>();
        TheLast.put("type", "opt");
        TheLast.put("index", "17");
        TheLast.put("value", "#");
        lexResult.add(TheLast);

    }
    public static void main(String[] args) throws IOException {
        lexAnalysis lex = new lexAnalysis();
        lex.LexAnaly();
        for (int i = 0; i < lex.lexResult.size(); i++) {
            System.out.println(lex.lexResult.get(i).get("value")+" := (" + lex.lexResult.get(i).get("type") + "," + lex.lexResult.get(i).get("index") + ")");
        }

    }
}
