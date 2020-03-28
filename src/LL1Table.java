import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ltt
 * @date 2020/3/24 21:23
 */
public class LL1Table {
    public static void main(String[] args) throws IOException {
        Syntax Cgramma = new Syntax();
        CreateLList(Cgramma);

        Iterator iter1 = Cgramma.First.keySet().iterator();
        String key1 =null;
        Set<String> value1=null;
        while(iter1.hasNext()){
            key1 =(String) iter1.next();
            value1 =Cgramma.First.get(key1);
            System.out.println("First("+key1+") = "+value1 );
        }

        Iterator iter2 = Cgramma.Follow.keySet().iterator();
        String key2 =null;
        Set<String> value2=null;
        while(iter2.hasNext()){
            key2 =(String) iter2.next();
            value2 =Cgramma.Follow.get(key2);
            System.out.println("Follow("+key2+") = "+value2 );
        }


        Iterator iter = Cgramma.LL1List.keySet().iterator();
        Pair key=null;
        Integer value=null;
        while (iter.hasNext()){
            key=(Pair)iter.next();
            value =(Integer) Cgramma.LL1List.get(key);
            if(value!=-1)
            System.out.println(key+"  "+Cgramma.prod[value].left+"->"+Cgramma.prod[value].right);
        }

        for(int i=0;i<Cgramma.size_of_prod;i++){
            System.out.println(Cgramma.prod[i].left);
        }

        System.out.println("PAUSE");
    }
    public static void preProcess(Syntax Cgramma) throws IOException{
        String line;
        File filename=new File("Cgrammer.txt");
        InputStreamReader reader=new InputStreamReader(new FileInputStream(filename));
        BufferedReader bfr= new BufferedReader(reader);
        line = bfr.readLine();

        int prod_index=0;
        while(line!=null){
            char[] chars = line.toCharArray();
            int ptr_of_rules=0;// 一行文法规则内每个字符的访问指针
            Cgramma.prod[prod_index]=new Syntax.production();
            while((chars[ptr_of_rules]==' ') || (chars[ptr_of_rules]=='\t')) ptr_of_rules++;
            if(chars[ptr_of_rules]==';'){
                line=bfr.readLine();
                continue;
            }
            if(Character.isLowerCase(chars[ptr_of_rules]) || Character.isUpperCase(chars[ptr_of_rules])){
                String VnTemp="";//临时变量，存储非终结符
                while(chars[ptr_of_rules] !=' ' && chars[ptr_of_rules]!='\t'){
                    VnTemp += chars[ptr_of_rules];//记录非终结符
                    ptr_of_rules++;
                }
                Cgramma.non_terminators.add(VnTemp);//将该非终结符加入到非终结符集合中
                Cgramma.is_deduced_epsilon.put(VnTemp,false);//能不能推出空字
                Cgramma.prod[prod_index].left=VnTemp;
                Cgramma.size_of_prod++;
                while(chars[ptr_of_rules]==' ' || chars[ptr_of_rules]=='\t' || chars[ptr_of_rules]==':'){
                    ptr_of_rules++; // 滤掉冒号和空白符
                }
                while (ptr_of_rules<chars.length){
                    String temp="";//存储终结符的临时变量
                    // \'代表当前符号是一个单引号字符，表示出现了终结符
                    if(chars[ptr_of_rules]=='\''){
                        ptr_of_rules++;//略去单引号
                        while(chars[ptr_of_rules]!= '\''){
                            temp += chars[ptr_of_rules];//记录终结符
                            ptr_of_rules++;
                        }
                        Cgramma.terminators.add(temp); // 将该终结符记录在终结符表中
                        Set<String> tempset = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                        tempset.add(temp);
                        Cgramma.First.put(temp, tempset);
                        Cgramma.prod[prod_index].right.add(temp);//产生式右部
                        ptr_of_rules += 2; // 略去终结符后面的单引号和空格
                    }else { // 当前符号是一个非终结符或是一个语义动作标志
                        while (ptr_of_rules < chars.length && chars[ptr_of_rules] != ' ') {
                            temp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        } // 拼出一个非终结符
                        ptr_of_rules++; // 略去文法符号之间的空格
                        Cgramma.prod[prod_index].right.add(temp); // 将该文法符号或语义动作符号存到产生式右部vector中
                        if (temp.equals("epsilon")) {
                            // 该产生式左部的非终结符可以直接推空
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[prod_index].left, true);
                        }
                    }

                }
                prod_index++;
                line = bfr.readLine();

            }else if(chars[ptr_of_rules] == '|'){
                //一个非终结符推出其他产生式
                ptr_of_rules += 2; //略去'|'和空格
                Cgramma.prod[prod_index].left = Cgramma.prod[prod_index - 1].left; //该产生式左部必定和上一个产生式是一样的
                Cgramma.size_of_prod++;
                while (ptr_of_rules < chars.length) { //记录产生式右部符号，填写终结符集合
                    //一直走到该行结束
                    String temp = ""; //拼出右部一个符号
                    if (chars[ptr_of_rules] == '\'') {
                        //当前出现了终结符，需要记录该终结符
                        ptr_of_rules++;//略过单引号
                        while (chars[ptr_of_rules] != '\'') {
                            temp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        }
                        Cgramma.terminators.add(temp); //将该终结符记录在终结符表中
                        Set<String> tmpset = new HashSet<String>();
                        tmpset.add(temp);
                        Cgramma.First.put(temp, tmpset);
                        Cgramma.prod[prod_index].right.add(temp); //产生式右部记录该符号
                        ptr_of_rules += 2; //略去终结符后面的单引号和空格
                    } else { //当前符号是一个非终结符或语义动作标志
                        while (ptr_of_rules < chars.length && chars[ptr_of_rules] != ' ') {
                            temp += chars[ptr_of_rules];
                            ptr_of_rules++;
                        }//拼出一个非终结符
                        ptr_of_rules++; //略去文法符号之间的空格
                        Cgramma.prod[prod_index].right.add(temp); //将该文法符号存到产生式右部vector中
                        if (temp.equals("epsilon")) {
                            //该产生式左部的非终结符可以直接推空
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[prod_index].left, true);
                        }
                    }
                }
                prod_index++;
                line = bfr.readLine();

            }
        }
        Set<String> Temp1 = new HashSet<String>(); // 临时集合，用于存储终结符的First集
        Cgramma.terminators.add("int_const"); //额外添加几个终结符
        Temp1.add("int_const");
        Cgramma.First.put("int_const", Temp1);

        Set<String> Temp2 = new HashSet<String>();
        Cgramma.terminators.add("char_const");
        Temp2.add("char_const");
        Cgramma.First.put("char_const", Temp2);

        Set<String> Temp3 = new HashSet<String>();
        Cgramma.terminators.add("id");
        Temp3.add("id");
        Cgramma.First.put("id", Temp3);

        Set<String> Temp4 = new HashSet<String>();
        Cgramma.terminators.add("float_const");
        Temp4.add("float_const");
        Cgramma.First.put("float_const", Temp4);

        Set<String> Temp5 = new HashSet<String>();
        Cgramma.terminators.add("string");
        Temp5.add("string");
        Cgramma.First.put("string", Temp5);

        reader.close();
    }

    /**
     * 正则表达式
     *
     */
    public static Boolean IsUpper(String str) {
        Pattern pattern = Pattern.compile("[A-Z_0-9]+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) return true;
        else
            return false;
    }
    public static void dataRevision(Syntax Cgramma){
        for(int i=Cgramma.size_of_prod-1;i >= 0;i--){
            if(!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].left)){//原可推空标志为false
                int index_of_right=0;//产生式右部访问指针
                while(index_of_right < Cgramma.prod[i].right.size() && (Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_of_right)) ||
                        IsUpper(Cgramma.prod[i].right.get(index_of_right)) ) ){
                    //如果产生式右端为非终结符或语义动作标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_of_right))) index_of_right++; //语义动作标志直接跳过
                    else { //是非终结符
                        if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_of_right))) {
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, false);
                            break;
                        } else index_of_right++;
                    }
                }
                if(index_of_right >= Cgramma.prod[i].right.size()){//产生式右端所有符号都可以推空
                    Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, true);
                    Set<String> Temp = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                    Temp.add("epsilon");
                    Cgramma.First.put(Cgramma.prod[i].left, Temp);
                }

            }
        }
        //再正着扫一遍
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].left)) { //原可推空标志为false
                int index_of_right = 0;
                while (index_of_right < Cgramma.prod[i].right.size() &&
                        (Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_of_right))
                                || IsUpper(Cgramma.prod[i].right.get(index_of_right)))) {
                    //如果产生式右端为非终结符或语义动作标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_of_right))) index_of_right++; //语义动作标志直接跳过
                    else { //是非终结符
                        if (!Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_of_right))) {
                            Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, false);
                            break;
                        } else index_of_right++;
                    }
                }
                if (index_of_right >= Cgramma.prod[i].right.size()) { //产生式右端所有符号都可以推空
                    Cgramma.is_deduced_epsilon.put(Cgramma.prod[i].left, true);
                    Set<String> Temp = new HashSet<String>(); // 临时集合，用于存储终结符的First集
                    Temp.add("epsilon");
                    Cgramma.First.put(Cgramma.prod[i].left, Temp);
                }
            }
        }
    }
    public static void First(String curSymbol,Syntax Cgramma){
        Cgramma.visited.put(curSymbol,true);//置访问标记
        for(int index_prod = 0; index_prod < Cgramma.size_of_prod;index_prod++){//遍历所有产生式
            if(Cgramma.prod[index_prod].left.equals(curSymbol)){//找到当前符号对应的产生式
                int index_right=0;//产生式右部符号集合的指针
                if (IsUpper(Cgramma.prod[index_prod].right.get(index_right))) index_right++; //如果右部首符号是一个语义标志，跳过他
                if (Cgramma.non_terminators.contains(Cgramma.prod[index_prod].right.get(index_right))){//如果产生式右部首符号是非终结符
                    List<String> tmpvec = new ArrayList<String>();
                    tmpvec.add(Cgramma.prod[index_prod].right.get(index_right)); //格式转换 string -> vector<string>
                    Cgramma.tmpStorage.put(Cgramma.prod[index_prod].left, tmpvec); //暂存之
                    if (!Cgramma.visited.get(Cgramma.prod[index_prod].right.get(index_right))) //如果该符号未被访问过!!!!!!!!
                        First(Cgramma.prod[index_prod].right.get(index_right), Cgramma); //获取该符号的first集合
                    Set<String> t_set = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)))
                            t_set.add(str);
                    }
                    if (Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                        //如果该符号可以推空  First type is: map<string, set<string>>
                        t_set.remove("epsilon");
                    }
                    Set<String> f_set = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                            f_set.add(str);
                    }
                    if (f_set == null) {
                        Cgramma.First.put(Cgramma.prod[index_prod].left, t_set); //合并到左部符号的first集合
                    } else {
                        f_set.addAll(t_set);
                        Cgramma.First.put(Cgramma.prod[index_prod].left, f_set); //合并到左部符号的first集合
                    }
                    boolean bool = false;
                    while (bool || Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                        //如果该产生式右部首符号可以推空
                        index_right++; //向后走一个，看下一个符号
                        bool = false;
                        if (index_right < Cgramma.prod[index_prod].right.size()) {    //如果右部该符号后面还有符号
                            if (IsUpper(Cgramma.prod[index_prod].right.get(index_right))) {
                                //遇到语义动作标志,跳过
                                bool = true;
                            } else if (Cgramma.non_terminators.contains(Cgramma.prod[index_prod].right.get(index_right))) {
                                //如果后面这个符号也是非终结符
                                tmpvec.add(Cgramma.prod[index_prod].right.get(index_right));
                                ;
                                Cgramma.tmpStorage.put(Cgramma.prod[index_prod].left, tmpvec); //暂存之
                                if (!Cgramma.visited.get(Cgramma.prod[index_prod].right.get(index_right))) //如果该符号未被访问过!!!!!!!!
                                    First(Cgramma.prod[index_prod].right.get(index_right), Cgramma); //获取该符号的first集合
                                //else { //如果该符号已被访问过
                                Set<String> set1 = new HashSet<String>();//
                                if (Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)) != null) {
                                    for (String str : Cgramma.First.get(Cgramma.prod[index_prod].right.get(index_right)))
                                        set1.add(str);
                                }
                                if (Cgramma.is_deduced_epsilon.get(Cgramma.prod[index_prod].right.get(index_right))) {
                                    //如果该符号可以推空  First type is: map<string, set<string>>
                                    set1.remove("epsilon");
                                }
                                Set<String> set2 = new HashSet<String>();//
                                if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                                    for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                                        set2.add(str);
                                }
                                if (set2 == null) {
                                    Cgramma.First.put(Cgramma.prod[index_prod].left, set1);
                                } else {
                                    set2.addAll(set1);
                                    Cgramma.First.put(Cgramma.prod[index_prod].left, set2);
                                }
                            } else {
                                Set<String> set3 = new HashSet<String>();
                                set3.add(Cgramma.prod[index_prod].right.get(index_right));
                                Cgramma.First.put(Cgramma.prod[index_prod].left, set3);
                                break;
                                //后面这个符号是终结符，存入左部符号first集合中
                            }
                        } else break;
                    }
                }
                else{//如果产生式右部首字符为终结符或者空字，将之加入左部符号的first集中
                    Set<String> temp = new HashSet<String>();//
                    if (Cgramma.First.get(Cgramma.prod[index_prod].left) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[index_prod].left))
                            temp.add(str);
                    }
                    if (temp != null) {
                        temp.add(Cgramma.prod[index_prod].right.get(index_right));
                        Cgramma.First.put(Cgramma.prod[index_prod].left, temp);
                    } else {
                        Set<String> _Set = new HashSet<String>();
                        _Set.add(Cgramma.prod[index_prod].right.get(index_right));
                        Cgramma.First.put(Cgramma.prod[index_prod].left, _Set);
                    }
                }
            }
        }

    }

    public static void Follow(String curSymbol,Syntax Cgramma){
        Cgramma.visited.put(curSymbol,true);
        if (curSymbol.equals(Cgramma.prod[0].left)) { //如果当前符号为开始符号，且该符号的follow集还未建立
            Set<String> _set = new HashSet<String>();
            _set.add("#");
            Cgramma.Follow.put(curSymbol, _set);
        }
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            //遍历产生式组
            int index_right = 0;
            int it = 0;//迭代器索引
            boolean bool = false, equal = false;
            Iterator iter = Cgramma.prod[i].right.iterator();
            String next = "";
            for (; iter.hasNext(); it++) {//遍历产生式的右部集合，找到当前的符号
                next = (String) iter.next();
                if (next.equals(curSymbol)) break;
            }
            while (!equal && it < Cgramma.prod[i].right.size()) { //如果该产生式右部可以找到待求符号
                index_right = it; //迭代器转换为索引值
                index_right++;
                if (index_right < Cgramma.prod[i].right.size() && IsUpper(Cgramma.prod[i].right.get(index_right)))
                    index_right++; //该符号后面是一个语义标志，跳过
                if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                    Set<String> _set = new HashSet<String>();
                    _set = Cgramma.Follow.get(curSymbol);
                    if (_set == null) {
                        Set<String> _Set = new HashSet<String>();
                        _Set.add(Cgramma.prod[i].right.get(index_right));
                        Cgramma.Follow.put(curSymbol, _Set);
                    } else {
                        _set.add(Cgramma.prod[i].right.get(index_right));
                        Cgramma.Follow.put(curSymbol, _set);
                    }
                    break;
                }
                while (index_right < Cgramma.prod[i].right.size() && (IsUpper(Cgramma.prod[i].right.get(index_right)) ||
                        Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right)))) {
                    //如果产生式右部该非终结符后面还有符号 且后面的符号可以推空或者后面是一个语义标志
                    if (IsUpper(Cgramma.prod[i].right.get(index_right))) {
                        index_right++;
                        if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                            Set<String> _set = new HashSet<String>();
                            _set = Cgramma.Follow.get(curSymbol);
                            if (_set == null) {
                                Set<String> _Set = new HashSet<String>();
                                _Set.add(Cgramma.prod[i].right.get(index_right));
                                Cgramma.Follow.put(curSymbol, _Set);
                            } else {
                                _set.add(Cgramma.prod[i].right.get(index_right));
                                Cgramma.Follow.put(curSymbol, _set);
                            }
                            bool = true;
                            break;
                        }
                    } //遇到语义动作标志，则跳过
                    Set<String> tmp = new HashSet<String>();
                    if (Cgramma.First.get(Cgramma.prod[i].right.get(index_right)) != null) {
                        for (String str : Cgramma.First.get(Cgramma.prod[i].right.get(index_right))) {
                            tmp.add(str);
                        }
                    }
                    tmp.remove("epsilon");
                    Cgramma.Follow.put(Cgramma.prod[i].right.get(it), tmp);
                    index_right++;
                    if (index_right < Cgramma.prod[i].right.size() && Cgramma.terminators.contains(Cgramma.prod[i].right.get(index_right))) {
                        Set<String> _set = new HashSet<String>();
                        if (Cgramma.Follow.get(curSymbol) != null) {
                            for (String str : Cgramma.Follow.get(curSymbol)) {
                                _set.add(str);
                            }
                        }
                        if (_set == null) {
                            Set<String> _Set = new HashSet<String>();
                            _Set.add(Cgramma.prod[i].right.get(index_right));
                            Cgramma.Follow.put(curSymbol, _Set);
                        } else {
                            _set.add(Cgramma.prod[i].right.get(index_right));
                            Cgramma.Follow.put(curSymbol, _set);
                        }
                        bool = true;
                        break;
                    }
                }
                if (bool)
                    break;
                if (index_right < Cgramma.prod[i].right.size() && !Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right))) //如果右面遇到一个符号推不空了, 把它的First集也加进来
                {
                    Cgramma.Follow.put(Cgramma.prod[i].right.get(it), Cgramma.First.get(Cgramma.prod[i].right.get(index_right)));
                    for (; iter.hasNext(); it++) {
                        next = (String) iter.next();
                        if (next.equals(curSymbol)) break;
                    }
                }
                if (index_right >= Cgramma.prod[i].right.size()) //如果后面的符号全部可以推空
                {
                    equal = true;
                    if (!Cgramma.visited.get(Cgramma.prod[i].left))
                        Follow(Cgramma.prod[i].left, Cgramma); //没有访问过，访问之
                    Set<String> _Set = new HashSet<String>();
                    if (Cgramma.Follow.get(Cgramma.prod[i].right.get(it)) != null) {
                        for (String str : Cgramma.Follow.get(Cgramma.prod[i].right.get(it))) {
                            _Set.add(str);
                        }
                    }
                    //_Set.add(Cgramma.Follow.get(Cgramma.prod[i].right.get(it)));
                    if (Cgramma.Follow.get(Cgramma.prod[i].right.get(it)) == null) {
                        Cgramma.Follow.put(Cgramma.prod[i].right.get(it), Cgramma.Follow.get(Cgramma.prod[i].left));
                    } else {
                        Set<String> temp = new HashSet<String>();
                        if (Cgramma.Follow.get(Cgramma.prod[i].left) != null) {
                            for (String str : Cgramma.Follow.get(Cgramma.prod[i].left)) {
                                temp.add(str);
                            }
                        }
                        if (temp != null) {
                            for (String str : temp) {
                                _Set.add(str);
                            }
                        }
                        Cgramma.Follow.put(Cgramma.prod[i].right.get(it), _Set);
                    }
                }
            }
        }
    }

    public static void AllFollow(Syntax Cgramma) {
        for (String str : Cgramma.non_terminators) {
            Cgramma.visited.put(str, false);
        }
        for (String str : Cgramma.non_terminators) {
            if (!Cgramma.visited.get(str))
                Follow(str, Cgramma);
        }
    }

    public static void AllFirst(Syntax Cgramma) {
        for (String str : Cgramma.non_terminators) {
            Cgramma.visited.put(str, false);
        }
        for (String str : Cgramma.non_terminators) {
            if (!Cgramma.visited.get(str))
                First(str, Cgramma);
        }
    }

    public static Set<String> int_first(List<String> alpha, Syntax Cgramma) {
        int index_right = 0;
        if (IsUpper(alpha.get(index_right))) index_right++; //首符为语义动作标志，跳过
        if (alpha.get(index_right).equals("epsilon") || Cgramma.terminators.contains(alpha.get(index_right))
                || (Cgramma.non_terminators.contains(alpha.get(index_right)) && !Cgramma.is_deduced_epsilon.get(alpha.get(index_right)))) {
            //如果产生式右部首符号是ε 或 终结符 或 不可以推空的非终结符
            Set<String> tmpset = new HashSet<String>();
            if (alpha.get(index_right).equals("epsilon")) { //如果某个产生式右部仅有一个ε
                tmpset.add("epsilon");
                return tmpset;
            }
            return Cgramma.First.get(alpha.get(index_right));
        } else {//产生式右部首符号是可以推空的非终结符
            Set<String> tmpset = new HashSet<String>();
            while (index_right < alpha.size() &&
                    ((Cgramma.non_terminators.contains(alpha.get(index_right)) &&
                            Cgramma.is_deduced_epsilon.get(alpha.get(index_right))) || IsUpper(alpha.get(index_right)))) {
                //如果产生式右部是可以推空的非终结符
                if (IsUpper(alpha.get(index_right))) {
                    //遇到语义动作符号，跳过（直接匹配可以在一定程度上提高效率，减少程序跳转）
                    index_right++;
                    continue;
                }
                if (Cgramma.First.get(alpha.get(index_right)) != null) {
                    for (String str : Cgramma.First.get(alpha.get(index_right)))
                        tmpset.add(str);
                }
                tmpset.remove("epsilon");
                index_right++;
            }
            if (index_right >= alpha.size()) {
                //右部全可以推空
                tmpset.remove("epsilon");
            } else { //遇到了无法推空的符号
                if (Cgramma.First.get(alpha.get(index_right)) != null) {
                    for (String str : Cgramma.First.get(alpha.get(index_right)))
                        tmpset.add(str);
                }
            }
            return tmpset;
        }
    }

    public static void AllSelect(Syntax Cgramma) {

        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            //遍历产生式集合
            //bool is_all_epsilon = false;
            int index_right = 0;
            while (index_right < Cgramma.prod[i].right.size()
                    && ((Cgramma.non_terminators.contains(Cgramma.prod[i].right.get(index_right))
                    && Cgramma.is_deduced_epsilon.get(Cgramma.prod[i].right.get(index_right)))
                    || Cgramma.prod[i].right.get(0).equals("epsilon") || IsUpper(Cgramma.prod[i].right.get(index_right)))) {
                //如果该产生式右部首符号为非终结符 且 可以推空
                index_right++;
            }
            if (index_right >= Cgramma.prod[i].right.size()) //如果产生式右部符号全部可以推空, alpha =>* epsilon
            {
                Set<String> tmpset = new HashSet<String>();
                Set<String> first = new HashSet<String>();
                Set<String> follow = new HashSet<String>();
                if (int_first(Cgramma.prod[i].right, Cgramma) != null) {
                    for (String str : int_first(Cgramma.prod[i].right, Cgramma)) {
                        first.add(str);
                    }
                }
                first.remove("epsilon");
                if (Cgramma.Follow.get(Cgramma.prod[i].left) != null) {
                    for (String str : Cgramma.Follow.get(Cgramma.prod[i].left)) {
                        follow.add(str);
                    }
                }
                if (follow != null)
                    first.addAll(follow);
                tmpset.addAll(first);
                Cgramma.Select.put(i, tmpset);
            } else { //alpha ≠>* epsilon
                Cgramma.Select.put(i, int_first(Cgramma.prod[i].right, Cgramma));
            }
        }
    }
    public static int list_of_LL1(String A, String a, Syntax Cgramma) {
        for (int i = 0; i < Cgramma.size_of_prod; i++) {
            if (Cgramma.prod[i].left.equals(A) && Cgramma.Select.get(i).contains(a)) {
                return i;
            }
        }
        return -1;
    }

    public static void CreateLList(Syntax Cgramma) throws IOException {
        preProcess(Cgramma);
        dataRevision(Cgramma);
        AllFirst(Cgramma);
        AllFollow(Cgramma);
        AllSelect(Cgramma);
        Set<String> tem = new HashSet<String>();
        Cgramma.terminators.add("#");
        Iterator iter1 = Cgramma.non_terminators.iterator();
        for (; iter1.hasNext(); ) {
            String str1 = (String) iter1.next();
            Iterator iter2 = Cgramma.terminators.iterator();
            for (; iter2.hasNext(); ) {
                String str2 = (String) iter2.next();
                Pair<String, String> _pair = new Pair<String, String>(str1, str2);
                Cgramma.LL1List.put(_pair, list_of_LL1(str1, str2, Cgramma));
            }
        }
    }

}
