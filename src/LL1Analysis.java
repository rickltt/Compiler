/**
 * @author ltt
 * @date 2020/3/25 23:56
 */
import java.io.IOException;
import java.util.Stack;

public class LL1Analysis {
    public static int i = 0;
    public static Boolean flag = true;

    public static boolean LL1_Analyse(Syntax Cgramma, lexAnalysis lex, SymbolTable Table) throws IOException {
        LL1Table.CreateLList(Cgramma);
        Stack<String> SYN = new Stack<String>();//分析栈
        SYN.push("#");
        SYN.push(Cgramma.prod[0].left);//文法开始符号
        int pos = 0;//扫描指针
        String curstate = "";
        int step=0;
        System.out.println("步骤\t\t分析栈\t\t余留符号串\t\t下一部动作\t\t产生式");
        while (!(SYN.peek().equals("#") && lex.lexResult.get(pos).get("value").equals("#"))) {
            curstate = SYN.peek();
            System.out.print(step++ +"\t\t"+SYN+"\t\t");
            for(int i=pos;i<lex.lexResult.size();i++){
                System.out.print(lex.lexResult.get(i).get("value"));
            }
            System.out.print("\t\t"+"弹出"+curstate+";");
            SYN.pop();
            //终结符
            if (Cgramma.terminators.contains(curstate)) {
                if ((curstate.equals("id") && lex.lexResult.get(pos).get("type").equals("id")) ||
                        (curstate.equals("int_const") && lex.lexResult.get(pos).get("type").equals("num")) ||
                        (curstate.equals("char_const") && lex.lexResult.get(pos).get("type").equals("char")) ||
                        (curstate.equals("string") && lex.lexResult.get(pos).get("type").equals("string")) ||
                        (curstate.equals(lex.lexResult.get(pos).get("value"))
                        )) {
                    pos++;
                } else {
                    return false;
                }
            }
            //非终结符
            else if (Cgramma.non_terminators.contains(curstate)) {
                int id_of_prod;
                if (lex.lexResult.get(pos).get("type").equals("id")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "id");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexResult.get(pos).get("type").equals("num")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "int_const");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexResult.get(pos).get("type").equals("char")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "char_const");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else if (lex.lexResult.get(pos).get("type").equals("string")) {
                    Pair<String, String> _pair = new Pair<String, String>(curstate, "string");
                    id_of_prod = Cgramma.LL1List.get(_pair);
                } else {
                    String curstr = (String) lex.lexResult.get(pos).get("value");
                    Pair<String, String> _pair = new Pair<String, String>(curstate, curstr);
                    id_of_prod = Cgramma.LL1List.get(_pair);
                }
                if (id_of_prod == -1) {
                    System.out.println("SYNTAX ERROR!");
                    return false;
                }
                if (id_of_prod != -1) {
                    if (Cgramma.prod[id_of_prod].right.get(0).equals("epsilon")) {
                        for (int i = Cgramma.prod[id_of_prod].right.size() - 1; i >= 0; i--) {//逆序压栈
                            if (!Cgramma.prod[id_of_prod].right.get(i).equals("epsilon")) {
                                SYN.push(Cgramma.prod[id_of_prod].right.get(i));
                                System.out.print(Cgramma.prod[id_of_prod].right.get(i)+"进栈,");
                            }
                        }
                        System.out.println();
                        continue;
                    } //continue;
                    for (int i = Cgramma.prod[id_of_prod].right.size() - 1; i >= 0; i--) {//逆序压栈
                        SYN.push(Cgramma.prod[id_of_prod].right.get(i));
                        System.out.print(Cgramma.prod[id_of_prod].right.get(i)+"进栈,");
                    }
                } else {
                    return false;
                }
                System.out.print("\t\t"+Cgramma.prod[id_of_prod].left+"->"+Cgramma.prod[id_of_prod].right);

            }
            //语义动作
            else {
                SemanticAnalysis.Call(curstate, pos, (String) lex.lexResult.get(pos - 1).get("value"), Cgramma, Table);
            }

            System.out.println();
        }

        if(!flag){
            //System.exit(0);
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        SymbolTable Table = new SymbolTable();
        lexAnalysis Lex = new lexAnalysis();
        Lex.LexAnaly();
        Table.getTable(Lex);
        Table.printtable();
        Syntax Cgramma = new Syntax();
        if (LL1Analysis.LL1_Analyse(Cgramma, Lex, Table)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        SemanticAnalysis.PrintQt(Cgramma);

    }
}

